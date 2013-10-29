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

/*FormHint MUST be setup befor other hints*/
var setupHints = function(){
	var formHint = $("form label.tooltip").children("span.jr-hint");
	if(formHint){
		var title = $("#form-title");
		title.attr("title",formHint.text());
		formHint.parent("label").remove();
		title.tooltip({
			placement:"bottom"
		});
	}
	var fieldHints = $("span.jr-hint");
	for ( var i = 0; i < fieldHints.length; i++) {
		var hint = $(fieldHints[i]);
		if(hint){
			var label = hint.siblings(".jr-label");
			
			label.attr("title",hint.text());
			hint.remove();
			label.tooltip({
				placement:"right"
			});
		}
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
						if(isNaN(field.val())){
							var parsedNumber = gui.toNumber(field.val());
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
//					return cncFromNumber(total);
					return total;
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
							var totalFunc = totalField.data("updateTotal");
							var totalVal = null;
							if(totalFunc){
								totalVal= totalFunc(sumFields,substractFields,fieldsetInstance);
								totalField.val(totalVal);
							}else{
								console.error("Se ha encontrado un campo totalizador con un nombre incorrecto.");
							}
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

var setupRelevantData = function(field, fieldset, oldField){
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
			
			var ancestor = gui.getField(data[0],fieldset);
			if(ancestor){ /*relate fields*/
				if(oldField)gui.removeDependant(ancestor,oldField);
				gui.addDependant(ancestor,field);
				/*add condition of visualization to field*/
				var relevant = {tutor:ancestor,value:data[1]};
				gui.addRelevant(field,relevant); 
				gui.flagPerformRelevantCheck(ancestor);
			}
		}
	}
};


var setupDependency = function(field, fieldset, oldField){
	
	if(field.data("jr:constraint:depends")!=undefined && field.is("input")){
		var ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"']";
		
		if(fieldset.instance!=undefined){
			ancestorSelector = "[name~='"+field.data("jr:constraint:depends")+"_"+fieldset.instance+"']";
		}
		
		var ancestor = $(ancestorSelector);
		
		console.info(ancestor);
		
		if(ancestor.is("select") /*|| ancestor.attr("data-type-xml") == "select2"*/){/*solo funciona con selects hasta ahora*/
			gui.addDependant(ancestor,field);
			if(field.is("input")) field.hide();
			
			ancestor.on("change",
			{
				dependant : field,
				ancestor : ancestor
			},
			function(event){
//				console.log("change fired and captured from dependency code");
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
			console.warn("Dependency of "+field.data("jr:constraint:depends")+" found but not field with such name could be retrieved");
		}
	}
};

var setupRemoteData = function(field,fieldset){
	var fieldName = field.attr("name");
	if(field.data("jr:constraint:remote")!=undefined && field.is("select")){
		var url = field.data("jr:constraint:remote");
//		console.group("CAMPO:"+fieldName+"---Lista remota");
		
		/*
		 * Select2 has a documented limitation when it concernes ajax remote loading
		 * It has to be attached to a hidden input element instead of a select
		 */
		var attrs = field.get()[0].attributes; //we retrieve all attributes
		var newField = $("<input type='hidden'/>"); //reference the new element
		$.each(attrs,function(key,value){
			newField.attr(attrs[key].name,attrs[key].value); //assign all previous attributes to new element
		});
		field.after(newField); //insert the new element in the same position as the old select element
		gui.replaceFieldInFieldset(newField, field, fieldset);
		newField.attr("data-type-xml","select2");
		newField.data(field.data());
		setupRelevantData(newField,fieldset, field);//vuelvo a setear dependencia relevante
		field.remove(); //and finally remove the select element
		
		/*
		 *Bug #192 url in get parameter --- 
		 * escape() will not encode: /@*+
		 * encodeURI() will not encode: ~!@#$&*()=:/,;?+'
		 * encodeURIComponent() will not encode: ~!*()'
		 */
		if(newField.data("jr:constraint:depends")!=undefined){//depende de otro campo
			var ancestorSelector = "[name~='"+newField.data("jr:constraint:depends")+"']";
			if(fieldset.instance!=undefined){
				ancestorSelector = "[name~='"+newField.data("jr:constraint:depends")+"_"+fieldset.instance+"']";
			}
			var ancestor = $(ancestorSelector);
			gui.addDependant(ancestor,newField);
			
			newField.select2({
				allowClear: true,
				placeholder:"Seleccione una opci\u00f3n",
				ajax:{
					url:"/FormRender/rest/service/relay",
					dataType:"json",
					type:"POST",
					quietMillis:300,
					data: function (term, page) { // page is the one-based page number tracked by Select2
						term = term.trim();
						if(term.length>0){
							term = encodeURIComponent(term); 
							console.log("remote1",term);
						}
						return {
		                	fkey:ancestor.val(),
		                    remoteUrl:url+"?limit=20&page="+page+(term && term.length>0?"&term="+term.toLowerCase():"") //remote service url
		                };
		            },
		            results: function (data, page) {
		                var more = (page * 10) < data.total; // whether or not there are more results available
		                // notice we return the value of more so Select2 knows if more results can be loaded
		                return {results: data.result, more: more};
		            }
				},
				formatSelection:function(record){
					return record.text.length>40?record.text.substring(0,40)+"...":record.text;
				}
			});
			ancestor.on("change",ancestor,function(event){
//				console.log("change fired and captured from remote code");
				var fields = event.data.data("dependant");
				gui.resetHierarchy(fields);
			});
//			console.log("remote "+newField.attr("name")+" has ancestor"+ancestor.attr("name"));
		}else{
			newField.select2({
				allowClear: true,
				placeholder:"Seleccione una opci\u00f3n",
				ajax:{
					url:"/FormRender/rest/service/relay",
					dataType:"json",
					type:"POST",
					quietMillis:300,
					data: function (term, page) { // page is the one-based page number tracked by Select2
						term = term.trim();
						if(term.length>0){
							term = encodeURIComponent(term); 
							console.log("remote2",term);
						}
						return {
	                        remoteUrl:url+"?limit=20&page="+page+(term && term.length>0?"&term="+term.toLowerCase():"") //remote service url
	                    };
	                },
	                results: function (data, page) {
	                    var more = (page * 10) < data.total; // whether or not there are more results available
	                    // notice we return the value of more so Select2 knows if more results can be loaded
	                    return {results: data.result, more: more};
	                }
				},
				formatSelection:function(record){
					return record.text.length>40?record.text.substring(0,40)+"...":record.text;
				}
			});
		}
	}
};

var setupDataConstraints = function(field,fieldset){
	var data_constraints = field.attr("data-constraint");
//	console.group("CAMPO:"+field.attr("name"));
	if(data_constraints){
		var constraintContainer = [];
		var constraints = data_constraints.split(" and ");
		/* Process constraints of field */
//		console.log(constraints);
		for ( var i = 0; i < constraints.length; i++) {
			var constraint = constraints[i];
//			console.log(constraint);
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
			if(constraint.indexOf(".<=")!=-1 || constraint.indexOf(".&lt;=")!=-1){
				var number = constraint.substring(constraint.indexOf(".<=")+3,constraint.length);
//				number = number.match(/\d*/gi)[0];
//				console.log("max="+number);
				var max = new Number(number);
				if(!isNaN(max)) field.data("jr:constraint:max",max);
				else log.error("Not a number for max constraint",number);
			}else
			/*min constraint*/
			if(constraint.indexOf(".>=")!=-1 || constraint.indexOf(".&gt;=")!=-1){
				var number = constraint.substring(constraint.indexOf(".>=")+3,constraint.length);
//				number = number.match(/\d*/gi)[0];
//				console.log("min="+number);
				var min = new Number(number); 
				if(!isNaN(min))	field.data("jr:constraint:min",min);
				else log.error("Not a number for min constraint",number);
			}else 
			/*lower constraint*/
			if(constraint.indexOf(".<")!=-1 || constraint.indexOf(".&lt;")!=-1){
				var number = constraint.substring(constraint.indexOf(".<")+2,constraint.length);
//				number = number.match(/\d*/gi)[0];
//				console.log("lower than="+number);
				var lower = new Number(number);
				if(!isNaN(lower)) field.data("jr:constraint:lower",lower);
				else console.error("Not a number for max constraint",number);
			}else 
			/*higher constraint*/
			if(constraint.indexOf(".>")!=-1 || constraint.indexOf(".&gt;")!=-1){
				var number = constraint.substring(constraint.indexOf(".>")+2,constraint.length);
//				number = number.match(/\d*/gi)[0];
//				console.log("higher than="+number);
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
			}else
			/*porcentual constraint*/
			if(constraint.indexOf("porcentual=")!=-1){
				var porcentual = constraint.substring(constraint.indexOf("porcentual=")+11);
				field.data("jr:constraint:porcentual",porcentual);
			}else
			/*porcentual constraint*/
			if(constraint.indexOf("divisor=")!=-1){
				var divisor = constraint.substring(constraint.indexOf("divisor=")+8);
				field.data("jr:constraint:divisor", divisor);
			}else
			/*agrupador constraint*/
			if(constraint.indexOf("agrupador=")!=-1){
				var agrupador = constraint.substring(constraint.indexOf("agrupador=")+10);
				field.data("jr:constraint:agrupador",gui.toNumber(agrupador));
				if(gui.renderTotalizadores){
					var header = field.siblings("span.jr-label");
					gui.gridTotalizadora.agrupadores.push({
						"nombre":gui.getCleanFieldName(field.attr("name"),fieldset.instance),
						"nivel":gui.toNumber(agrupador),
						"titulo":header.text()
					});
				}
			}else
			/*totalizador constraint*/
			if(constraint.indexOf("totalizador")!=-1){
//				var totalizador = constraint.substring(constraint.indexOf("totalizador")+11);
				field.data("jr:constraint:totalizador",true);
				if(gui.renderTotalizadores){
					var header = field.siblings("span.jr-label");
					gui.gridTotalizadora.totalizadores.push({
						"nombre":gui.getCleanFieldName(field.attr("name"),fieldset.instance),
						"titulo":header.text()
					});
				}
			}
			constraintContainer.push(constraint);
		}
		
		field.data("jr:constraints",constraintContainer);
	}
//	console.groupEnd();
};

var setupPorcentual = function(field,fieldset){
	if(field.data("jr:constraint:porcentual")!=undefined){
		var porcentualVars = field.data("jr:constraint:porcentual").split(",");
		if(porcentualVars.length==3){
			
			var dividendo = gui.getField(porcentualVars[0],fieldset);
			var divisor = gui.getField(porcentualVars[1],fieldset);
			var porcentual = porcentualVars[2];
			if(dividendo.length == 1 && divisor.length == 1 && !isNaN(porcentual)){
				
				dividendo.on("change",{
					divisor : divisor,
					porcentual : porcentual,
					total:field
				},function(evt){
					var divisor = evt.data.divisor;
					var porcentual = evt.data.porcentual;
					var total = evt.data.total;
					
					if(this.value==undefined || this.value.trim()=="" || isNaN(this.value)){
						console.warn("El dividendo ("+$(this).attr("name")+") no tiene un valor v\u00E1lido ("+this.value+")");
						return;
					}
					
					if(divisor.val()!=undefined && divisor.val().trim()!="" && !isNaN(divisor.val())){
						
						if(this.value>0 && divisor.val()>0){
							total.val(cncFromNumber((this.value / divisor.val() * porcentual),2));
						}else{
							total.val(0);
						}
					}else console.warn("El divisor ("+divisor.attr("name")+") no tiene un valor v\u00E1lido ("+divisor.val()+")");
				});

				divisor.on("change",{
					dividendo : dividendo,
					porcentual : porcentual,
					total:field
				},function(evt){
					var dividendo = evt.data.dividendo;
					var porcentual = evt.data.porcentual;
					var total = evt.data.total;
					
					if(this.value==undefined || this.value.trim()=="" || isNaN(this.value)){
						console.warn("El divisor ("+$(this).attr("name")+") no tiene un valor v\u00E1lido ("+this.value+")");
						return;
					}
					
					if(dividendo.val()!=undefined && dividendo.val().trim()!="" && !isNaN(dividendo.val())){
						
						if(this.value>0 && dividendo.val()>0){
							total.val(cncFromNumber((dividendo.val() / this.value * porcentual),2));
						}else{
							total.val(0);
						}
					}else console.warn("El dividendo ("+dividendo.attr("name")+") no tiene un valor v\u00E1lido ("+dividendo.val()+")");
				});
				
			}else{
				console.error("No se encontro el dividendo o el divisor para calcular porcentaje en "+field.attr("name"));
			}
		}else{
			console.error("No se puede determinar el porcentaje de "+field.attr("name")+" ya que se declararon menos de 3 variables.");
		}
	}
};

var setupDivisor = function(field,fieldset){
	if(field.data("jr:constraint:divisor")!=undefined){
		var divisorVars = field.data("jr:constraint:divisor").split(",");
		if(divisorVars.length==2){
			
			var dividendo = gui.getField(divisorVars[0],fieldset);
			var divisor = gui.getField(divisorVars[1],fieldset);
			if(dividendo.length == 1 && divisor.length == 1){
				
				dividendo.on("change",{
					divisor : divisor,
					total:field
				},function(evt){
					var divisor = evt.data.divisor;
					var total = evt.data.total;
					
					if(this.value==undefined || this.value.trim()=="" || isNaN(this.value)){
						console.warn("El dividendo ("+$(this).attr("name")+") no tiene un valor v\u00E1lido ("+this.value+")");
						return;
					}
					
					if(divisor.val()!=undefined && divisor.val().trim()!="" && !isNaN(divisor.val())){
						
						if(this.value>0 && divisor.val()>0){
							total.val(cncFromNumber((this.value / divisor.val() ),2));
						}else{
							total.val(0);
						}
					}else console.warn("El divisor ("+divisor.attr("name")+") no tiene un valor v\u00E1lido ("+divisor.val()+")");
				});

				divisor.on("change",{
					dividendo : dividendo,
					total:field
				},function(evt){
					var dividendo = evt.data.dividendo;
					var total = evt.data.total;
					
					if(this.value==undefined || this.value.trim()=="" || isNaN(this.value)){
						console.warn("El divisor ("+$(this).attr("name")+") no tiene un valor v\u00E1lido ("+this.value+")");
						return;
					}
					
					if(dividendo.val()!=undefined && dividendo.val().trim()!="" && !isNaN(dividendo.val())){
						
						if(this.value>0 && dividendo.val()>0){
							total.val(cncFromNumber((dividendo.val() / this.value),2));
						}else{
							total.val(0);
						}
					}else console.warn("El dividendo ("+dividendo.attr("name")+") no tiene un valor v\u00E1lido ("+dividendo.val()+")");
				});
				
			}else{
				console.error("No se encontro el divisor para calcular divisi\u00F3n en "+field.attr("name"));
			}
		}else{
			console.error("No se puede determinar el divisor de "+field.attr("name")+" ya que se declararon menos de 2 variables.");
		}
	}
};

var validationDecimal = function(field){
	var data_type = field.attr("data-type-xml");
//	console.log("DECIMAL:"+field.attr("name"));
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
//				console.log("should show ",field);
				field.closest("label").show();
			}else{
//				console.log("should not show");
				field.closest("label").hide();
			}
		}//else console.log("no relevants found");
		
		
//		console.groupEnd();
	};
	field.data("renderLogic",renderLogic);
	
};

