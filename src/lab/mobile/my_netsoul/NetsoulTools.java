package lab.mobile.my_netsoul;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import android.util.Log;

public class NetsoulTools {
	public static String gen_hash_auth(String rand_hash, String host_client, String port_client, String pass) {
		String msg = new String(rand_hash + "-" + host_client + "/" + port_client + pass);
		Log.d("NetsoulTools", msg);
		
		byte[] uniqueKey = msg.getBytes();
		byte[] hash      = null;

		try {
			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		
		StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++)
		{
		        String hex = Integer.toHexString(hash[i]);
		        if (hex.length() == 1)
		        {
		                hashString.append('0');
		                hashString.append(hex.charAt(hex.length() - 1));
		        }
		        else
		                hashString.append(hex.substring(hex.length() - 2));
		}
		
		return hashString.toString();
	}
	
	public static String url_encode(String str) {
		String res = new String();
//		str.replaceAll(" ", "%20");
//		str.replace(" ", "%20");
		if (str == null)
			return "null";
		try {
			res = URLEncoder.encode(str, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			Log.e("Url_encode", "UnsupportedEncodingException; " + e.getMessage());
			e.printStackTrace();
		}
		res = res.replaceAll(Pattern.quote("+"), "%20");
		Log.d("Url_encode", res);
		return res;
	}

	public static String url_decode(String string) {
		String res = new String();
		if (string == null)
			return "null";
		try {
			res = URLDecoder.decode(string, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			Log.e("Url_decode", "UnsupportedEncodingException; " + e.getMessage());
			e.printStackTrace();
		}
		Log.d("Url_decode", res);
		return res;
	}
}
