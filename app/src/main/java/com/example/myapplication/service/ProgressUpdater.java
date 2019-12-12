package com.example.myapplication.service;

import android.content.ContextWrapper;
import android.content.Intent;

public abstract class ProgressUpdater {
    private ContextWrapper contextWrapper;
    private String upodateAction;
    private String endAction;
    private String variant;

    ProgressUpdater(ContextWrapper contextWrapper, String upodateAction, String endAction, String variant) {
        this.contextWrapper = contextWrapper;
        this.upodateAction = upodateAction;
        this.endAction = endAction;
        this.variant = variant;
    }

    public void update(int progress) {
        Intent intent = new Intent();
        intent.setAction(upodateAction);
        String message = specificUpdateMessage(progress);
        intent.putExtra("progress", message);
        contextWrapper.sendBroadcast(intent);
    }

    public void end(String payload) {
        Intent intent = new Intent();
        intent.putExtra("payload", payload);
        intent.putExtra("variant", variant);
        intent.setAction(endAction);
        contextWrapper.sendBroadcast(intent);

    }

    abstract String specificUpdateMessage(int value);
}
