package com.discover.mobile.card.common.ui;

import java.util.List;

import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.common.ErrorListener;
import com.discover.mobile.card.common.SuccessListener;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.error.ErrorHandler;

/**
 * ©2013 Discover Bank
 * 
 * Abstract Class extending {@link NotLoggedInRoboActivity}. All pre login
 * Activities need to extend this class
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public abstract class CardNotLoggedInCommonActivity extends
        NotLoggedInRoboActivity implements SuccessListener, ErrorListener,
        CardErrorHandlerUi {

    @Override
    public TextView getErrorLabel() {
        return null;
    }

    @Override
    public List<EditText> getInputFields() {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void goBack() {

    }

    @Override
    protected void showErrorModal(final int titleText, final int bodyText,
            final boolean finishActivityOnClose) {

        super.showErrorModal(titleText, bodyText, finishActivityOnClose);
    }

}
