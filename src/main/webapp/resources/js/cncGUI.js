/*Singleton GUI logic*/
var gui = new function() {
	this.initialFieldset = undefined;
	this.fieldsets = [];
	this.repeatCount = undefined;
	this.renderGrid = false;
	this.renderTotal = false;
	this.renderTotalizadores = false;
	this.renderTotalizadoresIngresados = false;
	this.renderPorcentaje = false;
	this.readonly = false;
	this.MESSAGES = {
		INFO : "alert-info",
		WARN : "",
		SUCCESS : "alert-success",
		ERROR : "alert-error"
	};
	this.toNumber = function(value) {
		if (value && !isNaN(value)) {
			return parseFloat(value);
		} else if (isNaN(value)) {
			console
					.warn("Alphanumeric String passed as an argument, only String with Numeric characters allowed as argument");
			return value;
		} else {
			if (value == undefined || value == null || value.trim() == "") {
				console
						.warn("Undefined or Null value argument passed to \"toNumber()\", returning same value");
			} else {
				console
						.error("Unidentifiably value passed as a parameter to \"gui.toNumber()\", returning same value");
			}
			return value;
		}
	};
	this.replaceFieldInFieldset = function(newField, oldField, fieldset) {
		var fs;
		if (fieldset && fieldset.instance) {
			fs = fieldset;
		} else
			fs = this.fieldsets[0];

		for ( var i = 0; i < fs.fields.length; i++) {
			var f = fs.fields[i];
			if ($(f).attr("name") == oldField.attr("name")) {
				fs.fields.splice(i, 1, newField);
				break;
			}
		}
	};
	this.validateForm = function() {
		var validFields = false;
		var validSelects = false;

		validFields = $(gui.form).valid();
		validSelects = gui.validateRemoteSelects();

		return validFields && validSelects;

	};
	this.boot = function() {
		this.form = document.forms[0];

		setupValidationDefaults();

		/*
		 * Setup Version var versionElement = $("#version"); var versionValue =
		 * gui.getURLParameter("version"); if(versionElement && versionValue)
		 * versionElement.text("Versi\u00F3n: "+versionValue);
		 */

		/*
		 * Para repeat se necesitará duplicar todo lo que se encuentre dentro
		 * del fieldset.jr-repeat actualmente se está pasando un listado de
		 * campos al setup de validaciones y se están buscando globalmente los
		 * campos por nombres. Se deberá pasar los campos de un fieldset
		 * específico y las búsquedas internas deberán dejar de hacerse
		 * globalmente y deberán buscarse dentro del fieldset suministrado.
		 */
		this.initialFieldset = $("fieldset.jr-repeat");

		this.repeatCount = this.getURLParameter("repeat");
		this.renderGrid = this.initialFieldset.hasClass("grilla");
		this.renderTotal = this.initialFieldset.hasClass("sumarizada");
		this.renderTotalizadores = this.initialFieldset.hasClass("calculados");
		this.renderTotalizadoresIngresados = this.initialFieldset
				.hasClass("ingresados");
		this.readonly = gui.getURLParameter("readonly");

		// if(this.renderTotalizadores){
		// $("div[class~='form-actions']").find("[totalizador-action='calculados']").not(":visible").show();
		// }
		if (this.renderTotalizadoresIngresados) {
			this.renderTotalizadores = true;
		}
		if(!this.renderTotal){
			this.renderPorcentaje = true;
		}

		/** ******* SETUP GUI JQUERY PLUGIN FUNCTIONALITY ******** */
		this.setupPlugins();

	};
	this.setupPlugins = function() {
		/** ********** SCROLL TO ************* */
		$.fn.scrollTo = function() {
			var object = $(this);
			if (object.attr("data-type-xml") != undefined
					&& object.attr("data-type-xml") == "select2") {
				object = object.parent();
			}
			console.info("scrolling top from", object.offset().top + 'px');
			$('html, body').animate({
				scrollTop : object.offset().top + 'px'
			}, 'fast');
			return this; // for chaining...
		};

		/** ********** MARK INVALID ************* */
		$.fn.markInvalid = function(message) {
			var field = $(this);
			if (field.is("input")) {
				console.log("MARK INVALID CALLED ON ", field.attr("name"));
				if (!field.hasClass("error"))
					field.addClass("error");
				field.removeClass("valid");
				if (message && message.trim() != "") {
					var messageLabel = $(this).siblings("label.error");

					if (messageLabel.length > 1) {
						$.each(messageLabel, function(i, el) {
							el.remove();
						});
						messageLabel = [];
					}

					if (messageLabel.length == 1) {
						messageLabel.text(message);
					} else {
						messageLabel = $('<label for="' + field.attr("name")
								+ '" class="error"></label>');
						messageLabel.text(message);
						field.parent().append(messageLabel);
					}
				}
			}
			return this; // for chaining...
		};
	};
	this.setupDefaults = function() {
		$("select").select2();
		$("input[type='time']").timepicker();
		setupHints();
		if (this.readonly) {
			$("input[type='button'][draft]").parents("fieldset").remove();
		}
	};
	this.displayMessage = function(message, type) {
		if (message == undefined || message == null) {
			console.warn("No message supplied for message display");
			return;
		}
		if (type == null || type == undefined || type.trim() == "") {
			type = "";
		}

		var messageDiv = $("#internal-messages");
		var insert = true;
		$.each(messageDiv.children(),
				function(i, v) {
					if (insert == true
							&& $(v).children("#msg-text").text() == message) {
						insert = false;
					}
				});

		if (insert) {
			var alertDiv = $("<div data-dismiss='alert' class='fade alert "
					+ type
					+ "'><button  class='close' type='button'>×</button></div>");
			var m = alertDiv.append("<span id='msg-text'>" + message
					+ "</span>");
			messageDiv.append(m).scrollTo();
			alertDiv.addClass("in");
		}
	};
	this.displayWarning = function(message) {
		this.displayMessage(message);
	};
	this.displayInfo = function(message) {
		this.displayMessage(message, this.MESSAGES.INFO);
	};
	this.displaySuccess = function(message) {
		this.displayMessage(message, this.MESSAGES.SUCCESS);
	};
	this.displayError = function(message) {
		this.displayMessage(message, this.MESSAGES.ERROR);
	};
	this.closeAllMessages = function() {
		$("#internal-messages").children().each(function(count, el) {
			$(el).fadeOut();
		});
	};
	this.blockUI = function(message, unblockable) {
		if (!$("#unblockable").is("span")) {
			if (message) {
				$.blockUI({
					message : message
				});
			} else {
				$.blockUI({
					message : "Cargando...<br>Espere por favor..."
				});
			}
		}
		if (!$("#unblockable").is("span") && unblockable != undefined
				&& unblockable) {
			var unblock = $("<span id='unblockable'/>");
			unblock.appendTo("body");
		}
	};
	this.unblockUI = function(force) {
		if (force) {
			$("#unblockable").remove();
			$.unblockUI();
		} else {
			if (!$("#unblockable").is("span")) {
				$.unblockUI();
			}
		}
	};
	this.cache = {
		data : [],
		store : function(index, data) {
			this.data[index] = data;
			// console.info("Stored",data);
		},
		check : function(index) {
			var data = this.data[index];
			if (data != undefined) {
				return true;
			} else {
				// console.log("no data stored");
				return false;
			}
		},
		retrieve : function(index) {
			return this.data[index];
		},
		reset : function() {
			this.data = [];
			// console.log("Cleaning cache.");
		}
	};
	this.getURLParameter = function(name) {
		var par = decodeURIComponent((new RegExp('[?|&]' + name + '='
				+ '([^&;]+?)(&|#|;|$)').exec(location.search) || [ , "" ])[1]
				.replace(/\+/g, '%20'))
				|| null;
		return par;
	};
	this.cleanFormValidations = function() {
		$("[class~='error']").siblings("label[class='error']").remove();
		$("[class~='error']").removeClass("error");
	};
	this.isCNCNumberField = function(field) {
		var data_type = field.attr("data-type-xml");
		if (data_type == null || data_type == undefined) {
			return false;
		} else {
			if (data_type == "decimal")
				return true;
			if (data_type == "int")
				return true;

			return false;
		}
	};
	this.resetForm = function() {
		gui.form.reset();
		$("select").each(function(count, item) {
			if (!$(item).attr("name") == "repeat-grid_length")
				$(item).select2("data", null);
		});
		$("[data-type-xml='select2']").each(function(i, item) {
			$(item).select2("val", "");
		});
	};
	this.resetFields = function(fields) {

		for ( var i = 0; i < fields.length; i++) {

			var field = $(fields[i]);
			if (field.is("select")) {
				field.select2("val", "-1");
			} else if (field.attr("data-type-xml") == "select2")
				field.select2("data", null);
			else
				field.val('').removeAttr('checked');
		}
	};
	this.retrieveFormFieldData = function() {
		var data = [];
		for ( var i = 0; i < gui.fieldsets.length; i++) {
			var kvpair = {
				instance : i
			};
			for ( var j = 0; j < gui.fieldsets[i].fields.length; j++) {
				var field = $(gui.fieldsets[i].fields[j]);
				var key = gui.getCleanFieldName(field.attr("name"),
						kvpair.instance);
				var val;

				if (field.is("select")) {
					var os = field.children("option:selected");
					if (os != null && os != undefined) {
						val = {
							label : os.text(),
							value : field.val()
						};
						kvpair[key] = val;
					}
				} else if (field.attr("data-type-xml") == "select2") {
					var ds = field.select2("data");
					if (ds != null && ds != undefined) {
						val = {
							label : ds.text,
							value : ds.id
						};
						kvpair[key] = val;
					}
				} else {
					if (this.isCNCNumberField(field)) {
						val = this.toNumber(field.val());
					} else {
						val = field.val();
					}
					kvpair[key] = val;
				}
			}
			data.push(kvpair);
		}
		return data;
	};
	this.validateRemoteSelects = function() {
		var selectFields = $("input[class~='select2-offscreen']:hidden").not(".select2-focusser");
		var valid = true;
		for ( var i = 0; i < selectFields.length; i++) {
			if (!$(gui.form).validate().element(selectFields[i]) && valid) {
				valid = false;
			}
		}
		return valid;
	};
	this.executeSubmission = function(url, message) {
		return $
				.ajax({
					async : false,
					type : "POST",
					contentType : "application/x-www-form-urlencoded; charset=utf-8",
					url : "/" + formRenderContext + "/rest/service/submit",
					data : {
						"submit_data" : JSON.stringify(message),
						"url" : url
					},
					success : function(data, statusStr, xhr) {
						if (data.result && data.result.type) {
							if (data.success) {
								if (data.result.msg != undefined)
									gui.displaySuccess(data.result.msg);
								else
									gui.displaySuccess("Formulario guardado.");
							} else {
								if (data.result.msg != undefined)
									gui
											.displayError("Ha ocurrido un error en el servidor de persistencia<br/>"
													+ data.result.msg);
								else
									gui
											.displayError("Ha ocurrido un error no indicado en el servidor de persistencia");
							}
						} else {
							console.group("ERROR REMOTO DETECTADO");
							if (data.result != undefined
									&& data.result.msg != undefined) {
								gui.displayError(data.result.msg);
								console.warn("Error:" + data.result.msg);
							} else {
								if (data.msg != undefined) {
									console.warn("Remote Error: \"" + data.msg
											+ "\"");
								} else {
									console.warn("No remote error defined");
								}
								gui
										.displayError("Error remoto, contacte a su administrador.");
							}
							console.log("Objeto de respuesta", xhr);
							console.groupEnd();
						}
					},
					error : function(xhr, statusStr, errorStr) {
						gui
								.displayError("El servicio de persistencia no se encuentra disponible, contacte a su administrador.");
						console.error("Error en submit:" + statusStr);
					}
				});
	};
	this.saveDraft = function(message) {
		var thisForm = $(gui.form);
		if (thisForm.attr("submit-url")) {
			this.executeSubmission(thisForm.attr("submit-url") + "/draft",
					message);
		} else {
			gui.displayError("Error local, contacte a su administrador.");
			console.error("No se encuentra daclarada la URL de submission");
		}
	};
	this.saveFinal = function(message) {

		var result = gui.validarPeriodicidad();

		if (result == 1)
			gui.displayWarning("Debe agregar como mínimo un registro para cada mes.");
		else if (result == 2)
			gui.displayWarning("Debe agregar como mínimo un registro para cada trimestre.");
		else {
			// clickEvent.preventDefault();
			bootbox.confirm(
							"Esta acción implica que ha completado la carga de este formulario. " +
							"El estado del mismo será actualizado a \"Listo\" ¿Desea continuar?",
							function(confirmed) {
								if (confirmed) {
									var thisForm = $(gui.form);
									if (thisForm.attr("submit-url")) {
										gui.executeSubmission(thisForm.attr("submit-url"),message).done(
														function(data) {
															if (data.success) {
//																gui.resetForm();
																gui.cleanFormValidations();
																if (gui.renderGrid) {
//																	gui.grid.element.dataTable().fnClearTable();
																}
															}
														});
									} else {
										gui
												.displayError("Error local, contacte a su administrador.");
										console
												.error("No se encuentra daclarada la URL de submission");
									}
								}
							});
		}

	};

	var tipoFormulario = function() {

		var url = location.href;

		var index = 0;
		var result = 0;

		if (url.indexOf("ict") != null && url.indexOf("ict") != -1) {
			index = url.indexOf("ict");
			result = index + 3;
		}

		var form = url.substring(index, result);
		return form;
	};

	this.validarPeriodicidad = function() {

		var faltaRegistro = 0;

		if (tipoFormulario() == "ict") {
			var dl = gui.grid.element.dataTable().fnGetData();
			var dataList = [];

			var contTrim = [ 0, 0, 0, 0 ];
			var contTrim1 = [ 0, 0, 0 ];
			var contTrim2 = [ 0, 0, 0 ];
			var contTrim3 = [ 0, 0, 0 ];
			var contTrim4 = [ 0, 0, 0 ];
			var contAnual = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ];

			for ( var i = 0; i < dl.length; i++) {
				var data = dl[i];

				if (data.periodo_considerado.value < 13)
					contAnual[data.periodo_considerado.value - 1]++;
				if (data.periodo_considerado.value >= 13
						&& data.periodo_considerado.value < 16)
					contTrim1[data.periodo_considerado.value - 13]++;
				if (data.periodo_considerado.value >= 16
						&& data.periodo_considerado.value < 19)
					contTrim2[data.periodo_considerado.value - 16]++;
				if (data.periodo_considerado.value >= 19
						&& data.periodo_considerado.value < 22)
					contTrim3[data.periodo_considerado.value - 19]++;
				if (data.periodo_considerado.value >= 22
						&& data.periodo_considerado.value < 25)
					contTrim4[data.periodo_considerado.value - 22]++;
				if (data.periodo_considerado.value >= 25
						&& data.periodo_considerado.value < 29)
					contTrim[data.periodo_considerado.value - 25]++;

				dataList.push(data);
			}

			var url = location.href;
			var index = url.indexOf("?");
			var parameter = "periodicidad";
			index = url.indexOf(parameter, index) + parameter.length;
			if (url.charAt(index) == "=") {
				var result = url.indexOf("&", index);
				if (result == -1)
					result = url.length;
			}
			var periodicidad = url.substring(index + 1, result);

			if (periodicidad == "anual")
				for ( var i = 0; i < 12; i++)
					if (contAnual[i] == 0)
						faltaRegistro = 1;
			if (periodicidad == "trimestral_1")
				for ( var i = 0; i < 3; i++)
					if (contTrim1[i] == 0)
						faltaRegistro = 1;
			if (periodicidad == "trimestral_2")
				for ( var i = 0; i < 3; i++)
					if (contTrim2[i] == 0)
						faltaRegistro = 1;
			if (periodicidad == "trimestral_3")
				for ( var i = 0; i < 3; i++)
					if (contTrim3[i] == 0)
						faltaRegistro = 1;
			if (periodicidad == "trimestral_4")
				for ( var i = 0; i < 3; i++)
					if (contTrim4[i] == 0)
						faltaRegistro = 1;
			if (periodicidad == "trimestral")
				for ( var i = 0; i < 4; i++)
					if (contTrim[i] == 0)
						faltaRegistro = 2;
		}
		return faltaRegistro;
	};

	this.submissionHandler = function(clickEvent) {
		/* instancia del mensaje por defecto */
		var message = {
			header : {},
			payload : {
				formulario : {
					"id" : $(gui.form).attr("id"),
					data : []
				}
			}
		};
		/* verifico si tiene recordId para pasar en cabacera */
		if (gui.loadDataId != null && gui.loadDataId != undefined) {
			message.header.id = gui.loadDataId;
		}
		/* verifico si tiene callback_id para pasar en cabecera */
		var callbackId = gui.getURLParameter("callback_id");
		if (callbackId != null && callbackId != undefined) {
			message.header.callback_id = callbackId;
		} else {
			message.header.callback_id = "";
		}

		/*
		 * Reconozco que tipo de accion se quiere llevar a cabo
		 */
		var btn = clickEvent.target;
		var draft = $(btn).attr("draft") == "true";

		$(btn).attr("disabled", "disabled");

		/*
		 * si hay una grilla no es necesario validar nada simplemente se extraen
		 * los datos de la grilla para crear el batch de registros que enviara
		 */
		if (gui.renderGrid) {
			// console.log("Retrieving Grid data list");
			var dl = gui.grid.element.dataTable().fnGetData();
			if (dl.length > 0) {
				var dataList = [];
				var dataFinal = null;

				for ( var i = 0; i < dl.length; i++) {
					var data = dl[i];
					if (data["values"] != undefined)
						delete data["values"];
					if (data["fields"] != undefined)
						delete data["fields"];
					if (gui.repeatCount && gui.repeatCount <= 1) {
						if (data["item"] != undefined)
							delete data["item"];
					}
					dataList.push(data);
				}

				if (gui.renderTotalizadores) {
					var totalizados = gui.gridTotalizadora.getData();
					console.log("totalizados", totalizados);
					/*
					 * si hay totalizadores ingresado debe validarse que se
					 * hayan ingresado todos, de lo contrario se debe levantar
					 * la ventana con un mensaje que obligue a agregar todos los
					 * totalizadores ingresados
					 */
					if (gui.renderTotalizadoresIngresados) {

						var totalizadores = gui.gridTotalizadora.totalizadores;
						console.log("totalizadores", totalizadores);
						var fail = true;
						/*
						 * se verifica que todos los totalizados tengan un valor
						 * ingresado
						 */
						for ( var i = 0; i < totalizados.length; i++) {
							console.log("(i) totalizado:", i);
							for ( var j = 0; j < totalizadores.length; j++) {
								console.log("(j) totalizador:", j);
								var ingresado = totalizados[i][totalizadores[j].nombre
										+ "_ingresado"];
								fail = (ingresado == null || ingresado == "-"
										|| isNaN(ingresado) || $
										.trim(ingresado) == "");
								console.log(totalizados[i], "fail " + fail);
							}
						}

						if (fail) {
							gui.gridTotalizadora.getButton("ingresados")
									.click();
							$(btn).removeAttr("disabled");
							return;
						}
					}

					dataFinal = [ {
						registros : dataList,
						sumarizados : totalizados
					} ];
				} else {
					dataFinal = dataList;
				}

				message.payload.formulario.data = dataFinal;
				draft == true ? gui.saveDraft(message) : gui.saveFinal(message);


			} else {
				gui.displayWarning("No se encuentran registros para guardar.");
				console.warn("No data in grid");
				gui.validateForm();
			}

			/*
			 * si no hay grilla es necesario extraer los datos del formulario
			 * crudo y si es un draft, no hace falta validar caso contrario se
			 * debera validar todo el formulario antes de realizar el submit

			 */
		} else {
			if (draft == true) {
				message.payload.formulario.data = gui.retrieveFormFieldData();
				gui.saveDraft(message);
			} else {
				if (gui.validateForm()) {
					message.payload.formulario.data = gui
							.retrieveFormFieldData();
					gui.saveFinal(message);
				} else {
					gui.displayWarning("El formulario posee errores.");
				}
			}
		}
		$(btn).removeAttr("disabled");
	};
	this.addRelevant = function(field, relevant) {
		if (field && relevant) {
			var relevants = field.data("relevants");
			if (relevants) {
				var push = true;
				for ( var i = 0; i < relevants.length; i++) {
					var r = relevants[i];
					if (relevant.tutor.attr("name") == r.tutor.attr("name")) {
						r.value = [ r.value, relevant.value ];
						relevants.splice(i, 1, r);
						push = false;
						break;
					}
				}
				if (push) {
					relevants.push(relevant);
					// console.log("relevant added",relevant);
				}
			} else {
				relevants = [ relevant ];
			}
			field.data("relevants", relevants);
		}
	};
	this.removeRelevant = function(field, relevant) {
		if (field && relevant) {
			var relevants = field.data("relevants");
			if (relevants) {
				for ( var i = 0; i < relevants.length; i++) {
					var r = relevants[i];
					if (relevant.tutor.attr("name") == r.tutor.attr("name")) {
						// console.log("relevant removed:",r);
						relevants.splice(i, 1);
						break;
					}
				}
			}
			field.data("relevants", relevants);
		}
	};
	this.addDependant = function(tutor, dependant) {
		if (tutor && dependant) {
			var dependants = gui.getDependants(tutor);
			if (dependants) {
				var push = true;
				for ( var i = 0; i < dependants.length; i++) {
					var d = dependants[i];
					if (dependant.attr("name") == d.attr("name")) {
						push = false;
						break;
					}
				}
				if (push)
					dependants.push(dependant);
			} else {
				dependants = [ dependant ];
			}
			tutor.data("dependant", dependants);
		}
	};

	this.addDependencia = function(tutor, dependant) {
		if (tutor && dependant) {
			var dependants = gui.getDependencias(tutor);
			if (dependants) {
				var push = true;
				for ( var i = 0; i < dependants.length; i++) {
					var d = dependants[i];
					if (dependant.attr("name") == d.attr("name")) {
						push = false;
						break;
					}
				}
				if (push)
					dependants.push(dependant);
			} else {
				dependants = [ dependant ];
			}
			tutor.data("dependencia", dependants);
		}
	};

	this.removeDependant = function(tutor, dependant) {
		if (tutor && dependant) {
			var dependants = gui.getDependants(tutor);
			if (dependants) {
				for ( var i = 0; i < dependants.length; i++) {
					var d = dependants[i];
					if (dependant.attr("name") == d.attr("name")) {
						dependants.splice(i, 1);
						break;
					}
				}
			}
			tutor.data("dependant", dependants);
		}
	};
	this.getDependants = function(field) {
		if (field != undefined && field != null) {
			var dependants = field.data("dependant");
			if (dependants != undefined && dependants != null) {
				return dependants;
			} else
				return [];
		}
	};
	this.getDependencias = function(field) {
		if (field != undefined && field != null) {
			var dependants = field.data("dependencia");
			if (dependants != undefined && dependants != null) {
				return dependants;
			} else
				return [];

		}
	};
	this.getField = function(name, fieldset) {
		var ancestor = undefined;
		if (name) {
			var ancestorSelector = "[name~='" + name.trim() + "']";
			;
			if (fieldset.instance != undefined) {
				ancestorSelector = "[name~='" + name.trim() + "_"
						+ fieldset.instance + "']";
			}
			ancestor = $(ancestorSelector);
		}
		return ancestor;
	};
	this.getCleanFieldName = function(fullName, fieldsetInstance) {
		if (fieldsetInstance != undefined && !isNaN(fieldsetInstance)) {
			fullName = fullName.replace("_" + fieldsetInstance, "");
		}
		if (fullName && fullName.trim() != "") {
			var idx = fullName.lastIndexOf("/");
			var cleanName = fullName.substring(idx + 1);
			return cleanName;
		}
		return "";
	};
	this.flagPerformRelevantCheck = function(ancestor) {
		if (ancestor && ancestor.is("select")
				|| ancestor.attr("data-type-xml") == "select2") {
			// console.log(ancestor.attr("name"),ancestor.data());
			var isChecking = ancestor.data("isCheckingRelevants");
			if (!isChecking) {
				ancestor.data("isCheckingRelevants", true);
				ancestor.on("change", {
					me : ancestor
				}, function(event) {
					// console.log("change fired from ancestor and captured from
					// relevant");
					var _this = event.data.me;
					/* retrieve all dependant field of _this field */
					var dependants = _this.data("dependant");
					// console.log(dependants);

					for ( var i = 0; i < dependants.length; i++) {
						var dependant = dependants[i];
						dependant.data("renderLogic")(dependant);
						_this.data("funcionVisibility")(_this, dependant,i+1);
					}
				});
			}
		}
	};

	this.flagPerformRequiredCheck = function(ancestor) {
		if (ancestor && ancestor.is("select")
				|| ancestor.attr("data-type-xml") == "select2") {

			var isChecking = ancestor.data("isCheckingRequired");
			if (!isChecking) {
				ancestor.data("isCheckingRequired", true);
				ancestor.on("change", {
					me : ancestor
				}, function(event) {
					// console.log("change fired from ancestor and captured from
					// required");
					var _this = event.data.me;
					/* retrieve all dependant field of _this field */
					var dependants = _this.data("dependencia");

					// console.log(dependants);

					/*
					 * Obtengo los INPUTS ocultos,que tienen el mismo nombre de
					 * los SELECTS
					 */
					var hiddens = gui.getHidden();

					for ( var i = 0; i < hiddens.length; i++) {
						hiddens[i].data("jr:constraint:remote", null);
						dependants.push(hiddens[i]);
					}

					for ( var i = 0; i < dependants.length; i++) {
						var dependant = dependants[i];
						if (dependant[0].nodeName == "SELECT")
							continue;
						dependant.data("funcionRequired")(_this, dependant);
						validationRequired(dependant);
					}
				});
			}
		}
	};
	
	

	this.getHidden = function() {
		var inputs = $(":hidden");
		var hiddens = [];
		for ( var i = 0; i < inputs.length; i++) {
			if (inputs[i].name === undefined)
				continue;
			if (inputs[i].name == "/ict4.1.1-E/meses/mes/provincia_0"
					|| inputs[i].name == "/ict4.1.1-E/meses/mes/partido_0"
					|| inputs[i].name == "/ict4.1.1-E/meses/mes/localidad_0") {

				hiddens.push(new jQuery.fn.init(inputs[i], inputs[i]));
			}
		}
		return hiddens;
	};

	this.resetSelect = function(field) {
		if (field.is("select")) {
			var option = field.children("option[value='-1']").clone();
			field.empty();
			option.appendTo(field);
		}
	};
	this.redrawSelect = function(field, data) {
		gui.resetHierarchy([ field ]);
		for ( var count = 0; count < data.result.length; count++) {
			var o = data.result[count];
			var option = $("<option></option>");
			option.attr("value", o.id);
			option.text(o.nombre);
			field.append(option);
		}
		if ($.browser.msie)
			field.hide().show();
	};
	this.remoteReqHandler = function(field, url, data) {
		// console.log(data);
		if (data.success) {
			gui.redrawSelect(field, data);
			gui.cache.store(url, data);
			field.trigger("remote_complete");
		} else {
			console
					.warn("No se obtuvo una lista de elementos para agregar al campo "
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
				if (el) {
					// FIXME: fijarse en que esconda bien los campos
					// console.log("hidding el "+field.attr("name"));
					// el.hide();
				}
			}
			var dependants = field.data("dependant");
			if (dependants) {
				gui.resetHierarchy(dependants);
			}
		}
	};
	
	this.completeForm = function(record, fields) {
		for ( var i = 0; i < fields.length; i++) {
			var field = $(fields[i]);
			var fieldCleanName = gui.getCleanFieldName(field.attr("name"),
					record.instance);

			if (record[fieldCleanName] != undefined
					&& record[fieldCleanName] != null) {
				if (field.is("select")) {
					if (record[fieldCleanName].value == null) {
						field.select2("data", null);
					} else {
						field.select2("val", record[fieldCleanName].value);
					}
				} else if (field.attr("data-type-xml") == "select2") {
					if (record[fieldCleanName].value == null) {
						field.select2("data", null);
					} else {
						field.select2("data", {
							id : record[fieldCleanName].value,
							text : record[fieldCleanName].label
						});
					}
				} else {
					if (!isNaN(record[fieldCleanName])) {// javascript valid
						record[fieldCleanName] = record[fieldCleanName];
					}
					field.val(record[fieldCleanName]);
				}
			}

			field.trigger("change");
		}
	};
	this.gridTotalizadora = {
		agrupadores : [],
		totalizadores : [],
		headers : [],
		data : [],
		accountedFor : false,
		totalizadoIdx : 0,
		buttons : [],
		element : $('<table id="agrupadora-grid" class="table table-striped"></table>'),
		getDataTable : function() {
			return this.element.dataTable();
		},
		getData : function() {
			return this.getDataTable().fnGetData();
		},
		getRowData : function(rowIndex) {
			return this.getDataTable().fnGetData(rowIndex);
		},
		addRows : function(dataArray) {
			this.getDataTable().fnAddData(dataArray, true);
		},
		removeRow : function(rowIndex) {
			this.element.dataTable().fnDeleteRow(rowIndex);
		},
		addRow : function(data) {
			this.getDataTable().fnAddData(data, true);
		},
		updateRow : function(data, index) {
			this.getDataTable().fnUpdate(data, index);
		},
		updateRows : function(list) {
			for ( var i = 0; i < list.length; i++) {
				this.updateRow(list[i], i);
			}
		},
		resetProcessVars : function() {
			this.accountedFor = false;
			this.totalizadoIdx = 0;
		},
		checkCorrectTotalizadores : function(list) {
			console.log("list totalizadores", list);
		},
		getButton : function(type) {
			for ( var i = 0; i < this.buttons.length; i++) {
				if (this.buttons[i].type == type)
					return this.buttons[i].btnEl;
			}
			return undefined;
		},
		getNombreNivel : function(nivel) {
			if (isNaN(nivel))
				return;
			if (nivel > this.agrupadores.length)
				return;
			for ( var i = 0; i < this.agrupadores.length; i++) {
				if (this.agrupadores[i].nivel == nivel)
					return this.agrupadores[i].nombre;
			}
		},
		getTituloNivel : function(nivel) {
			if (isNaN(nivel))
				return;
			if (nivel > this.agrupadores.length)
				return;
			for ( var i = 0; i < this.agrupadores.length; i++) {
				if (this.agrupadores[i].nivel == nivel)
					return this.agrupadores[i].titulo;
			}
		},
		checkNiveles : function() {
			var niveles = 0;
			for ( var i = 0; i < this.agrupadores.length; i++) {
				if (niveles < this.agrupadores[i].nivel) {
					niveles = this.agrupadores[i].nivel;
				}
			}
			if (niveles != this.agrupadores.length) {
				console
						.error("La cantidad de agrupadores es diferente a los niveles expresados de agrupamiento, declarados="
								+ niveles
								+ " encontrados="
								+ this.agrupadores.length);
				return false;
			} else {
				console.log("Se encontraron " + niveles
						+ " niveles de agrupamiento correctamente declarados");
				return true;
			}
		},
		onInputChange : function(el) {
			var table = this.getDataTable();
			/*
			 * recupero las coordenadas en donde se ha cambiado el valor del
			 * input
			 */
			var td = $(el).closest('td').get(0);
			/*
			 * 0: row 1: column idx (visible) 2: column idx (all)
			 */
			var coords = table.fnGetPosition(td);
			/*
			 * recupero la informacion de la columna de la tabla en la que se
			 * cambio el valor del input para poder recuperar la info del campo
			 */
			var column = table.fnSettings().aoColumns[coords[2]];

			/* recupero el registro de la fila completo para actualizar el valor */
			var record = this.getRowData(coords[0]);

			record[column["field_name"]] = gui.toNumber(el.value);

			this.updateRow(record, coords[0]);

		},
		render : function(fieldset) {
			var calculadoBtn = $("div[class~='form-actions']").find(
					"[totalizador-action='calculados']");
			this.buttons.push({
				type : "calculados",
				btnEl : calculadoBtn
			});
			var ingresadoBtn = $("div[class~='form-actions']").find(
					"[totalizador-action='ingresados']");
			this.buttons.push({
				type : "ingresados",
				btnEl : ingresadoBtn
			});

			/*
			 * Mostrar los botones disparadores que inicialmente vienen
			 * ocultados
			 */
			$.each(this.buttons,
					function(i, element) {
						if (gui.renderTotalizadoresIngresados) {
							element.btnEl.show();
						} else {
							if (gui.renderTotalizadores
									&& element.type == "calculados") {
								element.btnEl.show();
							}
						}
					});

			this.getButton("calculados").click(function(evt) {
				var table = gui.gridTotalizadora.element.dataTable();
				var cols = table.fnSettings().aoColumns;
				$.each(cols, function(i, col) {
					if (col.ingresada) {
						table.fnSetColumnVis(i, false);
					}
				});
			});
			this.getButton("ingresados").click(function(evt) {
				var table = gui.gridTotalizadora.element.dataTable();
				var cols = table.fnSettings().aoColumns;
				$.each(cols, function(i, col) {
					if (col.ingresada) {
						table.fnSetColumnVis(i, true);
					}
				});
			});

			if (!gui.gridTotalizadora.checkNiveles())
				return;

			var modalBody = $("#modal-totalizadora").children("#agrupadora_modal_body");
			$('<div class="table-overflow"></div>').append(this.element).appendTo(modalBody);

			for ( var i = 1; i <= this.agrupadores.length; i++) {
				this.headers.push({
					"sTitle" : this.getTituloNivel(i),
					"mData" : "" + this.getNombreNivel(i)
				});
			}

			for ( var i = 0; i < this.totalizadores.length; i++) {
				var tot = this.totalizadores[i];
				this.headers.push({
					"sTitle" : tot.titulo,
					"mData" : "" + tot.nombre
				});

				if (gui.renderTotalizadoresIngresados) {
					this.headers
							.push({
								"sTitle" : tot.titulo + " (Ingresado)",
								"mData" : "" + tot.nombre + "_ingresado",
								"mRender" : function(data, type, row) {
									return '<input type="text" value="'
											+ data
											+ '" onchange="gui.gridTotalizadora.onInputChange(this);"/>';
								},
								"bVisible" : false,
								"field_name" : tot.nombre + '_ingresado',
								"ingresada" : true
							});
				}
			}

			if (gui.renderPorcentaje) {
				this.headers.push({
					"sTitle" : "Resultado %",
					"mData" : "resultado",
					"bSearchable" : false,
				});
			}
			
			if (gui.renderTotal) {
				this.headers.push({
					"sTitle" : "Total",
					"mData" : "rowTotal",
					"bSearchable" : false,
				});
			}
			
			this.element
					.dataTable({
						"bJQueryUI" : false,
						"bScrollCollapse" : true,
						"sPaginationType" : "full_numbers",
						"sDom" : '<"datatable-header"fl>t<"datatable-footer"ip>',
						"oLanguage" : {
							"sSearch" : "Buscar:",
							"sZeroRecords" : "Ning&uacute;n registro resultante",
							"sLengthMenu" : "<span>Filas _MENU_</span>",
							"sEmptyTable" : "No hay datos",
							"sInfoFiltered" : "(filtrado de _MAX_ registros)",
							"sInfoEmpty" : "No hay registros cargados",
							"sInfo" : "Mostrando _START_ a _END_, de _TOTAL_ registros",
							"oPaginate" : {
								"sFirst" : "Primera",
								"sLast" : "&Uacute;ltima",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						},
						"aaData" : gui.gridTotalizadora.data,
						"aoColumns" : gui.gridTotalizadora.headers
					});
		},
		prepareTotalizado : function(record) {
			var totalizado = {};
			var tmpHash = "";
			for ( var i = 1; i <= this.agrupadores.length; i++) {
				var r = record[this.getNombreNivel(i)];
				if (typeof r == "object") {
					totalizado[this.getNombreNivel(i)] = r.label;
					tmpHash += r.label;
				} else {
					totalizado[this.getNombreNivel(i)] = r;
					tmpHash += r;
				}
			}

			hash = new jsSHA(tmpHash, "TEXT");
			totalizado["firma_digital"] = hash.getHash("SHA-1", "HEX");

			/* set totalizadores if not already there */
			for ( var i = 0; i < this.totalizadores.length; i++) {
				totalizado[this.totalizadores[i].nombre] = null;
				if (gui.renderTotalizadoresIngresados) {
					totalizado[this.totalizadores[i].nombre + "_ingresado"] = "-";
				}
			}

			return totalizado;
		},
		/**
		 * Procesa el registro ingresado en la grilla principal identificando el
		 * registro totalizado al que pertenece si es que este se encontrase ya
		 * totalizado y prepara el objeto de registro totalizado para su
		 * correcto totalizado
		 */
		processGroups : function(record) {
			var totalizados = this.getData();

			var totalizado = this.prepareTotalizado(record);

			this.accountedFor = false;
			for ( var i = 0; i < totalizados.length; i++) {
				if (totalizados[i].firma_digital == totalizado.firma_digital) {
					this.totalizadoIdx = i;
					this.accountedFor = true;
					totalizado = totalizados[i];
					break;
				}
			}

			return totalizado;
		},
		/**
		 * Procesa la inclusi&oacute;n de un registro en la grilla principal
		 * totalizando sus valores totalizadores.
		 */
		processRecord : function(record) {
			// console.log("NUEVO RECORD PARA TOTALIZAR:",record);

			var totalizado = this.processGroups(record);

			var acumulaTotal = 0;
			var porcentaje = 0;
			
			for ( var i = 0; i < this.totalizadores.length; i++) {

				var r = record[this.totalizadores[i].nombre];
				totalizado[this.totalizadores[i].nombre] = this.accountedFor ? totalizado[this.totalizadores[i].nombre]
						+ r
						: r;
				acumulaTotal = acumulaTotal
						+ totalizado[this.totalizadores[i].nombre];
				
			}

			i=0;
			porcentaje = totalizado[this.totalizadores[i].nombre]*100/totalizado[this.totalizadores[i+1].nombre];
			
			totalizado["rowTotal"] = acumulaTotal;
			totalizado["resultado"] = porcentaje.toFixed(2);
			
			if (this.accountedFor) {
				this.updateRow(totalizado, this.totalizadoIdx);
			} else {
				this.addRow(totalizado);
			}
			this.resetProcessVars();
		},
		/**
		 * Procesa la edici&oacute;n de un registro de la grilla principal. La
		 * edici&oacute;n no implica solamente cambios en los totalizadores,
		 * sin&oacute; en sus otros campos tambi&eacute;n lo que debe procesarse
		 * de manera &iacute;ntegra ya que se puede dar el caso de agregar un
		 * registro totalizado nuevo si no se encuentra en la grilla de
		 * totalizados.
		 */
		processEdition : function(record) {
			console.log("RECORD PARA EDITAR EL TOTALIZADO:", record,
					record.original);
			var totalizadoNuevo = this.prepareTotalizado(record);
			var totalizadoOrig = this.prepareTotalizado(record.original);

			/*
			 * si las firmas son las mismas, han cambiado los totalizadores
			 * solamente
			 */
			if (totalizadoNuevo.firma_digital == totalizadoOrig.firma_digital) {
				var totalizado = this.processGroups(record);
				var acumulaTotal = 0;

				for ( var i = 0; i < this.totalizadores.length; i++) {

					if (record[this.totalizadores[i].nombre] != undefined) {
						var r = record[this.totalizadores[i].nombre];
						totalizadoNuevo[this.totalizadores[i].nombre] = r;
					}
				}

				for ( var i = 0; i < this.totalizadores.length; i++) {

					if (record[this.totalizadores[i].nombre] != undefined) {
						var r = record.original[this.totalizadores[i].nombre];
						totalizadoOrig[this.totalizadores[i].nombre] = r;
					}
				}

				var totalizadoDelta = totalizadoNuevo;

				for ( var i = 0; i < this.totalizadores.length; i++) {
					totalizadoDelta[this.totalizadores[i].nombre] = totalizadoNuevo[this.totalizadores[i].nombre]
							- totalizadoOrig[this.totalizadores[i].nombre];
				}

				for ( var i = 0; i < this.totalizadores.length; i++) {
					totalizado[this.totalizadores[i].nombre] = totalizado[this.totalizadores[i].nombre]
							+ totalizadoDelta[this.totalizadores[i].nombre];
					acumulaTotal = acumulaTotal
							+ totalizado[this.totalizadores[i].nombre];
				}

				i=0;
				porcentaje = totalizado[this.totalizadores[i].nombre]*100/totalizado[this.totalizadores[i+1].nombre];
				
				totalizado["rowTotal"] = acumulaTotal;
				totalizado["resultado"] = porcentaje.toFixed(2);

				this.updateRow(totalizado, this.totalizadoIdx);
				this.resetProcessVars();
			} else {
				/*
				 * si las firmas no son las mismas, han cambiado todo el
				 * registro, procesar integramente y restar los valores
				 * originales de la lista de totalizados
				 */
				console
						.log("ES OTRO REGISTRO TOTALIZADO, puede ser nuevo o estar contabilizado");
				this.processRemoval(record.original);
				this.processRecord(record);
			}
		},
		/**
		 * Procesa la eliminaci&oacute;n de un registro de la grilla principal
		 * actualizando sus valores totalizadores correspondientes en la grilla
		 * de totalizadores.
		 */
		processRemoval : function(record) {
			var totalizado = this.processGroups(record);

			var total = 0;
			var acumulaTotal = 0;
			for ( var i = 0; i < this.totalizadores.length; i++) {

				if (record[this.totalizadores[i].nombre] != undefined) {
					var r = record[this.totalizadores[i].nombre];
					totalizado[this.totalizadores[i].nombre] = totalizado[this.totalizadores[i].nombre]
							- r;
					total += totalizado[this.totalizadores[i].nombre];
					acumulaTotal = acumulaTotal
							+ totalizado[this.totalizadores[i].nombre];
				}
			}
			
			if(this.totalizadores.length>0){
				i=0;
				porcentaje = totalizado[this.totalizadores[i].nombre]*100/totalizado[this.totalizadores[i+1].nombre];
			}
			
			totalizado["rowTotal"] = acumulaTotal;
			
			if(porcentaje){
				totalizado["resultado"] = porcentaje.toFixed(2);
			}
			
			// si la sumatoria es menor igual que cero se elimina el registro
			if (total <= 0) {
				this.removeRow(this.totalizadoIdx);
			} else {
				this.updateRow(totalizado, this.totalizadoIdx);
			}
			this.resetProcessVars();
		},
		processTable : function(data) {
			console.log("PROCESANDO TODA LA TABLA");
		}
	};
	this.grid = {
		editing : -1,
		headers : [],
		claves_primarias: [],
		data : [],
		model : {
			fields : [],
			values : [],
			item : "",
			instance : -1
		},
		element : $('<table id="repeat-grid" class="table table-striped"></table>'),
		getData : function() {
			return this.element.dataTable().fnGetData();
		},
		getRowData : function(rowIndex) {
			return this.element.dataTable().fnGetData(rowIndex);
		},

		setupAddClick : function() {
			$("input[type~='button'][repeat-action='add']").click(
					this,
					function(evt) {
						gui.grid.addRow($(evt.target).parent().attr(
								"repeat-instance"));
					});
		},
		setupEditClck : function() {
			$("input[type~='button'][repeat-action='edit']").unbind("click");
			$("input[type~='button'][repeat-action='edit']")
					.click(
							function(evt) {
								var rowIndex = gui.grid.element.dataTable()
										.fnGetPosition(
												$(evt.target).closest('tr')
														.get(0));
								var record = gui.grid.getRowData(rowIndex);
								var fields = gui.fieldsets[record.instance].fields;
								gui.blockUI(
										"Cargando...<br>Espere por favor...",
										true);
								gui.resetForm();
								gui.completeForm(record, fields);

								for ( var i = 0; i < fields.length; i++) {
									var field = $(fields[i]);
									field.data("renderLogic")(field);
								}
								gui.grid.editing = rowIndex;
								gui.unblockUI(true);
								$(gui.fieldsets[record.instance].dom)
										.scrollTo();
							});

		},
		setupRemoveClick : function() {
			$("input[type~='button'][repeat-action='remove']").unbind("click");
			$("input[type~='button'][repeat-action='remove']").click(
					function(evt) {
						var rowIndex = gui.grid.element.dataTable()
								.fnGetPosition(
										$(evt.target).closest('tr').get(0));
						var record = gui.grid.getRowData(rowIndex);
						gui.grid.removeRow(rowIndex);
						var fields = gui.fieldsets[record.instance].fields;
						if (gui.grid.editing > -1) {
							gui.grid.editing = -1;
						}
						gui.resetFields(fields);
						gui.gridTotalizadora.processRemoval(record);
					});
		},
		setupRowActions : function() {
			this.setupEditClck();
			this.setupRemoveClick();
		},
		addRow : function(fieldsetInstance) {
			var fieldset = gui.fieldsets[fieldsetInstance];
			var fields = [];
			
			var record = $.extend(true, {}, this.model);
			var tmpHash = "";
			if (gui.repeatCount && gui.repeatCount > 1) {
				record.item = fieldset.title;
				tmpHash += tmpHash + fieldset.title;
			}
			if (gui.renderTotal) {
				record["rowTotal"] = 0;
			}
			var commit = true;
			var hash;
			for ( var i = 0; i < fieldset.fields.length; i++) {
				var field = $(fieldset.fields[i]);
				var attribute = gui.getCleanFieldName(field.attr("name"),
						fieldsetInstance);
				//Si es hidden pongo valores por defecto
				if(field[0].hidden==true){
					field.val(" ");
					if (field.is("select")){
						record[attribute] = {
							label : "",
							value : null
						};
					} else if (field.is("select2")){
						record[attribute] = {
							label : "",
							value : null
						};
					}else{
						record[attribute] = {
							label : "",
							value : null
						};
					}
				}
				else{
					
				if (field.is(":visible")
						|| field.attr("data-type-xml") == "select2") {
					var value = field.val();
					// console.log("Value",field.attr("name"),":_"+value+"_");
					if (value != undefined && value != null
							&& $(gui.form).validate().element(field)) {
						if (field.is("select")) {
							var o;
							if (value.trim() != "") {
								if(value!=-1){
									o = field.children("option:selected");
									record[attribute] = {
											label : o.text(),
											value : value
									};
									tmpHash = tmpHash + value;
								}
							} else {
								record[attribute] = {
									label : "",

									value : null
								};
							}
						} else if (field.attr("data-type-xml") == "select2") {
							var data;
							if (value.trim() != "") {
								data = field.select2("data");
								record[attribute] = {
									label : data.text,
									value : data.id
								};
								tmpHash = tmpHash + data.id;
							} else {
								record[attribute] = {

									label : "",

									value : null
								};
							}
						} else {
							if (gui.isCNCNumberField(field)) {

								value = gui.toNumber(value);
								if (gui.renderTotal
										&& field
												.data("jr:constraint:totalizador") == true) {
									record["rowTotal"] += value;
								}
							}

							record[attribute] = value;
							tmpHash = tmpHash + value;
						}
						hash = new jsSHA(tmpHash, "TEXT");

						record["firma_digital"] = hash.getHash("SHA-1", "HEX");
						fields.push(field);
					} else {
						commit = false;
						field.scrollTo();
						console.warn("Could not commit due to field", field);
						break;
					}

				} else {// si no es visible poner valores por defecto
					if (field.is("select")){
						record[attribute] = {
							label : ""
						};
					} else if (field.is("select2")){
						record[attribute] = {
							label : ""
						};
					}else
						record[attribute] = "";

				}
			}
				
			}
			record.instance = fieldset.instance;
			var storedData = this.getData();
			// console.info("new record",record.signature);

			if(gui.grid.claves_primarias.length==0){
			
				for ( var i = 0; i < storedData.length; i++) {
					var storedRecord = storedData[i];
					// console.info("stored record",storedRecord.signature);
					if (storedRecord.firma_digital == record.firma_digital) {
						gui.displayWarning("Ya se encuentra agregado un registro con esos valores");
						commit = false;
						break;
					}
				}
			}else{
				if(validationPrimaryKey(record,storedData,fieldset,gui.grid.claves_primarias,this.editing)){
					var claves=" ";
					for(var i=0; i < (gui.grid.claves_primarias.length);i++){
						claves+=gui.grid.claves_primarias[i].nombre+" - ";
					}
					gui.displayWarning("No se pueden repetir la combinación de campos:" + claves);
					commit = false;
				}
			}
			
			if (commit) {
				gui.cleanFormValidations();
				gui.resetFields(fields);
				for ( var i = 0; i < fieldset.fields.length; i++) {
					var af = $(fieldset.fields[i]);
					// console.log("campo a checkear visualizacion",af);
					af.data("renderLogic")(af);
				}
				if (this.editing == -1) {
					if (gui.renderTotalizadores)
						gui.gridTotalizadora.processRecord(record);
					this.element.dataTable().fnAddData(record, true);
				} else {
					if (gui.renderTotalizadores) {
						var r = record;
						r["original"] = this.getRowData(this.editing);
						gui.gridTotalizadora.processEdition(r);
					}

					this.element.dataTable().fnUpdate(record, this.editing);
					this.editing = -1;
				}
				// this.setupRowActions();
			}
		},
		addRows : function(dataArray) {
			this.element.dataTable().fnAddData(dataArray, true);
			// this.setupRowActions();
		},
		removeRow : function(rowIndex) {
			this.element.dataTable().fnDeleteRow(rowIndex);
		},
		processNulls : function() {
			var cont=0;
		for(var j =0; j<gui.fieldsets.length;j++){	
			for ( var i = 0; i < gui.fieldsets[j].fields.length; i++) {
				var f = $(gui.fieldsets[j].fields[i]);
				
				if(f.is("select")){
					if(f.labels!=null){
						gui.fieldsets[0].fields[i].labels="";
						gui.fieldsets[0].fields[i].value="";
						cont++;
					}
					
				}
			}
		}
		
		console.debug("Contador: "+cont);
		},
		
		/**
		 * Call grid render function after building Form.fieldsets with repeat
		 * parent fieldset
		 */
		render : function(pfs) {
			var gridFieldset = $('<fieldset class="jr-group well-white col1"></fieldset>');
			if (!gui.readonly) {
				$("fieldset[repeat-instance]")
						.append(
								'<input type="button" class="btn" value="Agregar" repeat-action="add"/>');
			}
			gridFieldset.appendTo(pfs);
			$('<h4></h4>').append("<span>Resultados</span>").appendTo(
					gridFieldset);
			$('<div class="table-overflow"></div>').append(this.element)
					.appendTo(gridFieldset);

			if (gui.repeatCount && gui.repeatCount > 1)
				this.headers.push({
					"sTitle" : "Meses",
					"mData" : "item"
				});

			for ( var i = 0; i < gui.fieldsets[0].fields.length; i++) {
				var f = $(gui.fieldsets[0].fields[i]);
				var attribute = gui.getCleanFieldName(f.attr("name"));
				this.model[attribute] = null;
				var header = f.siblings("span.jr-label");
				if (f.is("select")) {
					this.model[attribute] = {
						label : null,
						value : null
					};
					this.headers.push({
						"sTitle" : header.text(),
						"mData" : "" + attribute + ".label"
					});
				} else {
					this.headers.push({
						"sTitle" : header.text(),
						"mData" : "" + attribute
					});
				}
			}
			
			if (gui.renderTotal) {
				this.headers.push({
					"sTitle" : "Total",
					"mData" : "rowTotal",
					"bSearchable" : false,
				});

			}
			
			
			
			if (!gui.readonly) {
				this.headers
						.push({
							"sTitle" : "Acciones",
							"bSearchable" : false,
							"mData" : function(data, type, full) {
								return '<input type="button" repeat-action="edit" class="btn" value="Editar"/>&#10;'
										+ '<input type="button" repeat-action="remove" class="btn" value="Borrar"/>';
							}
						});
			}
			this.setupAddClick();
			// this.setupRowActions();
			this.element
					.dataTable({
						"bJQueryUI" : false,
						"bAutoWidth" : false,
						"sScrollX" : "100%",
						"sScrollXInner" : "100%",
						"bScrollCollapse" : true,
						"sPaginationType" : "full_numbers",
						"sDom" : '<"datatable-header"fl>t<"datatable-footer"ip>',
						"oLanguage" : {
							"sSearch" : "Buscar:",
							"sZeroRecords" : "Ning&uacute;n registro resultante",
							"sLengthMenu" : "<span>Filas _MENU_</span>",
							"sEmptyTable" : "No hay datos",
							"sInfoFiltered" : "(filtrado de _MAX_ registros)",
							"sInfoEmpty" : "No hay registros cargados",
							"sInfo" : "Mostrando _START_ a _END_, de _TOTAL_ registros",
							"oPaginate" : {
								"sFirst" : "Primera",
								"sLast" : "&Uacute;ltima",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						},
						"aaData" : gui.grid.data,
						"aoColumns" : gui.grid.headers,
						"fnDrawCallback" : function(oSettings) {
							gui.grid.setupRowActions();
						}
					});
		}
	};
};

var cncToNumber = function(value) {
	if (value && isNaN(value) && value.trim().length > 0) {
		var number = $.i18n.parseNumber(value, {
			region : 'es-AR'
		});
		return number;
	} else if (!isNaN(value)) {
		console.warn("Number passed to cnctoNumber, only String value allowed");
		return value;
	} else if (value == undefined) {
		console.error("Undefined value passed to cncToNumber");
		return value;
	}
};


var cncFromNumber = function(value, dec) {
	var vStr = "" + value;
	var decimals = dec;


	if (decimals == undefined) {
		var d = 0;
		if (vStr.indexOf(".") > 0) {
			d = vStr.substring(vStr.indexOf(".") + 1).length;
		}
		decimals = "n" + d;
	} else {
		decimals = "n" + decimals;
	}

	if (value && !isNaN(value)) {

		/* FIX de librería i18n que tiene problemas para formatear el 0 */
		if (value == 0) {
			var val = "0";
			for ( var i = 0; i < dec; i++) {
				if (i == 0)
					val = val + ",";
				val = val + "0";
			}
			return val;
		}

		var formated = $.i18n.formatNumber(value, decimals, {
			region : 'es-AR'
		});
		return formated;
	} else if (isNaN(value)) {
		console
				.warn("String passed to cncFromNumber, only numeric value allowed");
		return value;
	} else if (value == undefined) {
		console.error("Undefined value passed to cncFromNumber");
		return value;
	}
};
