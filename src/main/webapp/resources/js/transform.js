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
				number: "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}else if(data_type && data_type=="decimal"){
		f.rules( "add", {
			decimal:true
			,messages:{
				number: "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
	
	var data_constraints = f.attr("data-constraint");
	if(data_constraints){
		var constraintContainer = [];
		var constraints = data_constraints.split(" and ");
		/* Process constraints of field */
		for ( var i = 0; i < constraints.length; i++) {
			var constraint = constraints[i];
			/*FIXME: refactor this or case scenario*/
			if(constraint.indexOf(" or ")!=-1){/*check for 'ors' and 'ands' to constraints*/
				var cc = constraint.split(" or ");
				constraint = cc[0];
				constraints[i] = constraint;
				cc.reverse();
				cc.pop();
				constraints = constraints.concat(cc);
			}
			
			if(constraint.indexOf(".<")!=-1){/*max*/
				var number = constraint.substring(constraint.indexOf(".<")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				try {
					var max = new Number(number);
					f.data("jr:constraint:max",max);
				} catch (e) {
					log.error("Not a number for max constraint",number);
				}
			}else if(constraint.indexOf(".>")!=-1){/*min*/
				var number = constraint.substring(constraint.indexOf(".>")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				try {
					var min = new Number(number); 
					f.data("jr:constraint:min",min);
				} catch (e) {
					log.error("Not a number for min constraint",number);
				}
			}else if(constraint.indexOf("depends=")!=-1){/*dependency*/
				var dependency = constraint.substring(constraint.indexOf("depends=")+8);
				f.data("jr:constraint:depends",dependency);
			}else if(constraint.indexOf(" url=")!=-1){/*remote combo data*/
				var url = constraint.substring(constraint.indexOf("url=")+4);
				f.data("jr:constraint:remote",url);
			}else if(constraint.indexOf("type=cuit")!=-1){/*remote combo data*/
				constraint.substring(constraint.indexOf("type=cuit")+11);
				f.data("jr:constraint:cuit","^[0-9]{2}[0-9]{8}[0-9]$");
			}else if(constraint.indexOf("mask=")!=-1){/*remote combo data*/
				var mask = constraint.substring(constraint.indexOf("mask=")+5);
				f.data("jr:constraint:mask",mask);
			}
			constraintContainer.push(constraint);
		}
		
		f.data("jr:constraints",constraintContainer);
		
		if(data_type && data_type=="decimal"){
			f.rules( "add", {
				decimal:true
				,messages:{
					number: "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		/* Add constraint rules in validation framework */
		if(f.data("jr:constraint:max")!=undefined){
			var max =  f.data("jr:constraint:max");
			f.rules( "add", {
				max: max
				,messages:{
					max: "Debe ser un valor menor a {0}",
					number: "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:min")!=undefined){
			var min =  f.data("jr:constraint:min");
			f.rules( "add", {
				min: min
				,messages:{
					min: "Debe ser un valor mayor a {0}",
					number: "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:mask")!=undefined){
			var mask =  f.data("jr:constraint:mask");
			f.mask(mask);
		}
		
		if(f.data("jr:constraint:remote")!=undefined){
			var url = f.data("jr:constraint:remote");
			
			if(f.data("jr:constraint:depends")!=undefined){
				var ancestor = $("[name~='"+f.data("jr:constraint:depends")+"']");
				console.log("Se encontró dependencia para el campo "+fieldName);
				ancestor.data("dependant",f);
				ancestor.on("change",function(){
					$.ajax({
					  url: "/FormRender/rest/service/relay",
					  type: "POST",
					  dataType: "json",
					  data:{
						  fkey:ancestor.val(),
						  remoteUrl:url
					  },
					  success : function(data, statusStr, xhr) {
						  var resetHierarchy = function(field){
							  if(field.is("select")){
								  var option0 = field.children("option[value~='0']");
								  var option = field.children("option[value~='']");
								  var op = option0.length>0?option0:option;
								  field.html("").append(op.val(""));
							  }
							  var dependant = field.data("dependant");
							  if(dependant)
								  resetHierarchy(dependant);
						  };
						  if(data.success){
							  resetHierarchy(f);
							  for ( var count = 0; count < data.result.length; count++) {
								  var option = data.result[count];
								  f.append('<option value='+ option.id + '>'+ option.nombre + '</option>');
							  }
						  }else{
							  console.error("No se obtuvo una lista de elementos para agregar al campo "+ fieldName );
						  }
						},
					  error:function(xhr,statusStr,errorStr){
						  console.error("Error tratando de recuperar valores para "+ fieldName);
					  }
					});
				});
			}else{
				$.ajax({
				  url: "/FormRender/rest/service/relay",
				  type: "POST",
				  dataType: "json",
				  data:{
					remoteUrl:url  
				  },
				  success : function(data, statusStr, xhr) {
					  if(data.success){
						  if(f.is("select")){
							  var option0 = f.children("option[value~='0']");
							  var option = f.children("option[value~='']");
							  var op = option0.length>0?option0:option;
							  f.html("").append(op.val(""));
						  }
						  for ( var count = 0; count < data.result.length; count++) {
							  var option = data.result[count];
							  f.append('<option value='+ option.id + '>'+ option.nombre + '</option>');
						  }
					  }else{
						  console.error("No se obtuvo una lista de elementos para agregar al campo "+ fieldName );
					  }
					},
				  error:function(xhr,statusStr,errorStr){
					  console.error("Error tratando de recuperar valores para "+ fieldName);
				  }
				});
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
			return /^\s*-?(\d+(\.\d{2}){0,1})\s*$/.test(value); 
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
