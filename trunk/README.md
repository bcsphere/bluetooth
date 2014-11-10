BC.js (v0.5.1)
===================================
BC.js is the core implementation of Bluetooth SDK for Android/iOS, in Bluetooth JavaScript API.<br/>
It supports both Bluetooth 4.0 GATT/BLE interface in IOS/Android and Bluetooth 2.1 Classical Rfcomm/L2cap socket interface in Android.

  * Online Bluetooth Javascript API document: http://www.bcsphere.org/portal.php?mod=topic&topicid=3 <br/>
  * library based on BC.js, which can be re-used by applications to accelerate development.
  * [bcexplorer](https://github.com/bcsphere/bcexplorer) is the open source tool to explore the GATT services/characteristics of Bluetooth Smart devices. It helps debug devices as well as demostrates the Bluetooth Javascript API.
  * [bciBeacon](https://github.com/bcsphere/ibeacon) implements a demo iBeacon as a plugin based on BC.js.
  
Features
-----------------------------------
#### Multi-platform development and deployment 
* Develop Bluetooth enabled cross-platform mobile Apps for iOS and Android using JavaScript, HTML5 and CSS3.
* Single team on a single project instead of several teams for different platforms.
* Uniform UI code in multi-platform.

#### Bluetooth Dual Mode 
* Support Bluetooth 4.0 GATT interface in IOS/Android.
* Support Bluetooth 2.1 rfcomm interface in Android.

#### Beacon / iBeacon 
* Support Beacon/iBeacon capability in IOS/Android.
* Support proximity with beacon

#### Bluetooth Common Services Library 
* Implement Bluetooth 4.0 Standard GATT services. (extending)
* Integrate various customermized GATT services to access partners' Bluetooth Smart devices. (extending)

#### Offical profile & services implements
* BC.js implements some of offical profiles and services which defined in Bluetooth Specification(www.bluetooth.org)
* Profiles include [Find Me](https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.profile/find_me.js)/
[Proximity](https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.profile/proximity.js)
* Services include [TX Power](https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.service/tx_power.js)/
[Link Loss](https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.service/link_loss.js)/
[Immediate Alert](https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.service/immediate_alert.js)...
* Find Me example is [here](https://github.com/bcsphere/apps/tree/master/findme), it shows how to use the profiles.

#### Universal Bluetooth Serial Port 
* Cross-platform implementation of Bluetooth serial port, covering the IOS/Android platform difference when using the [serial port profile](https://https://github.com/bcsphere/bluetooth/blob/master/www/org.bluetooth.profile/serial_port.js).
* Automatic Bluetooth 4.0/2.1 routing to establish appropriate connection, considering both phones and peripheral devices modes.

#### Reuse rich Apache Cordova/PhoneGap Ecosystem
* PhoneGap enables the implementation of many useful plugins such as  [Camera](http://docs.phonegap.com/en/edge/cordova_camera_camera.md.html#Camera)/[Accelerometer](http://docs.phonegap.com/en/edge/cordova_accelerometer_accelerometer.md.html#Accelerometer)/[Compass](http://docs.phonegap.com/en/edge/cordova_compass_compass.md.html#Compass)...
* Reuse many open-source plugins implements by third party ([PlugReg](http://plugreg.com/)).
* Implement your own [plugins](http://docs.phonegap.com/en/3.3.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide) based on BC.js for others.

#### True Internet of Things
* Connect the Internet/Web with the objects in the real world via Bluetooth Javascript interface easily. 


Usage
-----------------------------------
1. To use in BCSphere official APP <br/>
(1) Include bc.js and other profiles .js files in your own javascript file directly. <br/>
(2) Access the API. <br/>

2. To incorporate into your own APP<br/>
(1) Integrate [Apache Cordova](http://cordova.apache.org) / [PhoneGap](http://phonegap.com) into your Android/iOS project.<br/>
(2) Use this command to install bc.js as a plugin in your project: <br/>
    <b>cordova plugin add https://github.com/bcsphere/bluetooth.git </b> <br/>


Support platforms
-----------------------------------
For Bluetooth GATT
  * Android API 18+, All devices with Bluetooth 4.0 Low Energy HW
  * Android API 17+, Sumsung devices with Bluetooth 4.0 Low Energy HW
  * Android API 16+, HTC devices with Bluetooth Low Energy HW, <br/><b>note:</b> HTC use shared library to operate BLE device, you should add the 'com.htc.android.bluetooth.le.jar' in the build path manually to support HTC devices, the files is in /others/HTC for optional use.
  * IOS 6.0.1+,  iPhone4S+/iTouch5+/iPad3+/iPad-mini+/iPadAir

For Bluetooth 2.1 Classic
  * Android API 9+ 

Commercial Service
-----------------------------------
- Quick 1 solution for all platforms, with project schedule down to 1 week.
- For Bluetooth APPs customization service, please contact team@bcsphere.org. 


[Version History & Roadmap](https://github.com/bcsphere/bluetooth/wiki/Version-History-&-Roadmap)


