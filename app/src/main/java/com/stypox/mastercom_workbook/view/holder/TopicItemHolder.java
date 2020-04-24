package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.util.DateUtils;

public class TopicItemHolder extends ItemHolder<TopicData> {
    private TextView titleView;
    private TextView descriptionView;
    private TextView assignmentView;
    private TextView teacherDateView;

    private Context context;

    public TopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        titleView = itemView.findViewById(R.id.title);
        descriptionView = itemView.findViewById(R.id.description);
        assignmentView = itemView.findViewById(R.id.assignment);
        teacherDateView = itemView.findViewById(R.id.teacherDate);
    }

    @Override
    public void updateItemData(TopicData data) {
        if (data.getTitle().isEmpty()) {
            titleView.setText(data.getDescription());
            descriptionView.setVisibility(View.GONE);
            descriptionView.setText("");
        } else {
            titleView.setText(data.getTitle());

            if (data.getDescription().isEmpty()) {
                descriptionView.setVisibility(View.GONE);
                descriptionView.setText("");
            } else {
                descriptionView.setVisibility(View.VISIBLE);
                descriptionView.setText(data.getDescription());
            }
        }

        if (data.getAssignment().isEmpty()) {
            assignmentView.setVisibility(View.GONE);
            assignmentView.setText("");
        } else {
            assignmentView.setVisibility(View.VISIBLE);
            assignmentView.setText(data.getAssignment());
        }

        teacherDateView.setText(context.getResources().getString(R.string.two_strings,
                data.getTeacher(), DateUtils.formatDate(data.getDate())));
    }

    public static class Factory implements ItemHolderFactory<TopicData> {
        @Override
        public TopicItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<TopicData> adapter) {
            return new TopicItemHolder(view, adapter);
        }
    }
}
