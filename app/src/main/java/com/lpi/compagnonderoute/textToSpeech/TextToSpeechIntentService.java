package com.lpi.compagnonderoute.textToSpeech;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * IntentService pour
 * helper methods.
 */
public class TextToSpeechIntentService extends IntentService
{
	private static final String ACTION = TextToSpeechIntentService.class.getName() + ".action";
	private static final String EXTRA_MESSAGE = TextToSpeechIntentService.class.getName() + ".extra.message";

	public TextToSpeechIntentService()
	{
		super("TextToSpeechIntentService");
	}
	/**
	 * Starts this service to perform action Foo with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void start(@NonNull final Context context, @NonNull final String message)
	{
		Intent intent = new Intent(context, TextToSpeechIntentService.class);
		intent.setAction(ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		if (intent != null)
		if ( ACTION.equals(intent.getAction()))
		{
			final String param1 = intent.getStringExtra(EXTRA_MESSAGE);
			if (param1 != null)
				TextToSpeechManager.getInstance(this).annonce(this.getApplicationContext(), param1);
		}
	}
}
