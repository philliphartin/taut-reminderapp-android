package ulster.serg.tautreminderapp.controller.http;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

import ulster.serg.tautreminderapp.controller.helpers.AcknowledgementHelper;
import ulster.serg.tautreminderapp.model.sugarorm.Acknowledgement;

/**
 * Created by philliphartin on 18/04/2014.
 */
public class RemoteDatabaseClientUsage extends Application {

    private static final String LOG = "RemoteDatabaseClientUsage";
    private static final String POST_KEY = "acknowledgementlog";
    private Context mContext;

    public RemoteDatabaseClientUsage(Context context) {
        this.mContext = context;
    }

    public void postAcknowledgementLogs() {

        final AcknowledgementHelper acknowledgementHelper = new AcknowledgementHelper(mContext);

        if (acknowledgementHelper.unSentAcknowledgementsAvailable()) {
            final List<Acknowledgement> acknowledgementList = acknowledgementHelper.getListUnsentAcknowledgements();            //Get List of Unsent Acknowledgements
            String JSONtoPost = acknowledgementHelper.getUnsentAcknowledgementsAsGSON(acknowledgementList);             //Then send list back to acknowledgement helper to have converted to GSON array

            //Set HTTP params for transmission
            RequestParams params = new RequestParams();
            params.put(POST_KEY, JSONtoPost);
            Log.d(LOG, params.toString()); // Output data to send

            //Set ASyncResponseHandler
            JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
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
                        Log.w(LOG, "Success: " + success);
                        if (success == 1) {
                            //FIXME: Update this!
                            Log.i(LOG, message);
                            //On success, iterate through the acknowledgement list and set sent as 1.
                            acknowledgementHelper.updateAcknowledgementsAsSentToServer(acknowledgementList);
                        } else {
                            //Do Nothing
                            Log.e(LOG, message);

                        }
                    } catch (Exception e) {
                        Log.d(LOG, "postAnswers Exception: " + e);
                    }
                }
            };

            RemoteDatabaseClient remoteDatabaseClient = new RemoteDatabaseClient(mContext);
            remoteDatabaseClient.postAcknowledgementLogs(JSONtoPost, params, responseHandler);

        } else {
            Log.d(LOG, "No new acknowledgement logs to send");
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
