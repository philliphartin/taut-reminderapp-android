package ulster.serg.tautreminderapp.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ulster.serg.tautreminderapp.controller.helpers.PreferencesHelper;
import ulster.serg.tautreminderapp.view.activity.SettingsActivity;

/**
 * Created by philliphartin on 30/09/2014.
 */
public class WelcomeDialog extends Dialog {
    private static final String LOG = "WelcomeDialog";

    //ActivityPreferences Helper
    private Context mContext;
    private static final int CARER_PIN = 7777;

    //UI Elements
    Button buttonSubmit;
    EditText editTextPassword;

    //Preferences
    PreferencesHelper preferencesHelper;


    //Dialog Variables

    public WelcomeDialog(Context context) {

        super(context);
        this.mContext = context;

        setContentView(ulster.serg.tautreminderapp.R.layout.dialog_welcome);
        setTitle("Welcome!");
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        View.OnClickListener clickFunction = new OnClickClass();

        editTextPassword = (EditText) findViewById(ulster.serg.tautreminderapp.R.id.password);
        buttonSubmit = (Button) findViewById(ulster.serg.tautreminderapp.R.id.button_submit);
        buttonSubmit.setOnClickListener(clickFunction);
    }


    private class OnClickClass implements View.OnClickListener {
        public void onClick(View v) {
            checkPassword(editTextPassword);
        }
    }


    private void checkPassword(EditText editText) {
        String password = editText.getText().toString();

        if (passwordIsAttempted(password)) {
            if (passwordIsCorrect(convertPasswordToInteger(password))) {
                dismiss();
                //Launch settings activity
                Intent intent_preferences = new Intent(mContext, SettingsActivity.class);
                mContext.startActivity(intent_preferences);
            }
        }
    }

    public boolean passwordIsAttempted(String password) {
        if (password.length() > 0) {
            return true;
        } else {
            Toast.makeText(mContext, "Please Enter the Master Password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean passwordIsCorrect(int userEnteredValue) {

        if (userEnteredValue == CARER_PIN) {
            return true;
        } else {
            Toast.makeText(mContext, "Please re-check password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private int convertPasswordToInteger(String password) {
        int passwordNumbers;

        try {
            passwordNumbers = Integer.parseInt(password);
        } catch (Exception e) {
            passwordNumbers = 0;
            Log.e(LOG, "Cannot parse password");
        }
        return passwordNumbers;
    }


}
