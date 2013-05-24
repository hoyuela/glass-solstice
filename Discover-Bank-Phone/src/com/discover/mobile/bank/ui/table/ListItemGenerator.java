package com.discover.mobile.bank.ui.table;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

/**
 * This detail item class is able to return the individual table cells to present
 * inside of a table view style interface element.
 * It is most commonly used in the ViewPager, confirmation screens, and other custom, not very scrollable lists.
 * 
 * @author scottseward
 *
 */
public class ListItemGenerator { 
	/** Minimum phone number String length required to manually format.*/
	private static final int PHONE_LENGTH_MIN = 5;
	/** Index where the phone number dash "-" should be inserted. */
	private static final int PHONE_DASH_INDEX = 3;

	private static final String STATUS_PAID = "PAID";
	private static final String STATUS_CANCELLED = "CANCELLED";

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

	/** Same as a Two Item Cell but with a decreased text size for the middle label. */
	public ViewPagerListItem getSmallTwoItemCell(final int topLabelResource, final String middleLabelText) {
		initNewListItem();
		if(middleLabelText != null) {
			listItem.getTopLabel().setText(topLabelResource);
			listItem.getMiddleLabel().setVisibility(View.GONE);
			// Allows the bottom label to "mimic" the middle label but keeps the smaller text size
			listItem.getBottomLabel().setText(middleLabelText);
			listItem.getBottomLabel().setTypeface(Typeface.DEFAULT_BOLD);
			listItem.getBottomLabel().setTextColor(listItem.getMiddleLabel().getTextColors());
		}
		return listItem;
	}

