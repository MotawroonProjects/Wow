package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.adapters.ProductsDetailsAdapter;
import com.creativeshare.wow.models.OrderClientFamilyDataModel;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Order_Products extends Fragment {
    private static final String TAG="DATA";
    private ImageView arrow;
    private LinearLayout ll_back;
    private ClientHomeActivity activity;
    private String current_language;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProductsDetailsAdapter adapter;
    private List<OrderClientFamilyDataModel.ProductModel> productModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_products, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Order_Products newInstance(List<OrderClientFamilyDataModel.ProductModel> productModelList) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, (Serializable) productModelList);

        Fragment_Order_Products fragment_order_products = new Fragment_Order_Products();
        fragment_order_products.setArguments(bundle);
        return fragment_order_products;
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);


        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            productModelList = (List<OrderClientFamilyDataModel.ProductModel>) bundle.getSerializable(TAG);
            adapter = new ProductsDetailsAdapter(productModelList,activity);
            recView.setAdapter(adapter);
        }



        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

    }


}
