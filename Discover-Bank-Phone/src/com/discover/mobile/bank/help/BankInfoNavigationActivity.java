package com.discover.mobile.bank.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Activity class used to display the Privacy & Terms pages when the user is not logged in.
 * 
 * @author henryoyuela
 *
 */
public class BankInfoNavigationActivity extends NavigationRootActivity implements OnClickListener{	
	public static final String CONTACT_US = "contact-us";
	public static final String PRIVACY_AND_TERMS = "privacy-terms";
	public static final String PROVIDE_FEEDBACK = "provide-feedback";
	public static final String GO_BACK_TO_LOGIN = "goBackToLogin";
	public static final String IS_CARD = "isCard";

	/**
	 * Reference to action bar back button.
	 */
	private ImageView navigationBackButton;
	private ImageView backButtonX;
	/**
	 * True when "onBackPressed" will navigate to login on back (given no fragments on the stack)
	 */
	private boolean goBackToLogin = true;

	/**
	 * Create the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_privacy_terms_activity_layout);
		showActionBar();

		/**Check if this is the first time this activity is being launched*/
		if( savedInstanceState == null ) {
			launchStartPage();
		} else {
			final Bundle bundle = getIntent().getExtras();
			goBackToLogin = bundle.getBoolean(GO_BACK_TO_LOGIN, true);
		}

		/**Sliding menu is not used for this activity*/
		enableSlidingMenu(false);
	}

	private void launchStartPage() {
		final Bundle bundle = getIntent().getExtras();
		if( (bundle == null) || bundle.containsKey(BankExtraKeys.CARD_MODE_KEY)) {
			Fragment fragment = null;

			if (bundle.containsKey(PRIVACY_AND_TERMS)) {
				fragment = new TermsLandingPageFragment();
			} else if (bundle.containsKey(PROVIDE_FEEDBACK)) {
				fragment = new ProvideFeedbackFragment();
			}

			if (fragment != null) {
				fragment.setArguments(bundle);
				makeFragmentVisible(fragment);
			}
		} else {
			if( bundle.containsKey(CONTACT_US)) {
				final CustomerServiceContactsFragment contactUs = new CustomerServiceContactsFragment();
				contactUs.setArguments(bundle);
				contactUs.setCardMode(bundle.getBoolean(IS_CARD, false));
				makeFragmentVisible( contactUs );
			} else if( bundle.containsKey(PRIVACY_AND_TERMS)) {
				makeFragmentVisible(new TermsLandingPageFragment());
			} else if( bundle.containsKey(PROVIDE_FEEDBACK)) {
				makeFragmentVisible(new ProvideFeedbackFragment());
			}
			goBackToLogin = bundle.getBoolean(GO_BACK_TO_LOGIN, true);
		}
	}

	/**
	 * Show the action bar with the custom layout
	 */
	@Override
	public void showActionBar() {
		setBehindContentView(getBehindContentView());

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final TextView titleView = (TextView) findViewById(R.id.title_view);
		final ImageView navigationToggle = (ImageView) findViewById(R.id.navigation_button);
		final Button logout = (Button) findViewById(R.id.logout_button);
		backButtonX = (ImageView) findViewById(R.id.navigation_back_x_button);

		navigationBackButton = (ImageView) findViewById(R.id.navigation_back_button);
		titleView.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.INVISIBLE);
		navigationBackButton.setVisibility(View.GONE);
		logout.setVisibility(View.INVISIBLE);	
		navigationBackButton.setOnClickListener(this);
		backButtonX.setOnClickListener(this);
	}

	/**
	 * Shows the "X" in the action bar
	 */
	@Override
	public void showBackX(){
		backButtonX.setVisibility(View.VISIBLE);
	}

	/**
	 * Show menu button
	 */
	@Override
	public void showMenuButton(){
		backButtonX.setVisibility(View.GONE);
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return null;
	}

	@Override
	public int getBehindContentView() {
		/**Because sliding menu is not used a dummy layout is returned here*/
		return R.layout.bank_empty_layout;
	}

	/**
	 * Method used to enable or disable sliding navigation menu. If disabled
	 * then user will not be able to use a swipe gesture to see the navigation
	 * menu.
	 * 
	 * @param value
	 *            True to enable sliding navigation menu, false otherwise.
	 */
	public void enableSlidingMenu(final boolean value) {
		final SlidingMenu slidingMenu = getSlidingMenu();

		if (value) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Override
	public void onClick(final View sender) {
		if( (sender.getId() == navigationBackButton.getId()) || 
				(sender.getId() == backButtonX.getId())) {
			onBackPressed();
		}
	}

	/**
	 * This method overrides NavigationRootActivity implementation as it is not using 
	 * the Android default implementation. The implementation for this class is the same
	 * as Android's.
	 */
	@Override
	public void onBackPressed() {
		final FragmentManager fragmentManager = getSupportFragmentManager();
		final int backStackCount = fragmentManager.getBackStackEntryCount();

		if( backStackCount > 1 ) {
			super.onBackPressed();
		} else if (goBackToLogin) {
			BankConductor.navigateToLoginPage(this, "", "");
			finish();
		} else {
			finish();
		}
	}

	/**
	 * Sets variables associated with controlling back navigation.
	 * @param goToLoginOnBack - True when "onBackPressed" will navigate to login on back 
	 * (given no fragments on the stack) 
	 */
	public void setOnBackNavigation(final boolean goToLoginOnBack) {
		goBackToLogin = goToLoginOnBack;
	}
}
