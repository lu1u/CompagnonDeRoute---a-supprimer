package com.lpi.compagnonderoute.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.lpi.compagnonderoute.R;


/**
 * Fonctions utilitaires diverses
 */
public class Utils
{
	public static void addHint(final Activity a, @IdRes int id, final String message)
	{
		View v = a.findViewById(id);
		if (v == null)
			return;
		addHint(a, v, message);
	}

	public static void addHint(final Activity a, View v, final String message)
	{
		v.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
					//Toast.makeText(v.getAuthentification(), ic_message, Toast.LENGTH_SHORT).set.show();
					displayToastAboveButton(a, v, message);
			}
		});
	}

	private static void displayToastAboveButton(Activity a, View v, String message)
	{
	/*
	int xOffset = 0;
	int yOffset = 0;
	Rect gvr = new Rect();

	View parent = (View) v.getParent();
	int parentHeight = parent.getHeight();

	if (v.getGlobalVisibleRect(gvr))
	{
		View root = v.getRootView();

		int halfWidth = root.getRight() / 2;
		int halfHeight = root.getBottom() / 2;

		int parentCenterX = ((gvr.right - gvr.left) / 2) + gvr.left;

		int parentCenterY = ((gvr.bottom - gvr.top) / 2) + gvr.top;

		if (parentCenterY <= halfHeight)
		{
			yOffset = -(halfHeight - parentCenterY) - parentHeight;
		}
		else
		{
			yOffset = (parentCenterY - halfHeight) - parentHeight;
		}

		if (parentCenterX < halfWidth)
		{
			xOffset = -(halfWidth - parentCenterX);
		}

		if (parentCenterX >= halfWidth)
		{
			xOffset = parentCenterX - halfWidth;
		}
	}

	LayoutInflater inflater = a.getLayoutInflater();
	View layout = inflater.inflate(R.layout.hint_toast_layout, (ViewGroup) a.findViewById(R.id.layoutRoot));
	TextView text = (TextView) layout.findViewById(R.id.text);
	text.setText(message);

	Toast toast = Toast.makeText(a, message, Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER, xOffset, yOffset);
	toast.setView(layout);
	toast.show();
	*/
		Snackbar.make(v, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}

	/***
	 * Change le theme d'une activity.
	 * Le theme est decrit dans values/styles.xml de type <array>
	 * La liste des themes disponibles est stockee dans un int array de values/array.xml
	 * @param a
	//	 * @param themesArray : idRes du tableau des themes
	 */
	public static void setTheme(@NonNull Activity a)//, @ArrayRes  int themesArray )
	{
		a.setTheme(getTheme(a));
	}

	public static int getTheme(@NonNull Context context)
	{
		switch (Preferences.getInstance(context).getTheme())
		{
			case 1:
				return R.style.Theme2;
			case 2:
				return R.style.Theme3;
			case 3:
				return R.style.Theme4;
			case 4:
				return R.style.Theme5;
			case 5:
				return R.style.Theme6;
			case 6:
				return R.style.Theme7;
			case 7:
				return R.style.Theme8;
			default:
				return R.style.Theme1;
		}
	}


	/***
	 * Affiche un message de confirmation
	 * Appelle une des methodes du ComfirmListener pour donner le resultat
	 * @param a
	 * @param titre
	 * @param message
	 * @param requestCode
	 * @param listener
	 */
	public static void confirmDialog(@NonNull Activity a, @NonNull String titre, @NonNull String message, final int requestCode, final @NonNull ConfirmListener listener)
	{
		new AlertDialog.Builder(a)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(titre)
				.setMessage(message)
				.setPositiveButton(a.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						listener.onConfirmOK(requestCode);
					}

				})
				.setNegativeButton(a.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						listener.onConfirmCancel(requestCode);
					}

				})
				.show();
	}


	/***
	 * Listener pour la methode confirmDialog
	 */
	public interface ConfirmListener
	{
		void onConfirmOK(int requestCode);

		void onConfirmCancel(int requestCode);
	}
}
