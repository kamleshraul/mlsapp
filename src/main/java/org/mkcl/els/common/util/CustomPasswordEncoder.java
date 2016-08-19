package org.mkcl.els.common.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder{

	int chrsz   = 8;
	@Override
	public String encode(CharSequence rawPassword) {
		StringBuffer retVal = new StringBuffer();
		
		for(int i = 0; i < rawPassword.length(); i++){
			char c = rawPassword.charAt(i);
			int ch = c;
			ch = ch ^ 128;
			retVal.append(((char)ch));
		}
				
		return retVal.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
//		String dbPassword = encode(encodedPassword);
//		if(decode(rawPassword.toString()).equals(decode(dbPassword))){
//			return true;
//		}
		if(rawPassword.toString().equals(encodedPassword.toString())) {
			return true;
		}
		return false;
	}

	private String decode(String encodedPassword){
		StringBuffer retVal = new StringBuffer();
		for(char c : encodedPassword.toCharArray()){
			int ch = c;
			ch = ch ^ 128;
			retVal.append(((char)ch));
		}
		
		return retVal.toString();
	}
	
}
