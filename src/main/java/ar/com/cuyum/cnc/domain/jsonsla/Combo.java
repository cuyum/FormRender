package ar.com.cuyum.cnc.domain.jsonsla;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.cuyum.cnc.exceptions.ExceptionComboRelayUrl;
import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

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
	
	protected void setRelayService(RelayService relayService) {
		this.relayService = relayService;		
	}

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
	
	public String findPadre(String name){
		
		String padre="";
			
		if(name.equals("partido"))
			padre="provincia";
		if(name.equals("localidad"))
			padre="partido";
		if(name.equals("area"))
			padre="localidad";
		if(name.equals("area2"))
			padre="localidad";
		
		return padre;
	}

	@Override
	public Boolean isDataValid(String name) throws ExceptionValidation {
		
		//si el campo es obligatorio el valor no puede ser nulo pero ya eso se evalua con el jsonschema 
		if(value == null) return null;
		
		if (cuit != null && cuit) {
			log.info("validando si cuit ok");
			if (!Componente.validCuit(value.getId())) {
				throw new ExceptionValidation("formato de cuit inválido");
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
				valuesFromURL = getValuesFromUrl(relayService,fkey,url,name);
			} catch (ExceptionComboRelayUrl e) {
				throw new ExceptionValidation("Servicio no disponible, "+e.getMessage());
			}
			boolean isValidId = false;
			
			for (Item item_ : valuesFromURL){
				if (item_.getId().equals(value.getId())){
					value.setText(item_.getText());
					isValidId = true;
				}
			}
			if (!isValidId){
				StringBuilder list= new StringBuilder();
				String coma="";
				for(int i=0,n=valuesFromURL.size();i<n;i++){
					Item item = valuesFromURL.get(i);
					log.info("("+item.getId()+","+item.getText()+")");
					list.append(coma+"("+item.getId()+","+item.getText()+")");
					coma=",";
				}
				throw new ExceptionValidation(
						" ("+value.getId()+","+value.getText()+") es invalido, no se corresponde con " + findPadre(name) + " ingresado/a " );				
			}			
		}else if (values.size()>1){
			if (!values.contains(value)){
				log.info("validando si el dato pertenece a la lista del combo");
				throw new ExceptionValidation(
						"valor inválido ("+value.getId()+","+value.getText()+") para el combo");
			}	
		}
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		if(!value.has("value") || !value.has("label"))  throw new ExceptionValidation("Json inválido para combo");
		this.value = Item.getItemFromJson(value);  
	}

	@Override
	public JsonNode valueToJson() {	   
	   return (value==null)?null:value.toJson();
	}

	@Override
	public String getValueToString() {
		if (value!=null) return value.getLabel();
		return null;
	}

	@Override
	public String getType() {		
		return Componente.COMBO;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}


}
