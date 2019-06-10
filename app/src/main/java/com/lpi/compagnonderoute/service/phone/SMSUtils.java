package com.lpi.compagnonderoute.service.phone;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

public class SMSUtils
{
	public static void send(@NonNull final Context context, @NonNull final String adress, @NonNull final String message, int subscriptionId)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
		{
			try
			{
				if (Build.VERSION.SDK_INT >= 22)
				{
					SubscriptionManager subscriptionManager = (context).getSystemService(SubscriptionManager.class);
					SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId);
					SmsManager smsManager;
					if (subscriptionInfo == null)
						smsManager = SmsManager.getDefault();
					else
						smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.getSubscriptionId());

					if (smsManager != null)
					{
						PendingIntent i1 = PendingIntent.getBroadcast(context, 0, new Intent("sent"), 0);
						PendingIntent i2 = PendingIntent.getBroadcast(context, 0, new Intent("received"), 0);
						smsManager.sendTextMessage(adress, null, message, i1, i2);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