var setupValidationDefaults = function(){
	$("input[type='button'][action='submit']").click(gui.submissionHandler);
	$.validator.setDefaults({
		debug: false,
		success: "valid"
	});
	
	$.validator.addMethod("entero", 
		function(value, element) {
		var match = /^(-)?[0-9]*$/.test(value);
//			var match = /^-?(?:\d+|\d{1,3}(?:\.\d{3})+)$/.test(value);
			return this.optional(element) || match; 
		}, 
		"N&uacute;mero no v&aacute;lido");
//		"Debe especificar un n&uacute;mero entero");
	
	$.validator.addMethod("decimal", 
		function(value, element) { 
//			var match =/^-?(?:\d+\,\d{1,3}|\d{1,3}(?:\.\d{3})+\,\d{1,3})$/.test(value);
		var match =/^\s*-?(\d+(\.\d{1,3})?|\.\d{1,3})\s*$/.test(value);
			return this.optional(element) || match; 
		}, 
		"N&uacute;mero no v&aacute;lido");
//		"Debe especificar un n&uacute;mero decimal con dos cifras luego del punto");
	
	$.validator.addMethod("higher", 
		function(value, element, param) { 
			return this.optional(element) || value > param;
		}, 
		"Debe ser mayor que {0} ");
	
	$.validator.addMethod("lower", 
		function(value, element, param) { 
			return this.optional(element) || value < param;
		}, 
		"Debe ser menor que {0} ");
	
	$.validator.addMethod("min", 
			function(value, element, param) { 
				return this.optional(element) || gui.toNumber(value) >= param;
			}, 
			"Debe ser mayor o igual que {0} ");
	
	$.validator.addMethod("max", 
			function(value, element, param) { 
				return this.optional(element) || gui.toNumber(value) <= param;
			}, 
			"Debe ser menor o igual que {0} ");
	
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
		                return this.optional(element) || true;
		            }
		        }
		    }
		    return this.optional(element) || false;
		}
	,"Cuit no v&aacute;lido");
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
	setupDataConstraints(field, fieldset);
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
	setupPorcentual(field,fieldset);
	setupDivisor(field,fieldset);
	setupCalculate(field,fieldset);
	setupMask(field);
	setupRelevantData(field,fieldset);
	setupDependency(field,fieldset);
	setupRemoteData(field,fieldset);
	
};