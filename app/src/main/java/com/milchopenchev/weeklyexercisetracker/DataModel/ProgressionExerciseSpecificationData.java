package com.milchopenchev.weeklyexercisetracker.DataModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Milcho on 3/20/2016.
 */
public class ProgressionExerciseSpecificationData {

    // DO NOT CHANGE!
    public static final String UNIQUE_ID_PREFIX = "PESD_";

    protected List<SingleExerciseSpecificationData> exerciseProgressionSteps = new ArrayList<>();
    protected String uniqueID;

    public ProgressionExerciseSpecificationData() {
        uniqueID = generateUniqueId();
    }

    public ProgressionExerciseSpecificationData(String uniqueID) {
        uniqueID = uniqueID;
    }

    public ProgressionExerciseSpecificationData(List<SingleExerciseSpecificationData> data) {
        if (data != null) {
            exerciseProgressionSteps = data;
        } else {
            exerciseProgressionSteps = new ArrayList<>();
        }
        uniqueID = generateUniqueId();
    }

    public void setExerciseProgressionSteps(List<SingleExerciseSpecificationData> exerciseProgressionSteps) {
        if (exerciseProgressionSteps != null) {
            this.exerciseProgressionSteps = exerciseProgressionSteps;
        } else {
            this.exerciseProgressionSteps = new ArrayList<>();
        }
    }

    public List<SingleExerciseSpecificationData> getExerciseProgressionSteps() {
        return exerciseProgressionSteps;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    ////////////////////////////////////////////////////////////////
    // Static methods
    ////////////////////////////////////////////////////////////////

    private static String generateUniqueId() {
        // just use milisecond timestamp from UTC - as the user should not be able to create multiple exercises at the exact same milisecond time!
        return UNIQUE_ID_PREFIX + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
    }

}
