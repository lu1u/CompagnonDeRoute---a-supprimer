package com.lpi.compagnonderoute.textToSpeech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.lpi.reportlibrary.Report;

import java.util.ArrayList;

/**
 * Created by lucien on 24/04/2018.
 * Classe singleton pour faciliter la synthese vocale sous Android
 */

public class TextToSpeechManager //implements TextToSpeech.OnInitListener
{
	@Nullable
	private static TextToSpeechManager INSTANCE = null;
	private TextToSpeech _textToSpeech;
	private MediaPlayer _mediaPlayer;
	private boolean _ttsInitialise;
	private ArrayList<String> _pendingMessages;
	private Context _context;

	/*******************************************************************************************************************
	 * Point d'accès pour l'instance unique du singleton
	 *******************************************************************************************************************/
	@NonNull
	public static synchronized TextToSpeechManager getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new TextToSpeechManager(context);
		}
		return INSTANCE;
	}

	/*******************************************************************************************************************
	 * Constructeur
	 * @param context
	 *******************************************************************************************************************/
	private TextToSpeechManager(Context context)
	{
		_context = context;
		//_pendingMessages = new ArrayList<>();
		//try
		//{
		//	_mediaPlayer = MediaPlayer.create(context, R.raw.beep2);
//
		//} catch (Exception e)
		//{
		//	_mediaPlayer = null;
		//	Report r = Report.getInstance(context);
		//	r.log(Report.NIVEAU.ERROR, "Erreur initialisation MediaPlayer");
		//	r.log(Report.NIVEAU.ERROR, e);
//
		//}
//
		//try
		//{
		//	_ttsInitialise = false;
		//	_textToSpeech = new TextToSpeech(context, this);
		//}
		//catch (Exception e)
		//{
		//	_textToSpeech = null;
		//	Report r = Report.getInstance(context);
		//	r.log(Report.NIVEAU.ERROR, "Erreur initialisation TextToSpeech");
		//	r.log(Report.NIVEAU.ERROR, e);
		//}
	}

	//public void finalize()
	//{
	//	try
	//	{
	//		super.finalize();
	//		if (_textToSpeech !=null)
	//			_textToSpeech.shutdown();
//
	//		if (_mediaPlayer !=null)
	//			_mediaPlayer.release();
	//	}
	//	catch(Exception e)
	//	{
	//	}
	//	catch (Throwable throwable)
	//	{
	//	}
	//}
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
		annonceFromReceiver(context, message);
		//try
		//{
		//	if (_mediaPlayer!=null)
		//	{
		//		final Bundle ttsParams = getTtsParams();
//
		//		_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		//		{
		//			/***
		//			 * Fonction callback appellee quand le son est termine
		//			 * @param mp
		//			 */
		//			@Override
		//			public void onCompletion(MediaPlayer mp)
		//			{
		//				if (_ttsInitialise && _textToSpeech != null)
		//					_textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, null);
		//				else
		//					// Dire le message une fois que le _textToSpeech sera initialise
		//					_pendingMessages.add(message);
		//			}
		//		});
//
		//		final float volume = (float) Preferences.getInstance(context).getVolume() / (float) getMaxVolume(context);
		//		_mediaPlayer.setVolume(volume, volume);
		//		_mediaPlayer.seekTo(0);
		//		_mediaPlayer.start();
		//		// Attention: suite du code dans le OnCompletionListener (voir plus haut)
		//	}
		//}
		//catch (IllegalStateException e)
		//{
		//	Report r = Report.getInstance(context);
		//	r.log(Report.NIVEAU.ERROR, "Erreur initialisation annonce");
		//	r.log(Report.NIVEAU.ERROR, e);
		//}


