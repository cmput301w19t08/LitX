package ca.ualberta.cs.phebert.litx.Models;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ualberta.cs.phebert.litx.BookStatus;
import ca.ualberta.cs.phebert.litx.Observers.BookObserver;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Book implements Serializable {
    private static final String TAG = "LitX.Book";
    private static final String BOOK_COLLECTION = "Books";
    private static Map<String, Book> db;
    private static Task<QuerySnapshot> task;
    private List<BookObserver> observers;

    private String author;
    private String title;
    private long isbn;
    private BookStatus status;
    private User owner;
    private String docID; // Document ID in Firestore
    private ArrayList<Request> requests;
    private Request acceptedRequest;
    private ImageView photograph;

    private int views;
    private int borrows;

    public Book(User owner, String author, String title, long isbn) {
        this();
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.status = BookStatus.available;
    }

    public Book(String owner, String author, String title, long isbn) {
        this();
        setOwner(owner);
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public Book() {
        observers = new ArrayList<>();
        requests = new ArrayList<>();
    } // For firestore


    ///////////////////////////////////// Database Stuff ///////////////////////////////////////////

    static private void loadDb() {
        if(task == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            task = FirebaseFirestore.getInstance()
                    .collection(BOOK_COLLECTION)
                    .get();

            FirebaseFirestore.getInstance()
                    .collection(BOOK_COLLECTION)
                    .addSnapshotListener((result, e) -> {
                        if(e != null) {
                            Log.e(TAG,"firebase error", e);
                            return;
                        }
                        if (result == null) return;
                        for (DocumentSnapshot doc: result.getDocuments()) {
                            Book oldVal = findByDocId(doc.getId());
                            Book newVal = fromSnapshot(doc);
                            if(oldVal == null || newVal == null) continue;
                            if(oldVal.equals(newVal)) continue;
                            // not using setters because they write to firebase, infinite loop
                            oldVal.title = newVal.title;
                            oldVal.author = newVal.author;
                            oldVal.isbn = newVal.isbn;
                            oldVal.borrows = newVal.borrows;
                            oldVal.views = newVal.views;
                            oldVal.status = newVal.status;
                            oldVal.publish();
                        }
                    });
        }
    }

    public static Map<String, Book> getAll() {
        loadDb();
        if(!User.isSignedIn()) return null;
        while(!task.isComplete()) Thread.yield();
        if(!task.isSuccessful()) return null;
        if(db == null) {
            db = new HashMap<>();
            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                Log.v(TAG, snapshot.getId());
                db.put(snapshot.getId(), fromSnapshot(snapshot));
            }
            Log.v(TAG, "amount of books: " + db.size());
        }

        return db;
    }

    private static Book fromSnapshot(DocumentSnapshot doc) {
        Book ans = new Book();
        ans.setDocID(doc.getId());

        String ownerUid = doc.getString("ownerUid");
        //Log.d(TAG, "Owner UID = " + ownerUid);
        ans.setOwner(ownerUid);
        ans.setStatus(doc.getString("status"));
        ans.setAuthor(doc.getString("author"));
        ans.setTitle(doc.getString("title"));
        // TODO (Scott): set/get photograph, might want to change this to a filename
        try {
            ans.setIsbn(doc.getLong("isbn"));
        } catch(NullPointerException e) {
            // whatever, no isbn, this book is weird
        }
        log.d("LitX.Book", ans.getTitle());
        // could be moved elsewhere, if this method is called more than once for a book.
        //Log.d(TAG,ans.getOwner() == null ? "user is null" : "user is not null");
        ans.getOwner().addBook(ans);
        return ans;
    }

    public static Book findByDocId(String docId) {
        return getAll().get(docId);
    }

    public void delete(Book book) {
        FirebaseFirestore.getInstance()
                .collection(BOOK_COLLECTION)
                .document(getDocID())
                .delete();
        db.remove(getDocID());
        if (User.currentUser().getMyBooks().contains(book)){
            User.currentUser().getMyBooks().remove(book);
        }
        while (requests.size() > 0){
            requests.get(0).deleteRequest();
            requests.remove(requests.get(0));
        }
    }

    public String getDocID() { return docID; }

    private void setDocID(String newDocID) { this.docID = newDocID; }

    public void push() {
        Map<String, Object> b = new HashMap<>();
        b.put("ownerUid",getOwner().getUserid());
        b.put("status", getStatus().toString());
        b.put("author",getAuthor());
        b.put("title", getTitle());
        b.put("isbn",getIsbn());

        for(Book book : Book.getAll().values()) {
            if(this.equals(book)) setDocID(book.docID);
        }

        CollectionReference collection = FirebaseFirestore.getInstance()
                .collection("Books");
        if (docID == null || docID.equals("")) {
            // Create a new book and add it to firestore
            setDocID(collection.document().getId());
            collection.document(getDocID()).set(b);
            db.put(getDocID(),this);
            owner.getMyBooks().add(this);
        } else {
            // Update the firestore document since the book already exists
            collection.document(docID).set(b);
            db.get(getDocID()).setTitle(getTitle());
            db.get(getDocID()).setAuthor(getAuthor());
            db.get(getDocID()).setIsbn(getIsbn());
            owner.getMyBooks().set(owner.getMyBooks().indexOf(this), this);
        }
    }

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
    public void setStatus(String status) {
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
    //private void setStatus(BookStatus status) {
    //    this.status = status;
    //}

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

    /**
     *
     * @param views
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     *
     * @param borrows
     */
    public void setBorrows(int borrows) {
        this.borrows = borrows;
    }

    /**
     *
     * @return
     */
    public int getViews() {
        return views;
    }

    /**
     *
     * @return
     */
    public int getBorrows() {
        return borrows;
    }



    public User getBorrower() {
        if (acceptedRequest != null) {
            return acceptedRequest.getRequester();
        }
        return null;
    }

    public void deleteRequest(Request request){
        if (requests.contains(request)){
            requests.remove(request);
        }
    }

    /**
     * should fail if acceptedRequest is not null.
     * @param request A request that has been accepted by owner
     */
    public void setAcceptedRequest(Request request) {
        if (acceptedRequest == null) {
            acceptedRequest = request;
            status = BookStatus.accepted;
            requests.clear();
            request.accept();
            push();

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

//    /**
//     * Add a new request created by this user
//     */
//    public void addRequest() {
//        Request request = new Request(this, this.owner, User.currentUser());
//        request.selfPush();
//        addRequest(request);
//    }

    /**
     * add a requests to this book's requests
     * @param request
     */
    public void addRequest(Request request) {
       requests.add(request);
       request.selfPush();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        if(obj instanceof Book) {
            Book other = (Book) obj;
            return owner.equals(other.owner) &&
                    isbn == other.isbn;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (isbn % Integer.MAX_VALUE);
    }

    public void publish() {
        for(BookObserver observer : observers) {
            observer.onUpdate(this);
        }
    }
}
