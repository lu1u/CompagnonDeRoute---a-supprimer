/**
 * Enregistre les traces du programme dans une base de donnees, consultable avec ReportActivity
 * Deux modes:
 *      - LOG
 *      - HISTORIQUE
 * Les traces ne sont enregistrees qu'en mode DEBUG
 */
package com.lpi.reportlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.lpi.reportlibrary.database.DatabaseHelper;
import com.lpi.reportlibrary.database.HistoriqueDatabase;
import com.lpi.reportlibrary.database.TracesDatabase;


/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
	@NonNull
	public static final String PREFERENCES = "lpi.com.reportlibrary.preferences";
	@NonNull
	public static final String PREF_TRACES = "lpi.com.reportlibrary.preferences.traces";
	@NonNull
	public static final String PREF_HISTORIQUE = "lpi.com.reportlibrary.preferences.historique";
	@NonNull
	final private static String TAG = "Report";
	private static boolean GENERER_TRACES = true;
	private static boolean GENERER_HISTORIQUE = true;

	// Niveaux de trace
	public enum NIVEAU
	{
		DEBUG,
		WARNING,
		ERROR
	}

	private static final int MAX_BACKTRACE = 10;
	@Nullable
	private static Report INSTANCE = null;
	HistoriqueDatabase _historiqueDatabase;
	TracesDatabase _tracesDatabase;

	private Report(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		GENERER_TRACES = settings.getBoolean(PREF_TRACES, GENERER_TRACES);
		GENERER_HISTORIQUE = settings.getBoolean(PREF_HISTORIQUE, GENERER_HISTORIQUE);

		if (GENERER_TRACES) _tracesDatabase = TracesDatabase.getInstance(context);
		if (GENERER_HISTORIQUE) _historiqueDatabase = HistoriqueDatabase.getInstance(context);
	}

	public static boolean isGenererTraces()
	{
		return GENERER_TRACES;
	}

	public static void setGenererTraces(Context context, boolean valeur)
	{
		if (GENERER_TRACES != valeur)
		{
			GENERER_TRACES = valeur;
			SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PREF_TRACES, GENERER_TRACES);
			editor.apply();
		}
	}

	public static boolean isGenererHistorique()
	{
		return GENERER_HISTORIQUE;
	}

	public static void setGenererHistorique(Context context, boolean valeur)
	{
		if (GENERER_HISTORIQUE != valeur)
		{
			GENERER_HISTORIQUE = valeur;
			SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PREF_HISTORIQUE, GENERER_HISTORIQUE);
			editor.apply();
		}
	}
	/**
	 * Point d'accès pour l'instance unique du singleton
	 *
	 * @param context: le context habituel d'ANdroid, peut être null si l'objet a deja ete utilise
	 */
	@NonNull
	public static synchronized Report getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Report(context);
		}
		return INSTANCE;
	}

	public static int toInt(NIVEAU n)
	{
		switch (n)
		{
			case DEBUG:
				return 0;
			case WARNING:
				return 1;
			case ERROR:
				return 2;
			default:
				return 0;
		}
	}

	public static NIVEAU toNIVEAU(int n)
	{
		switch (n)
		{
			case 0:
				return NIVEAU.DEBUG;
			case 1:
				return NIVEAU.WARNING;
			case 2:
				return NIVEAU.ERROR;
			default:
				return NIVEAU.DEBUG;
		}
	}


	public void log(@NonNull NIVEAU niv, @NonNull String message)
	{
		if (GENERER_TRACES)
		{
			Log.d(TAG, message);
			_tracesDatabase.Ajoute(DatabaseHelper.CalendarToSQLiteDate(null), toInt(niv), message);
		}
	}

	public void log(@NonNull NIVEAU niv, @NonNull Exception e)
	{
		if (GENERER_TRACES)
		{
			log(niv, e.getLocalizedMessage());
			for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
				log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());
		}
	}

	public void historique(@NonNull String message)
	{
		if (GENERER_HISTORIQUE)
			_historiqueDatabase.ajoute(DatabaseHelper.CalendarToSQLiteDate(null), message);
	}

}
