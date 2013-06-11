package com.discover.mobile.card.auth.strong;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.EnhanceSecurityConstant;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.help.HelpItemGenerator;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.net.error.RegistrationErrorCodes;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.uiwidget.NonEmptyEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.login.register.ForgotCredentialsActivity;
import com.discover.mobile.card.services.auth.strong.StrongAuthAns;

import com.fasterxml.jackson.core.JsonGenerationException;

/**
 * Class Description of EnhancedAccountSecurity
 * 
 * EnhancedAccountSecurity Activity is the Activity that is used to handle
 * Strong Auth actions. It should always be launched through a
 * startActivityForResult(), as it is essentially a modal view and does control
 * any further navigation itself. During the use of the overall application,
 * Strong Auth may be required for a user to complete certain actions. When
 * Strong Auth is required, it will prevent the user from completing their
 * action until the Strong Auth question is answered correctly.
 * 
 * This Activity is passed its question and question ID as extras. So when the
 * activity is launched it will set its question text label to be the question
 * that was passed in the extras. When the question is answered and submitted a
 * POST request is done to send the question response to the server, if the
 * answer was correct then this Activity will finish with RESULT_OK and return
 * control to the calling Activity. If the user cannot answer the question
 * correctly or presses the hardware back button, this Activity will finish with
 * RESULT_CANCELED and return control to the calling activity. The caller will
 * decide how to handle the responses from this Activity.
 * 
 * @author scottseward
 * 
 */

