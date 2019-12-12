package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Benchmark.BenchmarkData;
import com.example.myapplication.model.UpdateData;
import com.example.myapplication.service.BatteryNotificator;
import com.example.myapplication.service.BenchmarkExecutor;
import com.example.myapplication.service.PollingIntentService;
import com.example.myapplication.service.ServerConnection;
import com.example.myapplication.service.ToastIntentService;
import com.example.myapplication.utils.Cb;
import com.example.myapplication.utils.ReaderJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static android.Manifest.permission.INTERNET;
import static com.example.myapplication.service.BenchmarkIntentService.END_BENCHMARK_ACTION;
import static com.example.myapplication.service.BenchmarkIntentService.PROGRESS_BENCHMARK_ACTION;
import static com.example.myapplication.service.PollingIntentService.POLLING_ACTION;
import static com.example.myapplication.service.SamplingIntentService.END_SAMPLING_ACTION;
import static com.example.myapplication.service.SamplingIntentService.PROGRESS_SAMPLING_ACTION;
import static com.example.myapplication.service.ToastIntentService.TOAST_ACTION;

public class MainActivity extends Activity {

    //CHANGE THIS CONSTANT TO THE VALUE OF YOUR PREFERENCE
    public static final int INTERVAL_OFF_BATTERY_UPDATES = 5000;
    public static final int POLLING_INTERVAL = 5000;
    public static final String NOT_DEFINED = "notdefined";
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 53;
    public static int THIS_DEVICE_CPU_MHZ = 2000;
    public static int THIS_DEVICE_BATTERY_MAH = 2600;
    public static double THIS_DEVICE_BATTERY_MIN_START_BATTERY_LEVEL = 1d;
    //public static final String NOMBRE_ARCHIVO = "configServer.txt";
    //public static final String PATH_ARCHIVO = "/sdcard/Download/";

    //callbacks for errors
    final Cb<String> onError = new Cb<String>() {
        @Override
        public void run(String error) {
            ToastIntentService.createToasts(error);
        }
    };
    //callbacks for battery updates
    final Cb<JSONObject> batteryUpdateOnSucess = new Cb<JSONObject>() {
        @Override
        public void run(JSONObject jsonObject) {
            ToastIntentService.createToasts("Battery Update Complete :)");
            requestBenchmarksButton.setEnabled(true);
        }
    };
    //condicions for postInitPayload
    public double minBatteryLevel = THIS_DEVICE_BATTERY_MIN_START_BATTERY_LEVEL;
    public String stateOfCharge = NOT_DEFINED;
    //services
    private ServerConnection serverConnection;
    private BatteryNotificator batteryNotificator;

