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
		
		var FindMeProfile = BC.FindMeProfile = BC.Profile.extend({
			
			no_alert : function(device){
			  this.alert(device,'0');
			},
		   
			mild_alert : function(device){
			  this.alert(device,'1');
			},
		   
			high_alert : function(device){
			  this.alert(device,'2');
			},

			alert : function(device,level){
				device.discoverServices(function(){
					var service = device.getServiceByUUID(serviceUUID)[0];
					service.alert(level);
				});
			},
			
		});
		
		module.exports = BC;