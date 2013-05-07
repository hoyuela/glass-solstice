/*
 * ��� Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.atm.AtmMapFragment;
import com.discover.mobile.bank.atm.AtmModalFactory;
import com.discover.mobile.bank.deposit.BankDepositSelectAccount;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.help.HelpItemGenerator;

/**
 * Singleton class to put all of the help list items so that they are easily accessible to the fragment.
 * To use this just create a list of HelpItemGenerator items and return the list.  The search 
 * widget will then put all the items in the list into the widget. * 
 * 
 * @author jthornton
 *
 */
public final class HelpMenuListFactory {

	/**Default menu item representing the "All FAQ" item*/
	private final HelpItemGenerator allFaq;

	/**Instance of the factory*/
	private static HelpMenuListFactory factory;

	/**
	 * Private constructor, creates the default help menu items.
	 */
	private HelpMenuListFactory(){
		allFaq = new HelpItemGenerator(R.string.common_help_all_faq, true, true, getAllFaqListener());
	}

	/**
	 * Get an instance of the factory
	 * @return - an instance of the factory
	 */
	public static HelpMenuListFactory instance(){
		if(factory == null){
			factory = new HelpMenuListFactory();
		}
		return factory;
	}

	/**
	 * Get the menu items that are associated with the Login Page.
	 * 
	 * @return - the list of help menu items associated with the Login Page
	 */
	public List<HelpItemGenerator> getLoggedOutHelpItems(){
		/**Default menu item representing the "Bank ALL FAQ" item*/
		final HelpItemGenerator bankFaq = new HelpItemGenerator(R.string.help_bank_faq, true, true, getAllFaqListener());

		/**Default menu item representing the "Card ALL FAQ" item*/
		final HelpItemGenerator cardFaq = new HelpItemGenerator(R.string.help_card_faq, true, true, getCardFaqListener());

		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();

		items.add(cardFaq);
		items.add(bankFaq);
		return items;
	}

	/**
	 * Get the menu items that are associated with the account summary pages
	 * @return - the list of help menu items associated with the account summary pages
	 */
	public List<HelpItemGenerator> getCardHelpItems(){	
		/**Default menu item representing the "Card ALL FAQ" item*/
		final HelpItemGenerator cardFaq = new HelpItemGenerator(R.string.help_card_faq, true, true, getCardFaqListener());

		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();

		items.add(cardFaq);
		return items;
	}

	/**
	 * Get the menu items that are associated with the account summary pages
	 * @return - the list of help menu items associated with the account summary pages
	 */
	public List<HelpItemGenerator> getAccountHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		items.add(allFaq);
		return items;
	}


	/**
	 * Returns a list of items to be shown in the help dropdown menu during bank transfers.
	 * @return a list of items to be shown in the help dropdown menu during bank transfers.
	 */
	public List<HelpItemGenerator> getBankTransferHelpItems() {
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		items.add(allFaq);
		return items;
	}

	/**
	 * Get the menu items that are associated with the pay bills pages
	 * @return - the list of help menu items associated with the pay bills pages
	 */
	public List<HelpItemGenerator> getPayBillsHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		items.add(allFaq);
		return items;
	}

	/**
	 * Get the menu items that are associated with the check deposit pages
	 * @return - the list of help menu items associated with the check deposit pages
	 */
	public List<HelpItemGenerator> getCheckDepositHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		final HelpItemGenerator howItWorksModal = new HelpItemGenerator(R.string.check_deposit_help, false, true, 
				getHowItWorksModalListener());
		items.add(howItWorksModal);
		items.add(allFaq);
		return items;
	}

	/**
	 * Get the menu items that need to be added to the help menu when on ATM Locator
	 * @return the menu items for ATM locator
	 */
	public List<HelpItemGenerator> getAtmHelpItems(final AtmMapFragment fragment){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		final HelpItemGenerator atmHelp = 
				new HelpItemGenerator(R.string.help_menu_atm_help, false, true, getAtmHelpListener(fragment));
		items.add(atmHelp);
		items.add(allFaq);
		return items;
	}

	/**
	 * Show the ATM help modal if it needs to be shown
	 */
	public void showAtmHelpModal(final AtmMapFragment fragment) {
		final BaseFragmentActivity activity = (BaseFragmentActivity)DiscoverActivityManager.getActiveActivity();
		activity.showCustomAlert(AtmModalFactory.getAtmLocatorHelpModal(fragment));
	}

	/**
	 * Return the click listener for the atm help menu item
	 * @return the click listener for the atm help menu item
	 */
	private OnClickListener getAtmHelpListener(final AtmMapFragment fragment) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final BaseFragmentActivity activity = (BaseFragmentActivity)DiscoverActivityManager.getActiveActivity();
				activity.showCustomAlert(AtmModalFactory.getAtmLocatorHelpModal(fragment));
				fragment.setHelpModalShowing(true);
			}	
		};
	}

	/**
	 * Click listener for the all FAQ item.  On click the user will be directed to the all FAQ page
	 * @return Click listener for the all FAQ item.  On click the user will be directed to the all FAQ page
	 */
	private OnClickListener getCardFaqListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				FacadeFactory.getCardFaqFacade().launchCardFaq();
			}
		};
	}

	/**
	 * Click listener for the all FAQ item.  On click the user will be directed to the all FAQ page
	 * @return Click listener for the all FAQ item.  On click the user will be directed to the all FAQ page
	 */
	private OnClickListener getAllFaqListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				FacadeFactory.getBankFaqFacade().launchBankFaq();
			}
		};
	}

	/**
	 * Returns a click listener that will show the how it works modal onClick.
	 * @return a click listener that will show the how it works modal onClick.
	 */
	private OnClickListener getHowItWorksModalListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankDepositSelectAccount.showHowItWorksModal();
			}
		};
	}

}
