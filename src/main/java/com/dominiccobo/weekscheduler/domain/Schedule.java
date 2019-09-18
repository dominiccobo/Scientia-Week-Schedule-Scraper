package com.dominiccobo.weekscheduler.domain;

import com.dominiccobo.weekscheduler.services.ICSBuilderService;
import com.dominiccobo.weekscheduler.services.ScheduleBrowserAutomationService;
import com.dominiccobo.weekscheduler.services.ScheduleScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of individual events and implements collection of logic to
 * scrape entire schedule from Timetable system.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class Schedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(Schedule.class);

    private AcademicYear academicYear;
    private UserScheduleDetail userDetails;
    private List<ScheduleEvent> events = new ArrayList<>();

    public final List<ScheduleEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public Schedule(AcademicYear academicYear, UserScheduleDetail userDetails) {
        this.academicYear = academicYear;
        this.userDetails = userDetails;
    }

    public void retrieve() throws Exception {
        LOGGER.info("Retrieving timetable. Yr:{}, user:{}, week:{}", academicYear, userDetails.getUsername(), userDetails.getWeekNumber());
        ScheduleBrowserAutomationService browserAutomation = new ScheduleBrowserAutomationService(
                this.userDetails,
                this.academicYear
        );

        ScheduleScraperService scheduleScraperService = new ScheduleScraperService(browserAutomation.getDocument());
        this.events = scheduleScraperService.getResult();
        save(userDetails.getOutputDir());
    }

    private void save(String outputDir) {
        LOGGER.info("Saving ICS file to {}", outputDir);
        ICSBuilderService icsBuilderService = new ICSBuilderService();
        for(ScheduleEvent event: this.events) {
            icsBuilderService.addEntry(event.toICSFormat());
        }
        icsBuilderService.buildToICSFile(outputDir);
    }
}
