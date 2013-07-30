package com.discover.mobile.card.auth.strong;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.uiwidget.NonEmptyEditText;
import com.discover.mobile.card.common.utils.FragmentActionBarMenuTitleUtil;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardMenuInterface;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.strong.StrongAuthAns;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.EnhanceSecurityConstant;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.help.HelpItemGenerator;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.net.error.RegistrationErrorCodes;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.google.common.base.Strings;

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
public class EnhancedAccountSecurityFragment extends BaseFragment implements
        EnhanceSecurityConstant, OnClickListener, ErrorHandlerUi {

    /**
     * Field Description of HELP_DROPDOWN_LINE_HEIGHT The Strong Auth screen has
     * an expandable menu that provides help to the user, this value is used to
     * define the number of vertical lines that the menu will occupy when it is
     * expanded. (When collapsed it is set to 0)
     */

    private static final String TAG = EnhancedAccountSecurityFragment.class
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
    private TextView privacyTerms, provideFeedback;

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

    public static final String SERVER_ERROR_VISIBILITY = "a";
    public static final String SERVER_ERROR_TEXT = "c";
    public static final String ANSWER_ERROR_VISIBILITY = "b";
    public static final String ANSWER_ERROR_TEXT = "d";
    public static final String YES_RADIOBUTTON_SEL = "e";
    public static final String ANSWER_TEXT = "f";

    private static String answer;
    public static boolean yes_radiobutton = true;
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
    private View mainView;

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
            EnhancedAccountSecurityFragment.this.onTextChanged(s);
        }

        @Override
        public void afterTextChanged(final Editable s) {
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        CardShareDataStore mCardStoreData = CardShareDataStore.getInstance(this
                .getActivity().getApplicationContext());
        yes_radiobutton = true;
        mainView = inflater.inflate(R.layout.strongauth_page, null);
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
                if (authListener != null) {
                    authListener.onStrongAuthSucess(data);
                }
            }

            @Override
            public void OnError(Object data) {
                CardErrorBean bean = (CardErrorBean) data;

                /**
                 * if ans is incorrect then ask user to enter it again if ans is
                 * wrong consecutive three times then get the another question
                 **/
                Log.d("13.4", "error code" + bean.getErrorCode() + " message: "
                        + bean.getErrorMessage());
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
                            questionAnswerField.setText("");
                            questionLabel.setText(strongAuthQuestion);
                            strongAuthQuestionId = bean.getQuestionId();
                            // submitSecurityInfo(null);
                        }
                    } else {
                        errorMessage
                                .setText(R.string.account_security_answer_doesnt_match);

                        questionAnswerField.setText("");
                        questionAnswerField.setErrors();

                    }
                }

                // If account locked, show valid error and send error code to
                // calling activity
                else if (!bean.isAppError()
                        && bean.getErrorCode()
                                .contains(
                                        ""
                                                + RegistrationErrorCodes.STRONG_AUTH_STATUS_INVALID)) {
                    if (authListener != null) {
                        authListener.onStrongAuthCardLock(data);
                    }

                    // Tell calling activity that account has been locked.
                    // activityResult = STRONG_AUTH_LOCKED;
                    // finish();
                } else {
                    // If there is any other error, send error code to calling
                    // activity
                    if (authListener != null) {
                        authListener.onStrongAuthSkipped(data);
                    }
                    // activityResult = RESULT_CANCELED;
                    // finish();
                }
            }
        };
        setBundleValues(getArguments());
        return mainView;
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
            // if(inputErrorText != null && !inputErrorText.equals(""))
            {
                errorMessage.setText(inputErrorText);
                errorMessage.setVisibility(inputErrorVisibility);
                Utils.log(TAG, "inputErrorText " + inputErrorText
                        + " inputErrorVisibility " + inputErrorVisibility);
                restoreInputField();
            }

            // restoreExpandableHelpMenu();
        }

    }

    private void loadAllViews() {
        questionAnswerField = (NonEmptyEditText) mainView
                .findViewById(R.id.account_security_question_answer_field);
        securityRadioGroup = (RadioGroup) mainView
                .findViewById(R.id.account_security_choice_radio_group);

        // Defect ID: 95466
        // detailHelpLabel = (TextView)
        // findViewById(R.id.account_security_whats_this_detail_label);
        // detailHelpLabel = (TextView)
        // findViewById(R.id.account_security_whats_this_detail_label);
        // Defect ID: 95466
        mainView.findViewById(R.id.account_security_label).setVisibility(
                View.GONE);

        errorMessage = (TextView) mainView
                .findViewById(R.id.error_message_strong_auth);
        // Defect ID: 95466
        // statusIconLabel = (TextView)
        // findViewById(R.id.account_security_plus_label);
        // Defect ID: 95466
        questionLabel = (TextView) mainView
                .findViewById(R.id.account_security_question_placeholder_label);

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
        serverErrorLabel = (TextView) mainView
                .findViewById(R.id.account_security_server_error);
        mainScrollView = (ScrollView) mainView.findViewById(R.id.scrollView1);
        continueButton = (Button) mainView
                .findViewById(R.id.account_security_continue_button);
        continueButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                submitSecurityInfo(arg0);
            }
        });
        if (inputErrorText == null || inputErrorText.equalsIgnoreCase("")) {
            questionAnswerField.attachErrorLabel(errorMessage);
        }
        privacyTerms = (TextView) mainView.findViewById(R.id.privacy_terms);
        provideFeedback = (TextView) mainView
                .findViewById(R.id.provide_feedback_button);
        handlingClickEvents();
    }

    private void handlingClickEvents() {
        privacyTerms.setOnClickListener(this);
        provideFeedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.privacy_terms) {
            ((CardMenuInterface) getActivity())
                    .sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));

        } else if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(getActivity(),
                    "strongAuthEnroll-pg");
        } else if (v.getId() == R.id.logout_button) {

            // Changes for 13.4 start
            Utils.logoutUser(getActivity(), false);

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
                            yes_radiobutton = true;
                            radioButtonOne.setTextColor(subCopyColor);
                            radioButtonTwo.setTextColor(fieldCopyColor);
                        } else {
                            yes_radiobutton = false;
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
    public void setBundleValues(Bundle savedInstanceState) {
        final Bundle extras = savedInstanceState;
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

            if (extras.getBoolean("is_enhance")) {
                String errmsg = extras.getString(ANSWER_ERROR_TEXT);
                int errvisib = extras.getInt(ANSWER_ERROR_VISIBILITY);
                errorMessage.setText(errmsg);
                errorMessage.setVisibility(errvisib);

                if (errorMessage.getVisibility() == View.VISIBLE)
                    questionAnswerField.updateAppearanceForInput();

            }
            answer = extras.getString(ANSWER_TEXT);

            yes_radiobutton = extras.getBoolean(YES_RADIOBUTTON_SEL, true);

            if (answer != null && !answer.equals("")) {
                questionAnswerField.setText(answer);
            }
            final int subCopyColor = getResources().getColor(R.color.sub_copy);
            final int fieldCopyColor = getResources().getColor(
                    R.color.field_copy);
            if (!yes_radiobutton) {
                radioButtonOne.setChecked(false);
                radioButtonTwo.setChecked(true);
                radioButtonOne.setTextColor(fieldCopyColor);
                radioButtonTwo.setTextColor(subCopyColor);
            } else {
                radioButtonOne.setChecked(true);
                radioButtonTwo.setChecked(false);
                radioButtonOne.setTextColor(subCopyColor);
                radioButtonTwo.setTextColor(fieldCopyColor);

            }
            // restoreState(extras);
            // Defect id 95164
            questionLabel.setText(strongAuthQuestion);

            if (StrongAuthHandler.authListener != null) {
                authListener = StrongAuthHandler.authListener;
            }
        }

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

    /**
     * Toggles the help menu based on its current state. If the menu is closed
     * and it is clicked, it gets opened. If the menu is open and its gets
     * clicked, it closes.
     */

    // Defect ID: 95466
    // public void expandHelpMenu(final View v) {
    //          if ("+".equals(statusIconLabel.getText().toString())) { //$NON-NLS-1$
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

        if (!Strings.isNullOrEmpty(answer)
                && !questionAnswerField.isSpaceEntered()) {
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
                    (ErrorHandlerUi) getActivity(),
                    getActivity().getResources().getString(
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
        FacadeFactory.getCardFacade().navToHomeFragment(getActivity());
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
        StrongAuthAns strongAuthAns = new StrongAuthAns(getActivity(),
                authAnsListener);

        try {
            strongAuthAns.sendRequest(answer, strongAuthQuestionId,
                    selectedIndex);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            // handleError(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // handleError(e);
        } catch (IOException e) {
            e.printStackTrace();
            // handleError(e);
        } catch (Exception e) {
            e.printStackTrace();
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
                (CardErrorHandlerUi) getActivity());
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
        help = (HelpWidget) mainView.findViewById(R.id.help);
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
                Toast.makeText(getActivity(), "comming soon ",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public int getEnhanceSecurityRequestCodeForAccountLock() {
        return STRONG_AUTH_LOCKED;
    }

    @Override
    public void showCustomAlert(AlertDialog alert) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showOneButtonAlert(int title, int content, int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showDynamicOneButtonAlert(int title, String content,
            int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void setLastError(int errorCode) {

    }

    @Override
    public int getLastError() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getActionBarTitle() {
       // return R.string.account_security_text;
        FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
                ((CardNavigationRootActivity) getActivity()));
        return barMenuTitleUtil.getActionBarTitle();
    }

    /**
     * Return GrupMenuLocation
     */
    @Override
    public int getGroupMenuLocation() {
        Utils.log(TAG, "inside getGroupMenuLocation ");
        FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
                ((CardNavigationRootActivity) getActivity()));
        return barMenuTitleUtil
                .getGroupMenuLocation(R.string.section_title_home);
    }

    /**
     * Return selected Menu Location
     */
    @Override
    public int getSectionMenuLocation() {
        Utils.log(TAG, "inside getSectionMenuLocation");
        FragmentActionBarMenuTitleUtil barMenuTitleUtil = new FragmentActionBarMenuTitleUtil(
                ((CardNavigationRootActivity) getActivity()));
        return barMenuTitleUtil
                .getSectionMenuLocation(R.string.section_title_home);
    }

}
