package com.discover.mobile.card.passcode.enable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.ui.modals.EnhancedContentModal;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.PasscodeRouter;
import com.discover.mobile.card.passcode.request.CreateBindingRequest;
import com.discover.mobile.card.passcode.request.GetMatchRequest;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.utils.PasscodeUtils;

public class PasscodeEnableStep2Fragment extends PasscodeBaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeEnableStep2";
	private static String TAG = "PasscodeEnableStep2Fragment";
	
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
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
			new GetMatchRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new MatchRequestListener(deviceToken));
//			new CreateBindingRequest(this.getActivity(), deviceToken).loadDataFromNetwork(new EnableRequestListener(deviceToken));
		} else {
			passcodeResponse(false);
		}
	}

	@Override
	public void onPasscodeSuccessEvent() {
		final Context context = DiscoverActivityManager.getActiveActivity();
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_ENABLE_OVERLAY);
		final EnhancedContentModal modal = new EnhancedContentModal(context, 
				R.string.passcode_dialog_enabled_title, 
				R.string.passcode_dialog_enabled_content, 
				R.string.home_text,
				new NavigateACHomeAction(),
				new NavigateACHomeAction());
		modal.hideNeedHelpFooter();
		modal.setOrangeTitle();
		((NavigationRootActivity)context).showCustomAlert(modal);
		getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	@Override
	public void onPasscodeErrorEvent() {
		clearAllFields();
	}

	final PasscodeEnableStep2Fragment thisFrag = this;
	private final class MatchRequestListener implements CardEventListener {
		private String deviceToken;
		
		public MatchRequestListener(String deviceToken) {
			this.deviceToken = deviceToken;
		}
		@Override
		public void OnError(Object data) {
			CardErrorBean cardErrorBean = (CardErrorBean) data;
			Log.e(TAG, "Errorcode: " + cardErrorBean.getErrorCode());

			if (cardErrorBean.getErrorCode().contains("4002105")) {
				//fringe case when passcode is removed prior to user finising enable flow
				//TODO warn them first with error message that account doesn't have passcode?
				new PasscodeRouter(thisFrag).getStatusAndRoute();
				getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				passcodeResponse(false);
			}
		}

		@Override
		public void onSuccess(Object data) {
			new CreateBindingRequest(getActivity(), deviceToken).loadDataFromNetwork(new EnableRequestListener(deviceToken));
		}
	}
	
	private final class EnableRequestListener implements CardEventListener {
		private String deviceToken;
		
		public EnableRequestListener(String deviceToken) {
			this.deviceToken = deviceToken;
		}
		@Override
		public void OnError(Object data) {
			CardErrorBean cardErrorBean = (CardErrorBean) data;
			Log.e(TAG, "Errorcode: " + cardErrorBean.getErrorCode());

			if (cardErrorBean.getErrorCode().contains("4002105")) {
				//fringe case when passcode is removed prior to user finising enable flow
				//TODO warn them first with error message that account doesn't have passcode?
				new PasscodeRouter(thisFrag).getStatusAndRoute();
				getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				passcodeResponse(false);
			}
		}

		@Override
		public void onSuccess(Object data) {
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
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return AnalyticsPage.PASSCODE_ENABLE_STEP2;
	};
	
}