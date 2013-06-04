var validationRequired = function(field){
	var isRequired = field.attr("required");
	if(isRequired){
		field.rules( "add", {
			required:true,
			messages:{
				required : function(){
					var type = field.attr("type");
					if(type=="radio" || type==undefined){
						if(field.is("textarea")) return "El campo es requerido";
						return "Debe elegir una opci&oacute;n";
					}else{
						return "El campo es requerido";
					}
				}
			}
		});
		if(field.is("select")){
			field.rules( "select_required", {
				messages:{
					select_required : function(){
						return "El campo es requerido";
					}
				}
			});
		}
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
	field = $(field);
	var fieldName = field.attr("name");
	var data_calculate = field.attr("data-calculate");
	if(data_calculate && data_calculate.trim().length>0){
		if(data_calculate.indexOf("concat(")==-1){
			
			var totalFieldSpanSelector = "span.jr-output[data-value~='"+fieldName+"']";
			var totalFieldSpan = $(totalFieldSpanSelector);
			
			if(fieldset.instance!=undefined){
				totalFieldSpan = fieldset.dom.find(totalFieldSpanSelector);
			}
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
						cfield.on("change",{
							sumFields:sum,
							substractFields:substract,
							totalField:totalField,
							fieldsetInstance:fieldset.instance
						},function(event){
							var totalField = event.data.totalField;
							var sumFields = event.data.sumFields;
							var substractFields = event.data.substractFields;
							var fieldsetInstance = event.data.fieldsetInstance;
							var totalVal = totalField.data("updateTotal")(sumFields,substractFields,fieldsetInstance);
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

var setupRelevantData = function(field, fieldset){
	var data_relevant = field.attr("data-relevant");
	if(data_relevant && data_relevant.trim().length>0){
		/*-------HIDE FIELD FOR RELEVANT DEPENDENCY-------*/
		var el = null;
		if(field.is("input[type~='text']")){
			el = field.closest("label");
		}else if(field.is("input[type~='radio']") || field.is("input[type~='checkbox']")){
			el = field.closest("fieldset").closest("label");
		}else if(field.is("select")){
			el = field.closest("label");
		}else{
			console.warn("no se pudo encontrar el dom parent de "+field.attr("name")+" para esconder el campo" );
		}

		if(el!=null)el.hide();
		
		/*------SETUP RELEVANT DEPENDENCY CHECK--------*/
		/*split all dependencies*/
		var relevantFields = data_relevant.split(" or ");
		var or = new Array();
		var and = new Array();
		for ( var i = 0; i < relevantFields.length; i++) {
			var rfield = relevantFields[i].trim();
			rfield = rfield.split(" and ");
			or.push(rfield[0]);
			if(rfield.length>1){
				for ( var j = 1; j < rfield.length; j++) {
					and.push(rfield[j]);
				}
			}
		}
		var rfields = new Array();
		rfields = rfields.concat(or,and);
//		console.log(field.attr("name")+" has ",rfields);
		
		/*add each dependency detail to field and mark tutor field*/
		for ( var i = 0; i < rfields.length; i++) {
			var rfield = rfields[i];
			var data = undefined;
			if(rfield.indexOf("=")!=-1){
				data = rfield.split("=");
			}else if(rfield.indexOf("selected(")>=0){
				data = rfield.replace("selected(","");
				data = data.replace(")","");
				data = data.split(",");
			}else{
				console.error("Relevant data could not be parsed");
				data = [undefined];
			}
			
			var ancestor = FormRender.getField(data[0],fieldset);
			if(ancestor){ /*relate fields*/
				FormRender.addDependant(ancestor,field);
				/*add condition of visualization to field*/
				var relevant = {tutor:ancestor,value:data[1]};
				FormRender.addRelevant(field,relevant); 
				FormRender.flagPerformRelevantCheck(ancestor);
			}
		}
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
			FormRender.addDependant(ancestor,field);
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
					event.data.dependant.closest("label").show();
				}else{
					event.data.dependant.closest("label").hide();
				}
			});
		}else{
			console.warning("Dependency of "+field.data("jr:constraint:depends")+" found but not field with such name could be retrieved");
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
			
			FormRender.addDependant(ancestor,field);
			
			ancestor.on("change",function(){
				if(!$("#unblockable").is("span"))
					$.blockUI({message:"Cargando...<br>Espere por favor..."});
				$.ajax({
				  url: "/FormRender/rest/service/relay",
				  type: "POST",
				  dataType: "json",
				  data:{
					  fkey:ancestor.val(),
					  remoteUrl:url
				  },
				  success : function(data, statusStr, xhr) {
					  var resetHierarchy = function(fields){
						  for ( var i = 0; i < fields.length; i++) {
							  var field = fields[i];
							  if(field.is("select")){
								  var option = field.children("option[value='-1']").clone();
								  field.empty();
								  field.append(option);
							  }else{
								  if(field.is("input[type='text']")){
									  el = field.closest("label");
									  field.val("");
								  }else if(field.is("input[type='radio']") || field.is("input[type='checkbox']")){
									  el = field.closest("fieldset").closest("label");
								  }
								  if(el)el.hide();
							  }
							  var dependants = field.data("dependant");
							  if(dependants){
								  resetHierarchy(dependants);
							  }
						  }
					  };
					  if(data.success){
//						  console.group("resetHierarchy");
						  resetHierarchy([field]);
//						  console.groupEnd();
						  for ( var count = 0; count < data.result.length; count++) {
							  var o = data.result[count];
							  var option = $("<option></option>");
							  option.attr("value",o.id);
							  option.text(o.nombre);
							  field.append(option);
						  }
						  if($.browser.msie) field.hide().show();
					  }else{
						  console.warn("No se obtuvo una lista de elementos para agregar al campo "+ fieldName );
					  }
					  if(!$("#unblockable").is("span")) $.unblockUI();
					},
				  error:function(xhr,statusStr,errorStr){
					  $.unblockUI();
					  console.error("Error tratando de recuperar valores para "+ fieldName);
				  }
				});
			});
		}else{
			if(!$("#unblockable").is("span"))
				$.blockUI({message:"Cargando...<br>Espere por favor..."});
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
						  var option = field.children("option[value='-1']").clone();
						  field.empty();
						  option.appendTo(field);
						  for ( var count = 0; count < data.result.length; count++) {
							  var o = data.result[count];
							  var option = $("<option></option>");
							  option.attr("value",o.id);
							  option.text(o.nombre);
							  field.append(option);
						  }
					  }
				  }else{
					  console.warn("No se obtuvo una lista de elementos para agregar al campo "+ fieldName );
				  }
				  if(!$("#unblockable").is("span")) $.unblockUI();
				},
			  error:function(xhr,statusStr,errorStr){
				  $.unblockUI();
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
		"N&uacute;mero no v&aacute;lido");
//		"Debe especificar un n&uacute;mero entero");
	
	$.validator.addMethod("decimal", 
		function(value, element) { 
//			return this.optional(element) || /^\s*-?(\d+(\.\d{1,2})?|\.\d{1,2})\s*$/.test(value); 
			return /^-?(?:\d+\,\d{1,3}|\d{1,3}(?:\.\d{3})+\,\d{1,3})$/.test(value); 
		}, 
		"N&uacute;mero no v&aacute;lido");
//		"Debe especificar un n&uacute;mero decimal con dos cifras luego del punto");
	
	$.validator.addMethod("higher", 
		function(value, element, param) { 
			return value > param;
		}, 
		"Debe ser mayor que {0} ");
	
	$.validator.addMethod("lower", 
		function(value, element, param) { 
			return value < param;
		}, 
		"Debe ser menor que {0} ");
	$.validator.addMethod("required", 
		function(value, element, param) { 
			// check if dependency is met
			if ( !this.depend(param, element) ) {
				return "dependency-mismatch";
			}
			if ( element.nodeName.toLowerCase() === "select" ) {
				// could be an array for select-multiple or a string, both are fine this way
				var val = $(element).val();
				var numVal = new Number(val);
				if(!isNaN(numVal)){
					return numVal > -1;
				}else{
					return val && val.length > 0;
				}
			}
			if ( this.checkable(element) ) {
				return this.getLength(value, element) > 0;
			}
			return $.trim(value).length > 0;
		}, 
		"Debe elegir una opci&oacute;n");
	
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
	,"Cuit no v&aacute;lido");
};

