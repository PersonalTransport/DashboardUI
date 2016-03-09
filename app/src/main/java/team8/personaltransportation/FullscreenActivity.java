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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Hashtable;

//public class FullscreenActivity extends AppCompatActivity {
public class FullscreenActivity extends Activity {

    private int SID_BATTERY = LinSignal.signalHash("BATTERY".getBytes(), 0);
    private int SID_LIGHTS = LinSignal.signalHash("LIGHTS".getBytes(), 0);
    private int SID_SPEED = LinSignal.signalHash("SPEED".getBytes(), 0);
    private int SID_TURNSIGNAL = LinSignal.signalHash("TURN_SIGNAL".getBytes(), 0);
    private int SID_HAZARD = LinSignal.signalHash("HAZARD".getBytes(), 0);
    private int SID_DEFROST = LinSignal.signalHash("DEFROST".getBytes(), 0);
    private int SID_WIPERS = LinSignal.signalHash("WIPERS".getBytes(), 0);

    // variables for GUI interface
    boolean warningOn = false;
    boolean headlampOn = false;
    int wiperswitch = 0;
    int defrostswitch = 0;

    USB_Send_Receive usb_send_receive;

    /********************* Variables for DEFROST (AC) *********************/
    Hashtable<Integer,Integer> Hazard_hash;
    /********************* Variables for WIPERS *********************/
    Hashtable<Integer,Integer> Wiper_hash;
    /********************* Variables for BATTERY *********************/
    ImageView Battery_handle;
    Hashtable<Integer,Integer> Battery_hash;
    int batteryLife = 0;

    /********************* Variables for SPEEDOMETER *********************/
    ImageView Speed_handle1;
    ImageView Speed_handle2;
    Hashtable<Integer,Integer> Speed_hash;
    int currentSpeed = 0;

