package com.discover.mobile.bank.account;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.R;
import com.discover.mobile.bank.account.ViewPagerFragmentFactory.ActivityItem;

/**
 * This is a subclass of the DetailView pager.
 * The purpose of any subclass of the DetailViewPager is to handle any functionality that
 * is specific to where the ViewPager is needed. This class specifically will handle
 * passing data to and from the previous Fragment (Scheduled transactions list)
 * @author scottseward
 *
 */
public class ScheduledTransactionsViewPager extends DetailViewPager {
	List<ActivityItem> activityItems = null;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.transaction_details;
	}

	@Override
	protected Fragment getDetailItem(final int currentPosition) {
		return new DetailTableFragment();
	}
	
	@Override
	protected int getInitialViewPosition() {
		return 3;
	}
	
	/**
	 * Test fragment. Will be deleted once the TransactionFragmentFactory is completed.
	 * @author scottseward
	 *
	 */
	public static class DetailTableFragment extends Fragment {
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View mainView = inflater.inflate(R.layout.scheduled_transactions_detail, null);
			
			final LinearLayout contentTable = (LinearLayout)mainView.findViewById(R.id.content_table);
			
			for(int i = 0; i < 7; ++i){
					
				final ViewPagerListItem item = new ViewPagerListItem(this.getActivity());
				if(i == 0)
					item.getDividerLine().setVisibility(View.GONE);
				item.getTopLabel().setText("Top Item " + i);
				item.getMiddleLabel().setText("Middle item " + i);
				if(i % 2 == 0)
					item.getBottomLabel().setVisibility(View.GONE);
				else
					item.getBottomLabel().setText("Bottom item " + i);
				contentTable.addView(item);
			}
			return mainView;
		}
	}

	@Override
	protected int getViewCount() {
		// TODO Auto-generated method stub
		return activityItems.size();
	}

	@Override
	protected List<ActivityItem> getDataSet() {
		return activityItems;
	}

}
