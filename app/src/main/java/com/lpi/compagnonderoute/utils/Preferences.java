package com.lpi.compagnonderoute.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by lucien on 16/04/2018.
 */

public class Preferences
{
	private static final String PREFERENCES = Preferences.class.getName();

	public long getDernierSMS()
	{
		return _dernierSMS;
	}

	public void setDernierSMS(long dernierSMS)
	{
		_dernierSMS = dernierSMS;
		_dirty = true;
	}

	public ENTRANT getAnnoncerAppels()
	{
		return _annoncerAppels;
	}

	public void setAnnoncerAppels(ENTRANT annoncerAppels)
	{
		_annoncerAppels = annoncerAppels;
		_dirty = true;
	}

	public boolean isForcerHautParleur() {
		return _forcerHautParleur;
	}

	public void setForcerHautParleur(boolean s)
	{
		_forcerHautParleur = s;
		_dirty = true;
	}

	public boolean isVolumeDefaut()
	{
		return _volumeDefaut;
	}

	public void setVolumeDefaut(boolean b)
	{
		_volumeDefaut = b;
		_dirty = true;
	}

	public int getVolume()
	{
		return _volume;
	}

	public void setVolume(int i)
	{
		_volume = i;
		_dirty = true;
	}

	public int getTheme()
	{
		return _theme;
	}

	public void setTheme(int i)
	{
		_theme = i;
		_dirty = true;
	}

    public enum ANNONCER_HEURE
	{
		JAMAIS, TOUTES_LES_HEURES, TOUTES_LES_DEMI_HEURES, TOUS_LES_QUARTS_D_HEURES
	}


	public enum ENTRANT
	{
		JAMAIS, TOUJOURS, SI_CONTACT

	}

	@Nullable private static Preferences INSTANCE = null;
	@NonNull static private String PREFS_THEME = "theme"; //$NON-NLS-1$
	@NonNull static private String PREFS_FORCER_HAUT_PARLEUR = "forcer haut parleur"; //$NON-NLS-1$
	@NonNull static private String PREFS_VOLUME_DEFAUT = "volume defaut"; //$NON-NLS-1$
	@NonNull static private String PREFS_VOLUME= "volume"; //$NON-NLS-1$
	@NonNull static private String PREFS_EN_COURS = "en cours"; //$NON-NLS-1$
	@NonNull static private String PREFS_HEURE_DERNIERE_PAUSE = "heure derniere pause"; //$NON-NLS-1$
	@NonNull static private String PREFS_DERNIER_SMS = "dernier sms"; //$NON-NLS-1$
	@NonNull static private String PREFS_ANNONCE_BATTERIE_FAIBLE = "annonce batterie faible"; //$NON-NLS-1$
	@NonNull static private String PREFS_DELAI_ANNONCER_HEURE = "delai annoncer heure"; //$NON-NLS-1$
	@NonNull static private String PREFS_CONSEILLER_PAUSE = "conseiller pause"; //$NON-NLS-1$
	@NonNull static private String PREFS_MINUTES_ENTRE_PAUSES = "minutes entre pauses"; //$NON-NLS-1$
	@NonNull static private String PREFS_LIRE_SMS = "lire sms"; //$NON-NLS-1$
	@NonNull static private String PREFS_REPONDRE_SMS = "repondre sms"; //$NON-NLS-1$
	@NonNull static private String PREFS_REPONSE_SMS = "reponse sms"; //$NON-NLS-1$
	@NonNull static private String PREFS_REPONDRE_APPELS = "repondre appels"; //$NON-NLS-1$
	@NonNull static private String PREFS_REPONSE_APPELS = "reponse appels"; //$NON-NLS-1$
	@NonNull static private String PREFS_ANNONCER_APPELS = "annoncer appels"; //$NON-NLS-1$

