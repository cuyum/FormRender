$(document).ready(function() {
	FormRender.form = document.forms[0];
	setupValidationDefaults();
	
	/*
	 * Para repeat se necesitará duplicar todo lo que se encuentre dentro del fieldset.jr-repeat
	 * actualmente se está pasando un listado de campos al setup de validaciones y se están buscando 
	 * globalmente los campos por nombres. Se deberá pasar los campos de un fieldset específico y las
	 * búsquedas internas deberán dejar de hacerse globalmente y deberán buscarse dentro del fieldset
	 * suministrado. 
	 */
	
	FormRender.fieldsets = [];
	var fs = $("fieldset.jr-repeat");
	var repeatCount = getURLParameter("repeat");
	FormRender.renderGrid = fs.hasClass("grilla");
	
	if(fs.length>0 && repeatCount>0){
		var pfs = fs.parent();
		var calculatedItems = $("fieldset[name~='"+pfs.attr("name")+"']").siblings("#jr-calculated-items").find("[name]");
		/* iteracion normal, siguientes instancias de repeats */
		for ( var i = 0; i < repeatCount; i++) {
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
			if(fieldset.titleSpan){
				var titleVar = fieldset.titleSpan.text();
				if(titleVar && titleVar.trim()!="" && titleVar.indexOf("{")!=-1){
					titleVar = titleVar.replace("{","").replace("}","");
					var titleVal = getURLParameter(titleVar);
					if(titleVal!=null && titleVal!=undefined){
						fieldset.titleSpan.text(titleVal.split(",")[i]);
						fieldset.title = titleVal.split(",")[i];
					} 
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
			FormRender.grid = {};
			FormRender.grid.headers = [];
			FormRender.grid.data = [];
			FormRender.grid.element = $('<table id="repeat-grid" class="table table-striped"></table>');
			var gridFieldset = $('<fieldset class="jr-group well-white col1"></fieldset>');
			$("fieldset[repeat-instance]").append('<input type="button" class="btn" value="Agregar" repeat-action="add"/>');
			gridFieldset.appendTo(pfs);
			$('<h4></h4>').append("<span>Grilla</span>").appendTo(gridFieldset);
			$('<div class="table-overflow"></div>').append(FormRender.grid.element).appendTo(gridFieldset);
			
			FormRender.grid.headers.push({"sTitle":"Formulario"});
			for ( var i = 0 ; i<FormRender.fieldsets[0].fields.length;i++) {
				var header = $(FormRender.fieldsets[0].fields[i]).siblings("span.jr-label");
				FormRender.grid.headers.push({ "sTitle": header.text() });
			}
			$("input[type~='button'][repeat-action~='add']").click(function(evt){
				FormRender.addToGrid($(evt.target).parent().attr("repeat-instance"));
			});
			FormRender.grid.element.dataTable({
				"bJQueryUI": false,
				"bAutoWidth": false,
				"sScrollX": "100%",
				"sScrollXInner": "100%",
			    "bScrollCollapse": true,
				"sPaginationType": "full_numbers",
				"sDom": '<"datatable-header"fl>t<"datatable-footer"ip>',
				"oLanguage": {
					"sLengthMenu": "<span>Filas _MENU_</span>"
				},
				"aaData": FormRender.grid.data,
		        "aoColumns": FormRender.grid.headers
		    });
			
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
		
		console.info("No se encuentra el fieldset.jr-repeat");
		/*busqueda global de campos*/
		FormRender.fieldset.fields = $("[name]").not("fieldset");
		/*
		 * si van a venir las instancias de repeticiones tengo que tenerlas en cuenta
		 * e iterarlas antes de iterar los campos
		 */
		for ( var i = 0; i < FormRender.fieldset.fields.length; i++) {
			var field = FormRender.fieldset.fields[i];
			setupValidations(field);
		}
	}
	
});
