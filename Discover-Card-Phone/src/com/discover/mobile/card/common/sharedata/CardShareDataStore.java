/**
 * 
 */
package com.discover.mobile.card.common.sharedata;

import java.util.Collections;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.common.DiscoverApplication;

/**
 * This is wrapper class which will facilitate to access App cache
 * 
 * @author Ravi Bhojani
 * 
 */
public final class CardShareDataStore {

    /**
     * DiscoverApplication Instance variable
     */
    private final DiscoverApplication appCache;

    /**
     * Cardsharedatastore instance varaible
     */
    private static CardShareDataStore cardShareDataStore;

    /**
     * Context
     */
    private final Context context;

    /**
     * constructor used for setting Application
     * 
     * @param context
     */
    private CardShareDataStore(final Context context) {
        this.context = context;
        appCache = (DiscoverApplication) context.getApplicationContext();
    }

    /**
     * this method returns the object of CardShareDataStore
     * 
     * @param context
     *            Context
     * @return CardShareDataStore object
     */

    public static synchronized CardShareDataStore getInstance(
            final Context context) {

        if (CardShareDataStore.cardShareDataStore == null) {
            CardShareDataStore.cardShareDataStore = new CardShareDataStore(
                    context);
        }

        return CardShareDataStore.cardShareDataStore;
    }

    /**
     * This function will return all shared object which is stored by
     * application
     * 
     * @return Map
     */
    public Map<String, Object> getReadOnlyAppCache() {

        if (appCache.getData() != null) {
            return Collections.unmodifiableMap(appCache.getData());
        } else {
            return null;
        }
    }

    /**
     * This will allow to set shared variable
     * 
     * @param key
     * @param value
     */
    public void addToAppCache(final String key, final Object value) {

        appCache.setData(key, value);
    }

    /**
     * This will give the data mapped with particular key
     * 
     * @param key
     * @return
     */
    public Object getValueOfAppCache(final String key) {
        return appCache.getData().get(key);
    }

    /**
     * This function clear whole cache.
     */
    public void clearCache() {
        appCache.clearCache();
    }

    /**
     * This will delete mapped cache object by sending key
     * 
     * @param key
     */
    public void deleteCacheObject(final String key) {
        appCache.deleteCacheObject(key);
    }

    /**
     * This method get instance of SessionCookieManager Which will be used get
     * session variables.
     * 
     * @return SessionCookieManager
     */
    public SessionCookieManager getCookieManagerInstance() {
        return SessionCookieManager.getInstance(context);
    }
}
