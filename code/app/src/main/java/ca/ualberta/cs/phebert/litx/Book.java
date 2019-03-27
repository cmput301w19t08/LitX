package ca.ualberta.cs.phebert.litx;

import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Book implements Serializable {
    private static final String BOOK_COLLECTION = "Books";
    private static Map<String, Book> db;
    private static Task<QuerySnapshot> task;

    private String author;
    private String title;
    private long isbn;
    private BookStatus status;
    private User owner;
    private String docID; // Document ID in Firestore
    private ArrayList<Request> requests;
    private Request acceptedRequest;
    private ImageView photograph;

    public Book(User owner, String author, String title, long isbn) {
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.status = BookStatus.available;
    }

    public Book(String owner, String author, String title, long isbn) {
        setOwner(owner);
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public Book() {
    } // For firestore


    ///////////////////////////////////// Database Stuff ///////////////////////////////////////////

    static private void loadDb() {
        if(db == null && task == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            db = new HashMap<>();
            task = FirebaseFirestore.getInstance()
                    .collection(BOOK_COLLECTION)
                    .get()
                    .addOnCompleteListener(self -> {
                        if(self.isSuccessful()) {
                            QuerySnapshot result = self.getResult();
                            if(result == null) return;
                            for(DocumentSnapshot snapshot : result.getDocuments()) {
                                db.put(snapshot.getId(), fromSnapshot(snapshot));
                            }
                        }
                    });
        }
    }

    public static Map<String, Book> getAll() {
        loadDb();
        while(!task.isComplete()) Thread.yield();
        return db;
    }

    private static Book fromSnapshot(DocumentSnapshot doc) {
        Book ans = new Book();
        ans.setDocID(doc.getId());
        ans.setOwner(doc.getString("owner"));
        ans.setStatus(doc.getString("status"));
        ans.setAuthor(doc.getString("author"));
        // TODO (Scott): set/get photograph, might want to change this to a filename
        try {
            ans.setIsbn(doc.getLong("isbn"));
        } catch(NullPointerException e) {
            // whatever, no isbn, this book is weird
        }

        // could be moved elsewhere, if this method is called more than once for a book.
        ans.getOwner().addBook(ans);
        return ans;
    }

    public static Book findByDocId(String docId) {
        return getAll().get(docId);
    }

    public void delete() {
        // TODO delete method.
    }

    public String getDocID() { return docID; }

    private void setDocID(String newDocID) { this.docID = newDocID; }

    ////////////////////////////////// settters and getters ////////////////////////////////////////

    /**
     * Returns if the book is available
     *
     * @return Boolean
     */
    public Boolean isAvailable() {
        return this.status == BookStatus.available;
    }

    /**
     * getter for status
     * @return String
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * Sets this.status to status
     *
     * @param status (String) one of accepted, available, borrowed, requested
     */
    private void setStatus(String status) {
        try {
            this.status = BookStatus.valueOf(status.toLowerCase());
        } catch(IllegalArgumentException e) {
            log.e("LitX.Book","status does not exist", e);
        }
    }

    /**
     * Sets this book's status
     * @param status the status to set the book to.
     */
    private void setStatus(BookStatus status) {
        this.status = status;
    }

    /**
     * Getter for owner
     *
     * @return String
     */
    public User getOwner() {
        return owner;
    }

    /**
     * sets Owner of the book.
     *
     * @param ownerUid user ID string of the owner of the book
     */
    private void setOwner(String ownerUid) {
        owner = User.findByUid(ownerUid);
    }

    /**
     * getter for author
     *
     * @return String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * setter for Author
     *
     * @param author String
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * getter for title
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for Title
     *
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for ISBN
     *
     * @return long type
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * setter for ISBN
     *
     * @param isbn long type
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * getter for requests
     *
     * @returns ArrayList<Request>
     */
    public ArrayList<Request> getRequests() {
        return requests;
    }


    public User getBorrower() {
        if (acceptedRequest != null) {
            return acceptedRequest.getRequester();
        }
        return null;
    }

    /**
     * should fail if acceptedRequest is not null.
     * @param request A request that has been accepted by owner
     */
    public void setAcceptedRequest(Request request) {
        if (acceptedRequest == null) {
            acceptedRequest = request;
            status = BookStatus.accepted;
        }
        if (request == null) {
            acceptedRequest = request;
            status = BookStatus.available;
        }
    }

    /**
     * Getter for acceptedRequest
     * @return Request
     */
    public Request getAcceptedRequest() {
        return acceptedRequest;
    }

    /**
     * getter for the photograph
     *
     * @return an ImageView of the photograph
     */
    public ImageView getPhotograph() {
        return photograph;
    }

    /**
     * setter for photograph
     *
     * @param photograph ImageView
     */
    public void setPhotograph(ImageView photograph) {
        this.photograph = photograph;
    }

    /**
     * Add a new request created by this user
     */
    public void addRequest() {
        Request request = new Request(this, this.owner, User.currentUser());
        addRequest(request);
    }

    /**
     * add a requests to this book's requests
     * @param request
     */
    void addRequest(Request request) {
       requests.add(request);
    }
}
