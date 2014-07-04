package org.bcsphere.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.bcsphere.bluetooth.tools.Tools;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    // Debugging
    private static final String TAG = "BluetoothSerialService";
    private static final boolean D = true;

    // Well known SPP UUID
    //public static UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int CONNECT_FAILED = 5;
    public static final int CONNECT_LOST = 6;
    
    public static final String TOAST = "toast";
    
    public CallbackContext connectCallback;
    public CallbackContext disconnectCallback;
    public CallbackContext dataAvailableCallback;
    
    private String deviceAddress;
    
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    int bufferSize = 0;
    
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

                       //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                       switch (msg.arg1) {
                           case BluetoothSerialService.STATE_CONNECTED:
                               Log.i("BluetoothSerial", "BluetoothSerialService.STATE_CONNECTED");
                               notifyConnectionSuccess();
                               break;
                           case BluetoothSerialService.STATE_CONNECTING:
                               Log.i("BluetoothSerial", "BluetoothSerialService.STATE_CONNECTING");
                               break;
                           case BluetoothSerialService.STATE_LISTEN:
                               Log.i("BluetoothSerial", "BluetoothSerialService.STATE_LISTEN");
                               break;
                           case BluetoothSerialService.STATE_NONE:
                               Log.i("BluetoothSerial", "BluetoothSerialService.STATE_NONE");
                               break;
                       }
                       break;
                   case MESSAGE_WRITE:
                       //  byte[] writeBuf = (byte[]) msg.obj;
                       //  String writeMessage = new String(writeBuf);
                       //  Log.i(TAG, "Wrote: " + writeMessage);
                       break;
                   case MESSAGE_DEVICE_NAME:
                       //Log.i(TAG, msg.getData().getString(DEVICE_NAME));
                       break;
                   case CONNECT_FAILED:
                       notifyConnectionFailed();
                       break;
                   case CONNECT_LOST:
                       notifyConnectionLost();
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
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void listen(String name,String uuidstr,boolean secure, BCBluetooth bcbluetooth) {
        if (D) Log.d(TAG, "startListen");
        UUID uuid = UUID.fromString(uuidstr);
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_NONE);

        //Listen isn't working with Arduino. Ignore since assuming the phone will initiate the connection.
        setState(STATE_LISTEN);
        
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(name,uuid,secure,bcbluetooth,this);
            mAcceptThread.start();
        }
    }
    
    public synchronized void unlisten() {
        if (D) Log.d(TAG, "stopListen");
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_NONE);
        
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device,String uuidstr, boolean secure) {
        if (D) Log.d(TAG, "connect to: " + device);
        deviceAddress = device.getAddress();
        UUID uuid = UUID.fromString(uuidstr);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device,uuid,secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Tools.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
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
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(CONNECT_FAILED);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect to device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        //BluetoothSerialService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(CONNECT_LOST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        //BluetoothSerialService.this.start();
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
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure":"Insecure";
            bcbluetooth = bluetooth;
            service = serialService;
            this.name = name;
            this.uuidstr = uuid.toString();

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
            if (D) Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
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
                            	bcbluetooth.acceptServices.remove(name+uuidstr);
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),mSocketType);
                                break;
                            case STATE_NONE:
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
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, UUID uuid,boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(uuid);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                     Class<?> clazz = mmSocket.getRemoteDevice().getClass();
                     Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                     Method m;
                     try {
						m = clazz.getMethod("createRfcommSocket", paramTypes);
	                    Object[] params = new Object[] {Integer.valueOf(1)};
	                    mmSocket = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
	                    Thread.sleep(500);
	                    mmSocket.connect();
	              
                     }
                     catch (NoSuchMethodException e1) {
 						// TODO Auto-generated catch block
 						e1.printStackTrace();
 					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
		                    try {
								mmSocket.close();
							} catch (IOException e2) {
								// TODO Auto-generated catch block
			                    Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e1);
								e2.printStackTrace();
							}
		                    Log.e(TAG, e1.toString());
		                    e1.printStackTrace();
		                    connectionFailed();
				    } catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSerialService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
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
                	for(int i = 0;i < byteNum;i++){
                		data[i] = buffer[i];
                	}
                    
                    // Send the new data String to the UI Activity
                    mHandler.obtainMessage(MESSAGE_READ, data).sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    //BluetoothSerialService.this.start();
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
        	JSONObject obj = new JSONObject();
			Tools.addProperty(obj, Tools.DEVICE_ADDRESS, deviceAddress);
            PluginResult result = new PluginResult(PluginResult.Status.OK,obj);
            result.setKeepCallback(true);
            disconnectCallback.sendPluginResult(result);
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
