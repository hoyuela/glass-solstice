package com.discover.mobile.bank.atm;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.FrozenUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
	
	/** Holds a flag to whether or not the view is shown. */
	private boolean isShowing = false;
	
	/** Reference to the UI to be frozen */
	private FrozenUI delegate;
	
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
	
	/* ------------------------------ Publicly Exposed Methods ------------------------------ */
	
	/**
	 * Determines if it has been 90 days since the user has used this feature.
	 * 
	 * @return boolean representing whether or not its been 90 days since last use of feature.
	 */
	public static boolean shouldShowCoachOverlay() {
		return true;
	}
	
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
	
	/**
	 * Sets the delegate and disables the delegates UI
	 * 
	 * @param delegate
	 */
	public void setDelegate(FrozenUI delegate) {
		this.delegate = delegate;
		
		if (this.delegate != null) {
			this.delegate.disableUI();	
		}
	}
	
	/**
	 * Clears the delegate
	 */
	public void clearDelegate() {
		this.delegate = null;
	}
	
	public void showCoach() {
		this.setVisibility(View.VISIBLE);
		this.startAnimation(this.createFadeInAnimation());
		
		this.runLifeCycle();
		this.setShowing(true);
	}
	
	/* ------------------------------ Private Helper Methods ------------------------------ */
	
	/**
	 * Initializes the click listeners and layout of the Tap and Hold Coach
	 * 
	 * @param context
	 */
	private void initializeTapAndHoldCoach(Context context) {
		view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bank_atm_redo_search_coach, null);
		
		this.addClickListenerToXButton();
		this.addView(view);
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
				delegate.enableUI();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
		});
		
		this.startAnimation(fadeOut);
		this.setShowing(false);
	}
	
	/**
	 * Adds the click listener to the X button to make the AtmTapAndHoldCoachOverlay disappear.
	 */
	private void addClickListenerToXButton() {
		ImageButton closeButton = (ImageButton) view.findViewById(R.id.atm_tap_new_search_exit_button);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				causeFadeOutAnimation();
			}
		});
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
}