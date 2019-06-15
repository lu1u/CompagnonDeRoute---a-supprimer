package com.lpi.compagnonderoute.service.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.textToSpeech.TextToSpeechManager;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

public class IncomingCallReceiver extends BroadcastReceiver
{
    protected void onIncomingCallStarted(@NonNull final Context context, String number, long subId)
    {
        String contact = ContactUtils.getContactFromNumber(context, number);

        // Annoncer l'appel
        Preferences prefs = Preferences.getInstance(context);
        if (prefs.getAnnoncerAppels() != Preferences.ENTRANT.JAMAIS)
        {
            String appelant = number;
            if (prefs.getAnnoncerAppels() == Preferences.ENTRANT.SI_CONTACT)
                appelant = contact;

            if (appelant != null)
            {
                String message = context.getResources().getString(R.string.format_appel_telephonique, appelant);
                TextToSpeechManager.getInstance(context).annonceFromReceiver(context, message + ", subscription " + subId);
            }
        }

        // Repondre a l'appel
        if (prefs.getRepondreAppels() != Preferences.ENTRANT.JAMAIS)
        {
            String appelant = number;
            if (prefs.getRepondreAppels() == Preferences.ENTRANT.SI_CONTACT)
                appelant = contact;

            if (appelant != null)
            {
                SMSUtils.send(context, number, Preferences.getInstance(context).getReponseSMS() + "\n(Message envoyé automatiquement par l'application Compagnon de Route (c)2019 Lucien Pilloni)", 0);
            }
        }

        PhoneUtils.rejectCall(context);
    }

//	protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
//		Log.d("onOutgoingCallStarted",number);
//	}
//
//	// когда нажимается кнопка Завершить на входящем звонке
//	protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
//		Log.d("onIncomingCallEnded",number);
//
//	}
//
//	// когда нажимается кнопка Завершить на исходящем звонке
//	protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
//		Log.d("onOutgoingCallEnded",number);
//	}
//
//	// когда не сняли трубку при входящем звонке (пропуск звонка)
//	protected void onMissedCall(Context ctx, String number, Date start) {
//			}

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(@NonNull final Context context, int state, String number, long subId)
    {
        Preferences prefs = Preferences.getInstance(context);
        if (!prefs.isEnCours())
            return;

        if (prefs.getRepondreAppels() == Preferences.ENTRANT.JAMAIS)
            return;
        if (lastState == state)
        {
            //No change, debounce extras
            return;
        }
        switch (state)
        {
            case TelephonyManager.CALL_STATE_RINGING:
//				isIncoming = true;
//				//callStartTime = new Date();
//				savedNumber = number;
                onIncomingCallStarted(context, number, subId);
                break;
//			case TelephonyManager.CALL_STATE_OFFHOOK:
//				//Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
//				if(lastState != TelephonyManager.CALL_STATE_RINGING){
//					isIncoming = false;
//					callStartTime = new Date();
//					onOutgoingCallStarted(context, savedNumber, callStartTime);
//				}
//				break;
//			case TelephonyManager.CALL_STATE_IDLE:
//				//Went to idle-  this is the end of a call.  What type depends on previous state(s)
//				if(lastState == TelephonyManager.CALL_STATE_RINGING){
//					//Ring but no pickup-  a miss
//					onMissedCall(context, savedNumber, callStartTime);
//				}
//				else if(isIncoming){
//					onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
//				}
//				else{
//					onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
//				}
//				break;
        }
        lastState = state;
    }

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    //private static boolean isIncoming;
    //private static String savedNumber;  //because the passed incoming is only valid in ringing

    public void onReceive(@NonNull final Context context, @NonNull final Intent intent)
    {
        try
        {
            Bundle b = intent.getExtras();
            if ( b!=null)
            {
                String stateStr = b.getString(TelephonyManager.EXTRA_STATE);
                String number = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                long subId = intent.getLongExtra("subscription", 1);

                int state = 0;
                if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr))
                {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr))
                {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr))
                {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number, subId);
            }
        } catch (Exception e)
        {
            Report r = Report.getInstance(context);
            r.log(Report.NIVEAU.ERROR, "Erreur dans IncomingCallReceiver.onReceive");
            r.log(Report.NIVEAU.ERROR, e);
        }
    }
}
