package ar.com.cuyum.cnc.exceptions;

import org.apache.log4j.Logger;

public class ExceptionComboRelayUrl extends Exception {

	private static final long serialVersionUID = -2978804027998533379L;
	public static Logger log = Logger.getLogger(ExceptionValidation.class);
	
	public ExceptionComboRelayUrl(String msj){
		super(msj);
	}
}
