package com.example.myapplication.utils;

import com.example.myapplication.Benchmark.BenchmarkData;
import com.example.myapplication.Benchmark.BenchmarkDefinition;
import com.example.myapplication.Benchmark.EnergyPreconditionRunStage;
import com.example.myapplication.Benchmark.EnergyPreconditionSamplingStage;
import com.example.myapplication.Benchmark.ParamsRunStage;
import com.example.myapplication.Benchmark.ParamsSamplingStage;
import com.example.myapplication.Benchmark.Variant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReaderJson {
    private BenchmarkData benchmarkData;
    private BenchmarkDefinition benchmarkDefinition;
    private Variant variant;
    private String path;

    public ReaderJson(String file){
        this.path = file;
        variant = new Variant();
    }


    public BenchmarkData getBenchmarkData() {
        return benchmarkData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    /*
    * Reveer forma de obtener datos de benchmarkConfigFull.json
    * Obtener desde el servidor.
    * */
    public void readAndSet() throws JSONException {
    // Read from .json and generate BenchmarkData.
          String json = "{\n" +
                  "\"benchmarkDefinitions\" : [\n" +
                  "\t{\n" +
                  "\t\t\"benchmarkId\" : \"cpu\",\n" +
                  "\t\t\"benchmarkClass\" : \"benchmarks.CPUBenchmark\",\n" +
                  "\t\t\"variants\" : [\n" +
                  "\t\t\t{ \n" +
                  "\t\t\t  \"variantId\" : \"cpu.1\", \n" +
                  "\t\t\t  \"paramsSamplingStage\" : {\"convergenceThreshold\" : 0.01}, \n" +
                  "\t\t\t  \"paramsRunStage\" : {\"cpuLevel\": 1.0, \"screenState\": \"off\"},\n" +
                  "\t\t\t  \"energyPreconditionSamplingStage\" : {\"requiredBatteryState\" : \"charging\", \"minStartBatteryLevel\" : 0.1}, \n" +
                  "\t\t\t  \"energyPreconditionRunStage\" : {\"requiredBatteryState\" : \"discharging\", \"minStartBatteryLevel\" : 1.0, \"minEndBatteryLevel\" : 0.05}\n" +
                  "\t\t\t}, \n" +
                  "\n" +
                  "\t\t\t{ \n" +
                  "\t\t\t  \"variantId\" : \"cpu.2\", \n" +
                  "\t\t\t  \"paramsSamplingStage\" : {\"convergenceThreshold\" : 0.01}, \n" +
                  "\t\t\t  \"paramsRunStage\" : {\"cpuLevel\": 1.0, \"screenState\": \"on\"},\n" +
                  "\t\t\t  \"energyPreconditionSamplingStage\" : {\"requiredBatteryState\" : \"charging\", \"minStartBatteryLevel\" : 0.1}, \n" +
                  "\t\t\t  \"energyPreconditionRunStage\" : {\"requiredBatteryState\" : \"discharging\", \"minStartBatteryLevel\" : 1.0, \"minEndBatteryLevel\" : 0.05}\n" +
                  "\t\t\t}, \n" +
                  "\n" +
                  "\t\t\t{ \n" +
                  "\t\t\t  \"variantId\" : \"cpu.3\", \n" +
                  "\t\t\t  \"paramsSamplingStage\" : {\"convergenceThreshold\" : 0.015}, \n" +
                  "\t\t\t  \"paramsRunStage\" : {\"cpuLevel\": 0.75, \"screenState\": \"off\"},\n" +
                  "\t\t\t  \"energyPreconditionSamplingStage\" : {\"requiredBatteryState\" : \"charging\", \"minStartBatteryLevel\" : 0.1}, \n" +
                  "\t\t\t  \"energyPreconditionRunStage\" : {\"requiredBatteryState\" : \"discharging\", \"minStartBatteryLevel\" : 1.0, \"minEndBatteryLevel\" : 0.05}\n" +
                  "\t\t\t},\n" +
                  "\n" +
                  "\t\t\t{ \n" +
                  "\t\t\t  \"variantId\" : \"cpu.4\", \n" +
                  "\t\t\t  \"paramsSamplingStage\" : {\"convergenceThreshold\" : 0.015}, \n" +
                  "\t\t\t  \"paramsRunStage\" : {\"cpuLevel\": 0.75, \"screenState\": \"on\"},\n" +
                  "\t\t\t  \"energyPreconditionSamplingStage\" : {\"requiredBatteryState\" : \"charging\", \"minStartBatteryLevel\" : 0.1}, \n" +
                  "\t\t\t  \"energyPreconditionRunStage\" : {\"requiredBatteryState\" : \"discharging\", \"minStartBatteryLevel\" : 1.0, \"minEndBatteryLevel\" : 0.05}\n" +
                  "\t\t\t}\n" +
                  "\t\t]\n" +
                  "\t} \n" +
                  "],\n" +
                  "\n" +
                  "\"runOrder\" : [\n" +
                  "\t\t\"cpu.1\", \"cpu.2\", \"cpu.3\", \"cpu.4\" \n" +
                  "\t]\n" +
                  "}\n";
          JSONObject mainObject = new JSONObject(json);
          JSONArray benchmarkDef = mainObject.getJSONArray("benchmarkDefinitions");
          List<BenchmarkDefinition> definitions = new ArrayList<BenchmarkDefinition>();
          for (int i = 0; i<benchmarkDef.length();i++){
              JSONObject benchmarkObj = benchmarkDef.getJSONObject(i);
              String  benchmarkId =  benchmarkObj.getString("benchmarkId");
              String benchmarkClass = benchmarkObj.getString("benchmarkClass");
              JSONArray benchmarkVar = benchmarkObj.getJSONArray("variants");
              System.out.println("VALORES; ---------------------------------------");
              System.out.println("Benchmarck id  " + benchmarkId);
              System.out.println("BenchmarckClass  " + benchmarkClass);

              List<Variant> variants = new ArrayList<Variant>();
              for (int j = 0; j<benchmarkVar.length() ; j++) {
                  JSONObject var = benchmarkVar.getJSONObject(j);
                  String variantId = var.getString("variantId");

                  JSONObject preParamsSamplingStage = var.getJSONObject("paramsSamplingStage");
                  Double convergenceThreshold = preParamsSamplingStage.getDouble("convergenceThreshold");

                  JSONObject paramsRunStage = var.getJSONObject("paramsRunStage");
                  Double  cpuLevel = paramsRunStage.getDouble("cpuLevel");
                  String screenState = paramsRunStage.getString("screenState");

                  JSONObject energyPreconditionSamplingStage = var.getJSONObject("energyPreconditionSamplingStage");
                  String requiredBatteryStateSampling = energyPreconditionSamplingStage.getString("requiredBatteryState");
                  Double minStartBatteryLevelSampling = energyPreconditionSamplingStage.getDouble("minStartBatteryLevel");

                  JSONObject energyPreconditionRunStage = var.getJSONObject("energyPreconditionRunStage");
                  String requiredBatteryStateRun = energyPreconditionRunStage.getString("requiredBatteryState");
                  Double minStartBatteryLevelRun = energyPreconditionRunStage.getDouble("minStartBatteryLevel");
                  Double minEndBatteryLevelRun = energyPreconditionRunStage.getDouble("minEndBatteryLevel");

                  variant.setVariantId(variantId);
                  ParamsSamplingStage paramsSamplingStage1 = new ParamsSamplingStage();
                  paramsSamplingStage1.setConvergenceThreshold(convergenceThreshold);
                  variant.setParamsSamplingStage(paramsSamplingStage1);
                  ParamsRunStage paramsRunStage1 = new ParamsRunStage();
                  paramsRunStage1.setCpuLevel(cpuLevel);
                  paramsRunStage1.setScreenState(screenState);
                  variant.setParamsRunStage(paramsRunStage1);
                  EnergyPreconditionSamplingStage energyPreconditionSamplingStage1 = new EnergyPreconditionSamplingStage();
                  energyPreconditionSamplingStage1.setMinStartBatteryLevel(minStartBatteryLevelSampling);
                  energyPreconditionSamplingStage1.setRequiredBatteryState(requiredBatteryStateSampling);
                  variant.setEnergyPreconditionSamplingStage(energyPreconditionSamplingStage1);
                  EnergyPreconditionRunStage energyPreconditionRunStage1 = new EnergyPreconditionRunStage();
                  energyPreconditionRunStage1.setMinEndBatteryLevel(minEndBatteryLevelRun);
                  energyPreconditionRunStage1.setMinStartBatteryLevel(minStartBatteryLevelRun);
                  energyPreconditionRunStage1.setRequiredBatteryState(requiredBatteryStateRun);
                  variant.setEnergyPreconditionRunStage(energyPreconditionRunStage1);
                  variants.add(variant);

                  System.out.println("Variant ID : " + variantId );
                  System.out.println("Convergence Threshold :" + convergenceThreshold);
                  System.out.println("Cpu level: " + cpuLevel);
                  System.out.println("Screen STate: " + screenState);
                  System.out.println("requieredBatteryStateSampling: " + requiredBatteryStateSampling);
                  System.out.println("minStartBatterylevelsampling: " + minStartBatteryLevelSampling);
                  System.out.println("requiredBatterystaterun: " + requiredBatteryStateRun);
                  System.out.println("minStartBatteryLevelRun: " + minStartBatteryLevelRun);
                  System.out.println("midEndBatteryLevelRu: " + minEndBatteryLevelRun);
              }
              benchmarkDefinition = new BenchmarkDefinition();
              benchmarkDefinition.setBenchmarkClass(benchmarkClass);
              benchmarkDefinition.setBenchmarkId(benchmarkId);
              benchmarkDefinition.setVariants(variants);
              definitions.add(benchmarkDefinition);
          }
          List<String> runOrder = new ArrayList<String>();
          JSONArray order = mainObject.getJSONArray("runOrder");
          System.out.println("TAMAÑO ARREGLO" + order.length());
          for (int i = 0; i<order.length();i++){
              String obj = order.getString(i);
              runOrder.add(obj);
          }
          for (int i = 0; i<runOrder.size();i++)
              System.out.println(runOrder.get(i));
          benchmarkData = new BenchmarkData();
          benchmarkData.setBenchmarkDefinitions(definitions);
          benchmarkData.setRunOrder(runOrder);
    }

}
