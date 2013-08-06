/**
 * 
 */
package com.discover.mobile.card.auth.strong;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.services.auth.strong.StrongAuthCreateUser;
import com.discover.mobile.card.services.auth.strong.StrongAuthCreateUserDetails;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class generate key for Strong Authentication
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthUtil {

    /**
     * Context
     */
    private final Context context;

    /**
     * Prefix pattern for Message digest
     */
    private final String ID_PREFIX = "%&(()!12[";

    /**
     * Constructor
     * 
     * @param context
     */
    public StrongAuthUtil(final Context context) {
        this.context = context;
    }

    /* *//**
     * This method get deviceId, simId and subscriberId from telephone
     * manager and return strong Authentication data.
     * 
     * @return StrongAuthBean
     * @throws NoSuchAlgorithmException
     */
    /*
     * public StrongAuthBean getStrongAuthData() throws NoSuchAlgorithmException
     * { final TelephonyManager telephonyManager = (TelephonyManager) context
     * .getSystemService(Context.TELEPHONY_SERVICE); final StrongAuthBean
     * authBean = new StrongAuthBean();
     * authBean.setDeviceId(getSha256Hash(telephonyManager.getDeviceId()));
     * authBean.setSimId(getSha256Hash(telephonyManager.getSimSerialNumber()));
     * authBean.setSubscriberId(getSha256Hash(telephonyManager.getDeviceId()));
     * return authBean; }
     */

    /**
     * This method get deviceId, simId and subscriberId from telephone manager
     * and return strong Authentication data.
     * 
     * @return StrongAuthBean
     * @throws NoSuchAlgorithmException
     */
    public StrongAuthBean getStrongAuthData() throws NoSuchAlgorithmException {
        context.getSystemService(Context.TELEPHONY_SERVICE);
        final StrongAuthBean authBean = new StrongAuthBean();
        /*
         * authBean.setDeviceId(getSha256Hash(telephonyManager.getDeviceId()));
         * authBean
         * .setSimId(getSha256Hash(telephonyManager.getSimSerialNumber()));
         * authBean
         * .setSubscriberId(getSha256Hash(telephonyManager.getDeviceId()));
         */
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(context);
        Utils.log(
                "StrongAuthBean",
                "DID"
                        + cardShareDataStoreObj.getValueOfAppCache(
                                context.getResources().getString(R.string.DID))
                                .toString());
        Utils.log(
                "StrongAuthBean",
                "SID"
                        + cardShareDataStoreObj.getValueOfAppCache(
                                context.getResources().getString(R.string.SID))
                                .toString());
        Utils.log(
                "StrongAuthBean",
                "OID"
                        + cardShareDataStoreObj.getValueOfAppCache(
                                context.getResources().getString(R.string.OID))
                                .toString());
        authBean.setDeviceId(getSha256Hash(cardShareDataStoreObj
                .getValueOfAppCache(
                        context.getResources().getString(R.string.DID))
                .toString()));
        authBean.setSimId(getSha256Hash(cardShareDataStoreObj
                .getValueOfAppCache(
                        context.getResources().getString(R.string.SID))
                .toString()));
        authBean.setSubscriberId(getSha256Hash(cardShareDataStoreObj
                .getValueOfAppCache(
                        context.getResources().getString(R.string.OID))
                .toString()));

        return authBean;
    }

    /**
     * This method will get string data and create message digest
     * 
     * @param toHash
     * @return String
     * @throws NoSuchAlgorithmException
     */
    private String getSha256Hash(final String toHash)
            throws NoSuchAlgorithmException {
        final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX
                + toHash;

        final MessageDigest digester = MessageDigest.getInstance("SHA-256");
        final byte[] preHash = safeToHash.getBytes();

        // Reset happens automatically after digester.digest() but we don't know
        // its state beforehand so call reset()
        digester.reset();
        final byte[] postHash = digester.digest(preHash);

        return convertToHex(postHash);
    }

    /* 13.4 Changes Start */
    public void createUser(final CardErrorHandlerUi errorHandlerUi) {
        StrongAuthCreateUser authUserHandler = new StrongAuthCreateUser(
                context, new CardEventListener() {

                    @Override
                    public void onSuccess(final Object data) {
                        // TODO Auto-generated method stub
                        StrongAuthCreateUserDetails createUserDetails = (StrongAuthCreateUserDetails) data;
                        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                                .getInstance(context);
                        final Intent strongAuthEnterInfoActivity = new Intent(
                                context, StrongAuthEnterInfoActivity.class);
                        cardShareDataStoreObj.addToAppCache(context
                                .getString(R.string.sa_question_answer_list),
                                createUserDetails);
                        context.startActivity(strongAuthEnterInfoActivity);
                    }

                    @Override
                    public void OnError(final Object data) {
                        // TODO Auto-generated method stub
                        CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                                errorHandlerUi);
                        cardErrorResHandler
                                .handleCardError((CardErrorBean) data);
                    }
                });
        authUserHandler.sendRequest();
    }

    /* 13.4 Changes End */

    /**
     * This method convert byte to hex format
     * 
     * @param byte[]
     * @return String
     */
    private static String convertToHex(final byte[] data) {
        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1,
                data));
    }
}
