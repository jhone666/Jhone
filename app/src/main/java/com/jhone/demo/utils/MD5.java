package com.jhone.demo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对外提供getMD5(String)方法
 * 
 * @author randyjia
 * 
 */
public class MD5 {

	/**
	 * 获取加密后的密码，加密方式为 md5（md5（password）+salt)
	 * 
	 * @return
	 */
	public static String getMD5String(String src, String mixCode) {
		return getMD5(getMD5(src) + mixCode);
	}

	public static String getMD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
}
