(function($) {
    $.fn.scrollTo = function() {
    	var object = $(this);
    	if(object.attr("data-type-xml")!=undefined && object.attr("data-type-xml")=="select2"){
    		object = object.parent();
    	}
        console.info("scrolling top from",object.offset().top + 'px');
    	$('html, body').animate({
            scrollTop: object.offset().top + 'px'
        }, 'fast');
        return this; // for chaining...
    };
})(jQuery);