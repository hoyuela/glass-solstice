package com.discover.mobile.card.hybrid;

import org.apache.cordova.CordovaWebView;

import android.util.Log;

/**
 * 
 * This class will clear JQM cache by calling its clear cache function and clear
 * history function.
 * 
 * @author
 * 
 */
public final class CacheManagerUtil {

    private final CordovaWebView m_cwv;

    public CacheManagerUtil(final CordovaWebView cwv) {
        m_cwv = cwv;
    }

    public void clearJQMGlobalCache() {
        Log.d("CacheManagement", "inside clearJQMGlobalCache()......");
        if(null!=m_cwv)
        m_cwv.sendJavascript("clearGlobalCache();");
    }

    public void clearJQMHistory() {
        Log.d("CacheManagement", "inside clearJQMGlobalCache()......");
        if(null!=m_cwv)
        m_cwv.sendJavascript("clearHistory();");
    }
}
