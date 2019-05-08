package Generators;

import java.util.UUID;

public class TokenGenerator {

	/**
	 * Generate new login Token
	 * 
	 * @return send back generated token
	 */
	public StringBuilder GenerateToken() {
		StringBuilder token = new StringBuilder();
		for (int count = 0; count < 1; count++) {
			token.append(UUID.randomUUID().toString());

		}

		return token;
	}
}
