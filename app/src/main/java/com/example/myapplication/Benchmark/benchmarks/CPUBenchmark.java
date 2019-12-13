package com.example.myapplication.Benchmark.benchmarks;

import android.os.Environment;

import com.example.myapplication.Benchmark.Benchmark;
import com.example.myapplication.Benchmark.StopCondition;
import com.example.myapplication.Benchmark.Variant;
import com.example.myapplication.service.ProgressUpdater;
import com.example.myapplication.utils.AutoTuner;
import com.example.myapplication.utils.Logger;


public class CPUBenchmark extends Benchmark {

    private static final String EMPTY_PAYLOAD = "empty";
    private AutoTuner autoTuner;
    private Logger logger;
    private StringBuilder out = new StringBuilder();

    public CPUBenchmark(Variant variant) {
        super(variant);
        logger=new Logger(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CpuBatProfile.txt");
        autoTuner = new AutoTuner();
        autoTuner.setCPUs(1); // por defecto en la otra app era 1.
        autoTuner.setTarget(variant.getParamsRunStage().getCpuLevel().floatValue());
        autoTuner.setThreshold(variant.getParamsSamplingStage().getConvergenceThreshold().floatValue());
        autoTuner.setListener(new AutoTuner.TunerListener() {

            @Override
            public void onUnStable(float cpuUsage) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStable(float cpuUsage) {
            }

            @Override
            public StringBuilder onCPUUsageRead(float cpuUsage, long sleep) {
                out.append("CPUUsage: "+cpuUsage+ " Sleep: "+sleep + "\n");
                System.out.println(out);
                logger.write("CPUUsage: "+cpuUsage+ " Sleep: "+sleep);
                return out;
            }
        });
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
        progressUpdater.end(out.toString());
    }

    @Override
    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        System.out.println("HOLAA ");
        autoTuner.start();
        while (stopCondition.canContinue() && progress < 40) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress += 5;
            progressUpdater.update(progress);
        }
        progressUpdater.end(out.toString());
    }
}

