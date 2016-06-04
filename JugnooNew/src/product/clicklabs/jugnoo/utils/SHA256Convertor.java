package product.clicklabs.jugnoo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Convertor {

	public static String getSHA256String(String token) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(token.getBytes());

		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
		 sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
	
}
