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

#define BLUETOOTH_STATE                                         @"state"
#define BLUETOOTH_OPEN                                          @"bluetoothopen"
#define BLUETOOTH_CLOSE                                         @"bluetoothclose"
#define DEVICE_NAME                                             @"deviceName"
#define DEVICE_ADDRESS                                          @"deviceAddress"
#define DEVICE_TYPE                                             @"deviceType"
#define PERIPHERALADDRESS                                       @"peripheralAddress"
#define MES                                                     @"mes"
#define DATA                                                    @"data"
#define ADVERTISEMENT_DATA                                      @"advertisementData"
#define SERVICES                                                @"services"
#define CHARACTERISTICS                                         @"characteristics"
#define DESCRIPTORS                                             @"descriptors"
#define SERVICE_INDEX                                           @"serviceIndex"
#define SERVICE_NAME                                            @"serviceName"
#define SERVICE_TYPE                                            @"serviceType"
#define SERVICE_UUID                                            @"serviceUUID"
#define UINQUE_ID                                               @"uniqueID"
#define CHARACTERISTIC_INDEX                                    @"characteristicIndex"
#define CHARACTERISTIC_VALUE                                    @"characteristicValue"
#define CHARACTERISTIC_NAME                                     @"characteristicName"
#define CHARACTERISTIC_PERMISSION                               @"characteristicPermission"
#define CHARACTERISTIC_PROPERTY                                 @"characteristicProperty"
#define CHARACTERISTIC_UUID                                     @"characteristicUUID"
#define CHARACTERISTIC_UUIDS                                    @"characteristicUUIDs"
#define CHARACTERISTIC_VALUE_TYPE                               @"characteristicValueType"
#define DESCRIPTOR_INDEX                                        @"descriptorIndex"
#define DESCRIPTOR_NAME                                         @"descriptorName"
#define DESCRIPTOR_PERMISSION                                   @"descriptorPermission"
#define DESCRIPTOR_UUID                                         @"descriptorUUID"
#define DESCRIPTOR_VALUE                                        @"descriptorValue"
#define DESCRIPTOR_VALUE_TYPE                                   @"descriptorValueType"
#define PERIPHERAL_RSSI                                         @"RSSI"
#define VALUE                                                   @"value"
#define DATE                                                    @"date"
#define DATE_FORMATE                                            @"HH:mm:ss:SSS"

#define NOTAVAILABLE                                            @"n/a"
#define SUCCESS                                                 @"success"
#define ERROR                                                   @"error"
#define IS_TRUE                                                 @"true"
#define IS_FALSE                                                @"false"
#define ENABLE                                                  @"enable"
#define IS_CONNECTED                                            @"isConnected"
#define DISCONNECT                                              @"disconnect"

#define PERMISSION_READ                                         @"read"
#define PERMISSION_READ_ENCRYPTED                               @"readEncrypted"
#define PERMISSION_READ_ENCRYPTED_MITM                          @"readEncryptedMitm"
#define PERMISSION_WRITE                                        @"write"
#define PERMISSION_WRITE_ENCRYPTED_MITM                         @"writeEncryptedMitm"
#define PERMISSION_WRITE_ENCRYPTED                              @"writeEncrypted"
#define PERMISSION_WRITE_SIGEND                                 @"writeSigend"
#define PERMISSION_WRITE_SIGEND_MITM                            @"writeSigendMitm"
#define PROPERTY_AUTHENTICATED_SIGNED_WTRTES                    @"authenticatedSignedWrites"
#define PROPERTY_BROADCAST                                      @"broadcast"
#define PROPERTY_EXTENDED_PROPERTIES                            @"extendedProperties"
#define PROPERTY_INDICATE                                       @"indicate"
#define PROPERTY_NOTIFY                                         @"notify"
#define PROPERTY_READ                                           @"read"
#define PROPERTY_WRITE                                          @"write"
#define PROPERTY_WRITE_WITHOUT_RESPONSE                         @"writeWithoutResponse"
#define PROPERTY_NOTIFY_ENCRYPTION_REQUIRED                     @"NotifyEncryptionRequired"
#define PROPERTY_INDICATE_ENCRYPTION_REQUIRED                   @"IndicateEncryptionRequired"

#define KCBADVDATA_LOCALNAME                                    @"kCBAdvDataLocalName"
#define LOCAL_NAME                                              @"localName"
#define KCBADVDATA_SERVICE_UUIDS                                @"kCBAdvDataServiceUUIDs"
#define SERVICE_UUIDS                                           @"serviceUUIDs"
#define KCBADVDATA_TXPOWER_LEVEL                                @"kCBAdvDataTxPowerLevel"
#define TXPOWER_LEVEL                                           @"txPowerLevel"
#define KCBADVDATA_SERVICE_DATA                                 @"kCBAdvDataServiceData"
#define SERVICE_DATA                                            @"serviceData"
#define KCBADVDATALOCAL_NAME                                    @"kCBAdvDataManufacturerData"
#define MANUFACTURER_DATA                                       @"manufacturerData"
#define KCBADVDATA_OVERFLOW_SERVICE_UUIDS                       @"kCBAdvDataOverflowServiceUUIDs"
#define OVERFLOW_SERVICE_UUIDS                                  @"overflowServiceUUIDs"
#define KCBADVDATA_ISCONNECTABLE                                @"kCBAdvDataIsConnectable"
#define ISCONNECTABLE                                           @"isConnectable"
#define KCBADCDATA_SOLICITED_SERVICE_UUIDS                      @"kCBAdvDataSolicitedServiceUUIDs"
#define SOLICITED_SERVICE_UUIDS                                 @"solicitedServiceUUIDs"

#define EVENT_NAME                                              @"eventName"
#define EVENT_DISCONNECT                                        @"disconnect"
#define EVENT_ONSUBSCRIBE                                       @"onsubscribe"
#define EVENT_ONUNSUBSCRIBE                                     @"onunsubscribe"
#define EVENT_ONCHARACTERISTICREAD                              @"oncharacteristicread"
#define EVENT_ONCHARACTERISTICWRITE                             @"oncharacteristicwrite"
#define EVENT_NEWADVPACKET                                      @"newadvpacket"
#define EVENT_BLUETOOTHOPEN                                     @"bluetoothopen"
#define EVENT_BLUETOOTHCLOSE                                    @"bluetoothclose"

#define GETBLUETOOTHSTATE                                       @"getBluetoothState"
#define BLUETOOTHSTARTSTATE                                     @"bluetoothState"
#define GETCONNECTEDDEVICES                                     @"getConnectedDevices"
#define SETNOTIFICATION                                         @"setNotification"
#define ADDSERVICE                                              @"addService"
#define ONREADREQUEST                                           @"onReadRequest"
#define ONWRIESTREQUEST                                         @"onWriteRequest"
#define WRITE_TYPE                                              @"writeType"
#define WRITE_VALUE                                             @"writeValue"
#define ISON                                                    @"isON"
#define READCHARACTERISTIC                                      @"readCharacteristor"
#define READDESCRIPTOR                                          @"readDescriptor"
#define WRITE                                                   @"write"
#define ON_READ_REQUEST                                         @"onReadRequest"
#define ON_WRITE_REQUEST                                        @"onWriteRequest"
#define WRITEREQUESTVALUE                                       @"writeRequestValue"
#define BASE_LONG_UUID                                          @"00000000-0000-1000-8000-00805f9b34fb"
#define LOGINFORMATION                                          @"logInformation"
#define BLE_DEVICETYPE                                          @"BLE"
#define APP_ID                                                  @"appID"
#define API                                                     @"api"
#define VERSION                                                 @"VERSION"
#define IOS                                                     @"ios"
#define BLUETOOTHSTATE                                          @"bluetooth_state"
#define IS_IOS_VERSION_7_HIGHER     (([[[UIDevice currentDevice] systemVersion] floatValue] >=7.0)? (YES):(NO))
#define GAP_MODUAL                  1
#define GATT_MODUAL                 2
#define DATA_MODUAL                 4
#define BCLOG_FUNC(modual)          [self logFunc:modual information:[NSString stringWithFormat:@"%s",__FUNCTION__]];
#define BCLOG_DATA(info,s,d,uuid)   [self logValue:info operation:s device:d UUID:uuid];
#define BCLOG_SCANDATA              [self logScanDeviceUUID:[self getPeripheralUUID:peripheral] RSSI:\
                                    [NSString stringWithFormat:@"%@",rssi] advData:[self getAdvertisementData:advertisementData]];
#define BCLOG_RSSI                  [self logRSSI:rssi device:[self getPeripheralUUID:peripheral]];
#define BCLOG_UUID(type)            [self logUUID:type.UUID];


@implementation BCBluetooth{
    NSMutableDictionary *peripheralAndUUID;
}

