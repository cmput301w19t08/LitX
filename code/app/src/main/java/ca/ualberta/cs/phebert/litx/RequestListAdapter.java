package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Request> requests;

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
        holder.requestorName.setText(request.getRequester().getUserName());
        holder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(v.getContext(), "Request Accepted",
                        Toast.LENGTH_SHORT).show();
                request.getBook().setAcceptedRequest(request);
                notifyDataSetChanged();

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
