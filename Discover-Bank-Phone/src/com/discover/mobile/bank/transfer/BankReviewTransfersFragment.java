package com.discover.mobile.bank.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.transfer.TransferType;
import com.discover.mobile.bank.ui.table.LoadMoreBaseTable;
import com.discover.mobile.bank.ui.table.LoadMoreTableHeader;
import com.discover.mobile.bank.ui.table.TableTitles;
import com.discover.mobile.common.ui.table.TableHeaderButton;
import com.discover.mobile.common.utils.StringUtility;

public class BankReviewTransfersFragment extends LoadMoreBaseTable{
	private static final long serialVersionUID = 5179969192763576736L;

	@Override
	public View onCreateView(final LayoutInflater inflater, 
			final ViewGroup container, 															
			final Bundle savedInstanceState) {	
		final View mainView = super.onCreateView(inflater, container, savedInstanceState);

		return mainView;
	}

	@Override
	protected void addButtonsToHeader(final LoadMoreTableHeader header) {
		header.associateButtonsWithEnum(TransferType.Scheduled, TransferType.Completed, TransferType.Cancelled);
	}

	@Override
	protected void setTableTitles(final TableTitles titleRow) {
		titleRow.setLabel1("Date");
		titleRow.setLabel2("From/To Account");
		titleRow.setLabel3("Amount");
	}

	@Override
	public void goToDetailsScreen(final int index) {

	}

	@Override
	public void showFooterMessage() {

	}

	@Override
	public int getActionBarTitle() {
		return R.string.review_transfers_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.REVIEW_TRANSFERS_SECTION;
	}

	@Override
	public void setDefaultList(final Enum<?> defaultList) {

	}

	@Override
	public Enum<?> getDefaultList() {
		return null;
	}

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
				formattableMessage = removeDoubleSpaces(formattableMessage);
				break;
			}
		}else {
			formattableMessage = removeDoubleSpaces(formattableMessage);	
		}

		return formattableMessage;
	}

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
			BankReviewTransfersFragment.super.showEmptyTableForServiceCall();
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
