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

package org.bcsphere.bluetooth.tools;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.samsung.android.sdk.bt.gatt.BluetoothGattCharacteristic;


@SuppressLint({ "UseSparseArrays", "SimpleDateFormat", "DefaultLocale" })
public class Tools {
	public static final String ADVERTISEMENT_DATA = "advertisementData";
	public static final String BLUETOOTH_CLOSE = "bluetoothclose";
	public static final String BLUETOOTH_OPEN = "bluetoothopen";
	public static final String BLUETOOTH_STATE = "state";
	public static final String CHARACTERISTIC_INDEX = "characteristicIndex";
	public static final String CHARACTERISTIC_NAME = "characteristicName";
	public static final String CHARACTERISTIC_PERMISSION = "characteristicPermission";
	public static final String CHARACTERISTIC_PROPERTY = "characteristicProperty";
	public static final String CHARACTERISTIC_UUID = "characteristicUUID";
	public static final String CHARACTERISTIC_UUIDS = "characteristicUUIDs";
	public static final String CHARACTERISTIC_VALUE_TYPE = "characteristicValueType";
	public static final String CHARACTERISTIC_VALUE = "characteristicValue";
	public static final String CHARACTERISTICS = "characteristics";
	public static final String DATE = "date";
	public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss:SSS";
	public static final String DESCRIPTOR_INDEX = "descriptorIndex";
	public static final String DESCRIPTOR_NAME = "descriptorName";
	public static final String DESCRIPTOR_PERMISSION = "descriptorPermission";
	public static final String DESCRIPTOR_UUID = "descriptorUUID";
	public static final String DESCRIPTOR_VALUE = "descriptorValue";
	public static final String DESCRIPTOR_VALUE_TYPE = "descriptorValueType";
	public static final String DESCRIPTORS = "descriptors";
	public static final String DEVICE_ID = "deviceID";
	public static final String DEVICE_NAME = "deviceName";
	public static final String DISCONNECT = "disconnect";
	public static final String ENABLE = "enable";
	public static final String EVENT_NAME = "eventName";
	public static final String IS_CONNECTED = "isConnected";
	public static final String IS_FALSE = "false";
	public static final String IS_TRUE = "true";
	public static final String ON_READ_REQUEST = "onReadRequest";
	public static final String ON_WRITE_REQUEST = "onWriteRequest";
	public static final String PERMISSION_READ = "read";
	public static final String PERMISSION_READ_ENCRYPTED = "readEncrypted";
	public static final String PERMISSION_READ_ENCRYPTED_MITM = "readEncryptedMitm";
	public static final String PERMISSION_WRITE = "write";
	public static final String PERMISSION_WRITE_ENCRYPTED_MITM = "writeEncryptedMitm";
	public static final String PERMISSION_WRITE_ENCRYPTED = "writeEncrypted";
	public static final String PERMISSION_WRITE_SIGEND = "writeSigend";
	public static final String PERMISSION_WRITE_SIGEND_MITM = "writeSigendMitm";
	public static final String PROPERTY_SIGNED_WRITE = "authenticatedSignedWrites";
	public static final String PROPERTY_BROADCAST = "broadcast";
	public static final String PROPERTY_EXTENDED_PROPS = "extendedProperties";
	public static final String PROPERTY_INDICATE = "indicate";
	public static final String PROPERTY_NOTIFY = "notify";
	public static final String PROPERTY_READ = "read";
	public static final String PROPERTY_WRITE = "write";
	public static final String PROPERTY_WRITE_NO_RESPONSE = "writeWithoutResponse";
	public static final String RSSI = "RSSI";
	public static final String SERVICE_INDEX = "serviceIndex";
	public static final String SERVICE_NAME = "serviceName";
	public static final String SERVICE_PACKET = "servicePacket";
	public static final String SERVICE_TYPE = "serviceType";
	public static final String SERVICE_UUID = "serviceUUID";
	public static final String SERVICE_UUIDS = "serviceUUIDs";
	public static final String SERVICES = "services";
	public static final String UINQUE_ID = "uniqueID";
	public static final String VALUE = "value";
	public static final String WRITE_TYPE = "writeType";
	public static final String WRITE_VALUE = "writeValue";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String MES = "mes";
	public static final UUID NOTIFICATION_UUID = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID GENERIC_ACCESS_UUID = UUID
			.fromString("00001800-0000-1000-8000-00805f9b34fb");
	public static final UUID GENERIC_ATTRIBUTE_UUID = UUID
			.fromString("00001801-0000-1000-8000-00805f9b34fb");
	public static final String LOCAL_NAME = "localName";
	public static final String TXPOWER_LEVEL = "txPowerLevel";
	public static final String SERVICE_DATA = "serviceData";
	public static final String MANUFACTURER_DATA = "manufacturerData";
	public static final String OVERFLOW_SERVICE_UUIDS = "overflowServiceUUIDs";
	public static final String ISCONNECTABLE = "isConnectable";
	public static final String SOLICITED_SERVICE_UUIDS = "solicitedServiceUUIDs";

