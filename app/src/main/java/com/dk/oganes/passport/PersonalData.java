package com.dk.oganes.passport;

import java.util.HashMap;
import java.util.Map;

public class PersonalData {
    private String passport;
    private String passportType;
    private String issuingCountry;
    private String name;
    private String passportNumber;
    private String nationality;
    private String dateOfBirth;
    private String sex;
    private String passportExpirationDate;
    private String personalNumber;

    private class DataPosition {
        int start;
        int end;
        public DataPosition(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private static Map<String, Integer> dataStartPositions = new HashMap<>();
    private static Map<String, Integer> dataEndPositions = new HashMap<>();
    static {
            /*
        Passport code structure^:
       Ptiiinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
       #########CbbbYYMMDDCsyymmddCppppppppppppppCX
     */
        dataStartPositions.put("passport", 0);
        dataEndPositions.put("passport", 1);
        dataStartPositions.put("passportType", 1);
        dataEndPositions.put("passportType", 2);
        dataStartPositions.put("issuingCountry", 2);
        dataEndPositions.put("issuingCountry", 5);
        dataStartPositions.put("name", 5);
        dataEndPositions.put("name", 44);
        dataStartPositions.put("passportNumber", 44);
        dataEndPositions.put("passportNumber", 53);
        dataStartPositions.put("nationality", 54);
        dataEndPositions.put("nationality", 57);
        dataStartPositions.put("dateOfBirth", 57);
        dataEndPositions.put("dateOfBirth", 63);
        dataStartPositions.put("sex", 64);
        dataEndPositions.put("sex", 65);
        dataStartPositions.put("passportExpirationDate", 65);
        dataEndPositions.put("passportExpirationDate", 71);
        dataStartPositions.put("personalNumber", 72);
        dataEndPositions.put("personalNumber", 86);
    }

    PersonalData(String code) {
        passport = findPassport(code);
        passportType = findPassportType(code);
        issuingCountry = findIssuingCountry(code);
        name = findName(code);
        passportNumber = findPassportNumber(code);
        nationality = findNationality(code);
        dateOfBirth = findDateOfBirth(code);
        sex = findSex(code);
        passportExpirationDate = findPassportExpirationDate(code);
        personalNumber = findPersonalNumber(code);
    }

    private String findPassport(String code) {
        return code.substring(dataStartPositions.get("passport"),
                dataEndPositions.get("passport"));
    }

    private String findPassportType(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("passportType"),
                dataEndPositions.get("passportType"));
    }

    private String findIssuingCountry(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("issuingCountry"),
                dataEndPositions.get("issuingCountry"));
    }

    private String findName(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("name"),
                dataEndPositions.get("name"));
    }

    private String findPassportNumber(String code) {
        return code.substring(dataStartPositions.get("passportNumber"),
                dataEndPositions.get("passportNumber"));
    }

    private String findNationality(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("nationality"),
                dataEndPositions.get("nationality"));
    }

    private String findDateOfBirth(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("dateOfBirth"),
                dataEndPositions.get("dateOfBirth"));
    }

    private String findSex(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("sex"),
                dataEndPositions.get("sex"));
    }

    private String findPassportExpirationDate(String code) {
        // TODO add processing
        return code.substring(dataStartPositions.get("passportExpirationDate"),
                dataEndPositions.get("passportExpirationDate"));
    }

    private String findPersonalNumber(String code) {
        return code.substring(dataStartPositions.get("personalNumber"),
                dataEndPositions.get("personalNumber"));
    }
}
