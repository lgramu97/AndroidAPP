package com.example.myapplication.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Benchmark.BenchmarkData;
import com.example.myapplication.model.UpdateData;
import com.example.myapplication.utils.Cb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.MainActivity.NOT_DEFINED;


public class ServerConnection {
    private static ServerConnection serverConnectionInstance;
    private String url;
    private RequestQueue requestQueue;
    private Gson gson;


    private ServerConnection() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }

    public static synchronized ServerConnection getService() {
        if (serverConnectionInstance == null)
            serverConnectionInstance = new ServerConnection();
        return serverConnectionInstance;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public void registerServerUrl(String serverUrl) {
        this.url = serverUrl;
    }

    public synchronized void postUpdate(final UpdateData data, final Cb<JSONObject> onSucessCb, final Cb<String> onErrorCb, Context context) {

        Map<String, Object> params = new HashMap<>();
        params.put("cpu_mhz", data.getCpu_mhz());
        params.put("battery_Mah", data.getBattery_Mah());
        params.put("minStartBatteryLevel", data.getMinStartBatteryLevel());
        params.put("currentBatteryLevel", data.getCurrentBatteryLevel());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onSucessCb.run(response);
                    }
                },
                new MyErrorListener(onErrorCb)) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        getRequestQueue(context).add(jsonObjectRequest);
    }
// Pedir aca los datos de readerJson, que se asignan a onsuccesCB (benchmarkReceivedOnSucess del main) y se ejecutan.
    public void getBenchmarks(final Cb<BenchmarkData> onSucessCb, final Cb<String> onErrorCb, Context context) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + "?stage=init", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("message").equals(true)) {
                                BenchmarkData result = gson.fromJson(response.get("benchmarkData").toString(), BenchmarkData.class);
                                onSucessCb.run(result);
                            } else {
                                onErrorCb.run("cant't get the benchmarks yet");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new MyErrorListener(onErrorCb));
        getRequestQueue(context).add(jsonObjectRequest);
    }

    public void startBenchmarck(final Cb<Object> onSucessCb, final Cb<String> onErrorCb, Context context, String stateOfCharge) {
        String newurl = stateOfCharge.equals(NOT_DEFINED) ? url + "?stage=postinit" : url + "?stage=postinit&requiredBatteryState=" + stateOfCharge;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("message").equals(true)) {
                                onSucessCb.run("NO_PARAM");
                            } else {
                                onErrorCb.run("cant't start the benchmarks yet");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new MyErrorListener(onErrorCb));
        getRequestQueue(context).add(jsonObjectRequest);
    }

    public void sendResult(final Cb<String> onSuccesCb, Cb<String> onErrorCb, Context context, byte[] result, String stage, String variant) {

        MultipartRequest multipartRequest = new MultipartRequest(url + "?fileName=" + stage + "-" + variant, new MyErrorListener(onErrorCb), new Response.Listener<String>() {
            @Override
            public void onResponse(String useless) {
                onSuccesCb.run("useless");
            }
        }, result, stage + "-" + variant, context);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue(context).add(multipartRequest);
    }

    private static class MyErrorListener implements Response.ErrorListener {

        private final Cb<String> onErrorCb;

        public MyErrorListener(Cb<String> onErrorCb) {
            this.onErrorCb = onErrorCb;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.getMessage() != null)
                onErrorCb.run(error.getMessage());
        }
    }

    public class MultipartRequest extends Request<String> {

        private final Response.Listener<String> mListener;
        byte[] content;
        private MultipartEntity entity = new MultipartEntity();
        private HashMap<String, String> mParams;
        private String name;
        private Context context;

        public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, byte[] content, String name, Context context) {
            super(Method.POST, url, errorListener);
            mListener = listener;
            this.content = content;
            this.name = name;
            this.context = context;
            buildMultipartEntity();
        }

        private void buildMultipartEntity() {
            try {
                FileOutputStream outputStreamWriter = context.openFileOutput("filetosend", Context.MODE_PRIVATE);
                outputStreamWriter.write(content);
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            entity.addPart("data", new FileBody(context.getFileStreamPath("filetosend")));
        }

        @Override
        public String getBodyContentType() {
            return entity.getContentType().getValue();
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                entity.writeTo(bos);
            } catch (IOException e) {
                VolleyLog.e("IOException writing to ByteArrayOutputStream");
            }
            return bos.toByteArray();
        }


        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            return Response.success("Uploaded", getCacheEntry());
        }

        @Override
        protected void deliverResponse(String response) {
            mListener.onResponse(response);

        }
    }
}
