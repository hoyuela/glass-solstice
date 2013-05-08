/*
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) 2005-2011, Nitobi Software Inc.
 * Copyright (c) 2010-2011, IBM Corporation
 */
package com.discover.mobile.card.phonegap.plugins;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.card.R;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;

public class ChildBrowser extends CordovaPlugin {

	protected static final String LOG_TAG = "ChildBrowser";
	private static int CLOSE_EVENT = 0;

	private Dialog dialog;
	private WebView webview;
	ImageView logoView;
	String logoDestination;
	// private EditText edittext;
	private boolean showLocationBar = true;

	CallbackContext callbackContext1;
	JSONArray jsonArray;

	private Bitmap loadDrawable(String filename) throws java.io.IOException {
		InputStream input = this.cordova.getActivity().getAssets()
				.open(filename);
		return BitmapFactory.decodeStream(input);
	}

	/**
	 * Executes the request and returns PluginResult.
	 * 
	 * @param action
	 *            The action to execute.
	 * @param args
	 *            JSONArry of arguments for the plugin.
	 * @param callbackId
	 *            The callback id used when calling back into JavaScript.
	 * @return A PluginResult object with a status and message.
	 */

	public boolean execute(String action, String rawArgs,
			CallbackContext callbackContext) throws JSONException {
		this.callbackContext1 = callbackContext;
		Log.d(LOG_TAG, "---data-- " + rawArgs);
		jsonArray = new JSONArray(rawArgs);

		if (action.equals("showWebPage")) {
			Log.d("@@@ ChildWeb", action);
			// If the ChildBrowser is already open then throw an error
			if (dialog != null && dialog.isShowing()) {
				Log.d("123 dailog", rawArgs);
				callbackContext.error("ChildBrowser is already open");
				return false;
			}

			// result = this.showWebPage(args.getString(0),
			// args.optJSONObject(1));
			String url = jsonArray.getString(0);
			Log.d("dailog", url);
			JSONObject options = null;
			try {
				if (jsonArray.getJSONObject(1) != null) {
					Log.i(LOG_TAG, "--1--");
					options = jsonArray.getJSONObject(1);
					Log.i(LOG_TAG, "--1 --" + options);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.i(LOG_TAG, e.getMessage(), e);
			}
			Log.d(LOG_TAG, "---2---" + url);
			this.showWebPage(url, options);

			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
			callbackContext.sendPluginResult(pluginResult);
			return true;

		} else {
			Log.d("%% Else of Child Browser", rawArgs);
			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.ERROR);
			callbackContext.sendPluginResult(pluginResult);
			return false;
		}
	}

	/**
	 * Display a new browser with the specified URL.
	 * 
	 * @param url
	 *            The url to load.
	 * @param usePhoneGap
	 *            Load url in PhoneGap webview
	 * @return "" if ok, or error message.
	 */
	public String openExternal(String url, boolean usePhoneGap) {
		try {
			Intent intent = null;
			if (usePhoneGap) {
				// intent = new Intent().setClass(this.ctx.getContext(),
				// org.apache.cordova.DroidGap.class);
				intent = new Intent().setClass(this.cordova.getActivity()
						.getApplicationContext(), DroidGap.class);
				intent.setData(Uri.parse(url)); // This line will be removed in
												// future.
				intent.putExtra("url", url);

				// Timeout parameter: 60 sec max - May be less if http device
				// timeout is less.
				intent.putExtra("loadUrlTimeoutValue", 60000);

				// These parameters can be configured if you want to show the
				// loading dialog
				intent.putExtra("loadingDialog", "Wait,Loading web page..."); // show
																				// loading
																				// dialog
				intent.putExtra("hideLoadingDialogOnPageLoad", true); // hide it
																		// once
																		// page
																		// has
																		// completely
																		// loaded
			} else {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
			}

			CardNavigationRootActivity activity = (CardNavigationRootActivity) this.cordova
					.getActivity();
			activity.overridePendingTransition(
					com.discover.mobile.card.R.anim.slide_in_up,
					com.discover.mobile.card.R.anim.slide_out_down);

			this.cordova.getActivity().startActivity(intent);
			return "";
		} catch (android.content.ActivityNotFoundException e) {
			Log.d(LOG_TAG,
					"ChildBrowser: Error loading url " + url + ":"
							+ e.toString());
			return e.toString();
		}
	}

