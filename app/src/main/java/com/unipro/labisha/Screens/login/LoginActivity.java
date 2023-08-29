package com.unipro.labisha.Screens.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.R;
import com.unipro.labisha.pdaSettings.SoapSettings;
import com.unipro.labisha.server.SoapConnection;


import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kalaivanan on 23/01/2018.
 */
public class LoginActivity extends AppCompatActivity {
    public static final String My_pref = "Mypref";
    ImageView imgLogin, imgCancel, imgSettings;
    Boolean ConnectStatus;
    RadioGroup rbtnGrpDb;
    RadioButton rbtnOnline, rbtnOffline;
    LinearLayout linConnection;
    public SharedPreferences sprefSettings, sprefLogin;
    String baseUrl, sUserName, sPassword, android_id;
    EditText edtUserName, edtPassword;
    private int SWIPE_MIN_DISTANCE = 120;
    private int SWIPE_MAX_OFF_PATH = 250;
    private int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    public SharedPreferences.Editor editorPref;
    SoapConnection soapConn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
            sprefSettings = PreferenceManager.getDefaultSharedPreferences(this);
            imgLogin = (ImageView) findViewById(R.id.imgLogin);
            imgCancel = (ImageView) findViewById(R.id.imgCancel);
            imgSettings = (ImageView) findViewById(R.id.grpSettings);
            edtUserName = (EditText) findViewById(R.id.edtUserName);
            edtPassword = (EditText) findViewById(R.id.edtPassword);
            rbtnGrpDb = (RadioGroup) findViewById(R.id.rbtnGrpDb);
            rbtnOnline = (RadioButton) findViewById(R.id.rbtnOnline);
            rbtnOffline = (RadioButton) findViewById(R.id.rbtnOffline);
            linConnection = (LinearLayout) findViewById(R.id.linConnection);
            editorPref = getSharedPreferences(My_pref, 0).edit();
            sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
            gestureDetector = new GestureDetector(LoginActivity.this, new SwipeDetector());
            android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            System.out.println("android_id  " + android_id);
            soapConn = new SoapConnection();
            //   dbHelper.onUpgrade(mDb,1,1);
            ConnectStatus = sprefLogin.getBoolean("connection", false);
            linConnection.setVisibility(View.GONE);
            ConnectStatus = true;
            editorPref.putBoolean("connection", true);
            editorPref.commit();
            if (ConnectStatus) {
                rbtnOnline.setChecked(true);
            } else {
                rbtnOffline.setChecked(true);
            }
            rbtnGrpDb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (rbtnOnline.isChecked()) {
                        ConnectStatus = true;
                        editorPref.putBoolean("connection", true);
                        editorPref.commit();
                    } else {
                        ConnectStatus = false;
                        editorPref.putBoolean("connection", false);
                        editorPref.commit();
                    }
                }
            });
            imgLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sUserName = edtUserName.getText().toString();
                    sPassword = edtPassword.getText().toString();
                    if (sUserName.equalsIgnoreCase("") || sPassword.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "UserName or Password Not Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        fncGetUserLogin(sUserName, sPassword);
                    }
                }
            });

            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            baseUrl = sprefSettings.getString(SoapSettings.KEY_BaseURL, "");
            editorPref.putString("BaseUrl", baseUrl);
            editorPref.commit();
