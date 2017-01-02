package com.kabanietzsche.admin.calc2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

public class MainFragment extends Fragment{

    private final String OPERATION_PLUS = "operation_plus";
    private final String OPERATION_MINUS = "operation_minus";
    private final String OPERATION_MULTIPLY = "operation_multiply";
    private final String OPERATION_DIVIDE = "operation_divide";
    private final String OPERATION_SQRT = "operation_sqrt";
    private final String OPERATION_SQR = "operation_sqr";

    private boolean afterBinaryOperationFlag = false;
    private boolean firstCalculationFlag = true;
    private String lastOperation = "";

    private Button clearButton;
    private Button sqrtButton;
    private Button powerButton;
    private Button divisionButton;
    private Button multiplicationButton;
    private Button minusButton;
    private Button plusButton;
    private Button equalsButton;
    private Button pointButton;
    private Button buttonZero;
    private Button buttonOne;
    private Button buttonTwo;
    private Button buttonThree;
    private Button buttonFour;
    private Button buttonFive;
    private Button buttonSix;
    private Button buttonSeven;
    private Button buttonEight;
    private Button buttonNine;

    private TextView mainTextView;
    private TextView calculationTextView;

    private double resultInMemory = 0;

    private int buttonsTapped = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        setUI(v);
        Info.loadStatistics(getContext());

