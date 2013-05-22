package com.discover.mobile.card.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.PushConstant;
import com.discover.mobile.card.CardMenuItemLocationIndex;
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
	public static final String getSID = "getSID";
	public static final String getDID = "getDID";
	public static final String getOID = "getOID";
	public static final String showSpinner = "showSpinner";
	public static final String getVID = "getVID";	
	public static final String setOtherUserFlag = "setOtherUserFlag";
	public static final String getOtherUserFlag = "getOtherUserFlag";
	public static final String enableSlidingMenu = "enableSlidingMenu";
	
    private static final String TAG = "HybridControlPlugin";
    public static Fragment frag123 = null;
    public static String strLastTitleDisplayed = null;
    
    public static final String isDeviceReady = "deviceReadyUpdate";

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
                Utils.log(TAG, "title received is " + title0);
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
                        Utils.log(TAG, "calling showactionbarlogo from here");
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
            Utils.log(TAG, "inside popPhoneGapToFront action");
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
                Utils.log(TAG, "title received in popPhoneGapToFront action is "
                        + title0);
            } catch (Exception e) {
            }

            final String title = title0;

//            if (!title.equalsIgnoreCase("No Title")) {

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
//            } else {
//                cnrAct.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Utils.log(TAG,
//                                "calling showactionbarlogo from popPhoneGapToFront action");
//                        cnrAct.showActionBarLogo();
//                    }
//                });
//            }

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
                        
                        
                        String topfragName = fragmentManager.getBackStackEntryAt(fragCount - 1)
                                .getName();
                        Fragment topFragment = fragmentManager
                                .findFragmentByTag(topfragName);
                        /********** Hemang **********/
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1)
                        {
                            if (null != topFragment)
                            {
                                String topFragmentTag = topFragment.getTag();
                                if (!topFragmentTag.equalsIgnoreCase("CordovaWebFrag") && null!= strLastTitleDisplayed && !strLastTitleDisplayed.equalsIgnoreCase(title))
                                {
                                        ((CordovaWebFrag)cordovaFrag).getCordovaWebviewInstance().clearView();
                                       
                                        ((CordovaWebFrag)cordovaFrag).getCordovaWebviewInstance().invalidate();
                                        strLastTitleDisplayed=title;
                                }
                                else
                                    strLastTitleDisplayed=title;
                            }
                            else
                                strLastTitleDisplayed=title;
                        }
                        /********** Hemang **********/
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
            Utils.log(TAG, "inside popCurrentFragment ");
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
            Utils.log(TAG, "inside getAccountDetails ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String json = (String) CardShareDataStore
                    .getInstance(cnrAct)
                    .getReadOnlyAppCache()
                    .get(cordova.getActivity().getString(
                            R.string.account_details_for_js));
            Utils.log(TAG, "json: " + json);
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
                    Utils.log(TAG, "dismissProgressBar");
                    Utils.hideSpinner();
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(updatedAccountDetails)) {
            Utils.log(TAG, "inside updatedAcdountDetails");
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

                    Utils.log(TAG,
                            "inside updatedAccountDetails Success in account service call");
                }

                @Override
                public void OnError(final Object data) {
                    // TODO Auto-generated method stub
                    Utils.log(TAG,
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
            Utils.log(TAG, "inside getSecToken ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String secToken = CardShareDataStore.getInstance(cnrAct)
                    .getCookieManagerInstance().getSecToken();
            Utils.log(TAG, "token: " + secToken);

            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, secToken);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(getStrongAuthSvcs)) {

            Utils.log(TAG, "inside getStrongAuthSvcs ");
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            final String strongAuthSvcs = (String) CardShareDataStore
                    .getInstance(cnrAct).getReadOnlyAppCache()
                    .get(cnrAct.getString(R.string.strong_auth_svcs));
            Utils.log(TAG, "strongAuthSvcs: " + strongAuthSvcs);
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK, strongAuthSvcs);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(logOutUser)) {
            Utils.log(TAG, "inside logOut ");
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
        	 Utils.log(TAG, "inside gotoAchome ");
             final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                     .getActivity();
             final FragmentManager fragManager = cnrAct
                     .getSupportFragmentManager();

             cnrAct.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     cnrAct.getCordovaWebFragInstance().setTitle(null);
                 }
             });

             fragManager.popBackStack();
             Fragment homeFragment = fragManager
                     .findFragmentByTag("HomeSummaryFragment");
             cnrAct.makeFragmentVisible(homeFragment, false);
             cnrAct.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     cnrAct.showActionBarLogo();
                     cnrAct.highlightMenuItems(
                             CardMenuItemLocationIndex.HOME_GROUP,
                             CardMenuItemLocationIndex.HOME_SECTION);
                 }
             });
             final PluginResult pluginResult = new PluginResult(
                     PluginResult.Status.OK);
             pluginResult.setKeepCallback(true);
             callbackContext.sendPluginResult(pluginResult);
             return true;
        }else if (action.equals(getDID)) {
        	Utils.log(TAG, "inside DID ");        	
        	final TelephonyManager telephonyManager = (TelephonyManager) cordova.getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String did = telephonyManager.getDeviceId();
            final PluginResult pluginResult = new PluginResult(
        			PluginResult.Status.OK, did);
            Utils.log(TAG, "did:" + did);
        	pluginResult.setKeepCallback(true);
        	callbackContext.sendPluginResult(pluginResult);
        	return true;
        }else if (action.equals(getSID)) {
        	Utils.log(TAG, "inside SID ");        	
        	final TelephonyManager telephonyManager = (TelephonyManager) cordova.getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String sid = telephonyManager.getSimSerialNumber();
            if (null == sid)
                sid = telephonyManager.getDeviceId();
            Utils.log(TAG, "sid:" + sid);
            final PluginResult pluginResult = new PluginResult(
        			PluginResult.Status.OK, sid);
        	pluginResult.setKeepCallback(true);
        	callbackContext.sendPluginResult(pluginResult);
        	return true;
        }else if (action.equals(getOID)) {
        	Utils.log(TAG, "inside OID ");        	
        	final TelephonyManager telephonyManager = (TelephonyManager) cordova.getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String oid = telephonyManager.getDeviceId();
            Utils.log(TAG, "oid:" + oid);
            final PluginResult pluginResult = new PluginResult(
        			PluginResult.Status.OK, oid);
        	pluginResult.setKeepCallback(true);
        	callbackContext.sendPluginResult(pluginResult);
        	return true;
        }        
        else if (action.equals(showSpinner)) {
            final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            Utils.isSpinnerAllowed=true;
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.log(TAG, "inside showSpinner ");
                    Utils.showSpinner(cnrAct, "", "Loading...");
                }
            });
            final PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals(getVID)) {
        	Utils.log(TAG, "inside getVID ");        	

      	  SharedPreferences pushSharedPrefs = cordova.getContext().getSharedPreferences(PushConstant.pref.PUSH_SHARED, //TODO: Push
                    Context.MODE_PRIVATE);
           String vid = pushSharedPrefs.getString(PushConstant.pref.PUSH_XID,"0");        
          final PluginResult pluginResult = new PluginResult(
      			PluginResult.Status.OK, vid);
      	pluginResult.setKeepCallback(true);
      	callbackContext.sendPluginResult(pluginResult);
      	return true;
      }
      else if (action.equals(setOtherUserFlag)) {
      	Utils.log(TAG, "inside setOtherUserFlag ");
      	boolean otherUserFlag =  (Boolean) args.get(0);
      	Utils.log(TAG,"SetoTHERuSER: "+otherUserFlag);
      	SharedPreferences pushSharedPrefs = cordova.getContext().getSharedPreferences(PushConstant.pref.PUSH_SHARED, //TODO: Push
		                Context.MODE_PRIVATE);
      	
      	Editor  editor = pushSharedPrefs.edit();
      	editor.putBoolean(PushConstant.pref.PUSH_OTHER_USER_STATUS, otherUserFlag);
      	editor.commit();
      	final PluginResult pluginResult = new PluginResult(
      			PluginResult.Status.OK);
      	pluginResult.setKeepCallback(true);
      	callbackContext.sendPluginResult(pluginResult);
      	return true;
      } else if (action.equals(getOtherUserFlag)) {
      	Utils.log(TAG, "inside setOtherUserFlag ");
      	//need to get otherUserFlag from native...
      	
      	SharedPreferences pushSharedPrefs = cordova.getContext().getSharedPreferences(PushConstant.pref.PUSH_SHARED, //TODO: Push
	                Context.MODE_PRIVATE);	               
      	boolean isOtherUser = pushSharedPrefs.getBoolean(PushConstant.pref.PUSH_OTHER_USER_STATUS, false);
      	
      	final PluginResult pluginResult = new PluginResult(
      			PluginResult.Status.OK,isOtherUser);
      	pluginResult.setKeepCallback(true);
      	callbackContext.sendPluginResult(pluginResult);
      	return true;
      }else if (action.equals(enableSlidingMenu)) {
        	
        	final boolean enableSliding = (Boolean)args.get(0);
        	Utils.log(TAG, "inside enableSlidingMenu n isSlidingEnabled "+enableSliding);
        	
        	final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                    .getActivity();
            cnrAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	cnrAct.enableSlidingMenu(enableSliding);
                }
            });
          	final PluginResult pluginResult = new PluginResult(
          			PluginResult.Status.OK);
          	pluginResult.setKeepCallback(true);
          	callbackContext.sendPluginResult(pluginResult);
          	return true;
          }
      else if (action.equals(isDeviceReady)) {

    	  Log.i(TAG, "OH YES ABHI");
    	  
          /*final boolean isPhonegapReady = (Boolean) args.get(0);
          Utils.log(TAG, "inside enableSlidingMenu n isSlidingEnabled "
                  + isPhonegapReady);*/

          final CardNavigationRootActivity cnrAct = (CardNavigationRootActivity) cordova
                  .getActivity();
          cnrAct.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  cnrAct.isDeviceReady();
              }
          });
          final PluginResult pluginResult = new PluginResult(
                  PluginResult.Status.OK);
          pluginResult.setKeepCallback(true);
          callbackContext.sendPluginResult(pluginResult);
          return true;
      }
        return false;
    }
}
