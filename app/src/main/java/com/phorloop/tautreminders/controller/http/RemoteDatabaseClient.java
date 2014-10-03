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

    private static final String LOG = "RemoteDatabaseClient";
    private static final String URLuploadLogs = "post_Acknowledgements.php";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private Context mContext;

    public RemoteDatabaseClient(Context mContext) {
        this.mContext = mContext;
    }

    public void postAcknowledgementLogs(RequestParams params, AsyncHttpResponseHandler responseHandler) {

        String absoluteURL = getAbsoluteUrl(URLuploadLogs);

        Log.d(LOG, "Posting to: " + absoluteURL);
        client.post(absoluteURL, params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return getBaseURL() + relativeUrl;
    }

    private String getBaseURL() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(mContext);
        return preferencesHelper.getServerURL();
    }


}
