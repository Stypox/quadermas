package com.stypox.mastercom_workbook.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SchoolData {
    private final String APIUrl;
    private final String name;
    private final String municipality;
    private final String province;

    public SchoolData(final JSONObject json) throws JSONException {
        this.APIUrl = json.getString("mastercom_id");
        this.name = trimAndCapitalizeFully(json.getString("nome"));
        this.municipality = trimAndCapitalizeFully(json.getString("comune"));
        this.province = trimAndCapitalizeFully(json.getString("provincia"));
    }

    public boolean isValid() {
        return APIUrl != null && !APIUrl.trim().isEmpty() && !APIUrl.trim().equals("null");
    }

    public String getAPIUrl() {
        return APIUrl;
    }

    public String getName() {
        return name;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getProvince() {
        return province;
    }


    // taken from https://stackoverflow.com/a/27956930/9481500
    private static String trimAndCapitalizeFully(final String s) {
        final StringBuilder result = new StringBuilder();
        boolean capitalizeNextLetter = true;

        for (final char c : s.trim().toCharArray()) {
            final boolean isLetter = Character.isLetter(c);
            if (capitalizeNextLetter && isLetter) {
                result.append(Character.toUpperCase(c));
                capitalizeNextLetter = false;
            } else {
                result.append(Character.toLowerCase(c));
            }

            if (!isLetter) {
                capitalizeNextLetter = true;
            }
        }

        return result.toString();
    }
}
