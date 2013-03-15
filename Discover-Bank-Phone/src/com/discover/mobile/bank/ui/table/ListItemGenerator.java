package com.discover.mobile.bank.ui.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.util.BankStringFormatter;

/**
 * this detail item class is able to return the individual table cells to present
 * in the transaction detail fragment.
 * 
 * @author scottseward
 *
 */
public class ListItemGenerator { 

	private ViewPagerListItem listItem;
	private Context context = null;

	public ListItemGenerator(final Context context) {
		this.context = context;
	}

	private void initNewListItem() {
		listItem = new ViewPagerListItem(context);
	}

	public ViewPagerListItem getTwoItemCell(final int topLabelResource, final String middleLabelText) {
		initNewListItem();
		if(middleLabelText != null) {
			listItem.getTopLabel().setText(topLabelResource);
			listItem.getMiddleLabel().setText(middleLabelText);
			listItem.getBottomLabel().setVisibility(View.GONE);
		}
		return listItem;
	}

	public ViewPagerListItem getThreeItemCell(final int topLabelResource, final String middleLabelText, 
			final String bottomLabelText) {
		final ViewPagerListItem temp = getTwoItemCell(topLabelResource, middleLabelText);
		temp.getBottomLabel().setVisibility(View.VISIBLE);
		temp.getBottomLabel().setText(bottomLabelText);

		return temp;
	}

	public ViewPagerListItem getMemoItemCell(final String memo) {
		String memoText = memo;
		if(memo == null) {
			memoText = "";
		} 
		
		listItem = new ViewPagerListItem(context);
		getTwoItemCell(R.string.memo, memoText);
		listItem.getMiddleLabel().setTextAppearance(context, R.style.smallest_copy);

		return listItem;
	}

	public ViewPagerListItem getDescriptionCell(final String description, final String transId) {
		return getThreeItemCell(R.string.description, description, transId);
	}

	public ViewPagerListItem getBalanceCell(final int balance) {
		return getTwoItemCell(R.string.balance, BankStringFormatter.convertCentsToDollars(balance));
	}

	public ViewPagerListItem getDateCell(final String formattedDate) {
		return getTwoItemCell(R.string.date, convertDate(formattedDate.split(ActivityDetail.DATE_DIVIDER)[0]));
	}

	public ViewPagerListItem getAmountCell(final String amount) {
		return getTwoItemCell(R.string.amount, BankStringFormatter.convertCentsToDollars(amount));
	}

	public ViewPagerListItem getAmountCell(final int amount) {
		return getTwoItemCell(R.string.amount, BankStringFormatter.convertCentsToDollars(amount));
	}

	public ViewPagerListItem getFromCell(final String from, final String balance) {
		return getThreeItemCell(R.string.from, from, context.getString(R.string.available_balance) + " " + balance);
	}

	public ViewPagerListItem getToCell(final String to) {
		return getTwoItemCell(R.string.to, to);
	}

	public ViewPagerListItem getSendOnCell(final String sendOn) {
		return getTwoItemCell(R.string.send_on, sendOn);
	}

	public ViewPagerListItem getDeliverByCell(final String deliverByDate) {
		return getTwoItemCell(R.string.deliver_by, deliverByDate);
	}

	public ViewPagerListItem getDeliveredOnCell(final String deliveredOnDate) {
		return getTwoItemCell(R.string.delivered_on, deliveredOnDate);
	}

	public ViewPagerListItem getFrequencyCell(final String frequency) {
		return getTwoItemCell(R.string.frequency, frequency);
	}

	public ViewPagerListItem getPayeeCell(final String payee) {
		return getTwoItemCell(R.string.payee, payee);
	}

	public ViewPagerListItem getPayFromAccountCell(final String acctEndingNumber, final String accountName) {
		final StringBuilder formattedTitle = new StringBuilder();
		final String titleText = context.getString(R.string.pay_from_acct_ending_in);

		formattedTitle.append(titleText + " ");
		formattedTitle.append(acctEndingNumber);

		final ViewPagerListItem temp = getTwoItemCell(R.string.empty, accountName);
		temp.getTopLabel().setText(formattedTitle);
		return temp;
	}

	public ViewPagerListItem getStatusCell(final String status) {
		return getTwoItemCell(R.string.status, status);
	}

	public ViewPagerListItem getConfirmationCell(final String confirmationNumber) {
		return getTwoItemCell(R.string.confirmation_number, confirmationNumber);
	}

	public ViewPagerListItem getPayeeNameCell(final String payeeName) {
		return getTwoItemCell(R.string.payee_name, payeeName);
	}

	public ViewPagerListItem getPayeeNicknameCell(final String nickname) {
		return getTwoItemCell(R.string.nickname, nickname);
	}

	public ViewPagerListItem getAccountNumberCell(final String formattedAccountNumber) {
		return getTwoItemCell(R.string.account_number, formattedAccountNumber);
	}

