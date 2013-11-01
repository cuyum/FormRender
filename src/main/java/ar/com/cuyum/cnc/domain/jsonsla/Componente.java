package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.utils.CncFieldValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Componente implements Serializable {
	public transient static Logger log = Logger
			.getLogger(CncFieldValidator.class);

	private static final long serialVersionUID = 6076612513788995386L;

	protected Map<String, Componente> listComponets = new HashMap<String, Componente>();

	abstract public Boolean isDataValid() throws ExceptionValidation;

	abstract public void setValueFromJson(JsonNode value)
			throws ExceptionValidation;

	abstract public JsonNode valueToJson();

	protected void setAllComponet(Map<String, Componente> listComponets) {
		this.listComponets = listComponets;
	};

	// propiedades comunes a todos los componentes

	protected String ref;

	protected String id;

	protected String title;

	protected String appearance;

	protected String hint;

	protected String label;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRef() {
		return ref;
	}

	@JsonProperty("$ref")
	public void setRef(String ref) {
		this.ref = ref;
	}

	public boolean equals(Componente o) {
		if (!(o instanceof Componente))
			return false;
		return (ref == (((Componente) o).ref) && id == (((Componente) o).id)
				&& title == (((Componente) o).title)
				&& appearance == (((Componente) o).appearance)
				&& hint == (((Componente) o).hint) && label == (((Componente) o).label));
	}

	public String toString() {
		return "type = " + getType() + " ref =" + ref
				+ ((id != null) ? " id =" + id : "")
				+ ((title != null) ? " title =" + title : "")
				+ ((appearance != null) ? " appearance =" + appearance : "")
				+ ((hint != null) ? " hint =" + hint : "")
				+ ((label != null) ? " label =" + label : "");
	}

	public String getType() {
		return ref.split("#/definitions/")[1];
	}

	protected Boolean isOkRelevant(Map<String, List<String>> relevantMap)
			throws ExceptionValidation {
		Iterator<Entry<String, List<String>>> rel = relevantMap.entrySet()
				.iterator();
		while (rel.hasNext()) {
			Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) rel
					.next();
			String msg = "valor de " + e.getKey() + "=" + e.getValue()
					+ " y el valor del objeto es null";
			Componente componete = this.listComponets.get(e.getKey());
			if ("combo".equals(componete.getType())) {
				if (e.getValue().equals(((Combo) componete).getValue().getId())) {
					throw new ExceptionValidation(msg);
				}
			} else if ("integer".equals(componete.getType())) {
				Integer val = ((Entero) componete).getValue();
				if (e.getValue().equals(val.toString())) {
					throw new ExceptionValidation(msg);
				}
			} else if ("decimal".equals(componete.getType())) {
				BigDecimal dec = ((Decimal) componete).getValue();
				if (e.getValue().equals(dec.toString())) {
					throw new ExceptionValidation(msg);
				}
			} else if ("string".equals(componete.getType())) {
                String text = ((Texto) componete).getValue();
                if (e.getValue().equals(text)) {
					throw new ExceptionValidation(msg);
				}
			}

		}
		return true;
	}

}
