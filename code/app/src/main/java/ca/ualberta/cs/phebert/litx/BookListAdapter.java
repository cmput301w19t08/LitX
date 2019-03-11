package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.j2objc.annotations.ObjectiveCName;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Book> books;

    public BookListAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    /**
     *
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
        holder.photo = book.getPhotograph();
        if (!book.isAvailable()) {
            holder.borrower.setText(book.getAcceptedRequest().getRequestor().getUserName());
        } else {
            holder.borrower.setText(null);
        }
            if (book.isAvailable()) {
            holder.status.setText("Available");
        } else {
            holder.status.setText("Borrowed");
        }
        // Sets the onclick listener for each book to go to the view
        // Books activity  while displaying the books
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
     * returns the size of the list of recyclerView
     * @return
     */
    @Override
    public int getItemCount() {
        return books.size();
    }


    /**
     * Class viewholder that holds the view of a bookList Item
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView status;
        public TextView author;
        public TextView isbn;
        public ImageView photo;
        public TextView borrower;


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
}