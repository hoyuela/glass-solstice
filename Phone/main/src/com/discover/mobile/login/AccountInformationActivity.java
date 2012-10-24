package com.discover.mobile.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.discover.mobile.R;

public class AccountInformationActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.enter_account_information);
			setupAdapters();
	}
	
	private void setupAdapters(){
		Spinner spinner;
		ArrayAdapter<CharSequence> adapter;
		
		spinner =  (Spinner) findViewById(R.id.account_info_month_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.month_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.account_info_year_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);
		
	}

}
