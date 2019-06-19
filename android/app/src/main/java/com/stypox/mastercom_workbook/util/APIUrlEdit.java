package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.stypox.mastercom_workbook.R;

public class APIUrlEdit extends android.support.v7.widget.AppCompatEditText implements AdapterView.OnItemClickListener {
    String[] strings;
    ListPopupWindow listPopupWindow;

    APIUrlEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.APIUrlEdit,0, 0);

        try {
            int stringsId = typedArray.getResourceId(R.styleable.APIUrlEdit_list_entries, 0);
            if (stringsId == 0) throw new IllegalArgumentException("Must specify list_entries for APIUrlEdit");

            strings = getResources().getStringArray(stringsId);
            if (strings.length == 0) throw new IllegalArgumentException("APIUrlEdit's list_entries cannot be empty");
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && event.getX() >= (getWidth() - getCompoundPaddingRight())) {
            listPopupWindow = new ListPopupWindow(getContext());
            listPopupWindow.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, strings));
            listPopupWindow.setAnchorView(this);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(this);
            listPopupWindow.show();

            return true; // prevent keyboard from showing up
        }

        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                performClick();
                return true;
            default:
                return false;
        }
    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = strings[position];
        setText(s);
        listPopupWindow.dismiss();
    }
}
