package com.discover.mobile.card.common.utils;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.facade.FacadeFactory;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.auth.strong.StrongAuthEnterInfoActivity;
import com.discover.mobile.card.navigation.CardMenuInterface;
import com.discover.mobile.card.phonegap.plugins.ResourceDownloader;
import com.discover.mobile.card.services.auth.AccountDetails;

/**
 * This class is Util class and will contain Utility functions
 * 
 * @author cts
 * 
 */
public class Utils {

    static ProgressDialog progressBar;

    public static boolean enableLogging = false;

    private final static int CARD_NUMBER_LENGTH_OK = 16;
    private final static String CARD_NUMBER_PREFIX = "6011";
    public static boolean isSpinnerAllowed = true;

    // CARD TYPE DEFINED TO SHOW ON TOGGLE VIEW TOOL TIP
    public static final String CARDTYPE_MORE = "MOR";
    public static final String CARDTYPE_OPEN_ROAD = "OPR";
    public static final String CARDTYPE_MOTIVA = "MTV";
    public static final String CARDTYPE_DISCOVER_IT = "DIT";
    public static final String CARDTYPE_MILES = "MLS";
    public static final String CARDTYPE_ESCAPE = "ESC";
    public static final String CARDTYPE_ESSENTIALS = "ESN";
    public static final String CARDTYPE_ESSENTIALS_WITH_FEE = "ESF";
    public static final String CARDTYPE_CORP = "CRP";
    public static final String CARDTYPE_DBC = "DBC";
    public static final String CARDTYPE_DBC_MILES = "DBM";
    public static String vOne = null;
    public static final String CARDTYPE_DEFAULT = "Not Supported";

    /**
     * 
     * Simple utility method to check whether the internet connection is
     * available or not.
     * 
     * @param context
     *            - Application context
     * @return return either true/false based on availability of the network
     */

    private static final String ID_PREFIX = "%&(()!12["; //$NON-NLS-1$

    protected static final String LOG_TAG = "Utils";

    private static final String TYPE_PDF = "application/pdf";
    private static final String TITLE_NO_DL = "No SD Card";
    private static final String MSG_NO_DL = "An SD card is required to Download PDFs.";
    private static final String TITLE_NO_PDF = "No PDF Viewer";
    private static final String MSG_NO_PDF = "A PDF Viewer was not found to view the file.";
    private static final String TITLE_ERROR = "Error";
    private static final String MSG_ERROR = "There was a problem downloading the file, please try again later.";

    /** Potential directories to save PDF file is none of the defaults exist */
    private static final ArrayList<String> DIR_LIST;
    static {
        DIR_LIST = new ArrayList<String>();
        DIR_LIST.add("/mnt/emmc/download");
        DIR_LIST.add("/mnt/emmc/downloads");
        DIR_LIST.add("/mnt/emmc/Download");
        DIR_LIST.add("/mnt/emmc/Downloads");
    }

    /**
     * prevent the object creation
     */
    private Utils() {
        throw new UnsupportedOperationException(
                "This class is non-instantiable");
    }

    public static boolean validateUserforSSO(final String userId) {
        try {
            return userId.startsWith(CARD_NUMBER_PREFIX)
                    && userId.length() == CARD_NUMBER_LENGTH_OK
                    && !userId.contains(" ");
        } catch (final Exception e) {
            return false;
        }

    }

    /**
     * get the String resource associated with particular id
     * 
     * @param currentContext
     * @param id
     *            for which string value is to be returned
     * @return String value
     */
    public static String getStringResource(final Context currentContext,
            final int id) {
        final String value = currentContext.getString(id);
        return value;

    }

