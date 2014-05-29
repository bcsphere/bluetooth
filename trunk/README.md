BC.js (v0.4.0)
===================================
The BC.js is implemented as an [Apache Cordova](http://cordova.apache.org) / [PhoneGap](http://phonegap.com) Plugin for smartphones and tablets.
</br>It supports both Bluetooth Low Energy(BLE) API(in IOS/Android) & Bluetooth 2.1 classical interface(in Android)
  
  * Use this command to install our plugin in your Cordova/Phonegap project: <br/>
    <b>cordova plugin add https://github.com/bcsphere/bluetooth.git </b> <br/>
  * Online document is here: http://www.bcsphere.org/portal.php?mod=topic&topicid=3 <br/>
  * [BC Explorer](https://github.com/bcsphere/bcexplorer) is a useful tool to show the BLE services structure in device, we hope it will help you to start your development.
  * [BC IBeacon](https://github.com/bcsphere/ibeacon) implements as a plugin based on BC.js, provides a sample way to develop your own HW SDK based on BC.js .
  * [BC BLEprofiles](https://github.com/bcsphere/bleprofiles) implements some of Bluetooth standard profiles based on BC.js such as findme/proximity/serialport etc.
  
Features
-----------------------------------
#### Multi-platform development and deployment 
* Your team will not be separated into some smaller teams to develop app for different platforms.
* With the [Apache Cordova](http://cordova.apache.org) / [PhoneGap](http://phonegap.com) Plugin technique,
  you can develop BLE enabled cross-platform mobile apps for e.g. iOS and Android using JavaScript, HTML5 and CSS3.
* If you are a senior Web developer and want to develop a BLE app, BC.js is your best choice, you can use uniform UI code in multi-platform.

#### Dual-mode Bluetooth transport just like serial port 
* BC.js implement both BLE & classical interface in android platform.
* Cross-platform implementation of serial port, you can ignore the IOS/Android platform different when using the [serial port profile](https://github.com/bcsphere/bleprofiles) interface.
* Don't worry about the different between LE/Classical GAP. BC.js will choose the appropriate way to establish the connection.

#### Effective Utilization of Apache Cordova/PhoneGap Ecosystem
* PhoneGap enables the implementation of many useful plugins such as  [Camera](http://docs.phonegap.com/en/edge/cordova_camera_camera.md.html#Camera)/[Accelerometer](http://docs.phonegap.com/en/edge/cordova_accelerometer_accelerometer.md.html#Accelerometer)/[Compass](http://docs.phonegap.com/en/edge/cordova_compass_compass.md.html#Compass)...
* You can implements your own [plugins](http://docs.phonegap.com/en/3.3.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide) based on BC.js for others.
* There is many plugins implements by third party(you can find them on [PlugReg](http://plugreg.com/)), most of them are open-source.

#### Internet of Things
* Both Bluetooth Low Energy and JavaScript are asynchronous by their DNA,which makes them a good match. 
* Bluetooth is good enough for wireless short-range communication. But we won't be limited to Bluetooth API.
  
</br></br>
Support platforms
-----------------------------------
* Android API 18+ (All devices with Bluetooth Low Energy HW)
* Android API 17+ (Sumsung devices with Bluetooth Low Energy HW)
* Android API 16+ (HTC devices with Bluetooth Low Energy HW, <b>Please note:</b> HTC use shared library to operate BLE device, you should add the 'com.htc.android.bluetooth.le.jar' in the build path manually to support HTC devices, the files is in /others/HTC for optional use.)
* IOS 6.0.1+  (iPhone4S+/iTouch5/iPad3+/iPad-mini/iPadAir)

</br>[See Details](http://www.bcsphere.org/document/supportplatforms.html)
</br></br>

Backward
-----------------------------------
If your background is Java/C#/C/C++ or HW programming, you may feel confused when use functional programming language(such as javascript) for the first time.
</br>But once you get with it, you will love it, enjoy :)

[Version-History-&-Roadmap](https://github.com/bcsphere/bluetooth/wiki/Version-History-&-Roadmap)
-----------------------------------