    Handler usbInputHandler;
    LinBus linBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        /************************** WarningButton **************************************/
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
        /************************** HEADLAMP **************************************/
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
        /*************************** Defrost (AC) ********************************************/
        final ImageButton defrostButton = (ImageButton) findViewById(R.id.defrost);
        Hazard_hash = new Hashtable<>();
        Hazard_hash.put(1, R.drawable.defrost1);
        Hazard_hash.put(2, R.drawable.defrost2);
        Hazard_hash.put(3, R.drawable.defrost3);
        Hazard_hash.put(0, R.drawable.defrost);
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
        /*************************** Wiper ********************************************/
        final ImageButton wiperButton = (ImageButton) findViewById(R.id.wipers);
        Wiper_hash = new Hashtable<>();
        Wiper_hash.put(1,R.drawable.wipers1);
        Wiper_hash.put(2,R.drawable.wipers2);
        Wiper_hash.put(3,R.drawable.wipers3);
        Wiper_hash.put(0,R.drawable.wipers);
        wiperButton.setImageResource(R.drawable.wipers);
        wiperButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "I clicked it!");

                if (wiperswitch == 0) {
                    wiperButton.setImageResource(R.drawable.wipers1);
                    wiperswitch = 1;
                } else if (wiperswitch == 1) {
                    wiperButton.setImageResource(R.drawable.wipers2);
                    wiperswitch = 2;
                } else if (wiperswitch == 2) {
                    wiperButton.setImageResource(R.drawable.wipers3);
                    wiperswitch = 3;
                } else {
                    wiperButton.setImageResource(R.drawable.wipers);
                    wiperswitch = 0;
                }
            }
        });

        /*************************** Battery ********************************************/
        Battery_handle = (ImageView) findViewById(R.id.batteryLife);
        Battery_handle.setImageResource(R.drawable.battery100);
        Battery_hash = new Hashtable<>();
        for (int i = 0; i < 5; i++) {
            Battery_hash.put(i, R.drawable.battery00);
        }
        for (int i = 5; i < 15; i++) {
            Battery_hash.put(i, R.drawable.battery10);
        }
        for (int i = 15; i < 25; i++) {
            Battery_hash.put(i, R.drawable.battery20);
        }
        for (int i = 25; i < 35; i++) {
            Battery_hash.put(i, R.drawable.battery30);
        }
        for (int i = 35; i < 45; i++) {
            Battery_hash.put(i, R.drawable.battery40);
        }
        for (int i = 45; i < 55; i++) {
            Battery_hash.put(i, R.drawable.battery50);
        }
        for (int i = 55; i < 65; i++) {
            Battery_hash.put(i, R.drawable.battery60);
        }
        for (int i = 65; i < 75; i++) {
            Battery_hash.put(i, R.drawable.battery70);
        }
        for (int i = 75; i < 85; i++) {
            Battery_hash.put(i, R.drawable.battery80);
        }
        for (int i = 85; i < 95; i++) {
            Battery_hash.put(i, R.drawable.battery90);
        }
        for (int i = 95; i < 100; i++) {
            Battery_hash.put(i, R.drawable.battery100);
        }

        /*************************** Speedometer ******************************************/
        Speed_handle1 = (ImageView) findViewById(R.id.speedleft);
        Speed_handle2 = (ImageView) findViewById(R.id.speedright);
        Speed_handle1.setImageResource(R.drawable.zero_tmp);
        Speed_handle2.setImageResource(R.drawable.zero_tmp);
        Speed_hash = new Hashtable<>();
        Speed_hash.put(0, R.drawable.zero_tmp);
        Speed_hash.put(1, R.drawable.one_tmp);
        Speed_hash.put(2, R.drawable.two_tmp);
        Speed_hash.put(3, R.drawable.three_tmp);
        Speed_hash.put(4, R.drawable.four_tmp);
        Speed_hash.put(5, R.drawable.five_tmp);
        Speed_hash.put(6, R.drawable.six_tmp);
        Speed_hash.put(7, R.drawable.seven_tmp);
        Speed_hash.put(8, R.drawable.eight_tmp);
        Speed_hash.put(9, R.drawable.nine_tmp);

        // Handles incoming messages
        usbInputHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d("UIHandler", "handling message: " + msg);

                // interpret current data
                LinSignal signal = (LinSignal) msg.obj;
                //batteryLife = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);
                //batteryLife = ((((int) signal.data[0]) << 24) & 0xFF000000) | ((((int) signal.data[1]) << 16) & 0x00FF0000) | ((((int) signal.data[2]) << 8) & 0x0000FF00) | (((int) signal.data[3]) & 0x000000FF);
                // begin to prepare data to be sent back
                LinSignal sendSig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);

                Toast.makeText(getApplicationContext(), String.valueOf(LinSignal.signalHash("BATTERY".getBytes(), 0)) + ", " + String.valueOf(signal.sid) + "; " + String.valueOf(LinSignal.COMM_SET_VAR) + ", " + String.valueOf(signal.command) + "; " + String.valueOf(signal.data), Toast.LENGTH_SHORT).show();

                if (SID_BATTERY == signal.sid) {

                    if (signal.command == LinSignal.COMM_SET_VAR) {
//                        String Data_IN_TEST = signal.data.toString();

                        batteryLife = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);

                        Toast.makeText(getApplicationContext(), "::Battery:: " + batteryLife, Toast.LENGTH_SHORT).show();

                        Battery_handle.setImageResource(Battery_hash.get(batteryLife));

                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }

                }
                else if (SID_SPEED == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        // set speed (similar to battery)
                        currentSpeed = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);
                        Toast.makeText(getApplicationContext(), "::Speed:: " + currentSpeed, Toast.LENGTH_SHORT).show();

                        Speed_handle1.setImageResource(Speed_hash.get(currentSpeed / 10));
                        Speed_handle2.setImageResource(Speed_hash.get(currentSpeed%10));

                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }

                }
                else if (SID_LIGHTS == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        Toast.makeText(getApplicationContext(), "::Lights:: " + ((headlampOn)?1:0), Toast.LENGTH_SHORT).show();
                        sendSig.data = LinSignal.packIntToBytes((headlampOn)?1:0);
                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }

                }
                else if (SID_HAZARD == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        Toast.makeText(getApplicationContext(), "::Hazard:: " + ((warningOn)?1:0), Toast.LENGTH_SHORT).show();
                        sendSig.data = LinSignal.packIntToBytes((warningOn)?1:0);
                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }
                }
                else if (SID_WIPERS == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        Toast.makeText(getApplicationContext(), "::Wipers:: " + wiperswitch, Toast.LENGTH_SHORT).show();
                        sendSig.data = LinSignal.packIntToBytes(wiperswitch);
                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }
                }
                else if (SID_DEFROST == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        Toast.makeText(getApplicationContext(), "::Defrost:: " + defrostswitch, Toast.LENGTH_SHORT).show();
                        sendSig.data = LinSignal.packIntToBytes(defrostswitch);
                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO ?
                    }
                }
                else if (SID_TURNSIGNAL == signal.sid) {
                    if (signal.command == LinSignal.COMM_SET_VAR) {
                        Toast.makeText(getApplicationContext(), "::Turn_Signal:: No Data", Toast.LENGTH_SHORT).show();
                        //sendSig.data = LinSignal.packIntToBytes((warningOn)?1:0);
                    }
                    else if (signal.command == LinSignal.COMM_WARN_VAR) {
                        // TODO
                    }
                }
                else {
                    // TODO
                    // send dummy value (could not understand data sent)
                    sendSig.command = LinSignal.COMM_WARN_VAR;
                    Toast.makeText(getApplicationContext(), "::ERROR:: Did not understand inputs", Toast.LENGTH_SHORT).show();
                }

                // after we update the GUI/get updates from the GUI, send the update
                linBus.sendSignal(sendSig);
            }
        };


        linBus = new LinBus() { // LinBus.java
            @Override
            public void receiveSignal(LinSignal signal) {
                Message msg = Message.obtain(usbInputHandler);
                msg.obj = signal;
                usbInputHandler.sendMessage(msg);
            }
        };

        usb_send_receive = new USB_Send_Receive();
        usb_send_receive.onCreate(this, usbInputHandler, linBus);
    }

// *********************************************************************************************************
// ******************End Cut Here******************************************************************************
// *********************************************************************************************************

    @Override
    public void onResume() {
        super.onResume();

        usb_send_receive.onResume(getIntent());
    }


    @Override
    public void onPause() {
        super.onPause();

        usb_send_receive.onPause();
    }


    @Override
    public void onDestroy() {

        usb_send_receive.onDestroy(this);

        super.onDestroy();
    }


// *********************************************************************************************************
// ******************End Cut Here******************************************************************************
// *********************************************************************************************************
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
            Log.d("mShowPart2Runnable", "run...");
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

}