    /**
     * Hashing for the device identifiers
     * 
     * @param toHash
     *            String vlaue
     * @return Hashed String
     * @throws NoSuchAlgorithmException
     */
    public static String getSha256Hash(final String toHash)
            throws NoSuchAlgorithmException {
        final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX
                + toHash;

        final MessageDigest digester = MessageDigest.getInstance("SHA-256");
        final byte[] preHash = safeToHash.getBytes(); // TODO consider
        // specifying charset

        // Reset happens automatically after digester.digest() but we don't know
        // its state beforehand so call reset()
        digester.reset();
        final byte[] postHash = digester.digest(preHash);

        return convertToHex(postHash);
    }

    /**
     * This method converts byte data to hex
     * 
     * @param data
     * @return String
     */
    private static String convertToHex(final byte[] data) {
        return String.format("%0" + data.length * 2 + 'x', new BigInteger(1,
                data));
    }

    /**
     * Launches the android native phone dialer with a given telephone number,
     * and awaits user's action to initiate the call.
     * 
     * @param number
     *            - a String representation of a phone number to dial.
     * @param callingContext
     *            - When calling this method, pass it the context/activity that
     *            called this method.
     */
    public final static void dialNumber(final String number,
            final Context callingContext) {
        if (number != null && callingContext != null) {
            final Intent dialNumber = new Intent(Intent.ACTION_DIAL);

            dialNumber.setData(Uri.parse("tel:" + number));

            callingContext.startActivity(dialNumber);
        }
        return;

    }

