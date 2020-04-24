package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;

import com.stypox.mastercom_workbook.R;

public class APIUrlEdit extends AppCompatEditText implements AdapterView.OnItemClickListener {
    String[] strings;
    ListPopupWindow listPopupWindow;

    public APIUrlEdit(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public APIUrlEdit(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.APIUrlEdit,0, 0);

        try {
            int stringsId = typedArray.getResourceId(R.styleable.APIUrlEdit_entries, 0);
            if (stringsId == 0) throw new IllegalArgumentException("Must specify entries for APIUrlEdit");

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = strings[position];
        setText(s);
        listPopupWindow.dismiss();
    }
}
