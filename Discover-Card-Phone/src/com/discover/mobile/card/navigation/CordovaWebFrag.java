package com.discover.mobile.card.navigation;

import java.net.HttpCookie;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.phonegap.plugins.JQMResourceMapper;
import com.discover.mobile.common.BaseFragment;

/*
 Fragment Initialization:
 - fragment re-initializes every time view rotates
 Fragment Life Cycle:
 - First Initialization: onAttach -> onCreateView -> onCreate -> onResume
 - Destruction: onPause -> onDestroy
 - Screen Rotated Initialization: onPause -> onDestroy -> onCreate -> onResume
 onResume - screen shown
 onPause - screen turns off (save state)
 */

/*
 Adding PhoneGap Framework
 - Add cordova-2.3.0.jar inside libs folder
 - Add cordova folder to main project folder
 - Add www folder to assets
 - Add this class in as PhoneGap Fragment
 - Add this xml file cordova_web_frag.xml
 - Add permissions as shown by PhoneGap website
 */

/**
 * This class reprsents Cordova webview class used for PhoneGap integration
 * 
 * @author cts
 * 
 */
public class CordovaWebFrag extends BaseFragment implements PhoneGapInterface,
		CordovaInterface {
	static final String TAG = "CordovaWebFrag";

	/**
	 * Cordova Webview
	 */
	private CordovaWebView cwv;
	/**
	 * View
	 */
	private View mView;

	private String m_title = null;

	private JQMResourceMapper jqmResourceMapper	 = JQMResourceMapper.getInstance();

	private Context mContext;

	private CardMenuItemLocationIndex mCardMenuLocation;

	private String m_currentLoadedJavascript = null;

	private int m_currentTitleId = -1;

	/**
	 * Called when fragment gets attached to activity
	 */
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		Log.v(TAG, "onAttach");
	}

	/**
	 * called when View is to created,inflation of fragment layout happens here
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		Log.v(TAG, "inside onCreateView....");

		if (mView != null) {
			final ViewParent oldParent = mView.getParent();
			if (oldParent != container) {
				((ViewGroup) oldParent).removeView(mView);
			}
			return mView;
		} else {
			mView = inflater.inflate(R.layout.cordova_web_frag, null, false);
			cwv = (CordovaWebView) mView
					.findViewById(R.id.cordova_web_frag_cordova_webview);
			cwv.clearCache(true);
			cwv.clearHistory();

			cwv.loadUrl("file:///android_asset/www/index.html", 60000);

			MyWebviewClient myWebViewClient = new MyWebviewClient(this, mContext);
			myWebViewClient.setWebView(cwv);
			cwv.setWebViewClient(myWebViewClient);

			this.cwv.setWebChromeClient(new org.apache.cordova.CordovaChromeClient(
					CordovaWebFrag.this) {

				@Override
				public void onGeolocationPermissionsShowPrompt(
						final String origin,
						final GeolocationPermissions.Callback callback) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);

					builder.setMessage(
							"Discover Would Like to Use Your Current Location")

							.setPositiveButton("Accept",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog, int id) {

											callback.invoke(origin, true, false);

										}

									})

							.setNegativeButton("Decline",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog, int id) {

											// User cancelled the dialog

											callback.invoke(origin, false,
													false);

											// Toast.makeText(DiscoverMobileActivity.this,
											// "Cannot determine current location",
											// Toast.LENGTH_LONG).show();

										}

									});

					AlertDialog dialog = builder.create();

					dialog.show();

				}

			});
			/********** Hemang **********/
			if (cwv != null) {
				WebSettings webSettings = cwv.getSettings();
				webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
			}
			/********** Hemang **********/
			return mView;
		}
	}

	public String getM_currentLoadedJavascript() {
		return m_currentLoadedJavascript;
	}

	public void setM_currentLoadedJavascript(String m_currentLoadedJavascript) {
		this.m_currentLoadedJavascript = m_currentLoadedJavascript;
	}

	/**
	 * Create the fragment
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		mCardMenuLocation = CardMenuItemLocationIndex.getInstance();

		passCookieToWebview();
	}

	/**
	 * When the app resumes, make sure the fragment shows the same
	 */
	@Override
	public void onResume() {
		super.onResume();

		Log.v(TAG, "onResume");
		if (null != cwv)
			cwv.handleResume(true, false);
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.v(TAG, "onPause");
		if (null != cwv)
			cwv.handlePause(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestroy");
		if (null != cwv)
			cwv.handleDestroy();
	}

	/**
	 * get the action bar title
	 */
	@Override
	public int getActionBarTitle() {
    	m_currentTitleId = -1;
        Log.v(TAG, "getActionBarTitle n title is " + m_title);
        if (null != m_title) {
            jqmResourceMapper = JQMResourceMapper.getInstance();
            
            m_currentTitleId = jqmResourceMapper.getTitleStringId(m_title);
        } 
         return m_currentTitleId;
    }

	// PhoneGap Interface
	@Override
	public void javascriptCall(String javascript) {
		Log.v(TAG, "javascript: " + javascript);
		m_currentLoadedJavascript = javascript;
		String firstLetter = javascript.substring(0, 1);
		firstLetter = firstLetter.toLowerCase(Locale.ENGLISH);
		String rest = javascript.substring(1);
		rest = rest.replaceAll(" ", "");
		String restWithoutSpecialChar = firstLetter + rest;
		javascript = restWithoutSpecialChar.replaceAll("[^a-zA-Z]+", "") + "()";
		// javascript = firstLetter + rest + "();";
		Log.v(TAG, "javascript to send: " + javascript);
		if (null != cwv) {
			cwv.requestFocus(View.FOCUS_DOWN);
			cwv.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
						if (!v.hasFocus()) {
							v.requestFocus();
						}
						break;
					}
					return false;
				}
			});

			cwv.sendJavascript(javascript);
		}

	}

	private void passCookieToWebview() {
		final android.webkit.CookieSyncManager webCookieSync = CookieSyncManager
				.createInstance(getActivity());
		final android.webkit.CookieManager webCookieManager = CookieManager
				.getInstance();
		webCookieManager.setAcceptCookie(true);
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(getActivity());
		final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
				.getCookieManagerInstance();
		final List<HttpCookie> cookies = sessionCookieManagerObj
				.getHttpCookie();
		final String url = sessionCookieManagerObj.getBaseUri().toString();
		for (final HttpCookie cookie : cookies) {
			final String setCookie = new StringBuilder(cookie.toString())
					.append("; domain=").append(cookie.getDomain())
					.append("; path=").append(cookie.getPath()).toString();
			webCookieManager.setCookie(url, setCookie);
			webCookieSync.sync();

			Utils.log("passCookieToWebview", "setCookie11111" + setCookie);

		}
	}

	/**
	 * It returns the instance of CordovaWebView of this Fragment.
	 * CordovaWebView
	 */
	public CordovaWebView getCordovaWebviewInstance() {
		return cwv;
	}

	/**
	 * Receives the title to be set to actionbar.
	 * 
	 * @param title
	 *            to set.
	 */
	public void setTitle(final String title) {
		Utils.log(TAG, "inside setTitle n title is " + title);
		m_title = title;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public int getGroupMenuLocation() {
        Utils.log(TAG, "inside getGroupMenuLocation ");
        int tempId = getActionBarTitle();
        if(tempId == -1)
        {
        	if(null != m_currentLoadedJavascript){
        		
        	Utils.log(TAG,"m_currentLoadedJavascript is "+m_currentLoadedJavascript);
        	jqmResourceMapper = JQMResourceMapper.getInstance();
              
             tempId = jqmResourceMapper.getTitleStringId(m_currentLoadedJavascript);
             return mCardMenuLocation.getMenuGroupLocation(tempId);
        	}else
        		return mCardMenuLocation.getMenuGroupLocation(tempId);
        }
        else
        	return mCardMenuLocation.getMenuGroupLocation(tempId);
    }

	@Override
	public int getSectionMenuLocation() {
        Utils.log(TAG, "inside getSectionMenuLocation");
        int tempId = getActionBarTitle();
        if(tempId == -1)
        {
        	if(null != m_currentLoadedJavascript){
        	Utils.log(TAG,"m_currentLoadedJavascript is "+m_currentLoadedJavascript);
        	jqmResourceMapper = JQMResourceMapper.getInstance();
              
             tempId = jqmResourceMapper.getTitleStringId(m_currentLoadedJavascript);
             return mCardMenuLocation.getMenuSectionLocation(tempId);
        	}else
        		return mCardMenuLocation.getMenuSectionLocation(tempId);
        }
        else
        	return mCardMenuLocation.getMenuSectionLocation(tempId);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.CordovaInterface#cancelLoadUrl()
	 */
	@Override
	public void cancelLoadUrl() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.CordovaInterface#getContext()
	 */
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.CordovaInterface#getThreadPool()
	 */
	@Override
	public ExecutorService getThreadPool() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.CordovaInterface#onMessage(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object onMessage(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.cordova.api.CordovaInterface#setActivityResultCallback(org
	 * .apache.cordova.api.CordovaPlugin)
	 */
	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.cordova.api.CordovaInterface#startActivityForResult(org.apache
	 * .cordova.api.CordovaPlugin, android.content.Intent, int)
	 */
	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	class MyWebviewClient extends CordovaWebViewClient {
	    Context context;
		/**
		 * Constructor
		 * 
		 * @param cordova
		 */
		public MyWebviewClient(CordovaInterface cordova, Context context) {
			super(cordova);
			this.context = context;
			// TODO Auto-generated constructor stub
		}
		
		private void showDialogForExternalLink(final String strURL)
		{
    		AlertDialog.Builder builder = new AlertDialog.Builder(context);
            
            builder.setMessage("Do you want to open this link in an extenal web browser? ")
            .setCancelable(true)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    startUrlInChrome(strURL);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
		}
		protected void startUrlInChrome(final String urlStr) {
	        Uri uri = Uri.parse(urlStr);
	        try {
	            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
	            context.startActivity(launchBrowser);
	        } catch (ActivityNotFoundException e) {
	            e.printStackTrace();
	        }
	    }
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#onPageStarted(android.webkit
		 * .WebView, java.lang.String, android.graphics.Bitmap)
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			/* Defect 95810 and 96379*/
			if (url.indexOf("http://www.google.com/intl")>-1 &&
			    url.indexOf("help/terms_maps.html")>-1
			   )
			{
			    //cancelLoadUrl();
			    //cwv.goBack();
			    view.stopLoading();
			    showDialogForExternalLink(url);
			}
			else if (url.indexOf("http://maps.google.com/maps?")>-1 ||
			        url.indexOf("facebook.com")>-1 || 
                    url.indexOf("linkedin.com")>-1 || 
			        url.indexOf("twitter.com")>-1 || 
                    (url.indexOf("www.discover.com/credit-cards")>-1 && url.indexOf("privacy-policies")>-1)
			        )
            {
			    view.stopLoading();
                showDialogForExternalLink(url);
            }

			/* Defect 95810 and 96379*/
			Utils.log(TAG, "MyWebviewClient on pageStarted.... " + url);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#onPageFinished(android.webkit
		 * .WebView, java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView arg0, String arg1) {
			// TODO Auto-generated method stub
			super.onPageFinished(arg0, arg1);
			Utils.log(TAG, "MyWebviewClient on pagefinished.... " + arg1);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#doUpdateVisitedHistory(android
		 * .webkit.WebView, java.lang.String, boolean)
		 */
		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			super.doUpdateVisitedHistory(view, url, isReload);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#onReceivedError(android.webkit
		 * .WebView, int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView arg0, int arg1, String arg2,
				String arg3) {
			// TODO Auto-generated method stub
			super.onReceivedError(arg0, arg1, arg2, arg3);
			Utils.log(TAG, "on onReceivedError.... " + arg2);
			Utils.isSpinnerAllowed = true;
			Utils.isSpinnerForOfflinePush = false;
			Utils.hideSpinner();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onFormResubmission(android.webkit.WebView
		 * , android.os.Message, android.os.Message)
		 */
		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			// TODO Auto-generated method stub
			super.onFormResubmission(view, dontResend, resend);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#onReceivedHttpAuthRequest
		 * (android.webkit.WebView, android.webkit.HttpAuthHandler,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
		 * java.lang.String)
		 */
		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onReceivedLoginRequest(android.webkit
		 * .WebView, java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			// TODO Auto-generated method stub
			super.onReceivedLoginRequest(view, realm, account, args);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.cordova.CordovaWebViewClient#onReceivedSslError(android
		 * .webkit.WebView, android.webkit.SslErrorHandler,
		 * android.net.http.SslError)
		 */
		@Override
		public void onReceivedSslError(WebView arg0, SslErrorHandler arg1,
				SslError arg2) {
			// TODO Auto-generated method stub
			super.onReceivedSslError(arg0, arg1, arg2);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onScaleChanged(android.webkit.WebView,
		 * float, float)
		 */
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			super.onScaleChanged(view, oldScale, newScale);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onTooManyRedirects(android.webkit.WebView
		 * , android.os.Message, android.os.Message)
		 */
		@Override
		public void onTooManyRedirects(WebView view, Message cancelMsg,
				Message continueMsg) {
			// TODO Auto-generated method stub
			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onUnhandledKeyEvent(android.webkit.WebView
		 * , android.view.KeyEvent)
		 */
		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			super.onUnhandledKeyEvent(view, event);
		}

	}
}
