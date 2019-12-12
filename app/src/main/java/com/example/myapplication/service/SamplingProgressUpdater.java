package com.example.myapplication.service;

import android.content.ContextWrapper;

class SamplingProgressUpdater extends ProgressUpdater {

    SamplingProgressUpdater(ContextWrapper contextWrapper, String upodateAction, String endAction, String variant) {
        super(contextWrapper, upodateAction, endAction, variant);
    }

    @Override
    String specificUpdateMessage(int value) {
        return ("sampling stage: " + value + "%");
    }
}
