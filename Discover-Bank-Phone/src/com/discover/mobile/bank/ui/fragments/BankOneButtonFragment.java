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
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.CommonUtils;
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
	private LinearLayout contentTable;
	/**
	 * Reference to a button whose click event is to be handled by a sub-classes implementation of onActionButtonClick()
	 */
	private Button actionButton;
	/**
	 * Reference to a TextView whose click event is to be handled by a sub-classs implementation of onActionLinkClick
	 */
	private TextView actionLink;
	/**
	 * Reference to a Progress indicator used to display a users progress in the current work-flow
	 */
	private BankHeaderProgressIndicator progressIndicator;
	/**
	 * Reference to a TextView which displays a title for the instructions or note provided to the user.
	 * By default view is gone, sub-class to make it visible as required.
	 */
	private TextView noteTitle;
	/**
	 * Reference to a TextView which displays a note or instructions to the user. By default view is gone, 
	 * sub-class to make it visible as required.
	 */
	private TextView noteTextMsg;
	
	/**
	 * Reference to a TextView which displays page title for the layout.
	 */
	private TextView pageTitle;
	/**
	 * Holds list of items that are generated using the method getViewPagerListContent() or getRelativeLayoutListContent()
	 */
	private List<?> content;
	/**
	 * Reference to TextView that shows on top of content table used for showing general errors for the screen.
	 */
	private TextView generalError = null;
	/**
	 * Reference to scroll view
	 */
	private ScrollView scrollView;
	/**
	 * String appended to a string to determine whether a field has an error on rotation
	 */
	protected static final String KEY_ERROR_EXT = ".hasError";
	/**
	 * Reference to footer in layout
	 */
	private BankLayoutFooter footer;
	

	/**
	 * Sets click listeners for the actionButton, actionLink, feedbackLink. Calls the method to populate
	 * content on the contentTable and loads a reference to view for progressIndicator. Finally,
	 * hides the status bar for the Fragment activity hosting the fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_one_button_layout, null);
		
		progressIndicator = (BankHeaderProgressIndicator)view.findViewById(R.id.header);
		
		
		/**Fetch layout that will contain list of account groups*/
		contentTable = (LinearLayout)view.findViewById(R.id.content_table);
		
		actionButton = (Button)view.findViewById(R.id.actionButton);
		actionButton.setOnClickListener(this);
		
		actionLink = (TextView)view.findViewById(R.id.actionLink);
		actionLink.setOnClickListener(this);
			
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
		
		scrollView =(ScrollView)view.findViewById(R.id.main_layout);
		
		/**If there aren't any ViewPagerListItems then see if any RelativeLayout list items are provided*/
		if( null == content) { 
			content = getRelativeLayoutListContent();
		}
		
		/**Create footer that will listen when user taps on Need Help Number to dial*/
		footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.promptForHelp(promptUserForNeedHelp());
		
		loadListElementsToLayoutFromList(contentTable, content);
			
		/**Label used to show general errors for the screen, it shows on top of the content table*/
		generalError = (TextView)view.findViewById(R.id.general_error);
		
		CommonUtils.fixBackgroundRepeat(view);
		
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
			for(final Object element : elementList){
				layout.addView((View)element);
			}
		}
	}
	
	/**
	 * Abstract method to be implemented by sub-class to provide a list of ViewPagerListItems to be
	 * displayed by this fragment. Sub-class can choose to return null for this method and use 
	 * getRelativeLayoutListContent() instead.
	 * 
	 * @return Returns a list of ViewPagerListItem objects 
	 */
	protected abstract List<ViewPagerListItem> getViewPagerListContent();
	
	/**
	 * Abstract method to be implemented by sub-class to provide a list of RelativeLayout objects to be
	 * displayed by this fragment. Sub-class can choose to return null for this method and use 
	 * getRelativeLayoutListContent() instead.
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
		if( sender.equals(actionButton) ) {
			this.onActionButtonClick();
		} else if( sender.equals(actionLink)) {
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
	
	/**
	 * Method to show error string above content table
	 * 
	 * @param text Reference to error text to display
	 */
	public void showGeneralError(final String text) {
		if( !Strings.isNullOrEmpty(text)) {
			generalError.setText(text);
			generalError.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Method used to hide any error being shown above the content table which 
	 * had been previously shown wiht showGeneralError().
	 */
	public void clearGeneralError() {
		generalError.setVisibility(View.GONE);
	}
	
	/**
	 * Method used to set the error string for an inline error label and make it visible.
	 * 
	 * @param view TextView that represents an inline error whose text will be set using the param text.
	 * @param text String to show to the user as an inline error
	 */
	public void setErrorString(final TextView view, final String text ) {
		if( view != null && !Strings.isNullOrEmpty(text)  ) {
			view.setText(text);
			view.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Method used to scroll to the top of the page.
	 */
	public void scrollToTop() {
		if(scrollView != null ) {
			scrollView.fullScroll(ScrollView.FOCUS_UP);
			scrollView.smoothScrollTo(0, 0);
		}
	}
	
	protected void hideBottomNote() {
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
	}
	
	protected void setButtonText(final int resId) {
		actionButton.setText(resId);
	}
	
	protected void setLinkText(final int resId) {
		actionLink.setText(resId);
	}
	
	protected BankHeaderProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}
	
	protected Button getActionButton() {
		return actionButton;
	}
	
	protected TextView getActionLink() {
		return actionLink;
	}
	
	protected TextView getNoteTitle() {
		return noteTitle;
	}
	
	protected TextView getNoteMessage() {
		return noteTextMsg;
	}
	
	protected LinearLayout getTable() {
		return contentTable;
	}
	
	protected BankLayoutFooter getFooter() {
		return footer;
	}
	
	protected List<?> getContent() {
		return content;
	}
	
	protected TextView getGeneralError() {
		return generalError;
	}
	
	protected ScrollView getScrollView() {
		return scrollView;
	}
	
	protected TextView getPageTitleView() {
		return pageTitle;
	}
}
