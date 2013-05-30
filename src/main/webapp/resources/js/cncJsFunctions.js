if (typeof console == "undefined") {console = {log:function(){}, warn:function(){}, info:function(){}, error:function(){}};}
if (typeof window.console.debug == "undefined") {console.debug = console.log;}

var FormRender = new function(){
	this.fieldsets = [];
	this.repeatCount = undefined;
	this.grid = {
		editing : -1,
		headers : [],
		data : [],
		element : $('<table id="repeat-grid" class="table table-striped"></table>'),
		getRowData : function(rowIndex){
			return this.element.dataTable().fnGetData(rowIndex);
		},
		setupEditClck: function(){
			$("input[type~='button'][repeat-action='edit']").click(function(evt){
				var rowIndex =  FormRender.grid.element.dataTable().fnGetPosition($(evt.target).closest('tr').get(0));
				var data = FormRender.grid.getRowData(rowIndex);
				var ri = data[data.length-1];
				data.shift();
				data.pop();
				console.log(data);
				console.log("instancia:"+ri);
				var fields = FormRender.fieldsets[ri].fields;
				for ( var i = 0; i < fields.length; i++) {
					$(fields[i]).val(data[i]);
				};
				FormRender.grid.editing = rowIndex;
			});
		},
		setupAddClick: function(){
			$("input[type~='button'][repeat-action='add']").click(this,function(evt){
				FormRender.grid.addRow($(evt.target).parent().attr("repeat-instance"));
			});
		},
		addRow : function(fieldsetInstance){
			var fieldset = FormRender.fieldsets[fieldsetInstance];
			var reg = [fieldset.title];
			var commit = true; 
			for ( var i = 0; i < fieldset.fields.length; i++) {
				var field =$(fieldset.fields[i]);
				var value = field.val();
				if(field.is(":visible")){
					if(value!=undefined && value!=null && $(FormRender.form).validate().element(field)){
						reg.push(field.val());
					} else {
						console.warn("Field: "+field.attr("name")+" has no value");
						commit = false;
						break;
					}
				}else{
					reg.push(" ");
				}
			}
			reg.push(fieldset.instance);
			FormRender.form.reset();
			if(commit){
				if(this.editing==-1){
					this.element.dataTable().fnAddData(reg);
				}else{
					this.element.dataTable().fnUpdate(reg, this.editing);
				}
				this.setupEditClck();
			}
		},
		render : function(pfs){
			var gridFieldset = $('<fieldset class="jr-group well-white col1"></fieldset>');
			$("fieldset[repeat-instance]").append('<input type="button" class="btn" value="Agregar" repeat-action="add"/>');
			gridFieldset.appendTo(pfs);
			$('<h4></h4>').append("<span>Resultados</span>").appendTo(gridFieldset);
			$('<div class="table-overflow"></div>').append(this.element).appendTo(gridFieldset);
			
			this.headers.push({"sTitle":"Formulario"});
			for ( var i = 0 ; i<FormRender.fieldsets[0].fields.length;i++) {
				var header = $(FormRender.fieldsets[0].fields[i]).siblings("span.jr-label");
				this.headers.push({ "sTitle": header.text() });
			}
			this.headers.push({
				"sTitle":"Acciones", 
				"bSearchable": false, 
				"mRender": function ( data, type, full ) {
					return '<input type="button" repeat-action="edit" class="btn" value="Editar"/>';
				}
	        });
			this.setupAddClick();
			this.setupEditClck();
			this.element.dataTable({
				"bJQueryUI": false,
				"bAutoWidth": false,
				"sScrollX": "100%",
				"sScrollXInner": "100%",
			    "bScrollCollapse": true,
				"sPaginationType": "full_numbers",
				"sDom": '<"datatable-header"fl>t<"datatable-footer"ip>',
				"oLanguage": {
					"sSearch":"Buscar",
					"sZeroRecords":"Ning&uacute;n registro resultante",
					"sLengthMenu": "<span>Filas _MENU_</span>",
					"sEmptyTable": "No hay datos",
					"sInfoEmpty": "No hay registros cargados",
					"sInfo": "Mostrando _START_ a _END_, de _TOTAL_ registros",
					"oPaginate": {
				        "sFirst": "Primera",
				        "sLast" : "&Uacute;ltima",
				        "sNext" : "Siguiente",
				        "sPrevious" : "Anterior"
				     }
				},
				"aaData": FormRender.grid.data,
		        "aoColumns": FormRender.grid.headers
		    });
		}
	};
//	this.grid.render = function(){
//		this.grid.headers = [];
//		this.grid.data = [];
//		this.grid.element = $('<table id="repeat-grid" class="table table-striped"></table>');
//		var gridFieldset = $('<fieldset class="jr-group well-white col1"></fieldset>');
//		$("fieldset[repeat-instance]").append('<input type="button" class="btn" value="Agregar" repeat-action="add"/>');
//		gridFieldset.appendTo(pfs);
//		$('<h4></h4>').append("<span>Resultados</span>").appendTo(gridFieldset);
//		$('<div class="table-overflow"></div>').append(this.grid.element).appendTo(gridFieldset);
//		
//		this.grid.headers.push({"sTitle":"Formulario"});
//		for ( var i = 0 ; i<this.fieldsets[0].fields.length;i++) {
//			var header = $(this.fieldsets[0].fields[i]).siblings("span.jr-label");
//			this.grid.headers.push({ "sTitle": header.text() });
//		}
//		this.grid.headers.push({
//			"sTitle":"Acciones", 
//			"bSearchable": false, 
//			"mRender": function ( data, type, full ) {
//				console.log(data, type, full);
//				return '<input type="button" data="'+data+'" repeat-action="edit" value="Editar"/>';
//			}
//        });
//		$("input[type~='button'][repeat-action~='add']").click(function(evt){
//			FormRender.addToGrid($(evt.target).parent().attr("repeat-instance"));
//		});
//		this.grid.element.dataTable({
//			"bJQueryUI": false,
//			"bAutoWidth": false,
//			"sScrollX": "100%",
//			"sScrollXInner": "100%",
//		    "bScrollCollapse": true,
//			"sPaginationType": "full_numbers",
//			"sDom": '<"datatable-header"fl>t<"datatable-footer"ip>',
//			"oLanguage": {
//				"sSearch":"Buscar",
//				"sZeroRecords":"Ning&uacute;n registro resultante",
//				"sLengthMenu": "<span>Filas _MENU_</span>",
//				"sEmptyTable": "No hay datos",
//				"sInfoEmpty": "No hay registros cargados",
//				"sInfo": "Mostrando _START_ a _END_, de _TOTAL_ registros",
//				"oPaginate": {
//			        "sFirst": "Primera",
//			        "sLast" : "&Uacute;ltima",
//			        "sNext" : "Siguiente",
//			        "sPrevious" : "Anterior"
//			     }
//			},
//			"aaData": FormRender.grid.data,
//	        "aoColumns": FormRender.grid.headers
//	    });
//	};
//	this.addToGrid = function(fieldsetInstance){
//		var fieldset = this.fieldsets[fieldsetInstance];
//		var reg = [fieldset.instance, fieldset.instance];
//		var commit = true; 
//		for ( var i = 0; i < fieldset.fields.length; i++) {
//			var field =$(fieldset.fields[i]);
//			var value = field.val();
//			if(field.is(":visible")){
//				console.log("valid:"+$(this.form).validate().element(field));
//				if(value!=undefined && value!=null && $(this.form).validate().element(field)){
//					reg.push(field.val());
//				} else {
//					console.warn("Field: "+field.attr("name")+" has no value");
//					commit = false;
//					break;
//				}
//			}else{
//				reg.push(" ");
//			}
//		}
//		console.log(reg);
//		FormRender.form.reset();
//		if(commit) this.grid.element.dataTable().fnAddData(reg);
//	};
};


var cncToNumber = function(value){
	if(value && isNaN(value) && value.trim().length>0){
		var number = $.i18n.parseNumber( value ,{region:'es-AR'});
		return number;
	}else if(!isNaN(value)){
		console.warn("Number passed to cnctoNumber, only String value allowed");
		return value;
	}else if(value==undefined){
		console.error("Undefined value passed to cncToNumber");
		return value;
	}
};

var cncFromNumber = function(value,decimals){
	var d = 0;
	var vStr = ""+value;
	if(vStr.indexOf(".")>0){
		d = vStr.substring(vStr.indexOf(".")+1).length;
	}
	decimals = "n"+d;
	
	if(value && !isNaN(value)){
		var formated = $.i18n.formatNumber( value, decimals, {region:'es-AR'} );
		return formated;
	}else if(isNaN(value)){
		console.warn("String passed to cncFromNumber, only numeric value allowed");
		return value;
	}else if(value==undefined){
		console.error("Undefined value passed to cncFromNumber");
		return value;
	}
};

var getURLParameter = function(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
};

