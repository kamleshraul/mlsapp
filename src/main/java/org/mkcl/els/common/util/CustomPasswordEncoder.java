package org.mkcl.els.common.util;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

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
		//TODO://decrypt rawPassword here
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
	
	
	
}
