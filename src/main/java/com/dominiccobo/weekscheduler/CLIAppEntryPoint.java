package com.dominiccobo.weekscheduler;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.Schedule;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Application Entry Point for the command line.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
@Command(name = "btimescraper")
public class CLIAppEntryPoint implements Callable<Integer> {

    @Option(names = {"-u", "--user"}, required = true, description = "Student ID")
    private String userName;
    @Option(names = {"-p", "--password"}, interactive = true, required = true, hidden = true,
            description = "Password associated with the student ID")
    private String password;
    @Option(names = {"-w", "--week"}, required = true,
            description = "The week number to download or current, next, previous.")
    private String weekNumber;
    @Option(names = {"-f", "--file"}, required = true,
            description = "The path and name of the output file.")
    private File output;
    @Option(names = {"-y", "--academicyear"}, required = false,
            description = "Optional: The 2 end digits of the academic year start. Defaults to current.")
    private Integer academicYearStartAbbrev;

    @Override
    public Integer call() throws Exception {

        AcademicYear academicYear;

        if(academicYearStartAbbrev == null) {
            academicYear = AcademicYear.getCurrentAcademicYear();
        }
        else {
            // TODO: inbound y2k bug, should probably change this
            academicYear = new AcademicYear(academicYearStartAbbrev, ++academicYearStartAbbrev);
        }

        UserScheduleDetail userScheduleDetail = new UserScheduleDetail();
        userScheduleDetail.setUsername(userName);
        userScheduleDetail.setPassword(password);
        userScheduleDetail.setWeekNumber(weekNumber);
        userScheduleDetail.setOutputDir(output.getAbsolutePath());
        download(userScheduleDetail, academicYear);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        String[] modifiedArgs = ArrayUtils.add(args, "-p"); // include password by default so the user isn't hassled for arg
        CommandLine commandLine = new CommandLine(new CLIAppEntryPoint());
        commandLine.execute(modifiedArgs);
    }


    private static void download(UserScheduleDetail userScheduleDetail, AcademicYear academicYear) throws Exception {
        Schedule schedule = new Schedule(academicYear, userScheduleDetail);
        schedule.retrieve();
    }
}
