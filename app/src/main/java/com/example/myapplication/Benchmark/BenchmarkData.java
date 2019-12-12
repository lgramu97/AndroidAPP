package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BenchmarkData {
    @SerializedName("benchmarkDefinitions")
    @Expose
    private List<BenchmarkDefinition> benchmarkDefinitions = null;
    @SerializedName("runOrder")
    @Expose
    private List<String> runOrder = null;

    public List<BenchmarkDefinition> getBenchmarkDefinitions() {
        return benchmarkDefinitions;
    }

    public void setBenchmarkDefinitions(List<BenchmarkDefinition> benchmarkDefinitions) {
        this.benchmarkDefinitions = benchmarkDefinitions;
    }

    public List<String> getRunOrder() {
        return runOrder;
    }

    public void setRunOrder(List<String> runOrder) {
        this.runOrder = runOrder;
    }

}
