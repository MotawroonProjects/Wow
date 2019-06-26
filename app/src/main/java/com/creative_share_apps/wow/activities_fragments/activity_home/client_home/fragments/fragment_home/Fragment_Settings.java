package com.creative_share_apps.wow.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creative_share_apps.wow.R;
import com.creative_share_apps.wow.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creative_share_apps.wow.preferences.Preferences;
import com.creative_share_apps.wow.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Settings extends Fragment {

    private ClientHomeActivity activity;
    private ConstraintLayout cons_back, cons_complains, cons_edit_profile, cons_language, cons_terms, cons_privacy, cons_rate, cons_about, cons_payment, cons_not_tune,cons_banks;
    private ImageView arrow_back, arrow1, arrow2, arrow3, arrow4, arrow5, arrow6, arrow7, arrow8, arrow9, arrow10;
    private String current_language;
    private String[] language_array;
    private int tune = 1, tune_raw;
    private MediaPlayer mp;
    private Preferences preferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Settings newInstance() {
        return new Fragment_Settings();
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        arrow_back = view.findViewById(R.id.arrow_back);
        arrow1 = view.findViewById(R.id.arrow1);
        arrow2 = view.findViewById(R.id.arrow2);
        arrow3 = view.findViewById(R.id.arrow3);
        arrow4 = view.findViewById(R.id.arrow4);
        arrow5 = view.findViewById(R.id.arrow5);
        arrow6 = view.findViewById(R.id.arrow6);
        arrow7 = view.findViewById(R.id.arrow7);
        arrow8 = view.findViewById(R.id.arrow8);
        arrow9 = view.findViewById(R.id.arrow9);
        arrow10 = view.findViewById(R.id.arrow10);

        language_array = new String[]{"English", "العربية"};

        if (current_language.equals("ar")) {
            arrow_back.setImageResource(R.drawable.ic_right_arrow);

            arrow1.setImageResource(R.drawable.ic_left_arrow);
            arrow2.setImageResource(R.drawable.ic_left_arrow);
            arrow3.setImageResource(R.drawable.ic_left_arrow);
            arrow4.setImageResource(R.drawable.ic_left_arrow);
            arrow5.setImageResource(R.drawable.ic_left_arrow);
            arrow6.setImageResource(R.drawable.ic_left_arrow);
            arrow7.setImageResource(R.drawable.ic_left_arrow);
            arrow8.setImageResource(R.drawable.ic_left_arrow);
            arrow9.setImageResource(R.drawable.ic_left_arrow);
            arrow10.setImageResource(R.drawable.ic_left_arrow);

        } else {
            arrow_back.setImageResource(R.drawable.ic_left_arrow);
            arrow1.setImageResource(R.drawable.ic_right_arrow);
            arrow2.setImageResource(R.drawable.ic_right_arrow);
            arrow3.setImageResource(R.drawable.ic_right_arrow);
            arrow4.setImageResource(R.drawable.ic_right_arrow);
            arrow5.setImageResource(R.drawable.ic_right_arrow);
            arrow6.setImageResource(R.drawable.ic_right_arrow);
            arrow7.setImageResource(R.drawable.ic_right_arrow);
            arrow8.setImageResource(R.drawable.ic_right_arrow);
            arrow9.setImageResource(R.drawable.ic_right_arrow);
            arrow10.setImageResource(R.drawable.ic_right_arrow);


        }

        cons_banks = view.findViewById(R.id.cons_banks);
        cons_back = view.findViewById(R.id.cons_back);
        cons_complains = view.findViewById(R.id.cons_complains);
        cons_edit_profile = view.findViewById(R.id.cons_edit_profile);
        cons_language = view.findViewById(R.id.cons_language);
        cons_terms = view.findViewById(R.id.cons_terms);
        cons_privacy = view.findViewById(R.id.cons_privacy);
        cons_rate = view.findViewById(R.id.cons_rate);
        cons_about = view.findViewById(R.id.cons_about);
        cons_payment = view.findViewById(R.id.cons_payment);
        cons_not_tune = view.findViewById(R.id.cons_not_tune);

        cons_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
            }
        });

        cons_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPTERMS);
            }
        });

        cons_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPPRIVACY);
            }
        });

        cons_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPABOUT);
            }
        });

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        cons_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLanguageDialog();
            }
        });

        cons_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentEditProfile();
            }
        });
        cons_complains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentSendComplain();
            }
        });

        cons_banks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentBankAccount();
            }
        });

        cons_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentPayment();
            }
        });

        cons_not_tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTuneDialog();
            }
        });
    }


    private void CreateLanguageDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_language, null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(language_array.length - 1);
        numberPicker.setDisplayedValues(language_array);
        numberPicker.setWrapSelectorWheel(false);
        if (current_language.equals("ar")) {
            numberPicker.setValue(1);

        } else {
            numberPicker.setValue(0);

        }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int pos = numberPicker.getValue();
                if (pos == 0) {
                    activity.RefreshActivity("en");
                } else {
                    activity.RefreshActivity("ar");

                }

            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setView(view);
        dialog.show();
    }

    private void CreateTuneDialog() {


        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .create();

        int savedTune = preferences.get_tune(activity);


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_not_rings, null);
        final RadioButton rb1 = view.findViewById(R.id.rb1);
        final RadioButton rb2 = view.findViewById(R.id.rb2);
        final RadioButton rb3 = view.findViewById(R.id.rb3);
        final RadioButton rb4 = view.findViewById(R.id.rb4);

        if (savedTune == 1) {
            rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

        } else if (savedTune == 2) {
            rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

        } else if (savedTune == 3) {
            rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

        } else if (savedTune == 4) {
            rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

        }
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tune = 1;
                tune_raw = R.raw.not;
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    playTune(tune_raw);
                } else {
                    playTune(tune_raw);

                }

                rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tune = 2;
                tune_raw = R.raw.not1;
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    playTune(tune_raw);
                } else {
                    playTune(tune_raw);

                }
                rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tune = 3;
                tune_raw = R.raw.not2;
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    playTune(tune_raw);
                } else {
                    playTune(tune_raw);

                }
                rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

            }
        });

        rb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tune = 4;
                tune_raw = R.raw.not3;
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    playTune(tune_raw);
                } else {
                    playTune(tune_raw);

                }
                rb1.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb3.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                rb4.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

            }
        });


        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                }
                preferences.create_update_tune(activity, tune);
                dialog.dismiss();
            }
        });


        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private void playTune(int tune) {
        mp = MediaPlayer.create(activity, tune);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.start();
    }

}
