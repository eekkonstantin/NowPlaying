/*
 * Copyright 2015 kkontagion
 * Contact: eekkonstantin@gmail.com
 *
 * This file is part of the NowPlaying project, under GNU GPLv3.
 * The files in the project are free for distribution, modification, and commercial use.
 * Any use of this file must have the copyright and license notices preserved.
 */

package com.kkontagion.nowplaying;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class WidgetReceiver extends BroadcastReceiver {

    private static final String SHARENAME = "com.twitter.android";
    public static final String SHARE = "SHARE";

    public WidgetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == SHARE) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(SHARENAME);
            if (intent != null) {
                // The application exists
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage(SHARENAME);

                SharedPreferences sp = context.getSharedPreferences("Saved", Context.MODE_PRIVATE);
                String output = sp.getString("now", "No music");
                shareIntent.putExtra(Intent.EXTRA_TEXT, output);
                shareIntent.setType("text/plain");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Start the specific social application
                context.startActivity(shareIntent);
            } else {
                // The application does not exist
                // Open GooglePlay or use the default system picker
            }
        }
    }
}
