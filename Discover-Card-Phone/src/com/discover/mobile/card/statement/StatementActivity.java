/*
 * 
 */

package com.discover.mobile.card.statement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.cordova.DroidGap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.utils.PDFObject;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

/**
 * Displays cardmembers selected statement images in a scrollable view
 * 
 * @author sgoff0
 */
public class StatementActivity extends DroidGap {

    private static final String LOG_TAG = "StatementActivity";
    public static final int EXPIRE_SESSION = 1;
    public static final int MAINT_EXPIRE_SESSION = 2;
    public static final int STATEMENT_LOGOUT = 3;

    private static long sLastHealthCheck = new Date().getTime();
    private static long sHealthCheckThreshold = 30 * 1000;
    private static long sExpireSession = 15 * 60 * 1000; // 15 min
    private static long sWaitImageLoad = 7 * 1000;

    final Handler mHandler = new Handler();
    private Context mContext;

    private static String sBaseUrl = null;
    private static JSONArray sJsonArray = null;
    private StatementInfo mStatementInfo = new StatementInfo();
    private int previousIndex = 0;
    private boolean isPageLoading = false;

    private CountDownTimer mCountDownTimer;

    // page elements
    private TextView mTextCycleDate;     
    private WebView mWebView;
    private ImageButton mBtnPrev;
    private ImageButton mBtnNext;
    private Button mBtnDownloadPDF;
    

    private CharSequence mErrorMessage;

    private ProgressDialog mProgressDialog;
    private Animation mFadeInAnimation;

    @Override
    public void onUserInteraction() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
        performServiceHealthCheck();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        TrackingHelper.trackPageView(AnalyticsPage.STATEMENT_IMAGES);

        try {
            processBundle();
        } catch (final JSONException e) {
            Utils.log(LOG_TAG, "Error reading input");
            // TODO show error that input couldn't be read properly
            finish();
        }

        mTextCycleDate = (TextView) findViewById(R.id.statement_text_cycleDates);
        mWebView = (WebView) findViewById(R.id.statement_webView);
        mBtnDownloadPDF = (Button) findViewById(R.id.statement_btn_downloadPDF);
        mBtnPrev = (ImageButton) findViewById(R.id.statement_btn_prev);
        mBtnNext = (ImageButton) findViewById(R.id.statement_btn_next);        
        mFadeInAnimation = AnimationUtils.loadAnimation(StatementActivity.this,
                R.anim.fadein);

        hideUI();

        mContext = this;

        mCountDownTimer = new CountDownTimer(sExpireSession, sExpireSession) {
            @Override
            public void onFinish() {
                Utils.log("Stmt", "timer expiring...");
                setResult(EXPIRE_SESSION);
                finish();
            }

            @Override
            public void onTick(final long arg0) {
                // Do nothing on tick, we only care when it expires
            }
        }.start();

