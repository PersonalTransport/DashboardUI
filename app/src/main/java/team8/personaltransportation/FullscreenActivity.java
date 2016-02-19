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
import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//public class FullscreenActivity extends AppCompatActivity {
public class FullscreenActivity extends Activity {

    // variables for GUI interface
    boolean warningOn = false;
    boolean headlampOn = false;
    int wiperswitch = 0;
    int defrostswitch = 0;

    USB_Send_Receive usb_send_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

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

        usb_send_receive.onCreate(this);
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

