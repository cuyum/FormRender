package ar.com.cuyum.cnc.exceptions;

import org.apache.log4j.Logger;

public class ExceptionValidation extends Exception {

	private static final long serialVersionUID = -2978804027998533379L;
	public static Logger log = Logger.getLogger(ExceptionValidation.class);
	
	public ExceptionValidation(String msj){
		super(msj);
	}
}
