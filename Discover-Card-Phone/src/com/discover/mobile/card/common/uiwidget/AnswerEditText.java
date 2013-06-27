package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.base.Strings;

import com.discover.mobile.common.utils.CommonUtils;

import com.discover.mobile.card.common.InputValidator;

import com.discover.mobile.card.R;

/**
 * This class is an edit text which will function as Answer field.
 * 
 * This class contains functionality for answer field on Strong Auth Screen .
 * 
 * @author scottseward
 * 
 */
public class AnswerEditText extends ValidatedInputField {

    /**
     * Default constructors. Initially sets up the input field as an answer
     * field.
     */
    public AnswerEditText(final Context context) {
        super(context);
    }

    public AnswerEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerEditText(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
    }

    
    public void setErrorLabelText(String text )
    
    {
        if(errorLabel!=null)
        errorLabel.setText(text);
    }
    
    
    public String getErrorLabelText()
    {
        String errorText = "";
        if(errorLabel!=null)
        errorText = errorLabel.getText().toString();
        return errorText;
        
    }
    
    @Override
    public boolean isValid() {
        
        boolean valid =  true ;
        if(isNull()){
            setErrorLabelText(this.getResources().getString(R.string.answer_not_entered)); 
            valid = false ;
        }else if(isSpaceEntered()){
            setErrorLabelText(this.getResources().getString(R.string.space_entered));
            valid = false ;
        }else if(isSpecialCharacterEntered()){
            setErrorLabelText(this.getResources().getString(R.string.special_characters_entered));
            valid = false ;
        }else
        {
            valid = true ;
        }
        
        return valid ;
    }
    /**
     * This method validates the current input for the letters and numeric value
     */
   
    public boolean isNull(){
        
        return Strings.isNullOrEmpty(this.getText().toString());
    }
    
    public boolean isSpecialCharacterEntered() {
        final String currentInput = this.getText().toString();
        boolean isValid = false;
        isValid = InputValidator.validateAnswerField(currentInput);
        return isValid;
    }

    /**
     * This method validates the current input for space entered
     */
    public boolean isSpaceEntered() {
        final String currentInput = this.getText().toString();
        boolean isSpaceEntered = false;

        if (currentInput.contains(" ")) {
            isSpaceEntered = true;
        } else {
            isSpaceEntered = false;
        }
        return isSpaceEntered ;
    }

    @Override
    protected void showErrorLabel() {
        // TODO Auto-generated method stub
        if (errorLabel != null) {
            if(!isNull())
            errorLabel.setVisibility(View.VISIBLE);
        }
    }
   

    
}
