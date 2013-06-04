package com.discover.mobile.card.common.net.error;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.discover.mobile.common.DiscoverActivityManager;

import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.facade.CardLoginFacadeImpl;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityFacade;

/**
 * 
 * �2013 Discover Bank
 * 
 * handling errors and exceptions.
 * 
 * handle all types errors i.e. inline , pop up and generic.
 * 
 * @author CTS
 * 
 * @version 1.0
 */

public final class CardErrorResponseHandler {

    private CardErrorHandlerUi errorHandlerUi = null;

    /**
     * showing inline error messages
     */
    public static final int INCORRECT_USERID_PASSWORD = 401;

    public static final int LOCKOUT = 4011103;

    public static final int USER_ACCOUNT_LOCKED = 403;
    public static final int SERVICE_UNDER_MAINTENANCE = 503;
    public static final int INLINE_ERROR = 400;
    public static final int INVALID_INPUT = 500;

    // Registration module Inline Error Codes
    public static final int ID_AND_PASS_EQUAL = 1919;
    public static final int ID_AND_SSN_EQUAL = 1920;
    public static final int ID_ALREADY_TAKEN = 1921;
    public static final int ID_IS_EMPTY = 1923;
    public static final int ID_INVALID = 1924;
    public static final int PASS_EMPTY = 1925;
    public static final int PASS_INVALID = 1926;

    /**
     * Private constructor to prevent construction without a fragment or
     * activity
     */
    public CardErrorResponseHandler(final CardErrorHandlerUi errorHandlerUi) {
        this.errorHandlerUi = errorHandlerUi;

    }

    public CardErrorResponseHandler() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Handle Card error
     * 
     * @param cardErrorHold
     */
    public void handleCardError(final CardErrorBean cardErrorHold) {
        handleCardError(cardErrorHold, null);
    }

    /**
     * Handle Card error
     * 
     * @param cardErrorHold
     */
    public void handleCardError(final CardErrorBean cardErrorHold,
            final CardErrorCallbackListener errorClickCallback) {
        Utils.isSpinnerShow = true;
        Utils.hideSpinner();
        if (cardErrorHold.isAppError()) {
            handleAppError("Application Error", cardErrorHold.getErrorMessage());

        } else {
            final String errorCode = cardErrorHold.getErrorCode();
            final String[] errorMsgSplit = errorCode.split("_");
            final int errorCodeNumber = Integer.parseInt(errorMsgSplit[0]);
            switch (errorCodeNumber) {
            case INCORRECT_USERID_PASSWORD:
            case LOCKOUT:
            	if(CardLoginFacadeImpl.class.isInstance(errorHandlerUi))
            	{
            		final LoginActivityFacade loginFacade = FacadeFactory
    						.getLoginFacade();
    				final Bundle bundle = new Bundle();
    				bundle.putString(IntentExtraKey.SHOW_ERROR_MESSAGE,
    						cardErrorHold.getErrorMessage());
    				loginFacade.navToLoginWithMessage(
    						DiscoverActivityManager.getActiveActivity(), bundle);
            	}
            	else
            	{
         
            handleInlineError(cardErrorHold.getErrorMessage());
            	}
               break;

            

            default:
                handleGenericError(cardErrorHold.getErrorTitle(),
                        cardErrorHold.getErrorMessage(),
                        cardErrorHold.getNeedHelpFooter(), errorClickCallback);

                break;
            }
        }



    }

    private void handleInlineError(final String errorMessage) {
    	setErrorText(errorMessage);
        // setInputFieldsDrawableToRed();
        getErrorFieldUi().getCardErrorHandler().showErrorsOnScreen(
                getErrorFieldUi(), errorMessage);
        clearInputs();
		
	}

	/**
     * Handle generic error
     * 
     * @param errorTitle
     * @param errorText
     */
    private void handleGenericError(final String errorTitle,
            final String errorText, final String footerStatus,
            final CardErrorCallbackListener errorClickCallback) {

        final int status = Integer.parseInt(footerStatus);
        switch (status) {

        case 101:
            sendToTwoButtonErrorModal(errorTitle, errorText, footerStatus,
                    errorClickCallback);
            break;
        default:
            sendToErrorPage(errorTitle, errorText, footerStatus,
                    errorClickCallback);
            break;

        }

    }

    private void sendToTwoButtonErrorModal(final String errorTitle,
            final String errorText, final String footerStatus,
            final CardErrorCallbackListener errorClickCallback) {
        final CardErrHandler handler = getErrorFieldUi().getCardErrorHandler();
        final AlertDialog dialog = handler.createErrorModalWithTwoButton(
                errorTitle, errorText, footerStatus, errorClickCallback);
        handler.showCustomAlert(dialog);

    }

    /**
     * Handle app error
     * 
     * @param errorTitle
     * @param errorText
     */
    public void handleAppError(final String errorTitle, final String errorText) {
        showNativeAlert(errorTitle, errorText);
    }

    /**
     * Clears the activity edit texts in the getFieldsToClearAfterError()
     * interface method
     */
    private void clearInputs() {
        final CardErrorHandlerUi errorHandlerUi = getErrorFieldUi();
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
            for (final EditText text : errorHandlerUi.getInputFields()) {
                text.setText("");
            }
        }
    }

    /**
     * A common method to display an error message on the error field and make
     * the error label visible.
     * 
     * The activity needs to implement ErrorFieldActivity for this functionality
     * to work.
     * 
     * @param text
     * @param errorText
     */
    private void setErrorText(final String errorText) {
        final CardErrorHandlerUi errorHandlerUi = getErrorFieldUi();
        if (errorHandlerUi != null) {
            errorHandlerUi.getErrorLabel().setText(errorText);
            errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
        }
    }

    /**
     * If an error field activity, we can do more .. like display error text
     * highlight fields red, etc.
     * 
     * Returns the activity, fragment if they implement the ErrorHandlerUI
     * Otherwise returns null;
     * 
     * @return
     */
    private CardErrorHandlerUi getErrorFieldUi() {
        return errorHandlerUi;
    }

    /**
     * Show a custom modal alert dialog for the activity
     * 
     * @param alert
     *            - the modal alert to be shown
     */
    public void showCustomAlert(final AlertDialog alert) {
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
        alert.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * A common method used to forward user to error page with a given static
     * string text message
     * 
     * @param errorText
     */
    private void sendToErrorPage(final String title, final String errorText,
            final String footerStatus,
            final CardErrorCallbackListener errorClickCallback) {

        final CardErrHandler handler = getErrorFieldUi().getCardErrorHandler();
        final AlertDialog dialog = handler.createErrorModal(title, errorText,
                footerStatus, errorClickCallback);
        handler.showCustomAlert(dialog);

    }

    private void showNativeAlert(final String title, final String errorText) {

        final AlertDialog.Builder alertDialogBuilder;

        alertDialogBuilder = new AlertDialog.Builder(
                DiscoverActivityManager.getActiveActivity());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder.setMessage(errorText).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
