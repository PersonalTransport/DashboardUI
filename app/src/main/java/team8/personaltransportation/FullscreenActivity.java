/*********************************************************************************************
 Brainstorming for format:

 * Fixing getResources:
 http://stackoverflow.com/questions/32765906/android-getdrawable-deprecated-how-to-use-android-getdrawable
 http://stackoverflow.com/questions/29041027/android-getresources-getdrawable-deprecated-api-22/34750353
 http://developer.android.com/reference/android/support/v4/content/ContextCompat.html

 //        rightAnim.addFrame(ContextCompat.getDrawable(getActivity(), R.drawable.rightturnsignaloffnew), 0);
 //        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);


 Module similarities between wipers, battery, speed, turn signals, hazard, ...
 * Each has a button image/button clickable
 * each changes their image when clicked
 ** Buttons may have either animation or image background
 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
 leftturn.setBackgroundDrawable(leftAnim);
 } else {
 leftturn.setBackground(leftAnim);
 }

 * there may be dependencies between buttons - if they can be clicked/behaviour
 ** Turn signals have dependance on whether hazard is on

 * Different image when clicked, and different image when not clicked
 * Functionality is tied to USB output
 * setOnClickListener - initialization
 * If on, turn off. If off, turn on.
 * usbInputHander - functionality
 *
 *

 *********************************************************************************************/
/*********************************************************************************************/

package team8.personaltransportation;

// Sources:
// USB communication -
// http://developer.android.com/guide/topics/connectivity/usb/accessory.html
// http://www.java2s.com/Open-Source/Android_Free_Code/Example/code/com_examples_accessory_controllerMainUsbActivity_java.htm
// http://www.ftdichip.com/Support/SoftwareExamples/Android_Projects.htm

