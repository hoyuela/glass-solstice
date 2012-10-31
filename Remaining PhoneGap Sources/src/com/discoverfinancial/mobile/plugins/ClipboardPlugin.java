package com.discoverfinancial.mobile.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.text.ClipboardManager;

// By purple cabbage
// modified by Cognizant

public class ClipboardPlugin extends Plugin {

	private static final String setText = "setText";
	private static final String getText = "getText";
	private static final String errorParse = "Couldn't get the text to copy";
	private static final String errorUnknown = "Unknown Error";

	private ClipboardManager mClipboardManager;


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
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		// If we do not have the clipboard
		if(mClipboardManager == null)
			// get it
			mClipboardManager = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);

		// Copy
		if (action.equals(setText)) {
			String arg = "";
			try {
				arg = (String) args.get(0);
				mClipboardManager.setText(arg);
			} catch (JSONException e) {
				return new PluginResult(PluginResult.Status.ERROR, errorParse);
			} catch (Exception e) {
				return new PluginResult(PluginResult.Status.ERROR, errorUnknown);
			}
			return new PluginResult(PluginResult.Status.OK, arg);

		// Paste
		} else if (action.equals(getText)) {
			String arg = (String) mClipboardManager.getText();
			if (arg == null) {
				arg = "";
			}
			return new PluginResult(PluginResult.Status.OK, arg);
		} else {
			return new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
	}

}
