package com.lpi.compagnonderoute;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling compagnon notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class CompagnonNotification
{
	/**
	 * The unique identifier for this type of notification.
	 */
	@NonNull
	private static final String CHANNEL_ID = "Compagnon01";
	@NonNull private static final String NOTIFICATION_TAG = "Compagnon";

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 * @see #cancel(Context)
	 */
	public static void notify(@NonNull final Context context, @NonNull final String texte,@NonNull final String texteResume)
	{
		Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

		NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText(texteResume);
		bigText.setBigContentTitle(texte);
		bigText.setSummaryText("Cliquez pour ouvrir l'application");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(R.drawable.ic_compagnon);
		builder.setContentTitle("Compagnon");
		builder.setContentText(texte);
		builder.setStyle(bigText);
		builder.setSound(null);

		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
		{
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Compagnon", NotificationManager.IMPORTANCE_LOW);
			channel.setSound(null, null);
			channel.setShowBadge(false);
			if (nm != null)
				nm.createNotificationChannel(channel);
			builder.setChannelId(CHANNEL_ID);
		}

		if (nm != null)
			nm.notify(NOTIFICATION_TAG, 0, builder.build());
	}



	/**
	 * Cancels any notifications of this type previously shown using
	 */
	public static void cancel(final Context context)
	{
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (nm != null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
			{
				nm.cancel(NOTIFICATION_TAG, 0);
			} else
			{
				nm.cancel(NOTIFICATION_TAG.hashCode());
			}
		}
	}
}
