/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.validator.StringValid.java
 * Created On: Feb 7, 2012
 */
package org.mkcl.els.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * The Interface StringValid.
 *
 * @author nileshp
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StringValidator.class)
public @interface StringValid {

    /**
     * Message.
     *
     * @author nileshp
     * @since v1.0.0
     */
    String message() default "Pattern mismatch";

    /**
     * Checks if is alpha.
     *
     */
    boolean isAlpha();

    /**
     * Checks if is numeric.
     *
     */
    boolean isNumeric();

    /**
     * Checks if is space.
     *
     */
    boolean isSpace();

    /**
     * Length.
     *
     * @author nileshp
     * @since v1.0.0
     */
    int length();
    /**
     * Special symbols.
     *
     * @author nileshp
     * @since v1.0.0
     */
    String[] specialSymbols();

    /**
     * Groups.
     *
     * @author nileshp
     * @since v1.0.0
     */
    public Class<?>[] groups() default { };

    /**
     * Payload.
     *
     * @author nileshp
     * @since v1.0.0
     */
    public Class<? extends Payload>[] payload() default { };

}
