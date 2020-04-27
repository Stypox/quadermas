package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.util.DateUtils;

public class TopicItemHolder extends ItemHolder<TopicData> {
    private TextView titleView;
    private TextView teacherDateView;
    private TableRow descriptionTableRow;
    private TextView descriptionView;
    private TableRow assignmentTableRow;
    private TextView assignmentView;

    private Context context;

    public TopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        titleView = itemView.findViewById(R.id.title);
        teacherDateView = itemView.findViewById(R.id.teacherDate);
        descriptionTableRow = itemView.findViewById(R.id.description_table_row);
        descriptionView = itemView.findViewById(R.id.description);
        assignmentTableRow = itemView.findViewById(R.id.assignment_table_row);
        assignmentView = itemView.findViewById(R.id.assignment);
    }

    @Override
    public void updateItemData(TopicData data) {
        if (data.getTitle().isEmpty()) {
            if (data.getDescription().isEmpty()) {
                titleView.setText(data.getSubject());
            } else {
                titleView.setText(data.getDescription());
            }
            descriptionTableRow.setVisibility(View.GONE);
        } else {
            titleView.setText(data.getTitle());

            if (data.getDescription().isEmpty()) {
                descriptionTableRow.setVisibility(View.GONE);
            } else {
                descriptionView.setText(data.getDescription());
                descriptionTableRow.setVisibility(View.VISIBLE);
            }
        }

        teacherDateView.setText(context.getResources().getString(R.string.two_strings,
                data.getTeacher(), DateUtils.formatDate(data.getDate())));

        if (data.getAssignment().isEmpty()) {
            assignmentTableRow.setVisibility(View.GONE);
        } else {
            assignmentView.setText(data.getAssignment());
            assignmentTableRow.setVisibility(View.VISIBLE);
        }
    }

    public static class Factory implements ItemHolderFactory<TopicData> {
        @Override
        public TopicItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<TopicData> adapter) {
            return new TopicItemHolder(view, adapter);
        }
    }
}
