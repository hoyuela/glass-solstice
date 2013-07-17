package com.discover.mobile.card.passcode.forgot;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.PasscodeLandingFragment;
import com.discover.mobile.card.passcode.model.json.VerifySyntax;
import com.discover.mobile.card.passcode.request.GetSyntaxValidityRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

public class PasscodeForgotStep1Fragment extends PasscodeBaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeForgotStep1";
	private static String TAG = "PasscodeForgotStep1Fragment";

	

	@Override
	public boolean onKey(View paramView, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			Log.v(TAG, "Action Down");
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			getActivity().getSupportFragmentManager().popBackStack(PasscodeLandingFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			makeFragmentVisible(new HomeSummaryFragment(), false);
		}
		return super.onKey(paramView, keyCode, event);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_forgot_step1_header);
		passcodeGuidelinesTV.setVisibility(View.VISIBLE);
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_FORGOT_STEP1);
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
		PasscodeForgotStep2Fragment pStep2= new PasscodeForgotStep2Fragment();
		Bundle b = new Bundle();
		b.putString("passcode", getPasscodeString());
		pStep2.setArguments(b);

		getActivity().
		getSupportFragmentManager()
		.beginTransaction()
		.setCustomAnimations(R.anim.fragment_slide_in_right , R.anim.fragment_slide_out_left, R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right)
		.replace(R.id.navigation_content, pStep2)
		//Adds the class name and fragment to the back stack
		.commit();
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