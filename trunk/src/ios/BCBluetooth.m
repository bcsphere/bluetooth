/*
 Copyright 2013-2014 JUMA Technology
 
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


#import "BCBluetooth.h"

#import <Cordova/NSDictionary+Extensions.h>
#import <Cordova/NSArray+Comparisons.h>
#import "sys/sysctl.h"

#define BLUETOOTH_STATE @"state"
#define BLUETOOTH_OPEN @"bluetoothopen"
#define BLUETOOTH_CLOSE @"bluetoothclose"
#define DEVICE_NAME @"deviceName"
#define DEVICE_ID @"deviceID"
#define MES @"mes"
#define ADVERTISEMENT_DATA @"advertisementData"
#define SERVICES @"services"
#define CHARACTERISTICS @"characteristics"
#define DESCRIPTORS @"descriptors"
#define SERVICE_INDEX @"serviceIndex"
#define SERVICE_NAME @"serviceName"
#define SERVICE_TYPE @"serviceType"
#define SERVICE_UUID @"serviceUUID"
#define UINQUE_ID @"uniqueID"
#define CHARACTERISTIC_INDEX @"characteristicIndex"
#define CHARACTERISTIC_VALUE @"characteristicValue"
#define CHARACTERISTIC_NAME @"characteristicName"
#define CHARACTERISTIC_PERMISSION @"characteristicPermission"
#define CHARACTERISTIC_PROPERTY @"characteristicProperty"
#define CHARACTERISTIC_UUID @"characteristicUUID"
#define CHARACTERISTIC_UUIDS @"characteristicUUIDs"
#define CHARACTERISTIC_VALUE_TYPE @"characteristicValueType"
#define DESCRIPTOR_INDEX @"descriptorIndex"
#define DESCRIPTOR_NAME @"descriptorName"
#define DESCRIPTOR_PERMISSION @"descriptorPermission"
#define DESCRIPTOR_UUID @"descriptorUUID"
#define DESCRIPTOR_VALUE @"descriptorValue"
#define DESCRIPTOR_VALUE_TYPE @"descriptorValueType"
#define PERIPHERAL_RSSI @"RSSI"
#define VALUE @"value"
#define DATE @"date"
#define DATE_FORMATE @"yyyy-MM-dd HH:mm:ss:SSS"

#define NOTAVAILABLE @"n/a"
#define SUCCESS @"success"
#define ERROR @"error"
#define IS_TRUE @"true"
#define IS_FALSE @"false"
#define ENABLE @"enable"
#define IS_CONNECTED @"isConnected"
#define DISCONNECT @"disconnect"

#define PERMISSION_READ @"read"
#define PERMISSION_READ_ENCRYPTED @"readEncrypted"
#define PERMISSION_READ_ENCRYPTED_MITM @"readEncryptedMitm"
#define PERMISSION_WRITE @"write"
#define PERMISSION_WRITE_ENCRYPTED_MITM @"writeEncryptedMitm"
#define PERMISSION_WRITE_ENCRYPTED @"writeEncrypted"
#define PERMISSION_WRITE_SIGEND @"writeSigend"
#define PERMISSION_WRITE_SIGEND_MITM @"writeSigendMitm"
#define PROPERTY_AUTHENTICATED_SIGNED_WTRTES @"authenticatedSignedWrites"
#define PROPERTY_BROADCAST @"broadcast"
#define PROPERTY_EXTENDED_PROPERTIES @"extendedProperties"
#define PROPERTY_INDICATE @"indicate"
#define PROPERTY_NOTIFY @"notify"
#define PROPERTY_READ @"read"
#define PROPERTY_WRITE @"write"
#define PROPERTY_WRITE_WITHOUT_RESPONSE @"writeWithoutResponse"
#define PROPERTY_NOTIFY_ENCRYPTION_REQUIRED @"NotifyEncryptionRequired"
#define PROPERTY_INDICATE_ENCRYPTION_REQUIRED @"IndicateEncryptionRequired"

#define KCBADVDATA_LOCALNAME @"kCBAdvDataLocalName"
#define LOCAL_NAME @"localName"
#define KCBADVDATA_SERVICE_UUIDS @"kCBAdvDataServiceUUIDs"
#define SERVICE_UUIDS @"serviceUUIDs"
#define KCBADVDATA_TXPOWER_LEVEL @"kCBAdvDataTxPowerLevel"
#define TXPOWER_LEVEL @"txPowerLevel"
#define KCBADVDATA_SERVICE_DATA @"kCBAdvDataServiceData"
#define SERVICE_DATA @"serviceData"
#define KCBADVDATALOCAL_NAME @"kCBAdvDataManufacturerData"
#define MANUFACTURER_DATA @"manufacturerData"
#define KCBADVDATA_OVERFLOW_SERVICE_UUIDS @"kCBAdvDataOverflowServiceUUIDs"
#define OVERFLOW_SERVICE_UUIDS @"overflowServiceUUIDs"
#define KCBADVDATA_ISCONNECTABLE @"kCBAdvDataIsConnectable"
#define ISCONNECTABLE @"isConnectable"
#define KCBADCDATA_SOLICITED_SERVICE_UUIDS @"kCBAdvDataSolicitedServiceUUIDs"
#define SOLICITED_SERVICE_UUIDS @"solicitedServiceUUIDs"

#define EVENT_NAME @"eventName"
#define GETBLUETOOTHSTATE @"getBluetoothState"
#define EVENT_BLUETOOTHOPEN @"bluetoothopen"
#define EVENT_BLUETOOTHCLOSE @"bluetoothclose"
#define GETCONNECTEDDEVICES @"getConnectedDevices"
#define EVENT_DISCONNECT @"disconnect"
#define SETNOTIFICATION @"setNotification"
#define ADDSERVICE @"addService"
#define ONREADREQUEST @"onReadRequest"
#define ONWRIESTREQUEST @"onWriteRequest"
#define WRITE_TYPE @"writeType"
#define WRITE_VALUE @"writeValue"
#define ISON @"isON"
#define READ @"read"
#define WRITE @"write"
#define ON_READ_REQUEST @"onReadRequest"
#define ON_WRITE_REQUEST @"onWriteRequest"

#define APP_ID @"appID"
#define API @"api"
#define IOS @"ios"
#define IS_IOS_VERSION (([[[UIDevice currentDevice] systemVersion] floatValue] >=7.0)? (YES):(NO))

@implementation BCBluetooth

@synthesize  myPeripheralManager;
@synthesize  serviceAndKeyDic;
@synthesize  eventNameAndCallbackIdDic;
@synthesize  writeReqAndCharacteristicDic;
@synthesize  readReqAndCharacteristicDic;
@synthesize  valueAndCharacteristicDic;

@synthesize  myCentralManager;
@synthesize  _peripherals;
@synthesize  _allPeripherals;
@synthesize  _services;
@synthesize  _characteristics;
@synthesize  _descriptors;

@synthesize  servicesInfo;
@synthesize  characteristicsInfo;
@synthesize  descriptorsInfo;
@synthesize  peripheralsInfo;
@synthesize  advDataDic;
@synthesize  RSSIDic;
@synthesize  bluetoothState;

#pragma mark -
#pragma mark BC Interface
#pragma mark -

- (void)pluginInitialize{
    [super pluginInitialize];
    if (!isVariableInit) {
        [self variableInit];
    }
}

- (void)variableInit{
    isVariableInit = TRUE;
    isEndOfAddService = FALSE;
    isAddAllData = FALSE;
    isConnectedByManager = FALSE;
    isRead = FALSE;
    myPeripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:nil];
    serviceAndKeyDic = [[NSMutableDictionary alloc] init];
    eventNameAndCallbackIdDic = [[NSMutableDictionary alloc] init];
    writeReqAndCharacteristicDic = [[NSMutableDictionary alloc] init];
    readReqAndCharacteristicDic = [[NSMutableDictionary alloc] init];
    valueAndCharacteristicDic = [[NSMutableDictionary alloc] init];
    
    myCentralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
    _peripherals = [[NSMutableArray alloc] init];
    _allPeripherals = [[NSMutableArray alloc] init];
    _services = [[NSMutableArray alloc] init];
    _characteristics = [[NSMutableArray alloc] init];
    _descriptors = [[NSMutableArray alloc] init];
    
    servicesInfo = [[NSMutableArray alloc] init];
    characteristicsInfo = [[NSMutableArray alloc] init];
    descriptorsInfo = [[NSMutableArray alloc] init];
    peripheralsInfo = [[NSMutableArray alloc] init];
    advDataDic = [[NSMutableDictionary alloc] init];
    RSSIDic = [[NSMutableDictionary alloc] init];
    bluetoothState = [[NSString alloc] init];
}

- (void)getEnvironment:(CDVInvokedUrlCommand *)command{
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:NOTAVAILABLE forKey:DEVICE_ID];
    [callbackInfo setValue:NOTAVAILABLE forKey:APP_ID];
    [callbackInfo setValue:IOS forKey:API];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getBluetoothState:(CDVInvokedUrlCommand*)command{
    [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:GETBLUETOOTHSTATE];
    if (bluetoothState.length > 0) {
        [self backBluetoothState];
    }else{
        [self performSelector:@selector(backBluetoothState) withObject:nil afterDelay:1.0];
    }
}

- (void)backBluetoothState{
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:bluetoothState forKey:BLUETOOTH_STATE];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:GETBLUETOOTHSTATE]];
}

- (void)addEventListener:(CDVInvokedUrlCommand *)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *eventName = [self parseStringFromJS:command.arguments keyFromJS:EVENT_NAME];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:eventName];
    }
}

- (void)openBluetooth:(CDVInvokedUrlCommand*)command{
    [self error:command.callbackId];
}

- (void)startScan:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        if (_peripherals) {
            [_peripherals removeAllObjects];
        }
        NSMutableArray *serviceUUIDs = [self parseArrayFromJS:command.arguments keyFromJS:SERVICE_UUIDS];
        if (serviceUUIDs) {
            if (serviceUUIDs.count > 0){
                [myCentralManager scanForPeripheralsWithServices:serviceUUIDs options:0];
            }else{
                [myCentralManager scanForPeripheralsWithServices:nil options:0];
            }
        }else{
            [myCentralManager scanForPeripheralsWithServices:nil options:0];
        }
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }else{
        [self error:command.callbackId];
    }
}

- (void)stopScan:(CDVInvokedUrlCommand*)command{
    [myCentralManager stopScan];
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:SUCCESS forKey:MES];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getScanData:(CDVInvokedUrlCommand*)command{
    NSMutableArray* callbackInfo = [[NSMutableArray alloc] init];
    callbackInfo = [self storePeripheralInfo:_peripherals];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)creatPair:(CDVInvokedUrlCommand*)command{
    [self error:command.callbackId];
}

- (void)removePair:(CDVInvokedUrlCommand*)command{
    [self error:command.callbackId];
}

- (void)getPairedDevices:(CDVInvokedUrlCommand*)command{
    [self error:command.callbackId];
}

- (void)getConnectedDevices:(CDVInvokedUrlCommand*)command{
    [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:GETCONNECTEDDEVICES];
    [myCentralManager retrieveConnectedPeripherals];
}

- (void)connect:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:deviceID];
        if ([self isNormalString:deviceID]) {
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                if (IS_IOS_VERSION) {
                    if (peripheral.state == CBPeripheralStateConnected) {
                        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                        [callbackInfo setValue:SUCCESS forKey:MES];
                        [callbackInfo setValue:deviceID forKey:DEVICE_ID];
                        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                    }else if(peripheral.state == CBPeripheralStateDisconnected){
                        [myCentralManager connectPeripheral:peripheral options:nil];
                    }
                }else{
                    if (peripheral.isConnected) {
                        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                        [callbackInfo setValue:SUCCESS forKey:MES];
                        [callbackInfo setValue:deviceID forKey:DEVICE_ID];
                        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                    }else{
                        [myCentralManager connectPeripheral:peripheral options:nil];
                    }
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)disconnect:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:deviceID];
        if ([self isNormalString:deviceID]) {
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            isConnectedByManager = TRUE;
            if (peripheral) {
                if (peripheral.isConnected) {
                    [myCentralManager cancelPeripheralConnection:peripheral];
                }else{
                    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                    [callbackInfo setValue:SUCCESS forKey:MES];
                    [callbackInfo setValue:deviceID forKey:DEVICE_ID];
                    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getServices:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]){
            CBPeripheral *peripheral=[self getPeripheral:deviceID];
            [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:deviceID];
            if (peripheral) {
                if (peripheral.services.count > 0){
                    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                    callbackInfo = [self storeServiceInfo:peripheral];
                    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                }else{
                    peripheral.delegate = self;
                    [peripheral discoverServices:nil];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getCharacteristics:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        NSString *serviceIndex = [self parseStringFromJS:command.arguments keyFromJS:SERVICE_INDEX];
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]){
            CBPeripheral *peripheral=[self getPeripheral:deviceID];
            if (peripheral) {
                if ([self isNormalString:serviceIndex]){
                    [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:[NSString stringWithFormat:@"%d%@",[serviceIndex intValue],deviceID]];
                    if (peripheral.services.count > [serviceIndex intValue]) {
                        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
                        if (service.characteristics.count > 0) {
                            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                            callbackInfo = [self storeChatacteristicInfo:peripheral service:service];
                            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                        }else{
                            peripheral.delegate = self;
                            [peripheral discoverCharacteristics:nil forService:service];
                        }
                    } else {
                        [self error:command.callbackId];
                    }
                } else {
                    [self error:command.callbackId];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else {
        [self error:command.callbackId];
    }
}

- (void)getDescriptors:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        NSString *charateristicIndex = [self parseStringFromJS:command.arguments keyFromJS:CHARACTERISTIC_INDEX];
        NSString *serviceIndex = [self parseStringFromJS:command.arguments keyFromJS:SERVICE_INDEX];
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]){
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                if ([self isNormalString:serviceIndex] && (peripheral.services.count > [serviceIndex intValue])){
                    CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
                    if ([self isNormalString:charateristicIndex]){
                        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:[NSString stringWithFormat:@"%d%d%@",[charateristicIndex intValue],[serviceIndex intValue],deviceID]];
                        if (service.characteristics.count > [charateristicIndex intValue]) {
                            CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[charateristicIndex intValue]];
                            if (characteristic.descriptors.count > 0) {
                                NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                                callbackInfo = [self storeDescriptorInfo:peripheral characteristic:characteristic];
                                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                                [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                            }else{
                                peripheral.delegate = self;
                                [peripheral discoverDescriptorsForCharacteristic:characteristic];
                            }
                        }else{
                            [self error:command.callbackId];
                        }
                    } else{
                        [self error:command.callbackId];
                    }
                }else{
                    [self error:command.callbackId];
                }
                
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    } else{
        [self error:command.callbackId];
    }
}

- (void)getRSSI:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:[NSString stringWithFormat:@"getRssi%@",deviceID]];
        if ([self isNormalString:deviceID]) {
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                peripheral.delegate = self;
                [peripheral readRSSI];
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)writeValue:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:WRITE];
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]) {
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                NSString *valueWrite = [self parseStringFromJS:command.arguments keyFromJS:WRITE_VALUE];
                NSString *descriptorIndex = [self parseStringFromJS:command.arguments keyFromJS:DESCRIPTOR_INDEX];
                NSString *characteristicIndex = [self parseStringFromJS:command.arguments keyFromJS:CHARACTERISTIC_INDEX];
                NSString *serviceIndex = [self parseStringFromJS:command.arguments keyFromJS:SERVICE_INDEX];
                NSData *data = [self stringToByte:valueWrite];
                if (data) {
                    if ([self isNormalString:serviceIndex]){
                        if (peripheral.services.count > [serviceIndex intValue]) {
                            CBService *service=[peripheral.services objectAtIndex:[serviceIndex intValue]];
                            if ([self isNormalString:characteristicIndex]){
                                if (service.characteristics.count > [characteristicIndex intValue]) {
                                    CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[characteristicIndex intValue]];
                                    if ([self isNormalString:descriptorIndex]){
                                        if (characteristic.descriptors.count > [descriptorIndex intValue]) {
                                            peripheral.delegate = self;
                                            [peripheral writeValue:data forDescriptor:[characteristic.descriptors objectAtIndex: [descriptorIndex intValue] ]];
                                        }else{
                                            [self error:command.callbackId];
                                        }
                                    }else{
                                        peripheral.delegate = self;
                                        [peripheral writeValue:data forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
                                    }
                                }else{
                                    [self error:command.callbackId];
                                }
                            }else{
                                [self error:command.callbackId];
                            }
                        }else{
                            [self error:command.callbackId];
                        }
                    }else{
                        [self error:command.callbackId];
                    }
                }else{
                    [self error:command.callbackId];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)readValue:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:READ];
        NSString *descriptorIndex = [self parseStringFromJS:command.arguments keyFromJS:DESCRIPTOR_INDEX];
        NSString *characteristicIndex = [self parseStringFromJS:command.arguments keyFromJS:CHARACTERISTIC_INDEX];
        NSString *serviceIndex = [self parseStringFromJS:command.arguments keyFromJS:SERVICE_INDEX];
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]){
            CBPeripheral *peripheral=[self getPeripheral:deviceID];
            if (peripheral) {
                if ([self isNormalString:serviceIndex]){
                    if (peripheral.services.count > [serviceIndex intValue]) {
                        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
                        if([self isNormalString:characteristicIndex]){
                            if (service.characteristics.count > [characteristicIndex intValue]){
                                CBCharacteristic *characteristic=[service.characteristics objectAtIndex:[characteristicIndex intValue]];
                                if ([self isNormalString:descriptorIndex]){
                                    if (characteristic.descriptors.count > [descriptorIndex intValue]) {
                                        peripheral.delegate = self;
                                        [peripheral readValueForDescriptor:[characteristic.descriptors objectAtIndex:[descriptorIndex intValue]]];
                                    }else{
                                        [self error:command.callbackId];
                                    }
                                }else{
                                    isRead = TRUE;
                                    peripheral.delegate = self;
                                    [peripheral readValueForCharacteristic:[service.characteristics objectAtIndex:[characteristicIndex intValue]]];
                                }
                            }else{
                                [self error:command.callbackId];
                            }
                        }else{
                            [self error:command.callbackId];
                        }
                    }else{
                        [self error:command.callbackId];
                    }
                }else{
                    [self error:command.callbackId];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    } else{
        [self error:command.callbackId];
    }
}

- (void)setNotification:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]){
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:SETNOTIFICATION];
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        if ([self isNormalString:deviceID]){
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                NSString *characteristicIndex = [self parseStringFromJS:command.arguments keyFromJS:CHARACTERISTIC_INDEX];
                NSString *serviceIndex = [self parseStringFromJS:command.arguments keyFromJS:SERVICE_INDEX];
                NSString *enable = [self parseStringFromJS:command.arguments keyFromJS:ENABLE];
                if ([self isNormalString:serviceIndex]){
                    if (peripheral.services.count > [serviceIndex intValue]) {
                        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
                        if ([self isNormalString:characteristicIndex]){
                            if (service.characteristics.count > [characteristicIndex intValue]) {
                                CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[characteristicIndex intValue]];
                                peripheral.delegate = self;
                                [[NSUserDefaults standardUserDefaults] setValue:enable forKey:ISON];
                                if ([enable boolValue]) {
                                    [peripheral setNotifyValue:YES forCharacteristic:characteristic];
                                }else{
                                    [peripheral setNotifyValue:NO forCharacteristic:characteristic];
                                }
                            } else{
                                [self error:command.callbackId];
                            }
                        }else{
                            [self error:command.callbackId];
                        }
                    }else{
                        [self error:command.callbackId];
                    }
                }else{
                    [self error:command.callbackId];
                }
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getDeviceAllData:(CDVInvokedUrlCommand*)command{
    isAddAllData=TRUE;
    if ([self existCommandArguments:command.arguments]) {
        NSString *deviceID = [self parseStringFromJS:command.arguments keyFromJS:DEVICE_ID];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:[NSString stringWithFormat:@"perInfoCommand%@",deviceID]];
        if ([self isNormalString:deviceID]) {
            if (!peripheralsInfo) {
                peripheralsInfo=[[NSMutableArray alloc] init];
            }else{
                [peripheralsInfo removeAllObjects];
            }
            CBPeripheral *peripheral = [self getPeripheral:deviceID];
            if (peripheral) {
                peripheral.delegate=self;
                [peripheral discoverServices:nil];
            }else{
                [self error:command.callbackId];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getServiceInfo:(CBPeripheral *)peripheral{
    if (peripheral.services.count > 0) {
        for (int i = 0; i < peripheral.services.count; i++) {
            CBService *service = [peripheral.services objectAtIndex:i];
            NSMutableDictionary *serviceInfo = [[NSMutableDictionary alloc] init];
            [serviceInfo setValue:[self getServiceName:service.UUID] forKey:SERVICE_NAME];
            [serviceInfo setValue:[NSString stringWithFormat:@"%@",[self CBUUIDFiltrToString:service.UUID]] forKey:SERVICE_UUID];
            [serviceInfo setValue:[NSString stringWithFormat:@"%d",i] forKey:SERVICE_INDEX];
            [peripheralsInfo addObject:serviceInfo];
        }
        serviceNum = 0;
        [self getCharacteristicObjects:peripheral];
    } else {
        NSString *deviceID = [self getPeripheralUUID:peripheral];
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:peripheralsInfo forKey:SERVICES];
        [callbackInfo setValue:deviceID forKey:DEVICE_ID];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"perInfoCommand%@",deviceID]]];
    }
}

- (void)getCharacteristicObjects:(CBPeripheral *)peripheral{
    if (peripheral.services.count > serviceNum) {
        [peripheral discoverCharacteristics:nil forService:[peripheral.services objectAtIndex:serviceNum]];
    }
}

- (void)getAllCharacteristicInfo:(CBService *)service peripheral:(CBPeripheral *)peripheral{
    NSMutableArray *characteristicInfo=[[NSMutableArray alloc] init];
    for (int i = 0; i < service.characteristics.count; i++) {
        CBCharacteristic *characteristic = [service.characteristics objectAtIndex:i];
        NSMutableDictionary *characteristicInfoDic = [[NSMutableDictionary alloc] init];
        [characteristicInfoDic setValue:[self getServiceName:characteristic.UUID] forKey:CHARACTERISTIC_NAME];
        [characteristicInfoDic setValue:[self CBUUIDFiltrToString:characteristic.UUID] forKey:CHARACTERISTIC_UUID];
        [characteristicInfoDic setValue:[self printCharacteristicProperties:characteristic] forKey:CHARACTERISTIC_PROPERTY];
        [characteristicInfoDic setValue:[NSString stringWithFormat:@"%d",i] forKey:CHARACTERISTIC_INDEX];
        [characteristicInfo addObject:characteristicInfoDic];
    }
    [[peripheralsInfo objectAtIndex:serviceNum] setValue:characteristicInfo forKey:CHARACTERISTICS];
    if (peripheralsInfo.count-1 > serviceNum) {
        serviceNum = serviceNum+1;
        [self getCharacteristicObjects:peripheral];
    }else{
        serviceNum = 0;
        characteristicNum = 0;
        [self getDescriptorObjects:peripheral NSIntgerServiceNum:serviceNum NSIntgerCharacteristicNum:characteristicNum];
    }
}

- (void)getDescriptorObjects:(CBPeripheral *)peripheral NSIntgerServiceNum:(NSInteger)serNum NSIntgerCharacteristicNum:(NSInteger)characterNum{
    if (peripheralsInfo.count > serviceNum) {
        CBService *service = [peripheral.services objectAtIndex:serNum];
        if (service.characteristics.count > characterNum) {
            CBCharacteristic *characteristic=[service.characteristics objectAtIndex:characterNum];
            [peripheral discoverDescriptorsForCharacteristic:characteristic];
        }else{
            serviceNum = serviceNum + 1;
            characteristicNum = 0;
            [self getDescriptorObjects:peripheral NSIntgerServiceNum:serviceNum NSIntgerCharacteristicNum:characteristicNum];
        }
    }else{
        isAddAllData = FALSE;
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:peripheralsInfo forKey:SERVICES];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"perInfoCommand%@",[self getPeripheralUUID:peripheral]]]];
    }
}

- (void)addDescriptorArray:(CBPeripheral *)peripheral CBCharacteristic:(CBCharacteristic *)characteristic{
    NSMutableArray *descriptorInfo=[[NSMutableArray alloc] init];
    for (int i = 0; i < characteristic.descriptors.count; i++) {
        CBDescriptor *descriptor = [characteristic.descriptors objectAtIndex:i];
        NSMutableDictionary *descriptorInfoDic = [[NSMutableDictionary alloc] init];
        [descriptorInfoDic setValue:[self getServiceName:descriptor.UUID] forKey:DESCRIPTOR_NAME];
        [descriptorInfoDic setValue:[self CBUUIDFiltrToString:descriptor.UUID] forKey:DESCRIPTOR_UUID];
        [descriptorInfoDic setValue:[NSString stringWithFormat:@"%d",i] forKey:DESCRIPTOR_INDEX];
        [descriptorInfo addObject:descriptorInfoDic];
    }
    [[[[peripheralsInfo objectAtIndex:serviceNum] objectForKey:CHARACTERISTICS] objectAtIndex:characteristicNum] setValue:descriptorInfo forKey:DESCRIPTORS];
    NSMutableArray *characteristicCount=[[peripheralsInfo objectAtIndex:serviceNum] objectForKey:CHARACTERISTICS];
    if (characteristicCount.count-1 > characteristicNum) {
        [self getDescriptorObjects:peripheral NSIntgerServiceNum:serviceNum NSIntgerCharacteristicNum:characteristicNum];
        characteristicNum = characteristicNum + 1;
    }else{
        serviceNum = serviceNum + 1;
        characteristicNum = 0;
        [self getDescriptorObjects:peripheral NSIntgerServiceNum:serviceNum NSIntgerCharacteristicNum:characteristicNum];
    }
}

- (void)addServices:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]) {
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:ADDSERVICE];
        NSMutableDictionary *servicePacket=[[NSString stringWithFormat:@"%@",[command.arguments objectAtIndex:0]] JSONObject];
        NSMutableArray *services = [[NSMutableArray alloc] initWithArray:[servicePacket valueForKey:SERVICES]];
        if (services.count > 0) {
            CBMutableDescriptor *newDescriptor;
            for (int i=0; i < services.count; i++) {
                NSString *newServiceUUID = [NSString stringWithFormat:@"%@",[[services objectAtIndex:i] valueForKey:SERVICE_UUID]];
                NSString *newServiceType = [NSString stringWithFormat:@"%@",[[services objectAtIndex:i] valueForKey:SERVICE_TYPE]];
                NSString *uniqueID = [NSString stringWithFormat:@"%@",[[services objectAtIndex:i] valueForKey:UINQUE_ID]];
                NSMutableArray *characteristics = [[services objectAtIndex:i] valueForKey:CHARACTERISTICS];
                NSMutableArray *newCharacteristics = [[NSMutableArray alloc] init];
                if (characteristics.count > 0) {
                    for (int j = 0; j < characteristics.count; j++) {
                        NSString *newCharacteristicValue = [NSString stringWithFormat:@"%@",[[characteristics objectAtIndex:j] valueForKey:CHARACTERISTIC_VALUE]];
                        NSString *newCharacteristicUUID = [NSString stringWithFormat:@"%@",[[characteristics objectAtIndex:j] valueForKey:CHARACTERISTIC_UUID]];
                        NSString *onReadRequest = [NSString stringWithFormat:@"%@",[[characteristics objectAtIndex:j] valueForKey:ONREADREQUEST]];
                        NSString *onWriteRequest  = [NSString stringWithFormat:@"%@",[[characteristics objectAtIndex:j] valueForKey:ONWRIESTREQUEST]];
                        NSMutableArray *newCharacteristicProperty = [[NSMutableArray alloc] initWithArray:[[characteristics objectAtIndex:j] valueForKey:CHARACTERISTIC_PROPERTY]];
                        NSMutableArray *newCharacteristicPermission = [[NSMutableArray alloc] initWithArray:[[characteristics objectAtIndex:j] valueForKey:CHARACTERISTIC_PERMISSION]];
                        
                        NSMutableArray *descriptors = [[characteristics objectAtIndex:j] valueForKey:DESCRIPTORS];
                        BOOL addDescriptor = FALSE;
                        if (descriptors.count > 0) {
                            for (int k = 0; k < descriptors.count; k++) {
                                NSString *newDescriptorValue = [NSString stringWithFormat:@"%@",[[descriptors objectAtIndex:k] valueForKey:DESCRIPTOR_VALUE]];
                                NSString *descriptorUUID = [NSString stringWithFormat:@"%@",[[descriptors objectAtIndex:k] valueForKey:DESCRIPTOR_UUID]];
                                if ([descriptorUUID isEqualToString:@"00002901-0000-1000-8000-00805f9b34fb"]) {
                                    CBUUID * newDescriptorUUID = [CBUUID UUIDWithString:CBUUIDCharacteristicUserDescriptionString];
                                    addDescriptor = TRUE;
                                    newDescriptor = [[CBMutableDescriptor alloc] initWithType:newDescriptorUUID value:newDescriptorValue];
                                }
                            }
                        }
                        CBMutableCharacteristic *newCharacteristic = [self buildCharacteristicWithUUID:newCharacteristicUUID properties:newCharacteristicProperty value:newCharacteristicValue permissions:newCharacteristicPermission];
                        [writeReqAndCharacteristicDic setValue:newCharacteristic forKey:onWriteRequest];
                        [readReqAndCharacteristicDic setValue:newCharacteristic forKey:onReadRequest];
                        [valueAndCharacteristicDic setValue:newCharacteristic forKey:newCharacteristicValue];
                        if (addDescriptor) {
                            [newCharacteristic setDescriptors:@[newDescriptor]];
                        }
                        [newCharacteristics addObject:newCharacteristic];
                    }
                }
                CBMutableService *newService = [self buildServiceWithUUID:newServiceUUID primary:newServiceType];
                if (newCharacteristics.count > 0) {
                    [newService setCharacteristics:newCharacteristics];
                }
                if (!serviceAndKeyDic) {
                    serviceAndKeyDic = [[NSMutableDictionary alloc] init];
                }else{
                    [serviceAndKeyDic setValue:newService forKey:uniqueID];
                }
                [myPeripheralManager addService:newService];
                if (services.count == i + 1) {
                    isEndOfAddService = TRUE;
                }
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (CBMutableService *)buildServiceWithUUID:(NSString *)uuidStr primary:(NSString *)primaryStr{
    CBUUID *serviceUUID = [CBUUID UUIDWithString:uuidStr];
    CBMutableService *service = [[CBMutableService alloc] initWithType:serviceUUID primary:![primaryStr boolValue]];
    return service;
}

- (CBMutableCharacteristic *)buildCharacteristicWithUUID:(NSString *)uuidStr properties:(NSMutableArray *)properties value:(NSString *)dataStr permissions:(NSMutableArray *)permissions{
    Byte byte = (Byte)[dataStr intValue];
    NSData *data = [[NSData alloc] initWithBytes:&byte length:1];
    CBUUID *characteristicUUID = [CBUUID UUIDWithString:uuidStr];
    int propertyNum = [self property:properties] ^ CBCharacteristicPropertyRead;
    if (propertyNum == 0 || [self property:properties] == 0) {
        CBMutableCharacteristic *characteristic = [[CBMutableCharacteristic alloc] initWithType:characteristicUUID properties:CBCharacteristicPropertyRead value:data permissions:CBAttributePermissionsReadable];
        return characteristic;
    }else{
        CBMutableCharacteristic *characteristic;
        characteristic = [[CBMutableCharacteristic alloc] initWithType:characteristicUUID properties:[self property:properties]  value:nil permissions:[self permission:permissions]];
        return characteristic;
    }
}

- (void)removeServices:(CDVInvokedUrlCommand*)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *uniqueID = [self parseStringFromJS:command.arguments keyFromJS:UINQUE_ID];
        if ([self isNormalString:uniqueID]) {
            BOOL remove = FALSE;
            for (NSString *str in [serviceAndKeyDic allKeys]) {
                if ([str isEqualToString:uniqueID]) {
                    remove = TRUE;
                }
            }
            if (remove) {
                [myPeripheralManager removeService:[serviceAndKeyDic valueForKey:uniqueID]];
                NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                [callbackInfo setValue:SUCCESS forKey:MES];
                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            }else{
                [self error:command.callbackId];
            }
        }else{
            [myPeripheralManager removeAllServices];
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            [callbackInfo setValue:SUCCESS forKey:MES];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

#pragma mark -
#pragma mark - CBperipheralManagerDelegate
- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral {
    switch (peripheral.state) {
        case CBPeripheralManagerStatePoweredOn:
            break;
        default:
            [self error:[[NSUserDefaults standardUserDefaults] objectForKey:ADDSERVICE]];
            break;
    }
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didAddService:(CBService *)service error:(NSError *)error{
    if (!error) {
        if (isEndOfAddService) {
            [myPeripheralManager startAdvertising:@{ CBAdvertisementDataLocalNameKey : @"BCExplore", CBAdvertisementDataServiceUUIDsKey:@[[CBUUID UUIDWithString:@"0000ffe0-0000-1000-8000-00805f9b34fb"]]}];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:ADDSERVICE]];
        }
    }else{
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:ADDSERVICE]];
    }
}

- (void)peripheralManagerDidStartAdvertising:(CBPeripheralManager *)peripheral error:(NSError *)error{
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral central:(CBCentral *)central didSubscribeToCharacteristic:(CBCharacteristic *)characteristic{
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral central:(CBCentral *)central didUnsubscribeFromCharacteristic:(CBCharacteristic *)characteristic{
}

- (void)peripheralManagerIsReadyToUpdateSubscribers:(CBPeripheralManager *)peripheral{
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didReceiveReadRequest:(CBATTRequest *)request{
    CBATTRequest *newRequest = request;
    CBMutableCharacteristic *characteristic = (CBMutableCharacteristic *)request.characteristic;
    if (request.characteristic.value == nil) {
        if ([valueAndCharacteristicDic allKeysForObject:characteristic].count > 0) {
            NSString *characteristicValue = [[NSString alloc] initWithFormat:@"%@",[[valueAndCharacteristicDic allKeysForObject:characteristic] objectAtIndex:0]];
            Byte byte = (Byte)[characteristicValue intValue];
            NSData *data = [NSData dataWithBytes:&byte length:1];
            characteristic.value = data;
            newRequest.value = data;
        }
    }else{
        newRequest.value = request.characteristic.value;
    }
    [peripheral respondToRequest:newRequest withResult:CBATTErrorSuccess];
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didReceiveWriteRequests:(NSArray *)requests{
    CBATTRequest *requestLS = [requests objectAtIndex:0];
    [peripheral respondToRequest:requestLS withResult:CBATTErrorSuccess];
}

#pragma mark -
#pragma mark CBCentralManagerDelegate
- (void)centralManagerDidUpdateState:(CBCentralManager *)central {
    if (myCentralManager.state  != CBCentralManagerStatePoweredOn){
        bluetoothState = IS_FALSE;
        [self.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@')",EVENT_BLUETOOTHCLOSE]];
    }else{
        bluetoothState = IS_TRUE;
        [self.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@')",EVENT_BLUETOOTHOPEN]];
    }
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI{
    if (_peripherals.count == 0){
        _peripherals = [[NSMutableArray alloc] initWithObjects:peripheral,nil];
    }else{
        BOOL isAdd = TRUE;
        NSString *peripheralUUID = [[NSString alloc] init];
        NSString *oldPeripheralUUID = [[NSString alloc] init];
        peripheralUUID = [self getPeripheralUUID:peripheral];
        for (int i = 0; i < [_peripherals count]; i++)
        {
            CBPeripheral *oldPeripheral = [_peripherals objectAtIndex:i];
            oldPeripheralUUID = [self getPeripheralUUID:oldPeripheral];
            if ([peripheralUUID isEqualToString:oldPeripheralUUID] == YES){
                isAdd = FALSE;
            }
        }
        if (isAdd){
            [_peripherals addObject:peripheral];
        }
    }
    [self addPeripheralToAllPeripherals:_peripherals];
    advDataDic = [self getAdvertisementData:advertisementData];
    [RSSIDic setValue:[NSString stringWithFormat:@"%@",RSSI] forKey:[self getPeripheralUUID:peripheral]];
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:SUCCESS forKey:MES];
    [callbackInfo setValue:deviceID forKey:DEVICE_ID];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
}

- (void)centralManager:(CBCentralManager *)central didRetrieveConnectedPeripherals:(NSArray *)peripherals{
    NSMutableArray *callbackInfo = [[NSMutableArray alloc] init];
    NSMutableArray *peripheralObjects = [NSMutableArray arrayWithArray:peripherals];
    callbackInfo = [self storePeripheralInfo:peripheralObjects];
    NSMutableArray *myPeripherals = [NSMutableArray arrayWithArray:peripherals];
    [self addPeripheralToAllPeripherals:myPeripherals];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:GETCONNECTEDDEVICES]];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    if (!error) {
        [self error:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
    }
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error
{
    NSString *deviceID = [self getPeripheralUUID:aPeripheral];
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        [callbackInfo setValue:deviceID forKey:DEVICE_ID];
        isAddAllData=FALSE;
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
    }else{
        if (isConnectedByManager) {
            [self error:deviceID];
        }else{
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            [callbackInfo setValue:SUCCESS forKey:MES];
            [callbackInfo setValue:deviceID forKey:DEVICE_ID];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [result setKeepCallbackAsBool:TRUE];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:EVENT_DISCONNECT]];
        }
    }
}

- (void)peripheralDidUpdateRSSI:(CBPeripheral *)peripheral error:(NSError *)error
{
    if (!error) {
        NSString *RSSI = [NSString stringWithFormat:@"%@",[peripheral.RSSI description]];
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:RSSI forKey:PERIPHERAL_RSSI];
        [callbackInfo setValue:[self getPeripheralUUID:peripheral] forKey:DEVICE_ID];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"getRssi%@",[self getPeripheralUUID:peripheral]]]];
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"getRssi%@",[self getPeripheralUUID:peripheral]]]];
    }
}

#pragma mark -
#pragma mark CBPeripheralDelegate

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error {
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    if (!error) {
        if (isAddAllData) {
            [self getServiceInfo:peripheral];
        }else{
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            callbackInfo = [self storeServiceInfo:peripheral];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
        }
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] objectForKey:deviceID]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    if (!error) {
        NSString *deviceID = [self getPeripheralUUID:peripheral];
        if (isAddAllData) {
            [self getAllCharacteristicInfo:service peripheral:peripheral];
        }else{
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            callbackInfo = [self storeChatacteristicInfo:peripheral service:service];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%d%@",[self getServiceIndex:peripheral service:service],deviceID]]];        }
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%d%@",[self getServiceIndex:peripheral service:service],[NSString stringWithFormat:@"%@",[self getPeripheralUUID:peripheral]]]]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    if (!error) {
        if (isAddAllData) {
            [self addDescriptorArray:peripheral CBCharacteristic:characteristic];
        }else{
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            callbackInfo = [self storeDescriptorInfo:peripheral characteristic:characteristic];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%d%d%@",[self getCharacterIndex:characteristic.service character:characteristic],[self getServiceIndex:peripheral service:characteristic.service],deviceID]]];        }
    }else{
        if (isAddAllData) {
            [self addDescriptorArray:peripheral CBCharacteristic:characteristic];
        }else{
            [self error:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%d%d%@",[self getCharacterIndex:characteristic.service character:characteristic],[self getServiceIndex:peripheral service:characteristic.service],deviceID]]];
        }
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (!error) {
        NSString *deviceID = [self getPeripheralUUID:peripheral];
        NSString *date = [NSString stringWithFormat:@"%@",[self getDate]];
        CBService *service = characteristic.service;
        if ([[NSUserDefaults standardUserDefaults] valueForKey:ISON]) {
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            NSString *serviceIndex = [NSString stringWithFormat:@"%d",[self getServiceIndex:peripheral service:service]];
            NSString *characteristicIndex = [NSString stringWithFormat:@"%d",[self getCharacterIndex:service character:characteristic]];
            NSString *value = [NSString stringWithFormat:@"%@",[self getBase64EncodedFromData:[characteristic value]]];
            [callbackInfo setValue:value forKey:VALUE];
            [callbackInfo setValue:date forKey:DATE];
            [callbackInfo setValue:deviceID forKey:DEVICE_ID];
            [callbackInfo setValue:serviceIndex forKey:SERVICE_INDEX];
            [callbackInfo setValue:characteristicIndex forKey:CHARACTERISTIC_INDEX];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [result setKeepCallbackAsBool:TRUE];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:SETNOTIFICATION]];
        }
        if (isRead){
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            NSString *value = [NSString stringWithFormat:@"%@",[self getBase64EncodedFromData:[characteristic value]]];
            [callbackInfo setValue:value forKey:VALUE];
            [callbackInfo setValue:date forKey:DATE];
            CDVPluginResult* result;
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:READ]];
            isRead = FALSE;
        }
    }else{
        if (isRead) {
            [self error:[[NSUserDefaults standardUserDefaults] valueForKey:READ]];
        }
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error{
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        NSString *date = [NSString stringWithFormat:@"%@",[self getDate]];
        NSString *descriptorValue = [NSString stringWithFormat:@"%@",descriptor.value];
        NSData *descriptorData = [descriptorValue dataUsingEncoding:NSUTF8StringEncoding];
        NSString *value = [NSString stringWithFormat:@"%@",[self getBase64EncodedFromData:descriptorData]];
        [callbackInfo setValue:value forKey:VALUE];
        [callbackInfo setValue:date forKey:DATE];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:READ]];
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] valueForKey:READ]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:WRITE]];
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] valueForKey:WRITE]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error{
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:WRITE]];
    }else{
        [self error:[[NSUserDefaults standardUserDefaults] valueForKey:WRITE]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    NSString *enable = [NSString stringWithFormat:@"%@",[[NSUserDefaults standardUserDefaults] valueForKey:ISON]];
    if (!error) {
        if ([enable boolValue]) {
            [peripheral readValueForCharacteristic:characteristic];
        }else{
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:SETNOTIFICATION]];
        }
    }else{
        [self error:[[NSUserDefaults standardUserDefaults]valueForKey: @"setNotification"]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverIncludedServicesForService:(CBService *)service error:(NSError *)error {
}

# pragma mark -
# pragma mark MISC
# pragma mark -

- (BOOL)existCommandArguments:(NSArray*)comArguments{
    NSMutableArray *commandArguments=[[NSMutableArray alloc] initWithArray:comArguments];
    if (commandArguments.count > 0) {
        return TRUE;
    }else{
        return FALSE;
    }
}

- (BOOL)isNormalString:(NSString*)string{
    if (![string isEqualToString:@"null"] && string.length > 0){
        return TRUE;
    }else{
        return FALSE;
    }
}

- (NSString*)parseStringFromJS:(NSArray*)commandArguments keyFromJS:(NSString*)key{
    NSString *string = [NSString stringWithFormat:@"%@",[[commandArguments objectAtIndex:0] valueForKey:key]];
    return string;
}

- (NSMutableArray*)parseArrayFromJS:(NSArray*)commandArguments keyFromJS:(NSString*)key{
    NSMutableArray *array = [[NSMutableArray alloc] initWithArray:[[commandArguments objectAtIndex:0] valueForKey:key]];
    return array;
}

- (NSMutableDictionary*)parseDictionaryFromJS:(NSArray*)commandArguments keyFromJS:(NSString*)key{
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] initWithDictionary:[[commandArguments objectAtIndex:0] valueForKey:key]];
    return dictionary;
}

- (NSString*)getBase64EncodedFromData:(NSData*)data{
    NSData *newData = [[NSData alloc] initWithData:data];
    NSString *value = [[NSString alloc] init];
    value = [newData base64EncodedString];
    return value;
}

- (void)error:(NSString *)string{
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:ERROR forKey:MES];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:string];
}

- (NSString*)getDate{
    NSDate *valueDate = [NSDate date];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = DATE_FORMATE;
    NSString *dateString = [dateFormatter stringFromDate:valueDate];
    return dateString;
}

- (NSMutableArray*)storePeripheralInfo:(NSMutableArray*)peripheralObjs{
    NSMutableArray* callbackInfo = [[NSMutableArray alloc] init];
    if (peripheralObjs.count > 0) {
        for (int i = 0; i < peripheralObjs.count; i++)
        {
            NSMutableDictionary *peripheralInfo = [[NSMutableDictionary alloc] init];
            CBPeripheral *peripheral = [peripheralObjs objectAtIndex:i];
            NSString *peripheralUUID = [NSString stringWithFormat:@"%@",[self getPeripheralUUID:peripheral]];
            if ([peripheralObjs objectAtIndex:i] != nil){
                if ([[peripheralObjs objectAtIndex:i] name] != nil) {
                    [peripheralInfo setValue:[[peripheralObjs objectAtIndex:i] name] forKey:DEVICE_NAME];
                }else {
                    [peripheralInfo setValue:@"null" forKey:DEVICE_NAME];
                }
                if (peripheralUUID != nil) {
                    [peripheralInfo setValue:peripheralUUID forKey:DEVICE_ID];
                    if ([peripheralUUID isEqualToString:@"NULL"]) {
                        NSString *peripherialIndex = [NSString stringWithFormat:@"%d",i];
                        [peripheralInfo setValue:peripherialIndex forKey:DEVICE_ID];
                    }
                }else {
                    [peripheralInfo setValue:@"null" forKey:DEVICE_ID];
                }
                if ([[peripheralObjs objectAtIndex:i] isConnected]) {
                    [peripheralInfo setValue:IS_TRUE forKey:IS_CONNECTED];
                }else {
                    [peripheralInfo setValue:IS_FALSE forKey:IS_CONNECTED];
                }
                if (advDataDic) {
                    [peripheralInfo setValue:[advDataDic valueForKey:peripheralUUID] forKey:ADVERTISEMENT_DATA];
                }
                if (RSSIDic) {
                    [peripheralInfo setValue:[RSSIDic valueForKey:peripheralUUID] forKey:PERIPHERAL_RSSI];
                }
            }
            [callbackInfo addObject:peripheralInfo];
        }
    }
    return callbackInfo;
}

- (NSMutableDictionary *)storeServiceInfo:(CBPeripheral*)peripheral{
    if (!_services){
        _services = [[NSMutableArray alloc] init];
    }else{
        [_services removeAllObjects];
    }
    if (!servicesInfo) {
        servicesInfo = [[NSMutableArray alloc] init];
    }else{
        [servicesInfo removeAllObjects];
    }
    for(int i = 0; i < peripheral.services.count; i++){
        CBService *service = [peripheral.services objectAtIndex:i];
        [_services addObject:service];
        NSMutableDictionary *serviceInformation = [[NSMutableDictionary alloc] init];
        [serviceInformation setValue:[self getServiceName:service.UUID] forKey:SERVICE_NAME];
        [serviceInformation setValue:[NSString stringWithFormat:@"%@",[self CBUUIDFiltrToString:service.UUID]] forKey:SERVICE_UUID];
        [serviceInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:SERVICE_INDEX];
        [servicesInfo addObject:serviceInformation];
    }
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *serviceAndDeviceID = [[NSMutableDictionary alloc] init];
    [serviceAndDeviceID setValue:servicesInfo forKey:SERVICES];
    [serviceAndDeviceID setValue:deviceID forKey:DEVICE_ID];
    return serviceAndDeviceID;
}

- (NSMutableDictionary *)storeChatacteristicInfo:(CBPeripheral*)peripheral service:(CBService*)service{
    if (!_characteristics) {
        _characteristics = [[NSMutableArray alloc] init];
    }else{
        [_characteristics removeAllObjects];
    }
    if (!characteristicsInfo) {
        characteristicsInfo = [[NSMutableArray alloc] init];
    }else{
        [characteristicsInfo removeAllObjects];
    }
    for (int i = 0; i < service.characteristics.count; i++) {
        CBCharacteristic *character = [service.characteristics objectAtIndex:i];
        [_characteristics addObject:character];
        NSMutableDictionary *characteristicInformation = [[NSMutableDictionary alloc] init];
        [characteristicInformation setValue:[self getServiceName:character.UUID] forKey:CHARACTERISTIC_NAME];
        [characteristicInformation setValue:[self CBUUIDFiltrToString:character.UUID] forKey:CHARACTERISTIC_UUID];
        [characteristicInformation setValue:[self printCharacteristicProperties:character] forKey:CHARACTERISTIC_PROPERTY];
        [characteristicInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:CHARACTERISTIC_INDEX];
        [characteristicsInfo addObject:characteristicInformation];
    }
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *characteristicAndDeviceID = [[NSMutableDictionary alloc] init];
    [characteristicAndDeviceID setValue:characteristicsInfo forKey:CHARACTERISTICS];
    [characteristicAndDeviceID setValue:deviceID forKey:DEVICE_ID];
    return characteristicAndDeviceID;
}

- (NSMutableDictionary *)storeDescriptorInfo:(CBPeripheral*)peripheral characteristic:(CBCharacteristic*)characteristic{
    if (!_descriptors) {
        _descriptors = [[NSMutableArray alloc] init];
    }else{
        [_descriptors removeAllObjects];
    }
    if (!descriptorsInfo) {
        descriptorsInfo = [[NSMutableArray alloc] init];
    }else{
        [descriptorsInfo removeAllObjects];
    }
    for (int i = 0; i < characteristic.descriptors.count; i++) {
        CBDescriptor *descriptor = [characteristic.descriptors objectAtIndex:i];
        [_descriptors addObject:descriptor];
        NSMutableDictionary *descriptorInformation = [[NSMutableDictionary alloc] init];
        [descriptorInformation setValue:[self getServiceName:descriptor.UUID] forKey:DESCRIPTOR_NAME];
        [descriptorInformation setValue:[self CBUUIDFiltrToString:descriptor.UUID] forKey:DESCRIPTOR_UUID];
        [descriptorInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:DESCRIPTOR_INDEX];
        [descriptorsInfo addObject:descriptorInformation];
    }
    NSString *deviceID = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *descriptorAndDeviceID = [[NSMutableDictionary alloc] init];
    [descriptorAndDeviceID setValue:descriptorsInfo forKey:DESCRIPTORS];
    [descriptorAndDeviceID setValue:deviceID forKey:DEVICE_ID];
    return descriptorAndDeviceID;
}

- (void)addPeripheralToAllPeripherals:(NSMutableArray*)peripheralObj{
    if (_allPeripherals.count == 0) {
        if (peripheralObj.count > 0) {
            for (int j = 0; j < peripheralObj.count; j++) {
                CBPeripheral *myPeripheral = [peripheralObj objectAtIndex:j];
                [_allPeripherals addObject:myPeripheral];
            }
        }
    }else{
        if (peripheralObj.count > 0) {
            for (int i = 0; i < peripheralObj.count; i++) {
                BOOL isAddAll = TRUE;
                CBPeripheral *myPeripheral = [peripheralObj objectAtIndex:i];
                NSString *myPeripheralUUID = [self getPeripheralUUID:myPeripheral];
                for (int j = 0; j < _allPeripherals.count; j++) {
                    CBPeripheral *peripheralFromAll = [_allPeripherals objectAtIndex:j];
                    NSString *peripheralFromAllUUID = [self getPeripheralUUID:peripheralFromAll];
                    if ([myPeripheralUUID isEqualToString:peripheralFromAllUUID] == YES){
                        isAddAll = FALSE;
                    }
                }
                if (isAddAll){
                    [_allPeripherals addObject:myPeripheral];
                }
            }
        }
    }
}

- (CBCharacteristicProperties )property:(NSArray *)propertyArr{
    CBCharacteristicProperties property = 0;
    if (propertyArr.count > 0) {
        for (int i = 0; i < propertyArr.count; i++) {
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_READ]){
                property = property | CBCharacteristicPropertyRead;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_WRITE_WITHOUT_RESPONSE]){
                property = property | CBCharacteristicPropertyWriteWithoutResponse;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_WRITE]){
                property = property | CBCharacteristicPropertyWrite;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_NOTIFY]){
                property = property | CBCharacteristicPropertyNotify;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_INDICATE]){
                property = property | CBCharacteristicPropertyIndicate;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_AUTHENTICATED_SIGNED_WTRTES]){
                property = property | CBCharacteristicPropertyAuthenticatedSignedWrites;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_NOTIFY_ENCRYPTION_REQUIRED]){
                property = property | CBCharacteristicPropertyNotifyEncryptionRequired;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_INDICATE_ENCRYPTION_REQUIRED]){
                property = property | CBCharacteristicPropertyIndicateEncryptionRequired;
            }
        }
    }
    return property;
}

- (CBAttributePermissions )permission:(NSArray *)propertyArr{
    CBAttributePermissions permission = 0;
    if (propertyArr.count > 0) {
        for (int i = 0; i < propertyArr.count; i++) {
            if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_READ]){
                permission = permission | CBAttributePermissionsReadable;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_WRITE]){
                permission = permission | CBAttributePermissionsWriteable;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_READ_ENCRYPTED]){
                permission = permission | CBAttributePermissionsReadEncryptionRequired;
            }
            if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_WRITE_ENCRYPTED]){
                permission = permission | CBAttributePermissionsWriteEncryptionRequired;
            }
        }
        return permission;
    }else{
        return 0x00;
    }
}

- (NSString *)CBUUIDFiltrToString:(CBUUID *)UUID{
    NSString *results = [UUID.data description];
    results = [results stringByReplacingOccurrencesOfString:@"<" withString:@"0000"];
    results = [results stringByReplacingOccurrencesOfString:@">" withString:@"-0000-1000-8000-00805f9b34fb"];
    return results;
}

- (const char *) UUIDToString:(CFUUIDRef)UUID {
    if (!UUID) return "NULL";
    CFStringRef uuid = CFUUIDCreateString(NULL, UUID);
    return CFStringGetCStringPtr(uuid, 0);
    
}

-(NSData*)stringToByte:(NSString*)string{
    NSString *hexString = [[string uppercaseString] stringByReplacingOccurrencesOfString:@" " withString:@""];
    if ([hexString length]%2 != 0) {
        return nil;
    }
    Byte tempbyt[1] = {0};
    NSMutableArray *arryByte = [[NSMutableArray alloc] init];
    NSMutableData *bytes = [NSMutableData data];
    for(int i = 0;i < [hexString length];i++){
        unichar hex_char1 = [hexString characterAtIndex:i];
        int int_ch1;
        if(hex_char1 >= '0' && hex_char1 <='9'){
            int_ch1 = (hex_char1-48)*16;
        }else if(hex_char1 >= 'A' && hex_char1 <='F'){
            int_ch1 = (hex_char1-55)*16;
        }else{
            return nil;
        }
        i++;
        
        unichar hex_char2 = [hexString characterAtIndex:i];
        int int_ch2;
        if(hex_char2 >= '0' && hex_char2 <='9'){
            int_ch2 = (hex_char2-48);
        }else if(hex_char2 >= 'A' && hex_char2 <='F'){
            int_ch2 = hex_char2-55;
        }else{
            return nil;
        }
        
        [arryByte addObject:[NSString stringWithFormat:@"%i",int_ch1+int_ch2]];
    }
    if (arryByte.count > 0) {
        for (int i = arryByte.count-1; i >= 0; i--) {
            tempbyt[0] = [[NSString stringWithFormat:@"%@",[arryByte objectAtIndex:i]] intValue];
            [bytes appendBytes:tempbyt length:1];
        }
    }
    return bytes;
}

- (NSMutableArray *)getUUIDArr:(NSMutableArray *)array{
    if (array.count > 0) {
        NSMutableArray *UUIDs = [[NSMutableArray alloc] init];
        for (int i = 0; i < array.count; i++) {
            NSString *uuidStr = [array objectAtIndex:i];
            CBUUID *uuid = [CBUUID UUIDWithNSUUID:[[NSUUID alloc] initWithUUIDString:uuidStr]];
            [UUIDs addObject:uuid];
        }
        return UUIDs;
    }else{
        return nil;
    }
}

- (CBPeripheral *)getPeripheral:(NSString *)strDeviceID{
    CBPeripheral *peripheral=nil;
    if (_allPeripherals.count > 0) {
        for (int i = 0; i < [_allPeripherals count]; i++)
        {
            CBPeripheral* peripheral = [_allPeripherals objectAtIndex:i];
            const char *peripheralUUIDChar = [self UUIDToString:peripheral.UUID];
            NSString *peripheralUUIDStr = [NSString stringWithFormat:@"%s",peripheralUUIDChar];
            if ([[[NSUUID alloc] initWithUUIDString:peripheralUUIDStr] isEqual:[[NSUUID alloc] initWithUUIDString:strDeviceID]]) {
                return peripheral;
            }
            NSString *peripheralIndex = [NSString stringWithFormat:@"%d",i];
            if([peripheralIndex isEqualToString:strDeviceID]){
                return peripheral;
            }
        }
    }
    return peripheral;
}

- (NSString *)getPeripheralUUID:(CBPeripheral *)peripheral{
    CBPeripheral *newPeripheral = peripheral;
    const char *newPeripheralUUIDChar = [self UUIDToString:newPeripheral.UUID];
    NSString *newPeripheralUUIDStr = [[NSString alloc] init];
    if (!newPeripheralUUIDChar) {
        newPeripheralUUIDStr = NOTAVAILABLE;
    }else{
        newPeripheralUUIDStr = [NSString stringWithFormat:@"%s",newPeripheralUUIDChar];
    }
    return newPeripheralUUIDStr;
}

- (int)getServiceIndex:(CBPeripheral *)peripheral service:(CBService *)service{
    CBPeripheral *newPeripheral = peripheral;
    int serviceIndex = 0;
    if (newPeripheral.services.count > 0) {
        for (int i = 0; i < newPeripheral.services.count; i++) {
            if ([service isEqual:[newPeripheral.services objectAtIndex:i]]) {
                serviceIndex = i;
            }
        }
    }
    return serviceIndex;
}

- (int)getCharacterIndex:(CBService *)service character:(CBCharacteristic *)characteristic{
    int characteristicIndex = 0;
    if (service.characteristics.count > 0) {
        for (int i = 0; i < service.characteristics.count; i++) {
            if ([characteristic isEqual:[service.characteristics objectAtIndex:i]]) {
                characteristicIndex = i;
            }
        }
    }
    return characteristicIndex;
}

- (NSMutableDictionary *)getAdvertisementData:(NSDictionary *)advertisementData
{
    NSMutableDictionary *advertisementDataDic = [[NSMutableDictionary alloc] init];
    NSMutableArray *serviceUUIDs = [[NSMutableArray alloc] init];
    NSMutableArray *overFlowServiceUUIDs = [[NSMutableArray alloc] init];
    NSMutableArray *solicitServiceUUIDs = [[NSMutableArray alloc] init];
    
    if ([advertisementData valueForKey:KCBADVDATA_LOCALNAME]){
        NSString *localName = [NSString stringWithFormat:@"%@",[advertisementData valueForKey:KCBADVDATA_LOCALNAME]];
        [advertisementDataDic setValue:localName forKey:LOCAL_NAME];
    }
    if ([advertisementData valueForKey:KCBADVDATA_SERVICE_UUIDS]){
        NSMutableArray *advServiceUUIDs = [[NSMutableArray alloc] init];
        advServiceUUIDs = [advertisementData valueForKey:KCBADVDATA_SERVICE_UUIDS];
        for (int i = 0; i < advServiceUUIDs.count; i++) {
            CBUUID *UUID = [[advertisementData valueForKey:KCBADVDATA_SERVICE_UUIDS] objectAtIndex:i];
            NSString *UUIDStr = [self CBUUIDFiltrToString:UUID];
            [serviceUUIDs addObject:UUIDStr];
        }
        [advertisementDataDic setValue:serviceUUIDs forKey:SERVICE_UUIDS];
    }
    if ([advertisementData valueForKey:KCBADVDATA_TXPOWER_LEVEL]){
        NSString *txPowerLevel = [NSString stringWithFormat:@"%@",[advertisementData valueForKey:KCBADVDATA_TXPOWER_LEVEL]];
        [advertisementDataDic setValue:txPowerLevel forKey:TXPOWER_LEVEL];
    }
    if ([advertisementData valueForKey:KCBADVDATA_SERVICE_DATA]){
        NSString *serviceData = [NSString stringWithFormat:@"%@",[advertisementData valueForKey:KCBADVDATA_SERVICE_DATA]];
        [advertisementDataDic setValue:serviceData forKey:SERVICE_DATA];
    }
    if ([advertisementData valueForKey:KCBADVDATALOCAL_NAME]){
        NSString *manufacturerData = [NSString stringWithFormat:@"%@",[advertisementData valueForKey:KCBADVDATALOCAL_NAME]];
        [advertisementDataDic setValue:manufacturerData forKey:MANUFACTURER_DATA];
    }
    if ([advertisementData valueForKey:KCBADVDATA_OVERFLOW_SERVICE_UUIDS]){
        NSMutableArray *overFlowAdvServiceUUIDs = [[NSMutableArray alloc] init];
        overFlowAdvServiceUUIDs = [advertisementData valueForKey:KCBADVDATA_OVERFLOW_SERVICE_UUIDS];
        for (int i = 0; i < overFlowAdvServiceUUIDs.count; i++) {
            CBUUID *UUID = [[advertisementData valueForKey:KCBADVDATA_OVERFLOW_SERVICE_UUIDS] objectAtIndex:i];
            NSString *UUIDStr = [self CBUUIDFiltrToString:UUID];
            [overFlowServiceUUIDs addObject:UUIDStr];
        }
        [advertisementDataDic setValue:overFlowServiceUUIDs forKey:OVERFLOW_SERVICE_UUIDS];
    }
    if ([advertisementData valueForKey:KCBADVDATA_ISCONNECTABLE]){
        NSString *isConnectable = [NSString stringWithFormat:@"%@",[advertisementData valueForKey:KCBADVDATA_ISCONNECTABLE]];
        [advertisementDataDic setValue:isConnectable forKey:ISCONNECTABLE];
    }
    if ([advertisementData valueForKey:KCBADCDATA_SOLICITED_SERVICE_UUIDS]){
        NSMutableArray *solicitedAdvServiceUUIDs = [[NSMutableArray alloc] init];
        solicitedAdvServiceUUIDs = [advertisementData valueForKey:KCBADCDATA_SOLICITED_SERVICE_UUIDS];
        for (int i = 0; i < solicitedAdvServiceUUIDs.count; i++) {
            CBUUID *UUID = [[advertisementData valueForKey:KCBADCDATA_SOLICITED_SERVICE_UUIDS] objectAtIndex:i];
            NSString *UUIDStr = [self CBUUIDFiltrToString:UUID];
            [solicitServiceUUIDs addObject:UUIDStr];
        }
        [advertisementDataDic setValue:solicitServiceUUIDs forKey:SOLICITED_SERVICE_UUIDS];
    }
    return advertisementDataDic;
}

- (NSMutableArray *)printCharacteristicProperties:(CBCharacteristic *)characteristic
{
    CBCharacteristicProperties property = [characteristic properties];
    NSMutableArray *characteristicProperty = [[NSMutableArray alloc] init];
    if (property & CBCharacteristicPropertyRead){
        NSString *read = PROPERTY_READ;
        [characteristicProperty addObject:read];
    }
    if (property & CBCharacteristicPropertyWriteWithoutResponse){
        NSString *writeWithoutResponse = PROPERTY_WRITE_WITHOUT_RESPONSE;
        [characteristicProperty addObject:writeWithoutResponse];
    }
    if (property & CBCharacteristicPropertyWrite){
        NSString *write = PROPERTY_WRITE;
        [characteristicProperty addObject:write];
    }
    if (property & CBCharacteristicPropertyNotify){
        NSString *notify = PROPERTY_NOTIFY;
        [characteristicProperty addObject:notify];
    }
    if (property & CBCharacteristicPropertyIndicate) {
        NSString *indicate = PROPERTY_INDICATE;
        [characteristicProperty addObject:indicate];
    }
    if (property & CBCharacteristicPropertyAuthenticatedSignedWrites){
        NSString *authenticatedSignedWrites = PROPERTY_AUTHENTICATED_SIGNED_WTRTES;
        [characteristicProperty addObject:authenticatedSignedWrites];
    }
    if (property & CBCharacteristicPropertyNotifyEncryptionRequired){
        NSString *notifyEncryptionRequired = PROPERTY_NOTIFY_ENCRYPTION_REQUIRED;
        [characteristicProperty addObject:notifyEncryptionRequired];
    }
    if (property & CBCharacteristicPropertyIndicateEncryptionRequired){
        NSString *indicateEncryptionRequired = PROPERTY_INDICATE_ENCRYPTION_REQUIRED;
        [characteristicProperty addObject:indicateEncryptionRequired];
    }
    return characteristicProperty;
}

- (UInt16) CBUUIDToInt:(CBUUID *) UUID {
    char b1[16];
    [UUID.data getBytes:b1];
    return ((b1[0] << 8) | b1[1]);
}

- (NSString *)getServiceName:(CBUUID *)UUID{
    UInt16 _uuid = [self CBUUIDToInt:UUID];
    switch(_uuid)
    {
        case 0x1800: return @"Generic Access"; break;
        case 0x1801: return @"Generic Attribute"; break;
        case 0x1802: return @"Immediate Alert"; break;
        case 0x1803: return @"Link Loss"; break;
        case 0x1804: return @"Tx Power"; break;
        case 0x1805: return @"Current Time Service"; break;
        case 0x1806: return @"Reference Time Update Service"; break;
        case 0x1807: return @"Next DST Change Service"; break;
        case 0x1808: return @"Glucose"; break;
        case 0x1809: return @"Health Thermometer"; break;
        case 0x180A: return @"Device Information"; break;
        case 0x180B: return @"Network Availability Service"; break;
        case 0x180C: return @"Watchdog"; break;
        case 0x180D: return @"Heart Rate"; break;
        case 0x180E: return @"Phone Alert Status Service"; break;
        case 0x180F: return @"Battery Service"; break;
        case 0x1810: return @"Blood Pressure"; break;
        case 0x1811: return @"Alert Notification Service"; break;
        case 0x1812: return @"Human Interface Device"; break;
        case 0x1813: return @"Scan Parameters"; break;
        case 0x1814: return @"RUNNING SPEED AND CADENCE"; break;
        case 0x1815: return @"Automation IO"; break;
        case 0x1816: return @"Cycling Speed and Cadence"; break;
        case 0x1817: return @"Pulse Oximeter"; break;
        case 0x1818: return @"Cycling Power Service"; break;
        case 0x1819: return @"Location and Navigation Service"; break;
        case 0x181A: return @"Continous Glucose Measurement Service"; break;
        case 0x2A00: return @"Device Name"; break;
        case 0x2A01: return @"Appearance"; break;
        case 0x2A02: return @"Peripheral Privacy Flag"; break;
        case 0x2A03: return @"Reconnection Address"; break;
        case 0x2A04: return @"Peripheral Preferred Connection Parameters"; break;
        case 0x2A05: return @"Service Changed"; break;
        case 0x2A06: return @"Alert Level"; break;
        case 0x2A07: return @"Tx Power Level"; break;
        case 0x2A08: return @"Date Time"; break;
        case 0x2A09: return @"Day of Week"; break;
        case 0x2A0A: return @"Day Date Time"; break;
        case 0x2A0B: return @"Exact Time 100"; break;
        case 0x2A0C: return @"Exact Time 256"; break;
        case 0x2A0D: return @"DST Offset"; break;
        case 0x2A0E: return @"Time Zone"; break;
        case 0x2A0F: return @"Local Time Information"; break;
        case 0x2A10: return @"Secondary Time Zone"; break;
        case 0x2A11: return @"Time with DST"; break;
        case 0x2A12: return @"Time Accuracy"; break;
        case 0x2A13: return @"Time Source"; break;
        case 0x2A14: return @"Reference Time Information"; break;
        case 0x2A15: return @"Time Broadcast"; break;
        case 0x2A16: return @"Time Update Control Point"; break;
        case 0x2A17: return @"Time Update State"; break;
        case 0x2A18: return @"Glucose Measurement"; break;
        case 0x2A19: return @"Battery Level"; break;
        case 0x2A1A: return @"Battery Power State"; break;
        case 0x2A1B: return @"Battery Level State"; break;
        case 0x2A1C: return @"Temperature Measurement"; break;
        case 0x2A1D: return @"Temperature Type"; break;
        case 0x2A1E: return @"Intermediate Temperature"; break;
        case 0x2A1F: return @"Temperature in Celsius"; break;
        case 0x2A20: return @"Temperature in Fahrenheit"; break;
        case 0x2A21: return @"Measurement Interval"; break;
        case 0x2A22: return @"Boot Keyboard Input Report"; break;
        case 0x2A23: return @"System ID"; break;
        case 0x2A24: return @"Model Number String"; break;
        case 0x2A25: return @"Serial Number String"; break;
        case 0x2A26: return @"Firmware Revision String"; break;
        case 0x2A27: return @"Hardware Revision String"; break;
        case 0x2A28: return @"Software Revision String"; break;
        case 0x2A29: return @"Manufacturer Name String"; break;
        case 0x2A2A: return @"IEEE 11073-20601 Regulatory Certification Data List"; break;
        case 0x2A2B: return @"Current Time"; break;
        case 0x2A2C: return @"Elevation"; break;
        case 0x2A2D: return @"Latitude"; break;
        case 0x2A2E: return @"Longitude"; break;
        case 0x2A2F: return @"Position 2D"; break;
        case 0x2A30: return @"Position 3D"; break;
        case 0x2A31: return @"Scan Refresh"; break;
        case 0x2A32: return @"Boot Keyboard Output Report"; break;
        case 0x2A33: return @"Boot Mouse Input Report"; break;
        case 0x2A34: return @"Glucose Measurement Context"; break;
        case 0x2A35: return @"Blood Pressure Measurement"; break;
        case 0x2A36: return @"Intermediate Cuff Pressure"; break;
        case 0x2A37: return @"Heart Rate Measurement"; break;
        case 0x2A38: return @"Body Sensor Location"; break;
        case 0x2A39: return @"Heart Rate Control Point"; break;
        case 0x2A3A: return @"Removable"; break;
        case 0x2A3B: return @"Service Required"; break;
        case 0x2A3C: return @"Scientific Temperature in Celsius"; break;
        case 0x2A3D: return @"String"; break;
        case 0x2A3E: return @"Network Availability"; break;
        case 0x2A3F: return @"Alert Status"; break;
        case 0x2A40: return @"Ringer Control Point"; break;
        case 0x2A41: return @"Ringer Setting"; break;
        case 0x2A42: return @"Alert Category ID Bit Mask"; break;
        case 0x2A43: return @"Alert Category ID"; break;
        case 0x2A44: return @"Alert Notification Control Point"; break;
        case 0x2A45: return @"Unread Alert Status"; break;
        case 0x2A46: return @"New Alert"; break;
        case 0x2A47: return @"Supported New Alert Category"; break;
        case 0x2A48: return @"Supported Unread Alert Category"; break;
        case 0x2A49: return @"Blood Pressure Feature"; break;
        case 0x2A4A: return @"HID Information"; break;
        case 0x2A4B: return @"Report Map"; break;
        case 0x2A4C: return @"HID Control Point"; break;
        case 0x2A4D: return @"Report"; break;
        case 0x2A4E: return @"Protocol Mode"; break;
        case 0x2A4F: return @"Scan Interval Window"; break;
        case 0x2A50: return @"PnP ID"; break;
        case 0x2A51: return @"Glucose Features"; break;
        case 0x2A52: return @"Record Access Control Point"; break;
        case 0x2A53: return @"RSC Measurement"; break;
        case 0x2A54: return @"RSC Feature"; break;
        case 0x2A55: return @"SC Control Point"; break;
        case 0x2A56: return @"Digital Input"; break;
        case 0x2A57: return @"Digital Output"; break;
        case 0x2A58: return @"Analog Input"; break;
        case 0x2A59: return @"Analog Output"; break;
        case 0x2A5A: return @"Aggregate Input"; break;
        case 0x2A5B: return @"CSC Measurement"; break;
        case 0x2A5C: return @"CSC Feature"; break;
        case 0x2A5D: return @"Sensor Location"; break;
        case 0x2A5E: return @"Pulse Oximetry Spot-check Measurement"; break;
        case 0x2A5F: return @"Pulse Oximetry Continuous Measurement"; break;
        case 0x2A60: return @"Pulse Oximetry Pulsatile Event"; break;
        case 0x2A61: return @"Pulse Oximetry Features"; break;
        case 0x2A62: return @"Pulse Oximetry Control Point"; break;
        case 0x2A63: return @"Cycling Power Measurement Characteristic"; break;
        case 0x2A64: return @"Cycling Power Vector Characteristic"; break;
        case 0x2A65: return @"Cycling Power Feature Characteristic"; break;
        case 0x2A66: return @"Cycling Power Control Point Characteristic"; break;
        case 0x2A67: return @"Location and Speed Characteristic"; break;
        case 0x2A68: return @"Navigation Characteristic"; break;
        case 0x2A69: return @"Position Quality Characteristic"; break;
        case 0x2A6A: return @"LN Feature Characteristic"; break;
        case 0x2A6B: return @"LN Control Point Characteristic"; break;
        case 0x2A6C: return @"CGM Measurement Characteristic"; break;
        case 0x2A6D: return @"CGM Features Characteristic"; break;
        case 0x2A6E: return @"CGM Status Characteristic"; break;
        case 0x2A6F: return @"CGM Session Start Time Characteristic"; break;
        case 0x2A70: return @"Application Security Point Characteristic"; break;
        case 0x2A71: return @"CGM Specific Ops Control Point Characteristic"; break;
        default:
            return @"Custom Profile";
            break;
    }
}

@end