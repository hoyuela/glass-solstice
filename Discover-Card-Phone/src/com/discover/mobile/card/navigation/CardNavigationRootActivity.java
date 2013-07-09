package com.discover.mobile.card.navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.discover.mobile.PushConstant;
import com.discover.mobile.card.CardMenuItemLocationIndex;
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
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.hybrid.CacheManagerUtil;
import com.discover.mobile.card.passcode.enable.PasscodeEnableStep1Fragment;
import com.discover.mobile.card.passcode.enable.PasscodeEnableStep2Fragment;
import com.discover.mobile.card.passcode.menu.PasscodeMenuFragment;
import com.discover.mobile.card.passcode.remove.PasscodeRemoveFragment;
import com.discover.mobile.card.passcode.setup.PasscodeSetupStep1Fragment;
import com.discover.mobile.card.passcode.setup.PasscodeSetupStep2Fragment;
import com.discover.mobile.card.passcode.update.PasscodeUpdateStep1Fragment;
import com.discover.mobile.card.passcode.update.PasscodeUpdateStep2Fragment;
import com.discover.mobile.card.passcode.update.PasscodeUpdateStep3Fragment;
import com.discover.mobile.card.phonegap.plugins.HybridControlPlugin;
import com.discover.mobile.card.profile.quickview.QuickViewSetupFragment;
import com.discover.mobile.card.push.register.PushNowAvailableFragment;
import com.discover.mobile.card.services.push.PushReadMessage;
import com.discover.mobile.card.statement.StatementActivity;
import com.discover.mobile.card.ui.modals.ModalConfirmationTop;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.slidingmenu.lib.SlidingMenu;

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
    private CardShareDataStore mCardStoreData;  
    private CordovaPlugin activityResultCallback;
    

    private static final int DISPLAY_STATEMENTS = 1;
    private static final int PICK_CONTACT = 5;
    private static final int PICK_CREDENTIAL = 6;
    private Bundle extras;
    //private boolean pushStatus;
    private final String LOG_TAG = CardNavigationRootActivity.class
            .getSimpleName();
    private int redirect;
    private boolean isTimeout = false;

    public int cordovaState = -1;
    public static final int CORDOVA_LOADING = 1;
    public static final int CORDOVA_LOADED = 2;
    public static final int CORDOVA_ERROR = 3;
    
    private String navToJQMPage=null;
    
    private ArrayList<String> navigationlist;
    private ArrayList<String> nativeList;
    
    /***
	 * Private contentObserver for settings -> display -> screen rotation setting.
	 * For Defect: 101121
	 * 
	 */
	private ContentObserver contentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (!selfChange) {
				setOrientation();
			}
		}
	};
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cordovaState = CORDOVA_LOADING;
        navToJQMPage=null;
        setNavigationList();
        setNativeList();
        statusBarFragment = (StatusBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.status_bar);

        mCardStoreData = CardShareDataStore.getInstance(this
                .getApplicationContext());

        // Add CordovaWebFrag to initialization, if it is not already there
        cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
                .findFragmentByTag("CordovaWebFrag");
        final FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(statusBarFragment);
        // fragmentTransaction.commit();

        if (cordovaWebFrag == null) {
            cordovaWebFrag = new CordovaWebFrag();
            cordovaWebFrag.setContext(this.getContext());

            fragmentTransaction
                    .remove(cordovaWebFrag)
                    .add(R.id.navigation_content, cordovaWebFrag,
                            "CordovaWebFrag").addToBackStack("CordovaWebFrag")
                    .commit();
        }

        // Check intent for logout action and call logout function.
        final String action = getIntent().getAction();

        if (null != action) {
            if (action.equals(getString(R.string.logout_broadcast_action))) {
                Utils.log("CardNavigationRootActivity",
                        "Logout action is captured");
                logout();
                isLogout = true;
            }
        }

        // Start inital page timer.
        if (!isLogout)
            PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();

        extras = getIntent().getExtras();
        Utils.hideSpinner();
    	// 13.3 changes start
 		mCardStoreData.addToAppCache("onBackPressed", false);
 		// 13.3 changes end
 		
 		// Add Content Change Observer or orientation change setting
 		// For Defect: 101121 Start
 		getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),true, contentObserver);
 		setOrientation();
 		// For Defect: 101121 End
    }

    /**
     * For Defect: 101121
     * Set Orientation of screen according to System Rotation Settings.
     */
    private void setOrientation(){
    	if (android.provider.Settings.System.getInt(
				getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
		// Rotation is ON	
			CardNavigationRootActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		} else {
			// Rotation is OFF
			final Configuration currentConfig = getResources()
					.getConfiguration();
			//Request Activity to set default screen orientation
			CardNavigationRootActivity.this.setRequestedOrientation(currentConfig.orientation);
		}
	}
    private void setNativeList() {
    	nativeList = new ArrayList<String>();
    	nativeList.add(HomeSummaryFragment.class.getSimpleName());
    	nativeList.add(QuickViewSetupFragment.class.getSimpleName());
    	nativeList.add(PasscodeEnableStep1Fragment.class.getSimpleName());
    	nativeList.add(PasscodeEnableStep2Fragment.class.getSimpleName());
    	nativeList.add(PasscodeMenuFragment.class.getSimpleName());
    	nativeList.add(PasscodeRemoveFragment.class.getSimpleName());
    	nativeList.add(PasscodeSetupStep1Fragment.class.getSimpleName());
    	nativeList.add(PasscodeSetupStep2Fragment.class.getSimpleName());
    	nativeList.add(PasscodeUpdateStep1Fragment.class.getSimpleName());
    	nativeList.add(PasscodeUpdateStep2Fragment.class.getSimpleName());
    	nativeList.add(PasscodeUpdateStep3Fragment.class.getSimpleName());
	}

    private void setNavigationList() {
    	navigationlist = new ArrayList<String>();
    	navigationlist.add(getString(R.string.section_title_account));
    	navigationlist.add(getString(R.string.section_title_payments));
    	navigationlist.add(getString(R.string.section_title_earn_cashback_bonus));
    	navigationlist.add(getString(R.string.section_title_redeem_cashback_bonus));
    	navigationlist.add(getString(R.string.section_title_profile_and_settings));
        // 13.3 fast view start
    	navigationlist.add(getString(R.string.sub_section_title_fast_view));
		// 13.3 fast view end
    	navigationlist.add(getString(R.string.section_title_customer_service));
    	navigationlist.add(getString(R.string.section_title_miles));
    	navigationlist.add(getString(R.string.section_title_home));
    	//13.4 passcode
    	navigationlist.add(getString(R.string.sub_section_title_passcode));
		
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

        if (null != extras) {
            handleIntentExtras(extras);

            SharedPreferences pushSharedPrefs = getSharedPreferences(
                    PushConstant.pref.PUSH_SHARED, // TODO: Push
                    Context.MODE_PRIVATE);
            redirect = pushSharedPrefs.getInt(
                    PushConstant.pref.PUSH_NAVIGATION, 0);

            String requestId = pushSharedPrefs.getString(
                    PushConstant.pref.PUSH_REQUEST_ID, "");

            if (requestId != null && !requestId.equalsIgnoreCase("")) {
                PushReadMessage pushReadMessage = new PushReadMessage(
                        getActivity(), new CardEventListener() {
                            @Override
                            public void onSuccess(Object data) {
                                Utils.log(LOG_TAG, "On Sucess()");
                            }

                            @Override
                            public void OnError(Object data) {
                                Utils.log(LOG_TAG, "On OnError()");
                            }
                        });
                try {
                    pushReadMessage.sendRequest(requestId);

                    // Once request id send, make sure it wont call again.
                    // So making it blank.
                    requestId = "";
                    Editor editor = pushSharedPrefs.edit();
                    editor.putString(PushConstant.pref.PUSH_REQUEST_ID, "");
                    editor.commit();
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //redirect = R.string.sub_section_title_make_a_payment;
            if (redirect > 0) {
                	 Editor editor = pushSharedPrefs.edit();
                     editor.putInt(PushConstant.pref.PUSH_NAVIGATION, 0);
                     editor.commit();
                     sendNavigationTextToPhoneGapInterface(getString(redirect));
            } 
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
        top.setDialog(screenType);
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
    public void sendNavigationTextToPhoneGapInterface(final String text) {
    	
        if (!(navigationlist.contains(text))) {
            /*
             * cordovaWebFrag = (CordovaWebFrag)
             * this.getSupportFragmentManager()
             * .findFragmentByTag("CordovaWebFrag");
             */
            if (cordovaWebFrag != null) {
            	if (cordovaState == CORDOVA_LOADING)
            	{
        			Utils.isSpinnerAllowed = true;
        			Utils.showSpinner(this, "Discover", "Loading...");
        			Utils.isSpinnerAllowed = false;
        			navToJQMPage = text;
            	}
            	else if (cordovaState != CORDOVA_ERROR)
            	{
	            	if (text.indexOf("javascript")>-1)
	            	{
	            		cordovaWebFrag.getCordovaWebviewInstance().loadUrl(text);
	            		Utils.isSpinnerAllowed = true;
	        			Utils.hideSpinner();
	            	}
	            	else
	            	{
	                    try {
                            Utils.isSpinnerAllowed = true;
	                        Handler handler = new Handler();
	                        handler.postDelayed(new Runnable() {
	                            @Override
	                            public void run() {
	                                Utils.showSpinner(CardNavigationRootActivity.this,
	                                        "Discover", "Loading...");
	
	                            }
	                        }, 500);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
		                // hlin0, 20130530, integrate with new slidingmenu
		                this.showContent();
	                    cordovaWebFrag.javascriptCall(text);
	            	}
            	}
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
    public Object onMessage(final String message, final Object value) {
        return null;
    }

    @Override
    public void setActivityResultCallback(final CordovaPlugin plugin) {
        this.activityResultCallback = plugin;
    }

    @Override
    public void startActivityForResult(final CordovaPlugin plugin,
            final Intent intent, final int requestCode) {
        this.activityResultCallback = plugin;

        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void logout() {
        Utils.log("CardNavigationRootActivity", "inside logout...");
        // super.logout();
        Utils.isSpinnerAllowed = true;
        Utils.showSpinner(this, "Discover", "Signing Out...");
        final WSRequest request = new WSRequest();
        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.logOut_url);
        request.setUrl(url);
        request.setMethodtype("POST");
        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, null,
                "Discover", null, this);
        serviceCall.execute(request);

    }

    public void idealTimeoutLogout() {
        Utils.log("CardNavigationRootActivity", "inside logout...");
        // super.logout();
        isTimeout = true;
        Utils.isSpinnerAllowed = true;
        Utils.showSpinner(this, "Discover", "Signing Out...");
        final WSRequest request = new WSRequest();
        final String url = NetworkUtility.getWebServiceUrl(this,
                R.string.logOut_url);
        request.setUrl(url);
        request.setMethodtype("POST");
        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, null,
                "Discover", null, this);
        serviceCall.execute(request);

    }

    @Override
    public void OnError(final Object data) {
        // CardErrorResponseHandler cardErrorResHandler = new
        // CardErrorResponseHandler(
        // this);
        // cardErrorResHandler.handleCardError((CardErrorBean) data);

    	clearNativeCache();
        clearJQMCache(); // Call this method to clear JQM cache.
        /* 13.4 changes start*/
        //isTimeout = false;
        PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();

        final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, isTimeout);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        
        Utils.hideSpinner();
        
        finish();
    }

    @Override
    public void onSuccess(final Object data) {
        clearNativeCache();
        clearJQMCache(); // Call this method to clear JQM cache.
       /* 13.4 changes start*/
        //isTimeout = false;
        PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();

    	final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, isTimeout);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        
        Utils.hideSpinner();
        
        finish();
    }

    /*
     * This funciton will clear native global cache.
     */
    private void clearNativeCache() {

        final CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(getActivity());
        Utils.log("clear cache", "get token before clear"
                + cardShareDataStore.getCookieManagerInstance().getSecToken());
        cardShareDataStore.clearCache(); // Call this method to clear native
        // cache
        cardShareDataStore.getCookieManagerInstance().clearAllCookie(); // Call
        // to
        Utils.log("clear cache", "get token after clear"
                + cardShareDataStore.getCookieManagerInstance().getSecToken()); // clear
        // the
        // sectoken
    }

    /*
     * This method will call the JQM CacheManagement to clear JQM cache
     */
    private void clearJQMCache() {
        // cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
        // .findFragmentByTag("CordovaWebFrag");
        if (cordovaWebFrag != null) {
            final CacheManagerUtil cacheMgmt = new CacheManagerUtil(
                    cordovaWebFrag.getCordovaWebviewInstance());
            cacheMgmt.clearJQMGlobalCache();
            cacheMgmt.clearJQMHistory();
        } else
            Utils.log("logout from cardnavigation",
                    "Codova webview object is null");
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
    public void setActionBarTitle(final String title) {
        if (title.equalsIgnoreCase(getString(R.string.error_no_title)))
            showActionBarLogo();
        else
            super.setActionBarTitle(title);

        if (null != title) {
            Utils.log("CardNavigationRootActivity",
                    "inside setActionbartitle n title is " + title);
            // cordovaWebFrag = (CordovaWebFrag)
            // this.getSupportFragmentManager()
            // .findFragmentByTag("CordovaWebFrag");
            cordovaWebFrag.setTitle(title);
            if (title
                    .equalsIgnoreCase(getString(R.string.sub_section_title_statements)))
                highlightMenuItems(cordovaWebFrag.getGroupMenuLocation(),
                        cordovaWebFrag.getSectionMenuLocation());
            mCardStoreData.addToAppCache("currentPageTitle", title);
        }
    }

    public void updateActionBarTitle() {
        final TextView titleView = (TextView) findViewById(R.id.title_view);
        titleView.invalidate();
        titleView.postInvalidate();
    }

    /**
     * Sets the fragment seen by the user
     * 
     * @param fragment
     *            - fragment to be shown
     */
    @Override
    protected void setVisibleFragment(final Fragment fragment) {
        currentFragment = fragment;

        final FragmentManager fragManager = this.getSupportFragmentManager();
        final int fragCount = fragManager.getBackStackEntryCount();

        if (fragment.getClass().getSimpleName()
                .equalsIgnoreCase("HomeSummaryFragment")
                && fragCount >= 2) {
            String fragTag = fragManager.getBackStackEntryAt(fragCount - 1)
                    .getName();

            if (!fragTag.equalsIgnoreCase("HomeSummaryFragment")) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .add(R.id.navigation_content, fragment,
                                fragment.getClass().getSimpleName())
                        // Adds the class name and fragment to the back
                        // stack
                        .addToBackStack(fragment.getClass().getSimpleName())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .add(R.id.navigation_content, fragment,
                                fragment.getClass().getSimpleName()).commit();
            }
            mCardStoreData.addToAppCache("currentPageTitle", "Home");

            highlightMenuItems(CardMenuItemLocationIndex.HOME_GROUP,
                    CardMenuItemLocationIndex.HOME_SECTION);
            // Fix for defect 96085
            sendNavigationTextToPhoneGapInterface("javascript:home()");
            // Fix for defect 96085
        } else if (fragment.getClass().getSimpleName()
                .equalsIgnoreCase("RedeemMilesFragment")) {
            return;
        } else if (fragment.getClass().getSimpleName().equalsIgnoreCase("PasscodeLandingFragment")) {
        	//TODO sgoff0 - is there a cleaner way to handle this?
        	//don't keep this fragment in back stack
        	Log.v("CardNavigationRootActivity", "navigate passcode landing fragment without backstack");
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .add(R.id.navigation_content, fragment,
                            fragment.getClass().getSimpleName())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .add(R.id.navigation_content, fragment,
                            fragment.getClass().getSimpleName())
                    // Adds the class name and fragment to the back stack
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }

        PushNowAvailableFragment pushFrag = (PushNowAvailableFragment) this
                .getSupportFragmentManager().findFragmentByTag(
                        "PushNowAvailableFragment");
        if (null != pushFrag) {
            getSupportFragmentManager().beginTransaction().remove(pushFrag)
                    .commit();
        }
        
        //13.3 QuickView Change Start
        if (fragment.getClass().getSimpleName()
				.equalsIgnoreCase("QuickViewSetupFragment")) {
			sendNavigationTextToPhoneGapInterface("javascript:quickView()");
			mCardStoreData.addToAppCache("currentPageTitle", "Quick View");
		}
        //13.3 QuickView Change End
        
        hideSlidingMenuIfVisible();
    }

    /**
     * Sets the fragment seen by the user, but does not add it to the history
     * 
     * @param fragment
     *            - fragment to be shown
     */
    @Override
    protected void setVisibleFragmentNoHistory(final Fragment fragment) {
        currentFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .add(R.id.navigation_content, fragment,
                        fragment.getClass().getSimpleName()).commit();

        if (fragment.getClass().getSimpleName()
                .equalsIgnoreCase("HomeSummaryFragment")) {
            mCardStoreData.addToAppCache("currentPageTitle", "Home");

        }

      //13.3 QuickView Change Start
        if (fragment.getClass().getSimpleName()
				.equalsIgnoreCase("QuickViewSetupFragment")) {
			sendNavigationTextToPhoneGapInterface("javascript:quickView()");
			mCardStoreData.addToAppCache("currentPageTitle", "Quick View");
		}
        //13.3 QuickView Change End
        
        PushNowAvailableFragment pushFrag = (PushNowAvailableFragment) this
                .getSupportFragmentManager().findFragmentByTag(
                        "PushNowAvailableFragment");
        if (null != pushFrag) {
            getSupportFragmentManager().beginTransaction().remove(pushFrag)
                    .commit();
        }

        hideSlidingMenuIfVisible();
    }

    @Override
    public void onBackPressed() {
        /**
         * Clear any modal that may have been created during the life of the
         * current activity
         */
        Utils.log("CardNavigationRootActivity", "inside onBackPressed()");

        DiscoverModalManager.clearActiveModal();
		// 13.3 changes start
		mCardStoreData.addToAppCache("onBackPressed", true);
		// 13.3 changes end        
        cordovaWebFrag.setTitle(null);
        mCardStoreData.addToAppCache("currentPageTitle", null);
        cordovaWebFrag.setM_currentLoadedJavascript(null);
        final FragmentManager fragManager = this.getSupportFragmentManager();
        final int fragCount = fragManager.getBackStackEntryCount();

        Utils.log("CardNavigationRootActivity", "frag count is " + fragCount);

        if (fragCount > 2) {
            String fragTag = fragManager.getBackStackEntryAt(fragCount - 2)
                    .getName();

            boolean isPopped = fragManager.popBackStackImmediate();
            Utils.log("CardNavigationRootActivity", "is fragment popped"
                    + isPopped);
            //13.3 QuicView Changes Start
            if (nativeList.contains(fragTag)) {
                Fragment fragment = fragManager
                        .findFragmentByTag(fragTag);
                makeFragmentVisible(fragment, false);
                //13.3 QuicView Changes End
                if (fragManager.getBackStackEntryCount() == 2) {
                	sendNavigationTextToPhoneGapInterface("javascript:acHome()");
                }

            } else {
                if (fragTag
                        .equalsIgnoreCase(getString(R.string.enhanced_account_security_title))
                        || fragTag.equalsIgnoreCase("No Title")) {
                    Utils.log("CardNavigationRootActivity",
                            "inside onBackPressed()");
                    onBackPressed();
                } else {
                    sendNavigationTextToPhoneGapInterface(fragTag);
                    super.onBackPressed();
                }

            }
        } else {
        	sendNavigationTextToPhoneGapInterface("javascript:acHome()");
            Fragment homeFragment = fragManager
                    .findFragmentByTag("HomeSummaryFragment");
            makeFragmentVisible(homeFragment, false);

        }
        
		// 13.3 changes start
		mCardStoreData.addToAppCache("onBackPressed", false);
		// 13.3 changes end
    }

    /**
     * Utility method used for debugging issues in the back stack
     */
    @Override
    public void printFragmentsInBackStack() {

        final FragmentManager fragManager = this.getSupportFragmentManager();
        final int fragCount = fragManager.getBackStackEntryCount();
        if (fragCount > 0) {
            for (int i = 0; i < fragCount; i++) {
                if (null != fragManager.getBackStackEntryAt(i).getName())
                    Log.v("CardNavigationRootActivity", fragManager
                            .getBackStackEntryAt(i).getName());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.FragmentActivity#onAttachFragment(android.support
     * .v4.app.Fragment)
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        // TODO Auto-generated method stub
        super.onAttachFragment(fragment);

        Utils.log(
                "CardNavigationRootActivity",
                "inside onAttachFragment n attached fragment is "
                        + fragment.getTag());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.slidingmenu.lib.app.SlidingFragmentActivity#onKeyUp(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Utils.log("CardNavigationRootActivity", "inside onKeyUp");
        Fragment frg = HybridControlPlugin.frag123;// fragManager.findFragmentByTag(str);
        if (frg instanceof CordovaWebFrag
                && ((CordovaWebFrag) frg).getCordovaWebviewInstance()
                        .canGoBack()) {

            return ((CordovaWebFrag) frg).getCordovaWebviewInstance().onKeyUp(
                    keyCode, event);
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Utils.log("CardNavigationRootActivity", "inside onKeyDown");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CordovaPlugin callBack = this.activityResultCallback;
        if (requestCode == DISPLAY_STATEMENTS) {
            Utils.log("CardNavigationRootActivity",
                    "inside on activity result of stmt plugin n result code is "
                            + requestCode + " n result code is " + resultCode);
            if (resultCode == StatementActivity.EXPIRE_SESSION
                    || resultCode == StatementActivity.STATEMENT_LOGOUT) {
                logout();
            } else if (resultCode == StatementActivity.MAINT_EXPIRE_SESSION)
                Utils.log("CardNavigationRootActivity",
                        "call gethealthcheck from here...");
            else {
                sendNavigationTextToPhoneGapInterface(getString(R.string.sub_section_title_statements));
            }
        } else if (requestCode == PICK_CONTACT
                || requestCode == PICK_CREDENTIAL) {
            if (null != callBack) {
                callBack.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    public String getActionBarTitle() {
        return (String) mCardStoreData.getValueOfAppCache("currentPageTitle");
    }

    public void enableSlidingMenu(boolean enable) {
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setSlidingEnabled(enable);
    }

    public CordovaWebFrag getCordovaWebFragInstance() {
        return cordovaWebFrag;
    }

    /**
     * This method will be called once PhoneGap is ready to take request.
     */
    public void isDeviceReady() {
    	cordovaState = CORDOVA_LOADED;
    	if (null!=navToJQMPage)
    	{
    		Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                	sendNavigationTextToPhoneGapInterface(navToJQMPage);
                }
            }, 200);
    	}
    }
    
    public void onErrorOfCordovaLoading()
    {
    	cordovaState = CORDOVA_ERROR;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.common.BaseFragmentActivity#makeFragmentVisible(android
     * .support.v4.app.Fragment)
     */
    @Override
    public void makeFragmentVisible(Fragment fragment) {
        /**
         * Clear any modal that may have been created during the life of the
         * current fragment
         */
        DiscoverModalManager.clearActiveModal();

        setVisibleFragment(fragment);
        hideSlidingMenuIfVisible();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.discover.mobile.common.BaseFragmentActivity#makeFragmentVisible(android
     * .support.v4.app.Fragment, boolean)
     */
    @Override
    public void makeFragmentVisible(Fragment fragment, boolean addToHistory) {
        /**
         * Clear any modal that may have been created during the life of the
         * current fragment
         */
        DiscoverModalManager.clearActiveModal();

        if (addToHistory) {
            setVisibleFragment(fragment);
        } else {
            setVisibleFragmentNoHistory(fragment);
        }
        hideSlidingMenuIfVisible();
    }
    /***
	 * For Defect: 101121
	 * onConfigurationChanged need to handle
	 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	setOrientation();
    	super.onConfigurationChanged(newConfig);
    }
}
