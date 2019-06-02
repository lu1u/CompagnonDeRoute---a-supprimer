package com.lpi.compagnonderoute.textToSpeech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TextToSpeechActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		////////////////////////////////////////////////////////////////////////////////////////////
		// Voir TextToSpeechManager.annonceFromReceiver
		////////////////////////////////////////////////////////////////////////////////////////////
		Intent startingIntent = this.getIntent();
		String msg = startingIntent.getStringExtra(TextToSpeechManager.MESSAGE);
		TextToSpeechManager.getInstance(this).annonce(msg);
		this.finish();
	}
}