    //callbacks for results send
    final Cb<String> resultSendCb = new Cb<String>() {
        @Override
        public void run(String useless) {
            ToastIntentService.createToasts("Results send");
            serverConnection.postUpdate(new UpdateData(THIS_DEVICE_CPU_MHZ, THIS_DEVICE_BATTERY_MAH, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
        }
    };
    private long timeOfLastBatteryUpdate;
    private BenchmarkExecutor benchmarkExecutor;
    //callbacks for benchmarks received
    final Cb<BenchmarkData> benchmarkReceivedOnSucess = new Cb<BenchmarkData>() {
        @Override
        public void run(BenchmarkData benchmarkData) {
            ToastIntentService.createToasts("Benchmarks received :)");
            benchmarkExecutor.setBenchmarkData(benchmarkData);
            minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            stateOfCharge = benchmarkExecutor.getNeededBatteryState();
            serverConnection.postUpdate(new UpdateData(THIS_DEVICE_CPU_MHZ, THIS_DEVICE_BATTERY_MAH, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
            startBenchmarksButton.setEnabled(true);
            aSwitch.setEnabled(true);
        }
    };
    //mutex
    private Boolean running = false;
    private Boolean evaluating = false;
    //callbacks for benchmarks started
    final Cb<Object> benchmarkCanStartSucess = new Cb<Object>() {
        @Override
        public void run(Object useless) {
            synchronized (evaluating) {
                if (!running) {
                    ToastIntentService.createToasts("Benchmark can start :)");
                    if (benchmarkExecutor.hasMoreToExecute()) {
                        running = true;
                        benchmarkExecutor.execute(getApplicationContext());
                        minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                        serverConnection.postUpdate(new UpdateData(THIS_DEVICE_CPU_MHZ, THIS_DEVICE_BATTERY_MAH, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
                    } else { // ACA MATAR TODOS LOS PROCESOS, CERRAR LA APP.
                        ToastIntentService.createToasts("There is no more benchmark");
                    }
                    evaluating = false;
                }
            }
        }
    };
    //cb for error on benchmarks started
    final Cb<String> onErrorBS = new Cb<String>() {
        @Override
        public void run(String error) {
            ToastIntentService.createToasts(error);
            evaluating = false;
        }
    };
    //Suscriber to battery notifications from the OS
    private BroadcastReceiver batteryInfoReceiver;
    //receiver for updates from the benchmark run
    private ProgressReceiver rcv;
    //receiver for toasts
    private ToastReceiver trcv;
    //receiver for updates from the polling service
    private PollingReceiver prcv;
    //view components
    private EditText ipEditText;
    private EditText portEditText;
    private EditText modelEditText;
    private TextView ipTextView;
    private TextView portTextView;
    private TextView modelTextView;
    private Button manuaBatteryUpdateButton;
    private Button setServerButton;
    private Button requestBenchmarksButton;
    private Button startBenchmarksButton;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReaderJson reader = new ReaderJson("/home/lautaro/Escritorio/pepe.txt");
        try {
            reader.readAndSet();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //check for permission to use internet on the device
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }

        //set service to interact with the server
        serverConnection = ServerConnection.getService();

        //set service to render toasts
        Intent intent3 = new Intent(this, ToastIntentService.class);
        this.startService(intent3);

        //initialize battery intents receiver
        this.batteryNotificator = BatteryNotificator.getInstance();
        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryNotificator.updateBatteryLevel((level / (double) scale));
                if ((System.currentTimeMillis() - timeOfLastBatteryUpdate) > INTERVAL_OFF_BATTERY_UPDATES) {
                    timeOfLastBatteryUpdate = System.currentTimeMillis();
                    serverConnection.postUpdate(new UpdateData(THIS_DEVICE_CPU_MHZ, THIS_DEVICE_BATTERY_MAH, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
                }
            }
        };
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //set receiver for benchmark updates to the main thread
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRESS_BENCHMARK_ACTION);
        filter.addAction(END_BENCHMARK_ACTION);
        filter.addAction(PROGRESS_SAMPLING_ACTION);
        filter.addAction(END_SAMPLING_ACTION);
        rcv = new ProgressReceiver();
        registerReceiver(rcv, filter);

        //set receiver for polling service's updates to the main thread
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(POLLING_ACTION);
        prcv = new PollingReceiver();
        registerReceiver(prcv, filter2);

        //set receiver for toats service to the main thread
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(TOAST_ACTION);
        trcv = new ToastReceiver();
        registerReceiver(trcv, filter3);

        //initialize benchmark service
        this.benchmarkExecutor = new BenchmarkExecutor();

        //find the view component by their id
        ipEditText = findViewById(R.id.IpText);
        portEditText = findViewById(R.id.portText);
        modelEditText = findViewById(R.id.modelText);
        portTextView = findViewById(R.id.textViewPort);
        ipTextView = findViewById(R.id.textViewIP);
        modelTextView = findViewById(R.id.modelTextView);
        manuaBatteryUpdateButton = findViewById(R.id.manualStateUpdateButton);
        requestBenchmarksButton = findViewById(R.id.requestBenchmarksButton);
        setServerButton = findViewById(R.id.setServerButton);
        startBenchmarksButton = findViewById(R.id.startBenchmarksButton);
        aSwitch = findViewById(R.id.aSwitch);

        //set onChangeListener to display the complete formater url to the user
        bindInputToDisplayText(ipEditText, ipTextView);
        bindInputToDisplayText(portEditText, portTextView);
        bindInputToDisplayText(modelEditText, modelTextView);



        //bind button actions
        setServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setServerButton.getText().equals("Edit Server Url")) {
                    setServerButton.setText("Set Server Url");
                    modelEditText.setEnabled(true);
                    ipEditText.setEnabled(true);
                    portEditText.setEnabled(true);
                    manuaBatteryUpdateButton.setEnabled(false);
                    requestBenchmarksButton.setEnabled(false);
                    startBenchmarksButton.setEnabled(false);
                    aSwitch.setEnabled(false);

                } else {
                    String serverUrl = String.format("http://%s:%s/dewsim/%s", ipTextView.getText(), portTextView.getText(), modelEditText.getText());
                    serverConnection.registerServerUrl(serverUrl);
                    setServerButton.setText("Edit Server Url");
                    modelEditText.setEnabled(false);
                    ipEditText.setEnabled(false);
                    portEditText.setEnabled(false);
                    manuaBatteryUpdateButton.setEnabled(true);
                }
            }
        });
        manuaBatteryUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnection.postUpdate(new UpdateData(THIS_DEVICE_CPU_MHZ, THIS_DEVICE_BATTERY_MAH, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
            }
        });
        requestBenchmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnection.getBenchmarks(benchmarkReceivedOnSucess, onError, getApplicationContext());
            }
        });
        startBenchmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBenchmark();
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startPolling();
                } else {
                    stoptPolling();
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Internet Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "We need this permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.INTERNET},
                            MY_PERMISSIONS_REQUEST_INTERNET);
                }
                return;
            }
        }
    }

    //L&F related
    private void bindInputToDisplayText(final EditText input, final TextView display) {
        input.addTextChangedListener(new TextWatcher() {

                                         public void afterTextChanged(Editable s) {
                                             display.setText(input.getText());
                                         }

                                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                         }

                                         public void onTextChanged(CharSequence s, int start, int before, int count) {
                                         }
                                     }
        );
    }

    private void startBenchmark() {
        synchronized (evaluating) {
            if (!evaluating && !running) {
                evaluating = true;
                serverConnection.startBenchmarck(benchmarkCanStartSucess, onErrorBS, getApplicationContext(), stateOfCharge);
            }
        }
    }

    private void startPolling() {
        Intent intent = new Intent(this, PollingIntentService.class);
        PollingIntentService.setShouldContinue(true);
        this.startService(intent);
    }

    private void stoptPolling() {
        PollingIntentService.setShouldContinue(false);
    }

    //unregister the battery monitor
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.rcv);
        this.unregisterReceiver(this.prcv);
        this.unregisterReceiver(this.batteryInfoReceiver);
    }

    public class PollingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //polling try
            if (intent.getAction().equals(POLLING_ACTION)) {
                startBenchmark();
            }
        }
    }

    public class ToastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //polling try
            if (intent.getAction().equals(TOAST_ACTION)) {
                Toast.makeText(context, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //benchmarck run stage report
            if (intent.getAction().equals(PROGRESS_BENCHMARK_ACTION)) {
                String prog = intent.getStringExtra("progress");
                ToastIntentService.createToasts(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else if (intent.getAction().equals(END_BENCHMARK_ACTION)) {
                ToastIntentService.createToasts("Run stage finished");
                String result = intent.getStringExtra("payload");
                String variant = intent.getStringExtra("variant");
                serverConnection.sendResult(resultSendCb, onError, getApplicationContext(), result.getBytes(), "run", variant);
                running = false;
                if (benchmarkExecutor.hasMoreToExecute()) {
                    stateOfCharge = benchmarkExecutor.getNeededBatteryState();
                    minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                    startBenchmark();
                } else
                    ToastIntentService.createToasts("There is no more benchmark");
            }
            //benchmarck sampling stage report
            if (intent.getAction().equals(PROGRESS_SAMPLING_ACTION)) {
                String prog = intent.getStringExtra("progress");
                ToastIntentService.createToasts(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else if (intent.getAction().equals(END_SAMPLING_ACTION)) {
                ToastIntentService.createToasts("Sampling finished");
                String result = intent.getStringExtra("payload");
                String variant = intent.getStringExtra("variant");
                serverConnection.sendResult(resultSendCb, onError, getApplicationContext(), result.getBytes(), "sampling", variant);
                running = false;
                if (benchmarkExecutor.hasMoreToExecute()) {
                    stateOfCharge = benchmarkExecutor.getNeededBatteryState();
                    minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                    startBenchmark();
                } else
                    ToastIntentService.createToasts("There is no more benchmark");
            }
        }
    }



}
