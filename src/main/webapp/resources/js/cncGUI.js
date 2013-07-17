/*Singleton GUI logic*/
var gui = new function(){
	this.fieldsets = [];
	this.repeatCount = undefined;
	this.renderGrid = false;
	this.MESSAGES = {
		INFO : "alert-info",
		WARN : "",
		SUCCESS: "alert-success",
		ERROR: "alert-error"
	};
	this.replaceFieldInFieldset = function(newField, oldField, fieldset){
		var fs;
		if(fieldset && fieldset.instance){
			fs = fieldset;
		}else fs = this.fieldsets[0];
		
		for ( var i = 0; i < fs.fields.length; i++) {
			var f = fs.fields[i];
			if($(f).attr("name")==oldField.attr("name")){
				fs.fields.splice(i,1,newField);
				break;
			}
		}
	};
	this.validateForm = function(){
		var validFields = false;
		var validSelects = false;
		
		validFields = $(gui.form).valid();
		validSelects = gui.validateRemoteSelects();
		
		return validFields && validSelects;
			
	};
	this.setupDefaults = function(){
		$("select").select2();
		setupHints();
	};
	this.displayMessage = function(message,type){
		if(message==undefined || message==null){
			console.warn("No message supplied for message display");
			return;
		}
		if(type==null || type==undefined || type.trim()==""){
			type = "";
		}
		var alertDiv = $("<div data-dismiss='alert' class='alert "+type+"' style='display:none;'><button  class='close' type='button'>×</button></div>");
		var m = alertDiv.append(message);
		$("#internal-messages").append(m).goTo();
		m.fadeIn();
	};
	this.displayWarning = function(message){
		this.displayMessage(message);
	};
	this.displayInfo = function(message){
		this.displayMessage(message, this.MESSAGES.INFO);
	};
	this.displaySuccess = function(message){
		this.displayMessage(message, this.MESSAGES.SUCCESS);
	};
	this.displayError = function(message){
		this.displayMessage(message, this.MESSAGES.ERROR);
	};
	this.closeAllMessages = function(){
		$("#internal-messages").children().each(function(count,el){$(el).fadeOut();});
	};
	this.blockUI = function(message,unblockable){
		if(!$("#unblockable").is("span")){
			if(message){
				$.blockUI({message:message});
			}else{
				$.blockUI({message:"Cargando...<br>Espere por favor..."});
			}
		}
		if(!$("#unblockable").is("span") && unblockable!=undefined && unblockable){
			var unblock = $("<span id='unblockable'/>");
			unblock.appendTo("body");
		}
	};
	this.unblockUI = function(force){
		if(force) {
			$("#unblockable").remove();
			$.unblockUI();
		}else{
			if(!$("#unblockable").is("span")){
				$.unblockUI();
			}
		}
	};
	this.cache = {
		data : [],
		store : function(index,data){
			this.data[index] = data;
//			console.info("Stored",data);
		},
		check : function(index){
			var data = this.data[index];
			if(data!=undefined){
				return true;
			}else{
//				console.log("no data stored");
				return false;
			}
		},
		retrieve:function(index){
			return this.data[index];
		},
		reset : function(){
			this.data = [];
//			console.log("Cleaning cache.");
		}
	};
	this.getURLParameter = function(name) {
	    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
	};
	this.cleanFormValidations = function(){
		console.info("Cleaning Form Validations");
		$("[class~='error']").siblings("label[class='error']").remove();
		$("[class~='error']").removeClass("error");
	};
	this.resetForm = function(){
		gui.form.reset();
		$("select").each(function(count,item){
			$(item).select2("data",null);
		});
		$("[data-type-xml='select2']").each(function(i, item){
			$(item).select2("val","");
		});
	};
	this.resetFields = function(fields){
		for ( var i = 0; i < fields.length; i++) {
			var field = $(fields[i]);
			if(field.is("select")){
				field.select2("val","-1");
			}else if(field.attr("data-type-xml")=="select2")
				field.select2("data",null);
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
	this.validateRemoteSelects = function(){
		var selectFields = $("input[class~='select2-offscreen']:hidden");
		var valid = true;
		for ( var i = 0; i < selectFields.length; i++) {
			if(!$(gui.form).validate().element(selectFields[i]) && valid){
				valid = false;
			}
		}
		return valid;
	};
	this.submissionHandler = function(clickEvent){
		var thisForm = $(gui.form);
		var message = {header:{}, payload:{formulario:{"id":thisForm.attr("id"),data:[]}}};
		var submit = false;
		if(gui.renderGrid){
//			console.log("Retrieving Grid data list");
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
				gui.displayWarning("No se encuentran registros para guardar.");
				console.warn("No data in grid");
				gui.validateForm();
			}
		}else{
			var btn = clickEvent.target;
			var draft = $(btn).attr("draft");
			if(draft && draft=="true"){
				gui.cleanFormValidations();
				message.payload.formulario.data = gui.retrieveFormFieldData();
				submit = true;
			}else{
				if(gui.validateForm()){
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
							gui.displaySuccess("Formulario guardado ("+data.result.id+").")	
							gui.cleanFormValidations();
							gui.resetForm();
							if(gui.renderGrid){
								gui.grid.element.dataTable().fnClearTable();
							}
						}else if(data.result.type == "ERROR"){
							gui.displayError("Ha ocurrido un error en el servidor de persistencia<br/>"+data.result.msg);
						}else{
							gui.displayError("Ha ocurrido un error no indicado en el servidor de persistencia");
						}
					}else{
						if(!data.success){
							gui.displayError("Error Remoto, contacte a su administrador.");
							console.group("ERROR REMOTO DETECTADO");
							console.warn("Error:"+data.msg);
							console.log("Objeto de respuesta",data.remote);
							console.groupEnd();
						}
					}
				},
				error:function(xhr,statusStr,errorStr){
					gui.displayError("Error Remoto, contacte a su administrador.");
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
				if(push){
					relevants.push(relevant);
//					console.log("relevant added",relevant);
				}
			}else{
				relevants = [relevant];
			}
			field.data("relevants",relevants);
		}
	};
	this.removeRelevant = function(field,relevant){
		if(field && relevant){
			var relevants = field.data("relevants");
			if(relevants){
				for(var i = 0;i < relevants.length;i++){
					var r = relevants[i];
					if(relevant.tutor.attr("name") == r.tutor.attr("name")){
//						console.log("relevant removed:",r);
						relevants.splice(i,1);
						break;
					}
				}
			}
			field.data("relevants",relevants);
		}
	};
	this.addDependant = function(tutor,dependant){
		if(tutor && dependant){
			var dependants = gui.getDependants(tutor);
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
	this.removeDependant = function(tutor,dependant){
		if(tutor && dependant){
			var dependants = gui.getDependants(tutor);
			if(dependants){
				for(var i = 0;i < dependants.length;i++){
					var d = dependants[i];
					if(dependant.attr("name") == d.attr("name")){
						dependants.splice(i,1);
						break;
					}
				}
			}
			tutor.data("dependant",dependants);
		}
	};
	this.getDependants = function(field){
		if(field!=undefined && field!=null){
			var dependants = field.data("dependant");
			if(dependants!=undefined && dependants !=null){
				return dependants; 	
			}else return [];
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
		if(ancestor && ancestor.is("select") || ancestor.attr("data-type-xml")=="select2"){
//			console.log(ancestor.attr("name"),ancestor.data());
			var isChecking = ancestor.data("isCheckingRelevants");
			if(!isChecking){
				ancestor.data("isCheckingRelevants",true);
				ancestor.on("change",{ me:ancestor }, function(event){
//					console.log("change fired from ancestor and captured from relevant");
					var _this = event.data.me;
					/*retrieve all dependant field of _this field*/
					var dependants = _this.data("dependant");
//					console.log(dependants);

					for ( var i = 0; i < dependants.length; i++) {
						var dependant = dependants[i];
						dependant.data("renderLogic")(dependant);
					}
				});
			}
		}
	};
	this.resetSelect = function(field){
		if(field.is("select")){
			var option = field.children("option[value='-1']").clone();
			field.empty();
			option.appendTo(field);
		}  
	};
	this.redrawSelect = function(field,data){
		gui.resetHierarchy([field]);
		for ( var count = 0; count < data.result.length; count++) {
			var o = data.result[count];
			var option = $("<option></option>");
			option.attr("value",o.id);
			option.text(o.nombre);
			field.append(option);
		}
		if($.browser.msie) field.hide().show();
	};
	this.remoteReqHandler = function(field, url, data) {
//		console.log(data);
		if (data.success) {
			gui.redrawSelect(field, data);
			gui.cache.store(url, data);
			field.trigger("remote_complete");
		} else {
			console.warn("No se obtuvo una lista de elementos para agregar al campo "
					+ field.attr("name"));
		}
	};
	this.resetHierarchy = function(fields) {
		for ( var i = 0; i < fields.length; i++) {
			var field = fields[i];
			if (field.is("select") || field.attr("data-type-xml") == "select2") {
				field.select2("data", null);
			} else {
				if (field.is("input[type='text']")) {
					el = field.closest("label");
					field.val("");
				} else if (field.is("input[type='radio']")
						|| field.is("input[type='checkbox']")) {
					el = field.closest("fieldset").closest("label");
				}
				if (el){
					//FIXME: fijarse en que esconda bien los campos
//					console.log("hidding el "+field.attr("name"));
//					el.hide();
				}
			}
			var dependants = field.data("dependant");
			if (dependants) {
				gui.resetHierarchy(dependants);
			}
		}
	};
	this.completeForm = function(record, fields){
		for ( var i = 0; i < fields.length; i++) {
			var field = $(fields[i]);
			var fieldCleanName = gui.getCleanFieldName(field.attr("name"),record.instance);
			
			if(field.is("select") ){
				if(record[fieldCleanName].value == null){
					field.select2("data",null);
				}else{
					field.select2("val",record[fieldCleanName].value);
				}
			}else if(field.attr("data-type-xml")=="select2"){
				if(record[fieldCleanName].value == null){
					field.select2("data",null);
				}else{
					field.select2("data",{id:record[fieldCleanName].value,text:record[fieldCleanName].label});
				}
			}else{ 
				field.val(record[fieldCleanName]);
			}
			field.trigger("change");
		}
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
		getData : function(){
			return this.element.dataTable().fnGetData();
		},
		getRowData : function(rowIndex){
			return this.element.dataTable().fnGetData(rowIndex);
		},
		setupEditClck: function(){
			$("input[type~='button'][repeat-action='edit']").unbind("click");
			$("input[type~='button'][repeat-action='edit']").click(function(evt){
				var rowIndex =  gui.grid.element.dataTable().fnGetPosition($(evt.target).closest('tr').get(0));
				console.log(rowIndex);
				var record = gui.grid.getRowData(rowIndex);
//				console.log(record);
				var fields = gui.fieldsets[record.instance].fields;
				gui.blockUI("Cargando...<br>Espere por favor...",true);
				gui.resetForm();
				gui.completeForm(record,fields);
				
				for ( var i = 0; i < fields.length; i++) {
					var field = $(fields[i]);
					field.data("renderLogic")(field);
				}
				gui.grid.editing = rowIndex;
				gui.unblockUI(true);
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
			var tmpHash = "";
			var hash;
			for ( var i = 0; i < fieldset.fields.length; i++) {
				var field =$(fieldset.fields[i]);
				var attribute = gui.getCleanFieldName(field.attr("name"),fieldsetInstance);
				if(field.is(":visible") || field.attr("data-type-xml")=="select2"){
					var value = field.val();
//					console.log("Value",field.attr("name"),":_"+value+"_");
					if(value!=undefined && value!=null && $(gui.form).validate().element(field)){
						if(field.is("select")){
							var o;
							if(value.trim()!=""){
								o = field.children("option:selected");
								record[attribute] = {label:o.text(),value:value};
								tmpHash = tmpHash + value; 
							}else{
								record[attribute] = {label:" ",value:null};
							}
						}else if(field.attr("data-type-xml")=="select2"){
							var data; 
							if(value.trim()!=""){
								data = field.select2("data");
								record[attribute] = {label:data.text,value:data.id};
								tmpHash = tmpHash + data.id;
							}else{
								record[attribute] = {label:" ",value:null};
							}
						}else{
							record[attribute] = value;
							tmpHash = tmpHash + value;
						}
						hash = new jsSHA(tmpHash,"TEXT");
						
						record["firma_digital"] = hash.getHash("SHA-1","HEX");
						fields.push(field);
					} else {
						commit = false;
						console.warn("Could not commit due to field",field);
						break;
					}
				}else{
					if(field.is("select")) record[attribute] = {label:" "};
					else record[attribute] = " ";
				}
			}
			record.instance = fieldset.instance;
			var storedData = this.getData();
//			console.info("new record",record.signature);
			for ( var i = 0; i < storedData.length; i++) {
				var storedRecord = storedData[i];
//				console.info("stored record",storedRecord.signature);
				if(storedRecord.firma_digital == record.firma_digital){
					gui.displayWarning("Ya se encuentra agregado un registro con esos valores");
					commit = false;
					break;
				};
			}
			if(commit){
				gui.cleanFormValidations();
				gui.resetFields(fields);
				for ( var i = 0; i < fieldset.fields.length; i++) {
					var af = $(fieldset.fields[i]);
//					console.log("campo a checkear visualizacion",af);
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