	public ViewPagerListItem getPhoneNumberCell(final String phoneNumber) {
		return getTwoItemCell(R.string.phone_number, badPhoneNumberFormatter(unformatPhoneNumber(phoneNumber)));
	}

	// TEMP this may (and should) be replaced with the server formatting the phone numbers.
	private String badPhoneNumberFormatter(final String phoneNumber) {
		if(phoneNumber == null) {
			return "";
		} else if (phoneNumber.length() < 5) {
			return phoneNumber;
		} else {
			return phoneNumber.subSequence(0, 3) + "-" + badPhoneNumberFormatter(phoneNumber.substring(3));
		}
	}

	// TEMP should not be necessary if we get a formatted phone number from the server.
	private String unformatPhoneNumber(final String phoneNumber){
		//removes all characters that are not a number from a String.
		return phoneNumber.replaceAll("[^0-9]", "");
	}

	public ViewPagerListItem getAddressCell(final String address){
		return (address == null) ? getTwoItemCell(R.string.address, "") : getTwoItemCell(R.string.address, address);
	}

	public ViewPagerListItem getUnmanagedPayeeMemoCell(final String memo) {
		final ViewPagerListItem payeeMemo = getMemoItemCell(memo);
		payeeMemo.getTopLabel().setText(R.string.payee_memo);

		return payeeMemo;
	}

	/**
	 * Returns a list of ViewPagerListItems for a given Transaction from an ActivityDetail object.
	 * @param item an ActivityDetail object.
	 * @return a list of ViewPagerListItems for a given Transaction from an ActivityDetail object.
	 */
	public List<ViewPagerListItem> getDetailTransactionList(final ActivityDetail item){
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		items.add(getAmountCell(item.amount.value));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getDescriptionCell(item.description, item.id));
		items.add(getDateCell(item.dates.get(ActivityDetail.POSTED)));
		items.add(getBalanceCell(item.balance.value));

		return items;
	}

	/**
	 * Return a list of ViewPagerListItems that contain the appropriate information contained
	 * in the detail object that was passed to it
	 * @param item
	 * @return
	 */
	public List<ViewPagerListItem> getScheduledPaymentDetailList(final PaymentDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		final Account account = BankUser.instance().getAccount(item.paymentAccount.id);

		items.add(getPayeeCell(BankUser.instance().getPayees().getNameFromId(item.payee.id)));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getPayFromAccountCell(account.accountNumber.ending, account.nickname));
		items.add(getAmountCell(item.amount.value));
		items.add(getPaymentDateCell(item));
		if("SCHEDULED".equals(item.status)){
			items.add(getStatusCell(item.status));
		}
		items.add(getConfirmationCell(item.confirmationNumber));
		items.add(getMemoItemCell(item.memo));

		return items;
	}

	/**
	 * Get a payee detail list from a PayeeDetail object. Returns a list that is based on if the Payee
	 * is verified or not.
	 * @param item a PayeeDetail object.
	 * @return an appropritate list for a PayeeDetail object.
	 */
	public List<ViewPagerListItem> getPayeeDetailList(final PayeeDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();
		//verified payees have different content to show.
		items.add(getPayeeNameCell(item.name));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getPayeeNicknameCell(item.nickName));

		if(item.verified){
			items.add(getAccountNumberCell(item.account.formatted));
		}else{
			items.add(getPhoneNumberCell(item.phone.number));
			items.add(getAddressCell(item.address.formattedAddress));
			items.add(getUnmanagedPayeeMemoCell(item.memo));	
		}

		return items;
	}

	/**
	 * Returns a payment date cell based on a PaymentDetail object. It returns a date based on the kind of
	 * payment that was passed to it, like SCHEDULED, COMPLETED, or CANCELLED.
	 * @param item
	 * @return a ViewPagerListItem that contains a formatted date.
	 */
	public ViewPagerListItem getPaymentDateCell(final PaymentDetail item) {
		final String dates;
		final String itemStatus = item.status;
		ViewPagerListItem paymentDateItem = null;

		if("SCHEDULED".equals(itemStatus)){
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(convertDate(dates.split(PaymentDetail.DATE_DIVIDER)[0]));
		}else if("PAID".equals(itemStatus)){
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(convertDate(dates.split(PaymentDetail.DATE_DIVIDER)[0]));
			paymentDateItem.getTopLabel().setText(R.string.completed_pay_date);
		}else if ("CANCELLED".equals(itemStatus)){
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(convertDate(dates.split(PaymentDetail.DATE_DIVIDER)[0]));
			paymentDateItem.getTopLabel().setText(R.string.completed_pay_date);
		}

		return paymentDateItem;
	}

	/**
	 * Convert the date from the format dd/MM/yyyy to dd/MM/yy
	 * @param date - date to be converted
	 * @return the converted date
	 */
	private String convertDate(final String date){
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

		try{
			return tableFormat.format(serverFormat.parse(date));
		} catch (final ParseException e) {
			return date;
		}
	}

}
