package com.discover.mobile.card.common.net.error;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
/**
 * 
 * ©2013 Discover Bank
 *
 *handling errors and exceptions.
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
   
    public static final int USER_ACCOUNT_LOCKED = 403;
    public static final int SERVICE_UNDER_MAINTENANCE = 503;
    
    /**
     * Private constructor to prevent construction without a fragment or
     * activity
     */
    public CardErrorResponseHandler(final CardErrorHandlerUi errorHandlerUi) {
        this.errorHandlerUi = errorHandlerUi;
        
    }
    
    /**
     * Handle Card error
     * 
     * @param cardErrorHold
     */
    public void handleCardError(final CardErrorBean cardErrorHold) {
        if (cardErrorHold.isAppError()) {
            handleAppError("Application Error", cardErrorHold.getErrorMessage());

        } else {
        	String errorCode=cardErrorHold.getErrorCode();
        	String []errorMsgSplit=errorCode.split("_");
            final int errorCodeNumber = Integer.parseInt(errorMsgSplit[0]);
            
           
            switch (errorCodeNumber) {
            case INCORRECT_USERID_PASSWORD:// for inline error messages
                handleInlineError(cardErrorHold.getErrorMessage());
                break;

            default:
                handleGenericError(cardErrorHold.getErrorTitle(),
                        cardErrorHold.getErrorMessage());

                break;
            }
        }

    }

    /**
     * Handle inline error
     * 
     * @param errorMessage
     */
    private void handleInlineError(final String errorMessage) {
        setErrorText(errorMessage);
        setInputFieldsDrawableToRed();
        clearInputs();

    }

    /**
     * Handle generic error
     * 
     * @param errorTitle
     * @param errorText
     */
    private void handleGenericError(final String errorTitle,
            final String errorText) {

        sendToErrorPage(errorTitle, errorText);

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
     * Set the input fields to be highlighted in red.
     */
    private void setInputFieldsDrawableToRed() {
        final CardErrorHandlerUi errorHandlerUi = getErrorFieldUi();
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
            for (final EditText text : errorHandlerUi.getInputFields()) {
                text.setBackgroundResource(R.drawable.edit_text_red);
            }
        }
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
    private void sendToErrorPage(final String title, final String errorText) {

        final CardErrHandler handler = getErrorFieldUi().getCardErrorHandler();
        final AlertDialog dialog = handler.createErrorModal(title, errorText);
        handler.showCustomAlert(dialog);

    }

    private void showNativeAlert(final String title, final String errorText) {

        final AlertDialog.Builder alertDialogBuilder;
        

            alertDialogBuilder = new AlertDialog.Builder(errorHandlerUi.getContext());

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
