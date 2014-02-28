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
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import org.bcsphere.bluetooth.tools.BluetoothDetection;
import org.bcsphere.bluetooth.tools.Tools;


public class BCBluetooth extends CordovaPlugin {

	public static Context myContext = null;
	private static SharedPreferences sp;
	private boolean isSetContext = true;
	private static IBluetooth bluetoothAPI = null;
	private static String versionOfAPI;

	public BCBluetooth() {
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		
		super.initialize(cordova, webView);
		myContext = this.webView.getContext();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		myContext.registerReceiver(receiver, intentFilter);
		sp = myContext.getSharedPreferences("VERSION_OF_API", 1);
		BluetoothDetection.detectionBluetoothAPI(myContext);
		try {
			if ((versionOfAPI = sp.getString("API", "no_google")).equals("google")) {
				bluetoothAPI = (IBluetooth) Class.forName("org.bcsphere.bluetooth.BluetoothG43plus")
						.newInstance();
			} else if ((versionOfAPI = sp.getString("API", "no_samsung")).equals("samsung")) {
				bluetoothAPI = (IBluetooth) Class.forName("org.bcsphere.bluetooth.BluetoothSam42")
						.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean execute(String action, JSONArray json, CallbackContext callbackContext) throws JSONException {
		if (action.equals("addEventListener")) {
			bluetoothAPI.addEventListener(json, callbackContext);
			return true;
		}
		if (isSetContext) {
			try {
				bluetoothAPI.setContext(myContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
			isSetContext = false;
		}
		if (action.equals("getEnvironment")) {
			JSONObject jo = new JSONObject();
			jo.put("appID", "com.test.yourappid");
			jo.put("deviceID", "N/A");
			jo.put("api", versionOfAPI);
			callbackContext.success(jo);
			return true;
		}
		if (action.equals("openBluetooth")) {
			try {
				bluetoothAPI.openBluetooth(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
			return true;
		}
		if (action.equals("getBluetoothState")) {
			try {
				bluetoothAPI.getBluetoothState(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
			return true;
		}
		if (!Tools.isOpenBluetooth()) {
			Tools.sendErrorMsg(callbackContext);
			return true;
		}
		if (action.equals("startScan")) {
			try {
				bluetoothAPI.startScan(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getScanData")) {
			try {
				bluetoothAPI.getScanData(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("stopScan")) {
			try {
				bluetoothAPI.stopScan(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("connect")) {
			try {
				bluetoothAPI.connect(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("disconnect")) {
			try {
				bluetoothAPI.disconnect(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getConnectedDevices")) {
			try {
				bluetoothAPI.getConnectedDevices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getPairedDevices")) {
			try {
				bluetoothAPI.getPairedDevices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("createPair")) {
			try {
				bluetoothAPI.createPair(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext); 
			}
		} else if (action.equals("removePair")) {
			try {
				bluetoothAPI.removePair(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getServices")) {
			try {
				bluetoothAPI.getServices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getCharacteristics")) {
			try {
				bluetoothAPI.getCharacteristics(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getDescriptors")) {
			try {
				bluetoothAPI.getDescriptors(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("writeValue")) {
			try {
				bluetoothAPI.writeValue(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("readValue")) {
			try {
				bluetoothAPI.readValue(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("setNotification")) {
			try {
				bluetoothAPI.setNotification(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getDeviceAllData")) {
			try {
				bluetoothAPI.getDeviceAllData(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("addServices")) {
			try {
				bluetoothAPI.addServices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("removeServices")) {
			try {
				bluetoothAPI.removeServices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else if (action.equals("getRSSI")) {
			try {
				bluetoothAPI.getRSSI(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		} else {
			Tools.sendErrorMsg(callbackContext);
		}
		return true;
	}

	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1) == 11) {
				JSONObject joOpen = new JSONObject();
				try {
					joOpen.put("state", "open");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				webView.sendJavascript("cordova.fireDocumentEvent('bluetoothopen')");
			} else if (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1) == 13) {
				JSONObject joClose = new JSONObject();
				try {
					joClose.put("state", "close");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				webView.sendJavascript("cordova.fireDocumentEvent('bluetoothclose')");
			}
		}
	};


}
