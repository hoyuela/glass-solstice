package com.discover.mobile.bank.atm;

import java.util.Date;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.LocationPreferenceCache;
import com.discover.mobile.common.utils.StringUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * AtmTapAndHoldCoachOverlay handles displaying the Tap and Hold Coach over the ATM locator. 
 * 
 * @author Stephen Farr
 */
public class AtmTapAndHoldCoachOverlay extends RelativeLayout {

	/* Main view of the Tap and Hold Coach */
	private RelativeLayout view;
	
	/** Length of time the view will appear */
	private final int LIFESPAN = 5000;
	
	/** Animation Duration */
	private final int ANIMATIONDURATION = 1000;
	
	/** Milliseconds in a 90 day period */
	private final static long MILLISECONDS_IN_NINETY_DAYS = 7776000000L;
	
	/** Holds a flag to whether or not the view is shown. */
	private boolean isShowing = false;
	
	/* Persistent storage keys for the tap and hold feature to keep track of the last time it was used/coach was shown */
	private static final String PREFS_FILE = "UpdatePreferences";
	private static final String LAST_USED_KEY = "TAP_AND_HOLD_LAST_USED";
	
	/* ------------------------------ Constructors ------------------------------ */
	
	/**
	 * Constructor for AtmTapAndHoldCoachOverlay.  Initializes the view based on the related layout files.
	 * 
	 * @param context
	 */
	public AtmTapAndHoldCoachOverlay(Context context) {
		super(context);
		
		this.initializeTapAndHoldCoach(context);
	}
	
	/**
	 * Constructor for AtmTapAndHoldCoachOverlay.  Initializes the view based on the related layout files.
	 * 
	 * @param context
	 * @param attrs
	 */
	public AtmTapAndHoldCoachOverlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.initializeTapAndHoldCoach(context);
	}
	
	/**
	 * Constructor for AtmTapAndHoldCoachOverlay.  Initializes the view based on the related layout files.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AtmTapAndHoldCoachOverlay(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		this.initializeTapAndHoldCoach(context);
	}
	
	/* ------------------------------ Public Static Methods ------------------------------ */
	
	/**
	 * Determines if it has been 90 days since the user has used this feature.
	 * 
	 * @return boolean representing whether or not its been 90 days since last use of feature.
	 */
	public static boolean shouldShowCoachOverlay() {
		//Grabs the stored data in the shared preferences related to the last time the tap and hold feature was used or
		//the coach was seen
		final SharedPreferences prefs = DiscoverActivityManager.getActiveActivity().
																getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		final long lastUsedDate = prefs.getLong(AtmTapAndHoldCoachOverlay.getLastUseKey(), MILLISECONDS_IN_NINETY_DAYS + 1);
		
		//Checks to see if the time between today and the last used date is greater than the number of milliseconds in a
		//90 day period.
		return (MILLISECONDS_IN_NINETY_DAYS < new Date().getTime() - lastUsedDate);
	}
	
	/**
	 * Sets the last used date as todays date.
	 */
	public static void setFeatureWasUsed() {
		//Stores the last time the tap and hold feature was accessed.
		final SharedPreferences prefs = DiscoverActivityManager.getActiveActivity().
																getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putLong(AtmTapAndHoldCoachOverlay.getLastUseKey(), new Date().getTime());
		editor.commit();
	}
	
	/* ------------------------------ Publicly Exposed Methods ------------------------------ */
	
	/**
	 * Returns whether the view is showing or not.
	 */
	public boolean isShowing() {
		return this.isShowing;
	}
	
	/**
	 * Sets whether the view is showing or not.
	 * 
	 * @param isShowing - Flag to whether the view is showing or not.
	 */
	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}
	
	public void showCoach() {
		this.setVisibility(View.VISIBLE);
		this.startAnimation(this.createFadeInAnimation());
		
		this.runLifeCycle();
		this.setShowing(true);
	}
	
	public void dismissCoach() {
		this.setVisibility(View.GONE);
		this.isShowing = false;
		AtmTapAndHoldCoachOverlay.setFeatureWasUsed();
	}
	
	/* ------------------------------ Private Helper Methods ------------------------------ */
	
	/**
	 * Initializes the click listeners and layout of the Tap and Hold Coach
	 * 
	 * @param context
	 */
	private void initializeTapAndHoldCoach(Context context) {
		if (shouldShowCoachOverlay()) {
			view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bank_atm_redo_search_coach, null);
			
			this.addView(view);	
			this.runLifeCycle();
		}
	}
	
	
	/**
	 * Performs the fade in animation for AtmTapAndHoldCoachOverlay and displays the view.
	 */
	private Animation createFadeInAnimation() {
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(ANIMATIONDURATION);
		
		return fadeIn;
	} 
	
	/**
	 * Performs the fade out animation for AtmTapAndHoldCoachOverlay
	 */
	private void causeFadeOutAnimation() {
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setDuration(ANIMATIONDURATION);
		fadeOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.GONE);
				AtmTapAndHoldCoachOverlay.setFeatureWasUsed();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
		});
		
		this.startAnimation(fadeOut);
		this.setShowing(false);
	}
	
	/*
	 * Shows the AtmTapAndHoldCoachOverlay 5 seconds than calls disappear.
	 */
	private void runLifeCycle() {
		
		//After causing the view to appear count out 5 seconds than disappear.
		this.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isShowing) {
					causeFadeOutAnimation();
				}	
			}
		}, LIFESPAN);
	}
	
	
	/**
	 * Returns the key to store/get the data from the SharedPreferences
	 * 
	 * @return key to use.
	 */
	private static String getLastUseKey() {
		String key = LAST_USED_KEY;
		if (Globals.isLoggedIn()) {
			key = DiscoverApplication.getLocationPreference().getMostRecentUser() + LAST_USED_KEY;
		}
		return key;
	}
}