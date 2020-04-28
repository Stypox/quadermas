package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.util.DateUtils;

public class TopicItemHolder extends ItemHolder<TopicData> {
    private static final int MAX_TITLE_LENGTH = 40; // characters

    private TextView titleView;
    private TextView subtitleView;
    private TableRow descriptionTableRow;
    private TextView descriptionView;
    private TableRow assignmentTableRow;
    private TextView assignmentView;

    protected Context context;

    public TopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        titleView = itemView.findViewById(R.id.title);
        subtitleView = itemView.findViewById(R.id.subtitle);
        descriptionTableRow = itemView.findViewById(R.id.description_table_row);
        descriptionView = itemView.findViewById(R.id.description);
        assignmentTableRow = itemView.findViewById(R.id.assignment_table_row);
        assignmentView = itemView.findViewById(R.id.assignment);
    }

    @Override
    final public void updateItemData(TopicData data) {
        if (data.getTitle().isEmpty()) {
            if (data.getDescription().isEmpty()) {
                titleView.setText(data.getSubject());
                descriptionTableRow.setVisibility(View.GONE);
            } else if (data.getDescription().length() > MAX_TITLE_LENGTH) {
                titleView.setText(data.getSubject());
                descriptionView.setText(data.getDescription());
                descriptionTableRow.setVisibility(View.VISIBLE);
            } else {
                titleView.setText(data.getDescription());
                descriptionTableRow.setVisibility(View.GONE);
            }

        } else {
            if (data.getDescription().isEmpty()) {
                if (data.getTitle().length() > MAX_TITLE_LENGTH) {
                    titleView.setText(data.getSubject());
                    descriptionView.setText(data.getTitle());
                    descriptionTableRow.setVisibility(View.VISIBLE);
                } else {
                    titleView.setText(data.getTitle());
                    descriptionTableRow.setVisibility(View.GONE);
                }
            } else {
                titleView.setText(data.getTitle());
                descriptionView.setText(data.getDescription());
                descriptionTableRow.setVisibility(View.VISIBLE);
            }
        }

        subtitleView.setText(getSubtitleContent(data));

        if (data.getAssignment().isEmpty()) {
            assignmentTableRow.setVisibility(View.GONE);
        } else {
            assignmentView.setText(data.getAssignment());
            assignmentTableRow.setVisibility(View.VISIBLE);
        }
    }

    // overridden in SubjectTopicItemHolder to return subject instead of teacher
    protected String getSubtitleContent(TopicData data) {
        return context.getResources().getString(R.string.two_strings,
                data.getTeacher(), DateUtils.formatDate(data.getDate()));
    }


    private static class Factory implements ItemHolderFactory<TopicData> {
        @Override
        public TopicItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<TopicData> adapter) {
            return new TopicItemHolder(view, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static ItemHolderFactory<TopicData> getFactory() {
        return factory;
    }
}
