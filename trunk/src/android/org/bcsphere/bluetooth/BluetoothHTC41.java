package org.bcsphere.bluetooth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.bcsphere.bluetooth.tools.Tools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.htc.android.bluetooth.le.gatt.BleAdapter;
import com.htc.android.bluetooth.le.gatt.BleCharacteristic;
import com.htc.android.bluetooth.le.gatt.BleClientProfile;
import com.htc.android.bluetooth.le.gatt.BleClientService;
import com.htc.android.bluetooth.le.gatt.BleConstants;
import com.htc.android.bluetooth.le.gatt.BleDescriptor;
import com.htc.android.bluetooth.le.gatt.BleGattID;
import com.htc.android.bluetooth.le.gatt.BleServerProfile;
import com.htc.android.bluetooth.le.gatt.BleServerService;

public class BluetoothHTC41 implements IBluetooth {

    private static final String TAG = "BluetoothHTC41";

    private static BluetoothDevice connectDevice;
    public static final byte[] ENABLE_NOTIFICATION_VALUE = { 0x01, 0x00 };
    public static final byte[] ENABLE_INDICATION_VALUE = { 0x02, 0x00 };
    public static final byte[] DISABLE_NOTIFICATION_VALUE = { 0x00, 0x00 };
    public static final String GENERIC_ACCESS = "00001803-0000-1000-8000-00805f9b34fb";

    private Context context;
    private Map<String, Short> mapRssiData;
    private IntentFilter localIntentFilter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothClientProfile bleClientProfile;
    private CallbackContext addServiceCallBack;
    private List<BluetoothDevice> bluetoothDevices;
    private BluetoothClientService undefinedService;
    private ArrayList<BleServerService> listServices;
    private BluetoothClientProfile bluetoothClientProfile;
    private Map<String, CallbackContext> mapConnectCallBack;
    private Map<String, BleServerService> mapRemoteServices;
    private Map<Object, CallbackContext> mapReadValueCallBack;
    private Map<Object, CallbackContext> mapWriteValueCallBack;
    private Map<String, CallbackContext> mapAddListenerCallBack;
    private Map<String, List<BleClientService>> mapDeviceServices;
    private Map<Object, CallbackContext> mapSetNotificationCallBack;
    private Map<String, CallbackContext> mapGetDeviceAllDataCallBack;
    private Map<String, CallbackContext> mapDisconnectCallBack;

    @Override
    public void setContext(Context context) {
        Log.i(TAG, "setContext");
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothClientProfile = new BluetoothClientProfile(context);
        bluetoothClientProfile.initServices();
        localIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        localIntentFilter.addAction(BleAdapter.ACTION_UUID);
        context.registerReceiver(mReceiver, localIntentFilter);
    }

