package com.organization.Giscle.giscle_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Variable.Trip_record_variable;

import java.util.List;

/**
 * Created by sushen.kumaron 9/28/2017.
 */

public class Trip_adapter extends BaseAdapter {
    private List<Trip_record_variable> list;
    private Context context;

    public Trip_adapter(List<Trip_record_variable> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class holder {
        TextView name;
        TextView time;
        TextView earn_point;
    }

    int count = 1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return null;
        holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trip_list_item, parent, false);
            holder = new holder();
            holder.name = (TextView) convertView.findViewById(R.id.trip_name);
            holder.time = (TextView) convertView.findViewById(R.id.trip_time);
            holder.earn_point = (TextView) convertView.findViewById(R.id.trip_earn_point);
            convertView.setTag(holder);
        }
        else {
            holder = (Trip_adapter.holder) convertView.getTag();
        }

//        holder.name.setText("Trip Log " + (count++));
        holder.name.setText("Trip Log " + (position+1));
        holder.time.setText(""+ list.get(position).getStart_time()+" - "+list.get(position).getEnd_time());
        holder.earn_point.setText("Earn Point: " + list.get(position).getPoints());
        return convertView;
    }
}