// @ContentView(R.layout.strongauth_page)
public class EnhancedAccountSecurityActivity extends
        CardNotLoggedInCommonActivity implements EnhanceSecurityConstant {

    /**
     * Field Description of HELP_DROPDOWN_LINE_HEIGHT The Strong Auth screen has
     * an expandable menu that provides help to the user, this value is used to
     * define the number of vertical lines that the menu will occupy when it is
     * expanded. (When collapsed it is set to 0)
     */    

    private static final String TAG = EnhancedAccountSecurityActivity.class
            .getSimpleName();
    // Defect id 95164
    public static final String FORGOT_BOTH_FLOW = "forgotbothflow";
    public static final String FORGOT_PASSWORD_FLOW = "forgotpasswordflow";
    // Defect id 95164
    private String strongAuthQuestion;
    private String strongAuthQuestionId;
    /**
     * Holds a reference to a BankStrongAuthDetails which is provide after
     * requesting a Strong Challenge Question via an Intent in the onResume()
     * method of this activity or via updateQuestion().
     */

    
    private RadioGroup securityRadioGroup;    
    private TextView questionLabel;
    
    /**
     * Holds reference to the button that triggers the NetworkServiceCall<> to
     * POST the answer in the TextView with id
     * account_security_question_answer_field.
     */
    private Button continueButton;

    private String inputErrorText;
    private int inputErrorVisibility;
    

    // INPUT FIELDS
    private NonEmptyEditText questionAnswerField;

    // RADIO BUTTONS
    private RadioButton radioButtonOne;
    private RadioButton radioButtonTwo;

    // ERROR LABELS
    private TextView serverErrorLabel;
    private TextView errorMessage;

    // SCROLL VIEW
    private ScrollView mainScrollView;

    private int activityResult = RESULT_CANCELED;

    private static final String SERVER_ERROR_VISIBILITY = "a";
    private static final String SERVER_ERROR_TEXT = "c";
    private static final String ANSWER_ERROR_VISIBILITY = "b";
    private static final String ANSWER_ERROR_TEXT = "d";
    
    /**
     * Minimum string length allowed to be sent as an answer to a Strong Auth
     * Challenge Question
     */
    private static final int MIN_ANSWER_LENGTH = 1;

    private CardEventListener authAnsListener;
    private int questionAttemptCounter = 0;
    public static final int STRONG_AUTH_LOCKED = 0x11;

    // Tool tip Menu
    private HelpItemGenerator helpNum, helpInfo, helpFaq;
    private HelpWidget help;
    private StrongAuthListener authListener;

    // Defect id 95164
    // Back handling
    private boolean forgotBoth = false;
    private boolean forgotPassword = false;
    // Defect id 95164

    /**
     * Callback to watch the text field for empty/non-empty entered text from
     * user
     */
    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start,
                final int before, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start,
                final int before, final int after) {
            EnhancedAccountSecurityActivity.this.onTextChanged(s);
        }

        @Override
        public void afterTextChanged(final Editable s) {
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.strongauth_page);
        loadAllViews();
        setupRadioGroupListener();
        TrackingHelper.trackPageView(AnalyticsPage.STRONG_AUTH_FIRST_QUESTION);
        Utils.hideSpinner();
        restoreState(savedInstanceState);

        // Disabling continue button only applies to Bank
       // if (Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT)
        {
            // Add text change listener to determine when the user has entered
            // text
            // to enable/disable continue button
            questionAnswerField.addTextChangedListener(mTextWatcher);

            // Disable continue button by default
            continueButton.setEnabled(false);
        }
        
        // Adding Tool Tip menu
        // setupClickableHelpItem();
        /**
         * It's Listener for strong auth ans webservice call
         */
        authAnsListener = new CardEventListener() {

            @Override
            public void onSuccess(Object data) {
                // Strong Authentication successed, get back to last activity
                if (authListener != null) {
                    authListener.onStrongAuthSucess(data);
                }
                // activityResult = RESULT_OK;
                // finish();
            }

            @Override
            public void OnError(Object data) {
                CardErrorBean bean = (CardErrorBean) data;

                /**
                 * if ans is incorrect then ask user to enter it again if ans is
                 * wrong consecutive three times then get the another question
                 **/
                if (!bean.isAppError()
                        && bean != null
                        && bean.getErrorCode()
                                .contains(
                                        ""
                                                + RegistrationErrorCodes.INCORRECT_STRONG_AUTH_ANSWER)) {
                    Utils.log(TAG, "question text " + bean.getQuestionText());
                    questionAttemptCounter++;

                    // If there is consecutive 3 ans wrong change the question
                    if (questionAttemptCounter == 3) {
                        // change question
                        if (bean.getQuestionText() != null) {
                            strongAuthQuestion = bean.getQuestionText();
                            questionLabel.setText(strongAuthQuestion);
                            strongAuthQuestionId = bean.getQuestionId();
                            // submitSecurityInfo(null);
                        }
                    } else {
                        errorMessage
                                .setText(R.string.account_security_answer_doesnt_match);
                        // errorMessage.setVisibility(View.VISIBLE);
                        questionAnswerField.setText("");
                        questionAnswerField.setErrors();
                        // questionAnswerField.updateAppearanceForInput();
                    }
                }

                // If account locked, show valid error and send error code to
                // calling activity
                else if (!bean.isAppError()
                        && bean.getErrorCode()
                                .contains(
                                        ""
                                                + RegistrationErrorCodes.STRONG_AUTH_STATUS_INVALID)) {
                    // Check if it's valid
                    CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            EnhancedAccountSecurityActivity.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);

                    if (authListener != null) {
                        authListener.onStrongAuthCardLock(data);
                    }

                    // Tell calling activity that account has been locked.
                    // activityResult = STRONG_AUTH_LOCKED;
                    // finish();
                } else {
                    // If there is any other error, send error code to calling
                    // activity
                    CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            EnhancedAccountSecurityActivity.this);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);
                    if (authListener != null) {
                        authListener.onStrongAuthError(data);
                    }
                    // activityResult = RESULT_CANCELED;
                    // finish();
                }
            }
        };
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(SERVER_ERROR_VISIBILITY,
                serverErrorLabel.getVisibility());
        outState.putString(SERVER_ERROR_TEXT, serverErrorLabel.getText()
                .toString());

        outState.putInt(ANSWER_ERROR_VISIBILITY, errorMessage.getVisibility());
        outState.putString(ANSWER_ERROR_TEXT, errorMessage.getText().toString());
        // Defect ID: 95466
        // outState.putString(WHATS_THIS_STATE, statusIconLabel.getText()
        // .toString());
        // Defect ID: 95466
        super.onSaveInstanceState(outState);
    }

    private void restoreState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {            

            inputErrorVisibility = savedInstanceState
                    .getInt(ANSWER_ERROR_VISIBILITY);
            inputErrorText = savedInstanceState.getString(ANSWER_ERROR_TEXT);            

            errorMessage.setText(inputErrorText);
            errorMessage.setVisibility(inputErrorVisibility);
            Utils.log(TAG, "inputErrorText " + inputErrorText
                    + " inputErrorVisibility " + inputErrorVisibility);
            restoreInputField();

            // restoreExpandableHelpMenu();
        }

    }

    private void loadAllViews() {
        questionAnswerField = (NonEmptyEditText) findViewById(R.id.account_security_question_answer_field);
        securityRadioGroup = (RadioGroup) findViewById(R.id.account_security_choice_radio_group);

        // Defect ID: 95466
        // detailHelpLabel = (TextView)
        // findViewById(R.id.account_security_whats_this_detail_label);
        // detailHelpLabel = (TextView)
        // findViewById(R.id.account_security_whats_this_detail_label);
        // Defect ID: 95466
        errorMessage = (TextView) findViewById(R.id.error_message_strong_auth);
        // Defect ID: 95466
        // statusIconLabel = (TextView)
        // findViewById(R.id.account_security_plus_label);
        // Defect ID: 95466
        questionLabel = (TextView) findViewById(R.id.account_security_question_placeholder_label);

        // Defect ID: 95466
        /*
         * whatsThisLayout = (RelativeLayout)
         * findViewById(R.id.account_security_whats_this_relative_layout);
         */
        // Defect ID: 95466

        radioButtonOne = (RadioButton) securityRadioGroup
                .findViewById(R.id.account_security_choice_one_radio);
        radioButtonTwo = (RadioButton) securityRadioGroup
                .findViewById(R.id.account_security_choice_two_radio);
        serverErrorLabel = (TextView) findViewById(R.id.account_security_server_error);
        mainScrollView = (ScrollView) findViewById(R.id.scrollView1);
        continueButton = (Button) findViewById(R.id.account_security_continue_button);
        if (inputErrorText == null || inputErrorText.equalsIgnoreCase("")) {
            questionAnswerField.attachErrorLabel(errorMessage);
        }
    }

    private void setupRadioGroupListener() {
        securityRadioGroup
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    final int subCopyColor = getResources().getColor(
                            R.color.sub_copy);
                    final int fieldCopyColor = getResources().getColor(
                            R.color.field_copy);

                    @Override
                    public void onCheckedChanged(final RadioGroup group,
                            final int checkedId) {

                        final RadioButton checkedRadioButton = (RadioButton) group
                                .findViewById(checkedId);
                        if (checkedRadioButton.equals(radioButtonOne)) {
                            radioButtonOne.setTextColor(subCopyColor);
                            radioButtonTwo.setTextColor(fieldCopyColor);
                        } else {
                            radioButtonOne.setTextColor(fieldCopyColor);
                            radioButtonTwo.setTextColor(subCopyColor);
                        }
                    }
                });
    }

    /**
     * Moved intent logic to onResume instead of onCreate. onNewIntent will
     * update the intent before onResume is called.
     */
    @Override
    public void onResume() {
        super.onResume();

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Determine if the activity was created from a Card or a Bank
            // logical path
            

            // Check if activity was created via a Card or Bank logical path

            strongAuthQuestion = extras
                    .getString(IntentExtraKey.STRONG_AUTH_QUESTION);
            strongAuthQuestionId = extras
                    .getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
            // Defect id 95164
            forgotBoth = extras.getBoolean(FORGOT_BOTH_FLOW);
            forgotPassword = extras.getBoolean(FORGOT_PASSWORD_FLOW);
            // Defect id 95164
            questionLabel.setText(strongAuthQuestion);

            if (StrongAuthHandler.authListener != null) {
                authListener = StrongAuthHandler.authListener;
            }
        }

    }

    /**
     * When the activity is finished, set the result so that the calling
     * activity knows if strong auth exited properly or not.
     */
    @Override
    public void finish() {
        setResult(activityResult);
        super.finish();
    }

    /**
     * Restore the sate of the input field based on its error label. If the
     * label is present, its in an error state and must be updated.
     */
    private void restoreInputField() {
        errorMessage.setText(inputErrorText);
        errorMessage.setVisibility(inputErrorVisibility);

        if (errorMessage.getVisibility() == View.VISIBLE)
            questionAnswerField.updateAppearanceForInput();

    }

    // Defect ID: 95466
    // /**
    // * When orientation changes, we need to restore the state of the dropdown
    // menu.
    // * This is done by comparing the String character of the menu to known
    // open and close
    // * characters. Then we open or close the menu based on that.
    // */
    // private void restoreExpandableHelpMenu() {
    // statusIconLabel.setText(dropdownSymbol);
    // if("+".equals(statusIconLabel.getText().toString()))
    // closeHelpMenu();
    // else
    // openHelpMenu();
    // }
    // Defect ID: 95466

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        // Grab the updated intent
        setIntent(intent);
    }

    /**
     * Toggles the help menu based on its current state. If the menu is closed
     * and it is clicked, it gets opened. If the menu is open and its gets
     * clicked, it closes.
     */

    // Defect ID: 95466
    // public void expandHelpMenu(final View v) {
    //			if ("+".equals(statusIconLabel.getText().toString())) { //$NON-NLS-1$
    // openHelpMenu();
    // } else {
    // closeHelpMenu();
    // }
    // }
    //

    /**
     * Open the help menu by changing its character from a '+' to a '-' and
     * setting its content to be visible by changing its line height.
     */
    // private void openHelpMenu() {
    // statusIconLabel.setText(getString(R.string.account_security_minus_text));
    // detailHelpLabel.setMaxLines(HELP_DROPDOWN_LINE_HEIGHT);
    // }

    /**
     * Close the help menu by changing its character from a '-' to a '+' and
     * setting its content to be invisible by changing its line height to zero.
     */
    // private void closeHelpMenu() {
    // statusIconLabel.setText(getString(R.string.account_security_plus_text));
    // detailHelpLabel.setMaxLines(0);
    // }
    // Defect ID: 95466

    /**
     * Check to see if the user provided an answer to the strong auth question.
     * If they did, then submit the info to the server for validation. If they
     * did not, present an error message.
     * 
     * @param v
     */
    public void submitSecurityInfo(final View v) {
        mainScrollView.smoothScrollTo(0, 0);
        // Store answer in a string
        final String answer = questionAnswerField.getText().toString();

        if (!Strings.isNullOrEmpty(answer)) {
            // Find out which radio button is pressed.
            final int radioButtonId = securityRadioGroup
                    .getCheckedRadioButtonId();
            final View selectedButton = securityRadioGroup
                    .findViewById(radioButtonId);
            final int selectedIndex = securityRadioGroup
                    .indexOfChild(selectedButton);

            submitAns(selectedIndex, answer);

        } else {
            CardErrorHandler.getInstance().showErrorsOnScreen(
                    this,
                    this.getResources().getString(
                            R.string.error_strongauth_noanswer));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
     */
    @Override
    public TextView getErrorLabel() {
        return errorMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
     */
    @Override
    public List<EditText> getInputFields() {
        final List<EditText> inputFields = new ArrayList<EditText>();
        inputFields.add(questionAnswerField);
        return inputFields;
    }

    private void startHomeFragment() {
        FacadeFactory.getCardFacade().navToHomeFragment(this);
    }

    /**
     * If the back button is pressed then cancel the strong auth activity and
     * notify the calling activity that this activity was canceled.
     */
    @Override
    public void onBackPressed() {
        activityResult = RESULT_CANCELED;
        // Defect id 95164
        if (forgotBoth || forgotPassword) {
            final Intent forgotCredentialsActivity = new Intent(this,
                    ForgotCredentialsActivity.class);
            startActivity(forgotCredentialsActivity);
            forgotBoth = false;
            forgotPassword = false;
        }
        finish();
        // Defect id 95164
    }

    /**
     * If Strong Auth finishes with success, notify the calling activity of this
     * and close.
     */
    private void finishWithResultOK() {
        activityResult = RESULT_OK;
        finish();
    }

    /**
     * If the software back button is pressed, call the onBackPressed() method.
     */
    @Override
    public void goBack() {
        onBackPressed();
    }

    /**
     * Event handler for text change events on the TextView with id
     * account_security_question_answer_field. If no text is detected then the
     * continue button at the bottom of the page is disabled, else it is
     * enabled.
     * 
     * @param newText
     *            Text that is provided by the TextView whenever a change has
     *            been detected
     */
    private void onTextChanged(final CharSequence newText) {
        if (newText != null && newText.length() >= MIN_ANSWER_LENGTH) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.common.NotLoggedInRoboActivity#getErrorHandler()
     */
    @Override
    public com.discover.mobile.common.error.ErrorHandler getErrorHandler() {
        return FacadeFactory.getCardFacade().getCardErrorHandler();
    }

    @Override
    public void onSuccess(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnError(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();
    }

    /**
     * This method will submit strong auth ans to server and if it's correct
     * then it will replay calling activity with Success. On error it will check
     * if it's 1405 code then ask user to enter ans again and if it's 1402 then
     * this will show account lock error and it will replay calling activity
     * with ACCOUNT_LOCKED flag
     * 
     * @param selectedIndex
     * @param answer
     */
    public void submitAns(int selectedIndex, String answer) {
        mainScrollView.smoothScrollTo(0, 0);
        errorMessage.setVisibility(View.GONE);
        questionAnswerField.updateAppearanceForInput();
        StrongAuthAns strongAuthAns = new StrongAuthAns(
                EnhancedAccountSecurityActivity.this, authAnsListener);

        try {
            strongAuthAns.sendRequest(answer, strongAuthQuestionId,
                    selectedIndex);
        } catch (JsonGenerationException e) {
            handleError(e);
        } catch (NoSuchAlgorithmException e) {
            handleError(e);
        } catch (IOException e) {
            handleError(e);
        } catch (Exception e) {
            handleError(e);
        }
    }

    /**
     * This method application error if any occurs
     * 
     * @param Exception
     */
    private void handleError(Exception e) {
        e.printStackTrace();
        CardErrorBean cardErrorBean = new CardErrorBean(e.toString(), true);
        CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                EnhancedAccountSecurityActivity.this);
        cardErrorResHandler.handleCardError(cardErrorBean);
    }

    /**
     * This method get called on click of menu items on tooltip icon.
     * 
     */
    private void setupClickableHelpItem() {
        helpInfo = new HelpItemGenerator(R.string.help_all_Info, false, true,
                getAllFaqListener());
        helpNum = new HelpItemGenerator(R.string.help_menu_number, true, false,
                getAllFaqListener());
        helpFaq = new HelpItemGenerator(R.string.help_all_faq, true, true,
                getAllFaqListener());
        help = (HelpWidget) findViewById(R.id.help);
        help.showHelpItems(getEnhancedHelpItems());
    }

    /**
     * This method get called to set menu items on tooltip icon.
     * 
     * @return List<HelpItemGenerator>
     */
    public List<HelpItemGenerator> getEnhancedHelpItems() {
        final List<HelpItemGenerator> items = new ArrayList<HelpItemGenerator>();
        items.add(helpInfo);
        items.add(helpNum);
        items.add(helpFaq);
        return items;
    }

    /**
     * It's click listener for Help menu
     * 
     * @return
     */
    private OnClickListener getAllFaqListener() {
        return new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(EnhancedAccountSecurityActivity.this,
                        "comming soon ", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public int getEnhanceSecurityRequestCodeForAccountLock() {
        // TODO Auto-generated method stub
        return STRONG_AUTH_LOCKED;
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
    }
}
