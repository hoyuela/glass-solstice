/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.atm.AtmModalFactory;
import com.discover.mobile.bank.deposit.BankDepositSelectAccount;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
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

	/**Pay Bills Menu item*/
	private final HelpItemGenerator paybills;

	/**Check Deposit Menu Item*/
	private final HelpItemGenerator checkDeposit;

	/**Instance of the factory*/
	private static HelpMenuListFactory factory;

	/**
	 * Private constructor, creates the default help menu items.
	 */
	private HelpMenuListFactory(){
		allFaq = new HelpItemGenerator(R.string.help_all_faq, true, false, getAllFaqListener());
		paybills = new HelpItemGenerator(R.string.pay_bills_help, false, false, 
													getDefaultClickListener(BankExtraKeys.BILL_PAY_FAQ));
		checkDeposit = new HelpItemGenerator(R.string.check_deposit_faq, false, false, 
													getDefaultClickListener(BankExtraKeys.CHECK_DEPOSIT_FAQ));
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
		items.add(paybills);
		items.add(allFaq);
		return items;
	}

	/**
	 * Get the menu items that are associated with the check deposit pages
	 * @return - the list of help menu items associated with the check deposit pages
	 */
	public List<HelpItemGenerator> getCheckDepositHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		final HelpItemGenerator howItWorksModal = new HelpItemGenerator(R.string.check_deposit_help, false, false, 
																				getHowItWorksModalListener());
		items.add(howItWorksModal);
		items.add(checkDeposit);
		items.add(allFaq);
		return items;
	}

	/**
	 * Get the menu items that need to be added to the help menu when on ATM Locator
	 * @return the menu items for ATM locator
	 */
	public List<HelpItemGenerator> getAtmHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		final HelpItemGenerator atmHelp = 
				new HelpItemGenerator(R.string.help_menu_atm_help, false, true, getAtmHelpListener());
		final HelpItemGenerator atmFaq = 
				new HelpItemGenerator(R.string.help_menu_atm_faq, false, true, 
												getDefaultClickListener(BankExtraKeys.ATM_LOCATOR_FAQ));
		items.add(atmHelp);
		items.add(atmFaq);
		items.add(allFaq);
		return items;
	}

	/**
	 * Return the click listener for the atm help menu item
	 * @return the click listener for the atm help menu item
	 */
	private OnClickListener getAtmHelpListener() {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final BaseFragmentActivity activity = (BaseFragmentActivity)DiscoverActivityManager.getActiveActivity();
				activity.showCustomAlert(AtmModalFactory.getAtmLocatorHelpModal(activity));
			}	
		};
	}

	/**
	 * Default click listener for menu items. On click the menu item will navigate the user to
	 * a sub section of all the FAQs
	 * @return the click listener for menu items
	 */
	private OnClickListener getDefaultClickListener(final String faqType){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToSpecificFaq(faqType);

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
				BankConductor.navigateToFAQLandingPage();
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