	public ViewPagerListItem getTwoItemImageCell(final int topLabelResource, final String middleLabelText) {
		final ViewPagerListItemWithImage listItemWithImage = new ViewPagerListItemWithImage(context);

		if(middleLabelText != null) {
			listItemWithImage.getTopLabel().setText(topLabelResource);
			listItemWithImage.getMiddleLabel().setText(middleLabelText);
			listItemWithImage.getBottomLabel().setVisibility(View.GONE);
		}
		return listItemWithImage;
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

	public ViewPagerListItem getDateCell(final String date) {
		return getTwoItemCell(R.string.date, BankStringFormatter.getFormattedDate(date));
	}

	public ViewPagerListItem getAmountCell(final String amount) {
		return getTwoItemCell(R.string.amount, BankStringFormatter.convertCentsToDollars(amount));
	}

	public ViewPagerListItem getAmountCell(final int amount) {
		return getTwoItemCell(R.string.amount, BankStringFormatter.convertCentsToDollars(amount));
	}

	public ViewPagerListItem getFromCell(final String from, final String balance) {
		return getThreeItemCell(R.string.from, from, 
				context.getString(R.string.available_balance) + StringUtility.SPACE + balance);
	}

	public ViewPagerListItem getFromCell(final String from) {
		return getTwoItemCell(R.string.from, from);
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
		ViewPagerListItem item = null;
		final boolean isOneTimeTransfer = TransferDetail.ONE_TIME_TRANSFER.equalsIgnoreCase(frequency) ||
				context.getResources().getString(R.string.one_time).equalsIgnoreCase(frequency);

		if(isOneTimeTransfer){
			item = getTwoItemCell(R.string.frequency, frequency);
		}else{
			item = getTwoItemImageCell(R.string.frequency, frequency);
		}

		return item;
	}

	public ViewPagerListItem getPayeeCell(final String payee) {
		return getTwoItemCell(R.string.payee, payee);
	}

	public ViewPagerListItem getPayFromAccountCell(final String acctEndingNumber, final String accountName) {
		final StringBuilder formattedTitle = new StringBuilder();
		final String titleText = context.getString(R.string.pay_from_acct_ending_in);

		formattedTitle.append(titleText);
		formattedTitle.append(StringUtility.SPACE);
		formattedTitle.append(acctEndingNumber);

		final ViewPagerListItem temp = getTwoItemCell(R.string.empty, accountName);
		temp.getTopLabel().setText(formattedTitle);
		return temp;
	}

	public ViewPagerListItem getReferenceNumberCell(final String referenceNumber) {
		return getTwoItemCell(R.string.reference_number, StringUtility.HASH + referenceNumber);
	}

	public ViewPagerListItem getStatusCell(final String status) {
		return getTwoItemCell(R.string.status, status);
	}

	public ViewPagerListItem getConfirmationCell(final String confirmationNumber) {
		return getTwoItemCell(R.string.confirmation_number, confirmationNumber);
	}

	public ViewPagerListItem getLongConfirmationCell(final String confirmationNumber) {
		return getSmallTwoItemCell(R.string.confirmation_number, confirmationNumber);
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

	public ViewPagerListItem getFrequencyDurationCell(final String frequencyDuration) {
		final ViewPagerListItem item = getTwoItemCell(R.string.duration, frequencyDuration);
		item.getMiddleLabel().setSingleLine(false);

		return item;
	}

	// TEMP this may (and should) be replaced with the server formatting the phone numbers.
	private String badPhoneNumberFormatter(final String phoneNumber) {
		if(phoneNumber == null) {
			return "";
		} else if (phoneNumber.length() < PHONE_LENGTH_MIN) {
			return phoneNumber;
		} else {
			return phoneNumber.subSequence(0, PHONE_DASH_INDEX) + StringUtility.DASH 
					+ badPhoneNumberFormatter(phoneNumber.substring(PHONE_DASH_INDEX));
		}
	}

	// TEMP should not be necessary if we get a formatted phone number from the server.
	private String unformatPhoneNumber(final String phoneNumber){
		//removes all characters that are not a number from a String.
		return phoneNumber.replaceAll(StringUtility.NON_NUMBER_CHARACTERS, StringUtility.EMPTY);
	}

	public ViewPagerListItem getAddressCell(final String address){
		ViewPagerListItem item;
		if (address == null) {
			item = getTwoItemCell(R.string.address, StringUtility.EMPTY);
		} else {
			item = getTwoItemCell(R.string.address, address);
		}
		/**Need Multi-line for Address field*/
		item.getMiddleLabel().setSingleLine(false);
		return item;
	}

	public ViewPagerListItem getUnmanagedPayeeMemoCell(final String memo) {
		final ViewPagerListItem payeeMemo = getTwoItemCell(R.string.payee_memo, memo);
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
		items.add(getDescriptionCell(item.description, item.id));
		items.add(getDateCell(item.postedDate));
		items.add(getBalanceCell(item.balance.value));

		hideDivider(items);
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

		String payeeName = item.payee.nickName;	
		if( Strings.isNullOrEmpty(payeeName)) {
			payeeName = item.payee.nickName;
		}

		items.add(getPayeeCell(payeeName));
		items.add(getPayFromAccountCell(account.accountNumber.ending, account.nickname));
		items.add(getAmountCell(item.amount.value));
		items.add(getPaymentDateCell(item));
		if(!Strings.isNullOrEmpty(item.status)){
			items.add(getStatusCell( BankStringFormatter.capitalize(item.status)));
		}
		items.add(getConfirmationCell(item.confirmationNumber));

		hideDivider(items);
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
		items.add(getPayeeNicknameCell(item.nickName));

		if(item.verified){
			items.add(getAccountNumberCell(item.account.formatted));
		}else{
			items.add(getPhoneNumberCell(item.phone.number));
			items.add(getAddressCell(item.address.formattedAddress));

			/** only show memo field if it is provided */
			if (!Strings.isNullOrEmpty(item.memo)) {
				items.add(getUnmanagedPayeeMemoCell(item.memo));
			}
		}

		hideDivider(items);
		return items;
	}

	/**
	 * Returns a table cell to be shown in a table on screen.
	 * @param transferAccount the Account object that contains information to show on screen.
	 * @param isFromCell if the Account to be shown is a 'from' account instead of a 'to' account
	 * @return a ViewPagerListItem that can be inserted into a layout at runtime.
	 */
	public ViewPagerListItem getTransferAccountCell(final Account transferAccount, final boolean isFromCell) {
		final ViewPagerListItem item = getTwoItemCell(R.string.empty, "");
		final StringBuilder builder = new StringBuilder();
		int direction = 0;

		direction = (isFromCell) ? R.string.from : R.string.to;

		builder.append(context.getString(direction));
		builder.append(StringUtility.SPACE);
		builder.append(context.getString(R.string.account_ending_in_first_two_caps));
		builder.append(StringUtility.SPACE);
		builder.append(transferAccount.accountNumber.ending);

		item.getTopLabel().setText(builder.toString());
		item.getMiddleLabel().setText(transferAccount.nickname);
		item.getMiddleLabel().setSingleLine(false);
		item.getMiddleLabel().setMaxLines(2);

		return item;
	}

	/**
	 * Return a list of items that will be shown on the transfer confirmation page.
	 * This list shows different information based on the type of information provided in the 
	 * TransferDetail parameter object.
	 * @param results a TransferDetail object which contains information that needs to be presented on screen.
	 * @return a List of items which can be inserted into a layout at runtine.
	 */
	public List<ViewPagerListItem> getTransferConfirmationList(final TransferDetail results) {
		final List<ViewPagerListItem> list = new ArrayList<ViewPagerListItem>();

		if( results != null ) {
			list.add(getTransferAccountCell(results.fromAccount, true));
			list.add(getTransferAccountCell(results.toAccount, false));
			list.add(getAmountCell(results.amount.value));
			list.add(getSendOnCell(BankStringFormatter.getFormattedDate(results.sendDate)));
			list.add(getDeliverByCell(BankStringFormatter.getFormattedDate(results.deliverBy)));
			list.add(getFrequencyCell(results.getFormattedFrequency(context)));
			list.add(getReferenceNumberCell(results.id));
		}

		return list;
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

		if(STATUS_PAID.equalsIgnoreCase(itemStatus)){
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(BankStringFormatter.getFormattedDate(dates));
			paymentDateItem.getTopLabel().setText(R.string.completed_pay_date);
		}else if (STATUS_CANCELLED.equalsIgnoreCase(itemStatus)){
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(BankStringFormatter.getFormattedDate(dates));
			paymentDateItem.getTopLabel().setText(R.string.schedule_pay_date);
		}else {
			dates = item.deliverBy;
			paymentDateItem = getDeliverByCell(BankStringFormatter.getFormattedDate(dates));
		}

		return paymentDateItem;
	}

	/**
	 * Generates a list of items that can be inserted into a layout at runtime.
	 * 
	 * @param item an ActivityDetail item that contains information related to a schedueld transfer.
	 * @return a list of items which display the information in the ActivityDetail item.
	 */
	public List<ViewPagerListItem> getScheduledBillPayList(final ActivityDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		items.add(getFromCell(item.paymentMethod.nickname));	
		items.add(getPayeeCell(item.payee.nickName));
		items.add(getAmountCell(BankStringFormatter.convertCentsToDollars(item.amount.value)));
		items.add(getStatusCell(BankStringFormatter.capitalize(item.status)));
		items.add(getDeliverByCell(BankStringFormatter.getFormattedDate(item.deliverByDate)));

		// Using transaction id for the confirmation number. Using specialized Confirmation cell to fit the long id.
		if(Strings.isNullOrEmpty(item.confirmationNumber)){
			items.add(getLongConfirmationCell(item.id));
		}else{
			items.add(getLongConfirmationCell(item.confirmationNumber));
		}

		hideDivider(items);
		return items;
	}

	/**
	 * Generates a list of items that can be inserted into a linear layout.
	 * @param item an ActivityDetail item that contains information related to a schedueld transfer.
	 * @return a list of items which display the information in the ActivityDetail item.
	 */
	public List<ViewPagerListItem> getScheduledTransferList(final ActivityDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		if(item.fromAccount != null){
			items.add(getFromCell(item.fromAccount.nickname));
		}

		items.add(getToCell(item.toAccount.nickname));
		items.add(getAmountCell(item.amount.formatted));
		items.add(getSendOnCell(BankStringFormatter.getFormattedDate(item.sendDate)));
		items.add(getDeliverByCell(BankStringFormatter.getFormattedDate(item.deliverBy)));
		items.add(getFrequencyCell(TransferDetail.getFormattedFrequency(context, item.frequency)));


		if( !Strings.isNullOrEmpty(item.durationType) && 
				!Strings.isNullOrEmpty(item.frequency) &&
				!item.frequency.equalsIgnoreCase(TransferDetail.ONE_TIME_TRANSFER)){
			items.add(getFrequencyDurationCell(
					TransferDetail.getFormattedDuration(context, item.durationType)));
		}

		hideDivider(items);
		return items;
	}

	/**
	 * Generates a list of items that can be inserted into a layout at runtime.
	 * 
	 * @param item an ActivityDetail item that contains information related to a schedueld transfer.
	 * @return a list of items which display the information in the ActivityDetail item.
	 */
	public List<ViewPagerListItem> getScheduledDepositList(final ActivityDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		items.add(getAmountCell(item.amount.formatted));
		items.add(getDateCell(item.getTableDisplayDate()));

		hideDivider(items);
		return items;
	}

	private void hideDivider(final List<ViewPagerListItem> items) {
		if((items != null) && (items.size() > 0)){
			items.get(0).getDividerLine().setVisibility(View.GONE);
		}
	}

}
