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
		 * BC.SerialPortService is an implementation about serial port based on BLE
		 * @memberof BC
		 * @class
		 * @property {string} readCharacterUUID - The read characteristic uuid
		 * @property {string} writeCharacterUUID - The write characteristic uuid
		 */
		var SerialPortService = BC.SerialPortService = BC.Service.extend({
			
			readCharacterUUID : "6E400002-B5A3-F393-E0A9-E50E24DCCA9E",
			writeCharacterUUID : "6E400003-B5A3-F393-E0A9-E50E24DCCA9E",
			
			/**
			 * Reads data from readCharacterUUID
			 * @memberof SerialPortService
			 * @example 
			 * 	function read(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")[0];
			 *			service.read(function(){
			 *				alert(data.value.getHexString());
			 *			},function(){
			 *				alert("read data error!");
			 *			});
			 *		});
			 *  }
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */	
			read : function(successFunc,errorFunc){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.readCharacterUUID)[0].read(function(data){
						 callback(data.value);
					});
				});
			},

			/**
			 * Writes data to writeCharacterUUID
			 * @memberof SerialPortService
			 * @example
			 *	function write(device,writeType,writeValue,successFunc,errorFunc){
			 *		if(device.type == "Classical"){
			 *			device.rfcommWrite(writeType,writeValue,successFunc,errorFunc);
			 *		}else if(device.type == "BLE"){
			 *			device.discoverServices(function(){
			 *				var serviceToWrite = device.getServiceByUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")[0];
			 *				serviceToWrite.write(writeType,writeValue,successFunc,errorFunc);
			 *			});
			 *		}	
			 *	}
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */				
			write : function(writeType,writeValue,successFunc,errorFunc){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.writeCharacterUUID)[0].write(writeType,writeValue,successFunc,errorFunc);
				});
			},
			
			/**
			 * subscribe from the readCharacterUUID
			 * @memberof SerialPortService
			 * @example
			 *	function subscribe(device,callback){
			 *		if(device.type == "Classical"){
			 *			device.rfcommSubscribe(callback);
			 *		}else if(device.type == "BLE"){
			 *			if(role == "Master"){
			 *				device.discoverServices(function(){
			 *					var serviceTosub = device.getServiceByUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")[0];
			 *					serviceTosub.subscribe(function(data){alert(JSON.stringify(data))});
			 *				});
			 *			}
			 *		}	
			 *	}
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */			
			subscribe : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.readCharacterUUID)[0].subscribe(callback);
				});
			},
			
			/**
			 * unsubscribe from the readCharacterUUID
			 * @memberof SerialPortService
			 * @example
			 *	function unsubscribe(device){
			 *		if(device.type == "Classical"){
			 *			device.rfcommUnsubscribe();
			 *		}else if(device.type == "BLE"){
			 *			device.discoverServices(function(){
			 *				var serviceTosub = device.getServiceByUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")[0];
			 *				serviceTosub.unsubscribe(function(){alert("unsubscribe success!")});
			 *			});			   
			 *		}
			 *	}
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */					
			unsubscribe : function(){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.readCharacterUUID)[0].unsubscribe();
				});
			}, 
		});
		
		//register the service to BC.bluetooth for profile use.
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["6E400001-B5A3-F393-E0A9-E50E24DCCA9E"] = BC.SerialPortService;
		});
		
		module.exports = BC;
