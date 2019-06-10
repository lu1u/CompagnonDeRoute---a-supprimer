package com.lpi.compagnonderoute.textToSpeech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

/**
 * Created by lucien on 24/04/2018.
 * Classe singleton pour faciliter la synthese vocale sous Android
 */

public class TextToSpeechManager
{
	//public static final String MESSAGE = TextToSpeechManager.class.getName() + ".message";
	@Nullable
	//private static TextToSpeechManager INSTANCE = null;
	private TextToSpeech tts;

	/*******************************************************************************************************************
	 * Point d'accès pour l'instance unique du singleton
	 *******************************************************************************************************************/
	@NonNull
	public static synchronized TextToSpeechManager getInstance(Context context)
	{
		//if (INSTANCE == null)
		//{
		//	INSTANCE = new TextToSpeechManager(context);
		//}
		//return INSTANCE;
		return new TextToSpeechManager(context);
	}

	/*******************************************************************************************************************
	 * Constructeur
	 * @param context
	 *******************************************************************************************************************/
	private TextToSpeechManager(Context context)
	{
		//context = context;
	}

	/*******************************************************************************************************************
	 * Ouvre la fenetre de configuration Synthese Vocale du systeme
	 * @param mainActivity
	 *******************************************************************************************************************/
	public static void ouvrirConfigurationAndroid(@NonNull Activity mainActivity)
	{
		mainActivity.startActivityForResult(new Intent("com.android.settings.TTS_SETTINGS"), 0); // to come back to your activity.
	}

	/*******************************************************************************************************************
	 * Fait une annonce, apres un bip sonore
	 * @param context
	 * @param message
	 *******************************************************************************************************************/
	public void annonce(@NonNull final Context context, @NonNull final String message)
	{
		final Report r = Report.getInstance(context);
		r.log(Report.NIVEAU.DEBUG, "annonce: " + message);

		try
		{
			// Emettre un beep puis parler
			final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep2);
			final Preferences prefs = Preferences.getInstance(context);
			final boolean changerVolume = !prefs.isVolumeDefaut();
			final float volume = (float) Preferences.getInstance(context).getVolume() / (float) getMaxVolume(context);
			//final boolean deconnecterBluetooth = isBluetoothConnected(context) && prefs.isForcerHautParleur();

			/***********************************************************************************************************
			 * Quand le son sera fini on demarrera la synthese vocale
			 ***********************************************************************************************************/
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{
				@Override
				public void onCompletion(MediaPlayer mp)
				{
					r.log(Report.NIVEAU.DEBUG, "mediaplayer.OnCompletionListener");

					tts = new TextToSpeech(context, new TextToSpeech.OnInitListener()
					{
						/***
						 * TextToSpeech est initialisé
						 * @param status
						 */
						@Override
						public void onInit(int status)
						{
							if (status == TextToSpeech.SUCCESS && (tts != null))
							{
								tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
								{
									@Override
									public void onStart(String utteranceId)
									{
									}

									@Override
									public void onDone(String utteranceId)
									{
										tts.shutdown();
										tts = null;
									}

									@Override
									public void onError(String utteranceId)
									{
										r.log(Report.NIVEAU.ERROR, "TextToSpeech.UtteranceProgressListener error " + utteranceId);
									}
								});

								Bundle ttsParams = null;
								if (changerVolume)
								{
									ttsParams = new Bundle();
									ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
								}
								tts.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, "Compagnon");
								//if (deconnecterBluetooth)
								//   reconnectBluetooth(context);

							} else

							{
								r.log(Report.NIVEAU.WARNING, "TextToSpeech.OnInitListener erreur");
								r.log(Report.NIVEAU.WARNING, "Status =" + status);
								r.log(Report.NIVEAU.WARNING, "tts = " + tts == null ? "null" : tts.toString());
							}
						}
					});
					mediaPlayer.release();
				}
			});

			mediaPlayer.setVolume(volume, volume);
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
			// Attention: suite du code dans le OnCompletionListener (voir plus haut)
		} catch ( Exception e)

		{
			r.log(Report.NIVEAU.ERROR, "Erreur lors de la creation du mediaplayer");
			r.log(Report.NIVEAU.ERROR, e);
		}

	}


	/*******************************************************************************************************************
	 * Retourne le volume maximum
	 * @param context
	 * @return
	 *******************************************************************************************************************/
	public static int getMaxVolume(@NonNull Context context)
	{
		return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
	}


//    private void reconnectBluetooth(Context context)
//    {
//        // TODO: reconnecter bluetooth
//        // _audioManager.setMode(0);
//        // _audioManager.setBluetoothScoOn(true);
//        // _audioManager.startBluetoothSco();
//    }
//
//    /*******************************************************************************************************************
//     * Deconnecte temporairement le bluetooth
//     * @param context
//     *******************************************************************************************************************/
//    private void deconnectBluetooth(Context context)
//    {
//        // TODO: deconnecter bluetooth
//        //_audioManager.setBluetoothScoOn(false);
//        //_audioManager.stopBluetoothSco();
//        //_audioManager.setMode(AudioManager.MODE_NORMAL);
//    }
//
//    /*******************************************************************************************************************
//     * Retourne vrai si un ecouteur bluetooth est connecte
//     * @param context
//     * @return
//     *******************************************************************************************************************/
//    private boolean isBluetoothConnected(Context context)
//    {
//        // TODO: is bluetooth connected
//        return false;
//        //BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        //return (bluetoothAdapter != null && BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET));
//    }


	public void annonce(@NonNull Context ctx, int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		annonce(ctx, String.format(format, args));
	}

	/***
	 * Fait une annonce depuis un BroadcastReceiver en utilisant un IntentService
	 * Sous Android, un broadcastReceiver ne peut pas creer un service (TextToSpeech)
	 * https://stackoverflow.com/questions/4497757/android-call-tts-in-broadcastreceiver
	 * @param message
	 */
	public void annonceFromReceiver(@NonNull final Context context, final String message)
	{
		try
		{
			TextToSpeechIntentService.start(context, message);
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.NIVEAU.ERROR, "TextToSpeechManager.annonceFromReceiver");
			r.log(Report.NIVEAU.ERROR, e);
		}
	}

	public void annonceFromReceiver(@NonNull Context ctx, int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		annonceFromReceiver(ctx, String.format(format, args));
	}
}
