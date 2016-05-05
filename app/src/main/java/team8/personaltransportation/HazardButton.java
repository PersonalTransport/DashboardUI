package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joseph O on 4/18/2016.
 */
public class HazardButton extends Abstract_Button {

    public HazardButton(Context mycontext, LinBus linBus, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDrawArr, TurnSignalButton turnL, TurnSignalButton turnR) {
        //ArrayList<Drawable> onDrawArr = new ArrayList<Drawable>();
        //onDrawArr.add(DrawStates);
        super(mycontext, linBus, sidNum, buttonView, onDrawArr);
        // now add the turn signals as children buttons
        this.addChild(turnL);
        this.addChild(turnR);
        turnL.addParent(this);
        turnR.addParent(this);
    }

    public void turnOn(int state) {
        super.turnOn(state);
        DrawStates.get(state).setOneShot(false);
        DrawStates.get(state).start();
    }

    public void turnOff(int state) {
        super.turnOff(this.myState());
        DrawStates.get(state).stop();
        buttonState = OFF_STATE;
    }

    @Override
    public void buttonClicked() {
        if (this.myState() != OFF_STATE) {
            this.turnOff(this.myState());

            Toast toast1 = Toast.makeText(this, "Hazards Off", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast1.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast1.setGravity(Gravity.CENTER, 240, -500);
            toastTV.setTextSize(30);
            toast1.show();

        } else {
            this.turnOn(this.myState() + 1);

            Toast toast2 = Toast.makeText(this, "Hazards On, Contacting Emergency Services.", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast2.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast2.setGravity(Gravity.CENTER, 0, 0);
            toastTV.setTextSize(30);
            toast2.show();
        }
    }

    @Override
    public LinSignal update(LinSignal signal) {
        LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {
            Toast.makeText(this, "::Hazard:: " + this.myState(), Toast.LENGTH_SHORT).show();
            mySig.data = LinSignal.packIntToBytes(this.myState());
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(this, "::Hazard Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
        }
        return mySig;
    }
}
