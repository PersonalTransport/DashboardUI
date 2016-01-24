package team8.personaltransportation;

// Sources:
// USB communication -
// http://developer.android.com/guide/topics/connectivity/usb/accessory.html
// http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
// http://www.ftdichip.com/Support/SoftwareExamples/Android_Projects.htm

import android.annotation.SuppressLint;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.app.AppCompactActivity;
//import android.app.ActivityManager;
//import android.app.ActivityOptions;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
//public class FullscreenActivity extends AppCompatActivity {
public class FullscreenActivity extends Activity {

    // Used to obtain USB permission from the host device (to communicate)
    private static final String ACTION_USB_PERMISSION =    "FTDI.LED.USB_PERMISSION";  // XXX ??

    // variables for USB communication
    public USB_ACTIVITY_Thread UIhandlerThread;
    public UsbManager UIusbManager;
    public UsbAccessory UIaccessory;
    public PendingIntent UIpermissionIntent;
    private boolean UIPermissionRequestPending = true;

    public ParcelFileDescriptor UIfileDescriptor;
    public FileInputStream UIinputStream;
    public FileOutputStream UIoutputStream;


    // variables for GUI interface
    boolean warningOn = false;
    boolean headlampOn = false;
    int wiperswitch = 0;
    int defrostswitch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        // XXX Setup USB communication items
        UIusbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Log.d("LED", "usbmanager" +UIusbManager);
        UIpermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        Log.d("LED", "filter" +filter);
        registerReceiver(UIusbReceiver, filter);

        // Set up the user interaction to manually show or hide the system UI.
        /*
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        */

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        final ImageButton warningButton = (ImageButton) findViewById(R.id.warning);
        warningButton.setImageResource(R.drawable.warningoff);
        warningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "I clicked it!");

                if (warningOn) {
                    warningButton.setImageResource(R.drawable.warningoff);
                    warningOn = false;
                }
                else {
                    warningButton.setImageResource(R.drawable.warningon);
                    warningOn = true;
                }
            }
        });
        final ImageButton headlampButton = (ImageButton) findViewById(R.id.headlampoff);
        headlampButton.setImageResource(R.drawable.headlamp_off);
        headlampButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "I clicked it!");

                if (headlampOn) {
                    headlampButton.setImageResource(R.drawable.headlamp_off);
                    headlampOn = false;
                }
                else {
                    headlampButton.setImageResource(R.drawable.headlamp_on);
                    headlampOn = true;
                }
            }
        });
        /*************************** working on this ********************************************/
        final ImageButton defrostButton = (ImageButton) findViewById(R.id.defrost);
        defrostButton.setImageResource(R.drawable.defrost);
        defrostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "I clicked it!");

                if (defrostswitch == 0) {
                    defrostButton.setImageResource(R.drawable.defrost1);
                    defrostswitch = 1;
                }
                else if(defrostswitch == 1){
                    defrostButton.setImageResource(R.drawable.defrost2);
                    defrostswitch = 2;
                }
                else if(defrostswitch == 2){
                    defrostButton.setImageResource(R.drawable.defrost3);
                    defrostswitch = 3;
                }
                else {
                    defrostButton.setImageResource(R.drawable.defrost);
                    defrostswitch = 0;
                }
            }
        });
        /*************************** working ********************************************/
        final ImageButton wiperButton = (ImageButton) findViewById(R.id.wipers);
        wiperButton.setImageResource(R.drawable.wipers);
        wiperButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "I clicked it!");

                if (wiperswitch == 0) {
                    wiperButton.setImageResource(R.drawable.wipers1);
                    wiperswitch = 1;
                }
                else if(wiperswitch == 1){
                    wiperButton.setImageResource(R.drawable.wipers2);
                    wiperswitch = 2;
                }
                else if(wiperswitch == 2){
                    wiperButton.setImageResource(R.drawable.wipers3);
                    wiperswitch = 3;
                }
                else {
                    wiperButton.setImageResource(R.drawable.wipers);
                    wiperswitch = 0;
                }
            }
        });



        // Obtain USB UIusbManager
