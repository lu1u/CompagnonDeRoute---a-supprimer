package com.lpi.compagnonderoute;
/***
 * Affiche une AlertDialog pour choisir un theme de couleurs
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.lpi.compagnonderoute.utils.Preferences;

public class ThemeActivity extends AppCompatActivity
{
    public static void start(@NonNull final Activity activity)
    {
	    View dialogView = activity.getLayoutInflater().inflate(R.layout.activity_theme, null);

        final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme1_1), 0);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme1_2), 0);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme2_1), 1);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme2_2), 1);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme3_1), 2);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme3_2), 2);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme4_1), 3);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme4_2), 3);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme5_1), 4);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme5_2), 4);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme6_1), 5);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme6_2), 5);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme7_1), 6);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme7_2), 6);

	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme8_1), 7);
	    addListener(activity, dialogBuilder, dialogView.findViewById(R.id.imageButtonTheme8_2), 7);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


	private static void addListener(@NonNull final Activity activity, @NonNull final AlertDialog dialogBuilder, @NonNull final View button, final int theme)
    {
        button.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View v)
                                      {
                                          Preferences prefs = Preferences.getInstance(activity);
                                          prefs.setTheme(theme);
                                          prefs.flush(activity);
                                          dialogBuilder.dismiss();
                                          TaskStackBuilder.create(activity)
                                                  .addNextIntent(new Intent(activity, MainActivity.class))
                                                  .addNextIntent(activity.getIntent())
                                                  .startActivities();
                                      }
                                  }
        );
    }
}
