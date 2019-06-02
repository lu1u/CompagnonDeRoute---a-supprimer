package com.lpi.compagnonderoute;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.utils.Preferences;

import java.util.Calendar;

public class Pause
{
	@Nullable public static Calendar getProchaineNotification(@NonNull final Calendar maintenant, @NonNull final Preferences preferences)
	{
		if ( ! preferences.isConseillerPause())
			return null;

		long prochainePause = preferences.getHeureDernierePause() + (preferences.getMinutesEntrePauses()* 60 * 1000);

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
