package com.lpi.compagnonderoute;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import com.lpi.compagnonderoute.plannificateur.Plannificateur;
import com.lpi.compagnonderoute.utils.Preferences;

import java.util.Calendar;

public class Carillon
{
	/*******************************************************************************************************************
	 * Calcule l'heure de la prochaine notification d'annonce de l'heure
	 * @param maintenant
	 * @param preferences
	 * @return
	 *******************************************************************************************************************/
	public static @Nullable Calendar getProchaineNotification(@NonNull final Calendar maintenant, @NonNull final Preferences preferences)
	{
		Calendar depart ;

		switch (preferences.getDelaiAnnonceHeure())
		{
			case JAMAIS:
				return null;

			case TOUTES_LES_HEURES:
				depart = Plannificateur.prochaineHeure(maintenant);
				break;

			case TOUTES_LES_DEMI_HEURES:
				depart = Plannificateur.prochaineDemiHeure(maintenant);
				break;

			case TOUS_LES_QUARTS_D_HEURES:
				depart = Plannificateur.prochaineQuartDHeure(maintenant);
				break;

			default: //??? On ne devrait jamais passer par la
				depart = (Calendar) maintenant.clone();
				depart.roll(Calendar.MINUTE, 5);    // Histoire d'avoir qq chose
		}

		return depart;
	}

	/*******************************************************************************************************************
	 * Calcule une representation textuelle de l'heure
	 *******************************************************************************************************************/
	public static String toHourString(@NonNull Context context, @NonNull Calendar c)
	{
		return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
	}

	public static void changeDelai(@NonNull final Context context)
	{
		Preferences prefs = Preferences.getInstance(context);
		if ( ! prefs.isEnCours())
			return;

		if ( prefs.getDelaiAnnonceHeure() == Preferences.ANNONCER_HEURE.JAMAIS)
			return;

		Plannificateur.getInstance(context).plannifieProchaineNotification(context);
	}
}
