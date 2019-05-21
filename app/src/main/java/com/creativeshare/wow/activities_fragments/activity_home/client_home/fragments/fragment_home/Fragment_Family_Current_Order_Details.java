package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.adapters.ProductsDetailsAdapter;
import com.creativeshare.wow.models.ChatUserModel;
import com.creativeshare.wow.models.OrderClientFamilyDataModel;
import com.creativeshare.wow.models.UserModel;
import com.creativeshare.wow.singletone.UserSingleTone;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Family_Current_Order_Details extends Fragment {
    private static final String TAG = "ORDER";
    private ClientHomeActivity activity;
    private ImageView image_back,image_arrow2,image_chat;
    private LinearLayout ll_back;
    private AppBarLayout app_bar;
    private String current_lang;
    private TextView tv_client_name,tv_address,tv_order_state,tv_order_next_state,tv_total_cost;
    private FrameLayout fl_update_order_state;
    private OrderClientFamilyDataModel.OrderModel order;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private int order_state;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProductsDetailsAdapter adapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_family_current_order_details,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Family_Current_Order_Details newInstance(OrderClientFamilyDataModel.OrderModel order)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,order);
        Fragment_Family_Current_Order_Details fragment_delegate_Current_order_details = new Fragment_Family_Current_Order_Details();
        fragment_delegate_Current_order_details.setArguments(bundle);
        return fragment_delegate_Current_order_details;
    }
    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());


        image_back = view.findViewById(R.id.image_back);
        image_arrow2 = view.findViewById(R.id.image_arrow2);

        if (current_lang.equals("ar"))
        {
            image_back.setImageResource(R.drawable.ic_right_arrow);
            image_arrow2.setImageResource(R.drawable.ic_right_arrow);

        }else
        {
            image_back.setImageResource(R.drawable.ic_left_arrow);
            image_arrow2.setImageResource(R.drawable.ic_left_arrow);

        }
        image_chat = view.findViewById(R.id.image_chat);
        app_bar = view.findViewById(R.id.app_bar);

        ll_back = view.findViewById(R.id.ll_back);
        tv_order_state = view.findViewById(R.id.tv_order_state);
        tv_order_next_state = view.findViewById(R.id.tv_order_next_state);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_address = view.findViewById(R.id.tv_address);
        fl_update_order_state = view.findViewById(R.id.fl_update_order_state);
        tv_total_cost = view.findViewById(R.id.tv_total_cost);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);



        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            this.order = (OrderClientFamilyDataModel.OrderModel) bundle.getSerializable(TAG);
            UpdateUI(this.order);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });




        image_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatUserModel chatUserModel = new ChatUserModel(order.getClient_user_full_name(),order.getClient_user_image(),order.getClient_id(),order.getRoom_id_fk(),order.getClient_user_phone_code(),order.getClient_user_phone(),order.getOrder_id(),order.getDriver_offer());
                activity.NavigateToChatActivity(chatUserModel,"from_fragment");
            }
        });

        fl_update_order_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.familyUpdateMovement(userModel.getData().getUser_id(),order.getClient_id(),order.getOrder_id(),String.valueOf(order_state));

            }
        });


        app_bar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                int total_range = appBarLayout.getTotalScrollRange();

                if ((total_range+i)<60)
                {
                    image_chat.setVisibility(View.GONE);
                    tv_order_state.setVisibility(View.GONE);
                }else
                    {
                        image_chat.setVisibility(View.VISIBLE);
                        tv_order_state.setVisibility(View.VISIBLE);
                    }

            }
        });


    }

    private void UpdateUI(OrderClientFamilyDataModel.OrderModel order)
    {
        adapter = new ProductsDetailsAdapter(order.getProducts(),activity);
        recView.setAdapter(adapter);

        if (order.getProducts().size()>0)
        {
            Currency currency = Currency.getInstance(new Locale(current_lang,order.getProducts().get(0).getUser_country()));
            tv_total_cost.setText(String.format("%s %s %s",getString(R.string.total_order_cost),order.getTotal_order_cost(),currency.getSymbol()));
        }else
            {
                tv_total_cost.setText(String.format("%s %s",getString(R.string.total_order_cost),order.getTotal_order_cost()));

            }

        tv_client_name.setText(order.getClient_user_full_name());
        tv_address.setText(order.getClient_address());



        //check user type
        order_state = Integer.parseInt(order.getOrder_status());
        updateOrderState(order_state);

    }


    public void updateOrderState(int state)
    {


        this.order_state = state;
        switch (state)
        {
            case 1:
                tv_order_state.setText(getString(R.string.accepted));
                tv_order_next_state.setText(getString(R.string.proc_order));
                break;
            case 2:
                tv_order_state.setText(getString(R.string.order_comp));
                tv_order_next_state.setText(getString(R.string.done2));
                break;
            case 3:
                tv_order_state.setText(getString(R.string.done));
                tv_order_next_state.setText(getString(R.string.done));
                fl_update_order_state.setVisibility(View.INVISIBLE);
                break;




        }



    }





}
