package com.discover.mobile.card.common.net.error;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Spannable;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.NavigateToLoginOnDismiss;

import com.discover.mobile.card.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.card.common.ui.modals.ModalDefaultTopView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class is responsible for displaying custom error dialogs.
 * 
 * 
 * @author CTS
 * 
 * @version 1.0
 */

public class CardErrorUIWrapper implements CardErrHandler {

    static final CardErrHandler instance = new CardErrorUIWrapper();

    /**
     * Constructor
     */
    private CardErrorUIWrapper() {
    }

    /**
     * Create instance of ErrorHandler
     * 
     * @return ErrorHandler
     */
    public static CardErrHandler getInstance() {
        return instance;
    }

    /**
     * create custom alert for error popup
     * 
     * @param Title
     *            text
     * @param error
     *            text
     * @return Modal Alert dialog with one button
     */
    @Override
    public ModalAlertWithOneButton createErrorModal(final String titleText,
            final String errorText) {
        final Activity activeActivity = DiscoverActivityManager
                .getActiveActivity();

        // Keep track of times an error page is shown for login
        TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

        // Decide on what help number to show
        final int helpResId = R.string.need_help_number_text;

        final Spannable sp = Spannable.Factory.getInstance().newSpannable(
                errorText);

        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);

        // Create a one button modal with text as per parameters provided
        final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(
                activeActivity, titleText, sp.toString(), true, helpResId,
                R.string.ok);

        final ModalDefaultTopView view = (ModalDefaultTopView) modal.getTop();
        view.hideNeedHelpFooter();

        modal.getBottom().getButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                modal.dismiss();

            }
        });

        // Show one button error dialog
        return modal;
    }

    /**
     * create and handle locked out error pop-up
     * 
     * @param errorHandlerUi
     * @param errorText
     * @param title
     * @return
     */
    public ModalAlertWithOneButton handleLockedOut(
            final CardErrorHandlerUi errorHandlerUi, final String errorText,
            final String title) {
        final Activity activeActivity = DiscoverActivityManager
                .getActiveActivity();

        ModalAlertWithOneButton modal = null;

        // called during login attempt

        modal = createErrorModal(title, errorText);

        // Navigate back to login
        modal.setOnDismissListener(new NavigateToLoginOnDismiss(activeActivity));

        showCustomAlert(modal);

        // Clear text and set focus to first field
        clearTextOnScreen(errorHandlerUi);

        return modal;
    }

    /**
     * show inline error messages
     */
    @Override
    public void showErrorsOnScreen(final CardErrorHandlerUi errorHandlerUi,
            final String errorText) {
        // TODO Auto-generated method stub
        if (errorHandlerUi != null) {
            errorHandlerUi.getErrorLabel().setText(errorText);
            errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
        }

        // Set the input fields to be highlighted in red and clears text
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
            for (final EditText text : errorHandlerUi.getInputFields()) {
                text.setBackgroundResource(R.drawable.edit_text_red);
                text.setText("");
            }

            // Set Focus to first field in screen
            errorHandlerUi.getInputFields().get(0).requestFocus();
        }

    }

    /**
     * clear text
     */
    @Override
    public void clearTextOnScreen(final CardErrorHandlerUi errorHandlerUi) {

        // Hide error label and display error text
        if (errorHandlerUi != null && errorHandlerUi.getErrorLabel() != null) {
            errorHandlerUi.getErrorLabel().setVisibility(View.GONE);
        }

        // Set the input fields to be highlighted in red and clears text
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null) {
            // Set Focus to first field in screen
            errorHandlerUi.getInputFields().get(0).requestFocus();

            for (int i = errorHandlerUi.getInputFields().size() - 1; i >= 0; i--) {
                final EditText text = errorHandlerUi.getInputFields().get(i);
                text.setText("");
                text.setBackgroundResource(R.drawable.edit_text_default);
            }

        }

    }

    @Override
    public void handleHttpForbiddenError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleHttpUnauthorizedError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleSessionExpired() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleGenericError(final int httpErrorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleLoginAuthFailure(final CardErrorHandlerUi errorHandlerUi,
            final String errorMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public ModalAlertWithOneButton handleLockedOut(
            final CardErrorHandlerUi errorHandlerUi, final String errorText) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpInternalServerErrorModal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(
            final CardErrorHandlerUi mErrorHandlerUi, final String message) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton createErrorModal(final int errorCode,
            final int titleText, final int errorText) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpServiceUnavailableModal(
            final String errorText) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Show a custom modal alert dialog for the activity
     * 
     * @param alert
     *            - the modal alert to be shown
     */
    @Override
    public void showCustomAlert(final AlertDialog alert) {
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
        alert.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

}
