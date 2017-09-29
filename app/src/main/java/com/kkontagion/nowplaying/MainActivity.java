/*
 * Copyright 2015 kkontagion
 * Contact: eekkonstantin@gmail.com
 *
 * This file is part of the NowPlaying project, under GNU GPLv3.
 * The files in the project are free for distribution, modification, and commercial use.
 * Any use of this file must have the copyright and license notices preserved.
 */

package com.kkontagion.nowplaying;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SHARENAME = "com.twitter.android";

    Button btShare, btSave;
    EditText etFormat;
    TextView tvDemo;

    @Override
    protected void onPause() {
        super.onPause();
//        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
        for (int i=0; i<rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            Log.i("Service", "Process " + rsi.process + " with component " + rsi.service.getClassName());
        }


        Intent rcv = getIntent();
        if (rcv.getBooleanExtra("fromWidget", false)) {
            share(true);
        }

        IntentFilter iF = new IntentFilter();

        iF.addAction("com.android.music.metachanged");
//        iF.addAction("com.android.music.playstatechanged");
//        iF.addAction("com.android.music.playbackcomplete");
//        iF.addAction("com.android.music.queuechanged");

        registerReceiver(mReceiver, iF);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        fab.setVisibility(View.GONE);

        build();
        postBuild();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("fuck", action);
            if (action.equals("com.android.music.metachanged")) {
                String artist = intent.getStringExtra("artist");
                String album = intent.getStringExtra("album");
                String track = intent.getStringExtra("track");

                Log.d("fuck", artist + ":" + album + ":" + track);
                SharedPreferences.Editor spE = getSharedPreferences("Saved", MODE_PRIVATE).edit();
                spE.putString("artist", artist);
                spE.putString("album", album);
                spE.putString("song", track);
                spE.commit();

                formatOut();
                updateWidgets();
            } else {

            }
        }
    };


    private void build() {
        btShare = (Button) findViewById(R.id.bt_share);
        btSave = (Button) findViewById(R.id.bt_save);
        etFormat = (EditText) findViewById(R.id.et_format);
        tvDemo = (TextView) findViewById(R.id.tv_demo);
    }

    private void postBuild() {
        SharedPreferences sp = getSharedPreferences("Saved", MODE_PRIVATE);
        etFormat.setText(sp.getString("format", "#np <song> - <artist>"));

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSave = etFormat.getText().toString();
                SharedPreferences.Editor spE = getSharedPreferences("Saved", MODE_PRIVATE).edit();
                spE.putString("format", toSave);
                if (spE.commit()) {
                    formatOut();

                    Toast.makeText(getApplicationContext(),"Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Error saving.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(false);
            }
        });
    }


    private String formatOut() {
        SharedPreferences sp = getSharedPreferences("Saved", MODE_PRIVATE);
        String f = sp.getString("format", "#np <song> - <artist>");

        String song = sp.getString("song", "<song>");
        String album = sp.getString("album", "<album>");
        String artist = sp.getString("artist", "<artist>");

        f = f.replace("<song>", song);
        f = f.replace("<album>", album);
        f = f.replace("<artist>", artist);
        Log.d("fuck", f);

        tvDemo.setText(f);
        SharedPreferences.Editor spE = sp.edit();
        spE.putString("now", f);
        spE.commit();

        return f;
    }


    private void updateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(), NPWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            Intent intent = new Intent(this, NPWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(intent);
        }
    }

    private void share(boolean close) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(SHARENAME);
        if (intent != null) {
            // The application exists
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(SHARENAME);

            if (close) {
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Log.d("fuck", "flag set");
            }

            SharedPreferences sp = getSharedPreferences("Saved", MODE_PRIVATE);
            String output = sp.getString("now", "No music");
            shareIntent.putExtra(Intent.EXTRA_TEXT, output);
            shareIntent.setType("text/plain");

            // Start the specific social application
            startActivity(shareIntent);
            if (close) {
                finish();
            }
        } else {
            // The application does not exist
            // Open GooglePlay or use the default system picker
        }
    }
}
