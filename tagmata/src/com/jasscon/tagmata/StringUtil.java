package com.jasscon.tagmata;

public class StringUtil {
	
	public static String substring(String str, int length) {
		return str.substring(0, length > str.length() ? str.length() : length);
	}
}