	private boolean _dirty;
	private int _theme = 1;
	private boolean _enCours = false;
	private ANNONCER_HEURE _delaiAnnonceHeure = ANNONCER_HEURE.TOUS_LES_QUARTS_D_HEURES;
	private boolean _conseillerPause = true;
	private int _minutesEntrePauses = 60;
	private ENTRANT _lireSMS = ENTRANT.TOUJOURS ;
	private ENTRANT _repondreSMS = ENTRANT.SI_CONTACT;
	private String _reponseSMS;
	private ENTRANT _repondreAppels = ENTRANT.JAMAIS;
	private ENTRANT _annoncerAppels = ENTRANT.TOUJOURS;
	private String _reponseAppels;
	private long _heureDernierePause = 0;
	private long _dernierSMS = 0 ;
	private boolean _annonceBatterieFaible = false;
	private boolean _volumeDefaut = false;
	private int _volume = 1;
	private boolean _forcerHautParleur= false;

	private Preferences(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		_dirty = false;

		_theme = settings.getInt(PREFS_THEME, _theme);
		_enCours = settings.getBoolean(PREFS_EN_COURS, _enCours);
		_delaiAnnonceHeure = toANNONCER_HEURE(settings.getInt(PREFS_DELAI_ANNONCER_HEURE, toInt(_delaiAnnonceHeure)));
		_conseillerPause = settings.getBoolean(PREFS_CONSEILLER_PAUSE, _conseillerPause);
		_minutesEntrePauses = settings.getInt(PREFS_MINUTES_ENTRE_PAUSES, _minutesEntrePauses);
		_lireSMS = toENTRANT( settings.getInt(PREFS_LIRE_SMS, toInt(_lireSMS)));
		_repondreSMS = toENTRANT(settings.getInt(PREFS_REPONDRE_SMS, toInt(_repondreSMS)));
		_reponseSMS = settings.getString(PREFS_REPONSE_SMS, "Merci pour votre message. Je ne peux pas vous répondre actuellement.\nJe vous répondrai dès que possible");
		_repondreAppels = toENTRANT(settings.getInt(PREFS_REPONDRE_APPELS, toInt(_repondreAppels)));
		_annoncerAppels = toENTRANT(settings.getInt(PREFS_ANNONCER_APPELS, toInt(_annoncerAppels)));
		_reponseAppels = settings.getString(PREFS_REPONSE_APPELS, "Merci pour votre appel. Je ne suis pas disponible actuellement.\nJe vous répondrai dès que possible");
		_heureDernierePause = settings.getLong(PREFS_HEURE_DERNIERE_PAUSE, _heureDernierePause);
		_dernierSMS = settings.getLong(PREFS_DERNIER_SMS, _dernierSMS);
		_annonceBatterieFaible = settings.getBoolean(PREFS_ANNONCE_BATTERIE_FAIBLE, _annonceBatterieFaible);
		_volumeDefaut = settings.getBoolean(PREFS_VOLUME_DEFAUT, _volumeDefaut);
		_volume = settings.getInt(PREFS_VOLUME, _volume);
		_forcerHautParleur = settings.getBoolean(PREFS_FORCER_HAUT_PARLEUR, _forcerHautParleur);
	}

