package ca.ualberta.cs.phebert.litx;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Map;

/**
 * Background thread for notifications
 * @author zkist
 * @version 1.0
 * @see Request
 * @see MainActivity
 */
public class NotificationIntentService extends IntentService {

    /**
     * Constructor
     */
    public NotificationIntentService() {
        super("NOTIFICATION_INTENT_SERVICE");
    }

    /**
     * Handles when it gets passed the intent and notifies the user
     * @param intent intent that is getting passed
     */
    @Override
    protected void onHandleIntent(Intent intent) {
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
