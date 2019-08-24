package com.lpi.compagnonderoute.textToSpeech;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * IntentService pour
 * helper methods.
 */
public class TextToSpeechIntentService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener
{
	private static final String ACTION = TextToSpeechIntentService.class.getName() + ".action";
	private static final String EXTRA_MESSAGE = TextToSpeechIntentService.class.getName() + ".extra.message";
	private static final int MAX_LENGTH = 200;
	private TextToSpeech _tts;
	private MediaPlayer _mediaPlayer;
	private ArrayList<String> _message;

	private static void start(@NonNull final Context context, @NonNull final ArrayList<String> messages)
	{
		Intent intent = new Intent(context, TextToSpeechIntentService.class);
		intent.setAction(ACTION);
		intent.putStringArrayListExtra(EXTRA_MESSAGE, messages);
		//context.startService(intent);
		context.startForegroundService(intent);
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
	 * Retourne le volume maximum
	 * @param context
	 * @return
	 *******************************************************************************************************************/
	public static int getMaxVolume(@NonNull Context context)
	{
		try
		{
			return ((AudioManager) context.getSystemService(AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		} catch (Exception e)
		{
			return 1;
		}
	}


	/***
	 * Fait une annonce depuis un BroadcastReceiver en utilisant un IntentService
	 * Sous Android, un broadcastReceiver ne peut pas creer un service (TextToSpeech)
	 * https://stackoverflow.com/questions/4497757/android-call-tts-in-broadcastreceiver
	 * @param message
	 */
	public static void annonce(@NonNull final Context context, final String message)
	{
		ArrayList<String> chaines = new ArrayList<>();
		chaines.add(message);
		annonce(context, chaines);
	}

	public static void annonce(@NonNull final Context context, @NonNull ArrayList<String> messages)
	{
		try
		{
			start(context, messages);
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.NIVEAU.ERROR, "annonceFromReceiver");
			r.log(Report.NIVEAU.ERROR, e);
		}
	}

	public static void annonce(@NonNull Context ctx, @StringRes int resId, Object... args)
	{
		String format = ctx.getResources().getString(resId);
		annonce(ctx, String.format(format, args));
	}

	/***
	 * TextToSpeech initialisé
	 * @param status
	 */
	@Override
	public void onInit(int status)
	{
		if (status == TextToSpeech.SUCCESS && !_message.isEmpty())
		{
			_tts.speak(_message.get(0), TextToSpeech.QUEUE_ADD, null, null);
			_message.remove(0);
		} else
			stopSelf();
	}

	/***
	 * Message vocal terminé: enoncer le suivant ou arreter le service s'il n'y a plus rien a dire
	 * @param uttId
	 */
	@Override
	public void onUtteranceCompleted(String uttId)
	{
		if (_message.isEmpty())
			stopSelf();
		else
		{
			_tts.speak(_message.get(0), TextToSpeech.QUEUE_ADD, null, null);
			_message.remove(0);
		}
	}

	/***
	 * Terminaison du service
	 */
	@Override
	public void onDestroy()
	{
		if (_tts != null)
		{
			_tts.stop();
			_tts.shutdown();
		}

		if (_mediaPlayer != null)
			_mediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(@Nullable Intent intent, int flags, int startId)
	{
		if (intent != null)
			if (ACTION.equals(intent.getAction()))
			{
				// Initialisation du Mediaplayer
				try
				{
					_mediaPlayer = MediaPlayer.create(this, R.raw.beep2);
				} catch (Exception e)
				{
					_mediaPlayer = null;
					Report r = Report.getInstance(this);
					r.log(Report.NIVEAU.ERROR, "Erreur initialisation MediaPlayer");
					r.log(Report.NIVEAU.ERROR, e);
				}

				// Initialisation de TextToSpeech
				try
				{
					_tts = new TextToSpeech(this, this);
					_message = intent.getStringArrayListExtra(EXTRA_MESSAGE);
				} catch (Exception e)
				{
					_tts = null;
					Report r = Report.getInstance(this);
					r.log(Report.NIVEAU.ERROR, "Erreur initialisation TextToSpeech");
					r.log(Report.NIVEAU.ERROR, e);
				}

				final float volume = (float) Preferences.getInstance(this).getVolume() / (float) getMaxVolume(this);

				_mediaPlayer.setVolume(volume, volume);
				_mediaPlayer.seekTo(0);
				_mediaPlayer.start();
			}
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
}
