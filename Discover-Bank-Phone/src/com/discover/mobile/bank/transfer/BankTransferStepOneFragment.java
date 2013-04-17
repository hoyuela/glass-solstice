package com.discover.mobile.bank.transfer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.payees.BankEditDetail;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.table.AmountListItem;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.google.common.base.Strings;

/**
 * This is the first step in the transfer money process.
 * It presents the user with information about which accounts they are transferring
 * between, the amount of the transfer and the date the transfer will occur.
 * 
 * @author scottseward
 *
 */
public class BankTransferStepOneFragment extends BankTransferBaseFragment {

	/**Bank Edit Detail frequency slot*/
	private BankEditDetail frequencyListItem;
	private final List<BankEditDetail> transferListItems = new ArrayList<BankEditDetail>();

	/**Code of the frequency*/
	private String frequencyCode = TransferDetail.ONE_TIME_TRANSFER;
	private String frequencyText = "One Time";
	
	private final String date = "date";

	private AmountValidatedEditField amountField;

	private Account toAccount = null;
	private Account fromAccount = null;

	private TextView toAccountTextView;
	private TextView fromAccountTextView;
	private TextView dateTextView;

	/**Recurring frequency view*/
	private BankFrequencyDetailView recurring;
	
	/** This boolean is used for the back button to determine which onBackPressed method should be used */
	private boolean useMyBackPress = true;
	
