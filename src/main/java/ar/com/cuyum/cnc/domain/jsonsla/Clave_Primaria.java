package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
public class Clave_Primaria implements Serializable {

	private static final long serialVersionUID = 4270231775586991982L;
	
	private List<String> claves_primarias;

	public List<String> getClaves_primarias() {
		return claves_primarias;
	}

	public void setClaves_primarias(String claves_primarias) {

		List<String> lst = new ArrayList<String>();
		if (!claves_primarias.equals("[]")) {

			String pk = claves_primarias.replaceAll("\"", "");
			for (int i = 0; i < pk.length(); i++) {

				if (pk.charAt(i) == '[') {
					if (pk.indexOf(',') != -1)
						lst.add(pk.substring(i + 1, pk.indexOf(',')));
					else
						lst.add(pk.substring(i + 1, pk.indexOf(']')));
				} else if (pk.charAt(i) == ',') {
					if (pk.indexOf(',', i + 1) != -1)
						lst.add(pk.substring(i + 1, pk.indexOf(',', i + 1)));
					else
						lst.add(pk.substring(i + 1, pk.indexOf(']')));
				}
			}
		}
		this.claves_primarias = lst;
	}

	public ObjectNode valuesToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode clave_primaria = mapper.createObjectNode();

		try {
			clave_primaria = (ObjectNode) mapper
					.readTree(mapper.writeValueAsString(this));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clave_primaria;
	}
}
