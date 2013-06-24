package com.discover.mobile.card.passcode.disable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.request.DeletePasscodeRequest;
import com.discover.mobile.card.passcode.request.GetMatchRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;


//TODO delete this class when new flow is confirmed
public class PasscodeDisableStep1Fragment extends PasscodeBaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeDisableStep1";
	private static String TAG = "PasscodeDisableStep1Fragment";

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_DISABLE);
		this.setHeaderText(R.string.passcode_disable_step1_header);
		return view;
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isValid) {
			new GetMatchRequest(this.getActivity(), getPasscodeString(), getPasscodeToken()).loadDataFromNetwork(new MatchRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		showPasscodeDisabledModal();
	}
	
	@Override
	public void onPasscodeErrorEvent() {
		clearAllFields();
	}
	
	private void showPasscodeDisabledModal() {
		dialogHelper(MODAL_PASSCODE_DISABLED, "Home", true, new NavigateACHomeAction(), new NavigateACHomeAction());
	}
	
	private final class MatchRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			new DeletePasscodeRequest(getActivity(), getPasscodeToken()).loadDataFromNetwork(new DisableRequestListener());
		}
	};
	
	private final class DisableRequestListener implements CardEventListener {
		@Override
		public void OnError(Object data) {
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			deletePasscodeToken();
			getActivity().getSupportFragmentManager().popBackStack();
			passcodeResponse(true);
		}
	};

}