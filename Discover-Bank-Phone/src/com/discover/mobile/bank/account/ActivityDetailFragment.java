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
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.XHttpMethodOverrideValues;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.SimpleContentModal;
import com.discover.mobile.common.ui.widgets.CustomOptionsMenu;

/**
 * The Fragment responsible for presenting detailed information about a users Transactions.
 * @author scottseward
 *
 */
public class ActivityDetailFragment extends DetailFragment implements FragmentOnBackPressed{
	private static final String SELF = "self";
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
			showDeleteButonForDetailIfNeeded(item, fragmentView);
		}
		else if(ActivityDetail.TYPE_DEPOSIT.equals(item.type)) {
			items = generator.getScheduledDepositList(item);
		}
		else if(ActivityDetail.TYPE_TRANSFER.equals(item.type)) {
			items = generator.getScheduledTransferList(item);

			final ReceivedUrl recievedUrl = item.links.get(SELF);
			if (recievedUrl.method.contains(XHttpMethodOverrideValues.DELETE.toString())) {
				//If the Activity is of frequency One Time we want to use the "link" button type whereas if it a 
				//recurring transfer we want to allow the user
				//access to the "See Options" button so they can either delete the entire series or a single transfer.
				final boolean isOneTime = ActivityDetail.FREQUENCY_ONE_TIME_TRANSFER.equalsIgnoreCase(item.frequency);
				final Button deleteTransfersButton = (Button)((isOneTime) ? 
													fragmentView.findViewById(R.id.delete_one_time_transfer_button) :
					fragmentView.findViewById(R.id.delete_recurring_transfer_button));
				final NavigationRootActivity activity = 
												(NavigationRootActivity)DiscoverActivityManager.getActiveActivity();

				deleteTransfersButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(final View v) {
						if (isOneTime) {
							showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_ONE_TIME_TRANSFER,
													   R.string.bank_delete_transfer_text);
						} else {
							//In the case of a recurring transfer deletion we need to pop up 
							//a menu allowing the user to decide if the
							//deletion is specific just to the next transfer in the series 
							//or the entire series needs to be removed.
							customOptionsMenu = new CustomOptionsMenu(activity, R.layout.delete_recurring_transfers_menu);

							customOptionsMenu.addOnClickListener(R.id.delete_next_transfer_in_series_button, 
																							new OnClickListener() {

								@Override
								public void onClick(final View v) {
									showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_NEXT_TRANSFER,
															   R.string.bank_delete_transfer_text);
								}

							});

							customOptionsMenu.addOnClickListener(R.id.delete_entire_transfer_series_button, 
																							new OnClickListener() {

								@Override
								public void onClick(final View v) {
									showDeleteTransactionModal(activity, item, TransferDeletionType.DELETE_ALL_TRANSFERS,
															   R.string.bank_delete_transfer_series_text);
								}

							});

							customOptionsMenu.addOnClickListener(R.id.cancel_button, new OnClickListener() {

								@Override
								public void onClick(final View v) {
									customOptionsMenu.dismiss();
								}

							});


							customOptionsMenu.addOnClickListener(R.id.delete_recurring_transfers_relative_layout, 
																							new OnClickListener() {

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

	/**
	 * Presents a modal that will allow the user to delete the transaction that opened the modal.
	 * @param activity the current navigation activity
	 * @param item the detail item that will be deleted by this modal's action
	 * @param deleteType the type of transfer deletion.
	 */
	private void showDeleteTransactionModal(final NavigationRootActivity activity, final ActivityDetail item, 
																		final TransferDeletionType deleteType,
																		final int textBody) {
		// Create a one button modal to notify the user that they are leaving the application
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activity,
				R.string.bank_delete_transfer_title,
				textBody,
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
	 * If the ActivityDetail object contains a DELETE verb in its set of supported REST verbs, we need to allow
	 * the user to delete the currently viewed info.
	 * @param item an ActivityDetail item.
	 * @param fragmentView the view that contains the delete button to show.
	 */
	private void showDeleteButonForDetailIfNeeded(final ActivityDetail item, final View fragmentView) {
		//if contains DELETE REST verb show delete button.
		if(item.links != null) {
			final ReceivedUrl url = item.links.get(SELF);
			if(url != null) {
				//See if the set of REST verbs contains DELETE.
				final List<?> supportedActions = url.method;
				boolean canDelete = false;
				final String delete = XHttpMethodOverrideValues.DELETE.toString();
				for(final Object verb : supportedActions) {
					canDelete |= verb.toString().equalsIgnoreCase(delete);
				}
				
				//Show delete button
				if(canDelete) {
					final View button = fragmentView.findViewById(R.id.delete_button);
					if(button != null) {
						button.setVisibility(View.VISIBLE);
						button.setOnClickListener(getDeleteModalListener(item));
					}
				}
			}
		}
	}
	
	/**
	 * Returns a click listener for the delete payment button.
	 * @param item the item that will be deleted by the modal.
	 * @return an OnClickListener
	 */
	private OnClickListener getDeleteModalListener(final ActivityDetail item) {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				showDeletePaymentModal(item);
			}
		};
	}
	
	/**
	 * Presents the delete payment modal that is setup to delete the ActivityDetail item passed as
	 * the parameter.
	 * @param item the ActivityDetail item to delete on modal action.
	 */
	private void showDeletePaymentModal(final ActivityDetail item) {
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		
		if(currentActivity != null && currentActivity instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)currentActivity;
			
			final SimpleContentModal deleteModal = new SimpleContentModal(navActivity);
			deleteModal.setTitle(R.string.bank_delete_transaction_title);
			deleteModal.setContent(R.string.bank_delete_transaction_text);
			deleteModal.getButton().setText(R.string.yes_delete);
			deleteModal.getButton().setOnClickListener(getDeletePaymentListener(item));
			deleteModal.hideNeedHelpFooter();
			
			navActivity.showCustomAlert(deleteModal);
		}
	}
	
	/**
	 * Returns a click listener that will delete the provided ActivityDetail item
	 * @param item an Activity Detail item that will be deleted on modal action.
	 * @return an OnClickListener.
	 */
	private OnClickListener getDeletePaymentListener(final ActivityDetail item) {
		return new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				final PaymentDetail paymentDetail = item.toPaymentDetail();
				BankServiceCallFactory.createDeletePaymentServiceCall(paymentDetail).submit();
				DiscoverModalManager.clearActiveModal();
			}
		};
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
