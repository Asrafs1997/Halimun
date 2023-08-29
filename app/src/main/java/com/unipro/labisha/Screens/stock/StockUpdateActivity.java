package com.unipro.labisha.Screens.stock;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unipro.labisha.R;
import com.unipro.labisha.Screens.search.SearchActivity;
import com.unipro.labisha.adapter.PackageListAdapter;
import com.unipro.labisha.pdaSettings.SoapSettings;
import com.unipro.labisha.server.SoapConnection;
import com.unipro.labisha.utils.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by User1 on 21/06/2018.
 */
public class StockUpdateActivity extends AppCompatActivity {
    public static final String My_pref = "Mypref";

    SharedPreferences sprefLogin, sprefSettings;
    EditText edtItemCode, edtPhyStock;
    TextView txtDesc, txtAvgCost, txtUnitCost, txtArticle;
    TextView txtRetailPrice, txtSysStock, txtTitle;
    ImageView imgBtnSave, imgBtnClear, imgBtnCancel, imggrpClose;
    ImageButton btnScan, btnsearch;
    String baseUrl, sTag = "StockCheck", TerminalCode;
    String Uname, Loc_Code;
    LinearLayout linPhyStock;
    Toast toast;
    SoapConnection soapConn;
    Boolean ConnectStatus;
    DbHelper dbHelper;
    SQLiteDatabase mDB;

    ArrayList<HashMap<String, String>> mPackageItemList;

    ListView lstOrderItems;

    LinearLayout PackageItemList_llv;

    TextView txtOn_Hand, txtQty, txtInvCode;

    String android_id;
    private GestureDetector gestureDetector;

