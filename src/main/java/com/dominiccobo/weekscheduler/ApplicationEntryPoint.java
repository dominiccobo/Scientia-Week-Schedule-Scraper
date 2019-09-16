package com.dominiccobo.weekscheduler;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.ScheduleEvent;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import com.dominiccobo.weekscheduler.services.ICSBuilderService;
import com.dominiccobo.weekscheduler.services.ScheduleBrowserAutomationService;
import com.dominiccobo.weekscheduler.services.ScheduleScraperService;

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


        // automate the navigation to the timetable element
        ScheduleBrowserAutomationService browserAutomation = new ScheduleBrowserAutomationService(
                userScheduleDetail,
                new AcademicYear(19, 20)
        );

        // parse all contents...
        ScheduleScraperService scheduleScraperService = new ScheduleScraperService(
                browserAutomation.getDocument()
        );

        ICSBuilderService icsBuilderService = new ICSBuilderService();

        for(ScheduleEvent event: scheduleScraperService.getResult()) {
            icsBuilderService.addEntry(event.toICSFormat());
        }

        try {
            icsBuilderService.buildToICSFile(userScheduleDetail.getOutputDir());
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }


    }
}
