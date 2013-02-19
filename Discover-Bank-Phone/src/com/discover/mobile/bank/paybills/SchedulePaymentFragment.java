package com.discover.mobile.bank.paybills;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.AccountAdapter;
import com.discover.mobile.bank.ui.InvalidAmountCharacterFilter;
import com.discover.mobile.bank.ui.InvalidCharacterFilter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.common.ui.modals.ModalDefaultTwoButtonBottomView;
import com.discover.mobile.common.ui.widgets.CustomTitleDatePickerDialog;

public class SchedulePaymentFragment extends BaseFragment {

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
	private HeaderProgressIndicator progressHeader;
	/** Date picker for selecint a Deliver By date */
	private CustomTitleDatePickerDialog deliverByDatePicker;
	/** Spinner used to display all the user bank accounts */
	private Spinner paymentAccountSpinner;
	/** Error view for improper amount */
	private TextView amountError;
	/** Text view for payment account choice */
	private TextView paymentAccountText;
	/** Text view for memo */
	private TextView memoText;
	/** Edit view for memo */
	private EditText memoEdit;
	/** Text view for the payee */
	private TextView payeeText;
	/** Edit view for amount */
	private EditText amountEdit;
	/** Edit view for date */
	private EditText dateEdit;
	/** Error view for invalid date */
	private TextView dateError;
	/** Date Picker calendar icon */
	private ImageView calendarIcon;
	/** Cancel button for view */
	private Button cancelButton;
	/** Payment button */
	private Button payNowButton;

	/** Payee object (typically passed here via bundle) */
	private PayeeDetail payee;
	/** BankUser singleton */
	private BankUser bankUser;

	/** True when the amount had focus at some point */
	private boolean amountHadFocus = false;
	/** Id for currently selected account */
	private String accountId;
	/** List position for current Account */
	private int accountIndex;
	/** Earliest payment date */
	private Calendar earliestPaymentDate;
	/** Chosen payment date */
	private Calendar chosenPaymentDate;

	/** Max character length for memo */
	private final int MAX_CHAR_MEMO = 40;
	/** Maximum payment amount */
	private final float MAX_AMOUNT = 25000.0f;
	/** Minimum payment amount */
	private final float MIN_AMOUNT = 1.0f;

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
		progressHeader = (HeaderProgressIndicator) view
				.findViewById(R.id.schedule_pay_header);
		paymentAccountItem = (RelativeLayout) view
				.findViewById(R.id.payment_acct_element);
		paymentAccountText = (TextView) view
				.findViewById(R.id.payment_acct_text);
		paymentAccountSpinner = (Spinner) view
				.findViewById(R.id.payment_acct_spinner);
		amountEdit = (EditText) view.findViewById(R.id.amount_edit);
		amountItem = (RelativeLayout) view.findViewById(R.id.amount_element);
		amountError = (TextView) view.findViewById(R.id.amount_error);
		dateEdit = (EditText) view.findViewById(R.id.date_edit);
		dateError = (TextView) view.findViewById(R.id.date_error);
		dateItem = (RelativeLayout) view.findViewById(R.id.date_item);
		calendarIcon = (ImageView) view.findViewById(R.id.date_icon);
		memoItem = (RelativeLayout) view.findViewById(R.id.memo_element);
		memoText = (TextView) memoItem.findViewById(R.id.memo_text);
		memoEdit = (EditText) memoItem.findViewById(R.id.memo_edit);
		cancelButton = (Button) view.findViewById(R.id.cancel_button);

		bankUser = BankUser.instance();

		loadDataFromBundle();
		setInitialViewData();
		setAmountFieldRestrictions();
		setMemoFieldValidation();
		createItemListeners();
		restoreState(savedInstanceState);

