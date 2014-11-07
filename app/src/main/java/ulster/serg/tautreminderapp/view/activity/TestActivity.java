package ulster.serg.tautreminderapp.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.sensors.SensorUtils;

import ulster.serg.tautreminderapp.R;

public class TestActivity extends Activity {

    private static final String LOG = "TestActivity";
    private final static int sensorType = SensorUtils.SENSOR_TYPE_ACCELEROMETER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        Button buttonStart = (Button) findViewById(R.id.button);
        Button buttonStop = (Button) findViewById(R.id.button2);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG, "ButtonStart pressed");
                //startEnvironment();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void senseOnceAndStop() {
        try {
            ESSensorManager sensorManager = ESSensorManager.getSensorManager(this);
            sensorManager.getDataFromSensor(sensorType);
            Log.d(LOG, "Sensed from: " + SensorUtils.getSensorName(sensorType));

        } catch (ESException e) {
            e.printStackTrace();
        }
    }

//    private void senseShite() throws ESException {
//        ESSensorManager sensorManager = ESSensorManager.getSensorManager(getApplicationContext());
//        final SparseIntArray subscriptions;
//        subscriptions = new SparseIntArray();
//
//        int subscriptionId = sensorManager.subscribeToSensorData(s.getType(), SenseFromAllEnvSensorsTask.this);
//        subscriptions.put(s.getType(), subscriptionId);
//    }
//
//    private void startPull() {
//        new SenseFromAllPullSensorsTask(this) {
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//                //startEnvironment();
//            }
//        }.execute();
//    }
//
//    private void startEnvironment() {
//        new SenseFromAllEnvSensorsTask(this) {
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//                //startPush();
//            }
//        }.execute();
//    }
//
//    private void startPush() {
//        new SenseFromAllPushSensorsTask(this).execute();
//    }
}
