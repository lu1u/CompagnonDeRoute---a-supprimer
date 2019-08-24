package com.lpi.compagnonderoute;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechIntentService;
import com.lpi.compagnonderoute.utils.Preferences;

public class ParametresActivity extends AppCompatActivity {


    /*******************************************************************************************************************
     * Demarre l'activite
     *******************************************************************************************************************/
    public static void start(@NonNull final Activity context)
    {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

	    //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, Utils.getTheme(context));
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_parametres, null);

        final Switch swForcerHautParleur = dialogView.findViewById(R.id.switchHautParleur);
        final Switch swVolumeParDefaut = dialogView.findViewById(R.id.switchVolumeParDefaut);
        final SeekBar sbVolume = dialogView.findViewById(R.id.seekBarVolume);
        final Switch swPip = dialogView.findViewById(R.id.switchPictureInPicture);
        final Preferences prefs = Preferences.getInstance(context);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Initialisation de l'interface
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        swForcerHautParleur.setChecked(prefs.isForcerHautParleur());

        if (prefs.isVolumeDefaut()) {
            swVolumeParDefaut.setChecked(true);
            sbVolume.setVisibility(View.GONE);
        } else {
            swVolumeParDefaut.setChecked(false);
            sbVolume.setVisibility(View.VISIBLE);
        }

	    sbVolume.setMax(TextToSpeechIntentService.getMaxVolume(context));
        sbVolume.setProgress(prefs.getVolume());

        swPip.setChecked(prefs.isActiverModePip());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Listeners
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        swForcerHautParleur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setForcerHautParleur(isChecked);
                prefs.flush(context);

            }
        });

        swVolumeParDefaut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setVolumeDefaut(isChecked);
                prefs.flush(context);
                sbVolume.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
                if ( isChecked )
	                TextToSpeechIntentService.annonce(context, R.string.default_volume);
                else
	                TextToSpeechIntentService.annonce(context, R.string.volume, prefs.getVolume());
            }
        });

        swPip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setActiverModePip(isChecked);
                prefs.flush(context);
            }
        });
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                                             {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                                                 {
                                                     if ( fromUser)
                                                     {
                                                         prefs.setVolume(progress);
                                                         prefs.flush(context);
	                                                     TextToSpeechIntentService.annonce(context, R.string.volume, progress);
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

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Afficher la fenetre
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
