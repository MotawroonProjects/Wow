package com.creativeshare.wow.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Family;
import com.creativeshare.wow.models.FamiliesStoreDataModel;
import com.creativeshare.wow.tags.Tags;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.List;

public class FamiliesStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;
    private List<FamiliesStoreDataModel.FamilyModel> familiesStoreDataModelList;
    private Context context;
    private Fragment_Family fragment;

    public FamiliesStoreAdapter(List<FamiliesStoreDataModel.FamilyModel> familiesStoreDataModelList, Context context, Fragment_Family fragment) {

        this.familiesStoreDataModelList = familiesStoreDataModelList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.family_row, parent, false);
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
            FamiliesStoreDataModel.FamilyModel familyModel = familiesStoreDataModelList.get(myHolder.getAdapterPosition());
            myHolder.BindData(familyModel);

            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FamiliesStoreDataModel.FamilyModel familyModel = familiesStoreDataModelList.get(myHolder.getAdapterPosition());
                    //delegate send price offer client accept or refused
                    fragment.setItemData(familyModel, myHolder.getAdapterPosition());


                }

            });

        } else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return familiesStoreDataModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private SimpleRatingBar rateBar;
        private TextView tv_name, tv_rate, tv_distance;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            rateBar = itemView.findViewById(R.id.rateBar);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_name = itemView.findViewById(R.id.tv_name);


        }

        public void BindData(FamiliesStoreDataModel.FamilyModel familyModel) {
            tv_name.setText(familyModel.getUser_full_name());
            tv_rate.setText("("+familyModel.getRate()+")");

            tv_distance.setText(familyModel.getDistance()+" "+context.getString(R.string.km));
            rateBar.setRating((float)familyModel.getRate());

            if (familyModel.getUser_image().equals("0"))
            {
                Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
            }else
            {
                new MyAsnckTask().execute(familyModel.getUser_image());

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
        FamiliesStoreDataModel.FamilyModel familyModel = familiesStoreDataModelList.get(position);
        if (familyModel == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;
        }

    }
}
