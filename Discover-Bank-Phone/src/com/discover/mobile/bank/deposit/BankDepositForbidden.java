package com.discover.mobile.bank.deposit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;
import com.google.common.base.Strings;

/**
 * Fragment used to display the Check Deposit - Forbidden User Page. The body text displayed
 * in this fragment is provided by an error message from the Server. It uses the layout defined
 * in res/layout/bank_deposit_forbidden.xml.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositForbidden extends BaseFragment {
	/**
	 * Key used to read error message from bundle passed to this fragment.
	 */
	public final static String KEY_ERROR_MESSAGE = "error-messgae";
	/**
	 * Reference to footer in layout
	 */
	protected BankLayoutFooter footer;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_deposit_forbidden, null);
		
		/**Create footer that will listen when user taps on Need Help Number to dial*/
		footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.setHelpNumber(getString(com.discover.mobile.bank.R.string.bank_deposit_forbidden_number));
		
		/**Fetch text view that will display error message to the user*/
		final TextView errorText = (TextView)view.findViewById(R.id.top_note_text);
		
		/**Help widget setup to show faq*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
		
		/**Fetch error text that was provided from server*/
		final Bundle bundle = this.getArguments();
		if( bundle != null ) {
			final String errorMessage = bundle.getString(BankDepositForbidden.KEY_ERROR_MESSAGE);
			
			if( !Strings.isNullOrEmpty(errorMessage)) {
				errorText.setText(errorMessage);
			}
		}
		return view;
	}

	/**
	 * Method called by base class in onCreateView to determine what string to display in the action bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_deposit_title;
	}


	/**
	 * Method used to retrieve menu group this fragment class is associated with.
	 */
	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP;
	}

	/**
	 * Method used to retreive the menu section this fragment class is associated with.
	 */
	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_NOW_SECTION;
	}

}
