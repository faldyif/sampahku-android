package com.sampah_ku.sampahku.function;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sampah_ku.sampahku.AppConfig;

/**
 * Created by Faldy on 12/05/2017.
 */

public class SampahkuRestClient {
    private static final String TAG = SampahkuRestClient.class.getSimpleName();
    private static final String BASE_URL = AppConfig.apiUrl;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d(TAG, "getAbsoluteUrl: " + BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }
}
