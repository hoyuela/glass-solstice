package com.discover.mobile.bank.account;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.payment.PaymentDateDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;

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
		listItem = new ViewPagerListItem(this.context);
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
		getTwoItemCell(R.string.memo, memo);
		listItem.getMiddleLabel().setTextAppearance(context, R.style.field_copy);
		
		return listItem;
	}
	
	public ViewPagerListItem getDescriptionCell(final String description, final String transId) {
		return getThreeItemCell(R.string.description, description, transId);
	}
	
	public ViewPagerListItem getBalanceCell(final int balance) {
		return getTwoItemCell(R.string.balance, convertCentsToDollars(balance));
	}
	
	public ViewPagerListItem getDateCell(final String formattedDate) {
		return getTwoItemCell(R.string.date, formattedDate);
	}
	
	public ViewPagerListItem getAmountCell(final String amount) {
		return getTwoItemCell(R.string.amount, convertCentsToDollars(amount));
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
	
	public ViewPagerListItem getFrequencyCell(final String frequency) {
		return getTwoItemCell(R.string.frequency, frequency);
	}
	
	public ViewPagerListItem getPayeeCell(final String payee) {
		return getTwoItemCell(R.string.payee, payee);
	}
	
	public ViewPagerListItem getPayFromAccountCell(final String acctEndingNumber, final String accountName) {
		final String formattedTitle = 
				String.format(this.context.getString(R.string.pay_from_acct_ending_in, acctEndingNumber));
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
	
	/**
	 * Convert the string amount to a dollar amount
	 * @param cents - dollar amount
	 * @return the dollar amount in string form
	 */
	private String convertCentsToDollars(final String cents){
		if(null != cents){
			double amount = Double.parseDouble(cents)/100;
			final StringBuilder formattedString = new StringBuilder();

			if(amount < 0){
				amount *= -1;
				formattedString.append("-");
			}
			
			formattedString.append(NumberFormat.getCurrencyInstance(Locale.US).format(amount));
			
			return formattedString.toString();
			
		} else{
			return "$0.00";
		}
	}
	/**
	 * Convert the string amount to a dollar amount
	 * @param cents - dollar amount
	 * @return the dollar amount in string form
	 */
	//TODO: Move to common methods class.
	private String convertCentsToDollars(final int cents){
			double amount = (double)cents/100;
			final StringBuilder formattedString = new StringBuilder();

			//If negative, make positive
			if(amount < 0){
				amount *= -1;
				formattedString.append("-");
			}
			
			formattedString.append(NumberFormat.getCurrencyInstance(Locale.US).format(amount));

			return formattedString.toString();
	}
	
	public List<ViewPagerListItem> getDetailTransactionList(final ActivityDetail item){
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		items.add(getAmountCell(item.amount));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getDescriptionCell(item.description, item.id));
		items.add(getDateCell(item.dates.formattedDate));
		items.add(getBalanceCell(item.balance));

		return items;
	}
	
	public List<ViewPagerListItem> getScheduledPaymentDetailList(final PaymentDetail item) {
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();
	
		items.add(getPayeeCell(item.payee.name));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getPayFromAccountCell(item.paymentAccount.ending, item.paymentAccount.nickName));
		items.add(getAmountCell("" + item.amount));
		final PaymentDateDetail dates = item.dates.get("deliverBy");
		items.add(getDeliverByCell(dates.formattedDate));
		items.add(getStatusCell(item.status));
		items.add(getConfirmationCell(item.confirmationNumber));
		items.add(getMemoItemCell(item.memo));
		
		return items;
	}
	
}
