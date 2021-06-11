package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.util.DateUtils;

import java.text.DateFormat;

public class EventItemHolder extends ItemHolder<EventData> {

    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    private final View coloredBarView;
    private final TextView titleView;
    private final TextView timeView;
    private final TextView teacherView;
    private final TextView descriptionView;

    public EventItemHolder(@NonNull final View itemView,
                           @Nullable final ItemArrayAdapter<EventData> adapter) {
        super(itemView, adapter);

        coloredBarView = itemView.findViewById(R.id.coloredBar);
        titleView = itemView.findViewById(R.id.title);
        timeView = itemView.findViewById(R.id.time);
        teacherView = itemView.findViewById(R.id.teacher);
        descriptionView = itemView.findViewById(R.id.description);
    }

    @Override
    public void updateItemData(final EventData data) {
        coloredBarView.setBackgroundColor(context.getResources().getColor(
                data.getType() == EventData.Type.annotation
                        ? R.color.annotationEvent : R.color.eventEvent));

        titleView.setText(data.getTitle());

        final String beginDate = dateFormat.format(data.getBegin());
        final String beginTime = timeFormat.format(data.getBegin());
        final String endDate = dateFormat.format(data.getEnd());
        final String endTime = timeFormat.format(data.getEnd());
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

        itemView.setBackgroundColor(context.getResources().getColor(
                DateUtils.inTheFuture(data.getEnd()) ? R.color.futureEvent : R.color.transparent));
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
