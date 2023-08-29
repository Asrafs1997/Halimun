package com.unipro.labisha.Screens.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.adapter.SearchItemAdapter;
import com.unipro.labisha.server.SoapConnection;
import com.unipro.labisha.utils.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * Created by Kalaivanan on 11/13/2015.
 */
public class SearchActivity extends Activity {
    SharedPreferences spref1;
    SharedPreferences spref;
    public static String rslt="";
    public static final String My_printer="Myprinter";
    String[] ItemDesc,Customer,InvList;
    String baseUrl,Url,Uname,TerminalCode,DeviceId,Loc_Code;
    ArrayList<HashMap<String,String>> SearchList;
    EditText edtSearch;
    ImageButton imgBtnSearch,imgBtnClose;
    TextView txtSearch;
    String[] Name,Code,Price,result,Ph;
    Boolean ConnectStatus;
    int count=0;
    int value;
    ListView gv;
    SQLiteDatabase DB=null;
    DbHelper dbHelper;
    ArrayAdapter<String> adapter;
    private RecyclerView mRecyclerView;
    private SearchItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<HashMap<String,String>> vendorlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchview);
        txtSearch=(TextView)findViewById(R.id.txtSearch);
        gv=(ListView)findViewById(R.id.list);
        edtSearch=(EditText)findViewById(R.id.searchView);
        edtSearch.requestFocus();
        imgBtnSearch= (ImageButton) findViewById(R.id.imgBtnSearch);
        imgBtnClose= (ImageButton) findViewById(R.id.imgBtnClose);
        mRecyclerView = (RecyclerView) findViewById(R.id.rcyvSearchList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        onNewIntent(getIntent());
        SearchList=new ArrayList<>();
        spref1= this.getSharedPreferences(LoginActivity.My_pref, 0);
        final SharedPreferences.Editor editor=getSharedPreferences(My_printer,0).edit();
        vendorlist=new ArrayList<>();
        spref = PreferenceManager.getDefaultSharedPreferences(this);
        try{
            Uname = spref1.getString("Uname", "");
            DeviceId = spref1.getString("Deviceid", "");
            ConnectStatus = spref1.getBoolean("connection", false);
            TerminalCode = spref1.getString("TerminalCode", "");
            Loc_Code = spref1.getString("LocationCode", "");
            baseUrl = spref1.getString("BaseUrl", "");
            Url=baseUrl;
            dbHelper=new DbHelper(getApplicationContext());
            DB=dbHelper.getReadableDatabase();
        }catch (Exception error){
            Log.w("Predference value null", error);
        }
        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String query = edtSearch.getText().toString();
                    switch (value) {
                        case 1:
                            WsCallItemSearch wsCallItemSearch = new WsCallItemSearch();
                            wsCallItemSearch.Query = query;
                            wsCallItemSearch.execute();
                            break;
                        case 2:
                            WsCallCustomerSearch wsCallCustomerSearch = new WsCallCustomerSearch();
                            wsCallCustomerSearch.Query = query;
                            wsCallCustomerSearch.execute();
                            break;
                        case 11:
                            WsCallInvoiceList wsCallInvoiceList = new WsCallInvoiceList();
                            wsCallInvoiceList.Query = query;
                            wsCallInvoiceList.Terminal = TerminalCode;
                            wsCallInvoiceList.execute();
                            break;
                        case 12:
                            WsCallSalesReceiptList wsCallSalesReceiptList = new WsCallSalesReceiptList();
                            wsCallSalesReceiptList.Query = query;
                            wsCallSalesReceiptList.Terminal = TerminalCode;
                            wsCallSalesReceiptList.execute();
                            break;
                        case 13:
                            WsCallInvoiceReturnList wsCallInvoiceReturnList = new WsCallInvoiceReturnList();
                            wsCallInvoiceReturnList.Query = query;
                            wsCallInvoiceReturnList.Terminal = TerminalCode;
                            wsCallInvoiceReturnList.execute();
                            break;
                        case 14:
//                                fncCreateVendorList(query);
                            break;
                        case 4:
                            WsCallInvoiceItemSearch wsCallInvoiceItemSearch = new WsCallInvoiceItemSearch();
                            wsCallInvoiceItemSearch.Query = query;
                            wsCallInvoiceItemSearch.execute();
                            break;
                        case 5:
                            WsCallInvoiceCustomerSearch wsCallInvoiceCustomerSearch = new WsCallInvoiceCustomerSearch();
                            wsCallInvoiceCustomerSearch.Query = query;
                            wsCallInvoiceCustomerSearch.execute();
                            break;
                        case 6:
                            break;
                        default:
                            break;
                    }
                    return true;
//                    }
                }
                return false;
            }
        });
         gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               try {
                      String selectedFromList = gv.getItemAtPosition(position).toString();
                    if (selectedFromList.equalsIgnoreCase("NOT FOUND")) {
                        Toast toast= Toast.makeText(getApplicationContext(), "Not Found any value", Toast.LENGTH_SHORT);
                          toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    } else {
                        Bundle b = new Bundle();
                        b.putString("name", selectedFromList);
                        Intent intent = new Intent();
                        intent.putExtras(b);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }catch (Exception e){
                      e.printStackTrace();
                }
            }
         });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = edtSearch.getText().toString();
                    switch (value){
                        case 1:
                            WsCallItemSearch wsCallItemSearch=new WsCallItemSearch();
                            wsCallItemSearch.Query=query;
                            wsCallItemSearch.execute();
                            break;
                        case 2:
                            WsCallCustomerSearch wsCallCustomerSearch=new WsCallCustomerSearch();
                            wsCallCustomerSearch.Query=query;
                            wsCallCustomerSearch.execute();
                            break;
                        case 11:
                            WsCallInvoiceList wsCallInvoiceList=new WsCallInvoiceList();
                            wsCallInvoiceList.Query=query;
                            wsCallInvoiceList.Terminal=TerminalCode;
                            wsCallInvoiceList.execute();
                            break;
                        case 12:
                            WsCallSalesReceiptList wsCallSalesReceiptList=new WsCallSalesReceiptList();
                            wsCallSalesReceiptList.Query=query;
                            wsCallSalesReceiptList.Terminal=TerminalCode;
                            wsCallSalesReceiptList.execute();
                            break;
                        case 13:
                            WsCallInvoiceReturnList wsCallInvoiceReturnList=new WsCallInvoiceReturnList();
                            wsCallInvoiceReturnList.Query=query;
                            wsCallInvoiceReturnList.Terminal=TerminalCode;
                            wsCallInvoiceReturnList.execute();
                            break;
                        case 14:
//                            fncCreateVendorList(query);
                            break;
                        case 4:
                            WsCallInvoiceItemSearch wsCallInvoiceItemSearch=new WsCallInvoiceItemSearch();
                            wsCallInvoiceItemSearch.Query=query;
                            wsCallInvoiceItemSearch.execute();
                            break;
                        case 5:
                            WsCallInvoiceCustomerSearch wsCallInvoiceCustomerSearch=new WsCallInvoiceCustomerSearch();
                            wsCallInvoiceCustomerSearch.Query=query;
                            wsCallInvoiceCustomerSearch.execute();
                            break;
                        case 6:
                            break;
                        default:
                            break;
                    }
