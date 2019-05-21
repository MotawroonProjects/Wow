package com.creativeshare.wow.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Cart;
import com.creativeshare.wow.models.ItemModel;
import com.creativeshare.wow.tags.Tags;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyHolder> {

    private Context context;
    private List<ItemModel> itemModelList;
    private Fragment_Cart fragment_cart;
    private String lang;
    private Currency currency;
    private ScaleAnimation animation;

    public CartAdapter(Context context, List<ItemModel> itemModelList, Fragment_Cart fragment_cart) {
        this.context = context;
        this.itemModelList = itemModelList;
        this.fragment_cart = fragment_cart;
        animation = new ScaleAnimation(.7f, 1.0f, .7f, 1.0f, 1.0f, 1.0f);
        animation.setDuration(300);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        currency = Currency.getInstance(new Locale(lang,itemModelList.get(0).getCountry_code()));

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        ItemModel itemModel = itemModelList.get(position);
        holder.BindData(itemModel);
        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_cart.RemoveItem(holder.getAdapterPosition());
            }
        });
        holder.image_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel itemModel = itemModelList.get(holder.getAdapterPosition());

                holder.image_increment.clearAnimation();
                holder.image_decrement.clearAnimation();
                holder.image_increment.startAnimation(animation);
                int counter = Integer.parseInt(holder.tv_counter.getText().toString().trim()) + 1;
                holder.tv_counter.setText(String.valueOf(counter));
                double total = counter * itemModel.getProduct_price();
                holder.tv_price.setText(total+" "+currency.getSymbol());
                fragment_cart.Increment_Decrement(itemModel, counter,holder.getAdapterPosition());
            }
        });
        holder.image_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel itemModel = itemModelList.get(holder.getAdapterPosition());

                holder.image_decrement.clearAnimation();
                holder.image_increment.clearAnimation();
                holder.image_decrement.startAnimation(animation);

                int counter = Integer.parseInt(holder.tv_counter.getText().toString().trim()) - 1;
                if (counter < 1) {
                    counter = 1;
                }
                double total = counter * itemModel.getProduct_price();
                holder.tv_price.setText(total+" "+currency.getSymbol());

                holder.tv_counter.setText(String.valueOf(counter));
                fragment_cart.Increment_Decrement(itemModel, counter, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView image_delete, image, image_increment, image_decrement;
        private TextView tv_name, tv_price, tv_counter;

        public MyHolder(View itemView) {
            super(itemView);
            image_delete = itemView.findViewById(R.id.image_delete);
            image = itemView.findViewById(R.id.image);
            image_increment = itemView.findViewById(R.id.image_increment);
            image_decrement = itemView.findViewById(R.id.image_decrement);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_counter = itemView.findViewById(R.id.tv_counter);

        }

        public void BindData(ItemModel itemModel) {
            if (lang.equals("ar")) {
                tv_name.setText(itemModel.getAr_name());
            } else {
                tv_name.setText(itemModel.getEn_name());

            }

            tv_counter.setText(String.valueOf(itemModel.getQuantity()));
            tv_price.setText((itemModel.getProduct_price()*itemModel.getQuantity())+" "+currency.getSymbol());

            if (itemModel.getImage().equals("0"))
            {
                Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
            }else {
                new MyAsyncTask().execute(itemModel.getImage());
            }
        }

        private class MyAsyncTask extends AsyncTask<String,Void, Bitmap>
        {
            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmap = null;
                String url = Tags.IMAGE_URL+strings[0];
                try {
                    bitmap = Picasso.with(context).load(Uri.parse(url)).transform(new Transformation() {
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
}
