package team8.personaltransportation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class FourStateButton extends ImageButton {
    private static final int[] OFF_STATE = {R.attr.state_four_state_off};
    private static final int[] LOW_STATE = {R.attr.state_four_state_low};
    private static final int[] MEDIUM_STATE = {R.attr.state_four_state_medium};
    private static final int[] HIGH_STATE = {R.attr.state_four_state_high};

    private int state = 0;

    public FourStateButton(Context context) {
        super(context);
    }

    public FourStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributes(context, attrs);
    }

    public FourStateButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attributeSet) {

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FourStateButtonStates, 0, 0);

        if (typedArray.getBoolean(R.styleable.FourStateButtonStates_state_four_state_off, false)) {
            state = 0;
        } else if (typedArray.getBoolean(R.styleable.FourStateButtonStates_state_four_state_low, false)) {
            state = 1;
        } else if (typedArray.getBoolean(R.styleable.FourStateButtonStates_state_four_state_medium, false)) {
            state = 2;
        } else if (typedArray.getBoolean(R.styleable.FourStateButtonStates_state_four_state_high, false)) {
            state = 3;
        }

        typedArray.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        switch (state) {
            case 0: {
                mergeDrawableStates(drawableState, OFF_STATE);
                break;
            }
            case 1: {
                mergeDrawableStates(drawableState, LOW_STATE);
                break;
            }
            case 2: {
                mergeDrawableStates(drawableState, MEDIUM_STATE);
                break;
            }
            case 3: {
                mergeDrawableStates(drawableState, HIGH_STATE);
                break;
            }
        }
        return drawableState;
    }

    public void setState(int state) {
        this.state = state;
        refreshDrawableState();
    }

    public int getState() {
        return this.state;
    }

    public void nextState() {
        state++;
        if (state > 3)
            state = 0;
        refreshDrawableState();
    }
}
