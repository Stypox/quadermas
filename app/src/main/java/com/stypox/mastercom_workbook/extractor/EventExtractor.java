package extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Event;

public class EventExtractor {
    /**Get events from the given url with given user and password
     * 
     * @param baseUrl the url from where to load the events
     * @param user the username
     * @param password the password
     * @return the event list
     * @throws IOException
     */
    public static ArrayList<Event> getEvents (String baseUrl, String user, String password) throws IOException {
        return getEvents(getPage(baseUrl, user, password));
    }

    /**Get events from the given HTML file
     * 
     * @param agendaPage the HTML document
     * @return the event list
     */
    public static ArrayList<Event> getEvents (Document agendaPage) {
        ArrayList<Event> eventList = new ArrayList<Event>();

        //select the table where the annotations are stored
        Elements annotationTable = agendaPage.select("tbody tr");

        //cycle through every "td" which represents a single day
        for (Element annotation: annotationTable) {
            //get events and date containers
            Element eventsDate      = annotation.select("td").first();
            Element eventsContainer = annotation.select("td").last();
            
            String date;
            String day;
            String month;
            String dayName;
            boolean isTomorrow;

            date = eventsDate.text();
            /*
            save the date of the events
            date is always in <day number> <month name> <day name> [DOMANI] format
            */
            day = date.split(" ")[0]; 
            month = date.split(" ")[1];
            dayName = date.split(" ")[2]; 
            isTomorrow = date.endsWith("DOMANI");

            //cycle through every event in that day
            for (Element event: eventsContainer.getElementsByAttributeValue("data-type", "annotazione")) {
                String title;
                String description;
                String teacher;
                String timeStart;
                String timeEnd;

                //time is always in a format like this: <hour start>:<minute start> <hour end>:<minute end>
                timeStart = event.select("div").text().split(" ")[0];
                timeEnd   = event.select("div").text().split(" ")[1];

                //teacher is always in a format like this: (<teacher name>)
                teacher = event.select("i").text();
                //remove the parenthesis
                teacher = teacher.substring(1, teacher.length()-1);

                //title is always in a format like this: <event title>
                title = event.select("strong").text();

                //description is always in a format like this: <event description>
                description = event.ownText(); //gets the text that is ONLY into the element <event>

                eventList.add(
                    new Event(
                        title, description, teacher,
                        day, month, dayName, isTomorrow,
                        timeStart, timeEnd)
                );
            }
        }
        
        return eventList;
    }

    /**Retrieves the "agenda" page of the website (if supported) of the student
     * 
     * @param baseUrl the url of the site
     * @param user the username of the account
     * @param password the password of the account
     * @return the "agenda page"
     * @throws IOException on failure
     */
    public static Document getPage (String baseUrl, String user, String password) throws IOException {
        Document page; //buffer for currently loaded HTML page
        Connection pageConnection; //the connection to the server
        String currentUser = "", currentKey = "";

        //prepare the login to the main page url
        pageConnection = Jsoup.connect(baseUrl)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .data("user", user)
            .data("password_user", password)
            .data("form_login", "true");
        
        //login to page
        page = pageConnection.post();
        
        //extract userID and userKey
        currentUser  = page.select("input#current_user").attr("value");
        currentKey   = page.select("input#current_key").attr("value");
        
        //load "agenda" page
        page = Jsoup.connect(baseUrl)
        .header("Content-Type", "multipart/form-data")
        .data(
            /*<key>             <val>*/
            "form_stato",       "studente",
            "stato_principale", "agenda",
            "current_user",     currentUser,
            "current_key",      currentKey,
            "header",           "SI"
        )
        .post();
        
        return page;
    }
}
