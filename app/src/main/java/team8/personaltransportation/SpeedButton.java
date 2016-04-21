package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Joseph O on 4/18/2016.
 */
public class SpeedButton extends Abstract_Button {

    int digitnum;		// the speedometer digit number
    Hashtable<Integer, Drawable> speedHash;
    boolean IamLeftDigit;        // am I the left digit (false for right)

    public SpeedButton(Context mycontext, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDrawArr, int digitnum, boolean IamLeftDigit, Hashtable<Integer, Drawable> speedHash) {
        super(mycontext, sidNum, buttonView, onDrawArr);
        this.digitnum = digitnum;
        this.IamLeftDigit = IamLeftDigit;
        this.speedHash = speedHash;
        this.clickable = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.buttonView.setBackgroundDrawable(speedHash.get(0));
        } else {
            this.buttonView.setBackground(speedHash.get(0));
        }
    }

    @Override
    void buttonClicked() {
        // does nothing
    }

    @Override
    public LinSignal update(LinSignal signal) {
        LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {
            digitnum = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);
            Toast.makeText(this, "::Speed:: " + digitnum, Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                this.buttonView.setBackgroundDrawable(speedHash.get(mySpeed(digitnum)));
            } else {
                this.buttonView.setBackground(speedHash.get(mySpeed(digitnum)));
            }
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(this, "::Speed Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            mySig.data = LinSignal.packIntToBytes(this.myState());
        }
        return mySig;
    }

    // override function to affect the value which is passed to the buttonview
    int mySpeed(int currentSpeed) {
        if (IamLeftDigit)
            return currentSpeed / 10;    // preset for the larger digit
        else
            return currentSpeed % 10;
    }

}
