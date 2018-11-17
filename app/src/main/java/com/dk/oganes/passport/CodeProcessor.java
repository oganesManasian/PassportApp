package com.dk.oganes.passport;

public class CodeProcessor {
    /*
    Passport code structure^:
       Ptiiinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
       #########CbbbYYMMDDCsyymmddCppppppppppppppCX
     */

    private int[] controlDigitPositions = {43, 53, 63, 71, 86}; // 43 is fake, made for convenience

    private static final int codeLen = 88;
    public static final String CHARS_TO_DETECT = "0123456789<" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ACBDEFGHIJKLMNOPQRSTUVWXYZ";


    public PersonalData extractPersonalData(String text) {
        // Extract personal data in recognised text
        PersonalData personalData = null;

        String code = findCode(text);
        boolean isRight = validate(code);
        if (isRight) {
            // TODO Add extraction of name if passport type is recognised wrong (using check digits to know
            // which field is recognised right
            personalData = new PersonalData(code);
        }
        return personalData;
    }

    private String findCode(String text) {
        // Finds code in recognised text
        String code = "";
        // TODO implement
        return code;
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
