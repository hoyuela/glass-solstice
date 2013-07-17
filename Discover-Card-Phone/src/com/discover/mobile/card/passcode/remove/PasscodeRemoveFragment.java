package com.discover.mobile.card.passcode.remove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.passcode.PasscodeLandingFragment;
import com.discover.mobile.card.passcode.PasscodeRouter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.utils.PasscodeUtils;


public class PasscodeRemoveFragment extends BaseFragment {
	static final String TRACKING_PAGE_NAME = "PasscodeRemove";
	private static String TAG = "PasscodeRemoveFragment";
	protected PasscodeUtils pUtils;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		pUtils = new PasscodeUtils(this.getActivity().getApplicationContext());
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.passcode_remove,
				null);

		final Button removeButton = ((Button) view.findViewById(R.id.buttonRemove));
		final TextView linkNoThanks = ((TextView) view.findViewById(R.id.linkNoThanks));
		
		final PasscodeRemoveFragment prf = this;
		removeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pUtils.deletePasscodeToken();
				getActivity().getSupportFragmentManager().popBackStack();
				new PasscodeRouter(prf).getStatusAndRoute();
//				makeFragmentVisible(new PasscodeLandingFragment());
			}
		});

		linkNoThanks.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				makeFragmentVisible(new HomeSummaryFragment());
			}
		});
		TrackingHelper.trackPageView(AnalyticsPage.PASSCODE_REMOVE);
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
}