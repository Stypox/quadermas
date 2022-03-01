package com.stypox.mastercom_workbook.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;

public class TopicItemHolder extends ItemHolder<TopicData> {
    private static final int MAX_TITLE_LENGTH = 40; // characters

    private final TextView titleView;
    private final TextView subtitleView;
    private final View descriptionIconView;
    private final TextView descriptionView;
    private final View assignmentIconRow;
    private final TextView assignmentView;

    public TopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);

        titleView = itemView.findViewById(R.id.title);
        subtitleView = itemView.findViewById(R.id.subtitle);
        descriptionIconView = itemView.findViewById(R.id.descriptionIcon);
        descriptionView = itemView.findViewById(R.id.description);
        assignmentIconRow = itemView.findViewById(R.id.assignmentIcon);
        assignmentView = itemView.findViewById(R.id.assignment);
    }

    @Override
    final public void updateItemData(TopicData data) {
        final int descriptionVisibility;
        if (data.getTitle().isEmpty()) {
            if (data.getDescription().isEmpty()) {
                titleView.setText(data.getSubject());
                descriptionVisibility = View.GONE;
            } else if (data.getDescription().length() > MAX_TITLE_LENGTH) {
                titleView.setText(data.getSubject());
                descriptionView.setText(data.getDescription());
                descriptionVisibility = View.VISIBLE;
            } else {
                titleView.setText(data.getDescription());
                descriptionVisibility = View.GONE;
            }

        } else {
            if (data.getDescription().isEmpty()) {
                if (data.getTitle().length() > MAX_TITLE_LENGTH) {
                    titleView.setText(data.getSubject());
                    descriptionView.setText(data.getTitle());
                    descriptionVisibility = View.VISIBLE;
                } else {
                    titleView.setText(data.getTitle());
                    descriptionVisibility = View.GONE;
                }
            } else {
                titleView.setText(data.getTitle());
                descriptionView.setText(data.getDescription());
                descriptionVisibility = View.VISIBLE;
            }
        }
        descriptionIconView.setVisibility(descriptionVisibility);
        descriptionView.setVisibility(descriptionVisibility);

        subtitleView.setText(getSubtitleContent(data));

        final int assignmentVisibility;
        if (data.getAssignment().isEmpty()) {
            assignmentVisibility = View.GONE;
        } else {
            assignmentView.setText(data.getAssignment());
            assignmentVisibility = View.VISIBLE;
        }
        assignmentIconRow.setVisibility(assignmentVisibility);
        assignmentView.setVisibility(assignmentVisibility);
    }

    // overridden in SubjectTopicItemHolder to return subject instead of teacher
    protected String getSubtitleContent(TopicData data) {
        return context.getResources().getString(R.string.two_strings,
                data.getTeacher(), SHORT_DATE_FORMAT.format(data.getDate()));
    }
}
