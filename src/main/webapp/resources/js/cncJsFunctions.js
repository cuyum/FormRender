jQuery.browser = {};
jQuery.browser.mozilla = /mozilla/.test(navigator.userAgent.toLowerCase()) && !/webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
jQuery.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());

$.blockUI.defaults = { 
    title: null,        // title string; only used when theme == true 
    draggable: true,    // only used when theme == true (requires jquery-ui.js to be loaded) 
 
    theme: false, // set to true to use with jQuery UI themes 
 
    // styles for the message when blocking; if you wish to disable 
    // these and use an external stylesheet then do this in your code: 
    // $.blockUI.defaults.css = {}; 
    css: { 
	    width: '20%', 
        top: '40%', 
        left: '35%', 
        margin: 0,
        border: 'none', 
        padding: '15px',
        textAlign: 'center', 
        backgroundColor: '#000', 
        '-webkit-border-radius': '10px', 
        '-moz-border-radius': '10px', 
        opacity: .6,
        cursor: 'wait', 
        color: '#fff' 
    },
 
    // minimal style set used when themes are used 
    themedCSS: { 
        width:  '30%', 
        top:    '40%', 
        left:   '35%' 
    }, 
 
    // styles for the overlay 
    overlayCSS:  { 
        backgroundColor: '#fff', 
        opacity:         0.7, 
        cursor:          'wait' 
    }, 
 
    // style to replace wait cursor before unblocking to correct issue 
    // of lingering wait cursor 
    cursorReset: 'default', 
 
    // styles applied when using $.growlUI 
    growlCSS: { 
        width:    '350px', 
        top:      '10px', 
        left:     '', 
        right:    '10px', 
        border:   'none', 
        padding:  '5px', 
        opacity:   0.6, 
        cursor:    null, 
        color:    '#fff', 
        backgroundColor: '#000', 
        '-webkit-border-radius': '10px', 
        '-moz-border-radius':    '10px' 
    }, 
     
    // IE issues: 'about:blank' fails on HTTPS and javascript:false is s-l-o-w 
    // (hat tip to Jorge H. N. de Vasconcelos) 
    iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank', 
 
    // force usage of iframe in non-IE browsers (handy for blocking applets) 
    forceIframe: false, 
 
    // z-index for the blocking overlay 
    baseZ: 1000, 
 
    // set these to true to have the message automatically centered 
    centerX: true, // <-- only effects element blocking (page block controlled via css above) 
    centerY: true, 
 
    // allow body element to be stetched in ie6; this makes blocking look better 
    // on "short" pages.  disable if you wish to prevent changes to the body height 
    allowBodyStretch: true, 
 
    // enable if you want key and mouse events to be disabled for content that is blocked 
    bindEvents: true, 
 
    // be default blockUI will supress tab navigation from leaving blocking content 
    // (if bindEvents is true) 
    constrainTabKey: true, 
 
    // fadeIn time in millis; set to 0 to disable fadeIn on block 
    fadeIn:  200, 
 
    // fadeOut time in millis; set to 0 to disable fadeOut on unblock 
    fadeOut:  400, 
 
    // time in millis to wait before auto-unblocking; set to 0 to disable auto-unblock 
    timeout: 0, 
 
    // disable if you don't want to show the overlay 
    showOverlay: true, 
 
    // if true, focus will be placed in the first available input field when 
    // page blocking 
    focusInput: true, 
 
    // suppresses the use of overlay styles on FF/Linux (due to performance issues with opacity) 
    // no longer needed in 2012 
    // applyPlatformOpacityRules: true, 
 
    // callback method invoked when fadeIn has completed and blocking message is visible 
    onBlock: null, 
 
    // callback method invoked when unblocking has completed; the callback is 
    // passed the element that has been unblocked (which is the window object for page 
    // blocks) and the options that were passed to the unblock call: 
    //   onUnblock(element, options) 
    onUnblock: null, 
 
    // don't ask; if you really must know: http://groups.google.com/group/jquery-en/browse_thread/thread/36640a8730503595/2f6a79a77a78e493#2f6a79a77a78e493 
    quirksmodeOffsetHack: 4, 
 
    // class name of the message block 
    blockMsgClass: 'blockMsg', 
 
    // if it is already blocked, then ignore it (don't unblock and reblock) 
    ignoreIfBlocked: false 
}; 

