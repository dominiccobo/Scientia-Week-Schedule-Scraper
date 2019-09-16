package com.dominiccobo.weekscheduler.services;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Automates the process of browsing through the series of links used in Brunel's timetable system.
 *
 * @author Dominic Cobo (contact@dominiccobo.com)
 */
public class ScheduleBrowserAutomationService {

    private static final String BASE_URL = "https://teaching.brunel.ac.uk";
    private static final String SCHEDULE_LOGIN_PATH = "/login.aspx";
    private static final String SCHEDULE_DEFAULT_PATH = "/default.aspx";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";


    /**
     * Current cookies generated when navigating to a page, required for persisting session.
     */
    private java.util.Map<String, String> currentCookies = new HashMap<String, String>();

    private AcademicYear academicYear;

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
     * @param academicYear the academic year of the timetable to access.
     */
    public ScheduleBrowserAutomationService(UserScheduleDetail userScheduleDetail, AcademicYear academicYear) throws Exception {
        this.academicYear = academicYear;
        this.setUserScheduleDetail(userScheduleDetail);
        this.browse();
    }

    /**
     * Automates the browsing process to the timetable.
     */
    private void browse() throws Exception{

        Connection.Response res = null;
        openLoginPage();
        login();
        navigateToTimetableSelection();
        changeTypeOfReport();
        openSelectedTimetable();
    }

    private void openSelectedTimetable() throws IOException {
        Connection.Response res;
        Map<String, String> showTimetableData = new HashMap<String, String>();
        showTimetableData.put("tLinkType", "mystudentsettimetable");
        showTimetableData.put("tUser", this.getUserScheduleDetail().getUsername());
        showTimetableData.put("lbWeeks", this.getUserScheduleDetail().getWeekNumber());
        showTimetableData.put("dlPeriod", "1-24");
        showTimetableData.put("dlType", "TextSpreadsheet;swsurl;SWSCUST Object TextSpreadsheet");
        showTimetableData.put("bGetTimetable", "View Timetable");
        showTimetableData = createPostBack(this.getDocument(), showTimetableData);

        res = Jsoup
                .connect(buildUrl(SCHEDULE_DEFAULT_PATH, academicYear))
                .userAgent(USER_AGENT)
                .data(showTimetableData)
                .followRedirects(true)
                .method(Connection.Method.POST)
                .cookies(this.currentCookies)
                .execute();

        this.setDocument(res.parse());
    }

    /**
     * Change the type of timetable display to use list timetables as this is easier to parse.
     * @throws IOException
     */
    private void changeTypeOfReport() throws IOException {
        Connection.Response res;
        Map<String, String> timetableDlTypeData = new HashMap<String, String>();
        timetableDlTypeData.put("__EVENTTARGET", "dlType");
        timetableDlTypeData = createPostBack(this.getDocument(), timetableDlTypeData);

        res = Jsoup
                .connect(buildUrl(SCHEDULE_DEFAULT_PATH, academicYear))
                .userAgent(USER_AGENT)
                .data(timetableDlTypeData)
                .method(Connection.Method.POST)
                .followRedirects(true)
                .cookies(this.currentCookies)
                .execute();


        this.setDocument(res.parse());
    }

    private void navigateToTimetableSelection() throws IOException {
        Connection.Response res;// add schedule system area selection data to .NET post back data.
        Map<String, String> timetableSelectionData = new HashMap<String, String>();
        timetableSelectionData.put("__EVENTTARGET", "LinkBtn_mystudentsettimetable");
        timetableSelectionData = createPostBack(this.getDocument(), timetableSelectionData);

        // send data and change view
        res = Jsoup
                .connect(buildUrl(SCHEDULE_DEFAULT_PATH, academicYear))
                .userAgent(USER_AGENT)
                .data(timetableSelectionData)
                .method(Connection.Method.POST)
                .cookies(this.currentCookies)
                .execute();

        this.setDocument(res.parse());
    }

    private void login() throws Exception {
        Connection.Response res;// add login data to standard .NET post back data.
        Map<String, String> loginData = new HashMap<String, String>();
        loginData.put("tUserName", this.getUserScheduleDetail().getUsername());
        loginData.put("tPassword", this.getUserScheduleDetail().getPassword());
        loginData.put("bLogin", "Login");
        loginData = createPostBack(this.getDocument(), loginData);

        // submit post request with formulated data.
        res = Jsoup
                .connect(buildUrl(SCHEDULE_LOGIN_PATH, academicYear))
                .userAgent(USER_AGENT)
                .data(loginData)
                .method(Connection.Method.POST)
                .execute();

        this.setDocument(res.parse());
        Elements errorMessageElements = this.document.getElementsByClass("ErrorMessage");
        if(errorMessageElements.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("Error during login: \n");
            for(Element messageElement: errorMessageElements) {
                stringBuilder.append(messageElement.text()).append("\n");
            }
            throw new Exception(stringBuilder.toString());
        }
        this.currentCookies = res.cookies();
    }

    private void openLoginPage() throws IOException {
        Connection.Response res;// retrieve DOM  so we can extract post back fields.
        res = Jsoup
                .connect(buildUrl(SCHEDULE_LOGIN_PATH, academicYear))
                .userAgent(USER_AGENT)
                .method(Connection.Method.GET)
                .execute();

        this.setDocument(res.parse());
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

    /**
     * Builds the URL to access the timetable fer a particular year.
     * @param path the path o
     * @param academicYear an object ({@link AcademicYear}) representing the current academic year.
     * @return
     */
    private static String buildUrl(String path, AcademicYear academicYear) {
        return String.format("%s/SWS-%s%s", BASE_URL, academicYear.asString(), path);
    }
}
