package com.stypox.mastercom_workbook.extractor;

import android.os.Handler;
import android.os.Looper;

import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TopicData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

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

    private static String APIUrl;
    private static String user;
    private static String password;

    // extracted data
    private static List<SubjectData> subjects = new ArrayList<SubjectData>() {{
        try {
            add(new SubjectData(new JSONObject("{\"nome\": \"Latin language and literature\", \"id\": \"00100101\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"7.25\", \"tipo\": \"Scritto\", \"data\": \"Tue, 28 Mar 2020 00:00:00 +0000\", \"note\": \"Test 3: translation\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.5\", \"tipo\": \"Orale\", \"data\": \"Wed, 22 Mar 2020 00:00:00 +0000\", \"note\": \"Oral test on 3 people on lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"7\", \"tipo\": \"Scritto\", \"data\": \"Mon, 23 Dec 2019 00:00:00 +0000\", \"note\": \"Test 2: lupus, lupi, lupo\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Fri, 6 Dec 2019 00:00:00 +0000\", \"note\": \"Oral test on 5 people on rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.25\", \"tipo\": \"Pratico\", \"data\": \"Sat, 9 Nov 2019 00:00:00 +0000\", \"note\": \"Test 1 (practical): rosa, rosae, rosae\", \"docente\": \"John Smith\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2020 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Neutral form of lupus, lupi, lupo\", \"assegnazioni\":\"Study page 17\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 14 Mar 2020 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Plural form of lupus, lupi, lupo\", \"assegnazioni\":\"Translate the text on page 23\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 8 Mar 2020 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Answer to the following question: why is the latin phrase \\\"Lorem ipsum dolor sit amet, consectetur adipisci elit [...]\\\" used in computer programming? \", \"assegnazioni\":\"\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 7 Mar 2020 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Basics of latin\", \"descrizione\":\"Singular form of lupus, lupi, lupo\", \"assegnazioni\":\"Study pages 14-15\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Wed, 1 Mar 2020 00:00:00 +0000\", \"docente\":\"John Smith\", \"modulo\":\"Discussion\", \"descrizione\":\"Discussion about how to study\", \"assegnazioni\":\"\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Italian language\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"9.5\", \"tipo\": \"Scritto\", \"data\": \"Wed, 29 Mar 2020 00:00:00 +0000\", \"note\": \"Vocabulary test about countries\", \"docente\": \"Rachel Jonas\"}")));
                    add(new MarkData(new JSONObject("{\"valore\": \"8.75\", \"tipo\": \"Orale\", \"data\": \"Mon, 20 Mar 2020 00:00:00 +0000\", \"note\": \"Speaking test\", \"docente\": \"Rachel Jonas\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 27 Mar 2020 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"andare, passeggiare, camminare\", \"assegnazioni\":\"Study all of the words in table 7.2\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Tue, 21 Mar 2020 00:00:00 +0000\", \"docente\":\"Rachel Jonas\", \"modulo\":\"Vocabulary\", \"descrizione\":\"tastiera, schermo\", \"assegnazioni\":\"Read poem on page 139\"}")));
                }});
            }});
            add(new SubjectData(new JSONObject("{\"nome\": \"Arithmetic and geometry\", \"id\": \"00100102\"}")) {{
                setMarks(new ArrayList<MarkData>() {{
                    add(new MarkData(new JSONObject("{\"valore\": \"5.5\", \"tipo\": \"Orale\", \"data\": \"Sat, 25 Mar 2020 00:00:00 +0000\", \"note\": \"Trigonometric functions: sin, cos, tan\", \"docente\": \"Mario Rossi\"}")));
                }});
                setTopics(new ArrayList<TopicData>() {{
                    add(new TopicData(new JSONObject("{\"data\":\"Mon, 20 Mar 2020 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"Inverse of trigonometric functions: asin, acos, atan\", \"assegnazioni\":\"On page 302 do exercises 16-17-19-24\"}")));
                    add(new TopicData(new JSONObject("{\"data\":\"Sat, 18 Mar 2020 00:00:00 +0000\", \"docente\":\"Mario Rossi\", \"modulo\":\"Trigonometry\", \"descrizione\":\"sin, cos, tan\", \"assegnazioni\":\"Read and study pages 267-268\"}")));
                }});
            }});
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }};
    private static List<ClassData> classes;


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
     * To be called on logout, to clear all data and cached results
     */
    public static void removeAllData() {
        APIUrl = "";
        user = "";
        password = "";
        subjects = null;
        classes = null;
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


    ////////////
    // RANDOM //
    ////////////

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


    /////////////
    // HELPERS //
    /////////////

    private static void signalItemErrorOnMainThread(Throwable throwable, DataHandler dataHandler) {
        // json has surely been already parsed: the extractor was looping through it to get items
        ExtractorError error = ExtractorError.asExtractorError(throwable, true);
        runOnMainThread(() -> dataHandler.onItemError(error));
    }

    private static void runOnMainThread(Runnable runnable) {
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        mainThreadHandler.post(runnable);
    }
}
