package com.discover.mobile.common.customui;

import java.util.Locale;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.R;

public class ConfirmationEditText extends ValidatedInputField {
	private static int EMS_DEFAULT = 16;
	private boolean isUserId = false;
	private EditText editTextToMatch;
	
	public ConfirmationEditText(Context context) {
		super(context);
	}
	
	public ConfirmationEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ConfirmationEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	/**
	 * When an edit text field is attached to the confirmation field, start listening to text changes on that
	 * attached field. Then update the confirmation field based on the attached field. So we have a two way
	 * real time validation system going on.
	 * 
	 * @param matchTo An EditText that we want our confirmation field to match to.
	 */
	public void attachEditTextToMatch(final EditText matchTo) {
		editTextToMatch = matchTo;
		editTextToMatch.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				if(!isValid()){
					clearErrors();
					clearRightDrawable();
				}else{
					setAppearanceMatched();
				}
			}
			
		});
	}

	/**
	 * When a user navigates away from the field, do a validation and graphic update again.
	 * If the field is yet to be matched it will still be gray, but when a user navigates away it will turn red
	 * to notify them that they need to correct it.
	 */
	@Override
	protected void setupFocusChangedListener() {
		
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && !isValid()){
					setErrors();
				}
				else{
					setRightDrawableGrayX();
					if(isInErrorState)
						setRightDrawableRedX();
				}
				
			}
		});
	}
	
	/**
	 * Setup a text changed listener to the confirmation field (this). During a user editing the field
	 * it will stay gray until it matches, then it will turn green and place a green check mark in the right
	 * drawable location of the input field.
	 */
	@Override
	protected void setupTextChangedListener() {
		final ConfirmationEditText self = this;
		
		this.addTextChangedListener(new TextWatcher() {
			String beforeText;
			String afterText;
			@Override
			public void afterTextChanged(Editable s) {
				afterText = s.toString();
				if(beforeText.equals(afterText)){
					if(isValid())
						setAppearanceMatched();
					else
						updateAppearanceForInput();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				beforeText = s.toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if(isUserId)
					CommonMethods.setInputToLowerCase(s, self);
				if(count > 0)
					isInDefaultState = false;
				
				if(isInDefaultState)
					{/*Do nothing*/}
				else if(isValid()){
					clearErrors();
					setAppearanceMatched();
					
				}
				else
					clearErrors();
				
				
			}
			
		});
	}
	
	/**
	 * Tells the confirmation field that is it meant to confirm a username.
	 * This means that the input needs to be restricted to accept only lowercase letters and numbers.
	 * @param isUserId
	 */
	public void setIsUserIdConfirmation(final boolean isUserId) {
		this.isUserId = isUserId;
	}
	
	/**
	 * The appearance of the text field when it is matching an attached EditText's input.
	 */
	private void setAppearanceMatched() {
		this.setBackgroundResource(R.drawable.edit_text_green);
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, getGreenCheck(), null);
	}
	/**
	 * Returns true if the current text matches the attached text field text.
	 */
	@Override
	public boolean isValid() {
		boolean isValid = false;

		if(editTextToMatch != null && editTextToMatch.length() > 0){			
			isValid = this.getText().toString().equals(editTextToMatch.getText().toString());
		}
		
		return isValid;
	}
	
	/**
	 * Set the current input to loweracse if it is not already.
	 * @param input
	 */
	public void setInputToLowerCase(final CharSequence input){
		
		final String inputString = input.toString();
		final String lowerCaseInput = inputString.toLowerCase(Locale.getDefault());
		
		if( !inputString.equals(lowerCaseInput)){
			this.setText(lowerCaseInput);
			this.setSelection(lowerCaseInput.length());
		}
		
	}
	
}