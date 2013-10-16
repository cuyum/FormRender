package ar.com.cuyum.cnc.exceptions;

import org.apache.log4j.Logger;

public class ExceptionParserJson extends Exception {

	private static final long serialVersionUID = -4935007031185670272L;
	public static Logger log = Logger.getLogger(ExceptionParserJson.class);
	
	public ExceptionParserJson(String msj){
		super(msj);
	}
}
