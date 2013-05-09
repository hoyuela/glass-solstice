/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
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
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * /**
 * This activity allows the showing of the Card FAQ content while logged out.
 * It is an activity of its own that contains the Card FAQ fragments.

 * @author jthornton
 *
 */
public class CardLoggedOutFAQActivity  extends NavigationRootActivity{
	/**
	 * Create the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fragment_slide_in_right , R.anim.fragment_slide_out_left);
		setContentView(R.layout.faq_logged_out);
		showActionBar();
		final SlidingMenu slidingMenu = ((SlidingFragmentActivity)this).getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	@Override
	public void onResume() {
		super.onResume();

		if(this.getCurrentContentFragment() == null){
			showFAQDetailIfNeeded();
		}
	}

	@Override
	public void onPause() {
		overridePendingTransition(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right);
		super.onPause();
	}

	/**
	 * Show a Card Faq Detail if needed
	 */
	private void showFAQDetailIfNeeded() {
		final Bundle bundle = this.getIntent().getExtras();
		if(bundle != null) {
			final String faqType = bundle.getString(BankExtraKeys.FAQ_TYPE);
			BankConductor.navigateToCardFaqDetail(faqType);
		}else {
			final CardFAQLandingPageFragment landingPage = new CardFAQLandingPageFragment();
			makeFragmentVisible(landingPage);
		}
	}

	@Override
	public int getBehindContentView() {
		return R.layout.faq_not_logged_in_behind;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return null;
	}

	@Override
	public void onBackPressed() {
		final FragmentManager fragmentManager = this.getSupportFragmentManager();
		final int backStackCount = fragmentManager.getBackStackEntryCount();

		//Only handle back press if there is a fragment in the stack
		//Otherwise ignore the back press as we do not want to close the application via
		//Navigation root activity, it should only be closed from the logged in page
		if( backStackCount > 1 ) {
			fragmentManager.popBackStack();
		}else{
			finish();
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
		titleView.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.INVISIBLE);
		logout.setVisibility(View.INVISIBLE);
		titleView.setText(getResources().getString(R.string.card_faq_title));
	}
}
