package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.navigation.CordovaWebFrag;
import com.discover.mobile.card.navigation.StatusBarFragment;
import com.discover.mobile.common.Globals;

public class HybridControlPlugin extends CordovaPlugin {

    public static final String showMenu = "showMenu";
    public static final String hideMenu = "hideMenu";
    public static final String showHeaderNavBar = "showHeaderNavBar";
    public static final String hideHeaderNavBar = "hideHeaderNavBar";
    public static final String showHeaderStatusBar = "showHeaderStatusBar";
    public static final String hideHeaderStatusBar = "hideHeaderStatusBar";
    public static final String setTitleView = "setTitleView";
    public static final String changeStatusText = "changeStatusText";
    public static final String popPhoneGapToFront = "popPhoneGapToFront";
    public static final String pushFragment = "pushFragment";
    public static final String popCurrentFragment = "popCurrentFragment";
    public static final String getAccountDetails = "getAccountDetails";
    public static final String dismissProgressBar = "dismissProgressBar";
    public static final String getSecToken = "getSecToken";
    public static final String updatedAccountDetails = "updatedAccountDetails";
    public static final String getStrongAuthSvcs = "getStrongAuthSvcs";
    public static final String logOutUser = "logOutUser";
	public static final String gotoAchome = "gotoAchome";

    private static final String TAG = "HybridControlPlugin";
    public static Fragment frag123 = null;

