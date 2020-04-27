package com.stypox.mastercom_workbook.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.util.DateUtils;

public class SubjectTopicItemHolder extends TopicItemHolder {
    public SubjectTopicItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<TopicData> adapter) {
        super(itemView, adapter);
    }

    @Override
    protected String getSubtitleContent(TopicData data) {
        // show subject instead of teacher
        return context.getResources().getString(R.string.two_strings,
                data.getSubject(), DateUtils.formatDate(data.getDate()));
    }

    public static class Factory implements ItemHolderFactory<TopicData> {
        @Override
        public SubjectTopicItemHolder buildItemHolder(@NonNull View view, @Nullable ItemArrayAdapter<TopicData> adapter) {
            return new SubjectTopicItemHolder(view, adapter);
        }
    }
}
