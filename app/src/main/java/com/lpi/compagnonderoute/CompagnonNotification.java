package com.lpi.compagnonderoute;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling compagnon
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class CompagnonNotification
{
	/**
	 * The unique identifier for this type of notification.
	 */
	private static final String NOTIFICATION_TAG = "Compagnon";

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 * <p>
	 * TODO: Customize this method's arguments to present relevant content in
	 * the notification.
	 * <p>
	 * TODO: Customize the contents of this method to tweak the behavior and
	 * presentation of compagnon notifications. Make
	 * sure to follow the
	 * <a href="https://developer.android.com/design/patterns/notifications.html">
	 * Notification design guidelines</a> when doing so.
	 *
	 * @see #cancel(Context)
	 */
	public static void notify(final Context context,
	                          final String texte,
	                          final String texteResume)
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
		builder.setPriority(Notification.PRIORITY_LOW);
		builder.setStyle(bigText);

		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


//===removed some obsoletes
		if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
		{
			String channelId = "Your_channel_id";
			NotificationChannel channel = new NotificationChannel(channelId, "Compagnon", NotificationManager.IMPORTANCE_HIGH);
			channel.setSound(null, null);
			channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
			nm.createNotificationChannel(channel);
			builder.setChannelId(channelId);
		}

		nm.notify(NOTIFICATION_TAG, 0, builder.build());
	}



	/**
	 * Cancels any notifications of this type previously shown using
	 */
	public static void cancel(final Context context)
	{
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
		{
			nm.cancel(NOTIFICATION_TAG, 0);
		}
		else
		{
			nm.cancel(NOTIFICATION_TAG.hashCode());
		}
	}
}
