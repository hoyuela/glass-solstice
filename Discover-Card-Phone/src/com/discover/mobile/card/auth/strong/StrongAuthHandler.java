/**
 * 
 */
package com.discover.mobile.card.auth.strong;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.login.register.ForgotBothAccountInformationActivity;
import com.discover.mobile.card.login.register.ForgotPasswordAccountInformationActivity;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.services.auth.strong.StrongAuthCheck;
import com.discover.mobile.card.services.auth.strong.StrongAuthDetails;
import com.discover.mobile.card.services.auth.strong.StrongAuthQuestion;
import com.discover.mobile.common.IntentExtraKey;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class handles Strong Authentication flow. It check with server if Strong
 * Authentication is required or not. If its not required it send success to
 * calling Activity so that calling activity can move further with normal flow.
 * 
 * If Strong Authentication is required than it execute complete Strong
 * Authentication flow like displaying Question and verifying answer with server
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthHandler {

    private final String TAG = StrongAuthHandler.class.getSimpleName();
    private final Context context;
    private CardEventListener listener;
    private CardEventListener strongAuthQuestionListener;
    private int requestCode;
    public static StrongAuthListener authListener;
    private final int SALOCKED = 1401;
    private final int NOTENROLLED = 1402;
    private final int SKIPPED = 1404;
    private boolean skipCheck = false;
    // Defect id 95164
    public static final String FORGOT_BOTH_FLOW = "forgotbothflow";
    public static final String FORGOT_PASSWORD_FLOW = "forgotpasswordflow";

    // Defect id 95164
    /**
     * Constructor incase of
     * 
     * @param context
     * @param authListener
     */
    public StrongAuthHandler(final Context context,
            final StrongAuthListener authListener, final boolean skipCheck) {
        this.context = context;
        StrongAuthHandler.authListener = authListener;
        this.skipCheck = skipCheck;
    }

    /**
     * Default Constructor, pass Requestcode for intent
     * 
     * @param context
     * @param listener
     *            CardEventListener
     * @param requestCode
     */
    public StrongAuthHandler(final Context context,
            final CardEventListener listener, final int requestCode) {
        this.context = context;
        this.listener = listener;
        this.requestCode = requestCode;
    }

    /**
     * This methods make a service calls Strong Auth flow.
     * 
     */
    public void strongAuth() {

        strongAuthQuestionListener = new CardEventListener() {

            @Override
            public void onSuccess(final Object data) {
                final StrongAuthDetails authDetails = (StrongAuthDetails) data;
                final Intent strongAuth = new Intent(context,
                        EnhancedAccountSecurityActivity.class);
                strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION,
                        authDetails.questionText);
                strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID,
                        authDetails.questionId);

                strongAuth.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);// DEFECT
                                                                     // 96497

                // Defect id 95164
                if (context instanceof ForgotBothAccountInformationActivity) {
                    strongAuth.putExtra(FORGOT_BOTH_FLOW, true);
                } else if (context instanceof ForgotPasswordAccountInformationActivity) {
                    strongAuth.putExtra(FORGOT_PASSWORD_FLOW, true);
                }
                // Defect id 95164

                Log.i(TAG, "In Strong auth --" + context.getClass());
                if (authListener != null) {
                	Utils.hideSpinner();
                    context.startActivity(strongAuth);
                    Log.i(TAG, "In Strong auth " + context.getClass());
                    if (context instanceof ForgotPasswordAccountInformationActivity
                            || context instanceof ForgotBothAccountInformationActivity
                            || context instanceof RegistrationAccountInformationActivity) // DEFECT
                                                                                          // 96355
                    {
                        Log.i(TAG, "Finish him");
                        Activity activity = (Activity) context;
                        activity.finish();
                    }
                } else {
                	Utils.hideSpinner();
                    ((Activity) context).startActivityForResult(strongAuth,
                            requestCode);
                }

            }

            @Override
            public void OnError(final Object data) {
                if (authListener != null) {
                    authListener.onStrongAuthError(data);
                } else {
                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                            (CardErrorHandlerUi) context);
                    cardErrorResHandler.handleCardError((CardErrorBean) data);
                }
            }
        };

        if (skipCheck) {
            final StrongAuthQuestion authQuestion = new StrongAuthQuestion(
                    context, strongAuthQuestionListener);
            authQuestion.sendRequest();
        } else {
            // Call strong auth web service
            final StrongAuthCheck authCheckCall = new StrongAuthCheck(context,
                    new CardEventListener() {

                        // On success return back success to calling activity
                        @Override
                        public void onSuccess(final Object data) {
                            if (authListener != null) {
                                authListener.onStrongAuthSucess(data);
                            } else {
                                listener.onSuccess(data);
                            }
                        }

                        @Override
                        public void OnError(final Object data) {
                            final CardErrorBean bean = (CardErrorBean) data;
                            final CardShareDataStore cardShareDataStore = CardShareDataStore
                                    .getInstance(context);
                            final String cache = (String) cardShareDataStore
                                    .getValueOfAppCache("WWW-Authenticate");

                            Utils.log(
                                    TAG,
                                    "--Error message-- "
                                            + bean.getErrorMessage());

                            Log.i(TAG, "cache is " + cache);
                            // If error code is 401 and cache contains challenge
                            // then show strong auth question
                            if (bean.getErrorCode().contains(
                                    "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                                    && cache != null
                                    && cache.contains("challenge")) {
                                cardShareDataStore
                                        .deleteCacheObject("WWW-Authenticate");
                                final StrongAuthQuestion authQuestion = new StrongAuthQuestion(
                                        context, strongAuthQuestionListener);
                                authQuestion.sendRequest();
                            } else if (bean.getErrorCode().contains(
                                    "" + HttpURLConnection.HTTP_UNAUTHORIZED)
                                    && cache != null
                                    && cache.contains("skipped")) {
                                Log.i(TAG, "yoyoooooo");
                                cardShareDataStore
                                        .deleteCacheObject("WWW-Authenticate");
                                if (authListener != null) {
                                    Log.i(TAG, "yoyoooooo11");
                                    authListener.onStrongAuthSkipped(data);
                                }
                            } else if (bean.getErrorCode().contains(
                                    "" + HttpURLConnection.HTTP_FORBIDDEN)
                                    && bean.getErrorCode().contains(
                                            "" + SKIPPED)) {
                                if (authListener != null) {
                                    authListener.onStrongAuthSkipped(data);
                                }
                            } else if (bean.getErrorCode().contains(
                                    "" + HttpURLConnection.HTTP_FORBIDDEN)
                                    && bean.getErrorCode().contains(
                                            "" + SALOCKED)) {
                                if (authListener != null) {
                                    authListener.onStrongAuthCardLock(data);
                                }
                            } else if (bean.getErrorCode().contains(
                                    "" + HttpURLConnection.HTTP_FORBIDDEN)
                                    && bean.getErrorCode().contains(
                                            "" + NOTENROLLED)) {
                                if (authListener != null) {
                                    authListener.onStrongAuthNotEnrolled(data);
                                }
                            } else {
                                if (authListener != null) {
                                    authListener.onStrongAuthError(data);
                                } else {
                                    listener.OnError(data);
                                }
                            }
                        }
                    });
            authCheckCall.sendRequest();
        }
    }
}
