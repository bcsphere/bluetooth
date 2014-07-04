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
		
		var BC = require("org.bluetooth.service.immediate_alert");
		
		var serviceUUID = "1802";
		
		/**
		 * BC.FindMeProfile is an implementation of find me profile.
		 * <b>Please Note:</b> JSDoc can't generate part of the javascript file, please check the detail interface usage in the source file code comments.
		 * @memberof BC
		 * @class
		 * @property {string} serviceUUID - The find me service uuid for BLE
		 */
		var FindMeProfile = BC.FindMeProfile = BC.Profile.extend({
			
			/**
			 * Stops an alert on specification device.
			 * @memberof FindMeProfile
			 * @example 
			 *  var BC = window.BC = cordova.require("org.bluetooth.profile.find_me");
			 *  var findmeProfile = new BC.FindMeProfile();
			 *	findmeProfile.no_alert(device);
			 * @param {Device} device - the device to operate
			 */		
			no_alert : function(device){
			  this.alert(device,'0');
			},
		   
			/**
			 * Start a middle alert on specification device.
			 * @memberof FindMeProfile
			 * @example 
			 *  var BC = window.BC = cordova.require("org.bluetooth.profile.find_me");
			 *  var findmeProfile = new BC.FindMeProfile();
			 *	findmeProfile.mild_alert(device);
			 * @param {Device} device - the device to operate
			 */	
			mild_alert : function(device){
			  this.alert(device,'1');
			},
			
			/**
			 * Start a high alert on specification device.
			 * @memberof FindMeProfile
			 * @example 
			 *  var BC = window.BC = cordova.require("org.bluetooth.profile.find_me");
			 *  var findmeProfile = new BC.FindMeProfile();
			 *	findmeProfile.high_alert(device);
			 * @param {Device} device - the device to operate
			 */		   
			high_alert : function(device){
			  this.alert(device,'2');
			},
			
			/**
			 * Start a alert on specification device.
			 * @memberof FindMeProfile
			 * @example 
			 *  var BC = window.BC = cordova.require("org.bluetooth.profile.find_me");
			 *  var findmeProfile = new BC.FindMeProfile();
			 *	findmeProfile.high_alert(device);
			 * @param {Device} device - the device to operate
			 * @param {int} level - the alert level from 0 to 8, 0 stand for no alert, 8 stand for the top alert level
			 */	
			alert : function(device,level){
				device.discoverServices(function(){
					var service = device.getServiceByUUID(serviceUUID)[0];
					service.alert(level);
				});
			},
			
		});
		
		module.exports = BC;