package com.discover.mobile.card.passcode.enable;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.PasscodeLandingFragment;
import com.discover.mobile.card.passcode.request.CreateBindingRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.utils.PasscodeUtils;

public class PasscodeEnableStep2Fragment extends PasscodeBaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeEnableStep2";
	private static String TAG = "PasscodeEnableStep2Fragment";
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_ENABLE_STEP1);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_enable_step2_header);
		return view;
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isValid) {
			String deviceToken = PasscodeUtils.genClientBindingToken();
			//TODO make sure service is updated to accept passcode as well with this call
			new CreateBindingRequest(this.getActivity(), deviceToken).loadDataFromNetwork(new EnableRequestListener(deviceToken));
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		dialogHelper(MODAL_PASSCODE_ENABLED, "Home", true, new NavigateACHomeAction(), new NavigatePasscodeLandingAction());
		getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	@Override
	public void onPasscodeErrorEvent() {
		clearAllFields();
	}
	
	private final class EnableRequestListener implements CardEventListener {
		private String deviceToken;
		
		public EnableRequestListener(String deviceToken) {
			this.deviceToken = deviceToken;
		}
		@Override
		public void OnError(Object data) {
			// TODO Auto-generated method stub
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			//TODO make this result accurate from the service call
			passcodeResponse(true);
			//if successful store token on device
			createPasscodeToken(this.deviceToken);
			storeFirstName();

//			printFragmentsInBackStack();
			//calls resume which also seems to be calling submit
//			getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//			printFragmentsInBackStack();
			//TODO currently results in confirmation message being popped twice 
			//FROM https://groups.google.com/forum/?fromgroups#!topic/android-developers/0qXCA9rW7EI
			//Even with POP_BACK_STACK_INCLUSIVE onResume of popped Fragment is called. You need to add alternative method that will allow remove element from back stack without resuming it.
		}
	};
	
	protected class RemoveHistoryAndNavigatePasscodeLandingAction implements Runnable {
		public RemoveHistoryAndNavigatePasscodeLandingAction() {}
		@Override
		public void run() {
			getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			makeFragmentVisible(new PasscodeLandingFragment());
		}
	}
}