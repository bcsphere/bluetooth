 /*
    Copyright 2013-2014, JUMA Technology

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
