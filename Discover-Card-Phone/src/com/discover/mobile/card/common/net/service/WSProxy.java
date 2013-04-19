package com.discover.mobile.card.common.net.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.conn.ConnectionManager;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;

/**
 * This class will prepare the connection object from WSRequest which will be
 * passed to ConnectionManager to invoke the service, Returns the WSResponse
 * object which contains output of ConnectionManager which is response code and
 * response team.
 * 
 * @author Anuja Deshpande
 */

public final class WSProxy {

    // private static WSProxy handler = null;
    /**
     * instance of DeviceIdentifiers class
     */
    private static DeviceIdentifiers deviceIdentifiers = null;

    /**
     * App version
     */
    private static String X_APP_VERSION;

    /**
     * Content Type
     */
    private static String X_CONTENT_TYPE;

    /**
     * Device Platform
     */
    private static String X_CLIENT_PLATFORM;

    /**
     * Context
     */
    private Context mcontext;
    /**
     * Http Url Connection
     */
    private HttpURLConnection connection;
    public static long lastRestCallTime;

    /**
     * This method will invoke the webservice using the ConnectionManager class
     * 
     * @param context
     * @param requestDetail
     *            WSRequest instance having request parameters such as service
     *            url,header values
     * @return WSResponse Wsresponse object containing the response inputstream
     *         and response code.
     */
    public WSResponse invoke(final Context context,
            final WSRequest requestDetail) {
        mcontext = context;
        setLastRestCallTime();
        final WSResponse response = new WSResponse();
        X_APP_VERSION = Utils.getStringResource(context,
        		R.string.xApplicationVersion);
        X_CLIENT_PLATFORM = Utils.getStringResource(context,
        		 R.string.xClientPlatform );

        X_CONTENT_TYPE = "application/json";

        if (deviceIdentifiers == null) {
            setupDeviceIdentifiers(context);
        }
        connection = createConnection(requestDetail);
        setCookies(connection, context);
        try {
            final InputStream is = ConnectionManager.connect(connection,
                    context, requestDetail.getInput());
            if (null!=is)
            {
                response.setResponseCode(connection.getResponseCode());
                response.setHeaders(connection.getHeaderFields());
                response.setInputStream(is);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * This method disconnects the httpUrlConnection.
     */
    public void dispose() {
        if (null != connection) {
            connection.disconnect();
        }
    }

    /**
     * This method adds the headers to the UrlConnection
     * 
     * @return HttpURLConnection new HttpURLConnection with added header
     *         information,
     **/
    private HttpURLConnection createConnection(final WSRequest requestDetail) {

        HttpURLConnection connection = null;
        try {
            if (requestDetail.getUrl().startsWith("https")) {
                connection = getHttpsConnection(requestDetail);
            } else {
                connection = getHttpConnection(requestDetail);
            }

            // connection timeout to 10 secs
            connection.setConnectTimeout(requestDetail.getConnectionTimeOut());
            // Setting method type
            if (requestDetail.getMethodtype() != null
                    && requestDetail.getMethodtype().length() > 0) {
                connection.setDoOutput(true);
                connection.setRequestMethod(requestDetail.getMethodtype());
            } else {
                connection.setRequestMethod("GET");
            }
            // Setting Headers
            prepareConnection(connection);
            final HashMap<String, String> headers = requestDetail
                    .getHeaderValues();
            if (headers != null) {
                final Set<String> keys = headers.keySet();
                for (final String key : keys) {
                     System.out.println("Headers========> " + key + " "
                     + headers.get(key));
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final ProtocolException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // Posting data to OutputStream
        return connection;
    }

    /**
     * Veriy the Certificates and allow trusted certificates.
     */
    public static void trustEveryone() {
        try {
            HttpsURLConnection
                    .setDefaultHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(final String hostname,
                                final SSLSession session) {
                            return true;
                        }
                    });
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                        final String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                        final String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context
                    .getSocketFactory());
        } catch (final Exception e) {
            Log.e("RestfulHandler", e.getMessage(), e);
        }
    }

    /**
     * Returns the new HTTPSURL connection to the service referred to by the
     * service url contained in WSRequest.
     * 
     * @param requestDetail
     *            WsRequest POJO object containing Url and header values
     * @return HttpsURLConnection new HTTPSURL connection
     * @throws IOException
     */
    private HttpsURLConnection getHttpsConnection(final WSRequest requestDetail)
            throws IOException {

        HttpsURLConnection conn;
        final URL ur = new URL(requestDetail.getUrl());
        trustEveryone();

        // Getting the proxy information from the device
        final String proxyHost = android.net.Proxy.getDefaultHost();
        final int proxyPort = android.net.Proxy.getDefaultPort();
        if (proxyHost != null) { // With Proxy
            final Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyHost, proxyPort));
            conn = (HttpsURLConnection) ur.openConnection(proxy);
        } else { // Without Proxy
            conn = (HttpsURLConnection) ur.openConnection();
        }
        return conn;
    }

    /**
     * Returns the new HTTPURL connection to the service referred to by the
     * service url contained in WSRequest
     * 
     * @param requestDetail
     *            WsRequest POJO object containing Url and header values
     * @return HttpURLConnection new HTTPURL connection
     * @throws IOException
     */
    private HttpURLConnection getHttpConnection(final WSRequest requestDetail)
            throws IOException {

        HttpURLConnection conn;
        final URL url = new URL(requestDetail.getUrl());

        // Getting the proxy information from the device
        final String proxyHost = android.net.Proxy.getDefaultHost();
        final int proxyPort = android.net.Proxy.getDefaultPort();
        if (proxyHost != null) { // With Proxy
            final Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyHost, proxyPort));
            conn = (HttpURLConnection) url.openConnection(proxy);
        } else { // Without Proxy
            conn = (HttpURLConnection) url.openConnection();
        }
        return conn;
    }

    /**
     * Device Identifiers are set by Android Telephony class.
     * 
     * @param context
     *            Context
     */
    private void setupDeviceIdentifiers(final Context context) {
        deviceIdentifiers = new DeviceIdentifiers() {
            {
                final TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                did = telephonyManager.getDeviceId();
                sid = telephonyManager.getSimSerialNumber();
                oid = telephonyManager.getDeviceId();
            }
        };
    }

    /**
     * Static class to device identifiers
     * 
     * 
     */
    private static class DeviceIdentifiers {
        String did;
        String sid;
        String oid;
    }

    /**
     * This method add headers to the HttpUrlConnection
     * 
     * @param connection
     *            Url Connection
     * @return boolean indicating whether security token is added or not.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private boolean prepareConnection(final HttpURLConnection connection)
            throws IOException, NoSuchAlgorithmException {

        setDefaultHeaders(connection);
        setDeviceIdentifierHeaders(connection);

        return setSessionHeaders(connection);

    }

    /**
     * This method sets the default headers to the Url connection
     * 
     * @param connection
     *            Url Connection.
     */
    private void setDefaultHeaders(final HttpURLConnection connection) {
        connection.setRequestProperty("X-Client-Platform", X_CLIENT_PLATFORM);
        connection.setRequestProperty("X-Application-Version", X_APP_VERSION);
        connection.setRequestProperty("Content-Type", X_CONTENT_TYPE);
    }
    
    private void setCookies(final HttpURLConnection connection,Context context)
    {
    	final CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(context);
        final SessionCookieManager sessionCookieManager = cardShareDataStore
                .getCookieManagerInstance();
        List<HttpCookie> cookies=sessionCookieManager.getHttpCookie();
        StringBuffer cookieStringBuffer = new StringBuffer();
        for (HttpCookie cookie : cookies) {
        	cookieStringBuffer.append(cookie.getName());
        	cookieStringBuffer.append("=");
        	cookieStringBuffer.append(cookie.getValue());
        	cookieStringBuffer.append(";");
        	
        }
        connection.setRequestProperty("Cookie",cookieStringBuffer.toString());
    }

    /**
     * This method sets the security token in header
     * 
     * @param connection
     *            HttpsUrl Connection
     * @return boolean value indicating security token has been set or not.
     * @throws IOException
     */
    private boolean setSessionHeaders(final HttpURLConnection connection)
            throws IOException {
        return NetworkUtility.prepareWithSecurityToken(connection, mcontext);

    }

    /**
     * This method sets the device itendifiers parameters in UrlCOnnection
     * object
     * 
     * @param connection
     *            HttpUrlConnection
     * @throws NoSuchAlgorithmException
     */
    private void setDeviceIdentifierHeaders(final HttpURLConnection connection)
            throws NoSuchAlgorithmException {
        if (deviceIdentifiers == null) {
            return;
        }

        // TODO consider not setting headers if did/oid/sid is null/empty
        connection.setRequestProperty("X-DID",
                Utils.getSha256Hash(deviceIdentifiers.did));
        connection.setRequestProperty("X-SID",
                Utils.getSha256Hash(deviceIdentifiers.sid));
        connection.setRequestProperty("X-OID",
                Utils.getSha256Hash(deviceIdentifiers.oid));
    }

    public void setLastRestCallTime() {
        Log.d("WSProxy", "inside setLastRestCallTime()...");
        final Calendar mCalendar = Calendar.getInstance(TimeZone
                .getTimeZone("UTC"));
        lastRestCallTime = mCalendar.getTimeInMillis();
        Log.d("WSProxy", "lastrestcalltime modified to " + lastRestCallTime);
    }

    public long getLastRestCallTime() {
        return lastRestCallTime;
    }
}
