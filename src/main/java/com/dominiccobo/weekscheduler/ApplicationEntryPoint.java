package com.dominiccobo.weekscheduler;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.Schedule;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;

/**
 * Application Entry Point
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ApplicationEntryPoint {

    public static void main(String[] args) throws Exception {

        UserScheduleDetail userScheduleDetail = new UserScheduleDetail();

        boolean repeatInput = true;

        do {
            try {
                final String user = System.console().readLine("Username: ");
                userScheduleDetail.setUsername(user);
                repeatInput = false;
            } catch (IllegalArgumentException e) {
                repeatInput = true;
                System.err.println(e.getMessage());
            }
        } while (repeatInput);

        do {
            try {
                final String password = new String(System.console().readPassword("Password: "));
                userScheduleDetail.setPassword(password);
                repeatInput = false;
            }
            catch (IllegalArgumentException e) {
                repeatInput = true;
                System.err.println(e.getMessage());
            }

        } while (repeatInput);


        do {
            try {
                System.out.println("Hint: current, previous and next are valid inputs.");
                final String weekNumber = System.console().readLine("Week number: ");
                userScheduleDetail.setWeekNumber(weekNumber);
                repeatInput = false;
            }
            catch (IllegalArgumentException e) {
                repeatInput = true;
                System.err.println(e.getMessage());
            }
        } while (repeatInput);


        // TODO: implement cross-platform directory validation.
        final String output = System.console().readLine("Output Directory: ");
        userScheduleDetail.setOutputDir(output);
        download(userScheduleDetail);
    }

    private static void download(UserScheduleDetail userScheduleDetail) throws Exception {
        Schedule schedule = new Schedule(new AcademicYear(19, 20), userScheduleDetail);
        schedule.retrieve();
    }
}