	private static final String REMOVE_BOND = "removeBond";
	private static final String CREATE_BOND = "createBond";
	private static final String UNKNOWN = "unknown";

	private static HashMap<Integer, String> propertys = new HashMap<Integer, String>();
	static {
		propertys.put(1, PROPERTY_BROADCAST);
		propertys.put(2, PROPERTY_READ);
		propertys.put(4, PROPERTY_WRITE_NO_RESPONSE);
		propertys.put(8, PROPERTY_WRITE);
		propertys.put(16, PROPERTY_NOTIFY);
		propertys.put(32, PROPERTY_INDICATE);
		propertys.put(64, PROPERTY_SIGNED_WRITE);
		propertys.put(128, PROPERTY_EXTENDED_PROPS);
	}

	private static HashMap<String, String> UUIDInstructions = new HashMap<String, String>();
	static {
		UUIDInstructions.put("00001800-0000-1000-8000-00805f9b34fb",
				"Generic Access");
		UUIDInstructions.put("00001801-0000-1000-8000-00805f9b34fb",
				"Generic Attribute");
		UUIDInstructions.put("00001802-0000-1000-8000-00805f9b34fb",
				"Immediate Alert");
		UUIDInstructions.put("00001803-0000-1000-8000-00805f9b34fb",
				"Link Loss");
		UUIDInstructions
				.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
		UUIDInstructions.put("00001805-0000-1000-8000-00805f9b34fb",
				"Current Time Service");
		UUIDInstructions.put("00001806-0000-1000-8000-00805f9b34fb",
				"Reference Time Update Service");
		UUIDInstructions.put("00001807-0000-1000-8000-00805f9b34fb",
				"Next DST Change Service");
		UUIDInstructions.put("00001808-0000-1000-8000-00805f9b34fb", "Glucose");
		UUIDInstructions.put("00001809-0000-1000-8000-00805f9b34fb",
				"Health Thermometer");
		UUIDInstructions.put("0000180a-0000-1000-8000-00805f9b34fb",
				"Device Information");
		UUIDInstructions.put("0000180b-0000-1000-8000-00805f9b34fb",
				"Network Availability Service");
		UUIDInstructions
				.put("0000180c-0000-1000-8000-00805f9b34fb", "Watchdog");
		UUIDInstructions.put("0000180d-0000-1000-8000-00805f9b34fb",
				"Heart Rate");
		UUIDInstructions.put("0000180e-0000-1000-8000-00805f9b34fb",
				"Phone Alert Status Service");
		UUIDInstructions.put("0000180f-0000-1000-8000-00805f9b34fb",
				"Battery Service");
		UUIDInstructions.put("00001810-0000-1000-8000-00805f9b34fb",
				"Blood Pressure");
		UUIDInstructions.put("00001811-0000-1000-8000-00805f9b34fb",
				"Alert Notification Service");
		UUIDInstructions.put("00001812-0000-1000-8000-00805f9b34fb",
				"Human Interface Device");
		UUIDInstructions.put("00001813-0000-1000-8000-00805f9b34fb",
				"Scan Parameters");
		UUIDInstructions.put("00001814-0000-1000-8000-00805f9b34fb",
				"RUNNING SPEED AND CADENCE");
		UUIDInstructions.put("00001815-0000-1000-8000-00805f9b34fb",
				"Automation IO");
		UUIDInstructions.put("00001816-0000-1000-8000-00805f9b34fb",
				"Cycling Speed and Cadence");
		UUIDInstructions.put("00001817-0000-1000-8000-00805f9b34fb",
				"Pulse Oximeter");
		UUIDInstructions.put("00001818-0000-1000-8000-00805f9b34fb",
				"Cycling Power Service");
		UUIDInstructions.put("00001819-0000-1000-8000-00805f9b34fb",
				"Location and Navigation Service");
		UUIDInstructions.put("0000181a-0000-1000-8000-00805f9b34fb",
				"Continous Glucose Measurement Service");
		UUIDInstructions.put("00002a00-0000-1000-8000-00805f9b34fb",
				"Device Name");
		UUIDInstructions.put("00002a01-0000-1000-8000-00805f9b34fb",
				"Appearance");
		UUIDInstructions.put("00002a02-0000-1000-8000-00805f9b34fb",
				"Peripheral Privacy Flag");
		UUIDInstructions.put("00002a03-0000-1000-8000-00805f9b34fb",
				"Reconnection Address");
		UUIDInstructions.put("00002a04-0000-1000-8000-00805f9b34fb",
				"Peripheral Preferred Connection Parameters");
		UUIDInstructions.put("00002a05-0000-1000-8000-00805f9b34fb",
				"Service Changed");
		UUIDInstructions.put("00002a06-0000-1000-8000-00805f9b34fb",
				"Alert Level");
		UUIDInstructions.put("00002a07-0000-1000-8000-00805f9b34fb",
				"Tx Power Level");
		UUIDInstructions.put("00002a08-0000-1000-8000-00805f9b34fb",
				"Date Time");
		UUIDInstructions.put("00002a09-0000-1000-8000-00805f9b34fb",
				"Day of Week");
		UUIDInstructions.put("00002a0a-0000-1000-8000-00805f9b34fb",
				"Day Date Time");
		UUIDInstructions.put("00002a0b-0000-1000-8000-00805f9b34fb",
				"Exact Time 100");
		UUIDInstructions.put("00002a0c-0000-1000-8000-00805f9b34fb",
				"Exact Time 256");
		UUIDInstructions.put("00002a0d-0000-1000-8000-00805f9b34fb",
				"DST Offset");
		UUIDInstructions.put("00002a0e-0000-1000-8000-00805f9b34fb",
				"Time Zone");
		UUIDInstructions.put("00002a1f-0000-1000-8000-00805f9b34fb",
				"Local Time Information");
		UUIDInstructions.put("00002a10-0000-1000-8000-00805f9b34fb",
				"Secondary Time Zone");
		UUIDInstructions.put("00002a11-0000-1000-8000-00805f9b34fb",
				"Time with DST");
		UUIDInstructions.put("00002a12-0000-1000-8000-00805f9b34fb",
				"Time Accuracy");
		UUIDInstructions.put("00002a13-0000-1000-8000-00805f9b34fb",
				"Time Source");
		UUIDInstructions.put("00002a14-0000-1000-8000-00805f9b34fb",
				"Reference Time Information");
		UUIDInstructions.put("00002a15-0000-1000-8000-00805f9b34fb",
				"Time Broadcast");
		UUIDInstructions.put("00002a16-0000-1000-8000-00805f9b34fb",
				"Time Update Control Point");
		UUIDInstructions.put("00002a17-0000-1000-8000-00805f9b34fb",
				"Time Update State");
		UUIDInstructions.put("00002a18-0000-1000-8000-00805f9b34fb",
				"Glucose Measurement");
		UUIDInstructions.put("00002a19-0000-1000-8000-00805f9b34fb",
				"Battery Level");
		UUIDInstructions.put("00002a1a-0000-1000-8000-00805f9b34fb",
				"Battery Power State");
		UUIDInstructions.put("00002a1b-0000-1000-8000-00805f9b34fb",
				"Battery Level State");
		UUIDInstructions.put("00002a1c-0000-1000-8000-00805f9b34fb",
				"Temperature Measurement");
		UUIDInstructions.put("00002a1d-0000-1000-8000-00805f9b34fb",
				"Temperature Type");
		UUIDInstructions.put("00002a1e-0000-1000-8000-00805f9b34fb",
				"Intermediate Temperature");
		UUIDInstructions.put("00002a1f-0000-1000-8000-00805f9b34fb",
				"Temperature in Celsius");
		UUIDInstructions.put("00002a20-0000-1000-8000-00805f9b34fb",
				"Temperature in Fahrenheit");
		UUIDInstructions.put("00002a21-0000-1000-8000-00805f9b34fb",
				"Measurement Interval");
		UUIDInstructions.put("00002a22-0000-1000-8000-00805f9b34fb",
				"Boot Keyboard Input Report");
		UUIDInstructions.put("00002a23-0000-1000-8000-00805f9b34fb",
				"System ID");
		UUIDInstructions.put("00002a24-0000-1000-8000-00805f9b34fb",
				"Model Number String");
		UUIDInstructions.put("00002a25-0000-1000-8000-00805f9b34fb",
				"Serial Number String");
		UUIDInstructions.put("00002a26-0000-1000-8000-00805f9b34fb",
				"Firmware Revision String");
		UUIDInstructions.put("00002a27-0000-1000-8000-00805f9b34fb",
				"Hardware Revision String");
		UUIDInstructions.put("00002a28-0000-1000-8000-00805f9b34fb",
				"Software Revision String");
		UUIDInstructions.put("00002a29-0000-1000-8000-00805f9b34fb",
				"Manufacturer Name String");
		UUIDInstructions.put("00002a2a-0000-1000-8000-00805f9b34fb",
				"IEEE 11073-20601 Regulatory Certification Data List");
		UUIDInstructions.put("00002a2b-0000-1000-8000-00805f9b34fb",
				"Current Time");
		UUIDInstructions.put("00002a2c-0000-1000-8000-00805f9b34fb",
				"Elevation");
		UUIDInstructions
				.put("00002a2d-0000-1000-8000-00805f9b34fb", "Latitude");
		UUIDInstructions.put("00002a2e-0000-1000-8000-00805f9b34fb",
				"Longitude");
		UUIDInstructions.put("00002a2f-0000-1000-8000-00805f9b34fb",
				"Position 2D");
		UUIDInstructions.put("00002a30-0000-1000-8000-00805f9b34fb",
				"Position 3D");
		UUIDInstructions.put("00002a31-0000-1000-8000-00805f9b34fb",
				"Scan Refresh");
		UUIDInstructions.put("00002a32-0000-1000-8000-00805f9b34fb",
				"Boot Keyboard Output Report");
		UUIDInstructions.put("00002a33-0000-1000-8000-00805f9b34fb",
				"Boot Mouse Input Report");
		UUIDInstructions.put("00002a34-0000-1000-8000-00805f9b34fb",
				"Glucose Measurement Context");
		UUIDInstructions.put("00002a35-0000-1000-8000-00805f9b34fb",
				"Blood Pressure Measurement");
		UUIDInstructions.put("00002a36-0000-1000-8000-00805f9b34fb",
				"Intermediate Cuff Pressure");
		UUIDInstructions.put("00002a37-0000-1000-8000-00805f9b34fb",
				"Heart Rate Measurement");
		UUIDInstructions.put("00002a38-0000-1000-8000-00805f9b34fb",
				"Body Sensor Location");
		UUIDInstructions.put("00002a39-0000-1000-8000-00805f9b34fb",
				"Heart Rate Control Point");
		UUIDInstructions.put("00002a3a-0000-1000-8000-00805f9b34fb",
				"Removable");
		UUIDInstructions.put("00002a3b-0000-1000-8000-00805f9b34fb",
				"Service Required");
		UUIDInstructions.put("00002a3c-0000-1000-8000-00805f9b34fb",
				"Scientific Temperature in Celsius");
		UUIDInstructions.put("00002a3d-0000-1000-8000-00805f9b34fb", "String");
		UUIDInstructions.put("00002a3e-0000-1000-8000-00805f9b34fb",
				"Network Availability");
		UUIDInstructions.put("00002a3g-0000-1000-8000-00805f9b34fb",
				"Alert Status");
		UUIDInstructions.put("00002a40-0000-1000-8000-00805f9b34fb",
				"Ringer Control Point");
		UUIDInstructions.put("00002a41-0000-1000-8000-00805f9b34fb",
				"Ringer Setting");
		UUIDInstructions.put("00002a42-0000-1000-8000-00805f9b34fb",
				"Alert Category ID Bit Mask");
		UUIDInstructions.put("00002a43-0000-1000-8000-00805f9b34fb",
				"Alert Category ID");
		UUIDInstructions.put("00002a44-0000-1000-8000-00805f9b34fb",
				"Alert Notification Control Point");
		UUIDInstructions.put("00002a45-0000-1000-8000-00805f9b34fb",
				"Unread Alert Status");
		UUIDInstructions.put("00002a46-0000-1000-8000-00805f9b34fb",
				"New Alert");
		UUIDInstructions.put("00002a47-0000-1000-8000-00805f9b34fb",
				"Supported New Alert Category");
		UUIDInstructions.put("00002a48-0000-1000-8000-00805f9b34fb",
				"Supported Unread Alert Category");
		UUIDInstructions.put("00002a49-0000-1000-8000-00805f9b34fb",
				"Blood Pressure Feature");
		UUIDInstructions.put("00002a4a-0000-1000-8000-00805f9b34fb",
				"HID Information");
		UUIDInstructions.put("00002a4b-0000-1000-8000-00805f9b34fb",
				"Report Map");
		UUIDInstructions.put("00002a4c-0000-1000-8000-00805f9b34fb",
				"HID Control Point");
		UUIDInstructions.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report");
		UUIDInstructions.put("00002a4e-0000-1000-8000-00805f9b34fb",
				"Protocol Mode");
		UUIDInstructions.put("00002a4g-0000-1000-8000-00805f9b34fb",
				"Scan Interval Window");
		UUIDInstructions.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
		UUIDInstructions.put("00002a51-0000-1000-8000-00805f9b34fb",
				"Glucose Features");
		UUIDInstructions.put("00002a52-0000-1000-8000-00805f9b34fb",
				"Record Access Control Point");
		UUIDInstructions.put("00002a53-0000-1000-8000-00805f9b34fb",
				"RSC Measurement");
		UUIDInstructions.put("00002a54-0000-1000-8000-00805f9b34fb",
				"RSC Feature");
		UUIDInstructions.put("00002a55-0000-1000-8000-00805f9b34fb",
				"SC Control Point");
		UUIDInstructions.put("00002a56-0000-1000-8000-00805f9b34fb",
				"Digital Input");
		UUIDInstructions.put("00002a57-0000-1000-8000-00805f9b34fb",
				"Digital Output");
		UUIDInstructions.put("00002a58-0000-1000-8000-00805f9b34fb",
				"Analog Input");
		UUIDInstructions.put("00002a59-0000-1000-8000-00805f9b34fb",
				"Analog Output");
		UUIDInstructions.put("00002a5a-0000-1000-8000-00805f9b34fb",
				"Aggregate Input");
		UUIDInstructions.put("00002a5b-0000-1000-8000-00805f9b34fb",
				"CSC Measurement");
		UUIDInstructions.put("00002a5c-0000-1000-8000-00805f9b34fb",
				"CSC Feature");
		UUIDInstructions.put("00002a5d-0000-1000-8000-00805f9b34fb",
				"Sensor Location");
		UUIDInstructions.put("00002a5e-0000-1000-8000-00805f9b34fb",
				"Pulse Oximetry Spot-check Measurement");
		UUIDInstructions.put("00002a5f-0000-1000-8000-00805f9b34fb",
				"Pulse Oximetry Continuous Measurement");
		UUIDInstructions.put("00002a60-0000-1000-8000-00805f9b34fb",
				"Pulse Oximetry Pulsatile Event");
		UUIDInstructions.put("00002a61-0000-1000-8000-00805f9b34fb",
				"Pulse Oximetry Features");
		UUIDInstructions.put("00002a62-0000-1000-8000-00805f9b34fb",
				"Pulse Oximetry Control Point");
		UUIDInstructions.put("00002a63-0000-1000-8000-00805f9b34fb",
				"Cycling Power Measurement Characteristic");
		UUIDInstructions.put("00002a64-0000-1000-8000-00805f9b34fb",
				"Cycling Power Vector Characteristic");
		UUIDInstructions.put("00002a65-0000-1000-8000-00805f9b34fb",
				"Cycling Power Feature Characteristic");
		UUIDInstructions.put("00002a66-0000-1000-8000-00805f9b34fb",
				"Cycling Power Control Point Characteristic");
		UUIDInstructions.put("00002a67-0000-1000-8000-00805f9b34fb",
				"Location and Speed Characteristic");
		UUIDInstructions.put("00002a68-0000-1000-8000-00805f9b34fb",
				"Navigation Characteristic");
		UUIDInstructions.put("00002a69-0000-1000-8000-00805f9b34fb",
				"Position Quality Characteristic");
		UUIDInstructions.put("00002a6a-0000-1000-8000-00805f9b34fb",
				"LN Feature Characteristic");
		UUIDInstructions.put("00002a6b-0000-1000-8000-00805f9b34fb",
				"LN Control Point Characteristic");
		UUIDInstructions.put("00002a6c-0000-1000-8000-00805f9b34fb",
				"CGM Measurement Characteristic");
		UUIDInstructions.put("00002a6d-0000-1000-8000-00805f9b34fb",
				"CGM Features Characteristic");
		UUIDInstructions.put("00002a6e-0000-1000-8000-00805f9b34fb",
				"CGM Status Characteristic");
		UUIDInstructions.put("00002a6f-0000-1000-8000-00805f9b34fb",
				"CGM Session Start Time Characteristic");
		UUIDInstructions.put("00002a70-0000-1000-8000-00805f9b34fb",
				"Application Security Point Characteristic");
		UUIDInstructions.put("00002a71-0000-1000-8000-00805f9b34fb",
				"CGM Specific Ops Control Point Characteristic");
	}

