package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class MainActivityAdapter extends ArrayAdapter<clock> {

    private ArrayList<clock> selected_clocks;

    public MainActivityAdapter(@NonNull Context context, ArrayList<clock> c) {
        super(context, 0, c);
        this.selected_clocks = c;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(int position, View convertView, ViewGroup parent) {

        clock c = getItem(position);
        if(convertView == null){

            LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setBackgroundColor(Color.WHITE);


            TextView text = new TextView(getContext());
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            text.setId(1);
            text.setPadding(20,80,0,80);
            text.setTextColor(Color.BLACK);

            layout.addView(text);

            TextView text2 = new TextView(getContext());
            text2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            text2.setId(3);
            text2.setPadding(300,0,0,0);
            text2.setTextColor(Color.BLACK);
            layout.addView(text2);

            convertView = layout;
        }

        TextView text = (TextView) convertView.findViewById(1);
        text.setText(c.Name + System.getProperty("line.separator") + c.Day + "," + c.difference + " " + c.temp);


        TextView text_ = (TextView) convertView.findViewById(3);
        text_.setText(c.hours + " : "+ c.minutes +" : "+ c.seconds);


        return convertView;

    }
}

