package com.discover.mobile.card.passcode.update;

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
import com.discover.mobile.card.passcode.request.UpdatePasscodeRequest;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.nav.NavigationRootActivity;

public class PasscodeUpdateStep3Fragment extends PasscodeBaseFragment {
	
	private static String TAG = "PasscodeUpdateStep3Fragment";
	private static String mStep2Answer;

	@Override
	public String getPageName() {
		return AnalyticsPage.PASSCODE_UPDATE_STEP3;
	};

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		mStep2Answer = getArguments().getString("passcode");
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_update_step3_header);
		return view;
	}

	@Override
	public void onPasscodeErrorEvent() {
		Log.v(TAG, "Firing error event");
		//TODO on error go back to step 2 of 3
		makeFragmentVisible(new PasscodeUpdateStep2Fragment(), false);
		clearAllFields();
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isMatch = this.getPasscodeString().equals(mStep2Answer);
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isMatch && isValid) {
			new UpdatePasscodeRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new UpdatePasscodeRequestListener());
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
//		dialogHelper(MODAL_PASSCODE_UPDATED, "Home", true, new NavigateACHomeAction(), new NavigatePasscodeLandingAction());
		final Context context = DiscoverActivityManager.getActiveActivity();
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_UPDATE_OVERLAY);
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.passcode_dialog_updated_title, 
				R.string.passcode_dialog_updated_content, 
				R.string.home_text,
				new NavigateACHomeAction(),
				new NavigatePasscodeLandingAction());
		modal.hideNeedHelpFooter();
		modal.setOrangeTitle();
		((NavigationRootActivity)context).showCustomAlert(modal);
		getActivity().getSupportFragmentManager().popBackStack(PasscodeUpdateStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
			//TODO make this result accurate from the service call
			Log.v(TAG, "Success!");
			storeFirstName();
			getActivity().getSupportFragmentManager().popBackStack(PasscodeUpdateStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			passcodeResponse(true);
		}
	};

}