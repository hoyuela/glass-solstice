package com.discover.mobile.card.passcode;

import android.app.Activity;
import android.util.Log;

import com.discover.mobile.card.auth.strong.StrongAuthDefaultResponseHandler;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.passcode.enable.PasscodeEnableStep1Fragment;
import com.discover.mobile.card.passcode.forgot.PasscodeForgotStep1Fragment;
import com.discover.mobile.card.passcode.menu.PasscodeMenuFragment;
import com.discover.mobile.card.passcode.model.json.Status;
import com.discover.mobile.card.passcode.remove.PasscodeRemoveFragment;
import com.discover.mobile.card.passcode.request.GetStatusRequest;
import com.discover.mobile.card.passcode.setup.PasscodeSetupStep1Fragment;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.PasscodeUtils;

/**
 * Routes passcode flows to proper fragments
 * @author sgoff0
 *
 */
public class PasscodeRouter {
	private static String TAG = "PasscodeRouter";
	private PasscodeUtils pUtils;
	private Activity activity;
	private boolean doesAccountHavePasscode;
	private boolean isDevicePasscodeTokenMine;
	private CardNavigationRootActivity cnra;
	private BaseFragment baseFrag;
	
    public PasscodeRouter(CardNavigationRootActivity cardNavigationRootActivity) {
    	this.activity = cardNavigationRootActivity;
    	this.pUtils = new PasscodeUtils(activity.getApplicationContext());
		this.cnra = cardNavigationRootActivity;
	}

	public PasscodeRouter(BaseFragment frag) {
    	this.activity = frag.getActivity();
    	this.pUtils = new PasscodeUtils(activity.getApplicationContext());
		this.baseFrag = frag;
	}
	
	private void makeFragmentVisible(BaseFragment fragment) {
		if (cnra != null) {
			cnra.makeFragmentVisible(fragment);
		} else if (baseFrag != null) {
			baseFrag.makeFragmentVisible(fragment);
		}
	}

	public void getStatusAndRoute() {
		new GetStatusRequest(activity, pUtils.getPasscodeToken()).loadDataFromNetwork(new StatusRequestListener());
    }
    
	private void route() {
		if (pUtils.isForgotPasscode() && doesAccountHavePasscode) {
			//only allow forgot passcode flow if account has passcode already setup
			//when logged in via forgot passcode flow set status back to false
        	pUtils.setForgotPasscode(false);
        	navigateForgot();
		} else if (pUtils.doesDeviceTokenExist()) {
			if (isDevicePasscodeTokenMine){
				navigateMenu();
			} else {
				navigateRemove();
			}
		} else {
			if (doesAccountHavePasscode) {
				navigateEnable();
			} else {
				navigateSetup();
			}
		}
	}
	
	private void navigateForgot(){
		Log.v(TAG, "Navigate Forgot");
		final StrongAuthHandler authHandler = new StrongAuthHandler(
        		activity,
        		new PasscodeStrongAuthFlow(new PasscodeForgotStep1Fragment(), this), 
        		false);
        authHandler.strongAuth();
	}

	private void navigateSetup(){
		Log.v(TAG, "Navigate Setup");
		final StrongAuthHandler authHandler = new StrongAuthHandler(
        		activity,
        		new PasscodeStrongAuthFlow(new PasscodeSetupStep1Fragment(), this), false);
        authHandler.strongAuth();
	}
	
	private void navigateEnable(){
		Log.v(TAG, "Navigate Enable");
		makeFragmentVisible(new PasscodeEnableStep1Fragment());
	}
	
	private void navigateRemove() {
		Log.v(TAG, "Navigate Remove");
		makeFragmentVisible(new PasscodeRemoveFragment());
	}
	
	private void navigateMenu() {
		Log.v(TAG, "Navigate Menu");
		makeFragmentVisible(new PasscodeMenuFragment());
	}
	
	private final class StatusRequestListener implements CardEventListener {

		@Override
		public void OnError(Object data) {
			Log.e(TAG, "ERROR fetching passcode status");
			//TODO error fetching passcode status
		}

		@Override
		public void onSuccess(Object data) {
			Status status = (Status) data;
			Log.v(TAG, status.toString());
			doesAccountHavePasscode = status.isAccountHasPasscode();
			isDevicePasscodeTokenMine = status.isDeviceBoundToThisAccount();
			route();
		}
	};
	
	private static final class PasscodeStrongAuthFlow extends StrongAuthDefaultResponseHandler {
		private PasscodeBaseFragment frag;
		private PasscodeRouter pr;
		
		public PasscodeStrongAuthFlow(PasscodeBaseFragment frag, PasscodeRouter passcodeRouter) {
			this.frag = frag;
			this.pr = passcodeRouter;
		}

		@Override
		public void onStrongAuthSucess(Object data) {
			Log.v(TAG, "StrongAuth success: about to make fragment visible");
			if (frag != null) {
				pr.makeFragmentVisible(frag);
			}
		}
	}
}