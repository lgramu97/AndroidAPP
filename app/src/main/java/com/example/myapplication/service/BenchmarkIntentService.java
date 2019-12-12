package com.example.myapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.myapplication.Benchmark.BatteryStopCondition;
import com.example.myapplication.Benchmark.Benchmark;
import com.example.myapplication.Benchmark.Variant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BenchmarkIntentService extends IntentService {

    public static final String PROGRESS_BENCHMARK_ACTION = "progressBenchmark";
    public static final String END_BENCHMARK_ACTION = "endBenchmark";

    public BenchmarkIntentService() {
        super("BenchmarkIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Benchmark benchmark = null;
        BatteryNotificator batteryNotificator = BatteryNotificator.getInstance();
        try {
            Class<Benchmark> benchmarkClass = (Class<Benchmark>) Class.forName(intent.getStringExtra("benchmarkName"));
            benchmark = benchmarkClass.getConstructor(Variant.class).newInstance(gson.fromJson(intent.getStringExtra("benchmarkVariant"), Variant.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProgressUpdater progressUpdater = new BenchMarckProgressUpdater(this, PROGRESS_BENCHMARK_ACTION, END_BENCHMARK_ACTION, benchmark.getVariant().getVariantId());
        benchmark.runBenchmark(new BatteryStopCondition(benchmark.getVariant().getEnergyPreconditionRunStage().getMinEndBatteryLevel(), batteryNotificator), progressUpdater);

    }
}
