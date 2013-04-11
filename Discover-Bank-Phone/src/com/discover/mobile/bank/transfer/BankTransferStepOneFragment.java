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
import com.discover.mobile.bank.services.transfer.TransferEntity;
import com.discover.mobile.bank.ui.table.AmountListItem;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.google.common.base.Strings;

public class BankTransferStepOneFragment extends BankTransferBaseFragment {
	private final String TAG = BankTransferStepOneFragment.class.getSimpleName();

	/**Bank Edit Detail frequency slot*/
	private BankEditDetail frequencyListItem;

	/**Code of the frequency*/
	private String frequencyCode = TransferDetail.ONE_TIME_TRANSFER;
	private String frequencyText = "One Time";

	private AmountValidatedEditField amountField;

	private Account toAccount = null;
	private Account fromAccount = null;

	private TextView toAccountTextView;
	private TextView fromAccountTextView;
	private TextView dateTextView;
	private BankFrequencyDetailView reoccuring;

	private final List<Account>externalAccounts = new ArrayList<Account>();

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		/**Hide controls that are not needed*/
		actionButton.setText(R.string.schedule_transfer);
		actionLink.setText(R.string.cancel_text);
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		helpFooter.show(false);
		feedbackLink.setVisibility(View.GONE);

		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		amountField.enableBankAmountTextWatcher(false);
		restoreStateFromBundle(getArguments());
		restoreStateFromBundle(savedInstanceState);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		amountField.enableBankAmountTextWatcher(true);
		updateSelectedAccountLabels();
	}

	/**
	 * Save the current state of the screen.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		reoccuring.saveState(outState);
		outState.putAll(getCurrentFragmentBundle());
	}

	private Bundle getCurrentFragmentBundle() {
		final Bundle args = getArguments();
		final Bundle outState = new Bundle();

		if(args != null) {
			outState.putAll(args);
		}

		if(amountField != null) {
			outState.putString(BankExtraKeys.AMOUNT, amountField.getText().toString());
		}

		if(frequencyCode != null) {
			outState.putString(BankExtraKeys.FREQUENCY_CODE, frequencyCode);
		}

		frequencyText = frequencyListItem.getMiddleLabel().getText().toString();
		outState.putString(BankExtraKeys.FREQUENCY_TEXT, frequencyText);

		outState.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, getSelectedAccounts());

		outState.putString("date", dateTextView.getText().toString());
		return outState;
	}

	/**
	 * Restore the state of the screen from a provided bundle.
	 * @param savedInstanceState
	 */
	private void restoreStateFromBundle(final Bundle bundle) {
		if(bundle != null) {
			final Account[] selectedAccounts = (Account[])bundle.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
			this.setSelectedAccounts(selectedAccounts);
			this.updateSelectedAccountLabels();

			if(Strings.isNullOrEmpty(frequencyCode)) {
				frequencyCode = bundle.getString(BankExtraKeys.FREQUENCY_CODE);
			}
			if(Strings.isNullOrEmpty(frequencyText)) {
				frequencyText = bundle.getString(BankExtraKeys.FREQUENCY_TEXT);
			}

			dateTextView.setText(bundle.getString("date"));

			amountField.setText(bundle.getString(BankExtraKeys.AMOUNT));

			getReocurringWidget().resumeState(bundle);
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

		frequencyListItem.getMiddleLabel().setText(frequencyText);
		frequencyListItem.getErrorLabel().setVisibility(View.GONE);

		if(frequencyCode.equals(TransferDetail.ONE_TIME_TRANSFER)){
			reoccuring.setVisibility(View.GONE);
		}else{
			reoccuring.setVisibility(View.VISIBLE);
		}
	}

	public void handleChosenAccount(final Bundle bundle) {
		final Account[] selectedAccounts = (Account[])bundle.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
		setSelectedAccounts(selectedAccounts);
		updateSelectedAccountLabels();
	}

	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final FragmentActivity currentActivity = this.getActivity();
		final int expectedSize = 5;
		final List<RelativeLayout>content = new ArrayList<RelativeLayout>(expectedSize);
		reoccuring = new BankFrequencyDetailView(currentActivity, null);

		content.add(getFromListItem(currentActivity));
		content.add(getToListItem(currentActivity));
		content.add(getAmountListItem(currentActivity));
		final BankEditDetail temp = getFrequencyListItem(currentActivity);
		temp.getMiddleLabel().setText(frequencyText);
		content.add(getFrequencyListItem(currentActivity));
		content.add(getSendOnListItem(currentActivity));
		content.add(reoccuring);

		if(frequencyCode.equals(TransferDetail.ONE_TIME_TRANSFER)){
			reoccuring.setVisibility(View.GONE);
		}else{
			reoccuring.setVisibility(View.VISIBLE);
		}

		return content;
	}

	private BankFrequencyDetailView getReocurringWidget(){
		return reoccuring;
	}

	private Account[] getSelectedAccounts() {
		final Account[] currentAccounts = new Account[2];

		if(currentAccounts.length > 1) {
			currentAccounts[0] = toAccount;
			currentAccounts[1] = fromAccount;
		}

		return currentAccounts;
	}

	private void setSelectedAccounts(final Account[] selectedAccounts) {
		if(selectedAccounts.length > 1) {
			toAccount = selectedAccounts[0];
			fromAccount = selectedAccounts[1];
		}
	}

	final OnClickListener toAccountClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			navToSelectAccountWithTitle(R.string.to);
		}

	};

	final OnClickListener fromAccountClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			navToSelectAccountWithTitle(R.string.from);
		}
	};

	private void navToSelectAccountWithTitle(final int titleResource) {
		final Bundle accounts = getCurrentFragmentBundle();
		final AccountList internalAccounts = BankUser.instance().getAccounts();

		accounts.putInt(BankExtraKeys.TITLE_TEXT, titleResource);
		accounts.putSerializable(BankExtraKeys.INTERNAL_ACCOUNTS, internalAccounts);
		accounts.putSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS, null);

		BankConductor.navigateToSelectTransferAccount(accounts);
	}

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

	private AmountListItem getAmountListItem(final Activity currentActivity) {
		final AmountListItem amountListItem = new AmountListItem(currentActivity);
		amountField = amountListItem.getEditField();
		return amountListItem;
	}

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

	@Override
	protected void onActionButtonClick() {
		final TransferDetail transferObject = new TransferDetail();

		transferObject.fromAccount = new TransferEntity();
		transferObject.toAccount = new TransferEntity();
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

		transferObject.sendDate = "2013-04-10T00:00:00Z";

		final String inputAmount = amountField.getText().toString();
		final String cents = inputAmount.replaceAll("[^0-9]", "");

		if(!Strings.isNullOrEmpty(cents)) {
			transferObject.amount.value = Integer.parseInt(cents);
		} else {
			transferObject.amount.value = 0;
		}

		BankServiceCallFactory.createScheduleTransferCall(transferObject).submit();
	}

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

	@Override
	public void onBackPressed() {
		showCancelModal();
	}

	private boolean useMyBackPress = true;
	@Override
	public boolean isBackPressDisabled() {
		return useMyBackPress;
	}

}
