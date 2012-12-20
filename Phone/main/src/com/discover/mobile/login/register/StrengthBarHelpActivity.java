package com.discover.mobile.login.register;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;

public class StrengthBarHelpActivity extends NotLoggedInRoboActivity {
	private static final String TAG = StrengthBarHelpActivity.class.getSimpleName();

	
	// FIXME hardcoded strings, use ScreenType
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		
		setContentView(R.layout.register_help_id_strength_new);
	
		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			final String helpLayout = extras.getString("ScreenType");
			
			if("id".equals(helpLayout)) {					
				setHelpIdStrengthViewAttr();
			} else {		
				setHelpPswdStrengthViewAttr();
			}
		} else {
			Log.v(TAG, "No Extras Found");
		}
		

		setHelpIdStrengthViewAttr();

		
	}
	
	private void setHelpIdStrengthViewAttr() {
	
		
		//Set Moderate View Attributes
		View view = (View)this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, false);
			
		}
		
		//Set Strong View Attributes
		view = (View)this.findViewById(R.id.reg_help_strong_pswd);
		if( null != view) {
			setViewItemAttr(view, 
					R.string.account_info_help_level3, 
					R.string.account_info_help_action3, 
					R.string.account_info_help_msg3, R.drawable.rounded_green_bar, true);	
		}
	}
	
	private void setHelpPswdStrengthViewAttr() {
		//Set Weak View Attributes
		View view = (View)this.findViewById(R.id.reg_help_weak_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level1, 
					R.string.account_info_help_action1, 
					R.string.account_info_help_pswd_msg1, R.drawable.rounded_red_bar, false);		
			
		}
		
		//Set Moderate View Attributes
		view = (View)this.findViewById(R.id.reg_help_mod_pswd);
		if( null != view ) {
			setViewItemAttr(view, 
					R.string.account_info_help_level2, 
					R.string.account_info_help_action2, 
					R.string.account_info_help_msg2, R.drawable.rounded_yellow_bar, false);		
			
		}
		
		//Set Strong View Attributes
		view = (View)this.findViewById(R.id.reg_help_strong_pswd);
		if( null != view) {
			setViewItemAttr(view, 
					R.string.account_info_help_level3, 
					R.string.account_info_help_action3, 
					R.string.account_info_help_msg3, R.drawable.rounded_green_bar, true);	
		}
	}
	
	private void setViewItemAttr(View view, int level, int action, int msg, int image, boolean check ) {	
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
			
			if( check ) {
				View checkVw = (View)view.findViewById(R.id.reg_help_strong_check);
				if( null != checkVw)  {
					checkVw.setVisibility(View.VISIBLE);
				}
			}
		} else {
		
		}
	}
	
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	
	public void goBack(final View v){
		setResult(RESULT_OK);
		finish();
	}
	

	
}
