package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Fragment used to display the Check Deposit - Select Amount page. This is the second step in the
 * Check Deposit work-flow. Displays a an amount text where the user can specify the dollar amount for the check 
 * being deposited. Supports inline error handling as well using the BankAmountItem class.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositSelectAmount extends BankDepositBaseFragment {
	/**
	 * Reference to Account selected by the user in step 1 of Check Deposit workflow via the Select Account page
	 * and passed to this fragment via a bundle. 
	 */
	private Account account;
	/**
	 * Reference to view that will contains the amount field value which validates user entries.
	 */
	private BankAmountItem amountItem;
	/**
	 * Boolean flag to detect if fragment's orientation is changing
	 */
	private boolean isOrientationChanging = false;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final Bundle bundle = this.getArguments();
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			account = (Account)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		actionLink.setVisibility(View.GONE);
		feedbackLink.setVisibility(View.GONE);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		/**Show "Continue" text in single button on screen*/
		actionButton.setText(R.string.continue_text);

		final Drawable d = getActivity().getResources().getDrawable(R.drawable.light_gray_bkgrd);
		contentTable.setBackgroundDrawable(d);
		
		/**Listen when user taps on the layout to close the keyboard*/
		view.findViewById(R.id.main_layout).setOnTouchListener(new OnTouchListener() {           
	        @Override
			public boolean onTouch(final View v, final MotionEvent event) {
	         	amountItem.getEditableField().showKeyboard(false);
	            return false;
	        }
	    });
		
		return view;
	}
	
	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		return account.getDottedFormattedAccountNumber();
	}

	/**
	 * Used to determine the step in the bread-crumb.
	 * 
	 * @return Returns 0 to specify to base class to show indicator in step 1 of bread-crumb.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}


	/**
	 * Only one item is displayed for this page which is a BankAmountItem for the user to enter
	 * a dollar amount for the next step in the work-flow.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();
		
		amountItem = new BankAmountItem(getActivity());
		
		/**Set the limits to use to verify if what the user has entered is valid*/
		amountItem.getEditableField().setAccountLimits(account.limits);
		
		/**Add item to the content list table*/
		items.add( amountItem  );
		
		return items;
	}

	final int CAPTURE_ACTIVITY = 1;
	
	@Override
	protected void onActionButtonClick() {	
		/**Clear focus to close keyboard*/
		amountItem.getEditableField().clearFocus();
		
		if( amountItem.getEditableField().isValid() ) {
			/**
			 * Launch the check deposit capture activity when Continue is clicked and all limits are not exceeded
			 */
			final Intent captureCheckActivity = new Intent(getActivity(), CheckDepositCaptureActivity.class);
			startActivityForResult(captureCheckActivity, CAPTURE_ACTIVITY);
			
		} else {
			amountItem.getEditableField().updateAppearanceForInput();
		}
	}
	
	/**
	 * When the check capture activity finishes, navigate to the next step
	 * in the process of check deposit.
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		DiscoverActivityManager.setActiveActivity(getActivity());
		if(requestCode == CAPTURE_ACTIVITY)
			if(resultCode == Activity.RESULT_OK) {
				BankConductor.navigateToCheckDepositReview(getArguments());
			}
	}

	@Override
	protected void onActionLinkClick() {
		//this is not used for this screen
	}

	@Override
	public void onBackPressed() {
		//this is not required for this screen
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/**Reset flag*/
		isOrientationChanging = false;
		
		/**Enable text watcher which will format text in text field*/
		this.amountItem.getEditableField().enableBankAmountTextWatcher(true);
		
		/**
		 * Have to execute the setting of the editable field to edit mode asyncronously otherwise
		 * the keyboard doesn't open.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if( amountItem != null ) {
					amountItem.getEditableField().requestFocus();
				}
			}
		}, 1000);
		
	}
	
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);
		
		/**Set to true so that keyboard is not closed in onPause*/
		isOrientationChanging = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		/**Disable Text Watcher to support rotation*/
		this.amountItem.getEditableField().enableBankAmountTextWatcher(false);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		/**Check if onPause was called because of an orientation change*/
		if( !isOrientationChanging ) {
			this.amountItem.getEditableField().showKeyboard(false);
		}
	}
}
