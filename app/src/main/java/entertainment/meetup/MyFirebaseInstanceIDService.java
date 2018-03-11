package entertainment.meetup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import entertainment.meetup.common.Config;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String LOG_TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        String tokenId = FirebaseInstanceId.getInstance().getToken();

        //save tokenid in sharedpreferences
        storeRegIdInPref(tokenId);

        //sending reg Id to your server
        sendRegIdToServer(tokenId);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", tokenId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegIdToServer(String tokenId) {
        Log.i(LOG_TAG, "tokenId, " + tokenId);
    }

    private void storeRegIdInPref(String tokenId) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyApplication", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("regId", tokenId);
        editor.commit();
    }

}
