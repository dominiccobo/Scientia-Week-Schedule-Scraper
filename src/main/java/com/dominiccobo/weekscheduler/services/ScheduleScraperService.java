package com.dominiccobo.weekscheduler.services;

import com.dominiccobo.weekscheduler.domain.ScheduleEvent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Definition of the parser used to scrape content from the List view timetable and parse it into a series of Schedule,
 * events.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ScheduleScraperService {

    private org.jsoup.nodes.Document document;
    private List<ScheduleEvent> scheduleEvents;

    /**
     * Constructor implementing data scraping logic.
     *
     * @param document the document to parse and scrape the timetable events from.
     */
    public ScheduleScraperService(Document document) {

        this.setDocument(document);

        List<String> headers = new ArrayList<>();

        // in the event that there are no events then lets skip this.
        if(this.getDocument().getElementsByClass("columnTitles").first() == null) {
            throw new IllegalStateException(
                    "The application was passed a null calendar. \n Please check your credentials"
            );
        }

        for(Element column: this.getDocument().getElementsByClass("columnTitles").first().children()) {
            headers.add(column.text());
        }


        // retrieve date for week start
        String weekBeginning = this.getDocument()
                .getElementsByClass("header-1-2-3")
                .text();

        weekBeginning = weekBeginning.substring(0, weekBeginning.indexOf('-'));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

        LocalDate currentDay = LocalDate.parse(
                weekBeginning,
                dateFormatter
        );


        int dayIdx = 1;

        // for each day of the week
        for(Element weekDay: this.getDocument().getElementsByClass("spreadsheet")) {

            // get a list of events for the work day (defined by the table row tag)
            Elements listOfEvents = weekDay.getElementsByTag("tr");

            // for each row, minus the 1st one, which is the row containing the headers
            for (int i = 1; i < listOfEvents.size(); i++) {
                Element event = listOfEvents.get(i);

                // get all attributes of an event (contained in cells)
                Elements eventAttributes = event.getElementsByTag("td");

                // if the number of columns is equal to the number of expected headers ...
                if(eventAttributes.size() == headers.size()) {
                    String[] data = new String[8];

                    for (int j = 0, eventAttributesSize = eventAttributes.size(); j < eventAttributesSize; j++) {
                        Element field = eventAttributes.get(j);
                        data[j] = field.text();
                    }

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

                    LocalDateTime startTime = LocalDateTime.of(
                            currentDay,
                            LocalTime.parse(
                                    data[2],
                                    timeFormatter
                            )
                    );

                    LocalDateTime endTime = LocalDateTime.of(
                            currentDay,
                            LocalTime.parse(
                                    data[3],
                                    timeFormatter
                            )
                    );

                    DateTimeFormatter isoDateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

                    ScheduleEvent scheduleEvent = new ScheduleEvent(
                        data[0],
                        data[1],
                        startTime.format(isoDateTimeFormat),
                        endTime.format(isoDateTimeFormat),
                        data[4],
                        data[5],
                        data[6],
                        data[7]
                    );

                    this.getScheduleEvents().add(scheduleEvent);
                }
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    /**
     * Getter for scheduleEvents field, ensuring that null access cannot be possible, instantiating
     * the object to prevent said access from occurring.
     *
     * @return the list of schedule events existing.
     */
    private List<ScheduleEvent> getScheduleEvents() {

        if(this.scheduleEvents == null) {
            this.scheduleEvents = new ArrayList<>();
        }
        return this.scheduleEvents;
    }

    /**
     * Publicly exposed getter, deep copies results preventing modification of contents of instantiated class.
     *
     * @return deep copy of results, that is list of schedule events parsed from the page.
     */
    public List<ScheduleEvent> getResult() {

        final List<ScheduleEvent> finalResult = new ArrayList<>();
        finalResult.addAll(this.getScheduleEvents());
        return finalResult;
    }

    /**
     * Getter for document field.
     *
     * @return the document field contents.
     */
    private Document getDocument() {
        return document;
    }

    /**
     * Setter for the document field.
     *
     * @param document the document to set.
     */
    private void setDocument(Document document) {
        this.document = document;
    }
}