	/**
	 * Closes the dialog
	 */
	private void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/**
	 * Should we show the location bar?
	 * 
	 * @return boolean
	 */
	private boolean getShowLocationBar() {
		return this.showLocationBar;
	}

	/**
	 * Display a new browser with the specified URL.
	 * 
	 * @param url
	 *            The url to load.
	 * @param jsonObject
	 */
	public void showWebPage(final String url, JSONObject options) {
		// Determine if we should hide the location bar.
		if (options != null) {
			showLocationBar = options.optBoolean("showLocationBar", true);
		}
		Log.d("@@@ showWebPage", url);
		// Create dialog in new thread
		Runnable runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				dialog = new Dialog(ChildBrowser.this.cordova.getActivity(),
						android.R.style.Theme_Translucent_NoTitleBar);

				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						try {
							JSONObject obj = new JSONObject();
							obj.put("type", CLOSE_EVENT);

							sendUpdate(obj, false);
						} catch (JSONException e) {
							Log.d(LOG_TAG, "Should never happen");
						}
					}
				});

				// ** +added code to purple-cabbage **
				RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				logoParams.addRule(RelativeLayout.CENTER_IN_PARENT);

				RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				//closeParams.setMargins(0, 10, 20, 30); // left, top, right, bottom
				LinearLayout.LayoutParams wvParams = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

				LinearLayout main = new LinearLayout(ChildBrowser.this.cordova
						.getActivity().getApplicationContext());
				main.setOrientation(LinearLayout.VERTICAL);

				RelativeLayout toolbar = new RelativeLayout(
						ChildBrowser.this.cordova.getActivity()
								.getApplicationContext());
				//toolbar.setBackgroundColor(R.drawable.action_bar_background);
				toolbar.setBackgroundResource(R.drawable.action_bar_background);
				toolbar.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				logoView = new ImageView(ChildBrowser.this.cordova
						.getActivity().getApplicationContext());
				logoView.setId(3);
				logoView.setLayoutParams(logoParams);

				if (logoDestination == null) {
					logoView.setImageResource(R.drawable.discover_blk_logo_login);

				} else {
					try {
						logoView.setImageBitmap(loadDrawable(logoDestination));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// ** _added code to purple-cabbage **

				ImageButton close = new ImageButton(ChildBrowser.this.cordova
						.getActivity().getApplicationContext());
				close.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						closeDialog();
					}
				});
				close.setId(4);

				close.setImageResource(R.drawable.btn_close);
				close.setBackgroundResource(R.drawable.action_bar_background);
				close.setLayoutParams(closeParams);

				/*CookieManager.setAcceptFileSchemeCookies(true);
				CookieManager.getInstance().setAcceptCookie(true);*/

				//Changed as getting force close if submit is clicked without rating
				webview = new WebView(ChildBrowser.this.cordova.getActivity());
				webview.setWebChromeClient(new WebChromeClient());
				WebSettings settings = webview.getSettings();
				settings.setJavaScriptEnabled(true);
				settings.setJavaScriptCanOpenWindowsAutomatically(true);
				settings.setBuiltInZoomControls(true);
				settings.setPluginsEnabled(true);
				settings.setDomStorageEnabled(true);
				webview.loadUrl(url);
				webview.setId(5);
				webview.setInitialScale(0);
				webview.setLayoutParams(wvParams);
				webview.requestFocus();
				webview.requestFocusFromTouch();

				toolbar.addView(close);
				toolbar.addView(logoView);

				if (getShowLocationBar()) {
					main.addView(toolbar);
				}
				main.addView(webview);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.FILL_PARENT;
				lp.height = WindowManager.LayoutParams.FILL_PARENT;

				try {
					dialog.setContentView(main);
					dialog.show();
					dialog.getWindow().setAttributes(lp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		this.cordova.getActivity().runOnUiThread(runnable);
	}

	/**
	 * Create a new plugin result and send it back to JavaScript
	 * 
	 * @param obj
	 *            a JSONObject contain event payload information
	 */
	private void sendUpdate(JSONObject obj, boolean keepCallback) {
		PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
		result.setKeepCallback(keepCallback);
		callbackContext1.sendPluginResult(result);
	}

}
