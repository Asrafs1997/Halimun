package com.unipro.labisha.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderActivity;
import com.unipro.labisha.R;
import com.unipro.labisha.server.SoapConnection;
import com.unipro.labisha.utils.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kalaivanan on 08/03/2018.
 */
public class SupplierFragment extends Fragment {
    EditText edtAdd1,edtAddr2,edtAddr3,edtPhone,edtTel;
    EditText edtCity,edtPinCode,edtNote,edtEmail;
    public static EditText edtDoNo;
    TextView txtSuppliercode;
    AutoCompleteTextView aTxtSupplierCode;
    ImageView imgBtnSearch;
    Button btnConform;
    String sTag="",sPoNo="";
    LinearLayout linDoNo;
    public static String sVendorCode;
    String Uname,Loc_Code,TerminalCode,baseUrl;
    SharedPreferences sprefLogin;
    public ProgressDialog pDialog;
    ArrayList<String> SupplierCodeList;
    ArrayList<HashMap<String,String>> SupplierList;
    DbHelper dbHelper;
    SQLiteDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vw=inflater.inflate(R.layout.fragment_supplier,container,false);
        aTxtSupplierCode=(AutoCompleteTextView)vw.findViewById(R.id.aTxtSupplierCode);
        txtSuppliercode=(TextView)vw.findViewById(R.id.txtSupplierCode);
        edtAdd1=(EditText)vw.findViewById(R.id.edtAddr1);
        edtAddr2=(EditText)vw.findViewById(R.id.edtAddr2);
        edtAddr3=(EditText)vw.findViewById(R.id.edtAddr3);
        edtPhone=(EditText)vw.findViewById(R.id.edtPhone);
        edtTel=(EditText)vw.findViewById(R.id.edtTel);
        edtCity=(EditText)vw.findViewById(R.id.edtCity);
        edtPinCode=(EditText)vw.findViewById(R.id.edtPinCode);
        edtNote=(EditText)vw.findViewById(R.id.edtNotes);
        edtEmail=(EditText)vw.findViewById(R.id.edtEmail);
        edtDoNo=(EditText)vw.findViewById(R.id.edtDONO);
        btnConform=(Button)vw.findViewById(R.id.btnConfirm);
        imgBtnSearch=(ImageView)vw.findViewById(R.id.imgBtnSearch);
        linDoNo=(LinearLayout)vw.findViewById(R.id.linDoNo);
        SupplierCodeList=new ArrayList<>();
        SupplierList=new ArrayList<>();
        dbHelper=new DbHelper(getActivity());
        mDb=dbHelper.getReadableDatabase();
        sTag=getActivity().getIntent().getExtras().getString("Tag");
        sprefLogin = getActivity().getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        if(sTag.equalsIgnoreCase("GID")){
            sPoNo=getActivity().getIntent().getExtras().getString("PoNo");
            txtSuppliercode.setText("Po No");
            aTxtSupplierCode.setText(sPoNo);
            linDoNo.setVisibility(View.VISIBLE);
            aTxtSupplierCode.setEnabled(false);
            edtDoNo.requestFocus();
        }else {
            txtSuppliercode.setText("Supplier Code");
            linDoNo.setVisibility(View.GONE);
            aTxtSupplierCode.setEnabled(true);
        }
        if(InvoiceFragment.isPoedit){
            String vendorcode = sVendorCode.trim();
            aTxtSupplierCode.setEnabled(true);
            aTxtSupplierCode.setText(vendorcode);
            aTxtSupplierCode.setEnabled(false);
            fncGetVendorDetails(vendorcode);
           // aTxtSupplierCode.setSelection(aTxtSupplierCode.getText().length());
        }
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnConform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PurchaseOrderActivity) getActivity()).switchFragment(1);
            }
        });
        aTxtSupplierCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    sVendorCode = aTxtSupplierCode.getText().toString().trim();
                    if (sVendorCode.isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(), "Please Select Supplier", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        for (int i = 0; i < SupplierList.size();i++){
                            HashMap<String, String> mapData = SupplierList.get(i);
                            String sVendor=mapData.get("VendorCode").trim()+" - "+mapData.get("VendorName").trim();
                            if(sVendor.equalsIgnoreCase(sVendorCode)){
                                Toast.makeText(getActivity(),mapData.get("VendorCode"),Toast.LENGTH_SHORT).show();
                                sVendorCode=mapData.get("VendorCode").trim();
                                String sName = mapData.get("VendorName");
                                String sAddr1 = mapData.get("Address1");
                                String sAddr2 = mapData.get("Address2");
                                String sPhone = mapData.get("Phone");
                                String sFax = mapData.get("Fax");
                                String sCity = mapData.get("Country");
                                String sZipCode = mapData.get("ZipCode");
                                String sEmail = mapData.get("Email");
                                String sRemarks = mapData.get("Remarks");
                                edtAdd1.setText(sName);
                                edtAddr2.setText(sAddr1);
                                edtAddr3.setText(sAddr2);
                                edtPhone.setText(sPhone);
                                edtTel.setText(sFax);
                                edtCity.setText(sCity);
                                edtPinCode.setText(sZipCode);
                                edtEmail.setText(sEmail);
                                edtNote.setText(sRemarks);
                            }
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(!fncCheckCursorData()) {
            fncLoadSupplierDetails();
        }
        return vw;
    }

    private void fncGetVendorDetails(String vendorCode){
        if(!fncCheckCursorData()) {
            fncLoadSupplierDetails();
        }
        for (int i = 0; i < SupplierList.size();i++){
            HashMap<String, String> mapData = SupplierList.get(i);
            String sVendor=mapData.get("VendorCode").trim();
            if(sVendor.equalsIgnoreCase(vendorCode)){
                Toast.makeText(getActivity(),mapData.get("VendorCode"),Toast.LENGTH_SHORT).show();
                sVendorCode=mapData.get("VendorCode").trim();
                String sName = mapData.get("VendorName");
                String sAddr1 = mapData.get("Address1");
                String sAddr2 = mapData.get("Address2");
                String sPhone = mapData.get("Phone");
                String sFax = mapData.get("Fax");
                String sCity = mapData.get("Country");
                String sZipCode = mapData.get("ZipCode");
                String sEmail = mapData.get("Email");
                String sRemarks = mapData.get("Remarks");
                edtAdd1.setText(sName);
                edtAddr2.setText(sAddr1);
                edtAddr3.setText(sAddr2);
                edtPhone.setText(sPhone);
                edtTel.setText(sFax);
                edtCity.setText(sCity);
                edtPinCode.setText(sZipCode);
                edtEmail.setText(sEmail);
                edtNote.setText(sRemarks);
            }
        }
    }
    private boolean fncCheckCursorData() {
        try{
            SupplierCodeList=new ArrayList<>();
            SupplierList=new ArrayList<>();
//            mDb.execSQL("Delete from tblInvVendor");
            String sQry="Select * from tblInvVendor";
            Cursor crInvVendor=mDb.rawQuery(sQry,null);
            int mCount=crInvVendor.getCount();
            if(mCount>0){
                crInvVendor.moveToFirst();
                for(int i=0;i<mCount;i++){
                    HashMap<String,String> mapData=new HashMap<>();
                    String sVendorCode=crInvVendor.getString(crInvVendor.getColumnIndex("VendorCode")).trim();
                    mapData.put("VendorCode",sVendorCode);
                    mapData.put("VendorName",crInvVendor.getString(crInvVendor.getColumnIndex("VendorName")).trim());
                    mapData.put("Address1",crInvVendor.getString(crInvVendor.getColumnIndex("Address1")).trim());
                    mapData.put("Address2",crInvVendor.getString(crInvVendor.getColumnIndex("Address2")).trim());
                    mapData.put("Country",crInvVendor.getString(crInvVendor.getColumnIndex("Country")).trim());
                    mapData.put("ZipCode",crInvVendor.getString(crInvVendor.getColumnIndex("ZipCode")).trim());
                    mapData.put("Phone",crInvVendor.getString(crInvVendor.getColumnIndex("Phone")).trim());
                    mapData.put("Fax",crInvVendor.getString(crInvVendor.getColumnIndex("Fax")).trim());
                    mapData.put("Email",crInvVendor.getString(crInvVendor.getColumnIndex("Email")).trim());
                    mapData.put("Remarks",crInvVendor.getString(crInvVendor.getColumnIndex("Remarks")).trim());
                    sVendorCode=sVendorCode+" - "+crInvVendor.getString(crInvVendor.getColumnIndex("VendorName")).trim();
                    SupplierCodeList.add(sVendorCode);
                    SupplierList.add(mapData);
                    crInvVendor.moveToNext();
                }
                Log.e("Loaded from Curser ",mCount+"");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, SupplierCodeList);
                aTxtSupplierCode.setAdapter(adapter);
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fncLoadSupplierDetails() {
        try{
            SupplierCodeList=new ArrayList<>();
            SupplierList=new ArrayList<>();
            WsCallGetVendorList wsCallGetVendorList=new WsCallGetVendorList();
            wsCallGetVendorList.execute();
            /*JsonRequestQueue = Volley.newRequestQueue(getActivity());
            String sUrl="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?";
            pDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Dowloading");
            pDialog.setMax(100);
            pDialog.setIndeterminate(false);
            pDialog.show();
            pDialog.setProgress(10);
            jsonreq = new StringRequest(0, sUrl, new Response.Listener<String>() {
                public void onResponse(String response) {
                    Log.d("RESPONSE", response.toString());
                    try {
                        JSONArray jsArray=new JSONArray(response);
                        int mLen=jsArray.length();
                        for(int i=0;i<mLen;i++){
                            JSONObject jsObj=jsArray.getJSONObject(i);
                            HashMap<String,String> mapData=new HashMap<>();
                            String sVendorCode=jsObj.getString("VendorCode").trim();
                            mapData.put("VendorCode",sVendorCode);
                            mapData.put("VendorName",jsObj.getString("VendorName").trim());
                            mapData.put("Address1",jsObj.getString("Address1").trim());
                            mapData.put("Address2",jsObj.getString("Address2").trim());
                            mapData.put("Country",jsObj.getString("Country").trim());
                            mapData.put("ZipCode",jsObj.getString("ZipCode").trim());
                            mapData.put("Phone",jsObj.getString("Phone").trim());
                            mapData.put("Fax",jsObj.getString("Fax").trim());
                            mapData.put("Email",jsObj.getString("Email").trim());
                            mapData.put("Remarks",jsObj.getString("Remarks").trim());
                            sVendorCode=sVendorCode+" - "+jsObj.getString("VendorName").trim();
                            SupplierCodeList.add(sVendorCode);
                            SupplierList.add(mapData);
                            String sQry="Insert into tblInvVendor ([VendorCode],[VendorName],[Address1],[Address2],[Country],[ZipCode],[Phone],[Fax],[SMSNo],[Email],[Url] ," +
                                    "[TermsCode],[PurchaseLimit],[Attn],[BarCode],[Remarks],[GST],[BankCode],[BankBranchCode],[BankAccountNo],[PaymentMode],[CreditLimit]," +
                                    "[CreditDays],[PODiscount],[CurrencyCode],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate],[IsBank],[ApplyLC],[POExport],[POExportTime]," +
                                    "[AutoPOFax],[AutoPOFaxTime],[GLNCode],[PORemarks],[TradeType]) values("+fncFormatSQLText(jsObj.getString("VendorCode"))+","+fncFormatSQLText(jsObj.getString("VendorName"))+","+
                                    fncFormatSQLText(jsObj.getString("Address1"))+","+fncFormatSQLText(jsObj.getString("Address2"))+","+fncFormatSQLText(jsObj.getString("Country"))+","+fncFormatSQLText(jsObj.getString("ZipCode"))+","+
                                    fncFormatSQLText(jsObj.getString("Phone"))+","+fncFormatSQLText(jsObj.getString("Fax"))+","+fncFormatSQLText(jsObj.getString("SMSNo"))+","+fncFormatSQLText(jsObj.getString("Email"))+","+
                                    fncFormatSQLText(jsObj.getString("Url"))+","+fncFormatSQLText(jsObj.getString("TermsCode"))+","+fncFormatSQLText(jsObj.getString("PurchaseLimit"))+","+fncFormatSQLText(jsObj.getString("Attn"))+","+
                                    fncFormatSQLText(jsObj.getString("BarCode"))+","+fncFormatSQLText(jsObj.getString("Remarks"))+","+fncFormatSQLText(jsObj.getString("GST"))+","+
                                    fncFormatSQLText(jsObj.getString("BankCode"))+","+fncFormatSQLText(jsObj.getString("BankBranchCode"))+","+fncFormatSQLText(jsObj.getString("BankAccountNo"))+","+fncFormatSQLText(jsObj.getString("PaymentMode"))+","+
                                    fncFormatSQLText(jsObj.getString("CreditLimit"))+","+fncFormatSQLText(jsObj.getString("CreditDays"))+","+fncFormatSQLText(jsObj.getString("PODiscount"))+","+fncFormatSQLText(jsObj.getString("CurrencyCode"))+","+
                                    fncFormatSQLText(jsObj.getString("CreateUser"))+","+fncFormatSQLText(jsObj.getString("CreateDate"))+","+fncFormatSQLText(jsObj.getString("ModifyUser"))+","+
                                    fncFormatSQLText(jsObj.getString("ModifyDate"))+","+fncFormatSQLText(jsObj.getString("IsBank"))+","+fncFormatSQLText(jsObj.getString("ApplyLC"))+","+fncFormatSQLText(jsObj.getString("POExport"))+","+
                                    fncFormatSQLText(jsObj.getString("POExportTime"))+","+fncFormatSQLText(jsObj.getString("AutoPOFax"))+","+fncFormatSQLText(jsObj.getString("AutoPOFaxTime"))+","+fncFormatSQLText(jsObj.getString("GLNCode"))+","+
                                    fncFormatSQLText(jsObj.getString("PORemarks"))+","+fncFormatSQLText(jsObj.getString("TradeType"))+")";
                            mDb.execSQL(sQry);
                            Log.e("Saved",sQry);
                        }
                        pDialog.dismiss();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, SupplierCodeList);
                        aTxtSupplierCode.setAdapter(adapter);
                    }catch (Exception e2) {
                        e2.printStackTrace();
                        pDialog.dismiss();
                    }
                    Log.d("RESPONSE", response.toString());
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    if (error instanceof NetworkError) {
                        Log.d("Network Error", "Network Error");
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Log.d("ParseError Error", "ParseError Error");
                    } else {
                        Toast.makeText(getActivity(), "Network Error",Toast.LENGTH_SHORT).show();
                    }

                    Log.v("error", error.toString());
                }
            });
            jsonreq.setRetryPolicy(new DefaultRetryPolicy(120000, 1, 1.0f));
            jsonreq.setTag("images");
            JsonRequestQueue.add(this.jsonreq);
            jsonreq.setShouldCache(true);*/
        }catch (Exception e){
            e.printStackTrace();
            pDialog.dismiss();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            try {

               /* if (sTag.equalsIgnoreCase("GID")) {
                    ((PurchaseOrderActivity) getActivity()).switchFragment(3);
                }*/
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String fncFormatSQLText(String sText)
    {
        String sGenString = "";
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }

    private class WsCallGetVendorList extends AsyncTask<String,String,String>{
        String sWebMethod="fncGetVendorList",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                SoapConnection soapConn=new SoapConnection();
                sResp=soapConn.fncGetPoReturnList(Loc_Code, Uname, sWebMethod,baseUrl);
                return sResp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.startsWith("false")){
                    String[] sResponse=s.split(",");
                    Toast.makeText(getActivity(),sResponse[1],Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    String poDiscount = "0.00";
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsObj = null;
                    for(int i=0;i<jsonArray.length();i++){
                        jsObj=jsonArray.getJSONObject(i);
                        HashMap<String,String> mapData=new HashMap<>();
                        String sVendorCode=jsObj.getString("VendorCode").trim();
                        mapData.put("VendorCode",sVendorCode);
                        mapData.put("VendorName",jsObj.getString("VendorName").trim());
                        mapData.put("Address1",jsObj.getString("Address1").trim());
                        mapData.put("Address2",jsObj.getString("Address2").trim());
                        mapData.put("Country",jsObj.getString("Country").trim());
                        mapData.put("ZipCode",jsObj.getString("ZipCode").trim());
                        mapData.put("Phone",jsObj.getString("Phone").trim());
                        mapData.put("Fax",jsObj.getString("Fax").trim());
                        mapData.put("Email",jsObj.getString("Email").trim());
                        mapData.put("Remarks",jsObj.getString("Remarks").trim());
                        sVendorCode=sVendorCode+" - "+jsObj.getString("VendorName").trim();
                        SupplierCodeList.add(sVendorCode);
                        SupplierList.add(mapData);
                        String sQry="Insert into tblInvVendor ([VendorCode],[VendorName],[Address1],[Address2],[Country],[ZipCode],[Phone],[Fax],[SMSNo],[Email],[Url] ," +
                                "[TermsCode],[PurchaseLimit],[Attn],[BarCode],[Remarks],[GST],[BankCode],[BankBranchCode],[BankAccountNo],[PaymentMode],[CreditLimit]," +
                                "[CreditDays],[PODiscount],[CurrencyCode],[CreateUser],[CreateDate],[ModifyUser],[ModifyDate],[IsBank],[ApplyLC],[POExport],[POExportTime]," +
                                "[AutoPOFax],[AutoPOFaxTime],[GLNCode],[PORemarks],[TradeType]) values("+fncFormatSQLText(jsObj.getString("VendorCode"))+","+fncFormatSQLText(jsObj.getString("VendorName"))+","+
                                fncFormatSQLText(jsObj.getString("Address1"))+","+fncFormatSQLText(jsObj.getString("Address2"))+","+fncFormatSQLText(jsObj.getString("Country"))+","+fncFormatSQLText(jsObj.getString("ZipCode"))+","+
                                fncFormatSQLText(jsObj.getString("Phone"))+","+fncFormatSQLText(jsObj.getString("Fax"))+","+fncFormatSQLText(jsObj.getString("SMSNo"))+","+fncFormatSQLText(jsObj.getString("Email"))+","+
                                fncFormatSQLText(jsObj.getString("Url"))+","+fncFormatSQLText(jsObj.getString("TermsCode"))+","+fncFormatSQLText(jsObj.getString("PurchaseLimit"))+","+fncFormatSQLText(jsObj.getString("Attn"))+","+
                                fncFormatSQLText(jsObj.getString("BarCode"))+","+fncFormatSQLText(jsObj.getString("Remarks"))+","+fncFormatSQLText(jsObj.getString("GST"))+","+
                                fncFormatSQLText(jsObj.getString("BankCode"))+","+fncFormatSQLText(jsObj.getString("BankBranchCode"))+","+fncFormatSQLText(jsObj.getString("BankAccountNo"))+","+fncFormatSQLText(jsObj.getString("PaymentMode"))+","+
                                fncFormatSQLText(jsObj.getString("CreditLimit"))+","+fncFormatSQLText(jsObj.getString("CreditDays"))+","+fncFormatSQLText(poDiscount)+","+fncFormatSQLText(jsObj.getString("CurrencyCode"))+","+
                                fncFormatSQLText(jsObj.getString("CreateUser"))+","+fncFormatSQLText(jsObj.getString("CreateDate"))+","+fncFormatSQLText(jsObj.getString("ModifyUser"))+","+
                                fncFormatSQLText(jsObj.getString("ModifyDate"))+","+fncFormatSQLText("")+","+fncFormatSQLText("")+","+fncFormatSQLText(jsObj.getString("POExport"))+","+
                                fncFormatSQLText(jsObj.getString("POExportTime"))+","+fncFormatSQLText(jsObj.getString("AutoPOFax"))+","+fncFormatSQLText(jsObj.getString("AutoPOFaxTime"))+","+fncFormatSQLText(jsObj.getString("GLNCode"))+","+
                                fncFormatSQLText(jsObj.getString("PORemarks"))+","+fncFormatSQLText("")+")";
                        mDb.execSQL(sQry);
                        Log.e("Saved",sQry);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, SupplierCodeList);
                    aTxtSupplierCode.setAdapter(adapter);
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }
}
