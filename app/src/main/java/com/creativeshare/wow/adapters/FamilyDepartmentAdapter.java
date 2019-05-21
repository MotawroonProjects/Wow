package com.creativeshare.wow.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home.FragmentFamilyDepartments;
import com.creativeshare.wow.models.Department_Model;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class FamilyDepartmentAdapter extends RecyclerView.Adapter<FamilyDepartmentAdapter.MyHolder> {

    private Context context;
    private List<Department_Model> department_modelList;
    private FragmentFamilyDepartments fragmentFamilyDepartments;
    private String current_language;
    private SparseBooleanArray sparseBooleanArray;

    public FamilyDepartmentAdapter(Context context, List<Department_Model> department_modelList,FragmentFamilyDepartments fragmentFamilyDepartments) {
        this.context = context;
        this.department_modelList = department_modelList;
        this.fragmentFamilyDepartments = fragmentFamilyDepartments;
        Paper.init(context);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        sparseBooleanArray = new SparseBooleanArray();
        sparseBooleanArray.put(0,true);

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.department_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        Department_Model department_model = department_modelList.get(position);
        if (sparseBooleanArray.get(position))
        {
            holder.tv_dept_name.setBackgroundResource(R.drawable.selected_dept);
        }else
            {
                holder.tv_dept_name.setBackgroundResource(R.drawable.unselected_dept);

            }
        holder.BindData(department_model);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Department_Model department_model = department_modelList.get(holder.getAdapterPosition());
                sparseBooleanArray.clear();
                sparseBooleanArray.put(holder.getAdapterPosition(),true);
                holder.tv_dept_name.setBackgroundResource(R.drawable.selected_dept);
                fragmentFamilyDepartments.setItemData(department_model,holder.getAdapterPosition());
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return department_modelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_dept_name;
        public MyHolder(View itemView) {
            super(itemView);
            tv_dept_name = itemView.findViewById(R.id.tv_dept_name);
        }

        public void BindData(Department_Model department_model)
        {
            if (current_language.equals("ar"))
            {
                tv_dept_name.setText(department_model.getAr_title_dep());
            }else
                {
                    tv_dept_name.setText(department_model.getEn_title_dep());

                }
        }
    }
}
