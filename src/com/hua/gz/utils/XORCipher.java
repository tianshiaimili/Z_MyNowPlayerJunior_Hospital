package com.hua.gz.utils;
/** 
* 此方法根据异或方法实现加密与解密的过程 
*/ 
//package com.cainwise.util; 

import java.io.UnsupportedEncodingException; 

/**
 * 
 * @author Situ 2012-07-02
 *
 */
public class XORCipher { 

	/**
	 * Simple symmetric cipher (encrypt/decrypt) method
	 */
    public static void transcode(byte[] data, byte[] key){
    	int keyIndex = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (data[i] ^ key[keyIndex]);
			if (++keyIndex == key.length) {
				keyIndex = 0;
			}
		}
    }
    
    //将一个字符串与一个字节进行计算，生成一个新字符串. 
    private static String pass(byte b,String str){ 
        byte[] ee; 
        try { 
            ee = str.getBytes("ISO-8859-1"); 
            for(int i = 0 ;i<ee.length;i++){ 
                ee[i] = (byte)(ee[i] ^ b); 
            } 
            return new String(ee,"ISO-8859-1"); 
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace(); 
            return ""; 
        } 
        
    } 
    
   /**
    * encrypt "data" with "key". each data byte will XOR key.length times.
    */
    public static String encrypt(String data, String key){ 
        String tcode = data; 
        for(int i=0;i<key.length();i++){ 
            byte b = (byte)key.charAt(i); 
            tcode = pass(b,tcode); 
        } 
        return tcode; 
    } 
    
   /**
    * decrypt "data" with "key". each data byte will XOR key.length times.
    */
    public static String decrypt(String data, String key){ 
        String tcode = data; 
        for(int i=key.length();i > 0;i--){ 
            byte b = (byte)key.charAt(i-1); 
            tcode = pass(b,tcode); 
        } 
        return tcode; 
    } 

} 