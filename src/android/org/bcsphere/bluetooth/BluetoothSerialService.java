package org.bcsphere.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import java.lang.Object;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import android.util.Log;

import org.json.JSONObject;

import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import org.bcsphere.bluetooth.tools.Tools;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 *
 * This code was based on the Android SDK BluetoothChat Sample
 * $ANDROID_SDK/samples/android-17/BluetoothChat
 */
public class BluetoothSerialService {
  private static final boolean D = true;
  private static final String TAG = "BluetoothSerialService";

  public static final String TOAST = "toast";

  public static final int STATE_NONE = 0;
  public static final int STATE_LISTEN = 1;
  public static final int STATE_CONNECTING = 2;
  public static final int STATE_CONNECTED = 3;

  public static final int MESSAGE_STATE_CHANGE = 1;
  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_WRITE = 3;
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int CONNECT_FAILED = 5;
  public static final int CONNECT_LOST = 6;
  public static final int CONNECT_TIMED_OUT = 7;

  private int mState;
  private String deviceAddress;
  private final Handler mHandler;
  private AcceptThread mAcceptThread;
  private ConnectThread mConnectThread;
  private final BluetoothAdapter mAdapter;
  private ConnectedThread mConnectedThread;

  public CallbackContext connectCallback;
  public CallbackContext disconnectCallback;
  public CallbackContext dataAvailableCallback;
  
