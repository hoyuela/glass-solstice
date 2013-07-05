package com.discover.mobile.bank.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.account.activity.ActivityDetailType;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.transfer.TransferType;
import com.discover.mobile.bank.ui.table.LoadMoreBaseTable;
import com.discover.mobile.bank.ui.table.LoadMoreTableHeader;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.common.ui.table.TableHeaderButton;
import com.discover.mobile.common.utils.StringUtility;
/**
 * The Fragment that provides an implementation for a LoadMoreBaseTable and presents
 * types of Transfers in a list to the user.
 * 
 * @author scottseward
 *
 */
public class BankReviewTransfersFragment extends LoadMoreBaseTable {
	private static final long serialVersionUID = 5179969192763576736L;

	@Override
	public View onCreateView(final LayoutInflater inflater, 
			final ViewGroup container, 															
			final Bundle savedInstanceState) {	
		final View mainView = super.onCreateView(inflater, container, savedInstanceState);

		return mainView;
	}
	
	/**
	 * Add buttons to the header of the table so a user can navigate between possible transfer
	 * tables.
	 */
	@Override
	protected void addButtonsToHeader(final LoadMoreTableHeader header) {
		header.associateButtonsWithEnum(TransferType.Scheduled, TransferType.Completed, TransferType.Cancelled);
	}
	
	/**
	 * Set the tiles of the columns that are used for the table.
	 */
	@Override
	protected void setTableTitles(final TableTitles titleRow) {
		titleRow.setLabel1("Date");
		titleRow.setLabel2("From/To Account");
		titleRow.setLabel3("Amount");
	}

	/**
	 * Launches the ViewPager with the selected (index) item visible in the view
	 * pager.
	 * 
	 * @param the index to show first in the ViewPager.
	 */
	@Override
	public void goToDetailsScreen(final int index) {
		final ListActivityDetail activityDetailList = new ListActivityDetail(getCurrentList(), 
																				ActivityDetailType.Transfer);;
		final Bundle bundle = new Bundle();
		bundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, index);
		bundle.putSerializable(BankExtraKeys.PRIMARY_LIST, activityDetailList);
		bundle.putSerializable(BankExtraKeys.REVIEW_TRANSFERS_TYPE, getCurrentListKey());
		bundle.putInt(BankExtraKeys.GROUP_MENU_OVERRIDE, getGroupMenuLocation());
		bundle.putInt(BankExtraKeys.SECTION_MENU_OVERRIDE, getSectionMenuLocation());
		bundle.putInt(BankExtraKeys.TITLE_TEXT, R.string.funds_transfer_details);

		BankConductor.navigateToActivityDetailScreen(bundle);
	}

	@Override
	public void showFooterMessage() {

	}

	/**
	 * Will be called when setting up the Fragment to set the title for the page.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.review_transfers_title;
	}

	/**
	 * Set the highlighted group location in the sliding menu for Review Transfers.
	 */
	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	/**
	 * Set the highlighted section location in the sliding menu for Review Transfers.
	 */
	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.REVIEW_TRANSFERS_SECTION;
	}

	/**
	 * Returns a String for a given TransferType to be displayed upon that TransferType list 
	 * being empty.
	 */
	@Override
	public String getEmptyListMessageForEnum(final Enum<?> listType) {
		String formattableMessage = getResources().getString(R.string.no_transfers_in_list);

		if(listType != null) {
			switch((TransferType)listType) {
				case Cancelled:
					formattableMessage = String.format(formattableMessage, getResources().getString(R.string.cancelled));
					break;
				case Scheduled:
					formattableMessage = String.format(formattableMessage, getResources().getString(R.string.scheduled));
					break;
				case Completed:
					formattableMessage = String.format(formattableMessage, getResources().getString(R.string.completed));
					break;
				default:
					//If no ListType was passed into the method, then display a generic "You have no transfers" message.
					formattableMessage = removeDoubleSpaces(formattableMessage);
					break;
			}
		}else {
			//If no ListType was passed into the method, then display a generic "You have no transfers" message.
			formattableMessage = removeDoubleSpaces(formattableMessage);	
		}

		return formattableMessage;
	}

	/**
	 * 
	 * @param somethingWithDoubleSpaces a String that may contain two spaces grouped together.
	 * @return a String where all sets of two spaces next to eachother are replaced with a single
	 * space.
	 */
	private String removeDoubleSpaces(final String somethingWithDoubleSpaces) {
		return String.format(somethingWithDoubleSpaces, StringUtility.EMPTY)
				.replaceAll(StringUtility.SPACE + StringUtility.SPACE, 
						StringUtility.SPACE);
	}

	@Override
	public void onClick(final View view) {
		final TableHeaderButton clicked = (TableHeaderButton) view;
		final LoadMoreTableHeader header = getHeader();

		if(!header.isButtonSelected(clicked.getAssociatedEnum())){
			navigate((TransferType)clicked.getAssociatedEnum());
		}
	}

	protected void navigate(final TransferType type){
		if(type != null) {
			BankConductor.navigateToReviewTransfers(type);
		}
	}

	/**
	 * Get the array that will be used to display the labels on the buttons
	 * @return the resource array to be used as labels on the buttons
	 */
	@Override
	public int getButtonResourceArray() {
		return R.array.bank_review_transfers_buttons;
	}

}
