package ar.com.cuyum.cnc.exceptions;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBException;
import javax.el.ELException;
import javax.enterprise.context.BusyConversationException;
import javax.enterprise.context.NonexistentConversationException;
import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.resource.spi.SecurityException;

import org.apache.log4j.Logger;

public class ExceptionHandler extends ExceptionHandlerWrapper {
	
	public static Logger log = Logger.getLogger(ExceptionHandler.class);
	
	private final javax.faces.context.ExceptionHandler wrapped;

	public ExceptionHandler(final javax.faces.context.ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		boolean foundedMessage=false; 
		
		for (final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
			Throwable t = it.next().getContext().getException();
			while ((t instanceof FacesException || t instanceof EJBException || t instanceof ELException) && t.getCause() != null) {
				t =	t.getCause();
			}

			final FacesContext facesContext = FacesContext.getCurrentInstance();				
			final ExternalContext externalContext = facesContext.getExternalContext();
			final Map<String, Object> requestMap = externalContext.getRequestMap();
			try {				
				String message="";
				if (t instanceof SecurityException || t.getCause() instanceof SecurityException) {
					Throwable secException = t.getCause();
					foundedMessage = true;
					if (null != secException && secException instanceof SecurityException){							
						message = secException.getMessage();
						requestMap.put("errorMsg", message);							
					}
					if (secException == null){
						message = t.getMessage();
						requestMap.put("errorMsg", message);
					}
					
					try {
						log.info("Redirecting ../errorFinSesion.xhtml");
						externalContext.redirect("../errorFinSesion.xhtml");
					} catch (IOException e) {
						log.info("Redirecting ../errorFinSesion.xhtml not encontrada!");
						foundedMessage = false;								
					}							
											
				} else if (t instanceof NonexistentConversationException || t instanceof BusyConversationException) {
					foundedMessage = true;
					message="Fin Tiempo Alta/Modificacion.";
					requestMap.put("errorMsg", message);
					try {
						log.info("Redirecting ../errorTimeOut.xhtml");
						externalContext.redirect("../errorTimeOut.xhtml");
					} catch (IOException e) {
						log.info("Redirecting ../errorTimeOut.xhtml not encontrada!");
						foundedMessage = false;								
					}
				} 
				else if (t instanceof ViewExpiredException) {
					foundedMessage = true;
					message="Tiempo Vista expirada.";
					requestMap.put("errorMsg", message);
					try {
						log.info("Redirecting ../errorFinSesion.xhtml");
						externalContext.redirect("../errorFinSesion.xhtml");
					} catch (IOException e) {
						log.info("Redirecting ../errorFinSesion.xhtml not encontrada!");
						foundedMessage = false;								
					}
				}
				
				if (!foundedMessage){
					message = t.getMessage();
					requestMap.put("errorMsg", message);
					log.error(message);
					try {
						log.info("Redirecting ../error.xhtml");
						externalContext.redirect("../error.xhtml");
					} catch (IOException e) {
						log.info("Redirecting ../error.xhtml not encontrada!");
					}
				}	
				facesContext.responseComplete();
				return;
			} finally {					
				log.info("Removing exception");
				it.remove();					
			}
	   }
	   getWrapped().handle();
	 }

}
