package com.lpi.compagnonderoute;

import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Rational;
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
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechIntentService;
import com.lpi.compagnonderoute.utils.*;
import com.lpi.reportlibrary.activity.ReportActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{


	private ImageButton btnStart, btnStop;
	private @Nullable RadioGroup rgAnnonceHeure, rgConseillerPause, rgLireSMS, rgRepondreSMS, rgRepondreAppels, rgAnnoncerAppels;
	private ImageButton btnReponseSMS, btnReponseAppels;
	private TextView tvMessage;

	@NonNull
	private Preferences _preferences;
	@NonNull final BroadcastReceiver _receiverProchaineAlarme = new BroadcastReceiver()
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

	@NonNull final IntentFilter _intentFilter = new IntentFilter(Plannificateur.ACTION_MESSAGE_UI);

	@Override
	/***
	 * Creation de l'activity
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Utils.setTheme(this);//, R.array.tableauThemes);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null)
		{
			setSupportActionBar(toolbar);
			getSupportActionBar().setIcon(R.drawable.ic_compagnon);
		}

		_preferences = Preferences.getInstance(this);

		getControlsFromIds();
		initListeners();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Permissions.demandePermissionsSiBesoin(this);
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(_receiverProchaineAlarme);
		super.onDestroy();
	}

	/*******************************************************************************************************************
	 * Retrouve les objets permettant de manipuler les controles
	 *******************************************************************************************************************/
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


	/***
	 * Initialiser les interactions avec les boutons de l'interface
	 */
	private void initListeners()
	{
		final Preferences preferences = Preferences.getInstance(this);
		if (rgAnnonceHeure != null)
			rgAnnonceHeure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAnnonceJamais:
							preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.JAMAIS);
							break;
						case R.id.radioButtonAnnonceHeure:
							preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUTES_LES_HEURES);
							break;
						case R.id.radioButtonAnnonceDemi:
							preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUTES_LES_DEMI_HEURES);
							break;
						case R.id.radioButtonAnnonceQuart:
							preferences.setDelaiAnnonceHeure(Preferences.ANNONCER_HEURE.TOUS_LES_QUARTS_D_HEURES);
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
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonPauseJamais:
							preferences.setConseillerPause(false);
							break;
						case R.id.radioButtonPause2Heures:
							preferences.setConseillerPause(true);
							preferences.setMinutesEntrePauses(120);
							break;
						case R.id.radioButtonPause1Heure:
							preferences.setConseillerPause(true);
							preferences.setMinutesEntrePauses(60);
							break;
						case R.id.radioButtonPauseDemiHeure:
							preferences.setConseillerPause(true);
							preferences.setMinutesEntrePauses(30);
							break;
					}
					preferences.flush(MainActivity.this);
					Pause.changeDelai(MainActivity.this);

				}
			});

		if (rgLireSMS != null)
			rgLireSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonSMSJamais:
							preferences.setLireSMS(Preferences.ENTRANT.JAMAIS);
							break;
						case R.id.radioButtonSMSToujours:
							preferences.setLireSMS(Preferences.ENTRANT.TOUJOURS);
							break;
						case R.id.radioButtonSMSContacts:
							preferences.setLireSMS(Preferences.ENTRANT.SI_CONTACT);
							break;
					}
					preferences.flush(MainActivity.this);
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
							preferences.setRepondreSMS(Preferences.ENTRANT.JAMAIS);
							cacheBouton(btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreToujours:
							preferences.setRepondreSMS(Preferences.ENTRANT.TOUJOURS);
							montreBouton(btnReponseSMS);
							break;
						case R.id.radioButtonSMSRepondreContacts:
							preferences.setRepondreSMS(Preferences.ENTRANT.SI_CONTACT);
							montreBouton(btnReponseSMS);
							break;
					}
					preferences.flush(MainActivity.this);
				}
			});

		if (rgAnnoncerAppels != null)
			rgAnnoncerAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioAnnoncerAppelsJamais:
							preferences.setAnnoncerAppels(Preferences.ENTRANT.JAMAIS);
							break;
						case R.id.radioAnnoncerAppelsToujours:
							preferences.setAnnoncerAppels(Preferences.ENTRANT.TOUJOURS);
							break;
						case R.id.radioAnnoncerAppelsContacts:
							preferences.setAnnoncerAppels(Preferences.ENTRANT.SI_CONTACT);
							break;
					}
					preferences.flush(MainActivity.this);
				}
			});

		if (rgRepondreAppels != null)
			rgRepondreAppels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(final RadioGroup group, @IdRes final int checkedId)
				{
					switch (checkedId)
					{
						case R.id.radioButtonAppelsJamais:
							preferences.setRepondreAppels(Preferences.ENTRANT.JAMAIS);
							cacheBouton(btnReponseAppels);
							break;
						case R.id.radioButtonAppelsToujours:
							preferences.setRepondreAppels(Preferences.ENTRANT.TOUJOURS);
							montreBouton(btnReponseAppels);
							break;
						case R.id.radioButtonAppelsContacts:
							preferences.setRepondreAppels(Preferences.ENTRANT.SI_CONTACT);
							montreBouton(btnReponseAppels);
							break;
					}
					preferences.flush(MainActivity.this);
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
							preferences.setReponseSMS(s);
							preferences.flush(MainActivity.this);
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
							preferences.setReponseAppels(s);
							preferences.flush(MainActivity.this);
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
				TextToSpeechIntentService.ouvrirConfigurationAndroid(this);
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


	/*******************************************************************************************************************
	 * demarrer les messages de notification
	 *******************************************************************************************************************/
	private void demarrerNotifications()
	{
		TextToSpeechIntentService.annonce(this, R.string.start);
		Calendar maintenant = Calendar.getInstance();
		_preferences.setEnCours(true);
		_preferences.setHeureDernierePause(maintenant.getTimeInMillis());
		_preferences.flush(this);
		Plannificateur.getInstance(this).plannifieProchaineNotification(this);
		CompagnonService.start(this);
	}

	/*******************************************************************************************************************
	 * Arreter les messages de notification
	 *******************************************************************************************************************/
	private void arreterNotifications()
	{
		TextToSpeechIntentService.annonce(this, R.string.stop);
		Plannificateur.getInstance(this).arrete(this);
		_preferences.setEnCours(false);
		_preferences.flush(this);

		CompagnonService.stop(this);

		if (tvMessage != null)
			tvMessage.setText(R.string.clic_button_to_launch);
	}


	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			_preferences = Preferences.getInstance(this);
			majUI();

			registerReceiver(_receiverProchaineAlarme, _intentFilter);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*******************************************************************************************************************
	 * Mettre l'interface a jour en fonction de la configuration
	 *******************************************************************************************************************/
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

	/*******************************************************************************************************************
	 * Clic sur le bouton "Stop"
	 * @param v
	 *******************************************************************************************************************/
	public void onClickStop(View v)
	{
		arreterNotifications();

		btnStart.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter));
		btnStart.setVisibility(View.VISIBLE);
		btnStop.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit));
		btnStop.setVisibility(View.GONE);
	}

	/*******************************************************************************************************************
	 * Clic sur le bouton "Start"
	 * @param v
	 *******************************************************************************************************************/
	public void onClickStart(View v)
	{
		demarrerNotifications();
		// Montrer bouton stop
		btnStop.setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter));
		btnStop.setVisibility(View.VISIBLE);
		// Cacher bouton start
		btnStart.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit));
		btnStart.setVisibility(View.GONE);
		//String contact = ContactUtils.getContactFromNumber(this, "+33630531670");
		//String body = "Salut, est ce que je pourrais passer pour reessayer de claquer un pneu, maintenant que j'ai du savon à pneus" ;
		//String message = getResources().getString(R.string.format_nouveau_sms, contact, body);
		//TextToSpeechManager.annonceFromReceiver(this, message);
	}

	@Override
	public void onUserLeaveHint()
	{
		Preferences preferences = Preferences.getInstance(this);
		if (preferences.isEnCours() && preferences.isActiverModePip())
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			{
				Rational aspectRatio = new Rational(3, 3);
				PictureInPictureParams params = new PictureInPictureParams.Builder() .setAspectRatio(aspectRatio).build();
				enterPictureInPictureMode(params);
			} else
			{
				enterPictureInPictureMode();
			}
		}
	}

//	@Override
//	public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig)
//	{
//		if (isInPictureInPictureMode)
//		{
//			// Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
//		} else
//		{
//			// Restore the full-screen UI.
//		}
//	}


}
