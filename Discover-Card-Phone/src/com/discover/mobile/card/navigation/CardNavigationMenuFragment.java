package com.discover.mobile.card.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.AccountSectionInfo;
import com.discover.mobile.card.earncashbackbonus.EarnCashbackBonusInfo;
import com.discover.mobile.card.home.HomeSectionInfo;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.payments.PaymentsSectionInfo;
import com.discover.mobile.card.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.card.redeemcashbackbonus.RedeemCashbackBonusInfo;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;

public class CardNavigationMenuFragment extends NavigationMenuFragment {
    static final String TAG = "CardNavigationMenuFragment";

    CardMenuInterface cardMenuInterface;

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

        NavigationItem.initializeAdapterWithSections(navigationItemAdapter,
                CARD_SECTION_LIST, new HomeSummaryFragment());
        setListAdapter(navigationItemAdapter);

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
        if (!(text.equals("Home") || text.equals("Manage Alerts")
                || text.equals("Alert History")
                || text.equals("Enroll in Reminders") || text.equals("Account")
                || text.equals("Recent Activity")
                || text.equals("Search Transaction")
                || text.equals("Account Summary") || text.equals("Statements")
                || text.equals("Payments") || text.equals("Make a Payment")
                || text.equals("Manage Payments")
                || text.equals("Manage Bank Information")
                || text.equals("Send Money")
                || text.equals("Send Money History")
                || text.equals("Earn Cashback Bonus")
                || text.equals("Sign up for Cashback") || text.equals("Extras")
                || text.equals("Refer A Friend")
                || text.equals("Redeem Cashback Bonus")
                || text.equals("Partner Gift Cards n eCerts")
                || text.equals("Discover Gift Cards")
                || text.equals("Statement Credit")
                || text.equals("Direct Deposit")
                || text.equals("Pay with Cashback Bonus")
                || text.equals("Redemption History")
                || text.equals("Profile & Settings")
                || text.equals("Customer\nService") || text
                    .equals("Contact Us"))) {

            BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) DiscoverActivityManager
                    .getActiveActivity();
            baseFragmentActivity.showAbove();

        }
    }

    /**
     * Immutable list showing all the top level sections that are displayed in
     * the sliding nav menu
     */
    public static final ImmutableList<ComponentInfo> CARD_SECTION_LIST = ImmutableList
            .<ComponentInfo> builder()
            // Add Sections below
            .add(new HomeSectionInfo())
            .add(new AccountSectionInfo())
            .add(new PaymentsSectionInfo())
            .add(new EarnCashbackBonusInfo())
            .add(new RedeemCashbackBonusInfo())
            .add(new ProfileAndSettingsSectionInfo())
            .add(FacadeFactory.getCustomerServiceFacade()
                    .getCustomerServiceSection()).build();

}
