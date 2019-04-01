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
            if (entry.getValue().getBookOwner().getUserName().equals(User.currentUser().getUserName())) {
                if ((entry.getValue().getRequestSeen() == Boolean.FALSE) || (entry.getValue().getRequestSeen() == null)) {
                    Request request = entry.getValue();
                    request.generateNotification(this);
                    request.setRequestSeen(Boolean.TRUE);
                    request.toMap();
                    request.selfPush();
                }
            } else if (entry.getValue().getRequester().getUserName().equals(User.currentUser().getUserName())) {
                if ((entry.getValue().getRequestSeen() == Boolean.FALSE) || (entry.getValue().getRequestSeen() == null)) {
                    if (entry.getValue().getStatus().equals(RequestStatus.Accepted)) {
                        Request request = entry.getValue();
                        request.generateNotification(this);
                        request.setRequestSeen(Boolean.TRUE);
                        request.toMap();
                        request.selfPush();
                    }
                }
            }
        }
    }
}