//			_mediaPlayer.seekTo(0);
//			_mediaPlayer.start();
//			// Attention: suite du code dans le OnCompletionListener (voir plus haut)
//			{
//				@Override
//				public void onCompletion(MediaPlayer mp)
//				{
//					r.log(Report.NIVEAU.DEBUG, "mediaplayer.OnCompletionListener");
//		final Report r = Report.getInstance(context);
//		r.log(Report.NIVEAU.DEBUG, "annonce: " + message);
//
//		try
//		{
//			// Emettre un beep puis parler
//			final MediaPlayer _mediaPlayer = MediaPlayer.create(context, R.raw.beep2);
//			final Preferences prefs = Preferences.getInstance(context);
//			final boolean changerVolume = !prefs.isVolumeDefaut();
//			final float volume = (float) Preferences.getInstance(context).getVolume() / (float) getMaxVolume(context);
//			//final boolean deconnecterBluetooth = isBluetoothConnected(context) && prefs.isForcerHautParleur();
//
//			/***********************************************************************************************************
//			 * Quand le son sera fini on demarrera la synthese vocale
//			 ***********************************************************************************************************/
//			_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//			{
//				@Override
//				public void onCompletion(MediaPlayer mp)
//				{
//					r.log(Report.NIVEAU.DEBUG, "mediaplayer.OnCompletionListener");
//
//					_textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener()
//					{
//						/***
//						 * TextToSpeech est initialisé
//						 * @param status
//						 */
//						@Override
//						public void onInit(int status)
//						{
//							if (status == TextToSpeech.SUCCESS && (_textToSpeech != null))
//							{
//								_textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
//								{
//									@Override
//									public void onStart(String utteranceId)
//									{
//									}
//
//									@Override
//									public void onDone(String utteranceId)
//									{
//										_textToSpeech.shutdown();
//										_textToSpeech = null;
//									}
//
//									@Override
//									public void onError(String utteranceId)
//									{
//										r.log(Report.NIVEAU.ERROR, "UtteranceProgressListener error " + utteranceId);
//									}
//								});
//
//								Bundle ttsParams = null;
//								if (changerVolume)
//								{
//									ttsParams = new Bundle();
//									ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
//								}
//								_textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, "Compagnon");
//								//if (deconnecterBluetooth)
//								//   reconnectBluetooth(context);
//
//								_mediaPlayer.release();
//							} else
//
//							{
//								r.log(Report.NIVEAU.WARNING, "TextToSpeech.OnInitListener erreur");
//								r.log(Report.NIVEAU.WARNING, "Status =" + status);
//								r.log(Report.NIVEAU.WARNING, "_textToSpeech = " + _textToSpeech == null ? "null" : _textToSpeech.toString());
//							}
//						}
//					});
//				}
//			});
//
//			_mediaPlayer.setVolume(volume, volume);
//			_mediaPlayer.seekTo(0);
//			_mediaPlayer.start();
//			// Attention: suite du code dans le OnCompletionListener (voir plus haut)
//		}
//		catch ( Exception e)
//		{
//			r.log(Report.NIVEAU.ERROR, "Erreur mediaplayer");
//			r.log(Report.NIVEAU.ERROR, e);
//		}
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
//		annonce(context, message);
	}

	public void annonceFromReceiver(@NonNull Context ctx, int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		annonceFromReceiver(ctx, String.format(format, args));
	}

	///*******************************************************************************************************************
	// * Le TTS vient d'etre initialise, jouer les messages s'il y en a a dire
	// * @param status TextToSpeech
	// ******************************************************************************************************************/
	//@Override
	//public void onInit(int status)
	//{
	//	if (status == TextToSpeech.SUCCESS && (_textToSpeech != null))
	//	{
	//		_ttsInitialise = true;
//
	//		// Jouer les messages en attente
	//		if (_pendingMessages != null)
	//		{
	//			final Bundle ttsParams = getTtsParams();
//
	//			// Dire les messages qui étaient en attente de l'initialisation du TTS
	//			for (String message : _pendingMessages)
	//				_textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, null);
//
	//			_pendingMessages.clear();
	//		}
	//	}
	//	else
	//	{
	//		Report report =  Report.getInstance(_context);
	//		report.log(Report.NIVEAU.ERROR, "Impossible d'initialiser TTS");
	//		report.log(Report.NIVEAU.ERROR, "Status = " + status);
	//	}
	//}


	///*******************************************************************************************************************
	// * Construit un bundle de parametres pour TTS, en fonction des preferences
	// * @return
	// ******************************************************************************************************************/
	//private @Nullable Bundle getTtsParams()
	//{
	//	final Preferences prefs = Preferences.getInstance(_context);
	//	if (prefs.isVolumeDefaut())
	//		return null;
	//	final float volume = (float) prefs.getVolume() / (float) getMaxVolume(_context);
//
	//	final Bundle ttsParams = new Bundle();
	//	ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
	//	return ttsParams;
	//}
}
