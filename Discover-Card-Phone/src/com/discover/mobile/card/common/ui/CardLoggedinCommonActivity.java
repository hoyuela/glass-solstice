package com.discover.mobile.card.common.ui;

import com.discover.mobile.card.common.ErrorListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.common.LoggedInRoboActivity;
import com.discover.mobile.common.error.ErrorHandler;

/**
 * ©2013 Discover Bank
 * 
 * Abstract Class extending {@link LoggedInRoboActivity}. All After login
 * Activities need to extend this class
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public abstract class CardLoggedinCommonActivity extends LoggedInRoboActivity
        implements ErrorListener, CardErrorHandlerUi {

    @Override
    public int getBehindContentView() {
        return 0;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void OnError(final Object data) {
        if (data.getClass() == CardErrorBean.class) {
            final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                    this);
            cardErrorResHandler.handleCardError((CardErrorBean) data);

        }
    }
}
