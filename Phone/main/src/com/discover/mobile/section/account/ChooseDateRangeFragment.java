package com.discover.mobile.section.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.common.account.recent.GetActivityPeriods;
import com.discover.mobile.common.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.common.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;

public class ChooseDateRangeFragment extends BaseFragment{
	
	private AccountRecentActivityFragment fragment;
	
	private LinearLayout dates;
	
	private Context context;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.choose_period, null);
		
		final AsyncCallback<RecentActivityPeriodsDetail> callback = 
				GenericAsyncCallback.<RecentActivityPeriodsDetail>builder(this.getActivity())
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new GetActivityPeriodsSuccessListener(this))
				.withErrorResponseHandler(null)
				.build();
		
		new GetActivityPeriods(getActivity(), callback).submit();
		
		dates = (LinearLayout) view.findViewById(R.id.dates);
		
		context = this.getActivity();
		return view;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.recent_activity_title;
	}
	
	public void setReturnFragment(final AccountRecentActivityFragment fragment){
		this.fragment = fragment;
	}
	
	protected void setRangeInReturnFragment(){
		this.fragment.setDateRange();
		super.makeFragmentVisible(fragment);
	}
	
	public void displayDateRanges(final RecentActivityPeriodsDetail details){
		for(RecentActivityPeriodDetail detail : details.dates){
			final ChoosePeriodItem item = new ChoosePeriodItem(context, null, detail);
			dates.addView(item);
		}
	}

}