    @Override
    public boolean execute(final String action, final JSONArray args,
            final CallbackContext callbackContext) throws JSONException {

        if (action.equals(changeStatusText)) {
            final String newTitle = (String) args.get(0);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.statusBarFragment.setStatusBarText(newTitle);
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(showMenu)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.showBehind();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(hideMenu)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.showAbove();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(showHeaderNavBar)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ActionBar actionBar = cnrAct.getSupportActionBar();
                    actionBar.show();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;

        } else if (action.equals(hideHeaderNavBar)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ActionBar actionBar = cnrAct.getSupportActionBar();
                    actionBar.hide();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;

        } else if (action.equals(showHeaderStatusBar)) {
            Globals.setStatusBarVisibility(true);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.updateStatusBarVisibility();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(hideHeaderStatusBar)) {
            Globals.setStatusBarVisibility(false);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.updateStatusBarVisibility();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(setTitleView)) {
            String title0 = null;
            try {
                title0 = (String) args.get(0);
                Log.d(TAG, "title received is " + title0);
            } catch (final Exception e) {
            }
            final String title = title0;
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();

            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (title != null) {
                        cnrAct.setActionBarTitle(title);
                        // cnrAct.updateActionBarTitle();
                    } else {
                        Log.d(TAG, "calling showactionbarlogo from here");
                        cnrAct.showActionBarLogo();
                    }
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(popPhoneGapToFront)) {
            Log.d(TAG, "inside popPhoneGapToFront action");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) HybridControlPlugin.this.cordova
                    .getActivity();
            final FragmentManager fragmentManager = cnrAct
                    .getSupportFragmentManager();
            final int fragCount = fragmentManager.getBackStackEntryCount();

            String fragTag = fragmentManager.getBackStackEntryAt(fragCount - 1)
                    .getName();

            String title0 = null;
            try {
                title0 = (String) args.get(0);
                Log.d(TAG, "title received in popPhoneGapToFront action is "
                        + title0);
            } catch (Exception e) {
            }

            final String title = title0;

            if (!title.equalsIgnoreCase("No Title")) {

                cnrAct.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (title != null) {
                            cnrAct.hideActionBarLogo();
                            cnrAct.setActionBarTitle(title);
                            cnrAct.updateActionBarTitle();
                        }
                    }

                });
            } else {
                cnrAct.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG,
                                "calling showactionbarlogo from popPhoneGapToFront action");
                        cnrAct.showActionBarLogo();
                    }
                });
            }

            if (!fragTag.equalsIgnoreCase(title0)) {

                Fragment fragmentByTag = fragmentManager
                        .findFragmentByTag("CordovaWebFrag");
                if (fragmentByTag == null) {
                    fragmentByTag = new CordovaWebFrag();
                }
                final Fragment cordovaFrag = fragmentByTag;

                frag123 = cordovaFrag;

                final StatusBarFragment statusBarFragment;
                statusBarFragment = (StatusBarFragment) fragmentManager
                        .findFragmentById(R.id.status_bar);

                // final String backStackString = title0;

                cnrAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                         * fragmentManager.beginTransaction()
                         * .replace(R.id.navigation_content, cordovaFrag)
                         * .commit();
                         */

                        fragmentManager
                                .beginTransaction()
                              /*  .hide(statusBarFragment)*/
                                .remove(cordovaFrag)
                                .add(R.id.navigation_content, cordovaFrag,
                                        "CordovaWebFrag").addToBackStack(title)
                                .commit();
                        fragmentManager.executePendingTransactions();
                        Utils.hideSpinner();
                    }
                });
            } else {
                cnrAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideSpinner();
                    }
                });
            }

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);

            return true;
        } else if (action.equals(pushFragment)) {
            final String fragmentName = args.getString(0);
            String tag0 = null;
            final int lastIndexOf = fragmentName.lastIndexOf(".");
            if (lastIndexOf != -1) {
                tag0 = fragmentName.substring(lastIndexOf + 1);
            }
            final String tag = tag0;
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final FragmentManager fragmentManager = cnrAct
                    .getSupportFragmentManager();
            final Fragment fragmentByTag = fragmentManager
                    .findFragmentByTag(tag);
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragmentByTag != null) {
                        fragmentManager.beginTransaction().show(fragmentByTag)
                                .commit();
                    } else {
                        try {
                            final Fragment fragObject = (Fragment) Class
                                    .forName(fragmentName).newInstance();
                            fragmentManager
                                    .beginTransaction()
                                    .add(R.id.navigation_content, fragObject,
                                            tag).commit();
                        } catch (final InstantiationException e) {
                            e.printStackTrace();
                        } catch (final IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (final ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(popCurrentFragment)) {
            Log.d(TAG, "inside popCurrentFragment ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final FragmentManager fragmentManager = cnrAct
                    .getSupportFragmentManager();
            final int backStackCount = fragmentManager.getBackStackEntryCount();
            if (backStackCount > 1) {
                cnrAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (backStackCount > 1) {
                            cnrAct.onBackPressed();
                        }
                    }
                });
                final PluginResult pluginResult = new PluginResult(
                        PluginResult.Status.OK);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            }
        }

        else if (action.equals(getAccountDetails)) {
            /*
             * Toast.makeText(this.cordova.getActivity().getApplicationContext(),
             * "inside getAccountDetails", Toast.LENGTH_SHORT).show();
             */
            Log.d(TAG, "inside getAccountDetails ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String json = (String) CardShareDataStore
                    .getInstance(cnrAct)
                    .getReadOnlyAppCache()
                    .get(cordova.getActivity().getString(
                            R.string.account_details_for_js));
            Log.d(TAG, "json: " + json);
            /*
             * Toast.makeText(this.cordova.getActivity().getApplicationContext(),
             * "json: " + json, Toast.LENGTH_SHORT).show();
             */
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, json);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        else if (action.equals(dismissProgressBar)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.hideSpinner();
                }
            });
        } else if (action.equals(updatedAccountDetails)) {
            Log.d(TAG, "inside updatedAcdountDetails");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String accountDetails = cnrAct
                    .getString(R.string.account_details);
            final String accountDetailsJson = cnrAct
                    .getString(R.string.account_details_for_js);

            CardShareDataStore.getInstance(cnrAct).deleteCacheObject(
                    accountDetails);
            CardShareDataStore.getInstance(cnrAct).deleteCacheObject(
                    accountDetailsJson);

            final CardEventListener cardEventListener = new CardEventListener() {

                @Override
                public void onSuccess(final Object data) {
                    // TODO Auto-generated method stub
                    CardShareDataStore.getInstance(cnrAct).addToAppCache(
                            accountDetails, data);

                    final PluginResult pluginResult = new PluginResult(
                            PluginResult.Status.OK);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);

                    Log.d(TAG,
                            "inside updatedAccountDetails Success in account service call");
                }

                @Override
                public void OnError(final Object data) {
                    // TODO Auto-generated method stub
                    Log.d(TAG,
                            "inside updatedAccountDetails Error in account service call");

                    final PluginResult pluginResult = new PluginResult(
                            PluginResult.Status.ERROR);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);

                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            cnrAct);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);
                }
            };

            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.updateAccountDetails(cnrAct, cardEventListener ,"Discover", "Updating Account Details...");
                }
            });

            return true;
        }

        else if (action.equals(getSecToken)) {
            Log.d(TAG, "inside getSecToken ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String secToken = CardShareDataStore.getInstance(cnrAct)
                    .getCookieManagerInstance().getSecToken();
            Log.d(TAG, "token: " + secToken);

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, secToken);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(getStrongAuthSvcs)) {

            Log.d(TAG, "inside getStrongAuthSvcs ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String strongAuthSvcs = (String) CardShareDataStore
                    .getInstance(cnrAct).getReadOnlyAppCache()
                    .get(cnrAct.getString(R.string.strong_auth_svcs));
            Log.d(TAG, "strongAuthSvcs: " + strongAuthSvcs);
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, strongAuthSvcs);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(logOutUser)) {
            Log.d(TAG, "inside logOut ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.logout();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }else if (action.equals(gotoAchome)) {
        	Log.d(TAG, "inside gotoAchome ");
        	final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
        			.getActivity();
        	final FragmentManager fragManager = cnrAct.getSupportFragmentManager();
        	Fragment homeFragment = fragManager
        			.findFragmentByTag("HomeSummaryFragment");
        	cnrAct.makeFragmentVisible(homeFragment, false);
        	final PluginResult pluginResult = new PluginResult(
        			PluginResult.Status.OK);
        	pluginResult.setKeepCallback(true);
        	callbackContext.sendPluginResult(pluginResult);
        	return true;
        }

        return false;
    }
}
