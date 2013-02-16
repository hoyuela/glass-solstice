package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.List;

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
		String memoText;
		if(memo == null)
			memoText = "";
		else
			memoText = memo;

		listItem = new ViewPagerListItem(this.context);
		getTwoItemCell(R.string.memo, memoText);
		listItem.getMiddleLabel().setTextAppearance(context, R.style.sub_copy_big);
		
		return listItem;
	}
	
	public ViewPagerListItem getDescriptionCell(final String description, final String transId) {
		return getThreeItemCell(R.string.description, description, transId);
	}
	
	public ViewPagerListItem getBalanceCell(final int balance) {
		return getTwoItemCell(R.string.balance, BankStringFormatter.convertCentsToDollars(balance));
	}
	
	public ViewPagerListItem getDateCell(final String formattedDate) {
		return getTwoItemCell(R.string.date, formattedDate);
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
	
	public List<ViewPagerListItem> getDetailTransactionList(final ActivityDetail item){
		final List<ViewPagerListItem> items = new ArrayList<ViewPagerListItem>();

		items.add(getAmountCell(item.amount));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getDescriptionCell(item.description, item.id));
		items.add(getDateCell(item.dates.formattedDate));
		items.add(getBalanceCell(item.balance));

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
	
		items.add(getPayeeCell(item.payee.name));
		items.get(0).getDividerLine().setVisibility(View.GONE);
		items.add(getPayFromAccountCell(item.paymentAccount.ending, item.paymentAccount.nickName));
		items.add(getAmountCell(item.amount));
		items.add(getPaymentDateCell(item));
		items.add(getStatusCell(item.status));
		items.add(getConfirmationCell(item.confirmationNumber));
		items.add(getMemoItemCell(item.memo));
		
		return items;
	}
	
	public ViewPagerListItem getPaymentDateCell(final PaymentDetail item) {
		final PaymentDateDetail dates;
		final String itemStatus = item.status;
		ViewPagerListItem paymentDateItem = null;
		
		if("SCHEDULED".equals(itemStatus)){
			dates = item.dates.get("deliverBy");
			paymentDateItem = getDeliverByCell(dates.formattedDate);
		}else if("COMPLETED".equals(itemStatus)){
			dates = item.dates.get("deliveredOn");
			paymentDateItem = getDeliveredOnCell(dates.formattedDate);
		}
		
		return paymentDateItem;

	}
	
}