@synthesize bluetoothState;
@synthesize callbacks;
@synthesize pageInfomation;
@synthesize _peripherals;

@synthesize advDataDic;
@synthesize RSSIDic;

@synthesize serviceAndKeyDic;
@synthesize writeReqAndCharacteristicDic;
@synthesize readReqAndCharacteristicDic;
@synthesize valueAndCharacteristicDic;

@synthesize myPeripheralManager;
@synthesize myCentralManager;

@synthesize isEndOfAddService;
@synthesize stopScanTimer;
@synthesize isFindingPeripheral;

#pragma mark -
#pragma mark BC Interface
#pragma mark -


- (void)pluginInitialize
{
  [super pluginInitialize];

  peripheralAndUUID = [[NSMutableDictionary alloc] init];
  isEndOfAddService = FALSE;
  isFindingPeripheral = FALSE;
  self.isAdvertisingStopped = FALSE;

  myPeripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:nil];
  myCentralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];

  callbacks = [[NSMutableDictionary alloc] init];
  _peripherals = [[NSMutableArray alloc] init];
  advDataDic = [[NSMutableDictionary alloc] init];
  RSSIDic = [[NSMutableDictionary alloc] init];

  serviceAndKeyDic = [[NSMutableDictionary alloc] init];
  writeReqAndCharacteristicDic = [[NSMutableDictionary alloc] init];
  readReqAndCharacteristicDic = [[NSMutableDictionary alloc] init];
  valueAndCharacteristicDic = [[NSMutableDictionary alloc] init];
  bluetoothState = BLUETOOTHSTARTSTATE;
}

- (void)getEnvironment:(CDVInvokedUrlCommand *)command
{
  BCLOG_FUNC(GAP_MODUAL)
  NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
  [info setValue:[NSString stringWithFormat:@"%f",[[[UIDevice currentDevice] systemVersion] floatValue]] forKey:VERSION];
  [info setValue:IOS forKey:API];
  [info setValue:NOTAVAILABLE forKey:APP_ID];
  [info setValue:NOTAVAILABLE forKey:DEVICE_ADDRESS];
  [info setValue:BLE_DEVICETYPE forKeyPath:DEVICE_TYPE];
  CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];
  [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getBluetoothState:(CDVInvokedUrlCommand*)command
{
  BCLOG_FUNC(GAP_MODUAL)
  NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
  [info setValue:bluetoothState forKey:BLUETOOTH_STATE];
  CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];
  [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)addEventListener:(CDVInvokedUrlCommand *)command{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *eventName = [self getCommandArgument:command.arguments fromKey:EVENT_NAME];
    [self.callbacks setValue:command.callbackId forKey:eventName];
}

- (void)openBluetooth:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    [self error:command.callbackId];
}

- (void)startScan:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    NSMutableArray *service_uuids = [[NSMutableArray alloc] initWithArray:[[command.arguments objectAtIndex:0]
                                                                            valueForKey:SERVICE_UUIDS]];
    [self.myCentralManager scanForPeripheralsWithServices:service_uuids options:0];
    self.isFindingPeripheral = FALSE;

    NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
    [info setValue:SUCCESS forKey:MES];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)stopScan:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    [self.myCentralManager stopScan];
    self.isFindingPeripheral = TRUE;
    NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
    [info setValue:SUCCESS forKey:MES];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)creatPair:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    [self error:command.callbackId];
}

- (void)removePair:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    [self error:command.callbackId];
}

- (void)getPairedDevices:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    [self error:command.callbackId];
}

- (void)getConnectedDevices:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    NSArray *svcs = [NSArray arrayWithObjects:[CBUUID UUIDWithString:@"0000"], nil];
    [self.callbacks setValue:command.callbackId forKey:GETCONNECTEDDEVICES];
    [self.myCentralManager retrieveConnectedPeripheralsWithServices:svcs];
}

- (void)connect:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"connect%@",deviceAddress]];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    if (peripheral) {
        if (IS_IOS_VERSION_7_HIGHER) {
            if (peripheral.state == CBPeripheralStateConnected) {
                [self connectRequest:deviceAddress callbackId:command.callbackId isKeepCallback:FALSE];
            }else if(peripheral.state == CBPeripheralStateDisconnected){
                [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"connect%@",deviceAddress]];
                [self.myCentralManager connectPeripheral:peripheral options:nil];
            }
        }else{
            if ([peripheral state] == CBPeripheralStateConnected) {
                [self connectRequest:deviceAddress callbackId:command.callbackId isKeepCallback:FALSE];
            }else{
                [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"connect%@",deviceAddress]];
                [self.myCentralManager connectPeripheral:peripheral options:nil];
            }
        }
    }else{
        if (!self.isFindingPeripheral) {
            self.isFindingPeripheral = TRUE;
            [self.myCentralManager scanForPeripheralsWithServices:nil options:nil];
            [self.callbacks setValue:deviceAddress forKey:PERIPHERALADDRESS];
            stopScanTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(stopToScan) userInfo:nil repeats:NO];
        }else{
            [self error:command.callbackId];
        }
    }
}

- (void)connectRequest:(NSString *)deviceID callbackId:(NSString *)callback isKeepCallback:(BOOL)isKeepCallback{
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:SUCCESS forKey:MES];
    [callbackInfo setValue:deviceID forKey:DEVICE_ADDRESS];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    if (isKeepCallback) {
        [result setKeepCallbackAsBool:TRUE];
    }
    [self.commandDelegate sendPluginResult:result callbackId:callback];
}

- (void)stopToScan{
    BCLOG_FUNC(GAP_MODUAL)
    [self.myCentralManager stopScan];
    [self error:[self.callbacks valueForKey:PERIPHERALADDRESS]];
}

- (void)disconnect:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    if (peripheral) {
        [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"disConnect%@",deviceAddress]];
        [self.myCentralManager cancelPeripheralConnection:peripheral];
    }else{
        [self error:command.callbackId];
    }
}

- (void)getServices:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    CBPeripheral *peripheral=[self getPeripheral:deviceAddress];
    if (peripheral) {
        [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"getServices:%@",deviceAddress]];
        if (peripheral.services.count > 0){
            NSMutableDictionary *callbackInfo = [self storeServiceInfo:peripheral];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }else{
            peripheral.delegate = self;
            [peripheral discoverServices:nil];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getCharacteristics:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *serviceIndex = [self getCommandArgument:command.arguments fromKey:SERVICE_INDEX];
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    CBPeripheral *peripheral=[self getPeripheral:deviceAddress];
    if (peripheral && serviceIndex && (peripheral.services.count > serviceIndex.intValue)) {
        [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"%d%@",[serviceIndex intValue],deviceAddress]];
        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
        if (service.characteristics.count > 0) {
            NSMutableDictionary *callbackInfo = [self storeChatacteristicInfo:peripheral service:service];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }else{
            peripheral.delegate = self;
            [peripheral discoverCharacteristics:nil forService:service];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getDescriptors:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    NSString *serviceIndex  = [self getCommandArgument:command.arguments fromKey:SERVICE_INDEX];
    NSString *charateristicIndex = [self getCommandArgument:command.arguments fromKey:CHARACTERISTIC_INDEX];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    if (peripheral && serviceIndex && charateristicIndex && (serviceIndex.intValue < peripheral.services.count)){
        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
        if ((charateristicIndex.intValue < service.characteristics.count)) {
            [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"%d%d%@",[charateristicIndex intValue],
                                                                [serviceIndex intValue],deviceAddress]];
            CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[charateristicIndex intValue]];
            if (characteristic.descriptors.count > 0) {
                NSMutableDictionary *callbackInfo = [self storeDescriptorInfo:peripheral characteristic:characteristic];
                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            }else{
                peripheral.delegate = self;
                [peripheral discoverDescriptorsForCharacteristic:characteristic];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)getRSSI:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"getRssi%@",deviceAddress]];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    if (peripheral) {
        peripheral.delegate = self;
        [peripheral readRSSI];
    }else{
        [self error:command.callbackId];
    }
}

