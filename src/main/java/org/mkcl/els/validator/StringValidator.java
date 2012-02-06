/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.validator.StringValidator.java
 * Created On: Feb 6, 2012
 */
package org.mkcl.els.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

// TODO: Auto-generated Javadoc
/**
 * The Class UsernameValidator.
 *
 * @author nileshp
 */
public class StringValidator implements
        ConstraintValidator<StringValid, String> {

    /** The is alpha. */
    boolean isAlpha = false;

    /** The is numeric. */
    boolean isNumeric = false;

    /** The is space. */
    boolean isSpace = false;

    /** The special symbols. */
    String[] specialSymbols = null;

    /** The locale. */
    String locale = "en";

    /** The field name. */
    String fieldName = null;

    /** The session. */
    @Autowired
    HttpSession session = null;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final StringValid constraintAnnotation) {
        isAlpha = constraintAnnotation.isAlpha();
        isNumeric = constraintAnnotation.isNumeric();
        isSpace = constraintAnnotation.isSpace();
        specialSymbols = constraintAnnotation.specialSymbols();
        locale = session.getAttribute("locale_els").toString();
        fieldName = constraintAnnotation.fieldName();

    }

    /**
     * Checks if is valid.
     *
     * @param obj1 the value
     * @param context the context
     * @return true, if is valid
     */
    @SuppressWarnings("null")
    @Override
    public boolean isValid(final String obj1,
                           final ConstraintValidatorContext context) {
        final String str = obj1;

        String startPatternString = "[";
        String endPatternString = "]*";
        String patternString = "";

        if (locale.equalsIgnoreCase("en")) {
            if (isAlpha) {
                patternString = "a-zA-Z";
            }

            if (isNumeric) {
                patternString = patternString + "0-9";
            }

            if (isSpace) {
                patternString = patternString + "\\s";
            }
        } else if (locale.equalsIgnoreCase("mr_IN")
                || locale.equalsIgnoreCase("hi_IN")) {

            if (isAlpha) {
                patternString = "\u0900-\u0965"; // devnagari unicode character
            }

            if (isNumeric) {
                patternString = patternString + "\u0966-\u096F"; // devnagari
                                                                 // unicode
                                                                 // numbers
            }

            if (isSpace) {
                patternString = patternString + "\\s"; // space
            }
        }

        String specialSym = "";
        /**** added special symbols in pattern string **/
        if (specialSymbols != null && specialSymbols.length > 0) {
            for (int i = 0; i < specialSymbols.length; i++) {
                if (i == 0) {
                    specialSym = "\\" + specialSymbols[i];
                } else {
                    specialSym = specialSym + "\\" + specialSymbols[i];
                }
            }
            patternString = startPatternString + patternString + specialSym
                    + endPatternString;
        } else {
            patternString = startPatternString + patternString
                    + endPatternString;
        }
        String errorMessage = "pattern mismatch";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        boolean result = matcher.matches();
        System.out.println("result is :- " + result);
        if (false == result) {
            /*
             * MessageResource messageResource = MessageResource
             * .findByLocaleAndCode(locale, "Pattern.state.name"); errorMessage
             * = messageResource.getValue();
             */
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            /*
             * Errors errors = null; errors.rejectValue(fieldName, "Pattern");
             */

            // Pattern.state.name
            return false;
        }
        return result;
    }

}
