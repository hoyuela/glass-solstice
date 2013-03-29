package com.discover.mobile.card.common;

import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import android.content.Context;

import com.discover.mobile.card.R;

/**
 * Class used to manage the secToken , dfsKey , V1st etc .provided via cookies .
 * After the secToken dfsKey and V1st are extracted from the cookies it is
 * stored in this class which follows a Singleton design pattern. This class can
 * be used to clear the secToken.
 * 
 * 
 * @author Mayank G
 * 
 */

public final class SessionCookieManager {

    public static final String sectoken = "sectoken";
    public static final String vfirst = "v1st";
    public static final String dfsedskey = "dfsedskey";
    private final Context sessionCookieContext;
    private String secToken = null;
    private String dfsKey = null;
    private String vone = null;

    private CookieStore rawCookieStore;
    private URI baseUri;
    private static SessionCookieManager sessionCookieManager;

    /**
     * Follows a singleton design pattern. Therefore this constructor is made
     * private
     */

    private SessionCookieManager(final Context context) {
        sessionCookieContext = context;
    }

    public static synchronized SessionCookieManager getInstance(
            final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }

        if (sessionCookieManager == null) {
            sessionCookieManager = new SessionCookieManager(context);
        }
        return sessionCookieManager;
    }

    public String getVone() {
        return vone;
    }

    public void setVone(final String vone) {
        this.vone = vone;
    }

    public String getSecToken() {
        return secToken;
    }

    public void setSecToken(final String secToken) {
        this.secToken = secToken;
    }

    public String getDfsKey() {
        return dfsKey;
    }

    public void setDfsKey(final String dfsKey) {
        this.dfsKey = dfsKey;
    }

    public void clearSecToken() {
        secToken = null;
    }

    /**
     * This method is used to fetch the value from http Cookie manager
     */
    public void setCookieValues() {
        for (final HttpCookie cookie : getHttpCookie()) {

            if (sectoken.equalsIgnoreCase(cookie.getName())) {
                setSecToken(cookie.getValue());
            } else if (vfirst.equalsIgnoreCase(cookie.getName())) {
                setVone(cookie.getValue());
            } else if (dfsedskey.equalsIgnoreCase(cookie.getName())) {
                setDfsKey(cookie.getValue());
            }
        }
    }

    /**
     * This method will provide the list of http cookie
     */
    public List<HttpCookie> getHttpCookie() {
        final URI u = getBaseUri();
        return getCookieStore().get(u);
    }

    /**
     * This method will provide the current cookiestore used by cookiemanager
     */
    public CookieStore getCookieStore() {
        if (null == rawCookieStore) {
            rawCookieStore = ((java.net.CookieManager) CookieHandler
                    .getDefault()).getCookieStore();
        }

        return rawCookieStore;
    }

    public URI getBaseUri() {

        if (null == baseUri) {
            final String url = sessionCookieContext
                    .getString(R.string.base_url_dev);
            try {
                baseUri = new URI(url);
            } catch (final URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return baseUri;
    }

}
