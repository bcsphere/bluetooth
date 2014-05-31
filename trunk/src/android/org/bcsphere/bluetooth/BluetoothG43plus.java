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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.bcsphere.bluetooth.tools.Tools;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

@SuppressLint("NewApi")
public class BluetoothG43plus implements IBluetooth{
	private static final String TAG = "BluetoothG43plus";
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGattServer mBluetoothGattServer;
	private Context mContext;
	private boolean isScanning = false;
	private int scanSum = 0;
	private boolean isOpenGattServer = false;
	private int gattServerSum = 0;
	private HashMap<String, CallbackContext> connectCC = new HashMap<String, CallbackContext>();
	private HashMap<String, CallbackContext> disconnectCC = new HashMap<String, CallbackContext>();
	private HashMap<String, CallbackContext> getServicesCC = new HashMap<String, CallbackContext>();
	private HashMap<String, CallbackContext> writeValueCC = new HashMap<String, CallbackContext>();
	private HashMap<String, CallbackContext> readValueCC = new HashMap<String, CallbackContext>();
	private HashMap<BluetoothGattCharacteristic, CallbackContext> setNotificationCC = new HashMap<BluetoothGattCharacteristic, CallbackContext>();
	private HashMap<String, CallbackContext> getDeviceAllDataCC = new HashMap<String, CallbackContext>();
	private HashMap<String ,CallbackContext> getRSSICC = new HashMap<String, CallbackContext>();
	private HashMap<String, CallbackContext> addEventListenerCC = new HashMap<String, CallbackContext>();
	private CallbackContext addServiceCC;
	private HashMap<String, BluetoothGattService> localServices = new HashMap<String, BluetoothGattService>();
	private HashMap<BluetoothGattCharacteristic, Integer> recordServiceIndex = new HashMap<BluetoothGattCharacteristic, Integer>();
	private HashMap<BluetoothGattCharacteristic, Integer> recordCharacteristicIndex = new HashMap<BluetoothGattCharacteristic, Integer>();
	private HashMap< String ,Boolean> connectedDevice = new HashMap<String, Boolean>(); 
	private HashMap<String, BluetoothGatt> mBluetoothGatts = new HashMap<String, BluetoothGatt>();
	private HashMap<String, List<BluetoothGattService>> deviceServices = new HashMap<String, List<BluetoothGattService>>();
	
	@Override
	public void setContext(Context context) {
		Log.i(TAG, "setContext");
		this.mContext = context;
		mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
	}

	@Override
	public void startScan(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "startScan");
		if (isScanning) {
			Tools.sendSuccessMsg(callbackContext);
			scanSum = scanSum + 1;
			return;
		}