//                }
            }
        });

    }

    private void fncCreateVendorList(String query) {
        query=query.toUpperCase();
        vendorlist=new ArrayList<>();
        /*for(int i=0;i<VendorDetails.size();i++){
            if(VendorDetails.get(i).get("VendorName").startsWith(query)){
                vendorlist.add(VendorDetails.get(i));
            }
        }*/
        Name=new String[vendorlist.size()];
        Code=new String[vendorlist.size()];
        Price=new String[vendorlist.size()];
        for(int i=0;i<vendorlist.size();i++){
            Name[i]=vendorlist.get(i).get("VendorName");
            Code[i]=vendorlist.get(i).get("VendorCode");
        }
        adapter = new NavDescAdapter(getApplicationContext(), Name, Code, Price);
        gv.setAdapter(adapter);
        if(vendorlist.size()==0){
            Toast.makeText(getApplicationContext(), "Vendor not found to key Value", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("value")) {
                value = extras.getInt("value",0);
                switch (value){
                    case 1:
                        //Stock take,Price change,Pos Sales Inventory Search
                        txtSearch.setText("Search By Item Description");
                        break;
                    case 2:
                        //Pos Sales Customer search
                        txtSearch.setText("Search By Customer Name");
                        break;
                    case 11:
                        //Sales invoice reprint Search
                        txtSearch.setText("Search By Invoice Number");
                        break;
                    case 12:
                        //Pos Sales reprint
                        txtSearch.setText("Search By SalesReceipt Number");
                        break;
                    case 13:
                        //Sales return reprint
                        txtSearch.setText("Search By InvoiceReturn Number");
                        break;
                    case 14:
                        txtSearch.setText("Search Vendor by Name");
                        break;
                    case 4:
                        //Sales invoice Inventory search
                        txtSearch.setText("Sales Invoice Search By Item Description");
                        break;
                    case 5:
                        //Sales invoice customer search
                        gv.setVisibility(View.VISIBLE);
                        txtSearch.setText("Sales Invoice Search By Customer Name");
                        break;
                    case 6:
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private class WsCallCustomerSearch extends AsyncTask<String,Void,Void> {

        public String Query;

        @Override
        protected Void doInBackground(String... params) {
            try{
               /* CallSoap callSoap=new CallSoap();
                String webMethod="GetCustomerSearch";
                String resp=callSoap.fncGetCustomerList(Url, TerminalCode, Query,webMethod);
                SearchActivity.rslt=resp;*/
            }catch(Exception ex)
            {
                SearchActivity.rslt=ex.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
//                Customer = gson.fromJson(rslt,String[].class);
                count=Customer.length;
                Name=new String[count];
                Code=new String[count];
                Price=new String[count];
                Ph=new String[count];
                for(int i=0;i<count;i++) {
                    result = Customer[i].split("/");
                    Name[i] = result[0];
                    Code[i] = result[1];
                    Price[i] = result[2];
                    Ph[i]=result[3];
                }
                adapter=new NavListAdapter(getApplicationContext(),Name,Code,Price,Ph);
                gv.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class WsCallItemSearch extends AsyncTask<String,Void,Void> {

        public String Query;

        @Override
        protected Void doInBackground(String... params) {
            try{
                /*vendorlist=new ArrayList<>();
                String sQry="";
                sQry = "select Description,InventoryCode,CAST(RetailPrice as decimal(18,2)) as RetailPrice from tblInventory where Description like" + fncFormatSQLText(Query + "%");
                Statement stmt=DBcon.createStatement();
                ResultSet rs=stmt.executeQuery(sQry);
                while (rs.next()){
                   HashMap<String,String>map=new HashMap<>();
                    map.put("Description",rs.getString("Description").trim());
                    map.put("InventoryCode", rs.getString("InventoryCode").trim());
                    map.put("RetailPrice", rs.getString("RetailPrice").trim());
                    vendorlist.add(map);
                }
                Name = new String[vendorlist.size()];
                Code = new String[vendorlist.size()];
                Price = new String[vendorlist.size()];
                for(int i=0;i<vendorlist.size();i++){
                    Name[i] = vendorlist.get(i).get("Description");
                    Code[i] = vendorlist.get(i).get("InventoryCode");
                    Price[i] = vendorlist.get(i).get("RetailPrice");
                }*/
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

                adapter = new NavDescAdapter(getApplicationContext(), Name, Code, Price);
                gv.setAdapter(adapter);

        }
    }
    private class WsCallInvoiceItemSearch extends AsyncTask<String,String,String> {
        public String Query;
        String sWebmethod="fncGetInventorySearch",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(SearchActivity.this, ProgressDialog.THEME_HOLO_DARK);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                if(ConnectStatus) {
                    SoapConnection soapConn = new SoapConnection();
                    sResp = soapConn.fncGetInventorySearch(Loc_Code, Query, sWebmethod, baseUrl);
                    if (sResp.startsWith("false")) {
                        return sResp;
                    } else {
                        JSONArray jsArray = new JSONArray(sResp);
                        Name = new String[jsArray.length()];
                        Code = new String[jsArray.length()];
                        Price = new String[jsArray.length()];
                        for (int i = 0; i < jsArray.length(); i++) {
                            JSONObject jsObj = jsArray.getJSONObject(i);
                            Name[i] = jsObj.getString("Description").trim();
                            Code[i] = jsObj.getString("InventoryCode").trim();
                            Price[i] = jsObj.getString("RetailPrice").trim();
                        }
                    }
                }else {
                    String sQry="Select * from tblVwInventory where Description like "+fncFormatSQLText("%"+Query+"%");
                    Cursor crInv=DB.rawQuery(sQry,null);
                    int cOut=crInv.getCount();
                    if(crInv.getCount()>0){
                        Name = new String[cOut];
                        Code = new String[cOut];
                        Price = new String[cOut];
                        crInv.moveToFirst();
                        for (int i = 0; i < cOut; i++) {
                            Name[i] = crInv.getString(crInv.getColumnIndex("Description")).trim();
                            Code[i] = crInv.getString(crInv.getColumnIndex("InventoryCode")).trim();
                            Price[i] = crInv.getString(crInv.getColumnIndex("RetailPrice")).trim();
                            crInv.moveToNext();
                        }
                    }else {
                        return "false,Item Not Found";
                    }
                }
                return "true";
            }catch(Exception ex){
                Log.e("Exception ", ex.toString());
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String sMsg) {
            super.onPostExecute(sMsg);
            try {
                if(sMsg.startsWith("false")){
                    String[] sResponse=sMsg.split(",");
                    Toast toast= Toast.makeText(getApplicationContext(), sResponse[1], Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                dialog.dismiss();
                gv.setVisibility(View.VISIBLE);
                adapter = new NavDescAdapter(getApplicationContext(), Name, Code, Price);
                gv.setAdapter(adapter);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private class WsCallInvoiceItemSearchOld extends AsyncTask<String,String,String> {
        public String Query;
        ArrayList<String> ItemName=new ArrayList<>();
        ArrayList<String> ItemCode=new ArrayList<>();
        ArrayList<String> ItemPrice=new ArrayList<>();
        String sWebmethod="fncGetInventoryList",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(SearchActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                if(ConnectStatus){
                    DB.execSQL("Delete from tblvwInventory");
                    String sQry = "select * from tblvwInventory";
                    Cursor crInv=DB.rawQuery(sQry,null);
                    if(crInv.getCount()>0){
                        StandAloneDbArItemDesc(TerminalCode, Query);
                    }else {
                        SoapConnection soapConn=new SoapConnection();
                        JSONObject jsObj = null;
                        JSONArray jsonArray = null;
                        sResp=soapConn.fncGetInventoryList(Loc_Code, Uname, sWebmethod, baseUrl);
                        jsonArray = new JSONArray(sResp);
                        if (jsonArray.length() > 0)
                            DB.execSQL("Delete from tblvwInventory");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsObj = jsonArray.getJSONObject(i);
                            /*sQry = "Insert into [tblvwInventory](InventoryCode,Description,UOM,Compatible,WUnit,UnitCost,SellingPrice,QtyonHand ,Image) Values " +
                                    "(" + fncFormatSQLText(jobj.getString("InventoryCode")) + "," + fncFormatSQLText(jobj.getString("Description")) + "," +
                                    fncFormatSQLText(jobj.getString("Uom")) + "," + fncFormatSQLText(jobj.getString("Compatible")) + "," + fncFormatSQLText(jobj.getString("Wunit")) + "," +
                                    fncFormatSQLText(jobj.getString("UnitCost")) + "," + fncFormatSQLText(jobj.getString("SellingPrice")) + "," + fncFormatSQLText(jobj.getString("QtyonHand")) + "," + fncFormatSQLText(jobj.getString("Image")) + ")";
                            Log.e("Insert tblvwInventory " + i, sQry);*/
                            sQry="Insert into tblVwInventory (InventoryCode,Description,UOM,SellingPrice,UnitCost,RetailPrice,QtyonHand,WUnit,LocationCode,CreateUser,CreateDate,ModifyUser,ModifyDate)Values("+
                                    fncFormatSQLText(jsObj.getString("InventoryCode").trim())+","+fncFormatSQLText(jsObj.getString("Description"))+","+fncFormatSQLText(jsObj.getString("UOM"))+","+fncFormatSQLText(jsObj.getString("SellingPrice"))+","+
                                    fncFormatSQLText(jsObj.getString("UnitCost"))+","+fncFormatSQLText(jsObj.getString("RetailPrice"))+","+fncFormatSQLText(jsObj.getString("QtyonHand"))+","+fncFormatSQLText(jsObj.getString("WUnit"))+","+
                                    fncFormatSQLText(jsObj.getString("LocationCode").trim())+","+fncFormatSQLText(jsObj.getString("CreateUser"))+","+fncFormatSQLText(jsObj.getString("CreateDate"))+","+fncFormatSQLText(jsObj.getString("ModifyUser"))+","+
                                    fncFormatSQLText(jsObj.getString("ModifyDate"))+")";
                            Log.e("Resp "+i,sQry);
                            DB.execSQL(sQry);
                        }
                        StandAloneDbArItemDesc(TerminalCode, Query);
                    }
                    return "true";
                }else {
                    StandAloneDbArItemDesc(TerminalCode, Query);
                    return "true";
                }

            }catch(Exception ex){
                SearchActivity.rslt=ex.toString();
                Log.e("Exception ", ex.toString());
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String sMsg) {
            super.onPostExecute(sMsg);
            try {
                if(sMsg.equalsIgnoreCase("false")){
                    Toast toast= Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                dialog.dismiss();
                gv.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(),"Server off-line",Toast.LENGTH_SHORT).show();
                adapter = new NavDescAdapter(getApplicationContext(), Name, Code, Price);
                gv.setAdapter(adapter);
                }catch(Exception e){
                    e.printStackTrace();
                }
        }
    }
    private HashMap<String, Integer> calculateIndexesForName(ArrayList<String> items){
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i<items.size(); i++){
            String name = items.get(i);
            String index = name.substring(0,1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    private class WsCallInvoiceCustomerSearch extends AsyncTask<String,Void,Void> {
        public String Query;
        private final ProgressDialog dialog = new ProgressDialog(SearchActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try{
                if(ConnectStatus){
                    String sQry="";
                    sQry="select * from tblInvCustomer";
                    Cursor crCust=DB.rawQuery(sQry,null);
                    if(crCust.getCount()>0){
                        StandAloneDbArCustomer(TerminalCode, Query);
                    }else {
                        JSONObject jObj=null;
                        JSONArray jsonArray=null;
                        String WebMethod="fncGetCustomerDetails";
                        SoapConnection soapConn=new SoapConnection();
                        String sResp = null;//soapConn.fncGetCustomerDetails(DeviceId, TerminalCode, WebMethod, baseUrl);
                        int Strln=sResp.length();
                        sResp=sResp.substring(58,Strln-1);
                        jsonArray=new JSONArray(sResp);
                        if(jsonArray.length()>0)
                            DB.execSQL("Delete from tblInvCustomer");
                        for (int i=0;i<jsonArray.length();i++) {
                            jObj=jsonArray.getJSONObject(i);
                            sQry = "INSERT INTO tblInvCustomer ([CustomerCode],[CustomerName],[Address1],[Address2],[Country],[PostalCode],[Phone],[Fax],[Mobile],[Email],[Status],[GSTType],[Gst],CurrencyCode) VALUES ("+fncFormatSQLText(jObj.getString("CustomerCode"))+","+fncFormatSQLText(jObj.getString("CustomerName"))+","+fncFormatSQLText(jObj.getString("Address1"))+","+fncFormatSQLText(jObj.getString("Address2"))+","+fncFormatSQLText(jObj.getString("Country"))+","+fncFormatSQLText(jObj.getString("PostalCode"))+","+fncFormatSQLText(jObj.getString("Phone"))+","+fncFormatSQLText(jObj.getString("Fax"))+","+fncFormatSQLText(jObj.getString("Mobile"))+","+fncFormatSQLText(jObj.getString("Email"))+","+fncFormatSQLText(jObj.getString("Status"))+","+fncFormatSQLText(jObj.getString("GSTType"))+","+fncFormatSQLText(jObj.getString("Gst"))+","+fncFormatSQLText(jObj.getString("CurrencyCode"))+ ")";
                            DB.execSQL(sQry);
                        }
                        StandAloneDbArCustomer(TerminalCode, Query);
                    }
                }else {
                    StandAloneDbArCustomer(TerminalCode, Query);
                }
            }catch(Exception ex)
            {
                SearchActivity.rslt=ex.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                if(Code.length==0){
                    Toast toast= Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                adapter = new NavListAdapter(getApplicationContext(), Name, Code, Price, Ph);
                gv.setAdapter(adapter);
                dialog.dismiss();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void StandAloneDbArItemDesc(String terminalCode, String query) {
        String sSql = "select Description,InventoryCode,CAST(SellingPrice as decimal(18,2)) as SellingPrice from tblvwInventory where Description like" + fncFormatSQLText("%"+query + "%");//and InActive='N' and Discontinued='N'
        Cursor crArItem=DB.rawQuery(sSql,null);
        Name = new String[crArItem.getCount()];
        Code = new String[crArItem.getCount()];
        Price = new String[crArItem.getCount()];
        if(crArItem.getCount()>0)
            crArItem.moveToFirst();
        for (int i=0;i<crArItem.getCount();i++){
            Name[i] = crArItem.getString(crArItem.getColumnIndex("Description")).trim();
            Code[i] = crArItem.getString(crArItem.getColumnIndex("InventoryCode")).trim();
            Price[i] = crArItem.getString(crArItem.getColumnIndex("SellingPrice")).trim();
            crArItem.moveToNext();
        }

    }
    private void StandAloneDbArCustomer(String terminalCode, String query) {
        String sSql = "select CustomerName,CustomerCode,Address1,Phone from tblInvCustomer where CustomerName like" + fncFormatSQLText("%"+query + "%");//SalesManId="+fncFormatSQLText(Uname)+" and Status='Active' and
        Cursor crArCustomer=DB.rawQuery(sSql,null);
        Name = new String[crArCustomer.getCount()];
        Code = new String[crArCustomer.getCount()];
        Price = new String[crArCustomer.getCount()];
        Ph = new String[crArCustomer.getCount()];
        if(crArCustomer.getCount()>0)
            crArCustomer.moveToFirst();
        for(int i=0;i<crArCustomer.getCount();i++){
            Name[i]=crArCustomer.getString(crArCustomer.getColumnIndex("CustomerName")).trim();
            Code[i]=crArCustomer.getString(crArCustomer.getColumnIndex("CustomerCode")).trim();
            Price[i]=crArCustomer.getString(crArCustomer.getColumnIndex("Address1")).trim();
            Ph[i]=crArCustomer.getString(crArCustomer.getColumnIndex("Phone")).trim();
            crArCustomer.moveToNext();
        }
    }

    private class WsCallSalesReceiptList extends AsyncTask<String,Void,Void> {
        public String Query;
        public String Terminal;
        @Override
        protected Void doInBackground(String... params) {
            try{

            }catch (Exception e){
                e.printStackTrace();
                SearchActivity.rslt=e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
//                InvList = gson.fromJson(rslt, String[].class);
                count=InvList.length;
                Name=new String[count];
                Code=new String[count];
                Price=new String[count];
                for(int i=0;i<count;i++) {
                    result = InvList[i].split("/");
                    Name[i] = result[0];
                    Code[i] = result[1];
                    Price[i] = result[2];
                }
                adapter=new NavInvoiceAdapter(getApplicationContext(),Name,Code,Price);
                gv.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private class WsCallInvoiceReturnList extends AsyncTask<String,Void,Void> {
        public String Query,Terminal;

        @Override
        protected Void doInBackground(String... params) {
            try{

            }catch (Exception e){
                e.printStackTrace();
                SearchActivity.rslt=e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String jsonresp=rslt;
            try {
                if (jsonresp.equalsIgnoreCase("Exception")) {

                } else {
                    JSONArray jsonArray = new JSONArray(jsonresp);
                    Name=new String[jsonArray.length()];
                    Code=new String[jsonArray.length()];
                    Price=new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String InvNo = jsonObject.optString("sARInvNo").toString();
                        String Date = jsonObject.optString("sARInvDate").toString();
                        String Total = jsonObject.optString("sARInvNetTotal").toString();
                        Name[i]=InvNo;
                        Code[i]=Date;
                        Price[i]=Total;
                    }
                    adapter=new NavInvoiceAdapter(getApplicationContext(),Name,Code,Price);
                    gv.setAdapter(adapter);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    private class WsCallInvoiceList extends AsyncTask<String,Void,Void> {

        public String Query;
        public String Terminal;

        @Override
        protected Void doInBackground(String... params) {
            try{

            }catch (Exception e){
                e.printStackTrace();
                SearchActivity.rslt=e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                if(ConnectStatus){
                    count = SearchList.size();
                    Log.e("***Invoice List", count + "");
                    Name = new String[count];
                    Code = new String[count];
                    Price = new String[count];
                    for (int i = 0; i < count; i++) {
                        Name[i] = SearchList.get(i).get("InvoiceNo");
                        Code[i] = SearchList.get(i).get("InvoiceDate");
                        Price[i] = SearchList.get(i).get("TotalValue");
                    }
                    adapter = new NavInvoiceAdapter(getApplicationContext(), Name, Code, Price);
                    gv.setAdapter(adapter);
                }else {
                   Toast.makeText(getApplicationContext(), "Server off-line", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e("***Invoice List", e.toString());
                e.printStackTrace();
            }
        }
    }

    private class NavListAdapter extends ArrayAdapter<String> {
        private Context context;
        String[] Name;
        String[] Code;
        String[] Addr;
        String[] Ph;
        public NavListAdapter(Context context, String[] Name, String[] Code, String[] Addr,String[] ph) {
            super(context, R.layout.list_search, Code);
            this.Name = Name;
            this.Code = Code;
            this.Addr = Addr;
            this.Ph= ph;
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
              LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               View single_row = inflater.inflate(R.layout.list_search, null, true);
            try{
                TextView textView = (TextView) single_row.findViewById(R.id.name);
                TextView textView1 = (TextView) single_row.findViewById(R.id.code);
                TextView textView2 = (TextView) single_row.findViewById(R.id.addr);
                TextView textView3 = (TextView) single_row.findViewById(R.id.phone);
                textView.setText(Name[position]);
                textView1.setText(Code[position]);
                textView2.setText(Addr[position]);
                //textView3.setText(Ph[position]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return single_row;
        }
    }
    private class NavDescAdapter extends ArrayAdapter<String> {
        private Context context;
        String[] Name;
        String[] Code;
        String[] Price;
        public NavDescAdapter(Context context, String[] name, String[] code, String[] price) {
            super(context, R.layout.list_search, code);
            this.Name = name;
            this.Code = code;
            this.Price = price;
            this.context=context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View single_row = inflater.inflate(R.layout.list_search, null, true);
            try {

                TextView textView = (TextView) single_row.findViewById(R.id.name);
                TextView textView1 = (TextView) single_row.findViewById(R.id.code);
                TextView textView2 = (TextView) single_row.findViewById(R.id.addr);
                textView.setText(Name[position]);
                textView1.setText(Code[position]);
                textView2.setText(Price[position]);

            }catch (Exception e){
                e.printStackTrace();
            }
            return single_row;
        }
    }

    private class NavInvoiceAdapter extends ArrayAdapter<String> {
        private Context context;
        String[] InvNo;
        String[] Date;
        String[] Amt;
        public NavInvoiceAdapter(Context context, String[] name, String[] code, String[] price) {
            super(context, R.layout.list_search, name);
            this.InvNo = name;
            this.Date = code;
            this.Amt = price;
            this.context=context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View single_row = inflater.inflate(R.layout.list_search, null, true);
            try {

                TextView textView = (TextView) single_row.findViewById(R.id.name);
                TextView textView1 = (TextView) single_row.findViewById(R.id.code);
                TextView textView2 = (TextView) single_row.findViewById(R.id.addr);
                textView.setText(InvNo[position]);
                textView1.setText(Date[position]);
                textView2.setText(Amt[position]);

            }catch (Exception e){
                e.printStackTrace();
            }
            return single_row;
        }
    }
    private String fncFormatSQLText(String sText)
    {
        String sGenString = "";
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle b = new Bundle();
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

}
