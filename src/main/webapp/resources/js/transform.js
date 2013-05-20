var setupHint = function(field){
	var hint = $(field).siblings("[class^='jr-hint']");
	if(hint){
		var fieldName = $(field).attr("name");
		/*tooltipo selector*/
		var itemSelector = "[name~='"+fieldName+"']";
		
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
	
	$(form).validate();
	
	/*
	 * Para repeat se necesitará duplicar todo lo que se encuentre dentro del fieldset.jr-repeat
	 * actualmente se está pasando un listado de campos al setup de validaciones y se están buscando 
	 * globalmente los campos por nombres. Se deberá pasar los campos de un fieldset específico y las
	 * búsquedas internas deberán dejar de hacerse globalmente y deberán buscarse dentro del fieldset
	 * suministrado. 
	 */
	var fields = [];
	if($("fieldset.jr-repeat").length>0){
		var fsR = $("fieldset.jr-repeat");
		var pfs = fsR.parent();
		console.info("Se Encuentra el fieldset.jr-repeat",fsR);
		var fsR1 = fsR.clone();
		fsR1.attr("name",fsR.attr("name")+"_1");
		fsR1.appendTo(pfs);
		console.log();
		/*busqueda de campos dentro de repeat*/
//		var fields = fsR1.find("[name]").not("fieldset");
	}else{
		console.info("No se encuentra el fieldset.jr-repeat");
		/*busqueda global de campos*/
		fields = $("[name]").not("fieldset");
	}
	
	/*
	 * si van a venir las instancias de repeticiones tengo que tenerlas en cuenta
	 * e iterarlas antes de iterar los campos
	 */
	for ( var i = 0; i < fields.length; i++) {
		var field = fields[i];
		setupHint(field);
		setupValidations(field);
	}
});
