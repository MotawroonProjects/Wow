package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.adapters.NearbySearchAdapter;
import com.creative_share_apps.wow.adapters.SearchRecentAdapter;
import com.creative_share_apps.wow.models.NearbyModel;
import com.creative_share_apps.wow.models.NearbyStoreDataModel;
import com.creative_share_apps.wow.models.PlaceModel;
import com.creative_share_apps.wow.models.QueryModel;
import com.creative_share_apps.wow.models.SearchModel;
import com.creative_share_apps.wow.preferences.Preferences;
import com.creative_share_apps.wow.remote.Api;
import com.creative_share_apps.wow.share.Common;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Search extends Fragment {
    private static final String TAG2 = "LAT";
    private static final String TAG3 = "LNG";
    private ClientHomeActivity activity;
    private ImageView arrow, image_icon_delete;
    private ConstraintLayout cons_search;
    private LinearLayout ll_no_store;
    private EditText edt_search;
    private ExpandableLayout expandLayout;
    private RecyclerView recViewQueries, recView;
    private LinearLayoutManager manager, managerQueries;
    private ProgressBar progBar;
    private Animation animation;
    private String current_language;
    private String query = "";
    private List<QueryModel> queryModelList;
    private Preferences preferences;
    private SearchRecentAdapter searchRecentAdapter;
    private List<PlaceModel> placeModelList;
    private double lat = 0.0, lng = 0.0;
    private String user_address="";
    private NearbySearchAdapter adapter;
    private List<NearbyModel> nearbyModelList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Search newInstance(double lat, double lng) {
        Fragment_Search fragment_search = new Fragment_Search();
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG2, lat);
        bundle.putDouble(TAG3, lng);
        fragment_search.setArguments(bundle);
        return fragment_search;
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble(TAG2);
            lng = bundle.getDouble(TAG3);
            user_address = getUserAddress(lat,lng);
        }

        queryModelList = new ArrayList<>();
        placeModelList = new ArrayList<>();
        nearbyModelList = new ArrayList<>();




        preferences = Preferences.getInstance();
        queryModelList.addAll(preferences.getAllQueries(activity));

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        } else {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        }



        image_icon_delete = view.findViewById(R.id.image_icon_delete);
        cons_search = view.findViewById(R.id.cons_search);
        edt_search = view.findViewById(R.id.edt_search);
        expandLayout = view.findViewById(R.id.expandLayout);
        ll_no_store = view.findViewById(R.id.ll_no_store);

        recView = view.findViewById(R.id.recView);
        recViewQueries = view.findViewById(R.id.recViewQueries);
        manager = new LinearLayoutManager(activity);
        managerQueries = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new NearbySearchAdapter(nearbyModelList, activity, this, lat, lng);
        recView.setAdapter(adapter);
        recViewQueries.setLayoutManager(managerQueries);


        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        cons_search.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(activity, R.anim.search_anim);
        cons_search.clearAnimation();
        cons_search.startAnimation(animation);

        updateSearchAdapter();

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (image_icon_delete.getVisibility() != View.VISIBLE)
                {
                    image_icon_delete.setVisibility(View.VISIBLE);

                }

                if (queryModelList.size()>0) {
                    if (!expandLayout.isExpanded()) {
                        expandLayout.setExpanded(true);

                    }


                }

                if (edt_search.getText().toString().length() == 0){
                    query = "";
                    image_icon_delete.setVisibility(View.GONE);
                    expandLayout.collapse(true);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!edt_search.getText().toString().trim().isEmpty()) {
                        query = edt_search.getText().toString().trim();
                        Search();

                    }
                    return true;
                }
                return false;
            }
        });

        image_icon_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_icon_delete.setVisibility(View.GONE);
                edt_search.setText("");
                query = "";
            }
        });
    }


    private void Search() {


        //AIzaSyArjmbYWTWZhDFFtPOLRLKYwjtBDkOEGrY
        expandLayout.collapse(true);
        ll_no_store.setVisibility(View.GONE);

        Common.CloseKeyBoard(activity,edt_search);
        ll_no_store.setVisibility(View.GONE);
        placeModelList.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        progBar.setVisibility(View.VISIBLE);


        String loc = lat+","+lng;

        Api.getService("https://maps.googleapis.com/maps/api/")
                .getNearbySearchStores(loc,5000,query,current_language,getString(R.string.map_api_key))
                .enqueue(new Callback<NearbyStoreDataModel>() {
                    @Override
                    public void onResponse(Call<NearbyStoreDataModel> call, Response<NearbyStoreDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            progBar.setVisibility(View.GONE);
                            if (response.body()!=null&&response.body().getResults()!=null&&response.body().getResults().size()>0) {
                                preferences.saveQuery(activity, new QueryModel(query.trim()));
                                updateAdapter(response.body().getResults());

                            } else {
                                ll_no_store.setVisibility(View.VISIBLE);

                            }
                        } else {

                            progBar.setVisibility(View.GONE);

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<NearbyStoreDataModel> call, Throwable t) {
                        try {


                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void updateAdapter(List<NearbyModel> results) {


        nearbyModelList.addAll(sortData(results));
        adapter.notifyDataSetChanged();
        queryModelList.clear();
        queryModelList.addAll(preferences.getAllQueries(activity));
        updateSearchAdapter();
    }

    private List<PlaceModel> getPlaceModelFromResult(List<SearchModel> searchModelList)
    {
        List<PlaceModel> returnedList = new ArrayList<>();

        for (SearchModel searchModel : searchModelList)
        {
            PlaceModel placeModel = new PlaceModel(searchModel.getId(),searchModel.getPlace_id(),searchModel.getName(),searchModel.getIcon(),searchModel.getRating(),searchModel.getGeometry().getLocation().getLat(),searchModel.getGeometry().getLocation().getLng(),searchModel.getFormatted_address());

            if (searchModel.getOpening_hours()!=null)
            {
                placeModel.setOpenNow(searchModel.getOpening_hours().isOpen_now());
            }else
            {
                placeModel.setOpenNow(false);

            }
            returnedList.add(placeModel);
        }
        return returnedList;
    }
    private void updateSearchAdapter() {
        if (queryModelList.size()>0)
        {
            if (searchRecentAdapter==null)
            {
                searchRecentAdapter = new SearchRecentAdapter(queryModelList, activity, this);
                recViewQueries.setAdapter(searchRecentAdapter);
            }else
            {
                searchRecentAdapter.notifyDataSetChanged();

            }

        }
    }


    public void setQueryItemData(QueryModel queryModel) {
        query = queryModel.getKeyword();
        edt_search.setText(query);
        image_icon_delete.setVisibility(View.VISIBLE);
        Search();
    }

    public void setItemData(NearbyModel nearbyModel) {

        PlaceModel placeModel = new PlaceModel(nearbyModel.getId(),nearbyModel.getPlace_id(),nearbyModel.getName(),nearbyModel.getIcon(),nearbyModel.getRating(),nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng(),nearbyModel.getVicinity());
        if (nearbyModel.getOpening_hours()!=null)
        {
            placeModel.setOpenNow(nearbyModel.getOpening_hours().isOpen_now());

        }
        activity.DisplayFragmentStoreDetails(placeModel);
    }

    private String getUserAddress(double lat,double lng)
    {
        String user_address = "";

        Geocoder geocoder = new Geocoder(activity,new Locale(current_language));
        try {
            List<Address> addressList = geocoder.getFromLocation(lat,lng,1);
            if (addressList!=null&&addressList.size()>0)
            {
                Address address = addressList.get(0);
                if (address!=null)
                {
                    if (address.getLocality()!=null)
                    {
                        if (address.getSubAdminArea()!=null)
                        {
                            user_address = address.getSubAdminArea();

                        }else {
                            user_address = address.getLocality();

                        }


                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user_address;
    }

    private List<NearbyModel> sortData(List<NearbyModel> nearbylList) {

        List<NearbyModel> nearbyModelList = new ArrayList<>();
        for (NearbyModel nearbyModel :nearbylList)
        {
            double distance = SphericalUtil.computeDistanceBetween(new LatLng(lat,lng),new LatLng(nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng()));
            nearbyModel.setDistance(distance);
            nearbyModelList.add(nearbyModel);
        }

        Collections.sort(nearbyModelList, new Comparator<NearbyModel>() {
            @Override
            public int compare(NearbyModel o1, NearbyModel o2) {
                if (o1.getDistance()>o2.getDistance())
                {
                    return 1;
                }else if (o1.getDistance()<o2.getDistance())
                {
                    return -1;
                }else
                {
                    return 0;
                }
            }
        });
        return nearbyModelList;
    }

}
