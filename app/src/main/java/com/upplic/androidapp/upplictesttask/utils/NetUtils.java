package com.upplic.androidapp.upplictesttask.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class NetUtils {

    public final static int TYPE_NOT_CONNECTED = 0;

    private final static int TYPE_WIFI = 1;
    private final static int TYPE_MOBILE = 2;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static void getAsyncHttp(String url, HashMap<String, String> params, final Handler mHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        if (null != params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                requestParams.put(key, value);
            }
        }
        client.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mHandler.obtainMessage(AppConstants.MESSAGE_HTTP_RESPONSE_SUCCESS, responseBody).sendToTarget();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mHandler.obtainMessage(AppConstants.MESSAGE_HTTP_RESPONSE_FAILURE, error).sendToTarget();
            }

        });
    }

    public static void postAsyncHttp(String url, HashMap<String, String> params, final Handler mHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        if (null != params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                requestParams.put(key, value);
            }
        }
        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mHandler.obtainMessage(AppConstants.MESSAGE_HTTP_RESPONSE_SUCCESS, responseBody).sendToTarget();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mHandler.obtainMessage(AppConstants.MESSAGE_HTTP_RESPONSE_FAILURE, error).sendToTarget();
            }
        });
    }
}
