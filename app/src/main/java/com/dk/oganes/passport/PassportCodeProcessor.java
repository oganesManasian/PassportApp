package com.dk.oganes.passport;

import java.util.HashMap;
import java.util.Map;

public class PassportCodeProcessor {
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
        fieldStartPositions.put("passportNumber", 44);
        fieldEndPositions.put("passportNumber", 53);
        fieldStartPositions.put("nationality", 54);
        fieldEndPositions.put("nationality", 57);
        fieldStartPositions.put("dateOfBirth", 57);
        fieldEndPositions.put("dateOfBirth", 63);
        fieldStartPositions.put("sex", 64);
        fieldEndPositions.put("sex", 65);
        fieldStartPositions.put("passportExpirationDate", 65);
        fieldEndPositions.put("passportExpirationDate", 71);
        fieldStartPositions.put("personalNumber", 72);
        fieldEndPositions.put("personalNumber", 86);
    }

    private int[] controlDigitPositions = {43, 53, 63, 71, 86}; // 43 is fake, made for convenience

    private static final int codeLen = 88;
    public static final String CHARS_TO_DETECT = "0123456789<" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ACBDEFGHIJKLMNOPQRSTUVWXYZ";

    public PersonalData parseCode(String text) {
        String code = findCode(text);
        if (code == null) {
            return null;
        }
        // TODO validate
        //boolean isRight = validate(code);
        PersonalData personalData = extractPersonalData(code);
        return personalData;
    }

    private String getPersonalDataFieldFromCode(String code, String fieldName) {
        return code.substring(fieldStartPositions.get(fieldName),
                fieldEndPositions.get(fieldName));
    }

    private PersonalData extractPersonalData(String code) {
        // Extract personal data from code
        PersonalData personalData = new PersonalData();
        for (String fieldName : PersonalData.fieldNames) {
            personalData.fillField(fieldName, getPersonalDataFieldFromCode(code, fieldName));
        }
        return personalData;
    }

    private String findCode(String text) {
        // Finds code in recognised text
        // TODO improve quality of finding code position
        //String code = "";
        int startIndex = 0;
        startIndex = text.indexOf("P<", startIndex);
        //while(true)
        //    startIndex = text.indexOf("P", startIndex);
        //    char ch = text.charAt(startIndex + 1);
        //}
        int endIndex = startIndex + codeLen + 1; // + 1 for \n
        //return "sgdsfP<qwertyuiosdfghjkswdfghjksdfghjklsdfghjklsdfghjk\n" +
        //        "dfghjkl;dfghjkl;dfghjkldfghjkl;dfghjkl;<<<<<<<<<<<<<<<<<";

        if (endIndex < text.length() && startIndex > -1) {
            // TODO delete
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
}
