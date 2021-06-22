package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ClockListAdapter extends ArrayAdapter<clock> implements Filterable {
    private ArrayList<clock> clocks;
    private ArrayList<clock> filteredClocks;
    private Filter filter;

    public ClockListAdapter(@NonNull Context context, ArrayList<clock> c) {
        super(context, 0 , c);
        this.clocks = c;
        this.filteredClocks = c;

    }
    public clock getItem(int position){
        return filteredClocks.get(position);
    }

    public int getCount() {
        return filteredClocks.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(int position, View convertView, ViewGroup parent) {

        clock c = getItem(position);
        if(convertView == null){

            LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setBackgroundColor(Color.WHITE);


            CheckBox button = new CheckBox(getContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setId(2);
            button.setPadding(100,80,0,80);

            layout.addView(button);

            TextView text = new TextView(getContext());
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            text.setId(1);
            text.setPadding(20,80,0,80);
            text.setTextColor(Color.BLACK);
            layout.addView(text);

            TextView text2 = new TextView(getContext());
            text2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            text2.setId(3);
            text2.setPadding(200,0,0,0);
            text2.setTextColor(Color.BLACK);
            layout.addView(text2);

            convertView = layout;
       }
        CheckBox button = (CheckBox) convertView.findViewById(2);
        button.setTag(position);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                              @Override
                                              public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                  if(isChecked){
                                                      c.flag=true;

                                                  }
                                                  else
                                                      c.flag=false;

                                              }
                                          }
        );

        TextView text = (TextView) convertView.findViewById(1);
        text.setText(c.Name);

        TextView text_ = (TextView) convertView.findViewById(3);
        text_.setText(c.hours + " : "+ c.minutes + " : " + c.seconds);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ClocksFilter();
        }
        return filter;
    }
    private class ClocksFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                ArrayList<clock> filteredList = new ArrayList<clock>();
                for(int i=0; i < clocks.size(); i++){
                    if(clocks.get(i).Name.contains(constraint)){
                        filteredList.add(clocks.get(i));
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;

            }
            else{
                results.count = clocks.size();
                results.values = clocks;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredClocks = (ArrayList<clock>) results.values;
            notifyDataSetChanged();
        }

    }







    }
