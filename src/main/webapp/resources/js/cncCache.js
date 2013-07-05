var guiCache = function(){
	this.data = [];
	this.store = function(index,data){
		this.data[index] = data;
		console.info("Stored",data);
	};
	this.check = function(index){
		var data = this.data[index];
		if(data){
			console.log(data);
		}else{
			console.log("no data stored");
		}
	};
	this.reset = function(){
		this.data = [];
		console.log("Cleaning cache.");
	};
};