package com.asw.aswscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import android.util.Base64;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ScanConfirmActivity extends AppCompatActivity {
    private TextView sectionName;
    private TextView classDate;
    private TextView scanTime;
    private TextView test;
    private TextView txtName;
    private TextView txtId;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String REMEMBER_ME = "rememberMe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_confirm);

        txtName = findViewById(R.id.txtName);
        txtId = findViewById(R.id.txtId);
        sectionName = findViewById(R.id.sectionName);
        classDate = findViewById(R.id.classDate);
        scanTime = findViewById(R.id.scanTime);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String studentName = sharedPreferences.getString(USER_NAME, "");
        String studentId = sharedPreferences.getString(USER_ID, "");
        txtName.setText(studentName);
        txtId.setText(studentId);

        String decQrData = "";
        String encQrData = getIntent().getExtras().getString("qrData");
        byte[] data = Base64.decode(encQrData, Base64.DEFAULT);
        decQrData = new String(data, StandardCharsets.UTF_8);
        String[] dataArray = decQrData.split(",");
        sectionName.setText(dataArray[1]);
        String date2disp = "Class date: " + dataArray[2];
        classDate.setText(date2disp);

        String scanTimeData = getIntent().getExtras().getString("scanTime");
        String scanTime2disp = "Scan Time: " + scanTimeData;
        scanTime.setText(scanTime2disp);
    }
}