//        UIusbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        UIaccessory = (UsbAccessory) getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
//
//        String ACTION_USB_PERMISSION =
//                "com.android.example.USB_PERMISSION";
//
//        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(UIusbReceiver, filter);
//
//        UIusbManager.requestPermission(UIaccessory, permissionIntent);
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(1000, 0);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    openAccessory();
//                }
//            }
//        });
//
//        thread.run();

    }

    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (UIinputStream != null && UIoutputStream != null) {
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
            Log.d("UI_USB_OnResume", "accessory is null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(UIusbReceiver);
        super.onDestroy();
    }

    // source : http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
    private void openAccessory(UsbAccessory accessory) {

        Log.d("openAccessory", "trying to open UIaccessory: " + UIusbReceiver);

        UIfileDescriptor = UIusbManager.openAccessory(accessory);
        if (UIfileDescriptor != null) {
            UIaccessory = accessory;
            FileDescriptor fd = UIfileDescriptor.getFileDescriptor();
            UIinputStream = new FileInputStream(fd);
            UIoutputStream = new FileOutputStream(fd);

            // create a thread, passing it the USB input stream and this task's handler object
            UIhandlerThread = new USB_ACTIVITY_Thread(UIHandler, UIinputStream);
            UIhandlerThread.start();
            Log.d("openAccessory", "opened UIaccessory: " + UIusbReceiver);
        } else {
            Log.d("openAccessory", "UIaccessory open fail: " + UIusbReceiver);
        }
    }

    // Close the connected UIaccessory (either unplugged or normal
    private void closeAccessory() {
        // try to close the UIinputStream, UIoutputStream, and UIfileDescriptor
        try {
            UIfileDescriptor.close();
        } catch (IOException ex) {}
        try {
            UIinputStream.close();
        } catch (IOException ex) {}
        try {
            UIoutputStream.close();
        } catch (IOException ex) {}

        // afterwards, set them all to null
        UIfileDescriptor = null;
        UIinputStream = null;
        UIoutputStream = null;

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
            String action = intent.getAction();
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

    private class USB_ACTIVITY_Thread extends Thread {

        Handler USBhandler;
        FileInputStream USBInuptStream;

        USB_ACTIVITY_Thread(Handler h, FileInputStream fI) {
            USBhandler = h;
            USBInuptStream = fI;
        }

        @Override
        public void run() {
            byte[] data_recieved = new byte[10];
            int data_recieved_len;

            while(true) {
                try {
                    if(USBInuptStream != null) {
                        data_recieved_len = USBInuptStream.read(data_recieved,0,5); // change later
                        if (data_recieved_len < 6) {
                            Log.d("USBTask", "Error: did not read enough data from USB");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("USBTask", "Error: could not read data from USB");
                    break;
                }

                Message mss = Message.obtain(USBhandler); // XXX can also pass objects/input data with messages (obtain function is overloaded)
                // XXX fill in this with input functionality (interpreting the input data stream, then calling a )
                // XXX
                // XXX
                // XXX Temporary: save the input data in the message (to spit back to USB device)
                mss.obj = data_recieved.clone();
                // afterwards, post message to handler (so main task can deal with data)
                USBhandler.sendMessage(mss);
            }
        }
    }

    // handler object to handle messages from the USB thread
    Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // XXX perform functionality to handle message (and provide response to USB with UIoutputStream.write())
            // XXX Temporary: spit back input data in message to USB

            byte[] temp;

            temp = (byte[]) msg.obj;
            try {
                UIoutputStream.write(temp);
                UIoutputStream.flush(); // ???
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("UIHandler", "Error: could not write data to USB output");
            }

        }
    };


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }

        private ActionBar getSupportActionBar() {
            return null;
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("BUTTONS", "View.onTouchListener got here!");
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private ActionBar supportActionBar;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public ActionBar getSupportActionBar() {
        return supportActionBar;
    }


    /*public void onClick() {
        Log.d("BUTTON", "CLICKED YAY");
    }*/
}

