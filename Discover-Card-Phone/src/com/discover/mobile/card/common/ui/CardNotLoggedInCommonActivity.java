package com.discover.mobile.card.common.ui;

import java.util.List;

import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.ErrorListener;
import com.discover.mobile.card.common.SuccessListener;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.error.ErrorHandler;

import com.discover.mobile.card.error.CardErrorHandlerUi;



public abstract class CardNotLoggedInCommonActivity extends NotLoggedInRoboActivity  implements SuccessListener,ErrorListener,CardErrorHandlerUi {

	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void goBack() {
		// TODO Auto-generated method stub
		
	}

	protected void showErrorModal(int titleText, int bodyText,
			boolean finishActivityOnClose) {
		// TODO Auto-generated method stub
		super.showErrorModal(titleText, bodyText, finishActivityOnClose);
	}
	
	
	//public abstract CardErrHandler getCardErrorHandler() ;
	
        
	
}
