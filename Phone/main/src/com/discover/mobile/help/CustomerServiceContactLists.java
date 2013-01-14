package com.discover.mobile.help;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
/**
 * This class is responsible for returning a list of contact elements to be used in an Android layout.
 * It can return lists of phone numbers that will dial their number on click, or just a list of addresses.
 * 
 * @author scottseward
 *
 */
public class CustomerServiceContactLists {
	
	/**
	 * Easy handles for the calling context and its resources.
	 */
	private static Resources res;
	private static Context context;
	
	/**
	 * This is a static method that returns a list of telephone contacts that are in the form of RelativeLayout
	 * elements. They contain two labels, one that describes the phone number and one that is the phone number
	 * the phone number is clickable and initiates a telephone dial.
	 * 
	 * @param callingContext - a reference to the context that calls this method.
	 * @return - A TwoElementListItem List containing these contact View elements.
	 */
	public static List<TwoElementListItem> getCardPhoneNumberListElements(final Context callingContext) {
		context = callingContext;
		res = context.getResources();
		
		List<TwoElementListItem> phoneContactList = new ArrayList<TwoElementListItem>();
		
		phoneContactList.add(getTwoElementListItemWithText(R.string.inside_us_number, R.string.card_phone_us, true));
		phoneContactList.get(0).getDividerLine().setVisibility(View.GONE);
		phoneContactList.add(getTwoElementListItemWithText(R.string.outside_us_number, R.string.card_phone_non_us, true));
		phoneContactList.add(getTwoElementListItemWithText(R.string.tdd_number, R.string.card_phone_tdd, true));
		
		return phoneContactList;
	}
	
	/**
	 * This is a static method that returns a list of mailing address contacts that are in the form of RelativeLayout
	 * elements. They contain two labels, one that describes the address and one that is the address
	 * 
	 * @param callingContext - a reference to the context that calls this method.
	 * @return - A TwoElementListItem List containing these address View elements.
	 */
	public static List<TwoElementListItem> getCardMailingAddressListElements(final Context callingContext) {
		context = callingContext;
		res = context.getResources();
		
		List<TwoElementListItem> mailingAddressList = new ArrayList<TwoElementListItem>();
		
		mailingAddressList.add(getTwoElementListItemWithText(R.string.payment_address, R.string.card_mailing_payment, false));
		mailingAddressList.get(0).getDividerLine().setVisibility(View.GONE);
		mailingAddressList.add(getTwoElementListItemWithText(R.string.customer_service_two_line, R.string.card_mailing_customer_service, false));
		
		return mailingAddressList;
	}
	
	/**
	 * This is a static method that returns a list of mailing address contacts that are for Discover Banking
	 * They are in the form of RelativeLayout elements. 
	 * They contain two labels, one that describes the address and one that is the address.
	 * 
	 * @param callingContext - a reference to the context that calls this method.
	 * @return - A TwoElementListItem List containing these address View elements.
	 */
	public static List<TwoElementListItem> getBankMailingAddressListElements(final Context callingContext) {
		context = callingContext;
		res = context.getResources();
		
		List<TwoElementListItem> mailingAddressList = new ArrayList<TwoElementListItem>();

		mailingAddressList.add(getTwoElementListItemWithText(R.string.bank_general_mail_title, R.string.bank_general_mail, false));
		mailingAddressList.get(0).getDividerLine().setVisibility(View.GONE);
		mailingAddressList.add(getTwoElementListItemWithText(R.string.bank_new_accounts_mail_title, R.string.bank_new_accounts_mail, false));
		
		return mailingAddressList;
		
	}
	
	/**
	 * This is a static method that returns a list of telephone contacts that are in the form of RelativeLayout
	 * elements. They contain two labels, one that describes the phone number and one that is the phone number
	 * the phone number is clickable and initiates a telephone dial.
	 * 
	 * @param callingContext - a reference to the context that calls this method.
	 * @return - A TwoElementListItem List containing these contact View elements.
	 */
	public static List<TwoElementListItem> getBankPhoneNumberListElements(final Context callingContext) {
		context = callingContext;
		res = context.getResources();
		
		List<TwoElementListItem> phoneContactList = new ArrayList<TwoElementListItem>();
		
		phoneContactList.add(getTwoElementListItemWithText(R.string.open_an_account, R.string.bank_phone_open_account, true));
		phoneContactList.get(0).getDividerLine().setVisibility(View.GONE);
		phoneContactList.add(getTwoElementListItemWithText(R.string.bank_tech_support, R.string.bank_phone_tech_support, true));
		phoneContactList.add(getTwoElementListItemWithText(R.string.tdd_number, R.string.bank_phone_tdd, true));
		phoneContactList.add(getTwoElementListItemWithText(R.string.bank_outside_usa, R.string.bank_outside_usa, true));

		return phoneContactList;
	}
	
	/**
	 * The method that is used to assemble lists of phone number or mailing address View elements to be inserted
	 * into a linear layout in an Android GUI.
	 * 
	 * @param leftText - A String resource that will be used as the text of the left text label.
	 * @param rightText - A String resource that will be used as the text of the right text label.
	 * @param isPhoneNumber - Set to true if the element will contain a phone number. This adjusts appearance and adds an OnClickListener to the number so that it will dial its number.
	 * @return a newly created TwoElementListItem with the provided text values and apperance based on if it was a phone number or not.
	 */
	private static TwoElementListItem getTwoElementListItemWithText(final int leftText, final int rightText, final boolean isPhoneNumber) {
		TwoElementListItem newItem = new TwoElementListItem(context);
		
		newItem.setLeftText(res.getString(leftText));
		newItem.setRightText(res.getString(rightText));
		
		//Adjusts the appearance and click listener based on if this is a phone number or not.
		if(isPhoneNumber) {
			setToDialNumberOnClick(newItem.getRightTextView());
			newItem.getRightTextView().setTextAppearance(context, R.style.blue_hyperlink_smallest);
		}
		//No click listener, and change the appearance.
		else {
			newItem.getRightTextView().setTextAppearance(context, R.style.smallest_copy);
		}
		
		return newItem;
	}
	
	/**
	 * Set an OnClickListener to this text view, 
	 * this will initate a call to the number that is attached to it.
	 */
	private static void setToDialNumberOnClick(final TextView contactNumber) {
		contactNumber.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(contactNumber.getText().toString(), context);
			}
		});
	}
	
}
