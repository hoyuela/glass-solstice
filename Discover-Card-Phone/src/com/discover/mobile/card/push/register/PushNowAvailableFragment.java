package com.discover.mobile.card.push.register;

import java.io.IOException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;

import com.discover.mobile.common.LoggedInRoboActivity;
import com.discover.mobile.common.nav.NavigationRootActivity;

import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.navigation.CardMenuInterface;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xtify.sdk.api.XtifySDK;

/**
 * This is the screen that is showed to the user immediately following the login
 * of the activity. This will only be shown if the user has not chosen or denied
 * the use of push notifications. If the user has chosen to deny or opt into the
 * alerts then this will not be shown and the user will be forwarded to the
 * account home.
 * 
 * @author jthornton
 * 
 */
public class PushNowAvailableFragment extends BasePushRegistrationUI {

    /** String representing this class to enter into the back stack */
    private static final String TAG = PushNowAvailableFragment.class
            .getSimpleName();
    private static final String REFERER = "cardHome-pg";

    /**
     * Creates the fragment, inflates the view and defines the button
     * functionality.
     * 
     * @param inflater
     *            - inflater that will inflate the layout
     * @param container
     *            - container that will hold the views
     * @param savedInstanceState
     *            - bundle containing information about the previous state of
     *            the fragment
     * @return the inflated view
     */
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(R.layout.push_now_available, null);
        final Button manageAlerts = (Button) view
                .findViewById(R.id.manage_alerts_button);
        final TextView accountHome = (TextView) view
                .findViewById(R.id.account_home_view);

        TextView provideFeedback = (TextView) view
                .findViewById(R.id.provide_feedback_button);
        TextView termsOfUse = (TextView) view.findViewById(R.id.privacy_terms);

        provideFeedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utils.createProvideFeedbackDialog(getActivity(), REFERER);
            }
        });

        termsOfUse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((NavigationRootActivity) getActivity()).getSlidingMenu()
                        .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                ((LoggedInRoboActivity) getActivity()).enableMenuButton();

                ((CardMenuInterface) getActivity())
                        .sendNavigationTextToPhoneGapInterface(getString(R.string.privacy_terms_title));
            }
        });

        manageAlerts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    registerWithDiscover(ACCEPT, XtifySDK
                            .getXidKey(getActivity().getApplicationContext()));
                } catch (JsonGenerationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        accountHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                try {
                    registerWithDiscover(DECLINE, XtifySDK
                            .getXidKey(getActivity().getApplicationContext()));
                    // changeToDeclineScreen();
                } catch (JsonGenerationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Disabling menu navigation
        ((NavigationRootActivity) this.getActivity()).getSlidingMenu()
                .setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        ((LoggedInRoboActivity) this.getActivity()).disableMenuButton();
    }

    /**
     * Swap out this fragment and replace it with the push manage fragment so
     * that the user can manage his/her alerts
     */
    @Override
    public void changeToAcceptScreen() {
        super.changeToAcceptScreen(TAG);
    }

    /**
     * Swap out this fragment and replace it with the push manage fragment so
     * that the user can manage his/her alerts
     */
    @Override
    public void changeToDeclineScreen() {
        this.makeFragmentVisible(new HomeSummaryFragment());
    }

    /**
     * Return the integer value of the string that needs to be displayed in the
     * title
     */
    @Override
    public int getActionBarTitle() {
        return R.string.manage_push_fragment_title;
    }

    @Override
    public int getGroupMenuLocation() {
        return CardMenuItemLocationIndex.HOME_GROUP;
    }

    @Override
    public int getSectionMenuLocation() {
        return CardMenuItemLocationIndex.HOME_SECTION;
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        ((NavigationRootActivity) this.getActivity()).getSlidingMenu()
                .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        ((LoggedInRoboActivity) this.getActivity()).enableMenuButton();
    }
}
