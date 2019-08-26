package com.milchopenchev.weeklyexercisetracker.DiskIO;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.milchopenchev.weeklyexercisetracker.DataModel.ProgressionExerciseSpecificationData;
import com.milchopenchev.weeklyexercisetracker.DataModel.SingleExerciseSpecificationData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milcho on 3/24/2016.
 */
public class ExerciseStorage {

    public static final String SHARED_PREFS_KEY = "EXERCISE_STORAGE";

    private static final String SINGLE_EXERCISE_SPEC_COUNT_KEY = "SES_COUNT";
    private static final String PROGRESSION_EXERCISE_SPEC_COUNT_KEY = "PES_COUNT";

    private static final String SINGLE_EXERCISE_SPEC_ID_PREFIX = "SES_ID_";
    private static final String PROGRESSION_EXERCISE_SPEC_ID_PREFIX = "PES_ID_";

    private List<String> singleExerciseSpecificationIds = new ArrayList<>();
    private List<String> progressionExerciseSpecificationIds = new ArrayList<>();

    private final Context context;
    private final SharedPreferences prefs;


    public ExerciseStorage(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        loadAllIds();
    }

    ////////////////////////////////////////////////////////////////
    // ID access
    ////////////////////////////////////////////////////////////////

    public List<String> getSingleExerciseSpecificationIds () {
        return singleExerciseSpecificationIds;
    }

    public List<String> getProgressionExerciseSpecificationIds() {
        return progressionExerciseSpecificationIds;
    }

    ////////////////////////////////////////////////////////////////
    // ID read/write/update
    ////////////////////////////////////////////////////////////////

    private boolean removeUniqueIdFromSharedPrefs(String uniqueID) {
        if (singleExerciseSpecificationIds.contains(uniqueID)) {
            singleExerciseSpecificationIds.remove(uniqueID);
            saveSingleExerciseSpecificationIds();
            return true;
        }
        if (progressionExerciseSpecificationIds.contains(uniqueID)) {
            progressionExerciseSpecificationIds.remove(uniqueID);
            saveProgressionExerciseSpecificationIds();
            return true;
        }
        return false;
    }

    private boolean addSingleExercseSpecificationUniqueId(String uniqueID) {
        if (singleExerciseSpecificationIds.contains(uniqueID)
                || !uniqueID.startsWith(SingleExerciseSpecificationData.UNIQUE_ID_PREFIX)) {
            return false;
        } else {
            singleExerciseSpecificationIds.add(uniqueID);
            saveSingleExerciseSpecificationIds();
            return true;
        }
    }

    private boolean addProgressionExercseSpecificationUniqueId(String uniqueID) {
        if (progressionExerciseSpecificationIds.contains(uniqueID)
                || !uniqueID.startsWith(ProgressionExerciseSpecificationData.UNIQUE_ID_PREFIX)) {
            return false;
        } else {
            progressionExerciseSpecificationIds.add(uniqueID);
            saveProgressionExerciseSpecificationIds();
            return true;
        }
    }

    ////////////////////////////////////////////////////////////////
    // ID saving and retrieving from SharedPrefs
    ////////////////////////////////////////////////////////////////

    private void saveAllIds() {
        saveSingleExerciseSpecificationIds();
        saveProgressionExerciseSpecificationIds();
    }

    private void loadAllIds() {
        loadSingleExerciseSpecificationIds();
        loadProgressionExerciseSpecificationIds();
    }

