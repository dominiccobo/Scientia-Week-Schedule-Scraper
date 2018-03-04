package com.dominiccobo.weekscheduler.services;

import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Automates the process of browsing through the series of links used in Brunel's timetable system.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ScheduleBrowserAutomationService {

    // TODO: automate URLs in line with the current year.
    static final String SCHEDULE_LOGIN_URL = "https://teaching.brunel.ac.uk/SWS-1718/login.aspx";
    static final String SCHEDULE_DEFAULT_URL = "https://teaching.brunel.ac.uk/SWS-1718/default.aspx";
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

    /**
     * Current cookies generated when navigating to a page, required for persisting session.
     */
    private java.util.Map<String, String> currentCookies = new HashMap<String, String>();

    /**
     * Contains the details required to browse to the timetable.
     */
    private UserScheduleDetail userScheduleDetail;

    /**
     * Current DOM at the stage of the browsing automation.
     */
    private Document document;

    /**
     * Constructor specifying only required field for service to work.
     * @param userScheduleDetail the set of details required for the automation process.
     */
    public ScheduleBrowserAutomationService(UserScheduleDetail userScheduleDetail) {
        this.setUserScheduleDetail(userScheduleDetail);
        this.browse();
    }

    /**
     * Automates the browsing process to the timetable.
     */
    private void browse()  {

        Connection.Response res = null;
        try {

            // retrieve DOM  so we can extract post back fields.
            res = Jsoup
                    .connect(SCHEDULE_LOGIN_URL)
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.GET)
                    .execute();

            this.setDocument(res.parse());

            // add login data to standard .NET post back data.
            Map<String, String> loginData = new HashMap<String, String>();
            loginData.put("tUserName", this.getUserScheduleDetail().getUsername());
            loginData.put("tPassword", this.getUserScheduleDetail().getPassword());
            loginData.put("bLogin", "Login");
            loginData = createPostBack(this.getDocument(), loginData);

            // submit post request with formulated data.
             res = Jsoup
                     .connect(SCHEDULE_LOGIN_URL)
                     .userAgent(USER_AGENT)
                     .data(loginData)
                     .method(Connection.Method.POST)
                     .execute();

            this.setDocument(res.parse());
            this.currentCookies = res.cookies();

            // add schedule system area selection data to .NET post back data.
            Map<String, String> timetableSelectionData = new HashMap<String, String>();
            timetableSelectionData.put("__EVENTTARGET", "LinkBtn_mystudentsettimetable");
            timetableSelectionData = createPostBack(this.getDocument(), timetableSelectionData);

            // send data and change view
            res = Jsoup
                    .connect(SCHEDULE_DEFAULT_URL)
                    .userAgent(USER_AGENT)
                    .data(timetableSelectionData)
                    .method(Connection.Method.POST)
                    .cookies(this.currentCookies)
                    .execute();

            this.setDocument(res.parse());


            Map<String, String> timetableDlTypeData = new HashMap<String, String>();
            timetableDlTypeData.put("__EVENTTARGET", "dlType");
            timetableDlTypeData = createPostBack(this.getDocument(), timetableDlTypeData);

            res = Jsoup
                    .connect(SCHEDULE_DEFAULT_URL)
                    .userAgent(USER_AGENT)
                    .data(timetableDlTypeData)
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .cookies(this.currentCookies)
                    .execute();


            this.setDocument(res.parse());

            Map<String, String> showTimetableData = new HashMap<String, String>();
            showTimetableData.put("tLinkType", "mystudentsettimetable");
            showTimetableData.put("tUser", this.getUserScheduleDetail().getUsername());
            showTimetableData.put("lbWeeks", this.getUserScheduleDetail().getWeekNumber());
            showTimetableData.put("dlPeriod", "1-24");
            showTimetableData.put("dlType", "TextSpreadsheet;swsurl;SWSCUST Object TextSpreadsheet");
            showTimetableData.put("bGetTimetable", "View Timetable");
            showTimetableData = createPostBack(this.getDocument(), showTimetableData);

            res = Jsoup
                    .connect(SCHEDULE_DEFAULT_URL)
                    .userAgent(USER_AGENT)
                    .data(showTimetableData)
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .cookies(this.currentCookies)
                    .execute();

            this.setDocument(res.parse());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Generates a valid post-back Hashmap (used by MS) to send data.
     * @param document the current document object model to create the post back for.
     * @param additionalParams the additional parameters to append to the post-back.
     * @return
     */
    private Map<String, String> createPostBack(Document document, Map<String, String> additionalParams) {

        Map<String, String> mapToReturn = new HashMap<String, String>();
        mapToReturn.put("__VIEWSTATE",  document.getElementById("__VIEWSTATE").attr("value"));
        mapToReturn.put("__LASTFOCUS",  "");
        mapToReturn.put("__VIEWSTATEGENERATOR",  document.getElementById("__VIEWSTATEGENERATOR").attr("value"));
        mapToReturn.put("__EVENTVALIDATION",  document.getElementById("__EVENTVALIDATION").attr("value"));

        if(additionalParams != null) {
            mapToReturn.putAll(additionalParams);
        }

        return mapToReturn;
    }

    /**
     * Getter for schedule detail field.
     * @return the value of the schedule detail field.
     */
    private UserScheduleDetail getUserScheduleDetail() {
        return userScheduleDetail;
    }

    /**
     * Setter for the schedule detail field.
     * @param userScheduleDetail
     */
    private void setUserScheduleDetail(UserScheduleDetail userScheduleDetail) {
        this.userScheduleDetail = userScheduleDetail;
    }

    /**
     * Public getter to retrieve the DOM. Needs to be publicly exposed so that it can be parsed elsewhere.
     * @return the current DOM.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Setter for the DOM.
     * @param document the DOM to set as current.
     */
    private void setDocument(Document document) {
        this.document = document;
    }

}
