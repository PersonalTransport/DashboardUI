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
public class DefrostButton extends Abstract_Button {

    MediaPlayer buttonsound;

    public DefrostButton(Context mycontext, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDraw, final MediaPlayer buttonsound) {
        super(mycontext, sidNum, buttonView, onDraw);
        this.buttonsound = buttonsound;
    }

    @Override
    void buttonClicked(){
        if (this.myState() == 0) {
            buttonsound.start();
            Toast toast = Toast.makeText(getApplicationContext(), "Defrost Low", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast.setGravity(Gravity.CENTER, 240, -500);
            toastTV.setTextSize(30);
            toast.show();
            this.turnOn(1);
        } else if (this.myState() == 1) {
            buttonsound.start();
            Toast toast = Toast.makeText(getApplicationContext(), "Defrost Medium", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast.setGravity(Gravity.CENTER, 240, -500);
            toastTV.setTextSize(30);
            toast.show();
            this.turnOn(2);
        } else if (this.myState() == 2) {
            buttonsound.start();
            Toast toast = Toast.makeText(getApplicationContext(), "Defrost High", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast.setGravity(Gravity.CENTER, 240, -500);
            toastTV.setTextSize(30);
            toast.show();
            this.turnOn(3);
        } else {
            buttonsound.start();
            Toast toast = Toast.makeText(getApplicationContext(), "Defrost Off", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast.setGravity(Gravity.CENTER, 240, -500);
            toastTV.setTextSize(30);
            toast.show();
            this.turnOff(0);
        }
    }

    // called outside when this button needs user input
    @Override
    byte[] update(LinSignal signal){
        if (signal.command == LinSignal.COMM_SET_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost:: " + this.myState(), Toast.LENGTH_SHORT).show();
            return LinSignal.packIntToBytes(this.myState());
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(getApplicationContext(), "::Defrost Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }

}
