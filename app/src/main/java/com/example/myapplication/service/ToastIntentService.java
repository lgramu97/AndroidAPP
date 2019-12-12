package com.example.myapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;

public class ToastIntentService extends IntentService {
    public static final String TOAST_ACTION = "toast";
    private static final long TOASTS_INTERVAL = 2000;
    public static volatile ArrayDeque<String> toasts = new ArrayDeque<>();
    public static volatile long timeOfLastToast = 0L;


    public ToastIntentService() {
        super("ToastIntentService");
    }

    public static void createToasts(String msg) {
        synchronized (toasts) {
            toasts.add(msg);
            toasts.notifyAll();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            synchronized (toasts) {
                if (toasts.isEmpty()) {
                    try {
                        toasts.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            String msg;
            synchronized (toasts) {
                msg = toasts.remove();
            }
            if (System.currentTimeMillis() - timeOfLastToast < TOASTS_INTERVAL) {
                try {
                    Thread.sleep(TOASTS_INTERVAL - (System.currentTimeMillis() - timeOfLastToast));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timeOfLastToast = System.currentTimeMillis();
            Intent intent2 = new Intent();
            intent2.putExtra("msg", msg);
            intent2.setAction(TOAST_ACTION);
            this.sendBroadcast(intent2);
        }
    }
}
