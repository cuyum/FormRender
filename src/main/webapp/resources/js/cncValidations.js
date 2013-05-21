var setupValidations = function(field){
	var f = $(field);
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
	
	var data_calculate = f.attr("data-calculate");
	if(data_calculate && data_calculate.trim().length>0){
		if(data_calculate.indexOf("concat(")==-1){
			var totalFieldSpan = $("span.jr-output[data-value~='"+fieldName+"']");
			var totalField = totalFieldSpan.closest("label").children("input"); 
			
			if(totalField){
				totalField.data("updateTotal",function(sumFields,substractFields){
					var total = 0;
					for ( var i = 0; i < sumFields.length; i++) {
						var field = $("input[name~='"+sumFields[i]+"']");
						if(!field)	return "Error: "+sumFields[i]+" inexistent";
						var n = new Number(field.val());
						if(isNaN(n))
							console.error("Value of field "+sumFields[i] +" is not a number");
						else
							total = total + n;
					}
					
					for ( var i = 0; i < substractFields.length; i++) {
						var field = $("input[name~='"+substractFields[i]+"']");
						if(!field) return "Error: "+substractFields[i]+" inexistent";
						var n = new Number(field.val());
						if(isNaN(n))
							console.error("Value of field "+substractFields[i] +" is not a number");
						else
							total = total - n;
					}
					return total;
				});
				
				var calculatedFields = data_calculate.split(" + ");
				var sum = new Array();
				var substract = new Array();
				for ( var i = 0; i < calculatedFields.length; i++) {
					var field = calculatedFields[i].trim();
					field = field.split(" - ");
					sum.push(field[0]);
					if(field.length>1){
						for ( var j = 1; j < field.length; j++) {
							substract.push(field[j]);
						}
					}
				}
				var fields = new Array();
				fields = fields.concat(sum,substract);
				for ( var i = 0; i < fields.length; i++) {
					var field = $("input[name~='"+fields[i]+"']");
					if(field){
						field.on("change",{
							sumFields:sum,
							substractFields:substract,
							totalField:totalField
						},function(event){
							var totalField = event.data.totalField;
							var sumFields = event.data.sumFields;
							var substractFields = event.data.substractFields;
							var totalVal = totalField.data("updateTotal")(sumFields,substractFields);
							totalField.val(totalVal);
						});
					}
				}
			}
		}
	}
	
	var data_relevant = f.attr("data-relevant");
	if(data_relevant && data_relevant.trim().length>0){
		var el = null;
		if(f.is("input[type~='text']")){
			el = f.parent();
		}else if(f.is("input[type~='radio']") || f.is("input[type~='checkbox']")){
			el = f.closest("fieldset");
		}
		
		if(el!=null)el.hide();
		
		if(data_relevant.indexOf("=")>=0){
			var data = data_relevant.split("=");
			var ancestor = $("[name~='"+data[0].trim()+"']");
			if(ancestor && ancestor.is("select")){
				var value = data[1];
				ancestor.on("change",{
					ancestor:ancestor,
					element:el,
					value:value
				},function(event){
					if(event.data.ancestor.val()==event.data.value){
						event.data.element.show();
					}else{
						event.data.element.hide();
					}
				});
			}
		}else if(data_relevant.indexOf("selected(")>=0){
			var data = data_relevant.replace("selected(","");
			data = data.replace(")","");
			data = data.split(",");
			var ancestor = $("[name~='"+data[0].trim()+"']");
			var value = data[1].trim()=="'yes'";
			if(ancestor && (ancestor.is("input[type~='checkbox']") || ancestor.is("input[type~='radio']")) && value){
				ancestor.on("click",{
					ancestor:ancestor,
					element:el,
					value:value
				},function(event){
					el.show();
				});
			}
		}
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
		var constraintMsg = f.attr("data-constraint-msg");
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
				if(!isNaN(max)) f.data("jr:constraint:max",max);
				else log.error("Not a number for max constraint",number);
			}else
			/*min constraint*/
			if(constraint.indexOf(".>=")!=-1){
				var number = constraint.substring(constraint.indexOf(".>=")+3,constraint.length);
				number = number.match(/\d*/gi)[0];
				var min = new Number(number); 
				if(!isNaN(min))	f.data("jr:constraint:min",min);
				else log.error("Not a number for min constraint",number);
			}else 
			/*lower constraint*/
			if(constraint.indexOf(".<")!=-1){
				var number = constraint.substring(constraint.indexOf(".<")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				var lower = new Number(number);
				if(!isNaN(lower)) f.data("jr:constraint:lower",lower);
				else console.error("Not a number for max constraint",number);
			}else 
			/*higher constraint*/
			if(constraint.indexOf(".>")!=-1){
				var number = constraint.substring(constraint.indexOf(".>")+2,constraint.length);
				number = number.match(/\d*/gi)[0];
				var higher = new Number(number);
				if(!isNaN(higher))	f.data("jr:constraint:higher",higher);
				else console.error("Not a number for min constraint",number);
			}else
			/*dependency constraint*/
			if(constraint.indexOf("depends=")!=-1){
				var dependency = constraint.substring(constraint.indexOf("depends=")+8);
				f.data("jr:constraint:depends",dependency);
			}else 
			/*remote list constraint*/
			if(constraint.indexOf("url=")!=-1){
				var url = constraint.substring(constraint.indexOf("url=")+4);
				f.data("jr:constraint:remote",url);
			}else 
			/*cuit type constraint*/
			if(constraint.indexOf("cuit")!=-1){
				constraint.substring(constraint.indexOf("cuit")+9);
				f.data("jr:constraint:cuit","valid");
			}else 
			/*mask constraint*/
			if(constraint.indexOf("mask=")!=-1){
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
					decimal: constraintMsg || "Debe ser un valor con decimales",
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(data_type && data_type=="int"){
			f.rules( "add", {
				entero:true
				,messages:{
					entero: constraintMsg || "Debe ser un valor entero",
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		/* Add constraint rules in validation framework */
		if(f.data("jr:constraint:lower")!=undefined){
			var max =  f.data("jr:constraint:lower");
			f.rules( "add", {
				lower: max
				,messages:{
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:higher")!=undefined){
			var min =  f.data("jr:constraint:higher");
			f.rules( "add", {
				higher: min
				,messages:{
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:max")!=undefined){
			var max =  f.data("jr:constraint:max");
			f.rules( "add", {
				max: max
				,messages:{
					max: constraintMsg || "Debe ser un valor menor o igual que {0}",
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:min")!=undefined){
			var min =  f.data("jr:constraint:min");
			f.rules( "add", {
				min: min
				,messages:{
					min: constraintMsg || "Debe ser un valor mayor o igual que {0}",
					number: constraintMsg || "Debe ser un valor num&eacute;rico v&aacute;lido"
				}
			});
		}
		
		if(f.data("jr:constraint:mask")!=undefined){
			var mask =  f.data("jr:constraint:mask");
			/*tratar de procesar los espacio*/
			f.mask(mask);
		}
		
		if(f.data("jr:constraint:cuit")!=undefined){
			f.rules( "add", {
				cuit: true,
				messages :{
					cuit:constraintMsg || "Debe ser un cuit v\u00e1lido"
				}
			});
		}
		
		if(f.data("jr:constraint:depends")!=undefined && f.is("input")){
			var ancestor = $("[name~='"+f.data("jr:constraint:depends")+"']");
			if(ancestor.is("select")){
				ancestor.data("dependant",f);
				f.hide();
				ancestor.on("change",
				{
					dependant : f,
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
		
		if(f.data("jr:constraint:remote")!=undefined && f.is("select")){
			var url = f.data("jr:constraint:remote");
			
			if(f.data("jr:constraint:depends")!=undefined){
				var ancestor = $("[name~='"+f.data("jr:constraint:depends")+"']");
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
							  }else{
								  field.hide();
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
			return /^-?(?!0)(?:\d+|\d{1,3}(?:\.\d{3})+)$/.test(value); 
		}, 
		"Debe especificar un n&uacute;mero entero");
	
	$.validator.addMethod("decimal", 
		function(value, element) { 
//			return this.optional(element) || /^\s*-?(\d+(\.\d{1,2})?|\.\d{1,2})\s*$/.test(value); 
			return /^-?(?!0)(?:\d+\,\d{1,3}|\d{1,3}(?:\.\d{3})+\,\d{1,3})$/.test(value); 
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