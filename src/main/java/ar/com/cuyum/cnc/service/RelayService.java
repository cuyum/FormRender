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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

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

/**
 * Clase creada para llamada de servicios de listas externas.<br />
 * Principalmente creada para evitar limitaciones por CSRF.
 * @author Jorge Morando
 * 
 */
@Stateless
public class RelayService {
	
	@Inject
	private JsonUtils jsonUtils;
	
    @Inject
    private FormRenderProperties frp;
	
	public transient static Logger log = Logger.getLogger(RelayService.class);

	private HttpClient client = new DefaultHttpClient();
	
	/*========================SSL=========================*/
	
	private void setupSSLContext() {
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("TLS");
			 X509TrustManager tm = new X509TrustManager() {
		            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		 
		            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		 
		            public X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		        };
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        client = new DefaultHttpClient(ccm, client.getParams()); 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}
	
	/*================ POINT OF ENTRANCE =================*/
	
	public String submit(URL remoteUrl, String data){
		setupSSLContext();
		return performSubmission(remoteUrl,data);
	}
	
	public String massiveSubmit(URL remoteUrl, String data, HttpServletRequest request){
		setupSSLContext();
		
		JsonNode success = jsonUtils.proccessDataValidation(data, request, this,frp);
		if (!success.get("success").asBoolean()) return success.toString();
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode listData = null;
		try {			
			listData = mapper.readTree(success.get("msg").toString());
		} catch (JsonProcessingException e) {	
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		
		return JsonUtils.msg(true,"ok").toString();
		
		//return performMassiveSubmission(remoteUrl,listData,request);
	}
	
	public String retrieve(URL remoteUrl){
		setupSSLContext();
		return performRetrieval(remoteUrl);
	}
	
	public String request(URL remoteUrl, String fkey){
		setupSSLContext();
		return performRequest(remoteUrl,fkey);
	}

	private String performSubmission(URL url,String data){
		HttpPost request = buildSubmission(url,data);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}
	
	 private String performMassiveSubmission(URL url,ArrayNode data, HttpServletRequest request){

		StringBuilder respon = new StringBuilder();
		 
		for (int i = 0, n = data.size(); i < n; i++) {
				JsonNode item = data.get(i);			
				HttpPost requestPost = buildSubmission(url,item.toString());
				HttpResponse rawResponse = execute(requestPost);
				respon.append(processResponse(rawResponse));
		}
	
		return JsonUtils.msg(true,respon.toString()).toString();
	}
	
	private String performRetrieval(URL url){
		HttpGet request = buildRetrieval(url);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}
	
	private String performRequest(URL url,String fkey){
		HttpPost request = buildRequest(url,fkey);
		HttpResponse rawResponse = execute(request);
		String responseStr = processResponse(rawResponse);
		return responseStr;
	}
	
	/*=============== HTTP REQUEST======================*/
	
	private final HttpPost buildSubmission(URL url,String data) {
		HttpPost method = null;
		try {
			method = new HttpPost(url.toURI());
			method.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
		} catch (Exception e) {
			log.error("No se pudo construir el request",e);
		}
		return method;
	}
	
	private final HttpGet buildRetrieval(URL url) {
		HttpGet method = null;
		try {
			method = new HttpGet(url.toURI());
		} catch (Exception e) {
			log.error("No se pudo construir el request",e);
		}
		return method;
	}
	
	private final HttpPost buildRequest(URL url,String fkey) {
		HttpPost method = null;
		try {
			method = new HttpPost(url.toURI());
			
			List <NameValuePair> params = new ArrayList <NameValuePair>();
		    params.add(new BasicNameValuePair("fkey", fkey));

		    method.setEntity(new UrlEncodedFormEntity(params));
			
		} catch (Exception e) {
			log.error("No se pudo construir el request",e);
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
		if(rawResponse==null)return entity;
		try {
			entity = EntityUtils.toString(rawResponse.getEntity());
		} catch (Exception e) {
			log.error(e);
		}
		return entity;
	}
	
	public FormRenderProperties getFormRenderProperties(){
		return frp;
	}

}

