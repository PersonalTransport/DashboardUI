package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joseph O on 4/18/2016.
 */
public class TurnSignalButton extends AbstractButton {

    public static final boolean LEFTTURN = false;
    public static final boolean RIGHTTURN = true;

    TurnSignalButton otherTurnSignal;
    private boolean direction;
    private int currentState;

    public TurnSignalButton(Context mycontext, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDrawArr, boolean direction) {
        //ArrayList<Drawable> onDrawArr = new ArrayList<Drawable>();
        //onDrawArr.add(DrawStates);
        super(mycontext, sidNum, buttonView, onDrawArr);
        // FYI: parent hazard button already adds this as parent, so turn signals don't need to worry about adding their parent.
        this.direction = direction;
    }

    public void setOtherTurnSignal(TurnSignalButton otherTurnSignal) {
        this.otherTurnSignal = otherTurnSignal;
    }

    public void turnOn(int state) {
        super.turnOn(state);
        DrawStates.get(state).setOneShot(false);
        DrawStates.get(state).start();
    }

    public void turnOff(int state) {
        super.turnOff(buttonState);
        DrawStates.get(state).stop();
        buttonState = OFF_STATE;
    }

    @Override
    public void buttonClicked() {
        if (!clickable) return;
        if (this.myState() != OFF_STATE) {
            this.turnOff(buttonState);
        } else {
            this.turnOn(buttonState + 1);
            otherTurnSignal.turnOff(otherTurnSignal.buttonState);
        }
    }

    @Override
    public LinSignal update(LinSignal signal) {
        LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {
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
