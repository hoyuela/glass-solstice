package com.discover.mobile.card.navigation;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.PushConstant;
import com.discover.mobile.card.R;
import com.discover.mobile.card.account.AccountSectionInfo;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.earncashbackbonus.EarnCashbackBonusInfo;
import com.discover.mobile.card.help.CustomerServiceContactInfo;
import com.discover.mobile.card.home.HomeSectionInfo;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.miles.MilesForESCCards;
import com.discover.mobile.card.miles.MilesInfo;
import com.discover.mobile.card.miles.RedeemMilesInfo;
import com.discover.mobile.card.payments.PaymentsSectionInfo;
import com.discover.mobile.card.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.card.redeemcashbackbonus.RedeemCashbackBonusInfo;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.push.GetPushCount;
import com.discover.mobile.card.services.push.GetPushCountBean;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;
import com.xtify.sdk.api.XtifySDK;

public class CardNavigationMenuFragment extends NavigationMenuFragment {
    static final String TAG = "CardNavigationMenuFragment";

    CardMenuInterface cardMenuInterface;
    public static ImmutableList<ComponentInfo> CARD_SECTION_LIST = null;
    
    private CardShareDataStore mCardStoreData;

    // Added For Push notification
    private int pushUnReadCount = 0;
    private long currentTimeStamp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            cardMenuInterface = (CardMenuInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Setting up the footer info */
        final TextView version = (TextView) getActivity().findViewById(
                R.id.navigation_version);
        final TextView copy = (TextView) getActivity().findViewById(
                R.id.navigation_copyright);
        final TextView privacy = (TextView) getActivity().findViewById(
                R.id.navigation_privacy);
        final Calendar cal = Calendar.getInstance();

        final String year = String.valueOf(cal.get(Calendar.YEAR));
        String versionName = null;
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
            privacy.setTypeface(null, Typeface.BOLD);
            privacy.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        } catch (final NameNotFoundException e) {
            Utils.log(TAG, "No Version available.");
        }
        version.setText("Version " + versionName);
        copy.setText("\u00a9" + year + " Discover Bank, Member FDIC");

        populateLeftNavigationMenu();
        prepPushCount();

        final CardNavigationRootActivity activity = (CardNavigationRootActivity) getActivity();
        activity.setMenu(this);

        NavigationItem.initializeAdapterWithSections(navigationItemAdapter,
                CARD_SECTION_LIST, new HomeSummaryFragment());
        mCardStoreData = CardShareDataStore.getInstance(getActivity());
        mCardStoreData.addToAppCache("currentPageTitle", "Home");
        setListAdapter(navigationItemAdapter);

