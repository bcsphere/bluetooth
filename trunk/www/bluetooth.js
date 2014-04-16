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
var interval_index = null;

/**
 * Provides access to bluetooth on the device.
 */
var bluetooth = {
	
	initialBluetooth: function(){

	},
	
	
    /**
     * Open a native alert dialog, with a customizable title and button text.
     *
     * @param {Function} completeCallback   The callback that is bluetooth stop scan
     * 
     */
    startScan: function(successFunc,errorFunc,serviceUUIDs) {
        cordova.exec(successFunc,errorFunc, "BCBluetooth", "startScan", serviceUUIDs);
    },
    
	getScanData: function(getDevicesSuccess,getDevicesError){
		interval_index = window.setInterval(function() {
            cordova.exec(getDevicesSuccess,getDevicesError, "BCBluetooth", "getScanData", []);
        }, 1000);
	},
	
    stopScan: function(successFunc,errorFunc){
   		//alert("stopScan");
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "stopScan", []);
    	if(interval_index !== null){
			window.clearInterval(interval_index);
		}
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
	
	addEventListener : function(callback,errorFunc,arg){
		cordova.exec(callback,errorFunc,"BCBluetooth","addEventListener",[{"eventName":arg.eventName,"arg":arg.arg}]);
	},
};
module.exports = bluetooth;

