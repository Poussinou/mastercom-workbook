package com.stypox.mastercom_workbook.view;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.view.MarkItem;

public class SubjectActivity extends AppCompatActivity {
    public static final String subjectDataIntentKey = "subject_data";

    private SubjectData data;

    private LinearLayout marksLayout;
    private Spinner termSpinner;
    private TextView averageTextView;
    private EditText aimMarkEdit;
    private EditText remainingTestsEdit;
    private TextView neededMarkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = (SubjectData) getIntent().getSerializableExtra(subjectDataIntentKey);
        if (data.getMarks().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SubjectActivity with 0 marks");
        }

        marksLayout = findViewById(R.id.marksLayout);
        termSpinner = findViewById(R.id.termSpinner);
        averageTextView = findViewById(R.id.averageTextView);
        aimMarkEdit = findViewById(R.id.aimMarkEdit);
        remainingTestsEdit = findViewById(R.id.remainingTestsEdit);
        neededMarkTextView = findViewById(R.id.neededMarkTextView);

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    updateAverage();
                } catch (ArithmeticException e) {
                    // change selection; should be safe since data is guaranteed to have at least one mark
                    if (position == 0) {
                        termSpinner.setSelection(1, true);
                    } else /* position == 1 */ {
                        termSpinner.setSelection(0, true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                termSpinner.setSelection(0, true);
            }
        });
        termSpinner.setSelection(data.getMarks().get(0).getTerm(), false);

        aimMarkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateNeededMark();
            }
        });
        remainingTestsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && Integer.valueOf(s.toString()) <= 0) {
                    s.clear();
                }
                updateNeededMark();
            }
        });
        updateNeededMark();

        showInfo();
        showMarks();
    }

    private void showInfo() {
        ((TextView)findViewById(R.id.subject_name)).setText(data.getName());
        ((TextView)findViewById(R.id.teacher)).setText(data.getTeacher());
    }
    private void showMarks() {
        marksLayout.removeAllViews();
        for (MarkData mark : data.getMarks()) {
            marksLayout.addView(new MarkItem(getApplicationContext(), mark));
        }
    }

    private String floatToString(float f) {
        if (f > 1000) {
            throw new IllegalArgumentException();
        }

        String str = String.valueOf(f);
        str = str.substring(0, Math.min(4, str.length()));
        if (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    private void updateAverage() throws ArithmeticException {
        try {
            float average = data.getAverage(termSpinner.getSelectedItemPosition());
            averageTextView.setText(floatToString(average));
        } catch (Throwable e) {
            averageTextView.setText("");
        }
    }

    private void updateNeededMark() {
        try {
            float neededMark = data.getNeededMark(Float.valueOf(aimMarkEdit.getText().toString()), Integer.valueOf(remainingTestsEdit.getText().toString()));
            neededMarkTextView.setText(floatToString(neededMark));
        } catch (Throwable e) {
            neededMarkTextView.setText("");
        }
    }
}
