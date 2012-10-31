package com.discover.rdc.camera.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.discoverfinancial.mobile.R;
import com.discover.rdc.camera.plugin.utils.Base64;
import com.discover.rdc.camera.plugin.utils.CameraUtils;


public class MyCustomCameraPlugin extends Plugin
{
	public static final String TAG = "MyCustomCameraPlugin";
	
	private static final String ACTION_KEY = "action";
	
	public final static int REQUEST_CAMERA_ACTIVITY = 0;
	
	public static final int ACTION_CAPTURE_FRONT = 0;
	public static final int ACTION_CAPTURE_BACK = 1;
	public static final int ACTION_VIEW_FRONT = 2;
	public static final int ACTION_VIEW_BACK = 3;
	public static final int ACTION_VIEW_ONLY_FRONT = 4;
	public static final int ACTION_VIEW_ONLY_BACK = 5;
	public static final int ACTION_CAPTURE_ONLY_FRONT = 6;
	public static final int ACTION_CAPTURE_ONLY_BACK = 7;
		
	//Flags to redirect after camera custom has finished
	public static boolean shouldRedirectToEnterAmmount;
	public static boolean shouldRedirecToSelectAccount;
	
	//Flags to errors in encode Base64 process
	private boolean hasEncodeErrorInFront;
	private boolean hasEncodeErrorInBack;

	
	private String cameraAction;
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId)
	{
		Log.d(TAG, "Enter to Plugin execute");
		
		PluginResult pluginResult = null;
		JSONObject params = null;
		
		try
		{
			params =  data.getJSONObject(0);
			cameraAction = params.getString(ACTION_KEY);

			if(ctx.getResources().getString(R.string.CAPTURE_CHECK).equals(cameraAction))
			{	
				//Here init complete sequence
				openCustomCamera(ACTION_CAPTURE_FRONT);
			}
			else if(ctx.getResources().getString(R.string.CAPTURE_CHECK_FRONT).equals(cameraAction))
			{
				//Here capture only front
				openCustomCamera(ACTION_CAPTURE_ONLY_FRONT);
			}
			else if(ctx.getResources().getString(R.string.CAPTURE_CHECK_BACK).equals(cameraAction))
			{
				//Here capture only back
				openCustomCamera(ACTION_CAPTURE_ONLY_BACK);
			}
			else if(ctx.getResources().getString(R.string.CHECK_REVIEW_FRONT).equals(cameraAction))
			{
				//Here view only front
				openCustomCamera(ACTION_VIEW_ONLY_FRONT);
			}
			else if(ctx.getResources().getString(R.string.CHECK_REVIEW_BACK).equals(cameraAction))
			{
				//Here view only back
				openCustomCamera(ACTION_VIEW_ONLY_BACK);
				
			}
			else if(ctx.getResources().getString(R.string.BACK_CONFIRM_AUTHORIZE).equals(cameraAction))
			{
				//Here view back
				openCustomCamera(ACTION_VIEW_BACK);
			}
			else if(ctx.getResources().getString(R.string.INJECT_CHECKS_CAPTURES).equals(cameraAction))
			{
				//Here inject checks captures into webView
				injectCheckPicturesIntoWebView();
			}
			else if(ctx.getResources().getString(R.string.REMOVE_CHECK_IMAGES).equals(cameraAction))
			{
				//Remove Images
				removeCheckImages();
			}
			else
			{
				//do nothing
			}
			
			pluginResult = new PluginResult(Status.OK);
		}
		catch (JSONException e)
		{
			Log.w(TAG, "Got JSON Exception params. Message :" + e.getMessage());
			pluginResult = new PluginResult(Status.JSON_EXCEPTION);
		}
		
		Log.d(TAG, "Exit to Plugin execute");
		
		return pluginResult;
	}
	
	
	private void openCustomCamera(final int action)
	{	
		Log.d(TAG, "Enter openCustomCamera(" + action + ")");
		
		final Intent intent = new Intent(String.valueOf(action), null, ctx.getApplicationContext(), MyCustomCameraActivity.class);
		this.ctx.startActivityForResult((Plugin) this, intent, REQUEST_CAMERA_ACTIVITY);		
		
		Log.d(TAG, "Exit openCustomCamera()");
	}
	
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{	
		Log.d(TAG, "Enter onActivityResult()");
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(REQUEST_CAMERA_ACTIVITY == requestCode)
		{	
			if(shouldRedirectToEnterAmmount)
			{
				removeCheckImages();
				// super.webView.loadUrl(ctx.getResources().getString(R.string.ENTER_AMOUNT_PAGE));
				this.changePage( R.string.ENTER_AMOUNT_PAGE );
				MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
			}
			else if(shouldRedirecToSelectAccount)
			{
				removeCheckImages();
				// super.webView.loadUrl(ctx.getResources().getString(R.string.SELECT_ACCOUNT_PAGE));
				this.changePage( R.string.SELECT_ACCOUNT_PAGE );
				MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
			}
			else
			{
				// super.webView.loadUrl(ctx.getResources().getString(R.string.CONFIRM_AUTHORIZE_PAGE));
				this.changePage( R.string.CONFIRM_AUTHORIZE_PAGE );
			}
		}
		
		Log.d(TAG, "Exit onActivityResult()");
	}
	
	public static void setShouldRedirecToEnterAmmount(boolean redirectoToEnterAmmount)
	{
		Log.d(TAG, "Enter setShouldRedirecToEnterAmmount(" + redirectoToEnterAmmount + ")");
		shouldRedirectToEnterAmmount = redirectoToEnterAmmount;
		Log.d(TAG, "Exit setShouldRedirecToEnterAmmount(" + redirectoToEnterAmmount + ")");

	}

	public static void setShouldRedirecToSelectAccount(boolean redirectoToSelectAccount)
	{
		Log.d(TAG, "Enter setShouldRedirecToSelectAccount(" + redirectoToSelectAccount + ")");
		shouldRedirecToSelectAccount = redirectoToSelectAccount;
		Log.d(TAG, "Exit setShouldRedirecToSelectAccount(" + redirectoToSelectAccount + ")");
	}
	
	private void removeCheckImages()
	{
		Log.d(TAG, "Enter removeCheckImages()");
		
		removeFile(getFrontImageFilename());
		removeFile(getBackImageFilename());
		
		Log.d(TAG, "Exit removeCheckImages()");
	}
	
	private void injectCheckPicturesIntoWebView()
	{
		Log.d(TAG, "Enter injectCheckPicturesIntoWebView()");
		
		String stringEncodedImage = "";
		String filename = getFrontImageFilename();
		File file = new File(ctx.getApplicationContext().getFilesDir() + "/" + filename);
		if(file.exists())
		{			
			try
			{
				if(hasEncodeErrorInFront)
				{
					String javaScriptRemoveErrorForFront = CameraUtils.getJSToRevemoEncodeError(CameraUtils.ERROR_ENCODE_FRONT);
					super.webView.loadUrl(javaScriptRemoveErrorForFront);
					
					hasEncodeErrorInFront = false;
				}
				
				stringEncodedImage = Base64.encodeBytes(readFromLocalStorage(filename), Base64.NO_OPTIONS);
			}
			catch (IOException e)
			{
				hasEncodeErrorInFront = true;
				
				//Prepare javascript injection to display error
				String javaScriptErrorForFront = CameraUtils.getJSEncodeError(CameraUtils.ERROR_ENCODE_FRONT, ctx.getResources().getString(R.string.encodeBase64Error));
				super.webView.loadUrl(javaScriptErrorForFront);
				
				Log.e(TAG, e.getMessage());
			}
		}
		
		String javaScriptCode = CameraUtils.getJSImageInjectCode(stringEncodedImage, ctx.getResources().getString(R.string.FRONT_CHK_ELEMENT_ID));					
		super.webView.loadUrl(javaScriptCode);
		
		filename = getBackImageFilename();
		
		file = new File(ctx.getApplicationContext().getFilesDir() + "/" + filename);
		stringEncodedImage = "";
		if(file.exists())
		{
			try
			{
				if(hasEncodeErrorInBack)
				{
					String javaScriptRemoveErrorForBack = CameraUtils.getJSToRevemoEncodeError(CameraUtils.ERROR_ENCODE_BACK);
					super.webView.loadUrl(javaScriptRemoveErrorForBack);
					
					hasEncodeErrorInBack = false;
				}
				
				stringEncodedImage = Base64.encodeBytes(readFromLocalStorage(filename), Base64.NO_OPTIONS);
			}
			catch (IOException e)
			{
				hasEncodeErrorInBack = true;
				
				//Prepare javascript injection to display error
				String javaScriptErrorForBack = CameraUtils.getJSEncodeError(CameraUtils.ERROR_ENCODE_BACK, ctx.getResources().getString(R.string.encodeBase64Error));
				super.webView.loadUrl(javaScriptErrorForBack);
				
				Log.e(TAG, e.getMessage());
			}
		}
		
		javaScriptCode = CameraUtils.getJSImageInjectCode(stringEncodedImage, ctx.getResources().getString(R.string.BACK_CHK_ELEMENT_ID));		
		super.webView.loadUrl(javaScriptCode);
		
		Log.d(TAG, "Exit injectCheckPicturesIntoWebView()");
	}
	
	private String getBackImageFilename()
	{
		Log.d(TAG, "Enter getBackImageFilename()");
		Log.d(TAG, "Exit getBackImageFilename()");
		return ctx.getResources().getString(R.string.BACK_CHECK_FILENAME);
	}

	private String getFrontImageFilename() 
	{
		Log.d(TAG, "Enter getFrontImageFilename()");
		Log.d(TAG, "Exit getFrontImageFilename()");
		return ctx.getResources().getString(R.string.FRONT_CHECK_FILENAME);
	}
	
	private byte[] readFromLocalStorage(String filename) 
	{
		Log.d(TAG, "Enter readFromLocalStorage(" + filename + ")");
		
		byte[] data = new byte[0];

		try {
			FileInputStream fis = ctx.getApplicationContext().openFileInput(filename);
			File file = new File(ctx.getApplicationContext().getFilesDir() + "/" + filename);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
		} catch (Exception ex) {

		}
		
		Log.d(TAG, "Exit readFromLocalStorage(" + filename + ")");

		return data;
	}
	
	private void removeFile(String filename)
	{
		Log.d(TAG, "Enter removeFile(" + filename + ")");

		File aFile = new File(ctx.getApplicationContext().getFilesDir() + "/" + filename);
		if(aFile.exists())
		{
			aFile.delete();
		}
		
		Log.d(TAG, "Exit removeFile(" + filename + ")");
	}

	private void changePage( final int id ) {
		final String page = ctx.getResources().getString( id );
		final String js = new StringBuilder( "javascript:" ).append( "discover.mobile.remoteDepositCapture.changePage('" ).append( page ).append( "');" ).toString();
		
		super.webView.loadUrl( js );
	}
}
