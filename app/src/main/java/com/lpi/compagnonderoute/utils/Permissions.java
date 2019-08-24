package com.lpi.compagnonderoute.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.lpi.compagnonderoute.R;

/***
 * Gestion des permissions de l'application
 */
public class Permissions
{
	@NonNull
	private static final String[] PERMISSIONS = {
			Manifest.permission.VIBRATE,
			Manifest.permission.READ_SMS,
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.RECEIVE_SMS,
			Manifest.permission.SEND_SMS,
			Manifest.permission.BLUETOOTH,
			Manifest.permission.MODIFY_AUDIO_SETTINGS,
			Manifest.permission.CALL_PHONE};

	/*******************************************************************************************************************
	 * Verifie que toutes les permissions demandées par l'application ont bien ete accordées et fait une demande au
	 * systeme si besoin
	 *******************************************************************************************************************/
	public static void demandePermissionsSiBesoin(final Activity activity)
	{
		if (verifiePermissions(activity))
			return;

		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
		dlgAlert.setMessage(R.string.ask_for_permissions);
		dlgAlert.setTitle(activity.getResources().getString(R.string.missing_permissions));
		dlgAlert.setCancelable(false);
		dlgAlert.setPositiveButton(activity.getResources().getString(R.string.grant_permissions_button),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						//dismiss the dialog
						ActivityCompat.requestPermissions(activity, PERMISSIONS, 0);
					}
				});

		dlgAlert.create().show();
	}

	/*******************************************************************************************************************
	 * Verifie que toutes les permissions demandées par l'application ont bien ete accordées
	 * @return false si au moins une permission n'est pas accordee
	 *******************************************************************************************************************/
	public static boolean verifiePermissions(Context context)
	{
		for (String p : PERMISSIONS)
			if (ActivityCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED)
				return false;

		return true;
	}
}
