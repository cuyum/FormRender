/*Singleton GUI logic*/
var gui = new function(){
	this.fieldsets = [];
	this.repeatCount = undefined;
	this.renderGrid = false;
	this.getURLParameter = function(name) {
	    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
	};
	this.cleanFormValidations = function(){
		console.log("Cleaning Form");
		$("[class~='error']").siblings("label[class='error']").remove();
		$("[class~='error']").removeClass("error");
	};
	this.resetFields = function(fields){
		for ( var i = 0; i < fields.length; i++) {
			var field = $(fields[i]);
			if(field.is("select"))
				field.val("-1");
			else
				field.val('').removeAttr('checked');
		}
	};
	this.retrieveFormFieldData = function(){
		var data = [];
		for ( var i = 0; i < gui.fieldsets.length; i++) {
			var kvpair = {};
			for ( var j = 0; j < gui.fieldsets[i].fields.length; j++) {
				var field = $(gui.fieldsets[i].fields[j]);
				var key = gui.getCleanFieldName(field.attr("name"));
				var val = field.val();
				kvpair[key] = val;
			}
			data.push(kvpair);
		}
		return data;
	};
	this.submissionHandler = function(clickEvent){
		var thisForm = $(gui.form);
		var message = {header:{}, payload:{formulario:{"id":thisForm.attr("id"),data:[]}}};
		var submit = false;
		if(gui.renderGrid){
			console.log("Retrieving Grid data list");
			var dl = gui.grid.element.dataTable().fnGetData();
			if(dl.length>0){
				var dataList = [];
				for ( var i = 0; i < dl.length; i++) {
					var data = dl[i];
					if(data["values"] != undefined)
						delete data["values"];
					if(data["fields"] != undefined)
						delete data["fields"];
					if(gui.repeatCount && gui.repeatCount<=1){
						if(data["item"] != undefined)
						delete data["item"];
					}
					dataList.push(data);
				}
				message.payload.formulario.data = dataList;
				submit=true;
			} else {
				console.warn("No data in grid");
				thisForm.valid();
			}
		}else{
			var btn = clickEvent.target;
			var draft = $(btn).attr("draft");
			if(draft && draft=="true"){
				gui.cleanFormValidations();
				message.payload.formulario.data = gui.retrieveFormFieldData();
				submit = true;
			}else{
				if(thisForm.valid()){
					message.payload.formulario.data = gui.retrieveFormFieldData();
					submit=true;
				}
			}
		}
		if(submit && thisForm.attr("submit-url")){
			$.ajax({
				type: "POST",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				url: "/FormRender/rest/service/submit",
				data: {"submit_data":JSON.stringify(message),"url": thisForm.attr("submit-url")},
				success:function(data, statusStr, xhr){
					if(data.result && data.result.type){
						if(data.result.type == "SUCCESS"){
							alert("Formulario guardado ("+data.result.id+").");
							gui.form.reset();
							if(gui.renderGrid){
								gui.grid.element.dataTable().fnClearTable();
							}
						}else if(data.result.type == "ERROR"){
							alert("Ha ocurrido un error en el servidor de persistencia<br/>"+data.result.msg);
						}else{
							alert("Ha ocurrido un error no indicado en el servidor de persistencia");
						}
					}else{
						if(!data.success){
							alert("Error Remoto, contacte a su administrador.");
							console.group("ERROR REMOTO DETECTADO");
							console.warn("Error:"+data.msg);
							console.log("Objeto de respuesta",data.remote);
							console.groupEnd();
						}
					}
				},
				error:function(xhr,statusStr,errorStr){
					alert("Error Remoto, contacte a su administrador.");
					console.error("Error en submit:"+statusStr);
				}
			});
		}else{
			if(submit)
				console.error("No se encuentra daclarada la URL de submission");
			else
				console.error("No se ha podido llevar a cabo la submision de datos");
		}
	};
	this.addRelevant = function(field,relevant){
		if(field && relevant){
			var relevants = field.data("relevants");
			if(relevants){
				var push = true;
				for(var i = 0;i < relevants.length;i++){
					var r = relevants[i];
					if(relevant.tutor.attr("name") == r.tutor.attr("name")){
						r.value = [r.value,relevant.value];
						relevants.splice(i,1,r);
						push=false;
						break;
					}
				}
				if(push)relevants.push(relevant);
			}else{
				relevants = [relevant];
			}
			field.data("relevants",relevants);
		}
	};
	this.addDependant = function(tutor,dependant){
		if(tutor && dependant){
			var dependants = tutor.data("dependant");
			if(dependants){
				var push = true;
				for(var i = 0;i < dependants.length;i++){
					var d = dependants[i];
					if(dependant.attr("name") == d.attr("name")){
						push=false;
						break;
					}
				}
				if(push)dependants.push(dependant);
			}else{
				dependants = [dependant];
			}
			tutor.data("dependant",dependants);
		}
	};
	this.getField = function(name,fieldset){
		var ancestor = undefined;
		if(name){
			var ancestorSelector = "[name~='"+name.trim()+"']";;
			if(fieldset.instance!=undefined){
				ancestorSelector = "[name~='"+name.trim()+"_"+fieldset.instance+"']";
			}
			ancestor = $(ancestorSelector);
		}
		return ancestor;
	};
	this.getCleanFieldName = function(fullName,fieldsetInstance){
		if(fieldsetInstance!=undefined && !isNaN(fieldsetInstance)){
			fullName = fullName.replace("_"+fieldsetInstance,"");
		}
		if(fullName && fullName.trim()!=""){
			var idx = fullName.lastIndexOf("/");
			var cleanName = fullName.substring(idx+1);
			return cleanName;
		}
		return "";
	};
	this.flagPerformRelevantCheck = function(ancestor){
		if(ancestor && ancestor.is("select")){
			var isChecking = ancestor.data("isCheckingRelevants");
			if(!isChecking){
				ancestor.data("isCheckingRelevants",true);
				ancestor.on("change",{ me:ancestor }, function(event){
					var _this = event.data.me;
					/*retrieve all dependant field of _this field*/
					var dependants = _this.data("dependant");

					for ( var i = 0; i < dependants.length; i++) {
						var dependant = dependants[i];
						dependant.data("renderLogic")(dependant);
					}
				});
			}
		}
	};
	this.completeWithDelay = function(record,j,fields,delay){
		var delayTime = delay || 1000;
		var field = $(fields[j]);
//		console.log("RECORD:",record);
//		console.log("CAMPOS:",fields);
//		console.log("CAMPO:",field);
//		console.log("Iteracion:",(j+1));
		var fieldCleanName = gui.getCleanFieldName(field.attr("name"),record.instance);
		if (!field.is("select")) {
			delayTime = 0;
		}
		setTimeout(function() {
//			console.log(fields,fieldCleanName);
			if (field.is("select")){
//				console.log(fieldCleanName,record[fieldCleanName].value);
				field.val(record[fieldCleanName].value);
			}else { 
//				console.log(fieldCleanName,record[fieldCleanName]);
				field.val(record[fieldCleanName]);
			}
			field.trigger("change");
			
			j++;
			if (j < fields.length) {
				gui.completeWithDelay(record,j, fields, delayTime);
			}else{
				$("#unblockable").remove();
				$.unblockUI();
			}
		}, delayTime);
	};
	this.grid = {
		editing : -1,
		headers : [],
		data : [],
		model:{
			fields:[],
			values:[],
			item:"",
			instance:-1
		},
		element : $('<table id="repeat-grid" class="table table-striped"></table>'),
		getRowData : function(rowIndex){
			return this.element.dataTable().fnGetData(rowIndex);
		},
		setupEditClck: function(){
			$("input[type~='button'][repeat-action='edit']").unbind("click");
			$("input[type~='button'][repeat-action='edit']").click(function(evt){
				var rowIndex =  gui.grid.element.dataTable().fnGetPosition($(evt.target).closest('tr').get(0));
				var record = gui.grid.getRowData(rowIndex);
				var fields = gui.fieldsets[record.instance].fields;
				$.blockUI({message:"Cargando...<br>Espere por favor..."});
				var unblock = $("<span id='unblockable'/>");
				unblock.appendTo("body");
				
				gui.completeWithDelay(record,0,fields);
				
				for ( var i = 0; i < fields.length; i++) {
					var field = $(fields[i]);
					field.data("renderLogic")(field);
				}
				gui.grid.editing = rowIndex;
			});
		},
		setupAddClick: function(){
			$("input[type~='button'][repeat-action='add']").click(this,function(evt){
				gui.grid.addRow($(evt.target).parent().attr("repeat-instance"));
			});
		},
		addRow : function(fieldsetInstance){
			var fieldset = gui.fieldsets[fieldsetInstance];
			var fields = [];
			var record = $.extend(true,{},this.model);
			if(gui.repeatCount && gui.repeatCount>1)
				record.item = fieldset.title;
			var commit = true; 
			for ( var i = 0; i < fieldset.fields.length; i++) {
				var field =$(fieldset.fields[i]);
				var attribute = gui.getCleanFieldName(field.attr("name"),fieldsetInstance);
				if(field.is(":visible")){
					var value = field.val();
					if(value!=undefined && value!=null && $(gui.form).validate().element(field)){
						if(field.is("select")){
							var o = field.children("option:selected");
							record[attribute] = {label:o.text(),value:value};
						}else{
							record[attribute] = value;
						}
						fields.push(field);
					} else {
						commit = false;
						break;
					}
				}else{
					if(field.is("select")) record[attribute] = {label:" "};
					else record[attribute] = " ";
				}
			}
			record.instance = fieldset.instance;
			if(commit){
				gui.cleanFormValidations();
				gui.resetFields(fields);
				for ( var i = 0; i < fieldset.fields.length; i++) {
					var af = $(fieldset.fields[i]);
					af.data("renderLogic")(af);
				}
				if(this.editing==-1){
					this.element.dataTable().fnAddData(record,true);
				}else{
					this.element.dataTable().fnUpdate(record, this.editing);
					this.editing = -1;
				}
				this.setupEditClck();
			}
		},
		addRows : function(dataArray){
			this.element.dataTable().fnAddData(dataArray,true);
			this.setupEditClck();
		},
		/**
		 * Call grid render function after building Form.fieldsets with repeat parent fieldset 
		 */
		render : function(pfs){
			var gridFieldset = $('<fieldset class="jr-group well-white col1"></fieldset>');
			$("fieldset[repeat-instance]").append('<input type="button" class="btn" value="Agregar" repeat-action="add"/>');
			gridFieldset.appendTo(pfs);
			$('<h4></h4>').append("<span>Resultados</span>").appendTo(gridFieldset);
			$('<div class="table-overflow"></div>').append(this.element).appendTo(gridFieldset);
			if(gui.repeatCount && gui.repeatCount>1)
				this.headers.push({"sTitle":"Meses","mData":"item"});
			for ( var i = 0 ; i<gui.fieldsets[0].fields.length;i++) {
				var f = $(gui.fieldsets[0].fields[i]);
				var attribute = gui.getCleanFieldName(f.attr("name"));
				this.model[attribute] = null;
				var header = f.siblings("span.jr-label");
				if(f.is("select")){
					this.model[attribute] = {label:null,value:null};
					this.headers.push({ 
						"sTitle": header.text(),
						"mData" : ""+attribute+".label"
					});
				}else{
					this.headers.push({ 
						"sTitle": header.text(),
						"mData": ""+attribute
					});
				}
			}
			this.headers.push({
				"sTitle":"Acciones", 
				"bSearchable": false, 
				"mData": function ( data, type, full ) {
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
					"sSearch":"Buscar:",
					"sZeroRecords":"Ning&uacute;n registro resultante",
					"sLengthMenu": "<span>Filas _MENU_</span>",
					"sEmptyTable": "No hay datos",
					"sInfoFiltered": "(filtrado de _MAX_ registros)",
					"sInfoEmpty": "No hay registros cargados",
					"sInfo": "Mostrando _START_ a _END_, de _TOTAL_ registros",
					"oPaginate": {
				        "sFirst": "Primera",
				        "sLast" : "&Uacute;ltima",
				        "sNext" : "Siguiente",
				        "sPrevious" : "Anterior"
				     }
				},
				"aaData": gui.grid.data,
		        "aoColumns": gui.grid.headers
		    });
		}
	};
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