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
		console.log(f.attr("name") + " es requerido");
		var fieldName = f.attr("name");
		
		if(f.attr("required")){
			console.log(f.attr("name")+" is required");
			f.rules( "add", {
				required:true,
				messages:{
					required : function(){
						var type = f.attr("type");
						if(type=="radio" || type==undefined){
							if(f.is("textarea")) return "Debe ingresar un valor";
							return "Debe elegir una opción";
						}else{
							return "El campo es requerido";
						}
					}
				}
			});
		}
		
		var data_type = f.attr("data-type-xml");
		if(data_type && data_type=="int"){
			console.log(fieldName+" is integer");
			f.rules( "add", {
				number:true
				,messages:{
					number : "El valor debe ser entero"
				}
			});
		}else if(data_type && data_type=="decimal"){
			console.log(fieldName+" is float");
			f.rules( "add", {
				number:true
				,messages:{
					number : "El valor debe ser decimal"
				}
			});
		}
	}
};

var validator;

$(document).ready(function() {
	var form = document.forms[0];
	$.validator.setDefaults({
		debug: true,
		success: "valid"
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
			return /^\s*-?(\d+(\.\d{1,2})?|\.\d{1,2})\s*$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero decimal con dos cifras luego del punto");
	
	$(form).validate({
		submitHandler: function() {
			alert("Formulario Enviado!");
		}
	});
	
	var fields = $("[name]").not("fieldset");

	for ( var i = 0; i < fields.length; i++) {
		var field = fields[i];
		setupHint(field);
		setupValidations(field);
	}
});
