package ulster.serg.tautreminderapp.controller.http;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import ulster.serg.tautreminderapp.controller.helpers.AcknowledgementHelper;
import ulster.serg.tautreminderapp.model.sugarorm.Acknowledgement;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by philliphartin on 18/04/2014.
 */
public class RemoteDatabaseClientUsage extends Application {

    private static final String LOG = "RemoteDatabaseClientUsage";
    private Context mContext;

    public RemoteDatabaseClientUsage(Context context) {
        this.mContext = context;
    }

    public void postAcknowledgementLogs() {

        final AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(mContext);

        if (acknowledgementHelper.unSentAcknowledgementsAvailable()) {
            //Get List of Unsent Acknowledgements
            final List<Acknowledgement> acknowledgementList = acknowledgementHelper.getListUnsentAcknowledgements();
            //Then send list back to acknowledgement helper to have converted to GSON array
            String JSONtoPost = acknowledgementHelper.getUnsentAcknowledgementsAsGSON(acknowledgementList);

            //TODO: Find type
            RequestParams params = new RequestParams();
            params.put("acknowledgementlog", JSONtoPost);
            Log.d(LOG, params.toString());

            RemoteDatabaseClient remoteDatabaseClient = new RemoteDatabaseClient(mContext);
            remoteDatabaseClient.postAcknowledegementLogsJSONtype(JSONtoPost, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(LOG, "Failed");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        int success = response.getInt("success");
                        String message = response.getString("message");
                        Log.d(LOG, "Success: " + success + " - " + message);
                        if (success == 1) {
                            //FIXME: Update this!
                            //On success, iterate through the acknowledgement list and set sent as 1.
                            //acknowledgementHelper.updateAcknowledgementsAsSentToServer(acknowledgementList);
                        } else {
                            //Do Nothing
                        }
                    }catch (Exception e) {
                        Log.d(LOG, "postAnswers Exception: " + e);
                    }


                }

                //                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
//                    try {
//                        int success = response.getInt("success");
//                        String message = response.getString("message");
//                        Log.d(LOG, "Success: " + success + " - " + message);
//                        if (success == 1) {
//                            //On success, iterate through the acknowledgement list and set sent as 1.
//                            //acknowledgementHelper.updateAcknowledgementsAsSentToServer(acknowledgementList);
//                        } else {
//                            //Do Nothing
//                        }
//                    } catch (Exception e) {
//                        Log.d(LOG, "postAnswers Exception: " + e);
//                    }
//                }
            });

//            remoteDatabaseClient.postAcknowledgementLogs(params, new JsonHttpResponseHandler() {
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                    Log.d(LOG, "Failed");
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
//                    try {
//                        int success = response.getInt("success");
//                        String message = response.getString("message");
//                        Log.d(LOG, "Success: " + success + " - " + message);
//                        if (success == 1) {
//                            //On success, iterate through the acknowledgement list and set sent as 1.
//                            //acknowledgementHelper.updateAcknowledgementsAsSentToServer(acknowledgementList);
//                        } else {
//                            //Do Nothing
//                        }
//                    } catch (Exception e) {
//                        Log.d(LOG, "postAnswers Exception: " + e);
//                    }
//                }
//            });

        } else {
            Log.d(LOG, "No Acknowledgement logs to send");
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
