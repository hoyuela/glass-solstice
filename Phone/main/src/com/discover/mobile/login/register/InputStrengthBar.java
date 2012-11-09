/*
 * This class is currently NOT in use.
 * It is intended to be a custom View that would provide
 * password and user id strength bars to notify users of how
 * 'secure' their password and or user id is.
 */

package com.discover.mobile.login.register;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;

public class InputStrengthBar extends View{
	View barOne;
	View barTwo;
	View barThree;
	
	TextView strengthLabel;
	EditText textField;
	
	public InputStrengthBar(Context context){
		super(context);
	}
	
	public InputStrengthBar(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public InputStrengthBar(Context context, EditText textField, TextView strengthLabel) {
		super(context);

		this.strengthLabel = strengthLabel;
		this.textField     = textField;
		setupBars();
		setupField();
	}
	
	private void setupBars(){
		//Create default 'layout', with specified width and height.
		LayoutParams boxWidthAndHeight = new LayoutParams(32, 4);

		//Set default colors
		barOne.setBackgroundColor(getResources().getColor(R.color.gray));
		barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
		barThree.setBackgroundColor(getResources().getColor(R.color.gray));
		
		//Set width and height
		barOne.setLayoutParams(boxWidthAndHeight);
		barTwo.setLayoutParams(boxWidthAndHeight);
		barThree.setLayoutParams(boxWidthAndHeight);
		
		int boxOffset = boxWidthAndHeight.width + boxWidthAndHeight.height;
		//Move these bars to the right
		barTwo.offsetLeftAndRight(boxOffset);
		barThree.offsetLeftAndRight(boxOffset * 2);
	}
	
	private void setupField(){
		textField.addTextChangedListener(new TextWatcher(){

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
			}
			
		});
	}
	
	public void updateBars(CharSequence inputSequence){
		boolean hasGoodLength  = false;
		boolean hasUpperCase   = false;
		boolean hasLowerCase   = false;
		boolean hasNonAlphaNum = false;
		int 	numOfBars 	   = 0;
		
		//Check length of input.
		if(inputSequence.length() > 6 && inputSequence.length() <= 16)
			hasGoodLength = true;
			
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
				strengthLabel.setText(getResources().getString(R.string.strength_bar_strong));
			}
			else if(numOfBars > 1){
				barOne.setBackgroundColor(getResources().getColor(R.color.yellow));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.yellow));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				strengthLabel.setText(getResources().getString(R.string.strength_bar_moderate));
			}
			else if(numOfBars > 0){
				barOne.setBackgroundColor(getResources().getColor(R.color.red));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				strengthLabel.setText(getResources().getString(R.string.strength_bar_not_valid));
			}
			else{
				barOne.setBackgroundColor(getResources().getColor(R.color.gray));		
				barTwo.setBackgroundColor(getResources().getColor(R.color.gray));
				barThree.setBackgroundColor(getResources().getColor(R.color.gray));
				strengthLabel.setText(getResources().getString(R.string.empty));
			}
			
	}

}
