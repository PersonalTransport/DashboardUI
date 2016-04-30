/*********************************************************************************************
 Brainstorming for format:

 * Fixing getResources:
 http://stackoverflow.com/questions/32765906/android-getdrawable-deprecated-how-to-use-android-getdrawable
 http://stackoverflow.com/questions/29041027/android-getresources-getdrawable-deprecated-api-22/34750353
 http://developer.android.com/reference/android/support/v4/content/ContextCompat.html
/*********************************************************************************************/

package team8.personaltransportation;

// Sources:
// USB communication -
// http://developer.android.com/guide/topics/connectivity/usb/accessory.html
// http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
// http://www.ftdichip.com/Support/SoftwareExamples/Android_Projects.htm

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import team8.personaltransportation.ui2.BatteryButton;
import team8.personaltransportation.ui2.FourStateButton;
import team8.personaltransportation.ui2.SpeedButton;
import team8.personaltransportation.ui2.TwoStateButton;

public class FullscreenActivity extends Activity {

    private int SID_BATTERY = LinSignal.signalHash("BATTERY".getBytes(), 0);
    private int SID_LIGHTS = LinSignal.signalHash("LIGHTS".getBytes(), 0);
    private int SID_SPEED = LinSignal.signalHash("SPEED".getBytes(), 0);
    private int SID_TURNSIGNAL = LinSignal.signalHash("TURN_SIGNAL".getBytes(), 0);
    private int SID_HAZARD = LinSignal.signalHash("HAZARD".getBytes(), 0);
    private int SID_DEFROST = LinSignal.signalHash("DEFROST".getBytes(), 0);
    private int SID_WIPERS = LinSignal.signalHash("WIPERS".getBytes(), 0);

    private ImageView GPSbutton;
    private TextView GPStextview;
    private LocationManager locationManager;
    private LocationListener locationListener;

    USBSendReceive usbSendReceive;


    Handler usbInputHandler;
    LinBus linBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        // add sound to the button press
        final MediaPlayer pressButSound = MediaPlayer.create(FullscreenActivity.this, R.raw.robotblip);
        final MediaPlayer robot2 = MediaPlayer.create(FullscreenActivity.this, R.raw.robotblip2);
        final MediaPlayer pleasebut = MediaPlayer.create(FullscreenActivity.this, R.raw.pleaseturnoff);
        final MediaPlayer pindrop = MediaPlayer.create(FullscreenActivity.this, R.raw.pindrop);

        GPSbutton = (ImageView) findViewById(R.id.phoneconnbutn2);
        GPStextview = (TextView) findViewById(R.id.fullscreen_content);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                GPStextview.append("\n " + location.getLatitude() + " " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }
        else {
            configureButton();
        }

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mControlsView = findViewById(R.id.fullscreen_content_controls);


        /************************** Turn Signal *********************************************/
        final TwoStateButton rightTurnSignal = (TwoStateButton) this.findViewById(R.id.rightTurn);
        final TwoStateButton leftTurnSignal = (TwoStateButton) this.findViewById(R.id.leftTurn);
        final TwoStateButton hazardButton = (TwoStateButton) this.findViewById(R.id.warning);

        rightTurnSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hazardButton.isOn()) {
                    rightTurnSignal.toggle();
                    if (rightTurnSignal.isOn()) {
                        leftTurnSignal.setOn(false);
                    }
                }
            }
        });

        leftTurnSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hazardButton.isOn()) {
                    leftTurnSignal.toggle();
                    if (leftTurnSignal.isOn()) {
                        rightTurnSignal.setOn(false);
                    }
                }
            }
        });


        /************************** Hazard Button *********************************************/
        hazardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hazardButton.toggle();
                leftTurnSignal.setOn(hazardButton.isOn());
                rightTurnSignal.setOn(hazardButton.isOn());
            }
        });

        /************************** HEADLAMP **************************************/
        final TwoStateButton lowBeamButton = (TwoStateButton)findViewById(R.id.headLamp);
        final TwoStateButton highBeamButton = (TwoStateButton)findViewById(R.id.brights);

        lowBeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButSound.start();
                lowBeamButton.toggle();
                if (!lowBeamButton.isOn())
                    highBeamButton.setOn(false);
            }
        });

        highBeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lowBeamButton.isOn()) {
                    pressButSound.start();
                    highBeamButton.toggle();
                } else {
                    highBeamButton.setOn(false);
                }
            }
        });

        /*************************** DEFROST ********************************************/
        final FourStateButton defrostButton = (FourStateButton) findViewById(R.id.defrost1);
        defrostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButSound.start();
                defrostButton.nextState();
            }
        });


        /*************************** WIPERS ********************************************/
        final FourStateButton wiperButton = (FourStateButton) findViewById(R.id.wiper);
        wiperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButSound.start();
                wiperButton.nextState();
            }
        });


        /*************************** BATTERY ********************************************/
        final BatteryButton batteryButton = (BatteryButton) findViewById(R.id.batteryLife);


        /*************************** SPEEDOMETER ******************************************/
        final SpeedButton leftSpeedButton = (SpeedButton) findViewById(R.id.leftspeedo);
        final SpeedButton rightSpeedButton = (SpeedButton) findViewById(R.id.rightspeedo);


        /************************** SETTINGS **************************************/
        final ImageView settingsButton = (ImageView) findViewById(R.id.settingsbutton);
        settingsButton.setImageResource(R.drawable.cirbuttonmsc);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressButSound.start();
                Toast toast3 = Toast.makeText(FullscreenActivity.this, "You Clicked Settings", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast3.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toast3.setGravity(Gravity.CENTER, 240, -500);
                toastTV.setTextSize(30);
                toast3.show();
                Intent i = new Intent(FullscreenActivity.this, ActivitySettings.class);
                startActivity(i);
            }
        });


        // Handles incoming messages
        usbInputHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d("UIHandler", "handling message: " + msg);

               // TODO handle the message
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

        usbSendReceive = new USBSendReceive();
        usbSendReceive.onCreate(this, usbInputHandler, linBus);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    configureButton();
                }
                return;
        }
    }
    private void configureButton() {
        GPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationManager.requestLocationUpdates("gps",5000,0,locationListener);
            }
        });
    }

// *********************************************************************************************************
// ******************End Cut Here******************************************************************************
// *********************************************************************************************************

    @Override
    public void onResume() {
        super.onResume();

        usbSendReceive.onResume(getIntent());
    }


    @Override
    public void onPause() {
        super.onPause();

        usbSendReceive.onPause();
    }


    @Override
    public void onDestroy() {

        usbSendReceive.onDestroy(this);

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
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("mShowPart2Runnable", "run...");
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
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

