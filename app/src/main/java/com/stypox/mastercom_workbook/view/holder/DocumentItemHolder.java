package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;

import static com.stypox.mastercom_workbook.util.DateUtils.SHORT_DATE_FORMAT;
import static com.stypox.mastercom_workbook.util.StringUtils.isBlank;

public class DocumentItemHolder extends ItemHolder<DocumentData> {

    private final TextView nameView;
    private final TextView dateSubjectOwnerView;
    private final ProgressBar downloadProgress;

    @SuppressLint("ClickableViewAccessibility")
    public DocumentItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<DocumentData> adapter) {
        super(itemView, adapter);

        nameView = itemView.findViewById(R.id.name);
        dateSubjectOwnerView = itemView.findViewById(R.id.dateSubjectOwner);
        downloadProgress = itemView.findViewById(R.id.downloadProgress);

        HorizontalScrollView nameScrollView = itemView.findViewById(R.id.nameScrollView);
        HorizontalScrollView dateSubjectOwnerScrollView = itemView.findViewById(R.id.dateSubjectOwnerScrollView);
        nameScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
        dateSubjectOwnerScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(DocumentData data) {
        nameView.setText(data.getName());
        if (isBlank(data.getSubject())) {
            dateSubjectOwnerView.setText(context.getResources().getString(R.string.two_strings,
                    SHORT_DATE_FORMAT.format(data.getDate()), data.getOwner()));
        } else {
            dateSubjectOwnerView.setText(context.getResources().getString(R.string.three_strings,
                    SHORT_DATE_FORMAT.format(data.getDate()), data.getSubject(), data.getOwner()));
        }

        if (adapter == null) {
            itemView.setOnClickListener(null);
        } else {
            itemView.setOnClickListener(v -> {
                if (data.getDownloadProgress() >= 1.0f) {
                    // no download is in progress
                    adapter.onItemClick(data);
                }
            });
        }

        if (data.getDownloadProgress() <= 0.0f) {
            downloadProgress.setVisibility(View.VISIBLE);
            downloadProgress.setIndeterminate(true);
        } else if (data.getDownloadProgress() < 1.0f) {
            downloadProgress.setVisibility(View.VISIBLE);
            downloadProgress.setIndeterminate(false);
            downloadProgress.setProgress((int)(data.getDownloadProgress() * 1000));
        } else {
            downloadProgress.setVisibility(View.GONE);
        }
    }
}
