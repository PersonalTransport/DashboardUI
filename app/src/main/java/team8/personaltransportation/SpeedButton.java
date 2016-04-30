package team8.personaltransportation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class SpeedButton extends ImageButton {
    private static final int[] SPEED_0_STATE = {R.attr.state_speed_0};
    private static final int[] SPEED_1_STATE = {R.attr.state_speed_1};
    private static final int[] SPEED_2_STATE = {R.attr.state_speed_2};
    private static final int[] SPEED_3_STATE = {R.attr.state_speed_3};
    private static final int[] SPEED_4_STATE = {R.attr.state_speed_4};
    private static final int[] SPEED_5_STATE = {R.attr.state_speed_5};
    private static final int[] SPEED_6_STATE = {R.attr.state_speed_6};
    private static final int[] SPEED_7_STATE = {R.attr.state_speed_7};
    private static final int[] SPEED_8_STATE = {R.attr.state_speed_8};
    private static final int[] SPEED_9_STATE = {R.attr.state_speed_9};

    private int speed = 0;

    public SpeedButton(Context context) {
        super(context);
    }

    public SpeedButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributes(context, attrs);
    }

    public SpeedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SpeedStates, 0, 0);

        if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_0, false))
            speed = 0;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_1, false))
            speed = 1;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_2, false))
            speed = 2;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_3, false))
            speed = 3;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_4, false))
            speed = 4;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_5, false))
            speed = 5;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_6, false))
            speed = 6;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_7, false))
            speed = 7;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_8, false))
            speed = 8;
        else if (typedArray.getBoolean(R.styleable.SpeedStates_state_speed_9, false))
            speed = 9;
        typedArray.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        switch (speed) {
            case 0: {
                mergeDrawableStates(drawableState, SPEED_0_STATE);
                break;
            }
            case 1: {
                mergeDrawableStates(drawableState, SPEED_1_STATE);
                break;
            }
            case 2: {
                mergeDrawableStates(drawableState, SPEED_2_STATE);
                break;
            }
            case 3: {
                mergeDrawableStates(drawableState, SPEED_3_STATE);
                break;
            }
            case 4: {
                mergeDrawableStates(drawableState, SPEED_4_STATE);
                break;
            }
            case 5: {
                mergeDrawableStates(drawableState, SPEED_5_STATE);
                break;
            }
            case 6: {
                mergeDrawableStates(drawableState, SPEED_6_STATE);
                break;
            }
            case 7: {
                mergeDrawableStates(drawableState, SPEED_7_STATE);
                break;
            }
            case 8: {
                mergeDrawableStates(drawableState, SPEED_8_STATE);
                break;
            }
            case 9: {
                mergeDrawableStates(drawableState, SPEED_9_STATE);
                break;
            }
        }
        return drawableState;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(0, Math.min(speed, 9));
        refreshDrawableState();
    }
}