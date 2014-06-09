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
			
		var BC = require("org.bcsphere.bcjs");
		
		var DeviceInformationService = BC.DeviceInformationService = BC.Service.extend({

			   /* org.bluetooth.characteristic.manufacturer_name_string */
			   ManufactureNameUUID:'2a29',
			   ManufactureName,
			   /* org.bluetooth.characteristic.model_number_string */
			   ModelNumberUUID:'2a24',
			   ModelNumber,
			   /* org.bluetooth.characteristic.serial_number_string */
			   SerialNumberUUID:'2a25',
			   SerialNumber,
			   /* org.bluetooth.characteristic.hardware_revision_string */
			   HardwareRevisionUUID:'2a27',
			   HardwareRevision,
			   /* org.bluetooth.characteristic.firmware_revision_string */
			   FirmwareRevisionUUID:'2a26',
			   FirmwareRevision,
			   /* org.bluetooth.characteristic.software_revision_string */
			   SoftwareRevisionUUID:'2a28',
			   SoftwareRevision,
			   /* org.bluetooth.characteristic.system_id */
			   SystemIDUUID:'2a23',
			   SystemID,
			   /* org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list */
			   IEEE11073ListUUID:'2a2a',
			   IEEE11073List,
			   /* org.bluetooth.characteristic.pnp_id */
			   PnPIDUUID:'2a50',
			   PnPID,

			   getManufactureName : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.ManufactureNameUUID)[0].read(function(this.ManufactureName){
						 callback(this.ManufactureName.value);
					});
				});
			   },
			   getModelNumber : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.ModelNumberUUID)[0].read(function(this.ModelNumber){
						 callback(this.ModelNumber.value);
					});
				});
			   },
			   getSerialNumber : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.SerialNumberUUID)[0].read(function(this.SerialNumber){
						 callback(this.SerialNumber.value);
					});
				});
			   },
			   getHardwareRevision : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.HardwareRevisionUUID)[0].read(function(this.HardwareRevision){
						 callback(this.HardwareRevision.value);
					});
				});
			   },
			   getFirmwareRevision : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.FirmwareRevisionUUID)[0].read(function(this.FirmwareRevision){
						 callback(this.FirmwareRevision.value);
					});
				});
			   },
			   getSoftwareRevision : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.SoftwareRevisionUUID)[0].read(function(this.SoftwareRevision){
						 callback(this.SoftwareRevision.value);
					});
				});
			   },
			   getSystemID : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.SystemIDUUID)[0].read(function(this.SystemID){
						 callback(this.SystemID.value);
					});
				});
			   },
			   getIEEE11073List : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.IEEE11073ListUUID)[0].read(function(this.IEEE11073List){
						 callback(this.IEEE11073List.value);
					});
				});
			   },
			   getPnPID : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.PnPIDUUID)[0].read(function(this.PnPID){
						 callback(this.PnPID.value);
					});
				});
			   },

			   getAll : function(callback){
				this.getManufactureName();
				this.getModelNumber();
				this.getSerialNumber();
				this.getHardwareRevision();
				this.getFirmwareRevision();
				this.getSoftwareRevision();
				this.getSystemID();
				this.getIEEE11073List();
				this.getPnPID();
			   },

		});
		
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["0000180a-0000-1000-8000-00805f9b34fb"] = BC.DeviceInformationService;
		});
		module.exports = BC;
