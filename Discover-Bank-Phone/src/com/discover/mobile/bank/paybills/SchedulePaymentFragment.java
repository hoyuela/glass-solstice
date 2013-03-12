package com.discover.mobile.bank.paybills;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payment.CreatePaymentDetail;
import com.discover.mobile.bank.ui.AccountAdapter;
import com.discover.mobile.bank.ui.InvalidCharacterFilter;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.net.error.bank.BankError;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;
import com.discover.mobile.common.ui.widgets.CustomTitleDatePickerDialog;
import com.discover.mobile.common.ui.widgets.SchedulePaymentAmountEditText;
import com.discover.mobile.common.utils.CommonUtils;
import com.google.common.base.Strings;

public class SchedulePaymentFragment extends BaseFragment implements BankErrorHandlerDelegate {

	/** Keys used to save/load values possibly lost during rotation. */
	private static final String PAY_FROM_ACCOUNT_ID = "a";
	private static final String AMOUNT = "b";
	private static final String DATE_YEAR = "c";
	private static final String DATE_MONTH = "d";
	private static final String DATE_DAY = "e";
	private static final String MEMO = "f";

	private ScrollView parentView;

	/** Pay From table item */
	private RelativeLayout paymentAccountItem;
	/** Memo table item */
	private RelativeLayout memoItem;
	/** Amount table item */
	private RelativeLayout amountItem;
	/** Date table item */
	private RelativeLayout dateItem;

	/** Progress header - breadcrumb */
	private BankHeaderProgressIndicator progressHeader;
	/** Date picker for selecint a Deliver By date */
	private CustomTitleDatePickerDialog deliverByDatePicker;
	/** Spinner used to display all the user bank accounts */
	private Spinner paymentAccountSpinner;
	/** Error view for improper amount */
	private TextView amountError;
	/** Text view for payment account choice */
	private TextView paymentAccountText;
	/** Text View for payment account error*/
	private TextView paymentAccountError;
	/** Text view for memo */
	private TextView memoText;
	/** Edit view for memo */
	private EditText memoEdit;
	/**Text view for memo error*/
	private TextView memoError;
	/** Text view for the payee */
	private TextView payeeText;
	/** Text view for inline payee error*/
	private TextView payeeError;
	/** Edit view for amount */
	private SchedulePaymentAmountEditText amountEdit;
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
	/** BankUser singleton */
	private BankUser bankUser;

	/** Id for currently selected account */
	private int accountId;
	/** List position for current Account */
	private int accountIndex;
	/** Earliest payment date */
	private Calendar earliestPaymentDate;
	/** Chosen payment date */
	private Calendar chosenPaymentDate;

	/** saved bundle data */
	private Bundle savedBundle;

	/** date error exists - Cannot submit payment */
	private boolean isDateError = false;
	/** Max character length for memo */
	private final int MAX_CHAR_MEMO = 40;

	/** Reference to the Activity's canceled listener */
	OnPaymentCanceledListener canceledListener;

	/**
	 * Pattern to match the ISO8601 date & time returned by payee service -
	 * 2013-01-30T05:00:00.000+0000 - old 2013-01-30T05:00:00Z - new TODO
	 */
	static final Pattern r8601 = Pattern
			.compile("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):"
					+ "(\\d{2}):(\\d{2})\\.(\\d{3}))((\\+|-)(\\d{4}))");

