package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.cuyum.cnc.exceptions.ExceptionComboRelayUrl;
import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 
 * @author ltroconis
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Combo extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;

	private String url;
	private String depends;
	private Boolean cuit;
	private RelayService relayService;

	private List<Item> values = new ArrayList<Item>();

	private Item value;

	public String toString() {
		return super.toString()
				+((value==null)?" value = null":" value = ("+value.getId()+","+value.getText()+")") 
				+" url:"
				+ url
				+ ((cuit != null) ? " cuit:" + cuit : "")
				+ " depends:"
				+ depends
				+ ((values.size() > 0) ? " values size=" + values.size()
						+ " values=" + valuesToString() : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof Combo) || o == null)
			return false;

		return (super.equals(((Componente) o)) && url == ((Combo) o).url && depends == ((Combo) o).depends)
				&& values.containsAll(((Combo) o).values);
	}

	public void setRelayService(RelayService relayService) {
		this.relayService = relayService;		
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Item> getValues() {
		return values;
	}

	public void setValues(List<Item> values) {
		this.values = values;
	}

	public String getDepends() {
		return depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
	}

	public void setAddItems(List<Item> items) {
		this.values.addAll(items);
	}

	private String valuesToString() {
		StringBuilder entry = new StringBuilder();
		Iterator<Item> i = values.iterator();
		while (i.hasNext()) {
			Item val = i.next();
			entry.append("id=" + val.getId() + " txt=" + val.getText());
		}
		return entry.toString();
	}

	public Boolean getCuit() {
		return cuit;
	}

	public void setCuit(Boolean cuit) {
		this.cuit = cuit;
	}

	public Item getValue() {
		return value;
	}

	public void setValue(Item value) {
		this.value = value;
	}

	public Boolean validCuit(String value) {
		String inputString = value.toString();

		if (inputString.length() == 11) {
			String Caracters_1_2 = inputString.substring(0,2);
			if ("20".equals(Caracters_1_2) || "23".equals(Caracters_1_2)
					|| "24".equals(Caracters_1_2) || "27".equals(Caracters_1_2)
					|| "30".equals(Caracters_1_2) || "33".equals(Caracters_1_2)
					|| "34".equals(Caracters_1_2)) {
				int Count = inputString.charAt(0) * 5 + inputString.charAt(1)
						* 4 + inputString.charAt(2) * 3 + inputString.charAt(3)
						* 2 + inputString.charAt(4) * 7 + inputString.charAt(5)
						* 6 + inputString.charAt(6) * 5 + inputString.charAt(7)
						* 4 + inputString.charAt(8) * 3 + inputString.charAt(9)
						* 2 + inputString.charAt(10) * 1;
				double Division = Count / 11;
				if (Division == Math.floor(Division)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @author ltroconisanObject
	 * 
	 * @param url
	 *            remota
	 * @param fkey
	 *            valor de dependencia
	 * @return
	 * @throws ExceptionComboRelayUrl
	 */
     private List<Item> getValuesFromUrl(String fkey)
			throws ExceptionComboRelayUrl {
	
		if (relayService == null)
			throw new ExceptionComboRelayUrl(
					"No se puede cargar el combo a partir de la URL, el servicio no está disponible");
		FormRenderProperties frp = relayService.getFormRenderProperties();
		if (frp == null)
			throw new ExceptionComboRelayUrl(
					"No se puede cargar el combo a partir de la URL, propiedades no disponibles");
		ObjectMapper mapper = new ObjectMapper();
		List<Item> items = new ArrayList<Item>();
		String msg_error = "No se pudo generar la respuesta json en relay service ya que "
				+ "el servicio remoto ha respondido en un objeto JSON inv&aacute;lid";
		try {
			String remoteHost = frp.getRemoteListHost();
			URL urlRemote = new URL(remoteHost + url);
			String remoteResponse = relayService.request(urlRemote, fkey);					
			JsonNode remoteMsg = mapper.readTree(remoteResponse);
			ArrayNode result = (ArrayNode) remoteMsg.get("result");
			items = mapper.readValue(result.toString(),	new TypeReference<List<Item>>(){});			
		} catch (JsonParseException e) {
			log.error(msg_error, e);
		} catch (JsonMappingException e) {
			log.error(msg_error, e);
		} catch (JsonProcessingException e) {
			log.error(msg_error, e);
		} catch (IOException e) {
			log.error(msg_error, e);
		}

		return items;

	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		
		//si el campo es obligatorio el valor no puede ser nulo pero ya eso se evalua con el jsonschema 
		if(value == null) return null;
		
		if (cuit != null && cuit) {
			log.info("validando si cuit ok");
			if (!validCuit(value.getId())) {
				throw new ExceptionValidation("formato de cuit invalido");
			}
		}
		if (url != null) {
			log.info("validando si pertenece a la url");
			if (relayService == null)
				throw new ExceptionValidation(
						"Servicio remoto no disponible, para el combo");
			String fkey = null;
			if (depends != null) {
				if (this.listComponets == null)
					throw new ExceptionValidation(
							"No se tiene acceso al combo dependiente");
				Combo comboDepends = (Combo) this.listComponets.get(depends);
				fkey = comboDepends.getValue().getId();
			}
			List<Item> valuesFromURL = new ArrayList<Item>();
			try {
				valuesFromURL = getValuesFromUrl(fkey);
			} catch (ExceptionComboRelayUrl e) {
				throw new ExceptionValidation("Servicio no disponible, "+e.getMessage());
			}
			if (!valuesFromURL.contains(value)){
				StringBuilder list= new StringBuilder();
				String coma="";
				for(int i=0,n=valuesFromURL.size();i<n;i++){
					Item item = valuesFromURL.get(i);
					log.info("("+item.getId()+","+item.getText()+")");
					list.append(coma+"("+item.getId()+","+item.getText()+")");
					coma=",";
				}
				throw new ExceptionValidation(
						"valor invalido ("+value.getId()+","+value.getText()+") para el combo, segun la url remota con contenido:"+list.toString());
			}	
		}else if (values.size()>1){
			if (!values.contains(value)){
				log.info("validando si el dato pertenece a la lista del combo");
				throw new ExceptionValidation(
						"valor invalido ("+value.getId()+","+value.getText()+") para el combo");
			}	
		}
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		if(!value.has("value") || !value.has("label"))  throw new ExceptionValidation("Json invalido para combo");
		this.value = Item.getItemFromJson(value);  
	}

	@Override
	public JsonNode valueToJson() {	   
	   return (value==null)?null:value.toJson();
	}


}
