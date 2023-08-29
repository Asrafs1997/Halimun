package com.unipro.labisha.Screens.localDb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.adapter.DBTransferListAdapter;
import com.unipro.labisha.server.SoapConnection;
import com.unipro.labisha.utils.CustomButtonClick;
import com.unipro.labisha.utils.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by User1 on 26/06/2018.
 */
public class DBTransferActivity extends AppCompatActivity {
    ListView lstTable;
    SharedPreferences sprefLogin;
    String DeviceId,TerminalCode;
    String Uname,Loc_Code,baseUrl;
    public static Boolean IsDownload=true;
    ArrayList<HashMap<String,String>> TableList;
    public ArrayList<String> TableUpload ;
    public ArrayList<String> TableDownload ;
    ImageView imgGrpSync,imggrpClose;
    Boolean ConnectStatus;
    TextView txtTitle;
    RadioGroup rdgrpDb;
    RadioButton rbtnUpload,rbtnDownload;
    SQLiteDatabase mDb=null;
    DbHelper dbHelper;
    SoapConnection soapConn;
    DBTransferListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbtransfer_activity);
        dbHelper=new DbHelper(getApplicationContext());
        mDb=dbHelper.getReadableDatabase();
//        dbHelper.onCreate(mDb);
        lstTable=(ListView)findViewById(R.id.lstTable);
        imgGrpSync=(ImageView)findViewById(R.id.imgGrpReload);
        imggrpClose=(ImageView)findViewById(R.id.grpClose);
        txtTitle=(TextView)findViewById(R.id.txtTitle);
        sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
        rdgrpDb=(RadioGroup)findViewById(R.id.rbtnGrpDb);
        rbtnUpload=(RadioButton)findViewById(R.id.rbtnUpload);
        rbtnDownload=(RadioButton)findViewById(R.id.rbtnDownload);
        txtTitle.setText("DB Sync");
        DeviceId=sprefLogin.getString("Deviceid", null);
        ConnectStatus=sprefLogin.getBoolean("connection", false);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        soapConn=new SoapConnection();
        TableList=new ArrayList<>();
        TableUpload=new ArrayList<>();
        TableDownload=new ArrayList<>();

        mListAdapter = new DBTransferListAdapter(getApplicationContext());
        mListAdapter.setData(TableList);
        lstTable.setAdapter(mListAdapter);
        TableDownload.add("User Details");
//        TableDownload.add("Customer Details");
        TableDownload.add("Inventory Details");
//        TableDownload.add("Sales Invoice");

//        TableUpload.add("Delivery Order");
        TableUpload.add("Stock Update");
//        TableUpload.add("Collection Entry");

        if(IsDownload){
            rbtnDownload.setChecked(true);
        }else {
            rbtnUpload.setChecked(true);
        }
        rdgrpDb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtnUpload) {
                    fncLoadUploadDetails();
                } else {
                    fncLoadDownloadDetails();
                }
            }
        });
        lstTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();

            }
        });
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), MenuListActivity.class);
                finish();
                startActivity(in);
            }
        });

        if(rbtnUpload.isChecked()) {
            fncLoadUploadDetails();
        }else {
            fncLoadDownloadDetails();
        }
    }

    private void fncLoadDownloadDetails() {
        TableList=new ArrayList<>();
        for(int i=0;i<TableDownload.size();i++) {
            HashMap<String, String> map = new HashMap<>();
            String sQry="";
            String LastSync="";
            switch (i){
                case 0:
                    sQry="select [UserLastSync] from tblDownloadLog ";
                    LastSync = fncExecuteScalar(sQry);

//                    LastSync="00.00.00";
                    break;
                case 1:
                    sQry="select [InvLastSync] from tblDownloadLog";
                    LastSync = fncExecuteScalar(sQry);

//                    LastSync="00.00.00";
                    break;
                case 2:
                    sQry="select [CustLastSync] from tblDownloadLog";
                    LastSync = fncExecuteScalar(sQry);
//                    LastSync="00.00.00";
                    break;
                default:
                    break;
            }
            map.put("Details", TableDownload.get(i));
            map.put("Process", "Download");
            map.put("LastSync", LastSync);
            Log.w("TableList", "" + map);
            TableList.add(map);
        }
        mListAdapter=new DBTransferListAdapter(getApplicationContext(), new CustomButtonClick() {
            @Override
            public void onSynClick(View v, HashMap<String, String> map, String opr) {
                String sData=map.get("Details");
                Toast.makeText(getApplicationContext(),sData,Toast.LENGTH_SHORT).show();
                WsCallSyncDownloadTable wsCallSyncDownloadTable=new WsCallSyncDownloadTable();
                wsCallSyncDownloadTable.TableName=sData;
                wsCallSyncDownloadTable.execute();
            }
        });
        mListAdapter.setData(TableList);
        lstTable.setAdapter(mListAdapter);
        setListViewHeightBasedOnChildren(lstTable);
    }
    private void fncLoadUploadDetails() {
        TableList=new ArrayList<>();
        for(int i=0;i<TableUpload.size();i++) {
            HashMap<String, String> map = new HashMap<>();
            String sQry="";
            String LastSync="";
            switch (i){
                case 0:
                    sQry="select [InvStkLastSync] from tblStockUpdateLog order by InvStkLastSync Desc";
                   LastSync = fncExecuteScalar(sQry);
                   // LastSync="00.00.00";
                    break;
                case 1:
                    sQry="select [LastSync] from tblSqliteInvoiceLog order by LastSync Desc";
//                    LastSync = fncExecuteScalar(sQry);
                    LastSync="00.00.00";
                    break;
                case 2:
                    sQry="select [LastSync] from tblSqliteReceiptLog order by LastSync Desc";
//                    LastSync = fncExecuteScalar(sQry);
                    LastSync="00.00.00";
                    break;
                default:
                    break;
            }
            map.put("Details", TableUpload.get(i));
            map.put("Process", "Upload");
            map.put("LastSync", LastSync);
            Log.w("TableList",""+map);
            TableList.add(map);
        }
        mListAdapter=new DBTransferListAdapter(getApplicationContext(), new CustomButtonClick() {
            @Override
            public void onSynClick(View v, HashMap<String, String> map, String opr) {
                String sData=map.get("Details");
                Toast.makeText(getApplicationContext(),sData,Toast.LENGTH_SHORT).show();

                    WsCallSyncUploadTable wsCallSyncUploadTable = new WsCallSyncUploadTable();
                    wsCallSyncUploadTable.TableName = sData;
                    wsCallSyncUploadTable.execute();


            }
        });
        mListAdapter.setData(TableList);
        lstTable.setAdapter(mListAdapter);
        setListViewHeightBasedOnChildren(lstTable);
    }

