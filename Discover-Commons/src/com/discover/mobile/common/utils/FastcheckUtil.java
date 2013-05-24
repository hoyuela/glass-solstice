package com.discover.mobile.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;

public class FastcheckUtil {
	
	private static final String DISCOVER_FS_TOKEN_FILENAME = "discoverFSToken";
	private static final String DISCOVER_FS_DATA_FILENAME = "discoverFSData";
	private static final String SEED4KEY = "EoeBcwiahT5CxacQsaWI0e16p9lb+wQs0KFJdmzSTEo=";

	public static String getFastcheckAuthString(String token) {
        String concatenatedCreds = "fastcheck" + ": :" + token;
        return concatenatedCreds;
    }
	
	private static byte[] getKey() throws Exception {
		byte[] seedBytes = SEED4KEY.getBytes();
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// Note: need to add the second parameter for this to work on Android 4.2 or greater.
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		sr.setSeed(seedBytes);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] keyBytes = skey.getEncoded();
		return keyBytes;
	}
	
	public static void storeFastcheckToken(Context context, String token) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(DISCOVER_FS_TOKEN_FILENAME, Context.MODE_PRIVATE)));
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if (writer != null) {
		    	try {
		    		writer.close();
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	}
		    }
		}
	}
		
	public static String readFastcheckToken(Context context) {
		BufferedReader input = null;
		StringBuffer buffer = null;
		try {
			input = new BufferedReader(new InputStreamReader(context.openFileInput(DISCOVER_FS_TOKEN_FILENAME)));
		    String line;
		    buffer = new StringBuffer();
		    while ((line = input.readLine()) != null) {
		    	buffer.append(line);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		  if (input != null) {
			  try {
				  input.close();
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
		   }
		 }
		return buffer.toString();
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
	
	public static String decrypt(String encryptedStr)throws Exception {
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
