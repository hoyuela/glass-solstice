package com.discover.mobile.card.common.net.error;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Spannable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.card.common.ui.modals.ModalAlertWithTwoButtons;
import com.discover.mobile.card.common.ui.modals.ModalDefaultTopView;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.NavigateToLoginOnDismiss;
import com.discover.mobile.common.facade.FacadeFactory;

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

public class CardErrorUIWrapper implements CardErrHandler
{

    static final CardErrHandler instance = new CardErrorUIWrapper();

    /**
     * Constructor
     */
    private CardErrorUIWrapper()
    {
    }

    /**
     * Create instance of ErrorHandler
     * 
     * @return ErrorHandler
     */
    public static CardErrHandler getInstance()
    {
        return instance;
    }

    public ModalAlertWithOneButton createErrorModal(final String titleText,
            final String errorText, final String footerStatus) {
        return createErrorModal(titleText, errorText, footerStatus, null);
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
            final String errorText, final String footerStatus,
            final CardErrorCallbackListener cardErrorCallbackListener) {
        final Activity activeActivity = DiscoverActivityManager
                .getActiveActivity();

        // Keep track of times an error page is shown for login
        TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

        // Decide on what help number to show
        final int helpResId = R.string.need_help_number_text;

        final Spannable sp = Spannable.Factory.getInstance().newSpannable(errorText);

        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);

        // Create a one button modal with text as per parameters provided
        final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(activeActivity, titleText, sp.toString(), true, helpResId, R.string.ok);

        final ModalDefaultTopView view = (ModalDefaultTopView) modal.getTop();

