package ca.ualberta.cs.phebert.litx;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Map;

public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NOTIFICATION_INTENT_SERVICE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("LitX onHandleIntent", intent.getStringExtra("NOTIFICATION_INTENT_SERVICE"));
        Map<String, Request>  db =  Request.getAll();
        for (Map.Entry<String, Request> entry : db.entrySet()) {
            Log.v("LitX.REQUEST", entry.getValue().getRequester().getUserName());


            if (entry.getValue().getBookOwner().getUserName().equals(User.currentUser().getUserName())) {
                Log.i("LitX IF DUNGEONNNNNNNN", entry.getValue().getBook().getStatus().toString());
                if (entry.getValue().getRequestSeen() == null) {
                    Log.i("LitX IF Dundegoneq", "yea it null");
                }
                Log.i("LitX IF DUNGEON3", entry.getValue().getBookOwner().getUserName());
                Log.i("LitX IF DUNGEON4", entry.getValue().getRequester().getUserName());
                Log.i("LitX IF DUNGEON4", User.currentUser().getUserName());
                if ((entry.getValue().getRequestSeen() == Boolean.FALSE) || (entry.getValue().getRequestSeen() == null)) {
                    Request request = entry.getValue();
                    request.generateNotification(this);
                    request.setRequestSeen(Boolean.FALSE);
                    request.toMap();
                    request.selfPush();
                }
            } else if (entry.getValue().getRequester().getUserName().equals(User.currentUser().getUserName())) {
                Log.i("LitX IF DUNGEONSSSSSS", entry.getValue().getBook().getStatus().toString());

                if ((entry.getValue().getRequestSeen() == Boolean.FALSE) || (entry.getValue().getRequestSeen() == null)) {
                    Log.i("LitX IF Made it to the 2nd if", "second if");
                    if (entry.getValue().getStatus().equals(RequestStatus.Accepted)) {
                        Log.i("LitX IF", "sending");
                        Request request = entry.getValue();
                        request.generateNotification(this);
                        request.setRequestSeen(Boolean.FALSE);
                        request.toMap();
                        request.selfPush();
                    }
                }
            }
        }
    }
}
