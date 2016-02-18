package com.citybility.app.custom;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.citybility.app.R;

public class GreenDatePicker  extends android.widget.DatePicker {

    public GreenDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> internalRID = null;
        try {
            internalRID = Class.forName("com.android.internal.R$id");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field month = null;
        try {
            month = internalRID.getField("month");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        NumberPicker npMonth = null;
        try {
            npMonth = (NumberPicker) findViewById(month.getInt(null));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field day = null;
        try {
            day = internalRID.getField("day");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        NumberPicker npDay = null;
        try {
            npDay = (NumberPicker) findViewById(day.getInt(null));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field year = null;
        try {
            year = internalRID.getField("year");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        NumberPicker npYear = null;
        try {
            npYear = (NumberPicker) findViewById(year.getInt(null));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Class<?> numberPickerClass = null;
        try {
            numberPickerClass = Class.forName("android.widget.NumberPicker");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field selectionDivider = null;
        try {
            selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            selectionDivider.setAccessible(true);
            selectionDivider.set(npMonth, getResources().getDrawable(
                       R.drawable.numberpicker_divider_green));
            selectionDivider.set(npDay, getResources().getDrawable(
                       R.drawable.numberpicker_divider_green));
            selectionDivider.set(npYear, getResources().getDrawable(
                       R.drawable.numberpicker_divider_green));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
