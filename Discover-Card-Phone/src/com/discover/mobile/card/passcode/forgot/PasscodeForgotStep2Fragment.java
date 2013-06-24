package com.discover.mobile.card.passcode.forgot;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.request.CreateBindingRequest;
import com.discover.mobile.card.passcode.request.UpdatePasscodeRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.utils.PasscodeUtils;

public class PasscodeForgotStep2Fragment extends PasscodeBaseFragment {
	
	static final String TRACKING_PAGE_NAME = "PasscodeForgotStep2";
	private static String TAG = "PasscodeForgotStep2Fragment";
	private static String mStep1Answer;

	public void onCreate(Bundle paramBundle) {
		// super.onCreate(paramBundle, true);
		super.onCreate(paramBundle);
		mStep1Answer = getArguments().getString("passcode");
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_update_step3_header);
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_FORGOT_STEP2);
		return view;
	}

	@Override
	public void onPasscodeErrorEvent() {
		Log.v(TAG, "Firing error event");
		//TODO on step2 error go back to step 1
		clearAllFields();
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isMatch = this.getPasscodeString().equals(mStep1Answer);
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isMatch && isValid) {
			new UpdatePasscodeRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new UpdatePasscodeRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		dialogHelper(MODAL_PASSCODE_UPDATED, "Home", true, new NavigateACHomeAction(), new NavigatePasscodeLandingAction());
		getActivity().getSupportFragmentManager().popBackStack(PasscodeForgotStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	private final class UpdatePasscodeRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			// TODO Auto-generated method stub
			Log.v(TAG, "Error");
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			Log.v(TAG, "Success!");
			storeFirstName();
			//TODO fire binding request
			String deviceToken = PasscodeUtils.genClientBindingToken();
			new CreateBindingRequest(getActivity(), deviceToken).loadDataFromNetwork(new EnableRequestListener(deviceToken));
		}
	};

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
			getActivity().getSupportFragmentManager().popBackStack(PasscodeForgotStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	};
	

}