package product.clicklabs.jugnoo.driver.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.String;import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

	public static String encryptWithPublicKeyStr(String data) {

		String toReturn = "";
		try {

			String pemKey =
					"-----BEGIN PUBLIC KEY-----\n" +
							"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyBaIeT2jT3t3+ybE7bGG\n" +
							"HOi+xspzV35eKhkj25FMdAGBw9KnfVgv7xIgLNPizQOWfNJLvyXbk3CSRzZZlnLI\n" +
							"dhX0zWCB3F7RaWXxpEiBtPBDOyrxAubGDDhds0kY+N5J4cQRGEUZ/6n+QmwBAMJJ\n" +
							"6Ks0RfQGDaAFJ+WQq2upOb2VfYSB/ChCq1nilkWtsecJF8UNm6S8JJVRa4o5MiCk\n" +
							"gBu11Id3t/qpdva0ZzpVyJRByXtIJs9Rk1kBUzqzhVTDLCRL7BPDVj6vm71gycPD\n" +
							"3TM7AH4P4nqPfmeUnkMEBjHbAKhX/R/yDAJYv7gmXrLRcS76a6Ick/mfJ995Ej6Z\n" +
							"xwIDAQAB\n" +
							"-----END PUBLIC KEY-----";

			pemKey = pemKey.replace("-----BEGIN PUBLIC KEY-----", "");
			pemKey = pemKey.replace("-----END PUBLIC KEY-----", "");



			byte[] message = new byte[0];
			message = data.getBytes("UTF-8");

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] publicKeyBytes = Base64.decode(pemKey.getBytes("UTF-8"), Base64.DEFAULT);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
			PublicKey apiPublicKey = keyFactory.generatePublic(x509KeySpec);

			Cipher rsaCipher = Cipher.getInstance("RSA/NONE/PKCS1Padding"); //RSA/NONE/OAEPPadding   RSA/none/PKCS1Padding
			rsaCipher.init(Cipher.ENCRYPT_MODE, apiPublicKey);
			byte[] encodedBytes = rsaCipher.doFinal(message);

			toReturn = Base64.encodeToString(encodedBytes, Base64.DEFAULT);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

}
