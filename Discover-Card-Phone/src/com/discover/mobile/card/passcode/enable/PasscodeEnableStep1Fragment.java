package com.discover.mobile.card.passcode.enable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthDefaultResponseHandler;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

public class PasscodeEnableStep1Fragment extends BaseFragment  {
	private static String TAG = "PasscodeEnableStep1Fragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_ENABLE_STEP1);
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		//If you return a View from here, you will later be called in onDestroyView() when the view is being released.
		Log.v(TAG, "onCreateView");
		final View view = inflater.inflate(R.layout.passcode_enable_step_1,
				null);

		final Button yesButton = ((Button) view.findViewById(R.id.buttonYes));
		final TextView linkNoThanks = ((TextView) view.findViewById(R.id.linkNoThanks));

		yesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final StrongAuthHandler authHandler = new StrongAuthHandler(
						PasscodeEnableStep1Fragment.this.getActivity(),
						new EnablePasscodeStrongAuthFlow(), false);
				authHandler.strongAuth();
			}
		});

		linkNoThanks.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				makeFragmentVisible(new HomeSummaryFragment());
				//TODO this seems to show make a payment page rather than home page.
				//if I call just the popBackStack logic the first time I click no thanks it goes back properly but side menu is wrong, second time paymetn summary shows
//				getActivity().getSupportFragmentManager().popBackStack(PasscodeEnableStep1Fragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});
		Utils.setFooter(view, getActivity());
		return view;
	}

	@Override
	public int getActionBarTitle() {
        return R.string.sub_section_title_passcode;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.PROFILE_AND_SETTINGS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.PASSCODE_SECTION;
	}
	
	private final class EnablePasscodeStrongAuthFlow extends StrongAuthDefaultResponseHandler{
		@Override
		public void onStrongAuthSucess(Object data) {
			Log.v(TAG, "Success");
			makeFragmentVisible(new PasscodeEnableStep2Fragment());
		}
	}

}