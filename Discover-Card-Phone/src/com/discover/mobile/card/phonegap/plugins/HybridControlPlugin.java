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
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.navigation.CordovaWebFrag;
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

    private static final String TAG = "HybridControlPlugin";

    @Override
    public boolean execute(String action, JSONArray args,
            final CallbackContext callbackContext) throws JSONException {

        if (action.equals(changeStatusText)) {
            final String newTitle = (String) args.get(0);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.statusBarFragment.setStatusBarText(newTitle);
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(showMenu)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.showBehind();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(hideMenu)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.showAbove();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(showHeaderNavBar)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ActionBar actionBar = cnrAct.getSupportActionBar();
                    actionBar.show();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;

        } else if (action.equals(hideHeaderNavBar)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ActionBar actionBar = cnrAct.getSupportActionBar();
                    actionBar.hide();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;

        } else if (action.equals(showHeaderStatusBar)) {
            Globals.setStatusBarVisibility(true);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.updateStatusBarVisibility();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(hideHeaderStatusBar)) {
            Globals.setStatusBarVisibility(false);
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnrAct.updateStatusBarVisibility();
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(setTitleView)) {
            String title0 = null;
            try {
                title0 = (String) args.get(0);
                Log.d(TAG, "title received is " + title0);
            } catch (Exception e) {
            }
            final String title = title0;
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
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
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(popPhoneGapToFront)) {
            Log.d(TAG, "inside popPhoneGapToFront action");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) HybridControlPlugin.this.cordova
                    .getActivity();
            final FragmentManager fragmentManager = cnrAct
                    .getSupportFragmentManager();
            Fragment fragmentByTag = fragmentManager
                    .findFragmentByTag("CordovaWebFrag");
            if (fragmentByTag == null) {
                fragmentByTag = new CordovaWebFrag();
            }
            final Fragment cordovaFrag = fragmentByTag;
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.beginTransaction()
                            .replace(R.id.navigation_content, cordovaFrag)
                            .commit();
                }
            });
            String title0 = null;
            try {
                title0 = (String) args.get(0);
                Log.d(TAG, "title received in popPhoneGapToFront action is "
                        + title0);
            } catch (Exception e) {
            }
            final String title = title0;

            cnrAct.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (title != null) {
                    	cnrAct.hideActionBarLogo();
                        cnrAct.setActionBarTitle(title);
                        cnrAct.updateActionBarTitle();
                    } else {
                        Log.d(TAG,
                                "calling showactionbarlogo from popPhoneGapToFront action");
                        cnrAct.showActionBarLogo();
                    }
                }
                
                
            });

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            Utils.hideSpinner();
            
            return true;
        } else if (action.equals(pushFragment)) {
            final String fragmentName = args.getString(0);
            String tag0 = null;
            int lastIndexOf = fragmentName.lastIndexOf(".");
            if (lastIndexOf != -1) {
                tag0 = fragmentName.substring(lastIndexOf + 1);
            }
            final String tag = tag0;
            CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
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
                            Fragment fragObject = (Fragment) Class.forName(
                                    fragmentName).newInstance();
                            fragmentManager
                                    .beginTransaction()
                                    .add(R.id.navigation_content, fragObject,
                                            tag).commit();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(popCurrentFragment)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
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
                PluginResult pluginResult = new PluginResult(
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
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) this.cordova
                    .getActivity();
            String json = (String) CardShareDataStore
                    .getInstance(cnrAct)
                    .getReadOnlyAppCache()
                    .get(this.cordova.getActivity().getString(
                            R.string.account_details));
            Log.d(TAG, "json: " + json);
            /*
             * Toast.makeText(this.cordova.getActivity().getApplicationContext(),
             * "json: " + json, Toast.LENGTH_SHORT).show();
             */
            PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, json);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        
        else if(action.equals(dismissProgressBar))
        {
        	Utils.hideSpinner();
        	        	
        }
        return false;
    }
}