- (void)writeValue:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    [self.callbacks setValue:command.callbackId forKey:WRITE];
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    NSString *serviceIndex = [self getCommandArgument:command.arguments fromKey:SERVICE_INDEX];
    NSString *characteristicIndex = [self getCommandArgument:command.arguments fromKey:CHARACTERISTIC_INDEX];
    NSString *descriptorIndex = [self getCommandArgument:command.arguments fromKey:DESCRIPTOR_INDEX];
    NSString *valueWrite = [self getCommandArgument:command.arguments fromKey:WRITE_VALUE];
    NSData *data = [[NSData alloc] initWithBase64EncodedString:valueWrite options:0];
    if (peripheral && data && serviceIndex && characteristicIndex && (serviceIndex.intValue < peripheral.services.count)) {
        CBService *service=[peripheral.services objectAtIndex:[serviceIndex intValue]];
        if (service.characteristics.count > [characteristicIndex intValue]) {
            CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[characteristicIndex intValue]];
            if (descriptorIndex && descriptorIndex.length > 0){
                if (descriptorIndex.intValue < characteristic.descriptors.count) {
                    peripheral.delegate = self;
                    CBDescriptor *descripter = [characteristic.descriptors objectAtIndex: [descriptorIndex intValue] ];
                    [peripheral writeValue:data forDescriptor:descripter];
                    BCLOG_DATA(data,@"writeDescriptor",deviceAddress, [self CBUUIDToString:descripter.UUID])
                }else{
                    [self error:command.callbackId];
                }
            }else{
                peripheral.delegate = self;
                if (characteristic.properties & CBCharacteristicPropertyWriteWithoutResponse) {
                    [peripheral writeValue:data forCharacteristic:characteristic type:CBCharacteristicWriteWithoutResponse];
                    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                    [callbackInfo setValue:SUCCESS forKey:MES];
                    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];

                }else if (characteristic.properties & CBCharacteristicPropertyWrite){
                    [peripheral writeValue:data forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
                }
                BCLOG_DATA(data,@"writeCharacteristic",deviceAddress, [self CBUUIDToString:characteristic.UUID])
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)readValue:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    NSString *serviceIndex = [self getCommandArgument:command.arguments fromKey:SERVICE_INDEX];
    NSString *characteristicIndex = [self getCommandArgument:command.arguments fromKey:CHARACTERISTIC_INDEX];
    NSString *descriptorIndex = [self getCommandArgument:command.arguments fromKey:DESCRIPTOR_INDEX];
    CBPeripheral *peripheral=[self getPeripheral:deviceAddress];
    if (peripheral && serviceIndex && characteristicIndex && (serviceIndex.intValue < peripheral.services.count)) {
        CBService *service = [peripheral.services objectAtIndex:[serviceIndex intValue]];
        if (characteristicIndex.intValue < service.characteristics.count){
            CBCharacteristic *characteristic=[service.characteristics objectAtIndex:[characteristicIndex intValue]];
            if (descriptorIndex && descriptorIndex.length > 0){
                if (characteristic.descriptors.count > [descriptorIndex intValue]) {
                    [self.callbacks setValue:command.callbackId forKey:READDESCRIPTOR];
                    peripheral.delegate = self;
                    [peripheral readValueForDescriptor:[characteristic.descriptors objectAtIndex:[descriptorIndex intValue]]];
                }else{
                    [self error:command.callbackId];
                }
            }else{
                [self.callbacks setValue:command.callbackId forKey:READCHARACTERISTIC];
                peripheral.delegate = self;
                [peripheral readValueForCharacteristic:[service.characteristics objectAtIndex:[characteristicIndex intValue]]];
            }
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)setNotification:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    [self.callbacks setValue:command.callbackId forKey:SETNOTIFICATION];
    NSString *deviceAddress = [self getCommandArgument:command.arguments fromKey:DEVICE_ADDRESS];
    CBPeripheral *peripheral = [self getPeripheral:deviceAddress];
    NSString *serviceIndex = [self getCommandArgument:command.arguments fromKey:SERVICE_INDEX];
    NSString *characteristicIndex = [self getCommandArgument:command.arguments fromKey:CHARACTERISTIC_INDEX];
    NSString *enable = [self getCommandArgument:command.arguments fromKey:ENABLE];
    if (peripheral && serviceIndex && characteristicIndex && (serviceIndex.intValue < peripheral.services.count)) {
        [self.callbacks setValue:command.callbackId forKey:[NSString stringWithFormat:@"%@%@%@",serviceIndex,characteristicIndex,SETNOTIFICATION]];
        CBService *service = [peripheral.services objectAtIndex:serviceIndex.intValue];
        if (characteristicIndex.intValue < service.characteristics.count) {
            CBCharacteristic *characteristic = [service.characteristics objectAtIndex:[characteristicIndex intValue]];
            peripheral.delegate = self;
            [self.callbacks setValue:enable forKey:ISON];
            if ([enable boolValue]) {
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
            }else{
                [peripheral setNotifyValue:NO forCharacteristic:characteristic];
            }
        } else{
            [self error:command.callbackId];
        }
    } else{
        [self error:command.callbackId];
    }
}

- (void)getDeviceAllData:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(GATT_MODUAL)
    [self error:command.callbackId];
}

- (void)addServices:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(1)
    if ([self existCommandArguments:command.arguments]) {
        [self.callbacks setValue:command.callbackId forKey:ADDSERVICE];
 
        NSMutableArray *services = [[NSMutableArray alloc] initWithArray:[[command.arguments objectAtIndex:0] valueForKey:SERVICES]];
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
                        CBMutableCharacteristic *newCharacteristic = [self buildCharacteristicWithUUID:newCharacteristicUUID
                                properties:newCharacteristicProperty value:newCharacteristicValue permissions:newCharacteristicPermission];
                        [self.writeReqAndCharacteristicDic setValue:newCharacteristic forKey:onWriteRequest];
                        [self.readReqAndCharacteristicDic setValue:newCharacteristic forKey:onReadRequest];
                        [self.valueAndCharacteristicDic setValue:newCharacteristic forKey:newCharacteristicValue];
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
                if (!self.serviceAndKeyDic) {
                    self.serviceAndKeyDic = [[NSMutableDictionary alloc] init];
                }else{
                    [self.serviceAndKeyDic setValue:newService forKey:uniqueID];
                }
                [self.myPeripheralManager addService:newService];
                if (services.count == i + 1) {
                    self.isEndOfAddService = TRUE;
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

- (CBMutableCharacteristic *)buildCharacteristicWithUUID:(NSString *)uuidStr
        properties:(NSMutableArray *)properties value:(NSString *)dataStr permissions:(NSMutableArray *)permissions{
    Byte byte = (Byte)[dataStr intValue];
    NSData *data = [[NSData alloc] initWithBytes:&byte length:1];
    CBUUID *characteristicUUID = [CBUUID UUIDWithString:uuidStr];
    int propertyNum = [self property:properties] ^ CBCharacteristicPropertyRead;
    if (propertyNum == 0 || [self property:properties] == 0) {
        CBMutableCharacteristic *characteristic = [[CBMutableCharacteristic alloc]
            initWithType:characteristicUUID properties:CBCharacteristicPropertyRead value:data permissions:CBAttributePermissionsReadable];
        return characteristic;
    }else{
        CBMutableCharacteristic *characteristic;
        characteristic = [[CBMutableCharacteristic alloc] initWithType:characteristicUUID
                        properties:[self property:properties]  value:nil permissions:[self permission:permissions]];
        return characteristic;
    }
}

- (void)removeServices:(CDVInvokedUrlCommand*)command{
    BCLOG_FUNC(1)
    if ([self existCommandArguments:command.arguments]) {
        NSString *uniqueID = [self getCommandArgument:command.arguments fromKey:UINQUE_ID];
        BOOL remove = FALSE;
        for (NSString *str in [self.serviceAndKeyDic allKeys]) {
            if ([str isEqualToString:uniqueID]) {
                remove = TRUE;
            }
        }
        if (remove) {
            [self.myPeripheralManager removeService:[self.serviceAndKeyDic valueForKey:uniqueID]];
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            [callbackInfo setValue:SUCCESS forKey:MES];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }else{
            [self error:command.callbackId];
        }
    }else{
        [self error:command.callbackId];
    }
}

- (void)notify:(CDVInvokedUrlCommand*)command {
  if ([self existCommandArguments:command.arguments]) {
    NSData *data;
    BOOL notified;
    NSString *uniqueID;
    NSString *characteristicIndex;
    CBMutableCharacteristic *characteristic;

    uniqueID = [self getCommandArgument:command.arguments fromKey:UINQUE_ID];
      data = [[NSData alloc] initWithBase64EncodedString:[self getCommandArgument:command.arguments fromKey:DATA] options:0];
    characteristicIndex = [self getCommandArgument:command.arguments fromKey:CHARACTERISTIC_INDEX];
    characteristic = [self getNotifyCharacteristic:uniqueID characteristicIndex:characteristicIndex];

    notified = [self.self.myPeripheralManager updateValue:data forCharacteristic:characteristic onSubscribedCentrals:nil];

    if (notified){
      NSLog(@"-updateValue succeeded");
      CDVPluginResult* result;
      NSMutableDictionary *info;

      info = [[NSMutableDictionary alloc] init];
      [info setValue:SUCCESS forKey:MES];
      result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];

      [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    } else {
      NSLog(@"-updateValue postponed");

      self.notifyData = data;
      self.notifyCallbackId = command.callbackId;
      self.notifyCharacteristic = characteristic;
    }
  } else {
    NSLog(@"-notify was called with insufficient arguments");
    [self error:command.callbackId];
  }
}

/*--------------------------------------------------------------------------*/
#pragma mark -
#pragma mark - CBperipheralManagerDelegate
- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral
{
  switch (peripheral.state) {
  case CBPeripheralManagerStatePoweredOn:
    NSLog(@"****Bluetooth is powered ON.");
    return;

  case CBPeripheralManagerStatePoweredOff:
    NSLog(@"****Bluetooth is powered OFF.");
    if (self.isAdvertisingStopped) {
      NSLog(@"Started advertising after Bluetooth coming back on.");
      [self.myPeripheralManager startAdvertising:@{ CBAdvertisementDataLocalNameKey : @"Truma App",
        CBAdvertisementDataServiceUUIDsKey:@[[CBUUID UUIDWithString:@"61808880-b7b3-11e4-b3a4-0002a5d5c51b"]]}];
      self.isAdvertisingStopped = FALSE;
    }
    break;

  case CBPeripheralManagerStateUnauthorized:
    NSLog(@"****Bluetooth is NOT AUTHORIZED.");
    break;

  case CBPeripheralManagerStateUnsupported:
    NSLog(@"****Bluetooth is NOT SUPPORTED.");
    break;

  case CBPeripheralManagerStateResetting:
    NSLog(@"****Bluetooth is RESETTING.");
    break;

  case CBPeripheralManagerStateUnknown:
    NSLog(@"****Bluetooth STATE IS UNKNOWN.");
    break;

  default:
    NSLog(@"****Bluetooth STATE HAS INVALID VALUE.");
    break;
  }

  if ([self.callbacks objectForKey:ADDSERVICE]) {
    [self error:[self.callbacks objectForKey:ADDSERVICE]];
  }
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didAddService:(CBService *)service error:(NSError *)error{
  if (!error) {
    if (self.isEndOfAddService) {
      self.isEndOfAddService = FALSE;
      NSLog(@"Started advertising after adding the services.");
      [self.myPeripheralManager startAdvertising:@{ CBAdvertisementDataLocalNameKey : @"Truma App",
        CBAdvertisementDataServiceUUIDsKey:@[[CBUUID UUIDWithString:@"61808880-b7b3-11e4-b3a4-0002a5d5c51b"]]}];
      self.isAdvertisingStopped = FALSE;
      CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
      [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks objectForKey:ADDSERVICE]];
    }
  }else{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks objectForKey:ADDSERVICE]];
  }
}

