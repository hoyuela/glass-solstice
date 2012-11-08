package com.discover.mobile.forgotuidpassword;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.login.CustomArrayAdapter;
import com.discover.mobile.register.AccountInformationActivity;

@ContentView(R.layout.forgot_login)
public class ForgotCredentialsActivity extends RoboListActivity {
	private String[] vals = {"Forgot User ID","Forgot Password","Forgot Both"};

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	/*
	 * Using onResume so that if a user presses the back button
	 * the list fields will get reset instead of staying blue.
	 */
	@Override
	public void onResume(){
		super.onResume();
//		this.setContentView(R.layout.forgot_login);
//		final ListView listView = (ListView)findViewById(android.R.id.list);

//		ArrayAdapter<String> adapter;
		
//		adapter = new CustomArrayAdapter(this, vals);
//		listView.setAdapter(adapter);
		
		setListAdapter(new CustomArrayAdapter(this, vals));
	}
	
	
	@SuppressWarnings("deprecation")
	public void navigateToAppropriateScreen(View v){
		/*
		 * On click we get passed the view that called got clicked.
		 * We know its a linear layout because that is what is defined in the
		 * custom table cell layout. We are referencing the selected table as
		 * a whole.
		 */
		LinearLayout layout = (LinearLayout)v;
		
		/*
		 * Get the first text label (child) in the view so we can identify it.
		 */
		  
		TextView text = (TextView)layout.getChildAt(0);
		 
		 /* 
		  * Set the selection background color
		  * This gets overrided when onClick is called from XML - so without it,
		  * you cant see what element you clicked.
		  * 
		  * Using depreciated method because the drawBackground(drawable) is for
		  * API 16 and we need API 10
		  */
		v.setBackgroundDrawable(getResources()
				.getDrawable(R.drawable.rounded_table_corners_blue_no_padding));
		/*
		 * Start the new activity and pass the kind of layout that it should
		 * abide to.
		 */
		String screenType = text.getText().toString();
		handleSelection(screenType);
		
	}
	
	/*
	 * Check to see what was selected by the string value of the
	 * label text on the list item. Navigate based on that.
	 */
	public void handleSelection(String screenType){
		Intent forgotSomethingScreen = null;

		if(vals[0].equals(screenType)){
			forgotSomethingScreen = 
				new Intent(this, ForgotUserIdActivity.class);
		}
		else if(vals[1].equals(screenType)){
			forgotSomethingScreen = 
					new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra("screenType", "Forgot Password");
		}
		else if(vals[2].equals(screenType)){
			forgotSomethingScreen = 
					new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra("screenType", "Forgot Both");
		}

		if(forgotSomethingScreen != null)
			startActivity(forgotSomethingScreen);
	}
	
}
