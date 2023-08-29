package com.unipro.labisha.Screens.transfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.search.SearchActivity;
import com.unipro.labisha.adapter.SalesListAdapter;
import com.unipro.labisha.server.SoapConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalaivanan on 26/04/2018.
 */
public class TransferOutInventory extends AppCompatActivity {
    Spinner spnLocation;
    ListView lstTransferItems;
    SalesListAdapter mListAdapter;
    EditText edtItemCode,edtQty,edtSellingPrice;
    TextView txtFromLoc,txtDescription,txtQOH;
    TextView txtQtyTotal;
    ImageView imggrpClose;
    ImageButton imgBtnScan,imgBtnSearch;
    Button btnEnter,btnClear,btnAdd,btnCancel,btnSave;
    String sToLocation;
    SharedPreferences sprefLogin;
    String TerminalCode,Loc_Code,Uname,baseUrl;
    Toast toast;
    SoapConnection soapConn;
    ArrayList<HashMap<String,String>> sLocationList;
    ArrayList<HashMap<String,String>> arrayListTransferOut;
    ArrayList<String> sListLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_inventory);
        txtFromLoc=(TextView)findViewById(R.id.txtFromLoc);
        txtDescription=(TextView)findViewById(R.id.txtDescDetails);
        txtQOH=(TextView)findViewById(R.id.txtQOH);
        edtSellingPrice=(EditText)findViewById(R.id.edtSellingPrice);
        txtQtyTotal=(TextView)findViewById(R.id.txtQtyTotal);
        edtItemCode=(EditText)findViewById(R.id.edtItemCode);
        edtQty=(EditText)findViewById(R.id.edtQty);
        imgBtnScan=(ImageButton)findViewById(R.id.btnscan);
        imgBtnSearch=(ImageButton)findViewById(R.id.btnsearch);
        spnLocation=(Spinner)findViewById(R.id.spnOutletName);
        btnEnter=(Button)findViewById(R.id.btnEnter);
        btnClear=(Button)findViewById(R.id.btnClear);
        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnSave=(Button)findViewById(R.id.btnSave);
        lstTransferItems=(ListView)findViewById(R.id.lstTransferItems);
        sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        txtFromLoc.setText(Loc_Code);
        soapConn=new SoapConnection();
        sLocationList=new ArrayList<>();
        sListLocation=new ArrayList<>();
        arrayListTransferOut=new ArrayList<>();
        onNewIntent(getIntent());
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
                        fncGetItemCodeDetails(ItemCode);
                    }
                    return true;
                }
                return false;
            }
        });
        edtQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String sQty = edtQty.getText().toString().trim();
                    if (sQty.equals("")) {
                        toast = Toast.makeText(getApplicationContext(), "Please enter the Qunatity", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        edtQty.requestFocus();
                    } else {
                        QtyCheck();
                    }
                    return true;
                }
                return false;
            }
        });
        spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sToLocation = sLocationList.get(position).get("LocationCode").trim();
