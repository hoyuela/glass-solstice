package com.discover.mobile.card.auth.strong;

import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.common.DiscoverActivityManager;

public abstract class StrongAuthDefaultResponseHandler implements
		StrongAuthListener {

	private CardErrorHandlerUi errorHandlerUI = null;
	private CardErrorCallbackListener errorCallbackListener;
	public StrongAuthDefaultResponseHandler(CardErrorHandlerUi errorHandlerUI) {
		super();
		this.errorHandlerUI = errorHandlerUI;
	}

	@Override
    public abstract void onStrongAuthSucess(final Object data);

    @Override
    public void onStrongAuthSkipped(final Object data) {
    	if (null!=errorHandlerUI && errorHandlerUI instanceof CardErrorHandlerUi)
    	{
    		errorCallbackListener = new CardErrorCallbackListener() {
                @Override
                public void onButton1Pressed() {
                	//Utils.logoutUser(DiscoverActivityManager.getActiveActivity(), false);
                	DiscoverActivityManager.getActiveActivity().onBackPressed();
                }

				@Override
				public void onButton2Pressed() {
					
				}
            };
	        final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
	        		errorHandlerUI);
	        cardErrorResHandler.handleCardError((CardErrorBean) data, errorCallbackListener);
    	}
    }

//    @Override
//    public void onStrongAuthError(final Object data) {
//    	if (null!=errorHandlerUI && errorHandlerUI instanceof CardErrorHandlerUi)
//    	{
//	        final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
//	        		errorHandlerUI);
//	        cardErrorResHandler.handleCardError((CardErrorBean) data);
//    	}
//    }

    @Override
    public void onStrongAuthCardLock(final Object data) {
    	if (null!=errorHandlerUI && errorHandlerUI instanceof CardErrorHandlerUi)
    	{
    		errorCallbackListener = new CardErrorCallbackListener() {
                @Override
                public void onButton1Pressed() {
                	Utils.logoutUser(DiscoverActivityManager.getActiveActivity(), false);
                }

				@Override
				public void onButton2Pressed() {
					
				}
            };
    		
	        final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
	        		errorHandlerUI);
	        cardErrorResHandler.handleCardError((CardErrorBean) data, errorCallbackListener);
    	}
    }

}
