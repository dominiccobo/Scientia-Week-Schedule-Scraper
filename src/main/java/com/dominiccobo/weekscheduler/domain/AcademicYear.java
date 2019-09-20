package com.dominiccobo.weekscheduler.domain;

import java.time.LocalDateTime;

/**
 * Represents an acedemic year, as a new URL is created for each academic year the timetable is used.
 */
public class AcademicYear {

    public int start;
    public int end;

    public AcademicYear(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public String asString() {
        return String.format("%d%d", this.start, this.end);
    }

    @Override
    public String toString() {
        return String.format("Starting: %d, Ending %d", this.start, this.end);
    }

    public static AcademicYear getCurrentAcademicYear() {
        AcademicYear academicYear;
        LocalDateTime now = LocalDateTime.now();
        String lastTwoDigitsOfStartingYearAsString = Integer.toString(now.getYear()).substring(2);
        Integer startingYear = new Integer(lastTwoDigitsOfStartingYearAsString);
        academicYear = new AcademicYear(startingYear, ++startingYear);
        return academicYear;
    }
}