//                Toast.makeText(getApplicationContext(),sToLocation,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent in = new Intent(getApplicationContext(), SearchActivity.class);
                    in.putExtra("value", 4);
                    startActivityForResult(in, 4);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        lstTransferItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {
                    final String InvCode = arrayListTransferOut.get(position).get("InventoryCode");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransferOutInventory.this);
                    alertDialog.setTitle("Conform to delete");
                    alertDialog.setMessage(arrayListTransferOut.get(position).get("Description"));
                    alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            arrayListTransferOut.remove(position);
                            fncLoadInventoryList();
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    Log.e("***Delete Sucess", InvCode);
                } catch (Exception e) {
                    Log.e("***Delete Exception", e.toString());
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        imggrpClose=(ImageView)findViewById(R.id.grpClose);
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QtyCheck();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fncCleartext();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ItemCode = edtItemCode.getText().toString();
                String sDescription=txtDescription.getText().toString();
                String sPrice=edtSellingPrice.getText().toString();
                String ttl = txtQtyTotal.getText().toString();
//                sToLocation = spnLocation.getSelectedItem().toString();
                String Qty = edtQty.getText().toString();
                try {
                    if (sToLocation.equalsIgnoreCase("") || sToLocation.isEmpty()) {
                        toast = Toast.makeText(getApplicationContext(), "Please select ToLocation Details ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        spnLocation.requestFocus();
                    }else if(sToLocation.equalsIgnoreCase(Loc_Code)){
                        toast = Toast.makeText(getApplicationContext(), "ToLocation not to be Same Location ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        spnLocation.requestFocus();
                    }else if (ItemCode.equalsIgnoreCase("") || Qty.equalsIgnoreCase("")) {
                        toast = Toast.makeText(getApplicationContext(), "Please enter ItemCode and Qty Details", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else if (ttl.equalsIgnoreCase("")) {
                        toast = Toast.makeText(getApplicationContext(), "Please press Enter on quantity!!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        spnLocation.setEnabled(false);
                        fncAddInvenyoryList(sToLocation, ItemCode, sDescription, sPrice, Qty, ttl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"TransferOut Cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(arrayListTransferOut.size()>0){
                        JSONArray jsArray=new JSONArray(arrayListTransferOut);
                        String sJsonData=jsArray.toString();
                        fncSaveTransferOut(sJsonData);
                    }else {
                        Toast.makeText(getApplicationContext(),"No Item to Save",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void fncSaveTransferOut(String sJsonData) {
        try{
            WsCallSaveTransferOut wsCallSaveTransferOut=new WsCallSaveTransferOut();
            wsCallSaveTransferOut.sJsonData=sJsonData;
            wsCallSaveTransferOut.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncAddInvenyoryList(String sToLocation, String itemCode, String sDescription, String sPrice, String sQty, String sTtlAmnt) {
        try{
            Double dRetailPrice=Double.parseDouble(sPrice);
            Double dQty=Double.parseDouble(sQty);
            Double dSumTotal=dRetailPrice*dQty;
            Double dTotal=Double.parseDouble(sTtlAmnt);
            Double dDiscount=0.0,dDiscPerc=0.0;
            Boolean isExisting=false;
        /*if(dSumTotal != dTotal){
            dDiscount=dSumTotal-dTotal;
            dDiscPerc=dSumTotal/100;
            dDiscPerc=dDiscount/dDiscPerc;
        }*/
            HashMap<String,String> mapData=new HashMap<>();
            if(arrayListTransferOut.size()>0) {
                for (int i = 0; i < arrayListTransferOut.size(); i++) {
                    HashMap<String,String> eMapData=arrayListTransferOut.get(i);
                    String InventoryCode = eMapData.get("InventoryCode");
                    if(InventoryCode.equalsIgnoreCase(itemCode)){
                        dQty=dQty+Double.parseDouble(eMapData.get("Quantity"));
                        dTotal=dTotal+Double.parseDouble(eMapData.get("TotalAmount"));
                        dDiscount=dDiscount+Double.parseDouble(eMapData.get("Discount"));
                        mapData.put("ToLocation", sToLocation);
                        mapData.put("InventoryCode", itemCode);
                        mapData.put("Description", sDescription);
                        mapData.put("SellingPrize", sPrice);
                        mapData.put("Quantity", String.format("%.2f",dQty));
                        mapData.put("TotalAmount", String.format("%.2f",dTotal));
                        mapData.put("Discount", String.format("%.2f", dDiscount));
                        arrayListTransferOut.remove(i);
                        arrayListTransferOut.add(mapData);
                        isExisting=true;
                    }
                }
            }
            if(!isExisting) {
                mapData.put("ToLocation", sToLocation);
                mapData.put("InventoryCode", itemCode);
                mapData.put("Description", sDescription);
                mapData.put("SellingPrize", sPrice);
                mapData.put("Quantity", sQty);
                mapData.put("TotalAmount", sTtlAmnt);
                mapData.put("Discount", String.format("%.2f", dDiscount));
                arrayListTransferOut.add(mapData);
            }
            fncLoadInventoryList();
            fncCleartext();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void fncLoadInventoryList() {
        mListAdapter = new SalesListAdapter(getApplicationContext(), false);
        mListAdapter.setData(arrayListTransferOut);
        mListAdapter.setIsTransferOut(true);
        lstTransferItems.setAdapter(mListAdapter);
        setListViewHeightBasedOnChildren(lstTransferItems);
    }
    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            sLocationList=(ArrayList<HashMap<String,String>>)extras.getSerializable("sLocationList");
            sListLocation=(ArrayList<String>)extras.getSerializable("sListLocation");
            sListLocation.remove(0);
            sLocationList.remove(0);
            if(sListLocation.size()>0){
                ArrayAdapter<String> sectionArray = new ArrayAdapter<String>(TransferOutInventory.this, android.R.layout.simple_spinner_dropdown_item, sListLocation);
                sectionArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocation.setAdapter(sectionArray);
            }
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
    private void fncGetItemCodeDetails(String itemCode) {
        try{
            WsCallGetItemCode wsCallGetItemCode=new WsCallGetItemCode();
            wsCallGetItemCode.sItemCode=itemCode;
            wsCallGetItemCode.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void fncCleartext(){
        edtItemCode.setText("");;
        edtQty.setText("");
        edtSellingPrice.setText("");
        txtDescription.setText("");
        txtQOH.setText("");
        txtQtyTotal.setText("");
        edtItemCode.requestFocus();
    }
    private void QtyCheck() {
        String sPrice=edtSellingPrice.getText().toString();
        String sQty=edtQty.getText().toString();
        String sQoh=txtQOH.getText().toString();
        Double dQty, dPrice, dSum,dQoh;
        try {
            dPrice = Double.parseDouble(sPrice);
            dQty=Double.parseDouble(sQty);
            dQoh=Double.parseDouble(sQoh);
            if(dPrice==0){
                Toast toast=Toast.makeText(getApplicationContext(), "Retail Price Not to be 0", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                edtSellingPrice.setText("");
                edtSellingPrice.requestFocus();
            }else {
                try {
                    dSum = dQty * dPrice;
                    txtQtyTotal.setText(String.format("%.2f",dSum));
                    btnAdd.requestFocus();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class WsCallGetItemCode extends AsyncTask<String,String,String> {
        public String sItemCode;
        String sResp="",sMethod="fncGetInventoryDetail";
        private final ProgressDialog dialog = new ProgressDialog(TransferOutInventory.this, ProgressDialog.THEME_HOLO_DARK);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Scanning Inventory...");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                sResp=soapConn.fncGetInventoryDetail(sItemCode,Uname,Loc_Code,sMethod,baseUrl);
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
                if(s.equalsIgnoreCase("false")){
                    Toast.makeText(getApplicationContext(),"ItemCode Not Found",Toast.LENGTH_SHORT).show();
                    fncCleartext();
                    edtItemCode.requestFocus();
                }else {
                    JSONArray jsArray=new JSONArray(s);
                    JSONObject jsObj=jsArray.getJSONObject(0);
                    edtItemCode.setText(jsObj.getString("InventoryCode").trim());
                    txtDescription.setText(jsObj.getString("Description").trim());
                    edtSellingPrice.setText(jsObj.getString("SellingPrice").trim());
                    txtQOH.setText(jsObj.getString("QtyonHand").trim());
                    edtQty.requestFocus();
                }
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    private class WsCallSaveTransferOut extends AsyncTask<String,String,String>{
        String sResp="",sMethod="fncSaveTransferOut";
        private final ProgressDialog dialog = new ProgressDialog(TransferOutInventory.this, ProgressDialog.THEME_HOLO_DARK);
        public String sJsonData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Saving Inventory...");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                sResp=soapConn.fncSaveTransferOut(sJsonData,Uname,Loc_Code,sMethod,baseUrl);
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
                    Toast.makeText(getApplicationContext(), " Not Saved Successfully ", Toast.LENGTH_SHORT).show();
                }else {
                    String[] sResponse = s.split(",");
                    if (sResponse[0].equalsIgnoreCase("true")) {
                        Toast.makeText(getApplicationContext(), "Saved Successfully "+sResponse[1], Toast.LENGTH_SHORT).show();
                        Intent in=new Intent(getApplicationContext(), TransferOutActivity.class);
                        startActivity(in);
                        finish();
                    }
                }
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            Toast.makeText(getApplicationContext(),"Add Item ",Toast.LENGTH_SHORT).show();
        } else if(requestCode==4){
            if(resultCode== Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String CustSearch = bundle.getString("name");
                //String[] Cusdetail=CustSearch.split("/");
                edtItemCode.setText(CustSearch);
                if (CustSearch.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Item Code Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    fncGetItemCodeDetails(CustSearch);
                }
            }else if(resultCode==Activity.RESULT_CANCELED){
                Toast toast=Toast.makeText(getApplicationContext(),"Search Canceled",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                edtItemCode.requestFocus();
            }
        }
    }
}
