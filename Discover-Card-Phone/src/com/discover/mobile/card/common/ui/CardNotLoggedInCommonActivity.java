package com.discover.mobile.card.common.ui;

import java.util.List;

import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.error.ErrorHandler;

import com.discover.mobile.card.common.ErrorListener;
import com.discover.mobile.card.common.SuccessListener;

import com.discover.mobile.card.error.CardErrorHandlerUi;

public abstract class CardNotLoggedInCommonActivity extends
        NotLoggedInRoboActivity implements SuccessListener, ErrorListener,
        CardErrorHandlerUi {

    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void goBack() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void showErrorModal(final int titleText, final int bodyText,
            final boolean finishActivityOnClose) {
        // TODO Auto-generated method stub
        super.showErrorModal(titleText, bodyText, finishActivityOnClose);
    }

}