        final int footerValue = Integer.parseInt(footerStatus);
        view.hideFeedbackView(); // to hide feedback footer for all cases
        switch (footerValue) {
        case 0:// to hide only need help footer
            view.hideNeedHelpFooter();
            break;

        case 1: // to hide only feedback footer
            view.hideFeedbackView();
            break;

        case 2:// to hide both need help and feedback footer
            view.hideNeedHelpFooter();
            view.hideFeedbackView();

            break;

        case 3:
            // to show both help and feed back
            break;

        }
        modal.getBottom().getButton().setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(final View v) {
                // Log.d("ondismiss dialog",
                // "activity name"+activeActivity.getComponentName());

                Log.d("ondismiss dialog",
                        "activity name" + activeActivity.getComponentName());

                modal.getOrientationEventListener().disable();

                if (null != cardErrorCallbackListener) {
                    cardErrorCallbackListener.onButton1Pressed();
                    modal.dismiss();
                } else {
                    // footerValue == 4 for 503/5031006/5031007/5031008
                    // ok button will navigate to logout activity

                    if ((activeActivity instanceof CardNavigationRootActivity)
                            && footerValue == 4) {
                        Log.d("ondismiss dialog", "call logout activity");
                        modal.dismiss();
                        final Bundle bundle = new Bundle();
                        bundle.putBoolean(
                                IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE,
                                true);
                        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
                        FacadeFactory.getLoginFacade().navToLoginWithMessage(
                                activeActivity, bundle);
                    } else {
                        Log.d("ondismiss dialog", "modal dismiss");
                        modal.dismiss();
                    }
                }
            }
        });

        // Show one button error dialog
        return modal;
    }

    @Override
    public ModalAlertWithTwoButtons createErrorModalWithTwoButton(final String titleText, final String errorText, final String footerStatus)
    {
    	return createErrorModalWithTwoButton(titleText, errorText, footerStatus, null);
    }
    
    @Override
    public ModalAlertWithTwoButtons createErrorModalWithTwoButton(final String titleText, final String errorText,
    		final String footerStatus, final CardErrorCallbackListener errorClickCallback)
    {

        final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

        // Keep track of times an error page is shown for login
        TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);

        // Decide on what help number to show
        final int helpResId = R.string.need_help_number_text;

        /*
         * final Spannable sp =
         * Spannable.Factory.getInstance().newSpannable(errorText);
         * 
         * Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
         */

        // Create a one button modal with text as per parameters provided
        final ModalAlertWithTwoButtons modal = new ModalAlertWithTwoButtons(activeActivity, titleText, errorText, 
        		true, helpResId, R.string.register_btn, R.string.cancel_text);

        final ModalDefaultTopView view = (ModalDefaultTopView) modal.getTop();

        final int footerValue = Integer.parseInt(footerStatus);

        switch (footerValue)
        {

        default:// to hide both need help and feedback footer
            view.hideNeedHelpFooter();
            view.hideFeedbackView();

            break;

        }

        modal.getBottom().getOkButton().setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(final View v)
            {
                {
                    //Big Browser
                    modal.dismiss();
                    if (null != errorClickCallback) {
                    	errorClickCallback.onButton1Pressed();
                    }
                    
                }

            }
        });

        modal.getBottom().getCancelButton().setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(final View v)
            {
                {
                    Log.d("ondismiss dialog", "modal dismiss");
                    modal.dismiss();
                    if (null != errorClickCallback) {
                    	errorClickCallback.onButton2Pressed();
                    }
                }

            }
        });

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
    public ModalAlertWithOneButton handleLockedOut(final CardErrorHandlerUi errorHandlerUi, final String errorText, final String title)
    {
        final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

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
    public void showErrorsOnScreen(final CardErrorHandlerUi errorHandlerUi, final String errorText)
    {
        // TODO Auto-generated method stub
        if (errorHandlerUi != null)
        {
            errorHandlerUi.getErrorLabel().setText(errorText);
            errorHandlerUi.getErrorLabel().setVisibility(View.VISIBLE);
            errorHandlerUi.getErrorLabel().setTextColor(
                    DiscoverActivityManager.getActiveActivity().getResources()
                            .getColor(R.color.error_indicator));
        }

        // Set the input fields to be highlighted in red and clears text
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null)
        {
            for (final EditText text : errorHandlerUi.getInputFields())
            {
                text.clearFocus();
                text.setBackgroundResource(R.drawable.edit_text_red);
                // text.setText("");
            }

            // Set Focus to first field in screen
            // errorHandlerUi.getInputFields().get(0).requestFocus();
        }

    }

    /**
     * clear text
     */
    @Override
    public void clearTextOnScreen(final CardErrorHandlerUi errorHandlerUi)
    {

        // Hide error label and display error text
        if (errorHandlerUi != null && errorHandlerUi.getErrorLabel() != null)
        {
            errorHandlerUi.getErrorLabel().setVisibility(View.GONE);
        }

        // Set the input fields to be highlighted in red and clears text
        if (errorHandlerUi != null && errorHandlerUi.getInputFields() != null)
        {
            // Set Focus to first field in screen
            errorHandlerUi.getInputFields().get(0).requestFocus();

            for (int i = errorHandlerUi.getInputFields().size() - 1; i >= 0; i--)
            {
                final EditText text = errorHandlerUi.getInputFields().get(i);
                text.setText("");
                text.setBackgroundResource(R.drawable.edit_text_default);
            }

        }

    }

    @Override
    public void handleHttpForbiddenError()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleHttpUnauthorizedError()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleSessionExpired()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleGenericError(final int httpErrorCode)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleLoginAuthFailure(final CardErrorHandlerUi errorHandlerUi, final String errorMessage)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public ModalAlertWithOneButton handleLockedOut(final CardErrorHandlerUi errorHandlerUi, final String errorText)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpInternalServerErrorModal()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpFraudNotFoundUserErrorModal(final CardErrorHandlerUi mErrorHandlerUi, final String message)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton createErrorModal(final int errorCode, final int titleText, final int errorText)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModalAlertWithOneButton handleHttpServiceUnavailableModal(final String errorText)
    {
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
    public void showCustomAlert(final AlertDialog alert)
    {
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
        alert.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public ModalAlertWithOneButton createErrorModal(String titleText, String errorText)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.card.error.CardErrHandler#createErrorModalWithTwoButton
     * (int, int, int)
     */

}
