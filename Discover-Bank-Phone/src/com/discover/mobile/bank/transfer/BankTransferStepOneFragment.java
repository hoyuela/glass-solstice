package com.discover.mobile.bank.transfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.payees.BankSimpleEditDetail;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.table.AdjustedAmountListItem;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.widgets.CalendarFragment;
import com.discover.mobile.common.ui.widgets.CalendarListener;
import com.google.common.base.Strings;

/**
 * This is the first step in the transfer money process.
 * It presents the user with information about which accounts they are transferring
 * between, the amount of the transfer and the date the transfer will occur.
 * 
 * @author scottseward
 *
 */
public class BankTransferStepOneFragment extends BankTransferBaseFragment implements BankErrorHandlerDelegate {
	private static final String TAG = BankTransferStepOneFragment.class.getSimpleName();
	
	/**Code of the frequency*/
	private String frequencyCode = TransferDetail.ONE_TIME_TRANSFER;
	private String frequencyText = "One Time";
	
	private static final String DATE = "date";
	private static final String NON_NUMBER_CHARACTERS = "[^0-9]";
	private static final String ERROR_OBJECT = "err";
	/**
	 * Static field used to determine maximum value allowed for a transfer
	 */
	private static final double MAXIMUM_TRANSFER_VALUE = 999999.99;

	private AmountValidatedEditField amountField;
	
	private BankErrorResponse lastErrorObject = null;
	
	private Account toAccount = null;
	private Account fromAccount = null;

	private TextView toAccountTextView;
	private TextView fromAccountTextView;
	private TextView dateTextView;
	
	private CalendarFragment calendarFragment = null;
	
	/** Chosen payment date */
	private Calendar chosenPaymentDate = null;
	
	/** Earliest payment date */
	private Calendar earliestPaymentDate = null;
	
	/**Recurring frequency view*/
	private BankFrequencyDetailView recurring;
	
	/** This boolean is used for the back button to determine which onBackPressed method should be used */
	private boolean useMyBackPress = true;
	
	/** The downloaded external accounts */
	private AccountList externalAccounts = new AccountList();
	
	private BankSimpleEditDetail sendOnDateCell = null;
	private BankSimpleEditDetail frequencyCell = null;
	private BankSimpleEditDetail fromCell = null;
	private BankSimpleEditDetail toCell = null;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		setButtonText(R.string.schedule_transfer);
		setLinkText(R.string.cancel_text);
		hideBottomNote();

		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		final Bundle args = getArguments();
		
		chosenPaymentDate = Calendar.getInstance();
		earliestPaymentDate = CalendarFragment.getFirstValidDateCalendar(Calendar.getInstance(), 
				BankUser.instance().getHolidays());
		
