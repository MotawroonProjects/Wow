package com.creativeshare.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.creativeshare.wow.R;
import com.creativeshare.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.wow.models.OrderSpareDataModel;
import com.creativeshare.wow.models.UserModel;
import com.creativeshare.wow.remote.Api;
import com.creativeshare.wow.share.Common;
import com.creativeshare.wow.singletone.UserSingleTone;
import com.creativeshare.wow.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Add_Spare extends Fragment {

    private static final String TAG1="LAT";
    private static final String TAG2="LNG";

    private LinearLayout ll_back;
    private ImageView arrow,image,image_icon_upload;
    private EditText edt_city,edt_brand,edt_type,edt_year,edt_name,edt_quantity;
    private RadioButton rb_client_location,rb_postal;
    private Button btn_send;
    private String current_language;
    private ClientHomeActivity activity;
    private final String read_perm = Manifest.permission.READ_EXTERNAL_STORAGE,WRITE_PERM =Manifest.permission.WRITE_EXTERNAL_STORAGE,camera_permission = Manifest.permission.CAMERA;
    private final int IMG1=1,IMG2 = 2;
    private Uri uri;
    private int send_by = 0;
    private double lat = 0.0,lng=0.0;
    private UserSingleTone userSingleTone;
    private UserModel userModel;

    public static Fragment_Add_Spare newInstance(double lat,double lng)
    {
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG1,lat);
        bundle.putDouble(TAG2,lng);
        Fragment_Add_Spare fragment_add_spare = new Fragment_Add_Spare();
        fragment_add_spare.setArguments(bundle);
        return fragment_add_spare;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_spare,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);

        image = view.findViewById(R.id.image);
        image_icon_upload = view.findViewById(R.id.image_icon_upload);
        edt_city = view.findViewById(R.id.edt_city);
        edt_brand = view.findViewById(R.id.edt_brand);
        edt_type = view.findViewById(R.id.edt_type);
        edt_year = view.findViewById(R.id.edt_year);
        edt_name = view.findViewById(R.id.edt_name);
        edt_quantity = view.findViewById(R.id.edt_quantity);
        rb_client_location = view.findViewById(R.id.rb_client_location);
        rb_postal = view.findViewById(R.id.rb_postal);
        btn_send = view.findViewById(R.id.btn_send);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        rb_client_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_by = 2;
            }
        });

        rb_postal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_by = 1;
            }
        });

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog();
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            lat = bundle.getDouble(TAG1,0.0);
            lng = bundle.getDouble(TAG2,0.0);

        }

    }

    private void checkData() {

        String m_city = edt_city.getText().toString().trim();
        String m_brand = edt_brand.getText().toString().trim();
        String m_type = edt_type.getText().toString().trim();
        String m_year = edt_year.getText().toString().trim();
        String m_name = edt_name.getText().toString().trim();
        String m_quantity = edt_quantity.getText().toString().trim();


        if (!TextUtils.isEmpty(m_city)&&
                !TextUtils.isEmpty(m_brand)&&
                !TextUtils.isEmpty(m_type)&&
                !TextUtils.isEmpty(m_year)&&
                !TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_quantity)&&
                send_by!=0&&
                uri!=null
        )
        {
            edt_city.setError(null);
            edt_brand.setError(null);
            edt_type.setError(null);
            edt_year.setError(null);
            edt_name.setError(null);
            edt_quantity.setError(null);
            Common.CloseKeyBoard(activity,edt_city);
            Send(m_city,m_brand,m_type,m_year,m_name,m_quantity);

        }else
            {
                if (TextUtils.isEmpty(m_city))
                {
                    edt_city.setError(getString(R.string.field_req));
                }else
                    {
                        edt_city.setError(null);

                    }

                if (TextUtils.isEmpty(m_brand))
                {
                    edt_brand.setError(getString(R.string.field_req));
                }else
                {
                    edt_brand.setError(null);

                }

                if (TextUtils.isEmpty(m_type))
                {
                    edt_type.setError(getString(R.string.field_req));
                }else
                {
                    edt_type.setError(null);

                }

                if (TextUtils.isEmpty(m_year))
                {
                    edt_year.setError(getString(R.string.field_req));
                }else
                {
                    edt_year.setError(null);

                }

                if (TextUtils.isEmpty(m_name))
                {
                    edt_name.setError(getString(R.string.field_req));
                }else
                {
                    edt_name.setError(null);

                }

                if (TextUtils.isEmpty(m_quantity))
                {
                    edt_quantity.setError(getString(R.string.field_req));
                }else
                {
                    edt_quantity.setError(null);

                }

                if (send_by ==0)
                {
                    Toast.makeText(activity, R.string.Choose_delivery_method, Toast.LENGTH_SHORT).show();
                }

                if (uri == null)
                {
                    Toast.makeText(activity, R.string.ch_car_form, Toast.LENGTH_SHORT).show();

                }
            }

    }

    private void Send(String m_city, String m_brand, String m_type, String m_year, String m_name, String m_quantity) {

        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(lng));

        RequestBody city_part = Common.getRequestBodyText(m_city);
        RequestBody brand_part = Common.getRequestBodyText(m_brand);
        RequestBody type_part = Common.getRequestBodyText(m_type);
        RequestBody name_part = Common.getRequestBodyText(m_name);
        RequestBody year_part = Common.getRequestBodyText(m_year);
        RequestBody quantity_part = Common.getRequestBodyText(m_quantity);
        RequestBody send_by_part = Common.getRequestBodyText(String.valueOf(send_by));

        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"car_image");


        Api.getService(Tags.base_url)
                .addSparePart(user_id_part,city_part,lat_part,lng_part,brand_part,type_part,year_part,name_part,quantity_part,send_by_part,image_part)
                .enqueue(new Callback<OrderSpareDataModel.OrderSpare>() {
                    @Override
                    public void onResponse(Call<OrderSpareDataModel.OrderSpare> call, Response<OrderSpareDataModel.OrderSpare> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null)
                            {
                                CreateAlertDialog(response.body());
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
                    public void onFailure(Call<OrderSpareDataModel.OrderSpare> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });


    }

    private void CreateImageAlertDialog()
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image, null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_CameraPermission();

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_ReadPermission();


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }
    private void Check_ReadPermission()
    {
        if (ContextCompat.checkSelfPermission(activity, read_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{read_perm}, IMG1);
        } else {
            select_photo(1);
        }
    }
    private void Check_CameraPermission()
    {
        if (ContextCompat.checkSelfPermission(activity, WRITE_PERM) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, WRITE_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, WRITE_PERM}, IMG2);
        } else {
            select_photo(2);

        }

    }
    private void select_photo(int type)
    {

        Intent  intent = new Intent();

        if (type == 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,IMG1);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMG2);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data != null) {
            image_icon_upload.setVisibility(View.GONE);
            uri = data.getData();

            String path = Common.getImagePath(activity, uri);
            if (path != null) {
                Picasso.with(activity).load(new File(path)).fit().into(image);

            } else {
                Picasso.with(activity).load(uri).fit().into(image);

            }
        } else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data != null) {
            image_icon_upload.setVisibility(View.GONE);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                String path = Common.getImagePath(activity, uri);

                if (path != null) {
                    Picasso.with(activity).load(new File(path)).fit().into(image);

                } else {
                    Picasso.with(activity).load(uri).fit().into(image);

                }
            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    select_photo(IMG1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == IMG2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    select_photo(2);

                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }


    public  void CreateAlertDialog(final OrderSpareDataModel.OrderSpare orderSpare)
    {


        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_order_id,null);
        Button btn_follow = view.findViewById(R.id.btn_follow);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(getString(R.string.order_sent_successfully_order_number_is)+" #"+orderSpare.getOrder_id());
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.Back();
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.DisplayFragmentSpareOrderDetails(orderSpare);

                            }
                        },3);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.Back();


            }
        });

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }
}
