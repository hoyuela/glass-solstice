package com.discover.mobile.card.navigation;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
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
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
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
import com.discover.mobile.card.phonegap.plugins.HybridControlPlugin;
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
	private PageTimeOutUtil pageTimeOutUtil;

	private CordovaPlugin activityResultCallback;
	private final boolean keepRunning = false;

	private static final int DISPLAY_STATEMENTS = 1;
	private static final int PICK_CONTACT = 5;
	private static final int PICK_CREDENTIAL = 6;
	private Bundle extras;
	private boolean pushStatus;
	private final String LOG_TAG = CardNavigationRootActivity.class
			.getSimpleName();
	private int redirect;


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		statusBarFragment = (StatusBarFragment) getSupportFragmentManager()
				.findFragmentById(R.id.status_bar);

		mCardStoreData = CardShareDataStore.getInstance(this
				.getApplicationContext());
		mCardStoreData.addToAppCache("isFragmentAdded", false);

		// Add CordovaWebFrag to initialization, if it is not already there
		cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
				.findFragmentByTag("CordovaWebFrag");
		final FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.hide(statusBarFragment);
		// fragmentTransaction.commit();

		Log.d("CardNavigationRootActivity", "adding frag");
		if (cordovaWebFrag == null) {
			cordovaWebFrag = new CordovaWebFrag();
			cordovaWebFrag.setContext(this.getContext());

			final boolean isFragmentAdded = (Boolean) mCardStoreData
					.getValueOfAppCache("isFragmentAdded");

			if (isFragmentAdded) {
				Log.d("CardNavigationRootActivity", "frag is already added");
				fragmentTransaction.remove(cordovaWebFrag)
				.add(R.id.navigation_content, cordovaWebFrag, "CordovaWebFrag")
				.addToBackStack("CordovaWebFrag").commit();
			} else {
				fragmentTransaction
				.add(R.id.navigation_content, cordovaWebFrag, "CordovaWebFrag")
				.addToBackStack("CordovaWebFrag").commit();
				// fragmentTransaction.hide(frag);
				mCardStoreData.addToAppCache("isFragmentAdded", true);
			}
		}

		// Check intent for logout action and call logout function.
		final String action = getIntent().getAction();

		if (null != action) {
			if (action.equals(getString(R.string.logout_broadcast_action))) {
				Log.d("CardNavigationRootActivity", "Logout action is captured");
				logout();
				isLogout = true;
			}
		}

		// Start inital page timer.
		if (!isLogout) {
			PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();
		}

		/*
		 * pageTimeOutUtil = new PageTimeOutUtil(this);
		 * pageTimeOutUtil.startPageTimer();
		 */
		extras = getIntent().getExtras();
		if(extras != null){
			pushStatus = extras.getBoolean(PushConstant.extras.PUSH_GET_CALL_STATUS);		 
		}
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

			final SharedPreferences pushSharedPrefs = getSharedPreferences(PushConstant.pref.PUSH_SHARED, //TODO: Push
					Context.MODE_PRIVATE);
			redirect = pushSharedPrefs.getInt(PushConstant.pref.PUSH_NAVIGATION, 0);
			String requestId = pushSharedPrefs.getString(PushConstant.pref.PUSH_REQUEST_ID, "");

			if(requestId != null && !requestId.equalsIgnoreCase(""))
			{
				final PushReadMessage pushReadMessage = new PushReadMessage(getActivity(), new CardEventListener()
				{					
					@Override
					public void onSuccess(final Object data)
					{
						Log.i(LOG_TAG, "On Sucess()");						
					}

					@Override
					public void OnError(final Object data)
					{
						Log.i(LOG_TAG, "On OnError()");
					}
				});
				try
				{
					pushReadMessage.sendRequest(requestId);

					//Once request id send, make sure it wont call again.
					//So making it blank.
					requestId = "";
				}
				catch (final JsonGenerationException e)
				{
					final CardErrorBean data = new CardErrorBean("error", true);
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
					cardErrorResHandler.handleCardError(data);
					e.printStackTrace();
				}
				catch (final JsonMappingException e)
				{
					final CardErrorBean data = new CardErrorBean("error", true);
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
					cardErrorResHandler.handleCardError(data);
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					final CardErrorBean data = new CardErrorBean("error", true);
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
					cardErrorResHandler.handleCardError(data);
					e.printStackTrace();
				}
			}
			if(redirect > 0)
			{
				final Editor editor = pushSharedPrefs.edit();
				editor.putInt(PushConstant.pref.PUSH_NAVIGATION, 0);
				editor.commit();

				if(!pushSharedPrefs.getBoolean(PushConstant.pref.PUSH_OFFLINE, true))
				{
					sendNavigationTextToPhoneGapInterface(getString(redirect));
				}
				else
				{
					final Handler handler = new Handler();
					Utils.isSpinnerForOfflinePush=true;
					Utils.isSpinnerAllowed=true;

					Utils.showSpinner(getActivity(), null, null);
					handler.postDelayed(new Runnable()
					{						
						@Override
						public void run()
						{
							sendNavigationTextToPhoneGapInterface(getString(redirect));
							Utils.isSpinnerForOfflinePush=false;
							Utils.hideSpinner();
						}
					}, 60000);
				}
			}
			else
			{
				if(pushStatus)
				{
					pushStatus = false;
					makeFragmentVisible(new PushNowAvailableFragment());
				}
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

		if (!(text.equals(getString(R.string.section_title_account)) || text.equals(getString(R.string.section_title_payments))
				|| text.equals(getString(R.string.section_title_earn_cashback_bonus))
				|| text.equals(getString(R.string.section_title_redeem_cashback_bonus))
				|| text.equals(getString(R.string.section_title_profile_and_settings))
				|| text.equals(getString(R.string.section_title_customer_service))
				|| text.equals(getString(R.string.section_title_miles)) || text.equals(getString(R.string.section_title_home)))) {
			//			cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
			//					.findFragmentByTag("CordovaWebFrag");
			if (cordovaWebFrag != null) {
				try {
					Utils.isSpinnerAllowed=true;
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {

							Utils.showSpinner(getActivity(),null,null);

						}},500);  
				} catch (final Exception e) {
					e.printStackTrace();
				}
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
	public Object onMessage(final String message, final Object value) {
		return null;
	}

	@Override
	public void setActivityResultCallback(final CordovaPlugin plugin) {
		activityResultCallback = plugin;
	}

	@Override
	public void startActivityForResult(final CordovaPlugin plugin,
			final Intent intent, final int requestCode) {
		activityResultCallback = plugin;

		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void logout() {
		Log.d("CardNavigationRootActivity", "inside logout...");
		// super.logout();
		Utils.isSpinnerAllowed = true;
		final WSRequest request = new WSRequest();
		final String url = NetworkUtility.getWebServiceUrl(this,
				R.string.logOut_url);
		request.setUrl(url);
		request.setMethodtype("POST");
		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, null,
				"Discover", "Signing Out...", this);
		serviceCall.execute(request);

	}

	@Override
	public void OnError(final Object data) {
		// CardErrorResponseHandler cardErrorResHandler = new
		// CardErrorResponseHandler(
		// this);
		// cardErrorResHandler.handleCardError((CardErrorBean) data);

		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
		clearNativeCache();
		clearJQMCache(); // Call this method to clear JQM cache.

		PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();
	}

	@Override
	public void onSuccess(final Object data) {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
		clearNativeCache();
		clearJQMCache(); // Call this method to clear JQM cache.

		PageTimeOutUtil.getInstance(this.getContext()).destroyTimer();
	}

	/*
	 * This funciton will clear native global cache.
	 */
	private void clearNativeCache() {

		final CardShareDataStore cardShareDataStore = CardShareDataStore
				.getInstance(getActivity());
		Log.d("clear cache", "get token before clear"
				+ cardShareDataStore.getCookieManagerInstance().getSecToken());
		cardShareDataStore.clearCache(); // Call this method to clear native
		// cache
		cardShareDataStore.getCookieManagerInstance().clearSecToken(); // Call
		// to
		Log.d("clear cache", "get token after clear"
				+ cardShareDataStore.getCookieManagerInstance().getSecToken()); // clear
		// the
		// sectoken
	}

	/*
	 * This method will call the JQM CacheManagement to clear JQM cache
	 */
	private void clearJQMCache() {
		//		cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
		//				.findFragmentByTag("CordovaWebFrag");
		if (cordovaWebFrag != null) {
			final CacheManagerUtil cacheMgmt = new CacheManagerUtil(
					cordovaWebFrag.getCordovaWebviewInstance());
			cacheMgmt.clearJQMGlobalCache();
			cacheMgmt.clearJQMHistory();
		} else {
			Log.d("logout from cardnavigation", "Codova webview object is null");
		}
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
		if(title.equalsIgnoreCase(getString(R.string.error_no_title))) {
			showActionBarLogo();
		} else {
			super.setActionBarTitle(title);
		}

		if (null != title) {
			Log.d("CardNavigationRootActivity",
					"inside setActionbartitle n title is " + title);
			//			cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
			//					.findFragmentByTag("CordovaWebFrag");
			cordovaWebFrag.setTitle(title);
			if (title
					.equalsIgnoreCase(getString(R.string.sub_section_title_statements))) {
				highlightMenuItems(cordovaWebFrag.getGroupMenuLocation(),
						cordovaWebFrag.getSectionMenuLocation());
			}
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
				.equalsIgnoreCase("HomeSummaryFragment") && fragCount >= 2) {
			final String fragTag = fragManager.getBackStackEntryAt(fragCount - 1)
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
				mCardStoreData.addToAppCache("currentPageTitle", "Home");
			}
			highlightMenuItems(CardMenuItemLocationIndex.HOME_GROUP, CardMenuItemLocationIndex.HOME_SECTION);

		} else if(fragment.getClass().getSimpleName().equalsIgnoreCase("RedeemMilesFragment")) { 
			return;
		}else {

			getSupportFragmentManager()
			.beginTransaction()
			.remove(fragment)
			.add(R.id.navigation_content, fragment,
					fragment.getClass().getSimpleName())
					// Adds the class name and fragment to the back stack
					.addToBackStack(fragment.getClass().getSimpleName())
					.commit();
		}

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

		if(fragment.getClass().getSimpleName().equalsIgnoreCase("HomeSummaryFragment")){
			mCardStoreData.addToAppCache("currentPageTitle", "Home");

		}

		hideSlidingMenuIfVisible();
	}

	@Override
	public void onBackPressed() {
		/**
		 * Clear any modal that may have been created during the life of the
		 * current activity
		 */
		Log.d("CardNavigationRootActivity", "inside onBackPressed()");

		DiscoverModalManager.clearActiveModal();
		cordovaWebFrag.setTitle(null);
		cordovaWebFrag.setM_currentLoadedJavascript(null);
		final FragmentManager fragManager = this.getSupportFragmentManager();
		final int fragCount = fragManager.getBackStackEntryCount();

		Log.d("CardNavigationRootActivity", "frag count is " + fragCount);

		if (fragCount > 2) {
			final String fragTag = fragManager.getBackStackEntryAt(fragCount - 2)
					.getName();
			// Fragment frg = HybridControlPlugin.frag123;//
			// fragManager.findFragmentByTag(str);

			/*
			 * if (frg instanceof CordovaWebFrag && ((CordovaWebFrag)
			 * frg).getCordovaWebviewInstance() .canGoBack()) {
			 * ((CordovaWebFrag) frg).getCordovaWebviewInstance()
			 * .printBackForwardList(); ((CordovaWebFrag)
			 * frg).getCordovaWebviewInstance().goBack(); } else {
			 */

			final boolean isPopped = fragManager.popBackStackImmediate();
			Log.d("CardNavigationRootActivity", "is fragment popped" + isPopped);

			printFragmentsInBackStack();

			if (fragTag.equalsIgnoreCase("HomeSummaryFragment")) {

				final Fragment homeFragment = fragManager
						.findFragmentByTag("HomeSummaryFragment");
				makeFragmentVisible(homeFragment, false);

			} else {
				sendNavigationTextToPhoneGapInterface(fragTag);
				super.onBackPressed();
			}
		} else {
			sendNavigationTextToPhoneGapInterface("AcHome");
			final Fragment homeFragment = fragManager
					.findFragmentByTag("HomeSummaryFragment");
			makeFragmentVisible(homeFragment, false);

		}
		// }
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
				if (null != fragManager.getBackStackEntryAt(i).getName()) {
					Log.v("CardNavigationRootActivity", fragManager
							.getBackStackEntryAt(i).getName());
				}
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
	public void onAttachFragment(final Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);

		Log.d("CardNavigationRootActivity",
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
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {
		Log.d("CardNavigationRootActivity", "inside onKeyUp");
		final Fragment frg = HybridControlPlugin.frag123;// fragManager.findFragmentByTag(str);
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
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		Log.d("CardNavigationRootActivity", "inside onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		final CordovaPlugin callBack = activityResultCallback;
		if (requestCode == DISPLAY_STATEMENTS) {
			Log.d("CardNavigationRootActivity",
					"inside on activity result of stmt plugin n result code is "
							+ requestCode + " n result code is " + resultCode);
			if (resultCode == StatementActivity.EXPIRE_SESSION || resultCode == StatementActivity.STATEMENT_LOGOUT) {
				logout();
			} else if (resultCode == StatementActivity.MAINT_EXPIRE_SESSION) {
				Log.d("CardNavigationRootActivity",
						"call gethealthcheck from here...");
			} else {
				sendNavigationTextToPhoneGapInterface(getString(R.string.sub_section_title_statements));
			}
		} else if(requestCode == PICK_CONTACT || requestCode == PICK_CREDENTIAL)
		{
			if(null != callBack)
			{
				callBack.onActivityResult(requestCode, resultCode, data);
			}
		}

	}

	@Override
	public String getActionBarTitle() {
		return (String)mCardStoreData.getValueOfAppCache("currentPageTitle");
	}

	public void enableSlidingMenu(final boolean enable)
	{
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setSlidingEnabled(enable);
	}

	public CordovaWebFrag getCordovaWebFragInstance(){
		return cordovaWebFrag;
	}
}
