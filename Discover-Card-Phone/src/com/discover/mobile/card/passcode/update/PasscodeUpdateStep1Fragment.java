package com.discover.mobile.card.passcode.update;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.passcode.PasscodeBaseFragment;
import com.discover.mobile.card.passcode.request.GetMatchRequest;
import com.discover.mobile.common.analytics.AnalyticsPage;

/**
 * Step 1 of the update passcode flow.
 * 
 * @author sgoff0
 * 
 */
public class PasscodeUpdateStep1Fragment extends PasscodeBaseFragment {
	private static String TAG = "PasscodeUpdateStep1Fragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getPageName() {
		return AnalyticsPage.PASSCODE_UPDATE_STEP1;
	};

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setHeaderText(R.string.passcode_update_step1_header);
		return view;
	}

	@Override
	public void onPasscodeSubmitEvent() {
		boolean isValid = this.isPasscodeValidLocally(getPasscodeString());
		if (isValid) {
			new GetMatchRequest(this.getActivity(), getPasscodeString()).loadDataFromNetwork(new VerifyPasscodeMatchRequestListener());
		} else {
			passcodeResponse(false);
		}
	}
	
	@Override
	public void onPasscodeErrorEvent() {
		clearAllFields();
	}

	@Override
	public void onPasscodeSuccessEvent() {
		if (isAdded()){
			makeFragmentVisible(new PasscodeUpdateStep2Fragment(), false);
		}
	}
		
	private final class VerifyPasscodeMatchRequestListener implements CardEventListener {
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