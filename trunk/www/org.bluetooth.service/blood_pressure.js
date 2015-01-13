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
			
		var BC = require("org.bcsphere.bluetooth.bcjs");
		
		var BloodPressureService = BC.BloodPressureService = BC.Service.extend({

			   /* org.bluetooth.characteristic.blood_pressure_measurement */
			   BloodPressureMeasurementUUID:'2a35',
			   /* org.bluetooth.characteristic.intermediate_cuff_pressure */
			   IntermediateCuffPressureUUID:'2a36',
			   /* org.bluetooth.characteristic.blood_pressure_feature */
			   BloodPressureFeatureUUID:'2a49',

			   //ToDo
		});
		
		document.addEventListener('bccoreready',function(){
			BC.bluetooth.UUIDMap["00001810-0000-1000-8000-00805f9b34fb"] = BC.BloodPressureService;
		});
		module.exports = BC;