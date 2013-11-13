package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grid implements Serializable {

	private static final long serialVersionUID = 4270231775586991982L;

	private Boolean sumarizada;
	private Boolean calculados;
	private Boolean ingresados;
	
	private List<String> agrupadores;
	private List<String> totalizadores;

	public Boolean getSumarizada() {
		return sumarizada;
	}

	public void setSumarizada(Boolean sumarizada) {
		this.sumarizada = sumarizada;
	}

	public Boolean getCalculados() {
		return calculados;
	}

	public void setCalculados(Boolean calculados) {
		this.calculados = calculados;
	}

	public List<String> getAgrupadores() {
		return agrupadores;
	}

	public void setAgrupadores(List<String> agrupadores) {
		this.agrupadores = agrupadores;
	}

	public List<String> getTotalizadores() {
		return totalizadores;
	}

	public void setTotalizadores(List<String> totalizadores) {
		this.totalizadores = totalizadores;
	}

	public ObjectNode valuesToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode grid = mapper.createObjectNode();

		try {
			grid = (ObjectNode) mapper
					.readTree(mapper.writeValueAsString(this));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return grid;
	}

	public Boolean getIngresados() {
		return ingresados;
	}

	public void setIngresados(Boolean ingresados) {
		this.ingresados = ingresados;
	}

}
