package com.discover.mobile.help;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.error.ErrorHandlerFactory;
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
	
	/**
	 * Get the lists of elements that go into the contacts tables and insert them into the tables.
	 */
	private void loadLists() {
		loadListElementsToLayoutFromList(cardPhoneNumberList, CustomerServiceContactLists.getCardPhoneNumberListElements(this));
		loadListElementsToLayoutFromList(bankPhoneNumberList, CustomerServiceContactLists.getBankPhoneNumberListElements(this));

		loadListElementsToLayoutFromList(cardMailingAddressList, CustomerServiceContactLists.getCardMailingAddressListElements(this));
		loadListElementsToLayoutFromList(bankMailingAddressList, CustomerServiceContactLists.getBankMailingAddressListElements(this));
	}
	
	/**
	 * Loads a list of View elements into a LinearLayout
	 * 
	 * @param layout a linear layout to insert view elements into
	 * @param elementList a list of view elements that can be inserted into a linear layout.
	 */
	public static void loadListElementsToLayoutFromList(final LinearLayout layout, List<TwoElementListItem> elementList) {
		if(layout != null){
			for(TwoElementListItem element : elementList)
				layout.addView(element);
		}
	}

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showCustomAlert(AlertDialog alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorCode, int titleText, int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
