var TxPowerService = BC.TxPowerService = BC.Service.extend({

	characteristicUUID:'2a07',

	getValue : function(callback){
   	    this.discoverCharacteristics(function(){
			this.getCharacteristicByUUID(this.characteristicUUID)[0].read(function(data){
	         	 callback(data.value);
	        });
	    });
	},

	notify : function(callback){
	   	this.discoverCharacteristics(function(){
	   		this.getCharacteristicByUUID(this.characteristicUUID)[0].subscribe(function(data){
	   			callback(data.value);
	   		});
	   	});
	},

});