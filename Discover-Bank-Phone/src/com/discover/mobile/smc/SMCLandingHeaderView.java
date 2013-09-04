package com.discover.mobile.smc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.table.TableButtonGroup;
import com.discover.mobile.common.ui.table.TableHeaderButton;

/**
 * Contains the header view for the inbox landing page.
 * @author juliandale
 *
 */
public class SMCLandingHeaderView extends RelativeLayout {
	/*
	 * Header button view that will be inflated
	 */
	private final View view;
	/**Button group that contains the buttons to nav to inbox/sent messages*/
	private final TableButtonGroup group;
	/**Indexes of the buttons in the table header, used to return references to buttons*/
	private final static int INBOX_BUTTON = 0;
	private final static int SENTBOX_BUTTON = 1;
	/**Boolean to keep track if the inbox button is selected or not*/
	private boolean isInboxSelected;
	
	
	public SMCLandingHeaderView(Context context, final AttributeSet attrs) {
		super(context,attrs);
		view = LayoutInflater.from(context).inflate(R.layout.bank_smc_header, null);
		group = (TableButtonGroup) view.findViewById(R.id.header_buttons);
		addView(view);
	}
	
	/**
	 * Set the observers to the group
	 * @param observer - observer of the buttons
	 */
	public void setGroupObserver(final OnClickListener observer){
		group.addObserver(observer);
	}

	/**Returns a the inbox button
	 * @return inbox button*/
	public TableHeaderButton getInboxButton() {
		return group.getButton(INBOX_BUTTON);
	}
	
	/**Returns the sentbox button
	 * @return sent button*/
	public TableHeaderButton getSentBoxButton() {
		return group.getButton(SENTBOX_BUTTON);
	}
	
	/**Notify the button group that observer has stopped
	 * listening
	 */
	public void removeListeners() {
		group.removeObserver();
	}
	
	/**set the Sent button selected*/
	public void setSentSelected() {
		setIsInboxSelected(false);
		group.setButtonSelected(SENTBOX_BUTTON);
	}
	
	/**set the inbox button to selected*/
	public void setInboxSelected() {
		setIsInboxSelected(true);
		group.setButtonSelected(INBOX_BUTTON);
	}
	
	/**
	 * Set flag to determine if the user is on inbox
	 * @param selected
	 */
	public void setIsInboxSelected(boolean selected) {
		isInboxSelected = selected;
	}
	
	/**
	 * @return true if user is currently viewing inbox
	 */
	public boolean getInboxSelected() {
		return isInboxSelected;
	}

}
