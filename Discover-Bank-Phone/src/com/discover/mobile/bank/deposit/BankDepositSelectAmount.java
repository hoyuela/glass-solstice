package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.bank.ui.widgets.FooterType;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;
import com.google.common.base.Strings;

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
	 * Reference to bundle provided in onCreateView or via getArguments() depending on what created the fragment.
	 */
	private Bundle bundle = null;
	
	private static final int CAPTURE_ACTIVITY = 1;
	/** Time (ms) to delay keyboard display on resume. */
	private static final int KEYBOARD_DELAY = 800;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		/**Set bundle to data set via getArguments if saveInstanceState is null*/
		bundle = (savedInstanceState == null)? this.getArguments() : savedInstanceState;
		
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			account = (Account)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		hideUnusedLabels(view);
		
		/**Show "Continue" text in single button on screen*/
		setButtonText(R.string.continue_text);

		getTable().setBackgroundDrawable(null);
		
		/**Listen when user taps on the layout to close the keyboard*/
		view.findViewById(R.id.main_layout).setOnTouchListener(new OnTouchListener() {           
	        @Override
			public boolean onTouch(final View v, final MotionEvent event) {
	         	amountItem.getEditableField().showKeyboard(false);
	            return false;
	        }
	    });
		
		/**Setup footer*/
		final BankLayoutFooter footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.setFooterType(FooterType.PRIVACY_TERMS | FooterType.NEED_HELP);
		
		return view;
	}
	
	/**Hide controls that are not needed*/
	private void hideUnusedLabels(final View view) {
		hideBottomNote();
		getActionLink().setVisibility(View.GONE);
		(view.findViewById(R.id.top_note_text)).setVisibility(View.GONE);
	}
	
	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		final StringBuilder titleBuilder = new StringBuilder("");
		
		if(account != null && !Strings.isNullOrEmpty(account.nickname)) {
			titleBuilder.append(account.nickname);
			final String ending = account.getShortDottedFormattedAccountNumber();
			
			if(!Strings.isNullOrEmpty(ending)) {
				titleBuilder.append(" \n");
				titleBuilder.append(ending);
			}
		}
		
		return titleBuilder.toString();
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
		if( account != null  ) {
			amountItem.getEditableField().setAccountLimits(account.limits);
			amountItem.getEditableField().setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		}
			
		/**Add item to the content list table*/
		items.add( amountItem  );
		
		return items;
	}
	
	@Override
	protected void onActionButtonClick() {	
		/**Clear focus to close keyboard*/
		amountItem.getEditableField().clearFocus();
		
		if( amountItem.getEditableField().isValid() ) {
			/**
			 * Launch the check deposit capture activity when Continue is clicked and all limits are not exceeded
			 */
			final Bundle args = getArguments();
			boolean reviewDepositOnFinish = false;
			
			if(args != null){
				reviewDepositOnFinish = args.getBoolean(BankExtraKeys.REENTER_AMOUNT);
			}
			
			if(reviewDepositOnFinish) {
				//Reset the review deposit bundle boolean to prevent odd navigation issues later.
				args.putBoolean(BankExtraKeys.REENTER_AMOUNT, false);
				navigateToReviewDeposit();
			}
			else {
				final Intent captureCheckActivity = new Intent(getActivity(), CheckDepositCaptureActivity.class);
				startActivityForResult(captureCheckActivity, CAPTURE_ACTIVITY);
			}
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
		if(requestCode == CAPTURE_ACTIVITY){
			if(resultCode == Activity.RESULT_OK) {
				navigateToReviewDeposit();
			}
		}
	}

	/**
	 * Navigates to the review deposit Fragment using the BankConductor and passes
	 * the entered amount as a cent value as an integer to that Fragment.
	 */
	private void navigateToReviewDeposit() {
		/**Remove everything but numbers so we have the value in cents.*/
		final String amount = amountItem.getEditableField().getText().toString().replaceAll("[^0-9]", "");
		final Bundle arguments = getArguments();
		arguments.putInt(BankExtraKeys.AMOUNT, Integer.parseInt(amount));
		BankConductor.navigateToCheckDepositWorkFlow(arguments, BankDepositWorkFlowStep.ReviewDeposit);
	}
	
	/**
	 * Restore the amount field to a previous amount from the argument bundle.
	 * This is needed if a user has come back to this Fragment from 
	 */
	private void setupAmountFieldValue() {
		if(bundle != null ) {
			/**Value is an int if read from getArguments*/
			final double amount = bundle.getInt(BankExtraKeys.AMOUNT);	
			/**Value is a string if read from savedInstanceState in onCreateView*/
			final String amountString = bundle.getString(BankExtraKeys.AMOUNT);
			
			/**Set Text for editable field, this has to be called after super class onCreateView has been called*/
			if(amountItem != null && !Strings.isNullOrEmpty(amountString)) {
				amountItem.getEditableField().enableBankAmountTextWatcher(false);
				amountItem.getEditableField().setText(amountString);
				amountItem.getEditableField().enableBankAmountTextWatcher(true);
			}
			
			/**Check if value is greater than 0*/
			else if( amount > 0 ) {
				final double doubleAmount = amount/100;
				
				String valueText = BankStringFormatter.convertStringFloatToDollars(Double.toString(doubleAmount));
				valueText = valueText.replace("$", "");
				
				amountItem.getEditableField().setText(valueText);
			}		
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
		
		/**Restore amount value stored when paused*/
		setupAmountFieldValue();
		
		/**Enable text watcher which will format text in text field*/
		amountItem.getEditableField().enableBankAmountTextWatcher(true);
		
		amountItem.getEditableField().requestFocus();
		
		/**Set cursor to the end of the end of the text*/
		amountItem.getEditableField().setSelection(amountItem.getEditableField().getText().length());
		
		/**
		 * Have to show keyboard asynchronously otherwise it doesn't open.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if( amountItem != null ) {
					amountItem.getEditableField().showKeyboard(true);
				}
			}
		}, KEYBOARD_DELAY);	
	}
	
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);
			
		/**Saved data required for orientation change*/
		if( amountItem != null ) {
			outState.putString(BankExtraKeys.AMOUNT, amountItem.getEditableField().getText().toString());
			outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
		} 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		/**Disable Text Watcher to support rotation*/
		this.amountItem.getEditableField().enableBankAmountTextWatcher(false);
		
		/** Close the keyboard (will reopen after orientation change). */
		if (amountItem != null && amountItem.getEditableField() != null) {
			this.amountItem.getEditableField().showKeyboard(false);
		}
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
	}
}
