package com.dk.oganes.passport;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.max;

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

    private int[] controlDigitPositions = {43, 53, 63, 71, 86}; // 43 is fake, made for convenience

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

        // TODO validate
        //boolean isRight = validate(code);
        PersonalData personalData = extractPersonalData(code);
        return personalData;
    }

    private void logCodePerChar(String code) {
        int i = 0;
        for (char ch : code.toCharArray()) {
            Log.d(TAG, i + ": " + ch);
            i++;
        }
    }
    private String getPersonalDataFieldFromCode(String code, String field) {
        return code.substring(fieldStartPositions.get(field),
                fieldEndPositions.get(field));
    }

    private String processFieldValue(String field, String fieldValue) {
      switch (field) {
          case "passport":
              if (fieldValue.equals("P")) {
                  return "Passport";
              } else {
                  return "Unknown document" + fieldValue;
              }
          case "passportType":
              if(isFieldEmpty(fieldValue))
                  return "";
              else
                  return fieldValue;
          case "issuingCountry":
              String fullCountryName1 = getFullCountryName(fieldValue);
              if (fullCountryName1.equals("Unknown country")) {
                  return fullCountryName1 + ": " + fieldValue;
              } else {
                  return fullCountryName1;
              }
          case "name":
              fieldValue = fieldValue.replace("<", " ");
              return fieldValue;
          case "passportNumber":
                return deleteUnnesesarySymbols(fieldValue);
          case "nationality":
              String fullCountryName2 = getFullCountryName(fieldValue);
              if (fullCountryName2.equals("Unknown country")) {
                  return fullCountryName2 + ": " + fieldValue;
              } else {
                  return fullCountryName2;
              }
          case "dateOfBirth":
              return parseDate(fieldValue);
          case "sex":
              if (fieldValue.equals("M") || fieldValue.equals("H")) {
                  return "Male";
              } else if(fieldValue.equals("F") || fieldValue.equals("f")){
                  return "Female";
              } else {
                  return "Unknown sex identificator: " + fieldValue;
              }
          case "passportExpirationDate":
              return parseDate(fieldValue);
          case "personalNumber":
              if(isFieldEmpty(fieldValue))
                  return "";
              else
                  return deleteUnnesesarySymbols(fieldValue);
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
            fieldValue = processFieldValue(field, fieldValue);
            personalData.fillField(field, fieldValue);
        }
        return personalData;
    }

    private String findCode(String text) {
        // Finds code in recognised text
        // TODO improve quality of finding code position
        int startIndex = 0;
        startIndex = text.indexOf("P<", startIndex);
        //while(true)
        //    startIndex = text.indexOf("P", startIndex);
        //    char ch = text.charAt(startIndex + 1);
        //}
        int endIndex = Math.min(startIndex + codeLen + 1 + EXTRA_CELLS_FOR_WHITESPACES,
                text.length() - 1); // + 1 for \n

        Log.d(TAG, "start index: " + startIndex + "; end index: " + endIndex + "; length: " + text.length());
        if (endIndex <= text.length() && startIndex > -1) {
            return text.substring(startIndex, endIndex);
        }
        else {
            return null;
        }
    }

    private boolean validate(String code) {
        // Validate found code using check digits
        // TODO add checking final check digit
        boolean res = true;
        for( int i = 0; i < controlDigitPositions.length; i++) {
            int realDigit = (int)code.charAt(controlDigitPositions[i + 1]);
            String substr = code.substring(controlDigitPositions[i] + 1, controlDigitPositions[i + 1]);
            int calculatedDigit = calculateCheckDigit(substr);
            res &= (realDigit == calculatedDigit);
        }
        return res;
    }

    private int calculateCheckDigit(String str) {
        int checkDigit = 0;
        int[] weight = {7, 3, 1};

        for(int i = 0; i < str.length(); i++){
            checkDigit += (int)str.charAt(i) * weight[i % 3];
        }
        checkDigit %= 10;
        return checkDigit;
    }

    private String getFullCountryName(String countryCode) {
        String countryName = CountryNames.countryNames.get(countryCode);
        return (countryName == null) ? "Unknown country" : countryName;
    }

    private String parseDate(String date) {
        date = date.substring(4,6) + "." +
                date.substring(2, 4) + "." +
                date.substring(0, 2);
        return date;
    }

    private boolean isFieldEmpty(String fieldValue) {
        for (int i = 0; i < fieldValue.length(); ++i) {
            if (fieldValue.charAt(i) != '<')
                return false;
        }
        return true;
    }

    private String deleteUnnesesarySymbols(String fieldValue) {
        return fieldValue.replace('<', ' ');
    }
}
