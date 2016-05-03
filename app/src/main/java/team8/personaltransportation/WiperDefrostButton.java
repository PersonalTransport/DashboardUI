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
public class WiperDefrostButton extends Abstract_Button {

    MediaPlayer buttonsound;
    MediaPlayer buttonsound_off;
    private String[] DefrostLevels;
    private int numStates;

    public WiperDefrostButton(Context mycontext, LinBus toSendData, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDraw, String[] DefrostLevels, final MediaPlayer buttonsound, final MediaPlayer buttonsound_off) {
        super(mycontext, toSendData, sidNum, buttonView, onDraw);
        this.buttonsound = buttonsound;
        this.buttonsound_off = buttonsound_off;
        assert(onDraw.size() == DefrostLevels.length);      // the number of states has to equal the number of print statements
        this.DefrostLevels = DefrostLevels;
        numStates = onDraw.size();
    }

    @Override
    void buttonClicked(){

        //LinSignal mySig = new LinSignal();// provide pre-defined values
        Toast toast = Toast.makeText(getApplicationContext(), DefrostLevels[this.myState()], Toast.LENGTH_SHORT);
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
            this.turnOn(this.myState() + 1);
        }

        toSendData.sendSignal(new LinSignal(sidNum, LinSignal.packIntToBytes(this.myState())));
    }

    // called outside when this button needs user input
    @Override
    public LinSignal update(LinSignal signal){
        //LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost:: " + this.myState(), Toast.LENGTH_SHORT).show();
            //mySig.data = LinSignal.packIntToBytes(this.myState());
            //return mySig;
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            return null;
        }
        //linBus.sendSignal(sendSigArr[ij]);
        return null;
    }

}
