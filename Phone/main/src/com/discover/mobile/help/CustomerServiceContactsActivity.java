package com.discover.mobile.help;

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
	 * The two LinearLayouts in the view that will have elements inserted into them.
	 */
	private LinearLayout phoneNumberList;
	private LinearLayout mailingAddressList;
	
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
		loadCardPhoneNumbers();
		loadCardMailingAddresses();
	}
	
	/**
	 * Get references to all of the views in the layout that we may want to modify.
	 */
	private void loadViews() {
		phoneNumberList = (LinearLayout)findViewById(R.id.phone_numbers_list);
		mailingAddressList = (LinearLayout)findViewById(R.id.mailing_address_list);
	}
	
	/**
	 * Retrieve a list of TwoElementListItem-s that we can use to display in the UI. The returned elements are added
	 * to the phoneNumberList.
	 */
	private void loadCardPhoneNumbers() {
		if(phoneNumberList != null){
			for(TwoElementListItem element : CustomerServiceContactLists.getCardPhoneNumberListElements(this))
				phoneNumberList.addView(element);
		}
	}
	
	/**
	 * Retrieve a list of TwoElementListItem-s that we can use to display in the UI. The returned elements are added
	 * to the mailingAddressList.
	 */
	private void loadCardMailingAddresses() {
		if(mailingAddressList != null) {
			for(TwoElementListItem element : CustomerServiceContactLists.getCardMailingAddressListElements(this))
				mailingAddressList.addView(element);
		}
	}
	
}
