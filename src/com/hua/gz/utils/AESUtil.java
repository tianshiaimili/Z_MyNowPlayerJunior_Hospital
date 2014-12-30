package com.hua.gz.utils;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Log;

/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */

public class AESUtil {
	
	private static final String DEFAULT_ALGORITHM = "AES";
	private static final String DEFAULT_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final int KEY_SIZE = 128;
	public static final String CRYPT_KEY = "MoovOnAndroidTag";
	private static final String TAG = AESUtil.class.getSimpleName();

	private static byte[] dencrypt(byte[] src, byte[] seed, int mode) {
		try {
			// Declare KeyGenerator provider explicitly here in case Android change default provider someday.
			KeyGenerator kgen = KeyGenerator.getInstance(DEFAULT_ALGORITHM, "BC");
			// Android 4.2 has modified the default implementations of SecureRandom and Cipher.RSA to use OpenSSL.
			// original implementations is Crypto.
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");    
	        sr.setSeed(seed); 
			kgen.init(KEY_SIZE, sr);
			SecretKey aesKey = kgen.generateKey();
			Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
			cipher.init(mode, aesKey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			Log.e(TAG, "AES dencrypt failed.", e);
			return null;
		}
	}
	
	public static byte[] encrypt(byte[] toBeEncrypted, byte[] seed) {
		return dencrypt(toBeEncrypted, seed, Cipher.ENCRYPT_MODE);
	}
	
	public static byte[] decrypt(byte[] encrypted, byte[] seed) {
		return dencrypt(encrypted, seed, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * 二行制转十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	
	public final static String decrypt(String data) {
		if(data == null)
			return null;
		return new String(decrypt(hex2byte(data.getBytes()), CRYPT_KEY.getBytes()));
	}

	public final static String encrypt(String data) {
		if(data == null || data.equals(""))
			return null;
		return byte2hex(encrypt(data.getBytes(), CRYPT_KEY.getBytes()));
	}
	
}
