package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ScreenshotPlugin extends CordovaPlugin {

    static final String TAG = "ScreenshotPlugin";

    static final String takeScreenshot = "takeScreenshot";

    @Override
    public boolean execute(String action, String rawArgs,
            CallbackContext callbackContext) throws JSONException {
        PluginResult result = new PluginResult(Status.OK);
        if (action.equals(takeScreenshot)) {
            Log.d(TAG, "inside getSecToken ");
            boolean mExternalStorageAvailable = false;
            boolean mExternalStorageWriteable = false;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
            } else {
                // Something else is wrong. It may be one of many other states,
                // but all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
            }

            if (!(mExternalStorageAvailable && mExternalStorageWriteable)) {
                result = new PluginResult(Status.ILLEGAL_ACCESS_EXCEPTION);
                callbackContext.sendPluginResult(result);
                return false;
            }

            // Take the screenshot off the webView, store inside bitmap
            this.webView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(this.webView.getDrawingCache());
            this.webView.setDrawingCacheEnabled(false);

            MediaStore.Images.Media.insertImage(this.cordova.getActivity()
                    .getContentResolver(), bm, "coupon.png", "description");

            Toast.makeText(this.cordova.getActivity().getApplicationContext(),
                    "Saved to photos", Toast.LENGTH_LONG).show();
            return true;
        } else {
            result = new PluginResult(Status.INVALID_ACTION);
            callbackContext.sendPluginResult(result);
            return false;
        }

    }

}
