package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;

public class DocumentItemHolder extends ItemHolder<DocumentData> {
    private TextView nameView;
    private TextView subjectAndOwnerView;
    private HorizontalScrollView nameScrollView;

    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    public DocumentItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<DocumentData> adapter) {
        super(itemView, adapter);

        nameView = itemView.findViewById(R.id.name);
        subjectAndOwnerView = itemView.findViewById(R.id.subjectAndOwner);
        nameScrollView = itemView.findViewById(R.id.nameScrollView);

        context = itemView.getContext();


        nameScrollView.setOnTouchListener(new View.OnTouchListener() {
            boolean moved;

            private boolean textCanScroll() {
                View child = nameScrollView.getChildAt(0);
                if (child == null) {
                    return false;
                }

                int childWidth = (child).getWidth();
                return nameScrollView.getWidth() < childWidth + nameScrollView.getPaddingLeft() + nameScrollView.getPaddingRight();

            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // beginning of touch event
                        moved = false;
                        itemView.onTouchEvent(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (textCanScroll()) {
                            moved = true;
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            itemView.onTouchEvent(event);
                            event.setAction(MotionEvent.ACTION_MOVE);
                        } else {
                            itemView.onTouchEvent(event);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        itemView.onTouchEvent(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!moved) {
                            itemView.onTouchEvent(event);
                        }
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void updateItemData(DocumentData data) {
        nameView.setText(data.getName());
        subjectAndOwnerView.setText(context.getResources().getString(R.string.two_strings, data.getSubject(), data.getOwner()));

        if (adapter == null) {
            itemView.setOnClickListener(null);
        } else {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
    }

    public static class Factory implements ItemHolderFactory<DocumentData> {
        @Override
        public DocumentItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<DocumentData> adapter) {
            return new DocumentItemHolder(view, adapter);
        }
    }
}
