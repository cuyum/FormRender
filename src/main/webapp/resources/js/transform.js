$(document).ready(function() {

	/**
	 * Boot up FormRender GUI
	 */
	gui.boot();
	/*building Form.fieldsets*/
	if(gui.initialFieldset.length>0 && gui.repeatCount>0){
		var pfs = gui.initialFieldset.parent();
		var calculatedItems = $("fieldset[name~='"+pfs.attr("name")+"']").siblings("#jr-calculated-items").find("[name]");
		/* iteracion normal, siguientes instancias de repeats */
		for ( var i = 0; i < gui.repeatCount; i++) {
			var fieldset = {};
			var fsRepeat = gui.initialFieldset.clone();
			fieldset.instance = i;
			fsRepeat.attr("repeat-instance",fieldset.instance);
			/* Duplicacion de campos */
			
			/* logica de diferenciacion */
			fieldset.name = gui.initialFieldset.attr("name")+"_"+i;
			fsRepeat.attr("name",fieldset.name);
			
			/*identificacion de variable de repeticion*/
			fieldset.titleSpan = fsRepeat.find("fieldset[class~='variable'] > h4 > span.jr-label");
			if(fieldset.titleSpan){/*tiene title variable*/
				var titleVar = fieldset.titleSpan.text();
				if(titleVar.indexOf("{")!=-1 && titleVar.indexOf("}")!=-1){/*es variable*/
					titleVar = titleVar.replace("{","").replace("}","")+"";
					if(titleVar && titleVar.trim()!=""){
						var titleVal = gui.getURLParameter(titleVar);
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
			}else{
				console.warn("No se ha encontrado namespace de titulo variable en markup");
			}
			
			/* agregado de campos a grupo padre */
			fsRepeat.appendTo(pfs);
			fieldset.dom = fsRepeat;
			
			/* agregado al listado de campos para logica de validacion */
			var repeatFields = fsRepeat.find("[name]").not("fieldset");
			fieldset.fields = repeatFields;
			gui.fieldsets.push(fieldset);
			
		}
		
		gui.initialFieldset.remove();
		$(gui.form).validate();
		
		if(gui.renderGrid){
			gui.grid.render(pfs);
		}

		/*for each repeat instance*/
		for ( var i = 0; i < gui.fieldsets.length; i++) {
			/*for each field in instance*/
			
			var fieldset = gui.fieldsets[i];
			for ( var j = 0; j < fieldset.fields.length; j++) {
				var field = fieldset.fields[j];
				setupValidations(field,fieldset);
			}
			for ( var j = 0; j < calculatedItems.length; j++) {
				setupCalculate(calculatedItems[j],fieldset);
			}
		}
		
		
		if(gui.renderTotalizadores){
			gui.gridTotalizadora.render(pfs);
		}
		
	}else if(gui.initialFieldset.length>0 && gui.repeatCount==0){
		console.error("Se encuentra repeat infinito");
	}else{
		$(gui.form).validate();
		var fieldset = {};
		
		console.info("No se encuentra el fieldset.jr-repeat");
		/*busqueda global de campos*/
		fieldset.fields = $("[name]").not("fieldset").not(":hidden");
		/*
		 * si van a venir las instancias de repeticiones tengo que tenerlas en cuenta
		 * e iterarlas antes de iterar los campos
		 */
		gui.fieldsets.push(fieldset);
		for ( var i = 0; i < fieldset.fields.length; i++) {
			var field = fieldset.fields[i];
			setupValidations(field);
		}
	}
	
	gui.loadDataId = gui.getURLParameter("recordId");
	
	if(gui.loadDataId && gui.loadDataId.trim()!=""){
		$.blockUI({message:"Cargando...<br>Espere por favor..."});
		$.ajax({
			type: "GET",
			url: "/"+formRenderContext+"/rest/service/retrieve",
			data: {"recordId":gui.loadDataId},
			success:function(data, statusStr, xhr){
				if(data.result && data.result.type && data.result.type=="ERROR"){
					gui.displayError("Error remoto: "+data.result.msg);
				}else{//success retrieval
//					console.log(data);
					gui.readonly = data.header.readonly;
					var dataArray = data.payload.formulario.data;
					if(gui.renderGrid){
						if(gui.renderTotalizadores || gui.renderTotalizadoresIngresados){

							if(dataArray[0].registros!=null){
								for ( var i = 0; i < dataArray[0].registros.length; i++) {
									gui.gridTotalizadora.processRecord(dataArray[0].registros[i]);
								}
							}
							gui.grid.addRows(dataArray[0].registros);
//							gui.gridTotalizadora.addRows(dataArray[0].sumarizados);
						}else{
							gui.grid.addRows(dataArray);
						}
					}else{
						if(dataArray!=null){
							for ( var i = 0; i < dataArray.length; i++) {
							
								var record = dataArray[i];
//								console.group("CAMPOS INSTANCIA "+i);
								gui.completeForm(record,gui.fieldsets[i].fields);
//								console.groupEnd();
							}
						}
						else console.error("Error en la recepcion de los datos");
					}
				}
			},
			error:function(xhr,statusStr,errorStr){
				console.error(data);
			}
		});
		$.unblockUI();
	}
	gui.setupDefaults();
});
