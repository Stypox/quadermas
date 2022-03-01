package com.stypox.mastercom_workbook.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;

public class SubjectTopicItemHolder extends TopicItemHolder {
    public SubjectTopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);
    }

    @Override
    protected String getSubtitleContent(TopicData data) {
        // show subject instead of teacher
        return context.getResources().getString(R.string.two_strings,
                data.getSubject(), SHORT_DATE_FORMAT.format(data.getDate()));
    }
}
