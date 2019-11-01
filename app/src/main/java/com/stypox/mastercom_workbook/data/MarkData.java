package com.stypox.mastercom_workbook.data;

import android.content.Context;

import com.stypox.mastercom_workbook.R;
import com.stypox.mastercom_workbook.util.DateFormatting;
import com.stypox.mastercom_workbook.util.MarkFormatting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MarkData implements Serializable {
    private String subject;
    private final float value;
    private final MarkType type;
    private final Date date;
    private final String description;
    private final String teacher;

    public MarkData(JSONObject json) throws JSONException, InvalidKeyException, ParseException {
        subject = null;
        value = Float.parseFloat(json.getString("valore"));
        type = MarkType.parseType(json.getString("tipo"));
        date = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").parse(json.getString("data"));
        description = json.getString("note");
        teacher = json.getString("docente");
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject == null ? "" : subject;
    }
    public float getValue() {
        return value;
    }
    public MarkType getType() {
        return type;
    }
    public Date getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }
    public String getTeacher() {
        return teacher;
    }

    public int getTerm() {
        if (date.getMonth() > 6) {
            return 0; // first term
        } else {
            return 1; // second term
        }
    }
    static public int currentTerm() {
        if (Calendar.getInstance().get(Calendar.MONTH) > 6) {
            return 0; // first term
        } else {
            return 1; // second term
        }
    }
}
