package com.example.myapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import static com.example.myapplication.MainActivity.POLLING_INTERVAL;

public class PollingIntentService extends IntentService {
    public static final String POLLING_ACTION = "polling";
    private static volatile Boolean shouldContinue = true;

    public PollingIntentService() {
        super("PollingIntentService");
    }

    public static boolean isShouldContinue() {
        synchronized (shouldContinue) {
            return shouldContinue;
        }
    }

    public static void setShouldContinue(Boolean shouldContinue) {
        synchronized (PollingIntentService.shouldContinue) {
            PollingIntentService.shouldContinue = shouldContinue;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ServerConnection serverConnection = ServerConnection.getService();
        while (isShouldContinue()) {
            Intent intent2 = new Intent();
            intent2.setAction(POLLING_ACTION);
            this.sendBroadcast(intent2);
            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }
}
