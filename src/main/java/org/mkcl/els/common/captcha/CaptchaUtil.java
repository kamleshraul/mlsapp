package org.mkcl.els.common.captcha;

import java.util.Random;

public class CaptchaUtil {
	public static String generateCaptchaTextMethod1() {

		Random rdm = new Random();
		int rl = rdm.nextInt(); // Random numbers are generated.
		String hash1 = Integer.toHexString(rl); // Random numbers are converted to Hexa Decimal.

		return hash1;

	}

	public static String generateCaptchaTextMethod2(int captchaLength) {

		String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuffer captchaStrBuffer = new StringBuffer();
		java.util.Random rnd = new java.util.Random();

		// build a random captchaLength chars salt
		while (captchaStrBuffer.length() < captchaLength) {
			int index = (int) (rnd.nextFloat() * saltChars.length());
			captchaStrBuffer.append(saltChars.substring(index, index + 1));
		}

		return captchaStrBuffer.toString();

	}

	public static String generateCaptchaExpression() {

		String captchaStrBuffer = new String();
		int n1, n2;
		Random rn = new Random();
		int range = 9 - 0 + 1;
		n1 = rn.nextInt(range) + 0;
		n2 = rn.nextInt(range) + 0;
		captchaStrBuffer = n1 + "+" + n2;
		return captchaStrBuffer;

	}
}
