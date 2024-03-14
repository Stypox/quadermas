package com.stypox.mastercom_workbook.data;

import com.stypox.mastercom_workbook.util.DateUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FakeFetchedData {
    private FakeFetchedData() {
    }

    public static final String API_URL = "test-server-kel";
    public static final String USER = "999999";
    public static final String PASSWORD = "6xpHLe4f";
    public static final String FULL_NAME = "Theresa Aurelius";

    public static final List<SubjectData> SUBJECTS = new ArrayList<>() {{
        try {
            add(new SubjectData(new JSONObject("{\"nome\": \"Latin language and literature\", \"id\": \"00100101\"}")) {{
                setMarks(new ArrayList<>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"7.25\", \"tipo\": \"Scritto\", \"data\": \"Tue, 28 Mar 2021 00:00:00 +0000\", \"note\": \"Test 3: translation\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.5\", \"tipo\": \"Orale\", \"data\": \"Wed, 18 Feb 2021 00:00:00 +0000\", \"note\": \"Oral test on 3 people on lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"7\", \"tipo\": \"Scritto\", \"data\": \"Mon, 23 Dec 2020 00:00:00 +0000\", \"note\": \"Test 2: lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Fri, 6 Dec 2020 00:00:00 +0000\", \"note\": \"Oral test on 5 people on rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.25\", \"tipo\": \"Pratico\", \"data\": \"Sat, 9 Nov 2020 00:00:00 +0000\", \"note\": \"Test 1 (practical): rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                }});
                setTopics(new ArrayList<>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Neutral form of lupus, lupi, lupo\", \"assegnazioni\":\"Study page 17\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 14 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Plural form of lupus, lupi, lupo\", \"assegnazioni\":\"Translate the text on page 23\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 8 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Answer to the following question: why is the latin phrase \\\"Lorem ipsum dolor sit amet, consectetur adipisci elit [...]\\\" used in computer programming? \", \"assegnazioni\":\"\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 7 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Singular form of lupus, lupi, lupo\", \"assegnazioni\":\"Study pages 14-15\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 1 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Discussion about how to study\", \"assegnazioni\":\"\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 26 Feb 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Introduction of second term\", \"descrizione\":\"What does it mean to study latin? Why is it any useful?\", \"assegnazioni\":\"Study pages 11 to 13\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Italian language\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"9.5\", \"tipo\": \"Scritto\", \"data\": \"Wed, 29 Mar 2021 00:00:00 +0000\", \"note\": \"Vocabulary test about countries\", \"docente\": \"Rachel Jonas\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Mon, 13 Jan 2021 00:00:00 +0000\", \"note\": \"Speaking test\", \"docente\": \"Rachel Jonas\"}")));
                }});
                setTopics(new ArrayList<>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 27 Mar 2021 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"andare, passeggiare, camminare\", \"assegnazioni\":\"Study all of the words in table 7.2\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2021 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"tastiera, schermo\", \"assegnazioni\":\"Read poem on page 139\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Arithmetic and geometry\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"5.5\", \"tipo\": \"Orale\", \"data\": \"Sat, 25 Mar 2021 00:00:00 +0000\", \"note\": \"Trigonometric functions: sin, cos, tan\", \"docente\": \"Mario Rossi\"}")));
                }});
                setTopics(new ArrayList<>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 20 Mar 2021 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"Inverse of trigonometric functions: asin, acos, atan\", \"assegnazioni\":\"On page 302 do exercises 16-17-19-24\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Sat, 18 Mar 2021 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"sin, cos, tan\", \"assegnazioni\":\"Read and study pages 267-268\"}")));
                }});
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};

    public static final List<ClassData> CLASSES = new ArrayList<>() {{
        try {
            add(new ClassData(new JSONObject("{\"year\": \"2020-2021\", \"id\": \"01010101\"}")) {{
                setDocuments(new ArrayList<>() {{
                    add(new DocumentData(new JSONObject("{\"name\": \"Inverse trigonometric functions.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-03-23T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/Inverse%20trigonometric%20functions.pdf\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"Trigonometric functions.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-03-08T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/Trigonometric%20functions.pdf\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"i_promessi_sposi.epub\", \"owner_name\": \"Rachel\", \"owner_surname\": \"Jonas\", \"received\": \"2021-02-25T00:00:00+0000\", \"tags\": [\"Italian language\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/i_promessi_sposi.epub\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"vocabulary.odt\", \"owner_name\": \"Rachel\", \"owner_surname\": \"Jonas\", \"received\": \"2021-03-18T00:00:00+0000\", \"tags\": [\"Italian language\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/vocabulary.odt\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"rosae rosarum rosis.txt\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2020-10-12T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/rosae%20rosarum%20rosis.txt\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"lorem_ipsum_dolor_sit_amet.odp\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2021-03-21T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/lorem_ipsum_dolor_sit_amet.odp\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"lupus lupi lupo.txt\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2021-02-28T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/lupus%20lupi%20lupo.txt\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"calculus_introduction.mp4\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2020-09-28T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/calculus_introduction.mp4\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"calculus.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-10-18T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"https://raw.githubusercontent.com/Stypox/quadermas/master/meta/dummy_data/calculus.pdf\"}")));
                }});
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};

    private static String monthNow(int plusDays) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, plusDays);
        return new SimpleDateFormat("MMM", Locale.ITALY).format(c.getTime());
    }

    private static String dayNow(int plusDays) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, plusDays);
        return c.get(Calendar.DAY_OF_MONTH) + "";
    }

    public static final List<EventData> EVENTS = new ArrayList<>() {{
        try {
            add(new EventData(EventData.Type.annotation, "Latin test 4", "Translation from the emperor Julius Caesar", "John Smith", dayNow(10), monthNow(10), "07:45 08:30"));
            add(new EventData(EventData.Type.annotation, "Math test", "Inverse of trigonometric functions: asin, acos, atan", "Mario Rossi", dayNow(3), monthNow(3), "11:40 12:25"));
            add(new EventData(EventData.Type.annotation, "Small math review", "Trigonometry", "Mario Rossi", dayNow(1), monthNow(1), "07:00 07:45"));
            add(new EventData(EventData.Type.event, "International Olympiad in Informatics", "In Singapore", "Steven Halim", dayNow(0), monthNow(0), "x " + dayNow(-2) + " " + monthNow(-2) + " 06:15 y " + dayNow(3) + " " + monthNow(3) + " 20:45"));
            add(new EventData(EventData.Type.annotation, "Italian vocabulary test", "Countries", "Rachel Jonas", dayNow(-2), monthNow(-2), "09:30 10:15"));
            add(new EventData(EventData.Type.annotation, "Latin test 3", "Translation", "John Smith", dayNow(-10), monthNow(-10), "07:45 08:30"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};

    public static final Map<Integer, List<TimetableEventData>> TIMETABLE = new HashMap<>() {{
        try {
            put(DateUtils.dateToIndex(DateUtils.TODAY), new ArrayList<>() {{
                add(new TimetableEventData(new JSONObject("{\"materia\": \"Arithmetic and geometry\", \"professore\": \"Mario Rossi\", \"inizio\": \"Sat, 25 Mar 2021 07:00:00 +0000\", \"fine\": \"Sat, 25 Mar 2021 07:45:00 +0000\"}")));
                add(new TimetableEventData(new JSONObject("{\"materia\": \"Italian language\", \"professore\": \"Rachel Jonas\", \"inizio\": \"Sat, 25 Mar 2021 07:45:00 +0000\", \"fine\": \"Sat, 25 Mar 2021 08:30:00 +0000\"}")));
                add(new TimetableEventData(new JSONObject("{\"materia\": \"Italian language\", \"professore\": \"Rachel Jonas\", \"inizio\": \"Sat, 25 Mar 2021 08:30:00 +0000\", \"fine\": \"Sat, 25 Mar 2021 09:15:00 +0000\"}")));
                add(new TimetableEventData(new JSONObject("{\"materia\": \"Arithmetic and geometry\", \"professore\": \"Mario Rossi\", \"inizio\": \"Sat, 25 Mar 2021 09:55:00 +0000\", \"fine\": \"Sat, 25 Mar 2021 10:40:00 +0000\"}")));
                add(new TimetableEventData(new JSONObject("{\"materia\": \"Latin language and literature\", \"professore\": \"John Smith\", \"inizio\": \"Sat, 25 Mar 2021 10:40:00 +0000\", \"fine\": \"Sat, 25 Mar 2021 11:25:00 +0000\"}")));
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};
}
