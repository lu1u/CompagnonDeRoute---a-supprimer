package com.lpi.compagnonderoute;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import com.lpi.compagnonderoute.utils.Preferences;

public class ThemeActivity extends AppCompatActivity
{
    public static void start(Activity activity)
    {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("ResourceType") View dialogView = inflater.inflate(R.layout.activity_theme, null);

        final ImageButton theme1 = dialogView.findViewById(R.id.imageView1) ;
        final ImageButton theme2 = dialogView.findViewById(R.id.imageView2) ;
        final ImageButton theme3 = dialogView.findViewById(R.id.imageView3) ;
        final ImageButton theme4 = dialogView.findViewById(R.id.imageView4) ;
        final ImageButton theme5 = dialogView.findViewById(R.id.imageView5) ;
        final ImageButton theme6 = dialogView.findViewById(R.id.imageView6) ;

        Preferences prefs = Preferences.getInstance(activity);
        switch(prefs.getTheme())
        {
            case 1: theme2.setSelected(true); break;
            case 2: theme3.setSelected(true); break;
            case 3: theme4.setSelected(true); break;
            case 4: theme5.setSelected(true); break;
            case 5: theme6.setSelected(true); break;
            default: theme1.setSelected(true); break;
        }

        addListener(activity, dialogBuilder, theme1, 0) ;
        addListener(activity, dialogBuilder, theme2, 1) ;
        addListener(activity, dialogBuilder, theme3, 2) ;
        addListener(activity, dialogBuilder, theme4, 3) ;
        addListener(activity, dialogBuilder, theme5, 4) ;
        addListener(activity, dialogBuilder, theme6, 5) ;

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private static void addListener(@NonNull final Activity activity, @NonNull final AlertDialog dialogBuilder, @NonNull final ImageButton button, final int theme)
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
