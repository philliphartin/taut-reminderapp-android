/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.loggers;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.AppSettings;
import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Session;

public class FileLoggerFactory {
    private static String LOG = "GPSFileLogger";

    public static List<IFileLogger> GetFileLoggers() {

        File gpsFolder = new File(Environment.getExternalStorageDirectory(), "taut_logger/LegacyLogs");
        if (!gpsFolder.exists()) {
            gpsFolder.mkdirs();
        }

        List<IFileLogger> loggers = new ArrayList<IFileLogger>();

        if (AppSettings.shouldLogToPlainText()) {
            File file = new File(gpsFolder.getPath(), Session.getCurrentFileName() + ".csv");
            loggers.add(new PlainTextFileLogger(file));
            Log.w(LOG, "Writing to: " + file.getAbsolutePath());
        }

        if (AppSettings.shouldLogToGpx()) {
            File gpxFile = new File(gpsFolder.getPath(), Session.getCurrentFileName() + ".gpx");
            loggers.add(new Gpx10FileLogger(gpxFile, Session.shouldAddNewTrackSegment(), Session.getSatelliteCount()));
        }

        return loggers;
    }
}
