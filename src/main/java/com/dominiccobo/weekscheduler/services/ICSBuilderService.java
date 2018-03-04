package com.dominiccobo.weekscheduler.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service used for building the ICS output file.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ICSBuilderService {

    /**
     * List of events formatted to the ICS standard. See RFC on IETF.
     */
    private List<String> icsFormattedEvents = new ArrayList<>();

    /**
     * ICS output of formatted calendar events.
     */
    private String ICSOutput;

    /**
     * Adds an entry to the list of ICS events,
     *
     *  note, that assumptions are made that these are valid.
     * @param entry the ICS formatted entry to add.
     */
    public void addEntry(String entry) {
        getIcsFormattedEvents().add(entry);
    }

    /**
     * Builds the the entirely of the individual ICS events into a single ICSString with the header and footer parts
     * included.
     */
    public void buildToICSString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BEGIN:VCALENDAR\n");
        stringBuilder.append("VERSION:2.0\n");
        stringBuilder.append("PRODID://com//dominiccobo//brunel//weekscheduler\n");

        for(String event: getIcsFormattedEvents()) {
            stringBuilder.append(event);
        }
        stringBuilder.append("END:VCALENDAR");

        this.setICSOutput(stringBuilder.toString());
    }

    /**
     * Writes the ICS calendar to the desired directory.
     * @param directory the directory to write the ICS directory to.
     */
    public void buildToICSFile(String directory) {

        directory += "out.ics";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(directory));
            writer.write(this.getICSOutput());
            writer.close();

        } catch (IOException e) {
            throw new IllegalArgumentException("There was an issue with the provided directory");
        }
        System.out.println("Successfully written " + icsFormattedEvents.size() + " events to " + directory);
    }

    /**
     * Getter, permitting retrieval of the events locally.
     * @return list of events.
     */
    private List<String> getIcsFormattedEvents() {
        return icsFormattedEvents;
    }

    /**
     * Ensures the ICS output is built to a string and returns it.
     * @return the ICS formatted events.
     */
    public String getICSOutput() {
        this.buildToICSString();
        return ICSOutput;
    }

    /**
     * Generic setter for ICS output field.
     * @param ICSOutput the value to set the ICS output field to.
     */
    private void setICSOutput(String ICSOutput) {
        this.ICSOutput = ICSOutput;
    }
}
