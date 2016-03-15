package cike.sqasystem.util;

import java.io.UnsupportedEncodingException;

public class UtoCN {
	
	
	/**用于把Unicode流转换成字符串
	 * @param ucd Unicode流(/u...)
	 * @return
	 */
	public static String UcdtoString(String ucd) {
		String str=ucd; 
		try {
			str = new String(str.getBytes("Unicode"),"UTF-16");
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
