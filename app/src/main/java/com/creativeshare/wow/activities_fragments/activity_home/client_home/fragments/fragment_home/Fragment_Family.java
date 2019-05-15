package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.adapters.FamiliesStoreAdapter;
import com.creativeshare.wow.models.FamiliesStoreDataModel;
import com.creativeshare.wow.remote.Api;
import com.creativeshare.wow.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Family  extends Fragment {
    private static final String TAG1 = "LAT";
    private static final String TAG2 = "LNG";

    private ImageView arrow;
    private LinearLayout ll_back;
    private ClientHomeActivity activity;
    private RecyclerView recView;
    private FamiliesStoreAdapter adapter;
    private List<FamiliesStoreDataModel.FamilyModel> familyModelList;
    private LinearLayoutManager manager;
    private ProgressBar progBar;
    private TextView tv_no_store;
    private double lat,lng;
    private String current_language;
    private int current_page = 1;
    private boolean isLoading = false;

    public static Fragment_Family newInstance(double lat,double lng) {
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG1,lat);
        bundle.putDouble(TAG2,lng);
        Fragment_Family fragment_family = new Fragment_Family();
        fragment_family.setArguments(bundle);
        return fragment_family;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        familyModelList = new ArrayList<>();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        } else {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        }

        ll_back = view.findViewById(R.id.ll_back);

        tv_no_store = view.findViewById(R.id.tv_no_store);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        recView = view.findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new FamiliesStoreAdapter(familyModelList,activity,this);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5)&& !isLoading)
                    {
                        isLoading = true;
                        int next_page = current_page+1;
                        familyModelList.add(null);
                        adapter.notifyItemInserted(familyModelList.size()-1);
                        loadMore(next_page);
                    }
                }
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            lat = bundle.getDouble(TAG1,0.0);
            lng = bundle.getDouble(TAG2,0.0);
            getStores();
        }

    }

    private void getStores() {
        Api.getService(Tags.base_url)
                .getFamiliesStores(lat,lng,1)
                .enqueue(new Callback<FamiliesStoreDataModel>() {
                    @Override
                    public void onResponse(Call<FamiliesStoreDataModel> call, Response<FamiliesStoreDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                familyModelList.clear();
                                familyModelList.addAll(response.body().getData());
                                if (familyModelList.size()>0)
                                {
                                    tv_no_store.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();

                                }else
                                    {
                                        tv_no_store.setVisibility(View.VISIBLE);

                                    }
                            }
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
                    public void onFailure(Call<FamiliesStoreDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void loadMore(int page_index) {
        Api.getService(Tags.base_url)
                .getFamiliesStores(lat, lng, page_index)
                .enqueue(new Callback<FamiliesStoreDataModel>() {
                    @Override
                    public void onResponse(Call<FamiliesStoreDataModel> call, Response<FamiliesStoreDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        familyModelList.remove(familyModelList.size() - 1);
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                            if (response.body().getData().size() > 0) {

                                current_page = response.body().getMeta().getCurrent_page();
                                familyModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();

                            }else
                            {
                                adapter.notifyDataSetChanged();
                            }



                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<FamiliesStoreDataModel> call, Throwable t) {
                        try {
                            familyModelList.remove(familyModelList.size() - 1);
                            isLoading = false;
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void setItemData(FamiliesStoreDataModel.FamilyModel familyModel, int adapterPosition) {
        activity.DisplayFragmentFamilyDepartments(familyModel);
    }
}
