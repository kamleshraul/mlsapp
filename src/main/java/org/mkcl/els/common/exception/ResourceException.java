package org.mkcl.els.common.exception;

public class ResourceException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8246543443741032428L;	
	
	public ResourceException() {
		
	}
	
	/**
     * Constructor for ResourceException.
     *
     * @param message exception message
     */
    public ResourceException(final String message) {
        super(message);
    }

}
