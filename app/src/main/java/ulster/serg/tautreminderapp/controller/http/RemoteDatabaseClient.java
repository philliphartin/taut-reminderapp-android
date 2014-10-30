package ulster.serg.tautreminderapp.controller.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

/**
 * Created by philliphartin on 17/04/2014.
 */
public class RemoteDatabaseClient {

    private static final String LOG = "RemoteDatabaseClient";

    //URLs
    private static final String URL_SERVERIP_MAIN = "http://193.61.148.51:80";
    private static final String URL_SERVERIP_LEGACY = "http://193.61.148.92";

    private static final String URL_PROJECTPATH_MAIN = "/tautreminders/webresources/com.phorloop.tautreminders.reminderslog";
    private static final String URL_PROJECTPATH_DEV = "/tautreminders/webresources/com.phorloop.tautreminders.reminderslog";
    private static final String URL_PROJECTPATH_LEGACY = "/taut/scripts";

    private static final String POST_ACKNOWLEDGEMENTS_MAIN = "/upload";
    private static final String POST_ACKNOWLEDGEMENTS_DEV = "/upload";
    private static final String POST_ACKNOWLEDGEMENTS_LEGACY = "/log_upload.php";


    private static AsyncHttpClient client = new AsyncHttpClient();
    private Context mContext;

    public RemoteDatabaseClient(Context mContext) {
        this.mContext = mContext;
    }

    public void postAcknowledgementLogs(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String absoluteURL = getAbsoluteUrl();
        client.post(absoluteURL, params, responseHandler);
    }


    //Post method to send to RESTFUL Java server
    public void postAcknowledegementLogsJSONtype(String json, AsyncHttpResponseHandler responseHandler){

        String absoluteURL = getAbsoluteUrl();
        Log.d(LOG, "Posting to: " + absoluteURL);

        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Encode header of type
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(mContext, absoluteURL, entity, "application/json", responseHandler);
    }

    private String getAbsoluteUrl() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(mContext);
        String selectedServer = preferencesHelper.getServerURL();
        return getServerAddress(selectedServer) + getProjectPath(selectedServer) + getPostEndpoint(selectedServer);
    }

    private String getServerAddress(String selectedServer){
        if (selectedServer.equals("legacy")){
            return URL_SERVERIP_LEGACY;
        }else{
            return URL_SERVERIP_MAIN;
        }
    }

    private String getPostEndpoint(String selectedServer) {
        if (selectedServer.equals("main")){
            return POST_ACKNOWLEDGEMENTS_MAIN;
        }else if (selectedServer.equals("legacy")){
            return POST_ACKNOWLEDGEMENTS_LEGACY;
        }else if (selectedServer.equals("dev")){
            return POST_ACKNOWLEDGEMENTS_DEV;
        }else{
            //None of the above
            return POST_ACKNOWLEDGEMENTS_MAIN;
        }
    }

    private String getProjectPath (String selectedServer) {
        if (selectedServer.equals("main")){
            return URL_PROJECTPATH_MAIN;
        }else if (selectedServer.equals("legacy")){
            return URL_PROJECTPATH_LEGACY;
        }else if (selectedServer.equals("dev")){
            return URL_PROJECTPATH_DEV;
        }else{
            //None of the above
            return URL_PROJECTPATH_MAIN;
        }
    }
}