		UUID[] uuids = Tools.getUUIDs(json);
		if (uuids == null || uuids.length < 1) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			Tools.sendSuccessMsg(callbackContext);
			scanSum = scanSum + 1;
			isScanning = true;
		}else {
			mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
			Tools.sendSuccessMsg(callbackContext);
			scanSum = scanSum + 1;	
			isScanning = true;
		}
	}

	@Override
	public void stopScan(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "stopScan");
		if (!isScanning) {
			Tools.sendSuccessMsg(callbackContext);
			return;
		}
		scanSum = scanSum - 1;
		if (scanSum == 0 ) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			isScanning = false;
			Tools.sendSuccessMsg(callbackContext);
		}else {
			Tools.sendSuccessMsg(callbackContext);
		}
	}

	@Override
	public void connect(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "connect");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) != null) {
			Tools.sendSuccessMsg(callbackContext);
			return;
		}
		connectCC.put(deviceAddress, callbackContext);
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		mBluetoothGatts.put(device.getAddress(), device.connectGatt(mContext, false, mGattCallback));
	}

	@Override
	public void disconnect(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "disconnect");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendSuccessMsg(callbackContext);
			return;
		}
		disconnectCC.put(deviceAddress, callbackContext);
		mBluetoothGatts.get(deviceAddress).disconnect();
	}

	@Override
	public void getConnectedDevices(JSONArray json,
			CallbackContext callbackContext) {
		Log.i(TAG, "getConnectedDevices");
		JSONArray ary = new JSONArray();
		List<BluetoothDevice> devices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
		for (int i = 0; i < devices.size(); i++) {
			JSONObject obj = new JSONObject();
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, devices.get(i).getAddress());
			Tools.addProperty(obj, Tools.DEVICE_NAME, devices.get(i).getName());
			ary.put(obj);
		}
		callbackContext.success(ary);
	}

	@Override
	public void writeValue(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "writeValue");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		int serviceIndex = Integer.parseInt(Tools.getData(json, Tools.SERVICE_INDEX));
		int characteristicIndex = Integer.parseInt(Tools.getData(json, Tools.CHARACTERISTIC_INDEX));
		String  descriptorIndex =Tools.getData(json, Tools.DESCRIPTOR_INDEX);
		String writeValue = Tools.getData(json, Tools.WRITE_VALUE);
		writeValueCC.put(deviceAddress, callbackContext);
		if (descriptorIndex.equals("")) {
			BluetoothGattCharacteristic characteristic = deviceServices.get(deviceAddress).get(serviceIndex)
					.getCharacteristics().get(characteristicIndex);
			characteristic.setValue(Tools.decodeBase64(writeValue));
			mBluetoothGatts.get(deviceAddress).writeCharacteristic(characteristic);
		}else {
			BluetoothGattDescriptor descriptor = deviceServices.get(deviceAddress).get(serviceIndex).getCharacteristics()
					.get(characteristicIndex).getDescriptors().get(Integer.parseInt(descriptorIndex));
			descriptor.setValue(Tools.decodeBase64(writeValue));
			mBluetoothGatts.get(deviceAddress).writeDescriptor(descriptor);
		}
	} 

	@Override
	public void readValue(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "readValue");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null ) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		int serviceIndex = Integer.parseInt(Tools.getData(json, Tools.SERVICE_INDEX));
		int characteristicIndex = Integer.parseInt(Tools.getData(json, Tools.CHARACTERISTIC_INDEX));
		String  descriptorIndex =Tools.getData(json, Tools.DESCRIPTOR_INDEX);
		readValueCC.put(deviceAddress, callbackContext);
		if (descriptorIndex.equals("")) {
			mBluetoothGatts.get(deviceAddress).readCharacteristic(deviceServices.get(deviceAddress).get(serviceIndex)
					.getCharacteristics().get(characteristicIndex));
		}else {
			mBluetoothGatts.get(deviceAddress).readDescriptor(deviceServices.get(deviceAddress).get(serviceIndex)
					.getCharacteristics().get(characteristicIndex).getDescriptors().get(Integer.parseInt(descriptorIndex)));
		}
	}

	@Override
	public void setNotification(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "setNotification");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null ) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		int serviceIndex = Integer.parseInt(Tools.getData(json, Tools.SERVICE_INDEX));
		int characteristicIndex = Integer.parseInt(Tools.getData(json, Tools.CHARACTERISTIC_INDEX));
		String enable = Tools.getData(json, Tools.ENABLE);
		BluetoothGattCharacteristic characteristic = deviceServices.get(deviceAddress).get(serviceIndex).getCharacteristics()
				.get(characteristicIndex);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Tools.NOTIFICATION_UUID);
		if (enable.equals("true")) {
			setNotificationCC.put(characteristic, callbackContext);
			if(Tools.lookup(characteristic.getProperties(),BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=null){
			    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			}else{
			    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
			}
			mBluetoothGatts.get(deviceAddress).writeDescriptor(descriptor);
			mBluetoothGatts.get(deviceAddress).setCharacteristicNotification(characteristic, true);
			recordServiceIndex.put(characteristic, serviceIndex);
			recordCharacteristicIndex.put(characteristic, characteristicIndex);
		}else {
			descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			mBluetoothGatts.get(deviceAddress).writeDescriptor(descriptor);
			mBluetoothGatts.get(deviceAddress).setCharacteristicNotification(characteristic, false);
			Tools.sendSuccessMsg(callbackContext);
			setNotificationCC.remove(characteristic);
			recordServiceIndex.remove(characteristic);
			recordCharacteristicIndex.remove(characteristic);
		}
	}

	@Override
	public void getDeviceAllData(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "getDeviceAllData");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		getDeviceAllDataCC.put(deviceAddress, callbackContext);
		mBluetoothGatts.get(deviceAddress).discoverServices();
	}


	@Override
	public void removeServices(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "removeServices");
		String uniqueID = Tools.getData(json, Tools.UINQUE_ID);
		if (uniqueID.equals("")) {
			mBluetoothGattServer.clearServices();
			mBluetoothGattServer.close();
			isOpenGattServer = false;
			Tools.sendSuccessMsg(callbackContext);
		}else {
			if (mBluetoothGattServer.removeService(localServices.get(uniqueID))) {
				Tools.sendSuccessMsg(callbackContext);
			}else {
				Tools.sendErrorMsg(callbackContext);
			}
		}
	}

	@Override
	public void getRSSI(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "getRSSI");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		getRSSICC.put(deviceAddress, callbackContext);
		mBluetoothGatts.get(deviceAddress).readRemoteRssi();
	}

	@Override
	public void getServices(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "getServices");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null ) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		mBluetoothGatts.get(deviceAddress).discoverServices();
		getServicesCC.put(deviceAddress, callbackContext);
	}

	@Override
	public void getCharacteristics(JSONArray json,
			CallbackContext callbackContext) {
		Log.i(TAG, "getCharacteristics");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		JSONObject obj = new JSONObject();
		JSONArray ary = new JSONArray();
		int serviceIndex = Integer.parseInt(Tools.getData(json, Tools.SERVICE_INDEX));
		Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
		List<BluetoothGattCharacteristic> characteristics = deviceServices.get(deviceAddress).get(serviceIndex).getCharacteristics();
		for (int i = 0; i < characteristics.size(); i++) {
			JSONObject infoObj = new JSONObject();
			Tools.addProperty(infoObj, Tools.CHARACTERISTIC_INDEX, i);
			Tools.addProperty(infoObj, Tools.CHARACTERISTIC_UUID, characteristics.get(i).getUuid());
			Tools.addProperty(infoObj, Tools.CHARACTERISTIC_NAME, Tools.lookup(characteristics.get(i).getUuid()));
			Tools.addProperty(infoObj, Tools.CHARACTERISTIC_PROPERTY, Tools.decodeProperty(characteristics.get(i).getProperties()));
			ary.put(infoObj);
		}
		Tools.addProperty(obj, Tools.CHARACTERISTICS, ary);
		callbackContext.success(obj);
	}

	@Override
	public void getDescriptors(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "getDescriptors");
		String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
		if (connectedDevice.get(deviceAddress) == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		JSONObject obj = new JSONObject();
		JSONArray ary = new JSONArray();
		int serviceIndex = Integer.parseInt(Tools.getData(json, Tools.SERVICE_INDEX));
		int characteristicIndex = Integer.parseInt(Tools.getData(json, Tools.CHARACTERISTIC_INDEX));
		List<BluetoothGattDescriptor> descriptors = deviceServices.get(deviceAddress).get(serviceIndex).getCharacteristics().get(characteristicIndex).getDescriptors();
		for (int i = 0; i < descriptors.size(); i++) {
			JSONObject infoObj = new JSONObject();
			Tools.addProperty(infoObj, Tools.DESCRIPTOR_INDEX, i);
			Tools.addProperty(infoObj, Tools.DESCRIPTOR_UUID, descriptors.get(i).getUuid());
			Tools.addProperty(infoObj, Tools.DESCRIPTOR_NAME, Tools.lookup(descriptors.get(i).getUuid()));
			ary.put(infoObj);
		}
		Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
		Tools.addProperty(obj, Tools.DESCRIPTORS, ary);
		callbackContext.success(obj);
	}

	@Override
	public void addEventListener(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "addEventListener");
		String eventName = Tools.getData(json, Tools.EVENT_NAME);
		if (eventName == null) {
			Tools.sendErrorMsg(callbackContext);
			return;
		}
		addEventListenerCC.put(eventName, callbackContext);
	}

	@Override
	public void addServices(JSONArray json, CallbackContext callbackContext) {
		Log.i(TAG, "addServices");
		if (!isOpenGattServer) {
			mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
			isOpenGattServer = true;
		}
		addServiceCC = callbackContext;
		JSONArray services  = Tools.getArray(json, Tools.SERVICES);
		gattServerSum = services.length();
		for (int i = 0; i < services.length(); i++) {
			String uniqueID = Tools.getData(services, i, Tools.UINQUE_ID);
			int serviceType = -1;
			if (Tools.getData(services, i , Tools.SERVICE_TYPE).equals("0")) {
				serviceType = BluetoothGattService.SERVICE_TYPE_PRIMARY;
			}else {
				serviceType = BluetoothGattService.SERVICE_TYPE_SECONDARY;
			}
			UUID serviceUUID = UUID.fromString(Tools.getData(services, i , Tools.SERVICE_UUID));
			BluetoothGattService service =  new BluetoothGattService(serviceUUID, serviceType);
			JSONArray characteristics = Tools.getArray(services, i, Tools.CHARACTERISTICS);
			for (int j = 0; j <characteristics.length(); j++) {
				byte[] characteristicValue = Tools.decodeBase64(Tools.getData(characteristics, Tools.CHARACTERISTIC_VALUE));
				UUID characteristicUUID = UUID.fromString(Tools.getData(characteristics, Tools.CHARACTERISTIC_UUID));
				int characteristicProperty = Tools.encodeProperty(Tools.getArray(characteristics, Tools.CHARACTERISTIC_PROPERTY));
				int characteristicPermission = Tools.encodePermission(Tools.getArray(characteristics, Tools.CHARACTERISTIC_PERMISSION));
				BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(characteristicUUID, characteristicProperty, characteristicPermission);
				characteristic.setValue(characteristicValue);
				JSONArray descriptors = Tools.getArray(characteristics, j, Tools.DESCRIPTORS);
				for (int k = 0; k < descriptors.length(); k++) {
					byte[] descriptorValue =Tools.decodeBase64(Tools.getData(descriptors, Tools.DESCRIPTOR_VALUE));
					UUID descriptorUUID = UUID.fromString(Tools.getData(descriptors, Tools.DESCRIPTOR_UUID));
					int descriptorPermission = Tools.encodePermission(Tools.getArray(descriptors, Tools.DESCRIPTOR_PERMISSION));
					BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(descriptorUUID, descriptorPermission);
					descriptor.setValue(descriptorValue);
					characteristic.addDescriptor(descriptor);
				}
				service.addCharacteristic(characteristic);
			}
			if (mBluetoothGattServer.addService(service)) {
				localServices.put(uniqueID, service);
			}
		}
	}


	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			//Log.i(TAG, "onLeScan");
			startScanManage(device, rssi, scanRecord);
		}
	};

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged");
			super.onCharacteristicChanged(gatt, characteristic);
			setNotificationManage(gatt, characteristic);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicRead");
			super.onCharacteristicRead(gatt, characteristic, status);
			readValueManage( gatt, characteristic, status);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicWrite");
			super.onCharacteristicWrite(gatt, characteristic, status);
			writeValueManage(gatt,status);
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
			Log.i(TAG, "onConnectionStateChange");
			super.onConnectionStateChange(gatt, status, newState);
			String deviceAddress = gatt.getDevice().getAddress();
			if (connectCC.get(deviceAddress) != null) {
			    conncetManage(gatt,newState);
			}else if(disconnectCC.get(deviceAddress) != null){
			    disconnectManage(gatt,newState);
			}else{
			    addEventListenerManage(gatt ,newState);
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorRead");
			super.onDescriptorRead(gatt, descriptor, status);
			readValueManage( gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorWrite");
			super.onDescriptorWrite(gatt, descriptor, status);
			writeValueManage(gatt,status);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i(TAG, "onReadRemoteRssi");
			super.onReadRemoteRssi(gatt, rssi, status);
			getRSSIManage(gatt , rssi ,status);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onServicesDiscovered");
			super.onServicesDiscovered(gatt, status);
			getServicesManage(gatt , status);
			getDeviceAllDataManage(gatt, status);
		}
	};

	private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

		@Override
		public void onServiceAdded(int status, BluetoothGattService service) {
			Log.i(TAG, "onServiceAdded");
			super.onServiceAdded(status, service);
			addServiceManage(status);
		}
	};


	private void startScanManage(BluetoothDevice device , int rssi , byte[] scanRecord){
		JSONObject obj = new JSONObject();
		Tools.addProperty(obj, Tools.DEVICE_ADDRESS, device.getAddress());
		Tools.addProperty(obj, Tools.DEVICE_NAME, device.getName());
		Tools.addProperty(obj, Tools.IS_CONNECTED, Tools.IS_FALSE);
		Tools.addProperty(obj, Tools.RSSI, rssi);
		Tools.addProperty(obj, Tools.ADVERTISEMENT_DATA, Tools.decodeAdvData(scanRecord));
		Tools.addProperty(obj, Tools.TYPE, "BLE");
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK , obj);
		pluginResult.setKeepCallback(true);
		addEventListenerCC.get(Tools.NEW_ADV_PACKET).sendPluginResult(pluginResult);
	}

	private void conncetManage(BluetoothGatt gatt , int newState){
		String deviceAddress = getDeviceAddress(gatt);
		JSONObject obj = new JSONObject();
		if (connectCC.get(deviceAddress) != null) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				connectCC.get(deviceAddress).success(obj);
				connectCC.remove(deviceAddress);
				connectedDevice.put(deviceAddress, true);
			}else{
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				connectCC.get(deviceAddress).error(obj);
				connectCC.remove(deviceAddress);
			}
		}
	}

	private void disconnectManage(BluetoothGatt gatt , int newStatus){
		String deviceAddress = getDeviceAddress(gatt);
		JSONObject obj = new JSONObject();
		if (newStatus ==  BluetoothProfile.STATE_DISCONNECTED) {
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, getDeviceAddress(gatt));
			disconnectCC.get(deviceAddress).success(obj);
			disconnectCC.remove(deviceAddress);
			connectedDevice.remove(deviceAddress);
			if (deviceServices.get(deviceAddress) != null) {
				deviceServices.remove(deviceAddress);
			}
			mBluetoothGatts.remove(deviceAddress);
		}else {
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
			disconnectCC.get(deviceAddress).error(obj);
			disconnectCC.remove(deviceAddress);
		}
	}

	private void getServicesManage(BluetoothGatt gatt , int status){
		String deviceAddress = getDeviceAddress(gatt);
		JSONObject obj = new JSONObject();
		JSONArray ary = new JSONArray();
		if (getServicesCC.get(deviceAddress) !=null) {
			if (deviceServices.get(deviceAddress)==null) {
				deviceServices.put(deviceAddress, gatt.getServices());
			}
			if (deviceServices.get(deviceAddress)!=null) {
				deviceServices.get(deviceAddress).remove(deviceAddress);
				deviceServices.put(deviceAddress, gatt.getServices());
			}
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
			for (int i = 0; i <deviceServices.get(deviceAddress).size(); i++) {
				JSONObject infoObj = new JSONObject();
				Tools.addProperty(infoObj, Tools.SERVICE_INDEX, i);
				Tools.addProperty(infoObj, Tools.SERVICE_UUID, deviceServices.get(deviceAddress).get(i).getUuid());
				Tools.addProperty(infoObj, Tools.SERVICE_NAME, Tools.lookup(deviceServices.get(deviceAddress).get(i).getUuid()));
				ary.put(infoObj);
			}
			Tools.addProperty(obj, Tools.SERVICES, ary);
			getServicesCC.get(deviceAddress).success(obj);
			getServicesCC.remove(deviceAddress);
		}
	}

	private void writeValueManage(BluetoothGatt gatt , int status){
		String deviceAddress = getDeviceAddress(gatt);
		if (writeValueCC.get(deviceAddress) != null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Tools.sendSuccessMsg(writeValueCC.get(deviceAddress));
				writeValueCC.remove(deviceAddress);
			}else {
				Tools.sendErrorMsg(writeValueCC.get(deviceAddress));
				writeValueCC.remove(deviceAddress);
			}
		}
	}

	private void readValueManage(BluetoothGatt gatt ,BluetoothGattCharacteristic characteristic ,int status){
		String deviceAddress = getDeviceAddress(gatt);
		JSONObject obj = new JSONObject();
		if (readValueCC.get(deviceAddress) != null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				Tools.addProperty(obj, Tools.VALUE, Tools.encodeBase64(characteristic.getValue()));
				Tools.addProperty(obj, Tools.DATE, Tools.getDateString());
				readValueCC.get(deviceAddress).success(obj);
				readValueCC.remove(deviceAddress);
			}else {
				Tools.sendErrorMsg(readValueCC.get(deviceAddress));
				readValueCC.remove(deviceAddress);
			}
		}
	}

	private void readValueManage(BluetoothGatt gatt ,BluetoothGattDescriptor descriptor ,int status){
		String deviceAddress = getDeviceAddress(gatt);
		JSONObject obj = new JSONObject();
		if (readValueCC.get(deviceAddress) != null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				Tools.addProperty(obj, Tools.VALUE, Tools.encodeBase64(descriptor.getValue()));
				Tools.addProperty(obj, Tools.DATE, Tools.getDateString());
				readValueCC.get(deviceAddress).success(obj);
				readValueCC.remove(deviceAddress);
			}else {
				Tools.sendErrorMsg(readValueCC.get(deviceAddress));
				readValueCC.remove(deviceAddress);
			}
		}
	}

	private void setNotificationManage(BluetoothGatt gatt , BluetoothGattCharacteristic characteristic){
		String deviceAddress = getDeviceAddress(gatt);
		if (setNotificationCC.get(characteristic) != null) {
			JSONObject obj = new JSONObject();
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
			Tools.addProperty(obj, Tools.SERVICE_INDEX, recordServiceIndex.get(characteristic));
			Tools.addProperty(obj, Tools.CHARACTERISTIC_INDEX, recordCharacteristicIndex.get(characteristic));
			Tools.addProperty(obj, Tools.VALUE, Tools.encodeBase64(characteristic.getValue()));
			Tools.addProperty(obj, Tools.DATE, Tools.getDateString());
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK , obj);
			pluginResult.setKeepCallback(true);
			setNotificationCC.get(characteristic).sendPluginResult(pluginResult);
		}
	}

	private void getDeviceAllDataManage(BluetoothGatt gatt , int status){
		String deviceAddress =  getDeviceAddress(gatt);
		if (getDeviceAllDataCC.get(deviceAddress) != null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				JSONObject obj = new JSONObject();
				JSONArray servicesInfo = new JSONArray();
				List<BluetoothGattService> services = gatt.getServices();
				for (int i = 0; i < services.size(); i++) {
					JSONObject serviceInfo = new JSONObject();
					Tools.addProperty(serviceInfo, Tools.SERVICE_INDEX, i);
					Tools.addProperty(serviceInfo, Tools.SERVICE_UUID, services.get(i).getUuid());
					Tools.addProperty(serviceInfo, Tools.SERVICE_NAME, Tools.lookup(services.get(i).getUuid()));
					List<BluetoothGattCharacteristic>  characteristics = services.get(i).getCharacteristics();
					JSONArray characteristicsInfo = new JSONArray();
					for (int j = 0; j < characteristics.size(); j++) {
						JSONObject characteristicInfo = new JSONObject();
						Tools.addProperty(characteristicInfo, Tools.CHARACTERISTIC_INDEX, j);
						Tools.addProperty(characteristicInfo, Tools.CHARACTERISTIC_UUID, characteristics.get(j).getUuid());
						Tools.addProperty(characteristicInfo, Tools.CHARACTERISTIC_NAME,Tools.lookup(characteristics.get(j).getUuid()));
						Tools.addProperty(characteristicInfo, Tools.CHARACTERISTIC_PROPERTY, Tools.decodeProperty(characteristics.get(j).getProperties()));
						List<BluetoothGattDescriptor> descriptors = new ArrayList<BluetoothGattDescriptor>();
						JSONArray descriptorsInfo = new JSONArray();
						for (int k = 0; k < descriptors.size(); k++) {
							JSONObject descriptorInfo = new JSONObject();
							Tools.addProperty(descriptorInfo, Tools.DESCRIPTOR_INDEX, k);
							Tools.addProperty(descriptorInfo, Tools.DESCRIPTOR_UUID, descriptors.get(k).getUuid());
							Tools.addProperty(descriptorInfo, Tools.DESCRIPTOR_NAME, Tools.lookup(descriptors.get(k).getUuid()));
							descriptorsInfo.put(descriptorInfo);
						}
						Tools.addProperty(characteristicInfo, Tools.DESCRIPTORS, descriptorsInfo);
						characteristicsInfo.put(characteristicInfo);
					}
					Tools.addProperty(serviceInfo, Tools.CHARACTERISTICS, characteristicsInfo);
					servicesInfo.put(serviceInfo);
				}
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				Tools.addProperty(obj, Tools.SERVICES, servicesInfo);
				getDeviceAllDataCC.get(deviceAddress).success(obj);
				getDeviceAllDataCC.remove(deviceAddress);
				deviceServices.put(deviceAddress, services);
			}else {
				Tools.sendErrorMsg(getDeviceAllDataCC.get(deviceAddress));
				getDeviceAllDataCC.remove(deviceAddress);
			}
		}
	}

	private void addServiceManage(int status){
		if (addServiceCC != null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				gattServerSum = gattServerSum - 1 ;
				if (gattServerSum == 0) {
					Tools.sendSuccessMsg(addServiceCC);
					addServiceCC = null;
				}
			}else {
				gattServerSum = 0;
				Tools.sendErrorMsg(addServiceCC);
				addServiceCC = null;
			}
		}
	}

	private void getRSSIManage(BluetoothGatt gatt , int rssi , int status){
		String deviceAddress = getDeviceAddress(gatt);
		if (getRSSICC.get(deviceAddress)!=null) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				JSONObject obj = new JSONObject();
				Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
				Tools.addProperty(obj, Tools.RSSI, rssi);
				getRSSICC.get(deviceAddress).success(obj);
				getRSSICC.remove(deviceAddress);
			}else {
				Tools.sendErrorMsg(getRSSICC.get(deviceAddress));
				getRSSICC.remove(deviceAddress);
			}
		}
	}

	private void addEventListenerManage(BluetoothGatt gatt, int newState){
		String deviceAddress = getDeviceAddress(gatt);
		if (newState == BluetoothGatt.STATE_DISCONNECTED) {
		    connectedDevice.remove(deviceAddress);
		    mBluetoothGatts.remove(deviceAddress);
			JSONObject obj = new JSONObject();
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK , obj);
			pluginResult.setKeepCallback(true);
			addEventListenerCC.get(Tools.DISCONNECT).sendPluginResult(pluginResult);
		}
	}

	private String getDeviceAddress(BluetoothGatt gatt){
		return gatt.getDevice().getAddress();
	}
}
