package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Book> books;

    public BookListAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.itemView.setTag(books.get(position));
        Book book = books.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.isbn.setText(Long.toString(book.getIsbn()));
        holder.photo = book.getPhotograph();
        holder.borrower.setText(book.getBorrower().getUserName());
        if (book.isAvailable() == true) {
            holder.status.setText("Available");
        } else {
            holder.status.setText("Borrowed");
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView status;
        public TextView author;
        public TextView isbn;
        public ImageView photo;
        public TextView borrower;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.book_title);
            status = (TextView) itemView.findViewById(R.id.book_status);
            author = (TextView) itemView.findViewById(R.id.book_author);
            isbn = (TextView) itemView.findViewById(R.id.book_isbn);
            photo = (ImageView) itemView.findViewById(R.id.book_photo);
            borrower = (TextView) itemView.findViewById(R.id.book_borrower);

            // TODO implement on click Listener to show the rest of the profile
        }
    }
}