    /**
     * Set view invisible
     * 
     * @param v
     *            view
     */
    public final static void setViewGone(final View v) {
        if (v != null) {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * Set the view visible
     * 
     * @param v
     *            view
     */
    public final static void setViewVisible(final View v) {
        if (v != null) {
            v.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method show the progress dialog
     * 
     * @param context
     *            Activity Context
     * @param strTitle
     *            Progress Dialog Title
     * @param strMessage
     *            Progress Dialog Message
     */
    public static void showSpinner(final Context context,
            final String strTitle, final String strMessage) {
        if (isSpinnerAllowed) {
            if (null == progressBar) {
                progressBar = new ProgressDialog(context);
                progressBar.setCancelable(false);
                if (null != strMessage && strMessage != "") {
                    progressBar.setMessage(strMessage);
                } else {
                    progressBar.setMessage("Loading...");
                }
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                if (null != strTitle && strTitle != "") {
                    progressBar.setTitle(strTitle);
                } else {
                    progressBar.setTitle("Discover");
                }
            }
            try {
                if (progressBar != null && !progressBar.isShowing()) {
                    progressBar.show();
                } else if (null != strMessage && strMessage != "")
                    progressBar.setMessage(strMessage);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method will make a service call to retrieve account Details .
     * Whenever Account Details data changes this method can be utilized to get
     * the updated one .
     */
    public static void updateAccountDetails(final Context context,
            final CardEventListener cardEventListener, final String strTitle,
            final String strMessage) {
        // TODO Auto-generated method stub
        final WSRequest request = new WSRequest();

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.login_url);
        request.setUrl(url);
        request.setHeaderValues(headers);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new AccountDetails(), strTitle, strMessage, cardEventListener);
        serviceCall.execute(request);
    }

    public static void hideSpinner() {
        if (isSpinnerAllowed) {
            if (null != progressBar && progressBar.isShowing()) {
                try {
                    progressBar.dismiss();
                } catch (Exception e) {
                }
                progressBar = null;
            } else {
                isSpinnerAllowed = false;
            }
        }
    }

    /**
     * This method is used to create the Dialog for provide Feedback and will
     * load the respective URL
     * 
     */
    public static void createProvideFeedbackDialog(final Context context,
            final String referer) {
        // TODO Auto-generated method stub

        final CardShareDataStore cardShareDataStore = CardShareDataStore
                .getInstance(context);
        final SessionCookieManager sessionCookieManager = cardShareDataStore
                .getCookieManagerInstance();
        vOne = sessionCookieManager.getVone();
        if (vOne == null) {
            vOne = "v1st";
        }
        String customerInformation;

        // Added to create the customer information for provide feedback url
        if (null != vOne && null != sessionCookieManager.getDfsKey()) {
            customerInformation = "&custom_var="
                    + vOne
                    + "|"
                    + sessionCookieManager.getDfsKey()
                    + "|DiscoverMobileVersion="
                    + Utils.getStringResource(context,
                            R.string.xApplicationVersion);

        } else {
            customerInformation = "&custom_var=DiscoverMobileVersion="
                    + Utils.getStringResource(context,
                            R.string.xApplicationVersion);
        }
        final String provideFeedbackUrl = context.getString(R.string.share_url)
                + referer + customerInformation;

        Utils.log("inside createProvideFeedback", provideFeedbackUrl);
        final LinearLayout main = new LinearLayout(context);
        main.setOrientation(LinearLayout.VERTICAL);

        final RelativeLayout toolbar = new RelativeLayout(context);
        toolbar.setBackgroundResource(R.drawable.action_bar_background);
        toolbar.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        final RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        final RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        logoParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        final LinearLayout.LayoutParams wvParams = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

        final WebView webview = new WebView(context);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        final WebViewClient client = new WebViewClient();
        webview.setWebViewClient(client);
        webview.loadUrl(provideFeedbackUrl);
        webview.setId(3);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale(90);
        webview.setLayoutParams(wvParams);
        webview.requestFocus();
        webview.requestFocusFromTouch();

        final ImageButton close = new ImageButton(context);
        close.setBackgroundResource(R.drawable.action_bar_background);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (dialog != null) {
                    webview.stopLoading();
                    dialog.dismiss();
                }
            }
        });
        close.setId(1);
        close.setImageResource(R.drawable.btn_close);
        close.setLayoutParams(closeParams);

        /*
         * final ImageView logoView = new ImageView(context); logoView.setId(2);
         * logoView.setLayoutParams(logoParams);
         * logoView.setImageResource(R.drawable.discover_blk_logo_login);
         */

        final TextView headerText = new TextView(context);
        headerText.setId(2);
        headerText.setLayoutParams(logoParams);
        headerText.setTextAppearance(context, R.style.action_bar_text);
        headerText.setText(context.getString(R.string.provide_feedback_title));

        toolbar.addView(close);
        toolbar.addView(headerText);

        main.addView(toolbar);
        main.addView(webview);

        dialog.setContentView(main);
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        webview.setWebViewClient(new WebViewClient() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView
             * , int, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedError(final WebView view,
                    final int errorCode, final String description,
                    final String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                Utils.hideSpinner();
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView
             * , java.lang.String, android.graphics.Bitmap)
             */
            @Override
            public void onPageStarted(final WebView view, final String url,
                    final Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                Utils.isSpinnerAllowed = true;
                Utils.showSpinner(context, null, null);
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView
             * , java.lang.String)
             */
            @Override
            public void onPageFinished(final WebView view, final String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                Utils.hideSpinner();
            }

        });
    }

    /**
     * This method returns a CARD TYPE
     * 
     * @param context
     * @param cardGroupCode
     * @return card type
     */
    public static String getCardTypeFromGroupCode(final Context context,
            final String cardGroupCode) {

        final String cardtype = cardGroupCode;

        if (cardtype.equals(CARDTYPE_MORE)) {

            return context.getString(R.string.card_type_more);
        } else if (cardtype.equals(CARDTYPE_DISCOVER_IT)) {

            return context.getString(R.string.card_type_discover_it);
        } else if (cardtype.equals(CARDTYPE_MOTIVA)) {

            return context.getString(R.string.card_type_motiva);
        } else if (cardtype.equals(CARDTYPE_OPEN_ROAD)) {

            return context.getString(R.string.card_type_open_road);
        } else if (cardtype.equals(CARDTYPE_CORP)) {

            return context.getString(R.string.card_type_corp);
        } else if (cardtype.equals(CARDTYPE_DBC)) {

            return context.getString(R.string.card_type_dbc);
        } else if (cardtype.equals(CARDTYPE_MILES)) {

            return context.getString(R.string.card_type_miles);
        } else if (cardtype.equals(CARDTYPE_DBC_MILES)) {

            return context.getString(R.string.card_type_dbc);
        } else if (cardtype.equals(CARDTYPE_ESCAPE)) {

            return context.getString(R.string.card_type_escape);
        } else if (cardtype.equals(CARDTYPE_ESSENTIALS)) {

            return context.getString(R.string.card_type_essential);
        } else if (cardtype.equals(CARDTYPE_ESSENTIALS_WITH_FEE)) {

            return context.getString(R.string.card_type_essential_with_fee);
        }
        return CARDTYPE_DEFAULT;
    }

    public static void log(final String strMessage) {
        if (enableLogging) {
            log("Discover", strMessage);
        }
    }

    public static void log(final String strTag, final String strMessage) {
        if (enableLogging) {
            Log.d(strTag, strMessage);
        }
    }

    public static void log(final String strTag, final String strMessage,
            final Throwable e) {
        if (enableLogging) {
            Log.d(strTag, strMessage, e);
        }
    }

    public static PDFObject downloadPDF(final String url) {
        String directory = null;
        final File downloadDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir != null && downloadDir.exists()
                && downloadDir.isDirectory() && downloadDir.canWrite()) {
            directory = downloadDir.toString();
            Utils.log(LOG_TAG, "onPageStarted() using downloadDir=" + directory);
        } else if (Environment.getExternalStorageDirectory().canWrite()) {
            directory = Environment.getExternalStorageDirectory().toString();
            Utils.log(LOG_TAG,
                    "onPageStarted() using externalStorageDirectory="
                            + directory);
        } else {
            for (final String dir : DIR_LIST) {
                final File intStorage = new File(dir);
                if (intStorage.exists() && intStorage.isDirectory()
                        && intStorage.canWrite()) {
                    directory = dir;
                    Utils.log(LOG_TAG, "onPageStarted() using dir=" + dir);
                    break;
                }
            }
            if (directory == null) {
                // Can't find a directory to save the file.
                Utils.log(LOG_TAG,
                        "onPageStarted() can't find a directory to download");
                return new PDFObject(null, false, TITLE_NO_DL, MSG_NO_DL);
            }
        }
        HashMap<String, String> headers = null;
        final String domain = url.substring(0, url.indexOf(".com") + 4);
        CookieSyncManager.getInstance().sync();
        // Get the cookie from cookie jar.
        final String cookie = CookieManager.getInstance().getCookie(domain);
        if (cookie != null) {
            headers = new HashMap<String, String>();
            headers.put("Cookie", cookie);
            Utils.log(LOG_TAG, "onPageStarted() Cookie=" + cookie);
        } else {
            Utils.log(LOG_TAG, "onPageStarted() No Cookies");
        }

        Utils.log(LOG_TAG, "onPageStarted. before executeDownload, directory="
                + directory);

        boolean success = false;
        File file = null;
        try {
            file = ResourceDownloader.getInstance().executeDownload(url,
                    ResourceDownloader.GET, null, headers, directory, ".pdf",
                    TYPE_PDF);
            success = true;
        } catch (final Exception e1) {
            Utils.log(
                    LOG_TAG,
                    "onPageStarted() problem downloading file. message:"
                            + e1.getMessage());
            return new PDFObject(null, false, TITLE_ERROR, MSG_ERROR);
        }
        if (success && file != null && file.exists()) {
            final Uri path = Uri.fromFile(file);
            final Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, TYPE_PDF);
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {

                return new PDFObject(file, true);
            } catch (final Exception e) {
                Utils.log(LOG_TAG,
                        "onPageStarted() Problem with launching PDF Viewer.", e);
                return new PDFObject(null, false, TITLE_NO_PDF, MSG_NO_PDF);
            }
        } else {
            Utils.log(LOG_TAG,
                    "onPageStarted() Problem downloading/saving file.");
            return new PDFObject(null, false, TITLE_ERROR, MSG_ERROR);
        }
    }

    /**
     * 
     * @param context
     * @param title
     * @param message
     */
    public static void showOkAlert(final Context context, final String title,
            final String message) {
        // We can't download the file anywhere.
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });
        alertDialog.setIcon(android.R.drawable.stat_notify_error);
        alertDialog.show();
    }

    /**
     * Tests to see if there is a network connection (wifi or mobile network)
     * 
     * @param cm
     *            ConnectivityManager object from activity.
     * @return true if network connection exists, false if not.
     */
    public static boolean isNetworkConnection(final ConnectivityManager cm) {
        boolean isConnectedMobile = false;
        boolean isConnectedWifi = false;
        if (cm == null) {
            return true;
        }
        final NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (final NetworkInfo ni : netInfo) {
            if ("WIFI".equalsIgnoreCase(ni.getTypeName())) {
                if (ni.isConnected()) {
                    isConnectedWifi = true;
                }
            }
            if ("mobile".equalsIgnoreCase(ni.getTypeName())) {
                if (ni.isConnected()) {
                    isConnectedMobile = true;
                }
            }
        }

        Utils.log(LOG_TAG, "Wifi On: " + isConnectedWifi);
        Utils.log(LOG_TAG, "Mobile On: " + isConnectedMobile);
        return isConnectedWifi || isConnectedMobile;
    }

    /*
     * Changes for 13.4 start
     */
    public static void logoutUser(final Activity cur_Activity,
            final Boolean isTimeOut) {
        isSpinnerAllowed = true;
        CardEventListener logoutCardEventListener = new CardEventListener() {

            @Override
            public void onSuccess(Object data) {
                final Bundle bundle = new Bundle();
                PageTimeOutUtil.getInstance(cur_Activity).destroyTimer();
                if (isTimeOut) {
                    bundle.putBoolean(
                            IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
                    bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
                } else {
                    bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
                    bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
                }
                FacadeFactory.getLoginFacade().navToLoginWithMessage(
                        cur_Activity, bundle);
                Utils.hideSpinner();
                // cur_Activity.finish();
            }

            @Override
            public void OnError(Object data) {
                final Bundle bundle = new Bundle();
                PageTimeOutUtil.getInstance(cur_Activity).destroyTimer();
                if (isTimeOut) {
                    bundle.putBoolean(
                            IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
                    bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, true);
                } else {
                    bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
                    bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, true);
                }

                FacadeFactory.getLoginFacade().navToLoginWithMessage(
                        cur_Activity, bundle);
                Utils.hideSpinner();
                // cur_Activity.finish();
            }
        };
        final WSRequest request = new WSRequest();
        final String url = NetworkUtility.getWebServiceUrl(cur_Activity,
                R.string.logOut_url);
        request.setUrl(url);
        request.setMethodtype("POST");
        Utils.showSpinner(cur_Activity, "Discover", "Signing Out...");
        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(cur_Activity,
                null, "Discover", null, logoutCardEventListener);
        serviceCall.execute(request);
    }
    
    private static final String REFERER = "cardHome-pg";	
	/***
	 * Initialize and set action to Footer Items privacy menu & Term condition
	 * 
	 * @param mainView
	 */
	public static void setFooter(View mainView, final Activity activity) {
		final TextView provideFeedback = (TextView) mainView
				.findViewById(R.id.provide_feedback_button);
		provideFeedback.setTextColor(activity.getResources().getColor(
				R.color.footer_link));
		provideFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.createProvideFeedbackDialog(activity, REFERER);
			}
		});
		final TextView termsOfUse = (TextView) mainView
				.findViewById(R.id.privacy_terms);
		termsOfUse.setTextColor(activity.getResources().getColor(
				R.color.footer_link));
		termsOfUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((CardMenuInterface) activity)
				.sendNavigationTextToPhoneGapInterface(activity.getString(R.string.privacy_terms_title));
			}
		});
	}
    /*
     * Changes for 13.4 end
     */
}