    private void saveSingleExerciseSpecificationIds () {
        SharedPreferences.Editor editor = prefs.edit();
        // clear old ones
        int prevCount = prefs.getInt(SINGLE_EXERCISE_SPEC_COUNT_KEY, 0);
        editor.remove(SINGLE_EXERCISE_SPEC_COUNT_KEY);
        for (int i = 0; i < prevCount; i++) {
            editor.remove(SINGLE_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i));
        }
        // then add the new ones
        editor.putInt(SINGLE_EXERCISE_SPEC_COUNT_KEY, singleExerciseSpecificationIds.size());
        int i = 0;
        for (String id : singleExerciseSpecificationIds) {
            editor.putString(SINGLE_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i), id);
            i++;
        }
        editor.apply();
    }

    private void saveProgressionExerciseSpecificationIds () {
        SharedPreferences.Editor editor = prefs.edit();
        // clear old ones
        int prevCount = prefs.getInt(PROGRESSION_EXERCISE_SPEC_COUNT_KEY, 0);
        editor.remove(PROGRESSION_EXERCISE_SPEC_COUNT_KEY);
        for (int i = 0; i < prevCount; i++) {
            editor.remove(PROGRESSION_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i));
        }
        // then add the new ones
        editor.putInt(PROGRESSION_EXERCISE_SPEC_COUNT_KEY, progressionExerciseSpecificationIds.size());
        int i = 0;
        for (String id : progressionExerciseSpecificationIds) {
            editor.putString(PROGRESSION_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i), id);
            i++;
        }
        editor.apply();
    }

    private void loadSingleExerciseSpecificationIds() {
        singleExerciseSpecificationIds.clear();
        int count = prefs.getInt(SINGLE_EXERCISE_SPEC_COUNT_KEY, 0);
        for (int i = 0; i < count; i++) {
            String id = prefs.getString(SINGLE_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i), null);
            if (id != null) {
                singleExerciseSpecificationIds.add(id);
            }
        }
    }

    private void loadProgressionExerciseSpecificationIds() {
        progressionExerciseSpecificationIds.clear();
        int count = prefs.getInt(PROGRESSION_EXERCISE_SPEC_COUNT_KEY, 0);
        for (int i = 0; i < count; i++) {
            String id = prefs.getString(PROGRESSION_EXERCISE_SPEC_ID_PREFIX + String.valueOf(i), null);
            if (id != null) {
                progressionExerciseSpecificationIds.add(id);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // Deleting a file
    ////////////////////////////////////////////////////////////////

    public boolean delete(String uniqueID) {
        boolean res = context.deleteFile(uniqueID);
        if (res) {
            removeUniqueIdFromSharedPrefs(uniqueID);
        }
        return res;
    }


    ////////////////////////////////////////////////////////////////
    // Single exercise Read/Write
    ////////////////////////////////////////////////////////////////

    public boolean save(SingleExerciseSpecificationData data) {
        if (data == null || data.getUniqueID() == null || data.getUniqueID().isEmpty()) {
            return false;
        }
        String filename = data.getUniqueID();
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write((new Gson()).toJson(data).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        addSingleExercseSpecificationUniqueId(data.getUniqueID());
        return true;
    }

    public SingleExerciseSpecificationData loadSingleExerciseSpecification(String uniqueID) {
        try {
            FileInputStream inputStream = context.openFileInput(uniqueID);
            return (new Gson()).fromJson(ReadContent(inputStream), SingleExerciseSpecificationData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////
    // Progression exercise Read/Write
    ////////////////////////////////////////////////////////////////

    public boolean save(ProgressionExerciseSpecificationData data) {
        if (data == null || data.getUniqueID() == null || data.getUniqueID().isEmpty()) {
            return false;
        }
        String filename = data.getUniqueID();
        FileOutputStream outputStream;
        try {
            // store each step individually, and save the ids
            List<String> stepsIds = new ArrayList<>();

            // first entry is our own unique id
            stepsIds.add(data.getUniqueID());

            for (SingleExerciseSpecificationData step : data.getExerciseProgressionSteps()) {
                // save the step, get its id
                if (!save(step)) {
                    throw new Exception("Failed to save a single step as part of this progression! Terminating save!");
                }
                stepsIds.add(step.getUniqueID());
            }

            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write((new Gson()).toJson(stepsIds.toArray()).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        addProgressionExercseSpecificationUniqueId(data.getUniqueID());
        return true;
    }

    public ProgressionExerciseSpecificationData loadProgressionExerciseSpecification(String uniqueID) {
        try {
            FileInputStream inputStream = context.openFileInput(uniqueID);

            String[] uniqueIds = (new Gson()).fromJson(ReadContent(inputStream), String[].class);
            ProgressionExerciseSpecificationData progressionExerciseSpecificationData = new ProgressionExerciseSpecificationData(uniqueIds[0]);
            List<SingleExerciseSpecificationData> steps = new ArrayList<>();

            for (int i = 1; i < uniqueIds.length; i++) {
                steps.add(loadSingleExerciseSpecification(uniqueIds[i]));
            }
            progressionExerciseSpecificationData.setExerciseProgressionSteps(steps);
            return progressionExerciseSpecificationData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////
    // Helper methods
    ////////////////////////////////////////////////////////////////

    private static String ReadContent(FileInputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        int character;
        while ((character = inputStream.read()) != -1) {
            builder.append((char) character);
        }
        inputStream.close();
        return builder.toString();
    }
}
