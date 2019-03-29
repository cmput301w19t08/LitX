package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static ca.ualberta.cs.phebert.litx.ProfileActivity.UID_IN;

/** Based
 * TODO Change source (copied from Book List Adapter)
 *  Source: http://www.sanktips.com/2017/11/15/android-recyclerview-with-custom-adapter-example/
 * @version 1
 * @author plontke
 * @see MyBooksActivity, Book
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;

    /**
     * Constructor for the UserList adapter
     * will populate a user_list_item with the list provided in the constructor
     * @param context Context
     * @param users ArrayList<User>
     */
    public UserListAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    /**
     *
     * @param parent ViewGroup
     * @param viewType int
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent,
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
        holder.itemView.setTag(users.get(position));
        final User user = users.get(position);

        holder.username.setText(user.getUserName());

        /**
         * Sets the onclick listener for each book to go to the view
         * Books activity  while displaying the book passed
         * @Param View.OnClickListener
         */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookViewActivity.class);
            intent.putExtra("User", user.getUserid());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra(UID_IN, user.getUserid());

            context.startActivity(intent);
            return true;
        });
    }

    /**
     *
     * returns the size of the list of recyclerView
     * @return int
     */
    @Override
    public int getItemCount() {
        return users.size();
    }


    /**
     * ViewHolder Object that holds the view of a book_list_item
     * @author plontke
     * @see UserListAdapter
     * @version 1
     * Class viewholder that holds the view of a bookList Item
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;

        /**
         * Constructor for the ViewHolder object
         * @param itemView View
         */
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            username = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}
