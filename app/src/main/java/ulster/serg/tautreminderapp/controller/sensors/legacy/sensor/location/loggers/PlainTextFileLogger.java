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

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Locale;

import ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common.Utilities;


/**
 * Writes a comma separated plain text file.<br/>
 * First line of file is a header with the logged fields: time,lat,lon,elevation,accuracy,bearing,speed
 *
 * @author Jeroen van Wilgenburg
 *         https://github.com/jvwilge/gpslogger/commit/a7d45bcc1d5012513ff2246022ce4da2708adf47
 */

public class PlainTextFileLogger implements IFileLogger {

    private File file;
    protected final String name = "CSV";
    protected final String LOG = "PlainTextFileLogger";

    public PlainTextFileLogger(File file) {
        this.file = file;
    }

    @Override
    public void Write(Location loc) throws Exception {
        if (!file.exists()) {
            file.createNewFile();

            DateTime now = new DateTime();
            FileOutputStream writer = new FileOutputStream(file, true);
            BufferedOutputStream output = new BufferedOutputStream(writer);
            String header = "# === BEGINNING OUTPUT DUMP at " + formatDate(now, false) + " === #\n " +
                    "# === FORMAT unixtime, lat, lon, elevation, accuracy, bearing, speed ===#\n" +
                    "# === SENSOR TYPE: GPS === #";

            //output.write(header.getBytes());
            output.flush();
            output.close();
        }

        FileOutputStream writer = new FileOutputStream(file, true);
        BufferedOutputStream output = new BufferedOutputStream(writer);

        String dateTimeString = Utilities.GetIsoDateTime(new Date(loc.getTime()));
        long dateunix = loc.getTime();
        Log.d(LOG, "DateTimeString: " + dateTimeString);
        Log.d(LOG, "UnixTimeStamp: " + dateunix);

        String outputString = String.format(Locale.US, "%s,%f,%f,%f,%f,%f,%f \n", dateunix,
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getAltitude(),
                loc.getAccuracy(),
                loc.getBearing(),
                loc.getSpeed());

        output.write(outputString.getBytes());
        Log.i(LOG, "SensorOutput: " + outputString);

        output.flush();
        output.close();
    }

    @Override
    public void Annotate(String description, Location loc) throws Exception {
        // TODO Auto-generated method stub

    }

    private String formatDate(DateTime date, boolean forFilePath) {
        String format = forFilePath ? "yyyy-MM-dd_HH.mm" : "yyyy/MM/dd HH:mm";
        DateTimeFormatter fmt_date = DateTimeFormat.forPattern(format);
        String formattedDate = fmt_date.print(date);
        return formattedDate;
    }

    @Override
    public String getName() {
        return name;
    }

}
