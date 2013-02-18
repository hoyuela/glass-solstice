package com.discover.mobile.bank.paybills;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
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
import android.widget.ArrayAdapter;
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
import com.discover.mobile.bank.ui.InvalidAmountCharacterFilter;
import com.discover.mobile.bank.ui.InvalidCharacterFilter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.ui.widgets.SchedulePaymentDatePickerDialog;

public class SchedulePaymentFragment extends BaseFragment {

	private ScrollView parentView;

	/** Pay From table item */
	private RelativeLayout paymentAccountItem;
	/** Memo table item */
	private RelativeLayout memoItem;
	/** Amount table item */
	private RelativeLayout amountItem;

	/** Date picker for selecint a Deliver By date */
	private SchedulePaymentDatePickerDialog deliverByDatePicker;
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
	/** Date Picker calendar icon */
	private ImageView calendarIcon;

	/** Payee object (typically passed here via bundle) */
	private PayeeDetail payee;
	/** BankUser singleton */
	private BankUser bankUser;

	/** True when the amount had focus at some point */
	private boolean amountHadFocus = false;
	/** Currently selected account */
	private String accountId;
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

	/** Pattern to match the ISO8601 date & time returned by payee service */
	static final Pattern r8601 = Pattern
			.compile("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):"
					+ "(\\d{2}):(\\d{2})\\.(\\d{3}))((\\+|-)(\\d{4}))");

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.schedule_payment, null);

		parentView = (ScrollView) view;
		payeeText = (TextView) view.findViewById(R.id.payee_text);
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
		calendarIcon = (ImageView) view.findViewById(R.id.date_icon);
		memoItem = (RelativeLayout) view.findViewById(R.id.memo_element);
		memoText = (TextView) memoItem.findViewById(R.id.memo_text);
		memoEdit = (EditText) memoItem.findViewById(R.id.memo_edit);

		bankUser = BankUser.instance();

		loadDataFromBundle();
		setInitialViewData();
		setAmountFieldValidation();
		setMemoFieldValidation();
		createItemListeners();

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
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
				final ArrayAdapter<Account> spinnerAdapter = new ArrayAdapter<Account>(
						getActivity(), R.layout.push_simple_spinner,
						R.id.amount, bankUser.getAccounts().accounts);
				spinnerAdapter
						.setDropDownViewResource(R.layout.push_simple_spinner_dropdown);
				paymentAccountSpinner.setAdapter(spinnerAdapter);
			}
		}
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
	 * Initializes the views miscellaneous listeners. TODO test after merge with
	 * Jon.
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

		deliverByDatePicker = new SchedulePaymentDatePickerDialog(getActivity(),
				new OnDateSetListener() {

					@Override
					public void onDateSet(final DatePicker v, final int year,
							final int month, final int day) {
						setChosenPaymentDate(year, month + 1, day);
					}
				}, earliestPaymentDate.get(Calendar.YEAR),
				earliestPaymentDate.get(Calendar.MONTH),
				earliestPaymentDate.get(Calendar.DAY_OF_MONTH));
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
	 * 
	 * @param chosenYear
	 * @param chosenMonth
	 * @param chosenDay
	 * @return
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
	 * 
	 */
	private void setAmountFieldValidation() {
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