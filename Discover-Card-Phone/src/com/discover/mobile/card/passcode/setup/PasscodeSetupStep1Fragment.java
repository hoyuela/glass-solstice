package com.discover.mobile.card.passcode.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.request.GetSyntaxValidityRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

public class PasscodeSetupStep1Fragment extends PasscodeBaseFragment {
	private static String TAG = "PasscodeSetupStep1Fragment";
	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		Log.v(TAG, "onCreate");
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_SETUP_STEP1);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_setup_step1_header);
		passcodeGuidelinesTV.setVisibility(View.VISIBLE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPasscodeErrorEvent() {
		showPasscodeGuidelines();
		clearAllFields();
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isValid) {
			Log.v(TAG, "Firing off server request");
			new GetSyntaxValidityRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new SyntaxValidityRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		PasscodeSetupStep2Fragment pStep2 = new PasscodeSetupStep2Fragment();
		Bundle b = new Bundle();
		b.putString("passcode", getPasscodeString());
		Log.v(TAG,  "passing passcode: " + getPasscodeString());
		pStep2.setArguments(b);
		
		//must call step2 fragment putting it on back stack, otherwise step2 back nav will take you one page prior to this
		makeFragmentVisible(pStep2);
	}
		
	private final class SyntaxValidityRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			passcodeResponse(true);
		}
	};
}