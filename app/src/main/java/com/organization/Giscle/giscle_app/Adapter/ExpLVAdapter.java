package com.organization.Giscle.giscle_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.organization.Giscle.giscle_app.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sushen.kumaron 10/13/2017.
 */

public class ExpLVAdapter extends BaseExpandableListAdapter {
    private Map<String, String> mapChild;
    private Context context;
    private ArrayList<String> listCategories;

    public ExpLVAdapter(Map<String, String> mapChild, Context context, ArrayList<String> questions) {
        this.mapChild = mapChild;
        this.context = context;
        this.listCategories = questions;
    }

    @Override
    public int getGroupCount() {
        return listCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapChild.get(listCategories.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String titleCategory = (String) getGroup(groupPosition);

        convertView = LayoutInflater.from(context).inflate(R.layout.elv_group,null);
        TextView textView = (TextView)convertView.findViewById(R.id.elvGroup);

        textView.setText(titleCategory);
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String item = (String) getChild(groupPosition,childPosition);
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_child,null);
        TextView textView =(TextView)convertView.findViewById(R.id.tvChild);
        textView.setText(item);
        return convertView;
     }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
