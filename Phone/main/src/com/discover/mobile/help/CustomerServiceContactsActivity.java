package com.discover.mobile.help;

import java.util.List;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
/**
 * The activity which handles the logged out view of contact customer service.
 * This page is constructed from phone number and address contacts in two table like linear layouts. 
 * It contains clickable phone numbers that initiate phone numbers to be dialed by the device.
 * 
 * @author scottseward
 *
 */
public class CustomerServiceContactsActivity extends NotLoggedInRoboActivity {

	/**
	 * The LinearLayouts in the view that will have elements inserted into them.
	 */
	private LinearLayout cardPhoneNumberList;
	private LinearLayout cardMailingAddressList;
	private LinearLayout bankMailingAddressList;
	private LinearLayout bankPhoneNumberList;
	
	/**
	 * Finish the activity upon the back button in the action bar being pressed.
	 */
	@Override
	public void goBack() {
		finish();
	}

	/**
	 * Setup and load the view for presentation.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_service);
		
		loadViews();
		loadLists();
		super.setActionBarTitle(R.string.contact_us);
	}
	
	/**
	 * Get references to all of the views in the layout that we may want to modify.
	 */
	private void loadViews() {
		cardPhoneNumberList = (LinearLayout)findViewById(R.id.card_phone_numbers_list);
		cardMailingAddressList = (LinearLayout)findViewById(R.id.card_mail_address_list);
		
		bankPhoneNumberList = (LinearLayout)findViewById(R.id.bank_phone_numbers_list);
		bankMailingAddressList = (LinearLayout)findViewById(R.id.bank_mail_address_list);
	}
	
	private void loadLists() {
		loadListElementsToLayoutFromList(cardPhoneNumberList, CustomerServiceContactLists.getCardPhoneNumberListElements(this));
		loadListElementsToLayoutFromList(bankPhoneNumberList, CustomerServiceContactLists.getBankPhoneNumberListElements(this));

		loadListElementsToLayoutFromList(cardMailingAddressList, CustomerServiceContactLists.getCardMailingAddressListElements(this));
		loadListElementsToLayoutFromList(bankMailingAddressList, CustomerServiceContactLists.getBankMailingAddressListElements(this));
	}
	
	private void loadListElementsToLayoutFromList(final LinearLayout layout, List<TwoElementListItem> elementList) {
		if(layout != null){
			for(TwoElementListItem element : elementList)
				layout.addView(element);
		}
	}
	
}
