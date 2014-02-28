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
    
    connectDevice: function(successFunc,errorFunc,deviceID,appID){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "connect", [{"deviceID":deviceID,"appID":appID}]);
    },
    
    disconnectDevice: function(successFunc,errorFunc,deviceID,appID){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "disconnect", [{"deviceID":deviceID,"appID":appID}]);
    },
    
    discoverServices: function(successFunc,errorFunc,deviceID){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "getServices", [{"deviceID":deviceID}]);
    },
    
    discoverCharacteristics: function(successFunc,errorFunc,deviceID,serviceIndex){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "getCharacteristics", [{"deviceID":deviceID,"serviceIndex":serviceIndex}]);
    },
    
    discoverDescriptors: function(successFunc,errorFunc,deviceID,serviceIndex,charcteristicIndex){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "getDescriptors", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":charcteristicIndex}]);
    },
    
    readCharacteristic: function(successFunc,errorFunc,deviceID,serviceIndex,characteristicIndex){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "readValue", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":""}]);
    },
    
    writeCharacteristic: function(successFunc,errorFunc,deviceID,serviceIndex,characteristicIndex,writeValue,writeType){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "writeValue", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":"","writeValue":writeValue,"writeType":writeType}]);
    },
    
    subscribe: function(successFunc,errorFunc,deviceID,serviceIndex,characteristicIndex,notifyEventName){
        cordova.exec(successFunc,errorFunc, "BCBluetooth", "setNotification", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"enable":"true"}]); 
    },
    
    unsubscribe: function(successFunc,errorFunc,deviceID,serviceIndex,characteristicIndex,notifyEventName){
        cordova.exec(successFunc,errorFunc, "BCBluetooth", "setNotification", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"enable":"false"}]); 
    },

	readDescriptor: function(successFunc,errorFunc,deviceID,serviceIndex,characteristicIndex,descriptorIndex){
    	cordova.exec(successFunc,errorFunc, "BCBluetooth", "readValue", [{"deviceID":deviceID,"serviceIndex":serviceIndex,"characteristicIndex":characteristicIndex,"descriptorIndex":descriptorIndex}]);
    },
	
	getDeviceAllData : function(successFunc,errorFunc,deviceID){
		cordova.exec(successFunc,errorFunc, "BCBluetooth", "getDeviceAllData",[{"deviceID":deviceID}]);
	},
	
	getRSSI : function(successFunc,errorFunc,deviceID){
		cordova.exec(successFunc,errorFunc, "BCBluetooth", "getRSSI",[{"deviceID":deviceID}]);
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
	
	createPair : function(successFunc,errorFunc,deviceID){
		cordova.exec(successFunc,errorFunc, "BCBluetooth", "createPair",[{"deviceID":deviceID}]);
	},
	
	removePair : function(successFunc,errorFunc,deviceID){
		cordova.exec(successFunc,errorFunc, "BCBluetooth", "removePair",[{"deviceID":deviceID}]);
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

