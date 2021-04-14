package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.settings.SecondTermStart;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import static com.stypox.mastercom_workbook.util.ThemedActivity.resolveColor;

public class SubjectItemHolder extends ItemHolder<SubjectData> {

    private final TextView nameView;
    private final TextView teacherTextView;
    private final TextView averageTextView;

    @SuppressLint("ClickableViewAccessibility")
    public SubjectItemHolder(@NonNull final View itemView,
                             @Nullable final ItemArrayAdapter<SubjectData> adapter) {
        super(itemView, adapter);

        nameView = itemView.findViewById(R.id.name);
        teacherTextView = itemView.findViewById(R.id.teacher);
        averageTextView = itemView.findViewById(R.id.average);

        HorizontalScrollView nameScrollView = itemView.findViewById(R.id.nameScrollView);
        nameScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(final SubjectData data) {
        if (adapter != null) {
            itemView.setOnClickListener(v -> adapter.onItemClick(data));
        }
        nameView.setText(data.getName());

        averageTextView.setAlpha(1.0f); // set to 1.0, changed only if average is from another term
        averageTextView.setTextColor(resolveColor(context, R.attr.color_mark_not_classified));

        if (data.getMarks() == null) {
            if (data.getMarkExtractionError() == null) {
                // marks not yet extracted
                teacherTextView.setText("");
                averageTextView.setText("");
            } else {
                // error while extracting marks
                teacherTextView.setText(data.getMarkExtractionError().getMessage(context));
                averageTextView.setText("X");
            }

        } else if (data.getMarks().isEmpty()) {
            teacherTextView.setText(context.getString(R.string.error_no_marks));
            averageTextView.setText("?");

        } else {
            teacherTextView.setText(data.getTeacher());

            final SecondTermStart secondTermStart = SecondTermStart.fromPreferences(context);
            final int currentTerm = secondTermStart.currentTerm();

            float average = -1;
            boolean averageIsFromCurrentTerm = true;
            try {
                average = data.getAverage(secondTermStart, currentTerm); // average of current term

            } catch (final ArithmeticException e) {
                // current term has no marks
                try {
                    // if currentTerm is the second term, then currentTerm-1 is the first
                    average = data.getAverage(secondTermStart, currentTerm - 1);
                    averageIsFromCurrentTerm = false;
                } catch (final ArithmeticException e1) {
                    // handled below
                }
            }

            if (average < 0) {
                averageTextView.setText("-"); // should not be reachable
            } else {
                averageTextView.setText(MarkFormatting.floatToString(average, 2));
                averageTextView.setTextColor(MarkFormatting.colorOf(context, average));
                if (!averageIsFromCurrentTerm) {
                    averageTextView.setAlpha(0.5f);
                    teacherTextView.setText(R.string.error_no_marks_in_current_term);
                }
            }
        }
    }


    private static class Factory implements ItemHolderFactory<SubjectData> {
        @Override
        public SubjectItemHolder buildItemHolder(
                @NonNull final View itemView,
                @Nullable final ItemArrayAdapter<SubjectData> adapter) {
            return new SubjectItemHolder(itemView, adapter);
        }
    }

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }
}
