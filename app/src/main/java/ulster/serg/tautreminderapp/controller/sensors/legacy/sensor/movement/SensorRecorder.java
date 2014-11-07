package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.movement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorRecorder implements SensorEventListener, Runnable {
    public static final int[] SENSOR_AVAILABLE_DELAYS = {
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_UI,
            SensorManager.SENSOR_DELAY_NORMAL
    };
    private static final int THREAD_IDLE_LOOP_DELAY = 1000;
    private boolean mRunning, mAccelerometer, mOrientation, mMagnetic, mLight,
            mAmbientTemp, mProximity, mLinearAccel, mGyroscope;
    private int mRecordingRate;
    private SensorOutputWriter mAccelerometerLog, mOrientationLog, mMagneticLog,
            mLightLog, mAmbientTempLog, mProximityLog, mLinearAccelLog, mGyroscopeLog;
    private SensorManager mSensorManager;
    private Thread mThread;
    public SensorRecorder(Context context) {
        mRunning = false;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startRecording(boolean accelerometer, boolean orientation, boolean magnetic, boolean light,
                               boolean ambienttemp, boolean proximity, boolean linearaccel, boolean
            gyroscope, int rate) throws StorageErrorException {

        this.mAccelerometer = accelerometer;
        this.mOrientation = orientation;
        this.mMagnetic = magnetic;
        this.mLight = light;
        this.mRecordingRate = rate;
        this.mAmbientTemp = ambienttemp;
        this.mProximity = proximity;
        this.mLinearAccel = linearaccel;
        this.mGyroscope = gyroscope;
        this.mRunning = true;

        // Create loggers
        createLoggers();

        // Start recording thread
        mThread = new Thread(this);
        mThread.start();
    }

    public void stopRecording() {
        // Unregisterlistener moved to thread
        mRunning = false;
        // End recording thread
        mThread.interrupt();
        mThread = null;
    }

    public boolean isRunning() {
        return mRunning;
    }

    private void createLoggers() throws StorageErrorException {
        if (mAccelerometer)
            mAccelerometerLog = new SensorOutputWriter(SensorOutputWriter.TYPE_ACCELEROMETER);
        if (mOrientation)
            mOrientationLog = new SensorOutputWriter(SensorOutputWriter.TYPE_ORIENTATION);
        if (mMagnetic) mMagneticLog = new SensorOutputWriter(SensorOutputWriter.TYPE_MAGNETIC);
        if (mLight) mLightLog = new SensorOutputWriter(SensorOutputWriter.TYPE_LIGHT);
        if (mAmbientTemp)
            mAmbientTempLog = new SensorOutputWriter(SensorOutputWriter.TYPE_AMBIENTTEMP);
        if (mProximity) mProximityLog = new SensorOutputWriter(SensorOutputWriter.TYPE_PROXIMITY);
        if (mGyroscope) mGyroscopeLog = new SensorOutputWriter(SensorOutputWriter.TYPE_GYROSCOPE);
        if (mLinearAccel)
            mLinearAccelLog = new SensorOutputWriter(SensorOutputWriter.TYPE_LINEARACCELEROMETER);
    }

    private void registerListeners() {
        if (mAccelerometer)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mRecordingRate);
        if (mOrientation)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), mRecordingRate);
        if (mMagnetic)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), mRecordingRate);
        if (mLight)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), mRecordingRate);
        if (mAmbientTemp)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), mRecordingRate);
        if (mProximity)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), mRecordingRate);
        if (mGyroscope)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), mRecordingRate);
        if (mLinearAccel)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), mRecordingRate);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        try {
            switch (type) {
                case Sensor.TYPE_ACCELEROMETER:
                    mAccelerometerLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    mOrientationLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mMagneticLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_LIGHT:
                    mLightLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    mAmbientTempLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    mProximityLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    mGyroscopeLog.writeReadings(event.values);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    mLinearAccelLog.writeReadings(event.values);
                    break;
            }
        } catch (StorageErrorException e) {
            e.printStackTrace();
        }
    }

    public float[][] getBufferForSensor(String sensorType) {
        if (sensorType.equals(SensorOutputWriter.TYPE_ACCELEROMETER))
            return getAccelerometerBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_ORIENTATION))
            return getOrientationBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_MAGNETIC))
            return getMagneticBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_LIGHT))
            return getLightBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_AMBIENTTEMP))
            return getAmbientTempBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_GYROSCOPE))
            return getGyroscopeBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_PROXIMITY))
            return getProximityBuffer();
        if (sensorType.equals(SensorOutputWriter.TYPE_LINEARACCELEROMETER))
            return getLinearAccelerometerBuffer();

        //Else throw argument error
        throw new IllegalArgumentException();
    }

    public float[][] getAccelerometerBuffer() {
        if (mAccelerometer) {
            return mAccelerometerLog.getBuffer();
        } else return null;
    }

    public float[][] getOrientationBuffer() {
        if (mOrientation) {
            return mOrientationLog.getBuffer();
        } else return null;
    }

    public float[][] getMagneticBuffer() {
        if (mMagnetic) {
            return mMagneticLog.getBuffer();
        } else return null;
    }

    public float[][] getLightBuffer() {
        if (mLight) {
            return mLightLog.getBuffer();
        } else return null;
    }

    public float[][] getAmbientTempBuffer() {
        if (mAmbientTemp) {
            return mAmbientTempLog.getBuffer();
        } else return null;
    }

    public float[][] getProximityBuffer() {
        if (mProximity) {
            return mProximityLog.getBuffer();
        } else return null;
    }

    public float[][] getGyroscopeBuffer() {
        if (mGyroscope) {
            return mGyroscopeLog.getBuffer();
        } else return null;
    }

    public float[][] getLinearAccelerometerBuffer() {
        if (mLinearAccel) {
            return mLinearAccelLog.getBuffer();
        } else return null;
    }

    @Override
    public void run() {
        registerListeners();

        // Idle loop
        while (mRunning) {
            try {
                Thread.sleep(THREAD_IDLE_LOOP_DELAY);
            } catch (InterruptedException ex) {
            }
        }

        // Here we must end the registration, so
        mSensorManager.unregisterListener(this);

        try {
            if (mAccelerometer) mAccelerometerLog.close();
            if (mOrientation) mOrientationLog.close();
            if (mMagnetic) mMagneticLog.close();
            if (mLight) mLightLog.close();
            if (mAmbientTemp) mAmbientTempLog.close();
            if (mProximity) mProximityLog.close();
            if (mGyroscope) mGyroscopeLog.close();
            if (mLinearAccel) mLinearAccelLog.close();


        } catch (StorageErrorException ex) {
            ex.printStackTrace();
        }
    }


}
