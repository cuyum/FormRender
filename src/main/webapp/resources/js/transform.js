$(document).ready(function() {
	FormRender.form = document.forms[0];
	setupValidationDefaults();
	
	/*Setup Version*/
	var versionElement = $("#version");
	var versionValue = getURLParameter("version");
	if(versionElement && versionValue) versionElement.text("Versi\u00F3n: "+versionValue);
	/*
	 * Para repeat se necesitará duplicar todo lo que se encuentre dentro del fieldset.jr-repeat
	 * actualmente se está pasando un listado de campos al setup de validaciones y se están buscando 
	 * globalmente los campos por nombres. Se deberá pasar los campos de un fieldset específico y las
	 * búsquedas internas deberán dejar de hacerse globalmente y deberán buscarse dentro del fieldset
	 * suministrado. 
	 */
	
	var fs = $("fieldset.jr-repeat");
	FormRender.repeatCount = getURLParameter("repeat");
	FormRender.renderGrid = fs.hasClass("grilla");
	
	
	if(fs.length>0 && FormRender.repeatCount>0){
		var pfs = fs.parent();
		var calculatedItems = $("fieldset[name~='"+pfs.attr("name")+"']").siblings("#jr-calculated-items").find("[name]");
		/* iteracion normal, siguientes instancias de repeats */
		for ( var i = 0; i < FormRender.repeatCount; i++) {
			var fieldset = {};
			var fsRepeat = fs.clone();
			fieldset.instance = i;
			fsRepeat.attr("repeat-instance",fieldset.instance);
			/* Duplicacion de campos */
			
			/* logica de diferenciacion */
			fieldset.name = fs.attr("name")+"_"+i;
			fsRepeat.attr("name",fieldset.name);
			
			/*identificacion de variable de repeticion*/
			fieldset.titleSpan = fsRepeat.find("fieldset[class~='variable'] > h4 > span.jr-label");
			if(fieldset.titleSpan){/*tiene title variable*/
				var titleVar = fieldset.titleSpan.text();
				if(titleVar.indexOf("{")!=-1 && titleVar.indexOf("}")!=-1){/*es variable*/
					titleVar = titleVar.replace("{","").replace("}","")+"";
					if(titleVar && titleVar.trim()!=""){
						var titleVal = getURLParameter(titleVar);
						if(titleVal!=null && titleVal!=undefined){
							fieldset.titleSpan.text(titleVal.split(",")[i]);
							fieldset.title = titleVal.split(",")[i];
						} 
					}else{
						console.warn(titleVar+" is provided as variable for group title but no such GET parameter found");
					}
				}else{/*viene directo*/
					fieldset.title = titleVar;
				}
			}
			
			/* agregado de campos a grupo padre */
			fsRepeat.appendTo(pfs);
			fieldset.dom = fsRepeat;
			
			/* agregado al listado de campos para logica de validacion */
			var repeatFields = fsRepeat.find("[name]").not("fieldset");
			fieldset.fields = repeatFields;
			FormRender.fieldsets.push(fieldset);
			
		}
		
		fs.remove();
		$(FormRender.form).validate();
		
		if(FormRender.renderGrid){
			FormRender.grid.render(pfs);
		}
		
		/*for each repeat instance*/
		for ( var i = 0; i < FormRender.fieldsets.length; i++) {
			/*for each field in instance*/
			
			var fieldset = FormRender.fieldsets[i];
			for ( var j = 0; j < fieldset.fields.length; j++) {
				var field = fieldset.fields[j];
				setupValidations(field,fieldset);
			}
			for ( var j = 0; j < calculatedItems.length; j++) {
				setupCalculate(calculatedItems[j],fieldset);
			}
		}
		
	}else{
		$(FormRender.form).validate();
		var fieldset = {};
		
		console.info("No se encuentra el fieldset.jr-repeat");
		/*busqueda global de campos*/
		fieldset.fields = $("[name]").not("fieldset");
		/*
		 * si van a venir las instancias de repeticiones tengo que tenerlas en cuenta
		 * e iterarlas antes de iterar los campos
		 */
		for ( var i = 0; i < fieldset.fields.length; i++) {
			var field = fieldset.fields[i];
			setupValidations(field);
		}
//		FormRender.fieldsets.push(fieldset);
	}
	
});