        // fetch & display data
        mStatementInfo = getStatementDataAtIndex(mStatementInfo.getIndex());
        loadWebView(mStatementInfo);
        configureWebView();
    }

    private final class HealthCheckListener implements CardEventListener {
    	private final String TAG = "HealthCheckListener";
    	
		@Override
		public void OnError(final Object data) {
			Log.v(TAG, "Error: ");
			final CardErrorBean cardErrorBean = (CardErrorBean) data;
			Log.v(TAG, "CardErrorBean: " + cardErrorBean.toString());
			Log.v(TAG, "CardErrorBean Error code: " + cardErrorBean.getErrorCode());
			//TODO get status code out of data
			if (cardErrorBean.getErrorCode() == null) {
				// cannot connect to URL, probably no internet
				alertCloseActivity(getText(R.string.common_noInternetConnection_message));
			} else if (cardErrorBean.getErrorCode().startsWith("401")) {
				alertCloseActivity(getText(R.string.common_sessionExpired_message));
			} else if (cardErrorBean.getErrorCode().startsWith("503")) {
				setResult(MAINT_EXPIRE_SESSION);
				finish();
			} else {
				// another error
				finish();
			}
		}

		@Override
		public void onSuccess(final Object data) {
			//do nothing
			Log.v(TAG, "Success");
		}
	};

    /**
     * Configures settings for specified webview
     */
    private void configureWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        // loads the WebView completely zoomed out
        // webView.getSettings().setLoadWithOverviewMode(true);
        // makes the Webview have a normal viewport (such as a normal desktop
        // browser), while when false the webview will have a viewport
        // constrained to it's own dimensions (so if the webview is 50px*50px
        // the viewport will be the same size)
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setLoadsImagesAutomatically(true);

        final JavaScriptInterface myJavaScriptInterface = new JavaScriptInterface(
                this);
        mWebView.addJavascriptInterface(myJavaScriptInterface,
                "AndroidFunction");

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(final ConsoleMessage cm) {
                Utils.log("StatementsWebView",
                        cm.message() + " -- From line " + cm.lineNumber()
                                + " of " + cm.sourceId());
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            private CountDownTimer loadingTimer;

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                isPageLoading = true;
                Utils.log(LOG_TAG, "onPageStarted: " + url);
                mWebView.setVisibility(View.INVISIBLE);
                mBtnDownloadPDF.setVisibility(View.INVISIBLE);
                mProgressDialog = ProgressDialog.show(StatementActivity.this,
                        "", "Loading...", true);

                // start cdt
                loadingTimer = new CountDownTimer(sWaitImageLoad,
                        sWaitImageLoad) {
                    @Override
                    public void onFinish() {
                        showUI();
                        isPageLoading = false;
                        Utils.log(LOG_TAG, "ImageLoadTimer: expired");
                    }

                    @Override
                    public void onTick(final long arg0) {
                        // Do nothing on tick, we only care when it expires
                    }
                }.start();
            }

            @Override
            public void onLoadResource(final WebView view, final String url) {
                Utils.log(LOG_TAG, "onLoadResource: " + url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(mWebView, url);
                Utils.log(LOG_TAG, "onPageFinished: " + url);
                if (null != loadingTimer)
                    loadingTimer.cancel();
                showUI();
                mWebView.loadUrl("javascript:asyncEagerImageFetch()");
                isPageLoading = false;
            }
        });
    }

    private void showUI() {
        if (mStatementInfo == null || mStatementInfo.isError()) {
            mProgressDialog.dismiss();
            return;
        }

        mTextCycleDate.setText(mStatementInfo.getCycleDateText());
        mTextCycleDate.setTextSize(mStatementInfo.getFontSize());

        mBtnNext.setVisibility(View.VISIBLE);
        mBtnPrev.setVisibility(View.VISIBLE);
        mBtnNext.setEnabled(mStatementInfo.getIndex() != 0);
        mBtnPrev.setEnabled(mStatementInfo.getIndex() != sJsonArray.length() - 1);
        mWebView.setVisibility(View.VISIBLE);
        mBtnDownloadPDF.setVisibility(View.VISIBLE);
        mTextCycleDate.setVisibility(View.VISIBLE);

        mWebView.startAnimation(mFadeInAnimation);
        mBtnDownloadPDF.startAnimation(mFadeInAnimation);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void hideUI() {
        mBtnNext.setVisibility(View.INVISIBLE);
        mBtnPrev.setVisibility(View.INVISIBLE);
        mWebView.setVisibility(View.INVISIBLE);
        mTextCycleDate.setVisibility(View.INVISIBLE);
        mBtnDownloadPDF.setVisibility(View.INVISIBLE);
    }

    private void processBundle() throws JSONException {
        // extract input parameters
        final Bundle b = getIntent().getExtras();
        final String statements = b.getString("statements");
        sJsonArray = new JSONArray(statements);
        if (sJsonArray.length() == 0) {
            // TODO no content, define proper error
            alertCloseActivity(getText(R.string.statement_invalidData_message));
        }
        sBaseUrl = b.getString("baseUrl");
        mStatementInfo = new StatementInfo();
        mStatementInfo.setIndex(b.getInt("index"));
    }

    private StatementInfo getStatementDataAtIndex(int index) {
        if (!isValidIndex(index)) {
            Utils.log(LOG_TAG, "Invalid index: " + index);
            if (index < 0 && isValidIndex(0)) {
                index = 0;
            } else if (isValidIndex(index - 1)) {
                index = index - 1;
            } else {
                return null;
            }
        }

        final StatementInfo statementInfo = new StatementInfo();
        statementInfo.setIndex(index);
        previousIndex = index;

        final StatementDate thisMonth = new StatementDate();
        final StatementDate nextMonth = new StatementDate();
        final StatementDate previousMonth = new StatementDate();

        try {
            final JSONObject jsonObject = sJsonArray.getJSONObject(index);

            final String endDate = jsonObject.get("endDate").toString();
            final String startDate = jsonObject.get("startDate").toString();
            final String cycleDateHeader = getDateString(startDate, endDate);
            statementInfo.setCycleDateText(cycleDateHeader);

            // for smaller screen sizes
            if (getWindowManager().getDefaultDisplay().getWidth() <= 480) {
                if (cycleDateHeader.length() > 23) {
                    statementInfo.setFontSize(15);
                    // mTextCycleDate.setTextSize(15);
                } else {
                    statementInfo.setFontSize(17);
                    // mTextCycleDate.setTextSize(17);
                }
            } else {
                statementInfo.setFontSize(18);
                // mTextCycleDate.setTextSize(18);
            }

            thisMonth.setDate(jsonObject.get("date").toString());
            thisMonth.setPageCount(Integer.parseInt(jsonObject.get("pageCount")
                    .toString()));
            statementInfo.setThisMonth(thisMonth);

            if (index > 0) {
                final JSONObject prevJsonObject = sJsonArray.getJSONObject(index - 1);
                nextMonth.setDate(prevJsonObject.get("date").toString());
                nextMonth.setPageCount(Integer.parseInt(prevJsonObject.get(
                        "pageCount").toString()));
                statementInfo.setNextMonth(nextMonth);
            }

            if (index < sJsonArray.length() - 1) {
                final JSONObject nextJsonObject = sJsonArray.getJSONObject(index + 1);
                previousMonth.setDate(nextJsonObject.get("date").toString());
                previousMonth.setPageCount(Integer.parseInt(nextJsonObject.get(
                        "pageCount").toString()));
                statementInfo.setPreviousMonth(previousMonth);
            }
        } catch (final JSONException e) {
            Utils.log("JSON Exception", e.getMessage());
            // TODO handle error
        } catch (final NumberFormatException nfe) {
            Utils.log("NumberFormatException", nfe.getMessage());
            // TODO handle error
        }

        return statementInfo;
    }

    private void loadWebView(final StatementInfo statementInfo) {
        // performServiceHealthCheck();
        if (statementInfo == null) {
            // TODO handle error
            return;
        }

        final Display display = getWindowManager().getDefaultDisplay();
        final int screenWidth = display.getWidth();

        final double pageWidth = statementInfo.getThisMonth().getPageCount()
                * (807 + 20);
        final double initialScale = screenWidth / 875.0;
        final double minimumScale = initialScale * 0.5;
        final double maximumScale = initialScale * 3.0;
        Utils.log(LOG_TAG, "Screen width: " + screenWidth);
        Utils.log(LOG_TAG, "Initial scale: " + initialScale);

        final StringBuilder html = new StringBuilder();
        html.append("<html><head>")
                .append("<meta name='viewport' content='")
                // without this I have extra whitespace below images
                .append("width=device-width")
                .append(", initial-scale=")
                .append(initialScale)
                .append(", minimum-scale=")
                .append(minimumScale)
                .append(", maximum-scale=")
                .append(maximumScale)
                .append(", target-densitydpi=device-dpi")
                .append("' />")
                .append("<style>img{display:inline; margin:10px; "
                        + "-moz-box-shadow:    0 0 5px 5px lightgray; "
                        + "-webkit-box-shadow: 0 0 5px 5px lightgray; "
                        + "box-shadow:         0 0 5px 5px lightgray; " + ";}")
                .append("</style></head><body>");

        html.append("<div style='width:").append(pageWidth).append("px;'>");
        for (int i = 1; i <= statementInfo.getThisMonth().getPageCount(); i++) {
            html.append("<img src=\"")
                    .append(sBaseUrl + "/"
                            + statementInfo.getThisMonth().getDate() + "/" + i
                            + ".gif").append("\"></img>");
        }
        html.append("</div>");

        html.append("<script type='text/javascript'>").append(
                "function asyncEagerImageFetch(){");
        if (statementInfo.getNextMonth() != null) {
            html.append(prefetchImageHtml(statementInfo.getNextMonth()
                    .getDate(), statementInfo.getNextMonth().getPageCount()));
        }
        if (statementInfo.getPreviousMonth() != null) {
            html.append(prefetchImageHtml(statementInfo.getPreviousMonth()
                    .getDate(), statementInfo.getPreviousMonth().getPageCount()));
        }
        html.append("}").append("</script>").append("</body>")
                .append("</html>");

        Utils.log(LOG_TAG, "Source: " + html.toString());
        mWebView.loadData(html.toString(), "text/html", null);
    }

    private String prefetchImageHtml(final String date, final int pageCount) {
        final StringBuilder html = new StringBuilder();
        for (int i = 1; i <= pageCount; i++) {
            html.append("new Image().src='" + sBaseUrl + "/" + date + "/" + i
                    + ".gif';");
        }
        return html.toString();
    }

    private void performServiceHealthCheck() {
        final long currentTime = new Date().getTime();
        if (currentTime - sLastHealthCheck > (sHealthCheckThreshold)) {
            Utils.log(LOG_TAG, "Performing healthcheck");
//            new StatementHealthCheck().execute();
            Log.v(TAG, "About to call healthCheck");
//            new GetHealthCheck(getActivity(), sBaseUrl).loadDataFromNetwork(new HealthCheckListener());
            sLastHealthCheck = currentTime;
        }
    }

    /** Called when the user clicks the previous button */
    public void navigatePrevious(final View view) {
        if (isPageLoading)
            return;

        if (mStatementInfo == null) {
            mStatementInfo = new StatementInfo();
            mStatementInfo.setIndex(previousIndex);
        }
        mStatementInfo = getStatementDataAtIndex(mStatementInfo.getIndex() + 1);
        loadWebView(mStatementInfo);
    }

    /** Called when the user clicks the next button */
    public void navigateNext(final View view) {
        if (isPageLoading)
            return;

        if (mStatementInfo == null) {
            mStatementInfo = new StatementInfo();
            mStatementInfo.setIndex(previousIndex);
        }
        mStatementInfo = getStatementDataAtIndex(mStatementInfo.getIndex() - 1);
        loadWebView(mStatementInfo);
    }

    /** Called when the user clicks the download pdf button */
    public void downloadPdf(final View view) {
        // performServiceHealthCheck();
        final String url = sBaseUrl + "/" + mStatementInfo.getThisMonth().getDate()
                + ".pdf";
        Utils.log(LOG_TAG, "Download pdf from: " + url);

        Utils.log(LOG_TAG, "Testing network connection");
        if (!(Utils
                .isNetworkConnection((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))) {
            Utils.log(LOG_TAG, "No network connection");

            alertCloseActivity(getText(R.string.common_noInternetConnection_message));
        } else {
            Utils.log(LOG_TAG, "Yes network connection");
            new DownloadFile().execute(url);
        }
    }

    /** Called when the user clicks the Logout button */
    public void statementLogout(final View view) {

        Utils.log(LOG_TAG, "Logout From Statement is called");
        setResult(STATEMENT_LOGOUT);
        finish();
    }

    private boolean isValidIndex(final int index) {
        return (index >= 0 && index < sJsonArray.length());
    }

    private static String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    // formats date for header
    private static String getDateString(final String startDate, final String endDate) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
        try {
            final Date dStart = formatter.parse(startDate);
            final Date dEnd = formatter.parse(endDate);

            final Calendar cStart = Calendar.getInstance();
            cStart.setTime(dStart);
            final Calendar cEnd = Calendar.getInstance();
            cEnd.setTime(dEnd);

            String dStartString;
            if (cStart.get(Calendar.MONTH) == 11) {
                // set text size smaller so all text can fit
                dStartString = month[cStart.get(Calendar.MONTH)] + " "
                        + cStart.get(Calendar.DATE) + ", "
                        + (cStart.get(Calendar.YEAR));
            } else {
                // set text size to default size
                dStartString = month[cStart.get(Calendar.MONTH)] + " "
                        + cStart.get(Calendar.DATE);
            }

            return dStartString + " - " + month[cEnd.get(Calendar.MONTH)] + " "
                    + cEnd.get(Calendar.DATE) + ", "
                    + (cEnd.get(Calendar.YEAR));

        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Allows webview to invoke native code
     * 
     * @author sgoff0
     * 
     */
    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(final Context c) {
            mContext = c;
        }

        public void imageLoadError() {
            if (mStatementInfo.isError())
                return;
            mStatementInfo.setError(true);
            // image failed to load, determine whether to check into why this
            // happened

            if (!(Utils
                    .isNetworkConnection((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))) {
                alertCloseActivity(getText(R.string.common_noInternetConnection_message));
            } else {
//                new StatementHealthCheck().execute();
            	Log.v(TAG, "About to call healthCheck");
//                new GetHealthCheck(getActivity(), sBaseUrl).loadDataFromNetwork(new HealthCheckListener());
            }
        }
    }


	

    /**
     * Checks health of user's session. On no internet connection or non 200
     * http status from service returns a session expired message
     * 
     * @author sgoff0
     * 
     */
//    private class StatementHealthCheck extends AsyncTask<Void, Void, Integer> {
//        @Override
//        protected Integer doInBackground(Void... arg0) {
//            final HttpClient client = new DefaultHttpClient();
//            final HttpGet getRequest = new HttpGet(sBaseUrl + "/healthCheck");
//            CookieSyncManager.getInstance().sync();
//            String domain = sBaseUrl.substring(0, sBaseUrl.indexOf(".com") + 4);
//            String cookies = CookieManager.getInstance().getCookie(domain);
//            getRequest.setHeader("Cookie", cookies);
//            Integer status = -1;
//            try {
//                HttpResponse response = client.execute(getRequest);
//                final int statusCode = response.getStatusLine().getStatusCode();
//                status = statusCode;
//            } catch (IllegalStateException e) {
//                Utils.log(LOG_TAG, "ISE: " + e.getMessage(), e);
//                getRequest.abort();
//                mStatementInfo.setError(true);
//            } catch (Exception e) {
//                Utils.log(LOG_TAG, "E: " + e.getMessage(), e);
//                getRequest.abort();
//                mStatementInfo.setError(true);
//            } finally {
//                if ((client instanceof AndroidHttpClient)) {
//                    ((AndroidHttpClient) client).close();
//                }
//            }
//
//            return status;
//        }
//
//        @Override
//        protected void onPostExecute(Integer statusCode) {
//            if (statusCode != HttpStatus.SC_OK) {
//                Utils.log("StatementHealthCheck", "Error " + statusCode
//                        + " while calling healthcheck " + sBaseUrl
//                        + "/healthCheck");
//                if (statusCode == -1) {
//                    // cannot connect to URL, probably no internet
//                    alertCloseActivity(getText(R.string.common_noInternetConnection_message));
//                } else if (statusCode == 401) {
//                    // session expired
//                    alertCloseActivity(getText(R.string.common_sessionExpired_message));
//                } else if (statusCode == 503) {
//                    setResult(MAINT_EXPIRE_SESSION);
//                    finish();
//                } else {
//                    // another error
//                    finish();
//                }
//            }
//        }
//    }

    /**
     * Displays alert and closes activity
     * 
     * @param message
     */
    private void alertCloseActivity(final CharSequence message) {
        mErrorMessage = message;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mStatementInfo == null) {
                    mStatementInfo = new StatementInfo();
                }
                mStatementInfo.setError(true);
                hideUI();

                final AlertDialog alertDialog = new AlertDialog.Builder(
                        StatementActivity.this).create();
                alertDialog.setMessage(mErrorMessage);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                    final int which) {
                                Utils.log(LOG_TAG, "onClick: " + which);
                                if (mWebView != null) {
                                    mWebView.destroy();
                                }
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                }

                                // if session timeout
                                if (mErrorMessage
                                        .equals(getText(R.string.common_sessionExpired_message))) {
                                    setResult(EXPIRE_SESSION);
                                }
                                finish();
                            }
                        });
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    /**
     * Downloads PDF
     * 
     */
    private class DownloadFile extends AsyncTask<String, Integer, PDFObject> {
        private static final String TYPE_PDF = "application/pdf";
        private static final String TITLE_NO_PDF = "No PDF Viewer";
        private static final String MSG_NO_PDF = "A PDF Viewer was not found to view the file.";

        @Override
        protected PDFObject doInBackground(final String... sUrl) {
            try {
                final String url = sUrl[0];
                return Utils.downloadPDF(url);
            } catch (final Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Toast startToast = Toast.makeText(mContext, "Starting Download...",
                    Toast.LENGTH_SHORT);
            startToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            startToast.show();
        }

        @Override
        protected void onPostExecute(final PDFObject result) {
            super.onPostExecute(result);
            if (result.isSuccess()) {
                final File file = result.getFile();
                final Uri path = Uri.fromFile(file);
                final Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, TYPE_PDF);
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    final NotificationManager mNotificationManager = (NotificationManager) mContext
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    final Notification notification = new Notification(
                            android.R.drawable.stat_sys_download_done,
                            "Statement Download Complete",
                            System.currentTimeMillis());
                    final PendingIntent pendingIntent = PendingIntent.getActivity(
                            mContext, 1, pdfIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    notification.setLatestEventInfo(mContext,
                            "View Discover Statement",
                            "View " + file.getName(), pendingIntent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(1, notification);
                } catch (final Exception e) {
                    Utils.log(LOG_TAG,
                            "onPageStarted() Problem with launching PDF Viewer.",
                            e);
                    Utils.showOkAlert(mContext, TITLE_NO_PDF, MSG_NO_PDF);
                }
            } else {
                Utils.showOkAlert(mContext, result.getTitle(),
                        result.getMessage());
            }
        }
    }

    /**
     * Value object representing the statement being displayed.
     * 
     * @author sgoff0
     * 
     */
    private class StatementInfo {
        private String cycleDateText;
        private boolean error;
        private StatementDate nextMonth;
        private StatementDate previousMonth;
        private StatementDate thisMonth;
        private int index;
        private int fontSize;

        public int getIndex() {
            return index;
        }

        public String getCycleDateText() {
            return cycleDateText;
        }

        public void setCycleDateText(final String cycleDateText) {
            this.cycleDateText = cycleDateText;
        }

        public void setIndex(final int index) {
            this.index = index;
        }

        public boolean isError() {
            return error;
        }

        public void setError(final boolean error) {
            this.error = error;
        }

        public StatementDate getNextMonth() {
            return nextMonth;
        }

        public void setNextMonth(final StatementDate nextMonth) {
            this.nextMonth = nextMonth;
        }

        public StatementDate getPreviousMonth() {
            return previousMonth;
        }

        public void setPreviousMonth(final StatementDate previousMonth) {
            this.previousMonth = previousMonth;
        }

        public StatementDate getThisMonth() {
            return thisMonth;
        }

        public void setThisMonth(final StatementDate thisMonth) {
            this.thisMonth = thisMonth;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(final int fontSize) {
            this.fontSize = fontSize;
        }

    }

    /**
     * Value object holding on to a specific statement date and it's page count.
     * 
     * @author sgoff0
     * 
     */
    private class StatementDate {
        private String date;
        private Integer pageCount;

        public String getDate() {
            return date;
        }

        public void setDate(final String date) {
            this.date = date;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(final int pageCount) {
            this.pageCount = pageCount;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.DroidGap#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        Utils.log("Stmt", "inside onkeyup of stmt....");
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cordova.DroidGap#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        Utils.log("Stmt", "inside onkeydown of stmt....");

        if (mWebView == null) {
            Utils.log("My Tag",
                    "Webview is null on KeyCode: " + String.valueOf(keyCode));
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // if (mWebView.canGoBack()) {
            // Utils.log("Stmt", "back key detected n can go back.");
            // mWebView.goBack();
            // } else {
            finish();
            // }
        }
        return true;
    }
}
