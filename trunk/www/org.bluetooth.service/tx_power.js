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
		
		var BC = require("org.bcsphere.bluetooth.bcjs");
		/**
		 * BC.TxPowerService is an implementation about tx power based on BLE
		 * @memberof BC
		 * @class
		 * @property {string} characteristicUUID - The alert characteristic uuid
		 */
		var TxPowerService = BC.TxPowerService = BC.Service.extend({

			characteristicUUID:'2a07',

			/**
			 * Gets TX power value.
			 * @memberof TxPowerService
			 * @example 
			 * 	function getValue(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1804")[0];
			 *			service.getValue(function(data){
			 *				alert(data.getHexString());
			 *			});
			 *		});
			 *  }
			 * @param {function} callback - get tx power callback
			 */	
			getValue : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.characteristicUUID)[0].read(function(data){
						 callback(data.value);
					});
				});
			},

			/**
			 * Gets TX power when the tx power level changed.
			 * @memberof TxPowerService
			 * @example 
			 * 	function notify(device){
			 *		device.discoverServices(function(){
			 *			var service = device.getServiceByUUID("1804")[0];
			 *			service.notify(function(data){
			 *				alert(data.getHexString());
			 *			});
			 *		});
			 *  }
			 * @param {function} callback - when the tx power is changed the callback will be called.
			 */
			notify : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.characteristicUUID)[0].subscribe(function(data){
						callback(data.value);
					});
				});
			},

		});
		
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["00001804-0000-1000-8000-00805f9b34fb"] = BC.TxPowerService;
		});
		
		module.exports = BC;