		return view;
	}

	/**
	 * The Fragment is attached to the Activity. We register this interface that
	 * its parent must implement. Used to pass information back to the Activity.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			canceledListener = (OnPaymentCanceledListener) activity;
		} catch (ClassCastException e) {
			Log.e("SchedulePayment",
					"Activity must implement OnPaymentCanceledListener.");
		}
	}

	/** Fragment starts. Give focus to amount field if it's empty. */
	@Override
	public void onStart() {
		if (amountEdit.getText().length() < 1) {
			BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
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

		outState.putInt(PAY_FROM_ACCOUNT_ID, accountIndex);
		outState.putString(AMOUNT, amountEdit.getText().toString());
		String[] datesToSave = dateEdit.getText().toString().split("/");
		outState.putString(DATE_DAY, datesToSave[1]);
		outState.putString(DATE_MONTH, datesToSave[0]);
		outState.putString(DATE_YEAR, datesToSave[2]);
		outState.putString(MEMO, memoText.getText().toString());

		SchedulePaymentSingleton.getInstance().setState(outState);
	}

	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	public void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			savedInstanceState = SchedulePaymentSingleton.getInstance()
					.getState();
		}

		if (savedInstanceState != null) {
			paymentAccountSpinner.setSelection(savedInstanceState
					.getInt(PAY_FROM_ACCOUNT_ID));
			amountEdit.setText(savedInstanceState.getString(AMOUNT));
			dateEdit.setText(formatPaymentDate(
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
			dateEdit.setText(getPaymentDate(payee.paymentDate));
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
		for (Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_CHECKING)) {
				accountId = a.id;
				return a.nickname;
			}
		}
		for (Account a : bankUser.getAccounts().accounts) {
			if (a.type.equalsIgnoreCase(Account.ACCOUNT_MMA)) {
				accountId = a.id;
				return a.nickname;
			}
		}
		return "";
	}

	/**
	 * Loads the required view-data from the bundle/resources.
	 */
	private void loadDataFromBundle() {
		Bundle b = getArguments();
		if (b != null) {
			payee = (PayeeDetail) b
					.getSerializable(BankExtraKeys.SELECTED_PAYEE);
		}
	}

	/**
	 * Initializes the views miscellaneous listeners.
	 */
	private void createItemListeners() {
		// Listens for a focus change so that we can handle special view
		// behavior
		parentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!v.equals(memoEdit)) {
					flipMemoElements(false);
				}
				if (!v.equals(amountEdit) && amountHadFocus) {
					validateAmountField();
				}
				return false;
			}
		});

		memoItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (memoText.getVisibility() == View.VISIBLE) {
					flipMemoElements(true);
				}
			}
		});

		memoEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					flipMemoElements(false);
				}
			}
		});

		amountEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && amountHadFocus) {
					validateAmountField();
				} else {
					String stripCommas = amountEdit.getText().toString();
					stripCommas = stripCommas.replaceAll(",", "");
					amountEdit.setText(stripCommas);
					amountHadFocus = true;
				}
			}
		});

		paymentAccountItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
						accountId = a.id;
						accountIndex = position;
						paymentAccountText.setText(a.nickname);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		calendarIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deliverByDatePicker.show();
			}
		});

		dateEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
			public void onClick(View v) {
				setupCancelButton();
			}
		});

		payNowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Schedule a payment dude
				/*
				 * 1. Send payment 
				 * 2. Get response 
				 * 3. Handle Errors if necessary
				 * 4. Go to confirmation fragment
				 */
				setDateError(Math.random() > 0.5);
			}
		});
	}

	/**
	 * Instantiates the Cancel Button's modal and the modal's button listeners.
	 */
	private void setupCancelButton() {
		ModalDefaultTopView cancelModalTopView = new ModalDefaultTopView(
				getActivity(), null);
		cancelModalTopView.setTitle(R.string.schedule_pay_cancel_title);
		cancelModalTopView.setContent(R.string.schedule_pay_cancel_body);
		cancelModalTopView.hideNeedHelpFooter();

		ModalDefaultTwoButtonBottomView cancelModalButtons = new ModalDefaultTwoButtonBottomView(
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
					public void onClick(View v) {
						canceledListener.onPaymentCanceled();
						cancelModal.dismiss();
						((BankNavigationRootActivity) getActivity())
								.popTillFragment(BankSelectPayee.class);
					}
				});

		cancelModalButtons.getCancelButton().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
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
			dateEdit.setText(formatPaymentDate(year.toString(),
					month.toString(), day.toString()));
			chosenPaymentDate.set(year, month - 1, day);

		} else {
			dateEdit.setText(formatPaymentDate(
					earliestPaymentDate.get(Calendar.YEAR),
					earliestPaymentDate.get(Calendar.MONTH) + 1,
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH)));
			chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR),
					earliestPaymentDate.get(Calendar.MONTH),
					earliestPaymentDate.get(Calendar.DAY_OF_MONTH));
		}
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
	private void flipMemoElements(boolean showEditable) {
		BankNavigationRootActivity activity = (BankNavigationRootActivity) getActivity();
		InputMethodManager imm = activity.getInputMethodManager();

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
		InputFilter[] filters = new InputFilter[2];
		filters[0] = new InvalidCharacterFilter();
		filters[1] = new InputFilter.LengthFilter(MAX_CHAR_MEMO);
		memoEdit.setFilters(filters);
	}

	/**
	 * Sets restrictions related to the amount-to-pay field.
	 */
	private void setAmountFieldRestrictions() {
		InputFilter[] filters = new InputFilter[2];
		filters[0] = new InvalidAmountCharacterFilter();
		filters[1] = new InputFilter.LengthFilter(9);
		amountEdit.setFilters(filters);

		// Shows numeric keyboard as default.
		amountEdit.setRawInputType(Configuration.KEYBOARD_QWERTY);
	}

	/**
	 * Handles validation of the amount field.
	 */
	private void validateAmountField() {
		String amountStringNoCommas = amountEdit.getText().toString();
		amountStringNoCommas = amountStringNoCommas.replaceAll(",", "");
		double d;
		try {
			d = Double.parseDouble(amountStringNoCommas);
		} catch (Exception e) {
			d = 0.0f;
		}
		String outAmount = NumberFormat.getCurrencyInstance().format(d);
		outAmount = outAmount.replaceAll("\\$", "");
		amountEdit.setText(outAmount);

		if (d > MAX_AMOUNT) {
			setAmountError(true);
			amountError.setText(getString(R.string.schedule_pay_too_high));

		} else if (d < MIN_AMOUNT) {
			setAmountError(true);
			amountError.setText(getString(R.string.schedule_pay_too_low));

		} else {
			setAmountError(false);
		}
	}

	/**
	 * Sets the Error state of the amount field if isError is true and resets
	 * the state if false.
	 * 
	 * @param isError
	 */
	private void setAmountError(boolean isError) {
		int padding = (int) getResources().getDimension(
				R.dimen.between_related_elements_padding);
		if (isError) {
			amountEdit.setBackgroundResource(R.drawable.edit_text_red);
			amountItem.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height)
					+ (int) getResources().getDimension(R.dimen.small_copy_mid);
			amountItem.setPadding(padding, padding, padding, padding);
			amountItem.invalidate();
			amountError.setVisibility(View.VISIBLE);
		} else {
			amountEdit.setBackgroundResource(R.drawable.edit_text_default);
			amountItem.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height);
			amountItem.setPadding(padding, padding, padding, padding);
			amountItem.invalidate();
			amountError.setVisibility(View.GONE);
		}
	}

	/**
	 * Sets the date error in case a payment is actually sent after the previous
	 * cut-off time.
	 * 
	 * @param isError
	 *            displays the error if true, and normal if false.
	 */
	private void setDateError(boolean isError) {
		int padding = (int) getResources().getDimension(
				R.dimen.between_related_elements_padding);
		if (isError) {
			dateItem.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height)
					+ (int) getResources().getDimension(R.dimen.small_copy_mid);
			dateItem.setPadding(padding, padding, padding, padding);
			dateItem.invalidate();
			dateError.setVisibility(View.VISIBLE);
		} else {
			dateItem.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height);
			dateItem.setPadding(padding, padding, padding, padding);
			dateItem.invalidate();
			dateError.setVisibility(View.GONE);
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
	private String getPaymentDate(String date) {
		Matcher m = r8601.matcher(date);
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
	 *            formatted 1-12, not 0 for January
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
	 * Formats date as MM/dd/YYYY.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private String formatPaymentDate(final Integer year, final Integer month,
			final Integer day) {
		final StringBuilder sb = new StringBuilder();
		sb.append(month.toString()); // Month
		sb.append('/');
		sb.append(day.toString()); // Day
		sb.append('/');
		sb.append(year.toString()); // Year
		return sb.toString();
	}
}