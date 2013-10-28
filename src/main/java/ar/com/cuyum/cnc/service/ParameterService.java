/**
 * 
 */
package ar.com.cuyum.cnc.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Clase creada manejo transaccional de los par&aacute;metros .<br />
 * 
 * @author Jorge Morando
 * 
 */
@Stateless
public class ParameterService {

	public transient Logger log = Logger.getLogger(ParameterService.class);

	@Inject
	EntityManager em;

	public String getHour() {
		return "";

	}

}
