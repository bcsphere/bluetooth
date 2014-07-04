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

		
		var BC = require("org.bluetooth.service.serial_port");
		
		var serviceUUID   = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
		var readcharUUID  = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
		var writecharUUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
		var MASTER = "Master";
		var SLAVE = "Slave";
		var role = "Master";
		var service = {};
		
		/**
		 * BC.SerialPortProfile is an implementation of serial port.(best effort for IOS/Android implementation)
		 * <b>Please Note:</b> JSDoc can't generate part of the javascript file, please check the detail interface usage in the source file code comments.
		 * @memberof BC
		 * @class
		 * @property {string} readcharUUID - The read characteristic uuid
		 * @property {string} writecharUUID - The write characteristic uuid
		 * @property {string} serviceUUID - The serial port service uuid for BLE
		 */
		var SerialPortProfile = BC.SerialPortProfile = BC.Profile.extend({

			processRcvDataCallback : function(data){
				if(this.subscribeCallback == null){
					if(this.buffer == null){
						this.buffer = new BC.DataValue(null);
					}
					this.buffer.append(data.value);
				}else{
					this.subscribeCallback(data);
				}
			},
			
			/**
			 * Connect to device (choose the connection by device.type)
			 * @memberof SerialPortProfile
			 * @example 
			 * 	function connect(device){
			 *		BC.SerialPortProfile.connect(device,function(){
			 *			alert("device is connected!");
			 *		},function(){
			 *			alert("connect failed!");
			 *		},"7A9C3B55-78D0-44A7-A94E-A93E3FE118CE",true);
			 *  }
			 * @param {Device} device - the device object to connect
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @param {string} [uuid] - the RFCOMM uuid for classical bluetooth interface to connect
			 * @param {boolean} [secure] - establish security connection or not
			 */				
			connect : function(device,successFunc,errorFunc,uuid,secure){
				device.connect(function(){
					device.discoverServices(function(){
						var serviceTosub = device.getServiceByUUID(serviceUUID)[0];
						serviceTosub.subscribe(this.processRcvDataCallback);
					});
					successFunc();
				},errorFunc,uuid,secure);
			},
			
			/**
			 * Reads data from the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 *	BC.SerialPortProfile.read(device,function(data){
			 *		if(data){
			 *			alert("read success! Data: " + data.value.getASCIIString());
			 *		}else{
			 *			alert("no data");
			 *		}
			 *	});
			 * @param {Device} device - the device object to connect
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 */				
			read : function(device,successFunc,errorFunc){
				if(API == "ios" && role == this.SLAVE){
					if(!this.buffer){
					   successFunc(null);
					}
					var data = {};
					data.value = this.buffer;
					data.date = new Date().getTime();
					successFunc(data);
					this.buffer = null;
				}else{
					if(device.type == "Classical"){
						device.rfcommRead(successFunc,errorFunc);
					}else if(device.type == "BLE"){
						if(!this.buffer){
						   successFunc(null);
						}
						var data = {};
						data.value = this.buffer;
						data.date = new Date().getTime();
						successFunc(data);
						this.buffer = null;
					}
				}
			},
			
			/**
			 * Writes data to the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.write(device,"ascii","hello world!",function(){alert("serial port write success!")});
			 * @param {string} type - The type of the value to write ('hex'/'ascii'/'unicode'/'raw'/'base64')
			 * @param {string/Uint8Array} value - The value write to this characteristic, if the 'type' is 'raw', the value type should be Uint8Array
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 */
			write : function(device,writeType,writeValue,successFunc,errorFunc){
				if(API == "ios" && role == this.SLAVE){
					service.getCharacteristicByUUID(readcharUUID)[0].notify(writeType,writeValue,successFunc,errorFunc);
				}else{
					if(device.type == "Classical"){
						device.rfcommWrite(writeType,writeValue,successFunc,errorFunc);
					}else if(device.type == "BLE"){
						device.discoverServices(function(){
							var serviceToWrite = device.getServiceByUUID(serviceUUID)[0];
							serviceToWrite.write(writeType,writeValue,successFunc,errorFunc);
						});
					}
				}
			},
			
			/**
			 * Subscribe data on the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.write(device,"ascii","hello world!",function(){alert("serial port write success!")});
			 * @param {Device} device - the device object to connect
			 * @param {function} callback - this callback will be called when the data coming
			 */			
			subscribe : function(device,callback){
				if(API == "ios" && role == this.SLAVE){
					this.subscribeCallback = callback;
				}else{
					if(device.type == "Classical"){
					   device.rfcommSubscribe(callback);
					}else if(device.type == "BLE"){
					   if(role == "Master"){
							this.subscribeCallback = callback;
					   }
					}
				}
			},
			
			/**
			 * Unsubscribe data on the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.unsubscribe(device);
			 * @param {Device} device - the device object to connect
			 */				
			unsubscribe : function(device){
				if(API == "ios" && role == this.SLAVE){
					this.subscribeCallback = null;
				}else{
					if(device.type == "Classical"){
						device.rfcommUnsubscribe();
					}else if(device.type == "BLE"){
						if(role == "Master"){
						   this.subscribeCallback = null;
						}
					}
				}
			}, 
			
			/**
			 * Listen data on the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.listen("listenName","7A9C3B55-78D0-44A7-A94E-A93E3FE118CE",true);
			 * @param {string} [name] - the name of this connection, for the RFCOMM connection only
			 * @param {string} [uuid] - the uuid of this connection, for the RFCOMM connection only
			 * @param {boolean} [secure] - the RFCOMM connection is security or not
			 */	
			listen : function(name,uuid,secure){
				if(API !== "ios" && name && uuid && secure){
					BC.Bluetooth.RFCOMMListen(name,uuid,secure);
					role = this.SLAVE;
				}else{
					var serviceToAdd = new BC.Service({uuid:serviceUUID});
					
					var readcharpermission = ["read"];
					var readcharproperty = ["read","notify"];
					var readcharacter = new BC.Characteristic({uuid:readcharUUID,value:"",type:"Hex",property:readcharproperty,permission:readcharpermission});
					readcharacter.addEventListener("oncharacteristicread",function(s){});

					var writecharpermission = ["write"];
					var writecharproperty = ["write"];
					var writecharacter = new BC.Characteristic({uuid:writecharUUID,value:"01",type:"Hex",property:writecharproperty,permission:writecharpermission});
					writecharacter.addEventListener("oncharacteristicwrite",function(s){
						var data = {};
						data.value = s.writeValue;
						data.date = new Date().getTime();
						this.processRcvDataCallback(data);
					});

					//Adds a characteristic to a service. 
					serviceToAdd.addCharacteristic(readcharacter);
					serviceToAdd.addCharacteristic(writecharacter);
					
					//Adds a service to the smart phone.
					BC.Bluetooth.AddService(serviceToAdd,function(){
						service = serviceToAdd;
						role = this.SLAVE;
					},function(){
						alert("Listening error.");
					});
				}
			},

			/**
			 * Unlisten data on the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.unlisten("listenName","7A9C3B55-78D0-44A7-A94E-A93E3FE118CE");
			 * @param {string} [name] - the name of this connection, for the RFCOMM connection only
			 * @param {string} [uuid] - the uuid of this connection, for the RFCOMM connection only
			 */			
			unlisten : function(name,uuid){
				if(API !== "ios" && name && uuid){
					BC.Bluetooth.RFCOMMUnListen(name,uuid);
					role = this.MASTER;
				}else{
					if(this.service){
						BC.Bluetooth.RemoveService(this.service,function(){
							this.service = null;
							role = this.MASTER;
						},function(){
							alert("unlisten error.");
						});
					}
				}
			},
			
			/**
			 * Unlisten data on the connection.
			 * @memberof SerialPortProfile
			 * @example 
			 * BC.SerialPortProfile.disconnect(device);
			 * @param {Device} device - the device object to connect
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 */
			disconnect : function(device,success,error){
				device.disconnect(success,error);
			},
		});
		
		module.exports = BC;

