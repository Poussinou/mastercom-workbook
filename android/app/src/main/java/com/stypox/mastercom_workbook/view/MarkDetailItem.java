package com.stypox.mastercom_workbook.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class MarkDetailItem extends ConstraintLayout {
    private MarkData data;

    public MarkDetailItem(Context context, MarkData data) {
        super(context);
        this.data = data;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (data.getDescription().isEmpty()) {
            inflater.inflate(R.layout.mark_detail_item_no_desc, this);
        }
        else {
            inflater.inflate(R.layout.mark_detail_item, this);
        }
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView mark_value = findViewById(R.id.mark_value);
        mark_value.setText(data.getValueRepresentation());
        mark_value.setTextColor(MarkFormatting.colorOf(getContext(), data.getValue()));

        ((TextView)findViewById(R.id.mark_type)).setText(data.getTypeRepresentation(getContext()));
        ((TextView)findViewById(R.id.mark_subject)).setText(data.getSubject());
        if(!data.getDescription().isEmpty()) {
            ((TextView)findViewById(R.id.mark_description)).setText(data.getDescription());
        }
        ((TextView)findViewById(R.id.mark_teacher_date)).setText(String.format("%s  -  %s", data.getTeacher(), data.getDateRepresentation()));
    }
}
