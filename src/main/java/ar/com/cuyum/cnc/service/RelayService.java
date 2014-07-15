/**
 * 
 */
package ar.com.cuyum.cnc.service;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.utils.FormRenderProperties;
//import ar.com.cuyum.cnc.domain.jsonsla.Item;
//import ar.com.cuyum.cnc.utils.FormRenderProperties;
import ar.com.cuyum.cnc.utils.JsonUtils;








//import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Clase creada para llamada de servicios de listas externas.<br />
 * Principalmente creada para evitar limitaciones por CSRF.
 * 
 * @author Jorge Morando
 * 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class RelayService {

	@Inject
	private JsonUtils jsonUtils;

	@Inject
	private FormRenderProperties frp;

	public transient static Logger log = Logger.getLogger(RelayService.class);

	private HttpClient client = new DefaultHttpClient();
	
	@Inject
	private CacheManager cacheManager;
	

	@PostConstruct
	public void init() {
		if (!cacheManager.cacheExists("listasExternasCache")) {
			cacheManager.addCache("listasExternasCache");
		}
		setupSSLContextNew();
	}
	/* ========================SSL========================= */
	

	private void setupSSLContextNew() {
		SSLContext ctx;
		try {
			client = new DefaultHttpClient();
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	/* ================ POINT OF ENTRANCE ================= */

	private Cache getCache() {
		
		return cacheManager.getCache("listasExternasCache");
	}
	
	public String submit(URL remoteUrl, String data) {
		//setupSSLContext();
		return performSubmission(remoteUrl, data);
	}

	public String massiveSubmit(URL remoteUrl, String data,
			HttpServletRequest request) {
		//setupSSLContext();
		long inicio = System.currentTimeMillis();
		JsonNode success = jsonUtils.proccessDataValidation(data, request, this, frp);
		if (!success.get("success").asBoolean())
			return success.toString();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataForm = null;
		try {
			dataForm = mapper.readTree(success.get("msg").asText());
		} catch (JsonProcessingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

		String id = dataForm.get("id").asText();

		ArrayNode listDataForm = (ArrayNode) dataForm.get("listDataForm");
		long finValidacion= System.currentTimeMillis();
		if (listDataForm == null)
			return JsonUtils.msg(false,
					"Lista de datos nulla, error desconocido").toString();
		
		String ret = performMassiveSubmission(remoteUrl, id, listDataForm, request);
		long finSubmision= System.currentTimeMillis();
		
		long tiempoValidacion = (finValidacion - inicio)/1000;
		long tiempoSubmision = (finSubmision - finValidacion)/1000;
		long total = tiempoSubmision+tiempoValidacion;
		log.info("TIEMPO VALIDACION (Segundos): "+tiempoValidacion);
		log.info("TIEMPO SUBMISION (Segundos): "+tiempoSubmision);
		log.info("TIEMPO TOTAL (Segundos): "+total);
		return ret;
	}

	public String retrieve(URL remoteUrl) {
		//setupSSLContext();
		return performRetrieval(remoteUrl);
	}

	public String request(URL remoteUrl, String fkey, String tipo) {
		//setupSSLContext();
		return performRequest(remoteUrl, fkey, tipo);
	}
	
	
	public String requestWithCache(URL remoteUrl, String fkey, String tipo) {
		String key = "POST "+remoteUrl.toString()+"?tipo="+tipo+"&fkey="+fkey;
		log.info("Ejecutando con CACHE: " + key);
		if (getCache().isKeyInCache(key)) {
			Element elcache = getCache().get(key);
			if (!elcache.isExpired()) {
				log.info("IN CACHE: " + key);
				return (String)getCache().get(key).getObjectValue();
			}
			log.info("CACHE EXPIRED: " + key);
		}
		log.info("NOT IN CACHE: " + key);
		String ret =  performRequest(remoteUrl, fkey, tipo);
		getCache().put(new Element(key, ret));
		return ret;
	}
	
	

	private String performSubmission(URL url, String data) {
		HttpPost request = buildSubmission(url, data);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}

	private ObjectNode formData(String id, ObjectNode form) {
		ArrayNode data = (ArrayNode) form.get("data");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode formOut = mapper.createObjectNode();
		ObjectNode header = mapper.createObjectNode();
		ObjectNode payload = mapper.createObjectNode();
		ObjectNode formulario = mapper.createObjectNode();
		header.put("callback_id", "");
		formulario.put("id", id);
		if(form.has("recordId")){
			String recordId = form.get("recordId").asText();
			header.put("id", recordId);
//			formulario.put("recordId",recordId);
		}
		formulario.put("data", data);
		payload.put("formulario", formulario);
		formOut.put("header", header);
		formOut.put("payload", payload);
		return formOut;
	}

	private String performMassiveSubmission(URL url, String idForm,
			ArrayNode form, HttpServletRequest request) {

		ObjectMapper mapper = new ObjectMapper();

		ObjectNode respon = mapper.createObjectNode();

		for (int i = 0, n = form.size(); i < n; i++) {
			ObjectNode dataNode =  (ObjectNode) form.get(i);
			ObjectNode formData = formData(idForm, dataNode);
			log.info("Persistiendo:" + formData);

			HttpPost requestPost = buildSubmission(url, formData.toString());
			HttpResponse rawResponse = execute(requestPost);

			JsonNode response = null, result = null;

			try {
				
					response = mapper.readTree(processResponse(rawResponse));
				
			}catch (JsonProcessingException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			}

			try {
				result = mapper.readTree(response.get("result").toString());
			} catch(NullPointerException exp){
				log.error(exp);
			}catch (JsonProcessingException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			}

			if(response==null)
				respon.put("Error con servidor de persistencia - Contacte a su administrador", result);
			else if (!response.get("success").asBoolean()) {
					respon.put("Formulario-" + i + "", result);				
					} else {
						respon.put("Formulario-" + i + "", result);
					}
		}

		return JsonUtils.msg(true, respon.toString()).toString();
	}

	private String performRetrieval(URL url) {
		HttpGet request = buildRetrieval(url);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}

	private String performRequest(URL url, String fkey, String tipo) {
		HttpPost request = buildRequest(url, fkey, tipo);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}

	/* =============== HTTP REQUEST====================== */

	private final HttpPost buildSubmission(URL url, String data) {
		HttpPost method = null;
		try {
			method = new HttpPost(url.toURI());
			method.setEntity(new StringEntity(data,
					ContentType.APPLICATION_JSON));
		} catch (Exception e) {
			log.error("No se pudo construir el request", e);
		}
		return method;
	}

	private final HttpGet buildRetrieval(URL url) {
		HttpGet method = null;
		try {
			method = new HttpGet(url.toURI());
		} catch (Exception e) {
			log.error("No se pudo construir el request", e);
		}
		return method;
	}

	private final HttpPost buildRequest(URL url, String fkey, String tipo) {
		HttpPost method = null;
		try {
			method = new HttpPost(url.toURI());

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tipo", tipo));
			params.add(new BasicNameValuePair("fkey", fkey));

			method.setEntity(new UrlEncodedFormEntity(params));

		} catch (Exception e) {
			log.error("No se pudo construir el request", e);
		}
		return method;
	}

	private HttpResponse execute(HttpRequestBase method) {
		HttpResponse resp = null;
		try {
			log.info("Ejecutando: " + method);
			resp = client.execute(method);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return resp;
	}

	private final String processResponse(HttpResponse rawResponse) {
		String entity = "{\"success\":false,\"msg\":\"Empty response received.\"}";
		if (rawResponse == null){
			return entity;
		}
		try {
			entity = EntityUtils.toString(rawResponse.getEntity());
		} catch (Exception e) {
			
			log.error(e);
		}
		return entity;
	}

	public FormRenderProperties getFormRenderProperties() {
		return frp;
	}

}
