package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.util.MarkFormatting;

public class MarkItemHolder extends ItemHolder<MarkData> {
    private final TextView valueView;
    private final TextView typeView;
    private final TextView dateView;

    private final Context context;


    public MarkItemHolder(View view) {
        super(view);
        context = view.getContext();

        valueView = view.findViewById(R.id.mark_value);
        typeView = view.findViewById(R.id.mark_type);
        dateView = view.findViewById(R.id.mark_date);
    }

    @Override
    public void updateItemData(MarkData data) {
        valueView.setText(data.getValueRepresentation());
        valueView.setTextColor(MarkFormatting.colorOf(context, data.getValue()));
        typeView.setText(data.getTypeRepresentation(context));
        dateView.setText(data.getDateRepresentation());
    }

    public static class Factory implements ItemHolderFactory<MarkItemHolder> {
        @Override
        public MarkItemHolder buildItemHolder(View view) {
            return new MarkItemHolder(view);
        }
    }
}
