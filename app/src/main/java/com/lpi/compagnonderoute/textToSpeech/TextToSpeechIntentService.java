package com.lpi.compagnonderoute.textToSpeech;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;

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
	private TextToSpeech _tts;
	private String _message;


	/**
	 * Starts this service
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void start(@NonNull final Context context, @NonNull final String message)
	{
		Intent intent = new Intent(context, TextToSpeechIntentService.class);
		intent.setAction(ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		//context.startService(intent);
		context.startForegroundService(intent);
	}


	@Override
	public void onCreate()
	{
	}

	@Override
	public void onInit(int status)
	{
		if (status == TextToSpeech.SUCCESS)
			if (_message != null)
				_tts.speak(_message, TextToSpeech.QUEUE_ADD, null, null);
	}

	@Override
	public void onUtteranceCompleted(String uttId)
	{
		_tts.stop();
		_tts.shutdown();
		_tts = null;
		stopSelf();
	}

	@Override
	public void onDestroy()
	{
		if (_tts != null)
		{
			_tts.stop();
			_tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent != null)
			if (ACTION.equals(intent.getAction()))
			{
				//final String param1 = intent.getStringExtra(EXTRA_MESSAGE);
				//if (param1 != null)
				//TextToSpeechManager.getInstance(this).annonce(this.getApplicationContext(), param1);
				_message = intent.getStringExtra(EXTRA_MESSAGE);
				_tts = new TextToSpeech(this, this);
			}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
}
