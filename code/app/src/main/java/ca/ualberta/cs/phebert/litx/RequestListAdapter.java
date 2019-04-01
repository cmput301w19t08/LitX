package ca.ualberta.cs.phebert.litx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * An array adapter for the request object
 * @Author plontke
 * @See myBooks
 * @See MapActivity
 * @Version 1
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Request> requests;

    /**
     * Constructor for the RequestList Adapter
     * @param context
     * @param requests
     */
    public RequestListAdapter(Context context, ArrayList<Request> requests){
        this.context = context;
        this.requests = requests;
    }
    @Override
    public RequestListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(requests.get(position));
        Request request = requests.get(position);

        String uid = User.currentUser().getUserid();
        if (!uid.equals(request.getBookOwner().getUserid())) {
            holder.accept.setVisibility(View.GONE);
            if (!uid.equals(request.getRequester().getUserid())) {
                holder.delete.setVisibility(View.GONE);
            } else {
                holder.delete.setText("Cancel");
            }
        }

        holder.requestorName.setText(request.getRequester().getUserName());
        holder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(v.getContext(), "Request Accepted",
                        Toast.LENGTH_SHORT).show();
                request.getBook().setAcceptedRequest(request);
                notifyDataSetChanged();
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtra("BOOK", request.getBook().getDocID());
                intent.putExtra("MOVABLE", true);
                context.startActivity(intent);

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Request Deleted",
                        Toast.LENGTH_SHORT).show();
                request.getBook().deleteRequest(request);
                request.delete();
                Request.push();
                notifyDataSetChanged();

                Intent intent = new Intent(context, BookViewActivity.class);
                ((Activity) context).finish();
                intent.putExtra("Book", request.getBook().getDocID());
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount(){
        return requests.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView requestorName;
        public Button accept;
        public Button delete;

        public ViewHolder(View itemView) {
            super(itemView);

            requestorName = (TextView) itemView.findViewById(R.id.requests_item_owner);
            accept = (Button) itemView.findViewById(R.id.accept_requests);
            delete = (Button) itemView.findViewById(R.id.delete_request);
        }

    }
}