- (void)peripheralManagerDidStartAdvertising:(CBPeripheralManager *)peripheral error:(NSError *)error{
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral central:(CBCentral *)central
    didSubscribeToCharacteristic:(CBCharacteristic *)characteristic
{
  CBService *service;
  CDVPluginResult* result;
  NSMutableDictionary *callbackInfo;
  CBCharacteristic *characteristicNotify;

  characteristicNotify = characteristic;
  service = characteristicNotify.service;
  callbackInfo = [self getUniqueIDWithService:service andCharacteristicIndex:characteristicNotify];
  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];

  // stop advertising
  NSLog(@"Stopped advertising.");
  [peripheral stopAdvertising];
  self.isAdvertisingStopped = TRUE;

  [result setKeepCallbackAsBool:TRUE];
  [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:EVENT_ONSUBSCRIBE]];
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral central:(CBCentral *)central
    didUnsubscribeFromCharacteristic:(CBCharacteristic *)characteristic{
  CBService *service;
  CDVPluginResult* result;
  NSMutableDictionary *callbackInfo;
  CBCharacteristic *characteristicNotify;

  characteristicNotify = characteristic;
  service = characteristicNotify.service;
  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
  callbackInfo = [self getUniqueIDWithService:service andCharacteristicIndex:characteristicNotify];

  NSLog(@"Started advertising.");
  [self.myPeripheralManager startAdvertising:@{ CBAdvertisementDataLocalNameKey : @"Truma App",
    CBAdvertisementDataServiceUUIDsKey:@[[CBUUID UUIDWithString:@"61808880-b7b3-11e4-b3a4-0002a5d5c51b"]]}];
  self.isAdvertisingStopped = FALSE;

  [result setKeepCallbackAsBool:TRUE];
  [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:EVENT_ONUNSUBSCRIBE]];
}

- (void)peripheralManagerIsReadyToUpdateSubscribers:(CBPeripheralManager *)peripheral
{
  BOOL notified;

  if (self.notifyData)
  {
    notified = [peripheral updateValue:self.notifyData forCharacteristic:self.notifyCharacteristic onSubscribedCentrals:nil];
    if (notified)
    {
      CDVPluginResult *result;
      NSMutableDictionary *info;

      NSLog(@"-updateValue retry succeeded");

      info = [[NSMutableDictionary alloc] init];
      [info setValue:SUCCESS forKey:MES];
      result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];

      [self.commandDelegate sendPluginResult:result callbackId:self.notifyCallbackId];
    }
    else
    {
      NSLog(@"-updateValue retry failed");
    }

    self.notifyData = nil;
  }
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didReceiveReadRequest:(CBATTRequest *)request{
    CBMutableCharacteristic *characteristicRead = (CBMutableCharacteristic *)request.characteristic;
    if (request.characteristic.value == nil) {
        if ([self.valueAndCharacteristicDic allKeysForObject:characteristicRead].count > 0) {
            NSString *characteristicValue = [[NSString alloc] initWithFormat:@"%@",
                            [[self.valueAndCharacteristicDic allKeysForObject:characteristicRead] objectAtIndex:0]];
            Byte byte = (Byte)[characteristicValue intValue];
            NSData *data = [NSData dataWithBytes:&byte length:1];
            characteristicRead.value = data;
            request.value = data;
        }
    }else{
        request.value = request.characteristic.value;
    }
    [peripheral respondToRequest:request withResult:CBATTErrorSuccess];
    CBService *service = characteristicRead.service;
    NSMutableDictionary *callbackInfo = [self getUniqueIDWithService:service andCharacteristicIndex:characteristicRead];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [result setKeepCallbackAsBool:TRUE];
    [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:EVENT_ONCHARACTERISTICREAD]];
}

- (void)peripheralManager:(CBPeripheralManager *)peripheral didReceiveWriteRequests:(NSArray *)requests{
    NSLog(@"didReceiveWriteRequests()");
    CBATTRequest *writeRequest = [requests objectAtIndex:0];
    [peripheral respondToRequest:writeRequest withResult:CBATTErrorSuccess];
    CBCharacteristic *characteristicWrite = writeRequest.characteristic;
    CBService *service = characteristicWrite.service;
    NSMutableDictionary *callbackInfo = [self getUniqueIDWithService:service andCharacteristicIndex:characteristicWrite];
    [callbackInfo setValue:[self encodeBase64:writeRequest.value] forKey:WRITEREQUESTVALUE];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
    [result setKeepCallbackAsBool:TRUE];
    [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:EVENT_ONCHARACTERISTICWRITE]];
    CBMutableCharacteristic *cha = [self getNotifyCharacteristic:[callbackInfo objectForKey:UINQUE_ID]
                                             characteristicIndex:[callbackInfo objectForKey:CHARACTERISTIC_INDEX]];
    cha.value = writeRequest.value;
}

