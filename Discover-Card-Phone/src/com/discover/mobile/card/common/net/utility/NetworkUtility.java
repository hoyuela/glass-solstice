package com.discover.mobile.card.common.net.utility;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Base64;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.DiscoverEnvironment;
import com.discover.mobile.common.net.HttpHeaders;

/**
 * This class will hold the utility functions related to web service connection
 * like Check Network Connection,get the Authorization String etc.
 * 
 * @author yb
 * 
 */
public final class NetworkUtility {

    private NetworkUtility() {
        // Do nothing : Singelton class
    }

    /**
     * Check whether the network is connected or not
     * 
     * @param context
     *            Context
     * @return boolean vlaue indicating the network is on or not
     */
    public static boolean isConnected(final Context context) {
        ConnectivityManager conMgr = null;
        boolean isConnected = false;
        final Object obj = context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (obj != null) {
            conMgr = (ConnectivityManager) obj;
        } else {
            return isConnected;
        }

        if (conMgr != null && conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            isConnected = true;
        }
        return isConnected;
    }

    /**
     * get the Authorization string for the given username and password
     * 
     * @param username
     *            username
     * @param password
     *            password
     * @return Authorization string
     */
    public static String getAuthorizationString(final String username,
            final String password) {
        final String concatenatedCreds = username + ": :" + password;
        return "DCRDBasic "
                + Base64.encodeToString(concatenatedCreds.getBytes(),
                        Base64.NO_WRAP);
    }

    /**
     * get the Webservice url for the given String id
     * 
     * @param context
     *            Context
     * @param id
     *            Sring id
     * @return String webservice url
     */
    public static String getWebServiceUrl(final Context context, final int id) {
		return DiscoverEnvironment.getCardBaseUrl()
                + context.getString(R.string.discover_url)
                + context.getString(id);

    }

    /**
     * set the security token for Url COnnection
     * 
     * @param conn
     *            Connection
     * @param context
     *            context
     * @return boolean value indicating whether token is set or not
     */
    public static boolean prepareWithSecurityToken(
            final HttpURLConnection conn, final Context context) {
        final String token = getSecurityToken(context);
        Utils.log("Utils", "Token is " + token);
        if (isNullOrEmpty(token)) {
            return false;
        }

        setTokenHeader(conn, token);
        return true;
    }

    /**
     * Returns the token previously cached after a successful authentication
     * with the web-service server.
     * 
     * @return Token value in a String Object
     */
    private static String getSecurityToken(final Context context) {
        final CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(context);
        final SessionCookieManager sessionCookieManager = cardShareDataStore
                .getCookieManagerInstance();
        sessionCookieManager.setCookieValues();
        return sessionCookieManager.getSecToken();
    }

    /**
     * Used to include the currently cached token in the HTTP header of an
     * HttpURLConnection. For Card web-service request the token is stored in
     * the X-Sec-Token HTTP Header and for Bank web-service request it is stored
     * in the Authorization HTTP header.
     * 
     * @param conn
     *            Reference to an HttpURLConnection which will be used to send
     *            an HTTP request
     * @param token
     *            Reference to a token value to be stored within the HTTP
     *            Headers.
     */
    private static void setTokenHeader(final HttpURLConnection conn,
            final String token) {

        conn.addRequestProperty(HttpHeaders.XSecToken, token);

    }

    public static String getSecurityToken() {
        String token = null;

        // CookieManager is assumed to bring its own thread safety
        final CookieManager cookieManager = new CookieManager();
        for (final HttpCookie cookie : cookieManager.getCookieStore()
                .getCookies()) {
            if ("sectoken".equalsIgnoreCase(cookie.getName())) {
                token = cookie.getValue();
            }
        }

        return token;
    }
}
