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

package org.bcsphere.bluetooth;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;

import android.content.Context;

public interface IBluetooth {
	void setContext(Context context);
	void startScan(JSONArray json,CallbackContext callbackContext);
	void stopScan(JSONArray json,CallbackContext callbackContext);
	void connect(JSONArray json,CallbackContext callbackContext) ;
	void disconnect(JSONArray json,CallbackContext callbackContext) ;
	void getConnectedDevices(JSONArray json ,CallbackContext callbackContext) ;
	void writeValue(JSONArray json,CallbackContext callbackContext) ;
	void readValue(JSONArray json,CallbackContext callbackContext)  ;
	void setNotification(JSONArray json,CallbackContext callbackContext)  ;
	void getDeviceAllData(JSONArray json , CallbackContext callbackContext)  ;
	void addServices(JSONArray json, CallbackContext callbackContext);
	void removeServices(JSONArray json,CallbackContext callbackContext)  ;
	void getRSSI(JSONArray json, CallbackContext callbackContext)  ;
	void getServices(JSONArray json, CallbackContext callbackContext)  ;
	void getCharacteristics(JSONArray json, CallbackContext callbackContext)  ;
	void getDescriptors(JSONArray json, CallbackContext callbackContext)  ;
	void addEventListener(JSONArray json, CallbackContext callbackContext);
}
