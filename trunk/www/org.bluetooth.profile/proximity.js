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
		
		var BC = require("org.bluetooth.service.link_loss");
		var BC = require("org.bluetooth.service.tx_power");
		var LinkLossUUID = "1803";
		var TxPowerUUID = "1804";
	
		var ProximityProfile = BC.ProximityProfile = BC.Profile.extend({

			onPathLoss : function(device,closeTo_spacing,farAway_spacing,farAwayFunc,safetyZone_func,closeToFunc){
				var txPowerValue = 0;
				device.discoverServices(function(){
					var service = device.getServiceByUUID(TxPowerUUID)[0];
					service.getValue(function(dataValue){
						txPowerValue = dataValue.getHexString();
					});
				});
				this.pathLoss_interval = setInterval(function(){
					device.getRSSI(function(data){
						var value = data - txPowerValue;
						if(value<=farAway_spacing){
							if(farAwayFunc){
								farAwayFunc();
							}
						}else if(value>farAway_spacing && value<closeTo_spacing){
							if(safetyZone_func){
								safetyZone_func();
							}
						}else if(value>=closeTo_spacing){
							if(closeToFunc){
								closeToFunc();
							}
						}
					});
				},1500);
			},

			clearPathLoss : function(){
				if(this.pathLoss_interval){
					window.clearInterval(this.pathLoss_interval);
				}
			},

			onLinkLoss : function(device){
				this.alert(device,LinkLossUUID,'2');
			},

			alert : function(device,serviceUUID,level){
				device.discoverServices(function(){
					var service = device.getServiceByUUID(serviceUUID)[0];
					service.alert(level,'hex');
				});
			},
		});
		
		module.exports = BC;
	
	
