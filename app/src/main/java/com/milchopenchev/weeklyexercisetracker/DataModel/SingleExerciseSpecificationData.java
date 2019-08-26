package com.milchopenchev.weeklyexercisetracker.DataModel;

import enums.RepeatType;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Milcho on 3/20/2016.
 */
public class SingleExerciseSpecificationData {

    // DO NOT CHANGE!
    public static final String UNIQUE_ID_PREFIX = "SESD_";

    protected String title;
    protected String description;
    protected String imageSpecifier;
    protected String uniqueID;
    protected RepeatType countType;

    ////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////

    public SingleExerciseSpecificationData () {
        // bean
    }

    public SingleExerciseSpecificationData(String title, String description, String imageSpecifier, RepeatType countType) {
        this.title = title;
        this.description = description == null ? "" : description;
        this.imageSpecifier = imageSpecifier == null ? "" : imageSpecifier;
        this.countType = countType;
        uniqueID = generateUniqueId();
    }

    public SingleExerciseSpecificationData(String uniqueID, String title, String description, String imageSpecifier, RepeatType countType) {
        this.title = title;
        this.description = description == null ? "" : description;
        this.imageSpecifier = imageSpecifier == null ? "" : imageSpecifier;
        this.countType = countType;
        this.uniqueID = uniqueID == null || uniqueID.isEmpty() ? generateUniqueId() : uniqueID;
    }

    ////////////////////////////////////////////////////////////////
    // information getters
    ////////////////////////////////////////////////////////////////

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public RepeatType getCountType() {
        return countType;
    }

    public String getImageSpecifier() {
        return imageSpecifier;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    ////////////////////////////////////////////////////////////////
    // Information setters
    ////////////////////////////////////////////////////////////////

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCountType(RepeatType countType) {
        this.countType = countType;
    }

    public void setImageSpecifier(String imageSpecifier) {
        this.imageSpecifier = imageSpecifier;
    }

    ////////////////////////////////////////////////////////////////
    // Static methods
    ////////////////////////////////////////////////////////////////

    private static String generateUniqueId() {
        // just use milisecond timestamp from UTC - as the user should not be able to create multiple exercises at the exact same milisecond time!
        return UNIQUE_ID_PREFIX + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
    }

}