/*--------------------------------------------------------------------------*/
#pragma mark -
#pragma mark CBCentralManagerDelegate
- (void)centralManagerDidUpdateState:(CBCentralManager *)central {
    if ([bluetoothState isEqualToString:BLUETOOTHSTARTSTATE]) {
        if (self.myCentralManager.state  == CBCentralManagerStatePoweredOn){
            bluetoothState = IS_TRUE;
        }else{
            bluetoothState = IS_FALSE;
        }
    }else{
        if (self.myCentralManager.state  == CBCentralManagerStatePoweredOn){
            bluetoothState = IS_TRUE;
            [self.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@')",EVENT_BLUETOOTHOPEN]];
        }else{
            bluetoothState = IS_FALSE;
            [self.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@')",EVENT_BLUETOOTHCLOSE]];
        }
        NSMutableDictionary *stateInfo = [[NSMutableDictionary alloc] init];
        [stateInfo setValue:bluetoothState forKeyPath:@"state"];
        [[NSNotificationCenter defaultCenter] postNotificationName:BLUETOOTHSTATE object:stateInfo];
    }
}

- (void)waitNewPacketWithDeviceAddress:(NSString *)deviceAddress{
    NSMutableDictionary *callbackInfo = [peripheralAndUUID valueForKey:deviceAddress];
    if ([[callbackInfo valueForKey:ADVERTISEMENT_DATA] valueForKey:@"serviceData"]) {
        [[callbackInfo valueForKey:ADVERTISEMENT_DATA] removeObjectForKey:@"serviceData"];
    }
    if ([[callbackInfo valueForKey:ADVERTISEMENT_DATA] valueForKey:LOCAL_NAME]) {
        NSLog(@"~~~~~~~~~~~~~~%@",callbackInfo);
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [result setKeepCallbackAsBool:TRUE];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:EVENT_NEWADVPACKET]];
    }
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral
     advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)rssi {
    BCLOG_SCANDATA;

    if (self.isFindingPeripheral) {
        if ([[self.callbacks valueForKey:PERIPHERALADDRESS] isEqual:[self getPeripheralUUID:peripheral]]) {
            [self.myCentralManager stopScan];
            [self.myCentralManager connectPeripheral:peripheral options:nil];
            self.isFindingPeripheral = FALSE;
            [stopScanTimer invalidate];
        }
    }

    NSString *peripheralUUID = [self getPeripheralUUID:peripheral];
    [peripheralAndUUID setValue:[self getScanData:peripheral adv:advertisementData rssi:rssi] forKeyPath:peripheralUUID];
    [self performSelector:@selector(waitNewPacketWithDeviceAddress:) withObject:peripheralUUID afterDelay:0.2];

    if (self._peripherals.count == 0){
        self._peripherals = [[NSMutableArray alloc] initWithObjects:peripheral,nil];
    }else{
        for (int i = 0; i < self._peripherals.count; i++){
            if ([peripheralUUID isEqualToString:[self getPeripheralUUID:[self._peripherals objectAtIndex:i]]]){
                [self._peripherals removeObjectAtIndex:i];
                break;
            }
        }
    }
    [self._peripherals addObject:peripheral];
    [self.advDataDic setValue:[self getAdvertisementData:advertisementData] forKey:peripheralUUID];
    [self.RSSIDic setValue:[NSString stringWithFormat:@"%@",rssi] forKey:[self getPeripheralUUID:peripheral]];
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    [self connectRequest:deviceAddress callbackId:[self.callbacks
            valueForKey:[NSString stringWithFormat:@"connect%@",deviceAddress]] isKeepCallback:FALSE];
}

- (void)centralManager:(CBCentralManager *)central didRetrieveConnectedPeripherals:(NSArray *)peripherals{
    BCLOG_FUNC(GAP_MODUAL)
    [self addPeripheralToAllPeripherals:peripherals];
    NSArray *callbackInfo = [self storePeripheralInfo:peripherals];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:GETCONNECTEDDEVICES]];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    [self error:[self.callbacks valueForKey:[NSString stringWithFormat:@"connect%@",deviceAddress]]];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error
{
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:aPeripheral];
    if (!error) {
        if ([self.callbacks valueForKey:[NSString stringWithFormat:@"disConnect%@",deviceAddress]]) {
            [self connectRequest:deviceAddress callbackId:[self.callbacks valueForKey:
                            [NSString stringWithFormat:@"disConnect%@",deviceAddress]] isKeepCallback:FALSE];
            [self.callbacks removeObjectForKey:[NSString stringWithFormat:@"disConnect%@",deviceAddress]];
        }
    }else{
        if ([self.callbacks valueForKey:[NSString stringWithFormat:@"disConnect%@",deviceAddress]]) {
            [self error:[self.callbacks valueForKey:[NSString stringWithFormat:@"disConnect%@",deviceAddress]]];
        }else{
            [self connectRequest:deviceAddress callbackId:[self.callbacks valueForKey:EVENT_DISCONNECT] isKeepCallback:TRUE];
        }
    }
}

