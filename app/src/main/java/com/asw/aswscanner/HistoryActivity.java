package com.asw.aswscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    private Button btnLogout;
    private TextView txtName;
    private TextView txtId;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String REMEMBER_ME = "rememberMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        txtName = findViewById(R.id.txtName);
        txtId = findViewById(R.id.txtId);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String studentName = sharedPreferences.getString(USER_NAME, "");
        String studentId = sharedPreferences.getString(USER_ID, "");
        txtName.setText(studentName);
        txtId.setText(studentId);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}