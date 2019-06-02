package com.lpi.compagnonderoute;

import android.Manifest;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.service.CompagnonService;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.DialogAPropos;
import com.lpi.compagnonderoute.utils.ModalEditText;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.compagnonderoute.utils.Utils;
import com.lpi.reportlibrary.ReportActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.MODIFY_AUDIO_SETTINGS};

    private ImageButton btnStart, btnStop;
    private @Nullable
    RadioGroup rgAnnonceHeure, rgConseillerPause, rgLireSMS, rgRepondreSMS, rgRepondreAppels, rgAnnoncerAppels;
    private ImageButton btnReponseSMS, btnReponseAppels;
    private TextView tvMessage;

    @Nullable
    private Preferences _preferences;

    BroadcastReceiver _receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            String action = intent.getAction();
            if (Plannificateur.ACTION_MESSAGE_UI.equals(action))
            {
                if (tvMessage != null)
                {
                    String message = intent.getStringExtra(Plannificateur.EXTRA_MESSAGE_UI);
                    tvMessage.setText(message);
                }
            }
        }
    };

    IntentFilter _intentFilter = new IntentFilter(Plannificateur.ACTION_MESSAGE_UI);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.setTheme(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_compagnon);
        _preferences = Preferences.getInstance(this);

        getControlsFromIds();
        initListeners();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        demandePermissionsSiBesoin();

        registerReceiver(_receiver, _intentFilter);
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(_receiver);
        super.onDestroy();
    }

    /***
     * Retrouve les objets permettant de manipuler les controles
     */
    private void getControlsFromIds()
    {
        btnStart = findViewById(R.id.imageButtonStart);
        btnStop = findViewById(R.id.imageButtonStop);
        rgAnnonceHeure = findViewById(R.id.radiogroupAnnonceHeure);
        rgConseillerPause = findViewById(R.id.radiogroupConseilPause);
        rgLireSMS = findViewById(R.id.radiogroupLireSMS);
        rgRepondreSMS = findViewById(R.id.radiogroupRepondreSMS);
        rgAnnoncerAppels = findViewById(R.id.radiogroupAnnoncerAppels);
        rgRepondreAppels = findViewById(R.id.radiogroupRepondreAppels);
        btnReponseSMS = findViewById(R.id.imageButtonReponseSMS);
        btnReponseAppels = findViewById(R.id.imageButtonReponseAppels);
        tvMessage = findViewById(R.id.textViewStatus);
    }


    private void initListeners()
    {
        if (rgAnnonceHeure != null)
            rgAnnonceHeure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioButtonAnnonceJamais:
                            _preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.JAMAIS);
                            break;
                        case R.id.radioButtonAnnonceHeure:
                            _preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUTES_LES_HEURES);
                            break;
                        case R.id.radioButtonAnnonceDemi:
                            _preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUTES_LES_DEMI_HEURES);
                            break;
                        case R.id.radioButtonAnnonceQuart:
                            _preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUS_LES_QUARTS_D_HEURES);
                            break;
                    }

                    _preferences.flush(MainActivity.this);
                    Carillon.changeDelai(MainActivity.this);
                }
            });

        if (rgConseillerPause != null)
            rgConseillerPause.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioButtonPauseJamais:
                            _preferences.setConseillerPause(false);
                            break;
                        case R.id.radioButtonPause2Heures:
                            _preferences.setConseillerPause(true);
                            _preferences.setMinutesEntrePauses(120);
                            break;
                        case R.id.radioButtonPause1Heure:
                            _preferences.setConseillerPause(true);
                            _preferences.setMinutesEntrePauses(60);
                            break;
                        case R.id.radioButtonPauseDemiHeure:
                            _preferences.setConseillerPause(true);
                            _preferences.setMinutesEntrePauses(30);
                            break;
                    }
                    _preferences.flush(MainActivity.this);
                    Pause.changeDelai(MainActivity.this);

                }
            });

        if (rgLireSMS != null)
            rgLireSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioButtonSMSJamais:
                            _preferences.setLireSMS(Preferences.ENTRANT.JAMAIS);
                            break;
                        case R.id.radioButtonSMSToujours:
                            _preferences.setLireSMS(Preferences.ENTRANT.TOUJOURS);
                            break;
                        case R.id.radioButtonSMSContacts:
                            _preferences.setLireSMS(Preferences.ENTRANT.SI_CONTACT);
                            break;
                    }
                    _preferences.flush(MainActivity.this);
                }
            });


        if (rgRepondreSMS != null)
            rgRepondreSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioButtonSMSRepondreJamais:
                            _preferences.setRepondreSMS(Preferences.ENTRANT.JAMAIS);
                            cacheBouton(btnReponseSMS);
                            break;
                        case R.id.radioButtonSMSRepondreToujours:
                            _preferences.setRepondreSMS(Preferences.ENTRANT.TOUJOURS);
                            montreBouton(btnReponseSMS);
                            break;
                        case R.id.radioButtonSMSRepondreContacts:
                            _preferences.setRepondreSMS(Preferences.ENTRANT.SI_CONTACT);
                            montreBouton(btnReponseSMS);
                            break;
                    }
                    _preferences.flush(MainActivity.this);
                }
            });

        if (rgAnnoncerAppels != null)
            rgAnnoncerAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioAnnoncerAppelsJamais:
                            _preferences.setAnnoncerAppels(Preferences.ENTRANT.JAMAIS);
                            break;
                        case R.id.radioAnnoncerAppelsToujours:
                            _preferences.setAnnoncerAppels(Preferences.ENTRANT.TOUJOURS);
                            break;
                        case R.id.radioAnnoncerAppelsContacts:
                            _preferences.setAnnoncerAppels(Preferences.ENTRANT.SI_CONTACT);
                            break;
                    }
                    _preferences.flush(MainActivity.this);
                }
            });

        if (rgRepondreAppels != null)
            rgRepondreAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId)
                {
                    switch (checkedId)
                    {
                        case R.id.radioButtonAppelsJamais:
                            _preferences.setRepondreAppels(Preferences.ENTRANT.JAMAIS);
                            cacheBouton(btnReponseAppels);
                            break;
                        case R.id.radioButtonAppelsToujours:
                            _preferences.setRepondreAppels(Preferences.ENTRANT.TOUJOURS);
                            montreBouton(btnReponseAppels);
                            break;
                        case R.id.radioButtonAppelsContacts:
                            _preferences.setRepondreAppels(Preferences.ENTRANT.SI_CONTACT);
                            montreBouton(btnReponseAppels);
                            break;
                    }
                    _preferences.flush(MainActivity.this);
                }
            });

        if (btnReponseSMS != null)
            btnReponseSMS.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    ModalEditText.showEditText(MainActivity.this, R.layout.modal_edit_text, R.id.textViewTitre, R.id.editText, R.id.buttonOK, "SMS: réponse automatique", _preferences.getReponseSMS(), new ModalEditText.ModalEditListener()
                    {
                        @Override
                        public void onTextEdited(final String s)
                        {
                            _preferences.setReponseSMS(s);
                            _preferences.flush(MainActivity.this);
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        }
                    });
                }
            });

        if (btnReponseAppels != null)
            btnReponseAppels.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    ModalEditText.showEditText(MainActivity.this, R.layout.modal_edit_text, R.id.textViewTitre, R.id.editText, R.id.buttonOK, "Appels: réponse automatique", _preferences.getReponseAppels(), new ModalEditText.ModalEditListener()
                    {
                        @Override
                        public void onTextEdited(final String s)
                        {
                            _preferences.setReponseAppels(s);
                            _preferences.flush(MainActivity.this);
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        }
                    });
                }
            });
    }

    private void cacheBouton(final @NonNull View view)
    {
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_top));
        view.setVisibility(View.GONE);

    }

    private void montreBouton(final @NonNull View view)
    {
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_top));
        view.setVisibility(View.VISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    /***
     * Menu principal
     * @param item
     * @return
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_synthese_vocale:
                startActivityForResult(new Intent("com.android.settings.TTS_SETTINGS"), 0); // to come back to your activity.
                break;

            case R.id.menu_couleurs:
                ThemeActivity.start(this);
                break;

            case R.id.menu_apropos:
                DialogAPropos.start(this);
                break;

            case R.id.menu_parametres:
                ParametresActivity.start(this);
                break;

            case R.id.menu_traces:
                ReportActivity.start(this);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void demandePermissionsSiBesoin()
    {
        if (verifiePermissions())
            return;

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Cette application ne peut pas fonctionner sans que vous lui accordiez la permission d'accéder à certaines fonctionnalités de votre téléphone.");
        dlgAlert.setTitle("Permissions manquantes");
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //dismiss the dialog
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 0);
                    }
                });

        dlgAlert.create().show();

    }

    private boolean verifiePermissions()
    {
        for (String p : PERMISSIONS)
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    /***
     * Affiche un message generique
     * @param message
     */
    private void message(final String message)
    {
        TextToSpeechManager.getInstance(this).annonce(message);
    }

    /***
     * demarrer les messages de notification
     */
    private void demarrerNotifications()
    {
        message("Démarrage");
        Calendar maintenant = Calendar.getInstance();
        _preferences.setEnCours(true);
        _preferences.setHeureDernierePause(maintenant.getTimeInMillis());
        _preferences.flush(this);
        Plannificateur.getInstance(this).plannifieProchaineNotification(this);
        CompagnonService.start(this);
    }

    /***
     * Arreter les messages de notification
     */
    private void arreterNotifications()
    {
        message("Arrêt");
        Plannificateur.getInstance(this).arrete(this);
        _preferences.setEnCours(false);
        _preferences.flush(this);

        CompagnonService.stop(this);

        if (tvMessage != null)
            tvMessage.setText(R.string.compagnon_arrete);
    }


    @Override
    protected void onResume()
    {
        try
        {
            super.onResume();
            _preferences = Preferences.getInstance(this);
            majUI();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /***
     * Mettre l'interface a jour en fonction de la configuration
     */
    private void majUI()
    {
        if (rgAnnonceHeure != null)
            switch (_preferences.getDelaiAnnonceHeure())
            {
                case TOUTES_LES_HEURES:
                    rgAnnonceHeure.check(R.id.radioButtonAnnonceHeure);
                    break;
                case TOUTES_LES_DEMI_HEURES:
                    rgAnnonceHeure.check(R.id.radioButtonAnnonceDemi);
                    break;
                case TOUS_LES_QUARTS_D_HEURES:
                    rgAnnonceHeure.check(R.id.radioButtonAnnonceQuart);
                    break;
                default:
                    rgAnnonceHeure.check(R.id.radioButtonAnnonceJamais);
                    break;
            }

        if (rgConseillerPause != null)
        {
            if (_preferences.isConseillerPause())
            {
                switch (_preferences.getMinutesEntrePauses())
                {
                    case 120:
                        rgConseillerPause.check(R.id.radioButtonPause2Heures);
                        break;
                    case 60:
                        rgConseillerPause.check(R.id.radioButtonPause1Heure);
                        break;
                    case 30:
                        rgConseillerPause.check(R.id.radioButtonPauseDemiHeure);
                        break;
                    default:
                        rgConseillerPause.check(R.id.radioButtonPauseJamais);
                }
            } else
                rgConseillerPause.check(R.id.radioButtonPauseJamais);
        }

        if (rgLireSMS != null)
            switch (_preferences.getLireSMS())
            {
                case JAMAIS:
                    rgLireSMS.check(R.id.radioButtonSMSJamais);
                    break;
                case TOUJOURS:
                    rgLireSMS.check(R.id.radioButtonSMSToujours);
                    break;
                case SI_CONTACT:
                    rgLireSMS.check(R.id.radioButtonSMSContacts);
                    break;
            }

        if (rgRepondreSMS != null)
            switch (_preferences.getRepondreSMS())
            {
                case JAMAIS:
                    rgRepondreSMS.check(R.id.radioButtonSMSRepondreJamais);
                    btnReponseSMS.setVisibility(View.GONE);
                    break;
                case TOUJOURS:
                    rgRepondreSMS.check(R.id.radioButtonSMSRepondreToujours);
                    btnReponseSMS.setVisibility(View.VISIBLE);
                    break;
                case SI_CONTACT:
                    rgRepondreSMS.check(R.id.radioButtonSMSRepondreContacts);
                    btnReponseSMS.setVisibility(View.VISIBLE);
                    break;
            }

        if (rgRepondreAppels != null)
            switch (_preferences.getRepondreAppels())
            {
                case JAMAIS:
                    rgRepondreAppels.check(R.id.radioButtonAppelsJamais);
                    btnReponseAppels.setVisibility(View.GONE);
                    break;
                case TOUJOURS:
                    rgRepondreAppels.check(R.id.radioButtonAppelsToujours);
                    btnReponseAppels.setVisibility(View.VISIBLE);
                    break;
                case SI_CONTACT:
                    rgRepondreAppels.check(R.id.radioButtonAppelsContacts);
                    btnReponseAppels.setVisibility(View.VISIBLE);
                    break;
            }

        if (rgAnnoncerAppels != null)
            switch (_preferences.getAnnoncerAppels())
            {
                case JAMAIS:
                    rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsJamais);
                    break;
                case TOUJOURS:
                    rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsToujours);
                    break;
                case SI_CONTACT:
                    rgAnnoncerAppels.check(R.id.radioAnnoncerAppelsContacts);
                    break;
            }

        if (_preferences.isEnCours())
        {
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        } else
        {
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        }
    }

    public void onClickStop(View v)
    {
        arreterNotifications();

        btnStart.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter));
        btnStart.setVisibility(View.VISIBLE);
        btnStop.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit));
        btnStop.setVisibility(View.GONE);
    }

    public void onClickStart(View v)
    {
        demarrerNotifications();

        btnStop.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter));
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit));
        btnStart.setVisibility(View.GONE);
    }
}
