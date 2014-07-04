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
		
		var HealthThermometerService = BC.HealthThermometerService = BC.Service.extend({

			/*org.bluetooth.characteristic.temperature_measurement*/
			TemperatureMeasurementUUID:'2a1c',
			/*org.bluetooth.characteristic.temperature_type*/
			TemperatureTypeUUID:'2a1d',
			/*org.bluetooth.characteristic.intermediate_temperature*/
			IntermediateTemperatureUUID:'2a1e',
			/*org.bluetooth.characteristic.measurement_interval*/
			MeasurementIntervalUUID:'2a21',

			subscribeTemperature : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.TemperatureMeasurementUUID)[0].subscribe(function(data){
						callback(data.value);
						/*ToDo: Need parse data further:
						(1) Flags Field
						(2) Temperature Measurement Value Field
						(3) Time Stamp Field
						(4) Temperature Type Field
						*/
					});
				});
			},

			getType : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.TemperatureTypeUUID)[0].read(function(data){
						 callback(data.value);
					});
				});
			},

			subscribeIntermediateTemperature : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.IntermediateTemperatureUUID)[0].subscribe(function(data){
						callback(data.value);
					});
				});
			},
			
			getMeasurementInterval : function(callback){
				this.discoverCharacteristics(function(){
					this.getCharacteristicByUUID(this.MeasurementIntervalUUID)[0].read(function(data){
						 callback(data.value);
					});
				});
			},	
			
			setMeasurementInterval : function(writeValue,writeType,successFunc,errorFunc){
				  successFunc = successFunc || this.writeSuccess;
				  errorFunc = errorFunc || this.writeError;
				  this.discoverCharacteristics(function(){
						this.getCharacteristicByUUID(this.MeasurementIntervalUUID)[0].write(writeType,writeValue,successFunc,errorFunc);
				  });
			   },

			   writeSuccess : function(){
				  console.log('writeSuccess');
			   },

			   writeError : function(){
				  console.log('writeFailed');
			   },	
		});
		
		
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["00001809-0000-1000-8000-00805f9b34fb"] = BC.HealthThermometerService;
		});
		module.exports = BC;
