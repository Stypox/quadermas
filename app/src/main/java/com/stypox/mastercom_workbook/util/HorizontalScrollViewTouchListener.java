package com.stypox.mastercom_workbook.util;

import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Clicks on a HorizontalScrollView are not passed down to
 * the parent view. This onTouch listener fixes the issue.
 */
public class HorizontalScrollViewTouchListener implements View.OnTouchListener {
    private boolean moved;
    private View parent;

    public HorizontalScrollViewTouchListener(View parent) {
        this.parent = parent;
    }

    private boolean canScroll(HorizontalScrollView view) {
        View child = view.getChildAt(0);
        if (child == null) {
            return false;
        }

        int childWidth = (child).getWidth();
        return view.getWidth() < childWidth + view.getPaddingLeft() + view.getPaddingRight();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // beginning of touch event
                moved = false;
                parent.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canScroll((HorizontalScrollView) view)) {
                    moved = true;
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    parent.onTouchEvent(event);
                    event.setAction(MotionEvent.ACTION_MOVE);
                } else {
                    parent.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                parent.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                if (!moved) {
                    parent.onTouchEvent(event);
                }
                break;
        }

        return false;
    }
}
