package com.discover.mobile.card.navigation;

import java.net.HttpCookie;
import java.util.List;
import java.util.Locale;

import org.apache.cordova.CordovaWebView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
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
public class CordovaWebFrag extends BaseFragment implements PhoneGapInterface {
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

    /**
     * Called when fragment gets attached to activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");
    }

    /**
     * called when View is to created,inflation of fragment layout happens here
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Log.v(TAG, "inside onCreateView....");

        if (mView != null) {
            ViewParent oldParent = mView.getParent();
            if (oldParent != container) {
                ((ViewGroup) oldParent).removeView(mView);
            }
            return mView;
        } else {
            mView = inflater.inflate(R.layout.cordova_web_frag, null, false);
            cwv = (CordovaWebView) mView
                    .findViewById(R.id.cordova_web_frag_cordova_webview);

            cwv.loadUrl("file:///android_asset/www/index.html");

            return mView;
        }
    }

    /**
     * Create the fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
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
        javascript = firstLetter + rest + "();";
        Log.v(TAG, "javascript to send: " + javascript);
        cwv.sendJavascript(javascript);

    }

    /**
     * This method is used for passing the cookie from http Cookie manager to
     * WebView Cookie Manager
     */
    private void passCookieToWebview() {
        android.webkit.CookieSyncManager webCookieSync = CookieSyncManager
                .createInstance(getActivity());
        android.webkit.CookieManager webCookieManager = CookieManager
                .getInstance();
        webCookieManager.setAcceptCookie(true);
        CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(getActivity());
        SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                .getCookieManagerInstance();
        List<HttpCookie> cookies = sessionCookieManagerObj.getHttpCookie();
        String url = sessionCookieManagerObj.getBaseUri().toString();
        for (HttpCookie cookie : cookies) {
            String setCookie = new StringBuilder(cookie.toString())
                    .append("; domain=").append(cookie.getDomain())
                    .append("; path=").append(cookie.getPath()).toString();
            webCookieManager.setCookie(url, setCookie);
            webCookieSync.sync();

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
    public void setTitle(String title) {
        Log.d(TAG, "inside setTitle n title is " + title);
        m_title = title;
    }
}
