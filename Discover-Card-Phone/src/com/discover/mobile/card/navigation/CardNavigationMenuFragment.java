package com.discover.mobile.card.navigation;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.AccountSectionInfo;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.earncashbackbonus.EarnCashbackBonusInfo;
import com.discover.mobile.card.help.CustomerServiceContactInfo;
import com.discover.mobile.card.home.HomeSectionInfo;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.miles.MilesInfo;
import com.discover.mobile.card.miles.RedeemMilesInfo;
import com.discover.mobile.card.payments.PaymentsSectionInfo;
import com.discover.mobile.card.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.card.redeemcashbackbonus.RedeemCashbackBonusInfo;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;

public class CardNavigationMenuFragment extends NavigationMenuFragment {
    static final String TAG = "CardNavigationMenuFragment";

    CardMenuInterface cardMenuInterface;
    public static ImmutableList<ComponentInfo> CARD_SECTION_LIST = null;

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
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "No Version available.");
            }

        }
        version.setText("Version " + versionName);
        copy.setText("\u00a9" + year + " Discover Bank, Member FDIC");

        populateLeftNavigationMenu();

        final CardNavigationRootActivity activity = (CardNavigationRootActivity) getActivity();
        activity.setMenu(this);

        NavigationItem.initializeAdapterWithSections(navigationItemAdapter,
                CARD_SECTION_LIST, new HomeSummaryFragment());
        setListAdapter(navigationItemAdapter);
        
        privacy.setClickable(true);
        privacy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cardMenuInterface.sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));
			}
		});
    }

    @Override
    public void onListItemClick(final ListView listView,
            final View clickedView, final int position, final long id) {
        super.onListItemClick(listView, clickedView, position, id);

        TextView textView = (TextView) clickedView.findViewById(R.id.title);
        final String text = (String) textView.getText();
        try {

            cardMenuInterface.sendNavigationTextToPhoneGapInterface(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(text.equals("Home") || text.equals("Account")
                || text.equals("Recent Activity")
                || text.equals("Search Transactions")
                || text.equals("Account Summary") || text.equals("Statements")
                || text.equals("Payments") || text.equals("Make a Payment")
                || text.equals("Manage Payments")
                || text.equals("Manage Bank Information")
                || text.equals("Send Money")
                || text.equals("Send Money History")
                || text.equals("Earn Cashback Bonus")
                || text.equals("Sign up for 2%") || text.equals("Extras")
                || text.equals("Refer A Friend")
                || text.equals("Redeem Cashback Bonus")
                || text.equals("Partner Gift Cards & eCerts")
                || text.equals("Discover Gift Cards")
                || text.equals("Statement Credit")
                || text.equals("Direct Deposit")
                || text.equals("Pay with Cashback Bonus")
                || text.equals("Redemption History")
                || text.equals("Earn Miles")
                || text.equals("Earn More Miles Rewards")
                || text.equals("Sign up for Miles")
                || text.equals("Redeem Miles")
                || text.equals("Profile & Settings")
                || text.equals("Manage Text & Alerts")
                || text.equals("Alerts History")
                || text.equals("Create Cash PIN")
                || text.equals("Customer Service") || text.equals("Contact Us") || text
                    .equals("Frequently Asked Questions"))) {

            BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) DiscoverActivityManager
                    .getActiveActivity();
            baseFragmentActivity.showAbove();

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

        ArrayList<ComponentInfo> tempList = new ArrayList<ComponentInfo>();

        CardShareDataStore dataStore = CardShareDataStore.getInstance(context);

        AccountDetails accData = (AccountDetails) dataStore
                .getValueOfAppCache(getResources().getString(
                        R.string.account_details));

        if (null != accData) {

            Log.d("MenuFragment", "acc data : incentiveTypeCode :"
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

            if (supportedMenuItems.indexOf(context
                    .getString(R.string.card_cashback)) != -1) {

                CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                        .add(new HomeSectionInfo())
                        .add(new AccountSectionInfo())
                        .add(new PaymentsSectionInfo())
                        .add(new EarnCashbackBonusInfo())
                        .add(new RedeemCashbackBonusInfo())
                        .add(new ProfileAndSettingsSectionInfo())
                        .add(new CustomerServiceContactInfo()).build();
            }

            if (supportedMenuItems.indexOf(context
                    .getString(R.string.card_miles)) != -1) {
                CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                        .add(new HomeSectionInfo())
                        .add(new AccountSectionInfo())
                        .add(new PaymentsSectionInfo()).add(new MilesInfo())
                        .add(new RedeemMilesInfo())
                        .add(new ProfileAndSettingsSectionInfo())
                        .add(new CustomerServiceContactInfo()).build();

            }
        }

        if (null == CARD_SECTION_LIST) {
            // Added for the Other Card Types like Essential etc
            CARD_SECTION_LIST = ImmutableList.<ComponentInfo> builder()
                    .add(new HomeSectionInfo()).add(new AccountSectionInfo())
                    .add(new PaymentsSectionInfo()).build();
        }
    }

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
}

/*
 * .add(FacadeFactory.getCustomerServiceFacade() .getCustomerServiceSection())
 */