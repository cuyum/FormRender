package ar.com.cuyum.cnc.exceptions;

@SuppressWarnings("serial")
public class TechnicalException extends RuntimeException {

	public TechnicalException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public TechnicalException(Throwable cause) {
		super(cause);
		
	}
	
	public TechnicalException(String message) {
		super(message);
		
	}	
	
}
