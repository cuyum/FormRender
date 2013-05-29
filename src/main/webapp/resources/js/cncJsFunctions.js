if (typeof console == "undefined") {console = {log:function(){}, warn:function(){}, info:function(){}, error:function(){}};}
if (typeof window.console.debug == "undefined") {console.debug = console.log;}

var FormRender = new function(){
	this.addToGrid = function(fieldsetInstance){
		var fieldset = this.fieldsets[fieldsetInstance];
		var reg = [fieldset.title];
		var commit = true; 
		for ( var i = 0; i < fieldset.fields.length; i++) {
			var field =$(fieldset.fields[i]);
			var value = field.val();
			if(field.is(":visible")){
				console.log("valid:"+$(this.form).validate().element(field));
				if(value!=undefined && value!=null && $(this.form).validate().element(field)){
					reg.push(field.val());
				}else{
					console.warn("Field: "+field.attr("name")+" has no value");
					commit = false;
					break;
				}
			}else{
				reg.push(" ");
			}
		}
		console.log(reg);
		if(commit) this.grid.element.dataTable().fnAddData(reg);
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

