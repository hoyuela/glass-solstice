package com.discover.mobile.register;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;

public class AccountInformationTwoActivity extends Activity{
	int passStrength = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info_two);
		EditText idField = (EditText)findViewById(R.id.account_info_two_id_field);
		idField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				updateBars(s);
				Log.d("Update Bar With Chars", s.toString());
			}
			
		});
	}
	
	
	//Currently setup only for a single user id input.
	public void updateBars(CharSequence inputSequence){
		boolean hasGoodLength  = false;
		boolean hasUpperCase   = false;
		boolean hasLowerCase   = false;
		boolean hasNonAlphaNum = false;
		int 	numOfBars 	   = 0;
		
		//Check length of input.
		if(inputSequence.length() > 6 && inputSequence.length() <= 16)
			hasGoodLength = true;
		
			TextView label = (TextView)findViewById(R.id.strength_bar_label);
			View barOne   = (View)findViewById(R.id.strength_bar_one);
			View barTwo   = (View)findViewById(R.id.strength_bar_two);
			View barThree = (View)findViewById(R.id.strength_bar_three);
			
			for(int i = 0; i < inputSequence.length(); ++i){
				
				if(Character.isLowerCase(inputSequence.charAt(i))){
					hasLowerCase = true;
				}
				else if (Character.isUpperCase(inputSequence.charAt(i))){
					hasUpperCase = true;
				}
				else if(!Character.isLetterOrDigit(inputSequence.charAt(i))){
					hasNonAlphaNum = true;
				}
			}
			
			if(hasUpperCase)
				++numOfBars;
			if(hasLowerCase)
				++numOfBars;
			if(hasNonAlphaNum)
				++numOfBars;
			
			//If the length is bad then only set one bar.
			if(!hasGoodLength)
				numOfBars = 1;
			
			if(numOfBars > 2){
				barOne.setBackgroundColor(getResources().getColor(R.color.green));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.green));
				barThree.setBackgroundColor(getResources().getColor(R.color.green));
				label.setText(getResources().getString(R.string.strength_bar_strong));
			}
			else if(numOfBars > 1){
				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_moderate));
			}
			else if(numOfBars > 0){
				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.strength_bar_not_valid));
			}
			else{
				barOne.setBackgroundColor(getResources().getColor(R.color.gray));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				label.setText(getResources().getString(R.string.EMPTY));
			}
			
	}

}
