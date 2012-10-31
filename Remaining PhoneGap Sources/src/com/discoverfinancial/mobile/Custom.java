package com.discoverfinancial.mobile;

import org.apache.cordova.DroidGap;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;


public class Custom {
    private DroidGap mGap;

    public Custom(DroidGap gap) {
        mGap = gap;
    }

    /*
     * public String getImeiNumber(){ TelephonyManager tm = (TelephonyManager)
     * mGap.getSystemService(Context.TELEPHONY_SERVICE); String imeiId =
     * tm.getDeviceId(); return imeiId; }
     */
    public String getSimSerialNumber() {
    	Log.v("Custom", "getSimSerialNumber");
        TelephonyManager tm = (TelephonyManager) mGap
                .getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNum = tm.getSimSerialNumber();
        return simSerialNum;
    }

    public String getDeviceId() {
    	Log.v("Custom", "getDeviceId");
        TelephonyManager tm = (TelephonyManager) mGap
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = tm.getDeviceId();
        return deviceID;
    }
}