var addVisualizationLogic = function(field){
	var renderLogic = function(field){
//		console.group("RENDER LOGIC FOR "+field.attr("name"));
		var relevants = field.data("relevants");
		var show = false;
		if(relevants){
			for ( var i = 0; i < relevants.length; i++) {
				var relevant = relevants[i];
				if(typeof relevant.value == "string"){
					if(relevant.tutor.val()==relevant.value){
						show=true;
					}
				}else if (typeof relevant.value == "object"){
					for ( var j = 0; j < relevant.value.length; j++) {
						if(relevant.tutor.val()==relevant.value[j]){
							show=true;
							break;
						}
					}
				}else{
					console.error("Value for relevant field cannot be recognized");
				}
			}
			if(show){
//				console.log("should show");
				field.closest("label").show();
			}else{
//				console.log("should not show");
				field.closest("label").hide();
			}
		}
		
//		console.groupEnd();
	};
	field.data("renderLogic",renderLogic);
	
};

var setupValidations = function(f,fieldset){
	var field = $(f);
	if(fieldset){
		var instanceFieldName = field.attr("name")+"_"+fieldset.instance;
		field.attr("name",instanceFieldName);
	}else{
		fieldset = {instance:undefined};
	}
	
	/*Data constraints*/
	setupDataConstraints(field);
	addVisualizationLogic(field);
	
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
	setupRelevantData(field,fieldset);
	setupDependency(field,fieldset);
	setupRemoteData(field,fieldset);
	
};