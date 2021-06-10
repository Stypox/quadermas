package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventExtractor {

    private static final String indexUrl = "https://rosmini-tn.registroelettronico.com/mastercom/index.php";

    /**
     * Get events from the given url with given user and password
     *
     * @param itemErrorHandler the error handler for single items
     * @return the event list wrapped in a Single to subscribe to
     */
    public static Single<List<EventData>> fetchEvents(final ItemErrorHandler itemErrorHandler) {
        return Single.fromCallable(() -> {
            try {
                final Document document = downloadPage();
                final List<EventData> events = new ArrayList<>();

                // select the table where the annotations are stored
                final Elements annotationTable = document.select("tbody tr");

                // cycle through every "td" which represents a single day
                for (final Element annotation : annotationTable) {
                    // get date and events containers
                    final Element eventsDate = annotation.select("td").first();
                    final Element eventsContainer = annotation.select("td").last();

                    // save the date of the events
                    // date is always in <day number> <month name> [...] format
                    final String[] date = eventsDate.text().split(" ");
                    final String day = date[0].trim();
                    final String month = date[1].trim();

                    //cycle through every event in that day
                    for (final Element event : eventsContainer
                            .getElementsByAttributeValue("data-type", "annotazione")) {
                        try {
                            events.add(eventDataFrom(EventData.Type.annotation, day, month, event));
                        } catch (final Throwable e) {
                            itemErrorHandler.onItemError(ExtractorError.asExtractorError(e, true));
                        }
                    }
                    for (final Element event : eventsContainer
                            .getElementsByAttributeValue("data-type", "evento")) {
                        try {
                            events.add(eventDataFrom(EventData.Type.event, day, month, event));
                        } catch (final Throwable e) {
                            itemErrorHandler.onItemError(ExtractorError.asExtractorError(e, true));
                        }
                    }
                }

                return events;

            } catch (final Throwable e) {
                throw ExtractorError.asExtractorError(e, true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * Retrieves the "agenda" page of the website (if supported) of the student
     *
     * @return the "agenda page"
     * @throws IOException on failure
     */
    private static Document downloadPage() throws IOException {
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

    private static EventData eventDataFrom(final EventData.Type type,
                                           final String day,
                                           final String month,
                                           final Element event) throws ExtractorError {
        // time is usually in a format like this:
        // <hour start>:<minute start> <hour end>:<minute end>
        // but sometimes it could also contain the date for each time
        final String dateTime = event.select("div").text().trim();

        // teacher is always in a format like this: (<teacher name>)
        String teacher = event.select("i").text().trim();
        if (teacher.startsWith("(") && teacher.endsWith(")")) {
            // remove the parenthesis
            teacher = teacher.substring(1, teacher.length() - 1).trim();
        }

        // title is always in a format like this: <event title>
        final String title = event.select("strong").text().trim();

        // description is always in a format like this: <event description>
        // gets the text that is ONLY into the element <event>
        final String description = event.ownText().trim();

        return new EventData(type, title, description, teacher, day, month, dateTime);
    }
}