        privacy.setClickable(true);
        privacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cardMenuInterface
                        .sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));
            }
        });
    }

    @Override
    public void onListItemClick(final ListView listView,
            final View clickedView, final int position, final long id) {
        TextView textView = (TextView) clickedView.findViewById(R.id.title);
        final String text = (String) textView.getText();
    	if (text.equals(mCardStoreData.getValueOfAppCache("currentPageTitle")))
    	{
    		BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) DiscoverActivityManager
                    .getActiveActivity();
            
            // hlin0 20130530 integrate with new sliding menu
            baseFragmentActivity.showContent();
   			return;
    	}

    	super.onListItemClick(listView, clickedView, position, id);

        try {

            prepPushCount();
            cardMenuInterface.sendNavigationTextToPhoneGapInterface(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
     }

    /**
     * Immutable list showing all the top level sections that are displayed in
     * the sliding nav menu
     */
    /*
     * public static final ImmutableList<ComponentInfo> CARD_SECTION_LIST =
     * ImmutableList .<ComponentInfo> builder() // Add Sections below .add(new
     * HomeSectionInfo()).add(new AccountSectionInfo()) .add(new
     * PaymentsSectionInfo()).add(new EarnCashbackBonusInfo()) .add(new
     * RedeemCashbackBonusInfo()) .add(new ProfileAndSettingsSectionInfo())
     * .add(new CustomerServiceContactInfo()).build();
     */

    private void populateLeftNavigationMenu() {

        Context context = this.getActivity().getApplicationContext();

        //ArrayList<ComponentInfo> tempList = new ArrayList<ComponentInfo>();

        CardShareDataStore dataStore = CardShareDataStore.getInstance(context);

        AccountDetails accData = (AccountDetails) dataStore
                .getValueOfAppCache(getResources().getString(
                        R.string.account_details));

        if (null != accData) {

            Utils.log("MenuFragment", "acc data : incentiveTypeCode :"
                    + accData.incentiveTypeCode + " incentiveCode "
                    + accData.incentiveCode + " optionCode: "
                    + accData.optionCode);

            CardMenuManager cardMenuManager = new CardMenuManager(this
                    .getActivity().getApplicationContext());
            ArrayList<String> supportedMenuItems = cardMenuManager
                    .getValidMenuItems(accData.incentiveTypeCode,
                            accData.incentiveCode, accData.optionCode);

            // tempList.add(new HomeSectionInfo());
            // tempList.add(new AccountSectionInfo());
            // tempList.add(new PaymentsSectionInfo());

            // navigationItemAdapter.clear();
            if (supportedMenuItems.indexOf(context
                    .getString(R.string.card_cashback)) != -1) {

                CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                        .add(new HomeSectionInfo(true, countClickListenre))
                        .add(new AccountSectionInfo())
                        .add(new PaymentsSectionInfo())
                        .add(new EarnCashbackBonusInfo())
                        .add(new RedeemCashbackBonusInfo())
                        .add(new ProfileAndSettingsSectionInfo())
                        .add(new CustomerServiceContactInfo()).build();
            }

            if (supportedMenuItems.indexOf(context
                    .getString(R.string.card_miles)) != -1) {
                String strEscapeCardCode = context.getString(
                        R.string.card_product_group_code_esc).toLowerCase();
                if (accData.cardProductGroupCode.toLowerCase()
                        .compareToIgnoreCase(strEscapeCardCode) == 0) {
                    CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                            .add(new HomeSectionInfo(true, countClickListenre))
                            .add(new AccountSectionInfo())
                            .add(new PaymentsSectionInfo())
                            .add(new MilesForESCCards())
                            .add(new RedeemMilesInfo())
                            .add(new ProfileAndSettingsSectionInfo())
                            .add(new CustomerServiceContactInfo()).build();
                } else {
                    CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                            .add(new HomeSectionInfo())
                            .add(new AccountSectionInfo())
                            .add(new PaymentsSectionInfo())
                            .add(new MilesInfo()).add(new RedeemMilesInfo())
                            .add(new ProfileAndSettingsSectionInfo())
                            .add(new CustomerServiceContactInfo()).build();
                }

            }
        

            /* 13.3 Changes start */
            if (null == CARD_SECTION_LIST) {
                // Added for the Other Card Types like Essential etc
                if (accData.cardProductGroupCode.equals(context
                        .getString(R.string.card_product_group_code_essential))
                        || accData.cardProductGroupCode
                                .equals(context
                                        .getString(R.string.card_product_group_code_business_cashback))
                        || accData.cardProductGroupCode
                                .equals(context
                                        .getString(R.string.card_product_group_code_business_miles))
                        || accData.cardProductGroupCode
                                .equals(context
                                        .getString(R.string.card_product_group_code_corp))
                        || accData.cardProductGroupCode
                                .equals(context
                                        .getString(R.string.card_product_group_code_essential_without_fee))) {
                    // Change Left Nav in case of Corporate card
                    
                        CARD_SECTION_LIST = ImmutableList
                                .<ComponentInfo> builder()
                                .add(new HomeSectionInfo(true,
                                        countClickListenre))
                                .add(new AccountSectionInfo())
                                .add(new PaymentsSectionInfo(true))
                                .add(new ProfileAndSettingsSectionInfo())
                                .add(new CustomerServiceContactInfo()).build();
                    
                } else {
                    CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                            .add(new HomeSectionInfo(true, countClickListenre))
                            .add(new AccountSectionInfo())
                            .add(new PaymentsSectionInfo())
                            .add(new ProfileAndSettingsSectionInfo())
                            .add(new CustomerServiceContactInfo()).build();
                }
            }
        }
        /* 13.3 Changes end */

    }
        // onPushCountUpdate(pushUnReadCount);
    

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        CARD_SECTION_LIST = null;
    }

    /**
     * it's onClick listener for push Notification count. On click of it, user
     * will redirect to alert history
     */
    OnClickListener countClickListenre = new OnClickListener() {

        @Override
        public void onClick(View v) {
            SharedPreferences pushSharedPrefs = getActivity()
                    .getSharedPreferences(PushConstant.pref.PUSH_SHARED, // TODO:
                                                                         // Push
                            Context.MODE_PRIVATE);
            Editor editor = pushSharedPrefs.edit();
            pushUnReadCount = 0;
            editor.putInt(PushConstant.pref.PUSH_COUNT, pushUnReadCount);
            editor.commit();
            onPushCountUpdate(pushUnReadCount);
            cardMenuInterface
                    .sendNavigationTextToPhoneGapInterface(getString(R.string.sub_section_title_alert_history));
        }
    };

    public void prepPushCount() {
        // Getting data from Server
        SharedPreferences pushSharedPrefs = getActivity().getSharedPreferences(
                PushConstant.pref.PUSH_SHARED, // TODO: Push
                Context.MODE_PRIVATE);
        long countTimeStamp = pushSharedPrefs.getLong(
                PushConstant.pref.PUSH_COUNT_TIME_STAMP, 0);
        final Editor editor = pushSharedPrefs.edit();

        currentTimeStamp = System.currentTimeMillis();
        Utils.log(TAG, "--Current time Stamp-- " + currentTimeStamp
                + " --countTimeStamp-- " + countTimeStamp);

        // If last call was before/more than 30 sec then get count again
        if (currentTimeStamp - countTimeStamp > 30000) {
            // Adding time stamp for request to server.
            editor.putLong(PushConstant.pref.PUSH_COUNT_TIME_STAMP,
                    currentTimeStamp);
            editor.commit();

            GetPushCount getPushCount = new GetPushCount(getActivity(),
                    new CardEventListener() {

                        @Override
                        public void onSuccess(Object data) {
                            GetPushCountBean countBean = (GetPushCountBean) data;
                            Utils.log(TAG, "---countBean.newMsgCount--"
                                    + countBean.newMsgCount);

                            // Updating pref with the latest count.
                            editor.putInt(PushConstant.pref.PUSH_COUNT,
                                    pushUnReadCount);
                            editor.commit();
                            pushUnReadCount = Integer
                                    .parseInt(countBean.newMsgCount);
                            onPushCountUpdate(pushUnReadCount);
                        }

                        @Override
                        public void OnError(Object data) {
                            Utils.log(TAG,
                                    "--Error while getting push count data-- ");
                        }
                    });
            getPushCount.sendRequest(XtifySDK.getXidKey(getActivity()
                    .getApplicationContext()));
        } else {
            pushUnReadCount = pushSharedPrefs.getInt(
                    PushConstant.pref.PUSH_COUNT, 0);
        }
    }

}

/*
 * .add(FacadeFactory.getCustomerServiceFacade() .getCustomerServiceSection())
 */