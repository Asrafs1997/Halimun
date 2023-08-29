package com.unipro.labisha.Screens.purchase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.adapter.PurchaseListAdapter;
import com.unipro.labisha.fragments.InvoiceFragment;
import com.unipro.labisha.server.SoapConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.unipro.labisha.fragments.InvoiceFragment.OrderSummaryList;
import static com.unipro.labisha.fragments.SupplierFragment.sVendorCode;

/**
 * Created by kalaivanan on 30/01/2018.
 */
public class PurchaseOrderList extends AppCompatActivity {
    TextView txtTitle,txtPoNo;
    ImageView imgGrpAdd,imgGrpSearch,imgGrpBack;
    ImageView imggrpClose,imgBtnSearch,imgGrpReload;
    EditText edtPoNo;
    ListView lstOrderItems;
    LinearLayout linPoSearch;
    PurchaseListAdapter mListAdapter;
    Spinner spnType;
    String sTag="";
    Button dBtnCancel;
    String Loc_Code,TerminalCode,baseUrl,Uname;
    SharedPreferences sprefLogin;
    public StringRequest jsonreq;
    SoapConnection soapConn;
    public RequestQueue JsonRequestQueue;
    public ProgressDialog pDialog;
    private LayoutInflater mInflater;
    ArrayList<HashMap<String,String>> mArrayPurchase;
    ArrayList<String> arrayPurchaseType;
    public static String sPoNo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_purchase_list);
        imgGrpAdd=(ImageView)findViewById(R.id.imgGrpAdd);
        imgGrpBack=(ImageView)findViewById(R.id.imgGrpBack);
        imgGrpSearch=(ImageView)findViewById(R.id.imgGrpSearch);
        imggrpClose=(ImageView)findViewById(R.id.grpClose);
        imgGrpReload=(ImageView)findViewById(R.id.imgGrpReload);
        lstOrderItems=(ListView)findViewById(R.id.lstOrderItems);
        txtTitle=(TextView)findViewById(R.id.txtTitle);
        txtPoNo=(TextView)findViewById(R.id.txtPoNo);
        edtPoNo=(EditText)findViewById(R.id.edtPoNo);
        imgBtnSearch=(ImageView)findViewById(R.id.imgBtnSearch);
        linPoSearch=(LinearLayout)findViewById(R.id.linPoSearch);
        spnType=(Spinner)findViewById(R.id.spnType);
        mListAdapter = new PurchaseListAdapter(getApplicationContext());
        lstOrderItems.setAdapter(mListAdapter);
        lstOrderItems.setSmoothScrollbarEnabled(true);
        mArrayPurchase=new ArrayList<>();
        mInflater = LayoutInflater.from(this);
        sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        soapConn=new SoapConnection();
        onNewIntent(getIntent());


