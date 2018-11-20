package com.dk.oganes.passport;

import java.util.HashMap;
import java.util.Map;

public class PersonalData {
    private Map<String, String> fieldValues = new HashMap<>();
    public static String[] fieldNames = {
            "Passport",
            "Passport type",
            "Issuing country",
            "Name",
            "Passport number",
            "Nationality",
            "Date of birth",
            "Sex",
            "Passport expiration date",
            "Personal Number",
    };
    public static String[] fields = {
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
        for (String field: fields) {
            fieldValues.put(field, "Not recognised");
        }
    }

    public String getField(String field) {
        return fieldValues.get(field);
    }

    public void fillField(String field, String fieldValue) {
        fieldValues.put(field, fieldValue);
    }

}
