package com.phorloop.tautreminderapp.controller.helpers;

import com.phorloop.tautreminderapp.model.sugarorm.Reminder;

import java.io.File;

/**
 * Created by philliphartin on 26/09/2014.
 */
public class FileHelper {

    public void deleteAudioFileForReminder(Reminder reminder) {
        String filepath = reminder.getAudiofilepath();
        File file = new File(filepath);
        file.delete();
    }
}