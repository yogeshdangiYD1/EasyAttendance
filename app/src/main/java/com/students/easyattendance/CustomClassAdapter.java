package com.students.easyattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomClassAdapter extends ArrayAdapter<ClassData> {

    public CustomClassAdapter(Context context, List<ClassData> classDataList) {
        super(context, R.layout.list_item_class_square, classDataList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ClassData classData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_class_square, parent, false);
        }

        TextView classTitleTextView = convertView.findViewById(R.id.classTitleTextView);
        classTitleTextView.setText(classData.getClassName());

        return convertView;
    }

    public ClassData getClassData(int position) {
        return getItem(position);
    }
}