	/**
	 * Ecrire les modifications
	 *
	 * @param context
	 */
	public void flush(@NonNull Context context)
	{
		if (_dirty)
		{
			SharedPreferences settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();

			editor.putInt(PREFS_THEME, _theme);
			editor.putBoolean(PREFS_EN_COURS, _enCours);
			editor.putInt(PREFS_DELAI_ANNONCER_HEURE, toInt(_delaiAnnonceHeure));
			editor.putBoolean(PREFS_CONSEILLER_PAUSE, _conseillerPause);
			editor.putInt(PREFS_MINUTES_ENTRE_PAUSES, _minutesEntrePauses);
			editor.putInt(PREFS_LIRE_SMS, toInt(_lireSMS));
			editor.putInt(PREFS_REPONDRE_SMS, toInt(_repondreSMS));
			editor.putString(PREFS_REPONSE_SMS, _reponseSMS);
			editor.putInt(PREFS_REPONDRE_APPELS, toInt(_repondreAppels));
			editor.putInt(PREFS_ANNONCER_APPELS, toInt(_annoncerAppels));
			editor.putString(PREFS_REPONSE_APPELS, _reponseAppels);
			editor.putLong(PREFS_HEURE_DERNIERE_PAUSE, _heureDernierePause);
			editor.putLong(PREFS_DERNIER_SMS, _dernierSMS);
			editor.putBoolean(PREFS_ANNONCE_BATTERIE_FAIBLE, _annonceBatterieFaible);
			editor.putBoolean(PREFS_FORCER_HAUT_PARLEUR, _forcerHautParleur);
			editor.putBoolean(PREFS_VOLUME_DEFAUT, _volumeDefaut);
			editor.putInt(PREFS_VOLUME, _volume);
			editor.apply();
			_dirty = false;
		}
	}


	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@Nullable
	public static synchronized Preferences getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Preferences(context);
		}
		return INSTANCE;
	}

	public boolean isConseillerPause()
	{
		return _conseillerPause;
	}

	public void setConseillerPause(boolean conseillerPause)
	{
		_conseillerPause = conseillerPause;
		_dirty = true;
	}

	public int getMinutesEntrePauses()
	{
		return _minutesEntrePauses;
	}

	public void setMinutesEntrePauses(int minutesEntrePauses)
	{
		_minutesEntrePauses = minutesEntrePauses;
		_dirty = true;
	}

	public ENTRANT getLireSMS()
	{
		return _lireSMS;
	}

	public void setLireSMS(ENTRANT lireSMS)
	{
		_lireSMS = lireSMS;
		_dirty = true;
	}

	public ENTRANT getRepondreSMS()
	{
		return _repondreSMS;
	}

	public void setRepondreSMS(ENTRANT repondreSMS)
	{
		_repondreSMS = repondreSMS;
		_dirty = true;
	}

	@Nullable
	public String getReponseSMS()
	{
		return _reponseSMS;
	}

	public void setReponseSMS(String reponseSMS)
	{
		_reponseSMS = reponseSMS;
		_dirty = true;
	}

	public ENTRANT getRepondreAppels()
	{
		return _repondreAppels;
	}

	public void setRepondreAppels(ENTRANT repondreAppels)
	{
		_repondreAppels = repondreAppels;
		_dirty = true;
	}

	@Nullable
	public String getReponseAppels()
	{
		return _reponseAppels;
	}

	public void setReponseAppels(String reponseAppels)
	{
		_reponseAppels = reponseAppels;
		_dirty = true;
	}

	public ANNONCER_HEURE getDelaiAnnonceHeure()
	{
		return _delaiAnnonceHeure;
	}

	public void setDelaiAnnonceHeure(ANNONCER_HEURE delaiAnnonceHeure)
	{
		_delaiAnnonceHeure = delaiAnnonceHeure;
		_dirty = true;
	}


	@NonNull
	public static ENTRANT toENTRANT(final int value)
	{
		switch (value)
		{
			case 1: return ENTRANT.TOUJOURS;
			case 2: return ENTRANT.SI_CONTACT;
			default: return ENTRANT.JAMAIS;
		}
	}

	public static int toInt(final ENTRANT value)
	{
		switch (value)
		{
			case TOUJOURS:  return 1;
			case SI_CONTACT: return 2;
			default: return 0;
		}
	}

	@NonNull
	public static ANNONCER_HEURE toANNONCER_HEURE(final int value)
	{
		switch (value)
		{
			case 1:
				return ANNONCER_HEURE.TOUTES_LES_HEURES;
			case 2:
				return ANNONCER_HEURE.TOUTES_LES_DEMI_HEURES;
			case 3:
				return ANNONCER_HEURE.TOUS_LES_QUARTS_D_HEURES;
			default:
				return ANNONCER_HEURE.JAMAIS;
		}
	}

	public static int toInt(final ANNONCER_HEURE annonce)
	{
		switch (annonce)
		{
			case TOUTES_LES_HEURES:
				return 1;
			case TOUTES_LES_DEMI_HEURES:
				return 2;
			case TOUS_LES_QUARTS_D_HEURES:
				return 3;
			default:
				return 0;
		}
	}


	public boolean isEnCours()
	{
		return _enCours;
	}

	public void setEnCours(boolean enCours)
	{
		_enCours = enCours;
		_dirty = true;
	}

	public long getHeureDernierePause()
	{
		return _heureDernierePause;
	}

	public void setHeureDernierePause(long heureDernierePause)
	{
		_heureDernierePause = heureDernierePause;
		_dirty = true;
	}



}
