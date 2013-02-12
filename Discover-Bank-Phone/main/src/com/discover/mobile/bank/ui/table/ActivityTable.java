package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.discover.mobile.bank.BankNavigator;
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

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param attrs - layout attributes
	 */
	public ActivityTable(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		super.setTitle("Transactions");
	}

	/**
	 * Show the items in the table
	 */
	@Override
	public void showItems() {
		if(null == this.activities || this.activities.activities.isEmpty()){
			super.showNoDataMessage();
		}else{
			super.showDataView();
			createItems();
		}
	}

	/**
	 * Create a simple table item and display the data in that item.
	 */
	private void createItems() {
		final int length = this.activities.activities.size();
		ActivityDetail detail;
		for(int index = 0; index < length; index++){
			detail = this.activities.activities.get(index);
			final BankTableItem item = new BankTableItem(this.context, null, index);
			item.setDate(detail.dates.formattedDate);
			item.setDescription(detail.description);
			item.setAmount(detail.amount);
			item.setOnClickListener(getClickListener(index));
			super.addItem(item);
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
		bundle.putSerializable(BankTable.DATA_LIST, this.activities);
		bundle.putInt(BankTable.DATA_SELECTED_INDEX, index);
		bundle.putInt(BankTable.CATEGORY_SELECTED, getSelectedCategory());
		bundle.putInt(BankTable.SORT_ORDER, super.getSortState());
		bundle.putBoolean(BankTable.TITLE_EXPANDED, true);
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
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the activities
	 */
	public ListActivityDetail getActivities() {
		return this.activities;
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
}