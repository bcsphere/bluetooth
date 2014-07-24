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
 * Provides access to ibeacon.
 */
var ibeacon = {
	
	startIBeaconScan : function(callback,errorFunc,proximityUUID,major,minor){
		cordova.exec(callback,errorFunc,"BCIBeacon","startIBeaconScan",[{"proximityUUID":proximityUUID,"major":major,"minor":minor}]);
	},
	
	stopIBeaconScan : function(callback,errorFunc,proximityUUID,major,minor){
		cordova.exec(callback,errorFunc,"BCIBeacon","stopIBeaconScan",[{"proximityUUID":proximityUUID,"major":major,"minor":minor}]);
	},
	
	startIBeaconAdvertising : function(callback,errorFunc,proximityUUID,major,minor,identifier){
		cordova.exec(callback,errorFunc,"BCIBeacon","startIBeaconAdvertising",[{"proximityUUID":proximityUUID,"major":major,"minor":minor,"identifier":identifier}]);
	},
	
	addEventListener : function(eventName,callback,errorFunc){
		cordova.exec(callback,errorFunc,"BCIBeacon","addEventListener",[{"eventName":eventName}]);
	},
};
module.exports = ibeacon;

