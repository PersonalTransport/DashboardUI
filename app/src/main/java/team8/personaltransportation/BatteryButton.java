package team8.personaltransportation;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Joseph O on 4/17/2016.
 */
public class BatteryButton extends AbstractButton {

    Hashtable<Integer,Drawable> Battery_hash;
    MediaPlayer buttonsound;
    int batteryLife;

    public BatteryButton(Context mycontext, int sidNum, ImageView buttonView, ArrayList<AnimationDrawable> onDrawArr, Hashtable<Integer,Drawable> Battery_hash, final MediaPlayer buttonsound) {
        //ArrayList<Drawable> onDrawArr = new ArrayList<Drawable>();
        //onDrawArr.add(DrawStates);
        super(mycontext, sidNum, buttonView, onDrawArr);
        this.Battery_hash = Battery_hash;
        this.buttonsound = buttonsound;
        // TODO: Pull out of here,
//       	for (int i = 0; i < 5; i++) {
//             Battery_hash.put(i, R.drawable.battery00new);
//         }
//         for (int i = 5; i < 30; i++) {
//             Battery_hash.put(i, R.drawable.battery20new);
//         }
//         for (int i = 30; i < 50; i++) {
//             Battery_hash.put(i, R.drawable.battery40new);
//         }
//         for (int i = 50; i < 70; i++) {
//             Battery_hash.put(i, R.drawable.battery60new);
//         }
//         for (int i = 70; i < 95; i++) {
//             Battery_hash.put(i, R.drawable.battery80new);
//         }
//         for (int i = 95; i < 100; i++) {
//             Battery_hash.put(i, R.drawable.battery100new);
//         }
    }

    @Override
    void buttonClicked() {
        buttonsound.start();
        Toast toast = Toast.makeText(this, "Battery Level is at " + batteryLife + "%", Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toast.setGravity(Gravity.CENTER, 200, -500);
        toastTV.setTextSize(30);
        toast.show();
    }

    @Override
    public LinSignal update(LinSignal signal) {
        LinSignal mySig = new LinSignal(signal.command, signal.sid, signal.length, signal.data);
        if (signal.command == LinSignal.COMM_SET_VAR) {
            batteryLife = LinSignal.unpackBytesToInt(signal.data[0], signal.data[1], signal.data[2], signal.data[3]);
            Toast.makeText(this, "::Battery:: " + batteryLife, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                buttonView.setBackgroundDrawable(Battery_hash.get(batteryLife));
            } else {
                buttonView.setBackground(Battery_hash.get(batteryLife));
            }
        }
        else if (signal.command == LinSignal.COMM_WARN_VAR) {
            Toast.makeText(this, "::Battery Error:: " + new String(signal.data), Toast.LENGTH_SHORT).show();
            mySig.data = LinSignal.packIntToBytes(batteryLife);
        }
        return mySig;
    }

}
