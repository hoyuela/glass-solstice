package com.discover.mobile.card.navigation;

import java.net.HttpCookie;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.phonegap.plugins.JQMResourceMapper;
import com.discover.mobile.common.BaseFragment;

/*
 Fragment Initialization:
 - fragment re-initializes every time view rotates
 Fragment Life Cycle:
 - First Initialization: onAttach -> onCreateView -> onCreate -> onResume
 - Destruction: onPause -> onDestroy
 - Screen Rotated Initialization: onPause -> onDestroy -> onCreate -> onResume
 onResume - screen shown
 onPause - screen turns off (save state)
 */

/*
 Adding PhoneGap Framework
 - Add cordova-2.3.0.jar inside libs folder
 - Add cordova folder to main project folder
 - Add www folder to assets
 - Add this class in as PhoneGap Fragment
 - Add this xml file cordova_web_frag.xml
 - Add permissions as shown by PhoneGap website
 */

/**
 * This class reprsents Cordova webview class used for PhoneGap integration
 * 
 * @author cts
 * 
 */
public class CordovaWebFrag extends BaseFragment implements PhoneGapInterface,
        CordovaInterface {
    static final String TAG = "CordovaWebFrag";

    /**
     * Cordova Webview
     */
    private CordovaWebView cwv;
    /**
     * View
     */
    private View mView;

    private String m_title = null;

    private JQMResourceMapper jqmResourceMapper;

    private Context mContext;

    private CardMenuItemLocationIndex mCardMenuLocation;

    /**
     * Called when fragment gets attached to activity
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");
    }

    /**
     * called when View is to created,inflation of fragment layout happens here
     */
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {

        Log.v(TAG, "inside onCreateView....");

        if (mView != null) {
            final ViewParent oldParent = mView.getParent();
            if (oldParent != container) {
                ((ViewGroup) oldParent).removeView(mView);
            }
            return mView;
        } else {
            mView = inflater.inflate(R.layout.cordova_web_frag, null, false);
            cwv = (CordovaWebView) mView
                    .findViewById(R.id.cordova_web_frag_cordova_webview);
            cwv.clearCache(true);
            cwv.clearHistory();

            cwv.loadUrl("file:///android_asset/www/index.html", 60000);

            MyWebviewClient myWebViewClient = new MyWebviewClient(this);
            myWebViewClient.setWebView(cwv);
            cwv.setWebViewClient(myWebViewClient);

            this.cwv.setWebChromeClient(new org.apache.cordova.CordovaChromeClient(
                    CordovaWebFrag.this) {

                @Override
                public void onGeolocationPermissionsShowPrompt(
                        final String origin,
                        final GeolocationPermissions.Callback callback) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            mContext);

                    builder.setMessage(
                            "Discover Would Like to Use Your Current Location")

                            .setPositiveButton("Accept",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            callback.invoke(origin, true, false);

                                        }

                                    })

                            .setNegativeButton("Decline",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            // User cancelled the dialog

                                            callback.invoke(origin, false,
                                                    false);

                                            // Toast.makeText(DiscoverMobileActivity.this,
                                            // "Cannot determine current location",
                                            // Toast.LENGTH_LONG).show();

                                        }

                                    });

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }

            });

            return mView;
        }
    }

    /**
     * Create the fragment
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mCardMenuLocation = CardMenuItemLocationIndex.getInstance();

        passCookieToWebview();
    }

    /**
     * When the app resumes, make sure the fragment shows the same
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.v(TAG, "onResume");
        cwv.handleResume(true, false);

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.v(TAG, "onPause");
        cwv.handlePause(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        if (null != cwv)
            cwv.handleDestroy();
    }

    /**
     * get the action bar title
     */
    @Override
    public int getActionBarTitle() {
        Log.v(TAG, "getActionBarTitle n title is " + m_title);
        if (null != m_title) {
            jqmResourceMapper = JQMResourceMapper.getInstance();

            return jqmResourceMapper.getTitleStringId(m_title);
        } else
            return -1;
    }

    // PhoneGap Interface
    @Override
    public void javascriptCall(String javascript) {
        Log.v(TAG, "javascript: " + javascript);
        String firstLetter = javascript.substring(0, 1);
        firstLetter = firstLetter.toLowerCase(Locale.ENGLISH);
        String rest = javascript.substring(1);
        rest = rest.replaceAll(" ", "");
        String restWithoutSpecialChar = firstLetter + rest;
        javascript = restWithoutSpecialChar.replaceAll("[^a-zA-Z]+", "") + "()";
        // javascript = firstLetter + rest + "();";
        Log.v(TAG, "javascript to send: " + javascript);
        cwv.sendJavascript(javascript);

    }

    /**
     * This method is used for passing the cookie from http Cookie manager to
     * WebView Cookie Manager
     */
    /*private void passCookieToWebview() {
        final android.webkit.CookieSyncManager webCookieSync = CookieSyncManager
                .createInstance(getActivity());
        final android.webkit.CookieManager webCookieManager = CookieManager
                .getInstance();
        webCookieManager.setAcceptCookie(true);
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(getActivity());
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        final List<HttpCookie> cookies = sessionCookieManagerObj
                .getHttpCookie();
        final String url = sessionCookieManagerObj.getBaseUri().toString();
        if (null != cookies) {
            for (final HttpCookie cookie : cookies) {
                if ("sectoken".equalsIgnoreCase(cookie.getName())) {
                    String strognAuthCookie = new StringBuilder("sectoken=")
                    .append((String) sessionCookieManagerObj.getSecToken())
                    .append("; domain=.discovercard.com").append("; path=/")
                    .toString();
                    webCookieManager.setCookie(url, strognAuthCookie);
                    webCookieSync.sync();
                    
                    Log.d("cookies1111111", "cookies:"+strognAuthCookie);

                }else{
                final String setCookie = new StringBuilder(cookie.toString())
                        .append("; domain=").append(cookie.getDomain())
                        .append("; path=").append(cookie.getPath()).toString();
                webCookieManager.setCookie(url, setCookie);
                webCookieSync.sync();
                Log.d("cookies22222", "cookies:"+setCookie);
                }

            }
            String strognAuthCookie = new StringBuilder("STRONGAUTHSVCS=")
                    .append((String) CardShareDataStore
                            .getInstance(getActivity())
                            .getReadOnlyAppCache()
                            .get(getActivity().getString(
                                    R.string.strong_auth_svcs)))
                    .append("; domain=discovercard.com").append("; path=/")
                    .toString();
            webCookieManager.setCookie(url, strognAuthCookie);
            webCookieSync.sync();
            
            Log.d("cookies333333", "cookies:"+strognAuthCookie);

        }
    }
*/
 
    private void passCookieToWebview() {
        final android.webkit.CookieSyncManager webCookieSync = CookieSyncManager
                .createInstance(getActivity());
        final android.webkit.CookieManager webCookieManager = CookieManager
                .getInstance();
        webCookieManager.setAcceptCookie(true);
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(getActivity());
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        final List<HttpCookie> cookies = sessionCookieManagerObj
                .getHttpCookie();
        final String url = sessionCookieManagerObj.getBaseUri().toString();
        for (final HttpCookie cookie : cookies) {
            final String setCookie = new StringBuilder(cookie.toString())
                    .append("; domain=").append(cookie.getDomain())
                    .append("; path=").append(cookie.getPath()).toString();
            webCookieManager.setCookie(url, setCookie);
            webCookieSync.sync();

            Log.d("passCookieToWebview", "setCookie11111"+setCookie);
            
        }
    }

    
    /**
     * It returns the instance of CordovaWebView of this Fragment.
     * CordovaWebView
     */
    public CordovaWebView getCordovaWebviewInstance() {
        return cwv;
    }

    /**
     * Receives the title to be set to actionbar.
     * 
     * @param title
     *            to set.
     */
    public void setTitle(final String title) {
        Log.d(TAG, "inside setTitle n title is " + title);
        m_title = title;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public int getGroupMenuLocation() {
        Log.d(TAG, "inside getGroupMenuLocation ");

        return mCardMenuLocation.getMenuGroupLocation(getActionBarTitle());
    }

    @Override
    public int getSectionMenuLocation() {
        Log.d(TAG, "inside getSectionMenuLocation");

        return mCardMenuLocation.getMenuSectionLocation(getActionBarTitle());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.api.CordovaInterface#cancelLoadUrl()
     */
    @Override
    public void cancelLoadUrl() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.api.CordovaInterface#getContext()
     */
    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.api.CordovaInterface#getThreadPool()
     */
    @Override
    public ExecutorService getThreadPool() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.api.CordovaInterface#onMessage(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public Object onMessage(String arg0, Object arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cordova.api.CordovaInterface#setActivityResultCallback(org
     * .apache.cordova.api.CordovaPlugin)
     */
    @Override
    public void setActivityResultCallback(CordovaPlugin arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cordova.api.CordovaInterface#startActivityForResult(org.apache
     * .cordova.api.CordovaPlugin, android.content.Intent, int)
     */
    @Override
    public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    class MyWebviewClient extends CordovaWebViewClient {

        /**
         * Constructor
         * 
         * @param cordova
         */
        public MyWebviewClient(CordovaInterface cordova) {
            super(cordova);
            // TODO Auto-generated constructor stub
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#onPageStarted(android.webkit
         * .WebView, java.lang.String, android.graphics.Bitmap)
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "url loaded on page started.... " + url);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#onPageFinished(android.webkit
         * .WebView, java.lang.String)
         */
        @Override
        public void onPageFinished(WebView arg0, String arg1) {
            // TODO Auto-generated method stub
            super.onPageFinished(arg0, arg1);
            Log.d(TAG, "on pagefinished.... " + arg1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#doUpdateVisitedHistory(android
         * .webkit.WebView, java.lang.String, boolean)
         */
        @Override
        public void doUpdateVisitedHistory(WebView view, String url,
                boolean isReload) {
            // TODO Auto-generated method stub
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#onReceivedError(android.webkit
         * .WebView, int, java.lang.String, java.lang.String)
         */
        @Override
        public void onReceivedError(WebView arg0, int arg1, String arg2,
                String arg3) {
            // TODO Auto-generated method stub
            super.onReceivedError(arg0, arg1, arg2, arg3);
            Log.d(TAG, "on onReceivedError.... " + arg2);
            Utils.hideSpinner();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onFormResubmission(android.webkit.WebView
         * , android.os.Message, android.os.Message)
         */
        @Override
        public void onFormResubmission(WebView view, Message dontResend,
                Message resend) {
            // TODO Auto-generated method stub
            super.onFormResubmission(view, dontResend, resend);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#onReceivedHttpAuthRequest
         * (android.webkit.WebView, android.webkit.HttpAuthHandler,
         * java.lang.String, java.lang.String)
         */
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                HttpAuthHandler handler, String host, String realm) {
            // TODO Auto-generated method stub
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
         * java.lang.String)
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onReceivedLoginRequest(android.webkit
         * .WebView, java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void onReceivedLoginRequest(WebView view, String realm,
                String account, String args) {
            // TODO Auto-generated method stub
            super.onReceivedLoginRequest(view, realm, account, args);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.apache.cordova.CordovaWebViewClient#onReceivedSslError(android
         * .webkit.WebView, android.webkit.SslErrorHandler,
         * android.net.http.SslError)
         */
        @Override
        public void onReceivedSslError(WebView arg0, SslErrorHandler arg1,
                SslError arg2) {
            // TODO Auto-generated method stub
            super.onReceivedSslError(arg0, arg1, arg2);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onScaleChanged(android.webkit.WebView,
         * float, float)
         */
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            // TODO Auto-generated method stub
            super.onScaleChanged(view, oldScale, newScale);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onTooManyRedirects(android.webkit.WebView
         * , android.os.Message, android.os.Message)
         */
        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg,
                Message continueMsg) {
            // TODO Auto-generated method stub
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.webkit.WebViewClient#onUnhandledKeyEvent(android.webkit.WebView
         * , android.view.KeyEvent)
         */
        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            // TODO Auto-generated method stub
            super.onUnhandledKeyEvent(view, event);
        }

    }
}
