package com.discover.mobile.bank.help;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.utils.CommonUtils;

public class CustomerServiceContactsFragment extends BaseFragment {
	/** The LinearLayouts in the view that will have elements inserted into them. */
	private LinearLayout cardPhoneNumberList;
	private LinearLayout cardMailingAddressList;
	private LinearLayout bankMailingAddressList;
	private LinearLayout bankPhoneNumberList;

	/** Use this variable to setup the appearance of the screen based on card or bank user.*/
	private ContactUsType type = ContactUsType.CARD;
	private boolean card = false;

	/**
	 * Return the modified view that we need to display.
	 */
	@Override 
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.customer_service, null);

		loadViewsIn(mainView);
		loadLists(mainView);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) mainView.findViewById(R.id.help);
		switch( type ) {
		case ALL:
			help.showHelpItems(HelpMenuListFactory.instance().getLoggedOutHelpItems());
			final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)help.getLayoutParams();
			params.height = (int) this.getResources().getDimension(R.dimen.help_bar_height_three_items);
			help.setLayoutParams(params);
			break;
		case BANK:
			help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
			break;
		default:
			help.showHelpItems(HelpMenuListFactory.instance().getCardHelpItems());
			break;
		} 

		if(card){
			((BankLayoutFooter)mainView.findViewById(R.id.bank_footer)).setCardMode(card);
		}

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			// Must re-establish tiling for older devices.
			CommonUtils.fixBackgroundRepeat(mainView.findViewById(R.id.customer_service_layout));
		}

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
		final Bundle bundle = getArguments();

		if( bundle != null ) {
			type = (ContactUsType) bundle.getSerializable(BankInfoNavigationActivity.CONTACT_US);

			switch( type ) {
			case ALL:
				showCardElements(mainView);
				showBankElements(mainView);
				break;
			case BANK:
				showBankElements(mainView);
				hideCardElements(mainView);
				break;
			default:
				showCardElements(mainView);	
				hideBankElements(mainView);
				break;
			}

		} else {
			showCardElements(mainView);
			hideBankElements(mainView);
		}
	}

	/**
	 * Loads a list of View elements into a LinearLayout
	 * 
	 * @param layout a linear layout to insert view elements into
	 * @param elementList a list of view elements that can be inserted into a linear layout.
	 */
	public static void loadListElementsToLayoutFromList(final LinearLayout layout, final List<TwoElementListItem> elementList) {
		if(layout != null){
			for(final TwoElementListItem element : elementList){
				layout.addView(element);
			}
		}
	}

	private void showCardElements(final View mainView) {
		loadListElementsToLayoutFromList(cardPhoneNumberList, CustomerServiceContactLists.getCardPhoneNumberListElements(this.getActivity()));
		loadListElementsToLayoutFromList(cardMailingAddressList, CustomerServiceContactLists.getCardMailingAddressListElements(this.getActivity()));
	}

	private void showBankElements(final View mainView) {
		loadListElementsToLayoutFromList(bankPhoneNumberList, CustomerServiceContactLists.getBankPhoneNumberListElements(this.getActivity()));
		loadListElementsToLayoutFromList(bankMailingAddressList, CustomerServiceContactLists.getBankMailingAddressListElements(this.getActivity()));
	}

	/**
	 * Set the bank elements to be invisible.
	 * 
	 * @param mainView
	 */
	private void hideBankElements(final View mainView) {
		//Hide the "Discover Card" titles.
		CommonUtils.setViewInvisible(mainView.findViewById(R.id.card_phone_title_label));
		CommonUtils.setViewInvisible(mainView.findViewById(R.id.card_mail_title_label));

		//Hide bank mailing addresses and its title.
		CommonUtils.setViewGone(mainView.findViewById(R.id.bank_mail_title_label));
		CommonUtils.setViewGone(mainView.findViewById(R.id.bank_mail_address_list));

		//Hide bank phone numbers and its title.
		CommonUtils.setViewGone(mainView.findViewById(R.id.bank_phone_title_label));
		CommonUtils.setViewGone(mainView.findViewById(R.id.bank_phone_numbers_list));

	}

	/**
	 * Set the card elements to be invisible.
	 * 
	 * @param mainView
	 */
	private void hideCardElements(final View mainView) {
		//Hide the "Discover Bank" titles.
		CommonUtils.setViewInvisible(mainView.findViewById(R.id.bank_mail_title_label));
		CommonUtils.setViewInvisible(mainView.findViewById(R.id.bank_phone_title_label));

		CommonUtils.setViewGone(mainView.findViewById(R.id.card_mail_title_label));
		CommonUtils.setViewGone(mainView.findViewById(R.id.card_mail_address_list));

		//Hide bank phone numbers and its title.
		CommonUtils.setViewGone(mainView.findViewById(R.id.card_phone_title_label));
		CommonUtils.setViewGone(mainView.findViewById(R.id.card_phone_numbers_list));
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.CONTACT_US_SECTION;
	}

	public void setCardMode(final boolean isCard) {
		card = isCard;
	}

}
