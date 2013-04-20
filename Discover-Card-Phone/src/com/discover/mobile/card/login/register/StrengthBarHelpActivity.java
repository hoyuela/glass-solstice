package com.discover.mobile.card.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.error.ErrorHandler;

import com.discover.mobile.card.common.utils.Utils;

/**
 * Activity definition used to display a help guide for either User ID
 * or Password Strength to the user. A single layout is defined in 
 * res\layout\register_help_strength. The attributes for the views are
 * dynamically set at runtime. The attribute values are determined based
 * on whether the user opened the User ID Strength Guide or the Password
 * Strength Guide. The type of guide open will be based on the "ScreenType"
 * extra put in the INTENT that opens this activity
 * 
 * @author henryoyuela
 *
 */
public class StrengthBarHelpActivity extends NotLoggedInRoboActivity {
	private static final String TAG = StrengthBarHelpActivity.class.getSimpleName();
	private static final String REFERER = "forgot-password-step2-pg";
	protected TextView provideFeedback ;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register_help_strength);
		provideFeedback = (TextView)findViewById(R.id.provide_feedback_button);
		provideFeedback.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utils.createProvideFeedbackDialog(StrengthBarHelpActivity.this,
                        REFERER);
            }
        });

		//Determine what help guide to configure the screen for based on extra in INTENT
		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			final String helpLayout = extras.getString(ScreenType.INTENT_KEY);

			if("id".equals(helpLayout)) {					
				setHelpIdStrengthViewAttr();
			} else {		
				setHelpPswdStrengthViewAttr();
			}
		} else {
			//Use Password Help Guide as the default help guide
			setHelpPswdStrengthViewAttr();
		}	
	}

	/**
	 * Method called when StrengthBarHelpActivity is opened with ScreenType
	 * equal to "id" in order to set view attribute values.
	 */
	private void setHelpIdStrengthViewAttr() {
		//Set Weak View Attributes
		View view = this.findViewById(R.id.reg_help_weak_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level1, 
					R.string.account_info_help_action1, 
					R.string.account_info_help_msg1, R.drawable.rounded_red_bar, R.drawable.x_red);		

		}

		//Set Moderate View Attributes
		view = this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, 0);

		}

		//Set Strong View Attributes
		view = this.findViewById(R.id.reg_help_strong_pswd);
		if( null != view) {
			setViewItemAttr(view, 
					R.string.account_info_help_level3, 
					R.string.account_info_help_action3, 
					R.string.account_info_help_msg3, R.drawable.rounded_green_bar, R.drawable.checkmark_green);	
		}
	}

	/**
	 * Method called when StrengthBarHelpActivity is opened with ScreenType
	 * NOT equal to "id" in order to set view attribute values.
	 */
	private void setHelpPswdStrengthViewAttr() {
		//Set Weak View Attributes
		View view = this.findViewById(R.id.reg_help_weak_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level1, 
					R.string.account_info_help_action1, 
					R.string.account_info_help_pswd_msg1, R.drawable.rounded_red_bar, R.drawable.x_red);		

		}

		//Set Moderate View Attributes
		view = this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, 0);		

		}

		//Set Strong View Attributes
		view = this.findViewById(R.id.reg_help_strong_pswd);
		if( null != view) {
			setViewItemAttr(view, 
					R.string.account_info_help_level3, 
					R.string.account_info_help_action3, 
					R.string.account_info_help_msg3, R.drawable.rounded_green_bar, R.drawable.checkmark_green);	
		}
	}

	/**
	 * Generic Method called when StrengthBarHelpActivity is opened to set view attribute values.
	 * 
	 * @param view Reference to view used by the Activity to display help guide
	 * @param level Resource ID to string used to specify strength level (Strong, Moderate, Weak)
	 * @param action Resource ID to string used to specify what action user should take to improve password or user id strength
	 * @param msg Resource ID to message used to specify the criteria used to determine the strength level
	 * @param image Resource ID to image to display for the strength bar
	 * @param image2 Resource ID to image to display next to the strength bar
	 */
	private void setViewItemAttr(final View view, final int level, final int action, final int msg, final int image, final int image2 ) {	
		if( null != view ) {
			final TextView levelTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_level);
			final TextView actionTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_action);
			final TextView msgTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_msg);
			final View colorBarVw = view.findViewById(R.id.reg_help_strength_image);

			if( null != levelTxtVw)
				levelTxtVw.setText(level);

			if( null != actionTxtVw)
				actionTxtVw.setText(action);

			if( null != msgTxtVw)
				msgTxtVw.setText(msg);

			if( null != colorBarVw ) {
				colorBarVw.setBackgroundResource(image);
			}	

			//If resource id is not 0 then display image
			if( 0 != image2 ) {
				final View checkVw = view.findViewById(R.id.reg_help_strong_check);
				if( null != checkVw)  {
					checkVw.setBackgroundResource(image2);		
					checkVw.setVisibility(View.VISIBLE);
				}
			}
		} else {
			Log.v(TAG, "Unable to set view attributes");
		}
	}

	@Override
	public void onBackPressed() {
		goBack(null);
	}

	/**
	 * Method called when user hits the back hardware button on the device
	 */
	public void goBack(final View v){
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Method called when user hits the back soft-key in the action bar
	 */
	@Override
	public void goBack() {
		setResult(RESULT_OK);
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
	public void showCustomAlert(final AlertDialog alert) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content,
			final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(final int errorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
