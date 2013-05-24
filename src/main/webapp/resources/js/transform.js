var setupHint = function(field, fieldset){
	var hint = $(field).siblings("[class^='jr-hint']");
	if(hint){
		var fieldName = $(field).attr("name");
		/*tooltipo selector*/
		var itemSelector;
		if(fieldset){
			itemSelector = "fieldset[name~='"+fieldset.name+"'] [name~='"+fieldName+"']";
		}else{
			itemSelector = "[name~='"+fieldName+"']";
		}
		
		//FIXME: arreglar para radios y textareas
//		var fieldType = $(field).attr("type");
//		if(fieldType != "undefined" && fieldType == "radio"){
//			itemSelector = "[data-itext-id~='"+fieldName+":label']";
//		}
		$(field).tooltip({
			content:hint.text(),
			 items: itemSelector
		});
	}
};

var validator;

$(document).ready(function() {
	var form = document.forms[0];
	
	setupValidationDefaults();
	
	/*
	 * Para repeat se necesitará duplicar todo lo que se encuentre dentro del fieldset.jr-repeat
	 * actualmente se está pasando un listado de campos al setup de validaciones y se están buscando 
	 * globalmente los campos por nombres. Se deberá pasar los campos de un fieldset específico y las
	 * búsquedas internas deberán dejar de hacerse globalmente y deberán buscarse dentro del fieldset
	 * suministrado. 
	 */
	
	var fields = [];
	var fieldsets = [];
	var repeat =$("fieldset.jr-repeat") ;
	var repeatCount = getURLParameter("repeat");
	
	if(repeat.length>0 && repeatCount>0){
		var fs = $("fieldset.jr-repeat");
		var pfs = fs.parent();
		
		/* iteracion normal, siguientes instancias de repeats */
		for ( var i = 0; i < repeatCount; i++) {
			var fieldset = {};
			fieldset.instance = i;
			/* Duplicacion de campos */
			var fsRepeat = fs.clone();
			
			/* logica de diferenciacion */
			var repeatName = fs.attr("name")+"_"+i;
			fsRepeat.attr("name",repeatName);
			fieldset.name = repeatName;
			
			/*identificacion de variable de repeticion*/
			var rv = fsRepeat.find("fieldset[class~='variable'] h4 span");
			if(rv){
				var rvName = rv.text();
				if(rvName && rvName.trim()!="" && rvName.indexOf("{")!=-1){
					rvName = rvName.replace("{","").replace("}","");
					var varVal = getURLParameter(rvName);
					console.log(varVal);
					if(varVal!=null && varVal!=undefined){
						rv.text(varVal.split(",")[i]);
					} 
				}
			}
			
			/* agregado de campos a grupo padre */
			fsRepeat.appendTo(pfs);
			fieldset.dom = fsRepeat;
			
			/* agregado al listado de campos para logica de validacion */
			var repeatFields = fsRepeat.find("[name]").not("fieldset");
			fieldset.fields = repeatFields;
			fieldsets.push(fieldset);
		}
		fs.remove();
		
		$(form).validate();
		
		/*for each repeat instance*/
		for ( var i = 0; i < fieldsets.length; i++) {
			/*for each field in instance*/
			var fieldset = fieldsets[i];
			for ( var j = 0; j < fieldset.fields.length; j++) {
				var field = fieldset.fields[j];
				setupHint(field,fieldset);
				setupValidations(field,fieldset);
			}
		}
		
	}else{
		$(form).validate();
		
		console.info("No se encuentra el fieldset.jr-repeat");
		/*busqueda global de campos*/
		fields = $("[name]").not("fieldset");
		/*
		 * si van a venir las instancias de repeticiones tengo que tenerlas en cuenta
		 * e iterarlas antes de iterar los campos
		 */
		for ( var i = 0; i < fields.length; i++) {
			var field = fields[i];
			setupHint(field);
			setupValidations(field);
		}
	}
	
});
