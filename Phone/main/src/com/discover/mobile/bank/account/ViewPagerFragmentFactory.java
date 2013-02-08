package com.discover.mobile.bank.account;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.R;


/**
 * This class will be deleted soon. It is unnecessary in the long run. 
 * 
 * @author scottseward
 *
 */
public class ViewPagerFragmentFactory extends FragmentStatePagerAdapter implements Serializable {
	private static final long serialVersionUID = -1837431384828492628L;
	
	/**
	 * This is a temporary placeholder item for development purposes, it will be replaced
	 * with a Java object that is used to map JSON data.
	 * 
	 * @author scottseward
	 *
	 */
	public static class ActivityItem{
		final int DEPOSIT = 0;
		final int TRANSFER = 1;
		public String status;
		public String amount;
		public String from;
		public String availableBalance;
		public String to;
		public String sendOn;
		public String deliverBy;
		public String frequency;
		
		public int transactionType;
	}
	private final List<ActivityItem> dataSet;
	
	public ViewPagerFragmentFactory(final FragmentManager fragmentManager, final List<ActivityItem> dataSet) {
		super(fragmentManager);
		this.dataSet = dataSet;
	}

	/**
	 * Returns a Fragment to be presented in the ViewPager
	 */
	@Override
	public Fragment getItem(final int position) {
		return getDetailItem(position);
	}

	/**
	 * Returns the number of fragments that will be presented in the ViewPager
	 */
	@Override
	public int getCount() {
		return dataSet.size();
	}
	
	private Fragment getDetailItem(final int position) {
		return new DetailTableFragment();
	}
	
	private static class FragmentFactory{
		
	}
	

	public static class FundsTransferFragment extends Fragment {
		private int dataPosition;
		
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			dataPosition = getArguments() != null ? getArguments().getInt("position") : 1;
		}
		
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstancestate) {
			
			return null;
		}
	}
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
}

