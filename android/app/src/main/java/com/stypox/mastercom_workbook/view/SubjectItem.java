package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;

import java.util.ArrayList;

public class SubjectItem extends ConstraintLayout implements View.OnClickListener {
    private SubjectData data;

    private TextView teacherView;

    public SubjectItem(Context context, SubjectData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.subject_item, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView nameView = findViewById(R.id.name);
        teacherView = findViewById(R.id.teacher);

        nameView.setText(data.getName());
    }

    public void onMarksLoaded(ArrayList<MarkData> marks) {
        if (marks.isEmpty()) {
            this.teacherView.setText(getContext().getString(R.string.no_marks));
        } else {
            this.teacherView.setText(data.getTeacher());
            setOnClickListener(this);
        }
    }
    public void onMarksLoadingError(String error) {
        this.teacherView.setText(error);
    }

    @Override
    public void onClick(View v) {
        Intent openSubjectActivity = new Intent(getContext(), SubjectActivity.class);
        openSubjectActivity.putExtra(SubjectActivity.subjectDataIntentKey, data);
        getContext().startActivity(openSubjectActivity);
    }
}
