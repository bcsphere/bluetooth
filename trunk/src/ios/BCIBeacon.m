//
//  BCIBeacon.m
//  BCSphereCoreDev
//
//  Created by NPHD on 14-4-15.
//
//

#import "BCIBeacon.h"

#define STARTBEACON @"startBeacon"
#define STOPBEACON @"stopBeacon"
#define BEACON_PROXIMITYUUID @"proximityUUID"
#define BEACON_MAJOR @"major"
#define BEACON_MINOR @"minor"
#define BEACON_PROXIMITY @"proximity"
#define BEACON_ACCURACY @"accuracy"
#define BEACON_RSSI @"RSSI"
#define BEACON_IDENTIFIER @"identifier"
#define EVENT_IBEACONACCURACYUPDATE @"ibeaconaccuracyupdate"
#define EVENT_NAME @"eventName"

@implementation BCIBeacon

- (void)pluginInitialize{
    [super pluginInitialize];
    if (!isVariableInit) {
        [self variableInit];
    }
}

- (void)variableInit{
    isVariableInit = TRUE;
    self.beaconPeripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:nil];

}

- (void)addEventListener:(CDVInvokedUrlCommand *)command{
    if ([self existCommandArguments:command.arguments]) {
        NSString *eventName = [self parseStringFromJS:command.arguments keyFromJS:EVENT_NAME];
        [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:eventName];
    }else{
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }
}

#pragma mark -
#pragma mark - CBperipheralManagerDelegate
- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral {
    switch (peripheral.state) {
        case CBPeripheralManagerStatePoweredOn:
            break;
        default:
            break;
    }
}

#pragma mark
#pragma mark locationDelegate
#pragma mark
- (void)startRangingForBeacons{
    if (!self.locationManager) {
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self;
        self.locationManager.activityType = CLActivityTypeFitness;
        self.locationManager.distanceFilter = kCLDistanceFilterNone;
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    }
    if (!self.rangedRegions) {
        self.rangedRegions = [[NSMutableDictionary alloc] init];
    }
    
    [self turnOnRanging];
}

- (void)turnOnRanging{
    if (![CLLocationManager isRangingAvailable]) {
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:STARTBEACON]];
        return;
    }
}

- (void)startIBeaconAdvertising:(CDVInvokedUrlCommand *)command{
    if (self.beaconPeripheralManager.state != 5) {
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return;
    }
    
    CLBeaconRegion *beaconRegion;
    NSString *strKUUID = [self parseStringFromJS:command.arguments keyFromJS:BEACON_PROXIMITYUUID];
    NSString *kIdentifier = [self parseStringFromJS:command.arguments keyFromJS:BEACON_IDENTIFIER];
    NSUUID *proximityUUID = [[NSUUID alloc] initWithUUIDString:strKUUID];
    if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR]]) {
        CLBeaconMajorValue majorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR] intValue];
        if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR]]) {
            CLBeaconMinorValue minorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR] intValue];
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue minor:minorValue identifier:kIdentifier];
        }else{
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue identifier:kIdentifier];
        }
    }else{
        beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID  identifier:kIdentifier];
    }
    NSMutableDictionary *beaconPeripheralData = [beaconRegion peripheralDataWithMeasuredPower:nil];
    [self.beaconPeripheralManager startAdvertising:beaconPeripheralData];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    
}

- (void)startIBeaconScan:(CDVInvokedUrlCommand *)command{
    [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:STARTBEACON];
    
    [self startRangingForBeacons];
    [self.rangedRegions removeAllObjects];
    
    CLBeaconRegion *beaconRegion;
    NSString *strKUUID = [self parseStringFromJS:command.arguments keyFromJS:BEACON_PROXIMITYUUID];
    NSString *kIdentifier = [self parseStringFromJS:command.arguments keyFromJS:BEACON_IDENTIFIER];
    NSUUID *proximityUUID = [[NSUUID alloc] initWithUUIDString:strKUUID];
    if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR]]) {
        CLBeaconMajorValue majorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR] intValue];
        if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR]]) {
            CLBeaconMinorValue minorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR] intValue];
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue minor:minorValue identifier:kIdentifier];
        }else{
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue identifier:kIdentifier];
        }
    }else{
        beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID  identifier:kIdentifier];
    }
    
    [self.locationManager startRangingBeaconsInRegion:beaconRegion];
}

