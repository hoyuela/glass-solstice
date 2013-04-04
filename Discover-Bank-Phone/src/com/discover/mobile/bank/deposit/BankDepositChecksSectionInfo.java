package com.discover.mobile.bank.deposit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;

public final class BankDepositChecksSectionInfo extends GroupComponentInfo {
	/**
	 * Used for printing into Logs into Android Logcat
	 */
	protected static final String TAG = "CheckDeposit";


	public BankDepositChecksSectionInfo() {
		super(R.string.section_title_deposit_checks,
				new ClickComponentInfo(R.string.sub_section_title_deposit_a_check, getCheckDepositLandingClickListener()));
	}


	/**
	 * Click listener for the review payments menu item.  Makes the service call to the initial set 
	 * of data.
	 * @return the click listener
	 */
	public static OnClickListener getCheckDepositLandingClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if(hasCamera()){
					final Activity activity = DiscoverActivityManager.getActiveActivity();
	
					/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
					if( activity != null && activity instanceof BankNavigationRootActivity ) {
						final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
					
						/**Check if user is already in the Check Deposit work-flow*/
						if( navActivity.getCurrentContentFragment().getGroupMenuLocation()  != BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP) {
							/**Navigate the user back to the home fragment before navigating to the check deposit work-flow*/
							BankConductor.navigateToHomePage();
							
							/**Navigates to either to Check Deposit - Select Account Page or Check Deposit - Accept Terms page*/
							BankConductor.navigateToCheckDepositWorkFlow(null, BankDepositWorkFlowStep.SelectAccount);
						} else {
							
							if( Log.isLoggable(TAG, Log.WARN)) {
								Log.w(TAG,"User is already in the check deposit work-flow");
							}
							
							navActivity.hideSlidingMenuIfVisible();
						}
					}
				} else{
					showNoDeviceCameraModal();
				}
			}
		};	
	}
	
	/**
	 * Show a modal dialog that informs the user that their device cannot use check deposit because it is missing
	 * a back facing camera.
	 */
	private static void showNoDeviceCameraModal() {
			
		if(DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity currentActivity = 
					(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
			final ModalDefaultTopView top = new ModalDefaultTopView(currentActivity, null);
			top.showErrorIcon(true);
			top.setTitle(R.string.no_device_camera_title);
			top.setContent(R.string.no_device_camera_body);
			top.hideNeedHelpFooter();
			
			final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(currentActivity, null);
			bottom.setButtonText(R.string.ok);
			
			final ModalAlertWithOneButton modal = 
					new ModalAlertWithOneButton(currentActivity, top, bottom);
			
			bottom.getButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					modal.dismiss();
				}
			});
			
			modal.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(final DialogInterface dialog) {
					currentActivity.getSlidingMenu().showAbove();					
				}
			});
			
			currentActivity.showCustomAlert(modal);
		}

	}
	
	/**
	 * Do a check to see if the current device has a rear facing camera.
	 * @return if the device has a rear facing camera.
	 */
	private static boolean hasCamera() {
		boolean hasCamera = false;
		final Camera camera = Camera.open();
		
		hasCamera = (camera != null);
	
		if(hasCamera)
			camera.release();
		
		return hasCamera;
	}
	
}
