package com.lpi.compagnonderoute.service.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

public class SMSBroadcastReceiver extends BroadcastReceiver
{
    public SMSBroadcastReceiver()
    {
    }

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent)
    {
        Report r = Report.getInstance(context);

        try
        {
            Preferences preferences = Preferences.getInstance(context);
            if (!preferences.isEnCours())
                // Rien a faire
                return;
            if ((preferences.getLireSMS() == Preferences.ENTRANT.JAMAIS) && (preferences.getRepondreSMS() == Preferences.ENTRANT.JAMAIS))
                return;

            Bundle bundle = intent.getExtras();
            if ( bundle!=null)
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))
            {
                int subscriptionId = bundle.getInt("subscription", -1);
                SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (SmsMessage sms : smsMessages)
                {
                    r.log(Report.NIVEAU.DEBUG, "*SMSUtils recu:");
                    r.log(Report.NIVEAU.DEBUG, "Subscription ID:" + subscriptionId);
                    r.log(Report.NIVEAU.DEBUG, "Adress:" + sms.getDisplayOriginatingAddress());
                    r.log(Report.NIVEAU.DEBUG, "DisplayMessageBody " + sms.getDisplayMessageBody());
                    r.log(Report.NIVEAU.DEBUG, "Email from" + sms.getEmailFrom());
                    r.log(Report.NIVEAU.DEBUG, "Email body" + sms.getEmailBody());
                    r.log(Report.NIVEAU.DEBUG, "pseudo subject" + sms.getPseudoSubject());
                    r.log(Report.NIVEAU.DEBUG, "Service center adress" + sms.getServiceCenterAddress());
                    r.log(Report.NIVEAU.DEBUG, "Index on Icc" + sms.getIndexOnIcc());
                    r.log(Report.NIVEAU.DEBUG, "getMessageClass" + sms.getMessageClass());
                    r.log(Report.NIVEAU.DEBUG, "getStatus" + sms.getStatus());
                    r.log(Report.NIVEAU.DEBUG, "getStatusOnIcc" + sms.getStatusOnIcc());

                    if (preferences.getLireSMS() != Preferences.ENTRANT.JAMAIS)
                        lireSMS(context, sms);

                    if (preferences.getRepondreSMS() != Preferences.ENTRANT.JAMAIS)
                        repondreSMS(context, sms, subscriptionId);
                }
            }
        } catch (Exception e)
        {
           r.log(Report.NIVEAU.ERROR, "Erreur dans SMSBroadcastReceiver.onReceive");
            r.log(Report.NIVEAU.ERROR, e);
        }
    }

    /*******************************************************************************************************************
     * Envoyer un message de reponse automatique
     * @param context
     * @param sms
     * @param subscriptionId
     *******************************************************************************************************************/
    private void repondreSMS(final @NonNull Context context, @NonNull final SmsMessage sms, int subscriptionId)
    {
        String contact = sms.getDisplayOriginatingAddress();

        if (Preferences.getInstance(context).getRepondreSMS() == Preferences.ENTRANT.SI_CONTACT)
        {
            contact = ContactUtils.getContactFromNumber(context, contact);
            if (contact == null)
                // Ce sms ne provient pas d'un de nos contacts
                return;
        }

        SMSUtils.send(context, sms.getDisplayOriginatingAddress(),
                Preferences.getInstance(context).getReponseSMS() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)",
                subscriptionId);
    }

    /*******************************************************************************************************************
     * Lire le sms en synthese vocale
     * @param context
     * @param sms
     *******************************************************************************************************************/
    private void lireSMS(@NonNull final Context context, @NonNull final SmsMessage sms)
    {
        String contact = sms.getDisplayOriginatingAddress();

        if (Preferences.getInstance(context).getLireSMS() == Preferences.ENTRANT.SI_CONTACT)
        {
            contact = ContactUtils.getContactFromNumber(context, contact);
            if (contact == null)
                // Ce sms ne provient pas d'un de nos contacts
                return;
        }

        String body = sms.getDisplayMessageBody();
        if (contact != null && body != null)
        {
            String message = context.getResources().getString(R.string.format_nouveau_sms, contact, body);
            TextToSpeechManager.getInstance(context).annonceFromReceiver(context, message);
        }
    }
}