import android.Manifest;
import android.annotation.SuppressLint;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.app.AppCompactActivity;
//import android.app.ActivityManager;
//import android.app.ActivityOptions;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
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
    boolean brightsOn = false;
    int wiperswitch = 0;
    int defrostswitch = 0;
    private ImageView GPSbutton;
    private TextView GPStextview;
    private LocationManager locationManager;
    private LocationListener locationListener;

    USB_Send_Receive usb_send_receive;

    /************************ Storage for Button classes *******************/
    ArrayList<Abstract_Button> myButtons;
    //WiperButton myWiperButton;
    ArrayList<AnimationDrawable> onDrawArr_Wipers;
    /********************* Variables for DEFROST (AC) *********************/
    Hashtable<Integer,Integer> Defrost_hash;
    /********************* Variables for WIPERS *********************/
    Hashtable<Integer,Integer> Wiper_hash;
    /********************* Variables for BATTERY *********************/
    Hashtable<Integer,Integer> Battery_hash;
    int batteryLife = 0;
    ImageView batButton;

    /********************* Variables for SPEEDOMETER *********************/
    ImageView Speed_handle1;
    ImageView Speed_handle2;
    Hashtable<Integer,Integer> Speed_hash_left;
    Hashtable<Integer,Integer> Speed_hash_right;
    int currentSpeed = 0;

    Handler usbInputHandler;
    LinBus linBus;

    AnimationDrawable rightAnim;
    AnimationDrawable leftAnim;
    AnimationDrawable hazardAnim;
    AnimationDrawable hazardAnimOFF;
    //AnimationDrawable hazardAnimOn;

    int rightduration = 200;
    int leftduration = 200;
    int hazarduration = 200;
    int longduration = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        // add sound to the button press
        final MediaPlayer pressButSound = MediaPlayer.create(FullscreenActivity.this, R.raw.robotblip);
        final MediaPlayer robot = MediaPlayer.create(FullscreenActivity.this, R.raw.horn);
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
        /*** setup button array ***/
        myButtons = new ArrayList<>();
        /*** Ming's ***/
        /************************** Turn Signal *********************************************/
        final ImageView rightturn = (ImageView) this.findViewById(R.id.rightTurn);
        final ImageView leftturn = (ImageView) this.findViewById(R.id.leftTurn);
        final ImageView hazardbut = (ImageView) this.findViewById(R.id.warning);

        AnimationDrawable hazardAnim = new AnimationDrawable();
        AnimationDrawable hazardAnimOFF = new AnimationDrawable();
        hazardAnimOFF.addFrame(getResources().getDrawable(R.drawable.warningoffnew), 100);
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningonnew), 200);
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningonbnew), 200);

        AnimationDrawable rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        AnimationDrawable leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);


        /*setting for right turn animation*/
        rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        ArrayList<AnimationDrawable> onDrawArr_RightTurn = new ArrayList<>();
        AnimationDrawable rightAnim_off = new AnimationDrawable();
        rightAnim_off.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        onDrawArr_RightTurn.add(0, rightAnim_off);
        onDrawArr_RightTurn.add(1, rightAnim);

        TurnSignalButton myTurnSignalButtonR = new TurnSignalButton(this, linBus, SID_TURNSIGNAL, rightturn, onDrawArr_RightTurn, true);


        /*setting for leftturn animation*/
        leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);

        ArrayList<AnimationDrawable> onDrawArr_LeftTurn = new ArrayList<>();
        AnimationDrawable leftAnim_off = new AnimationDrawable();
        leftAnim_off.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        onDrawArr_LeftTurn.add(0, leftAnim_off);
        onDrawArr_LeftTurn.add(1, leftAnim);

        TurnSignalButton myTurnSignalButtonL = new TurnSignalButton(this, linBus, SID_TURNSIGNAL, leftturn, onDrawArr_LeftTurn, false);

        myTurnSignalButtonL.otherTurnSignal = myTurnSignalButtonR;
        myTurnSignalButtonR.otherTurnSignal = myTurnSignalButtonL;
        myButtons.add(myTurnSignalButtonR);
        myButtons.add(myTurnSignalButtonL);

        /************************** Hazard Button *********************************************/
        ArrayList<AnimationDrawable> onDrawArr_Hazard = new ArrayList<>();
        onDrawArr_Hazard.add(0, hazardAnimOFF);
        onDrawArr_Hazard.add(1, hazardAnim);
        HazardButton myHazardButton = new HazardButton(this, linBus, SID_HAZARD, hazardbut, onDrawArr_Hazard, myTurnSignalButtonL, myTurnSignalButtonR);
        myButtons.add(myHazardButton);

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
                Intent i = new Intent(FullscreenActivity.this, activitysettings.class);
                startActivity(i);
            }
        });

        /************************** HEADLAMP **************************************/
        final ImageView headlampButton = (ImageView) findViewById(R.id.headLamp);

        String[] HeadlampLevels = new String[]{"Headlamps On", "Headlamps Off"};
        headlampButton.setImageResource(0);
        // create an array of headlamp states which can be displayed
        ArrayList<AnimationDrawable> onDrawArr_Headlamp = new ArrayList<>();
        AnimationDrawable State_Headlamp0 = new AnimationDrawable();
        State_Headlamp0.addFrame(getResources().getDrawable(R.drawable.headlampoffnew), 0);

        AnimationDrawable State_Headlamp1 = new AnimationDrawable();
        State_Headlamp1.addFrame(getResources().getDrawable(R.drawable.headlamponnew), 0);

        //leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);

        onDrawArr_Headlamp.add(0, State_Headlamp0);
        onDrawArr_Headlamp.add(1, State_Headlamp1);

        WiperDefrostButton myHeadlampsButton = new WiperDefrostButton(this, linBus, SID_LIGHTS, headlampButton, onDrawArr_Headlamp, HeadlampLevels, pressButSound, pindrop);
        myButtons.add(myHeadlampsButton);


        /************************** HIBEAMS **************************************/
        // TODO: absorb HIBEAMS into Lights
        final ImageView hiBeamssButton = (ImageView) findViewById(R.id.brights);

        hiBeamssButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (brightsOn) {
                    pressButSound.start();
                    Toast toast =  Toast.makeText(FullscreenActivity.this, "Brights Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    hiBeamssButton.setImageResource(R.drawable.brightsoffnew);
                    brightsOn = false;
                } else {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Brights On", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    hiBeamssButton.setImageResource(R.drawable.brightsonnew);
                    brightsOn = true;
                }
            }
        });


        /*************************** DEFROST ********************************************/
        final ImageView defrostButton = (ImageView) findViewById(R.id.defrost1);
        String[] DefrostLevels = new String[]{"Defrost Low", "Defrost Medium", "Defrost High", "Defrost Off"};
        defrostButton.setImageResource(0);
        // create an array of wiper states which can be displayed
        ArrayList<AnimationDrawable> onDrawArr_Defrost = new ArrayList<>();
        AnimationDrawable State_Defrost0 = new AnimationDrawable();
        State_Defrost0.addFrame(getResources().getDrawable(R.drawable.defrostoffnew), 0);

        AnimationDrawable State_Defrost1 = new AnimationDrawable();
        State_Defrost1.addFrame(getResources().getDrawable(R.drawable.defroston1new), 0);

        AnimationDrawable State_Defrost2 = new AnimationDrawable();
        State_Defrost2.addFrame(getResources().getDrawable(R.drawable.defroston2new), 0);

        AnimationDrawable State_Defrost3 = new AnimationDrawable();
        State_Defrost3.addFrame(getResources().getDrawable(R.drawable.defroston3new), 0);
        //leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);

        onDrawArr_Defrost.add(0, State_Defrost0);
        onDrawArr_Defrost.add(1, State_Defrost1);
        onDrawArr_Defrost.add(2, State_Defrost2);
        onDrawArr_Defrost.add(3, State_Defrost3);

        WiperDefrostButton myDefrostButton = new WiperDefrostButton(this, linBus, SID_DEFROST, defrostButton, onDrawArr_Defrost, DefrostLevels, pressButSound, pleasebut);
        myButtons.add(myDefrostButton);


        /*************************** WIPERS ********************************************/
        final ImageView wiperButton = (ImageView) findViewById(R.id.wiper);
        String[] WiperLevels = new String[]{"Wipers Low", "Wipers Medium", "Wipers High", "Wipers Off"};
        wiperButton.setImageResource(0);    // make sure there are no images on the screen that will cover our images
        // create an array of wiper states which can be displayed
        ArrayList<AnimationDrawable> onDrawArr_Wipers = new ArrayList<>();
        AnimationDrawable offState_Wiper0 = new AnimationDrawable();
        offState_Wiper0.addFrame(getResources().getDrawable(R.drawable.wipersoffnew), 0);

        AnimationDrawable offState_Wiper1 = new AnimationDrawable();
        offState_Wiper1.addFrame(getResources().getDrawable(R.drawable.wiperson1new), 0);

        AnimationDrawable offState_Wiper2 = new AnimationDrawable();
        offState_Wiper2.addFrame(getResources().getDrawable(R.drawable.wiperson2new), 0);

        AnimationDrawable offState_Wiper3 = new AnimationDrawable();
        offState_Wiper3.addFrame(getResources().getDrawable(R.drawable.wiperson3new), 0);
        //leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);

        onDrawArr_Wipers.add(0, offState_Wiper0);
        onDrawArr_Wipers.add(1, offState_Wiper1);
        onDrawArr_Wipers.add(2, offState_Wiper2);
        onDrawArr_Wipers.add(3, offState_Wiper3);

        WiperDefrostButton myWiperButton = new WiperDefrostButton(this, linBus, SID_WIPERS, wiperButton, onDrawArr_Wipers, WiperLevels, pindrop, pindrop);
        myButtons.add(myWiperButton);


        /*************************** BATTERY ********************************************/
        final ImageView batButton = (ImageView) findViewById(R.id.batteryLife);
        batButton.setImageResource(0);

        Hashtable<Integer,Drawable> Battery_hash;
        Battery_hash = new Hashtable<>();

        for (int i = 0; i < 5; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery00new));
        }
        for (int i = 5; i < 30; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery20new));
        }
        for (int i = 30; i < 50; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery40new));
        }
        for (int i = 50; i < 70; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery60new));
        }
        for (int i = 70; i < 95; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery80new));
        }
        for (int i = 95; i < 100; i++) {
            Battery_hash.put(i, getResources().getDrawable(R.drawable.battery100new));
        }

        // add an initial state for start-image reasons
        ArrayList<AnimationDrawable> onDrawArr_Battery = new ArrayList<>();
        AnimationDrawable offState_battery = new AnimationDrawable();
        offState_battery.addFrame(getResources().getDrawable(R.drawable.battery100new), 0);
        onDrawArr_Battery.add(0, offState_battery);


        BatteryButton myBatteryButton = new BatteryButton(this, linBus, SID_BATTERY, batButton, onDrawArr_Battery, Battery_hash, robot2);
        myButtons.add(myBatteryButton);


        /*************************** SPEEDOMETER ******************************************/
        ImageView Speed_handle1 = (ImageView) findViewById(R.id.leftspeedo);
        ImageView Speed_handle2 = (ImageView) findViewById(R.id.rightspeedo);
        Speed_handle1.setImageResource(0);
        Speed_handle2.setImageResource(0);

        // add an initial state for start-image reasons (left image)
        ArrayList<AnimationDrawable> onDrawArr_Speedl = new ArrayList<>();
        AnimationDrawable offState_speedl = new AnimationDrawable();
        offState_speedl.addFrame(getResources().getDrawable(R.drawable.zero), 0);
        onDrawArr_Speedl.add(0, offState_speedl);
        // add an initial state for start-image reasons (left image)
        ArrayList<AnimationDrawable> onDrawArr_Speedr = new ArrayList<>();
        AnimationDrawable offState_speedr = new AnimationDrawable();
        offState_speedr.addFrame(getResources().getDrawable(R.drawable.zeror), 0);
        onDrawArr_Speedr.add(0, offState_speedr);

        Hashtable<Integer,Drawable> Speed_hash_left_digit = new Hashtable<>();
        Speed_hash_left_digit.put(0, getResources().getDrawable(R.drawable.zero));
        Speed_hash_left_digit.put(1, getResources().getDrawable(R.drawable.onel));
        Speed_hash_left_digit.put(2, getResources().getDrawable(R.drawable.twol));
        Speed_hash_left_digit.put(3, getResources().getDrawable(R.drawable.threel));
        Speed_hash_left_digit.put(4, getResources().getDrawable(R.drawable.fourl));
        Speed_hash_left_digit.put(5, getResources().getDrawable(R.drawable.fivel));
        Speed_hash_left_digit.put(6, getResources().getDrawable(R.drawable.sixl));
        Speed_hash_left_digit.put(7, getResources().getDrawable(R.drawable.sevenl));
        Speed_hash_left_digit.put(8, getResources().getDrawable(R.drawable.eightl));
        Speed_hash_left_digit.put(9, getResources().getDrawable(R.drawable.ninel));
        Hashtable<Integer,Drawable> Speed_hash_right_digit = new Hashtable<>();
        Speed_hash_right_digit.put(0, getResources().getDrawable(R.drawable.zeror));
        Speed_hash_right_digit.put(1, getResources().getDrawable(R.drawable.oner));
        Speed_hash_right_digit.put(2, getResources().getDrawable(R.drawable.twor));
        Speed_hash_right_digit.put(3, getResources().getDrawable(R.drawable.threer));
        Speed_hash_right_digit.put(4, getResources().getDrawable(R.drawable.fourr));
        Speed_hash_right_digit.put(5, getResources().getDrawable(R.drawable.fiver));
        Speed_hash_right_digit.put(6, getResources().getDrawable(R.drawable.sixr));
        Speed_hash_right_digit.put(7, getResources().getDrawable(R.drawable.sevenr));
        Speed_hash_right_digit.put(8, getResources().getDrawable(R.drawable.eightr));
        Speed_hash_right_digit.put(9, getResources().getDrawable(R.drawable.niner));

        SpeedButton mySpeedButtonl = new SpeedButton(this, linBus, SID_SPEED, Speed_handle1, onDrawArr_Speedl, 0, true, Speed_hash_left_digit);
        SpeedButton mySpeedButtonr = new SpeedButton(this, linBus, SID_SPEED, Speed_handle2, onDrawArr_Speedr, 0, false, Speed_hash_right_digit);

        myButtons.add(mySpeedButtonl);
        myButtons.add(mySpeedButtonr);

        // Handles incoming messages
        usbInputHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Boolean ActivatedButton;

                Log.d("UIHandler", "handling message: " + msg);

                // interpret current data
                LinSignal signal = (LinSignal) msg.obj;
                // begin to prepare data to be sent back
                LinSignal[] sendSigArr = new LinSignal[myButtons.size()];

                LinSignal sendSig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);

                Toast.makeText(getApplicationContext(), String.valueOf(LinSignal.signalHash("BATTERY".getBytes(), 0)) + ", " + String.valueOf(signal.sid) + "; " + String.valueOf(LinSignal.COMM_SET_VAR) + ", " + String.valueOf(signal.command) + "; " + String.valueOf(signal.data), Toast.LENGTH_SHORT).show();

                ActivatedButton = false;
                int ix = 0;
                int ij = 0;
                for (Abstract_Button button : myButtons) {
                    if (button.getSid() == signal.sid){
                        sendSigArr[ix++] = button.update(signal);
                        ij = ix - 1;
                        ActivatedButton = true;
                    }
                }

                if (!ActivatedButton) {
                    sendSig.command = LinSignal.COMM_WARN_VAR;
                    Toast.makeText(getApplicationContext(), "::ERROR:: Did not understand inputs", Toast.LENGTH_SHORT).show();
                }

                // after we update the GUI/get updates from the GUI, we DON'T send updates (when we get data, we just update GUI, that's it)
                // TODO: for now, keep it in for testing purposes
                //linBus.sendSignal(sendSigArr[ij]);
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

