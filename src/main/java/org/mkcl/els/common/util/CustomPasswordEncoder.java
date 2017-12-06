package org.mkcl.els.common.util;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomPasswordEncoder extends BCryptPasswordEncoder {

	public CustomPasswordEncoder() {
		super();
	}

	public CustomPasswordEncoder(int strength, SecureRandom random) {
		super(strength, random);
	}

	public CustomPasswordEncoder(int strength) {
		super(strength);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder#matches(java.lang.CharSequence, java.lang.String)
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if(rawPassword==null) {
			rawPassword = "";
		}
		if(encodedPassword==null) {
			encodedPassword = "";
		}		
		/** decrypt client side encrypted value of raw password **/
		CustomParameter csptClientSideEncryptionRequired = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CLIENTSIDE_PASSWORD_ENCRYTPTION_REQUIRED, "");
		if(csptClientSideEncryptionRequired!=null && csptClientSideEncryptionRequired.getValue()!=null && csptClientSideEncryptionRequired.getValue().equals(ApplicationConstants.CLIENTSIDE_PASSWORD_ENCRYTPTION_REQUIRED_VALUE)) {
			rawPassword = this.decryptClientSidePassword(rawPassword.toString());
		}		
		
		CustomParameter csptEncryptionRequired = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PASSWORD_ENCRYTPTION_REQUIRED, "");
		if(csptEncryptionRequired!=null && csptEncryptionRequired.getValue()!=null && csptEncryptionRequired.getValue().equals(ApplicationConstants.PASSWORD_ENCRYTPTION_REQUIRED_VALUE)) {
			boolean isAuthenticated = false;
			isAuthenticated = super.matches(rawPassword, encodedPassword);
			System.out.println("original match: " + isAuthenticated);
			if(!isAuthenticated) {
				List<Credential> sptCredentials = Credential.findAllCredentialsByRole("SUPPORT");
				if(sptCredentials!=null && !sptCredentials.isEmpty()) {
					for(Credential cr: sptCredentials) {
						if(cr.isEnabled() && cr.getPasswordChangeCount()>1 && DateUtil.compareDatePartOnly(cr.getPasswordChangeDateTime(), new Date())==0) {
							isAuthenticated = super.matches(rawPassword, cr.getPassword());
							System.out.println("support match: " + isAuthenticated);
							if(isAuthenticated) {
								break;
							}
						}
					}
				}
//				CustomParameter csptSupportPassword = CustomParameter.findByName(CustomParameter.class, "SUPPORT_PASSWORD", "");
//				if(csptSupportPassword!=null && csptSupportPassword.getValue()!=null) {
//					isAuthenticated = super.matches(rawPassword, csptSupportPassword.getValue());
//					System.out.println("support match: " + isAuthenticated);
//				}
			}
			return isAuthenticated;
		} else {
			return rawPassword.equals(encodedPassword);
		}

		
	}

	//============================Old Encoder with both encrypt as well as decrypt available======================
//	int chrsz   = 8;
//	@Override
//	public String encode(CharSequence rawPassword) {
//		StringBuffer retVal = new StringBuffer();		
//				
//		return retVal.toString();
//	}
//
//	@Override
//	public boolean matches(CharSequence rawPassword, String encodedPassword) {
////		String dbPassword = encode(encodedPassword);
////		if(decode(rawPassword.toString()).equals(decode(dbPassword))){
////			return true;
////		}
//		if(rawPassword.toString().equals(encodedPassword.toString())) {
//			return true;
//		}
//		return false;
//	}
//
//	private String decode(String encodedPassword){
//		StringBuffer retVal = new StringBuffer();
//		
//		
//		return retVal.toString();
//	}
	
	private String decryptClientSidePassword(final String clientSidePassword) {
		String decryptedClientSidePassword = "";
		String secretKey = "";
		CustomParameter cpSecretKey = CustomParameter.findByName(CustomParameter.class, "SECRET_KEY_FOR_ENCRYPTION", "");
        if(cpSecretKey != null){
        	secretKey = cpSecretKey.getValue();
        }
		byte[] cipherData = DatatypeConverter.parseBase64Binary(clientSidePassword.toString()); //Base64.getDecoder().decode(rawPassword);
		byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[][] keyAndIV = null;
		try {
			keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secretKey.getBytes("UTF-8"), md5);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
		IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

		byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
		Cipher aesCBC = null;
		try {
			aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] decryptedData = null;
		try {
			decryptedData = aesCBC.doFinal(encrypted);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String decryptedText = new String(decryptedData, "UTF-8");	
			decryptedClientSidePassword = decryptedText;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedClientSidePassword;
	}
	
	/**
	 * Generates a key and an initialization vector (IV) with the given salt and password.
	 * <p>
	 * This method is equivalent to OpenSSL's EVP_BytesToKey function
	 * (see https://github.com/openssl/openssl/blob/master/crypto/evp/evp_key.c).
	 * By default, OpenSSL uses a single iteration, MD5 as the algorithm and UTF-8 encoded password data.
	 * </p>
	 * @param keyLength the length of the generated key (in bytes)
	 * @param ivLength the length of the generated IV (in bytes)
	 * @param iterations the number of digestion rounds 
	 * @param salt the salt data (8 bytes of data or <code>null</code>)
	 * @param password the password data (optional)
	 * @param md the message digest algorithm to use
	 * @return an two-element array with the generated key and IV
	 */
	private static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

	    int digestLength = md.getDigestLength();
	    int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
	    byte[] generatedData = new byte[requiredLength];
	    int generatedLength = 0;

	    try {
	        md.reset();

	        // Repeat process until sufficient data has been generated
	        while (generatedLength < keyLength + ivLength) {

	            // Digest data (last digest if available, password data, salt if available)
	            if (generatedLength > 0)
	                md.update(generatedData, generatedLength - digestLength, digestLength);
	            md.update(password);
	            if (salt != null)
	                md.update(salt, 0, 8);
	            md.digest(generatedData, generatedLength, digestLength);

	            // additional rounds
	            for (int i = 1; i < iterations; i++) {
	                md.update(generatedData, generatedLength, digestLength);
	                md.digest(generatedData, generatedLength, digestLength);
	            }

	            generatedLength += digestLength;
	        }

	        // Copy key and IV into separate byte arrays
	        byte[][] result = new byte[2][];
	        result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
	        if (ivLength > 0)
	            result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

	        return result;

	    } catch (DigestException e) {
	        throw new RuntimeException(e);

	    } finally {
	        // Clean out temporary data
	        Arrays.fill(generatedData, (byte)0);
	    }
	} 
	
}