//        arrayPurchaseType.add("Closed");
//        arrayPurchaseType.add("Cancelled");

        if (sTag.equalsIgnoreCase("PurchaseOrder")) {
            txtPoNo.setText("PO.NO");
            linPoSearch.setVisibility(View.VISIBLE);
            arrayPurchaseType=new ArrayList<>();
            arrayPurchaseType.add("Open");
            arrayPurchaseType.add("Processed");
            arrayPurchaseType.add("Approved");
        } else if (sTag.equalsIgnoreCase("GID")) {
            txtPoNo.setText("GID.NO");
            linPoSearch.setVisibility(View.GONE);
            arrayPurchaseType=new ArrayList<>();
            arrayPurchaseType.add("Not Posted");
            arrayPurchaseType.add("Posted");
            imgGrpAdd.setVisibility(View.GONE);
        } else if (sTag.equalsIgnoreCase("PurchaseReturn")) {
            linPoSearch.setVisibility(View.GONE);
            txtPoNo.setText("Return.NO");
            arrayPurchaseType=new ArrayList<>();
            arrayPurchaseType.add("Not Posted");
            arrayPurchaseType.add("Posted");
        }else if (sTag.equalsIgnoreCase("PoVerification")) {
            txtPoNo.setText("PO.NO");
            linPoSearch.setVisibility(View.VISIBLE);
            arrayPurchaseType=new ArrayList<>();
            arrayPurchaseType.add("Open");
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(PurchaseOrderList.this,android.R.layout.simple_spinner_item, arrayPurchaseType);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(mAdapter);
        imgGrpAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Add", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(), PurchaseOrderActivity.class);
                InvoiceFragment.isPoedit=false;
                in.putExtra("Tag", sTag);
                startActivity(in);
            }
        });
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sPono=edtPoNo.getText().toString();
//                Toast.makeText(getApplicationContext(),sPono,Toast.LENGTH_SHORT).show();
                WsCallGetPoDetail wsCallGetPoDetail=new WsCallGetPoDetail();
                wsCallGetPoDetail.sPoNo=sPono;
                wsCallGetPoDetail.execute();
            }
        });
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = arrayPurchaseType.get(i);
//                Toast.makeText(getApplicationContext(),sSelected,Toast.LENGTH_SHORT).show();
                if (sTag.equalsIgnoreCase("PurchaseOrder")) {
                    txtPoNo.setText("PO.NO");
                    fncGetPOList(sSelected);
                } else if (sTag.equalsIgnoreCase("GID")) {
                    txtPoNo.setText("GID.NO");
                    fncGetGidList(sSelected);
                    imgGrpAdd.setVisibility(View.GONE);
                } else if (sTag.equalsIgnoreCase("PurchaseReturn")) {
                    txtPoNo.setText("Return.NO");
                    fncGetPOReturnList(sSelected);
                }else if (sTag.equalsIgnoreCase("PoVerification")) {
                    txtPoNo.setText("PO.NO");
                    fncGetPOList(sSelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgGrpReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sSelected =spnType.getSelectedItem().toString();
                    if (sTag.equalsIgnoreCase("PurchaseOrder")) {
                        txtPoNo.setText("PO.NO");
                        fncGetPOList(sSelected);
                    } else if (sTag.equalsIgnoreCase("GID")) {
                        txtPoNo.setText("GID.NO");
                        fncGetGidList(sSelected);
                        imgGrpAdd.setVisibility(View.GONE);
                    } else if (sTag.equalsIgnoreCase("PurchaseReturn")) {
                        txtPoNo.setText("Return.NO");
                        fncGetPOReturnList(sSelected);
                    }else if (sTag.equalsIgnoreCase("PoVerification")) {
                        txtPoNo.setText("PO.NO");
                        fncGetPOList(sSelected);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        imgGrpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
            }
        });
        imgGrpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
            }
        });
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(), MenuListActivity.class);
                startActivity(in);
                finishAffinity();
            }
        });
        lstOrderItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    HashMap<String, String> mapData = mArrayPurchase.get(i);
