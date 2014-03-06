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
(function(){

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
	
	BC.VERSION = "0.1.5";
	/** 
	 * Opens all useful alert.
	 * @global 
	 * @property {boolean} DEBUG - switch debug mode
	 */
	DEBUG = false;
	
	/**
	 * Triggered when bluetooth has been opened.
	 * @example document.addEventListener('bluetoothstatechange', onBluetoothStateChange, false);
	 * function onBluetoothStateChange(){
	 * 	if(BC.bluetooth.isopen){
	 *		alert("bluetooth is opend!");
	 * 	}else{
	 *		alert("bluetooth is closed!");
	 *	}
	 * }
	 * @event bluetoothstatechange
	 * @type {object}
	 */
	 
	/**
	 * Triggered when any device has been disconnected.
	 * @example document.addEventListener('devicedisconnect', onDeviceDisconnect, false);
	 * function onDeviceDisconnect(arg){
	 *  var deviceID = arg.param
	 * 	alert("device:"+ deviceID +" is disconnect!");
	 * }
	 * @event devicedisconnect
	 * @property {string} deviceID - The ID of this device which is disconnected
	 * @type {object}
	 */
	
	/**
	 * BC ready event,this is the "main" function of BLE application.
	 * @example document.addEventListener('bcready', onBCReady, false);
	 * function onBluetoothReady(){
	 * 	alert("BC is ready now! you can process UI event here");
	 * }
	 * @event bcready
	 * @type {object}
	 */
	 
	/**
	 * Triggered when new advertising device has been found.
	 * @example document.addEventListener('newdevice', addNewDevice, false);
	 * function addNewDevice(){
	 *  var deviceID = arg.param;
	 * 	alert("new device added! it's device ID is: "+deviceID);
	 *  var newDevice = BC.bluetooth.devices[deviceID];
	 * }
	 * @event newdevice
	 * @type {object}
	 */
	
	/**
	 * Triggered when new advertising device has been connected.
	 * @example document.addEventListener('deviceconnected', onConnected, false);
	 * function onConnected(){
	 *  var deviceID = arg.param;
	 * 	alert("new device connected! it's device ID is: "+deviceID);
	 *  var newDevice = BC.bluetooth.devices[deviceID];
	 * }
	 * @event deviceconnected
	 * @type {object}
	 */
	
	var _ = root._;
	if (!_ && (typeof require !== 'undefined')) _ = require('underscore');
	
	var testFunc = function(message){
		if(DEBUG){
			alert(JSON.stringify(message));
		}	
	}
	
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
	
	function fireBLEEvent(eventName,deviceID,serviceIndex,characteristicIndex,descriptorIndex,arg){
		var event = document.createEvent('Events');
		event.deviceID = deviceID;
		event.serviceIndex = serviceIndex;
		event.characteristicIndex = characteristicIndex;
		event.descriptorIndex = descriptorIndex;
		event.arg = arg;
		event.initEvent(eventName, false, false);
		document.dispatchEvent(event);
	}
	
	function onDeviceReady(){
		var bluetooth = new BC.Bluetooth("cordova");
		BC.bluetooth.addListener('disconnect', function(arg){
			BC.bluetooth.devices[arg.deviceID].isConnected = false;
			fireBLEEvent("devicedisconnected",arg.deviceID);
		});
		document.addEventListener("bluetoothclose",function(){
			BC.bluetooth.isopen = false;
			fireBLEEvent("bluetoothstatechange");
		},false);
		document.addEventListener("bluetoothopen",function(){
			BC.bluetooth.isopen = true;
			fireBLEEvent("bluetoothstatechange");
		},false);
		bluetooth.getEnvironment(function(data){
			if(DEBUG){
				alert(JSON.stringify(data));
			}
			window.APPID = data.appID;
			window.DEVICEID = data.deviceID;
			window.API = data.api;
			bluetooth.getBluetoothState(function(arg){
				if(arg.state == "false"){
					bluetooth.isopen = false;
				}else{
					bluetooth.isopen = true;
				}
				fireBLEEvent("bcready");
			},testFunc);
		},function(mes){alert(JSON.stringify(mes));});
	}
	
	function base64ToBuffer(rawData){
        var bytes = window.atob(rawData);
        var arraybuffer = new Uint8Array(bytes.length);
        for (var i = 0; i < bytes.length; i++) {
            arraybuffer[i] = bytes.charCodeAt(i);
        }
        return arraybuffer.buffer;
	}
  
  
	function isEmpty(s){
		return ((s == undefined || s == null || s == "") ? true : false); 
	}
	
	document.addEventListener('deviceready', onDeviceReady, false);
	
	//Portable Functions
	var BluetoothFuncs = BC.BluetoothFuncs = function(type){
		if(type == "cordova" && typeof cordova !== "undefined" ){
			this.initBluetooth = function(){
				navigator.bluetooth.initialBluetooth();
			};
		
			this.getEnvironment = function(success,error){
				navigator.bluetooth.getEnvironment(success,error);
			};
		
			this.startScan = function(processDevices,UUIDs){
				var uuids;
				if(typeof UUIDs !== 'undefined'){
					uuids = [{"serviceUUIDs":UUIDs}];;
				}else{
					uuids = [{"serviceUUIDs":[]}];
				}
				navigator.bluetooth.startScan(function(){
					navigator.bluetooth.getScanData(processDevices,testFunc);
				},testFunc,uuids);
			};
		
			this.stopScan = function(){
				navigator.bluetooth.stopScan(testFunc,testFunc);
			};
			this.getDeviceAllData = function(device){
				//bind "this" pointer in case of rewrite by js context.
				var processDeviceAllData = device.processDeviceAllData.bind(device,device.processDeviceAllData);
				navigator.bluetooth.getDeviceAllData(processDeviceAllData,testFunc,device.deviceID);
			};
		
			this.connect = function(device){
				var connectSuccess = device.connectSuccess.bind(device,device.connectSuccess);
				var connectError = device.connectError.bind(device,device.connectError);
				navigator.bluetooth.connectDevice(connectSuccess,connectError,device.deviceID,APPID);
			};
			this.disconnect = function(device){
				var disconnectSuccess = device.disconnectSuccess.bind(device,device.disconnectSuccess);
				var disconnectError = device.disconnectSuccess.bind(device,device.disconnectError);
				navigator.bluetooth.disconnectDevice(disconnectSuccess,disconnectError,device.deviceID,APPID);
			};
			
			this.writeCharacteristic = function(character,type,value){
				var writeSuccess = character.writeSuccess.bind(character,character.writeSuccess);
				var writeError = character.writeError.bind(character,character.writeError);
				navigator.bluetooth.writeCharacteristic(writeSuccess,writeError,character.device.deviceID,character.upper.index,character.index,value,type);
			};
			this.readCharacteristic = function(character){
				var readSuccess = character.readSuccess.bind(character,character.readSuccess);
				var readError = character.readError.bind(character,character.readError);
				navigator.bluetooth.readCharacteristic(readSuccess,readError,character.device.deviceID,character.upper.index,character.index);
			};
			this.subscribe = function(character){
				var subscribeCallback = character.subscribeCallback.bind(character,character.subscribeCallback);
				navigator.bluetooth.subscribe(subscribeCallback,testFunc,character.device.deviceID,character.upper.index,character.index);
			};
			this.unsubscribe = function(character){
				var unsubscribeSuccess = character.unsubscribeSuccess.bind(character,character.unsubscribeSuccess);
				var unsubscribeError = character.unsubscribeError.bind(character,character.unsubscribeError);		
				navigator.bluetooth.unsubscribe(unsubscribeSuccess,unsubscribeError,character.device.deviceID,character.upper.index,character.index,"");
			};
			this.getRSSI = function(device){
				var getRSSISuccess = device.getRSSISuccess.bind(device,device.getRSSISuccess);
				var getRSSIError = device.getRSSIError.bind(device,device.getRSSIError);
				navigator.bluetooth.getRSSI(getRSSISuccess,getRSSIError,device.deviceID);
			};
			this.addServices = function(service,serviceObj,success,error){
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
				navigator.bluetooth.createPair(success,error,device.deviceID);
			};
			this.removePair = function(device){
				var success = device.removePairSuccess.bind(device,device.removePairSuccess);
				var error = device.removePairError.bind(device,device.removePairError);
				navigator.bluetooth.removePair(success,error,device.deviceID);
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
				navigator.bluetooth.discoverServices(success,error,device.deviceID);
			};
			this.discoverCharacteristics = function(service){
				var success = service.discoverCharacteristicsSuccess.bind(service,service.discoverCharacteristicsSuccess);
				var error = service.discoverCharacteristicsError.bind(service,service.discoverCharacteristicsError);
				navigator.bluetooth.discoverCharacteristics(success,error,service.device.deviceID,service.index,[]);
			};
			this.discoverDescriptors = function(character){
				var success = character.discoverDescriptorsSuccess.bind(character,character.discoverDescriptorsSuccess);
				var error = character.discoverDescriptorsError.bind(character,character.discoverDescriptorsError);
				navigator.bluetooth.discoverDescriptors(success,error,character.device.deviceID,character.upper.index,character.index);
			};
			this.readDescriptor = function(descriptor){
				var readSuccess = descriptor.readSuccess.bind(descriptor,descriptor.readSuccess);
				var readError = descriptor.readError.bind(descriptor,descriptor.readError);
				navigator.bluetooth.readDescriptor(readSuccess,readError,descriptor.device.deviceID,descriptor.upper.upper.index,descriptor.upper.index,descriptor.index);
			};
			this.getBluetoothState = function(success,error){
				navigator.bluetooth.getBluetoothState(success,error);
			};
			this.openBluetooth = function(success,error){
				navigator.bluetooth.openBluetooth(success,error);
			};
			this.addEventListener = function(success,error,arg){
				navigator.bluetooth.addEventListener(success,error,arg);
			};
			
		}else{
			alert(type+" is not support now.");
		}
	};
	_.extend(BluetoothFuncs.prototype,{
	});
	
	/**
	 * Bluetooth class includes all useful bluetooth global interfaces. 
	 * <p><b>Please note</b> that the application should not create Bluetooth object, BC manages the object model.
	 * @class
	 * @property {Array<Device>} devices - The advertising devices, this is filled after 'BC.Blueooth.StartScan' called
	 * @property {boolean} isopen - Bluetooth is open or not
	 */
	var Bluetooth = BC.Bluetooth = function(type){
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
		this.addEventListener = this.bluetoothFuncs.addEventListener;
		
		//character operation
		this.writeCharacteristic = this.bluetoothFuncs.writeCharacteristic;
		this.readCharacteristic = this.bluetoothFuncs.readCharacteristic;
		this.subscribe = this.bluetoothFuncs.subscribe;
		this.unsubscribe = this.bluetoothFuncs.unsubscribe;
		this.getRSSI = this.bluetoothFuncs.getRSSI;
		this.addServices =  this.bluetoothFuncs.addServices;
		this.removeService = this.bluetoothFuncs.removeService;
		
		this.bluetoothFuncs.initBluetooth();
		
		/**
		 * @property {object}  defaults               - The default values for parties.
		 */
		var bluetooth = BC.bluetooth = this;
		
		this.devices = {};
		this.isopen = false;
	};
	_.extend(Bluetooth.prototype,{
		addListener : function(eventName,callback,arg){
			var args = {};
			args.eventName = eventName;
			args.arg = arg;
			this.addEventListener(callback,testFunc,args);
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
	 */
	var OpenBluetooth = BC.Bluetooth.OpenBluetooth = function(success,error){
		BC.bluetooth.openBluetooth(success,error);
	};
	/** 
	 * @memberof Bluetooth
	 * @method 
	 * @example //Generates a service instance.
	 * var service = BC.Bluetooth.CreateService("0000ffe0-0000-1000-8000-00805f9b34fb");
	 *
	 * //Adds a service to smart phone.
	 * BC.Bluetooth.AddService(service,app.addServiceSusscess,app.addServiceError);
	 * @param {string} uuid - Service UUID (should be 128bit such as : '0000ffe0-0000-1000-8000-00805f9b34fb')
	 * @param {Number} [type=0] - Is major service(0) or not(!0)
	 * @returns {Service} An instance of Service
	 */
	var CreateService = BC.Bluetooth.CreateService = function(uuid,type){
		var isMajor;
		if(type == null){
			isMajor = 0;
		}else{
			isMajor = type;
		}
		var timestr = new Date().getTime();
		var randomnum = Math.floor(Math.random()*10000);
		return new Service(null,uuid,null,null,null,null,isMajor,timestr.toString() + randomnum.toString());
	};
	/** 
	 * @memberof Bluetooth
	 * @method 
	 * @example //Generates a descriptor instance.
	 * var permission = ["write"];
	 * var property = ["write","read"];
	 * var descriptor1 = BC.Bluetooth.CreateDescriptor("00002901-0000-1000-8000-00805f9b34fb","00","Hex",permission);
	 *
	 * //Generates a characteristic instance.
	 * var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",property,permission);
	 *
	 * //Adds descriptor to characteristic
	 * character1.addDescriptor(descriptor1);
	 *
	 * //Generates a service instance.
	 * var service = BC.Bluetooth.CreateService("0000ffe0-0000-1000-8000-00805f9b34fb");
	 *
	 * //Adds a characteristic to service.
	 * service.addCharacteristic(character1);
	 * 
	 * //Adds a service to smart phone.
	 * BC.Bluetooth.AddService(service,app.addServiceSusscess,app.addServiceError);
	 * @param {string} uuid - Descriptor UUID (should be 128bit such as : '00002901-0000-1000-8000-00805f9b34fb')
	 * @param {string} value - The default value of this descriptor
	 * @param {string} type - The type of the value,include 'Hex'/'ASCII'/'unicode'
	 * @param {object} permission - The permission of this descriptor
	 * @returns {Descriptor} An instance of Descriptor
	 */
	var CreateDescriptor = BC.Bluetooth.CreateDescriptor = function(uuid,value,type,permission){
		return new BC.Descriptor(null,uuid,null,null,null,value,type,permission);
	};
	/** 
	 * @memberof Bluetooth
	 * @method 
	 * @example //Generates a characteristic instance.
	 * var permission = ["write","writeEncrypted"];			 
	 * var property = ["read","write"];
	 * var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",property,permission);
	 *
	 * //Generates a service instance.
	 * var service = BC.Bluetooth.CreateService("0000ffe0-0000-1000-8000-00805f9b34fb");
	 *
	 * //Adds a characteristic to service.
	 * service.addCharacteristic(character1);
	 *
	 * //Adds a service to smart phone
	 * BC.Bluetooth.AddService(service,app.addServiceSusscess,app.addServiceError);
	 * @param {string} uuid - Characteristic UUID (should be 128bit such as : '0000ffe0-0000-1000-8000-00805f9b34fb')
	 * @param {string} value - The default value of this characteristic
	 * @param {string} type - The type of the value,include 'Hex'/'ASCII'/'unicode'
	 * @param {object} property - The property of this characteristic
	 * @param {object} permission - The permission of this characteristic
	 * @param {string} [writecallback] - The name of write callback function
	 * @param {string} [readcallback] - The name of read callback function
	 * @returns {Characteristic} An instance of Characteristic
	 */
	var CreateCharacteristic = BC.Bluetooth.CreateCharacteristic = function(uuid,value,type,property,permission){
		return new BC.Characteristic(null,uuid,null,null,null,null,property,permission,type,value);
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
	 * var character1 = BC.Bluetooth.CreateCharacteristic("0000ffe1-0000-1000-8000-00805f9b34fb","01","Hex",property,permission);
	 *
	 * //Generates a service instance.
	 * var service = BC.Bluetooth.CreateService("0000ffe0-0000-1000-8000-00805f9b34fb");
	 *
	 * //Adds a characteristic to a service. 
	 * service.addCharacteristic(character1);
	 *
	 * //Adds a service to the smart phone.
	 * BC.Bluetooth.AddService(service,app.addServiceSusscess,app.addServiceError);
	 * @param {Service} service - The service to add
	 * @param {function} [success] - Success callback
	 * @param {function} [error] - Error callback
	 */
	var AddService = BC.Bluetooth.AddService = function(service,success,error){
		var serviceObj = serializeService(service);
		BC.bluetooth.addServices(service,serviceObj,success,error);
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
		BC.bluetooth.removeService(service,success,error);
	};
	/** 
	 * Starts a scan for Bluetooth LE devices, looking for devices with given services.
	 * @memberof Bluetooth
	 * @method 
	 * @example BC.Bluetooth.StartScan();
	 * @param {array} [uuids] - Array of services to look for. If null or [], it will scan all devices
	 */
	var StartScan = BC.Bluetooth.StartScan = function(uuids){
		BC.bluetooth.startScan(onGetDevicesSuccess,uuids);
	};
	function onGetDevicesSuccess(data){
		for(var i=0; i<data.length; i++){
			var advertisementData,deviceID,deviceName,isCon;
			if(data[i]['advertisementData']){
				advertisementData = data[i]['advertisementData'];
				if(advertisementData.manufacturerData){
					advertisementData.manufacturerData = new BC.DataValue(base64ToBuffer(advertisementData.manufacturerData));
				}
			}
			if(data[i]['deviceID']){
				deviceID = data[i]['deviceID'];
			}
			if(data[i]['deviceName']){
				deviceName = data[i]['deviceName'];
			}
			if(data[i]['isConnected']){
				isCon = data[i]['isConnected'];
			}

			var isConnected = false;
			if(isCon === "true"){
				isConnected = true;
				fireBLEEvent("deviceconnected",this.deviceID);
			}
			if(isNewDevice(deviceID)){
				BC.bluetooth.devices[deviceID] = new BC.Device(deviceName,deviceID,advertisementData,isConnected);
				fireBLEEvent("newdevice",deviceID);
			}
		}
	};
	function isNewDevice(deviceID){
		var res = true;
		_.each(BC.bluetooth.devices,function(device){
			if(device.deviceID == deviceID){
				res = false;
			}
		});
		return res;
	};
	/** 
	 * Stops a scanning for BLE Peripherals.
	 * @memberof Bluetooth
	 * @method 
	 * @example BC.Bluetooth.StopScan();
	 */
	var StopScan = BC.Bluetooth.StopScan = function(){
		BC.bluetooth.stopScan();
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
	 * @param {Uint8Array} value - The raw value
	 * @property {Uint8Array} value - The raw value of DataValue object
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
			if(length >= 2){
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
			for (var i=0; i<length;i++) {
				result+= dv.getUint8(i).toString(16);
			}
			return result;
		},
	   
	});
	
	/**
	 * Device represents the remote BLE Peripheral device. 
	 * <p><b>Please note</b> that the application should not create Device object, BC manages the object model.
	 * @class
	 * @param {string} deviceName - The name of the device
	 * @param {string} deviceID - The ID of the device(ID is assigned by the smart phone,if there is no ID, it is recommended to new the device instance after obtaining devices' information from BC.Bluetooth.startScan)
	 * @param {object} advertisementData - The device advertisement data, includes LocalName, TxPowerLevel, IsConnectable, ServiceData, ManufacturerData, ServiceUUIDs, SolicitedServiceUUIDs, OverflowServiceUUIDs
	 * @property {string} deviceName - The name of this device
	 * @property {string} deviceID - The ID of this device
	 * @property {Array<Service>} services - The services of this device
	 * @property {boolean} isConnected - If this device is connected
	 * @property {boolean} isPrepared - If this device is prepared ('prepared' means this device object can be used to access the services' characteristics)
	 * @property {function} connectSuccessCallback - This success callback function will be called after this device is well prepared
	 * @property {DataValue} systemID - The system ID of this device
	 * @property {DataValue} modelNum - The model number of this device
	 * @property {DataValue} serialNum - The serial number of this device
	 * @property {DataValue} firmwareRevision - The firmware revision of this device
	 * @property {DataValue} hardwareRevision - The hardware revision of this device
	 * @property {DataValue} softwareRevision - The software revision of this device
	 * @property {DataValue} manufacturerName - The manufacturer name of this device
	 */
	var Device = BC.Device = function(deviceName,deviceID,advertisementData,isConnected){
		this.deviceName = deviceName;
		this.deviceID = deviceID;
		this.advertisementData = advertisementData;
		this.isConnected = isConnected;
		this.services = [];
		this.isPrepared = false;
		this.systemID = null;
		this.modelNum = null;
		this.serialNum = null;
		this.firmwareRevision = null;
		this.hardwareRevision = null;
		this.softwareRevision = null;
		this.manufacturerName = null;
	};
	_.extend(Device.prototype,{
		
		/**
		 * Initiates a connection to the peripheral.
		 * @memberof Device
		 * @example //Gets a the Device instance.
		 * var device = window.device = new BC.bluetooth.devices["78:C5:E5:99:26:37"];
		 * device.connect(function(){alert("device is already prepared well!");});
		 * @param {function} successCallback - Success callback
		 * @param {function} [errorCallback] - Error callback
		 * @instance
		 */
		connect : function(success,error){
			this.success = success;
			this.error = error;
			BC.bluetooth.connect(this);
		},
		connectSuccess : function(){
			this.isConnected = true;
			fireBLEEvent("deviceconnected",this.deviceID);
			this.success();
		},
		
		connectError : function(){
			this.error();
		},
		
		/**
		 * Discovers services in peripheral.</br>After calling this interface, all the characteristics and descriptors is accessible. 
		 * @memberof Device
		 * @example //Gets a the Device instance.
		 * var device = window.device = new BC.bluetooth.devices["78:C5:E5:99:26:37"];
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
			BC.bluetooth.getDeviceAllData(this);
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
			BC.bluetooth.discoverServices(this);
			
		},
		
		discoverServicesSuccess : function(){
			var rawObj = arguments[1];
            var device = this;
            this.services = [];
            _.each(rawObj.services, function(service){
                    var sindex = service.serviceIndex;
                    var sname = service.serviceName;
                    var suuid = service.serviceUUID;
                    device.services.push(new BC.Service(sindex,suuid,sname,device,null));
                }
            );

            if(this.success !== null){
                this.success();
            }
		},
		
		discoverServicesError : function(){
			this.error();
		},
		
		processDeviceAllData : function(){
			var rawObj = arguments[1];
			var device = this;
			this.services = [];
			_.each(rawObj.services, function(service){
					var sindex = service.serviceIndex;
					var sname = service.serviceName;
					var suuid = service.serviceUUID;
					var chars = service.characteristics;
					device.services.push(new BC.Service(sindex,suuid,sname,device,null,chars));
				}
			);
			this.isPrepared = true;
			if(this.success !== null){
				this.success();
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
			BC.bluetooth.disconnect(this);
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
		 * Gets the device ID service(0000180a-0000-1000-8000-00805f9b34fb)	value.
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
			var deviceIDindex = -1;
			_.each( this.services, function( service ) {
				if(service.uuid === "0000180a-0000-1000-8000-00805f9b34fb"){
					deviceIDindex = service.index;
				}
            } );
            var self = this;
			var deviceInfoService = this.services[deviceIDindex];
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
			var uuid_128 = "";
			if(uuid.length == 4){
				uuid_128 = "0000"+ uuid +"-0000-1000-8000-00805f9b34fb";
			}else if(uuid.length == 36){
				uuid_128 = uuid;
			}
			_.each(this.services, function(service){
					if(service.uuid == uuid_128){
						result.push(service);
					}
				}
			);
			return result;
		},
	});
	
	//Entity 
	var Entity = BC.Entity = function(index,uuid,name,device,upper){
		this.index = index;
		this.uuid = uuid;
		this.name = name;
		this.upper = upper;
		this.device = device;
		this.initialize.apply(this, arguments);
	};
	_.extend(Entity.prototype,{
		initialize: function(){},
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

  // Set up inheritance for object which need to inherit 
  Entity.extend = extend;
  
  /**
   * BLE Service class.
   * @class
   * @property {Array<Characteristic>} characteristics - The characteristics of this service
   * @property {Device} device - The device to which this service belongs
   * @property {string} uuid - The uuid of this service
   * @property {string} name - The name of this service
   */
  var Service = BC.Service = Entity.extend({
		characteristics : null,
		
		initialize : function(){
			var chars = arguments[5];
			this.isMajor = arguments[6];
			this.uniqueID = arguments[7];
			var service = this;
			var device = this.device;
			this.characteristics = [];
			BC.Service.create = this.create;
			_.each(chars, function(characteristic){
					var cindex = characteristic.characteristicIndex;
					var cname = characteristic.characteristicName;
					var cuuid = characteristic.characteristicUUID;
					var dess = characteristic.descriptors;
					var property = characteristic.characteristicProperty;
					service.characteristics.push(new BC.Characteristic(cindex,cuuid,cname,device,service,dess,property));
				}
			);
			
			this.addCharacteristic = function(chara){
				chara.upper = this;
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
			BC.bluetooth.discoverCharacteristics(this);
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
                    service.characteristics.push(new BC.Characteristic(cindex,cuuid,cname,device,service,null,property));
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
			var uuid_128 = "";
			if(uuid.length == 4){
				uuid_128 = "0000"+ uuid +"-0000-1000-8000-00805f9b34fb";
			}else if(uuid.length == 36){
				uuid_128 = uuid;
			}
			_.each(this.characteristics, function(characteristic){
					if(characteristic.uuid == uuid_128){
						result.push(characteristic);
					}
				}
			);
			return result;
		},
  });
  
  /**
   * BLE Characteristic class.
   * @class
   * @property {Array<Descriptor>} descriptors - The descriptors of this characteristic
   * @property {Device} device - The device to which this characteristic belongs
   * @property {string} uuid - The uuid of this characteristic
   * @property {string} name - The name of this characteristic
   */
  var Characteristic = BC.Characteristic = Entity.extend({
		descriptors : null,
		value : null,
		property : null,
		type : null,
		
		initialize : function(){
            var dess = arguments[5];
            this.property = arguments[6];
            this.permission = arguments[7];
            this.type = arguments[8];
            this.value = arguments[9];
			
            this.descriptors = [];
            var chara = this;
            var device = this.device;
            _.each(dess,function(des){
                var dindex = des.descriptorIndex;
                var dname = des.descriptorName;
                var duuid = des.descriptorUUID;
                chara.descriptors.push(new BC.Descriptor(dindex,duuid,dname,device,chara));
            });
            
            this.addDescriptor = function(des){
                des.upper = this;
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
            data.deviceID=this.device.deviceID;
            data.serviceIndex = this.upper.index;
            data.characteristicIndex = this.index;
            data.date = arguments[1].date;
            data.value = new BC.DataValue(base64ToBuffer(arguments[1].value));
			this.success(data);
		},
		readError : function(){
			this.error("read data error");
		},
		
		/**
		 * Writes the characteristic value.
		 * @memberof Characteristic
		 * @example //New a device.			
		 * var device = window.device = new BC.Device("SimplePeripheral","78:C5:E5:99:26:37");
		 * //write after device is well prepared.
		 * device.connect(function(){
		 *	device.services[3].characteristics[0].write("Hex","01",writeSuccess);
		 * });
		 * function writeSuccess(data){
		 *	alert("write success!");
		 * }
		 * @param {string} type - The type of the value to write ('Hex'/'ASCII'/'unicode')
		 * @param {string} value - The value write to this characteristic
		 * @param {function} successCallback - Success callback
		 * @param {function} [errorCallback] - Error callback
		 * @instance
		 */
		write : function(type,value,success,error){
			this.success = success;
			this.error = error;
			if(this.property.contains("write") || this.property.contains("writeWithoutResponse")){
				BC.bluetooth.writeCharacteristic(this,type,value);
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
			if(this.property.contains("notify")){
				BC.bluetooth.subscribe(this);
			}
		},
		subscribeCallback : function(){
			var obj = arguments[1];
			var data = {};
			data.value = new BC.DataValue(base64ToBuffer(obj.value));
			data.serviceIndex = obj.serviceIndex;
			data.characteristicIndex = obj.characteristicIndex;
			data.date = obj.date;
			data.deviceID = obj.deviceID;
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
                chara.descriptors.push(new BC.Descriptor(dindex,duuid,dname,device,chara));
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
			var uuid_128 = "";
			if(uuid.length == 4){
				uuid_128 = "0000"+ uuid +"-0000-1000-8000-00805f9b34fb";
			}else if(uuid.length == 36){
				uuid_128 = uuid;
			}
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
   * BLE Descriptor class.
   * @class
   * @property {Device} device - The device to which this descriptor belongs
   * @property {string} uuid - The uuid of this descriptor
   * @property {string} name - The name of this descriptor
   */
  var Descriptor = BC.Descriptor = Entity.extend({
		value : null,
		
		initialize : function(){
			this.value = arguments[5];
			this.type = arguments[6];
			this.permission = arguments[7];
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
            data.deviceID=this.device.deviceID;
            data.serviceIndex = this.upper.upper.index;
            data.characteristicIndex = this.upper.index;
            data.descriptorIndex = this.index;
            data.date = arguments[1].date;
            data.value = new BC.DataValue(base64ToBuffer(arguments[1].value));
			this.success(data);
		},
		readError : function(mes){
			this.error(mes);
		},
  });
  
})();












