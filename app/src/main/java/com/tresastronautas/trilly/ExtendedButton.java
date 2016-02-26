package com.tresastronautas.trilly;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ExtendedButton extends Button {

    private Point p;
    private Drawable d;

    public ExtendedButton(Context context) {
        super(context);
    }

    public ExtendedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // Determine what action with a switch statement
        switch (event.getAction()) {

            // User presses down on the ImageView, record the original point
            // and set the color filter
            case MotionEvent.ACTION_DOWN: {

                // overlay is black with transparency of 0x77 (119)

                d = getBackground();
                setBackgroundColor(Color.argb((int) (0.5 * 255.0f), 0, 0, 0));
                invalidate();

                p = new Point((int) event.getX(), (int) event.getY());
                break;
            }

            // Once the Path releases, record new point then compare the
            // difference, if within a certain range perform onCLick
            // and or otherwise clear the color filter
            case MotionEvent.ACTION_UP: {
                Point f = new Point((int) event.getX(), (int) event.getY());
                if ((Math.abs(f.x - p.x) < 15)
                        && ((Math.abs(f.x - p.x) < 15))) {
                    performClick();
                }
                // clear the overlay
                setBackground(d);
                invalidate();
                break;
            }
        }
        return true;
    }

}
