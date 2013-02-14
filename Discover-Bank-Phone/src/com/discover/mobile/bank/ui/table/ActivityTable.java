package com.discover.mobile.bank.ui.table;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;

/**
 * Table holding activity detail data
 * @author jthornton
 *
 */
public class ActivityTable extends BankTable{

	/**List of activities*/
	private ListActivityDetail activities;

	/**Boolean used to decide background of items*/
	private boolean isWhiteBackground = false;

	/**Index that the table is currently on to display data*/
	private int currentIndex = 0;

	private boolean isPosted;

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public ActivityTable(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		super.setTitle(this.getResources().getString(R.string.activity_table_name));
	}

	/**
	 * Show the items in the table
	 */
	public void showItems(final List<ActivityDetail> activities, final boolean isPosted) {
		this.isPosted = isPosted;
		if(null == activities || activities.isEmpty()){
			super.showNoDataMessage();
		}else{
			super.showDataView();
			createItems(activities);
		}
	}

	/**
	 * Create a simple table item and display the data in that item.
	 */
	private void createItems(final List<ActivityDetail> activities) {
		if(null == activities){return;}
		for(final ActivityDetail detail : activities){
			final BankTableItem item = new BankTableItem(context, null, currentIndex);
			item.setDate(detail.dates.formattedDate);
			item.setDescription(detail.description);
			item.setAmount(detail.amount);
			item.setOnClickListener(getClickListener(currentIndex));
			item.setBackgroundResource((isWhiteBackground) ? R.color.white
					: R.color.transaction_table_stripe);
			isWhiteBackground = (isWhiteBackground) ? false : true;
			super.addItem(item);
			currentIndex++;
		}
	}

	/**
	 * Get the click listener for the item
	 * @param index - index of the item in the list
	 * @return the click listener for the item
	 */
	private OnClickListener getClickListener(final int index){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				goToDetailsScreen(index);
			}
		};
	}

	/**
	 * Gather the data to go to the detail screen and then use the navigator to go there
	 * @param index - index of the selected item
	 */
	protected void goToDetailsScreen(final int index){
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST, activities);
		bundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, index);
		bundle.putInt(BankExtraKeys.CATEGORY_SELECTED, getSelectedCategory());
		bundle.putInt(BankExtraKeys.SORT_ORDER, super.getSortState());
		bundle.putBoolean(BankExtraKeys.TITLE_EXPANDED, true);
		BankNavigator.navigateToActivityDetailScreen(bundle);
	}

	/**
	 * Get the selected category
	 * @return - return the selected category
	 */
	private int getSelectedCategory(){
		return 0;
	}

	/**
	 * Get the string resource of the no items string
	 * @return - the string resource of the no items string
	 */
	@Override
	public int getNoItemsMessage() {
		return (isPosted) ? R.string.activity_no_posted : R.string.activity_no_scheduled;
	}

	/**
	 * @return the activities
	 */
	public ListActivityDetail getActivities() {
		return activities;
	}

	/**
	 * @param activities the activities to set
	 */
	public void setActivities(final ListActivityDetail activities) {
		if(this.activities != null){
			activities.activities.addAll(0, this.activities.activities);
		}
		this.activities = activities;
	}

	public void clearActivties(){
		if(null != activities && null != activities.activities){
			activities.activities.clear();
		}
	}
}