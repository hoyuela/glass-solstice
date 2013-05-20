/*
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) 2005-2011, Nitobi Software Inc.
 * Copyright (c) 2010-2011, IBM Corporation
 */
package com.discover.mobile.card.phonegap.plugins;

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
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.common.utils.Utils;

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

    @Override
    public boolean execute(final String action, final String rawArgs,
            final CallbackContext callbackContext) throws JSONException {
        callbackContext1 = callbackContext;
        Utils.log(LOG_TAG, "---data-- " + rawArgs);
        jsonArray = new JSONArray(rawArgs);

        if (action.equals("showWebPage")) {
            Utils.log("@@@ ChildWeb", action);
            // If the ChildBrowser is already open then throw an error
            if (dialog != null && dialog.isShowing()) {
                Utils.log("123 dailog", rawArgs);
                callbackContext.error("ChildBrowser is already open");
                return false;
            }

            final String url = jsonArray.getString(0);
            Utils.log("dailog", url);
            JSONObject options = null;
            try {
                if (jsonArray.getJSONObject(1) != null) {
                    Utils.log(LOG_TAG, "--1--");
                    options = jsonArray.getJSONObject(1);
                    Utils.log(LOG_TAG, "--1 --" + options);
                }
            } catch (final JSONException e) {
                e.printStackTrace();
                Utils.log(LOG_TAG, e.getMessage(), e);
            }
            Utils.log(LOG_TAG, "---2---" + url);
            this.showWebPage(url, options);

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            callbackContext.sendPluginResult(pluginResult);
            return true;

        } else {
            Utils.log("%% Else of Child Browser", rawArgs);
            final PluginResult pluginResult = new PluginResult(
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
    public String openExternal(final String url, final boolean usePhoneGap) {
        try {
            Intent intent = null;
            if (usePhoneGap) {

                intent = new Intent().setClass(cordova.getActivity()
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

            final CardNavigationRootActivity activity = (CardNavigationRootActivity) cordova
                    .getActivity();
            activity.overridePendingTransition(
                    com.discover.mobile.card.R.anim.slide_in_up,
                    com.discover.mobile.card.R.anim.slide_out_down);

            cordova.getActivity().startActivity(intent);
            return "";
        } catch (final android.content.ActivityNotFoundException e) {
            Utils.log(LOG_TAG, "ChildBrowser: Error loading url " + url + ":"
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
        return showLocationBar;
    }

    /**
     * Display a new browser with the specified URL.
     * 
     * @param url
     *            The url to load.
     * @param jsonObject
     */
    public void showWebPage(final String url, final JSONObject options) {
        // Determine if we should hide the location bar.
        if (options != null) {
            showLocationBar = options.optBoolean("showLocationBar", true);
        }
        Utils.log("@@@ showWebPage", url);
        // Create dialog in new thread
        final Runnable runnable = new Runnable() {
            @Override
            @SuppressWarnings("deprecation")
            public void run() {
                dialog = new Dialog(ChildBrowser.this.cordova.getActivity(),
                        android.R.style.Theme_Translucent_NoTitleBar);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        try {
                            final JSONObject obj = new JSONObject();
                            obj.put("type", CLOSE_EVENT);

                            sendUpdate(obj, false);
                        } catch (final JSONException e) {
                            Utils.log(LOG_TAG, "Should never happen");
                        }
                    }
                });

                // ** +added code to purple-cabbage **
                final RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                logoParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                final RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                // bottom
                final LinearLayout.LayoutParams wvParams = new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.FILL_PARENT,
                        android.view.ViewGroup.LayoutParams.FILL_PARENT);

                final LinearLayout main = new LinearLayout(
                        ChildBrowser.this.cordova.getActivity()
                                .getApplicationContext());
                main.setOrientation(LinearLayout.VERTICAL);

                final RelativeLayout toolbar = new RelativeLayout(
                        ChildBrowser.this.cordova.getActivity()
                                .getApplicationContext());

                toolbar.setBackgroundResource(R.drawable.action_bar_background);
                toolbar.setLayoutParams(new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.FILL_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

                final TextView headerText = new TextView(
                        ChildBrowser.this.cordova.getActivity()
                                .getApplicationContext());
                headerText.setId(3);
                headerText.setLayoutParams(logoParams);
                headerText.setTextAppearance(ChildBrowser.this.cordova
                        .getActivity().getApplicationContext(),
                        R.style.action_bar_text);
                headerText.setText(ChildBrowser.this.cordova.getActivity()
                        .getApplicationContext()
                        .getString(R.string.provide_feedback_title));

                // ** _added code to purple-cabbage **

                final ImageButton close = new ImageButton(
                        ChildBrowser.this.cordova.getActivity()
                                .getApplicationContext());
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        closeDialog();
                    }
                });
                close.setId(4);

                close.setImageResource(R.drawable.btn_close);
                close.setBackgroundResource(R.drawable.action_bar_background);
                close.setLayoutParams(closeParams);

                // Changed as getting force close if submit is clicked without
                // rating
                webview = new WebView(ChildBrowser.this.cordova.getActivity());
                webview.setWebChromeClient(new WebChromeClient());
                final WebSettings settings = webview.getSettings();
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
                toolbar.addView(headerText);

                if (getShowLocationBar()) {
                    main.addView(toolbar);
                }
                main.addView(webview);

                final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;
                lp.height = android.view.ViewGroup.LayoutParams.FILL_PARENT;

                try {
                    dialog.setContentView(main);
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

        };
        cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Create a new plugin result and send it back to JavaScript
     * 
     * @param obj
     *            a JSONObject contain event payload information
     */
    private void sendUpdate(final JSONObject obj, final boolean keepCallback) {
        final PluginResult result = new PluginResult(PluginResult.Status.OK,
                obj);
        result.setKeepCallback(keepCallback);
        callbackContext1.sendPluginResult(result);
    }

}
