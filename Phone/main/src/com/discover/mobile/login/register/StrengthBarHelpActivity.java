package com.discover.mobile.login.register;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;

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
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.register_help_strength);
	
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
		View view = (View)this.findViewById(R.id.reg_help_weak_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level1, 
					R.string.account_info_help_action1, 
					R.string.account_info_help_msg1, R.drawable.rounded_red_bar, R.drawable.x_red);		
			
		}
		
		//Set Moderate View Attributes
		view = (View)this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, 0);
			
		}
		
		//Set Strong View Attributes
		view = (View)this.findViewById(R.id.reg_help_strong_pswd);
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
		View view = (View)this.findViewById(R.id.reg_help_weak_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level1, 
					R.string.account_info_help_action1, 
					R.string.account_info_help_pswd_msg1, R.drawable.rounded_red_bar, R.drawable.x_red);		
			
		}
		
		//Set Moderate View Attributes
		view = (View)this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, 0);		
			
		}
		
		//Set Strong View Attributes
		view = (View)this.findViewById(R.id.reg_help_strong_pswd);
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
	private void setViewItemAttr(View view, int level, int action, int msg, int image, int image2 ) {	
		if( null != view ) {
			TextView levelTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_level);
			TextView actionTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_action);
			TextView msgTxtVw = (TextView)view.findViewById(R.id.reg_help_strength_msg);
			View colorBarVw = (View)view.findViewById(R.id.reg_help_strength_image);
		
			if( null != levelTxtVw)
				levelTxtVw.setText(level);
			
			if( null != actionTxtVw)
				actionTxtVw.setText(action);
			
			if( null != msgTxtVw)
				msgTxtVw.setText(msg);
			
			if( null != colorBarVw ) {
			
				Drawable colorBar = (Drawable)getResources().getDrawable( image );
				if(null != colorBar ) 
					colorBarVw.setBackgroundDrawable(colorBar);
				
			}	
			
			//If resource id is not 0 then display image
			if( 0 != image2 ) {
				View checkVw = (View)view.findViewById(R.id.reg_help_strong_check);
				if( null != checkVw)  {
					Drawable strengthIcon = (Drawable)getResources().getDrawable( image2 );
					checkVw.setBackgroundDrawable(strengthIcon);		
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
	public void goBack() {
		setResult(RESULT_OK);
		finish();
	}
	
}
