package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Adapter for the top ten Books
 * @author plontke
 * @version 1.0
 * @see MainActivity
 * @see Book
 */
public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Book> books;

    /**
     * Constructor for the BookList adapter
     * will populate a book_list_item with the list provided  in the constructor
     * @param context Context
     * @param books ArrayList<Book>
     */
    public TopTenAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    /**
     * Creates a view holder object
     * @param parent ViewGroup
     * @param viewType int
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_ten_item, parent,
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
        int top = position + 1;
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.number.setText(Integer.toString(top));

        /**
         * Sets the onclick listener for each book to go to the view
         * Books activity  while displaying the book passed
         * @Param View.OnClickListener
         */
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchActivity.class);
                intent.putExtra("BOOK_NAME", book.getTitle());
                context.startActivity(intent);
            }
        });
    }

    /**
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
        public TextView author;
        public TextView number;


        /**
         * Constructor for the ViewHolder object
         * @param itemView View
         */
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            title = (TextView) itemView.findViewById(R.id.top_ten_book_title);
            author = (TextView) itemView.findViewById(R.id.top_ten_book_author);
            number = (TextView) itemView.findViewById(R.id.top_ten_number);

        }
    }
}