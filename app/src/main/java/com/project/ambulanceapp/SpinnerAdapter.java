package com.project.ambulanceapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.ambulanceapp.driver.Driver;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Driver> {

    private List<Driver> items;
    private Context ctx;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Driver> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.ctx = context;
    }

//    public SpinnerAdapter(Context context, List<Driver> items) {
//        this.items = items;
//        this.ctx = context;
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(ctx.getResources().getColor(R.color.grey_90));
        String fname = items.get(position).getFname() + " " + items.get(position).getLname();
        label.setText(PromptDialog.toCamelCase(fname));

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(ctx.getResources().getColor(R.color.grey_90));
        String fname = items.get(position).getFname() + " " + items.get(position).getLname();
        label.setText(PromptDialog.toCamelCase(fname));

        return label;
    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public Driver getItem(int position){
        return items.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


}
