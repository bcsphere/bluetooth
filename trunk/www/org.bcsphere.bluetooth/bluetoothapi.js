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


	var exec = require('cordova/exec');
	var platform = require('cordova/platform');

	/**
	 * Provides access to bluetooth on the device.
	 */
	var bluetooth = {
		
		initialBluetooth: function(){

		},

		startScan: function(successFunc,errorFunc,serviceUUIDs) {
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "startScan", serviceUUIDs);
		},
		
		stopScan: function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "stopScan", []);
		},
		
		connectDevice: function(successFunc,errorFunc,deviceAddress,appID){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "connect", [{"deviceAddress":deviceAddress,"appID":appID}]);
		},
		
		disconnectDevice: function(successFunc,errorFunc,deviceAddress,appID){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "disconnect", [{"deviceAddress":deviceAddress,"appID":appID}]);
		},
		
		discoverServices: function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getServices", [{"deviceAddress":deviceAddress}]);
		},
		
		discoverCharacteristics: function(successFunc,errorFunc,deviceAddress,serviceIndex){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getCharacteristics", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex}]);
		},
		
		discoverDescriptors: function(successFunc,errorFunc,deviceAddress,serviceIndex,charcteristicIndex){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getDescriptors", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":charcteristicIndex}]);
		},
		
		readCharacteristic: function(successFunc,errorFunc,deviceAddress,serviceIndex,characteristicIndex){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "readValue", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":""}]);
		},
		
		writeCharacteristic: function(successFunc,errorFunc,deviceAddress,serviceIndex,characteristicIndex,writeValue){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "writeValue", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":"","writeValue":writeValue}]);
		},
		
		subscribe: function(successFunc,errorFunc,deviceAddress,serviceIndex,characteristicIndex,notifyEventName){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "setNotification", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"enable":"true"}]); 
		},
		
		unsubscribe: function(successFunc,errorFunc,deviceAddress,serviceIndex,characteristicIndex,notifyEventName){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "setNotification", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"enable":"false"}]); 
		},
		
		notify: function(successFunc,errorFunc,uniqueID,characteristicIndex,data){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "notify" , [{"uniqueID":uniqueID,"characteristicIndex":characteristicIndex,"data":data}]);
		},

		readDescriptor: function(successFunc,errorFunc,deviceAddress,serviceIndex,characteristicIndex,descriptorIndex){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "readValue", [{"deviceAddress":deviceAddress,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":descriptorIndex}]);
		},
		
		getDeviceAllData : function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getDeviceAllData",[{"deviceAddress":deviceAddress}]);
		},
		
		getRSSI : function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getRSSI",[{"deviceAddress":deviceAddress}]);
		},
		
		addServices : function(successFunc,errorFunc,servicesData){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "addServices",[servicesData]);
		},
		
		removeService : function(successFunc,errorFunc,uniqueID){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "removeServices",[{"uniqueID":uniqueID}]);
		},
		
		getConnectedDevices : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","getConnectedDevices",[]);
		},
		
		getPairedDevices : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","getPairedDevices",[]);
		},
		
		createPair : function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "createPair",[{"deviceAddress":deviceAddress}]);
		},
		
		removePair : function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "removePair",[{"deviceAddress":deviceAddress}]);
		},
		
		getEnvironment : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "getEnvironment",[]);
		},
		
		getBluetoothState : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","getBluetoothState",[]);
		},
		
		openBluetooth : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","openBluetooth",[]);
		},
		closeBluetooth : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","closeBluetooth",[]);
		},
		
		addEventListener : function(callback,errorFunc,arg){
			cordova.exec(callback,errorFunc,"BCBluetooth","addEventListener",[{"eventName":arg.eventName,"arg":arg.arg}]);
		},
			startClassicalScan : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","startClassicalScan",[]);
		},

		stopClassicalScan : function(successFunc,errorFunc){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","stopClassicalScan",[]);
		},

		rfcommRead: function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommRead", [{"deviceAddress":deviceAddress}]);
		},
		
		rfcommWrite: function(successFunc,errorFunc,deviceAddress,writeValue){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommWrite", [{"deviceAddress":deviceAddress,"writeValue":writeValue}]);
		},
		
		rfcommSubscribe: function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommSubscribe", [{"deviceAddress":deviceAddress}]); 
		}, 
		   
		rfcommUnsubscribe: function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommUnsubscribe", [{"deviceAddress":deviceAddress}]); 
		},
		
		rfcommConnect: function(successFunc,errorFunc,deviceAddress,APPURL,uuid,secure){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommConnect", [{"deviceAddress":deviceAddress,"uuid":uuid,"secure":secure,"appurl":APPURL}]); 
		},
		
		rfcommDisconnect: function(successFunc,errorFunc,deviceAddress){
			cordova.exec(successFunc,errorFunc, "BCBluetooth", "rfcommDisconnect", [{"deviceAddress":deviceAddress}]); 
		},
		
		rfcommListen : function(successFunc,errorFunc,name,uuid,secure){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","rfcommListen",[{"name":name,"uuid":uuid,"secure":secure}]);
		},
		
		rfcommUnListen : function(successFunc,errorFunc,name,uuid){
			cordova.exec(successFunc,errorFunc,"BCBluetooth","rfcommUnListen",[{"name":name,"uuid":uuid}]);
		},
		
	};
		module.exports = bluetooth;
