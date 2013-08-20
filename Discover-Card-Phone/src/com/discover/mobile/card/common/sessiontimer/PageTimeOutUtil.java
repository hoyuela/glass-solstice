package com.discover.mobile.card.common.sessiontimer;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.os.Handler;

import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthEnterInfoActivity;
import com.discover.mobile.card.common.net.service.WSProxy;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.login.register.EnterNewPasswordActivity;
import com.discover.mobile.card.login.register.ForgotOrRegisterFinalStep;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.whatsnew.WhatsNewActivity;

/**
 * 
 * �2013 Discover Bank
 * 
 * * This class will hold page timeout related logic and will also be shared by
 * phonegap view
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public final class PageTimeOutUtil {

    private final Handler mHandler = new Handler();
    private final Context mContext;
    private static PageTimeOutUtil mPageTimeOut;
    private final WSProxy mWSProxy;

    private final String TAG = "PageTimeOut";
    private static final long TIMEOUT_PERIOD = 600000; // 600000
    private Calendar mCalendar;

    public PageTimeOutUtil(final Context context) {
        mContext = context;
        mWSProxy = new WSProxy();
    }

    public static synchronized PageTimeOutUtil getInstance(
            final Context mContext) {
        if (null == mPageTimeOut) {

            mPageTimeOut = new PageTimeOutUtil(mContext);
            Utils.log("getInstance", "mPageTimeOut new instance......"
                    + mPageTimeOut);

        }

        return mPageTimeOut;
    }

    public void destroyTimer() {

        mHandler.removeCallbacks(pageTimeOutAction);
        mPageTimeOut = null;
        Utils.log("inside destroyTimer", "mPageTimeOut object......"
                + mPageTimeOut);
    }

    /*
     * This function will start the initial timer after login
     */
    public void startPageTimer() {

        Utils.log(TAG, "inside startPageTimer()......");
        mHandler.removeCallbacks(pageTimeOutAction);
        mHandler.postDelayed(pageTimeOutAction, TIMEOUT_PERIOD);
    }

    private final Runnable pageTimeOutAction = new Runnable() {
        @Override
        public void run() {

            stopPageTimer();
        }
    };

    /**
     * It will stop the timer after specified duration.
     */
    private void stopPageTimer() {
        Utils.log(TAG, "inside stopPageTimer()......");

        mHandler.removeCallbacks(pageTimeOutAction);

        logoutUserOnTimerExpire();
    }

    /*
     * This function will be called whenever page transition takes place.
     */
    public void keepSessionAlive() {
        Utils.log(TAG, "inside keepSessionAlive()......");
        // fire KeepSessionAlive if last rest call is 30 seconds ago
        mCalendar = Calendar.getInstance(TimeZone.getTimeZone(mContext
                .getString(R.string.current_timezone)));
        final long now = mCalendar.getTimeInMillis();

        final long lastRestCallTime = mWSProxy.getLastRestCallTime();

        Utils.log(TAG, "current time is " + now + "lastrestcall time is "
                + lastRestCallTime + " diff in timing is "
                + (now - lastRestCallTime) / 1000);

        if ((now - lastRestCallTime) / 1000 >= 30) {

            final ResetServerTimeOutUtil resetTimer = new ResetServerTimeOutUtil(
                    mContext);
            resetTimer.resetServerTimeOut();
            startPageTimer();
        }
    }

    /*
     * It will update lastrestcalltime variable.
     */
    public void setLastRestCallTime() {

        mWSProxy.setLastRestCallTime();
    }

    /*
     * This function will logout user once the timer is expired.
     */
    public void logoutUserOnTimerExpire() {
        Utils.log(TAG, "inside logoutUserOnTimerExpire().....");
        // DEFECT 96936
        if (mContext instanceof CardNavigationRootActivity) {
            ((CardNavigationRootActivity) mContext).idealTimeoutLogout();
        } else if (mContext instanceof ForgotOrRegisterFinalStep) {
            ((ForgotOrRegisterFinalStep) mContext).idealTimeoutLogout();
        } else if (mContext instanceof EnterNewPasswordActivity) {
            ((EnterNewPasswordActivity) mContext).idealTimeoutLogout();
        }
        // DEFECT 96936
        /* 13.4 changes start */
        else if (mContext instanceof StrongAuthEnterInfoActivity) {
            ((StrongAuthEnterInfoActivity) mContext).idealTimeoutLogout();
        } else if (mContext instanceof WhatsNewActivity) {
            ((WhatsNewActivity) mContext).idealTimeoutLogout();
        }
        /* 13.4 changes End */
    }
}
