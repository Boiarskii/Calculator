package com.kabanietzsche.admin.calc2;


import com.orm.SugarRecord;

public class ResultModel extends SugarRecord {

    String userName;
    int buttonsTapped;
    String enteredData;
    double result;

    public ResultModel() {
    }

    public ResultModel(String userName, int buttonsTapped, String enteredData, double result) {
        this.userName = userName;
        this.buttonsTapped = buttonsTapped;
        this.enteredData = enteredData;
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public int getButtonsTapped() {
        return buttonsTapped;
    }

    public String getEnteredData() {
        return enteredData;
    }

    public double getResult() {
        return result;
    }
}
