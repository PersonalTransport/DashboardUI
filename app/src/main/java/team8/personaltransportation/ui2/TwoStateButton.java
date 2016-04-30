package team8.personaltransportation.ui2;

import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import team8.personaltransportation.R;

public class TwoStateButton extends ImageButton {
    private static final int[] ON_STATE = { R.attr.state_two_state_on };

    private boolean state = false;

    public TwoStateButton(Context context) {
        super(context);
    }

    public TwoStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributes(context, attrs);
    }

    public TwoStateButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attributeSet) {

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TwoStateButtonStates, 0, 0);

        state = typedArray.getBoolean(R.styleable.TwoStateButtonStates_state_two_state_on, false);

        typedArray.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if(state) {
            int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState,ON_STATE);
            return drawableState;
        }
        return super.onCreateDrawableState(extraSpace);
    }

    public boolean isOn() {
        return this.state;
    }

    public void setOn(boolean state) {
        this.state = state;
        refreshDrawableState();
        if(getBackground().getCurrent() instanceof Animatable) {
            ((Animatable) getBackground().getCurrent()).start();
        }
    }

    public void toggle() {
        this.setOn(!this.state);
    }
}