  int bufferSize = 0;
  ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);

  /**
   * Constructor. Prepares a new BluetoothSerial session.
   * @param handler  A Handler to send messages back to the UI Activity
   */
  public BluetoothSerialService() {
    mAdapter = BluetoothAdapter.getDefaultAdapter();
    mState = STATE_NONE;
    mHandler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
        case MESSAGE_READ:
          if (dataAvailableCallback != null) {
            sendDataToSubscriber((byte[])msg.obj);
          }else{
            byte[] data = (byte[])msg.obj;
            buffer.put(data);
            bufferSize = bufferSize + data.length;
          }
          break;

        case MESSAGE_STATE_CHANGE:
          switch (msg.arg1) {
            case BluetoothSerialService.STATE_CONNECTED:
              Log.i(TAG, "BluetoothSerialService.STATE_CONNECTED");
              notifyConnectionSuccess();
              break;

            case BluetoothSerialService.STATE_CONNECTING:
              Log.i(TAG, "BluetoothSerialService.STATE_CONNECTING");
              break;

            case BluetoothSerialService.STATE_LISTEN:
              Log.i(TAG, "BluetoothSerialService.STATE_LISTEN");
              break;

            case BluetoothSerialService.STATE_NONE:
              Log.i(TAG, "BluetoothSerialService.STATE_NONE");
              break;
          }
          break;

        case MESSAGE_WRITE:
          break;

        case MESSAGE_DEVICE_NAME:
          break;

        case CONNECT_FAILED:
          notifyConnectionFailed();
          break;

        case CONNECT_LOST:
          notifyConnectionLost();
          stop();
          break;

        case CONNECT_TIMED_OUT:
          Log.i(TAG, "Connection Attempt timed out.");
          break;
        }
      }
    };
  }

  /**
   * Set the current state of the chat connection
   * @param state  An integer defining the current connection state
   */
  private synchronized void setState(int state) {
    Log.d(TAG, "setState() " + mState + " -> " + state);
    mState = state;

    // Give the new state to the Handler so the UI Activity can update
    mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
  }

  /**
   * Return the current connection state.
   */
  public synchronized int getState() {
    return mState;
  }

  /**
   * Start the chat service. Specifically start AcceptThread to begin a
   * session in listening (server) mode. Called by the Activity onResume()
   */
  public synchronized void listen(String name,String uuidstr,boolean secure, BCBluetooth bcbluetooth) {
    Log.d(TAG, "listen() called");

    UUID uuid = UUID.fromString(uuidstr);

    if (mConnectThread != null) {
      Log.d(TAG, "Stopping connecting process");
      mConnectThread.cancel();
      mConnectThread = null;
    } else {
      Log.d(TAG, "No need to stop a connecting process - there is none");
    }

    if (mConnectedThread != null) {
      Log.d(TAG, "Stopping connection process");
      mConnectedThread.cancel();
      mConnectedThread = null;
    } else {
      Log.d(TAG, "No need to stop a connection process - there is none");
    }

    setState(STATE_NONE);
    setState(STATE_LISTEN);

    if (mAcceptThread == null) {
      Log.d(TAG, "Starting accept process");
      mAcceptThread = new AcceptThread(name,uuid,secure,bcbluetooth,this);
      mAcceptThread.start();
    } else {
      Log.d(TAG, "No need to start accept process - it is already running");
    }
  }

  public synchronized void unlisten() {
    Log.d(TAG, "unlisten()");

    if (mConnectThread != null) {
      Log.d(TAG, "Stopping connecting process");
      mConnectThread.cancel();
      mConnectThread = null;
    }

    if (mConnectedThread != null) {
      Log.d(TAG, "Stopping connection process");
      mConnectedThread.cancel();
      mConnectedThread = null;
    } else {
      Log.d(TAG, "No need to stop a connection process - there is none");
    }

    setState(STATE_NONE);

    // Start the thread to listen on a BluetoothServerSocket
    if (mAcceptThread != null) {
      Log.d(TAG, "Stopping accept process");
      mAcceptThread.cancel();
    } else {
      Log.d(TAG, "No need to stop accept process - there is none");
    }
  }

  /**
   * Start the ConnectThread to initiate a connection to a remote device.
   * @param device  The BluetoothDevice to connect
   * @param secure Socket Security type - Secure (true) , Insecure (false)
   */
  public synchronized void connect(BluetoothDevice device, String uuidstr, boolean secure) {
    Log.d(TAG, "connect()");
    UUID uuid = UUID.fromString(uuidstr);

    deviceAddress = device.getAddress();

    if (mState == STATE_CONNECTING) {
      if (mConnectThread != null) {
        Log.d(TAG, "Stopping accept process");
        mConnectThread.cancel();
        mConnectThread = null;
      } else {
        Log.d(TAG, "No need to stop accept process - there is none");
      }
    }

    if (mConnectedThread != null) {
      Log.d(TAG, "Stopping connection process");
      mConnectedThread.cancel();
      mConnectedThread = null;
    } else {
      Log.d(TAG, "No need to stop connection process - there is none");
    }

    Log.d(TAG, "Connecting to device: " + device);
    mConnectThread = new ConnectThread(device, uuid, secure);
    mConnectThread.start();

    setState(STATE_CONNECTING);
  }

  /**
   * Start the ConnectedThread to begin managing a Bluetooth connection
   * @param socket  The BluetoothSocket on which the connection was made
   * @param device  The BluetoothDevice that has been connected
   */
  public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
    Log.d(TAG, "connected()");

    if (mConnectThread != null) {
      Log.d(TAG, "Stopping connecting process");
      mConnectThread.cancel();
      mConnectThread = null;
    } else {
      Log.d(TAG, "No need to stop connecting process - there is none");
    }

    if (mConnectedThread != null) {
      Log.d(TAG, "Stopping connection process");
      mConnectedThread.cancel();
      mConnectedThread = null;
    } else {
      Log.d(TAG, "No need to stop connection process - there is none");
    }

    if (mAcceptThread != null) {
      Log.d(TAG, "Stopping accept process");
      mAcceptThread.cancel();
      mAcceptThread = null;
    } else {
      Log.d(TAG, "No need to stop accept process - there is none");
    }

    Log.d(TAG, "Starting connection thread for new connection");
    mConnectedThread = new ConnectedThread(socket, socketType);
    mConnectedThread.start();

    Log.d(TAG, "Notifying application about new connection");
    Bundle bundle = new Bundle();
    bundle.putString(Tools.DEVICE_NAME, device.getName());

    Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
    msg.setData(bundle);
    mHandler.sendMessage(msg);

    setState(STATE_CONNECTED);
  }

  /**
   * Stop all threads
   */
  public synchronized void stop() {
    Log.d(TAG, "stop() - This will stop all running service activities");

    if (mConnectThread != null) {
      Log.d(TAG, "Stopping connecting process");
      mConnectThread.cancel();
      mConnectThread = null;
    } else {
      Log.d(TAG, "No need to stop connecting process - there is none");
    }

    if (mConnectedThread != null) {
      Log.d(TAG, "Stopping connection process");
      mConnectedThread.cancel();
      mConnectedThread = null;
    } else {
      Log.d(TAG, "No need to stop connection process - there is none");
    }

    if (mAcceptThread != null) {
      Log.d(TAG, "Stopping accept process");
      mAcceptThread.cancel();
      mAcceptThread = null;
    } else {
      Log.d(TAG, "No need to stop accept process - there is none");
    }

    setState(STATE_NONE);
  }

  /**
   * Write to the ConnectedThread in an unsynchronized manner
   * @param out The bytes to write
   * @see ConnectedThread#write(byte[])
   */
  public void write(byte[] out) {
    // Create temporary object
    ConnectedThread r;

    // Synchronize a copy of the ConnectedThread
    synchronized (this) {
      if (mState != STATE_CONNECTED) {
        return;
      } else {
        r = mConnectedThread;
      }
    }

    // Perform the write unsynchronized
    r.write(out);
  }

  /**
   * Indicate that the connection attempt failed and notify the UI Activity.
   */
  private void connectionFailed() {
      Log.d(TAG, "connectionFailed()");

      Bundle bundle = new Bundle();
      bundle.putString(TOAST, "Unable to connect to device");

      Message msg = mHandler.obtainMessage(CONNECT_FAILED);
      msg.setData(bundle);

      mHandler.sendMessage(msg);
  }

  /**
   * Indicate that the connection was lost and notify the UI Activity.
   */
  private void connectionLost() {
      Log.d(TAG, "connectionLost()");

      Bundle bundle = new Bundle();
      bundle.putString(TOAST, "Device connection was lost");

      Message msg = mHandler.obtainMessage(CONNECT_LOST);
      msg.setData(bundle);

      mHandler.sendMessage(msg);
  }

  /**
   * This thread runs while listening for incoming connections. It behaves
   * like a server-side client. It runs until a connection is accepted
   * (or until cancelled).
   */
  private class AcceptThread extends Thread {
    // The local server socket
    private final BluetoothServerSocket mmServerSocket;
    private String mSocketType;
    private BCBluetooth bcbluetooth;
    private BluetoothSerialService service;
    private String name;
    private String uuidstr;

    public AcceptThread(String name,UUID uuid,boolean secure,BCBluetooth bluetooth,BluetoothSerialService serialService) {
      this.name = name;
      bcbluetooth = bluetooth;
      service = serialService;
      this.uuidstr = uuid.toString();
      BluetoothServerSocket tmp = null;
      mSocketType = secure ? "Secure":"Insecure";

      // Create a new listening server socket
      try {
        if (secure) {
          tmp = mAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
        } else {
          tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid);
        }
      } catch (IOException e) {
        Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
      }

      mmServerSocket = tmp;
    }

    public void run() {
      BluetoothSocket socket;

      if (D) Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
      setName("AcceptThread" + mSocketType);

      // Listen to the server socket if we're not connected
      while (mState != STATE_CONNECTED) {
        try {
          socket = mmServerSocket.accept(); // will block until success or throw
        } catch (IOException e) {
          Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
          break;
        }

        // If a connection was accepted
        if (socket != null) {
          synchronized (BluetoothSerialService.this) {
            switch (mState) {
            case STATE_LISTEN:
            case STATE_CONNECTING:
              service.deviceAddress = socket.getRemoteDevice().getAddress();
              bcbluetooth.classicalServices.put(service.deviceAddress,service);
              bcbluetooth.acceptServices.remove(name + uuidstr);
              connected(socket, socket.getRemoteDevice(), mSocketType);
              break;

            case STATE_NONE:  // fall through
            case STATE_CONNECTED:
              // Either not ready or already connected. Terminate new socket.
              try {
                socket.close();
              } catch (IOException e) {
                Log.e(TAG, "Could not close unwanted socket", e);
              }
              break;
            }
          }
        }
      }

      if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
    }

    public void cancel() {
      if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
      try {
        mmServerSocket.close();
      } catch (IOException e) {
        Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
      }
    }
  }


  /**
   * This thread runs while attempting to make an outgoing connection
   * with a device. It runs straight through; the connection either
   * succeeds or fails.
   */
  private class ConnectThread extends Thread {
    private static final String mSecureMethodName = "createRfcommSocket";
    private static final String mInsecureMethodName = "createInsecureRfcommSocket";
    
    private String mSocketType;
    private BluetoothSocket mmSocket;
    private Class<?> mmDroid42SocketClass;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device, UUID uuid, boolean secure) {
      mmDevice = device;
      
      if (secure) {
        mSocketType = "secure";
      } else {
        mSocketType = "insecure";
      }
      
      createRfcommSocket(device, uuid, secure);
    }
    
    private void createRfcommSocket(BluetoothDevice device, UUID uuid, boolean secure) {
      if (android.os.Build.VERSION.RELEASE.startsWith("4.2")) {
        createRfcommSocketDroid42(device, uuid, secure);
      } else if (android.os.Build.VERSION.RELEASE.startsWith("4.4.3")) {
        createRfcommSocketDroid42(device, uuid, secure);
      } else {
        createRfcommSocketRegular(device, uuid, secure);
      }
    }

    private void createRfcommSocketRegular(BluetoothDevice device, UUID uuid, boolean secure) {
      BluetoothSocket tmp;

      try {
        if (secure) {
          Log.i(TAG, "Creating secure bluetooth socket");
          tmp = device.createRfcommSocketToServiceRecord(uuid);
        } else {
          Log.i(TAG, "Creating insecure bluetooth socket");
          tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
      } catch (IOException ioExc) {
        Log.e(TAG, "createRegularRfcommSocket() - failed", ioExc);
        tmp = null;
      }

      mmSocket = tmp;
    }

    private void createRfcommSocketDroid42(BluetoothDevice device, UUID uuid, boolean secure) {
      String methodName;
      Object[] parameters;
      Class<?>[] parameterTypes;
      Method createRfcommSocketMethod;

      parameters = new Object[] {Integer.valueOf(1)};
      parameterTypes = new Class<?>[] { Integer.TYPE };
      
      if (secure) {
        methodName = mSecureMethodName;
      } else {
        methodName = mInsecureMethodName;
      }

      createRfcommSocketRegular(device, uuid, secure);
      
      mmDroid42SocketClass = mmSocket.getRemoteDevice().getClass();
      
      try {
        Log.i(TAG, "Creating bluetooth socket the special (i.e. Android 4.2) way");
        createRfcommSocketMethod = mmDroid42SocketClass.getMethod(methodName, parameterTypes);
        mmSocket = (BluetoothSocket) createRfcommSocketMethod.invoke(mmSocket.getRemoteDevice(), parameters);
      } catch (Exception exc) {
        Log.e(TAG, "Creating bluetooth socket the special (i.e. Android 4.2) way - failed", exc);
      }
    }

    private void connectRfcommSocket() {
      try {
        Log.d(TAG, "mmSocket.connect() - called");
        mmSocket.connect();
        Log.d(TAG, "mmSocket.connect() - success");

        synchronized (BluetoothSerialService.this) {
          mConnectThread = null;
        }

        connected(mmSocket, mmDevice, mSocketType);
      } catch (IOException connectExc) {
        Log.e(TAG, "mmSocket.connect() - failure", connectExc);

        try {
          if (null != mmSocket) {
            Log.d(TAG, "mmSocket.close() - called");
            mmSocket.close();
          } else {
            Log.d(TAG, "No need to call mmSocket.close() - there is no socket");
          }
        } catch (IOException ioExc) {
          Log.e(TAG, "mmSocket.close() - failure", ioExc);
        }

        connectionFailed();
      }
    }

    public void run() {
      final Message timeoutMsg = mHandler.obtainMessage(CONNECT_TIMED_OUT, -1 , -1);

      Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
      setName("ConnectThread" + mSocketType);

      Log.i(TAG, "Cancelling discovery.");
      mAdapter.cancelDiscovery();
      Log.i(TAG, "Discovery cancelled.");
      
      connectRfcommSocket();
    }

    public void cancel() {
      try {
        if (null != mmSocket) {
          mmSocket.close();
        }
      } catch (IOException e) {
        Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
      }
    }
  }

  /**
   * This thread runs during a connection with a remote device.
   * It handles all incoming and outgoing transmissions.
   */
  private class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket, String socketType) {
      Log.d(TAG, "create ConnectedThread: " + socketType);
      mmSocket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;

      // Get the BluetoothSocket input and output streams
      try {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
        Log.e(TAG, "temp sockets not created", e);
      }

      mmInStream = tmpIn;
      mmOutStream = tmpOut;
    }

    public void run() {
      Log.i(TAG, "BEGIN mConnectedThread");
      byte[] buffer = new byte[2048];
      int byteNum;
      // Keep listening to the InputStream while connected
      while (true) {
        try {
            // Read from the InputStream
          byteNum = mmInStream.read(buffer);
          byte[] data = new byte[byteNum];

          for(int i = 0;i < byteNum;i++) {
            data[i] = buffer[i];
          }

          // Send the new data String to the UI Activity
          mHandler.obtainMessage(MESSAGE_READ, data).sendToTarget();
        } catch (IOException e) {
          Log.e(TAG, "Read failed. Disconnecting.", e);

          try {
            mmSocket.close();
          } catch (IOException closeExc) {
            Log.e(TAG, "Unable to close socket after read error. - Probably already closed.", e);
          }

          connectionLost();
          break;
        }
      }
    }

    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
      try {
        mmOutStream.write(buffer);

        // Share the sent message back to the UI Activity
        mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
      } catch (IOException e) {
        Log.e(TAG, "Exception during write", e);
      }
    }

    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        Log.e(TAG, "close() of connect socket failed", e);
      }
    }
  }

  private void notifyConnectionSuccess() {
    if (connectCallback != null) {
      PluginResult result = new PluginResult(PluginResult.Status.OK);
      result.setKeepCallback(true);
      connectCallback.sendPluginResult(result);
    }
  }

  private void notifyConnectionLost() {
    if (disconnectCallback != null) {
      Log.i(TAG, "Notifying about lost connection.");
      JSONObject obj = new JSONObject();
      Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
      PluginResult result = new PluginResult(PluginResult.Status.OK,obj);
      result.setKeepCallback(true);
      disconnectCallback.sendPluginResult(result);
    } else {
      Log.i(TAG, "Unable to notify about lost connection. NO CALLBACK");
    }
  }

  private void notifyConnectionFailed() {
    if (connectCallback != null) {
      JSONObject obj = new JSONObject();
      Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
      connectCallback.error(obj);
      connectCallback = null;
    }
  }

  private void sendDataToSubscriber(byte[] data) {
    if (data != null) {
      JSONObject obj = new JSONObject();
      //Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
      Tools.addProperty(obj, Tools.VALUE, Tools.encodeBase64(data));
      Tools.addProperty(obj, Tools.DATE, Tools.getDateString());
      PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
      result.setKeepCallback(true);
      dataAvailableCallback.sendPluginResult(result);
    }
  }
}
