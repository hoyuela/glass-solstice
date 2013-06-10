package com.discover.mobile.bank.login;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.discover.mobile.analytics.BankTrackingHelper;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.atm.AtmLocatorActivity;
import com.discover.mobile.bank.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.error.BankExceptionHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankNetworkServiceCallManager;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.ContactUsType;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.bank.services.auth.BankLoginDetails;
import com.discover.mobile.bank.services.auth.PreAuthCheckCall;
import com.discover.mobile.bank.services.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.bank.ui.DiscoverToggleSwitch;
import com.discover.mobile.bank.ui.InvalidCharacterFilter;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.net.error.RegistrationErrorCodes;
import com.discover.mobile.common.ui.modals.SimpleContentModal;
import com.discover.mobile.common.ui.widgets.NonEmptyEditText;
import com.discover.mobile.common.utils.CommonUtils;
import com.discover.mobile.common.utils.EncryptionUtil;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;
import com.slidingmenu.lib.SlidingMenu;

/**
 * LoginActivity - This is the login screen for the application. It makes three
 * service calls - The first call is pre auth Second is starting xtify services
 * for push notifications And the third is the login call when a user tries to
 * login.
 *
 * @author scottseward, ekaram
 * 
 * Updated by hlin0 May 2013 --  quickview (aka fastcheck) 13.3
 *
 */

public class LoginActivity extends NavigationRootActivity implements LoginActivityInterface, OnClickListener {
	/* TAG used to print logs for the LoginActivity into logcat */
	private static final String TAG = LoginActivity.class.getSimpleName();
	final long halfSecond = 500;
	final int threeFifty = 350;
	
	/**
	 * These are string values used when passing extras to the saved instance
	 * state bundle for restoring the state of the screen upon orientation
	 * changes.
	 */
	private static final String PASS_KEY = "a";
	private static final String ID_KEY = "b";
	private static final String SAVE_ID_KEY = "c";
	private static final String PRE_AUTH_KEY = "e";
	private static final String ERROR_MESSAGE_KEY = "g";
	private static final String ERROR_MESSAGE_VISIBILITY = "h";
	private static final String ERROR_MESSAGE_COLOR = "i";
	private static final String TOGGLE_KEY = "j";

	/**
	 * A state flag so that we don't run this twice.
	 */
	private static boolean phoneGapInitComplete = false;
	
	/**The number of pixels that is between the left and center positions of the Discover logo */
	private static int cachedLogoOffset = 0;

	// INPUT FIELDS
	private NonEmptyEditText idField;
	private NonEmptyEditText passField;

	// BUTTONS
	private Button loginButton;
	private TextSwitcher registerOrAtmButton;
	private Button customerServiceButton;
	private Button provideFeedbackButton;
	private DiscoverToggleSwitch saveUserIdToggleSwitch;
		
	private RelativeLayout goToBankButton;
	private RelativeLayout goToCardButton;
	
	// Fastcheck Buttons
	private Button gotoFastcheckButton;
	private Button fcPrivacyTermButton;
	private Button fcProvideFeedbackButton;

	// TEXT LABELS
	private LinearLayout cardForgotAndPrivacySection;
	private TextView privacySecOrTermButtonBank;
	private TextView errorTextView;
	private TextView forgotUserIdOrPassText;
	private TextView goToBankLabel;
	private TextView goToCardLabel;
	private TextView cardPrivacyLink;

	// IMAGES
	private ImageView cardCheckMark;
	private ImageView bankCheckMark;
	private ProgressBar splashProgress;

	/*Used to specify whether the pre-authenciation call has been made for the application.
	 * Should only be done at application start-up.
	 */
	private boolean preAuthHasRun = false;

	/**Set to true when the alu modal is showing*/
	private boolean isAluModalShowing = false;

	private boolean saveUserId = false;

	/** The value of variable restoreToggle when we are not restoring based on orientation change */
	private static final int NO_TOGGLE_TO_RESTORE = -1;

	/** Equal to an Account Type ordinal when we restored a toggle on orientation change 
	 * (versus. restoring based on last login). */
	private int restoreToggle = NO_TOGGLE_TO_RESTORE;

	/** {@code true} when we restored an error on orientation change. */
	private boolean restoreError = false;

	private InputMethodManager imm;

	private static final int LOGOUT_TEXT_COLOR = R.color.body_copy;
	
