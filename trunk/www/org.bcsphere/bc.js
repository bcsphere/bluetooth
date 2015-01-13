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

		var root = this;
		/**
		 * BC namespace includes all kinds of magic things, all the classes is registered on it, enjoy it :).
		 * @property {string} VERSION - The version of BC
		 * @namespace
		 */
		var BC;

		if (typeof exports !== 'undefined') {
			BC = exports;
		} else {
			BC = root.BC = {};
		}
		
		BC.VERSION = "0.5.0";
		/** 
		 * Opens all useful alert.
		 * @global 
		 * @property {boolean} DEBUG - switch debug mode
		 */
		DEBUG = false;
		
		BC.plugins = {};
		
		/**
		 * BC ready event,this is the "main" function of BLE application.
		 * @example document.addEventListener('bcready', onBCReady, false);
		 * function onBCReady(){
		 * 	alert("BC is ready now! you can process UI event here");
		 * }
		 * @event bcready
		 * @type {object}
		 */
		 
		/**
		 * BC core ready event,this is the "main" function of BLE plugin based on BC.js .
		 * @example document.addEventListener('bccoreready', onBCCoreReady, false);
		 * 	function onBCCoreReady(){
		 *	var eventName = "org.bcsphere.ibeacon.ready";
		 *	var iBeaconManager = BC.iBeaconManager = new BC.IBeaconManager("org.bcsphere.ibeacon",eventName);
		 *	//plugin is ready, fire the event.
		 *	BC.bluetooth.dispatchEvent(eventName);
		 * }
		 * @event bccoreready
		 * @type {object}
		 */
		
		var _ = root._;                                          
		if (!_ && (typeof require !== 'undefined')) _ = require('org.bcsphere.bluetooth.underscorejs.underscore');
		
		var testFunc = function(message){
			if(DEBUG){
				alert(JSON.stringify(message));
			}	
		}
		
		//wait for plugin ready
		var time = 0;
		var bcreadyIsFired = false;
		document.addEventListener("bccoreready",function(){
			window.setTimeout(function(){
				var interval = window.setInterval(function() {
					var isAllReady = true;
					for(var plugin in BC.plugins){
						if(time == 5){
							window.clearInterval(interval);
							BC.bluetooth.dispatchEvent("pulginTimeout");
						}else{
							if(!plugin.isReady){
								isAllReady = false;
							}
						}
					}
					time++;
					if(isAllReady){
						window.clearInterval(interval);
						if(!bcreadyIsFired){
							BC.Tools.FireDocumentEvent("bcready");
							bcreadyIsFired = true;
						}
					}
				}, 100);
				
				if(!bcreadyIsFired){
					BC.Tools.FireDocumentEvent("bcready");
					bcreadyIsFired = true;
				}
			},100);
		});
		
		//class extend function
		var extend = function(protoProps, staticProps) {
			var parent = this;
			var child;

			// The constructor function for the new subclass is either defined by you
			// (the "constructor" property in your `extend` definition), or defaulted
			// by us to simply call the parent's constructor.
			if (protoProps && _.has(protoProps, 'constructor')) {
			  child = protoProps.constructor;
			} else {
			  child = function(){ return parent.apply(this, arguments); };
			}

			// Add static properties to the constructor function, if supplied.
			_.extend(child, parent, staticProps);

			// Set the prototype chain to inherit from `parent`, without calling
			// `parent`'s constructor function.
			var Surrogate = function(){ this.constructor = child; };
			Surrogate.prototype = parent.prototype;
			child.prototype = new Surrogate;

			// Add prototype properties (instance properties) to the subclass,
			// if supplied.
			if (protoProps) _.extend(child.prototype, protoProps);

			// Set a convenience property in case the parent's prototype is needed
			// later.
			child.__super__ = parent.prototype;

			return child;
		};
		
		function aa(iterable) {
			if (!iterable) return [];
			// Safari <2.0.4 crashes when accessing property of a node list with property accessor.
			// It nevertheless works fine with `in` operator, which is why we use it here
			if ('toArray' in Object(iterable)) return iterable.toArray();
			var length = iterable.length || 0, results = new Array(length);
			while (length--) results[length] = iterable[length];
			return results;
		}

		//this function is used to bind "this" pointer in case of it changed by params pass.
		function bind(){  
			if (arguments.length < 2 && arguments[0] === undefined)      
				return this;   
			var __method = this, args = aa(arguments), object = args.shift();   
				return function(){return __method.apply(object, args.concat(aa(arguments)));} 
		}
		
		if (!Function.prototype.bind){
			Function.prototype.bind = bind;
		}
		
		Array.prototype.S = String.fromCharCode(2);
		Array.prototype.contains = function(e) { 
			var r = new RegExp(this.S+e+this.S);
			return (r.test(this.S+this.join(this.S)+this.S));
		}
		
		var Tools = BC.Tools = function(){};
		_.extend(Tools.prototype,{});	
		var Base64ToBuffer = BC.Tools.Base64ToBuffer = function(rawData){
			var bytes = window.atob(rawData);
			var arraybuffer = new Uint8Array(bytes.length);
			for (var i = 0; i < bytes.length; i++) {
				arraybuffer[i] = bytes.charCodeAt(i);
			}
			return arraybuffer.buffer;
		}
		  
		var ConvertToBase64 = BC.Tools.ConvertToBase64 = function(data){
			return window.btoa(String.fromCharCode.apply(null, data));
		}
		  
		var ConvertHexStringToInt = BC.Tools.ConvertHexStringToInt = function(hexStr){
			var result = 0;
			if(hexStr.length < 5){
				if(hexStr.length % 2 !== 0){
					hexStr = "0" + hexStr.toLowerCase();
				}
				var pos = "0123456789abcdef";
				for(var i = 0; i < hexStr.length; i++){
					result += pos.indexOf(hexStr.charAt(i)) * Math.pow(16,(hexStr.length - i - 1));
				}
			}
			return result;
		}
		  
		var HexToBase64 = BC.Tools.HexToBase64 = function(value){
			if (value.length % 2) value = "0" + value;
			value = value.toLowerCase();
			var data = new Uint8Array(value.length/2);
			var pos = "0123456789abcdef";
			for(var i = 0,j = 0; i < value.length; i += 2,j++){
				data[j] = (pos.indexOf(value.charAt(i)) << 4) | (pos.indexOf(value.charAt(i + 1)));
			}
			return BC.Tools.ConvertToBase64(data);
		}
			
		var ASCIIToBase64 = BC.Tools.ASCIIToBase64 = function(value){
			var data = new Uint8Array(value.length);
			for(var i = 0; i < value.length; i++){
				data[i] = value.charCodeAt(i);
			}
			return BC.Tools.ConvertToBase64(data);
		}
			
		var UnicodeToBase64 = BC.Tools.UnicodeToBase64 = function(value){
			var data = new Uint8Array(value.length*2);
			var str = "";
			for(var i = 0,j = 0; i < value.length; i++, j += 2){
				data[j] = value.charCodeAt(i) / 256;
				data[j+1] = value.charCodeAt(i) % 256;
			}
			return BC.Tools.ConvertToBase64(data);
		}
		  
		var IsEmpty = BC.Tools.IsEmpty = function(s){
			return ((s == undefined || s == null || s == "") ? true : false); 
		}
		
		var ChangeTo128UUID = BC.Tools.ChangeTo128UUID = function(uuid){
			var uuid_128 = "";
			if(uuid.length == 4){
				var r = new RegExp("[A-Fa-f0-9]{1,4}");
				if(r.test(uuid)){
					uuid_128 = "0000"+ uuid +"-0000-1000-8000-00805f9b34fb";
				}
			}else if(uuid.length == 36){
				var r = new RegExp("^[A-Fa-f0-9]{1,8}-[A-Fa-f0-9]{1,4}-[A-Fa-f0-9]{1,4}-[A-Fa-f0-9]{1,12}");
				if(r.test(uuid)){
					uuid_128 = uuid;
				}
			}
			return uuid_128;
		}
		
		var FireDocumentEvent = BC.Tools.FireDocumentEvent = function(eventName,arg){
			var event = document.createEvent('Events');
			event.arg = arg;
			event.initEvent(eventName, false, false);
			document.dispatchEvent(event);
		}
		
		var EventDispatcher = BC.EventDispatcher = function(){
			var s = this;
			s._eventList = new Array();
			this.initialize.apply(this, arguments);
		};
		_.extend(EventDispatcher.prototype,{
		
			initialize:function(){},
		
			addEventListener:function(type,listener){
				this._eventList.push({listener:listener,type:type});
			},
			
			removeEventListener:function(type){
				var s = this,i,length;
				length = s._eventList.length;
				for(i=0; i < length; i++){
					if(type == s._eventList[i].type){
						s._eventList.splice(i,1);
						return;
					}
				}
			},
			
			removeAllEventListener:function (){
				this._eventList = [];
			},
			
			dispatchEvent:function(type,target){
				var s = this;
				var i,length = s._eventList.length;
				for(i=0; i < length; i++){
					if(type == s._eventList[i].type){
						if(!BC.Tools.IsEmpty(target)){
							s.target = target;
						}else{
							s.target = s;
						}
						s.event_type = type;
						s._eventList[i].listener(s);
						return;
					}
				}
			},
			
			hasEventListener:function(type){
				var s = this,i,length = s._eventList.length;
				for(i=0; i < length; i++){
					if(type == s._eventList[i].type)return true;
				}
				return false;
			},
			
		});
		EventDispatcher.extend = extend;
		
		//Portable Functions
		var BluetoothFuncs = BC.BluetoothFuncs = function(type){
			if(type == "cordova" && typeof cordova !== "undefined" ){
				this.initBluetooth = function(){
					navigator.bluetooth.initialBluetooth();
				};
			
				this.getEnvironment = function(success,error){
					navigator.bluetooth.getEnvironment(success,error);
				};
			
				this.startScan = function(UUIDs){
					var uuids;
					if(typeof UUIDs !== 'undefined'){
						uuids = [{"serviceUUIDs":UUIDs}];;
					}else{
						uuids = [{"serviceUUIDs":[]}];
					}
					navigator.bluetooth.startScan(null,testFunc,uuids);
				};
			
				this.stopScan = function(){
					navigator.bluetooth.stopScan(testFunc,testFunc);
				};
				
				this.getDeviceAllData = function(device){
					//bind "this" pointer in case of rewrite by js context.
					var processDeviceAllData = device.processDeviceAllData.bind(device,device.processDeviceAllData);
					var getAllDataError = device.getAllDataError.bind(device,device.getAllDataError);
					navigator.bluetooth.getDeviceAllData(processDeviceAllData,getAllDataError,device.deviceAddress);
				};
			
				this.connect = function(device){
					var connectSuccess = device.connectSuccess.bind(device,device.connectSuccess);
					var connectError = device.connectError.bind(device,device.connectError);
					navigator.bluetooth.connectDevice(connectSuccess,connectError,device.deviceAddress,APPURL);
				};
				this.disconnect = function(device){
					var disconnectSuccess = device.disconnectSuccess.bind(device,device.disconnectSuccess);
					var disconnectError = device.disconnectSuccess.bind(device,device.disconnectError);
					navigator.bluetooth.disconnectDevice(disconnectSuccess,disconnectError,device.deviceAddress,APPURL);
				};
				
				this.writeCharacteristic = function(character,value){
					var writeSuccess = character.writeSuccess.bind(character,character.writeSuccess);
					var writeError = character.writeError.bind(character,character.writeError);
					navigator.bluetooth.writeCharacteristic(writeSuccess,writeError,character.device.deviceAddress,character.upper.index,character.index,value);
				};
				this.readCharacteristic = function(character){
					var readSuccess = character.readSuccess.bind(character,character.readSuccess);
					var readError = character.readError.bind(character,character.readError);
					navigator.bluetooth.readCharacteristic(readSuccess,readError,character.device.deviceAddress,character.upper.index,character.index);
				};
				this.subscribe = function(character){
					var subscribeCallback = character.subscribeCallback.bind(character,character.subscribeCallback);
					navigator.bluetooth.subscribe(subscribeCallback,testFunc,character.device.deviceAddress,character.upper.index,character.index);
				};
				this.unsubscribe = function(character){
					var unsubscribeSuccess = character.unsubscribeSuccess.bind(character,character.unsubscribeSuccess);
					var unsubscribeError = character.unsubscribeError.bind(character,character.unsubscribeError);		
					navigator.bluetooth.unsubscribe(unsubscribeSuccess,unsubscribeError,character.device.deviceAddress,character.upper.index,character.index,"");
				};
				this.getRSSI = function(device){
					var getRSSISuccess = device.getRSSISuccess.bind(device,device.getRSSISuccess);
					var getRSSIError = device.getRSSIError.bind(device,device.getRSSIError);
					navigator.bluetooth.getRSSI(getRSSISuccess,getRSSIError,device.deviceAddress);
				};
				this.addServices = function(serviceObj,success,error){
					navigator.bluetooth.addServices(success,error,serviceObj);
				};
				this.removeService = function(service,success,error){
					navigator.bluetooth.removeService(success,error,service.uniqueID);
				};
				this.detectionBluetoothAPI = function(success,error){
					navigator.bluetooth.detectionBluetoothAPI(success,error);
				};
				this.createPair = function(device){
					var success = device.createPairSuccess.bind(device,device.createPairSuccess);
					var error = device.createPairError.bind(device,device.createPairError);
					navigator.bluetooth.createPair(success,error,device.deviceAddress);
				};
				this.removePair = function(device){
					var success = device.removePairSuccess.bind(device,device.removePairSuccess);
					var error = device.removePairError.bind(device,device.removePairError);
					navigator.bluetooth.removePair(success,error,device.deviceAddress);
				};
				this.getPairedDevices = function(success,error){
					navigator.bluetooth.getPairedDevices(success,error);
				};
				this.getConnectedDevices = function(success,error){
					navigator.bluetooth.getConnectedDevices(success,error);
				};
				this.discoverServices = function(device){
					var success = device.discoverServicesSuccess.bind(device,device.discoverServicesSuccess);
					var error = device.discoverServicesError.bind(device,device.discoverServicesError);
					navigator.bluetooth.discoverServices(success,error,device.deviceAddress);
				};
				this.discoverCharacteristics = function(service){
					var success = service.discoverCharacteristicsSuccess.bind(service,service.discoverCharacteristicsSuccess);
					var error = service.discoverCharacteristicsError.bind(service,service.discoverCharacteristicsError);
					navigator.bluetooth.discoverCharacteristics(success,error,service.device.deviceAddress,service.index,[]);
				};
				this.discoverDescriptors = function(character){
					var success = character.discoverDescriptorsSuccess.bind(character,character.discoverDescriptorsSuccess);
					var error = character.discoverDescriptorsError.bind(character,character.discoverDescriptorsError);
					navigator.bluetooth.discoverDescriptors(success,error,character.device.deviceAddress,character.upper.index,character.index);
				};
				this.readDescriptor = function(descriptor){
					var readSuccess = descriptor.readSuccess.bind(descriptor,descriptor.readSuccess);
					var readError = descriptor.readError.bind(descriptor,descriptor.readError);
					navigator.bluetooth.readDescriptor(readSuccess,readError,descriptor.device.deviceAddress,descriptor.upper.upper.index,descriptor.upper.index,descriptor.index);
				};
				this.getBluetoothState = function(success,error){
					navigator.bluetooth.getBluetoothState(success,error);
				};
				this.openBluetooth = function(success,error){
					navigator.bluetooth.openBluetooth(success,error);
				};
				this.closeBluetooth = function(success,error){
					navigator.bluetooth.closeBluetooth(success,error);
				};
				this.addSystemListener = function(success,error,arg){
					navigator.bluetooth.addEventListener(success,error,arg);
				};
				this.notify = function(characteristic,data){
					var notifySuccess = characteristic.notifySuccess.bind(characteristic,characteristic.notifySuccess);
					var notifyError = characteristic.notifyError.bind(characteristic,characteristic.notifyError);
					navigator.bluetooth.notify(notifySuccess,notifyError,characteristic.upper.uniqueID,characteristic.index,data);
				};
				this.startClassicalScan = function(){
					navigator.bluetooth.startClassicalScan(testFunc,testFunc);
				};
				this.stopClassicalScan = function(){
					navigator.bluetooth.stopClassicalScan(testFunc,testFunc);
				};
				this.rfcommRead = function(device){
					var readSuccess = device.readSuccess.bind(device,device.readSuccess);
					var readError = device.readError.bind(device,device.readError);
					navigator.bluetooth.rfcommRead(readSuccess,readError,device.deviceAddress);
				};
				this.rfcommWrite = function(device,value){
					var rfcommWriteSuccess = device.rfcommWriteSuccess.bind(device,device.rfcommWriteSuccess);
					var rfcommWriteError = device.rfcommWriteError.bind(device,device.rfcommWriteError);
					navigator.bluetooth.rfcommWrite(rfcommWriteSuccess,rfcommWriteError,device.deviceAddress,value);
				};
				this.rfcommSubscribe = function(device){
					var subscribeCallback = device.subscribeCallback.bind(device,device.subscribeCallback);
					navigator.bluetooth.rfcommSubscribe(subscribeCallback,testFunc,device.deviceAddress);
				};
				this.rfcommUnsubscribe = function(device){
					navigator.bluetooth.rfcommUnsubscribe(testFunc,testFunc,device.deviceAddress);
				};
				this.rfcommConnect = function(uuid,secure,device){
					var connectSuccess = device.connectSuccess.bind(device,device.connectSuccess);
					var connectError = device.connectError.bind(device,device.connectError);
					navigator.bluetooth.rfcommConnect(connectSuccess,connectError,device.deviceAddress,APPURL,uuid,secure);
				};
				this.rfcommDisconnect = function(device){
					var disconnectSuccess = device.disconnectSuccess.bind(device,device.disconnectSuccess);
					var disconnectError = device.disconnectSuccess.bind(device,device.disconnectError);
					navigator.bluetooth.rfcommDisconnect(disconnectSuccess,disconnectError,device.deviceAddress,APPURL);
				};
				this.rfcommListen = function(name,uuid,secure){
					navigator.bluetooth.rfcommListen(testFunc,testFunc,name,uuid,secure);
				};
				this.rfcommUnListen = function(name,uuid){
					navigator.bluetooth.rfcommUnListen(testFunc,testFunc,name,uuid);
				};
				
			}else{
				alert(type+" is not support now.");
			}
		};
		_.extend(BluetoothFuncs.prototype,{
		});
		
		/**
		 * Triggered when the device bluetooth state has been change.<b>Please Note:</b> this event should be listen after the 'bcready' event has been fired.
		 * @example BC.bluetooth.addEventListener('bluetoothstatechange', onBluetoothStateChange);
		 * function onBluetoothStateChange(){
		 * 	if(BC.bluetooth.isopen){
		 *		alert("bluetooth is opend!");
		 * 	}else{
		 *		alert("bluetooth is closed!");
		 *	}
		 * }
		 * @event Bluetooth#bluetoothstatechange
		 * @type {object}
		 */
		 
		/**
		 * Triggered when new advertising device has been found.
		 * @example BC.bluetooth.addEventListener("newdevice",addNewDevice);
		 * function addNewDevice(s){
		 *	var newDevice = s.target;
		 *	alert("new device be found! it's device Address is: " + newDevice.deviceAddress);
		 *	var newDevice = BC.bluetooth.devices[deviceAddress];
		 * }
		 * @event Bluetooth#newdevice
		 * @type {object}
		 */
		
		/**
		 * Bluetooth class includes all useful bluetooth global interfaces. 
		 * <p><b>Please note</b> that the application should not create Bluetooth object, BC manages the object model.
		 * @class
		 * @property {Array<Device>} devices - The advertising devices, this is filled after 'BC.Blueooth.StartScan' called
		 * @property {Array<Service>} services - The services add by 'AddService' interface
		 * @property {boolean} isopen - Bluetooth is open or not
		 */
		var Bluetooth = BC.Bluetooth = BC.EventDispatcher.extend({
			
			initialize :function(type){
				//get bluetooth operate function package
				this.bluetoothFuncs = new BC.BluetoothFuncs(type);
				
				//register functions in bluetooth object
				this.detectionBluetoothAPI = this.bluetoothFuncs.detectionBluetoothAPI;
				this.startScan = this.bluetoothFuncs.startScan;
				this.stopScan = this.bluetoothFuncs.stopScan;
				this.getDevices = this.bluetoothFuncs.getDevices;
				this.connect = this.bluetoothFuncs.connect;
				this.disconnect = this.bluetoothFuncs.disconnect;
				this.getDeviceAllData = this.bluetoothFuncs.getDeviceAllData;
				this.createPair = this.bluetoothFuncs.createPair;
				this.removePair = this.bluetoothFuncs.removePair;
				this.getConnectedDevices = this.bluetoothFuncs.getConnectedDevices;
				this.getPairedDevices = this.bluetoothFuncs.getPairedDevices;
				this.discoverServices = this.bluetoothFuncs.discoverServices;
				this.discoverCharacteristics = this.bluetoothFuncs.discoverCharacteristics;
				this.discoverDescriptors = this.bluetoothFuncs.discoverDescriptors;
				this.readDescriptor = this.bluetoothFuncs.readDescriptor;
				this.getEnvironment = this.bluetoothFuncs.getEnvironment;
				this.getBluetoothState = this.bluetoothFuncs.getBluetoothState;
				this.openBluetooth = this.bluetoothFuncs.openBluetooth;
				this.closeBluetooth = this.bluetoothFuncs.closeBluetooth;
				
				//character operation
				this.writeCharacteristic = this.bluetoothFuncs.writeCharacteristic;
				this.readCharacteristic = this.bluetoothFuncs.readCharacteristic;
				this.subscribe = this.bluetoothFuncs.subscribe;
				this.unsubscribe = this.bluetoothFuncs.unsubscribe;
				this.getRSSI = this.bluetoothFuncs.getRSSI;
				this.addServices =  this.bluetoothFuncs.addServices;
				this.removeService = this.bluetoothFuncs.removeService;
				this.notify = this.bluetoothFuncs.notify;
				
				//classical Bluetooth 2.1 interface
				this.startClassicalScan = this.bluetoothFuncs.startClassicalScan;
				this.stopClassicalScan = this.bluetoothFuncs.stopClassicalScan;
				this.rfcommRead = this.bluetoothFuncs.rfcommRead;
				this.rfcommWrite = this.bluetoothFuncs.rfcommWrite;
				this.rfcommSubscribe = this.bluetoothFuncs.rfcommSubscribe;
				this.rfcommUnsubscribe = this.bluetoothFuncs.rfcommUnsubscribe;
				this.rfcommConnect = this.bluetoothFuncs.rfcommConnect;
				this.rfcommDisconnect = this.bluetoothFuncs.rfcommDisconnect;
				this.rfcommListen = this.bluetoothFuncs.rfcommListen;
				this.rfcommUnListen = this.bluetoothFuncs.rfcommUnListen;
				
				this.bluetoothFuncs.initBluetooth();
				
				this.devices = {};
				this.services = {};
				this.isopen = false;

				this.UUIDMap = {};
				//the uuid should be lower case.
			},
		
			addSystemListener : function(eventName,callback,arg){
				var args = {};
				args.eventName = eventName;
				args.arg = arg;
				this.bluetoothFuncs.addSystemListener(callback,testFunc,args);
			},
		});
		/** 
		 * @memberof Bluetooth
		 * @method 
		 * @example 
		 * //Opens Bluetooth.
		 * BC.Bluetooth.OpenBluetooth(function(){alert("bluetooth opened!");},function(){alert("bluetooth open error!");});
		 * @param {function} [successCallback] - Success callback
		 * @param {function} [errorCallback] - Error callback
		 * @fires Bluetooth#bluetoothstatechange
		 */
		var OpenBluetooth = BC.Bluetooth.OpenBluetooth = function(success,error){
			BC.bluetooth.openBluetooth(success,error);
		};

		/** 
		 * @memberof Bluetooth
		 * @method 
		 * @example 
		 * //Close Bluetooth.
		 * BC.Bluetooth.CloseBluetooth(function(){alert("bluetooth closed!");},function(){alert("bluetooth close error!");});
		 * @param {function} [successCallback] - Success callback
		 * @param {function} [errorCallback] - Error callback
		 */
		var CloseBluetooth = BC.Bluetooth.CloseBluetooth = function(success,error){
			BC.bluetooth.closeBluetooth(success,error);
		};

		/** 
		 * Adds a BLE service to the smart phone.
		 * @memberof Bluetooth
		 * @method 
		 * @example //Generates a characteristic instance
		 * var permission = ["read","readEncrypted","readEncryptedMitm",
		 *					 "write","writeEncryptedMitm","writeEncrypted",
		 *					 "writeSigend","WriteSigendMitm"];			 
		 * var property = ["read","write","writeWithoutResponse",
		 *				   "broadcast","notify","indicate","authenticatedSignedWrites",
		 *				   "extendedProperties","notifyEncryptionRequired","indicateEncryptionRequired"];
		 * var service = new BC.Service({uuid:"ffe0"});
		 * var character1 = new BC.Characteristic({uuid:"ffe1",value:"01",type:"Hex",property:property,permission:permission});
		 * character1.addEventListener("onsubscribestatechange",function(s){alert("OBJECT EVENT!! onsubscribestatechange : (" + s.uuid + ") state:" + s.isSubscribed);});
		 * character1.addEventListener("oncharacteristicread",function(s){alert("OBJECT EVENT!! oncharacteristicread : (" + s.uuid + ")");});
		 * character1.addEventListener("oncharacteristicwrite",function(s){alert("OBJECT EVENT!! oncharacteristicwrite : (" + s.uuid + ") writeValue:" + s.writeValue.getHexString());});
		 * var descriptor1 = new BC.Descriptor({uuid:"2901",value:"00",type:"Hex",permission:permission});
		 * descriptor1.addEventListener("ondescriptorread",function(s){alert("OBJECT EVENT!! ondescriptorread : " + s.uuid);});
		 * //Adds a characteristic to a service. 
		 * service.addCharacteristic(character1);
		 * character1.addDescriptor(descriptor1);
		 *
		 * //Adds a service to the smart phone.
		 * BC.Bluetooth.AddService(service,app.addServiceSusscess,app.addServiceError);
		 * @param {Service} service - The service to add
		 * @param {function} [success] - Success callback
		 * @param {function} [error] - Error callback
		 */
		var AddService = BC.Bluetooth.AddService = function(service,success,error){
			var serviceObj = serializeService(service);
			BC.bluetooth.addServices(serviceObj,function(){
				BC.bluetooth.services[service.uniqueID] = service;
				success();
			},function(){
				error();
			});
		};
		/** 
		 * Removes a BLE service from the smart phone.
		 * @memberof Bluetooth
		 * @method 
		 * @example //Generates a service instance.
		 * var service = BC.Bluetooth.CreateService("0000ffe0-0000-1000-8000-00805f9b34fb");
		 *
		 * //Adds a service to smart phone.
		 * BC.Bluetooth.AddService(service,function(){alert("add service success!");},function(){alert("add service error!");});
		 * 
		 * //Removes a service. 
		 * BC.Bluetooth.RemoveService(service,function(){alert("remove service success!");},function(){alert("remove service error!");});
		 * @param {Service} service - The service to remove
		 * @param {function} [success] - Success callback
		 * @param {function} [error] - Error callback
		 */
		var RemoveService = BC.Bluetooth.RemoveService = function(service,success,error){
			BC.bluetooth.removeService(service,function(){
				delete BC.bluetooth.services[service.uniqueID];
				success();
			},function(){
				error();
			});
		};
		/** 
		 * Starts a scan for Bluetooth LE OR Classical(only in android platform) devices.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.StartScan();//start hybrid scan,in android platform the default scan strategy is scan for BLE device 5s first,
		 * then scan for classical device 12s,and restart the hybrid scan untill the BC.Bluetooth.StopScan be called. in IOS platform only start the LE scan.
		 * BC.Bluetooth.StartScan(null,["00000000-0000-0000-0000-000000000000"]);//start hybrid scan with LE service UUID.
		 * BC.Bluetooth.StartScan("LE"); //just start the LE scan
		 * BC.Bluetooth.StartScan("Classical"); //just start the classical scan in android platform, if the platform is IOS,call this method will nothing happen.
		 * @param {array} [type] - Is LE scan or classical scan, please input "LE" OR "Classical", if you want use default scan strategy, just ignore this param.
		 * @param {array} [uuids] - Array of services to look for. If null or [], it will scan all devices(just support BLE service UUID)
		 * @fires Bluetooth#newdevice
		 */
		var StartScan = BC.Bluetooth.StartScan = function(type,uuids){
			if(type){
				var type = type.toLowerCase();
				if(type == "le"){
					BC.bluetooth.startScan(uuids);
				}else if(type == "classical"){
					if(API !== "ios"){
						BC.bluetooth.startClassicalScan();
					}else{
						alert("classical scan is not support in IOS platform.");
					}
				}else{
					alert("please input 'LE' or 'Classical' string for type.");
				}
			}else{
				startDefaultScan(uuids);
			}
		};
		
		function startDefaultScan(uuids){
			if(API == "ios"){
				BC.bluetooth.startScan(uuids);
			}else if(API == "classical"){
				BC.bluetooth.startClassicalScan();
			}else{
				startDefaultScanImpl(uuids);
				BC.bluetooth.scanIntervalIndex = setInterval(function(){
					startDefaultScanImpl(uuids);
				},17100);
			}
		};
		
		function startDefaultScanImpl(uuids){
			BC.bluetooth.startScan(uuids);
			BC.bluetooth.bleScanIndex = setTimeout(function(){
				BC.bluetooth.stopScan(uuids);
				BC.bluetooth.startClassicalScan();
			},5000);
			BC.bluetooth.ClassicalScanIndex = setTimeout(function(){
				BC.bluetooth.stopClassicalScan();
			},17000);
		};
		
		/** 
		 * Starts a RFCOMM listen.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.RFCOMMListen("appName","7A9C3B55-78D0-44A7-A94E-A93E3FE118CE",true);
		 * @param {string} name - Name for the SDP record when creating server socket
		 * @param {string} uuid - Unique UUID for your application
		 * @param {boolean} secure - use secure connect or not
		 */
		var RFCOMMListen = BC.Bluetooth.RFCOMMListen = function(name,uuid,secure){
			BC.bluetooth.rfcommListen(name,uuid,secure);
		};
		
		/** 
		 * Stops a RFCOMM listen.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.RFCOMMUnListen("appname","your listen uuid");
		 * @param {string} name - Name for the SDP record when creating server socket
		 * @param {string} uuid - Unique UUID for your application
		 */
		var RFCOMMUnListen = BC.Bluetooth.RFCOMMUnListen = function(name,uuid){
			BC.bluetooth.rfcommUnListen(name,uuid);
		};
		
		function isNewDevice(deviceAddress){
			var res = true;
			_.each(BC.bluetooth.devices,function(device){
				if(device.deviceAddress == deviceAddress){
					res = false;
				}
			});
			return res;
		};
		
		/** 
		 * Stops a scanning.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.StopScan();
		 */
		var StopScan = BC.Bluetooth.StopScan = function(){
			BC.bluetooth.stopScan();
			if(API !== "ios"){
				BC.bluetooth.stopClassicalScan();
			}
			if(BC.bluetooth.scanIntervalIndex){
				clearTimeout(BC.bluetooth.classicalScanIndex);
				clearTimeout(BC.bluetooth.bleScanIndex);
				clearInterval(BC.bluetooth.scanIntervalIndex);
			}
		};
		
		/** 
		 * Gets a paired device list.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.GetPairedDevices(function(mes){alert(JSON.stringify(mes));});
		 * @param {function} successCallback - Success callback
		 * @param {function} [errorCallback] - Error callback
		 */
		var GetPairedDevices = BC.Bluetooth.GetPairedDevices = function(success,error){
			BC.bluetooth.getPairedDevices(success,error);
		};
		/** 
		 * Gets a connected device list.
		 * @memberof Bluetooth
		 * @method 
		 * @example BC.Bluetooth.GetConnectedDevices(function(mes){alert(JSON.stringify(mes));});
		 * @param {function} successCallback - Success callback
		 * @param {function} [errorCallback] - Error callback
		 */
		var GetConnectedDevices = BC.Bluetooth.GetConnectedDevices = function(success,error){
			BC.bluetooth.getConnectedDevices(success,error);
		};

		var serializeService = function(service){
			var serviceObj = {};
			serviceObj.services = [];
			var serviceItem = {};
			serviceItem.uniqueID = service.uniqueID;
			serviceItem.serviceType = service.isMajor;
			serviceItem.serviceUUID = service.uuid;
			serviceItem.characteristics = [];
			_.each(service.characteristics,function(chara){
				var charaObj = {};
				charaObj.characteristicValueType = chara.type;
				charaObj.characteristicValue = chara.value;
				charaObj.characteristicUUID = chara.uuid;
				charaObj.characteristicProperty = chara.property;
				charaObj.characteristicPermission = chara.permission;
				charaObj.descriptors = [];
				serviceItem.characteristics.push(charaObj);
				
				_.each(chara.descriptors,function(des){
					var desObj = {};
					desObj.descriptorValueType = des.type;
					desObj.descriptorValue = des.value;
					desObj.descriptorUUID = des.uuid;
					desObj.descriptorPermission = des.permission;
					charaObj.descriptors.push(desObj);
				});
			});
			
			serviceObj.services.push(serviceItem);
			return JSON.stringify(serviceObj);
		};
		
		/**
		 * DataValue provides some useful functions to convert raw byte data.
		 * @class
		 * @param {ArrayBuffer} value - The raw value
		 * @property {ArrayBuffer} value - The raw value of DataValue object
		 */
		var DataValue = BC.DataValue = function(value){
			this.value = value;
		};
		_.extend(DataValue.prototype,{
		
			/**
			 * Gets a ASCII string from ArrayBuffer.
			 * @memberof DataValue
			 * @example //Gets a Device instance.
			 * 	device.services[3].characteristic[0].descriptors[0].read(function(data){
			 *		alert(data.value.getASCIIString());
			 *	});
			 * @instance
			 * @returns {string} A raw value in ASCII string
			 */		
			getASCIIString : function(){
				var length = this.value.byteLength;
				var dv = new DataView(this.value);
				var result= "";
				for (var i=0; i<length; i++) {
					result+= String.fromCharCode(dv.getUint8(i)).toString(16);
				}
				return result;
			},
		   
			/**
			 * Gets a Unicode string from ArrayBuffer.
			 * @memberof DataValue
			 * @example //Gets the Device instance.
			 * 	device.services[3].characteristic[0].descriptors[0].read(function(data){
			 *		alert(data.value.getUnicodeString());
			 *	});
			 * @instance
			 * @returns {string} A raw value in Unicode string
			 */	
			getUnicodeString : function(){
				var length = this.value.byteLength;
				var dv = new DataView(this.value);
				var result= "";
				if(length >= 2 && length % 2 == 0){
					for (var i=0; i<length;) {
						result+=String.fromCharCode(dv.getUint8(i++)*256+dv.getUint8(i++));
					}
				}
				return result;
			},
			/**
			 * Gets a Hex string from ArrayBuffer.
			 * @memberof DataValue
			 * @example //Gets a Device instance.
			 * 	device.services[3].characteristic[0].descriptors[0].read(function(data){
			 *		alert(data.value.getHexString());
			 *	});
			 * @instance
			 * @returns {string} A raw value in Hex string
			 */
			getHexString : function(){
				var length = this.value.byteLength;
				var dv = new DataView(this.value);
				var result= "";
				for (var i=0; i<length; i++) {
					if(dv.getUint8(i) < 16){
						result+= '0' + dv.getUint8(i).toString(16);
					}else{
						result+= dv.getUint8(i).toString(16);
					}
				}
				return result;
			},
			
			/**
			 * Appends the dataValue object to the tail.
			 * @memberof DataValue
			 * @example //Gets a Device instance.
			 * 	dataValue.append(otherDataValue);
			 * @param {DataValue} dataValue - The dataValue to append
			 * @instance
			 * @returns {DataValue} return this pointer to support the chain operation
			 */
			append : function(dataValue){
				 if(!dataValue || !dataValue.value){
					return this;
				 }
				 var dataView = new DataView(dataValue.value);
				 var totalLength = 0;
				 var thisLength = 0;
				 var thatLength = 0;
				 if(this.value && dataValue.value){
					thisLength = this.value.byteLength;
					thatLength = dataValue.value.byteLength;
					length = thisLength + thatLength;
				 }else if(this.value){
					thisLength = this.value.byteLength;
					length = thisLength;
				 }else if(dataValue.value){
					thatLength = dataValue.value.byteLength;
					length = thatLength;
				 }
				 var ints = new Uint8Array(length);
				 if(thisLength != 0){
					var thisDataView = new DataView(this.value);
					for (var i = 0; i < thisLength; i++) {
						ints[i] = thisDataView.getUint8(i);
					}
				 }
				 for (var j = thisLength,r = 0; j < length; j++,r++) {
					ints[j] = dataView.getUint8(r);
				 }
				this.value = ints.buffer;
				return this;
			}
		});
		
		/**
		 * Plugin class provide a way to develop your HW SDK based on BC.js . Plugin should inherit this class,and implement your own 'pluginInitialize' method and fire the pluginReadyEvent after the plugin is ready.
		 * @class
		 * @example document.addEventListener('bccoreready', onBCCoreReady, false);
		 * function onBCCoreReady(){
		 *	var eventName = "org.bcsphere.ibeacon.ready";
		 *	var	iBeaconManager = BC.iBeaconManager = new BC.IBeaconManager("org.bcsphere.ibeacon",eventName);
		 *	//plugin is ready, fire the event.
		 *	BC.bluetooth.dispatchEvent(eventName);
		 * }
		 * var IBeaconManager = BC.IBeaconManager = BC.Plugin.extend({
		 *	pluginInitialize : function(){
		 *	 this.ibeacons = {};
		 *	 this.regions = [];
		 *	},
		 * }
		 * @property {string} pluginID - the pluginID of your plugin, it is recommend to use reverse domain name format(such as "your.plugin.id").
		 * @property {string} pluginReadyEvent - The services add by 'AddService' interface
		 */
		var Plugin = BC.Plugin = BC.EventDispatcher.extend({
			initialize : function(pluginID,pluginReadyEvent){
				this.pluginID = pluginID;
				this.isReady = false;
				BC.plugins[pluginID] = this;
				this.pluginReadyEvent = pluginReadyEvent;
				BC.bluetooth.addEventListener(pluginReadyEvent,function(){
					BC.plugins[pluginID].isReady = true;
					BC.bluetooth.removeEventListener(pluginReadyEvent);
				});

				this.pluginInitialize.apply(this, arguments);
			},
		});
		
		/**
		 * Profile is a bundle of functions to operate the devices, our implements are based on bluetooth specification(www.bluetooth.org).
		 * @example //define a custom profile
		 * var FindMeProfile = BC.FindMeProfile = BC.Profile.extend({
		 *	
		 *	no_alert : function(device){
		 *	  this.alert(device,'0');
		 *	},
		 *  
		 *	mild_alert : function(device){
		 *	  this.alert(device,'1');
		 *	},
		 *   
		 *	high_alert : function(device){
		 *	  this.alert(device,'2');
		 *	},
		 *
		 *	alert : function(device,level){
		 *		device.discoverServices(function(){
		 *			var service = device.getServiceByUUID(serviceUUID)[0];
		 *			service.alert(level);
		 *		});
		 *	},
		 *	
		 *});
		 *
		 * //use profile
		 * var findmeProfile = new BC.FindMeProfile();
		 * findmeProfile.high_alert(device);
		 * @class
		 */
		var Profile = BC.Profile = BC.EventDispatcher.extend({
		
		});
		
		/**
		 * Triggered when the device has been disconnected.
		 * @example var device = BC.bluetooth.devices["34:23:41:66:37:65"];
		 * device.addEventListener('devicedisconnected', onDeviceDisconnect);
		 * function onDeviceDisconnect(deviceObj){
		 *  var deviceAddress = deviceObj.deviceAddress;
		 * 	alert("device:"+ deviceAddress +" is disconnect!");
		 * }
		 * @event Device#devicedisconnected
		 * @type {object}
		 */
		
		/**
		 * Triggered when the device has been connected.
		 * @example var device = BC.bluetooth.devices["34:23:41:66:37:65"];
		 * device.addEventListener('deviceconnected', onConnected);
		 * function onConnected(deviceObj){
		 *  var deviceAddress = deviceObj.deviceAddress;
		 * 	alert("new device connected! it's device Address is: "+deviceAddress);
		 * }
		 * @event Device#deviceconnected
		 * @type {object}
		 */
		
		/**
		 * Device represents the remote BLE Peripheral device. 
		 * <p><b>Please note</b> that the application should not create Device object, BC manages the object model.The plugin can inherit this class,and encapsulate the function based on this class
		 * @example var BLELight = BC.BLELight = BC.Device.extend({
		 *  deviceInitialize : function(){},
		 *  open : function(successFunc,errorFunc){
		 *  	this.services[0].characteristics[1].write("Hex","1",successFunc,errorFunc);
		 *  },
		 *  close : function(successFunc,errorFunc){
		 *  	this.services[0].characteristics[1].write("Hex","0",successFunc,errorFunc);
		 *  },
		 * })
		 * @class
		 * @param {string} deviceAddress - The Address of the device(Address is assigned by the smart phone,if there is no Address, it is recommended to new the device instance after obtaining devices' information from BC.Bluetooth.StartScan)
		 * @param {string} deviceName - The name of the device
		 * @param {object} advertisementData - The device advertisement data, includes LocalName, TxPowerLevel, IsConnectable, ServiceData, ManufacturerData, ServiceUUIDs, SolicitedServiceUUIDs, OverflowServiceUUIDs
		 * @param {boolean} isConnected - If this device is connected
		 * @param {int} RSSI - The RSSI of the device
		 * @property {string} deviceName - The name of this device
		 * @property {string} deviceAddress - The Address of this device
		 * @property {Array<Service>} services - The services of this device
		 * @property {boolean} isConnected - If this device is connected
		 * @property {int} RSSI - The RSSI of the device
		 * @property {boolean} isPrepared - If this device is prepared ('prepared' means this device object can be used to access the services' characteristics)
		 * @property {function} connectSuccessCallback - This success callback function will be called after this device is well prepared
		 * @property {DataValue} systemID - The system ID of this device
		 * @property {DataValue} modelNum - The model number of this device
		 * @property {DataValue} serialNum - The serial number of this device
		 * @property {DataValue} firmwareRevision - The firmware revision of this device
		 * @property {DataValue} hardwareRevision - The hardware revision of this device
		 * @property {DataValue} softwareRevision - The software revision of this device
		 * @property {DataValue} manufacturerName - The manufacturer name of this device
		 * @property {type} type - The type of this device('BLE' or 'Classical')
		 */
		var Device = BC.Device = BC.EventDispatcher.extend({
			
			initialize : function(arg){
				this.deviceAddress = arg.deviceAddress;
				this.deviceName = arg.deviceName;
				this.advertisementData = arg.advertisementData;
				this.isConnected = arg.isConnected;
				this.services = [];
				this.isPrepared = false;
				this.systemID = null;
				this.modelNum = null;
				this.serialNum = null;
				this.firmwareRevision = null;
				this.hardwareRevision = null;
				this.softwareRevision = null;
				this.manufacturerName = null;
				this.RSSI = arg.RSSI;
				this.isDiscovering = false;
				this.advTimestamp = new Date().getTime();
				this.type = arg.type;
				if(arg.deviceAddress){
					BC.bluetooth.devices[arg.deviceAddress] = this;
				}
				
				if(!BC.Tools.IsEmpty(this.deviceInitialize)){
					this.deviceInitialize.apply(this, arguments);
				}
			},
			
			/**
			 * Initiates a connection to the peripheral(the connect can be BLE connect OR rfcomm connect(only in android platform) base on the device.type).
			 * @memberof Device
			 * @example //Gets a the Device instance.
			 * var device = window.device = BC.bluetooth.devices["78:C5:E5:99:26:37"];
			 * device.connect(function(){alert("device is already connected well!");});
			 * //device.connect(function(){alert("device is already connected well!");},null,"7A9C3B55-78D0-44A7-A94E-A93E3FE118CE",ture); //connect if the device is classical
			 * device.addEventListener("deviceconnected",function(s){alert("device:" + s.deviceAddress + "is connected successfully!")});
			 * device.addEventListener("devicedisconnected",function(s){alert("device:" + s.deviceAddress + "is connected successfully!")});
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @param {string} [uuid] - If connect with classical device,pass the uuid into this param(this param is required when you want to connect a classical device)
			 * @param {boolean} [secure] - The classical connection is secure or not(this param is required when you want to connect a classical device)
			 * @fires Device#deviceconnected
			 * @instance
			 */
			connect : function(success,error,uuid,secure){
				this.success = success;
				this.error = error;
				if(!this.isConnected){
					if(this.type == "BLE"){
						BC.bluetooth.connect(this);
					}else{
						BC.bluetooth.rfcommConnect(uuid,secure,this);
					}
				}else{
					success();
				}
			},
			
			connectSuccess : function(){
				this.isConnected = true;
				this.dispatchEvent("deviceconnected");
				this.success();
			},
			
			connectError : function(){
				this.error();
			},
			
			/**
			 * Discovers services in peripheral.</br>After calling this interface, all the characteristics and descriptors is accessible. 
			 * @memberof Device
			 * @example //Gets a the Device instance.
			 * var device = window.device = BC.bluetooth.devices["78:C5:E5:99:26:37"];
			 * device.connect(connectSuccess,function(){alert("connect device error!");});
			 * function connectSuccess(){
			 *	device.prepare(function(){alert("device prepared success!")},function(message){alert(message);});
			 * }
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */		
			prepare : function(success,error){
				this.success = success;
				this.error = error;
				if(!this.isConnected){
					this.error("device is not connected!please call device.connect() first!");
					return;
				}
				if(!this.isDiscovering){
				  this.isDiscovering = true;
				  BC.bluetooth.getDeviceAllData(this);
				}
			},
			
			/**
			 * Discovers services for the device.
			 * @memberof Device
			 * @example device.discoverServices();
			 * @param {function} [successCallback] - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			discoverServices : function(success,error){
				this.success = success;
				this.error = error;
				if(this.services == undefined || this.services == null || this.services.length==0){
					if(!this.isDiscovering){
						this.isDiscovering = true;
						BC.bluetooth.discoverServices(this);
					}
				}else{
					this.success();
				}
			},
			
			discoverServicesSuccess : function(){
				var rawObj = arguments[1];
				var device = this;
				this.isDiscovering = false;
				this.services = [];
				_.each(rawObj.services, function(service){
						var sindex = service.serviceIndex;
						var sname = service.serviceName;
						var suuid = service.serviceUUID;
						if(BC.bluetooth.UUIDMap[suuid]){
							device.services.push(new BC.bluetooth.UUIDMap[suuid]({index:sindex,uuid:suuid,name:sname,device:device}));
						}else{
							device.services.push(new BC.Service({index:sindex,uuid:suuid,name:sname,device:device}));
						}
					}
				);

				if(this.success !== null){
					this.success();
				}
			},
			
			discoverServicesError : function(){
				this.isDiscovering = false;
				this.error();
			},
			
			processDeviceAllData : function(){
				var rawObj = arguments[1];
				var device = this;
				this.isDiscovering = false;
				this.services = [];
				_.each(rawObj.services, function(service){
						var sindex = service.serviceIndex;
						var sname = service.serviceName;
						var suuid = service.serviceUUID;
						var chars = service.characteristics;
						if(BC.bluetooth.UUIDMap[suuid]){
							device.services.push(new BC.bluetooth.UUIDMap[suuid]({index:sindex,uuid:suuid,name:sname,device:device,chars:chars}));
						}else{
							device.services.push(new BC.Service({index:sindex,uuid:suuid,name:sname,device:device,chars:chars}));
						}
					}
				);
				this.isPrepared = true;
				if(this.success !== null){
					this.success();
				}
			},

			getAllDataError : function(){
			  this.isDiscovering = false;
			  if(this.error !== null){
				  this.error();
			  }
			},
			
			/**
			 * Disconnects a peripheral.
			 * @memberof Device
			 * @example device.disconnect();
			 * @param {function} [successCallback] - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */		
			disconnect : function(success,error){
				this.success = success;
				this.error = error;
				if(this.isConnected){
					if(this.type == "BLE"){
						BC.bluetooth.disconnect(this);
					}else if(this.type == "Classical"){
						BC.bluetooth.rfcommDisconnect(this);
					}
				}else{
					success();
				}
			},
			
			disconnectSuccess : function(){
				this.isConnected = false;
				this.success();
			},
			
			disconnectError : function(){
				this.error();
			},
			
			/**
			 * Gets the RSSI value of a connected peripheral.
			 * @memberof Device
			 * @example device.getRSSI(function(RSSI_value){alert("the RSSI value is:"+RSSI_value);});
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			getRSSI : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.getRSSI(this);
			},
			
			getRSSISuccess : function(){
				this.success(arguments[1].RSSI);
			},
			
			getRSSIError : function(){
				this.error();
			},
			
			/**
			 * Gets the device Address service(0000180a-0000-1000-8000-00805f9b34fb)	value.
			 * @memberof Device
			 * @example device.getDeviceInfo(function(){
			 *		alert("System ID:"+app.device.systemID.getASCIIString()+"\n"+
			 *		  "Model Number:"+app.device.modelNum.getASCIIString()+"\n"+
			 *		  "Serial Number:"+app.device.serialNum.getASCIIString()+"\n"+
			 *		  "Firmware Revision:"+app.device.firmwareRevision.getASCIIString()+"\n"+
			 *		  "Hardware Revision:"+app.device.hardwareRevision.getASCIIString()+"\n"+
			 *		  "Software Revision:"+app.device.softwareRevision.getASCIIString()+"\n"+
			 *		  "Manufacturer Name:"+app.device.manufacturerName.getASCIIString());	
			 *	  });
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			getDeviceInfo : function(success,error){
				var deviceAddressindex = -1;
				_.each( this.services, function( service ) {
					if(service.uuid === "0000180a-0000-1000-8000-00805f9b34fb"){
						deviceAddressindex = service.index;
					}
				} );
				var self = this;
				var deviceInfoService = this.services[deviceAddressindex];
				deviceInfoService.discoverCharacteristics(function(){
					var deviceInfoCharactertistic = deviceInfoService.characteristics;
					deviceInfoCharactertistic[0].read(function(data){
						self.systemID = data.value;
						deviceInfoCharactertistic[1].read(function(data){
							self.modelNum = data.value;
							deviceInfoCharactertistic[2].read(function(data){
								self.serialNum = data.value;
								deviceInfoCharactertistic[3].read(function(data){
									self.firmwareRevision = data.value;
									deviceInfoCharactertistic[4].read(function(data){
										self.hardwareRevision = data.value;
										deviceInfoCharactertistic[5].read(function(data){
											self.softwareRevision = data.value;
											deviceInfoCharactertistic[6].read(function(data){
												self.manufacturerName = data.value;
												success();
											},function(){error();});
										},function(){error();});
									},function(){error();});
								},function(){error();});
							},function(){error();});
						},function(){error();});
					},function(){error();});
				},testFunc);

			},
			
			/**
			 * Initializes a paired request to the device.
			 * @memberof Device
			 * @example device.createPair(function(mes){alert("create pair with device success!")});
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			createPair : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.createPair(this);
			},
			
			createPairSuccess : function(){
				alert("create Pair Success!");
				this.success();
			},
			
			createPairError : function(){
				alert("create Pair Error!");
				this.error();
			},
			
			/**
			 * Initializes a unpaired request to the device.
			 * @memberof Device
			 * @example device.removePair(function(mes){alert("remove pair with device success!")});
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			removePair : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.removePair(this);
			},
			
			removePairSuccess : function(){
				alert("remove Pair Success!");
				this.success();
			},
			
			removePairError : function(){
				alert("remove Pair Error!");
				this.error();
			},
			
			/**
			 * Gets service by UUID.
			 * @memberof Device
			 * @example alert(app.device.getServiceByUUID("fff0")[0].uuid);
			 * alert(app.device.getServiceByUUID("0000fff0-0000-1000-8000-00805f9b34fb")[0].uuid);
			 * @param {string} uuid - The uuid(128bit/16bit) of service
			 * @instance
			 * @returns {Array<Service>} An array of Service
			 */
			getServiceByUUID : function(uuid){
				var uuid = uuid.toLowerCase();
				var result = [];
				var uuid_128 = BC.Tools.ChangeTo128UUID(uuid);
				_.each(this.services, function(service){
						service.uuid = service.uuid.toLowerCase();
						if(service.uuid == uuid_128){
							result.push(service);
						}
					}
				);
				return result;
			},
			
			/**
			 * Reads value from classical RFCOMM interface.(only support Bluetooth2.1 device)
			 * @memberof Device
			 * @example 
			 * device.rfcommConnect(function(){
			 *	device.rfcommRead(readSuccess);
			 * });
			 * function readSuccess(data){
			 *	alert("Data : "+JSON.stringify(data.value)+" \n Time :"+data.date);
			 * }
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			rfcommRead : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.rfcommRead(this);
			},
			readSuccess : function(){
				var data = {};
				data.deviceAddress=this.deviceAddress;
				data.date = arguments[1].date;
				data.value = new BC.DataValue(BC.Tools.Base64ToBuffer(arguments[1].value));
				this.success(data);
			},
			readError : function(){
				this.error("read data error");
			},
			
			/**
			 * Writes the value into RFCOMM interface.(only support Bluetooth2.1 device)
			 * @memberof Device
			 * @example 
			 * //write after device is well prepared.
			 * device.rfcommConnect(function(){
			 *	device.rfcommWrite("Hex","01",writeSuccess);
			 * });
			 * function writeSuccess(data){
			 *	alert("write success!");
			 * }
			 * @param {string} type - The type of the value to write ('hex'/'ascii'/'unicode'/'raw')
			 * @param {string/Uint8Array} value - The value write to the RFCOMM, if the 'type' is 'raw', the value type should be Uint8Array
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			rfcommWrite : function(type,value,success,error){
				this.success = success;
				this.error = error;
				if(type.toLowerCase() == "hex"){
					value = BC.Tools.HexToBase64(value);
				}else if(type.toLowerCase() == "ascii"){
					value = BC.Tools.ASCIIToBase64(value);
				}else if(type.toLowerCase() == "unicode"){
					value = BC.Tools.UnicodeToBase64(value);
				}else if(type.toLowerCase() == "raw"){
					value = BC.Tools.ConvertToBase64(value);
				}else{
					error("Please input 'hex'/'ascii'/'unicode' type.");
					return;
				}
				BC.bluetooth.rfcommWrite(this,value);
			},
			rfcommWriteSuccess : function(){
				this.success(arguments);
			},
			rfcommWriteError : function(){
				this.error(arguments);
			},
			
			/**
			 * Subscribes the notification of this device RFCOMM.
			 * @memberof Device
			 * @example 
			 * device.rfcommSubscribe(onDataAvaliable);
			 * function onDataAvaliable(data){
			 *	$("#notifyValue_hex").html(data.value.getHexString());
			 *	$("#notifyValue_unicode").html(data.value.getUnicodeString());
			 *	$("#notifyValue_ascii").html(data.value.getASCIIString());
			 *	$("#notifyDate").html(data.date);
			 * }
			 * @param {function} callback - Called when peripheral sends data to this device.
			 * @instance
			 */
			rfcommSubscribe : function(callback){
				this.callback = callback;
				BC.bluetooth.rfcommSubscribe(this);
			},
			subscribeCallback : function(){
				var obj = arguments[1];
				var data = {};
				data.value = new BC.DataValue(BC.Tools.Base64ToBuffer(obj.value));
				data.date = obj.date;
				data.deviceAddress = obj.deviceAddress;
				this.callback(data);
			},
			
			/**
			 * Unsubscribes the notification of this device RFCOMM.
			 * @memberof Device
			 * @example 
			 * device.rfcommUnsubscribe();
			 * @instance
			 */
			rfcommUnsubscribe : function(){
				BC.bluetooth.rfcommUnsubscribe(this);
			},
			
		});
		Device.extend = extend;
		
		//GATTEntity 
		var GATTEntity = BC.GATTEntity = BC.EventDispatcher.extend({
			
			initialize : function(arg){
				this.index = arg.index;
				this.uuid = BC.Tools.ChangeTo128UUID(arg.uuid);
				this.name = arg.name;
				this.upper = arg.upper;
				this.device = arg.device;
				this.entityInitialize.apply(this, arguments);
			},
			
		});
		GATTEntity.extend = extend;
	  
	 
	  /**
	   * BLE Service class.
	   * @class
	   * @property {Array<Characteristic>} characteristics - The characteristics of this service
	   * @property {Device} device - The device to which this service belongs
	   * @property {string} uuid - The uuid of this service
	   * @property {string} name - The name of this service
	   */
	  var Service = BC.Service = GATTEntity.extend({
			characteristics : null,
			
			entityInitialize : function(arg){
				var chars = arg.chars;
				var service = this;
				var device = this.device;
				this.characteristics = [];

				var isMajor;
				if(arg.type == null){
					isMajor = 0;
				}else{
					isMajor = arg.type;
				}
				this.isMajor = isMajor;
				var timestr = new Date().getTime();
				var randomnum = Math.floor(Math.random()*10000);
				this.uniqueID = randomnum.toString();
				
				_.each(chars, function(characteristic){
						var cindex = characteristic.characteristicIndex;
						var cname = characteristic.characteristicName;
						var cuuid = characteristic.characteristicUUID;
						var dess = characteristic.descriptors;
						var property = characteristic.characteristicProperty;
						service.characteristics.push(new BC.Characteristic({index:cindex,uuid:cuuid,name:cname,device:device,upper:service,dess:dess,property:property}));
					}
				);
				
				this.addCharacteristic = function(chara){
					chara.upper = this;
					chara.index = this.characteristics.length;
					this.characteristics.push(chara);
				};
			},
			
			/**
			 * Discovers characteristics for the service.
			 * @memberof Service
			 * @example device.services[3].discoverCharacteristics();
			 * @param {function} [successCallback] - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			discoverCharacteristics : function(success,error){
				this.success = success;
				this.error = error;
				if(this.characteristics == undefined || this.characteristics == null || this.characteristics.length == 0){
					BC.bluetooth.discoverCharacteristics(this);
				}else{
					this.success();
				}
			},
			
			discoverCharacteristicsSuccess : function(){
				var chars = arguments[1];
				var service = this;
				var device = this.device;
				service.characteristics = [];
				_.each(chars.characteristics, function(characteristic){
						var cindex = characteristic.characteristicIndex;
						var cname = characteristic.characteristicName;
						var cuuid = characteristic.characteristicUUID;
						var property = characteristic.characteristicProperty;
						service.characteristics.push(new BC.Characteristic({index:cindex,uuid:cuuid,name:cname,device:device,upper:service,property:property}));
					}
				);
				this.success();
			},
			
			discoverCharacteristicsError : function(){
				this.error();
			},
			
			/**
			 * Gets characteristics by UUID.
			 * @memberof Service
			 * @example app.device.services[3].getCharacteristicByUUID("FFF1")[0].write("Hex","1",function(){alert("success!");});
			 * @param {string} uuid - The uuid(128bit/16bit) of characteristic
			 * @instance
			 * @returns {Array<Characteristic>} An array of Characteristic
			 */
			getCharacteristicByUUID : function(uuid){
				var uuid = uuid.toLowerCase();
				var result = [];
				var uuid_128 = BC.Tools.ChangeTo128UUID(uuid);
				
				_.each(this.characteristics, function(characteristic){
					characteristic.uuid = characteristic.uuid.toLowerCase();
					if(characteristic.uuid == uuid_128){
							result.push(characteristic);
						}
					}
				);
				return result;
			},
	  });


		/**
		 * Triggered when server's characteristic has been subscribe/unsubscribe.
		 * @example var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",["notify"],["read","write"]);
		 * character1.addEventListener("onsubscribestatechange",function(s){
		 *	alert("onsubscribestatechange : (" + s.uuid + ") state:" + s.isSubscribed);
		 * });
		 * @event Characteristic#onsubscribestatechange
		 * @type {object}
		 */
		
		/**
		 * Triggered when service characteristic has been read.
		 * @example var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",["read"],["read","write"]);
		 * character1.addEventListener("oncharacteristicread",function(s){
		 *  alert("oncharacteristicread : (" + s.uuid + ")");
		 * });
		 * @event Characteristic#oncharacteristicread
		 * @type {object}
		 */
		
		/**
		 * Triggered when service characteristic has been written.
		 * @example var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",["write"],["read","write"]);
		 * character1.addEventListener("oncharacteristicwrite",function(s){
		 *	alert("oncharacteristicwrite : (" + s.uuid + ") writeValue:" + s.writeValue.getHexString());
		 * });
		 * @event Characteristic#oncharacteristicwrite
		 * @type {object}
		 */
	  
		/**
		 * BLE Characteristic class.
		 * @class
		 * @property {Array<Descriptor>} descriptors - The descriptors of this characteristic
		 * @property {Device} device - The device to which this characteristic belongs
		 * @property {string} uuid - The uuid of this characteristic
		 * @property {string} name - The name of this characteristic
		 */
		var Characteristic = BC.Characteristic = GATTEntity.extend({
			descriptors : null,
			value : null,
			property : null,
			type : null,
			isSubscribed : false,
			
			entityInitialize : function(arg){
				var dess = arg.dess;
				this.property = arg.property;
				this.permission = arg.permission;
				this.type = arg.type;
				this.value = arg.value;
				
				this.descriptors = [];
				var chara = this;
				var device = this.device;
				_.each(dess,function(des){
					var dindex = des.descriptorIndex;
					var dname = des.descriptorName;
					var duuid = des.descriptorUUID;
					chara.descriptors.push(new BC.Descriptor({index:dindex,uuid:duuid,name:dname,upper:chara,device:device}));
				});
				
				this.addDescriptor = function(des){
					des.upper = this;
					des.index = this.descriptors.length;
					this.descriptors.push(des);
				};
			},
			
			/**
			 * Reads the characteristic value.
			 * @memberof Characteristic
			 * @example 
			 * //Reads after device is prepared well or after 'service.discoverCharacteristics' interface Called successfully.
			 * device.prepare(function(){
			 *	device.services[3].characteristics[0].read(readSuccess);
			 * });
			 * function readSuccess(data){
			 *	alert("Data : "+JSON.stringify(data.value)+" \n Time :"+data.date);
			 * }
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			read : function(success,error){
				this.success = success;
				this.error = error;
				if(this.property.contains("read")){
					BC.bluetooth.readCharacteristic(this);
				}
			},
			readSuccess : function(){
				var data = {};
				data.deviceAddress=this.device.deviceAddress;
				data.serviceIndex = this.upper.index;
				data.characteristicIndex = this.index;
				data.date = arguments[1].date;
				data.value = new BC.DataValue(BC.Tools.Base64ToBuffer(arguments[1].value));
				//data.value = new BC.DataValue(arguments[1].value);
				this.success(data);
			},
			readError : function(){
				this.error("read data error");
			},
			
			/**
			 * Writes the characteristic value.
			 * @memberof Characteristic
			 * @example 
			 * //write after device is well prepared.
			 * device.connect(function(){
			 *	device.services[3].characteristics[0].write("Hex","01",writeSuccess);
			 * });
			 * function writeSuccess(data){
			 *	alert("write success!");
			 * }
			 * @param {string} type - The type of the value to write ('hex'/'ascii'/'unicode'/'raw'/'base64')
			 * @param {string/Uint8Array} value - The value write to this characteristic, if the 'type' is 'raw', the value type should be Uint8Array
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			write : function(type,value,success,error){
				this.success = success;
				this.error = error;
				if(type.toLowerCase() == "hex"){
					value = BC.Tools.HexToBase64(value);
				}else if(type.toLowerCase() == "ascii"){
					value = BC.Tools.ASCIIToBase64(value);
				}else if(type.toLowerCase() == "unicode"){
					value = BC.Tools.UnicodeToBase64(value);
				}else if(type.toLowerCase() == "raw"){
					value = BC.Tools.ConvertToBase64(value);
				}else if(type.toLowerCase() == "base64"){
					value = value;
				}else{
					error("Please input 'hex'/'ascii'/'unicode'/'raw'/'base64' type.");
					return;
				}
				if(this.property.contains("write") || this.property.contains("writeWithoutResponse")){
					BC.bluetooth.writeCharacteristic(this,value);
				}else{
					error("This characteristic can't be written, please add 'write'/'writeWithoutResponse' in the property.");
				}
			},
			writeSuccess : function(){
				this.success(arguments);
			},
			writeError : function(){
				this.error(arguments);
			},
			
			/**
			 * Subscribes the notification of this characteristic.
			 * @memberof Characteristic
			 * @example 
			 * device.services[3].characteristics[3].subscribe(onNotify);
			 * function onNotify(data){
			 *	$("#notifyValue_hex").html(data.value.getHexString());
			 *	$("#notifyValue_unicode").html(data.value.getUnicodeString());
			 *	$("#notifyValue_ascii").html(data.value.getASCIIString());
			 *	$("#notifyDate").html(data.date);
			 * }
			 * @param {function} callback - Called when peripheral sends notification of this characteristic.
			 * @instance
			 */
			subscribe : function(callback){
				this.callback = callback;
				if(this.property.contains("notify") || this.property.contains("indicate")){
					BC.bluetooth.subscribe(this);
				}
			},
			subscribeCallback : function(){
				var obj = arguments[1];
				var data = {};
				data.value = new BC.DataValue(BC.Tools.Base64ToBuffer(obj.value));
				//data.value = new BC.DataValue(obj.value);
				data.serviceIndex = obj.serviceIndex;
				data.characteristicIndex = obj.characteristicIndex;
				data.date = obj.date;
				data.deviceAddress = obj.deviceAddress;
				this.callback(data);
			},
			
			/**
			 * Unsubscribes the notification of this characteristic.
			 * @memberof Characteristic
			 * @example device.services[3].characteristics[3].unsubscribe();
			 * @param {function} [successCallback] - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			unsubscribe : function(success,error){
				this.success = success;
				this.error = error;
				if(this.property.contains("notify")){
					BC.bluetooth.unsubscribe(this);
				}
			},
			unsubscribeSuccess : function(){
				this.success();
			},
			unsubscribeError : function(){
				this.error(arguments);
			},
			
			
			/**
			 * Sends notify data to the subscriber.
			 * @memberof Characteristic
			 * @example device.services[3].characteristics[3].notify('raw',value,successCallback,errorCallback);
			 * @param {string} type - The type of the value to write ('hex'/'ascii'/'unicode'/'raw')
			 * @param {string/Uint8Array} value - The value write to this characteristic, if the 'type' is 'raw', the value type should be Uint8Array
			 * @param {function} [successCallback] - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			notify : function(type,value,success,error){
				if(type.toLowerCase() == "hex"){
					value = BC.Tools.HexToBase64(value);
				}else if(type.toLowerCase() == "ascii"){
					value = BC.Tools.ASCIIToBase64(value);
				}else if(type.toLowerCase() == "unicode"){
					value = BC.Tools.UnicodeToBase64(value);
				}else if(type.toLowerCase() == "raw"){
					value = BC.Tools.ConvertToBase64(value);
				}else{
					error("Please input 'hex'/'ascii'/'unicode' type.");
					return;
				}
				if(this.property.contains("notify")){
					BC.bluetooth.notify(this,value);
				}else{
					error("This characteristic notify data, please add 'notify' in the property.");
				}
			},
			notifySuccess : function(){
				this.success();
			},
			notifyError : function(){
				this.error(arguments);
			},
			
			/**
			 * Discovers descriptors for the characteristic.
			 * @memberof Characteristic
			 * @example device.services[3].characteristics[3].discoverDescriptors();
			 * @param {function} [successCallback] - Called when discovering descriptors successfully
			 * @param {function} [errorCallback] - Called when discovering descriptors unsuccessfully
			 * @instance
			 */
			discoverDescriptors : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.discoverDescriptors(this);
			},
			
			discoverDescriptorsSuccess : function(data){
				var dess = arguments[1];
				var chara =  this;
				var device = this.device;
				chara.descriptors = [];
				_.each(dess.descriptors,function(des){
					var dindex = des.descriptorIndex;
					var dname = des.descriptorName;
					var duuid = des.descriptorUUID;
					chara.descriptors.push(new BC.Descriptor({index:dindex,uuid:duuid,name:dname,upper:chara,device:device}));
				});
				this.success();
			},
			
			discoverDescriptorsError : function(){
				this.error();
			},
			
			/**
			 * Gets descriptors by UUID.
			 * @memberof Characteristic
			 * @example app.device.services[3].characteristics[0].getDescriptorByUUID("2901")[0].read(function(data){alert(data.value.getASCIIString())});
			 * @param {string} uuid - The uuid(128bit/16bit) of descriptor
			 * @instance
			 * @returns {Array<Descriptor>} An array of Descriptor
			 */
			getDescriptorByUUID : function(uuid){
				var uuid = uuid.toLowerCase();
				var result = [];
				var uuid_128 = BC.Tools.ChangeTo128UUID(uuid);
				_.each(this.descriptors, function(descriptor){
						if(descriptor.uuid == uuid_128){
							result.push(descriptor);
						}
					}
				);
				return result;
			},
			
		});
		
		/**
		 * Triggered when service descriptor has been read.
		 * @example var descriptor1 = BC.Bluetooth.CreateDescriptor("00002901-0000-1000-8000-00805f9b34fb","00","Hex",permission);
		 * descriptor1.addEventListener("ondescriptorread",function(s){
		 *  alert("OBJECT EVENT!! ondescriptorread : " + s.uuid);
		 * });
		 * @event Descriptor#ondescriptorread
		 * @type {object}
		 */
		 
		/**
		 * Triggered when service descriptor has been written.
		 * @example var descriptor1 = BC.Bluetooth.CreateDescriptor("00002901-0000-1000-8000-00805f9b34fb","00","Hex",permission);
		 * descriptor1.addEventListener("ondescriptorwrite",function(s){
		 *  alert("OBJECT EVENT!! ondescriptorwrite : " + s.uuid);
		 * });
		 * @event Descriptor#ondescriptorwrite
		 * @type {object}
		 */
		 
		/**
		 * BLE Descriptor class.
		 * @class
		 * @property {Device} device - The device to which this descriptor belongs
		 * @property {string} uuid - The uuid of this descriptor
		 * @property {string} name - The name of this descriptor
		 */
		var Descriptor = BC.Descriptor = GATTEntity.extend({
			value : null,
			
			entityInitialize : function(arg){
				this.value = arg.value;
				this.type = arg.type;
				this.permission = arg.permission;
			},
			
			/**
			 * Reads the descriptor value.
			 * @memberof Descriptor
			 * @example 
			 * //Reads after device is well prepared or after 'Characteristic.discoverDescriptors' interface is called successfully
			 * device.prepare(function(){
			 *	device.services[3].characteristics[0].descriptor[0].read(readSuccess);
			 * });
			 * function readSuccess(data){
			 *	alert("Data : "+JSON.stringify(data.value)+" \n Time :"+data.date);
			 * }
			 * @param {function} successCallback - Success callback
			 * @param {function} [errorCallback] - Error callback
			 * @instance
			 */
			read : function(success,error){
				this.success = success;
				this.error = error;
				BC.bluetooth.readDescriptor(this);
			},
			readSuccess : function(){
				var data = {};
				data.deviceAddress=this.device.deviceAddress;
				data.serviceIndex = this.upper.upper.index;
				data.characteristicIndex = this.upper.index;
				data.descriptorIndex = this.index;
				data.date = arguments[1].date;
				data.value = new BC.DataValue(BC.Tools.Base64ToBuffer(arguments[1].value));
				this.success(data);
			},
			readError : function(mes){
				this.error(mes);
			},
		});
	  
		document.addEventListener('deviceready', onDeviceReady, false);
		
		function onDeviceReady(){
			var bluetooth = BC.bluetooth = new BC.Bluetooth("cordova");
			BC.bluetooth.addSystemListener('disconnect', function(arg){
				BC.bluetooth.devices[arg.deviceAddress].isConnected = false;
				BC.bluetooth.devices[arg.deviceAddress].dispatchEvent("devicedisconnected");
			});
			BC.bluetooth.addSystemListener('onsubscribe', function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				character.isSubscribed = true;
				character.dispatchEvent("onsubscribestatechange");
			});
			BC.bluetooth.addSystemListener('onunsubscribe', function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				character.isSubscribed = false;
				character.dispatchEvent("onsubscribestatechange");
			});
			BC.bluetooth.addSystemListener("oncharacteristicread", function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				character.dispatchEvent("oncharacteristicread");
			});
			BC.bluetooth.addSystemListener('oncharacteristicwrite', function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				var dataValue = new BC.DataValue(BC.Tools.Base64ToBuffer(arg.writeRequestValue));
				character.writeValue = dataValue;
				character.dispatchEvent("oncharacteristicwrite");
			});
			BC.bluetooth.addSystemListener("ondescriptorread", function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				var descriptor = character.descriptors[arg.descriptorIndex];
				descriptor.dispatchEvent("ondescriptorread");
			});
			BC.bluetooth.addSystemListener('ondescriptorwrite', function(arg){
				var service = BC.bluetooth.services[arg.uniqueID];
				var character = service.characteristics[arg.characteristicIndex];
				var descriptor = character.descriptors[arg.descriptorIndex];
				var dataValue = new BC.DataValue(BC.Tools.Base64ToBuffer(arg.writeRequestValue));
				descriptor.writeValue = dataValue;
				descriptor.dispatchEvent("ondescriptorwrite");
			});
			BC.bluetooth.addSystemListener("newadvpacket",function(scanData){
				var advertisementData,deviceAddress,deviceName,isCon,RSSI,txPower,type;
				if(scanData['advertisementData']){
					advertisementData = scanData['advertisementData'];
					if(advertisementData.manufacturerData){
						advertisementData.manufacturerData = new BC.DataValue(BC.Tools.Base64ToBuffer(advertisementData.manufacturerData));
					}
				}
				if(scanData['deviceAddress']){
					deviceAddress = scanData['deviceAddress'];
				}
				if(scanData['deviceName']){
					deviceName = scanData['deviceName'];
				}
				if(scanData['isConnected']){
					isCon = scanData['isConnected'];
				}
				if(scanData['RSSI']){
					RSSI = parseInt(scanData['RSSI']);
				}
				if(scanData['type']){
					type = scanData['type'];
				}
				
				var isConnected = false;
				if(isCon === "true"){
					isConnected = true;
				}
				if(isNewDevice(deviceAddress)){
					var newdevice = new BC.Device({deviceAddress:deviceAddress,deviceName:deviceName,advertisementData:advertisementData,isConnected:isConnected,RSSI:RSSI,type:type});
					BC.bluetooth.dispatchEvent("newdevice",newdevice);
				}else{
					var thedevice = BC.bluetooth.devices[deviceAddress];
					if(type == "BLE"){
						thedevice.RSSI = RSSI;
						//IOS will regard the different advertisement data which broadcast from same bluetooth address as different device
						//so be careful if you want to develop application based on changing advertisement data
						thedevice.advertisementData = advertisementData;	
					}
					thedevice.advTimestamp = new Date().getTime();
				}
				BC.bluetooth.dispatchEvent("newadvpacket",scanData);
			});

			document.addEventListener("bluetoothclose",function(){
				BC.bluetooth.isopen = false;
				BC.bluetooth.dispatchEvent("bluetoothstatechange");
			},false);
			document.addEventListener("bluetoothopen",function(){
				BC.bluetooth.isopen = true;
				BC.bluetooth.dispatchEvent("bluetoothstatechange");
			},false);
			
			BC.bluetooth.getEnvironment(function(data){
				if(DEBUG){
					alert(JSON.stringify(data));
				}
				window.APPURL = window.location.href;
				window.DEVICEADDRESS = data.deviceAddress;
				window.API = data.api;
				window.VERSION = data.version;
				window.DEVICETYPE = data.deviceType;
				
				BC.bluetooth.getBluetoothState(function(arg){
					if(arg.state == "false"){
						BC.bluetooth.isopen = false;
					}else{
						BC.bluetooth.isopen = true;
					}
					BC.Tools.FireDocumentEvent("bccoreready");
				},testFunc);
			},function(mes){alert(JSON.stringify(mes));});
		}
		
		module.exports = BC;


