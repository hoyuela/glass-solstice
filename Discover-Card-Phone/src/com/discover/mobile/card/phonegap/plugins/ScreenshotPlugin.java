package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.discover.mobile.card.common.utils.Utils;

public class ScreenshotPlugin extends CordovaPlugin {

    static final String TAG = "ScreenshotPlugin";

    static final String takeScreenshot = "takeScreenshot";

    @Override
    public boolean execute(final String action, final String rawArgs,
            final CallbackContext callbackContext) throws JSONException {
        PluginResult result = new PluginResult(Status.OK);
        if (action.equals(takeScreenshot)) {
            Utils.log(TAG, "inside getSecToken ");
            boolean mExternalStorageAvailable = false;
            boolean mExternalStorageWriteable = false;
            final String state = Environment.getExternalStorageState();

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
            webView.setDrawingCacheEnabled(true);
            final Bitmap bm = Bitmap.createBitmap(webView.getDrawingCache());
            webView.setDrawingCacheEnabled(false);

            MediaStore.Images.Media.insertImage(cordova.getActivity()
                    .getContentResolver(), bm, "coupon.png", "description");

            Toast.makeText(cordova.getActivity().getApplicationContext(),
                    "Saved to photos", Toast.LENGTH_LONG).show();
            return true;
        } else {
            result = new PluginResult(Status.INVALID_ACTION);
            callbackContext.sendPluginResult(result);
            return false;
        }

    }

}