/*--------------------------------------------------------------------------*/
#pragma mark -
#pragma mark CBPeripheralDelegate
- (void)peripheralDidUpdateRSSI:(CBPeripheral *)peripheral error:(NSError *)error{
    BCLOG_FUNC(GAP_MODUAL)
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:[peripheral.RSSI description] forKey:PERIPHERAL_RSSI];
        [callbackInfo setValue:[self getPeripheralUUID:peripheral] forKey:DEVICE_ADDRESS];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks objectForKey:
                                [NSString stringWithFormat:@"getRssi%@",[self getPeripheralUUID:peripheral]]]];
    }else{
        [self error:[self.callbacks objectForKey:[NSString stringWithFormat:@"getRssi%@",[self getPeripheralUUID:peripheral]]]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error {
    BCLOG_FUNC(GAP_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    if (!error) {
        NSMutableDictionary *callbackInfo = [self storeServiceInfo:peripheral];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:
         [self.callbacks valueForKey:[NSString stringWithFormat:@"getServices:%@",deviceAddress]]];
    }else{
        [self error:[self.callbacks valueForKey:[NSString stringWithFormat:@"getServices:%@",deviceAddress]]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error{
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    if (!error) {
        NSMutableDictionary *callbackInfo = [self storeChatacteristicInfo:peripheral service:service];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks objectForKey:
                [NSString stringWithFormat:@"%d%@",[self getServiceIndex:peripheral service:service],deviceAddress]]];
    }else{
        [self error:[self.callbacks objectForKey:[NSString stringWithFormat:@"%d%@",
                [self getServiceIndex:peripheral service:service],[NSString stringWithFormat:@"%@",deviceAddress]]]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:
                    (CBCharacteristic *)characteristic error:(NSError *)error {
    BCLOG_FUNC(GATT_MODUAL)
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    if (!error) {
        NSMutableDictionary *callbackInfo = [self storeDescriptorInfo:peripheral characteristic:characteristic];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks objectForKey:
            [NSString stringWithFormat:@"%d%d%@",[self getCharacterIndex:characteristic.service character:characteristic],
            [self getServiceIndex:peripheral service:characteristic.service],deviceAddress]]];
    }else{
        [self error:[self.callbacks objectForKey:[NSString stringWithFormat:@"%d%d%@",
                    [self getCharacterIndex:characteristic.service character:characteristic],
                    [self getServiceIndex:peripheral service:characteristic.service],deviceAddress]]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (!error) {
        NSString *deviceAddress = [self getPeripheralUUID:peripheral];
        NSString *date = [NSString stringWithFormat:@"%@",[self getTime]];
        CBService *service = characteristic.service;
        if ([self.callbacks valueForKey:ISON]) {
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            NSString *serviceIndex = [NSString stringWithFormat:@"%d",[self getServiceIndex:peripheral service:service]];
            NSString *characteristicIndex = [NSString stringWithFormat:@"%d",[self getCharacterIndex:service character:characteristic]];
            NSString *value = [self encodeBase64:[characteristic value]];
            BCLOG_DATA([characteristic value],@"characteristicNotify",deviceAddress, [self CBUUIDToString:characteristic.UUID])
            [callbackInfo setValue:value forKey:VALUE];
            [callbackInfo setValue:date forKey:DATE];
            [callbackInfo setValue:deviceAddress forKey:DEVICE_ADDRESS];
            [callbackInfo setValue:serviceIndex forKey:SERVICE_INDEX];
            [callbackInfo setValue:characteristicIndex forKey:CHARACTERISTIC_INDEX];
            if ([self.callbacks valueForKey:[NSString stringWithFormat:@"%@%@%@",serviceIndex,characteristicIndex,SETNOTIFICATION]]) {
                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                [result setKeepCallbackAsBool:TRUE];
                [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:
                    [NSString stringWithFormat:@"%@%@%@",serviceIndex,characteristicIndex,SETNOTIFICATION]]];
            }
        }
        if ([self.callbacks valueForKey:READCHARACTERISTIC]){
            NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
            NSString *value = [self encodeBase64:[characteristic value]];
            BCLOG_DATA([characteristic value],@"readCharacteristic",deviceAddress, [self CBUUIDToString:characteristic.UUID])
            [callbackInfo setValue:value forKey:VALUE];
            [callbackInfo setValue:date forKey:DATE];
            CDVPluginResult* result;
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
            [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:READCHARACTERISTIC]];
        }
    }else{
        if ([self.callbacks valueForKey:READCHARACTERISTIC]) {
            [self error:[self.callbacks valueForKey:READCHARACTERISTIC]];
        }
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error{
    BCLOG_FUNC(GATT_MODUAL)
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        NSString *date = [NSString stringWithFormat:@"%@",[self getTime]];
        NSString *descriptorValue = [NSString stringWithFormat:@"%@",descriptor.value];
        NSData *aData = [descriptorValue dataUsingEncoding: NSUTF8StringEncoding];
        BCLOG_DATA(aData,@"readDescriptor",[self getPeripheralUUID:peripheral], [self CBUUIDToString:descriptor.UUID]);
        NSData *descriptorData = [descriptorValue dataUsingEncoding:NSUTF8StringEncoding];
        NSString *value = [self encodeBase64:descriptorData];
        [callbackInfo setValue:value forKey:VALUE];
        [callbackInfo setValue:date forKey:DATE];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:READDESCRIPTOR]];
    }else{
        [self error:[self.callbacks valueForKey:READDESCRIPTOR]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    BCLOG_FUNC(GATT_MODUAL)
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:WRITE]];
    }else{
        [self error:[self.callbacks valueForKey:WRITE]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error{
    BCLOG_FUNC(GATT_MODUAL)
    if (!error) {
        NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
        [callbackInfo setValue:SUCCESS forKey:MES];
        CDVPluginResult* result;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
        [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:WRITE]];
    }else{
        [self error:[self.callbacks valueForKey:WRITE]];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:
                    (CBCharacteristic *)characteristic error:(NSError *)error {
    BCLOG_FUNC(GATT_MODUAL)
    NSString *enable = [NSString stringWithFormat:@"%@",[self.callbacks valueForKey:ISON]];
    CBService *service = characteristic.service;
    NSString *serviceIndex = [NSString stringWithFormat:@"%d",[self getServiceIndex:peripheral service:service]];
    NSString *characteristicIndex = [NSString stringWithFormat:@"%d",[self getCharacterIndex:service character:characteristic]];
    if (!error) {
        if ([enable boolValue]) {
            [peripheral readValueForCharacteristic:characteristic];
        }else{
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:result callbackId:[self.callbacks valueForKey:
                    [NSString stringWithFormat:@"%@%@%@",serviceIndex,characteristicIndex,SETNOTIFICATION]]];
        }
    }else{
        [self error:[self.callbacks valueForKey:[NSString stringWithFormat:@"%@%@%@",serviceIndex,characteristicIndex,SETNOTIFICATION]]];
    }

}
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverIncludedServicesForService:(CBService *)service error:(NSError *)error {
}

/*--------------------------------------------------------------------------*/
# pragma mark -
# pragma mark MISC
# pragma mark -

- (BOOL)existCommandArguments:(NSArray*)comArguments{
    return TRUE;
}

- (NSString*)getCommandArgument:(NSArray*)arguments fromKey:(NSString*)key{
    return [[arguments objectAtIndex:0] valueForKey:key];
}

- (NSString*)encodeBase64:(NSData*)data{
    return [data base64EncodedStringWithOptions:0];
}

- (NSString*)getTime{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = DATE_FORMATE;
    return [dateFormatter stringFromDate:[NSDate date]];
}

- (void)error:(NSString *)string{
    NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
    [callbackInfo setValue:ERROR forKey:MES];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:callbackInfo];
    [self.commandDelegate sendPluginResult:result callbackId:string];
}

- (NSMutableDictionary *)getScanData:(CBPeripheral*)peripheralObj adv:(NSDictionary*)advData rssi:(NSNumber*)RSSI{
    NSMutableDictionary *peripheralInfo = [[NSMutableDictionary alloc] init];
    if ([peripheralObj name] != nil) {
        [peripheralInfo setValue:[peripheralObj name] forKey:DEVICE_NAME];
    }else {
        [peripheralInfo setValue:[[self getAdvertisementData:advData] valueForKey:LOCAL_NAME] forKey:DEVICE_NAME];
    }
    [peripheralInfo setValue:[self getPeripheralUUID:peripheralObj] forKey:DEVICE_ADDRESS];
    [peripheralInfo setValue: (([peripheralObj state] == CBPeripheralStateConnected) ? IS_TRUE:IS_FALSE ) forKey:IS_CONNECTED];
    [peripheralInfo setValue:[self getAdvertisementData:advData] forKey:ADVERTISEMENT_DATA];
    [peripheralInfo setValue:[NSString stringWithFormat:@"%@",RSSI] forKey:PERIPHERAL_RSSI];
    [peripheralInfo setValue:@"BLE" forKey:@"type"];
    return peripheralInfo;
}

- (NSArray*)storePeripheralInfo:(NSArray*)peripheralObjs{
    NSMutableArray* callbackInfo = [[NSMutableArray alloc] init];
    for (int i = 0; i < peripheralObjs.count; i++){
        NSMutableDictionary *peripheralInfo = [[NSMutableDictionary alloc] init];
        CBPeripheral *peripheral = [peripheralObjs objectAtIndex:i];
        NSString *peripheralUUID = [self getPeripheralUUID:peripheral];
        if (peripheral != nil && peripheralUUID != nil){
            [peripheralInfo setValue:([peripheral name]!=nil)? [peripheral name]:@"null" forKey:DEVICE_NAME];
            [peripheralInfo setValue:peripheralUUID forKey:DEVICE_ADDRESS];
            [peripheralInfo setValue:([peripheral state] == CBPeripheralStateConnected) ? IS_TRUE:IS_FALSE forKey:IS_CONNECTED];
            [peripheralInfo setValue:[self.advDataDic valueForKey:peripheralUUID] forKey:ADVERTISEMENT_DATA];
            [peripheralInfo setValue:[self.RSSIDic valueForKey:peripheralUUID] forKey:PERIPHERAL_RSSI];
            [callbackInfo addObject:peripheralInfo];
        }
    }
    return callbackInfo;
}

- (NSMutableDictionary *)storeServiceInfo:(CBPeripheral*)peripheral{
    NSMutableArray *servicesInfo = [[NSMutableArray alloc] init];
    for(int i = 0; i < peripheral.services.count; i++){
        CBService *service = [peripheral.services objectAtIndex:i];
        NSMutableDictionary *serviceInformation = [[NSMutableDictionary alloc] init];
        [serviceInformation setValue:[self getGATTName:service.UUID] forKey:SERVICE_NAME];
        [serviceInformation setValue:[self CBUUIDToString:service.UUID] forKey:SERVICE_UUID];
        [serviceInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:SERVICE_INDEX];
        [servicesInfo addObject:serviceInformation];
        BCLOG_UUID(service)
    }
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *serviceAndDeviceAddress = [[NSMutableDictionary alloc] init];
    [serviceAndDeviceAddress setValue:servicesInfo forKey:SERVICES];
    [serviceAndDeviceAddress setValue:deviceAddress forKey:DEVICE_ADDRESS];
    return serviceAndDeviceAddress;
}

- (NSMutableDictionary *)storeChatacteristicInfo:(CBPeripheral*)peripheral service:(CBService*)service{
    NSMutableArray *characteristicsInfo = [[NSMutableArray alloc] init];
    for (int i = 0; i < service.characteristics.count; i++) {
        CBCharacteristic *character = [service.characteristics objectAtIndex:i];
        NSMutableDictionary *characteristicInformation = [[NSMutableDictionary alloc] init];
        [characteristicInformation setValue:[self getGATTName:character.UUID] forKey:CHARACTERISTIC_NAME];
        [characteristicInformation setValue:[self CBUUIDToString:character.UUID] forKey:CHARACTERISTIC_UUID];
        [characteristicInformation setValue:[self getProperties:character] forKey:CHARACTERISTIC_PROPERTY];
        [characteristicInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:CHARACTERISTIC_INDEX];
        [characteristicsInfo addObject:characteristicInformation];
        BCLOG_UUID(character)
    }
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *characteristicAndDeviceAddress = [[NSMutableDictionary alloc] init];
    [characteristicAndDeviceAddress setValue:characteristicsInfo forKey:CHARACTERISTICS];
    [characteristicAndDeviceAddress setValue:deviceAddress forKey:DEVICE_ADDRESS];
    return characteristicAndDeviceAddress;
}

- (NSMutableDictionary *)storeDescriptorInfo:(CBPeripheral*)peripheral characteristic:(CBCharacteristic*)characteristic{
    NSMutableArray *descriptorsInfo = [[NSMutableArray alloc] init];
    for (int i = 0; i < characteristic.descriptors.count; i++) {
        CBDescriptor *descriptor = [characteristic.descriptors objectAtIndex:i];
        NSMutableDictionary *descriptorInformation = [[NSMutableDictionary alloc] init];
        [descriptorInformation setValue:[self getGATTName:descriptor.UUID] forKey:DESCRIPTOR_NAME];
        [descriptorInformation setValue:[self CBUUIDToString:descriptor.UUID] forKey:DESCRIPTOR_UUID];
        [descriptorInformation setValue:[NSString stringWithFormat:@"%d",i] forKey:DESCRIPTOR_INDEX];
        [descriptorsInfo addObject:descriptorInformation];
        BCLOG_UUID(descriptor)
    }
    NSString *deviceAddress = [self getPeripheralUUID:peripheral];
    NSMutableDictionary *descriptorAndDeviceAddress = [[NSMutableDictionary alloc] init];
    [descriptorAndDeviceAddress setValue:descriptorsInfo forKey:DESCRIPTORS];
    [descriptorAndDeviceAddress setValue:deviceAddress forKey:DEVICE_ADDRESS];
    return descriptorAndDeviceAddress;
}

- (void)addPeripheralToAllPeripherals:(NSArray*)pheripherals{
    if (self._peripherals.count == 0) {
        for (int i = 0; i < pheripherals.count; i++) {
            [self._peripherals addObject:[pheripherals objectAtIndex:i]];
        }
    }else{
        for (int i = 0; i < pheripherals.count; i++) {
            BOOL isAddAll = TRUE;
            CBPeripheral *per = [pheripherals objectAtIndex:i];
            NSString *myPeripheralUUID = [self getPeripheralUUID:per];
            for (int j = 0; j < self._peripherals.count; j++) {
                CBPeripheral *peripheralFromAll = [self._peripherals objectAtIndex:j];
                NSString *peripheralFromAllUUID = [self getPeripheralUUID:peripheralFromAll];
                if ([myPeripheralUUID isEqualToString:peripheralFromAllUUID] == YES){
                    isAddAll = FALSE;
                }
            }
            if (isAddAll){
                [self._peripherals addObject:per];
            }
        }
    }
}

- (CBCharacteristicProperties )property:(NSArray *)propertyArr{
    CBCharacteristicProperties property = 0;
    for (int i = 0; i < propertyArr.count; i++) {
        if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_READ])
            property = property | CBCharacteristicPropertyRead;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_WRITE_WITHOUT_RESPONSE])
            property = property | CBCharacteristicPropertyWriteWithoutResponse;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_WRITE])
            property = property | CBCharacteristicPropertyWrite;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_NOTIFY])
            property = property | CBCharacteristicPropertyNotify;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_INDICATE])
            property = property | CBCharacteristicPropertyIndicate;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_AUTHENTICATED_SIGNED_WTRTES])
            property = property | CBCharacteristicPropertyAuthenticatedSignedWrites;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_NOTIFY_ENCRYPTION_REQUIRED])
            property = property | CBCharacteristicPropertyNotifyEncryptionRequired;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PROPERTY_INDICATE_ENCRYPTION_REQUIRED])
            property = property | CBCharacteristicPropertyIndicateEncryptionRequired;
    }
    return property;
}

