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
	/**
	 * Triggered when new iBeacon has been found.
	 * @example BC.iBeaconManager.addEventListener("newibeacon",app.onNewIBeacon);
	 * function onNewIBeacon(s){
	 *	var newibeacon = s.target;
	 *	newibeacon.addEventListener("ibeaconproximityupdate",app.onIBeaconProximityUpdate);
	 *	newibeacon.addEventListener("ibeaconaccuracyupdate",app.onIBeaconAccuracyUpdate);
	 * }
	 * @event newibeacon
	 * @type {object}
	 */
	 
	/**
	 * Triggered when iBeacon proximity has been updated.
	 * @example BC.iBeaconManager.addEventListener("ibeaconproximityupdate",onNewIBeacon);
	 * function onNewIBeacon(s){
	 *	var theibeacon = s.target;
	 *	theibeacon.addEventListener("ibeaconproximityupdate",onIBeaconProximityUpdate);
	 * }
	 * function onIBeaconProximityUpdate(theibeacon){
	 *	alert("iBeacon proximity: " + theibeacon.proximity);
	 * }
	 * @event ibeaconproximityupdate
	 * @type {object}
	 */
	 
	/**
	 * Triggered when iBeacon accuracy has been updated.
	 * @example BC.iBeaconManager.addEventListener("ibeaconaccuracyupdate",app.onNewIBeacon);
	 * function onNewIBeacon(s){
	 *	var theibeacon = s.target;
	 *	theibeacon.addEventListener("ibeaconaccuracyupdate",onIBeaconAccuracyUpdate);
	 * }
	 * function onIBeaconAccuracyUpdate(theibeacon){
	 *	alert("iBeacon accuracy: " + theibeacon.accuracy);
	 * }
	 * @event ibeaconaccuracyupdate
	 * @type {object}
	 */
	
	function isIBeacon(advData){
		var hexStr = advData.getHexString();
		for(var i = 4; i < 12; i += 2){
			if(hexStr.substring(i, i + 4) == "0215"){
				return i;
			}
		}
		return 0;
	};
	
	function isNewIBeacon(iBeaconID){
		var res = true;
		_.each(BC.iBeaconManager.ibeacons,function(ibeacon){
			if(ibeacon.iBeaconID == iBeaconID){
				res = false;
			}
		});
		
		return res;
	};
	
	function isEmpty(s){
		return ((s == undefined || s == null || s == "") ? true : false); 
	}
	
	document.addEventListener('bccoreready', onBCCoreReady, false);
	
	function onBCCoreReady(){
		var eventName = "org.bcsphere.ibeacon.ready";
		var	iBeaconManager = BC.iBeaconManager = new BC.IBeaconManager("org.bcsphere.ibeacon",eventName);
		BC.bluetooth.dispatchEvent(eventName);
	}
	
	var IBeaconManager = BC.IBeaconManager = BC.Plugin.extend({
	
		pluginInitialize : function(){
			
			if(API == "ios"){
				navigator.ibeacon.addEventListener('ibeaconaccuracyupdate', function(arg){
					var majorStrObj = new BC.DataValue(BC.Tools.base64ToBuffer(arg.major));
					var minorStrObj = new BC.DataValue(BC.Tools.base64ToBuffer(arg.minor));
					var majorStr = majorStrObj.getHexString();
					var minorStr = minorStrObj.getHexString();
					var iBeaconID = arg.proximityUUID + majorStr + minorStr;
					if(isNewIBeacon(iBeaconID)){
						var newibeacon = new BC.IBeacon({iBeaconID:iBeaconID,accuracy:arg.accuracy,proximity:arg.proximity});
						BC.iBeaconManager.ibeacons[iBeaconID] = newibeacon;
						BC.iBeaconManager.dispatchEvent("newibeacon",newibeacon);
					}else{
						var theibeacon = BC.iBeaconManager.ibeacons[iBeaconID];
						theibeacon.accuracy = arg.accuracy;
						theibeacon.RSSI = arg.RSSI;
						theibeacon.dispatchEvent("ibeaconaccuracyupdate");
						if(theibeacon.proximity !== arg.proximity){
							theibeacon.proximity = arg.proximity;
							theibeacon.dispatchEvent("ibeaconproximityupdate");
						}
					}
				});
			}
			
			this.ibeacons = {};
			this.regions = [];
			
		},
		
	});
	
	/** 
	 * Starts IBeacon Advertising (It's only support IOS >= 7.0 now).
	 * @memberof IBeaconManager
	 * @method 
	 * @example BC.IBeaconManager.StartIBeaconAdvertising(successFunc,errorFunc,"00000000-0000-0000-0000-000000000000",200,300,"iBeacon Name");
	 * @param {string} {proximityUUID} - The proximity UUID to looking for
	 * @param {int} [major] - The major of the ibeacon
	 * @param {int} [minor] - The minor of the ibeacon
	 * @param {string} [identifier] - The identifier of the ibeacon
	 */
	var StartIBeaconAdvertising = BC.IBeaconManager.StartIBeaconAdvertising = function(success,error,proximityUUID,major,minor,identifier){
		navigator.ibeacon.startIBeaconAdvertising(success,error,proximityUUID,major,minor,identifier);
	};
	
	/** 
	 * Starts a scan for iBeacons.
	 * @memberof IBeaconManager
	 * @method 
	 * @example BC.IBeaconManager.StartIBeaconScan("00000000-0000-0000-0000-000000000000",65535,256);
	 * @param {string} {proximityUUID} - The proximity UUID to looking for
	 * @param {int} [major] - The major of the ibeacon
	 * @param {int} [minor] - The minor of the ibeacon
	 */
	var StartIBeaconScan = BC.IBeaconManager.StartIBeaconScan = function(proximityUUID,major,minor){
		if(API.toLowerCase() == "ios"){
			navigator.ibeacon.startIBeaconScan(null,null,proximityUUID,major,minor);
		}else{
			var region = {};
			region.proximityUUID = proximityUUID;
			region.major = major;
			region.minor = minor;
			BC.iBeaconManager.region = region;
			BC.Bluetooth.StartScan();
			BC.bluetooth.addEventListener("newadvpacket",function(event){
				var scanData = event.target;
				var advertisementData,deviceAddress,deviceName,isCon,RSSI,txPower;
				if(scanData['advertisementData']){
					advertisementData = scanData['advertisementData'];
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
				var isConnected = false;
				if(isCon === "true"){
					isConnected = true;
				}
				
				if(!isEmpty(advertisementData.manufacturerData) && isIBeacon(advertisementData.manufacturerData) !== 0){
					var manufacturerData = advertisementData.manufacturerData;
					var startPos = isIBeacon(manufacturerData);
					var manufacturerDataHexStr = manufacturerData.getHexString();
					var iBeaconID = manufacturerDataHexStr.substring(startPos + 4,startPos + 44);
					var txPowerStr = manufacturerDataHexStr.substring(startPos + 44,startPos + 46);
					var txPower = BC.Tools.ConvertHexStringToInt(txPowerStr);
					if(txPower > 127){
						txPower = - (256 - txPower);
					}
							
					if(isNewIBeacon(iBeaconID)){
						var newibeacon = new BC.IBeacon({deviceAddress:deviceAddress,deviceName:deviceName,advertisementData:advertisementData,isConnected:isConnected,RSSI:RSSI,iBeaconID:iBeaconID,txPower:txPower});
						if(newibeacon.matchRegion(BC.iBeaconManager.region)){
							BC.iBeaconManager.ibeacons[iBeaconID] = newibeacon;
							BC.iBeaconManager.dispatchEvent("newibeacon",newibeacon);
						}
					}else{
						//update the RSSI and recalculate the proximity
						var theibeacon = BC.iBeaconManager.ibeacons[iBeaconID];
						theibeacon.txPower = txPower;
						theibeacon.RSSI = RSSI;
						if(!isEmpty(BC.iBeaconManager.region)){
							theibeacon.calculateAccuracy();
						}
					}
				}
			});
		}
	};
	
	/** 
	 * Stops a scanning for iBeacon.
	 * @memberof IBeaconManager
	 * @method 
	 * @example 
	 * BC.IBeaconManager.StopIBeaconScan(proximityUUID,major,minor);
	 * @param {string} {proximityUUID} - The proximity UUID to stop
	 * @param {int} [major] - The major of the region
	 * @param {int} [minor] - The minor of the region
	 */
	var StopIBeaconScan = BC.IBeaconManager.StopIBeaconScan = function(proximityUUID,major,minor){
		if(API.toLowerCase() == "ios"){
			navigator.ibeacon.stopIBeaconScan(null,null,proximityUUID,major,minor);
		}else{
			BC.iBeaconManager.region = null;
			BC.Bluetooth.StopScan();
		}
	};
	
	/**
	 * IBeacon is the BLE device which matches the Apple iBeacon format.
	 * @class
	 * @param {string} iBeaconID - The iBeaconID include all info about this iBeacon(proximityUUID/major/minor)
	 * @param {int} txPower - The iBeacon txPower
	 * @param {float} accuracy - The accuracy of this iBeacon
	 * @param {float} proximity - The proximity of this iBeacon
	 * @property {string} proximityUUID - The proximityUUID of this iBeacon
	 * @property {string} major - The major number of this iBeacon
	 * @property {string} minor - The minor number of this iBeacon
	 * @property {float} accuracy - The accuracy of this iBeacon
	 * @property {int} proximity - The proximity of this iBeacon(0:Unknown | 1:Less than half a meter away | 2:More than half a meter away, but less than four meters away | 3:More than four meters away)
	 */
	var IBeacon = BC.IBeacon = BC.Device.extend({
	
		deviceInitialize : function(arg){
			this.iBeaconID = arg.iBeaconID;
			this.proximityUUID = this.iBeaconID.substring(0,8);
			this.proximityUUID += '-';
			this.proximityUUID += this.iBeaconID.substring(8,12);
			this.proximityUUID += '-';
			this.proximityUUID += this.iBeaconID.substring(12,16);
			this.proximityUUID += '-';
			this.proximityUUID += this.iBeaconID.substring(16,20);
			this.proximityUUID += '-';
			this.proximityUUID += this.iBeaconID.substring(20,32);
			
			this.major = BC.Tools.ConvertHexStringToInt(this.iBeaconID.substring(32,36));
			this.minor = BC.Tools.ConvertHexStringToInt(this.iBeaconID.substring(36,40));
			this.txPower = arg.txPower;
			this.proximity = -1;
			
			if(isEmpty(arg.accuracy)){
				this.calculateAccuracy();
			}else{
				//if this new iBeacon form IOS > 7.0
				this.accuracy = arg.accuracy;
				this.proximity = arg.proximity;
			}
		},
	
		calculateAccuracy : function(){
			var ratio = this.RSSI * 1.0 / this.txPower;
			if (ratio < 1.0) {
				this.accuracy = Math.pow(ratio,10);
			}
			else {
				this.accuracy =  (0.89976) * Math.pow(ratio,7.7095) + 0.111;	
			}
			if(!isNewIBeacon(this.iBeaconID)){
				this.dispatchEvent("ibeaconaccuracyupdate");
			}
			
			var newproximity = -1;
			if(this.accuracy < 0.5){
				newproximity = 1;
			}else if(this.accuracy > 0.5 && this.accuracy < 4){
				newproximity = 2;
			}else if(this.accuracy > 4){
				newproximity = 3;
			}
			if(this.proximity !== newproximity){
				this.proximity = newproximity;
				if(!isNewIBeacon(this.iBeaconID)){
					this.dispatchEvent("ibeaconproximityupdate");
				}
			}
		},
		
		matchRegion : function(region){
			if(isEmpty(region)) return false;
			if(!isEmpty(region.proximityUUID) && region.proximityUUID !== this.proximityUUID){
				return false;
			}
			if(!isEmpty(region.major) && region.major !== this.major){
				return false;
			}
			if(!isEmpty(region.minor) && region.minor !== this.minor){
				return false;
			}
			return true;
		},
		
	});
	
	module.exports = BC;
	

