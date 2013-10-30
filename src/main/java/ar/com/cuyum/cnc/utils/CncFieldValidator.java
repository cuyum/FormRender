package ar.com.cuyum.cnc.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import ar.com.cuyum.cnc.exceptions.ExceptionParserJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 *
 */

public class CncFieldValidator {

	public transient static Logger log = Logger
			.getLogger(CncFieldValidator.class);

	 /**
	 * 
	 * @param node bind del xml
	 * @return un nodo json con los parametros de los constrains
	 * @throws ExceptionParserJson
	 */
	public static ObjectNode setConstraints(Node node,String type) throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode constraintResult = mapper.createObjectNode();
		Node nodeConstraint = node.getAttributes().getNamedItem("constraint");
		if (nodeConstraint == null)
			return null;

		List<String> constraints = Arrays.asList(nodeConstraint.getNodeValue()
				.split(" and "));

		for (int i = 0, n = constraints.size(); i < n; i++) {
			String constraint = constraints.get(i);
			if (constraints.get(i).indexOf(" or ") != -1) {
				List<String> cc = Arrays.asList(constraints.get(i)
						.split(" or "));
				constraint = cc.get(0);
				constraints.set(i, constraint);
				cc.remove(0);
				if (cc.size() > 0)
					constraints.addAll(cc);
			}
			if (constraint.indexOf(".<=") != -1
					|| constraint.indexOf(".&lt;=") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".<=") + 3, constraint.length());
				// minimum es equivalente a <= , a menos que se especifique la
				// propiedad
				// exclusiveMinimum=true
				if(type.equals("integer")){
					constraintResult.put("maximum",new Integer(number));
				}else if(type.equals("decimal")){
					constraintResult.put("maximum",new Double(number));	
				}
			} else if (constraint.indexOf(".>=") != -1
					|| constraint.indexOf(".&gt;=") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".>=") + 3, constraint.length());
				// maximum es equivalente a >= a menos que se especifique la
				// propiedad
				// exclusiveMaximum=true
				if(type.equals("integer")){
					constraintResult.put("minimum",new Integer(number));
				}else if(type.equals("decimal")){
					BigDecimal bd = new BigDecimal(number);
					bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
					constraintResult.put("minimum",bd);	
				}
			} else if (constraint.indexOf(".<") != -1
					|| constraint.indexOf(".&lt;") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".<") + 2, constraint.length());
				if(type.equals("integer")){
					constraintResult.put("minimum",new Integer(number));
				}else if(type.equals("decimal")){
					BigDecimal bd = new BigDecimal(number);
					constraintResult.put("minimum",bd);	
				}
				constraintResult.put("exclusiveMinimum", true);
			} else if (constraint.indexOf(".>") != -1
					|| constraint.indexOf(".&gt;") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".>") + 2, constraint.length());
				if(type.equals("integer")){
					constraintResult.put("maximum",new Integer(number));
				}else if(type.equals("decimal")){
					constraintResult.put("maximum",new Double(number));	
				}
				constraintResult.put("exclusiveMaximum", true);
			} else if (constraint.indexOf("depends=") != -1) {
				String[] depends = constraint.split("=");
				constraintResult
						.put(depends[0],
								depends[1].split("/")[depends[1].split("/").length - 1]);
			} else if (constraint.indexOf("url=") != -1) {
				String[] url = constraint.split("=");
				constraintResult.put(url[0], url[1]);
			} else if (constraint.indexOf("cuit") != -1) {
				constraintResult.put("cuit", true);
			} else if (constraint.indexOf("mask=") != -1) {
				String[] mask = constraint.split("=");
				constraintResult.put(mask[0], mask[1]);
			} else {
				throw new ExceptionParserJson(
						"Constraint propertie no reconocido");
			}
		}

		return constraintResult;

	}

}
