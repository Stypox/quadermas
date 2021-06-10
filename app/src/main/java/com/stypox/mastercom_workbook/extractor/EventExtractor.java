package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.EventData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventExtractor {

    private static final String indexUrl = "https://rosmini-tn.registroelettronico.com/mastercom/index.php";

    /**
     * Get events from the given url with given user and password
     *
     * @return the event list
     */
    public static List<EventData> getEvents() throws IOException {
        return getEvents(getPage());
    }

    /**
     * Get events from the given HTML file
     *
     * @param agendaPage the HTML document
     * @return the event list
     */
    private static List<EventData> getEvents(final Document agendaPage) {
        List<EventData> eventList = new ArrayList<>();

        // select the table where the annotations are stored
        Elements annotationTable = agendaPage.select("tbody tr");

        // cycle through every "td" which represents a single day
        for (final Element annotation : annotationTable) {
            // get date and events containers
            final Element eventsDate = annotation.select("td").first();
            final Element eventsContainer = annotation.select("td").last();

            // save the date of the events
            // date is always in <day number> <month name> <day name> [DOMANI] format
            final String[] date = eventsDate.text().split(" ");
            final String day = date[0].trim();
            final String month = date[1].trim();

            //cycle through every event in that day
            for (final Element event
                    : eventsContainer.getElementsByAttributeValue("data-type", "annotazione")) {
                // time is always in a format like this:
                // <hour start>:<minute start> <hour end>:<minute end>
                final String[] time = event.select("div").text().split(" ");
                final String timeStart = time[0].trim();
                final String timeEnd = time[1].trim();

                // teacher is always in a format like this: (<teacher name>)
                String teacher = event.select("i").text();
                // remove the parenthesis
                teacher = teacher.substring(1, teacher.length() - 1).trim();

                // title is always in a format like this: <event title>
                final String title = event.select("strong").text().trim();

                // description is always in a format like this: <event description>
                // gets the text that is ONLY into the element <event>
                final String description = event.ownText().trim();

                eventList.add(
                        new EventData(title, description, teacher, day, month, timeStart, timeEnd));
            }
        }

        return eventList;
    }

    /**
     * Retrieves the "agenda" page of the website (if supported) of the student
     *
     * @return the "agenda page"
     * @throws IOException on failure
     */
    private static Document getPage() throws IOException {
        //prepare the login to the main page url
        final Document loginPage = Jsoup.connect(indexUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data("user", Extractor.getUser())
                .data("password_user", Extractor.getPassword())
                .data("form_login", "true")
                .post();

        //extract userID and userKey
        final String currentUser = loginPage.select("input#current_user").attr("value");
        final String currentKey = loginPage.select("input#current_key").attr("value");

        //load "agenda" page
        return Jsoup.connect(indexUrl)
                .header("Content-Type", "multipart/form-data")
                .data(
                        /*<key>             <val>*/
                        "form_stato", "studente",
                        "stato_principale", "agenda",
                        "current_user", currentUser,
                        "current_key", currentKey,
                        "header", "SI"
                )
                .post();
    }
}
