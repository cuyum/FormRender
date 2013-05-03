var xml = $("#base").html();
console.log("id:"+xml);
$.ajax({
	type : "GET",
	url : "transform.xhtml",
	data : {id:xml},
	success : function(data) {
		var formDom = $(data).children("root").children("form");
		$('#form').html(formDom);
	}
});

