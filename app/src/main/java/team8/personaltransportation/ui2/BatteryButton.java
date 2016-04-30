package team8.personaltransportation.ui2;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import team8.personaltransportation.R;

public class BatteryButton extends ImageButton {
    private static final int[] BATTERY_00_STATE = { R.attr.state_battery_0 };
    private static final int[] BATTERY_20_STATE = { R.attr.state_battery_20 };
    private static final int[] BATTERY_40_STATE = { R.attr.state_battery_40 };
    private static final int[] BATTERY_60_STATE = { R.attr.state_battery_60 };
    private static final int[] BATTERY_80_STATE = { R.attr.state_battery_80 };
    private static final int[] BATTERY_100_STATE = { R.attr.state_battery_100 };

    private int level = 0;

    public BatteryButton(Context context) {
        super(context);
    }

    public BatteryButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributes(context, attrs);
    }

    public BatteryButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BatteryStates, 0, 0);

        if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_0, false))
            level = 0;
        else if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_20, false))
            level = 20;
        else if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_40, false))
            level = 40;
        else if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_60, false))
            level = 60;
        else if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_80, false))
            level = 80;
        else if(typedArray.getBoolean(R.styleable.BatteryStates_state_battery_100, false))
            level = 100;
        typedArray.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if(level > 90)
            mergeDrawableStates(drawableState,BATTERY_100_STATE);
        else if(level > 70)
            mergeDrawableStates(drawableState,BATTERY_80_STATE);
        else if(level > 50)
            mergeDrawableStates(drawableState,BATTERY_60_STATE);
        else if(level > 30)
            mergeDrawableStates(drawableState,BATTERY_40_STATE);
        else if(level > 10)
            mergeDrawableStates(drawableState,BATTERY_20_STATE);
        else
            mergeDrawableStates(drawableState,BATTERY_00_STATE);
        return drawableState;
    }

    public int getBatteryLevel() {
        return level;
    }

    public void setBatteryLevel(int level) {
        this.level = level;
        refreshDrawableState();
    }
}