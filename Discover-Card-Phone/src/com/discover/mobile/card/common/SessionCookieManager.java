package com.discover.mobile.card.common;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import android.content.Context;

import com.discover.mobile.card.common.utils.Utils;

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
    private String pmData = null;
    public static final String vfirst = "v1st";
    public static final String dfsedskey = "dfsedskey";
    private final Context sessionCookieContext;
    private String secToken = null;
    private String dfsKey = null;
    private String vone = null;
    private String dcsession = null;
    private String STRONGAUTHSVCS = null;

    private CookieStore rawCookieStore;
    private URI baseUri;
    private static SessionCookieManager sessionCookieManager;

    public String getDcsession() {
        return dcsession;
    }

    public void setDcsession(final String dcsession) {
        this.dcsession = dcsession;
    }

    public String getSTRONGAUTHSVCS() {
        return STRONGAUTHSVCS;
    }

    public void setSTRONGAUTHSVCS(final String sTRONGAUTHSVCS) {
        STRONGAUTHSVCS = sTRONGAUTHSVCS;
    }

    public String getPmData() {
        return pmData;
    }

    public void setPmData(final String pmData) {
        this.pmData = pmData;
    }

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
        Utils.log("setSecToken in ", "token:" + secToken);
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
        final List<HttpCookie> cookieList = getHttpCookie();
        if (null != cookieList) {
            for (final HttpCookie cookie : cookieList) {

                if (sectoken.equalsIgnoreCase(cookie.getName())) {
                    setSecToken(cookie.getValue());
                    Utils.log("setCookieValues",
                            "token value" + cookie.getValue());
                } else if (vfirst.equalsIgnoreCase(cookie.getName())) {
                    setVone(cookie.getValue());
                } else if ("dcsession".equalsIgnoreCase(cookie.getName())) {
                    setDcsession(cookie.getValue());
                } else if ("STRONGAUTHSVCS".equalsIgnoreCase(cookie.getName())) {
                    setSTRONGAUTHSVCS(cookie.getValue());
                }

                else if ("PMData".equalsIgnoreCase(cookie.getName())) {
                    setPmData(cookie.getValue());
                }
                else if (dfsedskey.equalsIgnoreCase(cookie.getName())) {
                    setDfsKey(cookie.getValue());
                }
            }
        }
    }

    /**
     * This method is used to fetch the value from http Cookie manager
     */
    public void setSecTokenInCookie(final String strSecToken) {
        final List<HttpCookie> cookieList = getHttpCookie();
        if (null != cookieList) {
            for (final HttpCookie cookie : cookieList) {

                if (sectoken.equalsIgnoreCase(cookie.getName())) {
                    setSecToken(cookie.getValue());
                    Utils.log("setCookieValues",
                            "token value" + cookie.getValue());
                } else if (vfirst.equalsIgnoreCase(cookie.getName())) {
                    setVone(cookie.getValue());
                } else if ("dcsession".equalsIgnoreCase(cookie.getName())) {
                    setDcsession(cookie.getValue());
                } else if ("STRONGAUTHSVCS".equalsIgnoreCase(cookie.getName())) {
                    setSTRONGAUTHSVCS(cookie.getValue());
                }

                else if ("PMData".equalsIgnoreCase(cookie.getName())) {
                    setPmData(cookie.getValue());
                }
                else if (dfsedskey.equalsIgnoreCase(cookie.getName())) {
                    setDfsKey(cookie.getValue());
                }
            }
        }
    }

    /**
     * This method will provide the list of http cookie
     */
    public List<HttpCookie> getHttpCookie() {
        final URI u = getBaseUri();
        Utils.log("getHttpCookie in ", "base url" + u);
        final java.net.CookieStore cookieStore = getCookieStore();
        if (null != u && null != cookieStore) {
            // return cookieStore.get(u);
            return cookieStore.getCookies();
        } else {
            return null;
        }
    }

    /**
     * This method will provide the current cookiestore used by cookiemanager
     */
    public CookieStore getCookieStore() {
        if (null == rawCookieStore) {
            try {
                final CookieManager cm = new CookieManager();
                cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cm);

                rawCookieStore = cm.getCookieStore();
            }

            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return rawCookieStore;
    }

    public URI getBaseUri() {

        if (null == baseUri) {
            final String url = sessionCookieContext
                    .getString(R.string.url_in_use);
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
