package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joseph O on 4/18/2016.
 */
public class HeadlampButton extends Abstract_Button {

    MediaPlayer buttonsound;
    MediaPlayer buttonsound_off;
    private String[] HeadlampLevels;
    private int numStates;
    private int ON_STATE = 2;

    public HeadlampButton(Context mycontext, LinBus toSendData, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDraw, String[] HeadlampLevels, WiperDefrostButton hibeames, final MediaPlayer buttonsound, final MediaPlayer buttonsound_off) {
        super(mycontext, toSendData, sidNum, buttonView, onDraw);
        this.buttonsound = buttonsound;
        this.buttonsound_off = buttonsound_off;
        assert(onDraw.size() == HeadlampLevels.length);      // the number of states has to equal the number of print statements
        this.HeadlampLevels = HeadlampLevels;
        numStates = onDraw.size();
        // add hi-beams as child
        this.addChild(hibeames);
        hibeames.ModifyStateFromParent(OFF_STATE+1, OFF_STATE);
    }

    @Override
    void buttonClicked(){

        if (!clickable) return;

        Toast toast = Toast.makeText(getApplicationContext(), HeadlampLevels[this.myState()], Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toast.setGravity(Gravity.CENTER, 240, -500);
        toastTV.setTextSize(30);
        toast.show();

        if (this.myState() >= numStates-1) {
            buttonsound_off.start();            //:)
            this.turnOff(this.myState());
        } else {
            buttonsound.start();
            this.turnOn(ON_STATE, this.myState(), this.myState(), OFF_STATE);
        }

        LinSignal sendSig = new LinSignal(LinSignal.COMM_SET_VAR, getSid(), (byte) 4, LinSignal.packIntToBytes(this.myState()));
        toSendData.sendSignal(sendSig);
    }

    // called outside when this button needs user input
    @Override
    public LinSignal update(LinSignal signal){

        if (signal.command == LinSignal.COMM_SET_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost:: " + this.myState(), Toast.LENGTH_SHORT).show();
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }

}
