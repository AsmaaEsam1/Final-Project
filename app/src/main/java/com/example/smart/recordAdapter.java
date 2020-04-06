package com.example.smart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class recordAdapter extends ArrayAdapter<records> {
    public recordAdapter(@NonNull Context context, ArrayList<records> records) {
        super(context, 0,records);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.record_items,parent,false);
        }
        records recordCurrent = getItem(position);

        TextView textUser = view.findViewById(R.id.record_name);
        textUser.setText(recordCurrent.getRecordName());

        TextView textUserType = view.findViewById(R.id.record_path);
        textUserType.setText(recordCurrent.getRecordPath());
    return view;
    }
}
