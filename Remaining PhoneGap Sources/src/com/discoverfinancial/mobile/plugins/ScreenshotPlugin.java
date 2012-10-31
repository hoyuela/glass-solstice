package com.discoverfinancial.mobile.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import android.provider.MediaStore;

public class ScreenshotPlugin extends Plugin {

	static final String TAG = "ScreenshotPlugin";
	
	static final String takeScreenshot = "takeScreenshot";
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = new PluginResult(Status.OK);
		if (action.equals(takeScreenshot)) {
			
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
			String imageName;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");			
			imageName = "discover_ecert_" + df.format(new Date()) + ".png";

			if (Environment.MEDIA_MOUNTED.equals(state)) {
			    // We can read and write the media
			    mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			    // We can only read the media
			    mExternalStorageAvailable = true;
			    mExternalStorageWriteable = false;
			} else {
			    // Something else is wrong. It may be one of many other states, but all we need
			    //  to know is we can neither read nor write
			    mExternalStorageAvailable = mExternalStorageWriteable = false;
			}
			
			if (!(mExternalStorageAvailable && mExternalStorageWriteable)) {
				result = new PluginResult(Status.ILLEGAL_ACCESS_EXCEPTION);
				return result;
			}
			
			// Take the screenshot off the webView, store inside bitmap
			this.webView.setDrawingCacheEnabled(true);
			Bitmap bm = Bitmap.createBitmap(this.webView.getDrawingCache());
			this.webView.setDrawingCacheEnabled(false);
			
			// Store screenshot to local storage and get a handle to it
//			FileOutputStream out;
//			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "coupon.png");
//			try {
//				file.createNewFile();
//			} catch (IOException e1) {
//				result = new PluginResult(Status.ILLEGAL_ACCESS_EXCEPTION);
//				return result;
//			}
//			try {
//				out = new FileOutputStream(file);
//				//out = this.ctx.getApplicationContext().openFileOutput("coupon.png", Context.MODE_PRIVATE);
//			} catch (FileNotFoundException e) {
//				result = new PluginResult(Status.ERROR);
//				return result;
//			}
//			bm.compress(CompressFormat.PNG, 100, out);
			MediaStore.Images.Media.insertImage(this.ctx.getContentResolver(), bm, imageName, "");
			
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
		}
		
		return result;
	}
	
}
