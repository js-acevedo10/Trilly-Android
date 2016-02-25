package com.tresastronautas.trilly;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by juansantiagoacev on 2/25/16.
 */
public class ExtendedImageButton extends ImageButton {

    public Point p;

    public ExtendedImageButton(Context context) {
        super(context);
    }

    public ExtendedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                getDrawable().setColorFilter(0x77000000,
                        PorterDuff.Mode.SRC_ATOP);
                invalidate();

                p = new Point((int) event.getX(), (int) event.getY());
                break;
            }

            // Once the user releases, record new point then compare the
            // difference, if within a certain range perform onCLick
            // and or otherwise clear the color filter
            case MotionEvent.ACTION_UP: {
                Point f = new Point((int) event.getX(), (int) event.getY());
                if ((Math.abs(f.x - p.x) < 15)
                        && ((Math.abs(f.x - p.x) < 15))) {
                    performClick();
                }
                // clear the overlay
                getDrawable().clearColorFilter();
                invalidate();
                break;
            }
        }
        return true;
    }
}
