package com.lpi.compagnonderoute.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lpi.compagnonderoute.BuildConfig;
import com.lpi.compagnonderoute.R;

public class DialogAPropos
{
	public static void start(Activity activity)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_apropos, null);
		String message = "Application Id:" + BuildConfig.APPLICATION_ID
				+ "\nBuild type:" + BuildConfig.BUILD_TYPE
				+ "\nFlavor:" + BuildConfig.FLAVOR
				+ "\nVersion name:" + BuildConfig.VERSION_NAME
				+ "\nVersion code:" + BuildConfig.VERSION_CODE
				+ "\nDebug:" + BuildConfig.DEBUG;

		TextView tv = dialogView.findViewById(R.id.textViewDescription);
		tv.setText(message);
//		final EditText editText = dialogView.findViewById(idEdit);
//		final Button button1 = dialogView.findViewById(idSubmit);
//		final TextView tvTitre = dialogView.findViewById(idTitre);
//		tvTitre.setText(titre);

//		if (valeur != null)
//			editText.setText(valeur);
//		button1.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
//				listener.onTextEdited(editText.getText().toString());
//				dialogBuilder.dismiss();
//				activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//			}
//		});

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
