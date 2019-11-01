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

    private void sendTouchEventToParent(View view, MotionEvent event) {
        event.setLocation(event.getX() + view.getX(), event.getY() + view.getY());
        parent.onTouchEvent(event);
        event.setLocation(event.getX() - view.getX(), event.getY() - view.getY());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // beginning of touch event
                moved = false;
                sendTouchEventToParent(view, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canScroll((HorizontalScrollView) view)) {
                    moved = true;
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    sendTouchEventToParent(view, event);
                    event.setAction(MotionEvent.ACTION_MOVE);
                } else {
                    sendTouchEventToParent(view, event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                sendTouchEventToParent(view, event);
                break;
            case MotionEvent.ACTION_UP:
                if (!moved) {
                    sendTouchEventToParent(view, event);
                }
                break;
        }

        return false;
    }
}
