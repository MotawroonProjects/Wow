package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.adapters.FamilyDepartmentAdapter;
import com.creative_share_apps.wow.adapters.FamilyOwnProductAdapter;
import com.creative_share_apps.wow.models.DepartmentDataModel;
import com.creative_share_apps.wow.models.Department_Model;
import com.creative_share_apps.wow.models.ProductsDataModel;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.remote.Api;
import com.creative_share_apps.wow.share.Common;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.creative_share_apps.wow.tags.Tags;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Family_Own_Products extends Fragment {

    private ClientHomeActivity activity;
    private RecyclerView recView,recViewDept;
    private LinearLayoutManager manager,manager_dept;
    private TextView tv_no_products;
    private ProgressBar progBar,progBarDept;
    private FloatingActionButton fab;
    private UserSingleTone userSingleTone;
    private List<Department_Model> department_modelList;
    private FamilyDepartmentAdapter familyDepartmentAdapter;
    private List<ProductsDataModel.ProductModel> productModelList;
    private FamilyOwnProductAdapter familyOwnProductAdapter;
    private String current_dept_id;
    private int current_page=1;
    private int item_pos;
    private UserModel userModel;

    public static Fragment_Family_Own_Products newInstance()
    {
        return new Fragment_Family_Own_Products();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_own_products,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        department_modelList = new ArrayList<>();
        productModelList = new ArrayList<>();

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        progBarDept = view.findViewById(R.id.progBarDept);
        progBarDept.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        tv_no_products = view.findViewById(R.id.tv_no_products);
        fab = view.findViewById(R.id.fab);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        familyOwnProductAdapter = new FamilyOwnProductAdapter(productModelList,activity,this);
        recView.setAdapter(familyOwnProductAdapter);

        recViewDept = view.findViewById(R.id.recViewDept);
        manager_dept = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
        recViewDept.setLayoutManager(manager_dept);
        familyDepartmentAdapter = new FamilyDepartmentAdapter(activity,department_modelList,this);
        recViewDept.setAdapter(familyDepartmentAdapter);


        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompleteVisibleItem = manager.findLastCompletelyVisibleItemPosition();

                if (dy>0&&productModelList.size()>10&&lastCompleteVisibleItem == (productModelList.size()-6))
                {
                    productModelList.add(null);
                    familyOwnProductAdapter.notifyItemInserted(productModelList.size()-1);
                    int next_page =  current_page+1;
                    loadMore(current_dept_id,next_page);
                }
            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentFamilyAddProduct();
            }
        });

        getDepartments();

    }

    private void getDepartments()
    {
        Api.getService(Tags.base_url)
                .getAllFamilyDepts(userModel.getData().getUser_id())
                .enqueue(new Callback<DepartmentDataModel>() {
                    @Override
                    public void onResponse(Call<DepartmentDataModel> call, Response<DepartmentDataModel> response) {
                        progBarDept.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                if (response.body().getData().size()>0)
                                {
                                    tv_no_products.setVisibility(View.GONE);
                                    department_modelList.addAll(response.body().getData());
                                    familyDepartmentAdapter.notifyDataSetChanged();
                                    getProductByDeptId(response.body().getData().get(0).getId_department());
                                }else
                                    {
                                        progBar.setVisibility(View.GONE);
                                        tv_no_products.setVisibility(View.VISIBLE);

                                    }

                            }
                        }else
                        {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DepartmentDataModel> call, Throwable t) {
                        try {
                            progBarDept.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });


    }

    private void getProductByDeptId(String id_department)
    {

        progBar.setVisibility(View.VISIBLE);
        tv_no_products.setVisibility(View.GONE);
        productModelList.clear();

        Api.getService(Tags.base_url)
                .getProductsByDeptId(id_department,1)
                .enqueue(new Callback<ProductsDataModel>() {
                    @Override
                    public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                productModelList.addAll(response.body().getData());
                                familyOwnProductAdapter.notifyDataSetChanged();
                                if (productModelList.size()>0)
                                {
                                    tv_no_products.setVisibility(View.GONE);

                                }else
                                {
                                    tv_no_products.setVisibility(View.VISIBLE);

                                }
                            }
                        }else
                        {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void loadMore(String department_id, int next_page)
    {

        Api.getService(Tags.base_url)
                .getProductsByDeptId(department_id,next_page)
                .enqueue(new Callback<ProductsDataModel>() {
                    @Override
                    public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                        int last_pos = productModelList.size()-1;
                        productModelList.remove(last_pos);
                        familyDepartmentAdapter.notifyItemRemoved(last_pos);

                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                if (response.body().getData().size()>0)
                                {
                                    int last_index = productModelList.size()-1;

                                    productModelList.addAll(response.body().getData());
                                    familyDepartmentAdapter.notifyItemRangeInserted(last_index,productModelList.size());

                                }


                            }
                        }else
                        {
                            try {

                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                        try {
                            int last_pos = productModelList.size()-1;
                            productModelList.remove(last_pos);
                            familyDepartmentAdapter.notifyItemRemoved(last_pos);
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void setItemDataModelDepartment(Department_Model department_model, int adapterPosition) {
        this.current_dept_id = department_model.getId_department();
        getProductByDeptId(department_model.getId_department());
    }



    public void setItemData(ProductsDataModel.ProductModel productModel, int adapterPosition, String type) {
        this.item_pos = adapterPosition;
        if (type.equals("update"))
        {
            activity.DisplayFragmentFamilyUpdateOwnProduct(productModel);
        }else if (type.equals("delete"))
        {
            createDeleteAlertDialog(productModel);
        }
    }

    public void Update(ProductsDataModel.ProductModel updated_productModel)
    {
        productModelList.set(item_pos,updated_productModel);
        familyOwnProductAdapter.notifyItemChanged(item_pos,updated_productModel);

    }

    private void Delete(ProductsDataModel.ProductModel productModel)
    {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteProduct(productModel.getId_product(),userModel.getData().getUser_id())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            productModelList.remove(item_pos);
                            familyOwnProductAdapter.notifyItemRemoved(item_pos);
                            new Handler()
                                    .postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    },2000);
                        } else {
                            dialog.dismiss();

                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });


    }

    private void createDeleteAlertDialog(final ProductsDataModel.ProductModel productModel)
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .create();

        dialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_product,null);
        Button btn_delete = view.findViewById(R.id.btn_delete);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Delete(productModel);
            }
        });

        dialog.setView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.show();
    }


}
