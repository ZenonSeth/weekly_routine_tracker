package com.milchopenchev.weeklyexercisetracker.DataModel;

import enums.RepeatType;

import java.util.Collections;
import java.util.List;

/**
 * Created by Milcho on 3/24/2016.
 */
public class Exercise {

    int currentProgressionStep = -1;
    ProgressionExerciseSpecificationData exerciseData;
    List<Integer> progressionStepsSetCounts;
    List<Integer> progressionStepsRepCounts;

    ////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////

    public Exercise() {
        exerciseData = new ProgressionExerciseSpecificationData();
    }

    public Exercise(ProgressionExerciseSpecificationData data) {
        if (data != null) {
            exerciseData = data;
        } else {
            exerciseData = new ProgressionExerciseSpecificationData();
        }
        if (exerciseData.getExerciseProgressionSteps().size() > 0) {
            currentProgressionStep = 0;
        }
    }

    ////////////////////////////////////////////////////////////////
    // information getters
    ////////////////////////////////////////////////////////////////

    public String getCurrentTitle() {
        if (currentProgressionStep >= 0) {
            return exerciseData.getExerciseProgressionSteps().get(currentProgressionStep).getTitle();
        } else {
            return null;
        }
    }

    public String getCurrentDescription() {
        if (currentProgressionStep >= 0) {
            return exerciseData.getExerciseProgressionSteps().get(currentProgressionStep).getDescription();
        } else {
            return null;
        }
    }

    public RepeatType getCurrentCountType() {
        if (currentProgressionStep >= 0) {
            return exerciseData.getExerciseProgressionSteps().get(currentProgressionStep).getCountType();
        } else {
            return null;
        }
    }

    public int getCurrentSetCount() {
        return progressionStepsSetCounts.get(currentProgressionStep);
    }

    public void setCurrentSetCount(int count) {
        if (count <= 0) {
            return;
        }
        progressionStepsSetCounts.set(currentProgressionStep, count);
    }

    public int getCurrentRepCount() {
        return progressionStepsRepCounts.get(currentProgressionStep);
    }

    public void setCurrentRepCount(int count) {
        if (count <= 0) {
            return;
        }
        progressionStepsRepCounts.set(currentProgressionStep, count);
    }

    ////////////////////////////////////////////////////////////////
    // information setters
    ////////////////////////////////////////////////////////////////

    public int getCurrentProgressionStep() {
        return currentProgressionStep;
    }

    public void setCurrentProgressionStep(int step) {
        if (step < 0 || step >= exerciseData.getExerciseProgressionSteps().size()) {
            return;
        }
        currentProgressionStep = step;
    }

    public int getTotalProgressionStepCount() {
        return exerciseData.getExerciseProgressionSteps().size();
    }

    public void addExerciseProgressionStep(SingleExerciseSpecificationData step) {
        if (step == null) {
            return;
        }
        exerciseData.getExerciseProgressionSteps().add(step);
        progressionStepsSetCounts.add(1);
        progressionStepsRepCounts.add(1);
        if (currentProgressionStep < 0) {
            currentProgressionStep = 0;
        }
    }

    public void deleteExerciseProgressionStep(int index) {
        if (index < 0 || index >= exerciseData.getExerciseProgressionSteps().size()) {
            return;
        }
        if (currentProgressionStep >= index && index > 0) {
            currentProgressionStep--;
        }
        exerciseData.getExerciseProgressionSteps().remove(index);
        progressionStepsSetCounts.remove(index);
        progressionStepsRepCounts.remove(index);
    }

    public void moveProgressionStepForward(int index) {
        if (index < 0 || index >= exerciseData.getExerciseProgressionSteps().size() - 1) {
            return;
        }
        Collections.swap(exerciseData.getExerciseProgressionSteps(), index, index + 1);
        Collections.swap(progressionStepsSetCounts, index, index + 1);
        Collections.swap(progressionStepsRepCounts, index, index + 1);
    }

    public void moveProgressionStepBackwards(int index) {
        if (index <= 0 || index >= exerciseData.getExerciseProgressionSteps().size()) {
            return;
        }
        Collections.swap(exerciseData.getExerciseProgressionSteps(), index, index - 1);
        Collections.swap(progressionStepsSetCounts, index, index - 1);
        Collections.swap(progressionStepsRepCounts, index, index - 1);
    }

}
