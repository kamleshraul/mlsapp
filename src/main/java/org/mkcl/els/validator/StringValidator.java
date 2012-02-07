/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.validator.StringValidator.java
 * Created On: Feb 7, 2012
 */
package org.mkcl.els.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * The Class UsernameValidator.
 *
 * @author nileshp
 */
public class StringValidator implements
        ConstraintValidator<StringValid, String> {

    /** The is alpha. */
    private boolean isAlpha = false;

    /** The is numeric. */
    private boolean isNumeric = false;

    /** The is space. */
    private boolean isSpace = false;

    /** The special symbols. */
    private String[] specialSymbols = null;

    /** The locale. */
    private String locale = "";

    /** The length. */
    private int length;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final StringValid constraintAnnotation) {
        isAlpha = constraintAnnotation.isAlpha();
        isNumeric = constraintAnnotation.isNumeric();
        isSpace = constraintAnnotation.isSpace();
        specialSymbols = constraintAnnotation.specialSymbols();
        locale = RequestContextHolder.currentRequestAttributes()
                .getAttribute("locale_els", RequestAttributes.SCOPE_SESSION).toString();
        length = constraintAnnotation.length();
    }

    /**
     * Checks if is valid.
     *
     * @param obj1 the value
     * @param context the context
     * @return true, if is valid
     */
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
        if (!result) {
             context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
             return false;
        } else {
            if (str.length() > length) {
                return false;
            }
        }
        return result;
    }

}
