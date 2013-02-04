package com.discover.mobile.navigation;

import android.os.Bundle;

import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.push.register.PushNowAvailableFragment;

/**
 * Root activity for the application after login. This will transition fragment on and off the screen
 * as well as show the sliding bar as well as the action bar.
 *
 */
public class CardNavigationRootActivity extends NavigationRootActivity {

	/**
	 * Resume the activity to the state that it was when the activity went to the background
	 */
	@Override
	public void onResume(){
		super.onResume();
		if(!CurrentSessionDetails.getCurrentSessionDetails().isNotCurrentUserRegisteredForPush()  && !wasPaused){	
			getSupportFragmentManager().popBackStack();
			makeFragmentVisible(new PushNowAvailableFragment());	
		} 
		
		final Bundle extras = getIntent().getExtras();
		if(null != extras){
			handleIntentExtras(extras);
		}
	}

	@Override
	public int getBehindContentView() {
		// TODO Auto-generated method stub
		return R.layout.navigation_card_menu_frame;
	}
	

	/**
	 * Handle the extras passed in an intent
	 * @param extras - extras passed into the app
	 */
	private void handleIntentExtras(final Bundle extras) {
		if(!shouldShowModal){return;}
		final String screenType = extras.getString(IntentExtraKey.SCREEN_TYPE);
		if(null != screenType){
			final String userId = extras.getString(IntentExtraKey.UID);
			final String email = extras.getString(IntentExtraKey.EMAIL);
			final String lastFour = extras.getString(IntentExtraKey.ACCOUNT_LAST4);
			showConirmationModal(screenType, userId, email, lastFour);
		}
		
	}
}
