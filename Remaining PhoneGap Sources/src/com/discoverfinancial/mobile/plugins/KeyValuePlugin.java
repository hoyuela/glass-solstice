package com.discoverfinancial.mobile.plugins;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.SharedPreferences;

public class KeyValuePlugin extends Plugin {

	static final String setKeyValue = "setKeyValue";
	static final String getValue = "getValue";
	static final String deleteKey = "deleteKey";

	static final String setEncryptedKeyValue = "setEncryptedKeyValue";
	static final String getEncryptedValue = "getEncryptedValue";
	static final String deleteEncryptedKey = "deleteEncryptedKey";

	static final String DISCOVER_OPEN_KEY_VALUE = "DISCOVER_OPEN_KEY_VALUE";
	static final String DISCOVER_OPEN_ENCRYPTED_KEY_VALUE = "DISCOVER_OPEN_ENCRYPTED_KEY_VALUE";

	static final String masterPassword = "iamthemasterpassword";

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		if (action.equals(setKeyValue)) {
			String key = null;
			String value = null;
			try {
				key = data.getString(0);
				value = data.getString(1);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp = this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_KEY_VALUE, 0);
			SharedPreferences.Editor edit = sp.edit();
			edit.putString(key, value);
			edit.commit();
		} else if (action.equals(getValue)) {
			String key = null;
			try {
				key = data.getString(0);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp = this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_KEY_VALUE, 0);
			String value = sp.getString(key, null);
			pluginResult = new PluginResult(PluginResult.Status.OK, value);
		} else if (action.equals(deleteKey)) {
			String key = null;
			try {
				key = data.getString(0);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp = this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_KEY_VALUE, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.commit();
			pluginResult = new PluginResult(PluginResult.Status.OK);
		} else if (action.equals(setEncryptedKeyValue)) {
			String key = null;
			String value = null;
			try {
				key = data.getString(0);
				value = data.getString(1);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			String encryptedKey = null;
			String encryptedValue = null;
			try {
				encryptedKey = SimpleCrypto.encrypt(masterPassword, key);
				encryptedValue = SimpleCrypto.encrypt(masterPassword, value);
			} catch (Exception e) {
				pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp = this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_ENCRYPTED_KEY_VALUE, 0);
			SharedPreferences.Editor edit = sp.edit();
			edit.putString(encryptedKey, encryptedValue);
			edit.commit();
		} else if (action.equals(getEncryptedValue)) {
			String key = null;
			try {
				key = data.getString(0);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			String encryptedKey = null;
			try {
				encryptedKey = SimpleCrypto.encrypt(masterPassword, key);
			} catch (Exception e) {
				pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp =  this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_ENCRYPTED_KEY_VALUE, 0);
			String encryptedValue = sp.getString(encryptedKey, null);
			if (encryptedValue == null) {
				pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return pluginResult;
			}
			String value = null;
			try {
				value = SimpleCrypto.decrypt(masterPassword, encryptedValue);
			} catch (Exception e) {
				pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return pluginResult;
			}
			pluginResult = new PluginResult(PluginResult.Status.OK, value);
		} else if (action.equals(deleteEncryptedKey)) {
			String key = null;
			try {
				key = data.getString(0);
			} catch (JSONException jsonException) {
				pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
				return pluginResult;
			}
			String encryptedKey = null;
			try {
				encryptedKey = SimpleCrypto.encrypt(masterPassword, key);
			} catch (Exception e) {
				pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
				return pluginResult;
			}
			SharedPreferences sp =  this.ctx.getApplicationContext().getSharedPreferences(DISCOVER_OPEN_ENCRYPTED_KEY_VALUE, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(encryptedKey);
			editor.commit();
		} else {
			pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
		}

		return pluginResult;
	}

	/**
	 * Usage:
	 * <pre>
	 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
	 * ...
	 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
	 * </pre>
	 * @author ferenc.hechler
	 */
	public static class SimpleCrypto {

		public static String encrypt(String seed, String cleartext) throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = encrypt(rawKey, cleartext.getBytes());
			return toHex(result);
		}

		public static String decrypt(String seed, String encrypted) throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] enc = toByte(encrypted);
			byte[] result = decrypt(rawKey, enc);
			return new String(result);
		}

		private static byte[] getRawKey(byte[] seed) throws Exception {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return raw;
		}


		private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(clear);
			return encrypted;
		}

		private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal(encrypted);
			return decrypted;
		}

		public static String toHex(String txt) {
			return toHex(txt.getBytes());
		}
		public static String fromHex(String hex) {
			return new String(toByte(hex));
		}

		public static byte[] toByte(String hexString) {
			int len = hexString.length()/2;
			byte[] result = new byte[len];
			for (int i = 0; i < len; i++)
				result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
			return result;
		}

		public static String toHex(byte[] buf) {
			if (buf == null)
				return "";
			StringBuffer result = new StringBuffer(2*buf.length);
			for (int i = 0; i < buf.length; i++) {
				appendHex(result, buf[i]);
			}
			return result.toString();
		}
		private final static String HEX = "0123456789ABCDEF";
		private static void appendHex(StringBuffer sb, byte b) {
			sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
		}

	}

}
