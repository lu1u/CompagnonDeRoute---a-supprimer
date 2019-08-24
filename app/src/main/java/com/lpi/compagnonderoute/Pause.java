package com.lpi.compagnonderoute;
/***
 * Gestion des messages qui conseillent de faire une pause
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.utils.Preferences;

import java.util.Calendar;

public class Pause
{
	/***
	 * Calcule l'heure de la prochaine pause
	 * @param maintenant
	 * @param preferences
	 * @return
	 */
	@Nullable public static Calendar getProchaineNotification(@NonNull final Calendar maintenant, @NonNull final Preferences preferences)
	{
		if ( ! preferences.isConseillerPause())
			return null;

		long prochainePause = preferences.getHeureDernierePause();
		if (prochainePause == 0)
			prochainePause += maintenant.getTimeInMillis();

		prochainePause += (preferences.getMinutesEntrePauses() * 60L * 1000L);

		Calendar res = Calendar.getInstance();
		res.setTimeInMillis(prochainePause);
		return res;
	}

	/***
	 * Changement de delai entre les pauses
	 */
	public static void changeDelai(@NonNull  final Context context)
	{
		Preferences prefs = Preferences.getInstance(context);
		if ( ! prefs.isEnCours())
			return;

		if ( !prefs.isConseillerPause())
			return;

		Plannificateur.getInstance(context).plannifieProchaineNotification(context);
	}
}
