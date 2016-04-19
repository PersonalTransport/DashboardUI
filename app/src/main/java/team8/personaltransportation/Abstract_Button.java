package team8.personaltransportation;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


abstract public class Abstract_Button extends ContextWrapper
{
    //final public static boolean ON_STATE = true;
    final public static int OFF_STATE = 0;

    protected boolean clickable = true;
    protected int buttonState = OFF_STATE; // on or off state

    protected ImageView buttonView;
    protected ArrayList<AnimationDrawable> DrawStates;		// can have multiple on states (have the first state be the off state)
    //protected AnimationDrawable offDraw;

    private List<Abstract_Button> childButtons;	  // dependencies for buttons which must be off before this button can be turned on
    private List<Abstract_Button> parentButtons;// dependencies for buttons which will be turned on when this button is turned on

    private int sidNum;


    public Abstract_Button(Context base, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> DrawStates, boolean onStart) {
        super(base);
        this.sidNum = sidNum;  //LinSignal.signalHash(sidStr.getBytes(), 0);
        this.DrawStates = DrawStates;
        this.buttonView = buttonView;
        childButtons = new LinkedList<Abstract_Button>();
        parentButtons = new LinkedList<Abstract_Button>();

        if (onStart) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                buttonView.setBackgroundDrawable(DrawStates.get(0));
            } else {
                buttonView.setBackground(DrawStates.get(0));
            }
        }

        this.buttonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClicked();
            }
        });

    }

    public Abstract_Button(Context base, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> DrawStates) {
        this(base, sidNum, buttonView, DrawStates, true);
    }

        // get the button's sid
    final public int getSid() {
        return sidNum;
    }

    // Add a dependancy for this button
    // The dependency button is turned off when this button is pressed
    final public void addChild(Abstract_Button newChild) {
        childButtons.add(newChild);
    }

    // Add a dependency for this button.
    // The dependency button must be off for this button to be pressed
    final public void addParent(Abstract_Button newParent) {
        parentButtons.add(newParent);
    }

    // fuction which parent calls to modify children button's states. Can be overrwritten
    // when implementing abstract class to perform more functionality before/after turning on/off button.
    public void ModifyStateFromParent(int newState){
        if (newState != OFF_STATE) {
            turnOff(1);
            turnOn(newState);
            setNotClickable();
            //turnOff();	// should I do this first (to reset animations and stuff?)

        } else {
            setClickable();
            turnOff(1);
        }
    }

    final public void setClickable() {
        clickable = true;
    }

    final public void setNotClickable() {
        clickable = false;
    }

    // Return the state of the button
    final public int myState() {
        return buttonState;
    }

    private void setDispOn(int state) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            buttonView.setBackgroundDrawable(DrawStates.get(state));
        } else {
            buttonView.setBackground(DrawStates.get(state));
        }
    }

    private void setDispOff() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            buttonView.setBackgroundDrawable(DrawStates.get(OFF_STATE));
        } else {
            buttonView.setBackground(DrawStates.get(OFF_STATE));
        }
    }

    // turn the button state on
    public void turnOn(int nextState) {
        if (nextState == myState() || !clickable)
            return;
        if (nextState == OFF_STATE) {
            turnOff(0);
            return;
        }
        for (Abstract_Button but : parentButtons) {
            if (but.myState() != OFF_STATE)
                return;
        }
        for (Abstract_Button but : childButtons) {
            but.ModifyStateFromParent(nextState);
        }
        this.setDispOn(nextState);
        buttonState = nextState;
    }

    public void turnOn() {
        this.turnOn(1);
    }

    // Turn the button state off
    public void turnOff(int off_test) {
        if (buttonState == OFF_STATE || !clickable)
            return;
 /*       for (Abstract_Button but : parentButtons) {
            if (but.myState() != OFF_STATE)
                return;
        }*/
        for (Abstract_Button but : childButtons) {
            but.ModifyStateFromParent(OFF_STATE);
        }
        setDispOff();
        buttonState = OFF_STATE;
    }

    // A simple method to be able to cycle through button states
    public void turnOn_NextState() {
        int drawStatesNum = DrawStates.size();
        buttonState++;
        if (buttonState >= drawStatesNum) {
            buttonState = 0;
        }
        turnOn(buttonState);
    }

    abstract void buttonClicked();
    abstract byte[] update(LinSignal signal); // called outside when this button needs user input

}