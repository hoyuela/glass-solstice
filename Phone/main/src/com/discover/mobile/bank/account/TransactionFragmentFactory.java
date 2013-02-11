package com.discover.mobile.bank.account;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.ViewPagerFragmentFactory.ActivityItem;

/**
 * This class is in active development.
 * 
 * The purpose of this class is designed to accept a data object for some JSON object
 * and it will return a Fragment which contains that information in a scrollable layout
 * with a table of the data and any applicable buttons. This is used by the ViewPager.
 * 
 * @author scottseward
 *
 */
public class TransactionFragmentFactory extends Fragment {
	
	/** The key to pass a activity item to the fragment that is getting constructed*/
	private final String ITEM_KEY = "a";
	
	/**
	 * This method returns a fully assembled fragment for the provided ActivityItem.
	 * It determines what style of table it needs to put the data into then generates
	 * and returns it as a fragment.
	 * 
	 * @param item
	 * @return
	 */
	public static Fragment getFragmentForData(final ActivityItem item ) {
		
		if("TRANSFER".equals(item.status)) {
			return new FundsTransferDetailItem();
		}
			
		return null;
	}	
	
	/**
	 * Same concept as getFragmentForData, given a ActivityItem, determine what title needs 
	 * to be displayed in the Fragment.
	 * 
	 * @param item
	 * @return
	 */
	public static int getTitleForData(final ActivityItem item) {
		if("TRANSFER".equals(item.status))
			return R.string.funds_transfer;
		else if("BILL_PAY".equals(item.status))
			return R.string.bill_pay;
		else if("CHECK_DEPOSIT".equals(item.status))
			return R.string.check_deposit;
		else if ("TRANSACTION".equals(item.status))
			return R.string.transaction;
		return 0;
	}
	
	/**
	 * this detail item class is able to return the individual table cells to present
	 * in the transaction detail fragment.
	 * 
	 * @author scottseward
	 *
	 */
	protected static abstract class DetailItem extends Fragment{
		protected ActivityItem activityItem = null;
		private ViewPagerListItem listItem;
		private final Activity currentActivity = this.getActivity();
		private final String ITEM_KEY = "a";
		
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if(getArguments() != null)
				activityItem = (ActivityItem)getArguments().getSerializable(ITEM_KEY);
		}
		
		private void initNewListItem() {
			listItem = new ViewPagerListItem(currentActivity);
		}
		
		public ViewPagerListItem getTwoItemCell(final int topLabelResource, final String middleLabelText) {
			initNewListItem();
			if(middleLabelText != null) {
				listItem.getTopLabel().setText(topLabelResource);
				listItem.getMiddleLabel().setText(middleLabelText);
				listItem.getBottomLabel().setVisibility(View.GONE);
			}
			return listItem;
		}
		
		public ViewPagerListItem getThreeItemCell(final int topLabelResource, final String middleLabelText, 
				final String bottomLabelText) {
			getTwoItemCell(topLabelResource, middleLabelText);
			listItem.getBottomLabel().setVisibility(View.VISIBLE);
			listItem.getBottomLabel().setText(bottomLabelText);
			
			return listItem;
		}
		
		public ViewPagerListItem getMemoItemCell(final String memo) {
			getTwoItemCell(R.string.memo, memo);
			listItem.getMiddleLabel().setTextAppearance(currentActivity, R.style.field_copy);
			
			return listItem;
		}
		
		public ViewPagerListItem getFromCell(final String from, final String balance) {
			return getThreeItemCell(R.string.from, from, getString(R.string.available_balance) + " " + balance);
		}
		
		public ViewPagerListItem getToCell(final String to) {
			return getTwoItemCell(R.string.to, to);
		}
		
		public ViewPagerListItem getSendOnCell(final String sendOn) {
			return getTwoItemCell(R.string.send_on, sendOn);
		}
		
		public ViewPagerListItem getDeliverByCell(final String deliverByDate) {
			return getTwoItemCell(R.string.deliver_by, deliverByDate);
		}
		
		public ViewPagerListItem getFrequencyCell(final String frequency) {
			return getTwoItemCell(R.string.frequency, frequency);
		}
	}
	/**
	 * This is a specific style of detail item.
	 * It calls the appropriate methods from the DetailItem class to fill the content table
	 * with list items and data.
	 * 
	 * @author scottseward
	 *
	 */
	protected static class FundsTransferDetailItem extends DetailItem {
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View mainView = inflater.inflate(R.layout.scheduled_transactions_detail, null);
			final LinearLayout contentTable = (LinearLayout)mainView.findViewById(R.id.content_table);
			final ViewPagerListItem item = new ViewPagerListItem(this.getActivity());

			item.getTopLabel().setText(R.string.amount);
			item.getMiddleLabel().setText(activityItem.from);
			
			contentTable.addView(item);
						
			return mainView;
		}
	}
}

