package com.dk.oganes.passport;

import java.util.HashMap;
import java.util.Map;

public class PersonalData {
    private Map<String, String> fieldValues = new HashMap<>();
    public static String[] fieldNames = {
            "passport",
            "passportType",
            "issuingCountry",
            "name",
            "passportNumber",
            "nationality",
            "dateOfBirth",
            "sex",
            "passportExpirationDate",
            "personalNumber",
    };


    PersonalData() {
        for (String fieldName: fieldNames) {
            fieldValues.put(fieldName, "Not recognised");
        }
    }

    public String getField(String fieldName) {
        return fieldValues.get(fieldName);
    }

    public void fillField(String fieldName, String fieldValue) {
        fieldValues.put(fieldName, fieldValue);
    }

}
