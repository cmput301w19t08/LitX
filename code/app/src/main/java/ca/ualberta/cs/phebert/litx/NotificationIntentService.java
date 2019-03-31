package ca.ualberta.cs.phebert.litx;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NOTIFICATION_INTENT_SERVICE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("LitX onHandleIntent", intent.getStringExtra("NOTIFICATION_INTENT_SERVICE"));

    }
}
