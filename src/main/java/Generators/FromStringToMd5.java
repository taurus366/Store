package Generators;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FromStringToMd5 {

	/**
	 * convert to MD5 from sessionID or password
	 * 
	 * @param text it can be converted from session ID or password to MD5
	 * @return send back converted text(MD5)
	 * @throws NoSuchAlgorithmException
	 */
	public String GenerateMd5(String text) throws NoSuchAlgorithmException {

		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashInBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));

		StringBuilder sb = new StringBuilder();
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));

		}
		return sb.toString();
	}
}
