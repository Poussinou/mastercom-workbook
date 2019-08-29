package com.stypox.mastercom_workbook.util;

import android.content.Context;

import com.stypox.mastercom_workbook.R;

public class MarkFormatting {
    public static String floatToString(float f, int maxLength) {
        int nonDecimalDigits = String.valueOf((int)f).length();
        if (nonDecimalDigits > maxLength) {
            throw new IllegalArgumentException();
        } else {
            int decimalDigits = maxLength - nonDecimalDigits;
            String string = String.format("%."+decimalDigits+"f", f+Math.pow(10, -decimalDigits-2)); // sum is to prevent floating number problems when the number ends with 5

            // remove padding zeros
            while (decimalDigits > 0) {
                if (string.endsWith("0")) {
                    string = string.substring(0, string.length()-(decimalDigits == 1 ? 2 : 1)); // also remove point
                    --decimalDigits;
                } else {
                    break;
                }
            }
            return string;
        }
    }

    public static int colorOf(Context context, float mark) {
        if (mark < 6) {
            return context.getResources().getColor(R.color.failingMark);
        } else if (mark < 8) {
            return context.getResources().getColor(R.color.halfwayMark);
        } else {
            return context.getResources().getColor(R.color.excellentMark);
        }
    }

    public static String valueRepresentation(float value) {
        float quarterPrecision = ((float)Math.round(value*4))/4; // 0.25 intervals: 0.0; 0.25; 0.5; 0.75; 1.0; ...
        int baseValue = (int)Math.floor(quarterPrecision);

        float delta = quarterPrecision-baseValue;
        if        (delta == 0.00) {
            return String.valueOf(baseValue);
        } else if (delta == 0.25) {
            return baseValue + "+";
        } else if (delta == 0.50) {
            return baseValue + "½";
        } else {//(delta == 0.75)
            return (baseValue+1) + "-";
        }
    }
}
