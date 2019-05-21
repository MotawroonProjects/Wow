package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.adapters.FamilyDepartmentAdapter;
import com.creativeshare.wow.adapters.ProductAdapter;
import com.creativeshare.wow.models.Department_Model;
import com.creativeshare.wow.models.FamiliesStoreDataModel;
import com.creativeshare.wow.models.ProductsDataModel;
import com.creativeshare.wow.remote.Api;
import com.creativeshare.wow.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentFamilyDepartments extends Fragment {
    private static final String TAG = "DATA";
    private ImageView arrow;
    private LinearLayout ll_back;
    private FrameLayout fl_cart;
    private TextView tv_counter,tv_no_product;
    private ProgressBar progBar;
    private List<ProductsDataModel.ProductModel> productModelList;
    private RecyclerView recViewDept,recView;
    private LinearLayoutManager manager_dept,manager;
    private String current_language;
    private ClientHomeActivity activity;
    private FamiliesStoreDataModel.FamilyModel familyModel;
    private FamilyDepartmentAdapter departmentAdapter;
    private ProductAdapter productAdapter;
    private int lastSelectedDeptPos = 0;




    public static FragmentFamilyDepartments newInstance(FamiliesStoreDataModel.FamilyModel familyModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,familyModel);
        FragmentFamilyDepartments fragmentFamilyDepartments = new FragmentFamilyDepartments();
        fragmentFamilyDepartments.setArguments(bundle);
        return fragmentFamilyDepartments;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departments,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        productModelList = new ArrayList<>();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);
        fl_cart = view.findViewById(R.id.fl_cart);
        tv_counter = view.findViewById(R.id.tv_counter);
        tv_no_product = view.findViewById(R.id.tv_no_product);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recViewDept = view.findViewById(R.id.recViewDept);
        recView = view.findViewById(R.id.recView);
        manager_dept = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
        recViewDept.setLayoutManager(manager_dept);
        manager = new GridLayoutManager(activity,2);
        recView.setLayoutManager(manager);
        productAdapter = new ProductAdapter(productModelList,activity,this);
        recView.setAdapter(productAdapter);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            familyModel = (FamiliesStoreDataModel.FamilyModel) bundle.getSerializable(TAG);
            updateUI(familyModel);
        }

        fl_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentCart(familyModel.getUser_id());
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
    }

    private void updateUI(FamiliesStoreDataModel.FamilyModel familyModel) {

        if (familyModel.getMy_dep().size()>0)
        {
            tv_no_product.setVisibility(View.GONE);
            departmentAdapter = new FamilyDepartmentAdapter(activity,familyModel.getMy_dep(),this);
            recViewDept.setAdapter(departmentAdapter);
        }else
            {
                tv_no_product.setVisibility(View.VISIBLE);

            }

        if (familyModel.getMy_dep()!=null && familyModel.getMy_dep().size()>0)
        {
            getProducts(familyModel.getMy_dep().get(0).getId_department());

        }else
            {
                tv_no_product.setVisibility(View.VISIBLE);
            }

    }

    private void getProducts(String dept_id)
    {
        progBar.setVisibility(View.VISIBLE);
        productModelList.clear();
        productAdapter.notifyDataSetChanged();
        tv_no_product.setVisibility(View.GONE);

        Api.getService(Tags.base_url)
                .getProductsByDeptId(dept_id,1)
                .enqueue(new Callback<ProductsDataModel>() {
                    @Override
                    public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                productModelList.addAll(response.body().getData());

                                if (productModelList.size()>0)
                                {
                                    tv_no_product.setVisibility(View.GONE);
                                    productAdapter.notifyDataSetChanged();

                                }else
                                {
                                    tv_no_product.setVisibility(View.VISIBLE);

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

    public void updateCartCount(int count)
    {
        if (count>0)
        {
            tv_counter.setText(String.valueOf(count));
            tv_counter.setVisibility(View.VISIBLE);
        }else
            {
                tv_counter.setText("");
                tv_counter.setVisibility(View.GONE);
            }
    }
    public void setItemData(Department_Model department_model,int lastSelectedDeptPos) {
        if (this.lastSelectedDeptPos!=lastSelectedDeptPos)
        {
            this.lastSelectedDeptPos = lastSelectedDeptPos;
            getProducts(department_model.getId_department());

        }
    }

    public void setItemDataForProduct(ProductsDataModel.ProductModel productModel) {
        activity.DisplayFragmentProductDetails(productModel);
    }
}
