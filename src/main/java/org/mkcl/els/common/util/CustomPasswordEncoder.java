package org.mkcl.els.common.util;

import java.security.SecureRandom;

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
		CustomParameter csptEncryptionRequired = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PASSWORD_ENCRYTPTION_REQUIRED, "");
		if(csptEncryptionRequired!=null && csptEncryptionRequired.getValue()!=null && csptEncryptionRequired.getValue().equals(ApplicationConstants.PASSWORD_ENCRYTPTION_REQUIRED_VALUE)) {
			boolean isAuthenticated = false;
			isAuthenticated = super.matches(rawPassword, encodedPassword);
			if(!isAuthenticated) {
				CustomParameter csptSupportPassword = CustomParameter.findByName(CustomParameter.class, "SUPPORT_PASSWORD", "");
				if(csptSupportPassword!=null && csptSupportPassword.getValue()!=null) {
					isAuthenticated = super.matches(rawPassword, csptSupportPassword.getValue());
				}
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
	
	
	
}
