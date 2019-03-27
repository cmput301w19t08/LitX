package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/** Based
 *  Source: http://www.sanktips.com/2017/11/15/android-recyclerview-with-custom-adapter-example/
 * @version 1
 * @author plontke
 * @see MyBooksActivity, Book
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Book> books;

    /**
     * Constructor for the BookList adapter
     * will populate a book_list_item with the list provided  in the constructor
     * @param context Context
     * @param books ArrayList<Book>
     */
    public BookListAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    /**
     *
     * @param parent ViewGroup
     * @param viewType int
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    /**
     *  Sets the TextViews and the ImageView to the proper values
     * @param holder is a ViewHolder object that holds the imageView and TextViews of a bookList
     * @param position is a int that denotes the position of the current item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(books.get(position));
        final Book book = books.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.isbn.setText(Long.toString(book.getIsbn()));

        load_image(holder, book);

        // Commented out since we cannot get request object from database at this time
        //        if (!book.isAvailable()) {
        //            holder.borrower.setText(book.getAcceptedRequest().getRequester().getUserName());
        //        } else {
        holder.borrower.setText(null);

        if (book.getStatus() == "Available" ) {
            holder.status.setText("Available");
        } else if (book.getStatus() == "Borrowed"){
            holder.status.setText("Borrowed");
        } else if (book.getStatus() == "Accepted") {
            holder.status.setText("Accepted");
        } else if (book.getStatus() == "Requested") {
            holder.status.setText("Requested");
        }
        /**
         * Sets the onclick listener for each book to go to the view
         * Books activity  while displaying the book passed
         * @Param View.OnClickListener
         */
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BookViewActivity.class);
                intent.putExtra("Book", book);
                context.startActivity(intent);
            }
        });
    }

    /**
     *
     * returns the size of the list of recyclerView
     * @return int
     */
    @Override
    public int getItemCount() {
        return books.size();
    }


    /**
     * ViewHolder Object that holds the view of a book_list_item
     * @author plontke
     * @see BookListAdapter
     * @version 1
     * Class viewholder that holds the view of a bookList Item
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView status;
        public TextView author;
        public TextView isbn;
        public ImageView photo;
        public TextView borrower;

        /**
         * Constructor for the ViewHolder object
         * @param itemView View
         */
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            title = (TextView) itemView.findViewById(R.id.book_title);
            status = (TextView) itemView.findViewById(R.id.book_status);
            author = (TextView) itemView.findViewById(R.id.book_author);
            isbn = (TextView) itemView.findViewById(R.id.book_isbn);
            photo = (ImageView) itemView.findViewById(R.id.book_photo);
            borrower = (TextView) itemView.findViewById(R.id.book_borrower);

        }
    }

    private void load_image(ViewHolder holder, Book book) {
        int iconId = context.getResources().getIdentifier("book_icon", "drawable", context.getPackageName());
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference path = storage.child(book.getOwnerUid() + "/" + Long.toString(book.getIsbn()));
        path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                GlideApp.with(context).load(imageURL).placeholder(iconId).into(holder.photo);
            }
        });
    }
}
