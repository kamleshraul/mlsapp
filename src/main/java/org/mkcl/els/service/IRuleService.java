package org.mkcl.els.service;

import java.util.List;
import java.util.Map;

/**
 * Interface to the Business Rules in the application.
 * All the interactions with the Rules Engine must happen through
 * this interface.
 */
public interface IRuleService {

	public List<String> fireStateLessRules(Map<String, String> properties);
	
}
