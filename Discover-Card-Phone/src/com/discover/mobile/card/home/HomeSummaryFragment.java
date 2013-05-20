package com.discover.mobile.card.home;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.Globals;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.uiwidget.CardAccountToggleView;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.CommonMethods;
import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardMenuInterface;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.navigation.CordovaWebFrag;
import com.discover.mobile.card.phonegap.plugins.JQMResourceMapper;
import com.discover.mobile.card.services.auth.AccountDetails;

import com.discover.mobile.PushConstant;

public class HomeSummaryFragment extends BaseFragment implements
        OnClickListener {

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    protected static final String TAG = "HomeSummaryFragment";
    private static final String REFERER = "cardHome-pg";
    private View view;
    private TextView ViewOption, payOption, redeemOption, offerOption,
            provideFeedback, accountName, termsOfUse;
    private LinearLayout currentBalance, lastStatement, bonusBalance;
    private RelativeLayout bonusOffer;
    private View statusBarView;
    private boolean isCashback = true;
    HashMap<String, String> rewardsInfo = new HashMap<String, String>();
    HashMap<String, String> rewardsOffer = new HashMap<String, String>();
    String incentiveCodePrefix = "";
    String incentiveTypeCodePrefix = "";
    String rewardsInfoKey = "";
    String rewardsOfferKey = "";
    AccountDetails accountDetails = null;
    Activity callingActivity = null;
    CardEventListener cardEventListener = null;

    // Toggle BANK/CARD VIEW
    private ImageView cardBankToggle = null;
    private CardAccountToggleView toggleView = null;
    private boolean showToggleView = false;
    private ImageView accountToggleArrow = null;
    private JQMResourceMapper jqmResourceMapper;
    // Changes for Push Notification
    private String pushErrorMsg;
    private TextView pushErrorTV;

    private CardMenuItemLocationIndex mCardMenuLocation;
    private CordovaWebFrag cordovaWebFrag = null;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.section_account_summary_landing, null);
        provideFeedback = (TextView) view
                .findViewById(R.id.provide_feedback_button);

        termsOfUse = (TextView) view.findViewById(R.id.privacy_terms);
        statusBarView = view.findViewById(R.id.accounthomestatusbar);

        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(callingActivity);
        accountDetails = (AccountDetails) cardShareDataStoreObj
                .getValueOfAppCache(callingActivity
                        .getString(R.string.account_details));
        accountName = (TextView) statusBarView.findViewById(R.id.account_name);
        pushErrorTV = (TextView) view.findViewById(R.id.push_ac_home_errorTV);

        mCardMenuLocation = CardMenuItemLocationIndex.getInstance();
        cordovaWebFrag = ((CardNavigationRootActivity) getActivity())
                .getCordovaWebFragInstance();

        // Get the boolean flag from extra
        final Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {

            showToggleView = extras.getBoolean("showToggleFlag");
            pushErrorMsg = extras
                    .getString(PushConstant.extras.PUSH_ERROR_AC_HOME);
        }

        if (pushErrorMsg != null) {
            pushErrorTV.setText(pushErrorMsg);
            pushErrorTV.setVisibility(View.VISIBLE);
        } else {
            pushErrorTV.setVisibility(View.GONE);
        }

        if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)) {
            // Statusbarfragment is removed from Account Home View is included
            // in Home Summary Fragment itself
            showActionBarLogo();
        }

        if (null != accountDetails) {

            // Toggle BANK/CARD VIEW
            setupAccountToggle(showToggleView);

            setAccountName();
            setupHomeElements();
        } else {
            updateAccountDetailsEventListener();
            Utils.updateAccountDetails(callingActivity, cardEventListener,
                    "Discover", "Updating Account Details...");
        }

        provideFeedback.setOnClickListener(this);
        termsOfUse.setOnClickListener(this);
        return view;
    }

    /**
     * To fetch the account name from the account details object
     */
    private void setAccountName() {
        // TODO Auto-generated method stub
        final StringBuilder defaultHiText = new StringBuilder(
                callingActivity.getString(R.string.hi_note));
        if (null != accountDetails) {
            if (null != accountDetails.mailingAddress) {
                if (null != accountDetails.mailingAddress.firstName) {
                    defaultHiText.append(" "
                            + accountDetails.mailingAddress.firstName);
                }
            } else {

                if (null != accountDetails.primaryCardMember) {
                    if (null != accountDetails.primaryCardMember.nameOnCard) {
                        defaultHiText.append(" "
                                + accountDetails.primaryCardMember.nameOnCard);
                    }
                }

            }

        }
        setStatusBarText(defaultHiText.toString());
    }

    /**
     * @param string
     *            the defaultHiText including user name if present
     * 
     *            Will set the text in Hi TextView
     */
    private void setStatusBarText(final String text) {
        // TODO Auto-generated method stub
        accountName.setText(text);
    }

    /**
     * to create cardEventListener Object that will be passed to
     * updateAccountDetails
     * 
     */
    private void updateAccountDetailsEventListener() {
        // TODO Auto-generated method stub
        cardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(final Object data) {
                // TODO Auto-generated method stub
                final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                        .getInstance(callingActivity);
                cardShareDataStoreObj.addToAppCache(
                        callingActivity.getString(R.string.account_details),
                        data);
                accountDetails = (AccountDetails) data;

                // Toggle BANK/CARD VIEW
                setupAccountToggle(showToggleView);
                setAccountName();
                setupHomeElements();
            }

            @Override
            public void OnError(final Object data) {
                // TODO Auto-generated method stub
                final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        (CardErrorHandlerUi) callingActivity);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        hideActionBarLogo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(final Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        callingActivity = activity;
    }

    /**
     * Builds the four elements (Current balance, last statement, cashback
     * balance, & cashback offer) on the Home view.
     */
    private void setupHomeElements() {

        setupCurrentBalanceElement(accountDetails);
        setupLastStatementElement(accountDetails);

        populateRewardsInfo();
        populateRewardsOffer();

        // This has been added to enable the visibility of CashBack Bonus
        // Balance and CashBack Bonus Offer options as per the Card Type

        if (!(accountDetails.rewardOutage || accountDetails.acLiteOutageMode)) {

            if (accountDetails.incentiveCode != null) {
                incentiveCodePrefix = "_" + accountDetails.incentiveCode;
            }

            if (accountDetails.incentiveTypeCode != null) {
                incentiveTypeCodePrefix = "_"
                        + accountDetails.incentiveTypeCode;
            }

            rewardsInfoKey = "rewards_info" + incentiveTypeCodePrefix
                    + incentiveCodePrefix;
            if (!rewardsInfo.containsKey(rewardsInfoKey)) {
                rewardsInfoKey = "rewards_info" + incentiveTypeCodePrefix;
                if (!rewardsInfo.containsKey(rewardsInfoKey)) {
                    view.findViewById(R.id.home_bonus_balance).setVisibility(
                            View.GONE);
                } else {
                    setupBonusBalance(accountDetails);
                }
            } else {
                setupBonusBalance(accountDetails);
            }

            rewardsOfferKey = "rewards_offer" + incentiveTypeCodePrefix
                    + incentiveCodePrefix;
            if (!rewardsOffer.containsKey(rewardsOfferKey)) {
                rewardsOfferKey = "rewards_offer" + incentiveTypeCodePrefix;
                if (!rewardsOffer.containsKey(rewardsOfferKey)) {
                    view.findViewById(R.id.home_bonus_offer).setVisibility(
                            View.GONE);
                } else {
                    setupBonusOffer(accountDetails);
                }
            } else {
                setupBonusOffer(accountDetails);
            }
        } else {
            view.findViewById(R.id.acoutagemode_message).setVisibility(
                    View.VISIBLE);
            view.findViewById(R.id.home_bonus_balance).setVisibility(View.GONE);
            view.findViewById(R.id.home_bonus_offer).setVisibility(View.GONE);
        }

        accountDetails = null;

    }

    // This method will populate the rewardsOffer with combinations of
    // incentiveCode and incentiveTypeCode
    private void populateRewardsOffer() {
        // TODO Auto-generated method stub

        rewardsOffer.put("rewards_offer_CBB_000001", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_CBB_000011", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_CBB_000013", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_CBB_000014", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_CBB_000015", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_CBB_000016", "cashBack Bonus Offer");
        rewardsOffer.put("rewards_offer_MI2", " ");
        rewardsOffer.put("rewards_offer_MIL", " ");
        rewardsOffer.put("rewards_offer_MI2_000002", "miles Rewards Offer");
        rewardsOffer.put("rewards_offer_SBC", " ");
        rewardsOffer.put("rewards_offer_CBB", " ");
        rewardsOffer.put("rewards_offer_SML", " ");
    }

    // This method will populate the rewardsInfo with combinations of
    // incentiveCode and incentiveTypeCode
    private void populateRewardsInfo() {
        // TODO Auto-generated method stub

        rewardsInfo.put("rewards_info_CBB_000001", "redeem");
        rewardsInfo.put("rewards_info_CBB_000011", "redeem");
        rewardsInfo.put("rewards_info_CBB_000013", "redeem");
        rewardsInfo.put("rewards_info_CBB_000014", "redeem");
        rewardsInfo.put("rewards_info_CBB_000015", "redeem");
        rewardsInfo.put("rewards_info_CBB_000016", "redeem");
        rewardsInfo.put("rewards_info_MI2", "earnRewardsAmount");
        rewardsInfo.put("rewards_info_MI2_000002", "redeem");
        rewardsInfo.put("rewards_info_MIL", "earnRewardsAmount");
        rewardsInfo.put("rewards_info_SBC", "newlyEarnRewardsAmount");
        rewardsInfo.put("rewards_info_CBB", "newlyEarnRewardsAmount");
        rewardsInfo.put("rewards_info_SML", "earnRewardsAmount");
    }

    private String convertToDollars(final String dollar) {
        if (null != dollar) {
            double amount = Double.parseDouble(dollar);
            if (amount < 0) {
                amount *= -1;
                return NumberFormat.getCurrencyInstance(Locale.US)
                        .format(amount).replace("$", "-$");
            }

            else {
                return NumberFormat.getCurrencyInstance(Locale.US).format(
                        amount);
            }
        } else {
            return "$0.00";
        }
    }

    /**
     * Formats and fills-out the element associated with the user's Current
     * Balance.
     * 
     * @param accountDetails
     */
    private void setupCurrentBalanceElement(final AccountDetails accountDetails) {
        currentBalance = (LinearLayout) view
                .findViewById(R.id.home_current_balance);

        // main content
        ((TextView) currentBalance.findViewById(R.id.title))
                .setText(R.string.current_balance);
        ((TextView) currentBalance.findViewById(R.id.content_text))
                .setText(convertToDollars(accountDetails.currentBalance));

        // subsection
        ((TextView) currentBalance.findViewById(R.id.bottom_bar_label))
                .setText(getString(R.string.credit_available));
        ((TextView) currentBalance.findViewById(R.id.bottom_bar_value))
                .setText(convertToDollars(accountDetails.availableCredit));

        // View button
        ViewOption = (TextView) currentBalance
                .findViewById(R.id.blue_button_text);
        ViewOption.setText(getString(R.string.view_blue_button_text));
        currentBalance.setOnClickListener(this);

    }

    /**
     * Formats and fills-out the element associated with the user's Last
     * Statement Balance.
     * 
     * @param accountDetails
     */
    private void setupLastStatementElement(final AccountDetails accountDetails) {
        lastStatement = (LinearLayout) view
                .findViewById(R.id.home_last_statement);

        if (null == accountDetails.minimumPaymentDue) {
            lastStatement.setVisibility(View.GONE);
            return;
        }

        // main content
        ((TextView) lastStatement.findViewById(R.id.title))
                .setText(R.string.last_statement_balance);
        ((TextView) lastStatement.findViewById(R.id.content_text))
                .setText(convertToDollars(accountDetails.statementBalance));

        // subsection
        ((TextView) lastStatement.findViewById(R.id.bottom_bar_label))
                .setText(formatMinimumPaymentTitle(accountDetails));
        ((TextView) lastStatement.findViewById(R.id.bottom_bar_value))
                .setText(convertToDollars(accountDetails.minimumPaymentDue));

        // Pay button
        payOption = (TextView) lastStatement
                .findViewById(R.id.blue_button_text);
        payOption.setText(R.string.pay_blue_button_text);
        lastStatement.setOnClickListener(this);
    }

    /**
     * Formats and fills-out the element associated with the user's
     * Cashback/Miles Bonus Balance.
     * 
     * @param accountDetails
     */
    private void setupBonusBalance(final AccountDetails accountDetails) {
        bonusBalance = (LinearLayout) view
                .findViewById(R.id.home_bonus_balance);

        // main content & subsection
        isCashback = CommonMethods.isCashbackCard(accountDetails);

        if (isCashback) {

            final double cashBonus = Double
                    .valueOf(accountDetails.earnRewardAmount);
            final double newlyEarned = Double
                    .valueOf(accountDetails.newlyEarnedRewards);

            ((TextView) bonusBalance.findViewById(R.id.title))
                    .setText(R.string.cashback_bonus_balance);
            ((TextView) bonusBalance.findViewById(R.id.content_text))
                    .setText(NumberFormat.getCurrencyInstance(Locale.US)
                            .format(cashBonus));

            ((TextView) bonusBalance.findViewById(R.id.bottom_bar_label))
                    .setText(getString(R.string.newly_earned));
            ((TextView) bonusBalance.findViewById(R.id.bottom_bar_value))
                    .setText(NumberFormat.getCurrencyInstance(Locale.US)
                            .format(newlyEarned));
        } else {
            ((TextView) bonusBalance.findViewById(R.id.title))
                    .setText(R.string.miles_balance);
            ((TextView) bonusBalance.findViewById(R.id.content_text))
                    .setText(CommonMethods
                            .insertCommas(accountDetails.earnRewardAmount));

            ((TextView) bonusBalance.findViewById(R.id.bottom_bar_label))
                    .setText(getString(R.string.newly_earned));
            ((TextView) bonusBalance.findViewById(R.id.bottom_bar_value))
                    .setText(CommonMethods
                            .insertCommas(accountDetails.newlyEarnedRewards));
        }

        redeemOption = (TextView) bonusBalance
                .findViewById(R.id.blue_button_text);
        redeemOption.setText(R.string.redeem_blue_button_text);
        bonusBalance.setOnClickListener(this);

    }

    /**
     * Formats and fills-out the element associated with the user's
     * Cashback/Miles Bonus Offer.
     * 
     * @param accountDetails
     */
    private void setupBonusOffer(final AccountDetails accountDetails) {
        bonusOffer = (RelativeLayout) view.findViewById(R.id.home_bonus_offer);
        final String strEscapeCardCode = getString(
                R.string.card_product_group_code_esc).toLowerCase();
        // In case of Escape Card SIGN UP option will be removed.
        if (accountDetails.cardProductGroupCode.toLowerCase()
                .compareToIgnoreCase(strEscapeCardCode) == 0) {
            bonusOffer.setVisibility(View.GONE);
            return;
        }
        // main content
        isCashback = CommonMethods.isCashbackCard(accountDetails);
        if (isCashback) {
            ((TextView) bonusOffer.findViewById(R.id.title))
                    .setText(R.string.cashback_bonus_offer);
        } else {
            ((TextView) bonusOffer.findViewById(R.id.title))
                    .setText(R.string.miles_offer);
        }

        ((TextView) bonusOffer.findViewById(R.id.content_text))
                .setText(getString(R.string.cashback_miles_offer_subtext));

        // SignUp button
        offerOption = (TextView) bonusOffer.findViewById(R.id.blue_button_text);
        offerOption.setText(R.string.sign_up_blue_button_text);
        bonusOffer.setOnClickListener(this);
    }

    /**
     * Formats the subtext String associated with the Last Statement. This
     * String is dynamic due to the date.
     * 
     * @param accountDetails
     * @return Formatted title with due date.
     */
    private String formatMinimumPaymentTitle(final AccountDetails accountDetails) {
        if (null == accountDetails.paymentDueDate) {
            return getString(R.string.min_payment_due);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.min_payment_due));
        sb.append(' ');
        sb.append(accountDetails.paymentDueDate.substring(0, 2));
        sb.append('/');
        sb.append(accountDetails.paymentDueDate.substring(2, 4));
        sb.append(':');

        return sb.toString();
    }

    /**
     * Return the integer value of the string that needs to be displayed in the
     * title
     */
    @Override
    public int getActionBarTitle() {
        final String m_title = ((CardNavigationRootActivity) getActivity())
                .getActionBarTitle();
        Log.v(TAG, "getActionBarTitle n title is " + m_title);
        if (null != m_title) {
            jqmResourceMapper = JQMResourceMapper.getInstance();

            return jqmResourceMapper.getTitleStringId(m_title);
        } else {
            return -1;
        }
    }

    @Override
    public int getGroupMenuLocation() {
        Utils.log(TAG, "inside getGroupMenuLocation ");
        int tempId = getActionBarTitle();
        final String m_title = ((CardNavigationRootActivity) getActivity())
                .getActionBarTitle();

        if (null != m_title) {

            if (!m_title.equalsIgnoreCase(getResources().getString(
                    R.string.section_title_home))
                    && tempId == -1) {
                if (null != cordovaWebFrag.getM_currentLoadedJavascript()) {

                    Utils.log(TAG, "m_currentLoadedJavascript is "
                            + cordovaWebFrag.getM_currentLoadedJavascript());
                    jqmResourceMapper = JQMResourceMapper.getInstance();

                    tempId = jqmResourceMapper.getTitleStringId(cordovaWebFrag
                            .getM_currentLoadedJavascript());
                    return mCardMenuLocation.getMenuGroupLocation(tempId);
                }
            }
        }
        return mCardMenuLocation.getMenuGroupLocation(tempId);
    }

    @Override
    public int getSectionMenuLocation() {
        Utils.log(TAG, "inside getSectionMenuLocation");
        int tempId = getActionBarTitle();
        final String m_title = ((CardNavigationRootActivity) getActivity())
                .getActionBarTitle();

        if (null != m_title) {

            if (!m_title.equalsIgnoreCase(getResources().getString(
                    R.string.section_title_home))
                    && tempId == -1) {
                if (null != cordovaWebFrag.getM_currentLoadedJavascript()) {

                    Utils.log(TAG, "m_currentLoadedJavascript is "
                            + cordovaWebFrag.getM_currentLoadedJavascript());
                    jqmResourceMapper = JQMResourceMapper.getInstance();

                    tempId = jqmResourceMapper.getTitleStringId(cordovaWebFrag
                            .getM_currentLoadedJavascript());
                    return mCardMenuLocation.getMenuSectionLocation(tempId);
                }
            }
        }

        return mCardMenuLocation.getMenuSectionLocation(tempId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub

        if (v.getId() == R.id.home_current_balance) {
            ((CardMenuInterface) callingActivity)
                    .sendNavigationTextToPhoneGapInterface("Recent Activity");
        } else if (v.getId() == R.id.home_last_statement) {
            ((CardMenuInterface) callingActivity)
                    .sendNavigationTextToPhoneGapInterface("Make a Payment");
        } else if (v.getId() == R.id.home_bonus_balance) {
            if (isCashback) {
                ((CardMenuInterface) callingActivity)
                        .sendNavigationTextToPhoneGapInterface("Partner Gift Cards & eCerts");
            } else {
                ((CardMenuInterface) callingActivity)
                        .sendNavigationTextToPhoneGapInterface("Redeem Miles");
            }
        } else if (v.getId() == R.id.home_bonus_offer) {
            if (isCashback) {
                ((CardMenuInterface) callingActivity)
                        .sendNavigationTextToPhoneGapInterface("Cashback Bonus Promos");
            } else {
                ((CardMenuInterface) callingActivity)
                        .sendNavigationTextToPhoneGapInterface("Miles Promotions");
            }
        } else if (v.getId() == R.id.provide_feedback_button) {
            Utils.log("provide_feedback_button",
                    "provide_feedback_button pressed");
            Utils.createProvideFeedbackDialog(callingActivity, REFERER);
        } else if (v.getId() == R.id.privacy_terms) {
            Utils.log("privacy_terms", "privacy_terms pressed");

            ((CardMenuInterface) callingActivity)
                    .sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));
        }
    }

    /**
     * Determines the placement of the icon upon its layout. It's then used to
     * measure the postion of the indicator. Additionally, this implements the
     * listeners for the AccountToggle.
     */
    private void setupAccountToggle(final boolean showToggleView) {

        // If user is SSO then only Account Toggle View will be displayed.
        if (showToggleView) {
            cardBankToggle = (ImageView) view.findViewById(R.id.cardBankIcon);
            toggleView = (CardAccountToggleView) view
                    .findViewById(R.id.acct_toggle);

            final ViewTreeObserver vto = cardBankToggle.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!toggleView.hasIndicatorBeenDrawn()) {
                        toggleView.setIndicatorPosition(
                                cardBankToggle.getLeft(),
                                cardBankToggle.getTop(),
                                cardBankToggle.getWidth(),
                                cardBankToggle.getHeight());
                    }
                }
            });

            accountToggleArrow = (ImageView) view
                    .findViewById(R.id.AC_orange_arrow_down);
            accountToggleArrow.setOnClickListener(new AccountToggleListener());
            cardBankToggle.setOnClickListener(new AccountToggleListener());

        } else {

            // Even Card icon will not be displayed.
            cardBankToggle = (ImageView) view.findViewById(R.id.cardBankIcon);
            toggleView = (CardAccountToggleView) view
                    .findViewById(R.id.acct_toggle);
            accountToggleArrow = (ImageView) view
                    .findViewById(R.id.AC_orange_arrow_down);
            cardBankToggle.setVisibility(View.GONE);
            toggleView.setVisibility(View.GONE);
            accountToggleArrow.setVisibility(View.GONE);
        }
    }

    /**
     * Listener associated with items that hide/show the Account Toggle Widget.
     */
    private class AccountToggleListener implements OnClickListener {

        @Override
        public void onClick(final View v) {
            toggleView.toggleVisibility();
        }

    }

}
