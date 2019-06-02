package com.lpi.compagnonderoute;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.Preferences;

public class ParametresActivity extends AppCompatActivity {
    static Switch _swForcerHautParleur;
    static Switch _swVolumeParDefaut;
    static SeekBar _sbVolume;

    /***
     * Demarre l'activite
     */
    public static void start(Activity activity) {
        //Intent intent = new Intent(context, ParametresActivity.class);
        //context.startActivity(intent);
        final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("ResourceType") View dialogView = inflater.inflate(R.layout.activity_parametres, null);


        getControlsFromIds(dialogView);
        majUI(activity);
        setListeners(activity);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_parametres);
//        getControlsFromIds();
//        setListeners();
//        majUI();
//    }

    private static void setListeners(final Context context) {
        final Preferences prefs = Preferences.getInstance(context);
        _swForcerHautParleur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setForcerHautParleur(isChecked);
                prefs.flush(context);

            }
        });

        _swVolumeParDefaut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setVolumeDefaut(isChecked);
                prefs.flush(context);
                _sbVolume.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
                if ( isChecked )
                    TextToSpeechManager.getInstance(context).annonce(context, R.string.volume_du_systeme);
                else
                    TextToSpeechManager.getInstance(context).annonce(context, R.string.volume, prefs.getVolume());
            }
        });

        _sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                                             {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                                                 {
                                                     if ( fromUser)
                                                     {
                                                         prefs.setVolume(progress);
                                                         prefs.flush(context);
                                                         TextToSpeechManager.getInstance(context).annonce(context, R.string.volume, progress);
                                                     }
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }

                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {

                                                 }
                                             }
        );
    }

    private static void majUI(Context context) {
        Preferences prefs = Preferences.getInstance(context);
        _swForcerHautParleur.setChecked(prefs.isForcerHautParleur());

        if (prefs.isVolumeDefaut()) {
            _swVolumeParDefaut.setChecked(true);
            _sbVolume.setVisibility(View.GONE);
        } else {
            _swVolumeParDefaut.setChecked(false);
            _sbVolume.setVisibility(View.VISIBLE);
        }

        _sbVolume.setMax(TextToSpeechManager.getInstance(context).getMaxVolume());
        _sbVolume.setProgress(prefs.getVolume());
    }

    private static void getControlsFromIds(View view) {
        _swForcerHautParleur = view.findViewById(R.id.switchHautParleur);
        _swVolumeParDefaut = view.findViewById(R.id.switchVolumeParDefaut);
        _sbVolume = view.findViewById(R.id.seekBarVolume);
    }
}
