package ar.com.cuyum.cnc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;


@SuppressWarnings("serial")
@ApplicationScoped
@Named("formRenderProperties")
public class FormRenderProperties extends Properties {

	public static final String DEFAULT_FILE_NAME = "formrender.properties";
	
	private static final String SERVER_CONF_URL = System.getProperty("jboss.server.config.dir");
	
	static Logger log = Logger.getLogger(FormRenderProperties.class);

	@PostConstruct
	protected void init() {

		log.info("PROPIEDADES DEL SISTEMA CARGADAS");

		try {
			File externalFile = getExternalConfig();
			InputStream elInput=null;
			if (externalFile==null) {
				log.error("No se encontr\u00F3 archivo de configuraci\u00F3n "+DEFAULT_FILE_NAME+ " en "+SERVER_CONF_URL+". Se usar\u00E1 el que se incluye en el paquete");
				elInput  = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME);
			} else {
				log.info("Usando archivo de configuraci\u00F3n "+externalFile.getAbsolutePath());
				elInput = new FileInputStream(externalFile);
			}
			this.load(elInput);
		} catch (Exception e) {
			log.error("No puedo cargar el arhivo properties "+ DEFAULT_FILE_NAME);
		}
	}

	private File getExternalConfig() {
		if (SERVER_CONF_URL == null)
			return null;
		File ret = new File(SERVER_CONF_URL, DEFAULT_FILE_NAME);

		if (!ret.exists())
			return null;

		return ret;

	}
	
	public String getDestinationXml(){
		if(!this.isEmpty())
			return get("xmlForms.destination");
		else return null;
	}
	
	public String getRemoteListHost(){
		if(!this.isEmpty()){
			String rlh = get("list.remote.host");
			String context = get("list.remote.context");
			String port = get("list.remote.port");
			String secure = get("list.remote.secure");
			//remove trailing slashes
			if(rlh!=null && !rlh.isEmpty() && rlh.charAt(rlh.length()-1)=='/'){
				rlh = rlh.substring(0, rlh.length()-1);
			}
			if(context!=null && !context.isEmpty() && context.length()>1 && context.charAt(context.length()-1)=='/'){
				context = context.substring(0, context.length()-1);
			}
			if(rlh!=null && rlh.indexOf("http")==-1){
				if(secure!= null && "true".equalsIgnoreCase(secure)){
					rlh = "https://"+rlh;
					port = "443";
					port = null;
				}else{
					rlh = "http://"+rlh;
				}
			}
			if(port!=null && !"80".equals(port)){
				rlh = rlh+":"+port;
			}
			if(context!=null && !"/".equals(context)){
				//FIXME: El contexto viene en el servicio que solicita el relay
//				rlh = rlh + context;
			}
			return rlh; 
			
		}else return null;
	}
	
	public String getRemoteSubmissionHost(){
		if(!this.isEmpty()){
			String rsh = get("submit.remote.host");
			String context = get("submit.remote.context");
			String finalUrl = get("submit.remote.final");
			String port = get("submit.remote.port");
			String secure = get("submit.remote.secure");
			//remove trailing slashes
			if(rsh!=null && !rsh.isEmpty() && rsh.charAt(rsh.length()-1)=='/'){
				rsh = rsh.substring(0, rsh.length()-1);
			}
			if(context!=null && !context.isEmpty() && context.length()>1 && context.charAt(context.length()-1)=='/'){
				context = context.substring(0, context.length()-1);
			}
			
			if(rsh!=null && rsh.indexOf("http")==-1){
				if(secure != null && "true".equalsIgnoreCase(secure)){
					rsh = "https://"+rsh;
					port = "443";
					port = null;
				}else{
					rsh = "http://"+rsh;
				}
			}
			if(port!=null && !"80".equals(port)){
				rsh = rsh+":"+port;
			}
			if(context!=null && !"/".equals(context)){
				//FIXME: El contexto de la url de guardado viene en el formulario complementando el Dominio aportado por formrender
				context += finalUrl!=null && !finalUrl.isEmpty()?finalUrl:"";
				//rsh = rsh+context;
			}
			return rsh;
		}else return null;
	}
	
	public String getRemoteDraftHost(){
		if(!this.isEmpty()){
			String rsh = get("submit.remote.host");
			String context = get("submit.remote.context");
			String draftUrl = get("submit.remote.draft");
			String port = get("submit.remote.port");
			String secure = get("submit.remote.secure");
			//remove trailing slashes
			if(rsh!=null && !rsh.isEmpty() && rsh.charAt(rsh.length()-1)=='/'){
				rsh = rsh.substring(0, rsh.length()-1);
			}
			if(context!=null && !context.isEmpty() && context.length()>1 && context.charAt(context.length()-1)=='/'){
				context = context.substring(0, context.length()-1);
			}
			
			if(rsh!=null && rsh.indexOf("http")==-1){
				if(secure != null && "true".equalsIgnoreCase(secure)){
					rsh = "https://"+rsh;
					port = "443";
					port = null;
				}else{
					rsh = "http://"+rsh;
				}
			}
			if(port!=null && !"80".equals(port)){
				rsh = rsh+":"+port;
			}
			if(context!=null && !"/".equals(context)){
				context += draftUrl!=null && !draftUrl.isEmpty() && !draftUrl.trim().equals("/")?draftUrl:"";
				rsh = rsh+context;
			}
			return rsh;
		}else return null;
	}
	
	private String get(String key){
		return this.getProperty(key);
	}
}