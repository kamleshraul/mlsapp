package org.mkcl.els.common.exception;

import java.util.HashMap;
import java.util.Map;

public class ELSException extends Exception {

	/**
	 * Serialization Id
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public ELSException() {
		super();
	}

	public ELSException(String key, String value) {
		parameters.put(key, value);
	}
	
	synchronized public String getParameter(){
		String value = null;
		for(String key : parameters.keySet()){
			value = parameters.get(key);
			break;
		}
		return value;	
	}
	
	synchronized public String getParameter(String key){
		return parameters.get(key);				
	}
	
	synchronized public void setParameter(String key, String value){
		parameters.put(key, value);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
}
