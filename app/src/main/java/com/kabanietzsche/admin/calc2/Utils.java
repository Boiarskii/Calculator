package com.kabanietzsche.admin.calc2;


import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {


    public static String formatResult(double result) {
        double doubleResult = result;
        int intResult = (int) doubleResult;
        if (doubleResult - intResult != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US));
            return decimalFormat.format(doubleResult);
        } else {
            return String.valueOf(intResult);
        }
    }


}
