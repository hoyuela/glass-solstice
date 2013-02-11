package com.discover.mobile.card.navigation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.push.register.PushNowAvailableFragment;
import com.discover.mobile.card.ui.modals.ModalConfirmationTop;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

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
			showConfirmationModal(screenType, userId, email, lastFour);
		}
		
	}
	
	/**
	 * Show the confirmation modal
	 * @param screenType - screen type to be displayed in the modal
	 * @param userId - user ID to place in the modal
	 * @param email - email to place in the modal
	 * @param lastFour - last four account number digits to place in the modal
	 */
	protected void showConfirmationModal(final String screenType, final String userId,
			final String email, final String lastFour) {
		
		final ModalConfirmationTop top = new ModalConfirmationTop(this, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(this, top, null);
		top.setUserId(userId);
		top.setEmail(email);
		top.setLastFour(lastFour);
		top.setScreenType(screenType);
		top.getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				modal.dismiss();	
				shouldShowModal = false;
			}
		});
		modal.show();
		
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.BaseFragmentActivity#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return CardErrorHandler.getInstance();
	}
}
