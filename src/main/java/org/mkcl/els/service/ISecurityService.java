package org.mkcl.els.service;

public interface ISecurityService {
	
	public String getEncodedPassword(String password);
	
	public boolean isAuthenticated(String enteredPassword, String storedPassword);

}
