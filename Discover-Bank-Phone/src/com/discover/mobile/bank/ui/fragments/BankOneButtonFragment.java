package com.discover.mobile.bank.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.BankNeedHelpFooter;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.google.common.base.Strings;

/**
 * An abstract base Fragment class that uses the layout defined in bank_one_button_layout.xml to display
 * a single button, a link beneath the button, feedback footer, and a linear layout to
 * display content. Class will require the subclass to provide a list of content to display in the linear layout
 * via the method implementation of:
 * 		
 * 			protected abstract List<ViewPagerListItem> getContent()
 * 
 * In addition, this class generates events for when button and link are clicked. The sub-class should implement
 * how to handle these clicks via the method implementations of:
 *
 *  		protected abstract void onActionButtonClick();
 *	
 *			protected abstract void onActionLinkClick();
 * 
 * 
 * @author henryoyuela
 *
 */
public abstract class BankOneButtonFragment extends BaseFragment implements OnClickListener, FragmentOnBackPressed {
	/**
	 * LinearLayout that will display a list of content provided by sub-class via getContent 
	 */
	protected LinearLayout contentTable;
	/**
	 * Reference to a button whose click event is to be handled by a sub-classes implementation of onActionButtonClick()
	 */
	protected Button actionButton;
	/**
	 * Reference to a TextView whose click event is to be handled by a sub-classs implementation of onActionLinkClick
	 */
	protected TextView actionLink;
	/**
	 * Reference to a TextView whose click event navigates the application to the feedback landing page
	 */
	protected TextView feedbackLink;
	/**
	 * Reference to a Progress indicator used to display a users progress in the current work-flow
	 */
	protected HeaderProgressIndicator progressIndicator;
	/**
	 * Reference to a TextView which displays a title for the instructions or note provided to the user.
	 * By default view is gone, sub-class to make it visible as required.
	 */
	protected TextView noteTitle;
	/**
	 * Reference to a TextView which displays a note or instructions to the user. By default view is gone, 
	 * sub-class to make it visible as required.
	 */
	protected TextView noteTextMsg;
	/**
	 * Reference to a TextView which displays page title for the layout.
	 */
	protected TextView pageTitle;
	/**
	 * Holds list of items that are generated using the method getViewPagerListContent() or getRelativeLayoutListContent()
	 */
	protected List<?> content;
	/**
	 * Helper class for enabling the user to dial the Need Help Number
	 */
	private BankNeedHelpFooter helpFooter;
	
	/**
	 * Sets click listeners for the actionButton, actionLink, feedbackLink. Calls the method to populate
	 * content on the contentTable and loads a reference to view for progressIndicator. Finally,
	 * hides the status bar for the Fragment activity hosting the fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_one_button_layout, null);
		
		progressIndicator = (HeaderProgressIndicator)view.findViewById(R.id.header);
		
		
		/**Fetch layout that will contain list of account groups*/
		contentTable = (LinearLayout)view.findViewById(R.id.content_table);
		
		actionButton = (Button)view.findViewById(R.id.actionButton);
		actionButton.setOnClickListener(this);
		
		actionLink = (TextView)view.findViewById(R.id.actionLink);
		actionLink.setOnClickListener(this);
		
		feedbackLink = (TextView)view.findViewById(R.id.provide_feedback);
		feedbackLink.setOnClickListener(this);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		noteTitle = (TextView)view.findViewById(R.id.note_text_title);
		noteTitle.setVisibility(View.GONE);
		
		noteTextMsg = (TextView)view.findViewById(R.id.note_text_msg);
		noteTextMsg.setVisibility(View.GONE);
		
		/**Check to see if ViewPagerListItems are provided*/
		content = getViewPagerListContent();
		
		/**Set Page title if required by the sub-class*/
		pageTitle = (TextView)view.findViewById(R.id.page_title);
		if( !Strings.isNullOrEmpty(getPageTitle()) ) {
			pageTitle.setVisibility(View.VISIBLE);
			pageTitle.setText(getPageTitle());
		} else {
			pageTitle.setVisibility(View.GONE);
		}
		
		/**If there aren't any ViewPagerListItems then see if any RelativeLayout list items are provided*/
		if( null == content) { 
			content = getRelativeLayoutListContent();
		}
		
		/**Create footer that will listen when user taps on Need Help Number to dial*/
		helpFooter = new BankNeedHelpFooter((ViewGroup)view, promptUserForNeedHelp() );
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);
		
		loadListElementsToLayoutFromList(contentTable, content);
			
		return view;
	}

	/**
	 * Method to be overridden by sub-class if a title is required for the layout. Otherwise
	 * the title will be hidden.
	 * 
	 * @return Return String that holds the text to display in the page title. 
	 * 		   Return null if page title should be hidden.
	 */
	protected String getPageTitle() {
		return null;
	}

	/**
	 * Loads a list of ViewPagerListItems into a LinearLayout to construct a table of items.
	 * @param layout a LinearLayout that will show a list of ViewPagerListItems
	 * @param elementList a list of ViewPagerListItems
	 */
	public void loadListElementsToLayoutFromList(final LinearLayout layout, final List<?> elementList){
		if(layout != null && elementList != null){
			for(final Object element : elementList)
				layout.addView((View)element);
		}
	}
	
	/**
	 * Abstract method to be implemented by sub-class to provid a list of ViewPagerListItems to be
	 * displayed by this fragment. Sub-class can choose to return null for this method and use getRelativeLayoutListContent() 
	 * instead.
	 * 
	 * @return Returns a list of ViewPagerListItem objects 
	 */
	protected abstract List<ViewPagerListItem> getViewPagerListContent();
	
	/**
	 * Abstract method to be implemented by sub-class to provide a list of RelativeLayout objects to be
	 * displayed by this fragment. Sub-class can choose to return null for this method and use getRelativeLayoutListContent() 
	 * instead.
	 * 
	 * @return Returns a list of RelativeLayout objects 
	 */
	protected abstract List<RelativeLayout> getRelativeLayoutListContent();
	
	/**
	 * Abstract Method to be implemented by sub-class to handle when the action button is clicked
	 */
	protected abstract void onActionButtonClick();
	
	/**
	 * Abstract Method to be implmented by sub-class to handle when the action link is clicked
	 */
	protected abstract void onActionLinkClick();

	/**
	 * Method implementation of OnClickListener to specifiy to the sub-class
	 * which button has been clicked via abstract methods.
	 */
	@Override
	public void onClick(final View sender) {
		if( sender == feedbackLink) {
			BankNavigator.navigateToFeedback();
		}  else if( sender == actionButton ) {
			this.onActionButtonClick();
		} else if( sender == actionLink) {
			this.onActionLinkClick();
		}
	}
	
	
	@Override
	public boolean isBackPressDisabled() {
		return false;
	}
	
	/**
	 * Method used to determine whether the user should be prompted with a modal before navigating
	 * to dialer when tapping on Need Help footer.
	 */
	public boolean promptUserForNeedHelp(){
		return false;
	}

}
