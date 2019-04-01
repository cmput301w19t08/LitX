package ca.ualberta.cs.phebert.litx;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Book implements Serializable {
    private static final String TAG = "LitX.Book";
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

    private int views;
    private int borrows;

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
        requests = new ArrayList<>();
    } // For firestore


    ///////////////////////////////////// Database Stuff ///////////////////////////////////////////

    static private void loadDb() {
        if(task == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            task = FirebaseFirestore.getInstance()
                    .collection(BOOK_COLLECTION)
                    .get();
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

        if (doc.getString("views") == null) {
            Log.d("FIELD", "DOESN'T EXIST");
            ans.setViews(0);
        } else {
            Log.d("FIELD", "EXISTS");
            ans.setViews(Integer.valueOf(doc.getString("views")));
        }

        String ownerUid = doc.getString("ownerUid");
        //Log.d(TAG, "Owner UID = " + ownerUid);
        ans.setOwner(ownerUid);
        ans.setStatus(doc.getString("status"));
        ans.setAuthor(doc.getString("author"));
        ans.setTitle(doc.getString("title"));

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
        for (Request request : requests){
            request.delete();
        }
        Request.push();
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

    ////////////////////////////////// setters and getters ////////////////////////////////////////

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
     * Setter for amount of views
     * @param views amount of views the book has
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Setter for amount of borrows
     * @param borrows amount of borrows the book has
     */
    public void setBorrows(int borrows) {
        this.borrows = borrows;
    }

    /**
     * Getter for views
     * @return amount of views the book currently has
     */
    public int getViews() {
        return views;
    }

    /**
     * Getter for borrows
     * @return amount of borrows the book currently has
     */
    public int getBorrows() {
        return borrows;
    }

    /**
     * Getter for borrower
     * @return the user borrowing the book
     */
    public User getBorrower() {
        if (acceptedRequest != null) {
            return acceptedRequest.getRequester();
        }
        return null;
    }

    /**
     * Deletes a request
     * @param request Request to be deleted from the book
     */
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
            for (Request deletedRequest : getRequests()){
                deletedRequest.delete();
            }
            request.accept();
            Request.push();
            request.selfPush();

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
     * add a requests to this book's requests
     * @param request
     */
    void addRequest(Request request) {
       requests.add(request);
       request.selfPush();
    }

    /**
     * Determines if the object and book are equal, for this to happen the books just need to have
     * the same isbn and owner
     * @param obj object to check if equal to
     * @return obj equals book
     */
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

    /**
     * Makes a hashcode
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return (int) (isbn % Integer.MAX_VALUE);
    }
}
