package com.lpi.compagnonderoute.plannificateur;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;


import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver
{
	public static final String TAG = AlarmReceiver.class.getName();

	/***
	 * Reception d'une alarme
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent)
	{
		Report r = Report.getInstance(context);
		try
		{
			String action = intent.getAction();
			r.log( Report.NIVEAU.DEBUG, "Alarme recue, action=" + action);
			if (Plannificateur.ACTION_ALARME.equals(action))
			{
				int type = intent.getIntExtra(Plannificateur.EXTRA_TYPE_NOTIFICATION, Plannificateur.TYPE_NOTIFICATION_CARILLON);
				switch (type)
				{
					case Plannificateur.TYPE_NOTIFICATION_CARILLON:
						annonceCarillon(context);
						break;

					case Plannificateur.TYPE_NOTIFICATION_PAUSE:
						annoncePause(context);
						break;
				}
			}
		} catch (Exception e)
		{
			r.log(Report.NIVEAU.ERROR, "Erreur dans AlarmReceiver.onReceive");
			r.log(Report.NIVEAU.ERROR, e);
		}
	}

	/***
	 * Conseille de faire une pause
	 * @param context
	 */
	private void annoncePause(@NonNull final Context context)
	{
		Preferences prefs = Preferences.getInstance(context);
		if ( prefs.isEnCours())
		if ( prefs.isConseillerPause())
		{
			Calendar maintenant = Calendar.getInstance();
			String message = "Il est " + toHourString(context, maintenant) + ", il est temps de faire une pause";
			TextToSpeechManager.getInstance(context).annonceFromReceiver(message);

			prefs.setHeureDernierePause(maintenant.getTimeInMillis());
			prefs.flush(context);

			Plannificateur.getInstance(context).plannifieProchaineNotification(context);
		}
	}

	private String toHourString(Context context, Calendar c)
	{
		return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
	}

	/***
	 * Annonce l'heure
	 * @param context
	 */
	private void annonceCarillon(final Context context)
	{
		Preferences preferences = Preferences.getInstance(context);
		if ( preferences.isEnCours() && (preferences.getDelaiAnnonceHeure()!= Preferences.ANNONCER_HEURE.JAMAIS))
		{
			Calendar maintenant = Calendar.getInstance();
			String message = "Il est " + toHourString(context, maintenant);
			TextToSpeechManager.getInstance(context).annonceFromReceiver(message);
			Plannificateur.getInstance(context).plannifieProchaineNotification(context);
		}
	}
}
