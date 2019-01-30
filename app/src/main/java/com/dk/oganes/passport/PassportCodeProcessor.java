package com.dk.oganes.passport;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassportCodeProcessor {
    private static final String TAG = "PassportCodeProcessor";
    private static final int EXTRA_CELLS_FOR_WHITESPACES = 10;
    /*
        Passport code structure^:
           Ptiiinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
           #########CbbbYYMMDDCsyymmddCppppppppppppppCX
    */
    private static Map<String, Integer> fieldStartPositions = new HashMap<>();
    private static Map<String, Integer> fieldEndPositions = new HashMap<>();

    static {
        fieldStartPositions.put("passport", 0);
        fieldEndPositions.put("passport", 1);
        fieldStartPositions.put("passportType", 1);
        fieldEndPositions.put("passportType", 2);
        fieldStartPositions.put("issuingCountry", 2);
        fieldEndPositions.put("issuingCountry", 5);
        fieldStartPositions.put("name", 5);
        fieldEndPositions.put("name", 44);
        fieldStartPositions.put("passportNumber", 45); // +1 for /n
        fieldEndPositions.put("passportNumber", 54); // +1 for /n
        fieldStartPositions.put("nationality", 55); // +1 for /n
        fieldEndPositions.put("nationality", 58); // +1 for /n
        fieldStartPositions.put("dateOfBirth", 58); // +1 for /n
        fieldEndPositions.put("dateOfBirth", 64); // +1 for /n
        fieldStartPositions.put("sex", 65); // +1 for /n
        fieldEndPositions.put("sex", 66); // +1 for /n
        fieldStartPositions.put("passportExpirationDate", 66); // +1 for /n
        fieldEndPositions.put("passportExpirationDate", 72); // +1 for /n
        fieldStartPositions.put("personalNumber", 73); // +1 for /n
        fieldEndPositions.put("personalNumber", 87); // +1 for /n
    }

    //private int[] controlDigitPositions = {43, 53, 63, 71, 86}; // 43 is fake, made for convenience
    private List<String> checkDigitResponsibilities = Arrays.asList("passportNumber", "dateOfBirth",
            "passportExpirationDate", "personalNumber");
    private static final int codeLen = 88;
    public static final String CHARS_TO_DETECT = "0123456789<" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ACBDEFGHIJKLMNOPQRSTUVWXYZ";

    public PersonalData parseCode(String text) {
        String code = findCode(text);
        if (code == null) {
            Log.d(TAG, "code is null");
            return new PersonalData();
        }
        //logCodePerChar(code);
        Log.d(TAG, code);

        return extractPersonalData(code);
    }

    private void logCodePerChar(String code) {
        int i = 0;
        for (char ch : code.toCharArray()) {
            Log.d(TAG, i + ": " + ch);
            i++;
        }
    }

    private String getPersonalDataFieldFromCode(String code, String field) {
        if (code.length() < fieldEndPositions.get(field))
            return "";
        return code.substring(fieldStartPositions.get(field),
                fieldEndPositions.get(field));
    }

    private String processFieldValue(String code, String field, String fieldValue) {
        fieldValue = fieldValue.toUpperCase();
        String errInfo;
        switch (field) {
            case "passport":
                if (fieldValue.equals("P")) {
                    return "Passport";
                } else {
                    return "Unknown document" + fieldValue;
                }
            case "passportType":
                if (isFieldEmpty(fieldValue))
                    return "";
                else
                    return fieldValue;
            case "issuingCountry":
                fieldValue = toLetters(fieldValue);
                String fullCountryName1 = getFullCountryName(fieldValue);
                if (fullCountryName1.equals("Unknown country")) {
                    return fullCountryName1 + ": " + fieldValue;
                } else {
                    return fullCountryName1;
                }
            case "name":
                fieldValue = toLetters(fieldValue);
                fieldValue = fieldValue.replace("<", " ");
                return fieldValue;
            case "passportNumber":
                fieldValue = toNumbers(fieldValue);
                errInfo = validate(code, field, fieldValue);
                if (errInfo.equals(""))
                    return deleteUnnecessarySymbols(fieldValue);
                else
                    return fieldValue + errInfo;
            case "nationality":
                fieldValue = toLetters(fieldValue);
                String fullCountryName2 = getFullCountryName(fieldValue);
                if (fullCountryName2.equals("Unknown country")) {
                    return fullCountryName2 + ": " + fieldValue;
                } else {
                    return fullCountryName2;
                }
            case "dateOfBirth":
                fieldValue = toNumbers(fieldValue);
                errInfo = validate(code, field, fieldValue);
                if (errInfo.equals(""))
                    return parseDate(fieldValue);
                else
                    return fieldValue + errInfo;
            case "sex":
                fieldValue = toLetters(fieldValue);
                switch (fieldValue) {
                    case "M":
                    case "H":
                    case "m":
                        return "Male";
                    case "F":
                    case "f":
                        return "Female";
                    default:
                        return "Unknown sex identificator: " + fieldValue;
                }
            case "passportExpirationDate":
                fieldValue = toNumbers(fieldValue);
                errInfo = validate(code, field, fieldValue);
                if (errInfo.equals(""))
                    return parseDate(fieldValue);
                else
                    return fieldValue + errInfo;
            case "personalNumber":
                fieldValue = toNumbers(fieldValue);
                errInfo = validate(code, field, fieldValue);
                if (errInfo.equals(""))
                    return deleteUnnecessarySymbols(fieldValue);
                else
                    return fieldValue + errInfo;
            default:
                return "ERROR not handled field name";
        }
    }

    private PersonalData extractPersonalData(String code) {
        // Delete whitespaces from codee
        code = code.replaceAll(" ", "");
        Log.d(TAG, "Code without whitespaces: \n" + code);
        // Extract personal data from code
        PersonalData personalData = new PersonalData();
        for (String field : PersonalData.fields) {
            String fieldValue = getPersonalDataFieldFromCode(code, field);
            fieldValue = processFieldValue(code, field, fieldValue);
            //String errInfo = validate(code, field, fieldValue);
            personalData.fillField(field, fieldValue);
        }
        return personalData;
    }


    private String findCode(String text) {
        // Finds code in recognised text
        // TODO improve quality of finding code position
        int startIndex = text.indexOf("P<");
        if (startIndex == -1)
            startIndex = text.indexOf("p<");
        //while(true)
        //    startIndex = text.indexOf("P", startIndex);
        //    char ch = text.charAt(startIndex + 1);
        //}
        int endIndex = Math.min(startIndex + codeLen + 1 + EXTRA_CELLS_FOR_WHITESPACES,
                text.length() - 1); // + 1 for \n

        Log.d(TAG, "start index: " + startIndex + "; end index: " + endIndex + "; length: " + text.length());
        if (endIndex <= text.length() && startIndex > -1) {
            return text.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }

    private String validate(String code, String field, String fieldValue) {
        if (!checkDigitResponsibilities.contains(field))
            return "";

        int checkDigitPos = fieldEndPositions.get(field);
        int checkDigit = Character.getNumericValue(code.charAt(checkDigitPos));
        int calculatedDigit = calculateCheckDigit(fieldValue);
        if (calculatedDigit == checkDigit)
            return "";
        else {
            Log.d(TAG, "Check digit: " + checkDigit + " Calculated digit: " + calculatedDigit);
            return " - Check digit does not match";
        }
    }

    private int calculateCheckDigit(String str) {
        int checkDigit = 0;
        int[] weight = {7, 3, 1};
        int codePoint, value;
        for (int i = 0; i < str.length(); i++) {
            codePoint = str.charAt(i);
            if (Character.isDigit(codePoint)) {
                value = Character.getNumericValue(codePoint);
            } else if (codePoint == (int)'<') {
                value = 0;
            } else {
                value = (codePoint - (int) 'A' + 10);
            }
            checkDigit += value * weight[i % 3];
        }
        checkDigit %= 10;
        return checkDigit;
    }

    private String getFullCountryName(String countryCode) {
        countryCode = recoverCountryCode(countryCode);
        String countryName = CountryNames.countryNames.get(countryCode);
        return (countryName == null) ? "Unknown country" : countryName;
    }

    private String parseDate(String date) {
        String day = date.substring(4, 6);
        String month = date.substring(2, 4);
        String year = date.substring(0, 2);

        String errInfo;
        if (Integer.parseInt(day) > 31 ||
                Integer.parseInt(month) > 12) {
            errInfo = " - Incorrect date";
        } else {
            errInfo = "";
        }

        date = day + "." + month + "." + year + errInfo;
        return date;
    }

    private boolean isFieldEmpty(String fieldValue) {
        for (int i = 0; i < fieldValue.length(); ++i) {
            if (fieldValue.charAt(i) != '<')
                return false;
        }
        return true;
    }

    private String deleteUnnecessarySymbols(String fieldValue) {
        return fieldValue.replace('<', ' ');
    }

    private String recoverCountryCode(String countryCode) {
        if (countryCode.equals("TNN"))
            return "TWN";
        return countryCode;
    }

    private String toNumbers(String fieldValue) { // Found similarities between letters and numbers
        fieldValue = fieldValue.replaceAll("D", "0");
        fieldValue = fieldValue.replaceAll("O", "0");
        fieldValue = fieldValue.replaceAll("U", "0");
        fieldValue = fieldValue.replaceAll("G", "0");
        fieldValue = fieldValue.replaceAll("I", "1");
        fieldValue = fieldValue.replaceAll("Z", "2");
        fieldValue = fieldValue.replaceAll("S", "5");
        fieldValue = fieldValue.replaceAll("F", "6");
        fieldValue = fieldValue.replaceAll("B", "8");
        return fieldValue;
    }

    private String toLetters(String fieldValue) { // Found similarities between letters and numbers
        fieldValue = fieldValue.replaceAll("0", "O");
        fieldValue = fieldValue.replaceAll("1", "I");
        fieldValue = fieldValue.replaceAll("2", "Z");
        fieldValue = fieldValue.replaceAll("3", "B");
        fieldValue = fieldValue.replaceAll("5", "S");
        fieldValue = fieldValue.replaceAll("6", "F");
        fieldValue = fieldValue.replaceAll("8", "S");
        return fieldValue;
    }
}
