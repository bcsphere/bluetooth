BC.js (v0.4.0)
===================================
BC.js is the core implementation of universal Bluetooth JavaScript API.
It supports both Bluetooth 4.0 GATT interface in IOS/Android and Bluetooth 2.1 Classical interface in Android.

  * Online Bluetooth Javascript API document: http://www.bcsphere.org/portal.php?mod=topic&topicid=3 <br/>
  * [bleprofiles](https://github.com/bcsphere/bleprofiles) is the BC official open source Bluetooth common profiles library based on BC.js, which can be re-used by applications to accelerate development.
  * [bcexplorer](https://github.com/bcsphere/bcexplorer) is the open source tool to explore the GATT services/characteristics of Bluetooth Smart devices. It helps debug devices as well as demostrates the Bluetooth Javascript API.
  * [bciBeacon](https://github.com/bcsphere/ibeacon) implements as a plugin based on BC.js, provides a sample way to develop your own HW SDK based on BC.js.
  
Features
-----------------------------------
#### Multi-platform development and deployment 
* Develop Bluetooth enabled cross-platform mobile apps for iOS and Android using JavaScript, HTML5 and CSS3.
* Single team on a single project instead of several teams for different platforms.
* Uniform UI code in multi-platform.

#### Universal Bluetooth Serial Port 
* BC.js supports implement both BLE & classical interface in android platform.
* Cross-platform implementation of serial port, you can ignore the IOS/Android platform different when using the [serial port profile](https://github.com/bcsphere/bleprofiles) interface.
* Don't worry about the different between LE/Classical GAP. BC.js will choose the appropriate way to establish the connection.

#### Effective Utilization of Apache Cordova/PhoneGap Ecosystem
* PhoneGap enables the implementation of many useful plugins such as  [Camera](http://docs.phonegap.com/en/edge/cordova_camera_camera.md.html#Camera)/[Accelerometer](http://docs.phonegap.com/en/edge/cordova_accelerometer_accelerometer.md.html#Accelerometer)/[Compass](http://docs.phonegap.com/en/edge/cordova_compass_compass.md.html#Compass)...
* You can implements your own [plugins](http://docs.phonegap.com/en/3.3.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide) based on BC.js for others.
* There is many plugins implements by third party(you can find them on [PlugReg](http://plugreg.com/)), most of them are open-source.

#### Internet of Things
* Both Bluetooth Low Energy and JavaScript are asynchronous by their DNA,which makes them a good match. 
* Bluetooth is good enough for wireless short-range communication. But we won't be limited to Bluetooth API.

Usage
-----------------------------------
It supports [Apache Cordova](http://cordova.apache.org) / [PhoneGap](http://phonegap.com) Plugin for smartphones and tablets at the time.
  * Use this command to install our plugin in your Cordova/Phonegap project: <br/>
    <b>cordova plugin add https://github.com/bcsphere/bluetooth.git </b> <br/>

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