//            Toast.makeText(getApplicationContext(),baseUrl,Toast.LENGTH_SHORT).show();
            Boolean status = isNetworkAvailable();
            if (status) {
                AsyncStatusCall task = new AsyncStatusCall();
                task.execute();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Check Your Network Connection");
                builder.setPositiveButton("Connect To LocalDB", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Open NetWorkConn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivityForResult(intent, 5);
                        dialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
            }


            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(getApplicationContext(), SoapSettings.class);
                    startActivity(in);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fncGetUserLogin(String sUserName, String sPassword) {
        try {
            WsCallGetLogin wsCallGetLogin = new WsCallGetLogin();
            wsCallGetLogin.sUserName = sUserName;
            wsCallGetLogin.sPassword = sPassword;
            wsCallGetLogin.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fncShowDialog(String s) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Server Connection Problem");
            builder.setMessage(s);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowNetworkDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Check Your Network Connection");
            builder.setPositiveButton("Open NetWorkConn", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivityForResult(intent, 5);
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowDeviceDialog(String Id) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Terminal Not Configured");
            builder.setMessage("Configure Terminal id is " + Id);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class AsyncStatusCall extends AsyncTask<String, String, String> {
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_HOLO_DARK);
        String sResp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Checking Server...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            super.onProgressUpdate(values);
            dialog.setMessage("Connecting Server");
            dialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // https://www.google.co.in/
                //  HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
                HttpURLConnection urlc = (HttpURLConnection) (new URL(baseUrl).openConnection());

                System.out.println("baseUrl  " + baseUrl);

                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(5000);
                urlc.setReadTimeout(5000); // 5 seconds
                urlc.disconnect();
                int code = urlc.getResponseCode();
                if (code == 200) {
                    return "true";
                } else {
                    return "false";
                }
            } catch (Exception e) {
                e.printStackTrace();
                sResp = e.toString();
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String sValues) {
            super.onPostExecute(sValues);
            dialog.dismiss();
            try {
                if (sValues.equalsIgnoreCase("true")) {
                    Toast.makeText(getApplicationContext(), "Server Connected", Toast.LENGTH_SHORT).show();
                    fncGetTerminalDetails();
                } else {
//                    Toast.makeText(getApplicationContext(), "No NetWork Access,Pls Check Connection", Toast.LENGTH_LONG).show();
                    //  ShowNetworkDialog();

                    System.out.println("Server error  " + sResp);
                    //  Toast.makeText(getApplicationContext(), sResp, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Server Not Connected");
                    builder.setMessage("Check Server Address To Connect...!");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LoginActivity.this, SoapSettings.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fncGetTerminalDetails() {
        try {
            WsCallGetTerminal wsCallGetTerminal = new WsCallGetTerminal();
            wsCallGetTerminal.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }

            //from left to right
            if (e2.getX() > e1.getX()) {
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    onSwipeRight();
//                    Toast.makeText(getApplicationContext(),"From Left to Right",Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

            //from Right to left
            if (e1.getX() > e2.getX()) {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    onSwipeLeft();
//                    Toast.makeText(getApplicationContext(),"From Right to Left",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(), SoapSettings.class);
                    startActivity(in);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TouchEvent dispatcher.
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class WsCallGetTerminal extends AsyncTask<String, String, String> {
        String sResp = "";
        String sWebMethod = "fncGetTerminal";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (ConnectStatus) {

                    sResp = soapConn.fncGetTerminal(android_id, sWebMethod, baseUrl);
                }
                return sResp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s.startsWith("false")) {
                    ShowDeviceDialog(android_id);
                } else {
                    JSONArray jsonArray = new JSONArray(s);
                    String sLocation = jsonArray.getString(1).trim();
                    String sTerminal = jsonArray.getString(0).trim();
                    editorPref.putString("TerminalCode", sTerminal);
                    editorPref.putString("LocationCode", sLocation);
                    editorPref.putString("sGstPerc", jsonArray.getString(2).trim());
                    editorPref.putString("sGstType", jsonArray.getString(3).trim());
                    editorPref.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fncShowDialog(s);
            }
        }
    }

    private String fncFormatSQLText(String sText) {
        if (sText == null) {
            return null;
        }
        String sGenString = "";
        sText = sText.trim();
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }

    private class WsCallGetLogin extends AsyncTask<String, String, String> {
        public String sUserName;
        public String sPassword;
        String sResp = "";
        String sWebMethod = "fncCheckLogin";
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Checking...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (ConnectStatus) {
                    MenuListActivity.UpdateAlert = false;
                    sResp = soapConn.fncCheckLogin(android_id, sUserName, sPassword, sWebMethod, baseUrl);
                }
                return sResp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String[] sResponse = s.split(",");
                if (sResponse[0].equalsIgnoreCase("true")) {
                    editorPref.putString("Uname", sUserName);
                    editorPref.putString("DeviceId", android_id);
                    editorPref.commit();
                    Toast.makeText(getApplicationContext(), "Welcome " + sUserName, Toast.LENGTH_SHORT).show();
                    //      Intent in=new Intent(getApplicationContext(),MenuActivity.class);
                    Intent in = new Intent(getApplicationContext(), MenuListActivity.class);
                    startActivity(in);
                } else {
                    Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT).show();
                    edtUserName.requestFocus();
                    if (sResponse[1].equalsIgnoreCase("Terminal Not Configured")) {
                        ShowDeviceDialog(android_id);
                    }
                }
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
