package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joseph O on 4/18/2016.
 */
public class TurnSignalButton extends Abstract_Button {

    public static final boolean LEFTTURN = false;
    public static final boolean RIGHTTURN = true;

    TurnSignalButton otherTurnSignal;
    private boolean direction;
    private int currentState;

    public TurnSignalButton(Context mycontext, LinBus linBus, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDrawArr, boolean direction) {
        super(mycontext, linBus, sidNum, buttonView, onDrawArr);
        this.direction = direction;
    }

    public void setOtherTurnSignal(TurnSignalButton otherTurnSignal) {
        this.otherTurnSignal = otherTurnSignal;
    }

    // Additional logic: turn on animation
    public void turnOn(int state) {
        super.turnOn(state);
        DrawStates.get(state).setOneShot(false);
        DrawStates.get(state).start();
    }

    // Additional logic: turn off animation
    public void turnOff(int state) {
        super.turnOff(state);
        DrawStates.get(state).stop();
        buttonState = OFF_STATE;
    }

    @Override
    public void buttonClicked() {
        if (!clickable) return;
        if (this.myState() != OFF_STATE) {
            this.turnOff(buttonState);
        } else {
            if (direction == RIGHTTURN) {
                this.turnOn(buttonState + 2);
            }
            else {
                this.turnOn(buttonState + 1);
            }
            otherTurnSignal.turnOff(otherTurnSignal.buttonState);
        }

        LinSignal sendSig = new LinSignal(LinSignal.COMM_SET_VAR, getSid(), (byte) 4, LinSignal.packIntToBytes(this.myState()));
        toSendData.sendSignal(sendSig);
    }

    @Override
    public LinSignal update(LinSignal signal) {
        int temp_signal_data = 0;
        LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {

            temp_signal_data = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);
            if (temp_signal_data == OFF_STATE) {
                if (this.myState() != OFF_STATE) {
                    this.turnOff(buttonState);
                }
            } else if ((direction == RIGHTTURN) && (temp_signal_data == buttonState + 2)) {
                this.buttonClicked();
            } else if ((direction == LEFTTURN) && (temp_signal_data == buttonState + 1)) {
                this.buttonClicked();
            }

            Toast.makeText(this, "::" + ((direction == RIGHTTURN) ? "Right" : "Left") + "TurnSignal:: " + this.myState(), Toast.LENGTH_SHORT).show();
            mySig.data = LinSignal.packIntToBytes(this.myState());
            return mySig;
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(this, "::" + ((direction == RIGHTTURN) ? "Right" : "Left") + "TurnSignal Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }

}
