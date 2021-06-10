package com.stypox.mastercom_workbook.extractor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.stypox.mastercom_workbook.data.ClassData;
import com.stypox.mastercom_workbook.data.EventData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.data.TimetableEventData;

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

    private static String APIUrl;
    private static String user;
    private static String password;

    // extracted data
    private static List<SubjectData> subjects;
    private static List<ClassData> classes;
    private static List<EventData> events;
    @NonNull
    private static final Map<Integer, List<TimetableEventData>> timetable = new HashMap<>();

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
