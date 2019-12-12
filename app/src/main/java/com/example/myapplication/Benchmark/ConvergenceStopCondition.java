package com.example.myapplication.Benchmark;

import com.example.myapplication.service.ThresholdNotificator;

public class ConvergenceStopCondition implements StopCondition {
    private double convergenceThreshold;
    private ThresholdNotificator thresholdNotificator;

    public ConvergenceStopCondition(double convergenceThreshold, ThresholdNotificator thresholdNotificator) {
        this.convergenceThreshold = convergenceThreshold;
        this.thresholdNotificator = thresholdNotificator;
    }

    @Override
    public boolean canContinue() {
        return convergenceThreshold > thresholdNotificator.getCurrentLevel();
    }
}
