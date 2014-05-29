var ImmediateAlertService = BC.ImmediateAlertService = BC.Service.extend({

	   characteristicUUID:'2a06',

	   no_alert : function(){
	      this.alert('0');
	   },
	   
	   mild_alert : function(){
	      this.alert('1');
	   },
	   
	   high_alert : function(){
	      this.alert('2');
	   },
	   
	   alert:function(writeValue,writeType,successFunc,errorFunc){
	   	  successFunc = successFunc || this.writeSuccess;
	   	  errorFunc = errorFunc || this.writeError;
	   	  writeType = writeType ||　'hex';
       	  this.discoverCharacteristics(function(){
            	this.getCharacteristicByUUID(this.characteristicUUID)[0].write(writeType,writeValue,successFunc,errorFunc);
          });
	   },
	      
	   writeSuccess : function(){
	      console.log('writeSuccess');
	   },
	   
	   writeError : function(){
	      console.log('writeFailed');
	   },

});