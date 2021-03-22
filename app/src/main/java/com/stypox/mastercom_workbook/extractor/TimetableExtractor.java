package com.stypox.mastercom_workbook.extractor;

import com.stypox.mastercom_workbook.data.TimetableEventData;
import com.stypox.mastercom_workbook.extractor.Extractor.ItemErrorHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.stypox.mastercom_workbook.util.DateUtils.addDaysToDateIndex;
import static com.stypox.mastercom_workbook.util.DateUtils.dateIndexToSecondsSinceEpoch;

public class TimetableExtractor {
    public static final String timetableUrl = "https://{api_url}.registroelettronico.com/mastercom/register_manager.php?action=get_timetable&data_inizio={begin_epoch}&data_fine={end_epoch}";

    static Single<Map<Integer, List<TimetableEventData>>> fetchTimetableDays(
            final int beginDateIndex,
            final int endDateIndex,
            final ItemErrorHandler itemErrorHandler) {

        return Single.fromCallable(() -> {
            boolean jsonAlreadyParsed = false;
            try {
                final URL url = new URL(timetableUrl
                        .replace("{api_url}", Extractor.getAPIUrl())
                        .replace("{begin_epoch}", dateIndexToSecondsSinceEpoch(beginDateIndex))
                        .replace("{end_epoch}", dateIndexToSecondsSinceEpoch(endDateIndex)));

                final JSONObject jsonResponse = AuthenticationExtractor.fetchJsonAuthenticated(url);
                jsonAlreadyParsed = true;

                final Map<Integer, List<TimetableEventData>> timetableDays = new HashMap<>();
                for (int i = beginDateIndex; i < endDateIndex; i = addDaysToDateIndex(i, 1)) {
                    timetableDays.put(i, new ArrayList<>());
                }

                final JSONArray list = jsonResponse.getJSONArray("result");
                for (int i = 0; i < list.length(); i++) {
                    try {
                        final TimetableEventData event
                                = new TimetableEventData(list.getJSONObject(i));
                        final int eventDateIndex = event.getBeginDateAsIndex();

                        if (timetableDays.containsKey(eventDateIndex)) {
                            Objects.requireNonNull(timetableDays.get(eventDateIndex)).add(event);
                        } else {
                            throw new Exception();
                        }
                    } catch (final Throwable e) {
                        itemErrorHandler.onItemError(ExtractorError.asExtractorError(e, true));
                    }
                }

                return timetableDays;
            } catch (Throwable e) {
                throw ExtractorError.asExtractorError(e, jsonAlreadyParsed);
            }
        }).subscribeOn(Schedulers.io());
    }
}
