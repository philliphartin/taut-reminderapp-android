package com.phorloop.tautreminders.controller.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phorloop.tautreminders.controller.helpers.PreferencesHelper;

/**
 * Created by philliphartin on 17/04/2014.
 */
public class RemoteDatabaseClient {

    private Context mContext;
    private static String BASE_URL = null;

    public RemoteDatabaseClient(Context mContext) {
        this.mContext = mContext;
        this.BASE_URL = getURL();
    }

    private static final String LOG = "RemoteDatabaseClient";
    private static final String URLuploadLogs = "frog_upload.php"; //FIXME: Change to real page log_upload.php

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void postAcknowledgementLogs(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(URLuploadLogs), params, responseHandler);
        Log.v(LOG, getAbsoluteUrl(URLuploadLogs));
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private String getURL() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(mContext);
        return preferencesHelper.getServerURL();
    }


}
