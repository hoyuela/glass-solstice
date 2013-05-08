package com.discover.mobile.card.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.services.auth.AccountDetails;

/**
 * This class is Util class and will contain Utility functions
 * 
 * @author cts
 * 
 */
public class Utils {

    static ProgressDialog progressBar;
    private final static int CARD_NUMBER_LENGTH_OK = 16;
    private final static String CARD_NUMBER_PREFIX = "6011";
    public static boolean  isSpinnerAllowed=true;
    public static boolean  isSpinnerForOfflinePush=false;
    
    //CARD TYPE DEFINED TO SHOW ON TOGGLE VIEW TOOL TIP
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
        } catch (Exception e) {
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
     * This method returns the Progress Dialog
     * 
     * @param context
     *            Activity Context
     * @param strTitle
     *            Progress Dialog Title
     * @param strMessage
     *            Progress Dialog Message
     * @return Progress Dialog
     */
    /*
     * public static ProgressDialog getProgressDialog(final Context context,
     * final String strTitle, final String strMessage) { ProgressDialog
     * progressDialog; progressDialog = ProgressDialog.show(context, strTitle,
     * strMessage); return progressDialog; }
     */

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
if(isSpinnerAllowed || isSpinnerForOfflinePush)
{
        if (null == progressBar) {
            progressBar = new ProgressDialog(context);
            progressBar.setCancelable(false);
            if (null != strMessage && strMessage != "")
                progressBar.setMessage(strMessage);
            else
                progressBar.setMessage("Loading...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (null != strTitle && strTitle != "")
                progressBar.setTitle(strTitle);
            else
                progressBar.setTitle("Discover");
        }
        try {
        	if(progressBar != null && !progressBar.isShowing())
        	{
        		progressBar.show();
        	}
        } catch (Exception e) {
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
            final CardEventListener cardEventListener , final String strTitle , final String strMessage) {
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
                new AccountDetails(), strTitle ,
                strMessage , cardEventListener);
        serviceCall.execute(request);
    }

    public static void hideSpinner() {
    	if(!isSpinnerForOfflinePush)
    	{
	        if (null != progressBar && progressBar.isShowing()) {
	            progressBar.dismiss();
	            progressBar = null;
	        }
	        else
	        {
	        	isSpinnerAllowed=false;
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
        String customerInformation;

        // Added to create the customer information for provide feedback url
        if (null != sessionCookieManager.getVone()
                && null != sessionCookieManager.getDfsKey()) {
            customerInformation = "&custom_var="
                    + sessionCookieManager.getVone()
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
        String provideFeedbackUrl = context.getString(R.string.share_url)
                + referer + customerInformation;
        Log.d("inside createProvideFeedback", provideFeedbackUrl);
        LinearLayout main = new LinearLayout(context);
        main.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout toolbar = new RelativeLayout(context);
        //toolbar.setBackgroundColor();
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

        ImageButton close = new ImageButton(context);
        //close.setBackgroundColor(Color.BLACK);
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

        ImageView logoView = new ImageView(context);
        logoView.setId(2);
        logoView.setLayoutParams(logoParams);
        logoView.setImageResource(R.drawable.discover_blk_logo_login);

        toolbar.addView(close);
        toolbar.addView(logoView);

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
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
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
                Utils.isSpinnerAllowed=true;
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
	 * @param context
	 * @param cardGroupCode
	 * @return card type
	 */
	public static String getCardTypeFromGroupCode(Context context,
			String cardGroupCode) {

		String cardtype = cardGroupCode;

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

}
