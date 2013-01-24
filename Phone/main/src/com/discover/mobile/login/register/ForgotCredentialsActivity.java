package com.discover.mobile.login.register;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.login.LoginActivity;
/**
 * This class handles a user's choice and navigation to the first step of forgot password/user id/ or both.
 * It contains a list of choices, upon tapping a choice, this activity is finished and the selected
 * activity is started.
 * 
 * @author scottseward
 *
 */
public class ForgotCredentialsActivity extends NotLoggedInRoboActivity {
	
	private ArrayAdapter<Option> optionAdapter;
	private ListView choicesList;
	final Activity currentContext = this;
	protected TextView helpNumber;

	/**
	 * Load list options into the list and setup an OnClickListener to wait for list selections.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_forgot_type_select);
		choicesList = (ListView)findViewById(android.R.id.list);
		optionAdapter = new ArrayAdapter<Option>(this, R.layout.register_forgot_type_select_option_item, android.R.id.text1,
				Option.values());
		helpNumber = (TextView)findViewById(R.id.help_number_label);
		choicesList.setAdapter(optionAdapter);

		
		choicesList.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * When a item is selected in the list, get the position that was pressed, select the option object
			 * at that position, start the activity associated with that object, then finish this activity.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final Option selection = optionAdapter.getItem(arg2);
				final Class<?> intentClass = selection.getIntentClass();
				startActivity(new Intent(currentContext, intentClass));		
				endActivity();
			}
		});
		setupClickablePhoneNumbers();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_MENU);
	}
	
	/**
	 * Make the help number clickable and dialable.
	 */
	protected void setupClickablePhoneNumbers() {
		final Context currentContext = this;
		helpNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(helpNumber.getText().toString(), currentContext);				
			}
		});
	}

	private void endActivity(){
		finish();
	}
	
	/**
	 * An enumerated type that contains list items and the class/activity that they start upon selection.
	 * 
	 * @author scottseward
	 *
	 */
	private static enum Option {
		USER_ID("Forgot User ID", ForgotUserIdActivity.class),
		PASSWORD("Forgot Password", ForgotPasswordAccountInformationActivity.class),
		BOTH("Forgot Both", ForgotBothAccountInformationActivity.class);
		
		private final String ITEM_TEXT;
		private final Class<?> INTENT_CLASS;
		
		Option(final String itemText, final Class<? extends Activity> intentClass) {
			ITEM_TEXT = itemText;
			INTENT_CLASS = intentClass;
		}
		
		/**
		 * Returns a String object containing the text of the list item.
		 */
		@Override
		public String toString() {
			return ITEM_TEXT;
		}
		
		/**
		 * Returns the class/activity associated with this list option.
		 * @return - the class/activity associated with this list item
		 */
		private Class<?> getIntentClass() {
			return INTENT_CLASS;
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}
	
	@Override
	public void goBack() {
		Intent login = new Intent(this, LoginActivity.class);
		startActivity(login);
		finish();
	}

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showCustomAlert(AlertDialog alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorCode, int titleText, int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
