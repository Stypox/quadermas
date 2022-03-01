package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TimetableEventData;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.ShareUtils;

import static com.stypox.mastercom_workbook.util.DateUtils.TIME_FORMAT;

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

        beginTimeView.setText(TIME_FORMAT.format(data.getBegin()));
        endTimeView.setText(TIME_FORMAT.format(data.getEnd()));

        itemView.setOnClickListener(v ->
                ShareUtils.addEventToCalendar(context, data.getSubject(), null,
                        data.getTeacher(), data.getBegin(), data.getEnd()));
    }
}