if (jQuery.browser.msie) {
	console = {
		log:function(){}, 
		warn:function(){}, 
		info:function(){},
		error:function(){},
		group:function(){},
		debug:function(){},
		groupEnd:function(){}
	};
}
if (typeof window.console.debug == "undefined") {console.debug = console.log;}

var FormRender = new function(){
	this.fieldsets = [];
	this.repeatCount = undefined;
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
		if(fieldsetInstance && !isNaN(fieldsetInstance)){
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
	this.grid = {
		editing : -1,
		headers : [],
		data : [],
		model:{
			fields:[],
			values:[],
			formulario:"",
			instance:-1
		},
		element : $('<table id="repeat-grid" class="table table-striped"></table>'),
		getRowData : function(rowIndex){
			return this.element.dataTable().fnGetData(rowIndex);
		},
		setupEditClck: function(){
			$("input[type~='button'][repeat-action='edit']").unbind("click");
			$("input[type~='button'][repeat-action='edit']").click(function(evt){
				var rowIndex =  FormRender.grid.element.dataTable().fnGetPosition($(evt.target).closest('tr').get(0));
				var record = FormRender.grid.getRowData(rowIndex);
				var fields = FormRender.fieldsets[record.instance].fields;
				
				$.blockUI({message:"Cargando...<br>Espere por favor..."});
				var unblock = $("<span id='unblockable'/>");
				unblock.appendTo("body");
				
				function loopWithDelay(i, fields) {
					var delayTime = 1000;
					var field = $(fields[i]);
					if (!field.is("select")) {
						delayTime = 0;
					}
					setTimeout(function() {
						field.val(record.values[i]);
						field.trigger("change");

						i++;
						if (i < fields.length) {
							loopWithDelay(i, fields, delayTime);
						}else{
							$("#unblockable").remove();
							$.unblockUI();
						}
					}, delayTime);
				}
				
				loopWithDelay(0,fields);
				
				
				for ( var i = 0; i < fields.length; i++) {
					var field = $(fields[i]);
					field.data("renderLogic")(field);
				}
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
			
			var record = $.extend(true,{},this.model);
			
			if(FormRender.repeatCount && FormRender.repeatCount>1) 	record.formulario = fieldset.title;
			var commit = true; 
			for ( var i = 0; i < fieldset.fields.length; i++) {
				var field =$(fieldset.fields[i]);
				var attribute = FormRender.getCleanFieldName(field.attr("name"),fieldsetInstance);
				record.fields.push(field.attr("name"));
				if(field.is(":visible")){
					var value = field.val();
					if(value!=undefined && value!=null && $(FormRender.form).validate().element(field)){
						if(field.is("select")){
							var o = field.children("option:selected");
							record[attribute] = {label:o.text(),value:value};
						}else{
							record[attribute] = value;
						}
						record.values.push(value);
					} else {
						commit = false;
						break;
					}
				}else{
					record[attribute] = " ";
					record.values.push(" ");
				}
			}
			record.instance = fieldset.instance;
			if(commit){
				FormRender.form.reset();
				for ( var i = 0; i < fieldset.fields.length; i++) {
					var af = $(fieldset.fields[i]);
					af.data("renderLogic")(af);
				}
				if(this.editing==-1){
					this.element.dataTable().fnAddData(record,true);
				}else{
					this.element.dataTable().fnUpdate(reg, this.editing);
					this.editing = -1;
				}
				this.setupEditClck();
			}
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
			if(FormRender.repeatCount && FormRender.repeatCount>1)
				this.headers.push({"sTitle":" ","mData":"formulario"});
			for ( var i = 0 ; i<FormRender.fieldsets[0].fields.length;i++) {
				var f = $(FormRender.fieldsets[0].fields[i]);
				var attribute = FormRender.getCleanFieldName(f.attr("name"));
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
				"aaData": FormRender.grid.data,
		        "aoColumns": FormRender.grid.headers
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

var getURLParameter = function(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
};

