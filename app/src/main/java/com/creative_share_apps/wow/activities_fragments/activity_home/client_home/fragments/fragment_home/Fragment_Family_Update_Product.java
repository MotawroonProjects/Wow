package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.models.DepartmentDataModel;
import com.creative_share_apps.wow.models.Department_Model;
import com.creative_share_apps.wow.models.ProductsDataModel;
import com.creative_share_apps.wow.models.SpinnerAdapter;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.remote.Api;
import com.creative_share_apps.wow.share.Common;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.creative_share_apps.wow.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Family_Update_Product extends Fragment {
    private static final String TAG = "PRODUCT";

    private LinearLayout ll_back;
    private ImageView arrow, image, image_add;
    private EditText edt_name_ar, edt_name_en, edt_details_ar, edt_details_en, edt_note, edt_price;
    private Button btn_update;
    private Spinner spinner;
    private List<Department_Model> department_modelList;
    private String current_language;
    private ClientHomeActivity activity;
    private final String read_perm = Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE, camera_permission = Manifest.permission.CAMERA;
    private final int IMG1 = 1, IMG2 = 2;
    private Uri uri;
    private String department_id = "";
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private SpinnerAdapter spinnerAdapter;
    private ProductsDataModel.ProductModel productModel;

    public static Fragment_Family_Update_Product newInstance(ProductsDataModel.ProductModel productModel) {
        Fragment_Family_Update_Product fragment_family_update_product = new Fragment_Family_Update_Product();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, productModel);
        fragment_family_update_product.setArguments(bundle);
        return fragment_family_update_product;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_update_product, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Department_Model department_model = new Department_Model("-1", "إختر", "Choose", "-1");
        department_modelList = new ArrayList<>();
        department_modelList.add(department_model);
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);

        image = view.findViewById(R.id.image);
        image_add = view.findViewById(R.id.image_add);

        edt_name_ar = view.findViewById(R.id.edt_name_ar);
        edt_name_en = view.findViewById(R.id.edt_name_en);
        edt_details_ar = view.findViewById(R.id.edt_details_ar);
        edt_details_en = view.findViewById(R.id.edt_details_en);
        edt_note = view.findViewById(R.id.edt_note);
        edt_price = view.findViewById(R.id.edt_price);
        spinner = view.findViewById(R.id.spinner);
        btn_update = view.findViewById(R.id.btn_update);

        //////////////////////////////////////////////////////
        spinnerAdapter = new SpinnerAdapter(activity, department_modelList);
        spinner.setAdapter(spinnerAdapter);
        //////////////////////////////////////////////////////

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
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

        image_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAddDeptAlertDialog();
            }
        });

        getDepartments();

        Bundle bundle = getArguments();
        if (bundle != null) {
            productModel = (ProductsDataModel.ProductModel) bundle.getSerializable(TAG);
            updateUI(productModel);
        }

    }

    private void updateUI(ProductsDataModel.ProductModel productModel) {

        edt_name_ar.setText(productModel.getAr_title_pro());
        edt_name_en.setText(productModel.getEn_title_pro());
        edt_details_ar.setText(productModel.getAr_details_pro());
        edt_details_en.setText(productModel.getEn_details_pro());
        edt_price.setText(productModel.getPrice());
        if (!productModel.getNotes().equals("0"))
        {
            edt_note.setText(productModel.getNotes());

        }
        Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL + productModel.getImage())).fit().into(image);

    }

    private void getDepartments() {
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getAllFamilyDepts(userModel.getData().getUser_id())
                .enqueue(new Callback<DepartmentDataModel>() {
                    @Override
                    public void onResponse(Call<DepartmentDataModel> call, Response<DepartmentDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getData() != null) {
                                department_modelList.addAll(response.body().getData());
                                spinnerAdapter.notifyDataSetChanged();

                                int pos = getProductDepartmentPos(productModel);
                                if (pos != -1) {
                                    department_id = department_modelList.get(pos).getId_department();
                                    spinner.setSelection(pos);
                                }
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
                    public void onFailure(Call<DepartmentDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    department_id = "";
                } else {
                    department_id = department_modelList.get(position).getId_department();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getProductDepartmentPos(ProductsDataModel.ProductModel productModel) {
        int pos = -1;
        for (int i = 0; i < department_modelList.size() - 1; i++) {
            if (productModel.getDep_id_fk().equals(department_modelList.get(i).getId_department())) {
                pos = i;
                return pos;

            }
        }
        return pos;
    }

    private void checkData() {

        String m_name_ar = edt_name_ar.getText().toString().trim();
        String m_name_en = edt_name_en.getText().toString().trim();

        String m_details_ar = edt_details_ar.getText().toString().trim();
        String m_details_en = edt_details_en.getText().toString().trim();

        String m_price = edt_price.getText().toString().trim();
        String m_note = edt_note.getText().toString().trim();

        String note = "";

        if (!productModel.getNotes().equals("0"))
        {
            note = productModel.getNotes();
        }

        if (m_name_ar.equals(productModel.getAr_title_pro()) &&
                m_name_en.equals(productModel.getEn_title_pro()) &&
                m_details_ar.equals(productModel.getAr_details_pro()) &&
                m_details_en.equals(productModel.getEn_details_pro()) &&
                m_price.equals(productModel.getPrice()) &&
                m_note.equals(note) &&
                uri!=null


        )
        {
            Toast.makeText(activity, getString(R.string.no_changes), Toast.LENGTH_LONG).show();
            
        }else 
            {
                if (!TextUtils.isEmpty(m_name_ar) &&
                        !TextUtils.isEmpty(m_details_ar) &&
                        !TextUtils.isEmpty(m_price) &&
                        !TextUtils.isEmpty(department_id)

                ) {
                    edt_name_ar.setError(null);
                    edt_details_ar.setError(null);
                    edt_price.setError(null);
                    Common.CloseKeyBoard(activity, edt_name_ar);

                    if (uri != null) {
                        UpdateProductWithImage(m_name_ar, m_name_en, m_details_ar, m_details_en, m_price, m_note);

                    } else {
                        UpdateProductWithoutImage(m_name_ar, m_name_en, m_details_ar, m_details_en, m_price, m_note);
                    }

                } else {
                    if (TextUtils.isEmpty(m_name_ar)) {
                        edt_name_ar.setError(getString(R.string.field_req));
                    } else {
                        edt_name_ar.setError(null);

                    }

                    if (TextUtils.isEmpty(m_details_ar)) {
                        edt_details_ar.setError(getString(R.string.field_req));
                    } else {
                        edt_details_ar.setError(null);

                    }

                    if (TextUtils.isEmpty(m_price)) {
                        edt_price.setError(getString(R.string.field_req));
                    } else {
                        edt_price.setError(null);

                    }

                    if (TextUtils.isEmpty(department_id)) {
                        Toast.makeText(activity, R.string.ch_dept, Toast.LENGTH_SHORT).show();
                    }


                }

            }

           
    }

    private void UpdateProductWithImage(String m_name_ar, String m_name_en, String m_details_ar, String m_details_en, String m_price, String m_note) {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        RequestBody name_en_part;
        RequestBody details_en_part;

        if (!TextUtils.isEmpty(m_name_en)) {
            name_en_part = Common.getRequestBodyText(m_name_en);

        } else {
            name_en_part = Common.getRequestBodyText(m_name_ar);

        }

        if (!TextUtils.isEmpty(m_details_en)) {
            details_en_part = Common.getRequestBodyText(m_details_en);

        } else {
            details_en_part = Common.getRequestBodyText(m_details_en);

        }
        RequestBody product_id_part = Common.getRequestBodyText(productModel.getId_product());
        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody name_ar_part = Common.getRequestBodyText(m_name_ar);
        RequestBody details_ar_part = Common.getRequestBodyText(m_details_ar);
        RequestBody price_part = Common.getRequestBodyText(m_price);
        RequestBody note_part = Common.getRequestBodyText(m_note);
        RequestBody department_id_part = Common.getRequestBodyText(department_id);
        MultipartBody.Part image_part = Common.getMultiPart(activity, uri, "image");


        Api.getService(Tags.base_url)
                .updateFamilyProductWithImage(product_id_part,user_id_part, department_id_part, name_ar_part, name_en_part, price_part, details_ar_part, details_en_part, note_part, image_part)
                .enqueue(new Callback<ProductsDataModel.ProductModel>() {
                    @Override
                    public void onResponse(Call<ProductsDataModel.ProductModel> call, Response<ProductsDataModel.ProductModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            activity.onProductUpdated(response.body());
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
                    public void onFailure(Call<ProductsDataModel.ProductModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void UpdateProductWithoutImage(String m_name_ar, String m_name_en, String m_details_ar, String m_details_en, String m_price, String m_note) {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        RequestBody name_en_part;
        RequestBody details_en_part;

        if (!TextUtils.isEmpty(m_name_en)) {
            name_en_part = Common.getRequestBodyText(m_name_en);

        } else {
            name_en_part = Common.getRequestBodyText(m_name_ar);

        }

        if (!TextUtils.isEmpty(m_details_en)) {
            details_en_part = Common.getRequestBodyText(m_details_en);

        } else {
            details_en_part = Common.getRequestBodyText(m_details_en);

        }
        RequestBody product_id_part = Common.getRequestBodyText(productModel.getId_product());

        RequestBody user_id_part = Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody name_ar_part = Common.getRequestBodyText(m_name_ar);
        RequestBody details_ar_part = Common.getRequestBodyText(m_details_ar);
        RequestBody price_part = Common.getRequestBodyText(m_price);
        RequestBody note_part = Common.getRequestBodyText(m_note);
        RequestBody department_id_part = Common.getRequestBodyText(department_id);



        Api.getService(Tags.base_url)
                .updateFamilyProductWithoutImage(product_id_part,user_id_part, department_id_part, name_ar_part, name_en_part, price_part, details_ar_part, details_en_part, note_part)
                .enqueue(new Callback<ProductsDataModel.ProductModel>() {
                    @Override
                    public void onResponse(Call<ProductsDataModel.ProductModel> call, Response<ProductsDataModel.ProductModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            activity.onProductUpdated(response.body());
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
                    public void onFailure(Call<ProductsDataModel.ProductModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }


    private void CreateAddDeptAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_add_department, null);
        Button btn_add = view.findViewById(R.id.btn_add);
        final EditText edt_ar_name = view.findViewById(R.id.edt_ar_name);
        final EditText edt_en_name = view.findViewById(R.id.edt_en_name);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m_ar_name = edt_ar_name.getText().toString().trim();
                String m_en_name = edt_en_name.getText().toString().trim();

                if (!TextUtils.isEmpty(m_ar_name)) {
                    edt_ar_name.setError(null);
                    Common.CloseKeyBoard(activity, edt_ar_name);

                    if (!TextUtils.isEmpty(m_en_name)) {
                        AddDept(m_ar_name, m_en_name, dialog);
                    } else {
                        AddDept(m_ar_name, m_ar_name, dialog);

                    }

                } else {
                    edt_ar_name.setError(getString(R.string.field_req));
                }

            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private void AddDept(String m_ar_name, String m_en_name, final AlertDialog dialog) {
        final ProgressDialog progressDialog = Common.createProgressDialog(activity, getString(R.string.wait));
        progressDialog.show();

        Api.getService(Tags.base_url)
                .addDept(m_ar_name, m_en_name, userModel.getData().getUser_id())
                .enqueue(new Callback<Department_Model>() {
                    @Override
                    public void onResponse(Call<Department_Model> call, Response<Department_Model> response) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                department_modelList.add(response.body());
                                spinnerAdapter.notifyDataSetChanged();

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
                    public void onFailure(Call<Department_Model> call, Throwable t) {
                        try {
                            progressDialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void CreateImageAlertDialog() {

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

    private void Check_ReadPermission() {
        if (ContextCompat.checkSelfPermission(activity, read_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{read_perm}, IMG1);
        } else {
            select_photo(1);
        }
    }

    private void Check_CameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, WRITE_PERM) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, WRITE_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, WRITE_PERM}, IMG2);
        } else {
            select_photo(2);

        }

    }

    private void select_photo(int type) {

        Intent intent = new Intent();

        if (type == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, IMG1);

        } else if (type == 2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMG2);
            } catch (SecurityException e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.getData();

            String path = Common.getImagePath(activity, uri);
            if (path != null) {
                Picasso.with(activity).load(new File(path)).fit().into(image);

            } else {
                Picasso.with(activity).load(uri).fit().into(image);

            }
        } else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data != null) {
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

}
