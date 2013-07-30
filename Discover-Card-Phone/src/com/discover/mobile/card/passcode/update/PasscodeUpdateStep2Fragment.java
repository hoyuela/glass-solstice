package com.discover.mobile.card.passcode.update;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;
import com.discover.mobile.card.passcode.request.GetSyntaxValidityRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

public class PasscodeUpdateStep2Fragment extends PasscodeBaseFragment {
	private static String TAG = "PasscodeUpdateStep2Fragment";

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_update_step2_header);
		passcodeGuidelinesTV.setVisibility(View.VISIBLE);
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_UPDATE_STEP2);
		return view;
	}

	@Override
	public void onPasscodeErrorEvent() {
		//TODO on step2 error go back to step 1
		showPasscodeGuidelines();
		clearAllFields();
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isValid) {
			new GetSyntaxValidityRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new SyntaxValidityRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		PasscodeUpdateStep3Fragment pStep3= new PasscodeUpdateStep3Fragment();
		Bundle b = new Bundle();
		b.putString("passcode", getPasscodeString());
		pStep3.setArguments(b);
		
		makeFragmentVisible(pStep3, false);
	}
	
	private final class SyntaxValidityRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			Log.e(TAG, "ERROR fetching passcode validity");
			//TODO show error page
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			VerifySyntax vs = (VerifySyntax) data;
			Log.v(TAG, vs.toString());
			onPasscodeSuccessEvent();
		}
	};

}