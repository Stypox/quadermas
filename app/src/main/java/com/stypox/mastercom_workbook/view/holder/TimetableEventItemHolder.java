package com.stypox.mastercom_workbook.view.holder;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.data.TimetableEventData;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.ShareUtils;

public class TimetableEventItemHolder extends ItemHolder<TimetableEventData> {

    private final TextView subjectView;
    private final TextView teacherView;
    private final TextView beginTimeView;
    private final TextView endTimeView;

    public TimetableEventItemHolder(@NonNull final View itemView,
                                    @Nullable final ItemArrayAdapter<TimetableEventData> adapter) {
        super(itemView, adapter);

        subjectView = itemView.findViewById(R.id.subject);
        teacherView = itemView.findViewById(R.id.teacher);
        beginTimeView = itemView.findViewById(R.id.beginTime);
        endTimeView = itemView.findViewById(R.id.endTime);

        final HorizontalScrollView subjectScrollView
                = itemView.findViewById(R.id.subjectScrollView);
        subjectScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(final TimetableEventData data) {
        subjectView.setText(data.getSubject());
        teacherView.setText(data.getTeacher());

        final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        beginTimeView.setText(timeFormat.format(data.getBegin()));
        endTimeView.setText(timeFormat.format(data.getEnd()));

        itemView.setOnClickListener(v -> {
            ShareUtils.addEventToCalendar(context, data.getSubject(), null, data.getTeacher(),
                    data.getBegin(), data.getEnd());
        });
    }

    private static class Factory implements ItemHolderFactory<TimetableEventData> {
        @Override
        public TimetableEventItemHolder buildItemHolder(
                @NonNull final View view,
                @Nullable final ItemArrayAdapter<TimetableEventData> adapter) {
            return new TimetableEventItemHolder(view, adapter);
        }
    }

    private static final TimetableEventItemHolder.Factory factory
            = new TimetableEventItemHolder.Factory();

    public static TimetableEventItemHolder.Factory getFactory() {
        return factory;
    }
}