- (CBAttributePermissions )permission:(NSArray *)propertyArr{
    CBAttributePermissions permission = 0;
    for (int i = 0; i < propertyArr.count; i++) {
        if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_READ])
            permission = permission | CBAttributePermissionsReadable;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_WRITE])
            permission = permission | CBAttributePermissionsWriteable;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_READ_ENCRYPTED])
            permission = permission | CBAttributePermissionsReadEncryptionRequired;
        else if ([[propertyArr objectAtIndex:i] isEqualToString:PERMISSION_WRITE_ENCRYPTED])
            permission = permission | CBAttributePermissionsWriteEncryptionRequired;
    }
    return permission;
}

- (NSString *)CBUUIDToString:(CBUUID *)uuid{
    unsigned char base[16] = {0x00,0x00,0x00,0x00,  0x00,0x00,  0x10,0x00,  0x80,0x00, 0x00,0x80,0x5f,0x9b,0x34,0xfb};
    const unsigned char *bytes = [[uuid data] bytes];
    if (uuid.data.length == 2){
        memcpy(base+2, bytes, 2);
    }else if((uuid.data.length == 4) || (uuid.data.length == 16)){
        memcpy(base, bytes, uuid.data.length);
    }
    NSMutableString *string = [NSMutableString stringWithCapacity:20];
    for (int i = 0; i < 16; i++){
        switch (i){
            case 3:
            case 5:
            case 7:
            case 9: [string appendFormat:@"%02x-", base[i]]; break;
            default:[string appendFormat:@"%02x",  base[i]]; break;
        }
    }
    NSLog(@"CBUUIDFiltrToString <:  %@", string);
    return string;
}

- (CBPeripheral *)getPeripheral:(NSString *)strDeviceAddress{
    if ([strDeviceAddress isEqual: @""])
        return nil;
    CBPeripheral *peripheral=nil;
    for (int i = 0; i < self._peripherals.count; i++){
        peripheral = [self._peripherals objectAtIndex:i];
        if ([[self getPeripheralUUID:peripheral] isEqual:strDeviceAddress]) {
            return peripheral;
        }
    }
    return nil;
}

- (NSString *)getPeripheralUUID:(CBPeripheral *)peripheral{
    return peripheral.identifier.UUIDString;
}

- (int)getServiceIndex:(CBPeripheral *)peripheral service:(CBService *)service{
    for (int i = 0; i < peripheral.services.count; i++) {
        if ([service isEqual:[peripheral.services objectAtIndex:i]])
            return i;
    }
    return 0;
}

- (int)getCharacterIndex:(CBService *)service character:(CBCharacteristic *)characteristic{
    for (int i = 0; i < service.characteristics.count; i++) {
        if ([characteristic isEqual:[service.characteristics objectAtIndex:i]])
            return i;
    }
    return 0;
}

- (NSMutableDictionary *)getUniqueIDWithService:(CBService *)service andCharacteristicIndex:(CBCharacteristic *)characteristic{
    if ([self.serviceAndKeyDic allKeys].count > 0) {
        NSMutableDictionary *uniqueIDAndCharacteristicIndex = [[NSMutableDictionary alloc] init];
        for (int i = 0; i < [self.serviceAndKeyDic allKeys].count; i++) {
            if ([service isEqual:[self.serviceAndKeyDic valueForKey:[[self.serviceAndKeyDic allKeys] objectAtIndex:i]]]) {
                NSString *uniqueID = [[self.serviceAndKeyDic allKeys] objectAtIndex:i];
                [uniqueIDAndCharacteristicIndex setValue:uniqueID forKey:UINQUE_ID];
                if (service.characteristics.count > 0) {
                    for (int j = 0; j < service.characteristics.count; j++) {
                        if ([characteristic isEqual:[service.characteristics objectAtIndex:j]]) {
                            NSString *characteristicIndex = [NSString stringWithFormat:@"%d",j];
                            [uniqueIDAndCharacteristicIndex setValue:characteristicIndex forKey:CHARACTERISTIC_INDEX];
                        }
                    }
                }
            }
        }
        return uniqueIDAndCharacteristicIndex;
    }else{
        return nil;
    }
}

- (CBMutableCharacteristic *)getNotifyCharacteristic:(NSString *)uniqueID characteristicIndex:(NSString *)characteristicIndex{
    CBMutableService *service = [self.serviceAndKeyDic objectForKey:uniqueID];
    return [service.characteristics objectAtIndex:characteristicIndex.intValue];
}

- (NSMutableDictionary *)getAdvertisementData:(NSDictionary *)advertisementData
{
    NSMutableDictionary *advertisementDataDic = [[NSMutableDictionary alloc] init];

    if ([advertisementData valueForKey:KCBADVDATA_LOCALNAME])
        [advertisementDataDic setValue:[advertisementData valueForKey:KCBADVDATA_LOCALNAME] forKey:LOCAL_NAME];
    if ([advertisementData valueForKey:KCBADVDATA_TXPOWER_LEVEL])
        [advertisementDataDic setValue:[advertisementData valueForKey:KCBADVDATA_TXPOWER_LEVEL] forKey:TXPOWER_LEVEL];
    if ([advertisementData valueForKey:KCBADVDATA_SERVICE_DATA])
        [advertisementDataDic setValue:[advertisementData valueForKey:KCBADVDATA_SERVICE_DATA] forKey:SERVICE_DATA];
    if ([advertisementData valueForKey:KCBADVDATA_ISCONNECTABLE])
        [advertisementDataDic setValue:[advertisementData valueForKey:KCBADVDATA_ISCONNECTABLE] forKey:ISCONNECTABLE];
    if ([advertisementData valueForKey:KCBADVDATALOCAL_NAME]){
        NSData *manufacturer = [advertisementData valueForKey:KCBADVDATALOCAL_NAME];
        [advertisementDataDic setValue:[self encodeBase64:manufacturer] forKey:MANUFACTURER_DATA];
    }
    if ([advertisementData valueForKey:KCBADVDATA_SERVICE_UUIDS]){
        NSMutableArray *advServiceUUIDs = [advertisementData valueForKey:KCBADVDATA_SERVICE_UUIDS];
        NSMutableArray *uuids = [[NSMutableArray alloc] init];
        for (int i = 0; i < advServiceUUIDs.count; i++)
            [uuids addObject:[self CBUUIDToString:[advServiceUUIDs objectAtIndex:i]]];
        [advertisementDataDic setValue:uuids forKey:SERVICE_UUIDS];
    }
    if ([advertisementData valueForKey:KCBADVDATA_OVERFLOW_SERVICE_UUIDS]){
        NSMutableArray *uuids = [[NSMutableArray alloc] init];
        NSMutableArray *overFlowAdvServiceUUIDs = [advertisementData valueForKey:KCBADVDATA_OVERFLOW_SERVICE_UUIDS];
        for (int i = 0; i < overFlowAdvServiceUUIDs.count; i++)
            [uuids addObject:[self CBUUIDToString : [overFlowAdvServiceUUIDs objectAtIndex:i]]];
        [advertisementDataDic setValue:uuids forKey:OVERFLOW_SERVICE_UUIDS];
    }
    if ([advertisementData valueForKey:KCBADCDATA_SOLICITED_SERVICE_UUIDS]){
        NSMutableArray *uuids = [[NSMutableArray alloc] init];
        NSMutableArray *solicitedAdvServiceUUIDs = [advertisementData valueForKey:KCBADCDATA_SOLICITED_SERVICE_UUIDS];
        for (int i = 0; i < solicitedAdvServiceUUIDs.count; i++)
            [uuids addObject:[self CBUUIDToString:[solicitedAdvServiceUUIDs objectAtIndex:i]]];
        [advertisementDataDic setValue:uuids forKey:SOLICITED_SERVICE_UUIDS];
    }
    return advertisementDataDic;
}

