package com.discover.mobile.card.common.ui;

import android.drm.DrmManagerClient.OnErrorListener;


import com.discover.mobile.card.common.ErrorListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;

import com.discover.mobile.card.error.CardErrorHandlerUi;

import com.discover.mobile.common.LoggedInRoboActivity;
import com.discover.mobile.common.error.ErrorHandler;

public abstract class CardLoggedinCommonActivity extends LoggedInRoboActivity implements ErrorListener, CardErrorHandlerUi  {

	@Override
	public int getBehindContentView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OnError(Object data) 
	{
		if (data.getClass() == CardErrorBean.class) {
			CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
					(CardLoggedinCommonActivity) this);
			cardErrorResHandler.handleCardError((CardErrorBean) data);

		}
	}
}