    private int SWIPE_MIN_DISTANCE = 120;
    private int SWIPE_MAX_OFF_PATH = 250;
    private int SWIPE_THRESHOLD_VELOCITY = 200;
    public SharedPreferences.Editor editorPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_update);
        editorPref = getSharedPreferences(My_pref, 0).edit();
        sprefLogin = this.getSharedPreferences(My_pref, 0);
        edtItemCode = (EditText) findViewById(R.id.edtItemCode);
        edtPhyStock = (EditText) findViewById(R.id.edtPhyStock);
        txtDesc = (TextView) findViewById(R.id.txtDescDetails);
        txtAvgCost = (TextView) findViewById(R.id.txtAvgCost);
        txtUnitCost = (TextView) findViewById(R.id.txtUnitCost);
        txtRetailPrice = (TextView) findViewById(R.id.txtRetailPrice);
        txtSysStock = (TextView) findViewById(R.id.txtSysStock);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtArticle = (TextView) findViewById(R.id.txtArticle);
        btnScan = (ImageButton) findViewById(R.id.btnscan);
        btnsearch = (ImageButton) findViewById(R.id.btnsearch);
        imgBtnSave = (ImageView) findViewById(R.id.imgBtnSave);
        imgBtnClear = (ImageView) findViewById(R.id.imgBtnClear);
        imgBtnCancel = (ImageView) findViewById(R.id.imgBtnCancel);
        imggrpClose = (ImageView) findViewById(R.id.grpClose);
        linPhyStock = (LinearLayout) findViewById(R.id.linPhyStock);
        lstOrderItems = (ListView) findViewById(R.id.lstOrderItems);
        PackageItemList_llv = (LinearLayout) findViewById(R.id.PackageItemList_llv);
        txtOn_Hand = (TextView) findViewById(R.id.txtOn_Hand);
        txtQty = (TextView) findViewById(R.id.txtQty);
        txtInvCode = (TextView) findViewById(R.id.txtInvCode);
        PackageItemList_llv.setVisibility(View.GONE);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode = sprefLogin.getString("TerminalCode", "");
        Loc_Code = sprefLogin.getString("LocationCode", "");
        baseUrl = sprefLogin.getString("BaseUrl", "");
        ConnectStatus = sprefLogin.getBoolean("connection", false);

        SharedPreferences settings = getApplicationContext().getSharedPreferences("baseUrl", 0);
        String setting = settings.getString("UsersUrl", "http://");
        baseUrl = setting;


        System.out.println("baseUrl " + baseUrl);
        onNewIntent(getIntent());
        soapConn = new SoapConnection();
        if (sTag.equalsIgnoreCase("StockCheck")) {
            linPhyStock.setVisibility(View.GONE);
            imgBtnSave.setVisibility(View.GONE);
        }
        dbHelper = new DbHelper(getApplicationContext());


        if (mDB != null) {
            mDB = dbHelper.getReadableDatabase();
        }
        mPackageItemList = new ArrayList<>();
        //mDB=dbHelper.getReadableDatabase();
        sprefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException ex) {
                    Log.e("onCreate", "Scanner Not Found", ex);
                    toast = Toast.makeText(getApplicationContext(), "Scanner Not Found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent in = new Intent(getApplicationContext(), SearchActivity.class);
                    in.putExtra("value", 4);
                    startActivityForResult(in, 4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* Intent in = new Intent(getApplicationContext(), SearchActivity.class);
                in.putExtra("value", 1);
                startActivityForResult(in, 5);*/
            }
        });
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), SoapSettings.class);
                startActivity(in);
                finish();
            }
        });
        edtItemCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String ItemCode = edtItemCode.getText().toString().trim();
                    if (ItemCode.equals("")) {
                        toast = Toast.makeText(getApplicationContext(), "Please enter ItemCode", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        edtItemCode.requestFocus();
                    } else {
                        WsCallGetInventory wsCallItemCode = new WsCallGetInventory();
                        wsCallItemCode.ItemCode = ItemCode;
                        wsCallItemCode.execute();
                    }
                    return true;
                }
                return false;
            }
        });
        edtPhyStock.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {//(event.getAction() == KeyEvent.ACTION_UP) &&
//                    btnUpdate.requestFocus();
                    fncUpdateStock();
                }
                return false;
            }
        });
        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fncUpdateStock();
            }
        });
        imgBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fncClearContent();
            }
        });
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //    gestureDetector = new GestureDetector(this, new SwipeDetector());
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        System.out.println("android_id  " + android_id);
        Boolean status = isNetworkAvailable();

        System.out.println("isNetworkAvailable status " + status);
        if (status) {
            AsyncStatusCall task = new AsyncStatusCall();
            task.execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection..!");
            builder.setCancelable(false);
            builder.setMessage("Check Your Network Connection");
           /* builder.setPositiveButton("Connect To LocalDB", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });*/
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

        private final ProgressDialog dialog = new ProgressDialog(StockUpdateActivity.this, ProgressDialog.THEME_HOLO_DARK);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(StockUpdateActivity.this);
                    builder.setTitle("Server Not Connected");
                    builder.setCancelable(false);
                    builder.setMessage("Check Server Address To Connect...!");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(StockUpdateActivity.this, SoapSettings.class);
                            startActivity(intent);
                            finish();
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

    private void fncShowDialog(String s) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(StockUpdateActivity.this);
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

    private void ShowDeviceDialog(String Id) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(StockUpdateActivity.this);
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
                    finish();
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

    @Override
    public void onNewIntent(Intent intent) {
        txtTitle.setText("Stock Check");
        Bundle extras = intent.getExtras();
        if (extras != null) {
           /* if (extras.containsKey("baseUrl")) {
                String baseUrls = extras.getString("baseUrl");
                baseUrl=baseUrls;
                SharedPreferences settings = getSharedPreferences("baseUrl", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("UsersUrl", baseUrls);
                editor.commit();

            }*/
        }


        System.out.println("--------" + baseUrl);
    }

    private void fncUpdateStock() {
        try {
            String sItemCode = edtItemCode.getText().toString().trim();
            String sQty = edtPhyStock.getText().toString().trim();
            if (sItemCode.equalsIgnoreCase("")) {
                toast = Toast.makeText(getApplicationContext(), "ItemCode Not to Empty!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                edtItemCode.requestFocus();
            } else if (sQty.equalsIgnoreCase("") || sQty.equalsIgnoreCase("0")) {
                toast = Toast.makeText(getApplicationContext(), "Qty Not to EMpty!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                edtPhyStock.requestFocus();
            }
            Double dQty = Double.parseDouble(sQty);
            if (dQty >= 0) {
                fncCallSaveStock(sItemCode, sQty, Loc_Code);
            } else {
                toast = Toast.makeText(getApplicationContext(), "Negative stock not allowed", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                edtPhyStock.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fncCallSaveStock(String sItemCode, String sQty, String loc_code) {
        try {
            WsCallSaveStockTake wsCallSaveStockTake = new WsCallSaveStockTake();
            wsCallSaveStockTake.ItemCode = sItemCode;
            wsCallSaveStockTake.sQty = sQty;
            wsCallSaveStockTake.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fncStandAloneStockUpdate(String ItemCode, String sQty) {

        String sQry = "";
        String PreviousQOH = "";
        try {
            sQry = "Select QtyonHand from tblvwinventory where Inventorycode=" + fncFormatSQLText(ItemCode);
            Cursor CurQty = mDB.rawQuery(sQry, null);
            if (CurQty.getCount() > 0) {
                CurQty.moveToFirst();
                PreviousQOH = CurQty.getString(CurQty.getColumnIndex("QtyonHand"));

                sQry = "select * from tblInventoryStockupdatefromPDA where InventoryCode=" + fncFormatSQLText(ItemCode);
                Cursor curData = mDB.rawQuery(sQry, null);
                if (curData.getCount() > 0) {
                    sQry = "Update tblInventoryStockupdatefromPDA set NewQOH=" + fncFormatSQLText(sQty) + ",CreateUser=" + fncFormatSQLText(Uname) + ",CreateDate=" + fncFormatSQLText(fncFormatSQLDate()) + " where InventoryCode=" + fncFormatSQLText(ItemCode);
                    mDB.execSQL(sQry);
                } else {
                    sQry = "Insert into tblInventoryStockupdatefromPDA (InventoryCode,PreviousQOH,NewQOH,CreateUser,CreateDate) values (" + fncFormatSQLText(ItemCode) + ","
                            + fncFormatSQLText(PreviousQOH) + "," + fncFormatSQLText(sQty) + "," + fncFormatSQLText(Uname) + "," + fncFormatSQLText(fncFormatSQLDate()) + ")";
                    Log.e("Insert StockupdateAlone", sQry);
                    mDB.execSQL(sQry);
                }

                sQry = "Update tblVwInventory set QtyonHand=" + fncFormatSQLText(sQty) + "where InventoryCode=" + fncFormatSQLText(ItemCode) + "and LocationCode=" + fncFormatSQLText(Loc_Code);
                Log.e("Update Inventory Qty", sQry);
                mDB.execSQL(sQry);

                sQry = "Insert into tblInventoryStockupdate (InventoryCode,NewQOH,CreateUser,CreateDate) values (" + fncFormatSQLText(ItemCode) + ","
                        + fncFormatSQLText(sQty) + "," + fncFormatSQLText(Uname) + "," + fncFormatSQLText(fncFormatSQLDate()) + ")";
                mDB.execSQL(sQry);

//            runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
//                    }
//                });
                // Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                return "true,Saved Successfully";
            } else {
                Toast.makeText(getApplicationContext(), "Inventory Not Found", Toast.LENGTH_SHORT).show();
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "Not Saved";
        }


    }


    private void fncClearContent() {
        edtItemCode.setText("");
        txtDesc.setText("");
        txtArticle.setText("");
        txtRetailPrice.setText("");
        txtUnitCost.setText("");
        txtAvgCost.setText("");
        txtSysStock.setText("");
        edtPhyStock.setText("");
        edtItemCode.post(new Runnable() {
            @Override
            public void run() {
                edtItemCode.requestFocus();
            }
        });
        PackageItemList_llv.setVisibility(View.GONE);
    }

    private class WsCallGetInventory extends AsyncTask<String, String, String> {
        public String ItemCode;
        String sResp = "", sMethod = "fncGetInventoryDetail";
//        String sUrl="http://192.168.0.88:82/UniproPDA/androidservice.asmx";

        private final ProgressDialog dialog = new ProgressDialog(StockUpdateActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (ConnectStatus) {
                    sResp = soapConn.fncGetInventoryDetail(ItemCode, Uname, Loc_Code, sMethod, baseUrl);
                    if (sResp.equalsIgnoreCase("false")) {
                        sResp = "false,Item not Found";
                    }
                    return sResp;
                } else {
                    sResp = fnccheckstockstandalone(ItemCode);
                    return sResp;
                }
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
                    String[] sResponse = s.split(",");
                    Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT).show();
                    fncClearContent();
                } else {
                    JSONArray jsArray = new JSONArray(s);
                    JSONObject jsObj = jsArray.getJSONObject(0);
                    edtItemCode.setText(jsObj.getString("InventoryCode").trim());
                    txtDesc.setText(jsObj.getString("Description").trim());
                    txtAvgCost.setText(jsObj.getString("AverageCost").trim());
                    txtUnitCost.setText(jsObj.getString("UnitCost").trim());
                    txtRetailPrice.setText(jsObj.getString("RetailPrice").trim());
                    txtSysStock.setText(jsObj.getString("QtyonHand").trim());
                    txtArticle.setText(jsObj.getString("CatalogueNo").trim());
                    String PackageItem = jsObj.getString("PackageItem").trim();
                    PackageItemList_llv.setVisibility(View.GONE);
                    txtOn_Hand.setVisibility(View.GONE);

                    if (PackageItem.equals("true")) {
                        WsCallGetGetPackage wsCallGetGetPackage = new WsCallGetGetPackage();
                        wsCallGetGetPackage.ItemCode = jsObj.getString("InventoryCode").trim();
                        wsCallGetGetPackage.isPackage = true;
                        wsCallGetGetPackage.execute();
                        txtInvCode.setText("Inv.Code");
                        txtQty.setText("Qty");
                        txtOn_Hand.setVisibility(View.VISIBLE);

                    } else {
                        WsCallGetGetPackage wsCallGetGetPackage = new WsCallGetGetPackage();
                        wsCallGetGetPackage.ItemCode = jsObj.getString("InventoryCode").trim();
                        wsCallGetGetPackage.isPackage = false;
                        wsCallGetGetPackage.execute();
                        txtInvCode.setText("Package Inv.Code");
                        txtQty.setText("Package Qty");

                        txtOn_Hand.setVisibility(View.GONE);
                    }


                    edtPhyStock.requestFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class WsCallGetGetPackage extends AsyncTask<String, String, String> {
        public String ItemCode;
        public boolean isPackage;
        String sResp = "", sMethod = "fncGetPackageItemList";
//        String sUrl="http://192.168.0.88:82/UniproPDA/androidservice.asmx";

        private final ProgressDialog dialog = new ProgressDialog(StockUpdateActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (ConnectStatus) {
                    sResp = soapConn.fncGetPackageItemList(ItemCode, isPackage, sMethod, baseUrl);
                    if (sResp.equalsIgnoreCase("false")) {
                        sResp = "false,Item not Found";
                    }
                    return sResp;
                } else {
                    sResp = fnccheckstockstandalone(ItemCode);
                    return sResp;
                }
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
                    String[] sResponse = s.split(",");
                    Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT).show();
                    fncClearContent();
                } else {
                    try {
                        mPackageItemList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsObj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsObj = jsonArray.getJSONObject(i);
                            HashMap<String, String> mapData = new HashMap<>();
                            // mapData.put("InventoryCode", jsObj.getString("InventoryCode").trim());
                            // mapData.put("Description1", jsObj.getString("Description1").trim());
                            mapData.put("QtyonHand", jsObj.getString("QtyonHand").trim());
                            //   mapData.put("Qty", jsObj.getString("Qty").trim());
                            mapData.put("RetailPrice", jsObj.getString("RetailPrice").trim());

                            if (isPackage) {
                                mapData.put("InventoryCode", jsObj.getString("InventoryCode").trim());
                            } else {
                                mapData.put("InventoryCode", jsObj.getString("PackageInvCode1").trim());
                            }
                            if (isPackage) {
                                mapData.put("Description1", jsObj.getString("Description1").trim());
                            } else {
                                mapData.put("Description1", jsObj.getString("Description").trim());
                            }
                            if (isPackage) {
                                mapData.put("Qty", jsObj.getString("Qty").trim());
                            } else {
                                mapData.put("Qty", jsObj.getString("TotalQty").trim());
                            }

                            mPackageItemList.add(mapData);

                        }


                        if (mPackageItemList.size() > 0) {
                            PackageItemList_llv.setVisibility(View.VISIBLE);
                            PackageListAdapter mListAdapter = new PackageListAdapter(getApplicationContext(), isPackage);
                            mListAdapter.setData(mPackageItemList);
                            lstOrderItems.setAdapter(mListAdapter);
                        } else {
                            PackageItemList_llv.setVisibility(View.GONE);
                        }

                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String fnccheckstockstandalone(String ItemCode) {
        try {
            String sQry = "";
            sQry = "select InventoryCode from tblbarcode where Barcode=" + fncFormatSQLText(ItemCode);
            Cursor crInv = mDB.rawQuery(sQry, null);
            int mSize = crInv.getCount();
            if (mSize > 0) {
                crInv.moveToFirst();
                ItemCode = crInv.getString(crInv.getColumnIndex("InventoryCode"));
            }
            sQry = "select * from tblVwInventory where InventoryCode=" + fncFormatSQLText(ItemCode);
            Cursor crinv = mDB.rawQuery(sQry, null);
            if (crinv.getCount() > 0) {
                crinv.moveToFirst();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("InventoryCode", crinv.getString(crinv.getColumnIndex("InventoryCode")));
                jsonObject.put("Description", crinv.getString(crinv.getColumnIndex("Description")));
                String sAvgCost = crinv.getString(crinv.getColumnIndex("AverageCost"));
                if (sAvgCost == null) {
                    sAvgCost = "0";
                }
                jsonObject.put("AverageCost", sAvgCost);
                jsonObject.put("UnitCost", crinv.getString(crinv.getColumnIndex("UnitCost")));
                jsonObject.put("RetailPrice", crinv.getString(crinv.getColumnIndex("RetailPrice")));
                jsonObject.put("QtyonHand", crinv.getString(crinv.getColumnIndex("QtyonHand")));
                jsonArray.put(jsonObject);
                return jsonArray.toString();
                /* edtItemCode.setText( crinv.getString( crinv.getColumnIndex( "InventoryCode" ) ) );
                    txtDesc.setText( crinv.getString( crinv.getColumnIndex( "Description" ) ) );
                    txtAvgCost.setText( crinv.getString( crinv.getColumnIndex( "AverageCost" ) ) );
                    txtUnitCost.setText( crinv.getString( crinv.getColumnIndex( "UnitCost" ) ) );
                    txtRetailPrice.setText( crinv.getString( crinv.getColumnIndex( "RetailPrice" ) ) );
                    txtSysStock.setText( crinv.getString( crinv.getColumnIndex( "QtyonHand" ) ) );
                    edtPhyStock.requestFocus();*/
            } else {
                return "false,Item Not Found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "false," + e.toString();
        }
    }

    private String fncFormatSQLText(String sText) {
        if (sText == null) {
            return "''";
        }
        String sGenString = "";
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String sItemCode = "";
        if (resultCode == 0) {
            if (requestCode == 4) {
                Toast toast = Toast.makeText(getApplicationContext(), "Search Canceled", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                edtItemCode.requestFocus();
            } else if (requestCode == 5) {
                Boolean status = isNetworkAvailable();
                if (status) {
                    AsyncStatusCall task = new AsyncStatusCall();
                    task.execute();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No Internet connection...!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

        } else {
            Bundle bundle = data.getExtras();
            if (requestCode == 4 && resultCode == -1) {
                sItemCode = bundle.getString("name");
            } else if (requestCode == 0 && resultCode == -1) {
                sItemCode = bundle.getString("SCAN_RESULT");
            }
            edtItemCode.setText(sItemCode);

            if (sItemCode.equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "Item Code Not Found", Toast.LENGTH_SHORT).show();
            } else {

                WsCallGetInventory wsCallItemCode = new WsCallGetInventory();
                wsCallItemCode.ItemCode = sItemCode;
                wsCallItemCode.execute();
            }


        }

    }

    private class WsCallSaveStockTake extends AsyncTask<String, String, String> {
        public String ItemCode;
        public String sQty;
        String sResp = "", sMethod = "fncSaveStockUpdate";
        private final ProgressDialog dialog = new ProgressDialog(StockUpdateActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (ConnectStatus) {
                    sResp = soapConn.fncSaveStockUpdate(ItemCode, sQty, Uname, Loc_Code, sMethod, baseUrl);
                    return sResp;
                } else {
                    sResp = fncStandAloneStockUpdate(ItemCode, sQty);
                    return sResp;
                }
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
                if (s.startsWith("true")) {
                    fncClearContent();
                    Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String fncFormatSQLDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String systemDate = df.format(date);
        return systemDate;
    }

}
