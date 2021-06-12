package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.ShareUtils;

import static com.stypox.mastercom_workbook.util.DateUtils.DAY_AFTER_TOMORROW;
import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;
import static com.stypox.mastercom_workbook.util.DateUtils.TIME_FORMAT;

public class EventItemHolder extends ItemHolder<EventData> {

    private final View coloredBackgroundView;
    private final View coloredBarView;
    private final TextView titleView;
    private final TextView timeView;
    private final TextView teacherView;
    private final TextView descriptionView;

    public EventItemHolder(@NonNull final View itemView,
                           @Nullable final ItemArrayAdapter<EventData> adapter) {
        super(itemView, adapter);

        coloredBackgroundView = itemView.findViewById(R.id.coloredBackground);
        coloredBarView = itemView.findViewById(R.id.coloredBar);
        titleView = itemView.findViewById(R.id.title);
        timeView = itemView.findViewById(R.id.time);
        teacherView = itemView.findViewById(R.id.teacher);
        descriptionView = itemView.findViewById(R.id.description);

        final HorizontalScrollView titleScrollView = itemView.findViewById(R.id.titleScrollView);
        titleScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(final EventData data) {
        @ColorRes final int backgroundColor;
        if (DateUtils.inTheFuture(data.getEnd())) {
            if (data.getBegin().before(DAY_AFTER_TOMORROW)) {
                backgroundColor = R.color.imminentEvent;
            } else {
                backgroundColor = R.color.futureEvent;
            }
        } else {
            backgroundColor = R.color.transparent;
        }
        coloredBackgroundView.setBackgroundColor(context.getResources().getColor(backgroundColor));

        coloredBarView.setBackgroundColor(context.getResources().getColor(
                data.getType() == EventData.Type.annotation
                        ? R.color.annotationEvent : R.color.eventEvent));

        titleView.setText(data.getTitle());

        final String beginDate = SHORT_DATE_FORMAT.format(data.getBegin());
        final String beginTime = TIME_FORMAT.format(data.getBegin());
        final String endDate = SHORT_DATE_FORMAT.format(data.getEnd());
        final String endTime = TIME_FORMAT.format(data.getEnd());
        if (data.getBegin().getDate() == data.getEnd().getDate()) {
            if (data.getBegin().equals(data.getEnd())) {
                // the two date-times are equal, show only one of them
                timeView.setText(context.getString(R.string.event_time_same_minute,
                        beginDate, beginTime));
            } else {
                // same day, show day/month/year only once
                timeView.setText(context.getString(R.string.event_time_same_day,
                        beginDate, beginTime, endTime));
            }
        } else {
            // two different days, show both full date-times
            timeView.setText(context.getString(R.string.event_time_different_days,
                    beginDate, beginTime, endDate, endTime));
        }

        if (data.getTeacher().isEmpty()) {
            teacherView.setVisibility(View.GONE);
        } else {
            teacherView.setVisibility(View.VISIBLE);
            teacherView.setText(data.getTeacher());
        }

        if (data.getDescription().isEmpty()) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(data.getDescription());
        }

        itemView.setOnClickListener(v ->
                ShareUtils.addEventToCalendar(context, data.getTitle(), data.getDescription(),
                        data.getTeacher(), data.getBegin(), data.getEnd()));
    }


    private static class Factory implements ItemHolderFactory<EventData> {
        @Override
        public EventItemHolder buildItemHolder(
                @NonNull final View itemView, @Nullable final ItemArrayAdapter<EventData> adapter) {
            return new EventItemHolder(itemView, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