- (NSMutableArray *)getProperties:(CBCharacteristic *)characteristic
{
    NSMutableArray *properties = [[NSMutableArray alloc] init];
    if (characteristic.properties & CBCharacteristicPropertyRead)
        [properties addObject:PROPERTY_READ];
    if (characteristic.properties & CBCharacteristicPropertyWriteWithoutResponse)
        [properties addObject:PROPERTY_WRITE_WITHOUT_RESPONSE];
    if (characteristic.properties & CBCharacteristicPropertyWrite)
        [properties addObject:PROPERTY_WRITE];
    if (characteristic.properties & CBCharacteristicPropertyNotify)
        [properties addObject:PROPERTY_NOTIFY];
    if (characteristic.properties & CBCharacteristicPropertyIndicate)
        [properties addObject:PROPERTY_INDICATE];
    if (characteristic.properties & CBCharacteristicPropertyAuthenticatedSignedWrites)
        [properties addObject:PROPERTY_AUTHENTICATED_SIGNED_WTRTES];
    if (characteristic.properties & CBCharacteristicPropertyNotifyEncryptionRequired)
        [properties addObject:PROPERTY_NOTIFY_ENCRYPTION_REQUIRED];
    if (characteristic.properties & CBCharacteristicPropertyIndicateEncryptionRequired)
        [properties addObject:PROPERTY_INDICATE_ENCRYPTION_REQUIRED];
    return properties;
}

- (UInt16) CBUUIDToInt:(CBUUID *) UUID {
    char b1[16];
    [UUID.data getBytes:b1];
    return ((b1[0] << 8) | b1[1]);
}

- (NSString *)getGATTName:(CBUUID *)UUID{
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
            return @"Customized";
            break;
    }
}

# pragma mark -
# pragma mark DEBUG
# pragma mark -

- (void)logFunc:(int)modualNumber information:(NSString *)information{
    NSRange foundModual = [information rangeOfString:@" "];
    if (foundModual.length > 0) {
        information = [information substringFromIndex:foundModual.location + foundModual.length];
        information = [information stringByReplacingOccurrencesOfString:@":]" withString:@""];
    }
    if (modualNumber == GAP_MODUAL) {
        information = [@"gap: enter " stringByAppendingString:information];
    }if (modualNumber == GATT_MODUAL) {
        information = [@"gatt: enter " stringByAppendingString:information];
    }if (modualNumber == DATA_MODUAL) {
        information = [@"data: enter " stringByAppendingString:information];
    }
    [self post:information];
}

- (void)logRSSI:(NSString *)information device:(NSString *)deviceUUID{
    NSString *getRssi = [NSString stringWithFormat:@"device = %@  get rssi = %@",deviceUUID,information];
    [self post:getRssi];
}

- (void)logScanDeviceUUID:(NSString *)deviceUUID RSSI:(NSString *)deviceRSSI advData:(NSMutableDictionary *)advertisementData{
    NSString *scanDeviceInfo = [NSString stringWithFormat:@"device = %@  scan rssi = %@  adv:",deviceUUID,deviceRSSI];
      for (NSString *key in [advertisementData allKeys]) {
        scanDeviceInfo = [scanDeviceInfo stringByAppendingString:
                          [NSString stringWithFormat:@"%@ = %@; ",key,[advertisementData valueForKey:key]]];
    }
    [self post:scanDeviceInfo];
}

- (void)logUUID:(CBUUID *)UUID{
    NSString *results = [UUID.data description];
    results = [results stringByReplacingOccurrencesOfString:@"<" withString:@""];
    results = [results stringByReplacingOccurrencesOfString:@">" withString:@""];
    NSString *result = [NSString stringWithFormat:@"uuid = %@",results];
    [self post:result];
}

- (void)logValue:(NSData *)value operation:(NSString *)operation device:(NSString *)deviceUUID UUID:(NSString *)UUID{
    [self post:[@"data: " stringByAppendingString:[NSString stringWithFormat:@"%@ :",operation]]];
    [self post:[NSString stringWithFormat:@"device = %@",deviceUUID]];
    [self post:[NSString stringWithFormat:@"uuid = %@",UUID]];
    [self post:@"value:"];

    Byte *valueByte = (Byte *)[value bytes];
    NSString *valueString = [NSString stringWithFormat:@"%@",value];
    valueString = [valueString uppercaseString];
    valueString = [valueString stringByReplacingOccurrencesOfString:@"<" withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@">" withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSInteger valueStringLength = valueString.length;
    for (int j = 0; j < valueStringLength / 16 + 1; j++) {
        NSString *valueInfo = @"";
        NSMutableString *result;
        NSString *asciiString;
        if (valueString.length <= 16) {
            result = [[NSMutableString alloc] initWithString:valueString];
            for (int i = 0; i < valueString.length / 2; i++) {
                [result insertString:@" " atIndex:2 + 2 * i + i];
            }
            asciiString = [self getAsciiString:valueByte infoDataLength:valueString.length / 2 byteIndex:j * 8];
            for (int k = 0; k < 8 - valueString.length / 2; k++) {
                asciiString = [@"     " stringByAppendingString:asciiString];
            }
            j++;
            valueInfo = [self changeToHexString:(j-1) * 8];
        }else{
            NSString *subString = [valueString substringToIndex:16];
            result = [[NSMutableString alloc] initWithString:subString];
            valueString = [valueString substringFromIndex:16];
            for (int i = 0; i < subString.length / 2; i++) {
                [result insertString:@" " atIndex:2 + 2 * i + i];
            }
            asciiString = [self getAsciiString:valueByte infoDataLength:8 byteIndex:j * 8];
            valueInfo = [self changeToHexString:j * 8];
        }
        valueInfo = [valueInfo stringByAppendingString:(NSString *)result];
        valueInfo = [valueInfo stringByAppendingString:asciiString];
        [self post:valueInfo];
    }
}

- (NSString *)getAsciiString:(Byte *)byte infoDataLength:(int)infoDataLength byteIndex:(int)byteIndex{
    NSString *result = [[NSString alloc] init];
    for (int i = 0; i < infoDataLength; i++) {
        NSData *data = [[NSData alloc] initWithBytes:&byte[i + byteIndex] length:1];
        NSString *asciiString = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
        if (byte[i + byteIndex] < 34 || byte[i + byteIndex] > 125) {
            result = [result stringByAppendingString:@"."];
        }else{
            result = [result stringByAppendingString:asciiString];
        }
    }
    result = [NSString stringWithFormat:@"     %@",result];
    return result;
}

-(NSString *)changeToHexString:(long long int)num
{
    NSString *letterOfHexValue;
    NSString *result =@"";
    long long int remainderNum;
    for (int i = 0; i < 9; i++) {
        remainderNum=num % 16;
        num=num / 16;
        switch (remainderNum)
        {
            case 10:
                letterOfHexValue =@"A";break;
            case 11:
                letterOfHexValue =@"B";break;
            case 12:
                letterOfHexValue =@"C";break;
            case 13:
                letterOfHexValue =@"D";break;
            case 14:
                letterOfHexValue =@"E";break;
            case 15:
                letterOfHexValue =@"F";break;
            default:letterOfHexValue=[[NSString alloc]initWithFormat:@"%lli",remainderNum];
        }
        result = [letterOfHexValue stringByAppendingString:result];
        if (num == 0) {
            break;
        }
    }
    if (result.length < 4) {
        for (int i = 0; i < 4 - result.length; ) {
            result = [@"0" stringByAppendingString:result];
        }
    }
    result = [result stringByAppendingString:@" : "];
    return result;
}

- (void)post:(NSString *)info{
    NSLog(@"%@", info);
}

@end
