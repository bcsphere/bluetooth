//
//  BCIBeacon.h
//  BCSphereCoreDev
//
//  Created by NPHD on 14-4-15.
//
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <Cordova/NSDictionary+Extensions.h>
#import <Cordova/NSArray+Comparisons.h>
#import <CoreBluetooth/CBService.h>
#import <Cordova/CDVPlugin.h>
#import <Cordova/NSData+Base64.h>
#import <Cordova/CDVJSON.h>
#import <CoreLocation/CoreLocation.h>
#import "BCBluetooth.h"

@interface BCIBeacon : CDVPlugin<CBPeripheralManagerDelegate,CLLocationManagerDelegate>
{
    BOOL isVariableInit;
}
@property (retain, nonatomic) CBPeripheralManager *beaconPeripheralManager;
@property (nonatomic, strong) CLLocationManager *locationManager;
@property (nonatomic, strong) CLBeaconRegion *beaconRegion;
@property NSMutableDictionary *rangedRegions;

- (void)addEventListener:(CDVInvokedUrlCommand *)command;
- (void)startIBeaconScan:(CDVInvokedUrlCommand *)command;
- (void)stopIBeaconScan:(CDVInvokedUrlCommand *)command;
- (void)startIBeaconAdvertising:(CDVInvokedUrlCommand *)command;
@end
