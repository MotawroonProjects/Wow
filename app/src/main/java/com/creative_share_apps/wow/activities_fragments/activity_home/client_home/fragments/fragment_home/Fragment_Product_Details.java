package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.models.ItemModel;
import com.creative_share_apps.wow.models.ProductsDataModel;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.singletone.OrderModelSingleTone;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.creative_share_apps.wow.tags.Tags;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Product_Details extends Fragment {

    private static final String TAG = "DATA";
    private ImageView image,arrow,image_increment,image_decrement;
    private LinearLayout ll_back;
    private TextView tv_name,tv_details,tv_counter;
    private ConstraintLayout cons_counter;
    private Button btn_add_to_cart;
    private String current_language;
    private ClientHomeActivity activity;
    private ProductsDataModel.ProductModel productModel;
    private int counter = 1;
    private OrderModelSingleTone orderModelSingleTone;
    private UserSingleTone userSingleTone;
    private UserModel userModel;



    public static Fragment_Product_Details newInstance(ProductsDataModel.ProductModel productModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,productModel);
        Fragment_Product_Details fragment_product_details = new Fragment_Product_Details() ;
        fragment_product_details.setArguments(bundle);
        return fragment_product_details;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        orderModelSingleTone = OrderModelSingleTone.newInstance();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        cons_counter = view.findViewById(R.id.cons_counter);

        image = view.findViewById(R.id.image);
        image_increment = view.findViewById(R.id.image_increment);
        image_decrement = view.findViewById(R.id.image_decrement);
        ll_back = view.findViewById(R.id.ll_back);
        tv_name = view.findViewById(R.id.tv_name);
        tv_details = view.findViewById(R.id.tv_details);
        tv_counter = view.findViewById(R.id.tv_counter);
        btn_add_to_cart = view.findViewById(R.id.btn_add_to_cart);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            productModel = (ProductsDataModel.ProductModel) bundle.getSerializable(TAG);
            updateUI(productModel);
        }

        image_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                tv_counter.setText(String.valueOf(counter));
            }
        });

        image_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (counter>1)
                {
                    counter--;
                    tv_counter.setText(String.valueOf(counter));
                }

            }
        });

        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double total_cost = Double.parseDouble(productModel.getPrice())*counter;
                ItemModel itemModel = new ItemModel(productModel.getId_product(),productModel.getDep_id_fk(),productModel.getUser_id_fk(),productModel.getAr_title_pro(),productModel.getEn_title_pro(),productModel.getImage(),counter,Double.parseDouble(productModel.getPrice()),total_cost,productModel.getUser_country());
                orderModelSingleTone.Add_Item(itemModel);
                activity.updateCartCount(orderModelSingleTone.getItemCount());
                Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(ProductsDataModel.ProductModel productModel)
    {

        Currency currency = Currency.getInstance(new Locale(current_language,productModel.getUser_country()));

        if (productModel.getImage().equals("0"))
        {
            Picasso.with(activity).load(R.drawable.logo_only).fit().into(image);
        }else
            {
                new MyAsyncTask().execute(productModel.getImage());
            }


        if (current_language.equals("ar"))
        {
            tv_name.setText(productModel.getAr_title_pro());
            if (productModel.getNotes().equals("0"))
            {
                tv_details.setText(productModel.getAr_details_pro()+"\n"+productModel.getPrice()+" "+currency.getSymbol());

            }else
                {
                    tv_details.setText(productModel.getAr_details_pro()+"\n"+productModel.getPrice()+" "+currency.getSymbol()+"\n"+productModel.getNotes());

                }
        }else
            {
                tv_name.setText(productModel.getEn_title_pro());

                if (TextUtils.isEmpty(productModel.getNotes()))
                {
                    tv_details.setText(productModel.getEn_details_pro()+"\n"+productModel.getPrice()+" "+currency.getSymbol());

                }else
                {
                    tv_details.setText(productModel.getEn_details_pro()+"\n"+productModel.getPrice()+" "+currency.getSymbol()+"\n"+productModel.getNotes());

                }


            }

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            cons_counter.setVisibility(View.VISIBLE);
        }else
            {
                cons_counter.setVisibility(View.INVISIBLE);

            }

    }
    private class MyAsyncTask extends AsyncTask<String,Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            String url = Tags.IMAGE_URL+strings[0];
            try {
                bitmap = Picasso.with(activity).load(Uri.parse(url)).transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int size = Math.min(source.getWidth(),source.getHeight());
                        int x = (source.getWidth()-size)/2;
                        int y = (source.getHeight()-size)/2;
                        Bitmap result = Bitmap.createBitmap(source,x,y,size,size);

                        if (source!=result)
                        {
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "recycle()";
                    }
                }).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            image.setImageBitmap(bitmap);
        }
    }
}
