package com.example.myapplication.service;

import android.content.ContextWrapper;

class BenchMarckProgressUpdater extends ProgressUpdater {

    BenchMarckProgressUpdater(ContextWrapper contextWrapper, String upodateAction, String endAction, String variant) {
        super(contextWrapper, upodateAction, endAction, variant);
    }

    @Override
    String specificUpdateMessage(int value) {
        return ("run stage: " + value + "%");
    }
}
