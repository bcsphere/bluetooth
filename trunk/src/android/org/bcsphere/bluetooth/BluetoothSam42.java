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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;

import org.bcsphere.bluetooth.tools.Tools;
import com.samsung.android.sdk.bt.gatt.BluetoothGatt;
import com.samsung.android.sdk.bt.gatt.BluetoothGattAdapter;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCallback;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCharacteristic;
import com.samsung.android.sdk.bt.gatt.BluetoothGattDescriptor;
import com.samsung.android.sdk.bt.gatt.BluetoothGattServer;
import com.samsung.android.sdk.bt.gatt.BluetoothGattServerCallback;
import com.samsung.android.sdk.bt.gatt.BluetoothGattService;
import com.samsung.android.sdk.bt.gatt.MutableBluetoothGattCharacteristic;
import com.samsung.android.sdk.bt.gatt.MutableBluetoothGattDescriptor;
import com.samsung.android.sdk.bt.gatt.MutableBluetoothGattService;

public class BluetoothSam42 implements IBluetooth {

    private static final String TAG = "BluetoothSam42";
    private static int serviceNumber = -1;
    private static int addedServiceNumber = 0;
    private boolean scanning = false;
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattServer bluetoothGattServer;
    private List<BluetoothDevice> bluetoothDevices;
    private CallbackContext addServiceCallBack;
    private Map<String, CallbackContext> mapGetRSSICallBack;
    private Map<String, CallbackContext> mapConnectCallBack;
    private Map<String, CallbackContext> mapDisconnectCallBack;
    private Map<Object, CallbackContext> mapReadValueCallBack;
    private Map<Object, CallbackContext> mapWriteValueCallBack;
    private Map<String, CallbackContext> mapGetServicesCallBack;
    private Map<String, CallbackContext> mapAddListenerCallBack;
    private Map<Object, CallbackContext> mapSetNotificationCallBack;
    private Map<String, CallbackContext> mapGetDeviceAllDataCallBack;
    private Map<String, Integer> mapRssiData;
    private Map<String, byte[]> mapDeviceAdvData;
    private Map<String, BluetoothGattService> mapRemoteServices;
    private Map<String, List<BluetoothGattService>> mapDeviceServices;

    @Override
    public void setContext(Context context) {
        Log.i(TAG, "setContext");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothGattAdapter.getProfileProxy(context, serviceListener, BluetoothGattAdapter.GATT);
        BluetoothGattAdapter.getProfileProxy(context, serviceListener, BluetoothGattAdapter.GATT_SERVER);
        while (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (bluetoothGatt != null) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openBluetooth(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "openBluetooth");
        bluetoothAdapter.enable();
    }

    @Override
    public void getBluetoothState(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getBluetoothState");
        boolean state = bluetoothAdapter.isEnabled();
        JSONObject jsonObject = new JSONObject();
        if (state) {
            Tools.addProperty(jsonObject, Tools.BLUETOOTH_STATE, Tools.IS_TRUE);
        } else {
            Tools.addProperty(jsonObject, Tools.BLUETOOTH_STATE, Tools.IS_FALSE);
        }
        callbackContext.success(jsonObject);
    }

    @Override
    public void startScan(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "startScan");
        if (!isInitialized(callbackContext)) {
            return;
        }
        if (mapDeviceAdvData == null) {
            mapDeviceAdvData = new HashMap<String, byte[]>();
        }
        if (mapRssiData == null) {
            mapRssiData = new HashMap<String, Integer>();
        }
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        UUID[] uuids = Tools.getUUIDs(json);
        boolean result = false;
        if (uuids == null || uuids.length < 1) {
            result = bluetoothGatt.startScan();
        } else {
            result = bluetoothGatt.startScan(uuids);
        }
        if (!result) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        scanning = true;
        Tools.sendSuccessMsg(callbackContext);
    }

    @Override
    public void getScanData(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getScanData");
        JSONArray jsonDevices = new JSONArray();
        for (BluetoothDevice device : bluetoothDevices) {
            String deviceID = device.getAddress();
            JSONObject jsonDevice = new JSONObject();
            Tools.addProperty(jsonDevice, Tools.DEVICE_ID, deviceID);
            Tools.addProperty(jsonDevice, Tools.DEVICE_NAME, device.getName());
            Tools.addProperty(jsonDevice, Tools.IS_CONNECTED, bluetoothGatt.getConnectedDevices().contains(device));
            Tools.addProperty(jsonDevice, Tools.RSSI, mapRssiData.get(deviceID));
            Tools.addProperty(jsonDevice, Tools.ADVERTISEMENT_DATA, Tools.decodeAdvData(mapDeviceAdvData.get(deviceID)));
            jsonDevices.put(jsonDevice);
        }
        callbackContext.success(jsonDevices);
    }

