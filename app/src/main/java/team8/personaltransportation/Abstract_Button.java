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

    final public static int OFF_STATE = 0;

    protected boolean clickable = true;
    protected int buttonState = OFF_STATE; // on or off state

    protected ImageView buttonView;
    protected ArrayList<AnimationDrawable> DrawStates;		// can have multiple on states (have the first state be the off state)

    private List<Abstract_Button> childButtons;	  // dependencies for buttons which must be off before this button can be turned on
    private List<Abstract_Button> parentButtons;// dependencies for buttons which will be turned on when this button is turned on

    protected LinBus toSendData;      // to make the android tablet send data to the master controller on a button press,
    private int sidNum;

    public Abstract_Button(Context base, LinBus toSendData, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> DrawStates, boolean onStart) {
        super(base);
        this.sidNum = sidNum;  //LinSignal.signalHash(sidStr.getBytes(), 0);
        this.toSendData = toSendData;
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

    public Abstract_Button(Context base, LinBus toSendData, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> DrawStates) {
        this(base, toSendData, sidNum, buttonView, DrawStates, true);
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

    // fuction which parent calls to modify children button's states. Can be overwritten
    // when implementing abstract class to perform more functionality before/after turning on/off button.
    // if newState is OFF_STATE, then set the button to be clickable and turn off the button state
    // if newState is not OFF_STATE, then turn on button to
    public void ModifyStateFromParent(int newState, int nextState){
        if (newState != OFF_STATE) {
            turnOff(buttonState);
            turnOn(nextState);
            setNotClickable();

        } else {
            setClickable();
            turnOff(buttonState);
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
    // if a parent button state is not child_off_state, then don't change my state
    // as my state is turnedOn, modify children state to be nextState
    public void turnOn(int nextState, int parent_off_state, int child_next_state, int child_off_state) {
        if (nextState == myState() || !clickable)
            return;
        if (nextState == OFF_STATE) {
            turnOff(buttonState);
            return;
        }

        for (Abstract_Button parent : parentButtons) {
            if (parent.myState() != parent_off_state)
                return;
        }

        for (Abstract_Button but : childButtons) {
            but.ModifyStateFromParent(child_next_state, child_off_state);
        }
        this.setDispOn(nextState);
        buttonState = nextState;
    }

    public void turnOn(int nextState) { this.turnOn(nextState, OFF_STATE, nextState, nextState);}

    public void turnOn() {
        this.turnOn(1);
    }

    // Turn the button state off
    public void turnOff(int child_off_state) {
        if (buttonState == OFF_STATE || !clickable)
            return;

        for (Abstract_Button but : childButtons) {
            but.ModifyStateFromParent(child_off_state, OFF_STATE);
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
    abstract LinSignal update(LinSignal signal); // called outside when this button needs user input

}