	/** The downloaded external accounts */
	private AccountList externalAccounts = new AccountList();

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		actionButton.setText(R.string.schedule_transfer);
		actionLink.setText(R.string.cancel_text);
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);

		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		final Bundle args = getArguments();
		
		if(args != null)
			externalAccounts = (AccountList)args.getSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS);
		
		restoreStateFromBundle(args);
		
		return view;
	}

	/**
	 * Finalize setup of the Fragment. Enable the amount field text watcher, so that it will format any restored input.
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		updateSelectedAccountLabels();
		frequencyListItem.setText(frequencyText);
		frequencyListItem.getErrorLabel().setVisibility(View.GONE);
		recurring.resumeState(getArguments());
		hideAllListErrorLabels();
		
		amountField.enableBankAmountTextWatcher(false);
		amountField.setText(getArguments().getString(BankExtraKeys.AMOUNT));
		amountField.enableBankAmountTextWatcher(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		amountField.enableBankAmountTextWatcher(false);
	}

	/**
	 * Save the current state of the screen.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		getCurrentFragmentBundle();
		
		if( recurring != null ) {
			recurring.saveState(getArguments());
		}
	}

	/**
	 * 
	 * @return a Bundle with all of the information that is currently presented or related to the state of
	 * 			this screen.
	 */
	private Bundle getCurrentFragmentBundle() {
		Bundle args = getArguments();

		if(args == null) {
			args = new Bundle();
		}

		if(amountField != null) {
			args.putString(BankExtraKeys.AMOUNT, amountField.getText().toString());
		}

		if(frequencyCode != null) {
			args.putString(BankExtraKeys.FREQUENCY_CODE, frequencyCode);
		}

		if(frequencyText != null && frequencyListItem != null ) {
			frequencyText = frequencyListItem.getMiddleLabel().getText().toString();
		
			args.putString(BankExtraKeys.FREQUENCY_TEXT, frequencyText);
		}

		if( toAccount != null && fromAccount != null) {
			args.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, getSelectedAccounts());
		}

		if( dateTextView != null )
			args.putString(date, dateTextView.getText().toString());
		
		return args;
	}

	/**
	 * Restore the state of the screen from a provided bundle.
	 * @param savedInstanceState
	 */
	private void restoreStateFromBundle(final Bundle bundle) {
		if(bundle != null) {
			final AccountList bundleExternalAccounts = 
					(AccountList)bundle.getSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS);
			if(bundleExternalAccounts != null)
				externalAccounts = bundleExternalAccounts;
			
			Account[] selectedAccounts = (Account[])bundle.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
			if(selectedAccounts == null)
				selectedAccounts = new Account[2];
			
			this.setSelectedAccounts(selectedAccounts);
			this.updateSelectedAccountLabels();

			final String freq = bundle.getString(BankExtraKeys.FREQUENCY_CODE);
			final String value = bundle.getString(BankExtraKeys.FREQUENCY_TEXT);

			if(!Strings.isNullOrEmpty(freq)) {
				frequencyCode = freq;
			}
			if(!Strings.isNullOrEmpty(value)) {
				frequencyText = value;
			}
			
			if(frequencyListItem != null)
				frequencyListItem.setText(frequencyText);
			
			if(dateTextView != null)
				dateTextView.setText(bundle.getString(date));



			if(frequencyCode.equals(TransferDetail.ONE_TIME_TRANSFER)){
				recurring.setVisibility(View.GONE);
			}else{
				recurring.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * Update the text labels on the screen for the selected accounts.
	 */
	private void updateSelectedAccountLabels() {
		if(toAccount != null) {
			toAccountTextView.setText(toAccount.nickname);
		}
		if(fromAccount != null) {
			fromAccountTextView.setText(fromAccount.nickname);
		}
	}



	/**
	 * Handle the chosen frequency from the frequency widget
	 * @param bundle - bundle of data
	 */
	public void handleChosenFrequency(final Bundle bundle){
		frequencyCode = bundle.getString(BankExtraKeys.FREQUENCY_CODE);
		frequencyText = bundle.getString(BankExtraKeys.FREQUENCY_TEXT);

		frequencyListItem.setText(frequencyText);
		frequencyListItem.getErrorLabel().setVisibility(View.GONE);

		if(frequencyCode.equals(TransferDetail.ONE_TIME_TRANSFER)){
			recurring.setVisibility(View.GONE);
		}else{
			recurring.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * This is used when the user has selected a new account or accounts from the select
	 * account widget. It loads the selected accounts into a local array of Account objects.
	 * @param bundle a Bundle which contains an Account array of selected Account objects.
	 */
	public void handleChosenAccount(final Bundle bundle) {
		restoreStateFromBundle(bundle);
		final Account[] selectedAccounts = (Account[])bundle.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
		setSelectedAccounts(selectedAccounts);
		updateSelectedAccountLabels();
	}

	/**
	 * Set the progress step to the first position.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}
	
	private void hideAllListErrorLabels() {
		for(final BankEditDetail item : transferListItems)
			item.getErrorLabel().setVisibility(View.GONE);
	}

	/**
	 * Returns the information that will be presented in the table on screen.
	 * This includes items such as the to and from account items, the amount cell, the date cell,
	 * and more.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final FragmentActivity currentActivity = this.getActivity();
		final int expectedMaxSize = 8;
		final List<RelativeLayout>content = new ArrayList<RelativeLayout>(expectedMaxSize);
		recurring = new BankFrequencyDetailView(currentActivity, null);

		content.add(getFromListItem(currentActivity));
		content.add(getToListItem(currentActivity));
		content.add(getAmountListItem(currentActivity));
		content.add(getFrequencyListItem(currentActivity));
		content.add(getSendOnListItem(currentActivity));
		content.add(recurring);

		for(final RelativeLayout item : content) {
			if(item instanceof BankEditDetail)
				transferListItems.add((BankEditDetail)item);
		}

		if(frequencyCode.equals(TransferDetail.ONE_TIME_TRANSFER)){
			recurring.setVisibility(View.GONE);
		}else{
			recurring.setVisibility(View.VISIBLE);
		}

		return content;
	}


	/**
	 *
	 * @return an array of Account objects which contain the currently selected Accounts.
	 */
	private Account[] getSelectedAccounts() {
		final Account[] currentAccounts = new Account[2];

		if(currentAccounts.length > 1) {
			currentAccounts[0] = toAccount;
			currentAccounts[1] = fromAccount;
		}

		return currentAccounts;
	}

	/**
	 * Sets the local selected Account objects to that of the provided array of Account objects.
	 * @param selectedAccounts an Account array of size 2.
	 */
	private void setSelectedAccounts(final Account[] selectedAccounts) {
		if(selectedAccounts.length > 1) {
			toAccount = selectedAccounts[0];
			fromAccount = selectedAccounts[1];
		}
	}

	/**
	 * Used for the to account table cell, when it is clicked, navigate to the select
	 * to account screen.
	 */
	final OnClickListener toAccountClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			navToSelectAccountWithTitle(R.string.to);
		}

	};

	/**
	 * Used for the from account table cell, when it is clicked, navigate to the select
	 * from account screen.
	 */
	final OnClickListener fromAccountClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			navToSelectAccountWithTitle(R.string.from);
		}
	};

	/**
	 * Sets up the navigate to select to/from account screen.
	 * @param titleResource the kind of screen we are going to (to/from)
	 */
	private void navToSelectAccountWithTitle(final int titleResource) {
		final Bundle accounts = getCurrentFragmentBundle();
		final AccountList internalAccounts = BankUser.instance().getAccounts();

		accounts.putInt(BankExtraKeys.TITLE_TEXT, titleResource);
		accounts.putSerializable(BankExtraKeys.INTERNAL_ACCOUNTS, internalAccounts);
		accounts.putSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS, externalAccounts);

		BankConductor.navigateToSelectTransferAccount(accounts);
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankEditDetail object which will be inserted into the content table on screen.
	 */
	private BankEditDetail getFromListItem(final Activity currentActivity) {
		final BankEditDetail fromListItem = new BankEditDetail(currentActivity);

		fromListItem.getDividerLine().setVisibility(View.INVISIBLE);
		fromListItem.getEditableField().setVisibility(View.GONE);
		fromListItem.getTopLabel().setText(R.string.from);
		fromListItem.getMiddleLabel().setText(R.string.select_account);
		fromListItem.getView().setOnFocusChangeListener(null);
		fromListItem.getErrorLabel().setVisibility(View.GONE);
		fromListItem.getView().setOnClickListener(fromAccountClickListener);
		fromAccountTextView = fromListItem.getMiddleLabel();

		return fromListItem;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankEditDetail object which will be inserted into the content table on screen.
	 */
	private BankEditDetail getToListItem(final Activity currentActivity) {
		final BankEditDetail toListItem = new BankEditDetail(currentActivity);
		toListItem.getEditableField().setVisibility(View.GONE);
		toListItem.getMiddleLabel().setText(R.string.select_account);
		toListItem.getView().setOnFocusChangeListener(null);
		toListItem.getErrorLabel().setVisibility(View.GONE);

		toListItem.getDividerLine().setVisibility(View.VISIBLE);
		toListItem.getTopLabel().setText(R.string.to);
		toListItem.getView().setOnClickListener(toAccountClickListener);
		toAccountTextView = toListItem.getMiddleLabel();

		return toListItem;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankEditDetail object which will be inserted into the content table on screen.
	 */
	private AmountListItem getAmountListItem(final Activity currentActivity) {
		final AmountListItem amountListItem = new AmountListItem(currentActivity);
		amountField = amountListItem.getEditField();
		return amountListItem;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankEditDetail object which will be inserted into the content table on screen.
	 */
	private BankEditDetail getFrequencyListItem(final Activity currentActivity) {
		frequencyListItem = new BankEditDetail(currentActivity);
		frequencyListItem.getTopLabel().setText(R.string.frequency);
		frequencyListItem.getMiddleLabel().setText(R.string.one_time);
		frequencyListItem.getView().setOnFocusChangeListener(null);
		frequencyListItem.getView().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View view){
				BankConductor.navigateToFrequencyWidget(getCurrentFragmentBundle());
			}
		});
		frequencyListItem.getEditableField().setVisibility(View.GONE);
		frequencyListItem.getErrorLabel().setVisibility(View.GONE);

		return frequencyListItem;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankEditDetail object which will be inserted into the content table on screen.
	 */
	private BankEditDetail getSendOnListItem(final Activity currentActivity) {
		final BankEditDetail sendOnListItem = new BankEditDetail(currentActivity);

		sendOnListItem.getTopLabel().setText(R.string.send_on);
		sendOnListItem.getMiddleLabel().setText(R.string.select_a_date);
		sendOnListItem.getView().setOnClickListener(null);
		sendOnListItem.getView().setOnFocusChangeListener(null);
		sendOnListItem.getEditableField().setVisibility(View.GONE);
		sendOnListItem.getErrorLabel().setVisibility(View.GONE);
		dateTextView = sendOnListItem.getMiddleLabel();
		return sendOnListItem;
	}

	@Override
	protected void onActionLinkClick() {
		showCancelModal();
	}

	/**
	 * Submit the current information on the page to schedule a transfer.
	 */
	@Override
	protected void onActionButtonClick() {
		final TransferDetail transferObject = new TransferDetail();

		transferObject.fromAccount = new Account();
		transferObject.toAccount = new Account();
		transferObject.amount = new Money();

		if(fromAccount != null) {
			transferObject.fromAccount.id = fromAccount.id;
		}
		if(toAccount != null) {
			transferObject.toAccount.id = toAccount.id;
		}
		if(!Strings.isNullOrEmpty(frequencyCode)) {
			transferObject.frequency = frequencyCode;
		}

		transferObject.sendDate = "2013-04-19T00:00:00Z";

		final String inputAmount = amountField.getText().toString();
		final String cents = inputAmount.replaceAll("[^0-9]", "");

		if(!Strings.isNullOrEmpty(cents)) {
			transferObject.amount.value = Integer.parseInt(cents);
		} else {
			transferObject.amount.value = 0;
		}

		if(recurring.getVisibility() == View.VISIBLE){
			transferObject.durationType = recurring.getDurationType();
			transferObject.durationValue = recurring.getDurationValue();
		}

		BankServiceCallFactory.createScheduleTransferCall(transferObject).submit();
	}

	/**
	 * Shows a modal dialog on the page to notify the user that if they cancel their current action
	 * all information will be lost.
	 */
	private void showCancelModal() {
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(this.getActivity(), null);

		bottom.setButtonText(R.string.cancel_this_action);
		bottom.getButton().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				useMyBackPress = false;
				((BankNavigationRootActivity)getActivity()).onBackPressed();
			}

		});
		final ModalDefaultTopView top = new ModalDefaultTopView(this.getActivity(), null);
		top.hideNeedHelpFooter();
		top.setTitle(R.string.cancel_this_action);
		top.setContent(R.string.cancel_this_action_content);
		final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(this.getActivity(), top, bottom);

		this.showCustomAlertDialog(cancelModal);
	}

	/**
	 * If the back button is pressed, we need to show a modal to alert the user that
	 * going back will delete any entered information.
	 */
	@Override
	public void onBackPressed() {
		showCancelModal();
	}

	@Override
	public boolean isBackPressDisabled() {
		return useMyBackPress;
	}

}
