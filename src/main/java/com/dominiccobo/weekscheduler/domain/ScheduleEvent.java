package com.dominiccobo.weekscheduler.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Model representing a Schedule Event found from the Scientia System.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ScheduleEvent {

    static final String TEMPORARY_UNIVERSITY_LOCATION = "Brunel University, Kingston Ln, London, Uxbridge UB8 3PH";

    private String activity;
    private String description ;
    private String startingTime;
    private String finishingTime;
    private String weeks;
    private String room;
    private String staff;
    private String type;

    public ScheduleEvent(String activity, String description, String startingTime, String finishingTime,
                         String weeks, String room, String staff, String type) {


        this.activity = activity;
        this.description = description;

        this.startingTime = startingTime;
        this.finishingTime =  finishingTime;
        this.weeks = weeks;
        this.room = room;
        this.staff = staff;
        this.type = type;


    }

    public String toICSFormat() {
        return String.format(
                "BEGIN:VEVENT\n" +
                        "SUMMARY:%s (%s)\n" +
                        "DESCRIPTION:Brief: %s Weeks: %s Lecturer: %s Type: %s\n" +
                        "LOCATION:%s\n" +
                        "DTSTART:%s\n" +
                        "DTEND:%s\n" +
                        "END:VEVENT\n",
                this.activity, this.room,
                this.description, this.weeks, this.staff, this.type,
                TEMPORARY_UNIVERSITY_LOCATION,
                this.startingTime,
                this.finishingTime
        );
    }
}