        setOnNumberClickListeners();

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backspace();
            }
        });
        clearButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearAll();
                return false;
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeBinaryOperation(OPERATION_PLUS);
                Info.plusPressed();
                buttonsTapped++;
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeBinaryOperation(OPERATION_MINUS);
                Info.minusPressed();
                buttonsTapped++;
            }
        });

        multiplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeBinaryOperation(OPERATION_MULTIPLY);
                Info.multiplyPressed();
                buttonsTapped++;
            }
        });

        divisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeBinaryOperation(OPERATION_DIVIDE);
                Info.dividePressed();
                buttonsTapped++;
            }
        });

        sqrtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeUnaryOperation(OPERATION_SQRT);
                Info.sqrtPressed();
            }
        });

        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeUnaryOperation(OPERATION_SQR);
                Info.sqrPressed();
            }
        });

        pointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoint();
                buttonsTapped++;
            }
        });

        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeEquals();
            }
        });


        initializeOnShake();

        vibrate();

        return v;
    }

    /**
     * Initialize all variables
     */
    private void setUI(View v) {
        clearButton = (Button) v.findViewById(R.id.button_clear);
        sqrtButton = (Button) v.findViewById(R.id.button_sqrt);
        powerButton = (Button) v.findViewById(R.id.button_pow);
        divisionButton = (Button) v.findViewById(R.id.button_division);
        multiplicationButton = (Button) v.findViewById(R.id.button_multiply);
        minusButton = (Button) v.findViewById(R.id.button_minus);
        plusButton = (Button) v.findViewById(R.id.button_plus);
        equalsButton = (Button) v.findViewById(R.id.button_equals);
        pointButton = (Button) v.findViewById(R.id.button_point);
        buttonZero = (Button) v.findViewById(R.id.button_zero);
        buttonOne = (Button) v.findViewById(R.id.button_one);
        buttonTwo = (Button) v.findViewById(R.id.button_two);
        buttonThree = (Button) v.findViewById(R.id.button_three);
        buttonFour = (Button) v.findViewById(R.id.button_four);
        buttonFive = (Button) v.findViewById(R.id.button_five);
        buttonSix = (Button) v.findViewById(R.id.button_six);
        buttonSeven = (Button) v.findViewById(R.id.button_seven);
        buttonEight = (Button) v.findViewById(R.id.button_eight);
        buttonNine = (Button) v.findViewById(R.id.button_nine);

        mainTextView = (TextView) v.findViewById(R.id.main_text_view);
        calculationTextView = (TextView) v.findViewById(R.id.calculation_text_view);
    }

    /**
     * print formatted result
     *
     * @param result - double result
     */
    private void printResult(Double result) {
        mainTextView.setText(Utils.formatResult(result));
    }

    /**
     * set OnClickListeners for 0-9 buttons
     */
    private void setOnNumberClickListeners() {

        View.OnClickListener onNumberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentMainText = mainTextView.getText().toString();
                String number = "";
                String newText = "";

                switch (v.getId()) {
                    case R.id.button_one:
                        number = "1";
                        break;
                    case R.id.button_two:
                        number = "2";
                        break;
                    case R.id.button_three:
                        number = "3";
                        break;
                    case R.id.button_four:
                        number = "4";
                        break;
                    case R.id.button_five:
                        number = "5";
                        break;
                    case R.id.button_six:
                        number = "6";
                        break;
                    case R.id.button_seven:
                        number = "7";
                        break;
                    case R.id.button_eight:
                        number = "8";
                        break;
                    case R.id.button_nine:
                        number = "9";
                        break;
                    case R.id.button_zero:
                        number = "0";
                        break;
                }

                if (currentMainText.equals("0") || afterBinaryOperationFlag)
                    newText = number;
                else
                    newText = currentMainText + number;

                mainTextView.setText(newText);
                afterBinaryOperationFlag = false;
                buttonsTapped++;

            }
        };
        buttonOne.setOnClickListener(onNumberClickListener);
        buttonTwo.setOnClickListener(onNumberClickListener);
        buttonThree.setOnClickListener(onNumberClickListener);
        buttonFour.setOnClickListener(onNumberClickListener);
        buttonFive.setOnClickListener(onNumberClickListener);
        buttonSix.setOnClickListener(onNumberClickListener);
        buttonSeven.setOnClickListener(onNumberClickListener);
        buttonEight.setOnClickListener(onNumberClickListener);
        buttonNine.setOnClickListener(onNumberClickListener);
        buttonZero.setOnClickListener(onNumberClickListener);
    }

    /**
     * Remove last symbol
     */
    private void backspace() {
        String currentMainText = mainTextView.getText().toString();
        if (currentMainText.length() == 1)
            mainTextView.setText("0");
        else {
            String newMainText = currentMainText.substring(0, currentMainText.length() - 1);
            mainTextView.setText(newMainText);
        }
        afterBinaryOperationFlag = false;
    }

    /**
     * Clear all calculations
     */
    private void clearAll() {
        mainTextView.setText("0");
        calculationTextView.setText("");
        resultInMemory = 0;
        afterBinaryOperationFlag = false;
        lastOperation = "";
        firstCalculationFlag = true;
        buttonsTapped = 0;
    }

    /**
     * Add point symbol
     */
    private void addPoint() {
        String currentMainText = mainTextView.getText().toString();
        String newText = "";

        if (afterBinaryOperationFlag)
            newText = "0.";
        else if (currentMainText.contains("."))
            newText = currentMainText;
        else newText = currentMainText + ".";

        mainTextView.setText(newText);
        afterBinaryOperationFlag = false;
    }

    /**
     * Execute operations +, -, *, /
     *
     * @param operationCode - String constant
     */
    private void executeBinaryOperation(String operationCode) {
        String previousStringValue = mainTextView.getText().toString();
        double operationResult = 0;
        String calculations = calculationTextView.getText().toString();

        //Remove last operation and set number before operation symbol
        if (afterBinaryOperationFlag) {
            calculations = calculations.substring(0, calculations.length() - 3);
            previousStringValue = "";
            lastOperation = operationCode;
        }

        switch (operationCode) {
            case OPERATION_PLUS:
                calculations += previousStringValue + " + ";
                break;
            case OPERATION_MINUS:
                calculations += previousStringValue + " - ";
                break;
            case OPERATION_MULTIPLY:
                calculations += previousStringValue + " * ";
                break;
            case OPERATION_DIVIDE:
                calculations += previousStringValue + " / ";
                break;

        }

        calculationTextView.setText(calculations);

        if (afterBinaryOperationFlag)
            return;


        if (firstCalculationFlag)
            operationResult = Double.parseDouble(previousStringValue);
        else
            operationResult = calculate();

        if (lastOperation.equals(OPERATION_DIVIDE) && Double.parseDouble(previousStringValue) == 0) {
            Snackbar.make(divisionButton, getString(R.string.division_by_zero), Snackbar.LENGTH_SHORT).show();
            clearAll();
        } else {
            printResult(operationResult);
            resultInMemory = operationResult;
            afterBinaryOperationFlag = true;
            lastOperation = operationCode;
            firstCalculationFlag = false;
        }
    }

    /**
     * Execute operations sqrt and sqr
     *
     * @param operationCode
     */
    private void executeUnaryOperation(String operationCode) {
        String previousStringValue = mainTextView.getText().toString();
        double operationResult = 0;

        if (operationCode.equals(OPERATION_SQRT) && Double.parseDouble(previousStringValue) < 0) {
            Snackbar.make(sqrtButton, getString(R.string.invalid_sqrt), Snackbar.LENGTH_SHORT).show();
            clearAll();
            return;
        }

        switch (operationCode) {
            case OPERATION_SQRT:
                operationResult = Math.sqrt(Double.parseDouble(previousStringValue));
                break;
            case OPERATION_SQR:
                operationResult = Math.pow(Double.parseDouble(previousStringValue), 2);
                break;
        }

        clearAll();

        if (Math.abs(operationResult) < 1000000000) {
            printResult(operationResult);
            resultInMemory = operationResult;
        } else
            Snackbar.make(powerButton, getString(R.string.invalid_result), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Calculate all
     */
    private void executeEquals() {
        double result = calculate();
        buttonsTapped++;
        String calculations = calculationTextView.getText().toString() + mainTextView.getText().toString();
        if (!calculations.equals("")) {
            ResultModel resultModel = new ResultModel(Info.getYourName(), buttonsTapped, calculations, result);
            resultModel.save();
        }


        if (lastOperation.equals(OPERATION_DIVIDE) && Double.parseDouble(mainTextView.getText().toString()) == 0) {
            Snackbar.make(divisionButton, getString(R.string.division_by_zero), Snackbar.LENGTH_SHORT).show();
            clearAll();
            return;
        }

        clearAll();


        if (Math.abs(result) < 1000000000)
            printResult(result);
        else
            Snackbar.make(equalsButton, getString(R.string.invalid_result), Snackbar.LENGTH_SHORT).show();

        if (result == 32.0)
            startActivity(new Intent(getContext(), PhotoActivity.class));
    }

    /**
     * Calculate last operation
     *
     * @return
     */
    private double calculate() {
        String previousStringValue = mainTextView.getText().toString();
        Double previousDoubleValue = Double.parseDouble(previousStringValue);
        double calculationsResult = 0;


        switch (lastOperation) {
            case OPERATION_PLUS:
                calculationsResult = resultInMemory + previousDoubleValue;
                break;
            case OPERATION_MINUS:
                calculationsResult = resultInMemory - previousDoubleValue;
                break;
            case OPERATION_MULTIPLY:
                calculationsResult = resultInMemory * previousDoubleValue;
                break;
            case OPERATION_DIVIDE:
                calculationsResult = resultInMemory / previousDoubleValue;
        }
        return calculationsResult;
    }

    private void initializeOnShake() {
        // ShakeDetector initialization
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				clearAll();
            }
        });
    }

    /**
     * Vibrate for 500 milliseconds
     */
    private void vibrate() {
        final Vibrator vibrator = (Vibrator) getContext().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vibrator != null && mainTextView.getText().toString().equals("0") && calculationTextView.getText().toString().equals("")) {
                    vibrator.vibrate(500);
                }
            }
        }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //register the Session Manager Listener onResume
        sensorManager.registerListener(shakeDetector, accelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        //unregister the Sensor Manager onPause
        sensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }

}
