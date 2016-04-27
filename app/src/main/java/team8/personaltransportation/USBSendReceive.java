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
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Joseph O on 2/1/2016.
 */
public class USBSendReceive {


    // variables for USB communication
    private static final String ACTION_USB_PERMISSION =    "team8.personaltransportation.action.USB_PERMISSION";
    public UsbManager UIusbManager;
    public UsbAccessory UIaccessory;
    public PendingIntent UIpermissionIntent;
    private boolean UIPermissionRequestPending = true;

    public ParcelFileDescriptor UIfileDescriptor;

    UsbCommThread usbCommThread;

    Handler uiHandler;

    FileInputStream inputStream;
    FileOutputStream outputStream;

    LinBus linBus;

    //static LinSignal linSignal;

    public void onCreate(final FullscreenActivity activity, Handler uiHandler, LinBus linBus) {
        // XXX Setup USB communication items  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        this.uiHandler = uiHandler;
        this.linBus = linBus;
        UIusbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        Log.d("onCreate", "usbmanager: " + UIusbManager);
        UIpermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        Log.d("onCreate", "filter: " + filter);
        activity.registerReceiver(UIusbReceiver, filter);
    }


    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    public void onResume(Intent intent) {

        Log.d("onResume", "resuming...");
        if (inputStream != null && outputStream != null) {
            return;
        }

        UsbAccessory[] accessories = UIusbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (UIusbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (UIusbManager) {
                    if (!UIPermissionRequestPending) {
                        UIusbManager.requestPermission(accessory,
                                UIpermissionIntent);
                        UIPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d("onResume", "accessory is null");
        }
    }

    public void onPause() {
        Log.d("onPause", "pausing...");
        //closeAccessory();
    }

    public void onDestroy(Activity activity) {
        Log.d("onDestroy", "destroying...");

        activity.unregisterReceiver(UIusbReceiver);
    }

    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    private void openAccessory(UsbAccessory accessory) {

        Log.d("openAccessory", "trying to open UIaccessory: " + UIusbReceiver);

        UIfileDescriptor = UIusbManager.openAccessory(accessory);
        if (UIfileDescriptor != null) {
            UIaccessory = accessory;
            FileDescriptor fd = UIfileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            linBus.initializeStreams(inputStream, outputStream);
            usbCommThread = new UsbCommThread(linBus, inputStream, outputStream);
            usbCommThread.start();
            Log.d("openAccessory", "opened UIaccessory: " + UIusbReceiver);
        } else {
            Log.d("openAccessory", "UIaccessory open fail: " + UIusbReceiver);
        }
    }

    // Close the connected UIaccessory (either unplugged or normal
    private void closeAccessory() {
        // try to close the UIinputStream, UIoutputStream, and UIfileDescriptor
        Log.d("closeAccessory", "closing accessory...");
        try {
            UIfileDescriptor.close();
        } catch (IOException ex) {}
        try {
            inputStream.close();
        } catch (IOException ex) {}
        try {
            outputStream.close();
        } catch (IOException ex) {}

        // afterwards, set them all to null
        UIfileDescriptor = null;
        inputStream = null;
        outputStream = null;

        // afterwards (?), stop execution of program
        System.exit(0); // XXX ??
    }

    /*
   * This receiver monitors for the event of a user granting permission to use
   * the attached UIaccessory.  If the user has checked to always allow, this will
   * be generated following attachment without further user interaction.
   * Source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
   */
    private final BroadcastReceiver UIusbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("UIusbReceiver", "initializing...");
            String action = intent.getAction();

            Log.d("UIusbReceiver", "action: " + action);

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d("USB", "permission denied for UIaccessory "+ accessory);
                    }
                    UIPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(UIaccessory)) {
                    closeAccessory();
                }
            }
        }
    };

    private class UsbCommThread extends Thread {

        FileInputStream inputStream;
        FileOutputStream outputStream;
        LinBus linBus;

        UsbCommThread(LinBus newLinBus, FileInputStream newInputStream, FileOutputStream newOutputStream) {
            this.inputStream = newInputStream;
            this.outputStream = newOutputStream;
            this.linBus = newLinBus;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    linBus.update();
                    Thread.sleep(1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}


