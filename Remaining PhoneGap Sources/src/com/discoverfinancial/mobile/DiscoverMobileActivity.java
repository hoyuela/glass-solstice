package com.discoverfinancial.mobile;

import java.io.IOException;
import java.util.List;

import org.apache.cordova.DroidGap;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.discover.rdc.camera.plugin.utils.Device;
import com.discover.rdc.camera.plugin.utils.DeviceParser;
import com.xtify.cordova.XtifyCordovaPlugin;

/** 
 * Main Activity extending from the PhoneGap class
 *
 */
public class DiscoverMobileActivity extends DroidGap {
	
	private Custom cust;
	private DiscoverMobileActivity thisInstance;
	
	public static final String INITIAL_URL ="file:///android_asset/www/index.html";
	public static final int SPLASH_DELAY_MS = 5000;
	private static final String NO_CONN_MSG = "The Internet connection appears to be offline at this moment. " +
			"In order to login and access the features of the application, you will need Internet connectivity. " +
			"Please check your device settings and try again once the connection has been restored.";
	private static final String TAG = "DiscoverMobileActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();
        this.appView.getSettings().setNavDump(false);
		this.appView.getSettings().setPluginsEnabled(true);
        this.setWebViewClient(appView, new DiscoverWebViewClient(this));
        cust = new Custom(this); 
        appView.addJavascriptInterface(cust, "Custom"); 
        super.setIntegerProperty("splashscreen", R.drawable.loading);
        thisInstance = this;
        clearSessionCookies(true);
        //evaluateDeviceTypeToCameraManagement();
		if(android.os.Build.VERSION.SDK_INT >= 14 && android.os.Build.VERSION.SDK_INT < 16) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		// jcoyne - 9/6/12 - Added this code when upgrading to SDK 10 to allow the PDF downloader to work 
	    StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder().permitAll().build() );
	    // The Splash screen will be hidden by JavaScript once index.html is loaded
	    super.loadUrl(INITIAL_URL,SPLASH_DELAY_MS);
    }
        
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
    	Intent intent = getIntent();
    	if (intent != null && intent.getExtras()!=null && intent.getExtras().getString(XtifyCordovaPlugin.EXTRA_ACTION_TYPE) != null) {
    		XtifyCordovaPlugin.processActivityExtras(intent.getExtras(), this);
    	}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 0, 0, "Info");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i = new Intent(this, InfoScreen.class);
    	startActivity(i);
    	return true;
    }
    
    public void spinnerOn(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (thisInstance.spinnerDialog != null) {
                    thisInstance.spinnerDialog.dismiss();
                }
                thisInstance.spinnerDialog = ProgressDialog.show(thisInstance, title, message);
            }
            
        });
        
    }
    
    public void spinnerOff() {
        if (thisInstance.spinnerDialog != null) {
        	if (thisInstance.spinnerDialog.isShowing()) { thisInstance.spinnerDialog.dismiss();}
        }
    }
   	
   	@Override
	public void onDestroy() {
   		clearSessionCookies(false);
		super.onDestroy();
	}

   	/**
   	 * Remove Session (eg transient)  Cookies 
   	 * 
   	 * @param create - Flag to indicate if should create instance of CookieSyncManager
   	 */
	private void clearSessionCookies(boolean create){
		if(create){
			CookieSyncManager.createInstance(this.getContext());
		}
		CookieSyncManager.getInstance().sync();
    	CookieManager.getInstance().removeSessionCookie(); 	
   	}
	
	/**
   	 * Evaluate Device in order to manage camera resources in Deposit Check
   	 * 
   	 */
	private void evaluateDeviceTypeToCameraManagement(){
		//Log device information
		Log.d(TAG, "======== BRAND:" + android.os.Build.BRAND);
		Log.d(TAG, "======== SKD LEVEL:" + android.os.Build.VERSION.SDK);
		Log.d(TAG, "======== MODEL:" + android.os.Build.MODEL);
		
		DeviceParser parser = new DeviceParser(getApplicationContext());
		
		try{
			List<Device> devices = parser.getDevicesToReleaseAllCameraResources();
			Boolean releaseAllCameraResources = false; 
			
			for (Device device : devices){
				if(device.getBrand().equalsIgnoreCase(android.os.Build.BRAND) &&
						device.getSdkLevel().equalsIgnoreCase(android.os.Build.VERSION.SDK) &&
						device.getModel().equalsIgnoreCase(android.os.Build.MODEL)){
					
					releaseAllCameraResources = true;
					break;
				}
			}
			
			//Save value in SharedPreferences
			SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFERENCES_FILE_NAME), 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(getResources().getString(R.string.PREFERENCES_KEY_RELEASE_ALL_CAMERA_RESOURCES), releaseAllCameraResources);
			editor.commit();
		}
		catch (XmlPullParserException e){
			Log.e(TAG, e.getMessage());
		}
		catch (IOException e){
			Log.e(TAG, e.getMessage());
		}
   	}

	/**
	 * 
	 * @param failingUrl
	 */
	public void showFailOrRetryMsg(final String failingUrl) { 
		this.runOnUiThread(new Runnable() {
			public void run() {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(thisInstance);
		    	builder.setMessage(NO_CONN_MSG).setTitle("Unable to Connect to Discover").setCancelable(false)
		    		.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    						thisInstance.finish();
		    				}
		    	});
		    	AlertDialog alert = builder.create();   
		    	alert.show();
		   }
		});
	}
}
