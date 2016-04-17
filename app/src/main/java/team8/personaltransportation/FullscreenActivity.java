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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    boolean brightsOn = false;
    int wiperswitch = 0;
    int defrostswitch = 0;
    private ImageView GPSbutton;
    private TextView GPStextview;
    private LocationManager locationManager;
    private LocationListener locationListener;

    USB_Send_Receive usb_send_receive;

    /********************* Variables for DEFROST (AC) *********************/
    Hashtable<Integer,Integer> Hazard_hash;
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
        //final ImageView warningButton = (ImageView) findViewById(R.id.warning);
        /*** Ming's ***/
        final ImageView rightturn = (ImageView) this.findViewById(R.id.rightTurn);
        final ImageView leftturn = (ImageView) this.findViewById(R.id.leftTurn);
        final ImageView hazardbut = (ImageView) this.findViewById(R.id.warning);

        hazardAnim = new AnimationDrawable();
        hazardAnimOFF = new AnimationDrawable();
        hazardAnimOFF.addFrame(getResources().getDrawable(R.drawable.warningoffnew), 100);
//        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningoffnew), 200);
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningonnew), 200);
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningonbnew), 200);

        rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            hazardbut.setBackgroundDrawable(hazardAnim);
        } else {
            hazardbut.setBackground(hazardAnim);
        }

        hazardbut.setBackgroundResource(R.drawable.warningoffnew);


        hazardbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");
                if (warningOn) {
                    hazardAnim.stop();
                    hazardbut.setBackgroundResource(R.drawable.warningoffnew);

                    //leftAnim.setVisible(true, true);
                    leftAnim.stop();
                    leftturn.setBackgroundResource(R.drawable.leftturnsignaloffnew);
                    //leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);

                    //rightAnim.setVisible(true, true);
                    rightAnim.stop();
                    rightturn.setBackgroundResource(R.drawable.rightturnsignaloffnew);
                    //rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);

                    Toast toast1 = Toast.makeText(FullscreenActivity.this, "Hazards Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast1.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toastTV.setTextSize(30);
                    toast1.show();

                    warningOn = false;

                } else {
                    //hazardAnim.start();
                    //hazardAnim.setLooping(true);
                    Toast toast2 = Toast.makeText(FullscreenActivity.this, "Hazards On, Contacting Emergency Services.", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast2.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast2.setGravity(Gravity.CENTER, 0, 0);
                    toastTV.setTextSize(30);
                    toast2.show();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        hazardbut.setBackgroundDrawable(hazardAnim);
                    } else {
                        hazardbut.setBackground(hazardAnim);
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rightturn.setBackgroundDrawable(rightAnim);
                    } else {
                        rightturn.setBackground(rightAnim);
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        leftturn.setBackgroundDrawable(leftAnim);
                    } else {
                        leftturn.setBackground(leftAnim);
                    }

                    if(rightAnim.isRunning()) {

                        rightAnim.stop();
                    }

                    if(leftAnim.isRunning()) {

                        leftAnim.stop();
                    }

                    if(hazardAnim.isRunning()) {

                        hazardAnim.stop();
                    }

                    hazardAnim.setOneShot(false);
                    hazardAnim.start();

                    rightAnim.setOneShot(false);
                    rightAnim.start();

                    leftAnim.setOneShot(false);
                    leftAnim.start();

                    warningOn = true;
                }
            }
        });


        /*setting for right turn animation*/
        rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            rightturn.setBackgroundDrawable(rightAnim);
        } else {
            rightturn.setBackground(rightAnim);
        }

        rightturn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!hazardAnim.isRunning()) {
                            if (rightAnim.isRunning()) {
                                rightAnim.stop();
                                rightturn.setBackgroundResource(R.drawable.rightturnsignaloffnew);
                                return true;
                            } else {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    rightturn.setBackgroundDrawable(rightAnim);
                                } else {
                                    rightturn.setBackground(rightAnim);
                                }
                                rightAnim.setOneShot(false);
                                rightAnim.start();

                                if (leftAnim.isRunning()) {
                                    leftAnim.stop();
                                    leftturn.setBackgroundResource(R.drawable.leftturnsignaloffnew);
                                }
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });

        /*setting for rightturn animation*/
        /*setting for leftturn animation*/
        leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            leftturn.setBackgroundDrawable(leftAnim);
        } else {
            leftturn.setBackground(leftAnim);
        }

        leftturn.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!hazardAnim.isRunning()) {
                            if (leftAnim.isRunning()) {
                                leftAnim.stop();
                                leftturn.setBackgroundResource(R.drawable.leftturnsignaloffnew);
                                return true;
                            } else {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    leftturn.setBackgroundDrawable(leftAnim);
                                } else {
                                    leftturn.setBackground(leftAnim);
                                }
                                leftAnim.setOneShot(false);
                                leftAnim.start();

                                if (rightAnim.isRunning()) {
                                    rightAnim.stop();
                                    rightturn.setBackgroundResource(R.drawable.rightturnsignaloffnew);
                                    //rightAnim.pause();
                                }
                                return true;
                            }
                        }
                    }
                }
                return false;
            }});

        /**************************************************************************************
        hazardAnim = new AnimationDrawable();
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningoffnew), hazarduration);
        hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningoffnew), hazarduration);
        //hazardAnim.stop();

       // hazardAnimOn = new AnimationDrawable();
       // hazardAnimOn.addFrame(getResources().getDrawable(R.drawable.warningonbnew), hazarduration);
      //  hazardAnimOn.addFrame(getResources().getDrawable(R.drawable.warningonnew), hazarduration);
      //  hazardAnimOn.start();

        rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            hazardbut.setBackgroundDrawable(hazardAnim);
        } else {
            hazardbut.setBackground(hazardAnim);
        }

        hazardbut.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                robot.start();
                Toast toast2 = Toast.makeText(FullscreenActivity.this, "Hazards On, Contacting Emergency Services.", Toast.LENGTH_SHORT);
                LinearLayout toastLayout = (LinearLayout) toast2.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toast2.setGravity(Gravity.CENTER, 240, -500);
                toastTV.setTextSize(30);
                toast2.show();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (hazardAnim.isRunning()) {
                            hazardAnim.stop();
                            hazardAnim.addFrame(getResources().getDrawable(R.drawable.warningoffnew), 0);

                            rightAnim.stop();
                            rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), longduration);

                            leftAnim.stop();
                            leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), longduration);

                            return true;
                        } else {
                            hazardAnim.setOneShot(false);
                            hazardAnim.start();

                            leftAnim.setOneShot(false);
                            leftAnim.start();

                            rightAnim.setOneShot(false);
                            rightAnim.start();

                            return true;
                        }
                    }
                }
                return false;
            }
        });
        //warningButton.setImageResource(R.drawable.warningoffnew);
       /* warningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");

                if (warningOn) {
                    pressButSound.start();
                    Toast toast1 = Toast.makeText(FullscreenActivity.this, "Hazards Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast1.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast1.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast1.show();
                    warningButton.setImageResource(R.drawable.warningoffnew);
                    warningOn = false;
                } else {
                    pressButSound.start();
                    Toast toast2 = Toast.makeText(FullscreenActivity.this, "Hazards On, Contacting Emergency Services.", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast2.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast2.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast2.show();
                    warningButton.setImageResource(R.drawable.warningonnew);
                    warningOn = true;
                }
            }
        });*/
        /*
        rightAnim = new AnimationDrawable();
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), longduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal1new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal2new), rightduration);
        rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignal3new), rightduration);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            rightturn.setBackgroundDrawable(rightAnim);
        } else {
            rightturn.setBackground(rightAnim);
        }

        rightturn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (rightAnim.isRunning()) {
                            rightAnim.stop();
                            rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
                            return true;
                        } else {

                            if(leftAnim.isRunning()){
                                leftAnim.stop();
                                leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
                            }
                            rightAnim.setOneShot(false);
                            rightAnim.start();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        leftAnim = new AnimationDrawable();
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), longduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal1new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal2new), leftduration);
        leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignal3new), leftduration);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            leftturn.setBackgroundDrawable(leftAnim);
        } else {
            leftturn.setBackground(leftAnim);
        }

        leftturn.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (leftAnim.isRunning()) {
                            leftAnim.stop();
                            leftAnim.addFrame(getResources().getDrawable(R.drawable.leftturnsignaloffnew), 0);
                            return true;
                        } else {
                            if(rightAnim.isRunning()){
                                rightAnim.stop();
                                rightAnim.addFrame(getResources().getDrawable(R.drawable.rightturnsignaloffnew), 0);
                            }
                            leftAnim.setOneShot(false);
                            leftAnim.start();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        *************************************************************************/


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
                //Intent intent = new Intent();
                //intent.setAction(Intent.ACTION_VIEW);
                //startActivity(new Intent(FullscreenActivity.this, activitysettings.class));
            }
        });

        /************************** HEADLAMP **************************************/
        final ImageView headlampButton = (ImageView) findViewById(R.id.headLamp);
        headlampButton.setImageResource(R.drawable.headlampoffnew);
        headlampButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");

                if (headlampOn) {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Headlamps Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    headlampButton.setImageResource(R.drawable.headlampoffnew);
                    headlampOn = false;
                } else {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Headlamps On", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    headlampButton.setImageResource(R.drawable.headlamponnew);
                    headlampOn = true;
                }
            }
        });

        final ImageView brightsButton = (ImageView) findViewById(R.id.brights);
        //headlampButton.setImageResource(R.drawable.brightsoffnew);
        brightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");

                if (brightsOn) {
                    pressButSound.start();
                    Toast toast =  Toast.makeText(FullscreenActivity.this, "Brights Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    brightsButton.setImageResource(R.drawable.brightsoffnew);
                    brightsOn = false;
                } else {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Brights On", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    brightsButton.setImageResource(R.drawable.brightsonnew);
                    brightsOn = true;
                }
            }
        });


        /*************************** working on this ********************************************/
        final ImageView defrostButton = (ImageView) findViewById(R.id.defrost1);
        Hazard_hash = new Hashtable<>();
        Hazard_hash.put(1, R.drawable.defroston1new);
        Hazard_hash.put(2, R.drawable.defroston2new);
        Hazard_hash.put(3, R.drawable.defroston3new);
        Hazard_hash.put(0, R.drawable.defrostoffnew);
        defrostButton.setImageResource(R.drawable.defrostoffnew);
        defrostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");

                if (defrostswitch == 0) {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Defrost Low", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    defrostButton.setImageResource(R.drawable.defroston1new);
                    defrostswitch = 1;
                } else if (defrostswitch == 1) {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Defrost Medium", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    defrostButton.setImageResource(R.drawable.defroston2new);
                    defrostswitch = 2;
                } else if (defrostswitch == 2) {
                    pressButSound.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Defrost High", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    defrostButton.setImageResource(R.drawable.defroston3new);
                    defrostswitch = 3;
                } else {
                    pleasebut.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Defrost Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    defrostButton.setImageResource(R.drawable.defrostoffnew);
                    defrostswitch = 0;
                }
            }
        });

        final ImageView batteryButton = (ImageView) findViewById(R.id.batteryLife);
        batteryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                robot2.start();
                Toast toast = Toast.makeText(FullscreenActivity.this, "Battery Level is at " + batteryLife + "%", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toast.setGravity(Gravity.CENTER, 200, -500);
                toastTV.setTextSize(30);
                toast.show();
            }
        });
        /*************************** working ********************************************/
        final ImageView wiperButton = (ImageView) findViewById(R.id.wiper);
        Wiper_hash = new Hashtable<>();
        Wiper_hash.put(1,R.drawable.wiperson1new);
        Wiper_hash.put(2,R.drawable.wiperson2new);
        Wiper_hash.put(3,R.drawable.wiperson3new);
        Wiper_hash.put(0,R.drawable.wipersoffnew);
        wiperButton.setImageResource(R.drawable.wipersoffnew);
        wiperButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("BUTTON", "I clicked it!");

                if (wiperswitch == 0) {
                    pindrop.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Wipers Low", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    wiperButton.setImageResource(R.drawable.wiperson1new);
                    wiperswitch = 1;
                } else if (wiperswitch == 1) {
                    pindrop.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Wipers Medium", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    wiperButton.setImageResource(R.drawable.wiperson2new);
                    wiperswitch = 2;
                } else if (wiperswitch == 2) {
                    pindrop.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Wipers High", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    wiperButton.setImageResource(R.drawable.wiperson3new);
                    wiperswitch = 3;
                } else {
                    pindrop.start();
                    Toast toast = Toast.makeText(FullscreenActivity.this, "Wipers Off", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toast.setGravity(Gravity.CENTER, 240, -500);
                    toastTV.setTextSize(30);
                    toast.show();
                    wiperButton.setImageResource(R.drawable.wipersoffnew);
                    wiperswitch = 0;
                }
            }
        });

        /*************************** Battery ********************************************/
        batButton = (ImageView) findViewById(R.id.batteryLife);
        //batButton.setImageResource(R.drawable.battery100);        //switched this to a speed bezel for another view setup
        Battery_hash = new Hashtable<>();
        for (int i = 0; i < 5; i++) {
            Battery_hash.put(i, R.drawable.battery00new);
        }
        for (int i = 5; i < 30; i++) {
            Battery_hash.put(i, R.drawable.battery20new);
        }
        for (int i = 30; i < 50; i++) {
            Battery_hash.put(i, R.drawable.battery40new);
        }
        for (int i = 50; i < 70; i++) {
            Battery_hash.put(i, R.drawable.battery60new);
        }
        for (int i = 70; i < 95; i++) {
            Battery_hash.put(i, R.drawable.battery80new);
        }
        for (int i = 95; i < 100; i++) {
            Battery_hash.put(i, R.drawable.battery100new);
        }

        /*************************** Speedometer ******************************************/
        Speed_handle1 = (ImageView) findViewById(R.id.leftspeedo);
        Speed_handle2 = (ImageView) findViewById(R.id.rightspeedo);
        Speed_handle1.setImageResource(R.drawable.zero);
        Speed_handle2.setImageResource(R.drawable.zeror);
        Speed_hash_left = new Hashtable<>();
        Speed_hash_left.put(0, R.drawable.zero);
        Speed_hash_left.put(1, R.drawable.onel);
        Speed_hash_left.put(2, R.drawable.twol);
        Speed_hash_left.put(3, R.drawable.threel);
        Speed_hash_left.put(4, R.drawable.fourl);
        Speed_hash_left.put(5, R.drawable.fivel);
        Speed_hash_left.put(6, R.drawable.sixl);
        Speed_hash_left.put(7, R.drawable.sevenl);
        Speed_hash_left.put(8, R.drawable.eightl);
        Speed_hash_left.put(9, R.drawable.ninel);
        Speed_hash_right = new Hashtable<>();
        Speed_hash_right.put(0, R.drawable.zeror);
        Speed_hash_right.put(1, R.drawable.oner);
        Speed_hash_right.put(2, R.drawable.twor);
        Speed_hash_right.put(3, R.drawable.threer);
        Speed_hash_right.put(4, R.drawable.fourr);
        Speed_hash_right.put(5, R.drawable.fiver);
        Speed_hash_right.put(6, R.drawable.sixr);
        Speed_hash_right.put(7, R.drawable.sevenr);
        Speed_hash_right.put(8, R.drawable.eightr);
        Speed_hash_right.put(9, R.drawable.niner);

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

                        batButton.setImageResource(Battery_hash.get(batteryLife));

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

                        Speed_handle1.setImageResource(Speed_hash_left.get(currentSpeed / 10));
                        Speed_handle2.setImageResource(Speed_hash_right.get(currentSpeed%10));

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
                        sendSig.data = LinSignal.packIntToBytes((warningOn)?1:0);
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

