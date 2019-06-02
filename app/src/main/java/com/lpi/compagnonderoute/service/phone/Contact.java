package com.lpi.compagnonderoute.service.phone;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Contact
{
	/**
	 * Essaie de retrouver le nom d'un contact a partir de son numero de telephone
	 *
	 * @param numero
	 *            : numero appelant
	 * @return le nom du contact ou "numero inconnu "+numero
	 */
	public static String getContactFromNumber(Context context, String numero)
	{
		String res;

		try
		{
			String[] COLONNES_NUMERO = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
			Cursor c = context.getContentResolver().query( Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numero)), COLONNES_NUMERO, null,
					null, null);
			c.moveToFirst();
			res = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
			c.close();

		} catch (Exception e)
		{
			if (numero.startsWith("+33")) //$NON-NLS-1$
			{
				numero = "0" + numero.substring(3); //$NON-NLS-1$
				return getContactFromNumber(context, numero);
			} else
				res = null;
		}

		return res;
	}
}
