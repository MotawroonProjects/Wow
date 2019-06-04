package com.creative_share_apps.wow.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Family_Own_Products;
import com.creative_share_apps.wow.models.ProductsDataModel;
import com.creative_share_apps.wow.tags.Tags;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class FamilyOwnProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;
    private List<ProductsDataModel.ProductModel> productModelList;
    private Context context;
    private Fragment_Family_Own_Products fragment;
    private String current_language;
    private  Currency currency;

    public FamilyOwnProductAdapter(List<ProductsDataModel.ProductModel> productModelList, Context context, Fragment_Family_Own_Products fragment) {

        this.productModelList = productModelList;
        this.context = context;
        this.fragment = fragment;
        Paper.init(context);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());



    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.family_own_product_row, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.load_more_row, parent, false);
            return new LoadMoreHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {

            final MyHolder myHolder = (MyHolder) holder;
            ProductsDataModel.ProductModel productModel = productModelList.get(myHolder.getAdapterPosition());
            myHolder.BindData(productModel);

            myHolder.btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductsDataModel.ProductModel productModel = productModelList.get(myHolder.getAdapterPosition());
                    fragment.setItemData(productModel,myHolder.getAdapterPosition(),"update");


                }

            });

            myHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductsDataModel.ProductModel productModel = productModelList.get(myHolder.getAdapterPosition());
                    fragment.setItemData(productModel,myHolder.getAdapterPosition(),"delete");


                }

            });

        } else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_name,tv_price;
        private Button btn_update,btn_delete;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_update = itemView.findViewById(R.id.btn_update);
            btn_delete = itemView.findViewById(R.id.btn_delete);


        }

        public void BindData(ProductsDataModel.ProductModel productModel) {

            currency = Currency.getInstance(new Locale(current_language,productModel.getUser_country()));

            if (current_language.equals("ar"))
            {
                tv_name.setText(productModel.getAr_title_pro());

            }else {
                tv_name.setText(productModel.getEn_title_pro());

            }
            if (currency!=null)
            {
                tv_price.setText(String.format("%s %s",productModel.getPrice(),currency.getSymbol()));

            }else
                {
                    tv_price.setText(productModel.getPrice());

                }


            if (productModel.getImage().equals("0"))
            {
                Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
            }else
                {
                    new MyAsnckTask().execute(productModel.getImage());

                }

        }

        private class MyAsnckTask extends AsyncTask<String,Void,Bitmap>
        {
            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = Tags.IMAGE_URL+strings[0];
                Bitmap bitmap = null;
                try {
                     bitmap = Picasso.with(context).load(Uri.parse(url)).transform(new Transformation() {
                         @Override
                         public Bitmap transform(Bitmap source) {
                             int size = Math.min(source.getWidth(),source.getHeight());
                             int x = (source.getWidth()-size)/2;
                             int y = (source.getHeight()-size)/2;
                             Bitmap result = Bitmap.createBitmap(source,x,y,size,size);

                             if (result!=source)
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
                if (bitmap!=null)
                {
                    image.setImageBitmap(bitmap);

                }
            }
        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private ProgressBar progBar;

        public LoadMoreHolder(View itemView) {
            super(itemView);
            progBar = itemView.findViewById(R.id.progBar);
            progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ProductsDataModel.ProductModel productModel = productModelList.get(position);
        if (productModel == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;
        }

    }
}
