package com.creativeshare.wow.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.models.OrderClientFamilyDataModel;
import com.creativeshare.wow.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ProductsDetailsAdapter extends RecyclerView.Adapter<ProductsDetailsAdapter.MyHolder> {

    private List<OrderClientFamilyDataModel.ProductModel> productModelList;
    private Context context;
    private String current_language;
    public ProductsDetailsAdapter(List<OrderClientFamilyDataModel.ProductModel> productModelList, Context context) {
        this.productModelList = productModelList;
        this.context = context;
        Paper.init(context);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_details_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        OrderClientFamilyDataModel.ProductModel productModel = productModelList.get(position);
        holder.BindData(productModel);


    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_name,tv_amount,tv_cost;

        public MyHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_cost = itemView.findViewById(R.id.tv_cost);
            image = itemView.findViewById(R.id.image);



        }

        public void BindData(OrderClientFamilyDataModel.ProductModel productModel) {
            Currency currency = Currency.getInstance(new Locale(current_language,productModel.getUser_country()));
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+productModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image);
            tv_amount.setText(productModel.getProduct_amount());
            double total_cost = Integer.parseInt(productModel.getProduct_amount())*Double.parseDouble(productModel.getProduct_price());
            tv_cost.setText(String.format("%s %s",total_cost,currency.getSymbol()));

            if (current_language.equals("ar"))
            {
                tv_name.setText(productModel.getAr_title_pro());
            }else
                {
                    tv_name.setText(productModel.getEn_title_pro());

                }
        }
    }
}
