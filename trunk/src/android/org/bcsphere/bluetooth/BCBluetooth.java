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

import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import org.bcsphere.bluetooth.tools.BluetoothDetection;
import org.bcsphere.bluetooth.tools.Tools;

public class BCBluetooth extends CordovaPlugin {

	public Context myContext = null;
	private SharedPreferences sp;
	private boolean isSetContext = true;
	private IBluetooth bluetoothAPI = null;
	private String versionOfAPI;
	private CallbackContext newadvpacketContext;
	private CallbackContext disconnectContext;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
	//classical interface relative data structure
	public HashMap<String, BluetoothSerialService> classicalServices = new HashMap<String, BluetoothSerialService>();
	//when the accept services construct a connection , the service will remove from this map & append into classicalServices map for read/write interface call
	public HashMap<String, BluetoothSerialService> acceptServices = new HashMap<String, BluetoothSerialService>();


	public BCBluetooth() {
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {

		super.initialize(cordova, webView);
		myContext = this.webView.getContext();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		myContext.registerReceiver(receiver, intentFilter);
		sp = myContext.getSharedPreferences("VERSION_OF_API", 1);
		BluetoothDetection.detectionBluetoothAPI(myContext);
		try {
			if ((versionOfAPI = sp.getString("API", "no_google"))
					.equals("google")) {
				bluetoothAPI = (IBluetooth) Class.forName(
						"org.bcsphere.bluetooth.BluetoothG43plus")
						.newInstance();
			} else if ((versionOfAPI = sp.getString("API", "no_samsung"))
					.equals("samsung")) {
				bluetoothAPI = (IBluetooth) Class.forName(
						"org.bcsphere.bluetooth.BluetoothSam42").newInstance();
			} else if ((versionOfAPI = sp.getString("API", "no_htc"))
					.equals("htc")) {
				bluetoothAPI = (IBluetooth) Class.forName(
						"org.bcsphere.bluetooth.BluetoothHTC41").newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean execute(final String action, final JSONArray json,
			final CallbackContext callbackContext) throws JSONException {

		if (action.equals("addEventListener")) {
			String eventName = Tools.getData(json, Tools.EVENT_NAME);
			if (eventName.equals("newadvpacket") ) {
				newadvpacketContext = callbackContext;
			}else if(eventName.equals("disconnect")){
				disconnectContext = callbackContext;
			}
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
			Tools.addProperty(jo, "appID", "com.test.yourappid");
			Tools.addProperty(jo, "deviceAddress", "N/A");
			Tools.addProperty(jo, "api", versionOfAPI);
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
		if(action.equals("startClassicalScan")){
			System.out.println("startClassicalScan");
			if(bluetoothAdapter.isEnabled()){
				if(bluetoothAdapter.startDiscovery()){
					callbackContext.success();
				}else{
					callbackContext.error("start classical scan error!");
				}
			}else{
				callbackContext.error("your bluetooth is not open!");
			}
		}
		if(action.equals("stopClassicalScan")){
			System.out.println("stopClassicalScan");
			if(bluetoothAdapter.isEnabled()){
				if(bluetoothAdapter.cancelDiscovery()){
					callbackContext.success();
				}else{
					callbackContext.error("stop classical scan error!");
				}
			}else{
				callbackContext.error("your bluetooth is not open!");
			}
		}
		if(action.equals("rfcommConnect")){
	    	String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
	    	String securestr = Tools.getData(json, Tools.SECURE);
	    	String uuidstr = Tools.getData(json, Tools.UUID);
	    	boolean secure = false;
	    	if(securestr.equals("true")){
	    		secure = true;
	    	}
	    	System.out.println("connect to "+deviceAddress);
	        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
	        BluetoothSerialService classicalService = classicalServices.get(deviceAddress);
	        
	        if(device != null && classicalService == null){
	        	classicalService = new BluetoothSerialService();
	        	classicalService.disconnectCallback = disconnectContext;
	        	classicalServices.put(deviceAddress, classicalService);
	        }
	        
	        if (device != null) {
	        	classicalService.connectCallback = callbackContext;
	        	classicalService.connect(device,uuidstr,secure);
	        } else {
	            callbackContext.error("Could not connect to " + deviceAddress);
	        }
		}
		if (action.equals("rfcommDisconnect")) {
			String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
			BluetoothSerialService service = classicalServices.get(deviceAddress);
			if(service != null){
				service.connectCallback = null;
				service.stop();
				classicalServices.remove(deviceAddress);
				callbackContext.success();
			}else{
				callbackContext.error("Could not disconnect to " + deviceAddress);
			}
        }
		if(action.equals("rfcommListen")){
			String name = Tools.getData(json, Tools.NAME);
			String uuidstr = Tools.getData(json, Tools.UUID);
	    	String securestr = Tools.getData(json, Tools.SECURE);
	    	boolean secure = false;
	    	if(securestr.equals("true")){
	    		secure = true;
	    	}
	    	BluetoothSerialService service = new BluetoothSerialService();
	    	service.listen(name, uuidstr, secure, this);
	    	acceptServices.put(name+uuidstr, service);
		}
		if(action.equals("rfcommUnListen")){
			String name = Tools.getData(json, Tools.NAME);
			String uuidstr = Tools.getData(json, Tools.UUID);
			BluetoothSerialService service = acceptServices.get(name+uuidstr);
			if(service != null){
				service.stop();
			}
		}
		if(action.equals("rfcommWrite")){
			String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
			BluetoothSerialService service = classicalServices.get(deviceAddress);
			if(service != null){
				String data = Tools.getData(json, Tools.WRITE_VALUE);
				service.write(Tools.decodeBase64(data));
				callbackContext.success();
			}else{
				callbackContext.error("there is no connection on device:" + deviceAddress);
			}
		}
		if(action.equals("rfcommRead")){
			String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
			BluetoothSerialService service = classicalServices.get(deviceAddress);
			if(service != null){
				byte[] data = new byte[2048];
				byte[] predata = service.buffer.array();
				for(int i = 0;i < service.bufferSize;i++){
					data[i] = predata[i];
				}
				
		        JSONObject obj = new JSONObject();
				//Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				Tools.addProperty(obj, Tools.VALUE, Tools.encodeBase64(data));
				Tools.addProperty(obj, Tools.DATE, Tools.getDateString());
				callbackContext.success(obj);
				service.bufferSize = 0;
				service.buffer.clear();
			}else{
				callbackContext.error("there is no connection on device:" + deviceAddress);
			}
		}
		if(action.equals("rfcommSubscribe")){
			String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
			BluetoothSerialService service = classicalServices.get(deviceAddress);
			if(service != null){
				service.dataAvailableCallback = callbackContext;
			}else{
				callbackContext.error("there is no connection on device:" + deviceAddress);
			}
		}
		if (action.equals("rfcommUnsubscribe")) {
			String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
			BluetoothSerialService service = classicalServices.get(deviceAddress);
			if(service != null){
				service.dataAvailableCallback = null;
			}else{
				callbackContext.error("there is no connection on device:" + deviceAddress);
			}
        }
		if (!Tools.isOpenBluetooth()) {
			Tools.sendErrorMsg(callbackContext);
			return false;
		}
		if (action.equals("stopScan")) {
			try {
				bluetoothAPI.stopScan(json, callbackContext);
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
		} else if (action.equals("removeServices")) {
			try {
				bluetoothAPI.removeServices(json, callbackContext);
			} catch (Exception e) {
				Tools.sendErrorMsg(callbackContext);
			} catch (java.lang.Error e) {
				Tools.sendErrorMsg(callbackContext);
			}
		}
		
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				if (action.equals("startScan")) {
					try {
						bluetoothAPI.startScan(json, callbackContext);
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

				} else if (action.equals("getServices")) {

					try {
						bluetoothAPI.getServices(json, callbackContext);
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
				} else if (action.equals("getRSSI")) {
					try {
						bluetoothAPI.getRSSI(json, callbackContext);
					} catch (Exception e) {
						Tools.sendErrorMsg(callbackContext);
					} catch (java.lang.Error e) {
						Tools.sendErrorMsg(callbackContext);
					}
				}
			}
		});
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
			} else if (intent.getIntExtra(
					BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1) == 13) {
				JSONObject joClose = new JSONObject();
				try {
					joClose.put("state", "close");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				webView.sendJavascript("cordova.fireDocumentEvent('bluetoothclose')");
			}else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            System.out.println("new classical bluetooth device found!"+device.getAddress());
	            // Add the name and address to an array adapter to show in a ListView
	    		JSONObject obj = new JSONObject();
	    		Tools.addProperty(obj, Tools.DEVICE_ADDRESS, device.getAddress());
	    		Tools.addProperty(obj, Tools.DEVICE_NAME, device.getName());
	    		Tools.addProperty(obj, Tools.IS_CONNECTED, Tools.IS_FALSE);
	    		Tools.addProperty(obj, Tools.TYPE, "Classical");
	    		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK , obj);
	    		pluginResult.setKeepCallback(true);
	    		newadvpacketContext.sendPluginResult(pluginResult);
	        }
		}
	};

}