	public static String lookup(int propertySum, int property) {
		if ((propertySum & property) == property) {
			String propertyName = propertys.get(property);
			return propertyName == null ? null : propertyName;
		} else {
			return null;
		}
	}

	public static String lookup(UUID uuid) {
		String instruction = UUIDInstructions.get(uuid.toString());
		return instruction == null ? UNKNOWN : instruction;
	}

	public static String getOSVersionNumber() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}

	public static String getPhoneManufacturers() {
		return android.os.Build.MANUFACTURER;
	}

	public static String getPhoneBrand() {
		return android.os.Build.BRAND;
	}

	public static String getPhoneBasebandVersion() {
		try {
			Class<?> cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class,
					String.class });
			Object result = m.invoke(invoker, new Object[] {
					"gsm.version.baseband", "no message" });
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean creatBond(Class<?> btcClass, BluetoothDevice device)
			throws Exception {
		Method creatBondMethod = btcClass.getMethod(CREATE_BOND);
		Boolean returnValue = (Boolean) creatBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	static public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod(REMOVE_BOND);
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	static public boolean isSupportBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			return false;
		} else {
			return true;
		}
	}

	static public boolean isBLE(Context context) {
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		} else {
			return true;
		}
	}

	static public boolean isOpenBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter.isEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	static public int encodeProperty(JSONArray ary) {
		int property = 0;
		if (PROPERTY_BROADCAST.equals(getDataFromArray(ary, PROPERTY_BROADCAST))) {
			property = property + 1;
		}
		if (PROPERTY_READ.equals(getDataFromArray(ary, PROPERTY_READ))) {
			property = property + 2;
		}
		if (PROPERTY_WRITE_NO_RESPONSE.equals(getDataFromArray(ary, PROPERTY_WRITE_NO_RESPONSE))) {
			property = property + 4;
		}
		if (PROPERTY_WRITE.equals(getDataFromArray(ary, PROPERTY_WRITE))) {
			property = property + 8;
		}
		if (PROPERTY_NOTIFY.equals(getDataFromArray(ary, PROPERTY_NOTIFY))) {
			property = property + 16;
		}
		if (PROPERTY_INDICATE.equals(getDataFromArray(ary, PROPERTY_INDICATE))) {
			property = property + 32;
		}
		if (PROPERTY_WRITE_NO_RESPONSE.equals(getDataFromArray(ary, PROPERTY_WRITE_NO_RESPONSE))) {
			property = property + 64;
		}
		if (PROPERTY_EXTENDED_PROPS.equals(getDataFromArray(ary, PROPERTY_EXTENDED_PROPS))) {
			property = property + 128;
		}
		return property;
	}

	public static JSONArray decodeProperty(int property) {
		JSONArray properties = new JSONArray();
		String strProperty = null;
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_BROADCAST)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_READ)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_WRITE)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_NOTIFY)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_INDICATE)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)) != null) {
			properties.put(strProperty);
		}
		if ((strProperty = lookup(property,
				BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS)) != null) {
			properties.put(strProperty);
		}
		return properties;
	}

	static public int encodePermission(JSONArray ary) {
		int permission = 0;
		if (PERMISSION_READ.equals(getDataFromArray(ary, PERMISSION_READ))) {
			permission = permission + 1;
		}
		if (PERMISSION_READ_ENCRYPTED.equals(getDataFromArray(ary, PERMISSION_READ_ENCRYPTED))) {
			permission = permission + 2;
		}
		if (PERMISSION_READ_ENCRYPTED_MITM.equals(getDataFromArray(ary, PERMISSION_READ_ENCRYPTED_MITM))) {
			permission = permission + 4;
		}
		if (PERMISSION_WRITE.equals(getDataFromArray(ary, PERMISSION_WRITE))) {
			permission = permission + 16;
		}
		if (PERMISSION_WRITE_ENCRYPTED.equals(getDataFromArray(ary, PERMISSION_WRITE_ENCRYPTED))) {
			permission = permission + 32;
		}
		if (PERMISSION_WRITE_ENCRYPTED_MITM.equals(getDataFromArray(ary, PERMISSION_WRITE_ENCRYPTED_MITM))) {
			permission = permission + 64;
		}
		if (PERMISSION_WRITE_SIGEND.equals(getDataFromArray(ary, PERMISSION_WRITE_SIGEND))) {
			permission = permission + 128;
		}
		if (PERMISSION_WRITE_SIGEND_MITM.equals(getDataFromArray(ary, PERMISSION_WRITE_SIGEND_MITM))) {
			permission = permission + 256;
		}
		return permission;
	}

	public static String encodeBase64(byte[] value) {
		return Base64.encodeToString(value, Base64.NO_WRAP | Base64.NO_PADDING);
	}

	public static void addProperty(JSONObject obj, String key, Object value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {

		}
	}

	public static JSONObject getObjectFromArray(JSONArray ary) {
		JSONObject jsonObject = null;
		if (ary != null && ary.length() > 0) {
			try {
				return new JSONObject(ary.get(0).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	public static JSONObject getObjectFromArray(JSONArray jsonArray,
			int objectIndex) {
		JSONObject jsonObject = null;
		if (jsonArray != null && jsonArray.length() > 0) {
			try {
				jsonObject = new JSONObject(jsonArray.get(objectIndex).toString());
			} catch (JSONException e) {

			}
		}
		return jsonObject;
	}

	public static JSONArray getArray(JSONArray jsonArray, String key) {
		JSONArray newJsonArray = null;
		try {
			newJsonArray = new JSONArray(getObjectFromArray(jsonArray).get(key).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return newJsonArray;
	}

	public static JSONArray getArray(JSONArray jsonArray, int objectIndex,
			String key) {
		JSONArray newJsonArray = null;
		try {
			newJsonArray = new JSONArray(getObjectFromArray(jsonArray, objectIndex).get(key).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return newJsonArray;
	}

	public static String getData(JSONArray ary, String key) {
		String result = null;
		try {
			result = getObjectFromArray(ary).getString(key);
		} catch (JSONException e) {

		}
		return result;
	}
	
	public static String[] getDataFromArray(JSONArray jsonArray,String key){
		if(jsonArray==null || jsonArray.length()==0){
			return null;
		}
		int length = jsonArray.length();
		String[] strArray = new String[length];
		try {
			for(int i=0;i<length;i++){
				strArray[i] = jsonArray.getString(i);
			}
		} catch (JSONException e) {

		}
		return strArray;
	}
	

	public static String getData(JSONArray jsonArray, int objectIndex,
			String key) {
		String result = null;
		try {
			result =  getObjectFromArray(jsonArray, objectIndex).getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static UUID[] getUUIDs(JSONArray ary) {
		try {
			if (getObjectFromArray(ary).getJSONArray(SERVICE_UUIDS) == null) {
				return null;
			} else {
				UUID[] uuids = new UUID[getObjectFromArray(ary).getJSONArray(
						SERVICE_UUIDS).length()];
				for (int i = 0; i < uuids.length; i++) {
					uuids[i] = (UUID) getObjectFromArray(ary).getJSONArray(
							SERVICE_UUIDS).get(i);
				}
				return uuids;
			}
		} catch (JSONException e) {

		}
		return null;
	}

	public static HashMap<String, Object> decodeAdvData(byte[] advData) {
		HashMap<String, Object> mapAdvData = new HashMap<String, Object>();
		List<String> serviceUUIDs = new ArrayList<String>();
		List<String> solicitedServiceUUIDs = new ArrayList<String>();
		List<String> overflowServiceUUIDs = new ArrayList<String>();
		boolean isOver = true;
		while (isOver) {
			int dataLen = advData[0];
			if (dataLen == 0) {
				isOver = false;
				break;
			}
			byte[] allData = new byte[dataLen];
			for (int i = 0; i < allData.length; i++) {
				allData[i] = advData[i + 1];
			}
			byte[] type = { allData[0] };
			byte[] data = new byte[allData.length - 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = allData[i + 1];
			}
			if (type[0] == 0x02) {
				byte[] mByte = new byte[data.length];
				for (int i = 0; i < mByte.length; i++) {
					mByte[i] = data[data.length - i - 1];
				}
				serviceUUIDs.add(bytesToHexString(mByte));
			} else if (type[0] == 0x03) {
				int number = data.length / 2;
				for (int i = 0; i < number; i++) {
					byte[] mByte = { data[i * 2], data[i * 2 + 1] };
					serviceUUIDs.add(bytesToHexString(mByte));
				}
			} else if (type[0] == 0x04) {
				byte[] mByte = new byte[data.length];
				for (int i = 0; i < mByte.length; i++) {
					mByte[i] = data[data.length - i - 1];
				}
				serviceUUIDs.add(bytesToHexString(mByte));
			} else if (type[0] == 0x05) {
				int number = data.length / 4;
				for (int i = 0; i < number; i++) {
					byte[] mByte = { data[i * 4], data[i * 4 + 1],
							data[i * 4 + 2], data[i * 4 + 3] };
					serviceUUIDs.add(bytesToHexString(mByte));
				}
			} else if (type[0] == 0x06) {
				byte[] mByte = new byte[data.length];
				for (int i = 0; i < mByte.length; i++) {
					mByte[i] = data[data.length - i - 1];
				}
				serviceUUIDs.add(bytesToHexString(mByte));
			} else if (type[0] == 0x07) {
				int number = data.length / 16;
				for (int i = 0; i < number; i++) {
					byte[] mByte = { data[i * 16], data[i * 16 + 1],
							data[i * 16 + 2], data[i * 16 + 3],
							data[i * 16 + 4], data[i * 16 + 5],
							data[i * 16 + 6], data[i * 16 + 7],
							data[i * 16 + 8], data[i * 16 + 9],
							data[i * 16 + 10], data[i * 16 + 11],
							data[i * 16 + 12], data[i * 16 + 13],
							data[i * 16 + 14], data[i * 16 + 15] };
					serviceUUIDs.add(bytesToHexString(mByte));
				}
			} else if (type[0] == 0x08) {
				mapAdvData.put(LOCAL_NAME,hexStrToStr(bytesToHexString(data)));
			} else if (type[0] == 0x09) {
				mapAdvData.put(LOCAL_NAME, hexStrToStr(bytesToHexString(data)));
			} else if (type[0] == 0x0a) {
				mapAdvData.put(TXPOWER_LEVEL,bytesToHexString(data));
			} else if (type[0] == 0x12) {
				mapAdvData
						.put(IS_CONNECTED, bytesToHexString(data));
			} else if (type[0] == 0x14) {
				int number = data.length / 2;
				for (int i = 0; i < number; i++) {
					byte[] mByte = { data[i * 2], data[i * 2 + 1] };
					solicitedServiceUUIDs.add(bytesToHexString(mByte));
				}
			} else if (type[0] == 0x15) {
				int number = data.length / 16;
				for (int i = 0; i < number; i++) {
					byte[] mByte = { data[i * 16], data[i * 16 + 1],
							data[i * 16 + 2], data[i * 16 + 3],
							data[i * 16 + 4], data[i * 16 + 5],
							data[i * 16 + 6], data[i * 16 + 7],
							data[i * 16 + 8], data[i * 16 + 9],
							data[i * 16 + 10], data[i * 16 + 11],
							data[i * 16 + 12], data[i * 16 + 13],
							data[i * 16 + 14], data[i * 16 + 15] };
					solicitedServiceUUIDs.add(bytesToHexString(mByte));
				}
			} else if (type[0] == 0x16) {
				mapAdvData
						.put(SERVICE_DATA, bytesToHexString(data));
			} else if (type[0] == 0xff) {
				mapAdvData.put(MANUFACTURER_DATA,bytesToHexString(data));
			}
			byte[] newData = new byte[advData.length - dataLen - 1];
			for (int i = 0; i < newData.length; i++) {
				newData[i] = advData[i + 1 + dataLen];
			}
			advData = newData;
		}
		mapAdvData.put(SERVICE_UUIDS, serviceUUIDs);
		mapAdvData.put(SOLICITED_SERVICE_UUIDS, solicitedServiceUUIDs);
		mapAdvData.put(OVERFLOW_SERVICE_UUIDS, overflowServiceUUIDs);
		return mapAdvData;
	}

	public static void sendErrorMsg(CallbackContext callbackContext) {
		JSONObject jsonObject = new JSONObject();
		Tools.addProperty(jsonObject, Tools.MES, Tools.ERROR);
		callbackContext.error(jsonObject);
	}

	public static void sendSuccessMsg(CallbackContext callbackContext) {
		JSONObject jsonObject = new JSONObject();
		Tools.addProperty(jsonObject, Tools.MES, Tools.SUCCESS);
		callbackContext.success(jsonObject);
	}

	public static String getDateString() {
		return new SimpleDateFormat(DATE_FORMATE).format(new Date());
	}

	public static byte[] parsingCodingFormat(String writeValue, String writeType) {
		if (writeType.toLowerCase().equals("hex")) {
			return hexStringToByte(writeValue);
		}
		if (writeType.toLowerCase().equals("ascii")) {
			return ascIIStringToByte(writeValue);
		}
		if (writeType.toLowerCase().equals("unicode")) {
			return writeValue.getBytes();
		}
		return null;
	}
	
	public static final String bytesToHexString(byte[] bArray)
	{
		StringBuffer sb = new StringBuffer(bArray.length);  
		String sTemp;  
		for (int i = 0; i < bArray.length; i++) {  
			sTemp = Integer.toHexString(0xFF & bArray[i]);  
			if (sTemp.length() < 2)  
				sb.append(0);  
			sb.append(sTemp.toUpperCase()); 
		}  
		return sb.toString();  
	}

	public static byte[] hexStringToByte(String hexString)
	{  
		hexString = hexString.toLowerCase();
		if(hexString.length() % 2 != 0){
			hexString = "0" + hexString;
		}
		int len = (hexString.length() / 2);  
		byte[] result = new byte[len];  
		char[] achar = hexString.toCharArray();  
		for (int i = 0; i <len; i++) {  
			int pos = i * 2;  
			result[len - i -1] = (byte) ("0123456789abcdef".indexOf(achar[pos]) << 4 | "0123456789abcdef".indexOf(achar[pos + 1]));  
		}  
		return result;   
	}
	
	public static byte[]  ascIIStringToByte(String ascIIString)
	{
		int asc = Integer.parseInt(ascIIString);
		byte[] b = new byte[ascIIString.length()];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (asc >> i*8);
		}
		return b;
	}
	
	public static String hexStrToStr(String hexStr)    
	{      
		String str = "0123456789ABCDEF";      
		char[] hexs = hexStr.toCharArray();      
		byte[] bytes = new byte[hexStr.length() / 2];      
		int n;      
		for (int i = 0; i < bytes.length; i++)    
		{      
			n = str.indexOf(hexs[2 * i]) * 16;      
			n += str.indexOf(hexs[2 * i + 1]);      
			bytes[i] = (byte) (n & 0xff);      
		}      
		return new String(bytes);      
	} 
	
	public static boolean isSupportUniversalAPI()
	{
		char[] universalVersion = {'4','.','3','.','0'};
		char[] currentVersion = getOSVersionNumber().toCharArray();
		if (universalVersion[0] > currentVersion[0]) 
		{
			return false;
		}
		if (universalVersion[0] < currentVersion[0]) 
		{
			return true;
		}
		if (universalVersion[0] == currentVersion[0]) {
			if (universalVersion[2] > currentVersion[2]) {
				return false;
			}else {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSupportSpecificAPI(String specificBrand)
	{
		if (getPhoneBrand().toLowerCase().indexOf(specificBrand.toLowerCase()) != -1 || 
				getPhoneManufacturers().toLowerCase().indexOf(specificBrand.toLowerCase()) != -1) 
		{
			return true;
		}else {
			return false;
		}
	}
	
	public static String getSupportBasebandVersionBrand()
	{
		String bv = getPhoneBasebandVersion().toLowerCase();
		String xiaomi = "xiaomi";
		if (bv.indexOf("m8064") != -1)
		{
			return xiaomi;
		}
		return null;
	}
	
	
}
