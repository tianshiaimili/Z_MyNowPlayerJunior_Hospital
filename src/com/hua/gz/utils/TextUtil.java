package com.hua.gz.utils;

public class TextUtil {
	public static boolean isBlank(String text) {
		if (text == null || text.length() == 0)
			return true;
		return false;
	}
}
