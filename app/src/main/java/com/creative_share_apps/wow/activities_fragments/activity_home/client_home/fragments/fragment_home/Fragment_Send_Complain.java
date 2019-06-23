package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.models.SocialMediaModel;
import com.creative_share_apps.wow.models.UserModel;
import com.creative_share_apps.wow.remote.Api;
import com.creative_share_apps.wow.share.Common;
import com.creative_share_apps.wow.singletone.UserSingleTone;
import com.creative_share_apps.wow.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Send_Complain extends Fragment {

    private ImageView image_back,image_whatsapp;
    private LinearLayout ll_back;
    private EditText edt_name, edt_email, edt_phone, edt_msg;
    private Button btn_send;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private ClientHomeActivity activity;
    private String current_language;
    private SocialMediaModel socialMediaModel;


    public static Fragment_Send_Complain newInstance()
    {
        return new Fragment_Send_Complain();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_complain, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        image_back = view.findViewById(R.id.image_back);

        if (current_language.equals("ar")) {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        }

        ll_back = view.findViewById(R.id.ll_back);

        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_msg = view.findViewById(R.id.edt_msg);
        btn_send = view.findViewById(R.id.btn_send);
        image_whatsapp = view.findViewById(R.id.image_whatsapp);

        updateUI();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        image_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socialMediaModel!=null&&!socialMediaModel.getCompany_whatsapp().equals("0")) {


                    String phone = socialMediaModel.getCompany_whatsapp();
                    if (!phone.startsWith("966"))
                    {
                        phone = "966"+phone;
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("whatsapp://send?phone="+phone)));

                } else {
                    CreateAlertDialog();

                }
            }
        });
    }

    private void updateUI() {
        if (userModel != null) {
            edt_name.setText(userModel.getData().getUser_full_name());
            edt_email.setText(userModel.getData().getUser_email());

            edt_phone.setText(String.format("%s%s", userModel.getData().getUser_phone_code().replaceFirst("00","+"), userModel.getData().getUser_phone()));
        }
        getSocialMedia();

    }

    private void getSocialMedia() {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getSocialMedia()
                .enqueue(new Callback<SocialMediaModel>() {
                    @Override
                    public void onResponse(Call<SocialMediaModel> call, Response<SocialMediaModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            socialMediaModel = response.body();
                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SocialMediaModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void CheckData() {
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();
        String m_phone = edt_phone.getText().toString().trim();
        String m_msg = edt_msg.getText().toString().trim();

        if (!TextUtils.isEmpty(m_name) &&
                !TextUtils.isEmpty(m_email) &&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches() &&
                !TextUtils.isEmpty(m_phone) &&
                !TextUtils.isEmpty(m_msg)

        ) {
            edt_name.setError(null);
            edt_email.setError(null);
            edt_phone.setError(null);
            edt_msg.setError(null);
            Common.CloseKeyBoard(activity, edt_name);
            SendComplain(m_name, m_email, m_phone, m_msg);
        } else {
            if (TextUtils.isEmpty(m_name)) {
                edt_name.setError(getString(R.string.field_req));
            } else {
                edt_name.setError(null);

            }

            if (TextUtils.isEmpty(m_email)) {
                edt_email.setError(getString(R.string.field_req));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
                edt_email.setError(getString(R.string.inv_email));

            } else {
                edt_email.setError(null);

            }

            if (TextUtils.isEmpty(m_phone)) {
                edt_phone.setError(getString(R.string.field_req));
            } else {
                edt_phone.setError(null);

            }

            if (TextUtils.isEmpty(m_msg)) {
                edt_msg.setError(getString(R.string.field_req));
            } else {
                edt_msg.setError(null);

            }

        }


    }

    private void SendComplain(String m_name, String m_email, String m_phone, String m_msg) {

        if (m_phone.startsWith("+"))
        {
            m_phone = m_phone.replace("+","00");
        }
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .send_complain(m_name, m_phone, m_email, m_msg)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void CreateAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_sign, null);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.app_name));
        tv_msg.setText(R.string.no_media_available);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }
}
