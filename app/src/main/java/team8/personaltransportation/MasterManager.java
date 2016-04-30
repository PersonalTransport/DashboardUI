package team8.personaltransportation;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MasterManager {
    private static final String ACTION_USB_PERMISSION = "team8.personaltransportation.action.USB_PERMISSION";
    public UsbManager usbManager;
    public UsbAccessory usbAccessory;
    public PendingIntent pendingIntent;
    private boolean permissionRequestPending = true;

    public ParcelFileDescriptor fileDescriptor;

    Handler handler;

    FileInputStream inputStream;
    FileOutputStream outputStream;

    public void onCreate(final FullscreenActivity activity, Handler handler) {
        this.handler = handler;
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        activity.registerReceiver(usbBroadcastReceiver, filter);
    }


    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    public void onResume(Intent intent) {
        if (inputStream != null && outputStream != null) {
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

    public void onDestroy(Activity activity) {
        activity.unregisterReceiver(usbBroadcastReceiver);
    }

    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    private void openAccessory(UsbAccessory accessory) {
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            usbAccessory = accessory;
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);

            // TODO start the threads here

        } else {
            Log.d("openAccessory", "usbAccessory open fail: " + usbBroadcastReceiver);
        }
    }

    private void closeAccessory() {
        try {
            inputStream.close();
            outputStream.close();
            fileDescriptor.close();
        } catch (IOException ex) {
        }

        fileDescriptor = null;
        inputStream = null;
        outputStream = null;

        System.exit(0); // TODO this is not really right?
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
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d("USB", "permission denied for usbAccessory " + accessory);
                    }
                    permissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(usbAccessory)) {
                    closeAccessory();
                }
            }
        }
    };
}


