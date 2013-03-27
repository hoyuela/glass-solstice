package com.discover.mobile.card.account.recent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.services.account.recent.RecentActivityPeriodDetail;
import com.discover.mobile.card.services.account.recent.RecentActivityPeriodsDetail;
import com.discover.mobile.common.BaseFragment;
/**
 * Fragment that allows the user to select a list of dates that can be selected as sort
 * criteria for the account activity fragment.
 * 
 * @author jthornton
 *
 */
public class ChooseDateRangeFragment extends BaseFragment{
	
	/**Account activity fragment that needs to get the new date range*/
	private AccountRecentActivityFragment fragment;
	
	/**Linear layout to hold all the dates*/
	private LinearLayout dates;
	
	/**Activity context*/
	private Context context;
	
	/**Key to get the account activity fragment in and out of the bundle*/
	private static final String FRAGMENT = "fragment";
	
	/**List of periods to be shown*/
	private RecentActivityPeriodsDetail periods;
	
	/**
	 * Create the view
	 * @param inflater - used to inflate the layout
	 * @param container - container holding the view
	 * @param savedInstanceState - state of the fragment
	 * @return the view
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.choose_period, null);
		
		dates = (LinearLayout) view.findViewById(R.id.dates);
		context = this.getActivity();
		
		resumeFragment(savedInstanceState);
		displayDateRanges();
		return view;
	}

	/**
	 * Get the action bar title
	 * @return the action bar title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.recent_activity_title;
	}
	
	/**
	 * Save the current state of the fragment
	 * @param outState - bundle to put the state in
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		final FragmentManager manager = this.getFragmentManager();
		if(null != manager && null != outState && null != fragment){
			manager.putFragment(outState, FRAGMENT, fragment);
			super.onSaveInstanceState(outState);
			CardSessionContext.getCurrentSessionDetails().setPeriods(periods);
		}
	}
	
	/**
	 * Resume the fragment
	 * @param savedInstanceState - bundle holding the state
	 */
	public void resumeFragment(final Bundle savedInstanceState){
		if(null == savedInstanceState){return;}
		final FragmentManager manager = this.getFragmentManager();
		if(null != manager){
			this.fragment = (AccountRecentActivityFragment)manager.getFragment(savedInstanceState, FRAGMENT);
			this.periods = CardSessionContext.getCurrentSessionDetails().getPeriods();
		}
	}
	
	/**
	 * Set the fragment that needs to have the selected period returned to it
	 * @param fragment- the fragment that needs to have the selected period returned to it
	 */
	public void setReturnFragment(final AccountRecentActivityFragment fragment){
		this.fragment = fragment;
	}
	
	/**
	 * Set the range in the return fragment and show the return fragment
	 * @param recentActivityPeriodDetail - period to be selected in the return fragment
	 */
	protected void setRangeInReturnFragment(final RecentActivityPeriodDetail recentActivityPeriodDetail){
		if(null == this.fragment){
			this.fragment = new AccountRecentActivityFragment();
		}
		this.fragment.setDateRange(recentActivityPeriodDetail);
		super.makeFragmentVisible(fragment);
	}
	
	/**
	 * Display the periods 
	 */
	public void displayDateRanges(){
		for(RecentActivityPeriodDetail detail : periods.dates){
			final ChoosePeriodItem item = new ChoosePeriodItem(context, null, detail);
			item.setOnClickListener(getClickListener());
			dates.addView(item);
			
		}
	}

	/**
	 * Get the click listener for the items in the date list
	 * @return the click listener for the items in the date list
	 */
	private OnClickListener getClickListener() {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final ChoosePeriodItem view = (ChoosePeriodItem) v;
				setRangeInReturnFragment(view.getPeriod());
			}
		};
	}

	/**
	 * Set the periods to be shown in the list
	 * @param periods - the periods to be shown in the list
	 */
	public void setPeriods(final RecentActivityPeriodsDetail periods){
		this.periods = periods;
	}
}
