package com.kabanietzsche.admin.calc2;


import android.content.Context;
import android.content.SharedPreferences;

public  class Info {

    public static final String MY_STATISTICS = "my_statistics";
    public static final String PLUS_BUTTON = "plus";
    public static final String MINUS_BUTTON = "minus";
    public static final String MULTIPLY_BUTTON = "multiply";
    public static final String DIVIDE_BUTTON = "divide";
    public static final String SQR_BUTTON = "sqr";
    public static final String SQRT_BUTTON = "sqrt";

    private static String yourName;

    private static int buttonPlusPressedCount;
    private static int buttonMinusPressedCount;
    private static int buttonMultiplyPressedCount;
    private static int buttonDividePressedCount;
    private static int buttonSqrPressedCount;
    private static int buttonSqrtPressedCount;

    private static SharedPreferences statistics;


    public static String getYourName() {
        return yourName;
    }

    public static void setYourName(String yourName) {
        Info.yourName = yourName;
    }

    public static void loadStatistics(Context context) {
        statistics = context.getSharedPreferences(MY_STATISTICS, Context.MODE_PRIVATE);

        buttonPlusPressedCount = statistics.getInt(PLUS_BUTTON, 0);
        buttonMinusPressedCount = statistics.getInt(MINUS_BUTTON, 0);
        buttonMultiplyPressedCount = statistics.getInt(MULTIPLY_BUTTON, 0);
        buttonDividePressedCount = statistics.getInt(DIVIDE_BUTTON, 0);
        buttonSqrPressedCount = statistics.getInt(SQR_BUTTON, 0);
        buttonSqrtPressedCount = statistics.getInt(SQRT_BUTTON, 0);
    }

    public static int getButtonPlusPressedCount() {
        return buttonPlusPressedCount;
    }

    public static int getButtonMinusPressedCount() {
        return buttonMinusPressedCount;
    }

    public static int getButtonMultiplyPressedCount() {
        return buttonMultiplyPressedCount;
    }

    public static int getButtonDividePressedCount() {
        return buttonDividePressedCount;
    }

    public static int getButtonSqrPressedCount() {
        return buttonSqrPressedCount;
    }

    public static int getButtonSqrtPressedCount() {
        return buttonSqrtPressedCount;
    }

    public static void plusPressed() {
        buttonPlusPressedCount++;
        saveValue(PLUS_BUTTON, buttonPlusPressedCount);
    }

    public static void minusPressed() {
        buttonMinusPressedCount++;
        saveValue(MINUS_BUTTON, buttonMinusPressedCount);
    }

    public static void multiplyPressed() {
        buttonMultiplyPressedCount++;
        saveValue(MULTIPLY_BUTTON, buttonMultiplyPressedCount);
    }

    public static void dividePressed() {
        buttonDividePressedCount++;
        saveValue(DIVIDE_BUTTON, buttonDividePressedCount);
    }

    public static void sqrPressed() {
        buttonSqrPressedCount++;
        saveValue(SQR_BUTTON, buttonSqrPressedCount);
    }

    public static void sqrtPressed() {
        buttonSqrtPressedCount++;
        saveValue(SQRT_BUTTON, buttonSqrtPressedCount);
    }


    private static void saveValue(String key, int value) {
        if (statistics != null) {
            SharedPreferences.Editor editor = statistics.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

}