    @Override
    public void stopScan(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "stopScan");
        if (!isInitialized(callbackContext)) {
            return;
        }
        if (scanning) {
            bluetoothGatt.stopScan();
            scanning = false;
        }
        Tools.sendSuccessMsg(callbackContext);
    }

    @Override
    public void connect(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "connect");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (isConnected(device)) {
            Tools.sendSuccessMsg(callbackContext);
            return;
        }
        if (mapConnectCallBack == null) {
            mapConnectCallBack = new HashMap<String, CallbackContext>();
        }
        mapConnectCallBack.put(deviceID, callbackContext);
        bluetoothGatt.connect(device, false);
    }

    @Override
    public void disconnect(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "disconnect");
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendSuccessMsg(callbackContext);
            return;
        }
        if (mapDisconnectCallBack == null) {
            mapDisconnectCallBack = new HashMap<String, CallbackContext>();
        }
        mapDisconnectCallBack.put(deviceID, callbackContext);
        bluetoothGatt.cancelConnection(device);
    }

    @Override
    public void getConnectedDevices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getConnectedDevices");
        if (!isInitialized(callbackContext)) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<BluetoothDevice> bluetoothDevices = bluetoothGatt.getConnectedDevices();
        JSONArray jsonDevices = new JSONArray();
        for (BluetoothDevice device : bluetoothDevices) {
            JSONObject jsonDevice = new JSONObject();
            Tools.addProperty(jsonDevice, Tools.DEVICE_ID, device.getAddress());
            Tools.addProperty(jsonDevice, Tools.DEVICE_NAME, device.getName());
            jsonDevices.put(jsonDevice);
        }
        callbackContext.success(jsonDevices);
    }

    @Override
    public void getPairedDevices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getPairedDevices");
        if (!isInitialized(callbackContext)) {
            return;
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        JSONArray jsonDevices = new JSONArray();
        for (BluetoothDevice device : bondedDevices) {
            JSONObject jsonDevice = new JSONObject();
            Tools.addProperty(jsonDevice, Tools.DEVICE_ID, device.getAddress());
            Tools.addProperty(jsonDevice, Tools.DEVICE_NAME, device.getName());
            jsonDevices.put(jsonDevice);
        }
        callbackContext.success(jsonDevices);
    }

    @Override
    public void createPair(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "createPair");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (bluetoothAdapter.getBondedDevices().contains(device)) {
            Tools.sendSuccessMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
        try {
            if (Tools.creatBond(device.getClass(), device)) {
                Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                callbackContext.success(jsonObject);
            } else {
                Tools.addProperty(jsonObject, Tools.MES, Tools.ERROR);
                callbackContext.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePair(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "removePair");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
        try {
            if (Tools.removeBond(device.getClass(), device)) {
                Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                callbackContext.success(jsonObject);
            } else {
                Tools.addProperty(jsonObject, Tools.MES, Tools.ERROR);
                callbackContext.success(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getServices");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapGetServicesCallBack == null) {
            mapGetServicesCallBack = new HashMap<String, CallbackContext>();
        }
        mapGetServicesCallBack.put(deviceID, callbackContext);
        bluetoothGatt.discoverServices(device);
    }

    @Override
    public void getCharacteristics(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getCharacteristics");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String[] args = new String[] { deviceID, serviceIndex };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }

        if (serviceIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
        JSONArray characteristics = new JSONArray();
        int size = getService(deviceID, serviceIndex).getCharacteristics().size();
        for (int i = 0; i < size; i++) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = getCharacteristic(deviceID, serviceIndex,
                    String.valueOf(i));
            UUID charateristicUUID = bluetoothGattCharacteristic.getUuid();
            JSONObject characteristic = new JSONObject();
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_INDEX, i);
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_UUID, charateristicUUID);
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_NAME, Tools.lookup(charateristicUUID));
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_PROPERTY,
                    Tools.decodeProperty(bluetoothGattCharacteristic.getProperties()));
            characteristics.put(characteristic);
        }
        Tools.addProperty(jsonObject, Tools.CHARACTERISTICS, characteristics);
        callbackContext.success(jsonObject);
    }

    @Override
    public void getDescriptors(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getDescriptors");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String[] args = new String[] { deviceID, serviceIndex, characteristicIndex };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
        JSONArray descriptors = new JSONArray();
        @SuppressWarnings("unchecked")
        List<BluetoothGattDescriptor> listBluetoothGattDescriptors = getCharacteristic(deviceID, serviceIndex,
                characteristicIndex).getDescriptors();
        int length = listBluetoothGattDescriptors.size();
        for (int i = 0; i < length; i++) {
            UUID uuid = listBluetoothGattDescriptors.get(i).getUuid();
            JSONObject descriptor = new JSONObject();
            Tools.addProperty(descriptor, Tools.DESCRIPTOR_INDEX, i);
            Tools.addProperty(descriptor, Tools.DESCRIPTOR_UUID, uuid);
            Tools.addProperty(descriptor, Tools.DESCRIPTOR_NAME, Tools.lookup(uuid));
            descriptors.put(descriptor);
        }
        Tools.addProperty(jsonObject, Tools.DESCRIPTORS, descriptors);
        callbackContext.success(jsonObject);
    }

    @Override
    public void writeValue(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "writeValue");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String descriptorIndex = Tools.getData(json, Tools.DESCRIPTOR_INDEX);
        String writeValue = Tools.getData(json, Tools.WRITE_VALUE);
        String writeType = Tools.getData(json, Tools.WRITE_TYPE);
        String[] args = new String[] { deviceID, serviceIndex, characteristicIndex, writeType, writeValue };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        if (descriptorIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        byte[] value = Tools.parsingCodingFormat(writeValue, writeType);
        if (mapWriteValueCallBack == null) {
            mapWriteValueCallBack = new HashMap<Object, CallbackContext>();
        }
        if ("".equals(descriptorIndex)) {
            writeCharacteristic(deviceID, serviceIndex, characteristicIndex, value,callbackContext);
        } else {
            writeDescriptor(deviceID, serviceIndex, characteristicIndex, descriptorIndex, value,callbackContext);
        }
    }

    @Override
    public void readValue(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "readValue");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String descriptorIndex = Tools.getData(json, Tools.DESCRIPTOR_INDEX);
        String[] args = new String[] { deviceID, serviceIndex, characteristicIndex};
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        if (descriptorIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapReadValueCallBack == null) {
            mapReadValueCallBack = new HashMap<Object, CallbackContext>();
        }
        if ("".equals(descriptorIndex)) {
            readCharacteristic(deviceID, serviceIndex, characteristicIndex,callbackContext);
        } else {
            readDescriptor(deviceID, serviceIndex, characteristicIndex, descriptorIndex,callbackContext);
        }
    }

    @Override
    public void setNotification(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "setNotification");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String enable = Tools.getData(json, Tools.ENABLE);
        String[] args = new String[] { deviceID, serviceIndex, characteristicIndex, enable };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = getCharacteristic(deviceID, serviceIndex,
                characteristicIndex);
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(Tools.NOTIFICATION_UUID);
        if ("true".equalsIgnoreCase(enable)) {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, false);
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        if (mapSetNotificationCallBack == null) {
            mapSetNotificationCallBack = new HashMap<Object, CallbackContext>();
        }
        mapSetNotificationCallBack.put(bluetoothGattCharacteristic, callbackContext);
        boolean result = bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        Log.i(TAG, "setNotification is " + result);
    }

    @Override
    public void getDeviceAllData(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getDeviceAllData");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapDeviceServices == null) {
            mapDeviceServices = new HashMap<String, List<BluetoothGattService>>();
        }
        if (mapGetDeviceAllDataCallBack == null) {
            mapGetDeviceAllDataCallBack = new HashMap<String, CallbackContext>();
        }
        mapGetDeviceAllDataCallBack.put(deviceID, callbackContext);
        bluetoothGatt.discoverServices(device);
    }

    @Override
    public void addServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "addService");
        JSONObject jsonServices = Tools.getObjectFromArray(json);
		JSONArray jsonArray = Tools.getArray(json, Tools.SERVICES);
		if(mapRemoteServices == null){
			mapRemoteServices = new HashMap<String, BluetoothGattService>();
		}
        addServiceCallBack = callbackContext;
        serviceNumber = jsonServices.length();
        for (int i = 0; i < jsonServices.length(); i++) {
            String serviceIndex = Tools.getData(jsonArray, Tools.UINQUE_ID);
            String serviceType = Tools.getData(jsonArray, Tools.SERVICE_TYPE);
            String strServiceUUID = Tools.getData(jsonArray, Tools.SERVICE_UUID);
            String[] args = new String[] { serviceIndex, serviceType, strServiceUUID };
            if (!isNullOrEmpty(args, callbackContext)) {
                return;
            }
            UUID serviceUUID = UUID.fromString(strServiceUUID);
            MutableBluetoothGattService bluetoothGattService = createService(serviceUUID, serviceType);
            JSONArray jsonCharacteristics = Tools.getArray(jsonArray, Tools.CHARACTERISTICS);
            addCharacteristics(bluetoothGattService, jsonCharacteristics, callbackContext);
            if (bluetoothGattServer.addService(bluetoothGattService)) {
                mapRemoteServices.put(serviceIndex, bluetoothGattService);
            }
        }

    }

    private void addCharacteristics(MutableBluetoothGattService bluetoothGattService, JSONArray jsonCharacteristics,
            CallbackContext callbackContext) {
        int characterLength = jsonCharacteristics.length();
        for (int j = 0; j < characterLength; j++) {
            String characteristicValueType = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_VALUE_TYPE);
            String characteristicValue = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_VALUE);
            String strCharacteristicUUID = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_UUID);
            String[] args = new String[] {characteristicValueType, characteristicValue,
                    strCharacteristicUUID };
            if (!isNullOrEmpty(args, callbackContext)) {
                return;
            }
            UUID characteristicUUID = UUID.fromString(strCharacteristicUUID);
            int characterProperty = Tools.encodeProperty(Tools.getArray(jsonCharacteristics, j,
                    Tools.CHARACTERISTIC_PROPERTY));
            int characterPermission = Tools.encodePermission(Tools.getArray(jsonCharacteristics, j,
                    Tools.CHARACTERISTIC_PERMISSION));
            MutableBluetoothGattCharacteristic bluetoothGattCharacteristic = createCharacteristic(characteristicUUID,
                    characterProperty, characterPermission, characteristicValueType, characteristicValue);
            JSONArray jsonDescriptors = Tools.getArray(jsonCharacteristics, j, Tools.DESCRIPTORS);
            addDescriptors(bluetoothGattCharacteristic, jsonDescriptors, callbackContext);
            bluetoothGattService.addCharacteristic(bluetoothGattCharacteristic);
        }
    }

    private void addDescriptors(MutableBluetoothGattCharacteristic bluetoothGattCharacteristic,
            JSONArray jsonDescriptors, CallbackContext callbackContext) {
        int descLength = jsonDescriptors.length();
        for (int k = 0; k < descLength; k++) {
            String descriptorValue = Tools.getData(jsonDescriptors, k, Tools.DESCRIPTOR_VALUE);
            String strDescriptorUUID = Tools.getData(jsonDescriptors, k, Tools.DESCRIPTOR_UUID);
            String descriptorValueType = Tools.getData(jsonDescriptors, k, Tools.DESCRIPTOR_VALUE_TYPE);
            UUID descriptorsUUID = UUID.fromString(strDescriptorUUID);
            int descriptorsPermission = Tools.encodePermission(Tools.getArray(jsonDescriptors, k,
                    Tools.DESCRIPTOR_PERMISSION));
            MutableBluetoothGattDescriptor bluetoothGattDescriptor = createDescriptor(descriptorsUUID,
                    descriptorsPermission, descriptorValueType, descriptorValue);
            bluetoothGattCharacteristic.addDescriptor(bluetoothGattDescriptor);
        }
    }

    @Override
    public void removeServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "removeServices");
        if (bluetoothGattServer == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        if ("".equals(serviceIndex)) {
            bluetoothGattServer.clearServices();
            bluetoothGattServer = null;
            Tools.sendSuccessMsg(callbackContext);
        } else {
            bluetoothGattServer.removeService(mapRemoteServices.get(serviceIndex));
            Tools.sendSuccessMsg(callbackContext);
        }
    }

    @Override
    public void getRSSI(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getRSSI");
        String deviceID = Tools.getData(json, Tools.DEVICE_ID);
        if (deviceID == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceID);
        if (!isConnected(device)) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapGetRSSICallBack == null) {
            mapGetRSSICallBack = new HashMap<String, CallbackContext>();
        }
        if (mapRssiData == null) {
            mapRssiData = new HashMap<String, Integer>();
        }
        mapGetRSSICallBack.put(deviceID, callbackContext);
        bluetoothGatt.readRemoteRssi(device);
    }

    private MutableBluetoothGattService createService(UUID uuid, String serviceType) {
        int type = -1;
        if ("0".equals(serviceType)) {
            type = BluetoothGattService.SERVICE_TYPE_PRIMARY;
        } else {
            type = BluetoothGattService.SERVICE_TYPE_SECONDARY;
        }
        return new MutableBluetoothGattService(uuid, type);
    }

    private MutableBluetoothGattCharacteristic createCharacteristic(UUID uuid, int property, int permission,
            String valueType, String value) {
        MutableBluetoothGattCharacteristic bluetoothGattCharacteristic = new MutableBluetoothGattCharacteristic(uuid,
                property, permission);
        byte[] charValue  = Tools.parsingCodingFormat(value, valueType);
        bluetoothGattCharacteristic.setValue(charValue);
        return bluetoothGattCharacteristic;
    }

    private MutableBluetoothGattDescriptor createDescriptor(UUID uuid, int permission, String valueType, String value) {
        MutableBluetoothGattDescriptor bluetoothGattDescriptor = new MutableBluetoothGattDescriptor(uuid, permission);
        byte[] desValue = Tools.parsingCodingFormat(value, valueType);
        bluetoothGattDescriptor.setValue(desValue);
        return bluetoothGattDescriptor;
    }

    private boolean writeCharacteristic(String deviceID, String serviceIndex, String characteristicIndex, byte[] value, CallbackContext callbackContext) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = getCharacteristic(deviceID, serviceIndex,characteristicIndex);
        mapWriteValueCallBack.put(bluetoothGattCharacteristic, callbackContext);
        bluetoothGattCharacteristic.setValue(value);
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    private boolean writeDescriptor(String deviceID, String serviceIndex, String characteristicIndex,
            String descriptorIndex, byte[] value, CallbackContext callbackContext) {
        BluetoothGattDescriptor bluetoothGattDescriptor = getDescriptor(deviceID, serviceIndex, characteristicIndex,descriptorIndex);
        mapWriteValueCallBack.put(bluetoothGattDescriptor, callbackContext);
        bluetoothGattDescriptor.setValue(value);
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }

    private boolean readCharacteristic(String deviceID, String serviceIndex, String characteristicIndex,CallbackContext callbackContext) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = getCharacteristic(deviceID, serviceIndex,characteristicIndex);
        mapReadValueCallBack.put(bluetoothGattCharacteristic, callbackContext);
        return bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    private boolean readDescriptor(String deviceID, String serviceIndex, String characteristicIndex,String descriptorIndex,CallbackContext callbackContext) {
        BluetoothGattDescriptor bluetoothGattDescriptor = getDescriptor(deviceID, serviceIndex, characteristicIndex,descriptorIndex);
        mapReadValueCallBack.put(bluetoothGattDescriptor, callbackContext);
        return bluetoothGatt.readDescriptor(bluetoothGattDescriptor);
    }

    private BluetoothGattDescriptor getDescriptor(String deviceID, String serviceIndex, String characteristicIndex,String descriptorIndex) {
        return (BluetoothGattDescriptor) getCharacteristic(deviceID, serviceIndex, characteristicIndex).getDescriptors().get(Integer.parseInt(descriptorIndex));
    }

    private BluetoothGattCharacteristic getCharacteristic(String deviceID, String serviceIndex,
            String characteristicIndex) {
        return (BluetoothGattCharacteristic) getService(deviceID, serviceIndex).getCharacteristics().get(
                Integer.parseInt(characteristicIndex));
    }

    private BluetoothGattService getService(String deviceID, String serviceIndex) {
        return mapDeviceServices.get(deviceID).get(Integer.parseInt(serviceIndex));
    }


    private String getDeviceID(BluetoothGattService service) {
        String deviceID = "";
        Iterator<Entry<String, List<BluetoothGattService>>> it = mapDeviceServices.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<BluetoothGattService>> bluetoothEntry = it.next();
            if (bluetoothEntry.getValue().contains(service)) {
                deviceID = bluetoothEntry.getKey();
                break;
            }
        }
        return deviceID;
    }

    private String getServiceIndex(BluetoothGattService service) {
        String serviceIndex = "";
        Iterator<Entry<String, List<BluetoothGattService>>> it = mapDeviceServices.entrySet().iterator();
        while (it.hasNext()) {
            List<BluetoothGattService> bluetoothGattServices = it.next().getValue();
            int index = bluetoothGattServices.indexOf(service);
            if (index != -1) {
                serviceIndex = String.valueOf(index);
                break;
            }
        }
        return serviceIndex;
    }

    private String getCharacteristicIndex(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        return String.valueOf(service.getCharacteristics().indexOf(characteristic));
    }

    private BluetoothProfile.ServiceListener serviceListener = new ServiceListener() {

        @Override
        public void onServiceDisconnected(int profile) {
            if (null != bluetoothGatt) {
                bluetoothGatt.unregisterApp();
                bluetoothGatt = null;
            }
            if (null != bluetoothGattServer) {
                bluetoothGattServer.unregisterApp();
                bluetoothGattServer = null;
            }
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if ((proxy instanceof BluetoothGatt) && bluetoothGatt == null) {
                bluetoothGatt = (BluetoothGatt) proxy;
                bluetoothGatt.registerApp(bluetoothGattCallback);
            }
            if ((proxy instanceof BluetoothGattServer) && bluetoothGattServer == null) {
                bluetoothGattServer = (BluetoothGattServer) proxy;
                bluetoothGattServer.registerApp(bluetoothGattServerCallback);
            }
        }
    };

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged");
            super.onCharacteristicChanged(characteristic);
            String deviceID = getDeviceID(characteristic.getService());
            CallbackContext callbackContext = null;
            if (mapSetNotificationCallBack != null) {
                callbackContext = mapSetNotificationCallBack.get(characteristic);
            }
            if (callbackContext != null) {
                JSONObject jsonObject = new JSONObject();
                Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                Tools.addProperty(jsonObject, Tools.SERVICE_INDEX, getServiceIndex(characteristic.getService()));
                Tools.addProperty(jsonObject, Tools.CHARACTERISTIC_INDEX,
                        getCharacteristicIndex(characteristic.getService(), characteristic));
                Tools.addProperty(jsonObject, Tools.VALUE, Tools.encodeBase64(characteristic.getValue()));
                Tools.addProperty(jsonObject, Tools.DATE, Tools.getDateString());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead");
            super.onCharacteristicRead(characteristic, status);
            String deviceID = getDeviceID(characteristic.getService());
            CallbackContext callbackContext = null;
            if (mapReadValueCallBack != null) {
                callbackContext = mapReadValueCallBack.get(characteristic);
            }
            if (callbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    JSONObject jsonObject = new JSONObject();
                    Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                    Tools.addProperty(jsonObject, Tools.VALUE, Tools.encodeBase64(characteristic.getValue()));
                    Tools.addProperty(jsonObject, Tools.DATE, Tools.getDateString());
                    callbackContext.success(jsonObject);
                } else {
                    Tools.sendErrorMsg(callbackContext);
                }
                mapReadValueCallBack.remove(deviceID);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(characteristic, status);
            Log.i(TAG, "onCharacteristicWrite");
            String deviceID = getDeviceID(characteristic.getService());
            CallbackContext callbackContext = mapWriteValueCallBack.get(characteristic);
            if (callbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Tools.sendSuccessMsg(callbackContext);
                } else {
                    Tools.sendErrorMsg(callbackContext);
                }
                mapWriteValueCallBack.remove(deviceID);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG, "onConnectionStateChange");
            String deviceID = device.getAddress();
            CallbackContext connectCallbackContext = null;
            CallbackContext disConnectCallbackContext = null;
            JSONObject jsonObject = new JSONObject();
            if (mapConnectCallBack != null) {
                connectCallbackContext = mapConnectCallBack.get(deviceID);
            }
            if (mapDisconnectCallBack != null) {
                disConnectCallbackContext = mapDisconnectCallBack.get(deviceID);
            }
            if (disConnectCallbackContext == null && mapAddListenerCallBack != null) {
                disConnectCallbackContext = mapAddListenerCallBack.get(Tools.DISCONNECT);
            }
            if (connectCallbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                    Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                    connectCallbackContext.success(jsonObject);
                } else {
                    Tools.sendErrorMsg(connectCallbackContext);
                }
                mapConnectCallBack.remove(deviceID);
                return;
            }
            if (disConnectCallbackContext != null) {
            	Log.i(TAG, "device: "+deviceID+" disconnect!");
                if (newState == BluetoothProfile.STATE_DISCONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                    Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                    Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    pluginResult.setKeepCallback(true);
                    disConnectCallbackContext.sendPluginResult(pluginResult);
                } else {
                    Tools.sendErrorMsg(disConnectCallbackContext);
                }
                mapDisconnectCallBack.remove(deviceID);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothDevice device, int rssi, int status) {
            super.onReadRemoteRssi(device, rssi, status);
            Log.i(TAG, "onReadRemoteRssi");
            String deviceID = device.getAddress();
            CallbackContext callbackContext = null;
            if (mapGetRSSICallBack != null) {
                callbackContext = mapGetRSSICallBack.get(deviceID);
            }
            if (callbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    JSONObject jsonObject = new JSONObject();
                    Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                    Tools.addProperty(jsonObject, Tools.RSSI, Integer.toString(rssi));
                    callbackContext.success(jsonObject);
                } else {
                    Tools.sendErrorMsg(callbackContext);
                }
                mapGetRSSICallBack.remove(deviceID);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(descriptor, status);
            Log.i(TAG, "onDescriptorRead");
            String deviceID = getDeviceID(descriptor.getCharacteristic().getService());
            CallbackContext callbackContext = mapReadValueCallBack.get(deviceID);
            if (callbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    JSONObject jsonObject = new JSONObject();
                    Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
                    Tools.addProperty(jsonObject, Tools.VALUE, Tools.encodeBase64(descriptor.getValue()));
                    Tools.addProperty(jsonObject, Tools.DATE, Tools.getDateString());
                    callbackContext.success(jsonObject);
                } else {
                    Tools.sendErrorMsg(callbackContext);
                }
                mapReadValueCallBack.remove(deviceID);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(descriptor, status);
            Log.i(TAG, "onDescriptorWrite");
            String deviceID = getDeviceID(descriptor.getCharacteristic().getService());
            CallbackContext writeValueCallbackContext = null;
            if (mapWriteValueCallBack != null) {
                writeValueCallbackContext = mapWriteValueCallBack.get(descriptor);
            }
            if (writeValueCallbackContext != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Tools.sendSuccessMsg(writeValueCallbackContext);
                } else {
                    Tools.sendErrorMsg(writeValueCallbackContext);
                }
                mapWriteValueCallBack.remove(deviceID);
            }
            
        }

        @Override
        public void onServicesDiscovered(BluetoothDevice device, int status) {
            super.onServicesDiscovered(device, status);
            Log.i(TAG, "onServicesDiscovered");
            String deviceID = device.getAddress();
            @SuppressWarnings("unchecked")
            List<BluetoothGattService> listServices = bluetoothGatt.getServices(device);
            JSONObject jsonObject = new JSONObject();
            Tools.addProperty(jsonObject, Tools.DEVICE_ID, deviceID);
            JSONArray services = new JSONArray();
            int index = 0;
            for(Iterator<BluetoothGattService> it = listServices.iterator();it.hasNext();){
                BluetoothGattService bluetoothGattService = it.next();
                UUID uuid = bluetoothGattService.getUuid();
                JSONObject service = new JSONObject();
                Tools.addProperty(service, Tools.SERVICE_INDEX, index++);
                Tools.addProperty(service, Tools.SERVICE_UUID, uuid);
                Tools.addProperty(service, Tools.SERVICE_NAME, Tools.lookup(uuid));
                services.put(service);
            }
            Tools.addProperty(jsonObject, Tools.SERVICES, services);
            if (mapDeviceServices == null) {
                mapDeviceServices = new HashMap<String, List<BluetoothGattService>>();
            }
            mapDeviceServices.put(deviceID, listServices);
            CallbackContext getServicesCallback = null;
            CallbackContext getDeviceAllDataCallback = null;
            if (mapGetServicesCallBack != null) {
                getServicesCallback = mapGetServicesCallBack.get(deviceID);
            }
            if (mapGetDeviceAllDataCallBack != null) {
                getDeviceAllDataCallback = mapGetDeviceAllDataCallBack.get(deviceID);
            }
            int serviceLength = listServices.size();
            if (getServicesCallback != null) {
                getServicesCallback.success(jsonObject);
                mapGetServicesCallBack.remove(deviceID);
            }
            if (getDeviceAllDataCallback != null) {
                JSONArray deviceAllData = null;
                try {
                    deviceAllData = jsonObject.getJSONArray(Tools.SERVICES);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < serviceLength; i++) {
                    JSONObject service = null;
                    try {
                        service = (JSONObject) deviceAllData.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray characteristics = new JSONArray();
                    @SuppressWarnings("unchecked")
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = getService(deviceID, Integer.toString(i)).getCharacteristics();
                    int charaLength = bluetoothGattCharacteristics.size();
                    for (int j = 0; j < charaLength; j++) {
                        JSONObject characteristic = new JSONObject();
                        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattCharacteristics.get(j);
                        UUID uuid = bluetoothGattCharacteristic.getUuid();
                        Tools.addProperty(characteristic, Tools.CHARACTERISTIC_UUID, uuid);
                        Tools.addProperty(characteristic, Tools.CHARACTERISTIC_INDEX, j);
                        Tools.addProperty(characteristic, Tools.CHARACTERISTIC_NAME, Tools.lookup(uuid));
                        Tools.addProperty(characteristic, Tools.CHARACTERISTIC_PROPERTY,
                        Tools.decodeProperty(bluetoothGattCharacteristic.getProperties()));
                        JSONArray descriptors = new JSONArray();
                        @SuppressWarnings("unchecked")
                        List<BluetoothGattDescriptor> listDescriptors = bluetoothGattCharacteristic.getDescriptors();
                        for (int k = 0; k < listDescriptors.size(); k++) {
                            JSONObject descriptor = new JSONObject();
                            BluetoothGattDescriptor bluetoothGattDescriptor = listDescriptors.get(k);
                            UUID descriptorUUID = bluetoothGattDescriptor.getUuid();
                            Tools.addProperty(descriptor, Tools.DESCRIPTOR_UUID, descriptorUUID);
                            Tools.addProperty(descriptor, Tools.DESCRIPTOR_INDEX, k);
                            Tools.addProperty(descriptor, Tools.DESCRIPTOR_NAME, Tools.lookup(descriptorUUID));
                            descriptors.put(descriptor);
                        }
                        Tools.addProperty(characteristic, Tools.DESCRIPTORS, descriptors);
                        characteristics.put(characteristic);
                    }
                    Tools.addProperty(service, Tools.CHARACTERISTICS, characteristics);
                }
                Tools.addProperty(jsonObject, Tools.SERVICES, deviceAllData);
                getDeviceAllDataCallback.success(jsonObject);
                mapGetDeviceAllDataCallBack.remove(deviceID);
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
            super.onScanResult(device, rssi, scanRecord);
            Log.i(TAG, "onScanResult");
            if (!bluetoothDevices.contains(device)) {
                String deviceID = device.getAddress();
                bluetoothDevices.add(device);
                mapRssiData.put(deviceID, rssi);
                mapDeviceAdvData.put(deviceID, scanRecord);
            }
        }
    };

    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, "onCharacteristicReadRequest");
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset,
                byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded,
                    offset, value);
            Log.i(TAG, "onCharacteristicWriteRequest");
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService bluetoothGattService) {
            super.onServiceAdded(status, bluetoothGattService);
            Log.i(TAG, "onServiceAdded");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                addedServiceNumber++;
                if (addedServiceNumber == serviceNumber) {
                    if (addServiceCallBack != null) {
                        Tools.sendSuccessMsg(addServiceCallBack);
                    }
                    addedServiceNumber = 0;
                }
            }
        }
    };

    private boolean isInitialized(CallbackContext callbackContext) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled() || bluetoothGatt == null) {
            Tools.sendErrorMsg(callbackContext);
            return false;
        }
        return true;
    }

    private boolean isConnected(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        return bluetoothGatt.getConnectedDevices().contains(device)
                && bluetoothGatt.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
    }

    @Override
    public void addEventListener(JSONArray json, CallbackContext callbackContext) {
        String eventName = Tools.getData(json, Tools.EVENT_NAME);
        if (eventName == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapAddListenerCallBack == null) {
            mapAddListenerCallBack = new HashMap<String, CallbackContext>();
        }
        mapAddListenerCallBack.put(eventName, callbackContext);
    }


    private boolean isNullOrEmpty(String[] args, CallbackContext callbackContext) {
        if (args == null) {
            Tools.sendErrorMsg(callbackContext);
            return false;
        }
        int length = args.length;
        for (int i = 0; i < length; i++) {
            String str = args[i];
            if (str == null || str.isEmpty()) {
                Tools.sendErrorMsg(callbackContext);
                return false;
            }
        }
        return true;
    }

}