		if(args != null) {
			externalAccounts = (AccountList)args.getSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS);
			lastErrorObject = (BankErrorResponse)args.getSerializable(ERROR_OBJECT);		
		}
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
		frequencyCell.setText(frequencyText);
		frequencyCell.getErrorLabel().setVisibility(View.GONE);
		recurring.resumeState(getArguments());
		
		restoreStateFromBundle(getArguments());

		updateDateField();
		
		if(lastErrorObject != null){
			handleError(lastErrorObject);
		}
		
		updateDateSelector();
	}
	
	/**
	 * Updates the selectability of the date picker.
	 */
	private void updateDateSelector() {
		final boolean canSelectDate = hasBothAccountsSelected() && !areBothAccountsInternal() && isAmountNotZero();
		
		if(canSelectDate) {
			enableDateSelection();
		} else {
			disableDateSelection();
		}
	}
	
	/**
	 * 
	 * @return if the current amount is greater than zero.
	 */
	private boolean isAmountNotZero() {
		final String amount = amountField.getText().toString();
		boolean isAmountNotZero = false;
		if(!Strings.isNullOrEmpty(amount)) {
			final String amountWithoutCommas = amount.replaceAll(",", "");
			isAmountNotZero =  Double.parseDouble(amountWithoutCommas) > 0.0d;
		}
		return isAmountNotZero;
	}
	
	/**
	 * Sets the send on date field to either the saved date from the bundle, or if there is no
	 * saved date in the bundle, it will be set to the first possible valid date.
	 */
	private void updateDateField() {
		final Bundle args = getArguments();
		if(args != null) {
			final String savedDate = args.getString(DATE);
			final boolean isRecurringTransfer = !TransferDetail.ONE_TIME_TRANSFER.equalsIgnoreCase(frequencyCode);
			
			final boolean accountsAreInternal = areBothAccountsInternal();
			
			
			if(isRecurringTransfer && !accountsAreInternal) {
				setDateFieldToFirstValidDate(1);
			} else if(accountsAreInternal) {
				setDateFieldToFirstValidDate(0);
				disableDateSelection();
				
				setFrequencyToOneTime();
				disableFrequencySelection();
			} else if(!Strings.isNullOrEmpty(savedDate)) {
				dateTextView.setText(savedDate);				
			}else {
				setDateFieldToFirstValidDate(0);
			}
		}
	}
	
	/**
	 * 
	 * @return if both of the accounts are selected and they are internal accounts.
	 */
	private boolean areBothAccountsInternal() {
		boolean areBothAccountsInternal = false;
		
		if(toAccount != null && fromAccount != null) {
			areBothAccountsInternal = !toAccount.isExternalAccount() && !fromAccount.isExternalAccount();
		}
		
		return areBothAccountsInternal;
	}
	
	/**
	 * Sets the frequency selection to 'One Time' along with the frequencyCode.
	 */
	private void setFrequencyToOneTime() {
		final Bundle args = getArguments();
		if(args != null) {
			args.putString(BankExtraKeys.FREQUENCY_CODE, TransferDetail.ONE_TIME_TRANSFER);
			args.putString(BankExtraKeys.FREQUENCY_TEXT, getString(R.string.one_time));
		}
		
		final Bundle bundle = new Bundle();
		bundle.putString(BankExtraKeys.FREQUENCY_CODE, TransferDetail.ONE_TIME_TRANSFER);
		bundle.putString(BankExtraKeys.FREQUENCY_TEXT, getString(R.string.one_time));
		
		handleChosenFrequency(bundle);
	}
	
	/**
	 * Prevent the frequency cell from being tapped.
	 */
	private void disableFrequencySelection() {
		frequencyCell.getMiddleLabel().setTextColor(getResources().getColor(R.color.field_copy));
		frequencyCell.getCaret().setVisibility(View.INVISIBLE);
		frequencyCell.getView().setOnClickListener(null);
	}
	
	/**
	 * Prevent the send on date from being tapped.
	 */
	private void disableDateSelection() {
		if(sendOnDateCell != null) {
			sendOnDateCell.getMiddleLabel().setTextColor(getResources().getColor(R.color.field_copy));
			sendOnDateCell.getCaret().setVisibility(View.INVISIBLE);
			sendOnDateCell.getView().setOnClickListener(null);
		}
	}
	
	/**
	 * Allow the send on date cell to be tapped.
	 */
	private void enableDateSelection() {
		if(sendOnDateCell != null) {
			sendOnDateCell.getMiddleLabel().setTextColor(getResources().getColor(R.color.body_copy));
			sendOnDateCell.getCaret().setVisibility(View.VISIBLE);
			sendOnDateCell.getView().setOnClickListener(openCalendarOnClick);
		}
	}
	
	/**
	 * Sets the dateTextView text to display the first valid date based on the current date.
	 */
	private void setDateFieldToFirstValidDate(final int offset) {
		final Calendar tempCal = Calendar.getInstance();
		tempCal.add(Calendar.DAY_OF_MONTH, offset);
		
		//Set the date to the first valid date for a transfer.
		earliestPaymentDate = CalendarFragment.getFirstValidDateCalendar(tempCal, BankUser.instance().getHolidays());
		
		//Set the dateTextView's text to the new valid date.
		if(dateTextView != null) {
			dateTextView.setText(BankStringFormatter.formatDate(String.valueOf(earliestPaymentDate.get(Calendar.YEAR)),
					String.valueOf(earliestPaymentDate.get(Calendar.MONTH) + 1),
							String.valueOf(earliestPaymentDate.get(Calendar.DAY_OF_MONTH))));
		}
		
		final Bundle args = getArguments();
		if(args != null && dateTextView != null && !Strings.isNullOrEmpty(dateTextView.getText().toString())) {
			args.putString(DATE, dateTextView.getText().toString());
		}
	}

	/**
	 * Save the current state of the screen.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		getAndSaveFragmentStateToArgumentBundle();
		
		if( recurring != null ) {
			recurring.saveState(getArguments());
		}
	}

	/**
	 * 
	 * @return a Bundle with all of the information that is currently presented or related to the state of
	 * 			this screen.
	 */
	private Bundle getAndSaveFragmentStateToArgumentBundle() {
		Bundle args = getArguments();

		if(args == null) {
			args = new Bundle();
		}

		if(amountField != null) {
			args.putString(BankExtraKeys.AMOUNT, amountField.getText().toString());
		}

		args.putString(BankExtraKeys.FREQUENCY_CODE, frequencyCode);
		
		if(frequencyCell != null ) {
			frequencyText = frequencyCell.getMiddleLabel().getText().toString();
		}
		
		args.putString(BankExtraKeys.FREQUENCY_TEXT, frequencyText);

		if(toAccount != null && fromAccount != null) {
			args.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, getSelectedAccounts());
		}

		if(dateTextView != null) {
			args.putString(DATE, dateTextView.getText().toString());
		}
		
		if(lastErrorObject != null) {
			try {
				args.putSerializable(ERROR_OBJECT, (BankErrorResponse)lastErrorObject.clone());
			} catch (final CloneNotSupportedException e) {
				Log.e(TAG, "Could not clone object : " + e);
			}
		}
		
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
			if(bundleExternalAccounts != null){
				externalAccounts = bundleExternalAccounts;
			}
			
			Account[] selectedAccounts = (Account[])bundle.getSerializable(BankExtraKeys.DATA_SELECTED_INDEX);
			if(selectedAccounts == null){
				selectedAccounts = new Account[2];
			}
			
			this.setSelectedAccounts(selectedAccounts);
			this.updateSelectedAccountLabels();

			restoreFrequencyText(bundle);
			restoreFrequencyTable(bundle);
			
			if(dateTextView != null){
				dateTextView.setText(bundle.getString(DATE));
			}
		
			amountField.enableBankAmountTextWatcher(false);
			amountField.setText(bundle.getString(BankExtraKeys.AMOUNT));
			amountField.enableBankAmountTextWatcher(true);
		}
		
		resetCalendarEventListener();
	}
	
	/**
	 * Access a Bundle and retrieve the saved frequency text and restore the local variables to its value and update
	 * the UI with this value.
	 * @param bundle a Bundle which contains the value saved from the frequencyCell.
	 */
	private void restoreFrequencyText(final Bundle bundle) {
		final String value = bundle.getString(BankExtraKeys.FREQUENCY_TEXT);
		
		if(!Strings.isNullOrEmpty(value)) {
			frequencyText = value;
		}
		
		if(frequencyCell != null){
			frequencyCell.setText(frequencyText);
		}
	}
	
	/**
	 * Access a Bundle and restore a saved frequency value.  
	 * @param bundle
	 */
	private void restoreFrequencyTable(final Bundle bundle) {
		final String freq = bundle.getString(BankExtraKeys.FREQUENCY_CODE);
		
		if(!Strings.isNullOrEmpty(freq)) {
			frequencyCode = freq;
		}
		
		if(frequencyCode.equalsIgnoreCase(TransferDetail.ONE_TIME_TRANSFER)){
			recurring.setVisibility(View.GONE);
		}else{
			recurring.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Reset Calendar Event Listener
	 */
	private void resetCalendarEventListener() {
	    final Fragment fragment = getFragmentManager().findFragmentByTag(CalendarFragment.TAG);
	    if( fragment != null && fragment instanceof CalendarFragment) {
	    	calendarFragment = (CalendarFragment) fragment;
	    	calendarFragment.setCalendarListener(createCalendarListener());
	    }
	}
	
	/**
	 * Update the text labels on the screen for the selected accounts.
	 */
	private void updateSelectedAccountLabels() {
		if(toAccount != null && toCell != null) {
			toCell.getTopLabel().setText(getEndingInText(true, toAccount));
			toAccountTextView.setText(toAccount.nickname);
		}
		if(fromAccount != null) {
			fromCell.getTopLabel().setText(getEndingInText(false, fromAccount));
			fromAccountTextView.setText(fromAccount.nickname);
		}
	}

	/**
	 * 
	 * @param cell a BankSimpleEditDetail cell that has a top label which should display an Account Ending in XXXX
	 * @param account an account which has a last 4 digits of an account.
	 * @return a String in the form of {previous title text} Account Ending in {last 4 digits}
	 */
	private String getEndingInText (final boolean isToAccount, final Account account) {
		final StringBuilder builder = new StringBuilder();
		String prefix = "From";
		if(isToAccount){
			prefix = "To";
		}
		
		builder.append(prefix);
		if(account != null && account.accountNumber != null && !Strings.isNullOrEmpty(account.accountNumber.ending)) {
			builder.append(" ");
			builder.append(BankStringFormatter.getAccountEndingInString(account.accountNumber.ending));
		}
		return builder.toString();
	}
	
	/**
	 * Handle the chosen frequency from the frequency widget
	 * @param bundle - bundle of data
	 */
	public void handleChosenFrequency(final Bundle bundle){
		frequencyCode = bundle.getString(BankExtraKeys.FREQUENCY_CODE);
		frequencyText = bundle.getString(BankExtraKeys.FREQUENCY_TEXT);

		frequencyCell.setText(frequencyText);
		frequencyCell.getErrorLabel().setVisibility(View.GONE);

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
		updateDateSelector();
	}

	/**
	 * Set the progress step to the first position.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
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
	 * Used for the to account table cell, when it is tapped, navigate to the select
	 * to account screen.
	 */
	private final OnClickListener toAccountClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			navToSelectAccountWithTitle(R.string.to);
		}

	};

	/**
	 * Used for the from account table cell, when it is tapped, navigate to the select
	 * from account screen.
	 */
	private final OnClickListener fromAccountClickListener = new OnClickListener() {

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
		final Bundle accounts = getAndSaveFragmentStateToArgumentBundle();
		final AccountList internalAccounts = BankUser.instance().getAccounts();

		accounts.putInt(BankExtraKeys.TITLE_TEXT, titleResource);
		accounts.putSerializable(BankExtraKeys.INTERNAL_ACCOUNTS, internalAccounts);
		accounts.putSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS, externalAccounts);

		BankConductor.navigateToSelectTransferAccount(accounts);
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankSimpleEditDetail object which will be inserted into the content table on screen.
	 */
	private BankSimpleEditDetail getFromListItem(final Activity currentActivity) {
		fromCell = new BankSimpleEditDetail(currentActivity);

		fromCell.getDividerLine().setVisibility(View.INVISIBLE);
		fromCell.getTopLabel().setText(R.string.from);
		fromCell.getMiddleLabel().setText(R.string.select_account);
		fromCell.getMiddleLabel().setSingleLine(false);
		fromCell.getMiddleLabel().setMaxLines(2);
		fromCell.getView().setOnClickListener(fromAccountClickListener);
		fromCell.getTopLabel().setTextAppearance(getActivity(), R.style.field_copy_medium);
		fromCell.getMiddleLabel().setTextAppearance(getActivity(), R.style.body_copy_title);
		fromAccountTextView = fromCell.getMiddleLabel();
		
		
		return fromCell;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankSimpleEditDetail object which will be inserted into the content table on screen.
	 */
	private BankSimpleEditDetail getToListItem(final Activity currentActivity) {
		toCell = new BankSimpleEditDetail(currentActivity);
		toCell.getMiddleLabel().setText(R.string.select_account);
		toCell.getMiddleLabel().setSingleLine(false);
		toCell.getMiddleLabel().setMaxLines(2);
		toCell.getDividerLine().setVisibility(View.VISIBLE);
		toCell.getTopLabel().setText(R.string.to);
		toCell.getView().setOnClickListener(toAccountClickListener);
		toCell.getTopLabel().setTextAppearance(getActivity(), R.style.field_copy_medium);
		toCell.getMiddleLabel().setTextAppearance(getActivity(), R.style.body_copy_title);
		toAccountTextView = toCell.getMiddleLabel();
		
		return toCell;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankSimpleEditDetail object which will be inserted into the content table on screen.
	 */
	private AdjustedAmountListItem getAmountListItem(final Activity currentActivity) {
		final AdjustedAmountListItem amountListItem = new AdjustedAmountListItem(currentActivity);
		amountField = amountListItem.getEditField();
		amountField.attachErrorLabel(amountListItem.getErrorLabel());
		amountListItem.getErrorLabel().setText(R.string.amount_less_than_twenty_five);
		final TextView amountLabel = (TextView)amountListItem.findViewById(R.id.amount_title);
		amountLabel.setTextAppearance(getActivity(), R.style.field_copy_medium);
		
		/**Set the maximum amount of digits allowed to be entered into the field*/
		amountField.setMaximumValue(MAXIMUM_TRANSFER_VALUE);
		
		amountField.addTextChangedListener(dateSelectorWatcher);
		amountField.clearErrors();
		return amountListItem;
	}
	
	private final TextWatcher dateSelectorWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(final Editable s) {
			updateDateSelector();
		}
		@Override
		public void beforeTextChanged(final CharSequence s, final int start, final int count,
				final int after) {
		}

		@Override
		public void onTextChanged(final CharSequence s, final int start, final int before,
				final int count) {
		}
		
	};

	/**
	 * 
	 * @param currentActivity
	 * @return a BankSimpleEditDetail object which will be inserted into the content table on screen.
	 */
	private BankSimpleEditDetail getFrequencyListItem(final Activity currentActivity) {
		frequencyCell = new BankSimpleEditDetail(currentActivity);
		frequencyCell.getTopLabel().setText(R.string.frequency);
		frequencyCell.getMiddleLabel().setText(R.string.one_time);
		frequencyCell.getTopLabel().setTextAppearance(getActivity(), R.style.field_copy_medium);
		frequencyCell.getMiddleLabel().setTextAppearance(getActivity(), R.style.body_copy_title);

		frequencyCell.getView().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View view){
				BankConductor.navigateToFrequencyWidget(getAndSaveFragmentStateToArgumentBundle());
			}
		});

		
		return frequencyCell;
	}

	/**
	 * 
	 * @param currentActivity
	 * @return a BankSimpleEditDetail object which will be inserted into the content table on screen.
	 */
	private BankSimpleEditDetail getSendOnListItem(final Activity currentActivity) {
	    sendOnDateCell = new BankSimpleEditDetail(currentActivity);

		sendOnDateCell.getTopLabel().setText(R.string.send_on);
		sendOnDateCell.getMiddleLabel().setText(R.string.select_a_date);
		sendOnDateCell.getTopLabel().setTextAppearance(getActivity(), R.style.field_copy_medium);
		sendOnDateCell.getMiddleLabel().setTextAppearance(getActivity(), R.style.body_copy_title);
		
		sendOnDateCell.getView().setOnClickListener(openCalendarOnClick);
		dateTextView = sendOnDateCell.getMiddleLabel();
		
		return sendOnDateCell;
	}

	@Override
	protected void onActionLinkClick() {
		showCancelModal(true);
	}
	
	private final OnClickListener openCalendarOnClick = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			showCalendar();
		}
	};

	/**
	 * Submit the current information on the page to schedule a transfer.
	 */
	@Override
	protected void onActionButtonClick() {		
		if(isFormInfoComplete()) {
			final TransferDetail transferObject = new TransferDetail();
	
			transferObject.fromAccount = fromAccount;
			transferObject.toAccount = toAccount;
			transferObject.amount = new Money();
	
			if(!Strings.isNullOrEmpty(frequencyCode)) {
				transferObject.frequency = frequencyCode;
			}

			transferObject.sendDate = BankStringFormatter.convertToISO8601Date(dateTextView.getText().toString(),false);
	
			final String cents = amountField.getText().toString().replaceAll(NON_NUMBER_CHARACTERS, "");
			if(!Strings.isNullOrEmpty(cents)) {
				transferObject.amount.value = Integer.parseInt(cents);
			}
			
			if(recurring.getVisibility() == View.VISIBLE){
				transferObject.durationType = recurring.getDurationType();
				transferObject.durationValue = recurring.getDurationValue();
			}
	
			BankServiceCallFactory.createScheduleTransferCall(transferObject).submit();
		}else {
			getScrollView().smoothScrollTo(0, 0);
		}
	}
	
	private boolean isFormInfoComplete() {
		boolean isComplete = true;
		
		isComplete &= validateAccountsAreSelected();
		isComplete &= validateAmount();
		isComplete &= validateContinueUntilDate();
		isComplete &= validateContinueUntilSetAmount();
		isComplete &= validateUntilTransfersReached();
		
		return isComplete;
	}
	
	private boolean validateUntilTransfersReached() {
		boolean hasEnoughTransfers = true;
		
		if(TransferDetail.UNTIL_COUNT.equalsIgnoreCase(recurring.getDurationType())) {
			hasEnoughTransfers = !Strings.isNullOrEmpty(recurring.getDurationValue()) && 1 < Integer.parseInt(recurring.getDurationValue());
			if(!hasEnoughTransfers) {
				showErrorLabel(getString(R.string.too_few_transfers), 
										(TextView)recurring.findViewById(R.id.transactions_error_label));
			}
		}
		
		return hasEnoughTransfers;
	}
	
	/**
	 * Update the UI based on if the user has a selected a valid 'continue until' date.
	 * @return if the user has selected a valid continue until date.
	 */
	private boolean validateContinueUntilDate() {
		boolean hasGoodDate = false;
		
		if(TransferDetail.UNTIL_DATE.equalsIgnoreCase(recurring.getDurationType())) {
			final String untilDate = recurring.getDurationValue();
			hasGoodDate = !Strings.isNullOrEmpty(untilDate) && 
					!getString(R.string.select_a_date).equalsIgnoreCase(untilDate);
			
			if(!hasGoodDate) {
				showErrorLabel(getString(R.string.invalid_date), 
										(TextView)recurring.findViewById(R.id.date_error_label));
			}
		}else {
			//User is not using this widget.
			hasGoodDate = true;
		}
		
		return hasGoodDate;
	}
	
	/**
	 * Validate and update the UI based on the continue until set amount input field. 
	 * @return if the amount in the field is a valid amount.
	 */
	private boolean validateContinueUntilSetAmount() {
		boolean isValid = false;
		
		if(TransferDetail.UNTIL_AMOUNT.equalsIgnoreCase(recurring.getDurationType())) {
			isValid = isAmountTwentyFiveDollarsOrMore(recurring.getDurationValue());
			if(!isValid) {
				final TextView recurringAmountErrorLabel = (TextView)recurring.findViewById(R.id.dollar_error_label);
				showErrorLabel(getString(R.string.amount_less_than_twenty_five), recurringAmountErrorLabel);
			}
		}else {
			isValid = true;
		}
		
		
		
		return isValid;
	}
	
	/**
	 * 
	 * @return if both the from and to account have been selected.
	 */
	private boolean hasBothAccountsSelected() {
		return toAccount != null && fromAccount != null;
	}
	
	/**
	 * Validates and updates the UI based on if both the to and from account are selected.
	 * @return if both the from and to account have been selected.
	 */
	private boolean validateAccountsAreSelected() {
		final boolean accountsAreSelected = hasBothAccountsSelected();
		
		if(!accountsAreSelected) {
			showErrorLabel(getString(R.string.select_accounts_error), 
								(TextView)getView().findViewById(R.id.general_error));
		}
		
		return accountsAreSelected;
	}
	
	/**
	 * Verify that the provided amount value is greater than $25, if it not valid then
	 * inline errors are shown.
	 * @return
	 */
	private boolean validateAmount() {
		amountField.clearFocus();
		final String amount = amountField.getText().toString();
		
		final boolean isValid = isAmountTwentyFiveDollarsOrMore(amount);
		
		if(!isValid) {
			amountField.setErrors();
			amountField.getErrorLabel().setText(getString(R.string.amount_less_than_twenty_five));
			amountField.getErrorLabel().setVisibility(View.VISIBLE);
		}
		
		return isValid;
	}
	
	/**
	 * Validates that the amount passed in, a String representation of a double, is 
	 * greater than or equal to $25.
	 * 
	 * @param amount an amount, should be a double in the form of a String.
	 * @return if the amount is equal to or greater than $25
	 */
	private boolean isAmountTwentyFiveDollarsOrMore(final String amount) {
		int value = 0;
		final int twentyFiveDollars = 2500;
		
		if(!Strings.isNullOrEmpty(amount)){
			value = Integer.parseInt(amount.replaceAll(NON_NUMBER_CHARACTERS, ""));
		}
		
		return InputValidator.isValueBoundedBy(value, twentyFiveDollars, Integer.MAX_VALUE);
	}

	/**
	 * Shows a modal dialog on the page to notify the user that if they cancel their current action
	 * all information will be lost.
	 */
	private void showCancelModal(final boolean goToAccountSummary) {
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(this.getActivity(), null);
		
		bottom.setButtonText(R.string.cancel_this_action);
		
		final ModalDefaultTopView top = new ModalDefaultTopView(this.getActivity(), null);
		top.hideNeedHelpFooter();
		top.setTitle(getString(R.string.cancel_this_action) + "?");
		top.setContent(R.string.cancel_this_action_content);
		final ModalAlertWithOneButton cancelModal = new ModalAlertWithOneButton(this.getActivity(), top, bottom);

		bottom.getButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				useMyBackPress = false;
				
				if (goToAccountSummary) {
					cancelModal.dismiss();
					((BankNavigationRootActivity)getActivity()).popTillFragment(BankAccountSummaryFragment.class);
				} else {
					((BankNavigationRootActivity)getActivity()).onBackPressed();
				}
			}

		});		
		
		this.showCustomAlertDialog(cancelModal);
	}

	/**
	 * If the back button is pressed, we need to show a modal to alert the user that
	 * going back will delete any entered information.
	 */
	@Override
	public void onBackPressed() {
		showCancelModal(false);
	}

	@Override
	public boolean isBackPressDisabled() {
		return useMyBackPress;
	}
	
	/**
	 * Return a calendar listener that will update the chosen payment date upon a user selecting
	 * a date on the calendar.
	 * @return a CalendarListener object.
	 */
	private CalendarListener createCalendarListener() {	
		// Setup listener
		final CalendarListener calendarListener = new CalendarListener(calendarFragment) {
			private static final long serialVersionUID = -5277452816704679940L;

			@Override
			public void onSelectDate(final Date date, final View view) {
				super.onSelectDate(date, view);
				
				final Calendar cal=Calendar.getInstance();
				cal.setTime(date);
				setChosenPaymentDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
			
				//Delay closing of calendar to be able to see the selection change
				final long halfSecondDelay = 500;
				new Handler().postDelayed(getCalendarDissmissRunnable(), halfSecondDelay);
			}
		};
	
		return calendarListener;
	}
	
	/**
	 * 
	 * @return a runnable that will dismiss the calendar fragment when run.
	 */
	private Runnable getCalendarDissmissRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				if(calendarFragment != null){
					calendarFragment.dismiss();
				}
			}
		};
	}
	
	/**
	 * Updates the chosen date Calendar variable, {@code chosenPaymentDate}. If
	 * the date is earlier than the earliest possible date, then it is set to
	 * that. This additionally updates the text view.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void setChosenPaymentDate(final Integer year, final Integer month,
			final Integer day) {

		dateTextView.setText(BankStringFormatter.formatDate(year.toString(),
				BankStringFormatter.formatDayOfMonth(month), BankStringFormatter.formatDayOfMonth(day)));
		chosenPaymentDate.set(year, month - 1, day);
	}

	/**
	 * Method displays a calendar in a dialog form with the chosen date selected.
	 */
	public void showCalendar() {
		calendarFragment = new CalendarFragment();

		/** The calendar will appear with the month and year in this Calendar instance */
		Calendar displayedDate = Calendar.getInstance();
		
		
		/**Convert stored in text field into chosen date, this will avoid issue on rotation*/
		try{
			final String[] selectedDate = dateTextView.getText().toString().split("[\\/]+");
			
			/** The Calendar will appear with the date specified by this calendar instance selected*/
			chosenPaymentDate.set( Integer.parseInt(selectedDate[2]),
				      Integer.parseInt(selectedDate[0]) - 1,
					  Integer.parseInt(selectedDate[1]));
			
			/**Check if restoring calendar selection date, -1 means it is initializing*/
			displayedDate = chosenPaymentDate;
			
		} catch(final Exception ex){
			chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
					chosenPaymentDate.get(Calendar.MONTH),
					chosenPaymentDate.get(Calendar.DAY_OF_MONTH));
			
			displayedDate = chosenPaymentDate;
		}
		
		/**Show calendar as a dialog*/
		calendarFragment
			.show(((NavigationRootActivity)DiscoverActivityManager.getActiveActivity()).getSupportFragmentManager(),
				getResources().getString(R.string.schedule_pay_deliver_on_title),
				displayedDate,
			    chosenPaymentDate, 
			    earliestPaymentDate,
				BankUser.instance().getHolidays(),
				createCalendarListener());
	}

	/**
	 * Handle errors that the server returns.
	 * This is for displaying inline errors on the screen for the user.
	 * 
	 */
	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		boolean handled = false;		
		lastErrorObject = msgErrResponse;
		
		for(final BankError error : msgErrResponse.errors) {
			final String errorFieldName = error.name;
			
			handled = true;
			
			if(TransferDetail.AMOUNT.equalsIgnoreCase(errorFieldName)) {
				showAmountError(error.message);
			} else if(TransferDetail.SEND_DATE.equalsIgnoreCase(errorFieldName)) {
				showErrorOnField(error.message, sendOnDateCell);
			} else if(TransferDetail.FROM_ACCOUNT.equalsIgnoreCase(errorFieldName)) {
				showErrorOnField(error.message, fromCell);
			} else if(TransferDetail.TO_ACCOUNT.equalsIgnoreCase(errorFieldName)) {
				showErrorOnField(error.message, toCell);
			} else {
				handled = false;
			}
		}
		
		handled |= recurring.handleError(msgErrResponse);
		
		showErrorLabel(getString(R.string.bank_deposit_error_notify), (TextView)getView().findViewById(R.id.general_error));

		getScrollView().smoothScrollTo(0, 0);
		return handled;
	}
	
	/**
	 * Show an inline error underneath the send amount field
	 * @param error a String to show as an error message.
	 */
	private void showAmountError(final String error) {
		amountField.getErrorLabel().setText(error);
		amountField.getErrorLabel().setVisibility(View.VISIBLE);
	}
	
	/**
	 * Show an error message on a given BankSimpleEditDetail object, which already has an error label defined
	 * for it.
	 * @param error a String error message to display.
	 * @param tableRow the BankSimpleEditDetail to show the error beneath.
	 */
	private void showErrorOnField(final String error, final BankSimpleEditDetail tableRow) {
		tableRow.getErrorLabel().setText(error);
		tableRow.getErrorLabel().setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets the provided TextView's text to the error String and make it visible.
	 * @param error a String to use as error text.
	 * @param label a TextView to use as an error label.
	 */
	private void showErrorLabel(final String error, final TextView label) {
		if(label != null && error != null) {
			label.setText(error);
			label.setVisibility(View.VISIBLE);
		}else {
			Log.e(TAG, "Could not show error label : TextView was NULL!");
		}
	}
	
}
