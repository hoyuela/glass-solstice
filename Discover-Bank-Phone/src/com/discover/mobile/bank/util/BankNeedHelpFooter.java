package com.discover.mobile.bank.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.common.ui.help.NeedHelpFooter;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * 
 * Utility class used to wrap the Need Help footer at the bottom of every page or modal. Provides
 * functionality to set the help number dynamically and to show or hide the footer on a page. In addition,
 * this class provides the capability of showing a Call Modal when the user taps on the phone number
 * displayed. The call modal can be shown or not shown depending on how promptUser flag is set at instantiation.
 * 
 * @author henryoyuela
 *
 */
public class BankNeedHelpFooter extends NeedHelpFooter {
	/**
	 * Flag used to decide whether to show Call Modal when the Need Help link is tapped by the user.
	 */
	private final boolean promptUser;
	
	public BankNeedHelpFooter(final ViewGroup rootView) {
		super(rootView);
		promptUser = false;
	}

	public BankNeedHelpFooter(final ViewGroup rootView, final boolean promptUser) {
		super(rootView);
		
		this.promptUser = promptUser;
	}
	
	/**
	 * Used to set the help number displayed in the footer
	 * 
	 * @param helpNumber Resource id of the help number in the resource file
	 */
	@Override
	public void setToDialNumberOnClick(final int helpNumber) {
		helpNumberTxtVw.setText(helpNumberTxtVw.getContext().getResources().getString(helpNumber));
		
		helpNumberTxtVw.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if( promptUser ) {
					BankNavigator.navigateToCallModal(helpNumberTxtVw.getText().toString());
				} else {
					CommonUtils.dialNumber(helpNumberTxtVw.getText().toString(), helpNumberTxtVw.getContext());
				}
			}
		});
	}
}
