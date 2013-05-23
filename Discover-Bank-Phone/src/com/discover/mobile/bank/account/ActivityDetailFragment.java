package com.discover.mobile.bank.account;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.widgets.CustomOptionsMenu;

/**
 * The Fragment responsible for presenting detailed information about a users Transactions.
 * @author scottseward
 *
 */
public class ActivityDetailFragment extends DetailFragment implements FragmentOnBackPressed{

	private CustomOptionsMenu customOptionsMenu;

	@Override
	protected int getFragmentLayout() {
		return R.layout.transaction_detail;
	}

	/**
	 * Setup the fragment layout with necessary information found in the ActivityDetail object
	 * to present.
	 * This method is called by an AsyncTask.
	 */
	@Override
	protected void setupFragmentLayout(final View fragmentView) {
		final ActivityDetail item = (ActivityDetail)getArguments().getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		final Bundle bundle = getArguments();

		/**This flag indicates whether the activity that will be displayed is a posted or scheduled activity*/
		final boolean isPosted = bundle.getBoolean(BankExtraKeys.CATEGORY_SELECTED);

		if( isPosted ) {
			setupTransactionData(item, fragmentView);
		} else {
			setupScheduledTransactionData(item, fragmentView);
		}
	}


	/**
	 * Inserts the information provided by the ActivityDetail item into the content table that exists
	 * inside of the fragmentView layout.
	 * @param item an ActivityDetail object which contains information about a scheduled transaction.
	 * @param fragmentView
	 */
	private void setupScheduledTransactionData(final ActivityDetail item, final View fragmentView) {
		final ListItemGenerator generator = new ListItemGenerator(fragmentView.getContext());
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);
		List<ViewPagerListItem> items = null;
		contentTable.removeAllViews();
		if(ActivityDetail.TYPE_PAYMENT.equals(item.type)) {
			items = generator.getScheduledBillPayList(item);
		}
		else if(ActivityDetail.TYPE_DEPOSIT.equals(item.type)) {
			items = generator.getScheduledDepositList(item);
		}
		else if(ActivityDetail.TYPE_TRANSFER.equals(item.type)) {
			items = generator.getScheduledTransferList(item);

			final ReceivedUrl recievedUrl = item.links.get("self");
			if (recievedUrl.method.contains("DELETE")) {
				//If the Activity is of frequency One Time we want to use the "link" button type whereas if it a recurring transfer we want to allow the user
				//access to the "See Options" button so they can either delete the entire series or a single transfer.
				final boolean isOneTime = ActivityDetail.FREQUENCY_ONE_TIME_TRANSFER.equalsIgnoreCase(item.frequency);
				final Button deleteTransfersButton = (Button)((isOneTime) ? fragmentView.findViewById(R.id.delete_one_time_transfer_button) :
					fragmentView.findViewById(R.id.delete_recurring_transfer_button));
				final NavigationRootActivity activity = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();

				deleteTransfersButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(final View v) {
						if (isOneTime) {
							showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_ONE_TIME_TRANSFER);
						} else {
							//In the case of a recurring transfer deletion we need to pop up a menu allowing the user to decide if the
							//deletion is specific just to the next transfer in the series or the entire series needs to be removed.
							customOptionsMenu = new CustomOptionsMenu(activity, R.layout.delete_recurring_transfers_menu);

							customOptionsMenu.addOnClickListener(R.id.delete_next_transfer_in_series_button, new OnClickListener() {

								@Override
								public void onClick(final View v) {
									showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_NEXT_TRANSFER);
								}

							});

							customOptionsMenu.addOnClickListener(R.id.delete_entire_transfer_series_button, new OnClickListener() {

								@Override
								public void onClick(final View v) {
									showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_ALL_TRANSFERS);
								}

							});

							customOptionsMenu.addOnClickListener(R.id.cancel_button, new OnClickListener() {

								@Override
								public void onClick(final View v) {
									customOptionsMenu.dismiss();
								}

							});


							customOptionsMenu.addOnClickListener(R.id.delete_recurring_transfers_relative_layout, new OnClickListener() {

								@Override
								public void onClick(final View v) {
									customOptionsMenu.dismiss();
								}

							});

							customOptionsMenu.addAnimation(R.style.delete_recurring_animation);

							activity.showCustomAlert(customOptionsMenu);
						}
					}
				});
				deleteTransfersButton.setVisibility(View.VISIBLE);
			}
		}

		//Add the items to the content table.
		if(items != null){
			for(final ViewPagerListItem row : items){
				contentTable.addView(row);
			}
		}
	}

	private void showDeleteTransactionModal(final NavigationRootActivity activity, final ActivityDetail item, final TransferDeletionType deleteType) {
		// Create a one button modal to notify the user that they are leaving the application
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
				R.string.bank_delete_transfer_title,
				R.string.bank_delete_transfer_text,
				R.string.bank_yes_delete);

		/**
		 * Hide the need help footer for the delete modal.
		 */
		final ModalDefaultTopView topView = (ModalDefaultTopView)modal.getTop();
		topView.hideNeedHelpFooter();

		//Set the click listener that will delete the payment
		modal.getBottom().getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToDeleteTransferConfirmation(item, deleteType);
				modal.dismiss();

				if (deleteType != TransferDeletionType.DELETE_ONE_TIME_TRANSFER) {
					customOptionsMenu.dismiss();
				}
			}
		});

		activity.showCustomAlert(modal);
	}

	/**
	 * Sets up this fragment do display standard Transaction data.
	 * This data is data which has already been posted to the current account and is not scheduled.
	 * @param item an ActivityDetail item which contains information that can describe the
	 * 				posted ActivityDetail.
	 * @param fragmentView the layout which will contains the content table that will be populated with data.
	 */
	private void setupTransactionData(final ActivityDetail item, final View fragmentView) {
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);

		((TextView)contentTable.findViewById(R.id.amount_cell))
		.setText(BankStringFormatter.convertCentsToDollars(item.amount.value));
		((TextView)contentTable.findViewById(R.id.description_cell)).setText(item.description);
		final TextView transactionId = ((TextView)contentTable.findViewById(R.id.transaction_id));
		if(!item.id.equals("0")){
			transactionId.setText(item.id);
		}else{
			transactionId.setVisibility(View.GONE);
		}

		((TextView)contentTable.findViewById(R.id.date_cell)).setText(
				BankStringFormatter.convertDate(
						item.getTableDisplayDate().split(ActivityDetail.DATE_DIVIDER)[0]));

		final int value = item.getBalanceValue();
		((TextView)contentTable.findViewById(R.id.balance_cell))
		.setText(BankStringFormatter.convertCentsToDollars(value));
	}

	@Override
	public void onBackPressed() {
		if (customOptionsMenu.isShowing()) {
			customOptionsMenu.dismiss();
		} else {
			final Activity activity = DiscoverActivityManager.getActiveActivity();
			activity.onBackPressed();
		}
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}
}
