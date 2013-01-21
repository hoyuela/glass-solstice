package com.discover.mobile.section.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	private static final String FRAGMENT = "fragment";
	
	private RecentActivityPeriodsDetail periods;
	
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
	
	@Override
	public void onSaveInstanceState(final Bundle outState){
		this.getFragmentManager().putFragment(outState, FRAGMENT, fragment);
		super.onSaveInstanceState(outState);
	}
	
	public void resumeFragment(final Bundle savedInstanceState){
		if(null == savedInstanceState){return;}
		this.fragment = 
				(AccountRecentActivityFragment)this.getFragmentManager().getFragment(savedInstanceState, FRAGMENT);
	}
	
	public void setReturnFragment(final AccountRecentActivityFragment fragment){
		this.fragment = fragment;
	}
	
	protected void setRangeInReturnFragment(final RecentActivityPeriodDetail recentActivityPeriodDetail){
		if(null == this.fragment){
			this.fragment = new AccountRecentActivityFragment();
		}
		this.fragment.setDateRange(recentActivityPeriodDetail);
		super.makeFragmentVisible(fragment);
	}
	
	public void displayDateRanges(final RecentActivityPeriodsDetail periods){
		this.periods = periods;
		for(RecentActivityPeriodDetail detail : periods.dates){
			final ChoosePeriodItem item = new ChoosePeriodItem(context, null, detail);
			item.setOnClickListener(getClickListener());
			dates.addView(item);
			
		}
	}

	private OnClickListener getClickListener() {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final ChoosePeriodItem view = (ChoosePeriodItem) v;
				setRangeInReturnFragment(view.getPeriod());
			}
		};
	}


}
