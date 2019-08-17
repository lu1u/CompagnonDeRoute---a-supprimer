/***
 * Recepteur des alarmes Carillon ou Pause
 */
package com.lpi.compagnonderoute.plannificateur;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver
{
	/***
	 * Reception d'une alarme
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
					default:
						r.log(Report.NIVEAU.WARNING, "Type inconnu " + type);
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
			TextToSpeechManager.getInstance(context).annonceFromReceiver(context, R.string.conseille_pause,  DateUtils.formatDateTime(context, maintenant.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

			prefs.setHeureDernierePause(maintenant.getTimeInMillis());
			prefs.flush(context);

			Plannificateur.getInstance(context).plannifieProchaineNotification(context);
		}
	}


	/***
	 * Annonce l'heure
	 * @param context
	 */
	private void annonceCarillon(@NonNull final Context context)
	{
		Preferences preferences = Preferences.getInstance(context);
		if ( preferences.isEnCours() && (preferences.getDelaiAnnonceHeure()!= Preferences.ANNONCER_HEURE.JAMAIS))
		{
			Calendar maintenant = Calendar.getInstance();
			TextToSpeechManager.getInstance(context).annonceFromReceiver(context, R.string.annonce_heure, DateUtils.formatDateTime(context, maintenant.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
			Plannificateur.getInstance(context).plannifieProchaineNotification(context);
		}
	}
}
