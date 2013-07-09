package com.discover.mobile.card.passcode.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.ui.modals.EnhancedContentModal;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.request.CreatePasscodeRequest;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.utils.PasscodeUtils;

public class PasscodeSetupStep2Fragment extends PasscodeBaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeSetupStep2";
	private static String TAG = "PasscodeSetupStep2Activity";
	private static String mStep1Answer;
	
	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		mStep1Answer = getArguments().getString("passcode");
		Log.v(TAG, "Step 1 answer: " + mStep1Answer);
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_SETUP_STEP1);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_setup_step2_header);
		return view;
	}

	protected void showPasscodeCreatedModal() {
//		dialogHelper(MODAL_PASSCODE_CREATED, "Home", true, new NavigateACHomeAction(), new NavigatePasscodeLandingAction());

		final Context context = DiscoverActivityManager.getActiveActivity();
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.passcode_dialog_created_title, 
				R.string.passcode_dialog_created_content, 
				R.string.home_text,
				new NavigateACHomeAction(),
				new NavigatePasscodeLandingAction());
		modal.hideNeedHelpFooter();
		modal.setOrangeTitle();
		((NavigationRootActivity)context).showCustomAlert(modal);
	}

	@Override
	public void onPasscodeErrorEvent() {
		clearAllFields();
		//Per RTM 14.2.4.3
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        makeFragmentVisible(new PasscodeSetupStep1Fragment(), false);
	}

	@Override
	public void onPasscodeSubmitEvent() {
		Log.v(TAG, "Does input: " + getPasscodeString() + " equal step1 answer: " + mStep1Answer);
		boolean isMatch = getPasscodeString().equals(mStep1Answer);
		boolean isValid = isPasscodeValidLocally(getPasscodeString());
		
		Log.v(TAG, "isMatch? " + isMatch);
		Log.v(TAG, "isValid? " + isValid);

		//generate new client token
		String deviceToken = PasscodeUtils.genClientBindingToken();
		if (isMatch && isValid) {
			new CreatePasscodeRequest(this.getActivity(), getPasscodeString(), deviceToken).loadDataFromNetwork(new CreatePasscodeRequestListener(deviceToken));
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		showPasscodeCreatedModal();
		getActivity().getSupportFragmentManager().popBackStack(PasscodeSetupStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	private final class CreatePasscodeRequestListener implements CardEventListener {
		
		private String deviceToken;
		
		public CreatePasscodeRequestListener(String deviceToken) {
			this.deviceToken = deviceToken;
		}
		
		@Override
		public void OnError(Object data) {
			Log.e(TAG, "ERROR fetching passcode validity");
			//TODO show error page
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			passcodeResponse(true);
			//if successful store token on device
			createPasscodeToken(this.deviceToken);
			//also store user's first name on device
			storeFirstName();
			getActivity().getSupportFragmentManager().popBackStack(PasscodeSetupStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	};
	 
}