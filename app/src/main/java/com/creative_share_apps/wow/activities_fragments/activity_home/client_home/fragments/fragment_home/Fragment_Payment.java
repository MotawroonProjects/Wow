package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.share.Common;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Payment extends Fragment {
    private ClientHomeActivity activity;
    private LinearLayout ll_back;
    private ImageView arrow,image_send,transfer_image,transfer_icon;
    private EditText edt_name,edt_bank_name,edt_account_number,edt_amount;
    private FrameLayout fl_transfer_image;
    private String current_language;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1;
    private Uri imgUri1 = null;
    private int selectedType = 0;
    private UserModel userModel;
    private UserSingleTone userSingleTone;

    public static Fragment_Payment newInstance() {
        return new Fragment_Payment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_payment,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);


        }
        ll_back = view.findViewById(R.id.ll_back);


        image_send = view.findViewById(R.id.image_send);
        transfer_image = view.findViewById(R.id.transfer_image);
        transfer_icon = view.findViewById(R.id.transfer_icon);
        edt_name = view.findViewById(R.id.edt_name);
        edt_bank_name = view.findViewById(R.id.edt_bank_name);
        edt_account_number = view.findViewById(R.id.edt_account_number);
        edt_amount = view.findViewById(R.id.edt_amount);

        fl_transfer_image = view.findViewById(R.id.fl_transfer_image);


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        fl_transfer_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ1);
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        updateUI();
    }

    private void updateUI() {
        if (userModel!=null)
        {
            edt_name.setText(userModel.getData().getUser_full_name());
        }

    }

    private void CheckData() {
        String m_name = edt_name.getText().toString().trim();
        String m_bank_name = edt_bank_name.getText().toString().trim();
        String m_account_number = edt_account_number.getText().toString().trim();
        String m_amount= edt_amount.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name) &&
                !TextUtils.isEmpty(m_bank_name)&&
                !TextUtils.isEmpty(m_account_number)&&
                !TextUtils.isEmpty(m_amount)&&
                imgUri1!=null


        )
        {
            edt_name.setError(null);
            edt_bank_name.setError(null);
            edt_account_number.setError(null);
            edt_amount.setError(null);

            Common.CloseKeyBoard(activity,edt_name);
            activity.Payment(m_name,m_bank_name,m_account_number,m_amount,imgUri1);

        }else
        {
            if (TextUtils.isEmpty(m_name))
            {
                edt_name.setError(getString(R.string.field_req));

            }else
            {
                edt_name.setError(null);

            }

            if (TextUtils.isEmpty(m_bank_name))
            {
                edt_bank_name.setError(getString(R.string.field_req));

            }else
            {
                edt_bank_name.setError(null);

            }

            if (TextUtils.isEmpty(m_account_number))
            {
                edt_account_number.setError(getString(R.string.field_req));

            }else
            {
                edt_account_number.setError(null);

            }

            if (TextUtils.isEmpty(m_amount))
            {
                edt_amount.setError(getString(R.string.field_req));

            }else
            {
                edt_amount.setError(null);

            }

            if (imgUri1==null)
            {
                Toast.makeText(activity, R.string.choose_identity_card_image, Toast.LENGTH_SHORT).show();
            }

           
        }
    }

    private void CreateImageAlertDialog(final int img_req)
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image,null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);



        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 2;
                Check_CameraPermission(img_req);

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 1;
                CheckReadPermission(img_req);



            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }
    private void CheckReadPermission(int img_req)
    {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1,img_req);
        }
    }

    private void Check_CameraPermission(int img_req)
    {
        if (ContextCompat.checkSelfPermission(activity,camera_permission)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(activity,write_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{camera_permission,write_permission},img_req);
        }else
        {
            SelectImage(2,img_req);

        }

    }
    private void SelectImage(int type,int img_req) {

        Intent intent = new Intent();

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
            startActivityForResult(intent,img_req);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,img_req);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {

            if (selectedType ==1)
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null)
        {
            if (selectedType == 1)
            {
                imgUri1 = data.getData();
                transfer_image.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri1));
                Picasso.with(activity).load(file).fit().into(transfer_icon);
            }else if (selectedType ==2)
            {
                transfer_image.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                if (imgUri1 != null) {
                    String path = Common.getImagePath(activity, imgUri1);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(transfer_icon);

                    } else {
                        Picasso.with(activity).load(imgUri1).fit().into(transfer_icon);

                    }
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
