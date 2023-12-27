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
        this.name = json.getString("nome");
        this.municipality = json.getString("comune");
        this.province = json.getString("provincia");
    }

    public SchoolData(final String APIUrl,
                      final String name,
                      final String municipality,
                      final String province) {
        this.APIUrl = APIUrl;
        this.name = name;
        this.municipality = municipality;
        this.province = province;
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
}
