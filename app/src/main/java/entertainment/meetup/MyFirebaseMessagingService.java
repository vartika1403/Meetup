package entertainment.meetup;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import entertainment.meetup.common.Config;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtil notificationUtils;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (message == null)
            return;

        Log.i(LOG_TAG, "From: " + message.getFrom());

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.i(LOG_TAG, "Notification Body: " + message.getNotification().getBody());
            handleNotification(message.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.i(LOG_TAG, "Data Payload: " + message.getData().toString());

            try {
                JSONObject json = new JSONObject(message.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Log.i(LOG_TAG, "message, " + message);
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
          //  NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
            //notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
            Log.i(LOG_TAG, "message app background, " + message);
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", message);

            // check for image attachment
                showNotificationMessage(getApplicationContext(), message, message, "11.53 PM", resultIntent);
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.i(LOG_TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.i(LOG_TAG, "title: " + title);
            Log.i(LOG_TAG, "message: " + message);
            Log.i(LOG_TAG, "isBackground: " + isBackground);
            Log.i(LOG_TAG, "payload: " + payload.toString());
            Log.i(LOG_TAG, "imageUrl: " + imageUrl);
            Log.i(LOG_TAG, "timestamp: " + timestamp);


            if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                //showNotificationMessage(getApplication(), title, message, timestamp, resultIntent);

                // play notification sound
              //  NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
                //notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                showNotificationMessage(getApplication(), title, message, timestamp, resultIntent);
                // check for image attachment

                /*if (TextUtils.isEmpty(message)) {
                    showNotificationMessage(getApplication(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplication(), title, message, timestamp, resultIntent, imageUrl);
                }*/
            }
        } catch (JSONException e) {
            Log.i(LOG_TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.i(LOG_TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
