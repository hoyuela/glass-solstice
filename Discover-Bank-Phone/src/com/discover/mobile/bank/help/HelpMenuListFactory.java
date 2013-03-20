/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
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

	/**Default menu item representing the "1-800-290-9885" item*/
	private final HelpItemGenerator number;
	
	/**Pay Bills Mnenu item*/
	private final HelpItemGenerator paybills;

	/**Instance of the factory*/
	private static HelpMenuListFactory factory;

	/**
	 * Private constructor, creates the default help menu items.
	 */
	private HelpMenuListFactory(){
		allFaq = new HelpItemGenerator(R.string.help_all_faq, R.color.blue, false, getAllFaqListener());
		number = new HelpItemGenerator(R.string.help_menu_number, R.color.blue, false, getNumberListener());
		paybills = new HelpItemGenerator(R.string.pay_bills_help, R.color.blue, false, getNumberListener());
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
	 * Get the menu items that are associated with the pay bills pages
	 * @return - the list of help mneu items associated with the pay bills pages
	 */
	public List<HelpItemGenerator> getPayBillsHelpItems(){
		final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
		items.add(paybills);
		items.add(allFaq);
		return items;
	}

	/**
	 * Default click listener for menu items. On click the menu item will navigate the user to
	 * a sub section of all the FAQs
	 * @return the click listener for menu items
	 */
	private OnClickListener getDefaultClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToSpecificFaq();

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
				BankConductor.navigateToAllFaq();
			}
		};
	}

	/**
	 * Click listener for the all number item.  On click the user will be directed to the dialer
	 * @return Click listener for the all number item.  On click the user will be directed to the dialer
	 */
	private OnClickListener getNumberListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				// TODO go to dialer
			}
		};
	}
}
