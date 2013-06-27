package com.discover.mobile.card.common.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.discover.mobile.card.R;

public class FastcheckUtil {

	private static final String keyStr = "OQrav49fqkUSB4yYe9HhDg==";
	private static final String DISCOVER_CARD_PREF = "DiscoverCardPref";

	private static byte[] getKey() {
		return Base64.decode(keyStr, Base64.DEFAULT);
	}

	public static void storeFastcheckToken(Activity activity, String token) {
		SharedPreferences sharedPref = activity.getSharedPreferences(DISCOVER_CARD_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(
				activity.getResources().getString(
						R.string.fast_check_token_key_in_ps), token);
		editor.commit();
	}

	public static String readFastcheckToken(Activity activity) {
		SharedPreferences sharedPref = activity.getSharedPreferences(DISCOVER_CARD_PREF, Context.MODE_PRIVATE);
		String token = sharedPref.getString(
				activity.getResources().getString(
						R.string.fast_check_token_key_in_ps), null);
		return token;
	}
	
	

	public static String genClientBindingToken() throws Exception {
		SecureRandom scrRndm = new SecureRandom();
		byte[] random = new byte[64];// 64 byte per INFO SEC
		scrRndm.nextBytes(random);
		return new String(Base64.encodeToString(random, Base64.NO_WRAP));
	}

	public static String encrypt(String clearStr) throws Exception {
		byte[] clearBytes = Base64.decode(clearStr, Base64.DEFAULT);
		byte[] encryptedBytes = encrypt(clearBytes);
		return new String(Base64.encodeToString(encryptedBytes, Base64.NO_WRAP));
	}

	private static byte[] encrypt(byte[] clearBytes) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encryptedBytes = cipher.doFinal(clearBytes);
		return encryptedBytes;
	}

	public static String decrypt(String encryptedStr) throws Exception {
		byte[] encryptedBytes = Base64.decode(encryptedStr, Base64.DEFAULT);
		byte[] clearBytes = decrypt(encryptedBytes);
		return new String(Base64.encodeToString(clearBytes, Base64.NO_WRAP));
	}

	private static byte[] decrypt(byte[] encryptedBytes) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;
	}

}
