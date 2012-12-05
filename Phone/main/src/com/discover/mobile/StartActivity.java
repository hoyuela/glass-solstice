package com.discover.mobile;

import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.PushNotificationService;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.login.LoginActivity;
import com.google.inject.Inject;

@ContentView(R.layout.start_landing)
public class StartActivity extends RoboActivity {
	
	private static final String TAG = StartActivity.class.getSimpleName();
	
	private static final String PACKAGE_NAME = "com.discover.mobile.DiscoverMobileActivity";
	private static final int OPTIONAL_UPGRADE_DAYS = 30;
	
	private static final int NO_DATE = 0;
	
	private static final int OPTIONAL_UPGRADE = 1;
	private static final int FORCED_UPGRADE = 2;
	
	private static final long DAY_IN_MILLISECONDS = 86400000;
	
	private static final String DATETIME_KEY = "com.discover.mobile.optionalupdatedate";

// BUTTONS
	
	@InjectView(R.id.card_login_button)
	private Button creditCardLoginButton;
	
	@Inject
	private PushNotificationService pushNotificationService;
    
// TEXT LABELS
	
	@InjectView(R.id.title_label)
	private TextView titleLabel;
	
	@InjectView(R.id.subtitle_label)
	private TextView subtitleLabel;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TrackingHelper.startActivity(this);
		TrackingHelper.trackPageView(AnalyticsPage.STARTING);
	}
	
	@Override
	public void onStart(){
		super.onStart();
				
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		final boolean hasUserLoggedOut = extras.getBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE);
    		showLogoutMessageIfTrue(hasUserLoggedOut);
    	}
    	
    	setupButtons();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		setupButtons();
		pushNotificationService.start();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		hideErrorLabels();
	}
	
	public void showLogoutMessageIfTrue(final boolean hasUserLoggedOut) {
		if(hasUserLoggedOut) {
			titleLabel.setText(getString(R.string.successful_logout_title_text));
			subtitleLabel.setText(getString(R.string.successful_logout_subtitle_text));
			showLabel(titleLabel);
			showLabel(subtitleLabel);
		}
		else {/*User has not logged out*/}
	}
	
	public void hideErrorLabels(){
		hideLabel(titleLabel);
		hideLabel(subtitleLabel);
	}
	
	public void showLabel(final View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	public void hideLabel(final View v) {
		v.setVisibility(View.GONE);
	}

	private void setupButtons() {
		//Setup the button that takes us to the login screen.
		creditCardLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startPreAuthCheck();
			}
		});
	}
	
	public void startPreAuthCheck() {
		final SuccessListener<PreAuthResult> optionalUpdateListener = new SuccessListener<PreAuthResult>() {
			@Override
			public CallbackPriority getCallbackPriority() {
				return CallbackPriority.MIDDLE;
			}
			
			@Override
			public void success(final PreAuthResult value) {
				Log.d(TAG, "Pre-auth status code: " + value.statusCode);
				
				//check if optional upgrade available
				if(shouldPresentOptionalUpdate(value.upgradeDescription)) {
					TrackingHelper.trackPageView(AnalyticsPage.OPTIONAL_UPGRADE);
					showUpgradeAlertDialog("Upgrade", 
							value.upgradeDescription, 
							OPTIONAL_UPGRADE);
				} else {
					navigateToLogin();
				}
			}
		};
		final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback.<PreAuthResult>builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(optionalUpdateListener)
				
				// FIXME DO NOT COPY THIS CODE
				.withErrorResponseHandler(new ErrorResponseHandler() {
					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}
					
					@Override
					public boolean handleFailure(final ErrorResponse<?> errorResponse) {
						if(errorResponse instanceof JsonMessageErrorResponse)
							return handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
						
						return false;
					}
					
					public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
						// FIXME named constants
						switch(messageErrorResponse.getMessageStatusCode()) {
							case 1002: 
								TrackingHelper.trackPageView(AnalyticsPage.FORCED_UPGRADE);
								showUpgradeAlertDialog("Upgrade", 
										"Your Discover app is out of date. You must update before continuing.", 
										FORCED_UPGRADE);
								removeDateFromPrefs();
								return true;
								
							case 1006:
							case 1007: 
								sendToMaintenancePage();
								return true;
							default:
								break;
						}
						
						return false;
					}
				})
				
				.build();
		
		new PreAuthCheckCall(this, callback).submit();
	}
	
	private void sendToMaintenancePage() {
		final Intent maintenancePageIntent = new Intent(StartActivity.this, LockOutUserActivity.class);
		ScreenType.MAINTENANCE.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
	}
	
	private static final int DOES_NOT_EXIST = 0;
	
	private boolean shouldPresentOptionalUpdate(final String updateDescription) {
		if(updateDescription != null) {
			final SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
			
			final long savedDate = prefs.getLong(DATETIME_KEY, DOES_NOT_EXIST);
			
			if (savedDate == DOES_NOT_EXIST) {
				final SharedPreferences.Editor editor=prefs.edit();
				editor.putLong(DATETIME_KEY, new Date().getTime());
				editor.commit();
				return true;
			}
			
			final long currentDate = new Date().getTime();
			final long daysDifference = (currentDate - savedDate)/DAY_IN_MILLISECONDS;
			
			if(daysDifference >= OPTIONAL_UPGRADE_DAYS) {
				updateDateInPrefs();
//				removeDateFromPrefs();
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
	private void updateDateInPrefs() {
		final SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
		
		final SharedPreferences.Editor editor=prefs.edit();
		editor.putLong(DATETIME_KEY, new Date().getTime());
		editor.commit();
	}
	
	//TODO why is the date being removed instead of updated? this causes the optional upgrade message to appear >1 time
	private void removeDateFromPrefs() {
		final SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
		
		final long savedDate = prefs.getLong(DATETIME_KEY, NO_DATE);
		
		if(savedDate != NO_DATE) {
			final SharedPreferences.Editor editor=prefs.edit();
			editor.remove(DATETIME_KEY);
			editor.commit();
		}
	}
	
	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
			    .setTitle(title)
			    .setMessage(message)
			    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						finish();
					}
				})
			    .show();
	}
	
	private void showUpgradeAlertDialog(final String title, final String message, final int typeOfUpgrade) {
		final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		
		alertBuilder.setTitle(title)
	    		.setMessage(message)
	    		.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						upgrade();
					}
				});
				
		if (typeOfUpgrade == OPTIONAL_UPGRADE)
			alertBuilder.setNegativeButton("Cancel", null);
		
	    alertBuilder.show();
	}
	
	private void upgrade() {
		final Uri marketUri = Uri.parse("market://details?id=" + PACKAGE_NAME);
		final Intent androidMarketplaceIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		startActivity(androidMarketplaceIntent);
	}
	
	private void navigateToLogin() {
		final Intent loginActivity = new Intent(this, LoginActivity.class);
		this.startActivity(loginActivity);
	}
	
}