//    public String fncStandAloneStockUpdate(String sData){
//
//        String sQry="";
//
//        sQry="Insert into tblInventoryStockupdatefromPDA (InventoryCode,PreviousQOH,NewQOH,CreateUser,CreateDate) values ("+f+)";
//
//        return null;
//    }

    public String fncExecuteScalar(String sQry) {

        try {
            Cursor cursor = mDb.rawQuery(sQry, null);
            cursor.moveToNext();
            if(cursor.getCount()>0){
                String val = cursor.getString(0);
                cursor.close();
                return val;
            }
            else {
                return "00:00:00";
            }
        }catch (Exception e) {
            Log.e("Exception", e.toString());
            return "00:00:00";

        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private class WsCallSyncDownloadTable extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(DBTransferActivity.this, ProgressDialog.THEME_HOLO_DARK);
        public String TableName;
        String sResp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Downloading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            switch (TableName){
                case "User Details":
                    sResp=fncSyncUserDetails();
                    break;
                case "Customer Details":
                    sResp=fncSyncCustomerDetails();
                    break;
                case "Inventory Details":
                    sResp=fncSyncInventoryDetails();
                    break;
//                case "Sales Invoice":
//                    sResp=fncSyncInvoiceDetails();
//                    break;
                default:
                    break;
            }
            return sResp;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            super.onPostExecute(sResponse);
            try {
                if (sResp.equalsIgnoreCase("true")) {
                    Toast.makeText(getApplicationContext(), "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                    if (rbtnUpload.isChecked()) {
                        fncLoadUploadDetails();
                    } else {
                        fncLoadDownloadDetails();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), sResp, Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private class WsCallSyncUploadTable extends AsyncTask<String,String,String>{
        private final ProgressDialog dialog = new ProgressDialog(DBTransferActivity.this, ProgressDialog.THEME_HOLO_DARK);
        public String TableName;
        String sResp,sQry="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Uploading Data...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            switch (TableName){
                case "Stock Update":
                    sQry="Select * from tblInventoryStockupdatefromPDA";
                    Cursor crHdr=mDb.rawQuery(sQry,null);
                    if(crHdr.getCount()>0) {
                      //  sResp = fncUpdateSalesOrder();
                        sResp = fncUpdateStockDetails();
                    }else {
                        sResp="No Item to Upload";
                    }
                    break;
//                case "Collection Entry":
//                    sQry="Select * from TBLCOLLECTIONENTRYHEADER";
//                    crHdr=mDb.rawQuery(sQry,null);
//                    if(crHdr.getCount()>0) {
//                        sResp = fncUpdateCollectionEntry();
//                    }else {
//                        sResp="No Item to Upload";
//                    }
//                    break;
                default:
                    sResp="No Item Found";
                    break;
            }
            return sResp;
        }
        @Override
        protected void onPostExecute(String sResponse) {
            super.onPostExecute(sResponse);
            try {
                String[] Response=sResponse.split(",");
                if(sResponse.startsWith("true")){
             //   if (sResp.equalsIgnoreCase("true")) {
                    Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    if (rbtnUpload.isChecked()) {
                        fncLoadUploadDetails();
                    } else {
                        fncLoadDownloadDetails();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), sResp, Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String fncUpdateCollectionEntry() {
        try{
            String sQry="",sResponse="",WebMethod="fncSaveCollectionEntryBulk";
            JSONArray jsArray=new JSONArray();
            sQry="Select * from TBLCOLLECTIONENTRYHEADER";
            Cursor crHdr=mDb.rawQuery(sQry,null);
            int mCount=crHdr.getCount();
            if(mCount>0){
                crHdr.moveToFirst();
                for (int i=0;i<mCount;i++){
                    jsArray=new JSONArray();
                    JSONObject jsObj=new JSONObject();
                    String sEntryNo=crHdr.getString(crHdr.getColumnIndex("ENTRYNO")).trim();
                    jsObj.put("ENTRYNO",crHdr.getString(crHdr.getColumnIndex("ENTRYNO")));
                    jsObj.put("ENTRYDATE",crHdr.getString(crHdr.getColumnIndex("ENTRYDATE")));
                    jsObj.put("CUSTOMERCODE",crHdr.getString(crHdr.getColumnIndex("CUSTOMERCODE")));
                    jsObj.put("CUSTOMERNAME",crHdr.getString(crHdr.getColumnIndex("CUSTOMERNAME")));
                    jsObj.put("SALESMANID",crHdr.getString(crHdr.getColumnIndex("SALESMANID")));
                    jsObj.put("SALESMANNAME",crHdr.getString(crHdr.getColumnIndex("SALESMANNAME")));
                    jsObj.put("TOTALAMOUNT",crHdr.getString(crHdr.getColumnIndex("TOTALAMOUNT")));
                    jsObj.put("REMARKS",crHdr.getString(crHdr.getColumnIndex("REMARKS")));
                    jsObj.put("SUBCOMPANYCODE",crHdr.getString(crHdr.getColumnIndex("SUBCOMPANYCODE")));
                    jsObj.put("DEVIDEID",crHdr.getString(crHdr.getColumnIndex("DEVIDEID")));
                    jsObj.put("CREATEDBY",crHdr.getString(crHdr.getColumnIndex("CREATEDBY")));
                    jsObj.put("CREATEDON",crHdr.getString(crHdr.getColumnIndex("CREATEDON")));
                    jsObj.put("MODIFIEDBY",crHdr.getString(crHdr.getColumnIndex("MODIFIEDBY")));
                    jsObj.put("MODIFIEDON",crHdr.getString(crHdr.getColumnIndex("MODIFIEDON")));
                    sQry="Select * from TBLCOLLECTIONENTRYDETAIL where ENTRYNO="+fncFormatSQLText(sEntryNo);
                    Cursor crDetail=mDb.rawQuery(sQry,null);
                    JSONArray jsDetail=new JSONArray();
                    int crCount=crDetail.getCount();
                    if(crCount>0){
                        crDetail.moveToFirst();
                        for(int k=0;k<crCount;k++){
                            JSONObject jsObjDetail=new JSONObject();
                            jsObjDetail.put("ENTRYNO",crDetail.getString(crDetail.getColumnIndex("ENTRYNO")));
                            jsObjDetail.put("ENTRYDATE",crDetail.getString(crDetail.getColumnIndex("ENTRYDATE")));
                            jsObjDetail.put("CUSTOMERCODE",crDetail.getString(crDetail.getColumnIndex("CUSTOMERCODE")));
                            jsObjDetail.put("CUSTOMERNAME",crDetail.getString(crDetail.getColumnIndex("CUSTOMERNAME")));
                            jsObjDetail.put("SALESMANID",crDetail.getString(crDetail.getColumnIndex("SALESMANID")));
                            jsObjDetail.put("SALESMANNAME",crDetail.getString(crDetail.getColumnIndex("SALESMANNAME")));
                            jsObjDetail.put("INVOICENO",crDetail.getString(crDetail.getColumnIndex("INVOICENO")));
                            jsObjDetail.put("INVOICEDATE",crDetail.getString(crDetail.getColumnIndex("INVOICEDATE")));
                            jsObjDetail.put("PAYMODE",crDetail.getString(crDetail.getColumnIndex("PAYMODE")));
                            jsObjDetail.put("CHEQUENO",crDetail.getString(crDetail.getColumnIndex("CHEQUENO")));
                            jsObjDetail.put("CHEQUEDATE",crDetail.getString(crDetail.getColumnIndex("CHEQUEDATE")));
                            jsObjDetail.put("BANK",crDetail.getString(crDetail.getColumnIndex("BANK")));
                            jsObjDetail.put("REFNO",crDetail.getString(crDetail.getColumnIndex("REFNO")));
                            jsObjDetail.put("TOTALAMOUNT",crDetail.getString(crDetail.getColumnIndex("TOTALAMOUNT")));
                            jsObjDetail.put("REMARKS",crDetail.getString(crDetail.getColumnIndex("REMARKS")));
                            jsObjDetail.put("SUBCOMPANYCODE",crDetail.getString(crDetail.getColumnIndex("SUBCOMPANYCODE")));
                            jsObjDetail.put("DEVICEID",crDetail.getString(crDetail.getColumnIndex("DEVICEID")));
                            jsObjDetail.put("CREATEDBY",crDetail.getString(crDetail.getColumnIndex("CREATEDBY")));
                            jsObjDetail.put("CREATEDON",crDetail.getString(crDetail.getColumnIndex("CREATEDON")));
                            jsObjDetail.put("MODIFIEDBY",crDetail.getString(crDetail.getColumnIndex("MODIFIEDBY")));
                            jsObjDetail.put("MODIFIEDON",crDetail.getString(crDetail.getColumnIndex("MODIFIEDON")));
                            jsDetail.put(jsObjDetail);
                            crDetail.moveToNext();
                        }
                    }
                    jsObj.put("sDetail",jsDetail.toString());
                    jsArray.put(jsObj);
                    sResponse=soapConn.fncUploadCollection(jsArray.toString(),Uname,Loc_Code,WebMethod, baseUrl);
                    if(sResponse.equalsIgnoreCase("true")){
                        mDb.execSQL("Delete from TBLCOLLECTIONENTRYHEADER where ENTRYNO="+fncFormatSQLText(sEntryNo));
                        mDb.execSQL("Delete from TBLCOLLECTIONENTRYDETAIL where ENTRYNO="+fncFormatSQLText(sEntryNo));
                    }else {
                        return sResponse;
                    }
                    crHdr.moveToNext();
                }
                return "true";
            }else {
                return "false,No Item Found";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "false,"+e.toString();
        }
    }
    private String fncUpdateStockDetails(){
        try{
            String sQry="",sResponse="",WebMethod="UploadfncStockUpdate";
            JSONArray jsonArray=null;
            JSONArray jsArray=null;
            sQry="select * from tblInventoryStockupdatefromPDA";
            Cursor crInvStk = mDb.rawQuery(sQry,null);
            int mCount=crInvStk.getCount();
            if(mCount>0){
                crInvStk.moveToFirst();
                for (int i=0;i<mCount;i++) {
                    jsonArray = new JSONArray();
                    JSONObject jsObj = new JSONObject();
                    String InvCode = crInvStk.getString(crInvStk.getColumnIndex("InventoryCode"));
                    String NQty = crInvStk.getString(crInvStk.getColumnIndex("NewQOH"));
                    String OQty = crInvStk.getString(crInvStk.getColumnIndex("PreviousQOH"));
                    String username = crInvStk.getString(crInvStk.getColumnIndex("CreateUser"));
                    String createdate = crInvStk.getString(crInvStk.getColumnIndex("CreateDate"));

                    sResponse = soapConn.fncUploadStockDetails(InvCode,NQty,OQty, Uname,createdate, Loc_Code, WebMethod, baseUrl);
                    if (sResponse.startsWith("true")) {
                        mDb.execSQL("Delete from tblInventoryStockupdatefromPDA where InventoryCode=" + fncFormatSQLText(InvCode));
                    } else {
                        return sResponse;
                    }
                    crInvStk.moveToNext();
                }
                if(sResponse.startsWith("true")){
                    sQry="delete from tblStockUpdateLog";
                    mDb.execSQL(sQry);
                    sQry="Insert into tblStockUpdateLog (InvStkLastSync) values ("+fncFormatSQLText(fncFormatSQLDate())+")";
                    mDb.execSQL(sQry);
                    mDb.execSQL("Update tblStockUpdateLog set [InvStkLastSync]=" + fncFormatSQLText(fncFormatSQLDate()));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "true";
    }

    private String fncUpdateSalesOrder() {
        try{
            String sQry="",sResponse="",WebMethod="fncSaveSoOrderBulk";
            JSONArray jsArray=null;
            sQry="Select * from tblSOHeader";
            Cursor crHdr=mDb.rawQuery(sQry,null);
            int mCount=crHdr.getCount();
            if(mCount>0){
                crHdr.moveToFirst();
                for (int i=0;i<mCount;i++){
                    jsArray=new JSONArray();
                    JSONObject jsObj=new JSONObject();
                    String sSoNo=crHdr.getString(crHdr.getColumnIndex("SoNo")).trim();
                    jsObj.put("SoNo",crHdr.getString(crHdr.getColumnIndex("SoNo")));
                    jsObj.put("SoType",crHdr.getString(crHdr.getColumnIndex("SoType")));
                    jsObj.put("SoVersion",crHdr.getString(crHdr.getColumnIndex("SoVersion")));
                    jsObj.put("InvoiceExpect",crHdr.getString(crHdr.getColumnIndex("InvoiceExpect")));
                    jsObj.put("CustomerCode",crHdr.getString(crHdr.getColumnIndex("CustomerCode")));
                    jsObj.put("CustomerName",crHdr.getString(crHdr.getColumnIndex("CustomerName")));
                    jsObj.put("SoDate",crHdr.getString(crHdr.getColumnIndex("SoDate")));
                    jsObj.put("InvoiceNo",crHdr.getString(crHdr.getColumnIndex("InvoiceNo")));
                    jsObj.put("InvoiceDueDate",crHdr.getString(crHdr.getColumnIndex("InvoiceDueDate")));
                    jsObj.put("DeliveryDate",crHdr.getString(crHdr.getColumnIndex("DeliveryDate")));
                    jsObj.put("CurrCode",crHdr.getString(crHdr.getColumnIndex("CurrCode")));
                    jsObj.put("CurrRate",crHdr.getString(crHdr.getColumnIndex("CurrRate")));
                    jsObj.put("ShipCode",crHdr.getString(crHdr.getColumnIndex("ShipCode")));
                    jsObj.put("Description",crHdr.getString(crHdr.getColumnIndex("Description")));
                    jsObj.put("soTotalValue",crHdr.getString(crHdr.getColumnIndex("soTotalValue")));
                    jsObj.put("sODiscPerc1",crHdr.getString(crHdr.getColumnIndex("sODiscPerc1")));
                    jsObj.put("sODiscPerc3",crHdr.getString(crHdr.getColumnIndex("sODiscPerc3")));
                    jsObj.put("sODiscPerc2",crHdr.getString(crHdr.getColumnIndex("sODiscPerc2")));
                    jsObj.put("sODiscPerc4",crHdr.getString(crHdr.getColumnIndex("sODiscPerc4")));
                    jsObj.put("sODiscAmount",crHdr.getString(crHdr.getColumnIndex("sODiscAmount")));
                    jsObj.put("sOSubTotal",crHdr.getString(crHdr.getColumnIndex("sOSubTotal")));
                    jsObj.put("Gst",crHdr.getString(crHdr.getColumnIndex("Gst")));
                    jsObj.put("Custom",crHdr.getString(crHdr.getColumnIndex("Custom")));
                    jsObj.put("Freight",crHdr.getString(crHdr.getColumnIndex("Freight")));
                    jsObj.put("Insurance",crHdr.getString(crHdr.getColumnIndex("Insurance")));
                    jsObj.put("Discount",crHdr.getString(crHdr.getColumnIndex("Discount")));
                    jsObj.put("Others",crHdr.getString(crHdr.getColumnIndex("Others")));
                    jsObj.put("soNetValue",crHdr.getString(crHdr.getColumnIndex("soNetValue")));
                    jsObj.put("Cancelled",crHdr.getString(crHdr.getColumnIndex("Cancelled")));
                    jsObj.put("Printed",crHdr.getString(crHdr.getColumnIndex("Printed")));
                    jsObj.put("NoCopies",crHdr.getString(crHdr.getColumnIndex("NoCopies")));
                    jsObj.put("Faxed",crHdr.getString(crHdr.getColumnIndex("Faxed")));
                    jsObj.put("Email",crHdr.getString(crHdr.getColumnIndex("Email")));
                    jsObj.put("ApprovedBy",crHdr.getString(crHdr.getColumnIndex("ApprovedBy")));
                    jsObj.put("ApprovedDate",crHdr.getString(crHdr.getColumnIndex("ApprovedDate")));
                    jsObj.put("Status",crHdr.getString(crHdr.getColumnIndex("Status")));
                    jsObj.put("soAtoDate",crHdr.getString(crHdr.getColumnIndex("soAtoDate")));
                    jsObj.put("CompanyCode",crHdr.getString(crHdr.getColumnIndex("CompanyCode")));
                    jsObj.put("Attn",crHdr.getString(crHdr.getColumnIndex("Attn")));
                    jsObj.put("soReqNo",crHdr.getString(crHdr.getColumnIndex("soReqNo")));
                    jsObj.put("PendingItem",crHdr.getString(crHdr.getColumnIndex("PendingItem")));
                    jsObj.put("CreatedBy",crHdr.getString(crHdr.getColumnIndex("CreatedBy")));
                    jsObj.put("CreatedDate",crHdr.getString(crHdr.getColumnIndex("CreatedDate")));
                    jsObj.put("ModifiedBy",crHdr.getString(crHdr.getColumnIndex("ModifiedBy")));
                    jsObj.put("ModifiedDate",crHdr.getString(crHdr.getColumnIndex("ModifiedDate")));
                    jsObj.put("AccDeptCode",crHdr.getString(crHdr.getColumnIndex("AccDeptCode")));
                    jsObj.put("AccDeptName",crHdr.getString(crHdr.getColumnIndex("AccDeptName")));
                    jsObj.put("Address1",crHdr.getString(crHdr.getColumnIndex("Address1")));
                    jsObj.put("Address2",crHdr.getString(crHdr.getColumnIndex("Address2")));
                    jsObj.put("Address3",crHdr.getString(crHdr.getColumnIndex("Address3")));
                    jsObj.put("Phone",crHdr.getString(crHdr.getColumnIndex("Phone")));
                    jsObj.put("Fax",crHdr.getString(crHdr.getColumnIndex("Fax")));
                    jsObj.put("SalesManID",crHdr.getString(crHdr.getColumnIndex("SalesManID")));
                    jsObj.put("Terms",crHdr.getString(crHdr.getColumnIndex("Terms")));
                    jsObj.put("ShippingSlNo",crHdr.getString(crHdr.getColumnIndex("ShippingSlNo")));
                    jsObj.put("ShippingTo",crHdr.getString(crHdr.getColumnIndex("ShippingTo")));
                    jsObj.put("ShippingAddr1",crHdr.getString(crHdr.getColumnIndex("ShippingAddr1")));
                    jsObj.put("ShippingAddr2",crHdr.getString(crHdr.getColumnIndex("ShippingAddr2")));
                    jsObj.put("ShippingPhone",crHdr.getString(crHdr.getColumnIndex("ShippingPhone")));
                    jsObj.put("ShippingFax",crHdr.getString(crHdr.getColumnIndex("ShippingFax")));
                    jsObj.put("sODiscPerc",crHdr.getString(crHdr.getColumnIndex("sODiscPerc")));
                    jsObj.put("TransportAmount",crHdr.getString(crHdr.getColumnIndex("TransportAmount")));
                    jsObj.put("GstType",crHdr.getString(crHdr.getColumnIndex("GstType")));
                    jsObj.put("PONo",crHdr.getString(crHdr.getColumnIndex("PONo")));
                    jsObj.put("Remarks",crHdr.getString(crHdr.getColumnIndex("Remarks")));
                    jsObj.put("LocInvoiceNo",crHdr.getString(crHdr.getColumnIndex("LocInvoiceNo")));
                    jsObj.put("SODiscount",crHdr.getString(crHdr.getColumnIndex("SODiscount")));
                    jsObj.put("GSTRoundOff",crHdr.getString(crHdr.getColumnIndex("GSTRoundOff")));
                    jsObj.put("ForeignCurrencyCode",crHdr.getString(crHdr.getColumnIndex("ForeignCurrencyCode")));
                    jsObj.put("ForeignCurrencyRate",crHdr.getString(crHdr.getColumnIndex("ForeignCurrencyRate")));
                    jsObj.put("FSoTotalValue",crHdr.getString(crHdr.getColumnIndex("FSoTotalValue")));
                    jsObj.put("FSoDiscAmount",crHdr.getString(crHdr.getColumnIndex("FSoDiscAmount")));
                    jsObj.put("FSoSubTotal",crHdr.getString(crHdr.getColumnIndex("FSoSubTotal")));
                    jsObj.put("FGst",crHdr.getString(crHdr.getColumnIndex("FGst")));
                    jsObj.put("FDiscount",crHdr.getString(crHdr.getColumnIndex("FDiscount")));
                    jsObj.put("FSoNetValue",crHdr.getString(crHdr.getColumnIndex("FSoNetValue")));
                    jsObj.put("FSoDiscount",crHdr.getString(crHdr.getColumnIndex("FSoDiscount")));
                    jsObj.put("FGSTRoundOff",crHdr.getString(crHdr.getColumnIndex("FGSTRoundOff")));
                    jsObj.put("sSubCompanyCode",crHdr.getString(crHdr.getColumnIndex("SubCompanyCode")));
                    Log.e("jsObj", jsObj.toString());
                    sQry="Select * from tblSODetail where SONO="+fncFormatSQLText(sSoNo);
                    Cursor crDetail=mDb.rawQuery(sQry,null);
                    JSONArray jsDetail=new JSONArray();
                    int crCount=crDetail.getCount();
                    if(crCount>0){
                        crDetail.moveToFirst();
                        for(int k=0;k<crCount;k++){
                            JSONObject jsObjDetail=new JSONObject();
                            jsObjDetail.put("SONO",crDetail.getString(crDetail.getColumnIndex("SONO")));
                            jsObjDetail.put("PoNo",crDetail.getString(crDetail.getColumnIndex("PoNo")));
                            jsObjDetail.put("ReleaseNumber",crDetail.getString(crDetail.getColumnIndex("ReleaseNumber")));
                            jsObjDetail.put("ItemCode",crDetail.getString(crDetail.getColumnIndex("ItemCode")));
                            jsObjDetail.put("ItemDesc",crDetail.getString(crDetail.getColumnIndex("ItemDesc")));
                            jsObjDetail.put("StoreCode",crDetail.getString(crDetail.getColumnIndex("StoreCode")));
                            jsObjDetail.put("PrcCode",crDetail.getString(crDetail.getColumnIndex("PrcCode")));
                            jsObjDetail.put("DeptCode",crDetail.getString(crDetail.getColumnIndex("DeptCode")));
                            jsObjDetail.put("Quotation",crDetail.getString(crDetail.getColumnIndex("Quotation")));
                            jsObjDetail.put("LUomCode",crDetail.getString(crDetail.getColumnIndex("LUomCode")));
                            jsObjDetail.put("WUomCode",crDetail.getString(crDetail.getColumnIndex("WUomCode")));
                            jsObjDetail.put("WQty",crDetail.getString(crDetail.getColumnIndex("WQty")));
                            jsObjDetail.put("LQty",crDetail.getString(crDetail.getColumnIndex("LQty")));
                            jsObjDetail.put("BaseUomCode",crDetail.getString(crDetail.getColumnIndex("BaseUomCode")));
                            jsObjDetail.put("BaseQty",crDetail.getString(crDetail.getColumnIndex("BaseQty")));
                            jsObjDetail.put("PoPrice",crDetail.getString(crDetail.getColumnIndex("PoPrice")));
                            jsObjDetail.put("UnitPrice",crDetail.getString(crDetail.getColumnIndex("UnitPrice")));
                            jsObjDetail.put("Disc1Perc",crDetail.getString(crDetail.getColumnIndex("Disc1Perc")));
                            jsObjDetail.put("Disc2Perc",crDetail.getString(crDetail.getColumnIndex("Disc2Perc")));
                            jsObjDetail.put("Disc3Perc",crDetail.getString(crDetail.getColumnIndex("Disc3Perc")));
                            jsObjDetail.put("Disc4Perc",crDetail.getString(crDetail.getColumnIndex("Disc4Perc")));
                            jsObjDetail.put("DiscAmount",crDetail.getString(crDetail.getColumnIndex("DiscAmount")));
                            jsObjDetail.put("Discount",crDetail.getString(crDetail.getColumnIndex("Discount")));
                            jsObjDetail.put("DiscUnitPrice",crDetail.getString(crDetail.getColumnIndex("DiscUnitPrice")));
                            jsObjDetail.put("TotalValue",crDetail.getString(crDetail.getColumnIndex("TotalValue")));
                            jsObjDetail.put("DeliveryDate",crDetail.getString(crDetail.getColumnIndex("DeliveryDate")));
                            jsObjDetail.put("Remarks",crDetail.getString(crDetail.getColumnIndex("Remarks")));
                            jsObjDetail.put("PoReqNo",crDetail.getString(crDetail.getColumnIndex("PoReqNo")));
                            jsObjDetail.put("DeliverQty",crDetail.getString(crDetail.getColumnIndex("DeliverQty")));
                            jsObjDetail.put("InvoiceQty",crDetail.getString(crDetail.getColumnIndex("InvoiceQty")));
                            jsObjDetail.put("AcctCode",crDetail.getString(crDetail.getColumnIndex("AcctCode")));
                            jsObjDetail.put("DrCostCentre",crDetail.getString(crDetail.getColumnIndex("DrCostCentre")));
                            jsObjDetail.put("DrGlAccount",crDetail.getString(crDetail.getColumnIndex("DrGlAccount")));
                            jsObjDetail.put("CrCostCentre",crDetail.getString(crDetail.getColumnIndex("CrCostCentre")));
                            jsObjDetail.put("CrGlAccount",crDetail.getString(crDetail.getColumnIndex("CrGlAccount")));
                            jsObjDetail.put("FocQty",crDetail.getString(crDetail.getColumnIndex("FocQty")));
                            jsObjDetail.put("OffSetWQty",crDetail.getString(crDetail.getColumnIndex("OffSetWQty")));
                            jsObjDetail.put("WUnit",crDetail.getString(crDetail.getColumnIndex("WUnit")));
                            jsObjDetail.put("CreatedBy",crDetail.getString(crDetail.getColumnIndex("CreatedBy")));
                            jsObjDetail.put("CreatedDate",crDetail.getString(crDetail.getColumnIndex("CreatedDate")));
                            jsObjDetail.put("ModifiedBy",crDetail.getString(crDetail.getColumnIndex("ModifiedBy")));
                            jsObjDetail.put("ModifiedDate",crDetail.getString(crDetail.getColumnIndex("ModifiedDate")));
                            jsObjDetail.put("DBaseQty",crDetail.getString(crDetail.getColumnIndex("DBaseQty")));
                            jsObjDetail.put("DFOCQty",crDetail.getString(crDetail.getColumnIndex("DFOCQty")));
                            jsObjDetail.put("GPCost",crDetail.getString(crDetail.getColumnIndex("GPCost")));
                            jsObjDetail.put("DBaseQty2",crDetail.getString(crDetail.getColumnIndex("DBaseQty2")));
                            jsObjDetail.put("DFocQTy2",crDetail.getString(crDetail.getColumnIndex("DFocQTy2")));
                            jsObjDetail.put("GSTPerc",crDetail.getString(crDetail.getColumnIndex("GSTPerc")));
                            jsObjDetail.put("GSTAmount",crDetail.getString(crDetail.getColumnIndex("GSTAmount")));
                            jsObjDetail.put("NetDiscount",crDetail.getString(crDetail.getColumnIndex("NetDiscount")));
                            jsObjDetail.put("GSTDisc",crDetail.getString(crDetail.getColumnIndex("GSTDisc")));
                            jsObjDetail.put("GSTRoundOff",crDetail.getString(crDetail.getColumnIndex("GSTRoundOff")));
                            jsObjDetail.put("NetPrice",crDetail.getString(crDetail.getColumnIndex("NetPrice")));
                            jsObjDetail.put("NetAmount",crDetail.getString(crDetail.getColumnIndex("NetAmount")));
                            jsObjDetail.put("ActualSales",crDetail.getString(crDetail.getColumnIndex("ActualSales")));
                            jsObjDetail.put("MAItemCode",crDetail.getString(crDetail.getColumnIndex("MAItemCode")));
                            jsObjDetail.put("MAPRCCode",crDetail.getString(crDetail.getColumnIndex("MAPRCCode")));
                            jsObjDetail.put("Origin",crDetail.getString(crDetail.getColumnIndex("Origin")));
                            jsObjDetail.put("MeasureUnit",crDetail.getString(crDetail.getColumnIndex("MeasureUnit")));
                            jsObjDetail.put("ForeignCurrencyCode",crDetail.getString(crDetail.getColumnIndex("ForeignCurrencyCode")));
                            jsObjDetail.put("ForeignCurrencyRate",crDetail.getString(crDetail.getColumnIndex("ForeignCurrencyRate")));
                            jsObjDetail.put("ForeignTotalValue",crDetail.getString(crDetail.getColumnIndex("ForeignTotalValue")));
                            jsObjDetail.put("FUnitPrice",crDetail.getString(crDetail.getColumnIndex("FUnitPrice")));
                            jsObjDetail.put("FGrossPrice",crDetail.getString(crDetail.getColumnIndex("FGrossPrice")));
                            jsObjDetail.put("FDiscUnitPrice",crDetail.getString(crDetail.getColumnIndex("FDiscUnitPrice")));
                            jsObjDetail.put("FDiscount",crDetail.getString(crDetail.getColumnIndex("FDiscount")));
                            jsObjDetail.put("FTotalValue",crDetail.getString(crDetail.getColumnIndex("FTotalValue")));
                            jsObjDetail.put("FGst",crDetail.getString(crDetail.getColumnIndex("FGst")));
                            jsObjDetail.put("FNetDiscount",crDetail.getString(crDetail.getColumnIndex("FNetDiscount")));
                            jsObjDetail.put("FNetCharges",crDetail.getString(crDetail.getColumnIndex("FNetCharges")));
                            jsObjDetail.put("FGSTDisc",crDetail.getString(crDetail.getColumnIndex("FGSTDisc")));
                            jsObjDetail.put("FGSTRoundOff",crDetail.getString(crDetail.getColumnIndex("FGSTRoundOff")));
                            jsObjDetail.put("FNetPrice",crDetail.getString(crDetail.getColumnIndex("FNetPrice")));
                            jsObjDetail.put("FNetAmount",crDetail.getString(crDetail.getColumnIndex("FNetAmount")));
                            jsObjDetail.put("FActualSales",crDetail.getString(crDetail.getColumnIndex("FActualSales")));
                            jsObjDetail.put("BarCode",crDetail.getString(crDetail.getColumnIndex("BarCode")));
                            jsObjDetail.put("InvWQty",crDetail.getString(crDetail.getColumnIndex("InvWQty")));
                            jsObjDetail.put("InvLQty",crDetail.getString(crDetail.getColumnIndex("InvLQty")));
                            jsObjDetail.put("InvFOCQty",crDetail.getString(crDetail.getColumnIndex("InvFOCQty")));
                            jsDetail.put(jsObjDetail);
                            crDetail.moveToNext();
                        }
                    }
                    jsObj.put("sDetail",jsDetail.toString());
                    jsArray.put(jsObj);
                    Log.e("Header",jsArray.toString());
                    sResponse=soapConn.fncUploadCollection(jsArray.toString(),Uname,Loc_Code,WebMethod, baseUrl);
                    if(sResponse.equalsIgnoreCase("true")){
                        mDb.execSQL("Delete from tblSOHeader where SoNo="+fncFormatSQLText(sSoNo));
                        mDb.execSQL("Delete from tblSODetail where SONO="+fncFormatSQLText(sSoNo));
                    }else {
                        return sResponse;
                    }
                }
                return "true";
            }else {
                return "false,No Item Found";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "false,"+e.toString();
        }
    }
    private String fncSyncInvoiceDetails() {
        try {
            String sQry = "",Webmethod="",sResp="";
            JSONArray jsonArray=null;
            JSONObject jobj=null;
            Webmethod = "fncGetInvoiceDetailList";
            sResp = soapConn.fncGetInventoryList(Loc_Code, Uname, Webmethod, baseUrl);
            jsonArray = new JSONArray(sResp);
            //
            JSONObject jsObj=jsonArray.getJSONObject( 0 );
            String sHeader=jsObj.getString("sHeader");
            String sDetail=jsObj.getString("sDetail");

            JSONArray jsArrayDetail=new JSONArray(sDetail);
            JSONArray jsArrayHeader=new JSONArray(sHeader);
            int mCountHdr=jsArrayHeader.length();
            int mCountDet=jsArrayDetail.length();
            //
            try {
                if (jsonArray.length() > 0) {
                    mDb.execSQL( "Delete from tblARInvoiceHeader" );
                    mDb.execSQL( "Delete from tblARInvoiceDetail" );
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            for (int i = 0; i < mCountHdr; i++) {
                jobj = jsArrayHeader.getJSONObject(i);
                sQry="Insert into tblARInvoiceHeader ([InvoiceNo],[InvoiceDate],[AccDeptCode],[AccDeptName],[TotalValue],[Gst],[DetailDiscount],[NetDiscount],[Discount],[DiscountType],[NetTotal],[CashierID],[LocationCode],[TerminalCode],[TotalLines],[TenderAmount],[Balance],[SalesType],[CustomerCode],[CustomerName],[Address1],[Address2],[Address3],[Phone],[Fax],[MemberDiscount],[GstType],[PaidStatus],[ChequeNo],[ChequeDate],[SoNo],[AccFlag],[SalesManID],[CommPerc],[PONo],[Verified],[VerifiedBy],[VerifiedDate],[InvoiceType],[Remarks],[Delivered],[PikLstPrint],[BCPRint],[Printed],[Terms],[ShippingSlNo],[ShippingTo],[ShippingAddr1],[ShippingAddr2],[ShippingPhone],[ShippingFax],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate],[NetDiscountPerc],[TransportAmount],[BCDelayPrint],[MiscDescription],[MiscAmount],[DeliveryManID],[SubTotal],[GSTRoundOff],[TotalCharges],[GSTDiscount],[Cancelled],[SONetDiscount],[DeliveryDate],[ForeignCurrencyCode],[ForeignCurrencyRate],[FTotalValue],[FGst],[FDetailDiscount],[FNetDiscount],[FDiscount],[FNetTotal],[FSubTotal],[FGSTRoundOff],[FTotalCharges],[FGSTDiscount],[PriceGroupCode],[SubCompanyCode])Values("+fncFormatSQLText(jobj.getString("InvoiceNo"))+","+fncFormatSQLText(jobj.getString("InvoiceDate"))+","+fncFormatSQLText(jobj.getString("AccDeptCode"))+","+fncFormatSQLText(jobj.getString("AccDeptName"))+","+fncFormatSQLText(jobj.getString("TotalValue"))+","+fncFormatSQLText(jobj.getString("Gst"))+","+fncFormatSQLText(jobj.getString("DetailDiscount"))+","+fncFormatSQLText(jobj.getString("NetDiscount"))+","+fncFormatSQLText(jobj.getString("Discount"))+","+fncFormatSQLText(jobj.getString("DiscountType"))+","+fncFormatSQLText(jobj.getString("NetTotal"))+","+fncFormatSQLText(jobj.getString("CashierID"))+","+fncFormatSQLText(jobj.getString("LocationCode"))+","+fncFormatSQLText(jobj.getString("TerminalCode"))+","+fncFormatSQLText(jobj.getString("TotalLines"))+","+fncFormatSQLText(jobj.getString("TenderAmount"))+","+fncFormatSQLText(jobj.getString("Balance"))+","+fncFormatSQLText(jobj.getString("SalesType"))+","+fncFormatSQLText(jobj.getString("CustomerCode"))+","+fncFormatSQLText(jobj.getString("CustomerName"))+","+fncFormatSQLText(jobj.getString("Address1"))+","+fncFormatSQLText(jobj.getString("Address2"))+","+fncFormatSQLText(jobj.getString("Address3"))+","+fncFormatSQLText(jobj.getString("Phone"))+","+fncFormatSQLText(jobj.getString("Fax"))+","+fncFormatSQLText(jobj.getString("MemberDiscount"))+","+fncFormatSQLText(jobj.getString("GstType"))+","+fncFormatSQLText(jobj.getString("PaidStatus"))+","+fncFormatSQLText(jobj.getString("ChequeNo"))+","+fncFormatSQLText(jobj.getString("ChequeDate"))+","+fncFormatSQLText(jobj.getString("SoNo"))+","+fncFormatSQLText(jobj.getString("AccFlag"))+","+fncFormatSQLText(jobj.getString("SalesManID"))+","+fncFormatSQLText(jobj.getString("CommPerc"))+","+fncFormatSQLText(jobj.getString("PONo"))+","+fncFormatSQLText(jobj.getString("Verified"))+","+fncFormatSQLText(jobj.getString("VerifiedBy"))+","+fncFormatSQLText(jobj.getString("VerifiedDate"))+","+fncFormatSQLText(jobj.getString("InvoiceType"))+","+fncFormatSQLText(jobj.getString("Remarks"))+","+fncFormatSQLText(jobj.getString("Delivered"))+","+fncFormatSQLText(jobj.getString("PikLstPrint"))+","+fncFormatSQLText(jobj.getString("BCPRint"))+","+fncFormatSQLText(jobj.getString("Printed"))+","+fncFormatSQLText(jobj.getString("Terms"))+","+fncFormatSQLText(jobj.getString("ShippingSlNo"))+","+fncFormatSQLText(jobj.getString("ShippingTo"))+","+fncFormatSQLText(jobj.getString("ShippingAddr1"))+","+fncFormatSQLText(jobj.getString("ShippingAddr2"))+","+fncFormatSQLText(jobj.getString("ShippingPhone"))+","+fncFormatSQLText(jobj.getString("ShippingFax"))+","+fncFormatSQLText(jobj.getString("CreateUser"))+","+fncFormatSQLText(jobj.getString("CreateDate"))+","+fncFormatSQLText(jobj.getString("ModifyUser"))+","+fncFormatSQLText(jobj.getString("ModifyDate"))+","+fncFormatSQLText(jobj.getString("NetDiscountPerc"))+","+fncFormatSQLText(jobj.getString("TransportAmount"))+","+fncFormatSQLText(jobj.getString("BCDelayPrint"))+","+fncFormatSQLText(jobj.getString("MiscDescription"))+","+fncFormatSQLText(jobj.getString("MiscAmount"))+","+fncFormatSQLText(jobj.getString("DeliveryManID"))+","+fncFormatSQLText(jobj.getString("SubTotal"))+","+fncFormatSQLText(jobj.getString("GSTRoundOff"))+","+fncFormatSQLText(jobj.getString("TotalCharges"))+","+fncFormatSQLText(jobj.getString("GSTDiscount"))+","+fncFormatSQLText(jobj.getString("Cancelled"))+","+fncFormatSQLText(jobj.getString("SONetDiscount"))+","+fncFormatSQLText(jobj.getString("DeliveryDate"))+","+fncFormatSQLText(jobj.getString("ForeignCurrencyCode"))+","+fncFormatSQLText(jobj.getString("ForeignCurrencyRate"))+","+fncFormatSQLText(jobj.getString("FTotalValue"))+","+fncFormatSQLText(jobj.getString("FGst"))+","+fncFormatSQLText(jobj.getString("FDetailDiscount"))+","+fncFormatSQLText(jobj.getString("FNetDiscount"))+","+fncFormatSQLText(jobj.getString("FDiscount"))+","+fncFormatSQLText(jobj.getString("FNetTotal"))+","+fncFormatSQLText(jobj.getString("FSubTotal"))+","+fncFormatSQLText(jobj.getString("FGSTRoundOff"))+","+fncFormatSQLText(jobj.getString("FTotalCharges"))+","+fncFormatSQLText(jobj.getString("FGSTDiscount"))+","+fncFormatSQLText(jobj.getString("PriceGroupCode"))+","+fncFormatSQLText(jobj.getString("SubCompanyCode"))+")";
                Log.e("InvoiceHdr",sQry);
                mDb.execSQL(sQry);
            }

            for(int i=0;i<mCountDet;i++)
            {
                jobj=jsArrayDetail.getJSONObject( i );
                sQry="Insert into tblARInvoiceDetail ([InvoiceNo] ,[SlNo],[InvoiceSlNo],[InventoryCode],[Description],[LUOMCode],[WUOMCode],[WUnit],[PrcCode],[WQty],[LQty],[FOCQty],[UnitPrice],[GrossPrice],[DiscUnitPrice],[Discount],[TotalValue],[Disc1],[Disc2],[Disc3],[DiscAmount],[DBaseQty],[DFOCQty],[DBaseQty2],[DFOCQty2],[GSTPerc],[Gst]  ,[NetDiscount]  ,[NetCharges],[GSTDisc],[GSTRoundOff],[NetPrice],[NetAmount],[ActualSales],[AverageCost]  ,[SalesManId] ,[LocationCode]     ,[DeptCode] ,[CategoryCode] ,[BrandCode] ,[OutletPrice]  ,[MemberDiscount]  ,[BaseQty],[SalesByMonth],[SalesByYear],[ReturnByMonth],[ReturnByYear],[BalanceStock],[OutletStock],[RecomRetailPrice],[CreateUser] ,[CreateDate] ,[ModifyUser] ,[ModifyDate],[GPCost],[MRP],[MAItemCode]  ,[MAPRCCode],[MAMRP],[Origin],[MeasureUnit],[ForeignCurrencyCode] ,[ForeignCurrencyRate],[ForeignTotalValue],[FUnitPrice],[FGrossPrice],[FDiscUnitPrice]  ,[FDiscount]  ,[FTotalValue]  ,[FGst]  ,[FNetDiscount]  ,[FNetCharges]  ,[FGSTDisc],[FGSTRoundOff],[FNetPrice],[FNetAmount],[FActualSales],[BarCode],[DeliveredWQty],[DeliveredLQty],[DeliveredFOCQty]) Values("+fncFormatSQLText(jobj.getString("InvoiceNo"))+ ","+fncFormatSQLText(jobj.getString("SlNo"))+","+fncFormatSQLText(jobj.getString("InvoiceSlNo"))+","+fncFormatSQLText(jobj.getString("InventoryCode"))+","+fncFormatSQLText(jobj.getString("Description"))+","+fncFormatSQLText(jobj.getString("LUOMCode"))+","+fncFormatSQLText(jobj.getString("WUOMCode"))+","+fncFormatSQLText(jobj.getString("WUnit"))+","+fncFormatSQLText(jobj.getString("PrcCode"))+","+fncFormatSQLText(jobj.getString("WQty"))+","+fncFormatSQLText(jobj.getString("LQty"))+","+fncFormatSQLText(jobj.getString("FOCQty"))+","+fncFormatSQLText(jobj.getString("UnitPrice"))+","+fncFormatSQLText(jobj.getString("GrossPrice"))+","+fncFormatSQLText(jobj.getString("DiscUnitPrice"))+","+fncFormatSQLText(jobj.getString("Discount"))+","+fncFormatSQLText(jobj.getString("TotalValue"))+","+fncFormatSQLText(jobj.getString("Disc1"))+","+fncFormatSQLText(jobj.getString("Disc2"))+","+fncFormatSQLText(jobj.getString("Disc3"))+","+fncFormatSQLText(jobj.getString("DiscAmount"))+","+fncFormatSQLText(jobj.getString("DBaseQty"))+","+fncFormatSQLText(jobj.getString("DFOCQty"))+","+fncFormatSQLText(jobj.getString("DBaseQty2"))+
                        ","  +fncFormatSQLText(jobj.getString("DFOCQty2"))+","+fncFormatSQLText(jobj.getString("GSTPerc"))+","+fncFormatSQLText(jobj.getString("Gst"))+  ","+fncFormatSQLText(jobj.getString("NetDiscount"))+  ","+fncFormatSQLText(jobj.getString("NetCharges"))+","+fncFormatSQLText(jobj.getString("GSTDisc"))+","+fncFormatSQLText(jobj.getString("GSTRoundOff"))+","+fncFormatSQLText(jobj.getString("NetPrice"))+","+fncFormatSQLText(jobj.getString("NetAmount"))+","+fncFormatSQLText(jobj.getString("ActualSales"))+","+fncFormatSQLText(jobj.getString("AverageCost"))+  ","+fncFormatSQLText(jobj.getString("SalesManId"))+ ","+fncFormatSQLText(jobj.getString("LocationCode"))+     ","+fncFormatSQLText(jobj.getString("DeptCode"))+ ","+fncFormatSQLText(jobj.getString("CategoryCode"))+ ","+fncFormatSQLText(jobj.getString("BrandCode"))+ ","+fncFormatSQLText(jobj.getString("OutletPrice"))+  ","+fncFormatSQLText(jobj.getString("MemberDiscount"))+  ","+fncFormatSQLText(jobj.getString("BaseQty"))+","+fncFormatSQLText(jobj.getString("SalesByMonth"))+","+fncFormatSQLText(jobj.getString("SalesByYear"))+","+fncFormatSQLText(jobj.getString("ReturnByMonth"))+","+fncFormatSQLText(jobj.getString("ReturnByYear"))+","+fncFormatSQLText(jobj.getString("BalanceStock"))+","+fncFormatSQLText(jobj.getString("OutletStock"))+
                        ","   +fncFormatSQLText(jobj.getString("RecomRetailPrice"))+","+fncFormatSQLText(jobj.getString("CreateUser"))+ ","+fncFormatSQLText(jobj.getString("CreateDate"))+ ","+fncFormatSQLText(jobj.getString("ModifyUser"))+ ","+fncFormatSQLText(jobj.getString("ModifyDate"))+","+fncFormatSQLText(jobj.getString("GPCost"))+","+fncFormatSQLText(jobj.getString("MRP"))+","+fncFormatSQLText(jobj.getString("MAItemCode"))+  ","+fncFormatSQLText(jobj.getString("MAPRCCode"))+","+fncFormatSQLText(jobj.getString("MAMRP"))+","+fncFormatSQLText(jobj.getString("Origin"))+","+fncFormatSQLText(jobj.getString("MeasureUnit"))+","+fncFormatSQLText(jobj.getString("ForeignCurrencyCode"))+ ","+fncFormatSQLText(jobj.getString("ForeignCurrencyRate"))+","+fncFormatSQLText(jobj.getString("ForeignTotalValue"))+","+fncFormatSQLText(jobj.getString("FUnitPrice"))+","+fncFormatSQLText(jobj.getString("FGrossPrice"))+","+fncFormatSQLText(jobj.getString("FDiscUnitPrice"))+  ","+fncFormatSQLText(jobj.getString("FDiscount"))+  ","+fncFormatSQLText(jobj.getString("FTotalValue"))+  ","+fncFormatSQLText(jobj.getString("FGst"))+  ","+fncFormatSQLText(jobj.getString("FNetDiscount"))+  ","+fncFormatSQLText(jobj.getString("FNetCharges"))+  ","+fncFormatSQLText(jobj.getString("FGSTDisc"))+
                        "," +fncFormatSQLText(jobj.getString("FGSTRoundOff"))+","+fncFormatSQLText(jobj.getString("FNetPrice"))+","+fncFormatSQLText(jobj.getString("FNetAmount"))+","+fncFormatSQLText(jobj.getString("FActualSales"))+","+fncFormatSQLText(jobj.getString("BarCode"))+","+fncFormatSQLText(jobj.getString("DeliveredWQty"))+","+fncFormatSQLText(jobj.getString("DeliveredLQty"))+","+fncFormatSQLText(jobj.getString("DeliveredFOCQty"))+")";

                Log.e("Invoicdet",sQry);
                mDb.execSQL( sQry );
            }



        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error",e.toString());
        }
        return "Sales Invoice";
    }
    private String fncSyncInventoryDetails() {
        try {
            String sQry="",Webmethod="",sResp="";
            JSONObject jobj=null;
            JSONArray jsonArray=null;

            Webmethod="fncGetInventoryList";
            sResp=soapConn.fncGetInventoryList(Loc_Code, Uname, Webmethod, baseUrl);
            jsonArray=new JSONArray(sResp);
            if(jsonArray.length()>0)
                mDb.execSQL("Delete from tblVwInventory");
            for(int i=0;i<jsonArray.length();i++) {
                jobj = jsonArray.getJSONObject(i);
                sQry = "Insert Into tblVwInventory([InventoryCode],[Description],[UOM],[SellingPrice],[UnitCost],[RetailPrice],[QtyonHand],[WUnit],[LocationCode],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate]) Values (" +
                        fncFormatSQLText(jobj.getString("InventoryCode"))+","+fncFormatSQLText(jobj.getString("Description"))+","+fncFormatSQLText(jobj.getString("UOM"))+","+fncFormatSQLText(jobj.getString("SellingPrice"))+","+
                        fncFormatSQLText(jobj.getString("UnitCost"))+","+fncFormatSQLText(jobj.getString("RetailPrice"))+","+fncFormatSQLText(jobj.getString("QtyonHand"))+","+fncFormatSQLText(jobj.getString("WUnit"))+","+
                        fncFormatSQLText(jobj.getString("LocationCode"))+","+fncFormatSQLText(jobj.getString("CreateUser"))+","+fncFormatSQLText(jobj.getString("CreateDate"))+","+fncFormatSQLText(jobj.getString("ModifyUser"))+","+fncFormatSQLText(jobj.getString("ModifyDate"))+")";
                Log.e("Insert tblInventoryStock "+i,sQry);
                mDb.execSQL(sQry);
            }

            Webmethod="fncGetBarcodeList";
            sResp=soapConn.fncGetInventoryList(Loc_Code, Uname, Webmethod, baseUrl);
            jsonArray=new JSONArray(sResp);
            if(jsonArray.length()>0)
                mDb.execSQL("Delete from tblBarcode");
            for (int i=0;i<jsonArray.length();i++) {
                jobj=jsonArray.getJSONObject(i);
                sQry = "Insert into tblBarcode ([InventoryCode],[BarCode],[Description],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate])Values(" +fncFormatSQLText(jobj.getString("InventoryCode"))+","+
                        fncFormatSQLText(jobj.getString("BarCode"))+","+fncFormatSQLText(jobj.getString("Description"))+","+fncFormatSQLText(jobj.getString("CreateUser"))+","+fncFormatSQLText(jobj.getString("CreateDate"))+","+
                        fncFormatSQLText(jobj.getString("ModifyUser"))+","+fncFormatSQLText(jobj.getString("ModifyDate"))+ ")";
                Log.e("Insert tblInvBarcode "+i,sQry);
                mDb.execSQL(sQry);
            }

            sQry="select * from tblDownloadLog";
            Cursor curDwd = mDb.rawQuery(sQry,null);
            if(curDwd.getCount()>0){
                mDb.execSQL("Update tblDownloadLog set [InvLastSync]=" + fncFormatSQLText(fncFormatSQLDate()));
            }else{
            sQry="Insert into tblDownloadLog (InvLastSync) values ("+fncFormatSQLText(fncFormatSQLDate())+")";
            mDb.execSQL(sQry);}
          //
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.toString();
        }
        return "true";
    }
    private String fncSyncCustomerDetails() {
        try{
            String WebMethod="",sResp,sql="";
            JSONObject jObj=null;
            JSONArray jsonArray=null;
            WebMethod="fncGetCustomerList";
            sResp = soapConn.fncGetCompanyList(Uname, Loc_Code, WebMethod, baseUrl);
            jsonArray=new JSONArray(sResp);
            if(jsonArray.length()>0)
                mDb.execSQL("Delete from tblARCustomer");
            for (int i=0;i<jsonArray.length();i++) {
                jObj=jsonArray.getJSONObject(i);
                sql = "INSERT INTO tblARCustomer ([CustomerCode],[CustomerName],[Address1],[Address2],[Country],[Phone],[Fax],[ZipCode],[Email],[Remarks]) VALUES ("+fncFormatSQLText(jObj.getString("CustomerCode"))+","+
                        fncFormatSQLText(jObj.getString("CustomerName"))+","+fncFormatSQLText(jObj.getString("Address1"))+","+fncFormatSQLText(jObj.getString("Address2"))+","+fncFormatSQLText(jObj.getString("Country"))+","+
                        fncFormatSQLText(jObj.getString("Phone"))+","+fncFormatSQLText(jObj.getString("Fax"))+","+fncFormatSQLText(jObj.getString("ZipCode"))+","+fncFormatSQLText(jObj.getString("Email"))+","+fncFormatSQLText(jObj.getString("Remarks"))+ ")";
                mDb.execSQL(sql);
            }

            WebMethod = "fncGetCustomerShippingList";
            sResp = soapConn.fncGetCompanyList(Uname, Loc_Code, WebMethod, baseUrl);
            jsonArray = new JSONArray(sResp);
            if(jsonArray.length()>0)
                mDb.execSQL("Delete from tblARCustomerShippingAddress");
            for(int i=0;i<jsonArray.length();i++) {
                jObj=jsonArray.getJSONObject(i);
                //CustomerCode,ShippingName,ShippingCode,Address1,Address2,Country,Phone,Fax,ZipCode
                sql = "Insert into tblARCustomerShippingAddress ([CustomerCode],[SlNo],[ShippingName],[ShippingCode],[Address1],[Address2],[Country],[Phone],[Fax],[ZipCode]) Values (" +fncFormatSQLText(jObj.getString("CustomerCode"))+","+i+1+","+
                        fncFormatSQLText(jObj.getString("ShippingName"))+","+fncFormatSQLText(jObj.getString("ShippingCode"))+","+fncFormatSQLText(jObj.getString("Address1"))+","+fncFormatSQLText(jObj.getString("Address2"))+","+
                        fncFormatSQLText(jObj.getString("Country"))+","+fncFormatSQLText(jObj.getString("Phone"))+","+fncFormatSQLText(jObj.getString("Fax"))+","+fncFormatSQLText(jObj.getString("ZipCode"))+ ")";
                mDb.execSQL(sql);
            }
            Log.e("Download","Successfully");
            sql="select * from tblDownloadLog";
            Cursor curDwd = mDb.rawQuery(sql,null);
            if(curDwd.getCount()>0){
                mDb.execSQL("Update tblDownloadLog set [CustLastSync]=" + fncFormatSQLText(fncFormatSQLDate()));
            }else{
            sql="Insert into tblDownloadLog (CustLastSync) values ("+fncFormatSQLText(fncFormatSQLDate())+")";
            mDb.execSQL(sql);}
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "true";
    }

    private String fncSyncUserDetails() {
        try {
            String sResp="",sql="",WebMethod;
            int strLen;
            JSONArray jsonArray=null;
            JSONObject jObj=null;
            mDb.execSQL("Delete from tblAndroidDeviceConfig");
            WebMethod="fncGetDeviceList";
            sResp = soapConn.fncGetDeviceList(Uname,Loc_Code, WebMethod, baseUrl);
            jsonArray=new JSONArray(sResp);
            for(int i=0;i<jsonArray.length();i++) {
                jObj=jsonArray.getJSONObject(i);
                sql = "Insert Into tblAndroidDeviceConfig ([DeviceId],[DeviceName],[TerminalCode],[TerminalName],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate]) Values (" +fncFormatSQLText(jObj.getString("DeviceId"))+","+fncFormatSQLText(jObj.getString("DeviceName"))+","+fncFormatSQLText(jObj.getString("TerminalCode"))+","+fncFormatSQLText(jObj.getString("TerminalName"))+","+fncFormatSQLText(jObj.getString("CreateUser"))+","+fncFormatSQLText(jObj.getString("CreateDate"))+","+fncFormatSQLText(jObj.getString("ModifyUser"))+","+fncFormatSQLText(jObj.getString("ModifyDate"))+ ")";
                Log.w("tblAndroidDeviceConfig", sql);
                mDb.execSQL(sql);
            }
            mDb.execSQL("Delete from tblUserMaster");
            WebMethod="fncGetUserDetails";
            sResp = soapConn.fncGetCompanyList(Uname, Loc_Code, WebMethod, baseUrl);
            jsonArray=new JSONArray(sResp);
            for(int i=0;i<jsonArray.length();i++) {
                jObj=jsonArray.getJSONObject(i);
                sql = "Insert into tblUserMaster ([UserCode],[UserGroupCode],[UserName],[Password],[ExpiryDate],[Designation],[Email],[Phone],[Fax],[StoreCode],[POLimit],[Admin],[UpdateApprovedPo],[POSDiscount],[DayOfValid],[DaysOfWarning],[ChangePassword],[ChangeDate],[FirstLogDate],[LastLogDate],[UpdateGst],[Contact],[CreateDate],[CreateUser],[ModifyDate],[ModifyUser],[SInvPriceChange]) Values(" +fncFormatSQLText(jObj.getString("UserCode").trim())+","+
                        fncFormatSQLText(jObj.getString("UserGroupCode").trim())+","+fncFormatSQLText(jObj.getString("UserName").trim())+","+fncFormatSQLText(jObj.getString("Password").trim())+"," +fncFormatSQLText(jObj.getString("ExpiryDate").trim())+","+fncFormatSQLText(jObj.getString("Designation").trim())+","+fncFormatSQLText(jObj.getString("Email").trim())+","+fncFormatSQLText(jObj.getString("Phone").trim())+","+fncFormatSQLText(jObj.getString("Fax").trim())+","+fncFormatSQLText(jObj.getString("StoreCode").trim())+","+
                        fncFormatSQLText(jObj.getString("POLimit").trim())+","+fncFormatSQLText(jObj.getString("Admin").trim())+","+fncFormatSQLText(jObj.getString("UpdateApprovedPo").trim())+","+fncFormatSQLText(jObj.getString("POSDiscount"))+","+fncFormatSQLText(jObj.getString("DayOfValid"))+","+fncFormatSQLText(jObj.getString("DaysOfWarning"))+","+fncFormatSQLText(jObj.getString("ChangePassword"))+","+fncFormatSQLText(jObj.getString("ChangeDate"))+","+fncFormatSQLText(jObj.getString("FirstLogDate"))+","+fncFormatSQLText(jObj.getString("LastLogDate"))
                        +","+fncFormatSQLText(jObj.getString("UpdateGst"))+","+fncFormatSQLText(jObj.getString("Contact"))+","+fncFormatSQLText(jObj.getString("CreateDate"))+","+fncFormatSQLText(jObj.getString("CreateUser"))+","+fncFormatSQLText(jObj.getString("ModifyDate"))+","+fncFormatSQLText(jObj.getString("ModifyUser"))+","+fncFormatSQLText(jObj.getString("SInvPriceChange"))+")";
                mDb.execSQL(sql);
            }

            sql="Select * from tblCompany";
            Cursor crComp=mDb.rawQuery(sql,null);
            mDb.execSQL("Delete from tblCompany");
            if(crComp.getCount()==0) {

                WebMethod = "fncGetCompanyDetails";
                sResp = soapConn.fncGetCompanyList(Uname, Loc_Code, WebMethod, baseUrl);
                jsonArray = new JSONArray(sResp);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jObj = jsonArray.getJSONObject(i);
//                    sql = "Insert into tblCompany ([CompanyID],[Name],[Address1],[Address2],[City],[State],[Country],[PostalCode],[Phone],[Fax],[Email],[Url],[LocationCode],[MainLocationCode],[BRNNo],[GSTRegNo],[NextMailID],[NextPosNo],[NextInvoice],[GstPerc],[GstType],[DisplayMessage1],[DisplayMessage2],[DisplayComport],[NextPaymentVoucherNo],[NextPosCreditNo],[NextReciptNo],[NextAvilableRange],[NextTransNo]," +
//                            "[MaxDiscountAllowed],[MaxPriceChangeAllowed],[BarCodeCOMPort],[ExpiredTime],[NextPONo],[NextGIDNo],[NextAverageCostPercentage],[TaxIN],[NextTransferNo],[NextEODDate],[NextPurchaseno],[NextPurReNo],[NextDmgReNo],[SalesReturnRefNo],[TaxTaken],[HQTransNo],[BillPrint],[UseSalesQty],[Remarks],[CompanyShortCode],[POOffReqNo],[SalesReturnReqNo],[NextPendingNo],[NextStkTakeAdjNo]," +
//                            "[Version],[NextStkAdjNo],[NextCustSONo],[DistributionNo],[PRReqNo],[NextDmgStkTakeAdjNo],[FaxServer],[StockClosingDt],[NextSONo],[NextDONo],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate],[NextPCNo],[LastLogStkTakeDate],[NextMemPointAdjNo],[GrossProfit],[PCReqNo],[NextDGIDNo],[NextConsNo],[NextConsRetnNo],[NextGSTRefundNo],[NextPOSSettleNo],[POSVersion],[ExPensesNo],[NextECRNo]," +
//                            "[NextMarginAnalysedNo],[SRReqNo],[NextConReqNo],[NextTranHoldNo],[NextMemberNo],[NextPkgSalesNo],[NextDepositNo],[NextGIDHoldNo],[PurchaseGstType],[CreditSalesGstType],[NextPaymentNo],[NextCreditNoteNo],[NextDebitNoteNo],[NextReceiptNo],[NextPurInvoiceNo],[NextAppointmentNo],[NextToCreateNo],[NextTIAcceptNo],[UASACVersion],[EODDate],[PrintPRMInReceipt],[NextRepairNo],[VersionType]," +
//                            "[MainLocationVersionType],[NextSupplierInvoiceNo],[NextCustomerCreditNoteNo]) Values(" +fncFormatSQLText(jObj.getString("CompanyID").trim()) + "," + fncFormatSQLText(jObj.getString("Name").trim()) + "," + fncFormatSQLText(jObj.getString("Address1").trim()) + "," + fncFormatSQLText(jObj.getString("Address2").trim()) + "," + fncFormatSQLText(jObj.getString("City").trim()) + "," + fncFormatSQLText(jObj.getString("State").trim()) + "," + fncFormatSQLText(jObj.getString("Country").trim()) + "," + fncFormatSQLText(jObj.getString("PostalCode").trim()) + "," + fncFormatSQLText(jObj.getString("Phone").trim()) + "," + fncFormatSQLText(jObj.getString("Fax").trim()) + "," + fncFormatSQLText(jObj.getString("Email").trim()) + "," + fncFormatSQLText(jObj.getString("Url").trim()) + "," + fncFormatSQLText(jObj.getString("LocationCode").trim()) + "," + fncFormatSQLText(jObj.getString("MainLocationCode").trim()) + "," + fncFormatSQLText(jObj.getString("BRNNo").trim()) + "," + fncFormatSQLText(jObj.getString("GSTRegNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextMailID").trim()) + "," + fncFormatSQLText(jObj.getString("NextPosNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextInvoice").trim()) + "," + fncFormatSQLText(jObj.getString("GstPerc").trim()) + "," + fncFormatSQLText(jObj.getString("GstType").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayMessage1").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayMessage2").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayComport").trim()) + "," + fncFormatSQLText(jObj.getString("NextPaymentVoucherNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPosCreditNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextReciptNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextAvilableRange").trim()) + "," + fncFormatSQLText(jObj.getString("NextTransNo").trim()) + "," +
//                            fncFormatSQLText(jObj.getString("MaxDiscountAllowed").trim()) + "," + fncFormatSQLText(jObj.getString("MaxPriceChangeAllowed").trim()) + "," + fncFormatSQLText(jObj.getString("BarCodeCOMPort").trim()) + "," + fncFormatSQLText(jObj.getString("ExpiredTime").trim()) + "," + fncFormatSQLText(jObj.getString("NextPONo").trim()) + "," + fncFormatSQLText(jObj.getString("NextGIDNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextAverageCostPercentage").trim()) + "," + fncFormatSQLText(jObj.getString("TaxIN").trim()) + "," + fncFormatSQLText(jObj.getString("NextTransferNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextEODDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextPurchaseno").trim()) + "," + fncFormatSQLText(jObj.getString("NextPurReNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDmgReNo").trim()) + "," + fncFormatSQLText(jObj.getString("SalesReturnRefNo").trim()) + "," + fncFormatSQLText(jObj.getString("TaxTaken").trim()) + "," + fncFormatSQLText(jObj.getString("HQTransNo").trim()) + "," + fncFormatSQLText(jObj.getString("BillPrint").trim()) + "," + fncFormatSQLText(jObj.getString("UseSalesQty").trim()) + "," + fncFormatSQLText(jObj.getString("Remarks").trim()) + "," + fncFormatSQLText(jObj.getString("CompanyShortCode").trim()) + "," + fncFormatSQLText(jObj.getString("POOffReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("SalesReturnReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPendingNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextStkTakeAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("Version").trim()) + "," + fncFormatSQLText(jObj.getString("NextStkAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextCustSONo").trim()) + "," + fncFormatSQLText(jObj.getString("DistributionNo").trim()) + "," + fncFormatSQLText(jObj.getString("PRReqNo").trim()) + "," +
//                            fncFormatSQLText(jObj.getString("NextDmgStkTakeAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("FaxServer").trim()) + "," + fncFormatSQLText(jObj.getString("StockClosingDt").trim()) + "," + fncFormatSQLText(jObj.getString("NextSONo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDONo").trim()) + "," + fncFormatSQLText(jObj.getString("CreateUser").trim()) + "," + fncFormatSQLText(jObj.getString("CreateDate").trim()) + "," + fncFormatSQLText(jObj.getString("ModifyUser").trim()) + "," + fncFormatSQLText(jObj.getString("ModifyDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextPCNo").trim()) + "," + fncFormatSQLText(jObj.getString("LastLogStkTakeDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextMemPointAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("GrossProfit").trim()) + "," + fncFormatSQLText(jObj.getString("PCReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDGIDNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextConsNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextConsRetnNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextGSTRefundNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPOSSettleNo").trim()) + "," + fncFormatSQLText(jObj.getString("POSVersion").trim()) + "," + fncFormatSQLText(jObj.getString("ExPensesNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextECRNo").trim())+ "," +  fncFormatSQLText(jObj.getString("NextMarginAnalysedNo").trim())+ "," + fncFormatSQLText(jObj.getString("SRReqNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextConReqNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextTranHoldNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextMemberNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextPkgSalesNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextDepositNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextGIDHoldNo").trim())+ "," +
//                            fncFormatSQLText(jObj.getString("PurchaseGstType").trim())+ "," + fncFormatSQLText(jObj.getString("CreditSalesGstType").trim())+ "," + fncFormatSQLText(jObj.getString("NextPaymentNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextCreditNoteNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextDebitNoteNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextReceiptNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextPurInvoiceNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextAppointmentNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextToCreateNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextTIAcceptNo").trim())+ "," + fncFormatSQLText(jObj.getString("UASACVersion").trim())+ "," + fncFormatSQLText(jObj.getString("EODDate").trim())+ "," + fncFormatSQLText(jObj.getString("PrintPRMInReceipt").trim())+ "," + fncFormatSQLText(jObj.getString("NextRepairNo").trim())+ "," + fncFormatSQLText(jObj.getString("VersionType").trim())+ "," + fncFormatSQLText(jObj.getString("MainLocationVersionType").trim())+ "," + fncFormatSQLText(jObj.getString("NextSupplierInvoiceNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextCustomerCreditNoteNo").trim())+ ")";
//
                    sql = "Insert into tblCompany ([CompanyID],[Name],[Address1],[Address2],[City],[State],[Country],[PostalCode],[Phone],[Fax],[Email],[Url],[LocationCode],[MainLocationCode],[BRNNo],[GSTRegNo],[NextMailID],[NextPosNo],[NextInvoice],[GstPerc],[GstType],[DisplayMessage1],[DisplayMessage2],[DisplayComport],[NextPaymentVoucherNo],[NextPosCreditNo],[NextReciptNo],[NextAvilableRange],[NextTransNo]," +
                            "[MaxDiscountAllowed],[MaxPriceChangeAllowed],[BarCodeCOMPort],[ExpiredTime],[NextPONo],[NextGIDNo],[NextAverageCostPercentage],[TaxIN],[NextTransferNo],[NextEODDate],[NextPurchaseno],[NextPurReNo],[NextDmgReNo],[SalesReturnRefNo],[TaxTaken],[HQTransNo],[BillPrint],[UseSalesQty],[Remarks],[CompanyShortCode],[POOffReqNo],[SalesReturnReqNo],[NextPendingNo],[NextStkTakeAdjNo]," +
                            "[Version],[NextStkAdjNo],[NextCustSONo],[DistributionNo],[PRReqNo],[NextDmgStkTakeAdjNo],[FaxServer],[StockClosingDt],[NextSONo],[NextDONo],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate],[NextPCNo],[LastLogStkTakeDate],[NextMemPointAdjNo],[GrossProfit],[PCReqNo],[NextDGIDNo],[NextConsNo],[NextConsRetnNo],[NextGSTRefundNo],[NextPOSSettleNo],[POSVersion],[ExPensesNo],[NextECRNo]," +
                            "[NextMarginAnalysedNo],[SRReqNo],[NextConReqNo],[NextTranHoldNo],[NextMemberNo],[NextPkgSalesNo],[NextDepositNo],[NextPaymentNo],[NextCreditNoteNo],[NextDebitNoteNo],[NextReceiptNo],[NextAppointmentNo],[NextToCreateNo],[NextTIAcceptNo]," +
                            "[NextSupplierInvoiceNo],[NextCustomerCreditNoteNo]) Values(" +fncFormatSQLText(jObj.getString("CompanyID").trim()) + "," + fncFormatSQLText(jObj.getString("Name").trim()) + "," + fncFormatSQLText(jObj.getString("Address1").trim()) + "," + fncFormatSQLText(jObj.getString("Address2").trim()) + "," + fncFormatSQLText(jObj.getString("City").trim()) + "," + fncFormatSQLText(jObj.getString("State").trim()) + "," + fncFormatSQLText(jObj.getString("Country").trim()) + "," + fncFormatSQLText(jObj.getString("PostalCode").trim()) + "," + fncFormatSQLText(jObj.getString("Phone").trim()) + "," + fncFormatSQLText(jObj.getString("Fax").trim()) + "," + fncFormatSQLText(jObj.getString("Email").trim()) + "," + fncFormatSQLText(jObj.getString("Url").trim()) + "," + fncFormatSQLText(jObj.getString("LocationCode").trim()) + "," + fncFormatSQLText(jObj.getString("MainLocationCode").trim()) + "," + fncFormatSQLText(jObj.getString("BRNNo").trim()) + "," + fncFormatSQLText(jObj.getString("GSTRegNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextMailID").trim()) + "," + fncFormatSQLText(jObj.getString("NextPosNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextInvoice").trim()) + "," + fncFormatSQLText(jObj.getString("GstPerc").trim()) + "," + fncFormatSQLText(jObj.getString("GstType").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayMessage1").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayMessage2").trim()) + "," + fncFormatSQLText(jObj.getString("DisplayComport").trim()) + "," + fncFormatSQLText(jObj.getString("NextPaymentVoucherNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPosCreditNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextReciptNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextAvilableRange").trim()) + "," + fncFormatSQLText(jObj.getString("NextTransNo").trim()) + "," +
                            fncFormatSQLText(jObj.getString("MaxDiscountAllowed").trim()) + "," + fncFormatSQLText(jObj.getString("MaxPriceChangeAllowed").trim()) + "," + fncFormatSQLText(jObj.getString("BarCodeCOMPort").trim()) + "," + fncFormatSQLText(jObj.getString("ExpiredTime").trim()) + "," + fncFormatSQLText(jObj.getString("NextPONo").trim()) + "," + fncFormatSQLText(jObj.getString("NextGIDNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextAverageCostPercentage").trim()) + "," + fncFormatSQLText(jObj.getString("TaxIN").trim()) + "," + fncFormatSQLText(jObj.getString("NextTransferNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextEODDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextPurchaseno").trim()) + "," + fncFormatSQLText(jObj.getString("NextPurReNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDmgReNo").trim()) + "," + fncFormatSQLText(jObj.getString("SalesReturnRefNo").trim()) + "," + fncFormatSQLText(jObj.getString("TaxTaken").trim()) + "," + fncFormatSQLText(jObj.getString("HQTransNo").trim()) + "," + fncFormatSQLText(jObj.getString("BillPrint").trim()) + "," + fncFormatSQLText(jObj.getString("UseSalesQty").trim()) + "," + fncFormatSQLText(jObj.getString("Remarks").trim()) + "," + fncFormatSQLText(jObj.getString("CompanyShortCode").trim()) + "," + fncFormatSQLText(jObj.getString("POOffReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("SalesReturnReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPendingNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextStkTakeAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("Version").trim()) + "," + fncFormatSQLText(jObj.getString("NextStkAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextCustSONo").trim()) + "," + fncFormatSQLText(jObj.getString("DistributionNo").trim()) + "," + fncFormatSQLText(jObj.getString("PRReqNo").trim()) + "," +
                            fncFormatSQLText(jObj.getString("NextDmgStkTakeAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("FaxServer").trim()) + "," + fncFormatSQLText(jObj.getString("StockClosingDt").trim()) + "," + fncFormatSQLText(jObj.getString("NextSONo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDONo").trim()) + "," + fncFormatSQLText(jObj.getString("CreateUser").trim()) + "," + fncFormatSQLText(jObj.getString("CreateDate").trim()) + "," + fncFormatSQLText(jObj.getString("ModifyUser").trim()) + "," + fncFormatSQLText(jObj.getString("ModifyDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextPCNo").trim()) + "," + fncFormatSQLText(jObj.getString("LastLogStkTakeDate").trim()) + "," + fncFormatSQLText(jObj.getString("NextMemPointAdjNo").trim()) + "," + fncFormatSQLText(jObj.getString("GrossProfit").trim()) + "," + fncFormatSQLText(jObj.getString("PCReqNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextDGIDNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextConsNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextConsRetnNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextGSTRefundNo").trim()) + "," + fncFormatSQLText(jObj.getString("NextPOSSettleNo").trim()) + "," + fncFormatSQLText(jObj.getString("POSVersion").trim()) + "," + fncFormatSQLText(jObj.getString("ExPensesNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextECRNo").trim())+ "," +  fncFormatSQLText(jObj.getString("NextMarginAnalysedNo").trim())+ "," + fncFormatSQLText(jObj.getString("SRReqNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextConReqNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextTranHoldNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextMemberNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextPkgSalesNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextDepositNo").trim())+ "," +
                            fncFormatSQLText(jObj.getString("NextPaymentNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextCreditNoteNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextDebitNoteNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextReceiptNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextAppointmentNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextToCreateNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextTIAcceptNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextSupplierInvoiceNo").trim())+ "," + fncFormatSQLText(jObj.getString("NextCustomerCreditNoteNo").trim())+ ")";
                    mDb.execSQL(sql);
                }
            }
//            mDb.execSQL("Delete from tblSubCompany");
//            WebMethod="fncGetSubCompanyDetails";
//            sResp = soapConn.fncGetCompanyList(Uname, Loc_Code, WebMethod, baseUrl);
//            jsonArray=new JSONArray(sResp);
//            for(int i=0;i<jsonArray.length();i++) {
//                jObj=jsonArray.getJSONObject(i);
//                sql = "Insert into tblSubCompany ([SubCompanyCode], [Name],[Address1],[Address2],[Address3],[Country],[ZipCode],[Phone],[Fax],[Email],[Url],[NextPONo],[NextGIDNo],[NextSONo],[NextDONo],[NextInvoiceNo],[NextCreditInvoiceNo],[NextPaymentVoucherNo],[NextReceiptNo],[NextTransferNo],[NextPurReturnNo],[NextSalesReturnNo],[MaxDiscountAllowed],[MaxPriceChangeAllowd],[NextCustomerCreditNoteNo],[NextCustomerDebitNoteNo],[NextCustomerOpeningCNNo],[NextCustomerOpeningDNNo],[NextSupplierInvoiceNo],[NextSupplierCreditNoteNo]," +
//                      "[NextSupplierDebitNoteNo],[NextSupplierOpeningCNNo],[NextSupplierOpeningDNNo],[NextSupplierReceiptNo],[SubCompanyShortCode],[DisplayMessage1],[DisplayMessage2],[CreatedBy],[CreatedDate],[ModifyBy],[ModifyDate]) Values(" +fncFormatSQLText(jObj.getString("SubCompanyCode"))+","+fncFormatSQLText(jObj.getString("Name"))+","+fncFormatSQLText(jObj.getString("Address1"))+","+fncFormatSQLText(jObj.getString("Address2"))+"," +fncFormatSQLText(jObj.getString("Address3"))+","+fncFormatSQLText(jObj.getString("Country"))+","+
//                        fncFormatSQLText(jObj.getString("ZipCode"))+","+fncFormatSQLText(jObj.getString("Phone"))+","+fncFormatSQLText(jObj.getString("Fax"))+","+fncFormatSQLText(jObj.getString("Email"))+","+fncFormatSQLText(jObj.getString("Url"))+","+fncFormatSQLText(jObj.getString("NextPONo"))+","+fncFormatSQLText(jObj.getString("NextGIDNo"))+","+fncFormatSQLText(jObj.getString("NextSONo"))+","+fncFormatSQLText(jObj.getString("NextDONo"))+","+fncFormatSQLText(jObj.getString("NextInvoiceNo"))+","+fncFormatSQLText(jObj.getString("NextCreditInvoiceNo"))+","+
//                        fncFormatSQLText(jObj.getString("NextPaymentVoucherNo"))+","+fncFormatSQLText(jObj.getString("NextReceiptNo"))+","+fncFormatSQLText(jObj.getString("NextTransferNo"))+","+fncFormatSQLText(jObj.getString("NextPurReturnNo"))+","+fncFormatSQLText(jObj.getString("NextSalesReturnNo"))+","+fncFormatSQLText(jObj.getString("MaxDiscountAllowed"))+","+fncFormatSQLText(jObj.getString("MaxPriceChangeAllowd"))+","+fncFormatSQLText(jObj.getString("NextCustomerCreditNoteNo"))+","+fncFormatSQLText(jObj.getString("NextCustomerDebitNoteNo"))+","+
//                        fncFormatSQLText(jObj.getString("NextCustomerOpeningCNNo"))+","+fncFormatSQLText(jObj.getString("NextCustomerOpeningDNNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierInvoiceNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierCreditNoteNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierDebitNoteNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierOpeningCNNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierOpeningDNNo"))+","+fncFormatSQLText(jObj.getString("NextSupplierReceiptNo"))+","+fncFormatSQLText(jObj.getString("SubCompanyShortCode"))+","+
//                        fncFormatSQLText(jObj.getString("DisplayMessage1")) +","+fncFormatSQLText(jObj.getString("DisplayMessage2"))+","+fncFormatSQLText(jObj.getString("CreatedBy"))+","+fncFormatSQLText(jObj.getString("CreatedDate"))+","+fncFormatSQLText(jObj.getString("ModifyBy"))+","+fncFormatSQLText(jObj.getString("ModifyDate"))+")";
//                mDb.execSQL(sql);
//            }


          /*  mDb.execSQL("Delete from tblMaParamMaster");
            WebMethod="fncGetSettings";
            sResp = soapConn.fncGetSettings(Uname, WebMethod, baseUrl);
            strLen=sResp.length();
            jsonArray=new JSONArray(sResp);
//            sResp=sResp.substring(1, strLen - 2);
            for(int i=0;i<jsonArray.length();i++) {
                jObj=jsonArray.getJSONObject(i);
                sql = "Insert into tblMaParamMaster ([ParamCode],[Description],[Value]) Values("+fncFormatSQLText(jObj.getString("sParamCode"))+","+fncFormatSQLText(jObj.getString("sParamCodeDescription"))+","+fncFormatSQLText(jObj.getString("sValue"))+ ")";
                mDb.execSQL(sql);
            }*/

            sql="select * from tblDownloadLog";
            Cursor curDwd = mDb.rawQuery(sql,null);
            if(curDwd.getCount()>0){
                mDb.execSQL("Update tblDownloadLog set [UserLastSync]=" + fncFormatSQLText(fncFormatSQLDate()));
            }else{
                sql="Insert into tblDownloadLog (UserLastSync) values ("+fncFormatSQLText(fncFormatSQLDate())+")";
                mDb.execSQL(sql);}
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "true";
    }
    private String fncFormatSQLText(String sText)
    {
        if(sText==null){
            return null;
        }
        String sGenString = "";
        sText=sText.trim();
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }
    private String fncFormatSQLDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= new Date(System.currentTimeMillis());
        String systemDate = df.format(date);
        return systemDate;
    }


}
