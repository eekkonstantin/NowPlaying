/*
 * Copyright 2015 kkontagion
 * Contact: eekkonstantin@gmail.com
 *
 * This file is part of the NowPlaying project, under GNU GPLv3.
 * The files in the project are free for distribution, modification, and commercial use.
 * Any use of this file must have the copyright and license notices preserved.
 */

package com.kkontagion.nowplaying;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NPWidget extends AppWidgetProvider {

    private static final String SHARENAME = "com.twitter.android";
    Button btGo;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sp = context.getSharedPreferences("Saved", Context.MODE_PRIVATE);
        String np = sp.getString("now", "No music playing.");

        Intent i = new Intent();
        i.setAction(WidgetReceiver.SHARE);
        i.setClassName(WidgetReceiver.class.getPackage().getName(), WidgetReceiver.class.getName());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.npwidget);
        views.setTextViewText(R.id.tv_widget_np, np);
        views.setOnClickPendingIntent(R.id.bt_long, pi);
//        views.setOnClickPendingIntent(R.id.rl_widget, pi);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

