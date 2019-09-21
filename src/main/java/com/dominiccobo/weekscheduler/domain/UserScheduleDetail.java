package com.dominiccobo.weekscheduler.domain;

import org.jsoup.helper.StringUtil;

/**
 * POJO containing fields describing the exact timetable that needs to be retrieved.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class UserScheduleDetail {

    /**
     * The username to be used to log in to the time table service.
     */
    private String username;

    /**
     * The password to be used to log in to the time table service.
     */
    private String password;

    /**
     * The week number to retrieve the time table for.
     */
    private String weekNumber;

    /**
     * The directory to retrieve the ICS output to.
     */
    private String outputDir;

    /**
     * Empty constructor to enforce usage of staged setters for verification of input.
     */
    public UserScheduleDetail() { }


    /**
     *
     * @param username the username value to set.
     */
    public void setUsername(String username) {
            this.username = username;
    }

    /**
     *
     * @param password the password value to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Constrained setter for the week number field. Verifies validity of input.
     *
     * Converts values to those accepted by the Scientia system.
     * @param weekNumber the week number value to set.
     */
    public void setWeekNumber(String weekNumber) {


        if(weekNumber == null) {
            throw new IllegalArgumentException("A valid argument must be passed");
        }

        if(!StringUtil.isNumeric(weekNumber)) {

            switch (weekNumber) {
                case "current": {
                    this.weekNumber = "t";
                    return;
                }
                case "previous": {
                    this.weekNumber = "p";
                    return;
                }
                case "next": {
                    this.weekNumber = "n";
                    return;
                }
                default: {
                    throw new IllegalArgumentException("Invalid non-numeric value. Accepts: this, previous, next");
                }
            }
        }
        else {

            Integer numericRepresentation = Integer.valueOf(weekNumber);

            if(numericRepresentation >= 1 && numericRepresentation <= 52) {
                this.weekNumber = " " + weekNumber; // for some reason the values are only accepted with a prepended space...
                return;
            }
            throw new IllegalArgumentException("Numeric inputs must be greater than 0 or less than 53");
        }
    }

    /**
     * Generic setter for the outputDir field.
     * @param outputDir the value of the field to output to.
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Generic getter for username field.
     * @return value of username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Generic getter for password field.
     * @return the value of password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Generic getter for week number field.
     * @return the value of the week number field.
     */
    public String getWeekNumber() {
        return weekNumber;
    }

    /**
     * Generic getter for the output directory field.
     * @return the value of the output directory field.
     */
    public String getOutputDir() {
        return outputDir;
    }
}