	private GestureDetector gestureDetector;
    private OnTouchListener gestureListener;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_start);

		loadResources();
	    setupTextSwitcher();

		// **Activity manager has to be set before using Track Helper*/
		DiscoverActivityManager.setActiveActivity(this);

		TrackingHelper.startActivity(this);
		TrackingHelper.trackPageView(AnalyticsPage.STARTING);
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

		restoreState(savedInstanceState);
		setupButtons();

		KeepAlive.setBankAuthenticated(false);
		KeepAlive.setCardAuthenticated(false);

		DiscoverActivityManager.setActiveActivity(this);
				
		gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
		gestureListener = new OnTouchListener() {
	        @Override
			public boolean onTouch(final View v, final MotionEvent event) {
	            return gestureDetector.onTouchEvent(event);
	        }
	    };
	    getSlidingMenu().setOnClickListener(this);
	    getSlidingMenu().setOnTouchListener(gestureListener);
	 }
	
	@Override
	public void onClick(final View v) {
		v.setSoundEffectsEnabled(false);
	}

	@Override
	protected void setupSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.RIGHT);
	}
	
	
	/**
	 * This method is being called to prevent onResume calls for rotation
	 * change. When not implemented (also from the manifest) then onResume is
	 * called for rotation changed.
	 */
	@Override
	public void onConfigurationChanged(final Configuration config) {
		super.onConfigurationChanged(config);
	}

	/**
	 * Assign local references to interface elements that we need to access in some way.
	 */
	private void loadResources() {
		final int maxIdLength = 16;
		final int maxPasswordLength = 32;
		
		final InputFilter[] filters = new InputFilter[1];
		filters[0] = new InvalidCharacterFilter();
		idField = (NonEmptyEditText) findViewById(R.id.username_field);
		idField.setFilters(filters);
		idField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxIdLength)});

		passField = (NonEmptyEditText) findViewById(R.id.password_field);
		passField.setFilters(filters);
		passField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxPasswordLength)});

		provideFeedbackButton = (Button) findViewById(R.id.provide_feedback_button);
		loginButton = (Button) findViewById(R.id.login_button);
		registerOrAtmButton = (TextSwitcher) findViewById(R.id.register_now_or_atm_button);
		privacySecOrTermButtonBank = (TextView) findViewById(R.id.privacy_and_security_button_bank);
		cardForgotAndPrivacySection = (LinearLayout) findViewById(R.id.card_forgot_and_privacy_section);
		customerServiceButton = (Button) findViewById(R.id.customer_service_button);
		errorTextView = (TextView) findViewById(R.id.error_text_view);
		forgotUserIdOrPassText = (TextView) findViewById(R.id.forgot_uid_or_pass_text);
		goToBankLabel = (TextView) findViewById(R.id.go_to_bank_label);
		goToCardLabel = (TextView) findViewById(R.id.go_to_card_label);
		cardPrivacyLink = (TextView) findViewById(R.id.privacy_and_security_button_card);

		goToBankButton = (RelativeLayout) findViewById(R.id.bank_login_toggle);
		goToCardButton = (RelativeLayout) findViewById(R.id.card_login_toggle);

		cardCheckMark = (ImageView) findViewById(R.id.card_check_mark);
		bankCheckMark = (ImageView) findViewById(R.id.bank_check_mark);
		saveUserIdToggleSwitch = (DiscoverToggleSwitch) findViewById(R.id.remember_user_id_button);
		splashProgress = (ProgressBar) findViewById(R.id.splash_progress);
		
		gotoFastcheckButton = (Button)findViewById(R.id.gotoFastcheck);
		fcPrivacyTermButton = (Button)findViewById(R.id.fastcheck_privacy_terms_button);
		fcProvideFeedbackButton = (Button)findViewById(R.id.fastcheck_provide_feedback_button);

	}
	
	
	/**
	 * Sets up the text switcher which will allow the register now / atm locator button to fade between text
	 * when the Bank and Card login button is toggled.
	 */
	private void setupTextSwitcher() {
		final AlphaAnimation inAnimation = new AlphaAnimation(0.0f, 1.0f);
		inAnimation.setDuration(halfSecond);
		
		final AlphaAnimation outAnimation = new AlphaAnimation(1.0f, 0.0f);
		outAnimation.setDuration(halfSecond);
		
		registerOrAtmButton.setInAnimation(inAnimation);
		registerOrAtmButton.setOutAnimation(outAnimation);
		
		registerOrAtmButton.addView(getButtonBarTextView());
		registerOrAtmButton.addView(getButtonBarTextView());

	}
	
	/**
	 * 
	 * @return a TextView that has the same style as the other TextViews that are used in the bottom button bar.
	 */
	private TextView getButtonBarTextView() {
		final TextView buttonBarTextView = new TextView(getContext());
		buttonBarTextView.setGravity(Gravity.CENTER);

		buttonBarTextView.setTextAppearance(getContext(), R.style.login_bottom_bar_button);
		return buttonBarTextView;
	}

	/**
	 * Check to see if the user just logged out, if the user just logged out show the message.
	 */
	private void maybeShowUserLoggedOut(){
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();

		if(extras != null){
			if(extras.getBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false)){
				showLogoutSuccessful();
				getIntent().putExtra(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
				passField.getText().clear();
			}
		}
	}

	/**
	 * Check to see if the user's session expired
	 */
	private void maybeShowSessionExpired() {
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();

		if(extras != null){
			if(extras.getBoolean(IntentExtraKey.SESSION_EXPIRED, false)){
				showSessionExpired();
				getIntent().putExtra(IntentExtraKey.SESSION_EXPIRED, false);
				passField.getText().clear();
			}
		}
	}

	/**
	 * Check to see if an error occurred which lead to the navigation to the Login Page
	 */
	private void maybeShowErrorMessage() {
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();

		if(extras != null){
			final String errorMessage = extras.getString(IntentExtraKey.SHOW_ERROR_MESSAGE);
			if( !Strings.isNullOrEmpty(errorMessage) ){
				showErrorMessage(errorMessage);
				getIntent().putExtra(IntentExtraKey.SHOW_ERROR_MESSAGE, StringUtility.EMPTY);
				errorTextView.setTextColor(extras.getInt(ERROR_MESSAGE_COLOR));
			}
		}
	}

	/**
	 * Display the error message provided in the argument list in red text.
	 *
	 * @param errorMessage Reference to error message that is to be displayed.
	 */
	public void showErrorMessage(final String errorMessage) {
		BankErrorHandler.getInstance().showErrorsOnScreen(this, errorMessage);
		idField.clearFocus();
		passField.clearFocus();
		setCheckMark(false, false);
	}

	/**
	 * Display session expired message
	 */
	public void showSessionExpired() {
		errorTextView.setText(getString(R.string.session_expired));
		errorTextView.setVisibility(View.VISIBLE);
		errorTextView.setTextColor(getResources().getColor(R.color.black));
		clearInputs();
	}

	/**
	 * Display succesful logout message at top of the screen
	 */
	public void showLogoutSuccessful() {
		final long tenSeconds = 10000;
		errorTextView.setText(getString(R.string.logout_sucess));
		errorTextView.setVisibility(View.VISIBLE);
		errorTextView.setTextColor(getResources().getColor(LOGOUT_TEXT_COLOR));
		
		startFadeOutAnimationForView(errorTextView, halfSecond, View.GONE, tenSeconds);
	}
	
	private void startFadeOutAnimationForView(final View viewToFade,
											  final long duration) {
		startFadeOutAimationForView(viewToFade, duration, viewToFade.getVisibility());
	}
	
	/**
	 * Convenience method for the longer version of this.
	 * @param viewToFade the view to apply the fade to.
	 * @param duration the number of miliseconds that the animation will animate for.
	 * @param endVisibility the visibility for the view after the animation completes.
	 * @return
	 */
	private void startFadeOutAimationForView(final View viewToFade, 
											 final long duration, 
											 final int endVisibility){
		
		startFadeOutAnimationForView(viewToFade, duration, endVisibility, 0);
	}
	
	/**
	 * Starts a fade out animation on a given View with the passed parameters.
	 * @param viewToFade the view to apply the fade to.
	 * @param duration the number of miliseconds that the animation will animate for.
	 * @param endVisibility the visibility for the view after the animation completes.
	 * @param animationDelay the number of miliseconds that will elapse before the animation begins.
	 */
	private void startFadeOutAnimationForView(final View viewToFade, 
														final long duration, 
														final int endVisibility, 
														final long animationDelay) {
		if(viewToFade != null) {
			final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
			
			fadeOut.setDuration(duration);
			fadeOut.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(final Animation animation) {
					viewToFade.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(final Animation animation) {				
				}
				
				@Override
				public void onAnimationEnd(final Animation animation) {
					viewToFade.setVisibility(endVisibility);
				}
			});
			fadeOut.setStartOffset(animationDelay);
			
			viewToFade.startAnimation(fadeOut);
		}
	}

	/**
	 * Called as a result of the activity's being brought to the front when
	 * using the Intent flag FLAG_ACTIVITY_REORDER_TO_FRONT.
	 */
	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
	}

	/**
	 * Resume the activity
	 */
	@Override
	public void onResume(){
		super.onResume();


		//Check if the login activity was launched because of a logout
		maybeShowUserLoggedOut();

		//Check if the login activity was launched because of a session expire
		maybeShowSessionExpired();

		//Check if the login activity was launched because of an invalid token
		maybeShowErrorMessage();

		final int lastError = getLastError();
		final boolean saveIdWasChecked = saveUserId;


		// Do not change screen appearance as it was just completed by restoring state
		// due to orientation change.  Simply resets flag since we've caught orientation.
		if (restoreError) {
			restoreError = false;
		}
		//Do not load saved credentials if there was a previous login attempt
		//which failed because of a lock out
		else if( StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS != lastError &&
				RegistrationErrorCodes.LOCKED_OUT_ACCOUNT != lastError) {
			if(idField.length() < 1) {
				loadSavedCredentials();
			}
		} else {
			//Clear Text Fields for username and password
			clearInputs();

			//Uncheck remember user id checkbox without remembering change
			setCheckMark(false, false);
			deleteAndSaveCurrentUserPrefs();
		}
		//The check box got unchecked from loadSavedCredentials
		//but should be checked from a rotation change.
		if(!saveUserId && saveIdWasChecked){
			setCheckMark(saveIdWasChecked, false);
		}

		// Proper card|bank Account Type removed on orientation change
		// (This is due to the BaseActivity onResume reloading prefs only stored on login)
		// (Must set before setApplicationAccount())
		if (restoreToggle >= 0) {
			Globals.setCurrentAccount(AccountType.values()[restoreToggle]);
			restoreToggle = NO_TOGGLE_TO_RESTORE;
		}

		//Default to the last path user chose for login Card or Bank
		setApplicationAccount();

		/*
		 * Check to see if pre-auth request is required; should be done at
		 * application resumption, but not rotation change.
		 */
		if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)) {
			startPreAuthCheck();
		}

		//Show splash screen while completing pre-auth, if pre-auth has not been done and
		//application is be launched for the first time
		if( !preAuthHasRun && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) ) {
			showSplashScreen(!preAuthHasRun);

			/**Download links for Bank Application*/
			BankServiceCallFactory.createBankApiServiceCall().submit();

			preAuthHasRun = true;
		} else {
			showLoginPane();
		}

		//If previous screen was Strong Auth Page then clear text fields and show text fields in red
		//because that means the user did not login successfully
		if( null != DiscoverActivityManager.getPreviousActiveActivity() &&
				DiscoverActivityManager.getPreviousActiveActivity().equals(EnhancedAccountSecurityActivity.class)) {
			getErrorHandler().showErrorsOnScreen(this, null);
			DiscoverActivityManager.clearPreviousActiveActivity();
		}

	}

	/**
	 * Ran at the start of an activity when an activity is brought to the front.
	 * This also will trigger the Xtify SDK to start.
	 * Check to see if the user just logged out, if the user just logged out show the message.
	 */
	@Override
	public void onStart() {
		super.onStart();
		FacadeFactory.getPushFacade().startXtifySDK(
				this);
		getSlidingMenu().showContent();
	}

		
	@Override
	public void onRestoreInstanceState(final Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		restoreState(bundle);
	}

	/**
	 * Place all necessary information to be restored in a bundle.
	 * This info is used when the screen orientation changes.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {

		outState.putString(ID_KEY, idField.getText().toString());
		outState.putString(PASS_KEY, passField.getText().toString());
		outState.putBoolean(SAVE_ID_KEY, saveUserId);
		outState.putBoolean(PRE_AUTH_KEY, preAuthHasRun);
		outState.putString(ERROR_MESSAGE_KEY, errorTextView.getText().toString());
		outState.putInt(ERROR_MESSAGE_VISIBILITY, errorTextView.getVisibility());
		outState.putInt(ERROR_MESSAGE_COLOR, errorTextView.getCurrentTextColor());
		outState.putInt(TOGGLE_KEY, Globals.getCurrentAccount().ordinal());

		super.onSaveInstanceState(outState);
	}

	/**
	 * Restore the state of the screen on orientation change.
	 *
	 * @param savedInstanceState A bundle of state information to be restored to the screen.
	 */
	public void restoreState(final Bundle savedInstanceState) {
		if (savedInstanceState != null) {

			idField.setText(savedInstanceState.getString(ID_KEY));

			passField.setText(savedInstanceState.getString(PASS_KEY));
			preAuthHasRun = savedInstanceState.getBoolean(PRE_AUTH_KEY);

			setCheckMark(savedInstanceState.getBoolean(SAVE_ID_KEY), true);

			restoreErrorTextView(savedInstanceState);
			resetInputFieldColors();

			restoreToggle = savedInstanceState.getInt(TOGGLE_KEY, NO_TOGGLE_TO_RESTORE);
		}

	}

	/**
	 * When restoring the state of the screen on rotation. Restore the error text at the top of the screen.
	 *
	 * @param savedInstanceState
	 */
	private void restoreErrorTextView(final Bundle savedInstanceState) {
		errorTextView.setText(savedInstanceState.getString(ERROR_MESSAGE_KEY));
		errorTextView.setVisibility(savedInstanceState.getInt(ERROR_MESSAGE_VISIBILITY));
		errorTextView.setTextColor(savedInstanceState.getInt(ERROR_MESSAGE_COLOR));
		if(!errorIsVisible() && errorTextView.length() > 0) {
			errorTextView.setTextColor(getResources().getColor(LOGOUT_TEXT_COLOR));
		}
		restoreError = savedInstanceState.getInt(ERROR_MESSAGE_VISIBILITY) == View.VISIBLE;
	}

	/**
	 * Set the input fields to red if an error is being displayed.
	 */
	private void resetInputFieldColors() {
		if(errorIsVisible()) {
			setInputFieldsDrawableToRed();
		}
	}

	/**
	 * Checks to see if an error message is displayed.
	 *
	 * @return returns true if an error is being displayed in the errorTextView
	 */
	private boolean errorIsVisible() {
		final boolean errorTextViewIsVisible = errorTextView.getVisibility() == View.VISIBLE;
		final boolean errorTextIsNotLogoutMessage =
				!getResources().getString(R.string.logout_sucess).equals(errorTextView.getText().toString());
		final boolean errorTextIsNotSessionExpired = 
				!getResources().getString(R.string.session_expired).equals(errorTextView.getText().toString());
		return errorTextViewIsVisible && errorTextIsNotLogoutMessage && errorTextIsNotSessionExpired;
	}

	/**
	 * Load user credentials from shared preferences.
	 * Set user ID field to the saved value, if it was supposed to be saved.
	 */
	private void loadSavedCredentials() {

		boolean rememberIdCheckState = false;

		rememberIdCheckState = Globals.isRememberId();

		final String savedId = Globals.getCurrentUser();
		if(rememberIdCheckState) {
			idField.setText(savedId);
			idField.clearFocus();
			idField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		} else {
			clearInputs();
		}

		if(!Strings.isNullOrEmpty(Globals.getCurrentUser())){
			setCheckMark(rememberIdCheckState, true);
		} else {
			setCheckMark(false, false);
		}

	}

	/**
	 * setupButtons() Attach onClickListeners to buttons. These buttons will
	 * execute the specified functionality in onClick when they are clicked...
	 */
	private void setupButtons() {
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				CommonUtils.setViewGone(errorTextView);
				
				try {
					final String encryptedUsername = EncryptionUtil.encrypt(idField.getText().toString());
					DiscoverApplication.getLocationPreference().setMostRecentUser(encryptedUsername);
				} catch (final Exception e) {
					Log.e(TAG, "Unable to cache last attempted login");
				}
				
				//Checking if imm is null before trying to hide the keyboard. This was causing a
				//null pointer exception in landscape.
				if (imm != null){
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				//Clear the last error that occurred
				setLastError(0);
				login();
			}
		});

		customerServiceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final boolean isCard = View.VISIBLE == cardCheckMark.getVisibility();
				BankConductor.navigateToContactUs(ContactUsType.ALL, isCard);
			}
		});

		provideFeedbackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				//Defect id 97126
				if (View.VISIBLE == cardCheckMark.getVisibility()) {
					FacadeFactory.getCardFacade().navToProvideFeedback(
							LoginActivity.this);
				} else {
					BankConductor.navigateToFeedback(false);
				}
				//Defect id 97126
			}
		});

		registerOrAtmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				//Check if registerOrAtm button is displaying text for Card or Bank
				if(isCardLogin()) {
				 	FacadeFactory.getCardFacade().navToRegister(LoginActivity.this);
				} else {
					openAtmLocator();
				}

			}
		});

		privacySecOrTermButtonBank.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				openPrivacyAndTerms();
			}
		});

		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				CommonUtils.setViewGone(errorTextView);
				forgotIdAndOrPass();
			}
		});

		cardPrivacyLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToCardPrivacyAndTermsLanding();
			}
		});
		gotoFastcheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				//FacadeFactory.getCardFacade().navToFastcheck(LoginActivity.this);
				getSlidingMenu().toggle();
			}
		});
		
		fcPrivacyTermButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToCardPrivacyAndTermsLanding();
			}
		});
		
		fcProvideFeedbackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				FacadeFactory.getCardFacade().navToProvideFeedback(
                        LoginActivity.this);
			}
		});
	
	}

	/**
	 * Method used to determine whether the user is in the Card Login Page or
	 * Bank Login Page.
	 * 
	 * @return True if in the Card login page, false otherwise.
	 */
	public boolean isCardLogin() {
	   return View.VISIBLE == cardCheckMark.getVisibility();
	}

	/**
	 * If the user id, or password field are effectively blank, do not
	 * allow a service call to be made display the error message for id/pass not
	 * matching records. If the fields have data - submit it to the server for
	 * validation.
	 */
	private void login() {
		//Close Soft Input Keyboard
		final InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromInputMethod(passField.getWindowToken(), 0);

		Globals.setOldTouchTimeInMillis(0);
		idField.clearFocus();
		passField.clearFocus();
		setInputFieldsDrawablesToDefault();
		if (!showErrorIfAnyFieldsAreEmpty() && !showErrorWhenAttemptingToSaveAccountNumber()) {
			runAuthWithUsernameAndPassword(idField.getText().toString(),
					passField.getText().toString());
		}
	}

	/**
	 * Set the background color of the input fields back to their default values
	 */
	private void setInputFieldsDrawablesToDefault() {
		idField.clearErrors();
		passField.clearErrors();
		idField.setCompoundDrawables(null, null, null, null);
		passField.setCompoundDrawables(null, null, null, null);
	}

	/** Set the input fields to be highlighted in red. */
	private void setInputFieldsDrawableToRed() {
		idField.setErrors();
		passField.setErrors();
	}

	/**
	 * If a user tries to save their login ID but provides an account number, we need to show an error
	 * clear the input fields and un-check the save-user-id box.
	 *
	 * @return a boolean that represents if an error was displayed or not.
	 */
	private boolean showErrorWhenAttemptingToSaveAccountNumber() {
		final String inputId = idField.getText().toString();

		if(saveUserId && InputValidator.isCardAccountNumberValid(inputId)) {
			errorTextView.setTextColor(getResources().getColor(R.color.red));
			errorTextView.setText(getString(R.string.cannot_save_account_number));
			errorTextView.setVisibility(View.VISIBLE);
			clearInputs();
			toggleSaveUserIdSwitch(idField, true);
			idField.updateAppearanceForInput();
			passField.updateAppearanceForInput();
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * This method submits the users information to the Card or Bank server for
	 * verification depending on what is selected in login page.
	 *
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 *
	 */
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		// Prevent data from restoring after a crash.
		passField.getText().clear();
		if(!saveUserId) {
			idField.getText().clear();
		}
		passField.clearFocus();
		idField.clearFocus();
		setInputFieldsDrawablesToDefault();
		//Check if card account has been selected
		if( View.VISIBLE == cardCheckMark.getVisibility() ) {
			cardLogin(username, password) ;
		} else {
			bankLogin(username, password);
		}
	}

	/**
	 * This method submits the users information to the Card server for verification.
	 *
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 *
	 */
	private void cardLogin(final String username, final String password) {
		FacadeFactory.getCardLoginFacade().login(this, username, password);
	}

	/**
	 * This method submits the users information to the Bank server for verification.
	 *
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 *
	 */
	private void bankLogin(final String username, final String password) {
		final BankLoginDetails login = new BankLoginDetails();
		login.password = password;
		login.username = username;
		BankConductor.authorizeWithCredentials(login);
	}


	/**
	 * toggleSaveUserIdSwitch - This method handles the state of the save user id switch on the login screen.
	 *
	 * It changes the state of the saveUserId value.
	 *
	 * @param v Reference to view which contains the remember user id check
	 * @param cache Specifies whether to remember the state change
	 */
	public void toggleSaveUserIdSwitch(final View view, final boolean cache) {
		if(saveUserId == saveUserIdToggleSwitch.isChecked()) {
			saveUserIdToggleSwitch.toggle();
		}
		saveUserId = saveUserIdToggleSwitch.isChecked();
		
		//Check whether to save change in persistent storage
		if(cache) {
			Globals.setRememberId(saveUserId);
		}
	}

	/**
	 * Calls toggleCheckBox(v, true)
	 * This method allows us to call toggleCheckBox from XML, we always want to save the state of the button
	 * so we always pass true as the second argument.
	 *
	 * @param v the calling view.
	 */
	public void toggleCheckBoxFromXml(final View v) {
		toggleSaveUserIdSwitch(v, true);
	}

	/**
	 * Updates the view based on the application account selected by the user. Called by application at start-up.
	 */
	private void setApplicationAccount() {
		/**
		 * Used to remember the lastLoginAccount at startup of the application, in case the user 
		 * toggles to a different account and does not login. This variable will be used to revert 
		 * the application back to the original last logged in account.
		 */
		final AccountType lastLoginAcct = Globals.getCurrentAccount();
		if (AccountType.BANK_ACCOUNT == lastLoginAcct) {
			setLoginTypeToBank();
		} else {
			setLoginTypeToCard();
		}
	}

	/**
	 * toggleBankCardLogin(final View v) This method handles the login choices
	 * for logging in as a bank user or a card user.
	 *
	 * It changes the visible position of a check mark and the color of
	 * the labels next to it. In addition, it updates the text for the bottom
	 * row buttons.
	 */
	public void toggleBankCardLogin(final View v) {
		final AccountType initialAccountType = Globals.getCurrentAccount();
		boolean isTogglingCardOrBank = false;

		//See if we are toggling or not (the user didn't press the selection they are on again.)
		if (v.equals(goToCardButton) && initialAccountType.equals(AccountType.CARD_ACCOUNT)) {
			isTogglingCardOrBank = false;
		}else if(v.equals(goToBankButton) && initialAccountType.equals(AccountType.BANK_ACCOUNT)){
			isTogglingCardOrBank = false;
		}else{
			isTogglingCardOrBank = true;
		}

		//Do Common setup between Bank and Card toggling
		if(isTogglingCardOrBank){
			clearInputs();
			Globals.setCurrentUser(StringUtility.EMPTY);
			errorTextView.setText(StringUtility.EMPTY);
			errorTextView.setVisibility(View.GONE);

			//Delete saved use if toggle is made and save user ID is not checked.
			if(!saveUserId){
				deleteAndSaveCurrentUserPrefs();
			}

			if(v.equals(goToCardButton)){
				setLoginTypeToCard();
				animateCardSetup();
			}
			//Setup Bank Login.
			else {
				setLoginTypeToBank();
				animateBankSetup();
				//Track that the bank toggle was selected
				BankTrackingHelper.trackPage(LoginActivity.class.getSimpleName());
			}

			// Need to reset the error handler type to card|bank for service calls
			BankNetworkServiceCallManager.getInstance().resetErrorHandler();

			//Refresh Screen based on Selected Account Preferences
			loadSavedCredentials();
			idField.clearFocus();
			passField.clearFocus();

			setInputFieldsDrawablesToDefault();

		}
	}

	@Override
	public void hideFastcheck() {
		gotoFastcheckButton.setVisibility(View.GONE);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);	
		alignLogoLeft(false);
	}
	
	/**
	 * Returns the distance in pixels that the given view needs to move in order to be centered in the screen.
	 * 
	 * @param view
	 * @return the number of pixels that view needs to move to reach the become centered in the screen.
	 */
	private int getXDistanceToCenterForView(final View view) {
		int xDelta = 0;
		final int[] coords = new int[] {0, 0};
		view.getLocationInWindow(coords);
		
		final int viewWidth = view.getMeasuredWidth() / 2;	
		
		xDelta = getDisplayWidth() / 2;
		xDelta -= viewWidth;
		xDelta -= coords[0];
		
		return xDelta;
	}
	
	/**
	 * 
	 * @return the width in pixels of the screen.
	 */
	private int getDisplayWidth() {
		final DisplayMetrics dm = new DisplayMetrics();
		
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.widthPixels;
	}

	/**
	 * Animates the Discover logo so that it will slide into its appropriate position based on 
	 * if the login screen is setup for Card or Bank.
	 */
	private void animateLogoSlide() {
		final View logo = findViewById(R.id.discoverLogo);
		final int[] coords = new int[] {0, 0};
		logo.getLocationInWindow(coords);
		
		TranslateAnimation translation = null;
		if(isCardLogin()) {
			translation = new TranslateAnimation(coords[0] - cachedLogoOffset, 0, 0, 0);
		}else{
			translation = new TranslateAnimation(-getXDistanceToCenterForView(logo), 0, 0, 0);
		}
					
		translation.setDuration(halfSecond);

		logo.startAnimation(translation);
	}
	
	private void showFastcheckOnCondition() {
		if (fastcheckTokenExists()) {
			gotoFastcheckButton.setVisibility(View.VISIBLE);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			hideFastcheck();
		}
	}

	private void deleteAndSaveCurrentUserPrefs() {
		Globals.setRememberId(false);
		Globals.setCurrentUser(StringUtility.EMPTY);
		Globals.savePreferences(this);
	}

	/**
	 * Sets the login screen to display the proper UI elements for a Card login.
	 */
	private void setLoginTypeToCard() {
		goToCardLabel.setTextColor(getResources().getColor(R.color.black));
		goToCardButton
		.setBackgroundResource(R.drawable.card_login_background_on);
		goToCardButton.setPadding(
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad));

		CommonUtils.setViewVisible(cardCheckMark);
		CommonUtils.setViewInvisible(bankCheckMark);

		goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
		goToBankButton
		.setBackgroundResource(R.drawable.bank_login_background_off);
		goToBankButton.setPadding(
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad));

		registerOrAtmButton.setText(getResources().getString(R.string.register_now));

		CommonUtils.setViewInvisible(privacySecOrTermButtonBank);
		CommonUtils.setViewVisible(cardForgotAndPrivacySection);

		// Load Card Account Preferences for refreshing UI only
		Globals.loadPreferences(this, AccountType.CARD_ACCOUNT);
		
		alignLogoLeft(true);

		showFastcheckOnCondition();
	}
	
	/**
	 * Aligns the Discover logo on the login screen to it's parent left alignment if given 'true' as its parameter.
	 * If 'false', it will align to center horizontal.
	 * @param alignLeft will align the logo to its parent left if true, or centered horizontal if false.
	 */
	private void alignLogoLeft(final boolean alignLeft) {
		final View logo = findViewById(R.id.discoverLogo);
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)logo.getLayoutParams();
		
		if(alignLeft) {
	 		params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		}else {
			params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			final int[] location = new int[2];
			logo.getLocationOnScreen(location);
			cachedLogoOffset = location[0];
		}
		
		logo.setLayoutParams(params);
	}
	
	/**
	 * Animates the login screen into the card login state.
	 */
	private void animateCardSetup() {
		final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(halfSecond);
		forgotUserIdOrPassText.startAnimation(fadeIn);
		cardPrivacyLink.startAnimation(AnimationUtils.loadAnimation(getContext(), 
									   R.anim.to_center_from_left_of_center));
		animateFastCheckIfNeeded();
	}
	
	/**
	 * If the fastcheck button should be visible, then this method will animate the discover logo sliding to 
	 * the left and the button fading in.
	 */
	private void animateFastCheckIfNeeded() {
		if(fastcheckTokenExists() && gotoFastcheckButton != null) {
			if(isCardLogin()) {
				animateLogoSlide();
				final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
				fadeIn.setDuration(threeFifty);
				gotoFastcheckButton.startAnimation(fadeIn);
			}else if(!isCardLogin()) {
				animateLogoSlide();
				startFadeOutAnimationForView(gotoFastcheckButton, threeFifty);
			}
		}
		
	}

	/**
	 * Sets the login screen to display the proper UI elements for a Bank login.
	 */
	private void setLoginTypeToBank() {
		goToCardLabel.setTextColor(getResources().getColor(R.color.blue_link));
		goToCardButton
		.setBackgroundResource(R.drawable.card_login_background_off);
		goToCardButton.setPadding(
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad));
		CommonUtils.setViewInvisible(cardCheckMark);
		CommonUtils.setViewVisible(bankCheckMark);

		goToBankLabel.setTextColor(getResources().getColor(R.color.black));
		goToBankButton
		.setBackgroundResource(R.drawable.bank_login_background_on);
		goToBankButton.setPadding(
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad),
				(int) getResources().getDimension(R.dimen.top_pad));

		registerOrAtmButton.setText(getResources().getString(R.string.atm_locator));

		CommonUtils.setViewVisible(privacySecOrTermButtonBank);
		CommonUtils.setViewInvisible(cardForgotAndPrivacySection);

		alignLogoLeft(false);

		forgotUserIdOrPassText.setVisibility(View.VISIBLE);
		
		// Load Bank Account Preferences for refreshing UI only
		Globals.loadPreferences(this, AccountType.BANK_ACCOUNT);
		
		hideFastcheck();
	}
	
	/**
	 * Perform animations that will transition the login screen into a Bank state.
	 */
	private void animateBankSetup() {
		animatePrivacyAndTermsSlideToBank();
		animateFastCheckIfNeeded();
	}
	
	/**
	 * Animates the privacy and terms link for bank into the center of its row and fades out the
	 * forgot password link.
	 */
	private void animatePrivacyAndTermsSlideToBank() {
		CommonUtils.setViewVisible(cardForgotAndPrivacySection);
		privacySecOrTermButtonBank.setVisibility(View.INVISIBLE);

		final View forgotLink = cardForgotAndPrivacySection.findViewById(R.id.forgot_uid_or_pass_text);
		final Animation translate = AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_center);
		translate.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(final Animation animation) {
				startFadeOutAimationForView(forgotLink, threeFifty, View.INVISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(final Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(final Animation animation) {
				forgotLink.setVisibility(View.VISIBLE);
				CommonUtils.setViewVisible(privacySecOrTermButtonBank);
				CommonUtils.setViewInvisible(cardForgotAndPrivacySection);
				privacySecOrTermButtonBank.setVisibility(View.VISIBLE);
			}
		});
		
		cardPrivacyLink.startAnimation(translate);
	}

	/**
	 * clearInputs() Removes any text in the login input fields.
	 */
	private void clearInputs() {
		idField.getText().clear();
		passField.getText().clear();
		idField.clearFocus();
		passField.clearFocus();
		setInputFieldsDrawablesToDefault();
	}

	/**
	 * Sets the check mark on the login screen to the given boolean (checked/unchecked) state.
	 *
	 * @param shouldBeChecked Sets the check mark to checked or unchecked for true or false respectively.
	 * @param cached Sets whether the state change should be remembered
	 */
	private void setCheckMark(final boolean shouldBeChecked, final boolean cached) {
		if(shouldBeChecked && !saveUserIdToggleSwitch.isChecked()) {
			saveUserIdToggleSwitch.toggle();
		}else if (!shouldBeChecked && saveUserIdToggleSwitch.isChecked()) {
			saveUserIdToggleSwitch.toggle();
		}
		
		saveUserId = shouldBeChecked;
		
		//Check whether to save change in persistent storage
		if(cached) {
			Globals.setRememberId(saveUserId);
		}	
	}

	/**
	 * This method launches a new activity given an activity class.
	 */
	public void launchActivityFromClass(final Class<?> newActivity) {
		final Intent newVisibleIntent = new Intent(this, newActivity);
		this.startActivity(newVisibleIntent);
		finish();
	}

	/**
	 * Opens ATM Locator screen when user taps the ATM Locator button while
	 * in the BANK Login Screen
	 */
	public void openAtmLocator() {
		BankTrackingHelper.forceTrackPage(R.string.bank_atm_start);
		launchActivityFromClass(AtmLocatorActivity.class);
	}

	/**
	 * Opens Privacy and Terms screen when user taps the Privacy and Terms button while
	 * in the Bank Login Screen
	 */
	public void openPrivacyAndTerms() {
		BankConductor.navigateToPrivacyTerms(PrivacyTermsType.LandingPage);
	}

	/**
	 * forgotIdAndOrPass() This method is the same as registerNewUser except
	 * that it launches the forgot nav screen and is instead called from Java.
	 */
	private void forgotIdAndOrPass() {
		FacadeFactory.getCardFacade().navToForgot(this);
	}
	
	private boolean fastcheckTokenExists() {
		return FacadeFactory.getCardFacade().fastcheckTokenExists(this);
	}

	/**
	 * showErrorIfAnyFieldsAreEmpty() Sets error tags for input fields if a
	 * field is empty.
	 *
	 * @return boolean value to show if any errors should be shown.
	 */
	private boolean showErrorIfAnyFieldsAreEmpty() {

		final boolean wasIdEmpty = Strings.isNullOrEmpty(idField.getText().toString());
		final boolean wasPassEmpty = Strings.isNullOrEmpty(passField.getText().toString());

		if (wasIdEmpty && wasPassEmpty) {
			setInputFieldsDrawableToRed();
			setCheckMark(false, true);
			return true;
		} else if(wasIdEmpty || wasPassEmpty) {
			final String errorText = getResources().getString(R.string.login_error);
			getErrorHandler().showErrorsOnScreen(this, errorText);
			idField.clearFocus();
			passField.clearFocus();
			setCheckMark(false, true);
			return true;
		}
		// All fields were populated.
		return false;
	}

	/**
	 * Run the pre-auth call. Check with the server if the version of the
	 * application we are running is OK. Also checks to see if the server is
	 * available and will allow users to login.
	 */
	public void startPreAuthCheck() {
		final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback.<PreAuthResult> builder(this)
				.withSuccessListener(new PreAuthSuccessResponseHandler(this))
				.withErrorResponseHandler(new PreAuthErrorResponseHandler(this))
				.withExceptionFailureHandler(BankExceptionHandler.getInstance()).build();

		new PreAuthCheckCall(this, callback).submit();
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
	 */
	@Override
	public TextView getErrorLabel() {
		return errorTextView;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
	 */
	@Override
	public List<EditText> getInputFields() {
		final List<EditText> inputFields = new ArrayList<EditText>();
		inputFields.add(idField);
		inputFields.add(passField);
		return inputFields;
	}

	/**
	 * Used to update the globals data stored at login for CARD or BANK and retrieves
	 * user information. Should only be called if logged in otherwise will return false.
	 *
	 * @param account Specify either Globals.CARD_ACCOUNT or Globals.BANK_ACCOUNT
	 *
	 * @return Returns true if successful, false otherwise.
	 */
	@Override
	public boolean updateAccountInformation(final AccountType account) {
		return Globals.updateAccountInformation(account,getContext(),idField.getText().toString(),saveUserId);
	}

	/**
	 * Used to display splash screen at start-up of the application prior to pre-authentication. If show is
	 * set to true shows splash screen back ground and a progress bar with animation. If show is set to false,
	 * then login and toolbar views are shown. The login view use an aninmation to fade in.
	 *
	 * @param show True hides all login views and toolbar, false hides progress bar and fades in login views.
	 */
	public void showSplashScreen(final boolean show) {
		final ViewGroup loginStartLayout = (ViewGroup) findViewById( R.id.login_start_layout );
		final ViewGroup loginPane = (ViewGroup) findViewById(R.id.login_pane);
		final ViewGroup toolbar = (ViewGroup)findViewById(R.id.login_bottom_button_row);

		//Verify loginStartLayout has a valid instance of ViewGroup
		if( null != loginStartLayout && null != loginPane && null != toolbar ) {

			//Show progress bar and hide login view
			if( show ) {
				splashProgress.setVisibility(View.VISIBLE);

				loginPane.setVisibility(View.GONE);
				toolbar.setVisibility(View.GONE);
			}
			//Hide progress bar and fade in login view
			else {
				//If login views already visible nothing more needs to be done
				if( loginPane.getVisibility() != View.VISIBLE ) {
					splashProgress.setVisibility(View.GONE);

					loginPane.setVisibility(View.VISIBLE);

					final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
					animationFadeIn.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(final Animation anim) {
							final Animation translate = 
									AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_animation);
							toolbar.setVisibility(View.VISIBLE);
							toolbar.startAnimation(translate);
						};
						@Override
						public void onAnimationRepeat(final Animation anim)  {
						};
						@Override
						public void onAnimationEnd(final Animation anim) {
						};
					});
					
					loginPane.startAnimation(animationFadeIn);
				} else {
					if( Log.isLoggable(TAG, Log.WARN)) {
						Log.w(TAG,"login views already visible");
					}
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG,"Unable to find views");
			}
		}
	}

	/**
	 * Shows the login pane with credential text fields and the toolbar at the bottom of the page
	 */
	public void showLoginPane() {
		final ViewGroup loginPane = (ViewGroup) findViewById(R.id.login_pane);
		final ViewGroup toolbar = (ViewGroup)findViewById(R.id.login_bottom_button_row);

		splashProgress.setVisibility(View.GONE);
		toolbar.setVisibility(View.VISIBLE);
		loginPane.setVisibility(View.VISIBLE);
	}

	/**
	 * Sets a flag which is used to determine whether Pre-Authentication has been performed
	 * by the application already. This flag helps avoid Pre-Authentication being performed
	 * more than once by the application. In addition, it hides the splash screen and shows
	 * login views in the case the splash screen is being displayed.
	 *
	 * @param result True if Pre-Authentication has been completed, false otherwise.
	 */
	public void preAuthComplete(final boolean result) {
		//Set flag to detect if pre-authentication needs to be performed
		//the next time login activity is launched
		preAuthHasRun = true;

		// splash screen is still up - let's init phone gap now before
		// we take it down
		if ( !phoneGapInitComplete ) {
			FacadeFactory.getCardFacade().initPhoneGap();

			phoneGapInitComplete = true;
		}
		setInputFieldsDrawablesToDefault();
		CommonUtils.setViewGone(errorTextView);
		showSplashScreen(false);

	}

	/**
	 * @return True if Pre-authentication has been performed already, false otherwise.
	 */
	public boolean getPreAuthHasRun() {
		return preAuthHasRun;
	}

	/**
	 * Returns error handler
	 */
	@Override
	public ErrorHandler getErrorHandler(){
		if( Globals.getCurrentAccount() == AccountType.CARD_ACCOUNT) {
			return FacadeFactory.getCardFacade().getCardErrorHandler();
		} else {
			return BankErrorHandler.getInstance();
		}
	}

	/**
	 * Creates and shows a modal to inform the user that their account skipped
	 * SSO sign-on because of a Card BadStatus.
	 */
	public void showALUStatusModal(final BankLoginDetails credentials) {
		//Set that an sso user is attempting to login
		BankUser.instance().setSsoUser(false);


		final SimpleContentModal aluModal = new SimpleContentModal(this);
		aluModal.setTitle(R.string.skipsso_modal_title);
		aluModal.setContent(R.string.skipsso_modal_body);
		aluModal.getHelpFooter().setToDialNumberOnClick(R.string.skipsso_modal_number);
		aluModal.showErrorIcon(true);
		aluModal.setButtonText(R.string.skipsso_modal_button);

		aluModal.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {		
				if (credentials == null) {
					BankConductor.continueAuthDueToALU();
				} else {
					BankConductor.continueAuthDueToALU(credentials);
				}
				aluModal.dismiss();		
			}
		});

		aluModal.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(final DialogInterface dialog) {
				isAluModalShowing = true;
			}
		});

		aluModal.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(final DialogInterface dialog) {
				isAluModalShowing = false;

			}
		});
		closeDialog();
		showCustomAlert(aluModal);
	}

	/**
	 * Start an activity (this needs to be overwritten so that the ALU modal
	 * can stay visible after the phone number is clicked)
	 */
	@Override
	public void startActivity(final Intent intent){
		if(isAluModalShowing){
			super.startActivityNoReset(intent);
		}else{
			super.startActivity(intent);
		}
	}

	/**
	 * Start an activity for result but dont clear the active moddal
	 * (this needs to be overwritten so that the ALU modal
	 * can stay visible after the phone number is clicked)
	 * @param intent - intent to start
	 * @param requestCode - requestCode
	 */
	@Override
	public void startActivityForResult(final Intent intent, final int requestCode){
		if(isAluModalShowing){
			super.startActivityForResultNoReset(intent, requestCode);
		}else{
			super.startActivityForResult(intent, requestCode);
		}
	}

	
	@Override
    public int getBehindContentView() {
        // TODO Auto-generated method stub
        return R.layout.fastcheck_frame;
    }
	
	@Override
	public void showActionBar(){
		setBehindContentView(getBehindContentView());
	}
	
	@Override
	protected void showActionBarLogo(final boolean show) {}
	
	@Override
	public void setActionBarTitle(final String title) {}
	
	@Override
	public String getActionBarTitle() {
		return StringUtility.EMPTY;
	}
	
		
	public void onLeftSwipe() {
	}
	
	public void onRightSwipe() {
		if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "onRightSwipe");
		getSlidingMenu().toggle();
	}
	
	
	
	private class SwipeGestureDetector extends SimpleOnGestureListener {
	    private static final int SWIPE_MIN_DISTANCE = 50;
	    private static final int SWIPE_MAX_OFF_PATH = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	    @Override
	    public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX,
	            final float velocityY) {
	        try {
	            //Toast t = Toast.makeText(this, "Gesture detected", Toast.LENGTH_SHORT);
	            //t.show();
	            final float diffAbs = Math.abs(e1.getY() - e2.getY());
	            final float diff = e1.getX() - e2.getX();

	            if (diffAbs > SWIPE_MAX_OFF_PATH)
	                return false;

	            // Left swipe
	            if (diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                onLeftSwipe();
	            } 
	            // Right swipe
	            else if (-diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                onRightSwipe();
	            }
	        } catch (final Exception e) {
	            if (Log.isLoggable(TAG, Log.ERROR)) Log.e(TAG, "onFling() Error on gestures");
	        }
	        return false;
	    }

	}
	
	@Override
	public void onBackPressed() {
		navigateBack();
	}

}	
	