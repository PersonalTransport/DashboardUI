package com.ptransportation.USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;


public class MasterManager {
    private static final String ACTION_USB_PERMISSION = "com.ptransportation.action.USB_PERMISSION";
    public UsbManager usbManager;
    public UsbAccessory usbAccessory;
    public PendingIntent pendingIntent;
    private boolean permissionRequestPending = true;

    private ParcelFileDescriptor fileDescriptor;
    private ArrayList<MasterDeviceConnectionListener> connectionListeners;

    public MasterManager() {
        this.connectionListeners = new ArrayList<>();
    }

    public void addConnectionListener(MasterDeviceConnectionListener listener) {
        this.connectionListeners.add(listener);
    }

    public void removeConnectionListener(MasterDeviceConnectionListener listener) {
        this.connectionListeners.remove(listener);
    }

    public void onCreate(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        context.registerReceiver(usbBroadcastReceiver, filter);
    }


    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    public void onResume(Intent intent) {
        if (fileDescriptor != null) {
            return;
        }

        UsbAccessory[] accessories = usbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (usbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (usbManager) {
                    if (!permissionRequestPending) {
                        usbManager.requestPermission(accessory,
                                pendingIntent);
                        permissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d("onResume", "accessory is null");
        }
    }

    public void onPause() {
        //closeAccessory();
    }

    public void onDestroy(Context context) {
        context.unregisterReceiver(usbBroadcastReceiver);
    }

    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    private void openAccessory(UsbAccessory accessory) {
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            usbAccessory = accessory;
            for(MasterDeviceConnectionListener connectionListener:connectionListeners)
                connectionListener.onMasterDeviceConnected(fileDescriptor.getFileDescriptor());
        } else {
            Log.d("openAccessory", "usbAccessory open fail: " + usbBroadcastReceiver);
        }
    }

    private void closeAccessory() {
        try {
            if (fileDescriptor != null) {
                for (MasterDeviceConnectionListener connectionListener : connectionListeners)
                    connectionListener.onMasterDeviceDisconnected(fileDescriptor.getFileDescriptor());
            }
            fileDescriptor.close();
        } catch (IOException ignored) {
        }
        fileDescriptor = null;
    }

    /*
   * This receiver monitors for the event of a user granting permission to use
   * the attached usbAccessory.  If the user has checked to always allow, this will
   * be generated following attachment without further user interaction.
   * Source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
   */
    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d("USB", "permission denied for usbAccessory " + accessory);
                    }
                    permissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(usbAccessory)) {
                    closeAccessory();
                }
            }
        }
    };
}


