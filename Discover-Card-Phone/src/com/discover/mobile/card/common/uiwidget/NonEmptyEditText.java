package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.base.Strings;

public class NonEmptyEditText extends ValidatedInputField {

    /**
     * Default constructor
     * 
     * @param context
     *            the context of use for the EditText.
     */
    public NonEmptyEditText(final Context context) {
        super(context);
    }

    public NonEmptyEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NonEmptyEditText(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isValid() {
        return !Strings.isNullOrEmpty(this.getText().toString());
    }
    
    /* 13.4 Defect ID 104309 start */
    
    /**
     * This method validates the current input for space entered
     */
    public boolean isSpaceEntered() {
        final String currentInput = this.getText().toString();
        boolean isSpaceEntered = false;

        String trimInput = currentInput.trim();
        
        if(trimInput.length()==0){
        	isSpaceEntered = true ;
        }else{
        	isSpaceEntered = false ;
        }
        
        /* if (currentInput.contains(" ")) {
             isSpaceEntered = true;
         } else {
             isSpaceEntered = false;
         }*/
       /*  if(currentInput.startsWith(" ")||currentInput.endsWith(" ")){
         	isSpaceEntered = true;
         }else
         {
         	isSpaceEntered = false;
         }*/
         /* 13.4 Defect ID 104309 End */	
         
        return isSpaceEntered;
    }
    /* 13.4 Defect ID 104309 End */

    /* 13.4 Baclklog items start*/
    
	@Override
	protected void showErrorLabel() {
		// TODO Auto-generated method stub
		if (errorLabel != null) {
			if (isNull()) {
				errorLabel.setVisibility(View.GONE);
			} else
			{
				errorLabel.setVisibility(View.VISIBLE);
			}
		}

	}
	  /*    13.4 Baclklog items end*/
}
