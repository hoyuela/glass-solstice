package com.discover.mobile.bank.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.util.BankNeedHelpFooter;
import com.discover.mobile.common.BaseFragment;
import com.google.common.base.Strings;

/**
 * Fragment used to display a message to the user.
 * 
 * @author henryoyuela
 *
 */
public abstract class BankMessageFragment extends BaseFragment implements OnClickListener  {

	/**
	 * Helper class for enabling the user to dial the Need Help Number
	 */
	private BankNeedHelpFooter helpFooter;
	/**
	 * Reference to single button on screen
	 */
	protected Button actionButton;
	/**
	 * Reference to page title on the fragment
	 */
	protected TextView pageTitle;
	/**
	 * Reference to text view that holds the message text for the fragment.
	 */
	protected TextView bodyText;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_message_fragment_layout, null);
		
		/**Create footer that will listen when user taps on Need Help Number to dial*/
		helpFooter = new BankNeedHelpFooter((ViewGroup)view);
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);
		
		/**Set the fragment as the handler for the button click event*/
		actionButton = (Button)view.findViewById(R.id.action_button);
		if( Strings.isNullOrEmpty(getActionButtonText())) {
			actionButton.setVisibility(View.GONE);
		} else {
			actionButton.setText(getActionButtonText());
			actionButton.setOnClickListener(this);
		}
				
		pageTitle = (TextView)view.findViewById(R.id.page_title);
		pageTitle.setText(getPageTitle());
		
		bodyText = (TextView)view.findViewById(R.id.body_text);
		bodyText.setText(getBodyText());
		
		return view;
	}
	
	/**
	 * @return Returns a String that is to be displayed as the text for the action button. 
	 * 			If null or empty it hides the the button.
	 */
	public abstract String getActionButtonText();
	
	/** 
	 * @return Returns a String that is to be displayed as the title of the page.
	 */
	public abstract String getPageTitle();
	
	/**
	 * 
	 * @return Returns a String that is to be displayed as the body text of the page.
	 */
	public abstract String getBodyText();
	
	/**
	 * @param value True to show icon in layout false to hide it.
	 */
	public void showIcon(final boolean value ) {
		final ImageView image = (ImageView)this.getView().findViewById(R.id.icon);
		if( value ) {
			image.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.GONE);
		}
	}
	
}