- (void)stopIBeaconScan:(CDVInvokedUrlCommand *)command{
    if (self.locationManager.rangedRegions.count == 0) {
        return;
    }
    [[NSUserDefaults standardUserDefaults] setValue:command.callbackId forKey:STOPBEACON];
    CLBeaconRegion *beaconRegion;
    NSString *strKUUID = [self parseStringFromJS:command.arguments keyFromJS:BEACON_PROXIMITYUUID];
    NSString *kIdentifier = [self parseStringFromJS:command.arguments keyFromJS:BEACON_IDENTIFIER];
    NSUUID *proximityUUID = [[NSUUID alloc] initWithUUIDString:strKUUID];
    if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR]]) {
        CLBeaconMajorValue majorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MAJOR] intValue];
        if ([self isNormalString:[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR]]) {
            CLBeaconMinorValue minorValue = [[self parseStringFromJS:command.arguments keyFromJS:BEACON_MINOR] intValue];
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue minor:minorValue identifier:kIdentifier];
        }else{
            beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID major:majorValue identifier:kIdentifier];
        }
    }else{
        beaconRegion = [[CLBeaconRegion alloc] initWithProximityUUID:proximityUUID  identifier:kIdentifier];
    }
    [self.locationManager stopRangingBeaconsInRegion:beaconRegion];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:STOPBEACON]];
}

- (void)locationManager:(CLLocationManager *)manager didRangeBeacons:(NSArray *)beacons inRegion:(CLBeaconRegion *)region {
    self.rangedRegions[region] = beacons;
    
    NSMutableArray *allBeacons = [NSMutableArray array];
    
    for (NSArray *regionResult in [self.rangedRegions allValues]){
        [allBeacons addObjectsFromArray:regionResult];
    }
    
    for (NSNumber *range in @[@(CLProximityUnknown), @(CLProximityImmediate), @(CLProximityNear), @(CLProximityFar)])
    {
        NSArray *proximityBeacons = [allBeacons filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"proximity = %d", [range intValue]]];
        if([proximityBeacons count]){
            for (CLBeacon *beacon in proximityBeacons) {
                NSMutableDictionary *callbackInfo = [[NSMutableDictionary alloc] init];
                [callbackInfo setValue:[NSString stringWithFormat:@"%@",beacon.proximityUUID.UUIDString] forKey:BEACON_PROXIMITYUUID];
                [callbackInfo setValue:[self getBase64EncodedFromData:[[NSString stringWithFormat:@"%@",beacon.major] dataUsingEncoding: NSUTF8StringEncoding]] forKey:BEACON_MAJOR];
                [callbackInfo setValue:[self getBase64EncodedFromData:[[NSString stringWithFormat:@"%@",beacon.minor] dataUsingEncoding: NSUTF8StringEncoding]] forKey:BEACON_MINOR];
                [callbackInfo setValue:[NSString stringWithFormat:@"%d",beacon.proximity] forKey:BEACON_PROXIMITY];
                [callbackInfo setValue:[NSString stringWithFormat:@"%f",beacon.accuracy] forKey:BEACON_ACCURACY];
                [callbackInfo setValue:[NSString stringWithFormat:@"%i",beacon.rssi] forKey:BEACON_RSSI];
                
                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:callbackInfo];
                [result setKeepCallbackAsBool:TRUE];
                [self.commandDelegate sendPluginResult:result callbackId:[[NSUserDefaults standardUserDefaults] valueForKey:EVENT_IBEACONACCURACYUPDATE]];
            }
        }
    }
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

- (NSString*)getBase64EncodedFromData:(NSData*)data{
    NSData *newData = [[NSData alloc] initWithData:data];
    NSString *value = [newData base64EncodedString];
    return value;
}

- (BOOL)isNormalString:(NSString*)string{
    if (![string isEqualToString:@"(null)"] && ![string isEqualToString:@"null"] && string.length > 0){
        return TRUE;
    }else{
        return FALSE;
    }
}

- (NSString*)parseStringFromJS:(NSArray*)commandArguments keyFromJS:(NSString*)key{
    NSString *string = [NSString stringWithFormat:@"%@",[[commandArguments objectAtIndex:0] valueForKey:key]];
    return string;
}

@end
