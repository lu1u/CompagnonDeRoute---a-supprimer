/***
 * Classe de gestion de la plannification des alarme
 * Singleton
 */
package com.lpi.compagnonderoute.plannificateur;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.lpi.compagnonderoute.Carillon;
import com.lpi.compagnonderoute.CompagnonNotification;
import com.lpi.compagnonderoute.Pause;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

import java.util.Calendar;

public class Plannificateur
{
    public static final String ACTION_MESSAGE_UI = Plannificateur.class.getName() + ".messageUI";
    public static final String ACTION_ALARME = Plannificateur.class.getName() + ".action";

    public static final String EXTRA_MESSAGE_UI = "MessageUI";

    public static final String EXTRA_TYPE_NOTIFICATION = "type notification";
    public static final int TYPE_NOTIFICATION_CARILLON = 0;
    public static final int TYPE_NOTIFICATION_PAUSE = 1;

    @Nullable
    private static Plannificateur INSTANCE = null;


    private AlarmManager _alarmManager;
    private PendingIntent _pendingIntent;

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    @Nullable
    public static synchronized Plannificateur getInstance(@NonNull Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Plannificateur(context);
        }
        return INSTANCE;
    }

    private Plannificateur(Context context)
    {
        _alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /***
     * Programme une alarme Android
     * @param context
     * @param prochaineNotification
     * @param typeNotification
     */
    public void plannifie(final @NonNull Context context, @NonNull final Calendar prochaineNotification, final int typeNotification)
    {
        Report r = Report.getInstance(context);
        try
        {
            r.log(Report.NIVEAU.DEBUG, "plannification prochaine alarme: " + Carillon.toHourString(context, prochaineNotification));
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(ACTION_ALARME);
            intent.putExtra(EXTRA_TYPE_NOTIFICATION, typeNotification);
            _pendingIntent = PendingIntent.getBroadcast(context, typeNotification, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            _alarmManager.setExact(AlarmManager.RTC_WAKEUP, prochaineNotification.getTimeInMillis(), _pendingIntent);
        } catch (Exception e)
        {
            r.log(Report.NIVEAU.ERROR, "Plannificateur.plannifie");
            r.log(Report.NIVEAU.ERROR, e);
        }
    }

    /***
     * Calcule la prochaine heure pleine apres celle donnee en parametres
     * @param maintenant
     * @return
     */
    public static @NonNull
    Calendar prochaineHeure(final @NonNull Calendar maintenant)
    {
        Calendar res = (Calendar) maintenant.clone();
        res.set(Calendar.SECOND, 0);
        res.set(Calendar.MINUTE, 0);
        res.roll(Calendar.HOUR_OF_DAY, 1);
        return res;
    }

    /***
     * Calcule la prochaine demi heure pleine apres celle donnee en parametres
     * @param maintenant
     * @return
     */
    public static @NonNull
    Calendar prochaineDemiHeure(final @NonNull Calendar maintenant)
    {
        Calendar res = (Calendar) maintenant.clone();
        res.set(Calendar.SECOND, 0);
        final int minute = maintenant.get(Calendar.MINUTE);
        if (minute < 30)
            // Entre 0 et 30 minutes
            res.set(Calendar.MINUTE, 30);
        else
        {
            // 30 a 60 minute -> prochaine heure
            res.set(Calendar.MINUTE, 0);
            res.roll(Calendar.HOUR_OF_DAY, 1);
        }
        return res;
    }

    /***
     * Calcule le prochain quart d'heure plein apres celle donnee en parametres
     * @param maintenant
     * @return
     */
    public static @NonNull
    Calendar prochaineQuartDHeure(final @NonNull Calendar maintenant)
    {
        Calendar res = (Calendar) maintenant.clone();
        res.set(Calendar.SECOND, 0);
        final int minute = maintenant.get(Calendar.MINUTE);

        if (minute < 15)
            // Entre 0 et 15 minutes
            res.set(Calendar.MINUTE, 15);
        else if (minute < 30)
            // Entre 15 et 30 minutes
            res.set(Calendar.MINUTE, 30);
        else if (minute < 45)
            // Entre 30 et 45 minutes
            res.set(Calendar.MINUTE, 45);
        else
        {
            // 45 a 60 minute -> prochaine heure
            res.set(Calendar.MINUTE, 0);
            res.roll(Calendar.HOUR_OF_DAY, 1);
        }

        //DEBUG res.roll(Calendar.MINUTE, 1);
        return res;
    }

    public void plannifieProchaineNotification(@NonNull final Context context)
    {
        Preferences preferences = Preferences.getInstance(context);
        if (!preferences.isEnCours())
        {
            // Arreter toute plannification
            CompagnonNotification.cancel(context);
            return;
        }

        Calendar maintenant = Calendar.getInstance();

        Calendar prochaineNotification = null;
        int typeNotification = -1;
        String message = "";
        String messageUI = "";

        if (preferences.getDelaiAnnonceHeure() != Preferences.ANNONCER_HEURE.JAMAIS)
        {
            prochaineNotification = Carillon.getProchaineNotification(maintenant, preferences);
            if (prochaineNotification != null)
            {
                typeNotification = Plannificateur.TYPE_NOTIFICATION_CARILLON;
                message += "Prochain carillon " + Carillon.toHourString(context, prochaineNotification);
                messageUI += "Prochain carillon " + Carillon.toHourString(context, prochaineNotification) + "\n";
            }
        }

        if (preferences.isConseillerPause())
        {
            Calendar pause = Pause.getProchaineNotification(maintenant, preferences);
            if (pause != null)
            {
                messageUI += "Prochaine pause " + Carillon.toHourString(context, pause) + "\n";

                if (prochaineNotification == null || pause.before(prochaineNotification))
                {
                    prochaineNotification = pause;
                    typeNotification = Plannificateur.TYPE_NOTIFICATION_PAUSE;
                    if (message.length() > 0)
                        message += ", ";
                    message += "Prochaine pause " + Carillon.toHourString(context, prochaineNotification);

                }
            }
        }

        if (prochaineNotification != null)
            plannifie(context, prochaineNotification, typeNotification);

        CompagnonNotification.notify(context, "Compagnon démarré", message);
        if (messageUI.length() > 0)
            envoieMessageUI(context, messageUI);
    }

    /**
     * Envoi un message a l'interface utilisateur
     *
     * @param messageUI
     */
    private void envoieMessageUI(Context context, final String messageUI)
    {
        Intent intent = new Intent(ACTION_MESSAGE_UI);
        intent.putExtra(EXTRA_MESSAGE_UI, messageUI);
        context.sendBroadcast(intent);
    }

    /***
     * Arrete la plannificaton
     * @param context
     */
    public void arrete(final Context context)
    {
        if (_pendingIntent != null)
            _alarmManager.cancel(_pendingIntent);

        CompagnonNotification.cancel(context);
    }
}
