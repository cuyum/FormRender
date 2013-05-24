var validationRequired = function(field){
	var isRequired = field.attr("required");
	if(isRequired){
		if(field.is("select")){
			var option = field.children("option[value='0']");
			option.attr("value","");
		}
		field.rules( "add", {
			required:true,
			messages:{
				required : function(){
					var type = field.attr("type");
					if(type=="radio" || type==undefined){
						if(field.is("textarea")) return "Debe ingresar un valor";
						return "Debe elegir una opci&oacute;n";
					}else{
						return "El campo es requerido";
					}
				}
			}
		});
	}
};

var setupHint = function(field){
	var hint = field.siblings("[class^='jr-hint']");
	if(hint){
		var fieldName = field.attr("name");
		/*tooltipo selector*/
		itemSelector = "[name~='"+fieldName+"']";
		
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

var setupCalculate = function(field,fieldset){
	var fieldName = field.attr("name");
	console.log(fieldName);
	var data_calculate = field.attr("data-calculate");
	if(data_calculate && data_calculate.trim().length>0){
		console.log(fieldName,"calculate",field);
		if(data_calculate.indexOf("concat(")==-1){
			
			var totalFieldSpanSelector = "span.jr-output[data-value~='"+fieldName+"']";
			if(fieldset.instance!=undefined){
				totalFieldSpanSelector = "span.jr-output[data-value~='"+fieldName+"_"+fieldset.instance+"']";
			}
			
			var totalFieldSpan = $(totalFieldSpanSelector);
			var totalField = totalFieldSpan.closest("label").find("input"); 
			
			if(totalField){
				var updateTotal = function(sumFields,substractFields,fieldsetInstance){
					var total = 0;
					for ( var i = 0; i < sumFields.length; i++) {
						var fieldSelector = "input[name~='"+sumFields[i]+"']";
						
						if(fieldsetInstance!=undefined){
							fieldSelector = "input[name~='"+sumFields[i]+"_"+fieldsetInstance+"']";
						}
						
						var field = $(fieldSelector);
						
						if(!field)	return "Error: "+sumFields[i]+" inexistent";
						var n = new Number(field.val());
						if(isNaN(n)){
							var parsedNumber = cncToNumber(field.val());
							if(parsedNumber && !isNaN(parsedNumber)){
								total = total+parsedNumber;
							}
						}
						else
							total = total + n;
					}
					
					for ( var i = 0; i < substractFields.length; i++) {
						var fieldSelector = "input[name~='"+substractFields[i]+"']";
						if(fieldsetInstance!=undefined){
							fieldSelector = "input[name~='"+substractFields[i]+"_"+fieldsetInstance+"']";
						}
						
						var field = $(fieldSelector);
						
						if(!field) return "Error: "+substractFields[i]+" inexistent";
						var n = new Number(field.val());
						if(isNaN(n))
							console.error("Value of field "+substractFields[i] +" is not a number");
						else
							total = total - n;
					}
					return cncFromNumber(total);
				};
				
				totalField.data("updateTotal",updateTotal);
				
				var calculatedFields = data_calculate.split(" + ");
				var sum = new Array();
				var substract = new Array();
				for ( var i = 0; i < calculatedFields.length; i++) {
					var cfield = calculatedFields[i].trim();
					cfield = cfield.split(" - ");
					sum.push(cfield[0]);
					if(cfield.length>1){
						for ( var j = 1; j < cfield.length; j++) {
							substract.push(cfield[j]);
						}
					}
				}
				var cfields = new Array();
				cfields = cfields.concat(sum,substract);
				for ( var i = 0; i < cfields.length; i++) {
					var cfieldSelector = "input[name~='"+cfields[i]+"']";
					
					if(fieldset.instance!=undefined){
						cfieldSelector = "input[name~='"+cfields[i]+"_"+fieldset.instance+"']";
					}
					
					var cfield = $(cfieldSelector);
					
					if(cfield){
						field.on("change",{
							sumFields:sum,
							substractFields:substract,
							totalField:totalField,
							fieldsetInstance:fieldset.instance
						},function(event){
							var totalField = event.data.totalField;
							var sumFields = event.data.sumFields;
							var substractFields = event.data.substractFields;
							var fieldsetName = event.data.fieldsetName;
							var totalVal = totalField.data("updateTotal")(sumFields,substractFields,fieldsetName);
							totalField.val(totalVal);
						});
					}
				}
			}
		}
	}
};

var setupMask = function(field){
	if(field.data("jr:constraint:mask")!=undefined){
		var mask =  field.data("jr:constraint:mask");
		/*tratar de procesar los espacio*/
		field.mask(mask);
	}

};

var setupDependency = function(field, fieldset){
	
	if(field.data("jr:constraint:depends")!=undefined && field.is("input")){
		var ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"']";
		
		if(fieldset.instance!=undefined){
			ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"_"+fieldset.instance+"']";
		}
		
		
		var ancestor = $(ancestorSelector);
		
		if(ancestor.is("select")){/*solo funciona con selects hasta ahora*/
			ancestor.data("dependant",field);
			if(field.is("input"))
				field.hide();
			
			ancestor.on("change",
			{
				dependant : field,
				ancestor : ancestor
			},
			function(event){
				var name = event.data.dependant.attr("name");
				var slashIndex = name.lastIndexOf("/");
				var cleanName = name.substring(slashIndex+1);
				if(event.data.ancestor.val()==cleanName){
					event.data.dependant.show();
				}else{
					event.data.dependant.hide();
				}
			});
		}
	}
};

var setupRemoteData = function(field,fieldset){
	var fieldName = field.attr("name");
	if(field.data("jr:constraint:remote")!=undefined && field.is("select")){
		var url = field.data("jr:constraint:remote");
		
		if(field.data("jr:constraint:depends")!=undefined){
			var ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"']";
			if(fieldset.instance!=undefined){
				ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"_"+fieldset.instance+"']";
			}
			var ancestor = $(ancestorSelector);
			
			ancestor.data("dependant",field);
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
							  var option = field.children("option[value='']");
							  field.html("").append(option.attr("value",""));
						  }else{
							  field.hide();
						  }
						  var dependant = field.data("dependant");
						  if(dependant)
							  resetHierarchy(dependant);
					  };
					  if(data.success){
						  resetHierarchy(field);
						  for ( var count = 0; count < data.result.length; count++) {
							  var option = data.result[count];
							  field.append('<option value='+ option.id + '>'+ option.nombre + '</option>');
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
					  if(field.is("select")){
						  var option = field.children("option[value='']");
						  field.html("").append(option.attr("value",""));
					  }
					  for ( var count = 0; count < data.result.length; count++) {
						  var option = data.result[count];
						  field.append('<option value='+ option.id + '>'+ option.nombre + '</option>');
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
};

var setupDataConstraints = function(field){
	var data_constraints = field.attr("data-constraint");
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
			/*max constraint*/
			if(constraint.indexOf(".<=")!=-1){
				var number = constraint.substring(constraint.indexOf(".<=")+3,constraint.length);
				number = number.match(/\d*/gi)[0];
				var max = new Number(number);
				if(!isNaN(max)) field.data("jr:constraint:max",max);
				else log.error("Not a number for max constraint",number);
			}else
			/*min constraint*/
			if(constraint.indexOf(".>=")!=-1){
				var number = constraint.substring(constraint.indexOf(".>=")+3,constraint.length);
				number = number.match(/\d*/gi)[0];
				var min = new Number(number); 
				if(!isNaN(min))	field.data("jr:constraint:min",min);
				else log.error("Not a number for min constraint",number);
			}else 
			/*lower constraint*/
			if(constraint.indexOf(".<")!=-1){
				var number = constraint.substring(constraint.indexOf(".<")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				var lower = new Number(number);
				if(!isNaN(lower)) field.data("jr:constraint:lower",lower);
				else console.error("Not a number for max constraint",number);
			}else 
			/*higher constraint*/
			if(constraint.indexOf(".>")!=-1){
				var number = constraint.substring(constraint.indexOf(".>")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				var higher = new Number(number);
				if(!isNaN(higher))	field.data("jr:constraint:higher",higher);
				else console.error("Not a number for min constraint",number);
			}else
			/*dependency constraint*/
			if(constraint.indexOf("depends=")!=-1){
				var dependency = constraint.substring(constraint.indexOf("depends=")+8);
				field.data("jr:constraint:depends",dependency);
			}else 
			/*remote list constraint*/
			if(constraint.indexOf("url=")!=-1){
				var url = constraint.substring(constraint.indexOf("url=")+4);
				field.data("jr:constraint:remote",url);
			}else 
			/*cuit type constraint*/
			if(constraint.indexOf("cuit")!=-1){
				constraint.substring(constraint.indexOf("cuit")+9);
				field.data("jr:constraint:cuit","valid");
			}else 
			/*mask constraint*/
			if(constraint.indexOf("mask=")!=-1){
				var mask = constraint.substring(constraint.indexOf("mask=")+5);
				field.data("jr:constraint:mask",mask);
			}
			constraintContainer.push(constraint);
		}
		
		field.data("jr:constraints",constraintContainer);
	}
};

var validationDecimal = function(field){
	var data_type = field.attr("data-type-xml");
	if(data_type && data_type=="decimal"){
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			decimal:true
			,messages:{
				decimal: constraintMsg || "Debe ser un valor con decimales",
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationInteger = function(field){
	var data_type = field.attr("data-type-xml");
	if(data_type && data_type=="int"){
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			entero:true
			,messages:{
				entero: constraintMsg || "Debe ser un valor entero",
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationLower = function(field){
	if(field.data("jr:constraint:lower")!=undefined){
		var max =  field.data("jr:constraint:lower");
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			lower: max
			,messages:{
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationHigher = function(field){
	if(field.data("jr:constraint:higher")!=undefined){
		var min =  field.data("jr:constraint:higher");
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			higher: min
			,messages:{
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationMax = function(field){
	if(field.data("jr:constraint:max")!=undefined){
		var max =  field.data("jr:constraint:max");
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			max: max
			,messages:{
				max: constraintMsg || "Debe ser un valor menor o igual que {0}",
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationMin = function(field){
	if(field.data("jr:constraint:min")!=undefined){
		var min =  field.data("jr:constraint:min");
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			min: min
			,messages:{
				min: constraintMsg || "Debe ser un valor mayor o igual que {0}",
				number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
			}
		});
	}
};

var validationCuit = function(field){
	if(field.data("jr:constraint:cuit")!=undefined){
		var constraintMsg = field.attr("data-constraint-msg");
		field.rules( "add", {
			cuit: true,
			messages :{
				cuit:constraintMsg || "Debe ser un cuit v\u00e1lido"
			}
		});
	}
};

var setupValidations = function(f,fieldset){
	var field = $(f);
	if(fieldset){
		var instanceFieldName = field.attr("name")+"_"+fieldset.instance;
		field.attr("name",instanceFieldName);
	}else{
		fieldset = {instance:undefined,fields:undefined,name:undefined,dom:undefined};
	}
	
	/*Data constraints*/
	setupDataConstraints(field);

	/*Validations*/
	validationRequired(field);
	validationDecimal(field);
	validationInteger(field);
	validationLower(field);
	validationHigher(field);
	validationMin(field);
	validationMax(field);
	validationCuit(field);
	
	/*Aditional field logic*/
	setupHint(field);
	setupCalculate(field,fieldset);
	setupMask(field);
	setupDependency(field,fieldset);
	setupRemoteData(field,fieldset);
	
};

var setupValidationDefaults = function(){
	$.validator.setDefaults({
		debug: true,
		success: "valid",
		submitHandler: function() {
			alert("Formulario guardado.");
		}
	});
	
	$.validator.addMethod("entero", 
		function(value, element) {
//			return this.optional(element) || /^(-)?[0-9]*$/.test(value); 
			return /^-?(?:\d+|\d{1,3}(?:\.\d{3})+)$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero entero");
	
	$.validator.addMethod("decimal", 
		function(value, element) { 
//			return this.optional(element) || /^\s*-?(\d+(\.\d{1,2})?|\.\d{1,2})\s*$/.test(value); 
			return /^-?(?:\d+\,\d{1,3}|\d{1,3}(?:\.\d{3})+\,\d{1,3})$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero decimal con dos cifras luego del punto");
	
	$.validator.addMethod("higher", 
			function(value, element, param) { 
				return value > param;
			}, 
			"Debe especificar un n&uacute;mero mayor que {0} ");
	
	$.validator.addMethod("lower", 
			function(value, element, param) { 
				return value < param;
			}, 
			"Debe especificar un n&uacute;mero menor que {0} ");
	
	$.validator.addMethod("cuit",
		function(value,element){
		    inputString = value.toString();
		    if (inputString.length == 11) {
		        var Caracters_1_2 = inputString.charAt(0) + inputString.charAt(1);
		        if (Caracters_1_2 == "20" || Caracters_1_2 == "23" || Caracters_1_2 == "24" || Caracters_1_2 == "27" || Caracters_1_2 == "30" || Caracters_1_2 == "33" || Caracters_1_2 == "34") {
		            var Count = inputString.charAt(0) * 5 + inputString.charAt(1) * 4 + inputString.charAt(2) * 3 + inputString.charAt(3) * 2 + inputString.charAt(4) * 7 + inputString.charAt(5) * 6 + inputString.charAt(6) * 5 + inputString.charAt(7) * 4 + inputString.charAt(8) * 3 + inputString.charAt(9) * 2 + inputString.charAt(10) * 1;
		            Division = Count / 11;
		            if (Division == Math.floor(Division)) {
		                return true;
		            }
		        }
		    }
		    return false;
		}
	,"Debe especificar un cuit v&aacute;lido");
};