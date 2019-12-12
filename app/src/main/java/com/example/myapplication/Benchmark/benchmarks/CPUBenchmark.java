package com.example.myapplication.Benchmark.benchmarks;

import com.example.myapplication.Benchmark.Benchmark;
import com.example.myapplication.Benchmark.StopCondition;
import com.example.myapplication.Benchmark.Variant;
import com.example.myapplication.service.ProgressUpdater;


public class CPUBenchmark extends Benchmark {

    private static final String EMPTY_PAYLOAD = "empty";

    public CPUBenchmark(Variant variant) {
        super(variant);
    }

    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        while (stopCondition.canContinue() && progress < 40) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress += 5;
            progressUpdater.update(progress);
        }
        progressUpdater.end(EMPTY_PAYLOAD);
    }

    @Override
    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        while (stopCondition.canContinue() && progress < 40) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress += 5;
            progressUpdater.update(progress);
        }
        progressUpdater.end(EMPTY_PAYLOAD);
    }
}

