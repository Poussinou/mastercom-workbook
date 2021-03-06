package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.util.DateUtils;
import com.stypox.mastercom_workbook.util.HorizontalScrollViewTouchListener;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class SubjectItemHolder extends ItemHolder<SubjectData> {
    private TextView nameView;
    private TextView teacherTextView;
    private TextView averageTextView;

    private Context context;


    @SuppressLint("ClickableViewAccessibility")
    public SubjectItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<SubjectData> adapter) {
        super(itemView, adapter);
        context = itemView.getContext();

        nameView = itemView.findViewById(R.id.name);
        teacherTextView = itemView.findViewById(R.id.teacher);
        averageTextView = itemView.findViewById(R.id.average);

        HorizontalScrollView nameScrollView = itemView.findViewById(R.id.nameScrollView);
        nameScrollView.setOnTouchListener(new HorizontalScrollViewTouchListener(itemView));
    }

    @Override
    public void updateItemData(SubjectData data) {
        nameView.setText(data.getName());
        if (data.getMarks() == null) {
            teacherTextView.setText(data.getError().getMessage(context));
            averageTextView.setText("X");
            averageTextView.setTextColor(context.getResources().getColor(R.color.notClassifiedMark));
            itemView.setOnClickListener(null);

        } else if (data.getMarks().isEmpty()) {
            teacherTextView.setText(context.getString(R.string.error_no_marks));
            averageTextView.setText("?");
            averageTextView.setTextColor(context.getResources().getColor(R.color.notClassifiedMark));
            itemView.setOnClickListener(null);

        } else {
            teacherTextView.setText(data.getTeacher());

            try {
                float average = data.getAverage(DateUtils.getTerm(data.getMarks().get(0).getDate())); // current average
                averageTextView.setText(MarkFormatting.floatToString(average, 2));
                averageTextView.setTextColor(MarkFormatting.colorOf(context, average));
            } catch (ArithmeticException e) {
                averageTextView.setText("-");
                averageTextView.setTextColor(context.getResources().getColor(R.color.notClassifiedMark));
            }

            if (adapter != null) {
                itemView.setOnClickListener(v -> adapter.onItemClick(data));
            }
        }
    }

    public static class Factory implements ItemHolderFactory<SubjectData> {
        @Override
        public SubjectItemHolder buildItemHolder(@NonNull View itemView, @Nullable ItemArrayAdapter<SubjectData> adapter) {
            return new SubjectItemHolder(itemView, adapter);
        }
    }
}
