package com.discover.mobile.card.navigation;

import java.util.concurrent.ExecutorService;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.hybrid.CacheManagerUtil;
import com.discover.mobile.card.ui.modals.ModalConfirmationTop;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.nav.StatusBarFragment;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;

/**
 * Root activity for the application after login. This will transition fragment
 * on and off the screen as well as show the sliding bar as well as the action
 * bar.
 * 
 */

/*
 * NavigationRootActivity (com.discover.mobile.common.nav) ->
 * LoggedInRoboActivity (com.discover.mobile.common) -> BaseFragmentActivity
 * (com.discover.mobile.common) -> SlidingFragmentActivity
 * (com.slidingmenu.lib.app)
 */

public class CardNavigationRootActivity extends NavigationRootActivity
        implements CardMenuInterface, CordovaInterface, CardEventListener,
        CardErrorHandlerUi {

    CordovaWebFrag cordovaWebFrag;
    FrameLayout navigationContent;
    private boolean isLogout = false;

    public StatusBarFragment statusBarFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarFragment = (StatusBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.status_bar);

        // Add CordovaWebFrag to initialization, if it is not already there
        CordovaWebFrag frag = (CordovaWebFrag) this.getSupportFragmentManager()
                .findFragmentByTag("CordovaWebFrag");
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(statusBarFragment);
        // fragmentTransaction.commit();
        if (frag == null) {
            frag = new CordovaWebFrag();
            fragmentTransaction
                    .add(R.id.navigation_content, frag, "CordovaWebFrag")
                    .addToBackStack("CordovaWebFrag").commit();
            // fragmentTransaction.hide(frag);
        }

        // Check intent for logout action and call logout function.
        String action = getIntent().getAction();

        if (null != action) {
            if (action.equals(getString(R.string.logout_broadcast_action))) {
                Log.d("CardNavigationRootActivity", "Logout action is captured");
                logout();
                isLogout = true;
            }
        }

        // Start inital page timer.
        if (!isLogout)
            PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Resume the activity to the state that it was when the activity went to
     * the background
     */
    @Override
    public void onResume() {
        super.onResume();
        // if(!CardSessionContext.getCurrentSessionDetails().isNotCurrentUserRegisteredForPush()
        // && !wasPaused){
        // getSupportFragmentManager().popBackStack();
        // makeFragmentVisible(new PushNowAvailableFragment());
        // }
        final Bundle extras = getIntent().getExtras();
        if (null != extras) {
            handleIntentExtras(extras);
        }
    }

    @Override
    public int getBehindContentView() {
        // TODO Auto-generated method stub
        return R.layout.navigation_card_menu_frame;
    }

    /**
     * Handle the extras passed in an intent
     * 
     * @param extras
     *            - extras passed into the app
     */
    private void handleIntentExtras(final Bundle extras) {
        if (!shouldShowModal) {
            return;
        }
        final String screenType = extras.getString(IntentExtraKey.SCREEN_TYPE);
        if (null != screenType) {
            final String userId = extras.getString(IntentExtraKey.UID);
            final String email = extras.getString(IntentExtraKey.EMAIL);
            final String lastFour = extras
                    .getString(IntentExtraKey.ACCOUNT_LAST4);
            showConfirmationModal(screenType, userId, email, lastFour);
        }

    }

    /**
     * Show the confirmation modal
     * 
     * @param screenType
     *            - screen type to be displayed in the modal
     * @param userId
     *            - user ID to place in the modal
     * @param email
     *            - email to place in the modal
     * @param lastFour
     *            - last four account number digits to place in the modal
     */
    protected void showConfirmationModal(final String screenType,
            final String userId, final String email, final String lastFour) {

        final ModalConfirmationTop top = new ModalConfirmationTop(this, null);
        final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(this,
                top, null);
        top.setUserId(userId);
        top.setEmail(email);
        top.setLastFour(lastFour);
        top.setScreenType(screenType);
        top.getButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                modal.dismiss();
                shouldShowModal = false;
            }
        });
        modal.show();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.discover.mobile.common.BaseRFragmentActivity#getErrorHandler()
     */
    @Override
    public ErrorHandler getErrorHandler() {
        // return CardErrorHandler.getInstance();
        return null;
    }

    @Override
    public void sendNavigationTextToPhoneGapInterface(String text) {
   
        if (!(text.equals("Account") || text.equals("Payments")
                || text.equals("Earn Cashback Bonus")
                || text.equals("Redeem Cashback Bonus")
                || text.equals("Profile & Settings")
                || text.equals("Customer\nService")
                || text.equals("Manage Alerts")
                || text.equals("Alerts History")
                || text.equals("Enroll in Reminders") || text.equals("Home"))) {
            cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
                    .findFragmentByTag("CordovaWebFrag");
            if (cordovaWebFrag != null) {
                Utils.showSpinner(getActivity());
             	this.showAbove();
                cordovaWebFrag.javascriptCall(text);
            }
        }
    }

    // CordovaInterface Methods
    @Override
    public void cancelLoadUrl() {
        // TODO Auto-generated method stub
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    // @Override
    // public Context getContext() {
    // return this.getActivity();
    // }

    @Override
    public ExecutorService getThreadPool() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object onMessage(String message, Object value) {
        return null;
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void logout() {
        Log.d("CardNavigationRootActivity", "inside logout...");
        // super.logout();

        WSRequest request = new WSRequest();
        String url = NetworkUtility.getWebServiceUrl(this, R.string.logOut_url);
        request.setUrl(url);
        request.setMethodtype("POST");
        WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, null,
                "Discover", "Signing Out...", this);
        serviceCall.execute(request);

        clearNativeCache();
        clearJQMCache(); // Call this method to clear JQM cache.
    }

    @Override
    public void OnError(Object data) {
        // CardErrorResponseHandler cardErrorResHandler = new
        // CardErrorResponseHandler(
        // this);
        // cardErrorResHandler.handleCardError((CardErrorBean) data);

        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
    }

    @Override
    public void onSuccess(Object data) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
    }

    /*
     * This funciton will clear native global cache.
     */
    private void clearNativeCache() {
        CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(getActivity());
        cardShareDataStore.clearCache(); // Call this method to clear native
                                         // cache
        cardShareDataStore.getCookieManagerInstance().clearSecToken(); // Call
                                                                       // to
                                                                       // clear
                                                                       // the
                                                                       // sectoken
    }

    /*
     * This method will call the JQM CacheManagement to clear JQM cache
     */
    private void clearJQMCache() {
        cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
                .findFragmentByTag("CordovaWebFrag");
        if (cordovaWebFrag != null) {
            CacheManagerUtil cacheMgmt = new CacheManagerUtil(
                    cordovaWebFrag.getCordovaWebviewInstance());
            cacheMgmt.clearJQMGlobalCache();
            cacheMgmt.clearJQMHistory();
        } else
            Log.d("logout from cardnavigation", "Codova webview object is null");
    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.common.BaseFragmentActivity#setActionBarTitle(java
     * .lang.String)
     */
    @Override
    public void setActionBarTitle(String title) {
        super.setActionBarTitle(title);
        if (null != title) {
            Log.d("CardNavigationRootActivity",
                    "inside setActionbartitle n title is " + title);
            cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
                    .findFragmentByTag("CordovaWebFrag");
            cordovaWebFrag.setTitle(title);
        }
    }
}
