package com.discover.mobile.bank.payees;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

/**
 * Fragment class used to display the Enter Payee Page Step 1 of the Add Payee workflow. 
 * Consists of a single text box and button, where the users is allowed to enter a
 * name or company that is to be searched for using the Bank API GET /api/payees/search.
 * 
 * Input is validated to ensuer it has a minium of 2 characters and does not allow 
 * characters <>()&;'"[]{}.
 * 
 * @author henryoyuela
 *
 */
public class BankEnterPayeeFragment extends BaseFragment implements OnClickListener {
	private static final String KEY_KEEP_TEXT = "keep-text";
	
	/**
	 * Reference to feedback link in the view of this fragment. When clicked on
	 * will open the Feedback Landing Page
	 */
	private TextView feedback;
	/**
	 * Reference to TextView used to display a help message when the user navigates
	 * to this page as a result of a failed attempt to Enter A Pay Bill with no Payees
	 */
	private TextView msgText;
	/**
	 * Field used to run a search of Payees using the Bank API Web Services
	 */
	private PayeeValidatedEditField searchField;
	/**
	 * Executes the Bank API Web Service GET /api/payees/search using the text in searchField
	 */
	private Button continueButton;
	/**
	 * Button used to open a help guide when clicked on
	 */
	private ImageButton helpButton;
	/**
	 * Used to determine if text should be cleared onResume. Uses bundle saveInstanceState in onCreateView to determine this.
	 */
	private boolean clearText = false;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_enter_payee, null);
		final TextView errorLabel = (TextView)view.findViewById(R.id.error_text);
				
		/**Button used to trigger Payee search*/
		continueButton = (Button)view.findViewById(R.id.continue_button); 
		continueButton.setOnClickListener(this);
				
		/**Lookup EditText field used for searching for a payee**/
		searchField = (PayeeValidatedEditField)view.findViewById(R.id.search_field);
		searchField.setInvalidPattern(PayeeValidatedEditField.INVALID_CHARACTERS);
		searchField.setMinimum(2);
		searchField.attachErrorLabel(errorLabel);
		
		/**Hyperlink used to provide feedback*/
		feedback = (TextView)view.findViewById(R.id.provide_feedback);
		feedback.setOnClickListener(this);
		
		/**Button for help**/
		helpButton = (ImageButton)view.findViewById(R.id.help);
		helpButton.setOnClickListener(this);
		
		/**Text View which displays a message to the user on why they are shown this screen, by visibility is gone*/
		msgText = (TextView)view.findViewById(R.id.msg_text);
		
		/**
		 * Check if message text should be made visible, shown when navigating from Manage Payees Welcome page
		 * and user attempts to enter the Pay Bills flow without having set up any payees they are brought to this 
		 * Manage Payees flow and notified that they must set up a payee in order to pay a bill*/
		final Bundle bundle = this.getArguments();
		if( null != bundle && bundle.getBoolean(BankExtraKeys.DATA_LIST_ITEM)) {
			msgText.setVisibility(View.VISIBLE);	
		} else {
			msgText.setVisibility(View.GONE);
		}
		
		if( null == savedInstanceState || !savedInstanceState.getBoolean(KEY_KEEP_TEXT) ) {
			clearText = true;
		}
		
		return view;
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	/**
	 * Event handler for when any of the views in this fragment are clicked on by the user.
	 */
	@Override
	public void onClick(final View sender ) {
		/**Hide Keyboard*/
		final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
	
		if( sender == feedback ) {
			BankNavigator.navigateToFeedback();
		} else if( sender == helpButton ) {
			final CharSequence text = "Help Under Development";
			final int duration = Toast.LENGTH_SHORT;

			final Toast toast = Toast.makeText(this.getActivity(), text, duration);
			toast.show();
		} else if( sender == continueButton ) {
			if( searchField.isValid() ) {
				final String search = searchField.getText().toString().trim();
				BankServiceCallFactory.createPayeeSearchRequest(search).submit();
				
			} else {
				searchField.updateAppearanceForInput();
			}
		}
	}
	
	/**
	 * Save the state of the current fragment
	 * @param outState - bundle to save the state in.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(KEY_KEEP_TEXT, true);
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		if( clearText ) {
			searchField.getText().clear();
		}
	}
}
