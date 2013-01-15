package com.discover.mobile.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;

public class CustomerServiceContactsFragment extends BaseFragment {
	/** The LinearLayouts in the view that will have elements inserted into them. */
	private LinearLayout cardPhoneNumberList;
	private LinearLayout cardMailingAddressList;
	private LinearLayout bankMailingAddressList;
	private LinearLayout bankPhoneNumberList;
	
	/** Use this variable to setup the appearance of the screen based on card or bank user.*/
	private boolean isCardUser = true;
	
	/**
	 * Return the modified view that we need to display.
	 */
	@Override 
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.customer_service, null);
		
		loadViewsIn(mainView);
		loadLists(mainView);
		
		return mainView;
	}
	
	/**
	 * The title of the screen that will be presented in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.contact_us;
	}
	
	/**
	 * Initialize the local variables to the views that we will need to modify.
	 * 
	 * @param mainView
	 */
	private void loadViewsIn(final View mainView) {
		cardPhoneNumberList = (LinearLayout)mainView.findViewById(R.id.card_phone_numbers_list);
		cardMailingAddressList = (LinearLayout)mainView.findViewById(R.id.card_mail_address_list);
		
		bankPhoneNumberList = (LinearLayout)mainView.findViewById(R.id.bank_phone_numbers_list);
		bankMailingAddressList = (LinearLayout)mainView.findViewById(R.id.bank_mail_address_list);
	}
	
	/**
	 * Get the lists of elements that go into the contacts tables and insert them into the tables.
	 */
	private void loadLists(final View mainView) {
		if(isCardUser){
			CustomerServiceContactsActivity.loadListElementsToLayoutFromList(cardPhoneNumberList, CustomerServiceContactLists.getCardPhoneNumberListElements(this.getActivity()));
			CustomerServiceContactsActivity.loadListElementsToLayoutFromList(cardMailingAddressList, CustomerServiceContactLists.getCardMailingAddressListElements(this.getActivity()));
			hideBankElements(mainView);
		}
		else{
			CustomerServiceContactsActivity.loadListElementsToLayoutFromList(bankPhoneNumberList, CustomerServiceContactLists.getBankPhoneNumberListElements(this.getActivity()));
			CustomerServiceContactsActivity.loadListElementsToLayoutFromList(bankMailingAddressList, CustomerServiceContactLists.getBankMailingAddressListElements(this.getActivity()));
			hideCardElements(mainView);
		}
	}
	
	/**
	 * Set the bank elements to be invisible.
	 * 
	 * @param mainView
	 */
	private void hideBankElements(final View mainView) {
		//Hide the "Discover Card" titles.
		CommonMethods.setViewInvisible((TextView)mainView.findViewById(R.id.card_phone_title_label));
		CommonMethods.setViewInvisible((TextView)mainView.findViewById(R.id.card_mail_title_label));

		//Hide bank mailing addresses and its title.
		CommonMethods.setViewGone((TextView)mainView.findViewById(R.id.bank_mail_title_label));
		CommonMethods.setViewGone((LinearLayout)mainView.findViewById(R.id.bank_mail_address_list));

		//Hide bank phone numbers and its title.
		CommonMethods.setViewGone((TextView)mainView.findViewById(R.id.bank_phone_title_label));
		CommonMethods.setViewGone((LinearLayout)mainView.findViewById(R.id.bank_phone_numbers_list));

	}
	
	/**
	 * Set the card elements to be invisible.
	 * 
	 * @param mainView
	 */
	private void hideCardElements(final View mainView) {
		//Hide the "Discover Bank" titles.
		CommonMethods.setViewInvisible((TextView)mainView.findViewById(R.id.bank_mail_title_label));
		CommonMethods.setViewInvisible((TextView)mainView.findViewById(R.id.bank_phone_title_label));
		
		CommonMethods.setViewGone((TextView)mainView.findViewById(R.id.card_mail_title_label));
		CommonMethods.setViewGone((LinearLayout)mainView.findViewById(R.id.card_mail_address_list));

		//Hide bank phone numbers and its title.
		CommonMethods.setViewGone((TextView)mainView.findViewById(R.id.card_phone_title_label));
		CommonMethods.setViewGone((LinearLayout)mainView.findViewById(R.id.card_phone_numbers_list));
	}
	

}
