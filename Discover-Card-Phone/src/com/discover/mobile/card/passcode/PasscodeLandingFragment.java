package com.discover.mobile.card.passcode;

import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.auth.strong.StrongAuthListener;
import com.discover.mobile.card.common.CardEventListener;
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
 * Fragment (no UI) that detects which page should be shown next
 * @author StevenI7
 *
 */
public class PasscodeLandingFragment extends BaseFragment {
	private static String TAG = "PasscodeLandingFragment";
	private PasscodeUtils pUtils;
	private boolean doesAccountHavePasscode;
	private boolean isDevicePasscodeTokenMine;

	//TODO sgoff0 investigate - why when routing logic was in onStart() it would be executed after strongauth finish() even though the menu fragment should have been active
//    @Override
//	public void onStart() {
//        super.onStart();
//        Log.v(TAG, "onStart");
//        
//		new GetStatusRequest(this.getActivity(), pUtils.getPasscodeToken()).loadDataFromNetwork(new StatusRequestListener());
//    }
    
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
		pUtils = new PasscodeUtils(getActivity().getApplicationContext());
		new GetStatusRequest(this.getActivity(), pUtils.getPasscodeToken()).loadDataFromNetwork(new StatusRequestListener());
	}
	
	public void route() {
		if (pUtils.isForgotPasscode() && doesAccountHavePasscode) {
			//only allow forgot passcode flow if account has passcode already setup
			//when logged in via forgot passcode flow delete passcode and set status back to false
			pUtils.deletePasscodeToken();
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
        		PasscodeLandingFragment.this.getActivity(),
        		new PasscodeStrongAuthFlow(new PasscodeForgotStep1Fragment()), false);
        authHandler.strongAuth();
	}

	private void navigateSetup(){
		Log.v(TAG, "Navigate Setup");
		//TODO sgoff0 - properly configure SA
		final StrongAuthHandler authHandler = new StrongAuthHandler(
        		PasscodeLandingFragment.this.getActivity(),
        		new PasscodeStrongAuthFlow(new PasscodeSetupStep1Fragment()), false);
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
	
	@Override
	public int getActionBarTitle() {
        return R.string.sub_section_title_passcode;
		// TODO Auto-generated method stub
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.PASSCODE_SECTION;
	}
	
	private final class StatusRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			Log.e(TAG, "ERROR fetching passcode status");
			//TODO show error page
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
	
	private final class PasscodeStrongAuthFlow implements StrongAuthListener {
		
		private PasscodeBaseFragment frag;
		
		public PasscodeStrongAuthFlow(PasscodeBaseFragment frag) {
			super();
			this.frag = frag;
		}

		@Override
		public void onStrongAuthSucess(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "StrongAuth success: about to make fragment visible");
//			makeFragmentVisible(new PasscodeSetupStep1Fragment());
			if (frag != null) {
				makeFragmentVisible(frag);
			}
		}

		@Override
		public void onStrongAuthError(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "Error");
//			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
//					PasscodeUpdateStep1Fragment.this);
//            cardErrorResHandler.handleCardError((CardErrorBean) data);
			
		}

		@Override
		public void onStrongAuthCardLock(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "Lock");
			
		}

		@Override
		public void onStrongAuthSkipped(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "Skipped");
			
		}

		@Override
		public void onStrongAuthNotEnrolled(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "NotEnrolled");
			
		}
		
	}
}