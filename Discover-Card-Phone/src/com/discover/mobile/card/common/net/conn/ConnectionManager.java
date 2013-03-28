package com.discover.mobile.card.common.net.conn;

/**
 * This class will establishes the connection and returns the response as inputStream object.
 * Also handles network availability and other exceptions which may occur during the network call.
 * 
 * @author Hemang Kakadia
 * 
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.card.common.net.utility.NetworkUtility;

public final class ConnectionManager {

    /**
     * prevent creation of object
     */
    private ConnectionManager() {
        throw new UnsupportedOperationException(
                "This class is non-instantiable");
    }

    /**
     * Opens a connection to the service and returns an InputStream for reading
     * data from the resource pointed by this URLConnection
     * 
     * @param httpConn
     *            HttpUrlConnection
     * @param context
     *            Connection
     * @param input
     *            input data
     * @return input stream containing the json response.
     */
    public static InputStream connect(final HttpURLConnection httpConn,
            final Context context, final byte[] input) {
        InputStream in = null;

        try {
            if (!NetworkUtility.isConnected(context)) {

            } else {
                if (input != null) {
                    httpConn.setRequestProperty("Content-Length",
                            String.valueOf(input.length));
                    httpConn.connect();
                    final OutputStream os = httpConn.getOutputStream();
                    os.write(input, 0, input.length);
                } else {
                    httpConn.connect();
                }
                in = httpConn.getInputStream();
            }
        } catch (final MalformedURLException e) {
            in = httpConn.getErrorStream();
            Log.e(context.getClass().getName(), e.getMessage(), e);
        } catch (final SocketTimeoutException e) {
            in = httpConn.getErrorStream();
            Log.e(context.getClass().getName(), e.getMessage(), e);
        } catch (final IOException e) {
            in = httpConn.getErrorStream();
            Log.e(context.getClass().getName(), e.getMessage(), e);
        } catch (final Exception e) {
            in = httpConn.getErrorStream();
            Log.e(context.getClass().getName(), e.getMessage(), e);
        } finally {
            // httpConn.disconnect();
        }

        return in;
    }

}