	// NEW PATTERN NOT YET THERE:
	// static final Pattern r8601 = Pattern
	// .compile("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):"
	// + "(\\d{2}):(\\d{2})Z)");


	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.schedule_payment, null);

		parentView = (ScrollView) view;
		payeeText = (TextView) view.findViewById(R.id.payee_text);
		payeeError = (TextView)view.findViewById(R.id.payee_error);
		progressHeader = (BankHeaderProgressIndicator) view
				.findViewById(R.id.schedule_pay_header);
		conflictError = (TextView) view.findViewById(R.id.conflict_error);
		paymentAccountItem = (RelativeLayout) view
				.findViewById(R.id.payment_acct_element);
		paymentAccountText = (TextView) view
				.findViewById(R.id.payment_acct_text);
		paymentAccountSpinner = (Spinner) view
				.findViewById(R.id.payment_acct_spinner);
		paymentAccountError = (TextView)view.findViewById(R.id.payment_acct_error);
		amountEdit = (SchedulePaymentAmountEditText) view.findViewById(R.id.amount_edit);
		amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		amountItem = (RelativeLayout) view.findViewById(R.id.amount_element);
		amountError = (TextView) view.findViewById(R.id.amount_error);
		dateText = (TextView) view.findViewById(R.id.date_text);
		dateError = (TextView) view.findViewById(R.id.date_error);
		dateItem = (RelativeLayout) view.findViewById(R.id.date_item);
		memoItem = (RelativeLayout) view.findViewById(R.id.memo_element);
		memoText = (TextView) memoItem.findViewById(R.id.memo_text);
		memoEdit = (EditText) memoItem.findViewById(R.id.memo_edit);
		memoError = (TextView) view.findViewById(R.id.memo_error);
		payNowButton = (Button) view.findViewById(R.id.pay_now);
		cancelButton = (Button) view.findViewById(R.id.cancel_button);
		bankUser = BankUser.instance();

		loadDataFromBundle();
		setInitialViewData();
		setMemoFieldValidation();
		createItemListeners();
		restoreState(savedInstanceState);

		return view;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			savedBundle = new Bundle(savedInstanceState);
		}

		super.onCreate(savedInstanceState);

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
			final BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
			InputMethodManager imm = activity.getInputMethodManager();
			amountEdit.requestFocus();
			imm.showSoftInput(amountEdit, InputMethodManager.SHOW_IMPLICIT);
			imm = null;
		}
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		if(amountEdit == null || dateText == null || memoText == null) { return; }

		outState.putInt(PAY_FROM_ACCOUNT_ID, accountIndex);
		outState.putString(AMOUNT, amountEdit.getText().toString());
		final String[] datesToSave = dateText.getText().toString().split("/");
		outState.putString(DATE_DAY, datesToSave[1]);
		outState.putString(DATE_MONTH, datesToSave[0]);
		outState.putString(DATE_YEAR, datesToSave[2]);
		outState.putString(MEMO, memoText.getText().toString());
	}

	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	public void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState == null && savedBundle != null) {
			savedInstanceState = new Bundle(savedBundle);
		}

		if (savedInstanceState != null) {
			paymentAccountSpinner.setSelection(savedInstanceState
					.getInt(PAY_FROM_ACCOUNT_ID));
			amountEdit.setText(savedInstanceState.getString(AMOUNT));
			dateText.setText(formatPaymentDate(
					savedInstanceState.getString(DATE_YEAR),
					savedInstanceState.getString(DATE_MONTH),
					savedInstanceState.getString(DATE_DAY)));
			memoEdit.setText(savedInstanceState.getString(MEMO));
			memoText.setText(savedInstanceState.getString(MEMO));
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

	/**
	 * Informs the implementing Activity that this fragment's transaction was
	 * canceled.
	 */
	public interface OnPaymentCanceledListener {
		public void onPaymentCanceled();
	}

	/**
	 * Initializes the layout's elements with dynamic data either passed to the
	 * fragment or loaded from elsewhere.
	 */
	private void setInitialViewData() {
		if (payee != null) {
			dateText.setText(getPaymentDate(payee.paymentDate));
			payeeText.setText(payee.nickName);
			paymentAccountText.setText(defaultPaymentAccount());

			if (bankUser.getAccounts().accounts.size() > 1) {
				final AccountAdapter accountAdapter = new AccountAdapter(
						getActivity(), R.layout.push_simple_spinner_view,
						bankUser.getAccounts().accounts);

				accountAdapter
				.setDropDownViewResource(R.layout.push_simple_spinner_dropdown);
				paymentAccountSpinner.setAdapter(accountAdapter);
			}
		}
		amountEdit.attachErrorLabel(amountError);
		amountEdit.setLowAndHighErrorText(getString(R.string.schedule_pay_too_low), getString(R.string.schedule_pay_too_high));
		progressHeader.initChangePasswordHeader(0);
		progressHeader.hideStepTwo();
		progressHeader.setTitle(R.string.bank_pmt_details,
				R.string.bank_pmt_scheduled, R.string.bank_pmt_scheduled);
	}

	/**
	 * Finds the User's default payment account. Populates with the first
	 * checking account, otherwise first Money Market account.
	 * 
	 * @return Name of default account
	 */
	private String defaultPaymentAccount() {
		for (final Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_CHECKING)) {
				accountId = Integer.valueOf(a.id);
				return a.nickname;
			}
		}
		for (final Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_MMA)) {
				accountId = Integer.valueOf(a.id);
				return a.nickname;
			}
		}
		return "";
	}

	/**
	 * Loads the required view-data from the bundle/resources.
	 */
	private void loadDataFromBundle() {
		final Bundle b = getArguments();
		if (b != null) {
			payee = (PayeeDetail) b
					.getSerializable(BankExtraKeys.SELECTED_PAYEE);
		}
	}

	/**
	 * Instantiates the Cancel Button's modal and the modal's button listeners.
	 */
	private void setupCancelButton() {
		final ModalDefaultTopView cancelModalTopView = new ModalDefaultTopView(
				getActivity(), null);
		cancelModalTopView.setTitle(R.string.schedule_pay_cancel_title);
		cancelModalTopView.setContent(R.string.schedule_pay_cancel_body);
		cancelModalTopView.hideNeedHelpFooter();

		final ModalDefaultTwoButtonBottomView cancelModalButtons = new ModalDefaultTwoButtonBottomView(
				getActivity(), null);
		cancelModalButtons
		.setCancelButtonText(R.string.schedule_pay_cancel_button_cancel);
		cancelModalButtons
		.setOkButtonText(R.string.schedule_pay_cancel_button_confirm);

		final ModalAlertWithTwoButtons cancelModal = new ModalAlertWithTwoButtons(
				getActivity(), cancelModalTopView, cancelModalButtons);
		((BankNavigationRootActivity) getActivity())
		.showCustomAlert(cancelModal);

		cancelModalButtons.getOkButton().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						canceledListener.onPaymentCanceled();
						cancelModal.dismiss();
						
						/**
						 * Checking if the activity is null before navigating. If it is null 
						 * the app would crash. 
						 */
						if ((BankNavigationRootActivity) getActivity() != null){
							((BankNavigationRootActivity) getActivity()).popTillFragment(BankSelectPayee.class);
						}
					}
				});

		cancelModalButtons.getCancelButton().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(final View v) {
						cancelModal.dismiss();
					}
				});
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
		if (isValidPaymentDate(year, month, day)) {
			dateText.setText(formatPaymentDate(year.toString(),
					formateDayMonth(month), formateDayMonth(day)));
			chosenPaymentDate.set(year, month - 1, day);

		} else {
			final String date = CommonUtils.getFormattedDate(
					earliestPaymentDate.get(Calendar.MONTH),
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH),
					earliestPaymentDate.get(Calendar.YEAR));
			dateText.setText(date);
			chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
					earliestPaymentDate.get(Calendar.MONTH),
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH));
		}
	}
	
	private String formateDayMonth(final Integer value){
		String valueString = value.toString();
		if (value < 10){
			valueString = "0" + valueString;
		}
		return valueString;
	}

	/**
	 * Checks to see if the chosen date is a valid date according to the
	 * earliest payment date.
	 * 
	 * @param chosenYear
	 * @param chosenMonth
	 * @param chosenDay
	 * @return true if valid, false otherwise.
	 */
	private boolean isValidPaymentDate(final int chosenYear,
			final int chosenMonth, final int chosenDay) {

		final int payYear = earliestPaymentDate.get(Calendar.YEAR);
		final int payMonth = earliestPaymentDate.get(Calendar.MONTH) + 1;
		final int payDay = earliestPaymentDate.get(Calendar.DAY_OF_MONTH);

		if (chosenYear < payYear) {
			return false;
		} else if (chosenYear > payYear) {
			return true;
		}

		// Years are equal.
		if (chosenMonth < payMonth) {
			return false;
		} else if (chosenMonth > payMonth) {
			return true;
		}

		// Years & Months are equal.
		if (chosenDay < payDay) {
			return false;
		} else if (chosenDay > payDay) {
			return true;
		}

		// Dates are equal.
		return true;
	}

	/**
	 * Flips the memo elements, TextView and EditText, visually and textually.
	 * 
	 * @param showEditable
	 *            shows the editable field if true; the text field if false.
	 */
	private void flipMemoElements(final boolean showEditable) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
		InputMethodManager imm = activity.getInputMethodManager();

		/**Hide memo error code*/
		memoError.setVisibility(View.GONE);
		
		// EditText will be shown.
		if (showEditable) {
			memoText.setVisibility(View.INVISIBLE);
			memoEdit.setVisibility(View.VISIBLE);
			memoEdit.setText(memoText.getText().toString());
			memoEdit.requestFocus();
			imm.showSoftInput(memoEdit, 0);
			memoEdit.setSelection(memoEdit.getText().length());

			// TextView will be shown.
		} else {
			memoText.setVisibility(View.VISIBLE);
			memoEdit.setVisibility(View.INVISIBLE);
			memoText.setText(memoEdit.getText().toString());
			imm.hideSoftInputFromWindow(memoEdit.getWindowToken(), 0);
		}
		imm = null;
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
		final Matcher m = r8601.matcher(date);
		if (m.lookingAt()) {
			earliestPaymentDate = Calendar.getInstance();
			// Month - 1 is because Calendar starts Months at 0.
			earliestPaymentDate.set(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)) - 1,
					Integer.parseInt(m.group(3)));
			chosenPaymentDate = Calendar.getInstance();
			chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
					earliestPaymentDate.get(Calendar.MONTH),
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH));

			return formatPaymentDate(m.group(1), m.group(2), m.group(3));
		}
		return "";
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
		sb.append(month); // Month
		sb.append('/');
		sb.append(day); // Day
		sb.append('/');
		sb.append(year); // Year
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

		memoItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				if (memoText.getVisibility() == View.VISIBLE) {
					flipMemoElements(true);
				}
			}
		});

		memoEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if (!hasFocus) {
					flipMemoElements(false);
				}
			}
		});

		paymentAccountItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				paymentAccountError.setVisibility(View.GONE);
				if(bankUser.getAccounts().accounts.size() > 1)
					paymentAccountSpinner.performClick();
			}
		});

		paymentAccountSpinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent,
					final View v, final int position, final long id) {
				final Account a = (Account) paymentAccountSpinner
						.getSelectedItem();
				accountId = Integer.valueOf(a.id);
				accountIndex = position;
				paymentAccountText.setText(a.nickname);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> arg0) {
			}
		});

		dateItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				setDateError(false);
				deliverByDatePicker.show();
			}
		});

		deliverByDatePicker = new CustomTitleDatePickerDialog(getActivity(),
				new OnDateSetListener() {

			@Override
			public void onDateSet(final DatePicker v, final int year,
					final int month, final int day) {
				setChosenPaymentDate(year, month + 1, day);
			}
		}, earliestPaymentDate.get(Calendar.YEAR),
		earliestPaymentDate.get(Calendar.MONTH),
		earliestPaymentDate.get(Calendar.DAY_OF_MONTH),
		getString(R.string.schedule_pay_date_picker_title));

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
				if(!amountEdit.isValid()) {
					amountEdit.setErrors();
				}
				
				if (amountEdit.isValid() && !isDateError) {
					clearErrors();
					
					final String memo = memoEdit.getText().toString();
					final CreatePaymentDetail payment = new CreatePaymentDetail();
					payment.payee.id = payee.id;
					payment.amount = CommonUtils
							.formatCurrencyStringAsBankInt(amountEdit.getText()
									.toString());
					payment.paymentMethod.id = Integer.toString(accountId);
					payment.deliverBy = CommonUtils
							.getServiceFormattedISO8601Date(chosenPaymentDate
									.get(Calendar.MONTH), chosenPaymentDate
									.get(Calendar.DAY_OF_MONTH),
									chosenPaymentDate.get(Calendar.YEAR));
					if (!memo.equals("")) {
						payment.memo = memo;
					}
					BankServiceCallFactory.createMakePaymentCall(payment)
					.submit();
				}
			}
		});
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
		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Check if error is for Payee field*/
				if( error.name.equals(CreatePaymentDetail.PAYEE_FIELD) ) {
					payeeError.setText(error.message);
					payeeError.setVisibility(View.VISIBLE);
				}
				/**Check if error is for amount field*/
				else if( error.name.equals(CreatePaymentDetail.AMOUNT_FIELD)) {
					amountEdit.showErrorLabelText(error.message);
				}
				/**Check if error is for Payment method field*/
				else if( error.name.equals(CreatePaymentDetail.PAYMENT_METHOD_FIELD)) {
					paymentAccountError.setText(error.message);
					paymentAccountError.setVisibility(View.VISIBLE);
				}
				/**Check if error is for Deliver by field*/
				else if( error.name.equals(CreatePaymentDetail.DELIVERBY_FIELD) ) {
					dateError.setText(error.message);
					setDateError(true);
				}
				/**Check if error is for Memo Field*/
				else if( error.name.equals(CreatePaymentDetail.MEMO_FIELD)) {
					memoError.setText(error.message);
					memoError.setVisibility(View.VISIBLE);
				}
				/**Show error at the top of the screen */
				else {
					conflictError.setText(error.message);
					conflictError.setVisibility(View.VISIBLE);
				}
			}
		}
		return true;
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
	}
	
}