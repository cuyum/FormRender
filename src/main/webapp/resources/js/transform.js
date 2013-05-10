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

var setupValidations = function(field){
	var f = $(field);
	var form = $(document.forms[0]);
	if(f.attr("required") != undefined){
		var fieldName = f.attr("name");
		
		var isRequired = f.attr("required");
		if(isRequired){
			f.rules( "add", {
				required:true,
				messages:{
					required : function(){
						var type = f.attr("type");
						if(type=="radio" || type==undefined){
							if(f.is("textarea")) return "Debe ingresar un valor";
							return "Debe elegir una opci&oacute;n";
						}else{
							return "El campo es requerido";
						}
					}
				}
			});
		}
		
		var data_type = f.attr("data-type-xml");
		if(data_type && data_type=="int"){
			f.rules( "add", {
				entero:true
				,messages:{
					number: "Debe ser un valor num&eacute;ico v&aacute;lido"
				}
			});
		}else if(data_type && data_type=="decimal"){
			f.rules( "add", {
				decimal:true
				,messages:{
					number: "Debe ser un valor num&eacute;ico v&aacute;lido"
				}
			});
		}
		
		var data_constraints = f.attr("data-constraint");
		if(data_constraints){
			var constraints = data_constraints.split("and");
			for ( var i = 0; i < constraints.length; i++) {
				var constraint = constraints[i];
				constraint = constraint.trim();
				console.log(constraint);
				
				if(constraint.indexOf("or")!=-1){/*check for ors and add to constraints*/
					var sc = constraint.split("or");
					console.log("encontré un or en "+fieldName,sc);
				}
				
				/*process the constraint*/
				if(constraint.indexOf(".<")!=-1){/*max*/
					try {
						var max = new Number(constraint.substring(2,constraint.length)); 
						f.rules( "add", {
							max: max
							,messages:{
								max: "Debe ser un valor menor a {0}"
							}
						});
					} catch (e) {
						log.error("Not a number",constraint.substring(2,constraint.length));
					}
				}
				if(constraint.indexOf(".>")!=-1){/*min*/
					try {
						var min = new Number(constraint.substring(2,constraint.length)); 
						f.rules( "add", {
							min: min
							,messages:{
								min: "Debe ser un valor mayor a {0}"
							}
						});
					} catch (e) {
						log.error("Not a number",constraint.substring(2,constraint.length));
					}
				}
			}
		}
	}
};

var setupValidationDefaults = function(){
	$.validator.setDefaults({
		debug: true,
		success: "valid",
		submitHandler: function() {
			alert("Formulario Enviado!");
		}
	});
	
	$.validator.addMethod("entero", 
		function(value, element) { 
//			return this.optional(element) || /^(-)?[0-9]*$/.test(value); 
			return /^(-)?[0-9]*$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero entero");
	
	$.validator.addMethod("decimal", 
		function(value, element) { 
//			return this.optional(element) || /^\s*-?(\d+(\.\d{1,2})?|\.\d{1,2})\s*$/.test(value); 
			return /^\s*-?(\d+(\.\d{2}){1})\s*$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero decimal con dos cifras luego del punto");
};

var validator;

$(document).ready(function() {
	var form = document.forms[0];
	
	setupValidationDefaults();
	
	$(form).validate();
	
	var fields = $("[name]").not("fieldset");

	for ( var i = 0; i < fields.length; i++) {
		var field = fields[i];
		setupHint(field);
		setupValidations(field);
	}
});
