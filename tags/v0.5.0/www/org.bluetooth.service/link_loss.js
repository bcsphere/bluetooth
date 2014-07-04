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
		
		var BC = require("org.bcsphere.bcjs");
		
		/**
		 * BC.LinkLossService is an implementation about link loss based on BLE
		 * @memberof BC
		 * @class
		 * @property {string} characteristicUUID - The alert characteristic uuid
		 */		
		var LinkLossService = BC.LinkLossService = BC.Service.extend({

			characteristicUUID:'2a06',

			/**
			 * Stops an alert.
			 * @memberof LinkLossService
			 * @example 
			 * 	function no_alert(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1803")[0];
			 *			service.no_alert();
			 *		});
			 *  }
			 */	
			no_alert : function(){
				this.alert('0');
			},

			/**
			 * Starts an middle alert.
			 * @memberof LinkLossService
			 * @example 
			 * 	function mild_alert(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1803")[0];
			 *			service.mild_alert();
			 *		});
			 *  }
			 */
			mild_alert : function(){
				this.alert('1');
			},
			
			/**
			 * Starts an high alert.
			 * @memberof LinkLossService
			 * @example 
			 * 	function high_alert(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1803")[0];
			 *			service.high_alert();
			 *		});
			 *  }
			 */	
			high_alert : function(){
				this.alert('2');
			},
			
			/**
			 * Gets TX power value.
			 * @memberof LinkLossService
			 * @example 
			 * 	function high_alert(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1803")[0];
			 *			service.high_alert();
			 *		});
			 *  }
			 */	
			getValue : function(callback){
				this.discoverCharacteristics(function(){
					 this.getCharacteristicByUUID(this.characteristicUUID)[0].read(function(data){
						callback(data.value);
					 });
				});
			},
			   
			alert:function(writeValue,writeType,successFunc,errorFunc){
				successFunc = successFunc || this.writeSuccess;
				errorFunc = errorFunc || this.writeError;
				writeType= writeType ||　'hex';
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
		
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["00001803-0000-1000-8000-00805f9b34fb"] = BC.LinkLossService;
		});
		module.exports = BC;
		
