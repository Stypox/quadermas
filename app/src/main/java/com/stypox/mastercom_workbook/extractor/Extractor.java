package com.stypox.mastercom_workbook.extractor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.DocumentData;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TimetableEventData;
import com.stypox.mastercom_workbook.data.TopicData;
import com.stypox.mastercom_workbook.util.DateUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.stypox.mastercom_workbook.util.DateUtils.addDaysToDateIndex;
import static com.stypox.mastercom_workbook.util.DateUtils.dateToIndex;

public class Extractor {

    public interface DataHandler<D> {
        void onExtractedData(D data);
        void onItemError(ExtractorError error);
        void onError(ExtractorError error);
    }

    public interface ItemErrorHandler {
        void onItemError(Throwable throwable);
    }

    private static final String APIUrlToShow = "{api_url}.registroelettronico.com";

    private static String APIUrl = "mit";
    private static String user = "";
    private static String password = "";

    // extracted data
    private static List<SubjectData> subjects = new ArrayList<SubjectData>() {{
        try {
            add(new SubjectData(new JSONObject("{\"nome\": \"Latin language and literature\", \"id\": \"00100101\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"7.25\", \"tipo\": \"Scritto\", \"data\": \"Tue, 28 Mar 2021 00:00:00 +0000\", \"note\": \"Test 3: translation\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.5\", \"tipo\": \"Orale\", \"data\": \"Wed, 18 Feb 2021 00:00:00 +0000\", \"note\": \"Oral test on 3 people on lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"7\", \"tipo\": \"Scritto\", \"data\": \"Mon, 23 Dec 2020 00:00:00 +0000\", \"note\": \"Test 2: lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Fri, 6 Dec 2020 00:00:00 +0000\", \"note\": \"Oral test on 5 people on rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.25\", \"tipo\": \"Pratico\", \"data\": \"Sat, 9 Nov 2020 00:00:00 +0000\", \"note\": \"Test 1 (practical): rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Neutral form of lupus, lupi, lupo\", \"assegnazioni\":\"Study page 17\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 14 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Plural form of lupus, lupi, lupo\", \"assegnazioni\":\"Translate the text on page 23\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 8 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Answer to the following question: why is the latin phrase \\\"Lorem ipsum dolor sit amet, consectetur adipisci elit [...]\\\" used in computer programming? \", \"assegnazioni\":\"\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 7 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Singular form of lupus, lupi, lupo\", \"assegnazioni\":\"Study pages 14-15\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 1 Mar 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Discussion about how to study\", \"assegnazioni\":\"\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 26 Feb 2021 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Introduction of second term\", \"descrizione\":\"What does it mean to study latin? Why is it any useful?\", \"assegnazioni\":\"Study pages 11 to 13\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Italian language\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"9.5\", \"tipo\": \"Scritto\", \"data\": \"Wed, 29 Mar 2021 00:00:00 +0000\", \"note\": \"Vocabulary test about countries\", \"docente\": \"Rachel Jonas\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Mon, 13 Jan 2021 00:00:00 +0000\", \"note\": \"Speaking test\", \"docente\": \"Rachel Jonas\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 27 Mar 2021 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"andare, passeggiare, camminare\", \"assegnazioni\":\"Study all of the words in table 7.2\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2021 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"tastiera, schermo\", \"assegnazioni\":\"Read poem on page 139\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Arithmetic and geometry\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"5.5\", \"tipo\": \"Orale\", \"data\": \"Sat, 25 Mar 2021 00:00:00 +0000\", \"note\": \"Trigonometric functions: sin, cos, tan\", \"docente\": \"Mario Rossi\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 20 Mar 2021 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"Inverse of trigonometric functions: asin, acos, atan\", \"assegnazioni\":\"On page 302 do exercises 16-17-19-24\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Sat, 18 Mar 2021 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"sin, cos, tan\", \"assegnazioni\":\"Read and study pages 267-268\"}")));
                }});
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};
    private static List<ClassData> classes = new ArrayList<ClassData>() {{
        try {
            add(new ClassData(new JSONObject("{\"year\": \"2020-2021\", \"id\": \"01010101\"}")) {{
                setDocuments(new ArrayList<DocumentData>() {{
                    add(new DocumentData(new JSONObject("{\"name\": \"Inverse trigonometric functions.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-03-23T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"Trigonometric functions.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-03-08T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"i_promessi_sposi.epub\", \"owner_name\": \"Rachel\", \"owner_surname\": \"Jonas\", \"received\": \"2021-02-25T00:00:00+0000\", \"tags\": [\"Italian language\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"vocabulary.odt\", \"owner_name\": \"Rachel\", \"owner_surname\": \"Jonas\", \"received\": \"2021-03-18T00:00:00+0000\", \"tags\": [\"Italian language\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"rosae rosarum rosis.txt\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2020-10-12T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"lorem_ipsum_dolor_sit_amet.odp\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2021-03-21T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"lupus lupi lupo.txt\", \"owner_name\": \"John\", \"owner_surname\": \"Smith\", \"received\": \"2021-02-28T00:00:00+0000\", \"tags\": [\"Latin language and literature\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"calculus_introduction.mp4\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2020-09-28T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"\"}")));
                    add(new DocumentData(new JSONObject("{\"name\": \"calculus.pdf\", \"owner_name\": \"Mario\", \"owner_surname\": \"Rossi\", \"received\": \"2021-10-18T00:00:00+0000\", \"tags\": [\"Arithmetic and geometry\"], \"id\": \"\"}")));
                }});
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};
    private static List<EventData> events = new ArrayList<EventData>() {{
        try {
            add(new EventData(EventData.Type.annotation, "Latin test 4", "Translation from the emperor Julius Caesar", "John Smith", "23", "apr", "07:45 08:30"));
            add(new EventData(EventData.Type.annotation, "Math test", "Inverse of trigonometric functions: asin, acos, atan", "Mario Rossi", "15", "apr", "11:40 12:25"));
            add(new EventData(EventData.Type.annotation, "Small math review", "Trigonometry", "Mario Rossi", "3", "apr", "07:00 07:45"));
            add(new EventData(EventData.Type.event, "International Olympiad in Informatics", "In Singapore", "Steven Halim", "3", "apr", "x 1 apr 06:15 y 7 apr 20:45"));
            add(new EventData(EventData.Type.annotation, "Italian vocabulary test", "Countries", "Rachel Jonas", "29", "mar", "09:30 10:15"));
            add(new EventData(EventData.Type.annotation, "Latin test 3", "Translation", "John Smith", "28", "mar", "07:45 08:30"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }};
    @NonNull
    private static final Map<Integer, List<TimetableEventData>> timetable = new HashMap<Integer, List<TimetableEventData>>() {{
        try {
            put(DateUtils.dateToIndex(DateUtils.TODAY), new ArrayList<TimetableEventData>() {{
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

    /////////////
    // GETTERS //
    /////////////

    /**
     * Extracts the subject list and feeds it to the handler
     * @param forceReload if {@code true}, do not use cached results
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractSubjects(boolean forceReload,
                                       CompositeDisposable disposables,
                                       DataHandler<List<SubjectData>> handler) {
        if (forceReload || subjects == null) {
            disposables.add(SubjectExtractor
                    .fetchSubjects(t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                                subjects = data;
                                handler.onExtractedData(data);
                            },
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(subjects);
        }
    }

    /**
     * Extracts the marks of the specified subject and feeds the subject to the handler when done
     * @param subject the subject of which to extract marks, and on which to save them
     * @param forceReload if {@code true}, do not use the results cached inside {@code subject}
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractMarks(SubjectData subject,
                                    boolean forceReload,
                                    CompositeDisposable disposables,
                                    DataHandler<SubjectData> handler) {
        if (forceReload || subject.getMarks() == null) {
            disposables.add(SubjectExtractor
                    .fetchMarks(subject, t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handler::onExtractedData,
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(subject);
        }
    }

    /**
     * Extracts the topics of the specified subject and feeds the subject to the handler when done
     * @param subject the subject of which to extract topics, and on which to save them
     * @param forceReload if {@code true}, do not use the results cached inside {@code subject}
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractTopics(SubjectData subject,
                                     boolean forceReload,
                                     CompositeDisposable disposables,
                                     DataHandler<SubjectData> handler) {
        if (forceReload || subject.getTopics() == null) {
            disposables.add(SubjectExtractor
                    .fetchTopics(subject, t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handler::onExtractedData,
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(subject);
        }
    }

    /**
     * Extracts the class list needed to extract documents and feeds it to the handler
     * @param forceReload if {@code true}, do not use cached results
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractClasses(boolean forceReload,
                                      CompositeDisposable disposables,
                                      DataHandler<List<ClassData>> handler) {
        if (forceReload || classes == null) {
            disposables.add(DocumentExtractor
                    .fetchClasses(t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                                classes = data;
                                handler.onExtractedData(classes);
                            },
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(classes);
        }
    }

    /**
     * Extracts the documents for the specified class and feeds the class to the handler when done
     * @param classData the class of which to extract documents, and on which to save them
     * @param forceReload if {@code true}, do not use the results cached inside {@code classData}
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractDocuments(ClassData classData,
                                        boolean forceReload,
                                        CompositeDisposable disposables,
                                        DataHandler<ClassData> handler) {
        if (forceReload || classData.getDocuments() == null) {
            disposables.add(DocumentExtractor
                    .fetchDocuments(classData, t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handler::onExtractedData,
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(classData);
        }
    }

    /**
     * Extracts the event list and feeds it to the handler
     * @param forceReload if {@code true}, do not use cached results
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractEvents(boolean forceReload,
                                     CompositeDisposable disposables,
                                     DataHandler<List<EventData>> handler) {
        if (forceReload || events == null) {
            disposables.add(EventExtractor
                    .fetchEvents(t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                                events = data;
                                handler.onExtractedData(data);
                            },
                            throwable -> handler.onError((ExtractorError) throwable)));
        } else {
            handler.onExtractedData(events);
        }
    }

    /**
     * Extracts the timetable for the specified date and feeds the list of events to the handler
     * when done. In order to optimize the loading strategy, some days before and after are also
     * loaded and cached for later.
     * @param date the date for which to extract the timetable
     * @param disposables ReactiveX disposables are added here if needed
     * @param handler where to notify about extracted data and errors, on the main thread
     */
    public static void extractTimetable(final Date date,
                                        final CompositeDisposable disposables,
                                        final DataHandler<List<TimetableEventData>> handler) {

        final int dateIndex = dateToIndex(date);
        if (timetable.containsKey(dateIndex)) {
            handler.onExtractedData(timetable.get(dateIndex));
        } else {
            int i = 0;
            while (i > -3 && !timetable.containsKey(addDaysToDateIndex(dateIndex, i - 1))) {
                --i; // at most three days before
            }
            final int beginDateIndex = addDaysToDateIndex(dateIndex, i);

            int j = 1; // at least the requested day (since end date is exclusive)
            while (j < 14 + i && !timetable.containsKey(addDaysToDateIndex(dateIndex, j))) {
                ++j; // at most 14 days at a time (usually 11 days in advance)
            }
            final int endDateIndex = addDaysToDateIndex(dateIndex, j); // end date is exclusive

            disposables.add(TimetableExtractor
                    .fetchTimetableDays(beginDateIndex, endDateIndex,
                            t -> signalItemErrorOnMainThread(t, handler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                                for (final Map.Entry<Integer, List<TimetableEventData>> entry
                                        : data.entrySet()) {
                                    timetable.put(entry.getKey(), entry.getValue());
                                }
                                handler.onExtractedData(timetable.get(dateIndex));
                            },
                            throwable -> handler.onError((ExtractorError) throwable)));
        }
    }

    /**
     * To be called on logout, to clear all data and cached results
     */
    public static void removeAllData() {
        APIUrl = "";
        user = "";
        password = "";
        subjects = null;
        classes = null;
        timetable.clear();
    }


    ////////////////////
    // AUTHENTICATION //
    ////////////////////

    public static void setAPIUrl(String APIUrl) {
        Extractor.APIUrl = APIUrl;
    }

    public static void setUser(String user) {
        Extractor.user = user;
    }

    public static void setPassword(String password) {
        Extractor.password = password;
    }


    static String getAPIUrl() {
        return APIUrl;
    }

    static String getUser() {
        return user;
    }

    static String getPassword() {
        return password;
    }


    ///////////////////
    // MISCELLANEOUS //
    ///////////////////

    /**
     * @return a user-friendly representation of the API url
     */
    public static String getFullAPIUrlToShow() {
        return APIUrlToShow.replace("{api_url}", APIUrl);
    }

    /**
     * To be used ONLY to obtain subjects in activities where it is obvious that they have already
     * been extracted.
     * @return the already extracted SubjectData
     */
    public static List<SubjectData> getExtractedSubjects() {
        return subjects;
    }

    public static boolean areSomeMarksExtracted() {
        if (subjects == null) {
            return false;
        }
        for (final SubjectData subject : subjects) {
            if (subject.getMarks() != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean areAllMarksExtracted() {
        if (subjects == null) {
            return false;
        }
        for (final SubjectData subject : subjects) {
            if (subject.getMarks() == null) {
                return false;
            }
        }
        return true;
    }

    /////////////
    // HELPERS //
    /////////////

    private static void signalItemErrorOnMainThread(final Throwable throwable,
                                                    final DataHandler<?> dataHandler) {
        // json has surely been already parsed: the extractor was looping through it to get items
        ExtractorError error = ExtractorError.asExtractorError(throwable, true);
        runOnMainThread(() -> dataHandler.onItemError(error));
    }

    private static void runOnMainThread(final Runnable runnable) {
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        mainThreadHandler.post(runnable);
    }
}
