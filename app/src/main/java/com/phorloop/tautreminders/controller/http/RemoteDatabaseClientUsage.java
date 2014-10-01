package com.phorloop.tautreminders.controller.http;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phorloop.tautreminders.controller.helpers.AcknowledgementHelper;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by philliphartin on 18/04/2014.
 */
public class RemoteDatabaseClientUsage extends Application {

    private static final String LOG = "RemoteDatabaseClientUsage";
    private Context context;

    public RemoteDatabaseClientUsage(Context context) {
        this.context = context;
    }

    public void postAcknowledgementLogs() {

        AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(context);

        if (acknowledgementHelper.unSentAcknowledgementsAvailable()) {

            RequestParams params = new RequestParams();
            params.put("acknowledgements", acknowledgementHelper.getUnsentAcknowledgementsGSON());
            Log.d(LOG, params.toString());

            RemoteDatabaseClient.postAcknowledgementLogs(params, new JsonHttpResponseHandler() {

                @Override //FIXME: Check all this works
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int success = response.getInt("success");
                        String message = response.getString("message");
                        Log.d(LOG, "Success: " + success + " - " + message);
                        if (success == 1) {
                            //FIXME: Update the acknowledgements as sent
                        } else {
                            //Do Nothing
                        }
                    } catch (Exception e) {
                        Log.d(LOG, "postAnswers Exception: " + e);
                    }

                }
            });
        }
    }

//    private void updateAllAnswersAsSent() {
//        List<Answer> answers = Answer.find(Answer.class, "uploaded = ?", "0");
//        for (int i = 0; i < answers.size(); i++) {
//            Answer answer = answers.get(i);
//            answer.setUploaded(1);
//            answer.save();
//        }
//    }
}