//                    Toast.makeText(getApplicationContext(), mapData + "", Toast.LENGTH_SHORT).show();
                    if (mapData != null) {
                        if (sTag.equalsIgnoreCase("PoVerification")) {
                            sPoNo = mapData.get( ("PoNo") );
                            InvoiceFragment.isPoedit=true;
                            fncGetPoDetails(sPoNo);
                        }else {
                            View dialogView = mInflater.inflate(R.layout.dialog_itemlist, null);
                            initDialog(dialogView, mapData);
                            AlertDialog dialog = showDialog(dialogView);
                            dBtnCancel.setTag(dialog);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        fncCreatePurchaseList();
    }

    private void fncGetGidList(String sSelected) {
        WsCallGetGidList wsCallGetGidList=new WsCallGetGidList();
        wsCallGetGidList.sSelected=sSelected;
        wsCallGetGidList.execute();
    }

    public AlertDialog showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(null).setView(view);
        return builder.show();
    }
    private void initDialog(View view, final HashMap<String, String> Orderlistmap) {
        dBtnCancel= (Button) view.findViewById(R.id.btnCancel);
        Button btnEditOrder=(Button)view.findViewById(R.id.btnEditOrder);
        Button btnPostGID=(Button)view.findViewById(R.id.btnPostGid);
        btnPostGID.setVisibility(View.GONE);
        if(sTag.equalsIgnoreCase("PurchaseOrder")){
            btnPostGID.setVisibility(View.VISIBLE);
            btnEditOrder.setVisibility(View.VISIBLE);
            try {
                String sProcessed=Orderlistmap.get("Processed");
                if(sProcessed.equalsIgnoreCase("Y")){
                    btnPostGID.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        TextView txtDescription=(TextView)view.findViewById(R.id.txtDescription);
        String date = Orderlistmap.get("PoDate");
        date = date.substring(0,10);
        String Description="Po No      : "+Orderlistmap.get("PoNo")+"\n"+"Po Date    : "+date+"\n";
//        Description = Description + "Created By     : "+Orderlistmap.get("CreateUser")+"\n"+"Created On     : "+Orderlistmap.get("CreateDate");
        txtDescription.setText(Description);
        Log.w("Invoice No", 1 + Orderlistmap.get("InvoiceNo") + 1);

//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                dBtnCancel.getLayoutParams();
//        params.weight = 2.0f;
//        dBtnCancel.setLayoutParams(params);

        dBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (AlertDialog) v.getTag();
//                Toast.makeText(getApplicationContext(), "View Details", Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        btnEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sPoNo = Orderlistmap.get( ("PoNo") );
                    InvoiceFragment.isPoedit=true;
//                    SummaryFragment.fncgetpono(pono);
                    fncGetPoDetails(sPoNo);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnPostGID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = (AlertDialog) dBtnCancel.getTag();
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent in = new Intent(getApplicationContext(), PurchaseOrderActivity.class);
                InvoiceFragment.isPoedit=false;
                in.putExtra("Tag", "GID");
                in.putExtra("PoNo", Orderlistmap.get("PoNo"));
                startActivity(in);
            }
        });

    }
    private void fncGetPoDetails(String sPoNo) {
        WsCallGetPOGID wscallgetpogid=new WsCallGetPOGID();
        wscallgetpogid.sPoOrderNo=sPoNo;
        wscallgetpogid.execute();
    }
    private void fncGetPOReturnList(String sSelected) {
        try{
            WsCallGetPoReturnList wsCallGetPoReturnList=new WsCallGetPoReturnList();
            wsCallGetPoReturnList.sSelected=sSelected;
            wsCallGetPoReturnList.execute();
           /* mArrayPurchase=new ArrayList<>();
            JsonRequestQueue = Volley.newRequestQueue(PurchaseOrderList.this);
            String sUrl="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?sLocCode=&user=";
            pDialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);
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
                            sVendorCode=sVendorCode+" - "+jsObj.getString("VendorName").trim();
                            mapData.put("PoNo",jsObj.getString("ReturnNo").trim());
                            mapData.put("PoDate",jsObj.getString("Date").trim());
                            mapData.put("SupplierName", sVendorCode);
                            mapData.put("NetTotal",jsObj.getString("NetAmount").trim());
                            mArrayPurchase.add(mapData);
                        }
                        pDialog.dismiss();
                        mListAdapter = new PurchaseListAdapter(getApplicationContext());
                        mListAdapter.setData(mArrayPurchase);
                        lstOrderItems.setAdapter(mListAdapter);
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
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Log.d("ParseError Error", "ParseError Error");
                    } else {
                        Toast.makeText(getApplicationContext(), "Network Error",Toast.LENGTH_SHORT).show();
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
        }
    }

    private void fncGetPOList(String sSelected) {
        try{
            WsCallGetPoList wsCallGetPoList =new WsCallGetPoList();
            wsCallGetPoList.sSelected=sSelected;
            wsCallGetPoList.execute();
           /* mArrayPurchase=new ArrayList<>();
            JsonRequestQueue = Volley.newRequestQueue(PurchaseOrderList.this);
            String sUrl="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?sLocCode=&sUser=&sMethod="+sSelected;
            pDialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);
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
                       *//* response=response.replace("\"","");
                        response=convertStandardJSONString(response);
                        response=response.replace("\\","");*//*
                        JSONArray jsArray=new JSONArray(response);
                        int mLen=jsArray.length();
                        for(int i=0;i<mLen;i++){
                            JSONObject jsObj=jsArray.getJSONObject(i);
                            HashMap<String,String> mapData=new HashMap<>();
                            String sVendorCode=jsObj.getString("VendorCode").trim();
                            sVendorCode=sVendorCode+" - "+jsObj.getString("VendorName").trim();
                            mapData.put("PoNo",jsObj.getString("PoNo").trim());
                            mapData.put("PoDate",jsObj.getString("PoDate").trim());
                            mapData.put("SupplierName", sVendorCode);
                            mapData.put("NetTotal",jsObj.getString("NetTotal").trim());
                            mArrayPurchase.add(mapData);
                        }
                        pDialog.dismiss();
                        mListAdapter = new PurchaseListAdapter(getApplicationContext());
                        mListAdapter.setData(mArrayPurchase);
                        lstOrderItems.setAdapter(mListAdapter);
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
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Log.d("ParseError Error", "ParseError Error");
                    } else {
                        Toast.makeText(getApplicationContext(), "Network Error",Toast.LENGTH_SHORT).show();
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
        }
    }
    public String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }
    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("Tag")) {
                sTag=extras.getString("Tag");
                txtTitle.setText(sTag);
            }
        }
    }
    private void fncCreatePurchaseList() {
        try{
            HashMap<String,String>mapData=new HashMap<>();
            mapData.put("PoNo","HQ150001");
            mapData.put("PoDate","15/12/2016");
            mapData.put("SupplierName","V001-ABC pvt Ltd");
            mapData.put("NetTotal","923522.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150015");
            mapData.put("PoDate","07/05/2017");
            mapData.put("SupplierName", "V065-MNC pvt Ltd");
            mapData.put("NetTotal","1104.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150074");
            mapData.put("PoDate","19/11/2015");
            mapData.put("SupplierName", "V023-LLC pvt Ltd");
            mapData.put("NetTotal","756.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150085");
            mapData.put("PoDate","09/02/2016");
            mapData.put("SupplierName", "V095-KLL pvt Ltd");
            mapData.put("NetTotal","8569.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150032");
            mapData.put("PoDate","25/11/2017");
            mapData.put("SupplierName", "V018-FUG pvt Ltd");
            mapData.put("NetTotal","8778.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150029");
            mapData.put("PoDate","27/09/2015");
            mapData.put("SupplierName", "V0078-HKL pvt Ltd");
            mapData.put("NetTotal","1522.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150064");
            mapData.put("PoDate","24/08/2016");
            mapData.put("SupplierName", "V027-MLL pvt Ltd");
            mapData.put("NetTotal","9235.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150031");
            mapData.put("PoDate","16/07/2016");
            mapData.put("SupplierName", "V054-LMC pvt Ltd");
            mapData.put("NetTotal","3518.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150062");
            mapData.put("PoDate","06/08/2017");
            mapData.put("SupplierName", "V006-EPM pvt Ltd");
            mapData.put("NetTotal","8790.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150074");
            mapData.put("PoDate","09/10/2016");
            mapData.put("SupplierName", "V018-KTM pvt Ltd");
            mapData.put("NetTotal","9287.00");
            mArrayPurchase.add(mapData);

         /*   mapData=new HashMap<>();
            mapData.put("PoNo","HQ150015");
            mapData.put("PoDate","07/05/2017");
            mapData.put("SupplierName", "V065-MNC pvt Ltd");
            mapData.put("NetTotal","1104.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150074");
            mapData.put("PoDate","19/11/2015");
            mapData.put("SupplierName", "V023-LLC pvt Ltd");
            mapData.put("NetTotal","756.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150085");
            mapData.put("PoDate","09/02/2016");
            mapData.put("SupplierName", "V095-KLL pvt Ltd");
            mapData.put("NetTotal","8569.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150032");
            mapData.put("PoDate","25/11/2017");
            mapData.put("SupplierName", "V018-FUG pvt Ltd");
            mapData.put("NetTotal","8778.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150029");
            mapData.put("PoDate","27/09/2015");
            mapData.put("SupplierName", "V0078-HKL pvt Ltd");
            mapData.put("NetTotal","1522.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150064");
            mapData.put("PoDate","24/08/2016");
            mapData.put("SupplierName", "V027-MLL pvt Ltd");
            mapData.put("NetTotal","9235.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150031");
            mapData.put("PoDate","16/07/2016");
            mapData.put("SupplierName", "V054-LMC pvt Ltd");
            mapData.put("NetTotal","3518.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150062");
            mapData.put("PoDate","06/08/2017");
            mapData.put("SupplierName", "V006-EPM pvt Ltd");
            mapData.put("NetTotal","8790.00");
            mArrayPurchase.add(mapData);
            mapData=new HashMap<>();
            mapData.put("PoNo","HQ150074");
            mapData.put("PoDate","09/10/2016");
            mapData.put("SupplierName", "V018-KTM pvt Ltd");
            mapData.put("NetTotal","9287.00");
            mArrayPurchase.add(mapData);*/
            mListAdapter = new PurchaseListAdapter(getApplicationContext());
            mListAdapter.setData(mArrayPurchase);
            lstOrderItems.setAdapter(mListAdapter);
//            setListViewHeightBasedOnChildren(lstOrderItems);
        }catch (Exception e){
            e.printStackTrace();
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
//            totalHeight += view.getMeasuredHeight();
            totalHeight += 50;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));;
        listView.setLayoutParams(params);
    }
    public  class WsCallGetPOGID extends AsyncTask<String,String,String>{

        public String sPoOrderNo;
        String sWebMethod="fncGetPoGid",sResp="";
        //  private final ProgressDialog dialog = new ProgressDialog(getApplicationContext(), ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setCanceledOnTouchOutside(false);
//            this.dialog.setMessage("Loading...");
//            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                sResp=soapConn.fncGetPoGid(sPoOrderNo,Uname,sWebMethod,baseUrl);
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
                    Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT);
                    // Toast.makeText(getActivity(),sResponse[1],Toast.LENGTH_SHORT).show();
                }else {
                    InvoiceFragment.isPoedit = true;
                    OrderSummaryList=new ArrayList<>(  );
                    JSONObject jsObj=new JSONObject(s);
                    String sDetail=jsObj.getString("sDetail");
                    String sHeader=jsObj.getString("sHeader");
                    JSONArray jsArrayDeatil=new JSONArray(sDetail);
                    JSONArray jsArrayHeader=new JSONArray(sHeader);
                    int mCountHdr=jsArrayHeader.length();
                    int mCountDet=jsArrayDeatil.length();
                    jsObj=jsArrayHeader.getJSONObject(0);
                    sVendorCode=jsObj.getString("VendorCode");
                    sPoNo=jsObj.getString("PoNo");
                    for(int i=0;i<mCountDet;i++) {
                        jsObj=jsArrayDeatil.getJSONObject(i);
                        HashMap<String, String> mapData = new HashMap<>();
                        mapData.put("sInventory", jsObj.getString("ItemCode"));
                        mapData.put("sDescription", jsObj.getString("ItemDesc"));
                        mapData.put("sPrice", jsObj.getString("UnitCost"));
                        if (!sTag.equalsIgnoreCase("PoVerification")) {
                            mapData.put("sCtnQty", jsObj.getString("WQty"));
                            mapData.put("sFocQty", jsObj.getString("FocQty"));
                            mapData.put("sLQty", jsObj.getString("LQty"));
                        }else {
                            mapData.put("sCtnQty", "0");
                            mapData.put("sFocQty", "0");
                            mapData.put("sLQty", "0");
                        }
                        mapData.put("sPoCtnQty", jsObj.getString("WQty"));
                        mapData.put("sPoFocQty", jsObj.getString("FocQty"));
                        mapData.put("sPoLQty", jsObj.getString("LQty"));
                        mapData.put("sWnit", jsObj.getString("WUnit"));
                        mapData.put("sTotalAmnt", "0");
                        Log.e("mapData", mapData + "");
                        OrderSummaryList.add(mapData);
                        Toast.makeText( getApplicationContext(),"success",Toast.LENGTH_LONG );
                    }
                    Intent i = new Intent( getApplicationContext(),PurchaseOrderActivity.class );
                    i.putExtra("Tag", sTag);
//                    i.putExtra("Tag","Edit");
//                    startActivity(i);
                    startActivity( i );






                }
                //  dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                //dialog.dismiss();
            }
        }
    }
    private class WsCallGetPoList extends AsyncTask<String,String,String>{
        String sResp="",sWebMethod="fncGetPoList";
        private final ProgressDialog dialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);
        public String sSelected;

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
                sResp=soapConn.fncGetPoList(Loc_Code,Uname,sSelected,sWebMethod,baseUrl);
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
                mArrayPurchase=new ArrayList<>();
                JSONArray jsonArray=new JSONArray(s);
                JSONObject jsObj=null;
                for(int i=0;i<jsonArray.length();i++){
                     jsObj=jsonArray.getJSONObject(i);
                    HashMap<String,String> mapData=new HashMap<>();
                    String sVendorCode=jsObj.getString("VendorCode").trim();
                    sVendorCode=sVendorCode+" - "+jsObj.getString("VendorName").trim();
                    mapData.put("PoNo",jsObj.getString("PoNo").trim());
                    mapData.put("PoDate",jsObj.getString("PoDate").trim());
                    mapData.put("SupplierName", sVendorCode);
                    mapData.put("NetTotal",jsObj.getString("PoNetValue").trim());
                    mapData.put("Processed",jsObj.getString("Processed").trim());
                    mArrayPurchase.add(mapData);

                }
                mListAdapter = new PurchaseListAdapter(getApplicationContext());
                mListAdapter.setData(mArrayPurchase);
                lstOrderItems.setAdapter(mListAdapter);
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private class WsCallGetPoDetail extends AsyncTask<String,String,String>{
        String sResp="",sWebMethod="fncGetPoDetail";
        private final ProgressDialog dialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);
        public String sPoNo;

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
                sResp=soapConn.fncGetPoDetail(sPoNo, Loc_Code, Uname, sWebMethod, baseUrl);
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
                    Toast.makeText(getApplicationContext(),"Purchase Order Not Found",Toast.LENGTH_SHORT).show();
                }else {
                    mArrayPurchase = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsObj = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsObj = jsonArray.getJSONObject(i);
                        HashMap<String, String> mapData = new HashMap<>();
                        String sVendorCode = jsObj.getString("VendorCode").trim();
                        sVendorCode = sVendorCode + " - " + jsObj.getString("VendorName").trim();
                        mapData.put("PoNo", jsObj.getString("PoNo").trim());
                        mapData.put("PoDate", jsObj.getString("PoDate").trim());
                        mapData.put("SupplierName", sVendorCode);
                        mapData.put("NetTotal", jsObj.getString("PoNetValue").trim());
                        mapData.put("Processed", jsObj.getString("Processed").trim());
                        if (sTag.equalsIgnoreCase("PoVerification")) {
                            if(mapData.get("Processed").equalsIgnoreCase("N") && jsObj.getString("Approved").trim().equalsIgnoreCase("N")  && jsObj.getString("Cancelled").trim().equalsIgnoreCase("0")  && jsObj.getString("CancelPartialPO").trim().equalsIgnoreCase("N") ) {
                                mArrayPurchase.add(mapData);
                            }
                        }else {
                            mArrayPurchase.add(mapData);
                        }
                    }
                    mListAdapter = new PurchaseListAdapter(getApplicationContext());
                    mListAdapter.setData(mArrayPurchase);
                    lstOrderItems.setAdapter(mListAdapter);
                }
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private class WsCallGetGidList extends AsyncTask<String,String,String>{
        String sResp="",sWebMethod="fncGetGidList";
        private final ProgressDialog dialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);
        public String sSelected;

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
                sResp=soapConn.fncGetGidList(Loc_Code, sSelected, sWebMethod,baseUrl);
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
                    Toast.makeText(getApplicationContext(),sResponse[1],Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    mArrayPurchase = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsObj = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsObj = jsonArray.getJSONObject(i);
                        HashMap<String, String> mapData = new HashMap<>();
                        String sVendorCode = jsObj.getString("VendorCode").trim();
                        sVendorCode = sVendorCode + " - " + jsObj.getString("VendorName").trim();
                        mapData.put("PoNo", jsObj.getString("GidNo").trim());
                        mapData.put("PoDate", jsObj.getString("GidDate").trim());
                        mapData.put("SupplierName", sVendorCode);
                        mapData.put("NetTotal", jsObj.getString("GidNetValue").trim());
                        mArrayPurchase.add(mapData);
                    }
                    mListAdapter = new PurchaseListAdapter(getApplicationContext());
                    mListAdapter.setData(mArrayPurchase);
                    lstOrderItems.setAdapter(mListAdapter);
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    private class WsCallGetPoReturnList extends AsyncTask<String,String,String>{
        public String sSelected;
        String sWebMethod="fncGetPoReturnList",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(PurchaseOrderList.this, ProgressDialog.THEME_HOLO_DARK);

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
                sResp=soapConn.fncGetPoReturnList(Loc_Code, sSelected, sWebMethod,baseUrl);
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
                    Toast.makeText(getApplicationContext(),sResponse[1],Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    mArrayPurchase = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsObj = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsObj = jsonArray.getJSONObject(i);
                        HashMap<String, String> mapData = new HashMap<>();
                        String sVendorCode = jsObj.getString("VendorCode").trim();
                        sVendorCode = sVendorCode + " - " + jsObj.getString("VendorName").trim();
                        mapData.put("PoNo", jsObj.getString("ReturnNo").trim());
                        mapData.put("PoDate", jsObj.getString("ReturnDate").trim());
                        mapData.put("SupplierName", sVendorCode);
                        mapData.put("NetTotal", jsObj.getString("NetAmount").trim());
                        mArrayPurchase.add(mapData);
                    }
                    mListAdapter = new PurchaseListAdapter(getApplicationContext());
                    mListAdapter.setData(mArrayPurchase);
                    lstOrderItems.setAdapter(mListAdapter);
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }
}
