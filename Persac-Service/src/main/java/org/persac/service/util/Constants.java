package org.persac.service.util;

import java.text.SimpleDateFormat;

/**
 * @author mzhokha
 * @since 01.05.14
 */
public class Constants {

    public static final String INCOME = "Income";
    public static final String OUTCOME = "Outcome";

    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String DD_MM_YYYY_EEEE = "dd-MM-yyyy EEEE";
    public static final SimpleDateFormat SDF  = new SimpleDateFormat(DD_MM_YYYY);

    public static final String USD = "USD";
}
