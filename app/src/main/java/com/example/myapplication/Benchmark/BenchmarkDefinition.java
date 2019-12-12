package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BenchmarkDefinition {

    @SerializedName("benchmarkId")
    @Expose
    private String benchmarkId;
    @SerializedName("benchmarkClass")
    @Expose
    private String benchmarkClass;
    @SerializedName("variants")
    @Expose
    private List<Variant> variants = null;

    public String getBenchmarkId() {
        return benchmarkId;
    }

    public void setBenchmarkId(String benchmarkId) {
        this.benchmarkId = benchmarkId;
    }

    public String getBenchmarkClass() {
        return benchmarkClass;
    }

    public void setBenchmarkClass(String benchmarkClass) {
        this.benchmarkClass = benchmarkClass;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

}