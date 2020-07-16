package com.asw.aswscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText editId;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnForgot;
    private WebView webView;
    private CheckBox rememberMe;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String REMEMBER_ME = "rememberMe";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgot = findViewById(R.id.btnForgot);
        webView = findViewById(R.id.webView);
        rememberMe = findViewById(R.id.rememberMe);

        final Context myApp = this;

        /* An instance of this class will be registered as a JavaScript interface */
        class MyJavaScriptInterface
        {
            @JavascriptInterface
            @SuppressWarnings("unused")
            public void processHTML(String html)
            {
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setTitle("HTML");
//                alertDialog.setMessage(html);
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
                //alertDialog.show();
                if (html.contains("Invalid Captcha Answer")){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Too many failed login attempts. Use an external browser to answer captcha first before trying again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if (html.contains("Invalid username or password") || html.contains("Invalid Username or Password")){
                    //Toast.makeText(MainActivity.this, "Invalid Username or Password. Please Try Again", Toast.LENGTH_LONG).show();AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Invalid Username or Password. Please Try Again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
                else if (html.contains("Sign in"))
                {
                    Toast.makeText(MainActivity.this, "Login with your VUES ID and Password", Toast.LENGTH_LONG).show();
                }
                else if (html.contains("Welcome") && !html.contains("CGPA")) {
                    //Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_LONG).show();
                    //webView.loadUrl("https://portal.aiub.edu/Student/Home/Profile");
                    final WebView mWebView = (WebView) findViewById(R.id.webView);
                    mWebView.post(new Runnable() {
                        public void run() {
                            mWebView.loadUrl("https://portal.aiub.edu/Student/Home/Profile");
                        }
                    });
                }
                else if (html.contains("Welcome") && html.contains("CGPA")){
                    String studentName = "Student Name";
                    final Pattern pattern = Pattern.compile("<legend>(.+?)</legend>", Pattern.DOTALL);
                    final Matcher matcher = pattern.matcher(html);
                    matcher.find();
                    studentName = matcher.group(1);
                    studentName = studentName.trim();

                    String studentId = "Student ID";
                    final Pattern pattern2 = Pattern.compile("<td style=\"text-align: right; width: 30%\">Student ID :</td>\\s*<td style=\"text-align: left; width: 70%\">(.+?)</td>", Pattern.DOTALL);
                    final Matcher matcher2 = pattern2.matcher(html);
                    matcher2.find();
                    studentId = matcher2.group(1);
                    studentId = studentId.trim();

                    if (studentName != null){
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        if(sharedPreferences.getBoolean(REMEMBER_ME, false)){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(USER_NAME, studentName);
                            editor.putString(USER_ID, studentId);
                            editor.apply();

                            Toast.makeText(MainActivity.this, "User data saved in prefs", Toast.LENGTH_SHORT).show();
                        }
                        
                        Toast.makeText(MainActivity.this, "Logged In " + studentName + " " + studentId + " first", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.putExtra("studentName", studentName);
                        intent.putExtra("studentId", studentId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
                else if (!html.contains("Welcome")){
                    Toast.makeText(MainActivity.this, "Login with your VUES ID and Password 2", Toast.LENGTH_SHORT).show();
                    //webView.loadUrl("https://portal.aiub.edu/Login");final WebView mWebView = (WebView) findViewById(R.id.webView);
                    final WebView mWebView = (WebView) findViewById(R.id.webView);
                    mWebView.post(new Runnable() {
                        public void run() {
                            mWebView.loadUrl("https://portal.aiub.edu/Login");
                        }
                    });

                }
                else{
                    Toast.makeText(MainActivity.this, "what", Toast.LENGTH_SHORT).show();
                }
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                //Toast.makeText(MainActivity.this, "Load Finished", Toast.LENGTH_SHORT).show();
                webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(MainActivity.this, "Could not connect to portal.aiub.edu. Error code: " + errorCode + " . Please check your Internet connection", Toast.LENGTH_LONG).show();
            }
        });

        webView.loadUrl("https://portal.aiub.edu/Login/Logout");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = editId.getText().toString();
                String userPassword = editPassword.getText().toString();

                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != MainActivity.this.getCurrentFocus())
                    imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus()
                            .getApplicationWindowToken(), 0);

                webView.evaluateJavascript("document.getElementById('username').value = \"" + userId + "\";" +
                        "document.getElementById('password').value = \"" + userPassword + "\";" +
                        "document.forms[0].submit();", null);
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean(REMEMBER_ME, rememberMe.isChecked());
                editor.apply();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean rememberState = sharedPreferences.getBoolean(REMEMBER_ME, false);
        rememberMe.setChecked(rememberState);

        if(rememberState){
            String studentName = sharedPreferences.getString(USER_NAME, "");
            String studentId = sharedPreferences.getString(USER_ID, "");

            if(!(studentId.equals("") || studentName.equals(""))){
                Toast.makeText(MainActivity.this, "Logged In " + studentName + " " + studentId + " first", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("studentName", studentName);
                intent.putExtra("studentId", studentId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }
}