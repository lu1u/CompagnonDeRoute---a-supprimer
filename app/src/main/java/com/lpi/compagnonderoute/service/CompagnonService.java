package com.lpi.compagnonderoute.service;
/***
 * Service pour recevoir les appels et SMS
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.lpi.compagnonderoute.service.phone.IncomingCallReceiver;
import com.lpi.compagnonderoute.service.phone.SMSBroadcastReceiver;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

public class CompagnonService extends Service
{
	@NonNull public static final String ACTION_DEMARRAGE = CompagnonService.class.getName() + "DEMARRAGE";
	@NonNull public static final String ACTION_ARRET = CompagnonService.class.getName() + "ARRET";

	@Nullable SMSBroadcastReceiver _smsBroadcastReceiver ;
	@Nullable IncomingCallReceiver _incomingCallReceiver;
	public CompagnonService()
	{
	}

	/***
	 * Demarrage du service
	 * @param context
	 */
	public static void start(@NonNull final Context context)
	{
		Preferences prefs = Preferences.getInstance(context);
		if (prefs.isEnCours())
			if (prefs.getRepondreAppels() != Preferences.ENTRANT.JAMAIS && prefs.getLireSMS() != Preferences.ENTRANT.JAMAIS && prefs.getRepondreSMS() != Preferences.ENTRANT.JAMAIS)
			{
				Report r = Report.getInstance(context);
				r.log(Report.NIVEAU.DEBUG, "Lancement du service");
				r.historique("Lancement du service");
				Intent intent = new Intent(context, CompagnonService.class);
				intent.setAction(ACTION_DEMARRAGE);
				context.startService(intent);
			}
	}

	/***
	 * Arreter le service
	 * @param context
	 */
	public static void stop(@NonNull final Context context)
	{
		Report r = Report.getInstance(context);
		r.log(Report.NIVEAU.DEBUG, "Arret du service");
		r.historique("Arret du service");
		Intent intent = new Intent(context, CompagnonService.class);
		intent.setAction(ACTION_ARRET);
		context.startService(intent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO: Return the communication channel to the service.
		return null;
	}

	@Override
	public int onStartCommand(@NonNull Intent intent, int flags, int startId)
	{
		Report r = Report.getInstance(this);
		try
		{
			super.onStartCommand(intent, flags, startId);
			final String action = intent.getAction();
			r.log(Report.NIVEAU.DEBUG, "service onCommand " + action);

			if (ACTION_DEMARRAGE.equals(action))
			{
				// Demarre le service
				handleActionDemarre();
				return START_STICKY;
			}
			else
				if (ACTION_ARRET.equals(action))
				{
					// Arrete ce service
					handleActionStop();
				}
		} catch (Exception e)
		{
			r.log(Report.NIVEAU.ERROR, "Erreur dans CompagnonService.onStartCommand");
			r.log(Report.NIVEAU.ERROR, e);
		}

		return START_NOT_STICKY;
	}

	/***
	 * Gestion de l'arret du service
	 */
	private void handleActionStop()
	{
		Report.getInstance(this).log(Report.NIVEAU.DEBUG, "handleActionStop");

		// Arreter reception SMS
		if ( _smsBroadcastReceiver != null)
		{
			unregisterReceiver(_smsBroadcastReceiver);
			_smsBroadcastReceiver = null;
		}

		// Arreter reception appels
		if ( _incomingCallReceiver!=null)
		{
			unregisterReceiver(_incomingCallReceiver);
			_incomingCallReceiver = null;
		}

		// Arreter ce service
		this.stopSelf();
	}

	private void handleActionDemarre()
	{
		Report.getInstance(this).log(Report.NIVEAU.DEBUG, "handleActionDemarre");
		Preferences prefs = Preferences.getInstance(this);
		if ((prefs.getLireSMS() != Preferences.ENTRANT.JAMAIS) || (prefs.getRepondreSMS() != Preferences.ENTRANT.JAMAIS))
		{
			_smsBroadcastReceiver = new SMSBroadcastReceiver();
			registerReceiver(_smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
		}

		if (prefs.getRepondreAppels() != Preferences.ENTRANT.JAMAIS)
		{
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
			{
				//	SubscriptionManager subManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
				//	List<SubscriptionInfo> subInfoList = subManager.getActiveSubscriptionInfoList();
				//for (int i = 0; i < subInfoList.size(); i++)
				//{
				//	int subID = subInfoList.get(i).getSubscriptionId();
				//	int simPosition = subInfoList.get(i).getSimSlotIndex();
				//
				//	if (subManager.isNetworkRoaming(subID))
				//		Log.d("TEST", "Simcard in slot " + simPosition + " has subID == " + subID + " and it is in ROAMING");
				//	else
				//		Log.d("TEST", "Simcard in slot " + simPosition + " has subID == " + subID + " and it is HOME");
				//}

			_incomingCallReceiver = new IncomingCallReceiver();
			registerReceiver(_incomingCallReceiver, new IntentFilter(Intent.ACTION_CALL));
			}
		}
	}
}
