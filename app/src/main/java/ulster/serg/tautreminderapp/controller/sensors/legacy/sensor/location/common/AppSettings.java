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

package ulster.serg.tautreminderapp.controller.sensors.legacy.sensor.location.common;

import android.app.Application;

public class AppSettings extends Application {

    // ---------------------------------------------------
    // User Preferences
    // ---------------------------------------------------
    private static boolean useImperial = false;
    private static boolean newFileOnceADay = false; //manual override
    private static String newFileCreation;
    private static boolean preferCellTower;
    private static boolean logToPlainText = true; // manual override
    private static boolean logToGpx = true;
    private static boolean showInNotificationBar = true;
    private static int minimumSeconds;
    private static boolean keepFix;
    private static int retryInterval;
    private static boolean debugToFile;
    private static int minimumDistance = 0;
    private static int minimumAccuracy;
    private static boolean shouldSendZipFile;

    private static boolean isStaticFile = false;

    /**
     * @return the useImperial
     */
    public static boolean shouldUseImperial() {
        return useImperial;
    }

    /**
     * @param useImperial the useImperial to set
     */
    static void setUseImperial(boolean useImperial) {
        AppSettings.useImperial = useImperial;
    }

    /**
     * @return the newFileOnceADay
     */
    public static boolean shouldCreateNewFileOnceADay() {
        return newFileOnceADay;
    }

    /**
     * @param newFileOnceADay the newFileOnceADay to set
     */
    static void setNewFileOnceADay(boolean newFileOnceADay) {
        AppSettings.newFileOnceADay = newFileOnceADay;
    }

    /**
     * @return the preferCellTower
     */
    public static boolean shouldPreferCellTower() {
        return preferCellTower;
    }

    /**
     * @param preferCellTower the preferCellTower to set
     */
    static void setPreferCellTower(boolean preferCellTower) {
        AppSettings.preferCellTower = preferCellTower;
    }

    public static boolean shouldLogToPlainText() {
        return logToPlainText;
    }

    static void setLogToPlainText(boolean logToPlainText) {
        AppSettings.logToPlainText = logToPlainText;
    }

    /**
     * @return the logToGpx
     */
    public static boolean shouldLogToGpx() {
        return logToGpx;
    }

    /**
     * @param logToGpx the logToGpx to set
     */
    static void setLogToGpx(boolean logToGpx) {
        AppSettings.logToGpx = logToGpx;
    }

    /**
     * @return the showInNotificationBar
     */
    public static boolean shouldShowInNotificationBar() {
        return showInNotificationBar;
    }

    /**
     * @param showInNotificationBar the showInNotificationBar to set
     */
    static void setShowInNotificationBar(boolean showInNotificationBar) {
        AppSettings.showInNotificationBar = showInNotificationBar;
    }

    /**
     * @return the minimumSeconds
     */
    public static int getMinimumSeconds() {
        return minimumSeconds;
    }

    /**
     * @param minimumSeconds the minimumSeconds to set
     */
    static void setMinimumSeconds(int minimumSeconds) {
        AppSettings.minimumSeconds = minimumSeconds;
    }

    /**
     * @return the keepFix
     */
    public static boolean shouldkeepFix() {
        return keepFix;
    }

    /**
     * @param keepFix the keepFix to set
     */
    static void setKeepFix(boolean keepFix) {
        AppSettings.keepFix = keepFix;
    }

    /**
     * @return the retryInterval
     */
    public static int getRetryInterval() {
        return retryInterval;
    }

    /**
     * @param retryInterval the retryInterval to set
     */
    static void setRetryInterval(int retryInterval) {
        AppSettings.retryInterval = retryInterval;
    }

    /**
     * @return the minimumDistance
     */
    public static int getMinimumDistanceInMeters() {
        return minimumDistance;
    }

    /**
     * @param minimumDistance the minimumDistance to set
     */
    static void setMinimumDistanceInMeters(int minimumDistance) {
        AppSettings.minimumDistance = minimumDistance;
    }

    /**
     * @return the minimumAccuracy
     */
    public static int getMinimumAccuracyInMeters() {
        return minimumAccuracy;
    }

    /**
     * @param minimumAccuracy the minimumAccuracy to set
     */
    static void setMinimumAccuracyInMeters(int minimumAccuracy) {
        AppSettings.minimumAccuracy = minimumAccuracy;
    }

    /**
     * @return the newFileCreation
     */
    static String getNewFileCreation() {
        return newFileCreation;
    }

    /**
     * @param newFileCreation the newFileCreation to set
     */
    static void setNewFileCreation(String newFileCreation) {
        AppSettings.newFileCreation = newFileCreation;
    }

    public static boolean isDebugToFile() {
        return debugToFile;
    }

    public static void setDebugToFile(boolean debugToFile) {
        AppSettings.debugToFile = debugToFile;
    }

    public static boolean shouldSendZipFile() {
        return shouldSendZipFile;
    }

    public static void setShouldSendZipFile(boolean shouldSendZipFile) {
        AppSettings.shouldSendZipFile = shouldSendZipFile;
    }

    public static boolean isStaticFile() {
        return isStaticFile;
    }

    public static void setStaticFile(boolean staticFile) {
        AppSettings.isStaticFile = staticFile;
    }
}
