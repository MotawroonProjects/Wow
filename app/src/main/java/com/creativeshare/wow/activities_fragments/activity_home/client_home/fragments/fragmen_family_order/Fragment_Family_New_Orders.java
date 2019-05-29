package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragmen_family_order;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.adapters.OrdersFamiliesAdapter;
import com.creativeshare.wow.models.OrderClientFamilyDataModel;
import com.creativeshare.wow.models.UserModel;
import com.creativeshare.wow.preferences.Preferences;
import com.creativeshare.wow.remote.Api;
import com.creativeshare.wow.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Family_New_Orders extends Fragment {

    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ClientHomeActivity activity;
    private TextView tv_no_orders;
    private List<OrderClientFamilyDataModel.OrderModel> orderModelList;
    private OrdersFamiliesAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;
    private boolean isLoading = false;
    private int current_page = 1;
    private Call<OrderClientFamilyDataModel> call;

    private boolean isFirstTime = true;

    @Override
    public void onStart() {
        super.onStart();
        if (!isFirstTime&&adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_order_type,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Family_New_Orders newInstance()
    {
        return new Fragment_Family_New_Orders();
    }
    private void initView(View view) {
        orderModelList = new ArrayList<>();

        activity = (ClientHomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        tv_no_orders = view.findViewById(R.id.tv_no_orders);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new OrdersFamiliesAdapter(orderModelList,activity,userModel.getData().getUser_type(),this);
        recView.setAdapter(adapter);

        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int lastVisibleItem = ((LinearLayoutManager)manager).findLastCompletelyVisibleItemPosition();
                    int totalItems = manager.getItemCount();

                    if (lastVisibleItem>=(totalItems-5)&&!isLoading)
                    {
                        isLoading = true;
                        orderModelList.add(null);
                        adapter.notifyItemInserted(orderModelList.size()-1);
                        int next_page = current_page+1;
                        loadMore(next_page);
                    }
                }
            }
        });
        getOrders();

    }

    public void getOrders()
    {

        if (userModel==null)
        {
            preferences = Preferences.getInstance();
            userModel = preferences.getUserData(activity);
        }

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        call  = Api.getService(Tags.base_url).getDelegateFamiliesOrders(userModel.getData().getUser_id(),"new",userModel.getData().getUser_type(), 1);




                        call.enqueue(new Callback<OrderClientFamilyDataModel>() {
                            @Override
                            public void onResponse(Call<OrderClientFamilyDataModel> call, Response<OrderClientFamilyDataModel> response) {
                                progBar.setVisibility(View.GONE);
                                if (response.isSuccessful())
                                {
                                    orderModelList.clear();

                                    if (response.body()!=null&&response.body().getData().size()>0)
                                    {
                                        tv_no_orders.setVisibility(View.GONE);
                                        orderModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        isFirstTime = false;
                                    }else
                                    {
                                        tv_no_orders.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                }else
                                {

                                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                                    try {
                                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<OrderClientFamilyDataModel> call, Throwable t) {
                                try {
                                    progBar.setVisibility(View.GONE);
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    Log.e("Error",t.getMessage());
                                }catch (Exception e){}
                            }
                        });
                    }
                },1000);

    }


    private void loadMore(int page_index)
    {



        call  = Api.getService(Tags.base_url).getDelegateFamiliesOrders(userModel.getData().getUser_id(),"new",userModel.getData().getUser_type(), page_index);



        call.enqueue(new Callback<OrderClientFamilyDataModel>() {
            @Override
            public void onResponse(Call<OrderClientFamilyDataModel> call, Response<OrderClientFamilyDataModel> response) {
                orderModelList.remove(orderModelList.size()-1);
                adapter.notifyDataSetChanged();
                isLoading = false;

                if (response.isSuccessful())
                {

                    if (response.body()!=null&&response.body().getData().size()>0)
                    {
                        orderModelList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        current_page = response.body().getMeta().getCurrent_page();


                    }
                }else
                {


                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderClientFamilyDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (orderModelList.get(orderModelList.size()-1)==null)
                    {
                        orderModelList.remove(orderModelList.size()-1);
                        adapter.notifyDataSetChanged();
                    }
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }catch (Exception e){}
            }
        });
    }

    public void setItemData(OrderClientFamilyDataModel.OrderModel orderModel) {

        switch (userModel.getData().getUser_type()) {
            case Tags.TYPE_CLIENT:
                if (orderModel.getFamily_order_end().equals("0")) {
                    activity.DisplayFragmentClientFamilyOrderDetails(orderModel);

                } else {
                    activity.DisplayFragmentClientFamilyDelegateOrderDetails(orderModel);
                }
                break;
            case Tags.TYPE_DELEGATE:

                activity.DisplayFragmentDelegateFamilyAddOffer(orderModel);
                break;
            case Tags.TYPE_FAMILY:

                activity.DisplayFragmentFamilyNewOrderAction(orderModel);
                break;
        }
    }
}
