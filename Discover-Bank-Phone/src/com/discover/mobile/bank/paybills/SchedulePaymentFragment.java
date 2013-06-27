package com.discover.mobile.bank.paybills;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payment.CreatePaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.AccountAdapter;
import com.discover.mobile.bank.ui.InvalidCharacterFilter;
import com.discover.mobile.bank.ui.modals.AreYouSureGoBackModal;
import com.discover.mobile.bank.ui.modals.CancelThisActionModal;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.widgets.CalendarFragment;
import com.discover.mobile.common.ui.widgets.CalendarListener;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

public class SchedulePaymentFragment extends BaseFragment 
implements BankErrorHandlerDelegate, OnEditorActionListener, FragmentOnBackPressed {

	/** Keys used to save/load values possibly lost during rotation. */
	private static final String PAY_FROM_ACCOUNT_ID = "a";
	private static final String AMOUNT = "b";
	private static final String DATE_YEAR = "c";
	private static final String DATE_MONTH = "d";
	private static final String DATE_DAY = "e";
	private static final String MEMO = "f";
	private static final String CONFLICT = "conflict";
	private static final String FOCUS="focus";

	private RelativeLayout parentView;

	/** Pay From table item */
	private RelativeLayout paymentAccountItem;
	/** Memo table item */
	private RelativeLayout memoItem;
	/** Date table item */
	private RelativeLayout dateItem;

	/** Progress header - breadcrumb */
	private BankHeaderProgressIndicator progressHeader;
	/** Spinner used to display all the user bank accounts */
	private IcsSpinner paymentAccountSpinner;
	/** Error view for improper amount */
	private TextView amountError;
	/** Text view for payment title */
	private TextView paymentAccountTitle;
	/** Text View for payment account error*/
	private TextView paymentAccountError;
	/** Edit view for memo */
	private EditText memoEdit;
	/**Text view for memo error*/
	private TextView memoError;
	/** Text view for the payee */
	private TextView payeeText;
	/** Text view for inline payee error*/
	private TextView payeeError;
	/** Edit view for amount */
	private AmountValidatedEditField amountEdit;
	/** Edit view for date */
	private TextView dateText;
	/** Error view for invalid date */
	private TextView dateError;
	/** Error view for duplicate payment conflict */
	private TextView conflictError;
	/** Cancel button for view */
	private Button cancelButton;
	/** Payment button */
	private Button payNowButton;

	/** Payee object (typically passed here via bundle) */
	private PayeeDetail payee;
	/** Payment object passed via bundle */
	private PaymentDetail paymentDetail;
	/** BankUser singleton */
	private BankUser bankUser;

	/** Id for currently selected account */
	private int accountId;
	/** Earliest payment date */
	private Calendar earliestPaymentDate;
	/** Chosen payment date */
	private Calendar chosenPaymentDate;
	/** Fragment used to select a payment date*/
	private CalendarFragment calendarFragment;

	/** saved bundle data */
	private Bundle savedBundle;

	/** date error exists - Cannot submit payment */
	private boolean isDateError = false;
	/** Max character length for memo */
	private static final int MAX_CHAR_MEMO = 34;
	/** Boolean flag to detect if fragment's orientation is changing*/
	private boolean isOrientationChanging = false;
	/** Reference to the Activity's canceled listener */
	private OnPaymentCanceledListener canceledListener;
	/**Flag used to control whether back press should show cancel modal*/
	private final boolean isBackPressedDisabled = true;

	/** boolean set to true when the fragment is in edit mode*/
	private boolean editMode = false;

	/**
	 * Pattern to match the ISO8601 date & time returned by payee service -
	 * 2013-01-30T05:00:00.000+0000 - old 2013-01-30T05:00:00Z - new TODO
	 */
	private static final Pattern R8601 = Pattern
			.compile("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):"
					+ "(\\d{2}):(\\d{2}))((\\+|-)(\\d{4}))");

	/** Amount of time to wait when closing the calendar to finish selection animation. */
	private static final int CALENDAR_DELAY = 500;
	private static final int ERROR_STATE_DELAY = 1000;
	private static final int TWO_DIGIT_DAY = 10;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.schedule_payment, null);

		parentView = (RelativeLayout) view;

		payeeText = (TextView) view.findViewById(R.id.payee_text);
		payeeError = (TextView)view.findViewById(R.id.payee_error);
		progressHeader = (BankHeaderProgressIndicator) view
				.findViewById(R.id.schedule_pay_header);
		conflictError = (TextView) view.findViewById(R.id.conflict_error);
		paymentAccountItem = (RelativeLayout) view
				.findViewById(R.id.payment_acct_element);
		paymentAccountSpinner = (IcsSpinner) view.findViewById(R.id.payment_acct_spinner);
		paymentAccountError = (TextView)view.findViewById(R.id.payment_acct_error);
		paymentAccountTitle = (TextView)view.findViewById(R.id.payment_acct_title);
		amountEdit = (AmountValidatedEditField) view.findViewById(R.id.amount_edit);
		amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		amountError = (TextView) view.findViewById(R.id.amount_error);
		dateText = (TextView) view.findViewById(R.id.date_text);
		dateError = (TextView) view.findViewById(R.id.date_error);
		dateItem = (RelativeLayout) view.findViewById(R.id.date_item);
		memoItem = (RelativeLayout) view.findViewById(R.id.memo_element);
		memoEdit = (EditText) memoItem.findViewById(R.id.memo_edit);
		memoError = (TextView) view.findViewById(R.id.memo_error);
		payNowButton = (Button) view.findViewById(R.id.pay_now);
		cancelButton = (Button) view.findViewById(R.id.cancel_button);
		bankUser = BankUser.instance();

		/**Set a default value for chosen payment date*/
		chosenPaymentDate = Calendar.getInstance();

		loadDataFromBundle();
		setupInitialViewData();
		setMemoFieldValidation();
		createItemListeners();
		restoreState(savedInstanceState);

		return view;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			savedBundle = new Bundle(savedInstanceState);
		}
	}

	/**
	 * The Fragment is attached to the Activity. We register this interface that
	 * its parent must implement. Used to pass information back to the Activity.
	 */
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		try {
			canceledListener = (OnPaymentCanceledListener) activity;
		} catch (final ClassCastException e) {
			Log.e("SchedulePayment",
					"Activity must implement OnPaymentCanceledListener.");
		}
	}

	/** Fragment starts. Give focus to amount field if it's empty. */
	@Override
	public void onStart() {
		if (amountEdit.getText().length() < 1) {
			final int halfSecond = 500;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Activity currentActivity = DiscoverActivityManager.getActiveActivity();
					
					// Double check for instance type (do not assume BankNavigationRootActivity)
					// Due to possibility of timeout occurring when coming from background
					if (currentActivity instanceof BankNavigationRootActivity) {
						InputMethodManager imm = ((BankNavigationRootActivity) currentActivity)
								.getInputMethodManager();
						amountEdit.requestFocus();
						imm.showSoftInput(amountEdit, InputMethodManager.SHOW_IMPLICIT);
						imm = null;
					}
				}
			}, halfSecond);
		}
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		if((amountEdit == null) || (dateText == null)) { return; }


		outState.putInt(PAY_FROM_ACCOUNT_ID, paymentAccountSpinner.getSelectedItemPosition());
		outState.putString(AMOUNT, amountEdit.getText().toString());
		final String[] datesToSave = dateText.getText().toString().split("/");
		outState.putString(DATE_DAY, datesToSave[1]);
		outState.putString(DATE_MONTH, datesToSave[0]);
		outState.putString(DATE_YEAR, datesToSave[2]);
		outState.putString(MEMO, memoEdit.getText().toString());

		/**Remember which field has focus*/
		if( amountEdit.hasFocus() ) {
			outState.putString(FOCUS, AMOUNT);
		} else if( memoEdit.hasFocus() ) {
			outState.putString(FOCUS, MEMO);
		} else {
			/**Set to empty string to clear focus on rotation if none of the text editable fields had focus*/
			outState.putString(FOCUS, "");
		}

		/**Set to true so that keyboard is not closed in onPause*/
		isOrientationChanging = true;

		/**Store current error state*/
		if( payeeError.getVisibility() == View.VISIBLE ) {
			outState.putString(CreatePaymentDetail.PAYEE_FIELD, payeeError.getText().toString());
		}
		if( paymentAccountError.getVisibility() == View.VISIBLE ) {
			outState.putString(CreatePaymentDetail.PAYMENT_METHOD_FIELD, paymentAccountError.getText().toString());
		}
		if( amountError.getVisibility() == View.VISIBLE ) {
			outState.putString(CreatePaymentDetail.AMOUNT_FIELD, amountError.getText().toString());
		}
		if( dateError.getVisibility() == View.VISIBLE ) {
			outState.putString(CreatePaymentDetail.DELIVERBY_FIELD, dateError.getText().toString());
		}
		if( memoError.getVisibility() == View.VISIBLE ) {
			outState.putString(CreatePaymentDetail.MEMO_FIELD, memoError.getText().toString());
		}
		if( conflictError.getVisibility() == View.VISIBLE ) {
			outState.putString(CONFLICT, conflictError.getText().toString());
		}
	}


	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	public void restoreState(final Bundle savedInstanceState) {
		Bundle saveState = savedInstanceState;

		if ((saveState == null) && (savedBundle != null)) {
			saveState = new Bundle(savedBundle);
		}

		if (saveState != null) {
			paymentAccountSpinner.setSelection(saveState
					.getInt(PAY_FROM_ACCOUNT_ID));
			amountEdit.setText(saveState.getString(AMOUNT));
			final String year = saveState.getString(DATE_YEAR);
			if(year != null) {
				dateText.setText(formatPaymentDate(
						year,
						saveState.getString(DATE_MONTH),
						saveState.getString(DATE_DAY)));
			}
			memoEdit.setText(saveState.getString(MEMO));

			/**Restore error state*/
			final Bundle data = saveState;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setErrorString(payeeError,data.getString(CreatePaymentDetail.PAYEE_FIELD));
					setErrorString(paymentAccountError,data.getString(CreatePaymentDetail.PAYMENT_METHOD_FIELD));
					setErrorString(amountError,data.getString(CreatePaymentDetail.AMOUNT_FIELD));
					setErrorString(dateError, data.getString(CreatePaymentDetail.DELIVERBY_FIELD));
					setErrorString(memoError,data.getString(CreatePaymentDetail.MEMO_FIELD));
					setErrorString(conflictError,data.getString(CONFLICT));

					/** Highlight text field in red with X if has an error */
					if (data.containsKey(CreatePaymentDetail.AMOUNT_FIELD)) {
						amountEdit.setErrors();
					}

					restoreCellFocus(data);
				}
			}, ERROR_STATE_DELAY);

		}

		/**Reset Calendar Event Listener*/
		final Fragment fragment = getFragmentManager().findFragmentByTag(CalendarFragment.TAG);
		if(fragment instanceof CalendarFragment) {
			calendarFragment = (CalendarFragment) fragment;
			calendarFragment.setCalendarListener(createCalendarListener());
		}
	}

	private void restoreCellFocus(final Bundle data) {
		/**Restore focus state*/
		if( data.containsKey(FOCUS) ) {
			if( data.getString(FOCUS).equals(MEMO)) {
				dispatchTouchToMemoItem(MotionEvent.ACTION_DOWN);
				dispatchTouchToMemoItem(MotionEvent.ACTION_UP);               
			} else if(data.getString(FOCUS).equals(AMOUNT)) {
				amountEdit.requestFocus();
			}				
		} else {
			amountEdit.requestFocus();
		}
	}

	/**
	 * Is used when focus needs to be set to the memo item cell.
	 * @param actionEvent
	 */
	private void dispatchTouchToMemoItem(final int actionEvent) {
		final MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), actionEvent , 0, 0, 0);
		memoItem.dispatchTouchEvent(event);
		event.recycle();
	}

	/**
	 * Method used to set the error string for an inline error label and make it visible.
	 * 
	 * @param view TextView that represents an inline error whose text will be set using the param text.
	 * @param text String to show to the user as an inline error
	 */
	public void setErrorString(final TextView view, final String text ) {
		if( (view != null) && !Strings.isNullOrEmpty(text)  ) {
			view.setText(text);
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}
	
	/** Populates {@link IcsSpinner} dropdown with eligible bank accounts for Bill Pay. */
	private void setupSpinnerAdapter() {
		final AccountAdapter accountAdapter = new AccountAdapter(getActivity(),
				R.layout.bank_dropdown_selection_view_large, bankUser.getPaymentCapableAccounts().accounts);
		paymentAccountSpinner.setAdapter(accountAdapter);
	}
	
	/**
	 * Selects a specific Account from the IcsSpinner if found.
	 * Otherwise, does not alter the current selection.
	 * @param account - {@link Account} you wish to select.
	 */
	private void setSpinnerSelectedAccount(Account account) {
		// Parse all items in the Adapter and determine if one matches the requested Account.
		for (int x=0; x<paymentAccountSpinner.getAdapter().getCount(); ++x) {
			if (paymentAccountSpinner.getItemAtPosition(x).equals(account)) {
				paymentAccountSpinner.setSelection(x);
				return;
			}
		}
	}

	/**
	 * Initializes the layout's elements with dynamic data either passed to the
	 * fragment or loaded from elsewhere.
	 */
	private void setupInitialViewData() {
		setupSpinnerAdapter();

		/**Check if page is displayed to add a payment*/
		if (payee != null) {
			dateText.setText(getPaymentDate(payee.paymentDate));
			payeeText.setText(payee.nickName);

			setSpinnerSelectedAccount(getDefaultAccount());
			setSelectedAccountTitle(BankUser.instance().getAccount(Integer.toString(accountId)));
		}
		/**Check if page is displayed to edit a payment*/
		else if( paymentDetail != null ) {
			dateText.setText(getPaymentDate(paymentDetail.deliverBy));
			payeeText.setText(paymentDetail.payee.nickName);
			amountEdit.setText(paymentDetail.amount.formatted.replace("$", ""));
			memoEdit.setText(paymentDetail.memo);
			
			accountId = Integer.parseInt(paymentDetail.paymentAccount.id);
			setSpinnerSelectedAccount(paymentDetail.paymentAccount); 
			setSelectedAccountTitle(BankUser.instance().getAccount(Integer.toString(accountId)));

			/**Update Pay Now Button Text*/
			payNowButton.setText(R.string.schedule_pay_save_payment);

			/** Update the earliest payment based on what is provided via the payee list */
			PayeeDetail currentPayee = paymentDetail.payee;
			if (currentPayee != null && BankUser.instance().hasPayees()) {
				currentPayee = BankUser.instance().getPayees().getPayeeFromId(currentPayee.id);
				updateEarliestPaymentDate(currentPayee.paymentDate);
			}
		}

		amountEdit.attachErrorLabel(amountError);
		progressHeader.initialize(0);
		progressHeader.hideStepTwo();
		progressHeader.setTitle(R.string.bank_pmt_details,
				R.string.confirm, R.string.confirm);
	}

	/**
	 * Finds the User's default payment account. Populates with the first
	 * checking account, otherwise first Money Market account.
	 * 
	 * @return Name of default account
	 */
	private Account getDefaultAccount() {
		for (final Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_CHECKING)) {
				accountId = Integer.valueOf(a.id);
				return a;
			}
		}
		for (final Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_MMA)) {
				accountId = Integer.valueOf(a.id);
				return a;
			}
		}
		return null;
	}

	/**
	 * Loads the required view-data from the bundle/resources.
	 */
	private void loadDataFromBundle() {
		final Bundle b = getArguments();
		if (b != null) {
			payee = (PayeeDetail) b.getSerializable(BankExtraKeys.SELECTED_PAYEE);

			//If payee is null user can be attempting to edit payment instead 
			if( payee == null ) {
				paymentDetail = (PaymentDetail)b.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			}
			editMode = b.getBoolean(BankExtraKeys.EDIT_MODE, false);
		}
	}

	/**
	 * Instantiates the Cancel Button's modal and the modal's button listeners.
	 */
	private void setupCancelButton() {
		new CancelThisActionModal(this).showModal();
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

		dateText.setText(formatPaymentDate(year.toString(),
				formateDayMonth(month), formateDayMonth(day)));
		chosenPaymentDate.set(year, month - 1, day);
	}

	private String formateDayMonth(final Integer value){
		String valueString = value.toString();
		if (value < TWO_DIGIT_DAY){
			valueString = "0" + valueString;
		}
		return valueString;
	}

	/**
	 * Flips the memo elements, TextView and EditText, visually and textually.
	 * 
	 * @param showEditable
	 *            shows the editable field if true; the text field if false.
	 */
	private void flipMemoElements(final boolean showEditable) { // TODO REMOVE
//		final BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
//		InputMethodManager imm = activity.getInputMethodManager();
//
//		// EditText will be shown.
//		if (showEditable) {
//			/**Hide memo error code*/
//			memoError.setVisibility(View.GONE);
//
//			memoText.setVisibility(View.INVISIBLE);
//			memoEdit.setVisibility(View.VISIBLE);
//			memoEdit.setText(memoText.getText().toString());
//			memoEdit.requestFocus();
//			imm.showSoftInput(memoEdit, 0);
//			memoEdit.setSelection(memoEdit.getText().length());
//
//			// TextView will be shown.
//		} else {
//			memoText.setVisibility(View.VISIBLE);
//			memoEdit.setVisibility(View.INVISIBLE);
//			memoText.setText(memoEdit.getText().toString());
//			imm.hideSoftInputFromWindow(memoEdit.getWindowToken(), 0);
//		}
//		imm = null;
	}

	/**
	 * Limits the memo's character length and prevents invalid characters.
	 */
	private void setMemoFieldValidation() {
		final InputFilter[] filters = new InputFilter[2];
		filters[0] = new InvalidCharacterFilter();
		filters[1] = new InputFilter.LengthFilter(MAX_CHAR_MEMO);
		memoEdit.setFilters(filters);
	}

	/**
	 * Sets the date error in case a payment is actually sent after the previous
	 * cut-off time.
	 * 
	 * @param isError
	 *            displays the error if true, and normal if false.
	 */
	public void setDateError(final boolean isError) {
		isDateError = isError;
		if (isError) {
			dateError.setVisibility(View.VISIBLE);
		} else {
			dateError.setVisibility(View.GONE);
		}
	}

	/**
	 * Sets an error message underneath the error upon a 409 Conflict from the
	 * payment request.
	 * 
	 * @param isError
	 *            displays the error if true, and hides it if false.
	 */
	public void setDuplicatePaymentError(final boolean isError) {
		if (isError) {
			conflictError.setVisibility(View.VISIBLE);
		} else {
			conflictError.setVisibility(View.GONE);
		}
	}

	/**
	 * Takes an ISO8601 formatted date of format 2013-01-30T05:00:00.000+0000
	 * and returns a string formatted for the 'Deliver By' field. Additionally
	 * the {@code earliestPaymentDate} and {@code chosenPaymentDate} are set.
	 * 
	 * @param date
	 * @return
	 */
	private String getPaymentDate(final String date) {
		final Matcher m = R8601.matcher(date);
		final int groupThree = 3;
		if (m.lookingAt()) {
			earliestPaymentDate = Calendar.getInstance();
			// Month - 1 is because Calendar starts Months at 0.
			earliestPaymentDate.set(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)) - 1,
					Integer.parseInt(m.group(groupThree)));
			chosenPaymentDate = Calendar.getInstance();
			chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
					earliestPaymentDate.get(Calendar.MONTH),
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH));

			return formatPaymentDate(m.group(1), m.group(2), m.group(groupThree));
		} else {
			return formatPaymentDate( Integer.toString(chosenPaymentDate.get(Calendar.YEAR)), 
					Integer.toString(chosenPaymentDate.get(Calendar.MONTH) + 1), 
					Integer.toString(chosenPaymentDate.get(Calendar.DAY_OF_MONTH)));
		}
	}

	/**
	 * Takes an ISO8601 formatted date of format 2013-01-30T05:00:00.000+0000
	 * and sets the earliestPaymentDate date member.
	 * 
	 * @param date
	 * @return
	 */
	private void updateEarliestPaymentDate(final String date) {
		final int three = 3;
		final Matcher m = R8601.matcher(date);
		if (m.lookingAt()) {
			earliestPaymentDate = Calendar.getInstance();
			// Month - 1 is because Calendar starts Months at 0.
			earliestPaymentDate.set(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)) - 1,
					Integer.parseInt(m.group(three)));	
		}
	}

	/**
	 * Formats date as MM/dd/YYYY.
	 * 
	 * @param year
	 * @param month
	 *            formatted 1-12 (i.e. not 0 for January)
	 * @param day
	 * @return formatted date
	 */
	private String formatPaymentDate(final String year, final String month,
			final String day) {
		final StringBuilder sb = new StringBuilder();
		sb.append(month);
		sb.append('/');
		sb.append(day); 
		sb.append('/');
		sb.append(year); 
		return sb.toString();
	}

	/**
	 * Initializes the view's miscellaneous listeners.
	 */
	private void createItemListeners() {

		// Listens for a focus change so that we can handle special view
		// behavior
		parentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if (!v.equals(memoEdit)) {
					flipMemoElements(false);
				}
				return false;
			}
		});
        
		memoEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if (!hasFocus) {
					flipMemoElements(false);
				}
			});

			/**Set listener to flip memo edit field from editable to non-editable when user taps done on keyboard*/
			memoEdit.setOnEditorActionListener(this);
		}else{
			memoItem.setVisibility(View.GONE);

		}

		paymentAccountItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				memoEdit.clearFocus();
				amountEdit.clearFocus();

				paymentAccountError.setVisibility(View.GONE);
				if(bankUser.getPaymentCapableAccounts().accounts.size() > 1) {
					paymentAccountSpinner.performClick();
				}
			}
		});
		
		paymentAccountSpinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view, 
					int position, long id) {

				// Retrieve the newly selected account
				final Account selectedAccount = (Account) paymentAccountSpinner.getSelectedItem();
				
				if (selectedAccount == null) {
					return;
				}
				
				accountId = Integer.valueOf(selectedAccount.id);

				// Update the title above the spinner
				setSelectedAccountTitle(selectedAccount);
			}

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
			}
		});

		dateItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				setDateError(false);
				final BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
				final InputMethodManager imm = activity.getInputMethodManager();
				imm.hideSoftInputFromWindow(memoEdit.getWindowToken(), 0);
				showCalendar();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				setupCancelButton();
			}
		});

		amountEdit.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE){
					amountEdit.clearFocus();
					final BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
					final InputMethodManager imm = activity.getInputMethodManager();
					imm.hideSoftInputFromWindow(memoEdit.getWindowToken(), 0);
				}
				return true;
			}
		});

		payNowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				setDuplicatePaymentError(false);
				/**Clear focus to close keyboard*/
				if(!amountEdit.isValid()) {
					amountEdit.setErrors();
				}

				if( amountEdit.isValid() && !isDateError) {
					amountEdit.clearFocus();

					clearErrors();

					final String memo = memoEdit.getText().toString();

					final CreatePaymentDetail payment = new CreatePaymentDetail();
					payment.payee.id = (payee != null ) ? payee.id : paymentDetail.payee.id;
					payment.amount = formatAmount(amountEdit.getText()
							.toString());
					payment.paymentMethod.id = Integer.toString(accountId);
					payment.deliverBy = BankStringFormatter.convertToISO8601Date(dateText.getText().toString(),true);

					if ( !Strings.isNullOrEmpty(memo)) {
						payment.memo = memo;
					}

					//Check if user is adding payment
					if( payee != null ) {		
						BankServiceCallFactory.createMakePaymentCall(payment).submit();
					}
					//Check if user is editing a payment
					else if( paymentDetail != null ){
						BankServiceCallFactory.updatePaymentCall(payment, paymentDetail.id).submit();
					}
				}
			}
		});
	}
	
	private void setSelectedAccountTitle(final Account account) {
		if (null != account.accountNumber && null != account.accountNumber.ending) {
			paymentAccountTitle.setText(getString(R.string.schedule_pay_from_account_ending) + 
					  StringUtility.SPACE + 
					  account.accountNumber.ending);
		}
	}

	/**
	 * Formats the amount from 1.13 to 113 and converts to a int
	 * @param amount
	 * @return
	 */
	private int formatAmount(final String amount) {
		int ret = 0;
		if (!Strings.isNullOrEmpty(amount)) {
			String formattedAmount = amount.replaceAll(",", "");
			formattedAmount = formattedAmount.replace(".", "");
			ret = Integer.parseInt(formattedAmount);
		}
		return ret;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_SECTION;
	}

	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {		
		/**If handled is false it the Error Handler that called this method will show a catch all modal*/
		boolean handled = false;

		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Check if error is for Payee field*/
				if( error.name.equals(CreatePaymentDetail.PAYEE_FIELD) ) {
					setErrorString(payeeError,error.message);
				}
				/**Check if error is for amount field*/
				else if( error.name.equals(CreatePaymentDetail.AMOUNT_FIELD)) {
					setErrorString(amountError, error.message);

					/** Highlight in red to show error */
					amountEdit.setErrors();
				}
				/**Check if error is for Payment method field*/
				else if( error.name.equals(CreatePaymentDetail.PAYMENT_METHOD_FIELD)) {
					setErrorString(paymentAccountError,error.message);
				}
				/**Check if error is for Deliver by field*/
				else if( error.name.equals(CreatePaymentDetail.DELIVERBY_FIELD) ) {
					setErrorString(dateError,error.message);
				}
				/**Check if error is for Memo Field*/
				else if( error.name.equals(CreatePaymentDetail.MEMO_FIELD)) {
					setErrorString(memoError,error.message);
				}
				/**Show error at the top of the screen */
				else {
					setErrorString(conflictError,error.message);
				}

				/**Set to true to avoid a catch all modal being displayed*/
				handled = true;
			} else if( !Strings.isNullOrEmpty(error.message) ) { 
				setErrorString(conflictError,error.message);

				/**Set to true to avoid a catch all modal being displayed*/
				handled = true;
			}
		}

		((ScrollView)getView().findViewById(R.id.main_view)).smoothScrollTo(0,0);

		return handled;
	}

	/**
	 * Clear any inline errors shown
	 */
	public void clearErrors() {
		payeeError.setVisibility(View.GONE);
		paymentAccountError.setVisibility(View.GONE);
		amountError.setVisibility(View.GONE);
		setDateError(false);
		memoError.setVisibility(View.GONE);
		conflictError.setVisibility(View.GONE);
		amountEdit.clearErrors();
	}

	@Override 
	public void onResume() {
		super.onResume();

		/**Reset flag*/
		isOrientationChanging = false;

		/**Enable text watcher which will format text in text field*/
		amountEdit.enableBankAmountTextWatcher(true);
	}

	@Override
	public void onPause() {
		super.onPause();

		/**Disable Text Watcher to support rotation*/
		amountEdit.enableBankAmountTextWatcher(false);
		/**hide the soft keyboard --fixes defect  DE6032 
		   keyboard wasn't getting hidden due to reataining focus**/
		amountEdit.clearFocus();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		/**Check if onPause was called because of an orientation change*/
		if( !isOrientationChanging && (amountEdit != null) ) {
			amountEdit.showKeyboard(false);
		}
	}

	/**
	 * Method used to detect if user has pressed done on the soft keyboard. This callback will only be called
	 * if the ime option for the editable field has been set to EditorInfo.IME_ACTION_DONE.
	 * 
	 * @param v	The view that was clicked.
	 * @param actionId	Identifier of the action. This will be either the identifier you supplied, 
	 * or EditorInfo.IME_NULL if being called due to the enter key being pressed.
	 * @param event	If triggered by an enter key, this is the event; otherwise, this is null.
	 * 
	 * @return Return true if you have consumed the action, else false.
	 */
	@Override
	public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
		if ((actionId == EditorInfo.IME_ACTION_DONE) && v.equals(memoEdit)) {
			flipMemoElements(false);
		}
		return false;
	}

	/** Cancel modal presentation should override default Back button behavior */
	@Override
	public void onBackPressed() {
		/**Show Cancel Modal only if back press has been disabled*/
		if( isBackPressedDisabled ) {
			final AreYouSureGoBackModal modal = new AreYouSureGoBackModal(this, new OnClickListener() {
				@Override
				public void onClick(final View v) {
					canceledListener.onPaymentCanceled();
					final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
					if((currentActivity != null) && (currentActivity instanceof BankNavigationRootActivity)) {
						final BankNavigationRootActivity navActivity = (BankNavigationRootActivity)currentActivity;
						if(editMode){
							navActivity.getSupportFragmentManager().popBackStackImmediate();
						}else{
							navActivity.popTillFragment(BankSelectPayee.class);
						}
					}
				}
			});

			modal.setOverridePop(true);
			modal.showModal();
		}
	}

	@Override
	public boolean isBackPressDisabled() {
		return isBackPressedDisabled;
	}

	private CalendarListener createCalendarListener() {	
		//A runnable that will dismiss the calendar fragment.
		final Runnable closeCalendarRunnable = new Runnable() {
			@Override
			public void run() {
				calendarFragment.dismiss();

				/** Set Calendar Fragment to null to allow it to be shown again and recreated */
				calendarFragment = null;
			}
		};

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
				new Handler().postDelayed(closeCalendarRunnable, CALENDAR_DELAY);
			}

			@Override
			public void onCancel() {
				/** Set Calendar Fragment to null to allow it to be shown again and recreated */
				calendarFragment = null;
			}
		};


		return calendarListener;
	}

	/**
	 * Method displays a calendar in a dialog form with the chosen date selected.
	 */
	private void showCalendar() {
		/** Verify calendar hasn't already been created */
		if (null == calendarFragment) {
			calendarFragment = new CalendarFragment();
	
			/** The calendar will appear with the month and year in this Calendar instance */
			Calendar displayedDate = Calendar.getInstance();
	
	
			/**Convert stored in text field into chosen date, this will avoid issue on rotation*/
			try{
				final String[] date = dateText.getText().toString().split("[\\/]+");
	
				/** The Calendar will appear with the date specified by this calendar instance selected*/
				chosenPaymentDate.set( Integer.parseInt(date[2]),
						Integer.parseInt(date[0]) - 1,
						Integer.parseInt(date[1]));
	
				displayedDate = chosenPaymentDate;	
			}catch(final NumberFormatException ex){
				chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
						chosenPaymentDate.get(Calendar.MONTH),
						chosenPaymentDate.get(Calendar.DAY_OF_MONTH));
	
				displayedDate = chosenPaymentDate;
			}
	
			/**Show calendar as a dialog*/
			calendarFragment.show(getFragmentManager(),
					getString(R.string.schedule_pay_date_picker_title),
					displayedDate,
					chosenPaymentDate, 
					earliestPaymentDate,
					BankUser.instance().getHolidays(),
					createCalendarListener());

		}
	}

	/**
	 * Informs the implementing Activity that this fragment's transaction was
	 * canceled.
	 */
	public interface OnPaymentCanceledListener {
		void onPaymentCanceled();
	}
}