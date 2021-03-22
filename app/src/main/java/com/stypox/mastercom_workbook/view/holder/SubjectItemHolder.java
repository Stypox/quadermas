package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import static com.stypox.mastercom_workbook.util.ThemedActivity.resolveColor;

public class SubjectItemHolder extends ItemHolder<SubjectData> {

    private final TextView nameView;
    private final TextView teacherTextView;
    private final TextView averageTextView;

    @SuppressLint("ClickableViewAccessibility")
    public SubjectItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<SubjectData> adapter) {
        super(itemView, adapter);

        nameView = itemView.findViewById(R.id.name);
        teacherTextView = itemView.findViewById(R.id.teacher);
        averageTextView = itemView.findViewById(R.id.average);

        HorizontalScrollView nameScrollView = itemView.findViewById(R.id.nameScrollView);
        nameScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(SubjectData data) {
        if (adapter != null) {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
        nameView.setText(data.getName());

        if (data.getMarks() == null) {
            if (data.getMarkExtractionError() == null) {
                // marks not yet extracted
                teacherTextView.setText("");
                averageTextView.setText("");
            } else {
                // error while extracting marks
                teacherTextView.setText(data.getMarkExtractionError().getMessage(context));
                averageTextView.setText("X");
                averageTextView.setTextColor(resolveColor(context, R.attr.color_mark_not_classified));
            }

        } else if (data.getMarks().isEmpty()) {
            teacherTextView.setText(context.getString(R.string.error_no_marks));
            averageTextView.setText("?");
            averageTextView.setTextColor(resolveColor(context, R.attr.color_mark_not_classified));

        } else {
            teacherTextView.setText(data.getTeacher());

            try {
                float average = data.getAverage(DateUtils.getTerm(data.getMarks().get(0).getDate())); // current average
                averageTextView.setText(MarkFormatting.floatToString(average, 2));
                averageTextView.setTextColor(MarkFormatting.colorOf(context, average));
            } catch (ArithmeticException e) {
                averageTextView.setText("-");
                averageTextView.setTextColor(resolveColor(context, R.attr.color_mark_not_classified));
            }
        }
    }


    private static class Factory implements ItemHolderFactory<SubjectData> {
        @Override
        public SubjectItemHolder buildItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<SubjectData> adapter) {
            return new SubjectItemHolder(itemView, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
