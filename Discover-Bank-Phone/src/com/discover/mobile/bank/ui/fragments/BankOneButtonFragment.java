package com.discover.mobile.bank.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.nav.HeaderProgressIndicator;

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

		loadListElementsToLayoutFromList(contentTable, getContent());
			
		return view;
	}

	/**
	 * Loads a list of ViewPagerListItems into a LinearLayout to construct a table of items.
	 * @param layout a LinearLayout that will show a list of ViewPagerListItems
	 * @param elementList a list of ViewPagerListItems
	 */
	public void loadListElementsToLayoutFromList(final LinearLayout layout, final List<ViewPagerListItem> elementList){
		if(layout != null && elementList != null){
			for(final ViewPagerListItem element : elementList)
				layout.addView(element);
		}
	}
	
	protected abstract List<ViewPagerListItem> getContent();
	
	protected abstract void onActionButtonClick();
	
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
	
	/**
	 * Disable back press for this fragment
	 */
	@Override
	public void onBackPressed() {
		
	}

}
