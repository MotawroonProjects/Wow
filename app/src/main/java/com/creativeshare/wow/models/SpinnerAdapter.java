package com.creativeshare.wow.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativeshare.wow.R;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SpinnerAdapter extends BaseAdapter {

    private List<Department_Model> department_modelList;
    private LayoutInflater inflater;
    private String current_language;

    public SpinnerAdapter(Context context,List<Department_Model> department_modelList) {
        this.department_modelList = department_modelList;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public int getCount() {
        return department_modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return department_modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
        {
            convertView = inflater.inflate(R.layout.spinner_row,parent,false);
        }
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        if (current_language.equals("ar"))
        {
            tv_name.setText(department_modelList.get(position).getAr_title_dep());
        }else
        {
            tv_name.setText(department_modelList.get(position).getEn_title_dep());
        }
        return convertView;
    }


}
