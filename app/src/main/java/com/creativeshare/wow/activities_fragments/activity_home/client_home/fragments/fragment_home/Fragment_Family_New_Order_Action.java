package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.creativeshare.wow.models.OrderClientFamilyDataModel;
import com.creativeshare.wow.models.UserModel;
import com.creativeshare.wow.singletone.UserSingleTone;
import com.creativeshare.wow.tags.Tags;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Fragment_Family_New_Order_Action extends Fragment {

    private final static String TAG = "Data";
    private ImageView image_back;
    private LinearLayout ll_back, ll_client_container;
    private CircleImageView image;
    private TextView tv_client_name, tv_order_address;
    private Button btn_accept, btn_refused;
    private OrderClientFamilyDataModel.OrderModel orderModel ;
    private String current_language;
    private ClientHomeActivity activity;
    private AppBarLayout app_bar;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProductsDetailsAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_new_order_action, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Family_New_Order_Action newInstance(OrderClientFamilyDataModel.OrderModel  orderModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, orderModel);

        Fragment_Family_New_Order_Action fragment_delegate_add_offer = new Fragment_Family_New_Order_Action();
        fragment_delegate_add_offer.setArguments(bundle);
        return fragment_delegate_add_offer;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        image_back = view.findViewById(R.id.image_back);

        if (current_language.equals("ar")) {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        } else {

            image_back.setImageResource(R.drawable.ic_left_arrow);
        }
        ll_back = view.findViewById(R.id.ll_back);
        image = view.findViewById(R.id.image);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_order_address = view.findViewById(R.id.tv_order_address);
        btn_accept = view.findViewById(R.id.btn_accept);
        btn_refused = view.findViewById(R.id.btn_refused);
        ll_client_container = view.findViewById(R.id.ll_client_container);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        app_bar = view.findViewById(R.id.app_bar);


        Bundle bundle = getArguments();

        if (bundle != null) {
            orderModel = (OrderClientFamilyDataModel.OrderModel) bundle.getSerializable(TAG);
            UpdateUI(orderModel);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.familyAcceptOrder(userModel.getData().getUser_id(), orderModel.getClient_id(), orderModel.getOrder_id(),"1");

            }
        });

        btn_refused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.familyRefuse_FinishOrder(userModel.getData().getUser_id(), orderModel.getClient_id(), orderModel.getOrder_id(), "4");

            }
        });


        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalRange = appBarLayout.getTotalScrollRange();
                if ((totalRange + verticalOffset) <= 50) {
                    ll_client_container.setVisibility(View.GONE);
                } else {
                    ll_client_container.setVisibility(View.VISIBLE);

                }
            }
        });


    }


    private void UpdateUI(OrderClientFamilyDataModel.OrderModel orderModel) {
        if (orderModel != null) {
            adapter = new ProductsDetailsAdapter(orderModel.getProducts(),activity);
            recView.setAdapter(adapter);
            Picasso.with(activity).load(Tags.IMAGE_URL + orderModel.getClient_user_image()).placeholder(R.drawable.logo_only).fit().into(image);
            tv_client_name.setText(orderModel.getClient_user_full_name());
            tv_order_address.setText(orderModel.getClient_address());


        }

    }




}
