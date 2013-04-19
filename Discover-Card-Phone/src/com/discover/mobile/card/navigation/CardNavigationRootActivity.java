package com.discover.mobile.card.navigation;

import java.util.concurrent.ExecutorService;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.discover.mobile.card.phonegap.plugins.HybridControlPlugin;
import com.discover.mobile.card.statement.StatementActivity;
import com.discover.mobile.card.ui.modals.ModalConfirmationTop;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.NavigationRootActivity;
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

	private CardShareDataStore mCardStoreData;
	private PageTimeOutUtil pageTimeOutUtil;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statusBarFragment = (StatusBarFragment) getSupportFragmentManager()
				.findFragmentById(R.id.status_bar);

		mCardStoreData = CardShareDataStore.getInstance(this
				.getApplicationContext());
		mCardStoreData.addToAppCache("isFragmentAdded", false);

		// Add CordovaWebFrag to initialization, if it is not already there
		CordovaWebFrag frag = (CordovaWebFrag) this.getSupportFragmentManager()
				.findFragmentByTag("CordovaWebFrag");
		final FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.hide(statusBarFragment);
		// fragmentTransaction.commit();

		Log.d("CardNavigationRootActivity", "adding frag");
		if (frag == null) {
			frag = new CordovaWebFrag();
			frag.setContext(this.getContext());

			boolean isFragmentAdded = (Boolean) mCardStoreData
					.getValueOfAppCache("isFragmentAdded");

			if (isFragmentAdded) {
				Log.d("CardNavigationRootActivity", "frag is already added");
				fragmentTransaction.remove(frag)
						.add(R.id.navigation_content, frag, "CordovaWebFrag")
						.addToBackStack("CordovaWebFrag").commit();
			} else {
				fragmentTransaction
						.add(R.id.navigation_content, frag, "CordovaWebFrag")
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
		if (!isLogout)
			PageTimeOutUtil.getInstance(this.getContext()).startPageTimer();

		/*
		 * pageTimeOutUtil = new PageTimeOutUtil(this);
		 * pageTimeOutUtil.startPageTimer();
		 */
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

		if (!(text.equals("Account") || text.equals("Payments")
				|| text.equals("Earn Cashback Bonus")
				|| text.equals("Redeem Cashback Bonus")
				|| text.equals("Earn Miles")
				|| text.equals("Profile & Settings")
				|| text.equals("Customer Service")
				|| text.equals("Manage Alerts")
				|| text.equals("Alerts History")
				|| text.equals("Enroll in Reminders") || text.equals("Home"))) {
			cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
					.findFragmentByTag("CordovaWebFrag");
			if (cordovaWebFrag != null) {
				try {
					Utils.showSpinner(getActivity(), null, null);
				} catch (Exception e) {
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
	public void setActivityResultCallback(final CordovaPlugin arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivityForResult(final CordovaPlugin arg0,
			final Intent arg1, final int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout() {
		Log.d("CardNavigationRootActivity", "inside logout...");
		// super.logout();

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

		PageTimeOutUtil.destroyTimer();
	}

	@Override
	public void onSuccess(final Object data) {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
		bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
		FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
		clearNativeCache();
		clearJQMCache(); // Call this method to clear JQM cache.

		PageTimeOutUtil.destroyTimer();
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
		cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
				.findFragmentByTag("CordovaWebFrag");
		if (cordovaWebFrag != null) {
			final CacheManagerUtil cacheMgmt = new CacheManagerUtil(
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
	public void setActionBarTitle(final String title) {
		super.setActionBarTitle(title);
		if (null != title) {
			Log.d("CardNavigationRootActivity",
					"inside setActionbartitle n title is " + title);
			cordovaWebFrag = (CordovaWebFrag) this.getSupportFragmentManager()
					.findFragmentByTag("CordovaWebFrag");
			cordovaWebFrag.setTitle(title);
			if (title
					.equalsIgnoreCase(getString(R.string.sub_section_title_statements)))
				highlightMenuItems(cordovaWebFrag.getGroupMenuLocation(),
						cordovaWebFrag.getSectionMenuLocation());
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
			}			
		}else{

		getSupportFragmentManager()
				.beginTransaction()
				.remove(fragment)
				.add(R.id.navigation_content, fragment,
						fragment.getClass().getSimpleName())
				// Adds the class name and fragment to the back stack
				.addToBackStack(fragment.getClass().getSimpleName()).commit();
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
;
		final FragmentManager fragManager = this.getSupportFragmentManager();
		final int fragCount = fragManager.getBackStackEntryCount();

		Log.d("CardNavigationRootActivity", "frag count is " + fragCount);

		if (fragCount > 2) {
			String fragTag = fragManager.getBackStackEntryAt(fragCount - 2)
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

			boolean isPopped = fragManager.popBackStackImmediate();
			Log.d("CardNavigationRootActivity", "is fragment popped" + isPopped);

			printFragmentsInBackStack();

			if (fragTag.equalsIgnoreCase("HomeSummaryFragment")) {

				Fragment homeFragment = fragManager
						.findFragmentByTag("HomeSummaryFragment");
				makeFragmentVisible(homeFragment, false);

			} else {
				sendNavigationTextToPhoneGapInterface(fragTag);
				super.onBackPressed();
			}
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("CardNavigationRootActivity", "inside onKeyUp");
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
		Log.d("CardNavigationRootActivity", "inside onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			Log.d("CardNavigationRootActivity",
					"inside on activity result of stmt plugin n result code is "
							+ requestCode + " n result code is " + resultCode);
			if (resultCode == StatementActivity.EXPIRE_SESSION) {
				logout();
			} else if (resultCode == StatementActivity.MAINT_EXPIRE_SESSION)
				Log.d("CardNavigationRootActivity",
						"call gethealthcheck from here...");
			else {
				sendNavigationTextToPhoneGapInterface(getString(R.string.sub_section_title_statements));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
