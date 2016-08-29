package dataservicios.com.ttauditalicorp.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;



import dataservicios.com.ttauditalicorp.app.AppController;

/**
 * Created by Jaime on 25/08/2016.
 */
public class UpdateServices extends Service {
    private final String TAG = UpdateServices.class.getSimpleName();
    private final Integer contador = 0;

    private Context context = this;

    static final int DELAY = 3000;
    private boolean runFlag = false;
    private Updater updater;

    private AppController application;

   // private DBOperations dbOperations;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        application = (AppController) getApplication();
        updater = new Updater();
        //dbOperations = new DBOperations(this);
        Log.d(TAG, "onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runFlag = false;
        application.setServiceRunningFlag(false);
        updater.interrupt();
        updater = null;

        Log.d(TAG, "onDestroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag){
            runFlag = true;
            application.setServiceRunningFlag(true);
            updater.start();
        }

        Log.d(TAG, "onStarted");
        return START_STICKY;
    }

    private class Updater extends Thread {
        public Updater(){
            super("UpdaterService-UpdaterThread");
        }


        @Override
        public void run() {

            UpdateServices updaterService = UpdateServices.this;
            while (updaterService.runFlag) {
                Log.d(TAG, "UpdaterThread running");
                try{
                   // timeline = TwitterUtils.getTimelineForSearchTerm(GlobalConstants.DATASERVICIOS_TERM);

                    ContentValues values = new ContentValues();
//                    for(Tweet tweet : timeline){
//                        values.clear();
//                        values.put(DBHelper.C_ID, tweet.getId());
//                        values.put(DBHelper.C_NAME, tweet.getName());
//                        values.put(DBHelper.C_SCREEN_NAME, tweet.getScreenName());
//                        values.put(DBHelper.C_IMAGE_PROFILE_URL, tweet.getProfileImageUrl());
//                        values.put(DBHelper.C_TEXT, tweet.getText());
//                        values.put(DBHelper.C_CREATED_AT, tweet.getCreatedAt());
//
//                        Log.i(TAG, "CREATED AT_SERVICE: " + tweet.getCreatedAt());
//
//                        dbOperations.insertOrIgnore(values);
//                    }

                   // Toast.makeText(context,"Segundo plano",Toast.LENGTH_SHORT);
                    Log.i(TAG, "CREATED AT_SERVICE: ");

                    Thread.sleep(DELAY);
                }catch(InterruptedException e){
                    updaterService.runFlag = false;
                    application.setServiceRunningFlag(true);
                }

            }
        }


    }
}
