package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.adapters.CartAdapter;
import com.creative_share_apps.wow.models.ItemModel;
import com.creative_share_apps.wow.models.OrderModelToUpload;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.remote.Api;
import com.creative_share_apps.wow.share.Common;
import com.creative_share_apps.wow.singletone.OrderModelSingleTone;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.creative_share_apps.wow.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Cart extends Fragment {
    private static final String TAG1="LAT";
    private static final String TAG2="LNG";
    private static final String TAG3="FAMILY_ID";

    private ImageView arrow;
    private LinearLayout ll_back,ll_no_products;
    private TextView tv_total_cost;
    private Button btn_send;
    private ConstraintLayout cons_product_cost;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ClientHomeActivity activity;
    private String current_language;
    private OrderModelSingleTone orderModelSingleTone;
    private double total_order_cost = 0.0;
    private List<ItemModel> itemModelList;
    private CartAdapter cartAdapter;
    private Currency currency;
    private double lat=0.0,lng=0.0;
    private String client_address="";
    private OrderModelToUpload orderModelToUpload;
    private String family_id="";
    private UserSingleTone userSingleTone;
    private UserModel userModel;


    public static Fragment_Cart newInstance(double lat,double lng,String family_id)
    {
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG1,lat);
        bundle.putDouble(TAG2,lng);
        bundle.putString(TAG3,family_id);

        Fragment_Cart fragment_cart = new Fragment_Cart();
        fragment_cart.setArguments(bundle);
        return fragment_cart;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        itemModelList = new ArrayList<>();
        orderModelSingleTone = OrderModelSingleTone.newInstance();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);
        ll_no_products = view.findViewById(R.id.ll_no_products);
        cons_product_cost = view.findViewById(R.id.cons_product_cost);
        tv_total_cost = view.findViewById(R.id.tv_total_cost);
        btn_send = view.findViewById(R.id.btn_send);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(client_address))
                {
                    CreateAddDeliveryAddressDialog();
                }else
                    {
                        SendOrder();
                    }
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null){
            lat = bundle.getDouble(TAG1);
            lng = bundle.getDouble(TAG2);
            family_id = bundle.getString(TAG3);

        }
        updateUI();

    }

    private void updateUI()
    {
       updateContainerProductData(orderModelSingleTone.getItemCount());
       if (orderModelSingleTone.getItemCount()>0)
       {
           currency = Currency.getInstance(new Locale(current_language,orderModelSingleTone.getItemsList().get(0).getCountry_code()));
           total_order_cost = getTotalOrderCost(orderModelSingleTone.getItemsList());
           tv_total_cost.setText(total_order_cost+" "+currency.getSymbol());
           itemModelList.addAll(orderModelSingleTone.getItemsList());
           cartAdapter = new CartAdapter(activity,itemModelList,this);
           recView.setAdapter(cartAdapter);
       }
    }
    private void updateContainerProductData(int product_size)
    {
        if (product_size>0)
        {
            ll_no_products.setVisibility(View.GONE);
            cons_product_cost.setVisibility(View.VISIBLE);
        }else
        {
            ll_no_products.setVisibility(View.VISIBLE);
            cons_product_cost.setVisibility(View.GONE);

        }
    }
    private double getTotalOrderCost(List<ItemModel> itemModelList)
    {
        double total = 0.0;

        for (ItemModel itemModel :itemModelList)
        {
            total+=itemModel.getProduct_price()*itemModel.getQuantity();
        }

        return total;
    }

    public void RemoveItem(int pos)
    {
        orderModelSingleTone.removeItem(pos);
        this.itemModelList.remove(pos);
        getTotalOrderCost(orderModelSingleTone.getItemsList());
        cartAdapter.notifyItemRemoved(pos);
        if (this.itemModelList.size()==0)
        {
            updateContainerProductData(this.itemModelList.size());
            orderModelSingleTone.clear();
            activity.updateCartCount(0);
        }

    }

    public void Increment_Decrement(ItemModel itemModel, int counter, int pos) {

        itemModel.setQuantity(counter);
        double total_cost = itemModel.getProduct_price()*counter;

        itemModel.setTotal_cost(total_cost);
        this.itemModelList.set(pos,itemModel);

        orderModelSingleTone.Update_Item(itemModel,pos);
        tv_total_cost.setText(String.format("%s %s", getTotalOrderCost(this.itemModelList), currency.getSymbol()));

    }

    private   void CreateAddDeliveryAddressDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_add_delivery_address,null);
        Button btn_add = view.findViewById(R.id.btn_select_location);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_sub_title = view.findViewById(R.id.tv_sub_title);

        final EditText edt_address = view.findViewById(R.id.edt_nationality);
        tv_title.setText(getString(R.string.address));
        tv_sub_title.setText(getString(R.string.address));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 client_address = edt_address.getText().toString().trim();

                if (!TextUtils.isEmpty(client_address))
                {
                    edt_address.setError(null);
                    Common.CloseKeyBoard(activity,edt_address);
                    dialog.dismiss();
                    SendOrder();
                }else
                {
                    edt_address.setError(getString(R.string.field_req));
                }

            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private void SendOrder() {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();

        Log.e("family_id",family_id+"_");
        orderModelToUpload = new OrderModelToUpload(userModel.getData().getUser_id(),getTotalOrderCost(this.itemModelList),lat,lng,client_address,family_id,itemModelList);

        Api.getService(Tags.base_url)
                .sendFamilyOrder(orderModelToUpload)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            Toast.makeText(activity,getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            itemModelList.clear();
                            cartAdapter.notifyDataSetChanged();
                            updateContainerProductData(0);
                            orderModelSingleTone.clear();
                            activity.updateCartCount(0);
                            activity.RefreshFragment_FamilyOrder();

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
}
