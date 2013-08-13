package com.discover.mobile.card.passcode.forgot;

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
import com.discover.mobile.card.passcode.request.CreateResetRequest;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.nav.NavigationRootActivity;

public class PasscodeForgotStep2Fragment extends PasscodeBaseFragment {
	private static String TAG = "PasscodeForgotStep2Fragment";
	private static String mStep1Answer;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		mStep1Answer = getArguments().getString("passcode");
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_forgot_step2_header);
		return view;
	}

	@Override
	public String getPageName() {
		return AnalyticsPage.PASSCODE_FORGOT_STEP2;
	};

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
			new CreateResetRequest(this.getActivity(), getPasscodeString(), getPasscodeToken()).loadDataFromNetwork(new ResetPasscodeRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		final Context context = DiscoverActivityManager.getActiveActivity();
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_FORGOT_OVERLAY);
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.passcode_dialog_forgot_title, 
				R.string.passcode_dialog_forgot_content, 
				R.string.home_text,
				new NavigateACHomeAction(),
				new NavigateACHomeAction());
		modal.hideNeedHelpFooter();
		modal.setOrangeTitle();
		((NavigationRootActivity)context).showCustomAlert(modal);
		getActivity().getSupportFragmentManager().popBackStack(PasscodeForgotStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	private final class ResetPasscodeRequestListener implements CardEventListener {

		@Override
		public void OnError(Object data) {
			passcodeResponse(false);
		}

		@Override
		public void onSuccess(Object data) {
			passcodeResponse(true);
			storeFirstName();
			getActivity().getSupportFragmentManager().popBackStack(PasscodeForgotStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	};
	

}