package com.discover.mobile.card.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.card.R;

/*
 * This class acts as an broadcast receiver for CardNavigation.
 */

public class CardNavigationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = CardNavigationBroadcastReceiver.class
            .getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(
                context.getString(R.string.logout_broadcast_action))) {
            Log.d(TAG, "logout action is called");
            Intent logoutIntent = new Intent(context,
                    CardNavigationRootActivity.class);
            logoutIntent.setAction(context
                    .getString(R.string.logout_broadcast_action));
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(logoutIntent);
        }
    }
}
