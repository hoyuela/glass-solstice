package com.discover.mobile.card.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InputValidator is used to store validation methods for checking if certain
 * user inputs meet certain criteria for validity. It works by having a large
 * amount of local boolean variables that store the state of past validation
 * checks. These local variables are set by running methods associated with an
 * InputValidator .
 * 
 * In example, with a new InputValidator you can the emailValid(String email) to
 * check an email address against the local regular expression for validity. The
 * emailValid method will return true or false based on the input, then you can
 * also later check what the result was by accessing the wasEmailValid member
 * variable.
 * 
 * @author Darshana
 * 
 */
public final class InputValidator {
    private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Matcher matcher;
    private static Pattern pattern;

    private final static int CARD_NUMBER_LENGTH_OK = 16;
    private final static String CARD_NUMBER_PREFIX = "6011";

    private final static int YEAR_MIN_VALUE = 1890;
    private final static int YEAR_MAX_VALUE = 2100;

    private final static int DAY_MAX_VALUE = 31;
    private final static int DAY_MIN_VALUE = 1;

    private final static int MONTH_MAX_VALUE = 11;
    private final static int MONTH_MIN_VALUE = 0;

    private InputValidator() {
        // do nothing: static utility class.
    }

    /**
     * Compares a String to a regular expression to determine if that String
     * could be a valid email address.
     * 
     * @author Darshana
     * @param email
     *            A String representation of an email address.
     * @return true if the email address could be a valid email address.
     */
    public static boolean validateEmail(final String email) {
        if (email == null) {
            return false;
        }

        if (pattern == null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
        }

        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    /**
     * Checks the validity of an account number.
     * 
     * @author Darshana
     * @param cardAccountNumber
     *            A String representation of an account number
     * @return returns true if the profvided String is a valid account number.
     *         That is, 16 digits in length, starts with the correct prefix and
     *         does not contain any spaces.
     */
    public static boolean validateCardAccountNumber(
            final String cardAccountNumber) {
        return cardAccountNumber.startsWith(CARD_NUMBER_PREFIX)
                && cardAccountNumber.length() == CARD_NUMBER_LENGTH_OK
                && !cardAccountNumber.contains(" ");
    }

    /**
     * Checks to see if a given password is valid.
     * 
     * @author Darshana
     * @param inputSequence
     *            a password
     * @return returns true if the password is valid.
     */
    public static boolean validatePassword(final String inputSequence) {
        boolean isPassValid = false;
        boolean hasGoodLength = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasNumber = false;

        // Check length of input.
        if (inputSequence.length() >= 8 && inputSequence.length() <= 32) {
            hasGoodLength = true;
        }

        // A password must have at least 1 letter and 1 number and cannot be
        // 'password'
        // but password doesn't have a number...
        for (int i = 0; i < inputSequence.length(); ++i) {

            if (Character.isLowerCase(inputSequence.charAt(i))) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(inputSequence.charAt(i))) {
                hasUpperCase = true;
            } else if (Character.isDigit(inputSequence.charAt(i))) {
                hasNumber = true;
            }
        }

        isPassValid = (hasUpperCase || hasLowerCase) && hasGoodLength
                && hasNumber;

        return isPassValid;
    }

    /**
     * Checks to see if a given user id is valid.
     * 
     * @author Darshana
     * @param uid
     *            A String representation of a user id.
     * @return returns true if a given user Id is valid.
     */
    public static boolean validateUserId(final String uid) {
        boolean isUserIdValid = false;
        if (!uid.equals("Credit Card User ID") && uid.length() >= 6
                && uid.length() <= 16 && !uid.contains(" ")
                && !uid.contains("`") && !uid.contains("'")
                && !uid.contains("\"") && !uid.contains("\\")
                && !uid.startsWith("6011")) {
            isUserIdValid = true;
        } else {
            isUserIdValid = false;
        }

        return isUserIdValid;
    }
    
    /* 13.4 Changes Start */
    public static boolean validateAnswerField(final String answer) {
        Boolean specialCharacterEntered = false;
        /* 13.4 Defect ID 105360 start */
        String regex = "^[a-zA-Z0-9 ]+$";
        /* 13.4 Defect ID 105360 End */
        Matcher matcher = Pattern.compile(regex).matcher(answer);
        if (matcher.find()) {
            specialCharacterEntered = false;
        } else {
            specialCharacterEntered = true;
        }

        return specialCharacterEntered;
    }
    /* 13.4 Changes End */

    /**
     * Determines if a given String value represents a valid day value. i.e.
     * true if 1 <= day <= 31
     * 
     * @author Darshana
     * @param day
     *            A String representation of a day of the month.
     * @return returns true if the day is a valid day.
     */
    public static boolean validateDay(final int day) {
        return valueBoundedBy(day, DAY_MIN_VALUE, DAY_MAX_VALUE);
    }

    /**
     * Determines if a given String value represents a valid month value. i.e.
     * true if 1 <= month <= 12
     * 
     * @author Darshana
     * @param day
     *            A String representation of a value of the month.
     * @return returns true if the month is a valid month.
     */
    public static boolean validateMonth(final int month) {
        return valueBoundedBy(month, MONTH_MIN_VALUE, MONTH_MAX_VALUE);
    }

    /**
     * Determines if a given String value represents a valid year value.
     * 
     * @author Darshana
     * @param year
     *            an integer representation of a year value.
     * @return returns true if the year is within reasonable year bounds.
     */
    public static boolean validateYear(final int year) {
        return valueBoundedBy(year, YEAR_MIN_VALUE, YEAR_MAX_VALUE);
    }

    /**
     * Checks to see if a given integer value is bounded by a min and max value.
     * 
     * @author Darshana
     * @param value
     *            an integer to check if it is within a range.
     * @param min
     *            the minimum value that the given value can be equal to.
     * @param max
     *            the maximum value that the given value can be equal to.
     * @return returns true if the value is greater than or equal to the min and
     *         less than or equal to the max.
     */
    public static boolean valueBoundedBy(final int value, final int min,
            final int max) {
        if (value >= min && value <= max) {
            return true;
        } else {
            return false;
        }
    }

}