    @Override
    public void startScan(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "startScan");
        if (!isInitialized(callbackContext)) {
            return;
        }
        if (bluetoothDevices == null) {
            bluetoothDevices = new ArrayList<BluetoothDevice>();
        }
        if (mapRssiData == null) {
            mapRssiData = new HashMap<String, Short>();
        }
        boolean result = false;
        if (bluetoothAdapter != null) {
            result = bluetoothAdapter.startDiscovery();
        }
        Log.i(TAG, "startScan result is " + result);
        Tools.sendSuccessMsg(callbackContext);
    }

    @Override
    public void getScanData(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getScanData");
        JSONArray jsonDevices = new JSONArray();
        for (BluetoothDevice device : bluetoothDevices) {
            String deviceAddress = device.getAddress();
            JSONObject jsonDevice = new JSONObject();
            Tools.addProperty(jsonDevice, Tools.DEVICE_ADDRESS, deviceAddress);
            Tools.addProperty(jsonDevice, Tools.DEVICE_NAME, device.getName());
            Tools.addProperty(jsonDevice, Tools.IS_CONNECTED, "NO");
            Tools.addProperty(jsonDevice, Tools.RSSI, mapRssiData.get(deviceAddress));
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
        if (!bluetoothAdapter.isDiscovering()) {
            Tools.sendSuccessMsg(callbackContext);
            return;
        }
        boolean result = bluetoothAdapter.cancelDiscovery();
        Log.i(TAG, "stopScan result is " + result);
        context.unregisterReceiver(mReceiver);
        Tools.sendSuccessMsg(callbackContext);
    }

    @Override
    public void connect(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "connect");
        if (bleClientProfile != null && bleClientProfile.isProfileRegistered()) {
            bluetoothClientProfile = bleClientProfile;
        }
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (bluetoothClientProfile.isProfileRegistered()) {
            Log.i(TAG, "registerProfile is true");
        } else {
            Log.i(TAG, "registerProfile is false");
            Tools.sendErrorMsg(callbackContext);
            return;
        }

        if (bluetoothClientProfile.connect(device) == BleConstants.GATT_SUCCESS) {
            if (mapConnectCallBack == null) {
                mapConnectCallBack = new HashMap<String, CallbackContext>();
            }
            mapConnectCallBack.put(deviceAddress, callbackContext);
        } else {
            Tools.sendErrorMsg(callbackContext);
        }
    }

    @Override
    public void disconnect(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "disconnect");
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (mapDisconnectCallBack == null) {
            mapDisconnectCallBack = new HashMap<String, CallbackContext>();
        }
        mapDisconnectCallBack.put(deviceAddress, callbackContext);
        bleClientProfile.disconnect(device);
    }

    @Override
    public void getConnectedDevices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getConnectedDevices");
        if (!isInitialized(callbackContext)) {
            return;
        }
        BluetoothDevice[] bluetoothDevices = null;
        if (bleClientProfile != null && bleClientProfile.isProfileRegistered()) {
            bluetoothDevices = bleClientProfile.getConnectedDevices();
        } else {
            bluetoothDevices = bluetoothClientProfile.getConnectedDevices();
        }
        JSONArray jsonDevices = new JSONArray();
        for (BluetoothDevice device : bluetoothDevices) {
            JSONObject jsonDevice = new JSONObject();
            Tools.addProperty(jsonDevice, Tools.DEVICE_ADDRESS, device.getAddress());
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
            Tools.addProperty(jsonDevice, Tools.DEVICE_ADDRESS, device.getAddress());
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
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (bluetoothAdapter.getBondedDevices().contains(device)) {
            Tools.sendSuccessMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
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
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
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
    public void writeValue(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "writeValue");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String descriptorIndex = Tools.getData(json, Tools.DESCRIPTOR_INDEX);
        String writeValue = Tools.getData(json, Tools.WRITE_VALUE);
        String[] args = new String[] { deviceAddress, serviceIndex, characteristicIndex, writeValue };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        if (descriptorIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        byte[] value = Tools.decodeBase64(writeValue);
        if (mapWriteValueCallBack == null) {
            mapWriteValueCallBack = new HashMap<Object, CallbackContext>();
        }
        if ("".equals(descriptorIndex)) {
            int status = writeCharacteristic(device, serviceIndex, characteristicIndex, value, callbackContext);
            if (status == BleConstants.GATT_SUCCESS) {
                Tools.sendSuccessMsg(callbackContext);
            } else {
                Tools.sendErrorMsg(callbackContext);
            }
        } else {
            writeDescriptor(device, serviceIndex, characteristicIndex, descriptorIndex, value, callbackContext);
        }
    }

    @Override
    public void readValue(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "readValue");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String descriptorIndex = Tools.getData(json, Tools.DESCRIPTOR_INDEX);
        String[] args = new String[] { deviceAddress, serviceIndex, characteristicIndex };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        if (descriptorIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapReadValueCallBack == null) {
            mapReadValueCallBack = new HashMap<Object, CallbackContext>();
        }
        byte[] value = null;
        if ("".equals(descriptorIndex)) {
            value = readCharacteristic(device, serviceIndex, characteristicIndex, callbackContext);
        } else {
            value = readDescriptor(device, serviceIndex, characteristicIndex, descriptorIndex, callbackContext);
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
        Tools.addProperty(jsonObject, Tools.VALUE, Tools.encodeBase64(value));
        Tools.addProperty(jsonObject, Tools.DATE, Tools.getDateString());
        callbackContext.success(jsonObject);
    }

    @Override
    public void setNotification(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "setNotification");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String enable = Tools.getData(json, Tools.ENABLE);
        String[] args = new String[] { deviceAddress, serviceIndex, characteristicIndex, enable };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BleClientService bleClientService = getService(deviceAddress, serviceIndex);
        BleCharacteristic bleCharacteristic = getCharacteristic(deviceAddress, serviceIndex, characteristicIndex);
        BleDescriptor bleDescriptor = bleCharacteristic.getDescriptor(new BleGattID(Tools.NOTIFICATION_UUID));
        if ("true".equalsIgnoreCase(enable)) {
            bleClientService.registerForNotification(device, 0, bleCharacteristic.getID());
            bleDescriptor.setValue(ENABLE_NOTIFICATION_VALUE);
        } else {
            bleClientService.unregisterNotification(device, 0, bleCharacteristic.getID());
            bleDescriptor.setValue(DISABLE_NOTIFICATION_VALUE);
        }
        if (mapSetNotificationCallBack == null) {
            mapSetNotificationCallBack = new HashMap<Object, CallbackContext>();
        }
        bleDescriptor.setWriteType(BleConstants.GATTC_TYPE_WRITE);
        mapSetNotificationCallBack.put(bleCharacteristic, callbackContext);
        int result = bleClientService.writeCharacteristic(device, 0, bleCharacteristic);
        Log.i(TAG, "setNotification is " + (result == BleConstants.GATT_SUCCESS));
    }

    @Override
    public void getDeviceAllData(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getDeviceAllData");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (mapDeviceServices == null) {
            mapDeviceServices = new HashMap<String, List<BleClientService>>();
        }
        if (mapGetDeviceAllDataCallBack == null) {
            mapGetDeviceAllDataCallBack = new HashMap<String, CallbackContext>();
        }
        List<BleClientService> listServices = bleClientProfile.getAllServices();
        mapDeviceServices.put(deviceAddress, listServices);
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
        JSONArray services = new JSONArray();
        int index = 0;
        for (Iterator<BleClientService> it = listServices.iterator(); it.hasNext();) {
            BleClientService bleClientService = it.next();
            UUID uuid = bleClientService.getServiceId().getUuid();
            JSONObject service = new JSONObject();
            Tools.addProperty(service, Tools.SERVICE_INDEX, index++);
            Tools.addProperty(service, Tools.SERVICE_UUID, uuid);
            Tools.addProperty(service, Tools.SERVICE_NAME, Tools.lookup(uuid));
            services.put(service);
        }
        Tools.addProperty(jsonObject, Tools.SERVICES, services);
        int serviceLength = listServices.size();
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
            List<BleCharacteristic> bleCharacteristics = getService(deviceAddress, Integer.toString(i))
                    .getAllCharacteristics(device);
            int charaLength = bleCharacteristics.size();
            for (int j = 0; j < charaLength; j++) {
                JSONObject characteristic = new JSONObject();
                BleCharacteristic bleCharacteristic = bleCharacteristics.get(j);
                UUID uuid = bleCharacteristic.getID().getUuid();
                bleCharacteristic.setProperty(154);
                Tools.addProperty(characteristic, Tools.CHARACTERISTIC_UUID, uuid);
                Tools.addProperty(characteristic, Tools.CHARACTERISTIC_INDEX, j);
                Tools.addProperty(characteristic, Tools.CHARACTERISTIC_NAME, Tools.lookup(uuid));
                Tools.addProperty(characteristic, Tools.CHARACTERISTIC_PROPERTY,
                        Tools.decodeProperty(bleCharacteristic.getProperty()));
                JSONArray descriptors = new JSONArray();
                List<BleDescriptor> listDescriptors = bleCharacteristic.getAllDescriptors();
                for (int k = 0; k < listDescriptors.size(); k++) {
                    JSONObject descriptor = new JSONObject();
                    BleDescriptor bleDescriptor = listDescriptors.get(k);
                    UUID descriptorUUID = bleDescriptor.getID().getUuid();
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
        callbackContext.success(jsonObject);
    }

    @Override
    public void addServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "addService");
        JSONObject jsonServices = Tools.getObjectFromArray(json);
        JSONArray jsonArray = Tools.getArray(json, Tools.SERVICES);
        if (mapRemoteServices == null) {
            mapRemoteServices = new HashMap<String, BleServerService>();
        }
        addServiceCallBack = callbackContext;
        int serviceNumber = jsonServices.length();
        listServices = new ArrayList<BleServerService>();
        for (int i = 0; i < serviceNumber; i++) {
            String serviceIndex = Tools.getData(jsonArray, Tools.UINQUE_ID);
            String serviceType = Tools.getData(jsonArray, Tools.SERVICE_TYPE);
            String strServiceUUID = Tools.getData(jsonArray, Tools.SERVICE_UUID);
            String[] args = new String[] { serviceIndex, serviceType, strServiceUUID };
            if (!isNullOrEmpty(args, callbackContext)) {
                return;
            }
            BleServerService bleServerService = new BluetoothServerService(new BleGattID(strServiceUUID), i);
            bleServerService.createService();
            JSONArray jsonCharacteristics = Tools.getArray(jsonArray, Tools.CHARACTERISTICS);
            addCharacteristics(bleServerService, jsonCharacteristics, callbackContext);
            listServices.add(bleServerService);
        }
        BleServerProfile bleServerProfile = new BluetoothServiceProfile(context, new BleGattID(UUID.randomUUID()),
                listServices);
        bleServerProfile.startProfile();
    }

    private void addCharacteristics(BleServerService bleServerService, JSONArray jsonCharacteristics,
            CallbackContext callbackContext) {
        int characterLength = jsonCharacteristics.length();
        for (int j = 0; j < characterLength; j++) {
            String characteristicValueType = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_VALUE_TYPE);
            String characteristicValue = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_VALUE);
            String characteristicUUID = Tools.getData(jsonCharacteristics, j, Tools.CHARACTERISTIC_UUID);
            String[] args = new String[] { characteristicValueType, characteristicValue, characteristicUUID };
            if (!isNullOrEmpty(args, callbackContext)) {
                return;
            }
            int characterProperty = Tools.encodeProperty(Tools.getArray(jsonCharacteristics, j,
                    Tools.CHARACTERISTIC_PROPERTY));
            int characterPermission = Tools.encodePermission(Tools.getArray(jsonCharacteristics, j,
                    Tools.CHARACTERISTIC_PERMISSION));
            byte[] value = Tools.decodeBase64(characteristicValue);;
            BleCharacteristic bleCharacteristic = new BleCharacteristic(new BleGattID(characteristicUUID));
            bleCharacteristic.setHandle(j);
            bleCharacteristic.setProperty(characterProperty);
            bleCharacteristic.setPermission(characterPermission, 1);
            bleCharacteristic.setWriteType(BleConstants.GATTC_TYPE_WRITE);
            bleCharacteristic.setValue(value);
            JSONArray jsonDescriptors = Tools.getArray(jsonCharacteristics, j, Tools.DESCRIPTORS);
            addDescriptors(bleCharacteristic, jsonDescriptors, callbackContext);
            bleServerService.addCharacteristic(bleCharacteristic);
        }
    }

    private void addDescriptors(BleCharacteristic bleCharacteristic, JSONArray jsonDescriptors,
            CallbackContext callbackContext) {
        int descLength = jsonDescriptors.length();
        for (int k = 0; k < descLength; k++) {
            String descriptorValue = Tools.getData(jsonDescriptors, k, Tools.DESCRIPTOR_VALUE);
            String descriptorUUID = Tools.getData(jsonDescriptors, k, Tools.DESCRIPTOR_UUID);
            int descriptorsPermission = Tools.encodePermission(Tools.getArray(jsonDescriptors, k,Tools.DESCRIPTOR_PERMISSION));
            byte[] value = Tools.decodeBase64(descriptorValue);;
            BleDescriptor bleDescriptor = new BleDescriptor(new BleGattID(descriptorUUID));
            bleDescriptor.setHandle(k);
            bleDescriptor.setPermission(descriptorsPermission, k);
            bleDescriptor.setWriteType(BleConstants.GATTC_TYPE_WRITE);
            bleDescriptor.setValue(value);
            bleCharacteristic.addDescriptor(bleDescriptor);
        }
    }

    @Override
    public void removeServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "removeServices");
        if (listServices == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        if ("".equals(serviceIndex)) {
            for (BleServerService bleServerService : listServices) {
                bleServerService.deleteService();
            }
            listServices = new ArrayList<BleServerService>();
            Tools.sendSuccessMsg(callbackContext);
        } else {
            BleServerService bleServerService = listServices.get(Integer.parseInt(serviceIndex));
            bleServerService.deleteService();
            listServices.remove(bleServerService);
            Tools.sendSuccessMsg(callbackContext);
        }
    }

    @Override
    public void getRSSI(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "onReadRemoteRssi");
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        Short RSSI = null;
        if (mapRssiData != null) {
            RSSI = mapRssiData.get(deviceAddress);
        }
        if (RSSI != null) {
            JSONObject jsonObject = new JSONObject();
            Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
            Tools.addProperty(jsonObject, Tools.RSSI, Integer.toString(RSSI));
            callbackContext.success(jsonObject);
        } else {
            Tools.sendErrorMsg(callbackContext);
        }
    }

    @Override
    public void getServices(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getServices");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        if (deviceAddress == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        List<BleClientService> listServices = bleClientProfile.getAllServices();
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
        JSONArray services = new JSONArray();
        int index = 0;
        for (Iterator<BleClientService> it = listServices.iterator(); it.hasNext();) {
            BleClientService bluetoothGattService = it.next();
            UUID uuid = bluetoothGattService.getServiceId().getUuid();
            JSONObject service = new JSONObject();
            Tools.addProperty(service, Tools.SERVICE_INDEX, index++);
            Tools.addProperty(service, Tools.SERVICE_UUID, uuid);
            Tools.addProperty(service, Tools.SERVICE_NAME, Tools.lookup(uuid));
            services.put(service);
        }
        Tools.addProperty(jsonObject, Tools.SERVICES, services);
        callbackContext.success(jsonObject);
    }

    @Override
    public void getCharacteristics(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "getCharacteristics");
        if (!isInitialized(callbackContext)) {
            return;
        }
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String[] args = new String[] { deviceAddress, serviceIndex };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (serviceIndex == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
        JSONArray characteristics = new JSONArray();
        int size = mapDeviceServices.get(deviceAddress).get(Integer.parseInt(serviceIndex)).getAllCharacteristics(device)
                .size();
        for (int i = 0; i < size; i++) {
            BleCharacteristic bleCharacteristic = getCharacteristic(deviceAddress, serviceIndex, String.valueOf(i));
            bleCharacteristic.setProperty(154);
            UUID charateristicUUID = bleCharacteristic.getID().getUuid();
            JSONObject characteristic = new JSONObject();
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_INDEX, i);
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_UUID, charateristicUUID);
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_NAME, Tools.lookup(charateristicUUID));
            Tools.addProperty(characteristic, Tools.CHARACTERISTIC_PROPERTY,
                    Tools.decodeProperty(bleCharacteristic.getProperty()));
            Log.i(TAG, "property is " + bleCharacteristic.getProperty());
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
        String deviceAddress = Tools.getData(json, Tools.DEVICE_ADDRESS);
        String serviceIndex = Tools.getData(json, Tools.SERVICE_INDEX);
        String characteristicIndex = Tools.getData(json, Tools.CHARACTERISTIC_INDEX);
        String[] args = new String[] { deviceAddress, serviceIndex, characteristicIndex };
        if (!isNullOrEmpty(args, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
        JSONArray descriptors = new JSONArray();
        List<BleDescriptor> listBleDescriptors = getCharacteristic(deviceAddress, serviceIndex, characteristicIndex)
                .getAllDescriptors();
        int length = listBleDescriptors.size();
        for (int i = 0; i < length; i++) {
            UUID uuid = listBleDescriptors.get(i).getID().getUuid();
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
    public void addEventListener(JSONArray json, CallbackContext callbackContext) {
        Log.i(TAG, "addEventListener");
        String eventName = Tools.getData(json, Tools.EVENT_NAME);
        Log.i(TAG, "eventName is " + eventName);
        if (eventName == null) {
            Tools.sendErrorMsg(callbackContext);
            return;
        }
        if (mapAddListenerCallBack == null) {
            mapAddListenerCallBack = new HashMap<String, CallbackContext>();
        }
        mapAddListenerCallBack.put(eventName, callbackContext);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            String action = paramIntent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) paramIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = device.getAddress();
                short RSSI = paramIntent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -32768);
                if (BleAdapter.getDeviceType(device) == BleAdapter.DEVICE_TYPE_BLE
                        || BleAdapter.getDeviceType(device) == BleAdapter.DEVICE_TYPE_DUMO) {
                    if (!bluetoothDevices.contains(device)) {
                        Log.i(TAG, "deviceAddress " + device + " RSSI " + RSSI);
                        bluetoothDevices.add(device);
                        mapRssiData.put(deviceAddress, RSSI);
                    }
                }
            }
        }
    };

    private boolean isInitialized(CallbackContext callbackContext) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Tools.sendErrorMsg(callbackContext);
            return false;
        }
        return true;
    }

    private class BluetoothClientProfile extends BleClientProfile {

        private Context context;
        private List<BleGattID> peerServices;
        private ArrayList<BleClientService> listServices;

        public BluetoothClientProfile(Context context) {
            super(context, new BleGattID(UUID.randomUUID()));
            this.context = context;
        }

        @Override
        public void onInitialized(boolean success) {
            super.onInitialized(success);
        }

        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            Log.i(TAG, "onDeviceConnected");
            super.onDeviceConnected(device);
            String deviceAddress = device.getAddress();
            peerServices = getPeerServices();
            if (listServices == null) {
                listServices = (ArrayList<BleClientService>) mapDeviceServices.get(deviceAddress);
            }
            if (peerServices != null) {
                if (bleClientProfile == null) {
                    listServices = new ArrayList<BleClientService>();
                    for (BleGattID aServiceID : peerServices) {
                        undefinedService = new BluetoothClientService(aServiceID.getUuid().toString());
                        listServices.add(undefinedService);
                    }
                } else {
                    undefinedService = null;
                }

                if (mapDeviceServices == null) {
                    mapDeviceServices = new HashMap<String, List<BleClientService>>();
                }
                mapDeviceServices.put(deviceAddress, listServices);

                if (undefinedService != null) {
                    bluetoothClientProfile.deregisterProfile();
                    bleClientProfile = new BluetoothClientProfile(context);
                    bleClientProfile.init(listServices, null);
                    BluetoothHTC41.connectDevice = device;
                }

            }

            CallbackContext callbackContext = null;
            if (mapConnectCallBack != null) {
                callbackContext = mapConnectCallBack.get(deviceAddress);
            }
            if (callbackContext != null && undefinedService == null) {
                JSONObject jsonObject = new JSONObject();
                Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
                Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                callbackContext.success(jsonObject);
            }
        }

        @Override
        public void onProfileRegistered() {
            Log.i(TAG, "onProfileRegistered");
            super.onProfileRegistered();
            if (undefinedService != null) {
                Log.i(TAG, "reconnect the device");
                bleClientProfile.connect(connectDevice);
            }
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device) {
            Log.i(TAG, "onDeviceDisconnected");
            super.onDeviceDisconnected(device);
            String deviceAddress = device.getAddress();
            Log.i(TAG, "device: " + deviceAddress + " disconnect!");
            CallbackContext callbackContext = null;
            if(mapDisconnectCallBack != null){
                callbackContext = mapDisconnectCallBack.get(deviceAddress);
            }
            if (callbackContext == null && mapAddListenerCallBack != null) {
                callbackContext = mapAddListenerCallBack.get(Tools.DISCONNECT);
            }
            if (callbackContext != null) {
                JSONObject jsonObject = new JSONObject();
                Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
                Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        }

        @SuppressWarnings("unchecked")
        private ArrayList<BleGattID> getPeerServices() {
            try {
                Field field = BleClientProfile.class.getDeclaredField("mPeerServices");
                field.setAccessible(true);
                Object value = field.get(this);
                field.setAccessible(false);
                if (value == null) {
                    return null;
                }
                return (ArrayList<BleGattID>) value;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public void initServices() {
            Log.i(TAG, "initServices");
            BleClientService bleClientService = new BluetoothClientService(GENERIC_ACCESS);
            listServices = new ArrayList<BleClientService>();
            listServices.add(bleClientService);
            init(listServices, null);
        }

        public ArrayList<BleClientService> getAllServices() {
            return listServices;
        }

    }

    private class BluetoothClientService extends BleClientService {

        private static final String TAG = "BluetoothClientService";

        public BluetoothClientService(String serviceUUID) {
            super(new BleGattID(serviceUUID));
        }

        @Override
        public void onReadCharacteristicComplete(int status, BluetoothDevice remoteDevice,
                BleCharacteristic characteristic) {
            Log.i(TAG, "onReadCharacteristicComplete");
            super.onReadCharacteristicComplete(status, remoteDevice, characteristic);
            System.out.println(new String(characteristic.getValue()));
        }

        @Override
        public void onWriteCharacteristicComplete(int status, BluetoothDevice remoteDevice,
                BleCharacteristic characteristic) {
            Log.i(TAG, "onWriteCharacteristicComplete");
            super.onWriteCharacteristicComplete(status, remoteDevice, characteristic);
            CallbackContext callbackContext = null;
            if (mapWriteValueCallBack != null) {
                callbackContext = mapWriteValueCallBack.get(characteristic);
            }
            if (callbackContext != null) {
                if (status == BleConstants.GATT_SUCCESS) {
                    Tools.sendSuccessMsg(callbackContext);
                } else {
                    Tools.sendErrorMsg(callbackContext);
                }
                mapWriteValueCallBack.remove(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothDevice device, BleCharacteristic characteristic) {
            super.onCharacteristicChanged(device, characteristic);
            Log.i(TAG, "onCharacteristicChanged");
            String deviceAddress = device.getAddress();
            CallbackContext callbackContext = null;
            if (mapSetNotificationCallBack != null) {
                callbackContext = mapSetNotificationCallBack.get(characteristic);
            }
            if (callbackContext != null) {
                JSONObject jsonObject = new JSONObject();
                Tools.addProperty(jsonObject, Tools.DEVICE_ADDRESS, deviceAddress);
                Tools.addProperty(jsonObject, Tools.VALUE, Tools.encodeBase64(characteristic.getValue()));
                Tools.addProperty(jsonObject, Tools.DATE, Tools.getDateString());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        }

        @Override
        public void onReadCharacteristicComplete(BluetoothDevice device, BleCharacteristic characteristic) {
            Log.i(TAG, "onReadCharacteristicComplete");
            super.onReadCharacteristicComplete(device, characteristic);
            System.out.println(new String(characteristic.getValue()));
        }

        @Override
        public int readCharacteristic(BluetoothDevice device, BleCharacteristic characteristic) {
            return super.readCharacteristic(device, characteristic);
        }

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

    private BleDescriptor getDescriptor(String deviceAddress, String serviceIndex, String characteristicIndex,
            String descriptorIndex) {
        return getCharacteristic(deviceAddress, serviceIndex, characteristicIndex).getAllDescriptors().get(
                Integer.parseInt(descriptorIndex));
    }

    private BleCharacteristic getCharacteristic(String deviceAddress, String serviceIndex, String characteristicIndex) {
        return getService(deviceAddress, serviceIndex).getAllCharacteristics(bluetoothAdapter.getRemoteDevice(deviceAddress))
                .get(Integer.parseInt(characteristicIndex));
    }

    private BleClientService getService(String deviceAddress, String serviceIndex) {
        return mapDeviceServices.get(deviceAddress).get(Integer.parseInt(serviceIndex));
    }

    private int writeCharacteristic(BluetoothDevice device, String serviceIndex, String characteristicIndex,
            byte[] value, CallbackContext callbackContext) {
        String deviceAddress = device.getAddress();
        BleClientService bleClientService = getService(deviceAddress, serviceIndex);
        BleCharacteristic bleCharacteristic = getCharacteristic(deviceAddress, serviceIndex, characteristicIndex);
        bleCharacteristic.setValue(value);
        bleCharacteristic.setWriteType(BleConstants.GATTC_TYPE_WRITE);
        return bleClientService.writeCharacteristic(device, 0, bleCharacteristic);
    }

    private int writeDescriptor(BluetoothDevice device, String serviceIndex, String characteristicIndex,
            String descriptorIndex, byte[] value, CallbackContext callbackContext) {
        String deviceAddress = device.getAddress();
        BleClientService bleClientService = getService(deviceAddress, serviceIndex);
        BleCharacteristic bleCharacteristic = getCharacteristic(deviceAddress, serviceIndex, characteristicIndex);
        BleDescriptor bleDescriptor = getDescriptor(deviceAddress, serviceIndex, characteristicIndex, descriptorIndex);
        mapWriteValueCallBack.put(bleDescriptor, callbackContext);
        bleDescriptor.setWriteType(BleConstants.GATTC_TYPE_WRITE);
        bleDescriptor.setValue(value);
        return bleClientService.writeCharacteristic(device, 0, bleCharacteristic);
    }

    private byte[] readCharacteristic(BluetoothDevice device, String serviceIndex, String characteristicIndex,
            CallbackContext callbackContext) {
        String deviceAddress = device.getAddress();
        BleClientService bleClientService = getService(deviceAddress, serviceIndex);
        BleCharacteristic bleCharacteristic = getCharacteristic(deviceAddress, serviceIndex, characteristicIndex);
        mapReadValueCallBack.put(bleCharacteristic, callbackContext);
        bleClientService.readCharacteristic(device, bleCharacteristic);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bleCharacteristic.getValue();
    }

    private byte[] readDescriptor(BluetoothDevice device, String serviceIndex, String characteristicIndex,
            String descriptorIndex, CallbackContext callbackContext) {
        String deviceAddress = device.getAddress();
        BleDescriptor bleDescriptor = getDescriptor(deviceAddress, serviceIndex, characteristicIndex, descriptorIndex);
        return bleDescriptor.getValue();
    }

    private class BluetoothServiceProfile extends BleServerProfile {

        public BluetoothServiceProfile(Context context, BleGattID appId, ArrayList<BleServerService> lists) {
            super(context, appId, lists);
        }

        @Override
        public void onClientConnected(String arg0, boolean arg1) {

        }

        @Override
        public void onCloseCompleted(int arg0) {

        }

        @Override
        public void onInitialized(boolean arg0) {

        }

        @Override
        public void onOpenCancelCompleted(int arg0) {

        }

        @Override
        public void onOpenCompleted(int arg0) {

        }

        @Override
        public void onStarted(boolean success) {
            if (success) {
                Tools.sendSuccessMsg(addServiceCallBack);
            }
        }

        @Override
        public void onStopped() {

        }

    }

    private class BluetoothServerService extends BleServerService {

        public BluetoothServerService(BleGattID serviceId, int numHandles) {
            super(serviceId, numHandles);
        }

        public BluetoothServerService(BleGattID serviceId, byte supTransport, int numHandles) {
            super(serviceId, supTransport, numHandles);
        }

    }
}
