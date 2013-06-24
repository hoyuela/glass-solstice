package com.discover.mobile.common.utils;

import java.security.SecureRandom;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.discover.mobile.common.R;

public class PasscodeUtils {

	private static final String TAG = "PasscodeUtils";
    private static final String APP_SHARED_PREFS = PasscodeUtils.class.getSimpleName(); //  Name of the file -.xml
	public static final String PREFS_HAS_SEEN_OVERVIEW = "hasUserSeenOverview";
    private SharedPreferences _sharedPrefs;
    private Editor _prefsEditor;
    
    public static final String PREFS_NAME = "PasscodePrefsFile";
	public static final String PREFS_TOKEN = "token";
	public static final String PREFS_USERS_FIRST_NAME = "fname";
	public static final String PREFS_FORGOT_PASSCODE = "forgot_passcode";

    public PasscodeUtils(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

	/*
	 * Has user seen the passcode overview modal? Happens once per device.
	 */
	public boolean hasSeenOverview() {
		return _sharedPrefs.getBoolean(PREFS_HAS_SEEN_OVERVIEW, false);
	}

	public void setOverviewSeen() {
		_prefsEditor.putBoolean(PREFS_HAS_SEEN_OVERVIEW, true);
		_prefsEditor.commit();
	}

	public String getFirstName() {
		return _sharedPrefs.getString(PREFS_USERS_FIRST_NAME, null);
	}

	public String getPasscodeToken() {
		return _sharedPrefs.getString(PREFS_TOKEN, null);
	}
	
	public void deletePasscodeToken() {
		_prefsEditor.remove(PREFS_TOKEN);
		_prefsEditor.commit();
	}
	
	public boolean isForgotPasscode() {
		return _sharedPrefs.getBoolean(PREFS_FORGOT_PASSCODE, false);
	}

	public boolean doesDeviceTokenExist() {
		return (_sharedPrefs.contains(PREFS_TOKEN));
	}
	
	public void setForgotPasscode(boolean status){
		_prefsEditor.putBoolean(PREFS_FORGOT_PASSCODE, status);
		_prefsEditor.commit();
	}

	public void createPasscodeToken(String token) {
		if (_sharedPrefs.contains(PREFS_TOKEN)) {
			// TODO can't write token when one already exists
			return;
		}

		// write token to shared preference "token"
		_prefsEditor.putString(PREFS_TOKEN, token);
		_prefsEditor.commit();
	}
	
	public void storeFirstName(String fname) {
		_prefsEditor.putString(PREFS_USERS_FIRST_NAME, fname);
		_prefsEditor.commit();
	}

	public void deleteFirstName() {
		_prefsEditor.remove(PREFS_USERS_FIRST_NAME);
		_prefsEditor.commit();
	}
	
	public boolean isPasscodeToken() {
		return (_sharedPrefs.contains(PREFS_TOKEN));
	}
	
	public static String genClientBindingToken() {
		SecureRandom scrRndm = new SecureRandom();
		byte[] random = new byte[64];// 64 byte per INFO SEC
		scrRndm.nextBytes(random);
		return new String(Base64.encodeToString(random, Base64.NO_WRAP));
	}
	
	public static boolean isPasscodeValidLocally(String passcode) {
		// i.e. 1,2,3,4
		if (isNumberSequential(0, passcode, 1)) {
			return false;
		}
		// i.e. 4,3,2,1
		if (isNumberSequential(0, passcode, -1)) {
			return false;
		}
		// i.e. 2,2,2,2
		if (isNumberSequential(0, passcode, 0)) {
			return false;
		}
		if ("6011".equals(passcode)) {
			return false;
		}
		return true;
	}

	private static boolean isNumberSequential(int position, String s, int step) {
		if (position >= s.length() - 1) {
			return true;
		}
		int n1 = Character.getNumericValue(s.charAt(position));
		int n2 = Character.getNumericValue(s.charAt(position + 1));
		if (n1 + step != n2) {
			return false;
		} else {
			return isNumberSequential(position + 1, s, step);
		}
	}
	
	public static boolean isCharNumeric(CharSequence paramCharSequence) {
		if (paramCharSequence.length() != 1) {
			return false;
		}
		return new String("1234567890").contains(paramCharSequence);
	}
	
	public static boolean isCharEmpty(CharSequence paramCharSequence) {
		return (paramCharSequence.length() == 0);
	}
	
	public String getWelcomeMessage() {
		StringBuffer sb = new StringBuffer("Good ");
		if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12)
			sb.append("Morning, ").append(getFirstName());
		else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18)
			sb.append("Afternoon, ").append(getFirstName());
		else
			sb.append("Evening, ").append(getFirstName());
		return sb.toString();
	}
	
	public void dialogHelper(FragmentActivity activity, int viewLayout, String buttonText, boolean isOrangeButton, Runnable action, Runnable backAction){

		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(viewLayout, null);

		// make dialog fill screen
		Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		view.setMinimumHeight(height);
		view.setMinimumWidth(width);

		Builder builder = new AlertDialog.Builder(activity);
		builder.setView(view)
				.setPositiveButton(buttonText, new MyClickListener(action))
				.setOnKeyListener(new MyKeyListener(backAction));

		AlertDialog dialog = builder.create();
		dialog.show();

		if (isOrangeButton) {
			// Orange button
			Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			if (b != null) {
				b.setBackgroundColor(activity.getResources().getColor(R.color.passcodeOrange));
				b.setTextColor(activity.getResources().getColor(R.color.white));
			}
		} else {
			// gray button
			Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			if (b != null) {
				b.setBackgroundColor(activity.getResources().getColor(R.color.passcodeGray));
				b.setTextColor(activity.getResources().getColor(R.color.black));
			}
		}
	}
	// Fires off specified action on back button pressed
	private class MyClickListener implements DialogInterface.OnClickListener {

		private Runnable action;

		public MyClickListener(Runnable action) {
			this.action = action;
		}

		public Runnable getAction() {
			return action;
		}

		@Override
		public void onClick(DialogInterface dialog, int arg1) {
			// TODO Auto-generated method stub
			Log.v(TAG, "About to execute action");
			if (getAction() != null) {
				Log.v(TAG, "Action executed!");
				getAction().run();
			}
			dialog.dismiss();
		}
	}
	
	private class MyKeyListener implements DialogInterface.OnKeyListener {

		private Runnable action;

		public MyKeyListener(Runnable action) {
			this.action = action;
		}

		public Runnable getAction() {
			return action;
		}

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			//TODO action to take on back button press
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_UP
					&& !event.isCanceled()) {
				Log.v(TAG, "About to execute action");
				if (getAction() != null) {
					Log.v(TAG, "Back button action executed!");
					getAction().run();
				} else {
					Log.e(TAG, "Back button action is null");
				}
				dialog.dismiss();
				return true;
			}
			return false;
		}
	}
}
