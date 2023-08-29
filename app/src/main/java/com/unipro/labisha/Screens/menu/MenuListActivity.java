package com.unipro.labisha.Screens.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.unipro.labisha.Screens.localDb.DBTransferActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderList;
import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.stock.StockUpdateActivity;
import com.unipro.labisha.Screens.transfer.TransferOutActivity;
import com.unipro.labisha.adapter.MenuListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 21/06/2018.
 */
public class MenuListActivity extends AppCompatActivity {
    ListView lstMenu;
    String Uname;
    ImageView imggrpClose;
    MenuListAdapter mAdapter;
    HashMap<String, String> mapData;
    ArrayList<HashMap<String, String>> MenuList;
    Boolean ConnectStatus;

    Boolean menupermission;
    SharedPreferences sprefLogin;
    public static Boolean UpdateAlert = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        lstMenu = (ListView) findViewById(R.id.lstMenu);
        imggrpClose = (ImageView) findViewById(R.id.grpClose);
        sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
        ConnectStatus = sprefLogin.getBoolean("connection", false);
        menupermission = sprefLogin.getBoolean("menupermission", false);
        Uname = sprefLogin.getString("Uname", "");
        MenuList = new ArrayList<>();

        if (ConnectStatus) {
            mapData = new HashMap<>();
            mapData.put("txtTitle", "Stock Check");
            mapData.put("txtId", "10001");
            MenuList.add(mapData);

            //if (menupermission) {
                mapData = new HashMap<>();
                mapData.put("txtTitle", "Stock Update");
                mapData.put("txtId", "00001");
                MenuList.add(mapData);
                mapData = new HashMap<>();
                mapData.put("txtTitle", "Purchase Order");
                mapData.put("txtId", "00002");
                MenuList.add(mapData);
                mapData = new HashMap<>();
                mapData.put("txtTitle", "Purchase Return");
                mapData.put("txtId", "00003");
                MenuList.add(mapData);
                mapData = new HashMap<>();
                mapData.put("txtTitle", "GID");
                mapData.put("txtId", "00006");
                MenuList.add(mapData);
                mapData = new HashMap<>();
                mapData.put("txtTitle", "PO Verification");
                mapData.put("txtId", "00007");
                MenuList.add(mapData);
                mapData = new HashMap<>();
                mapData.put("txtTitle", "Transfer Out");
                mapData.put("txtId", "00008");
                MenuList.add(mapData);
        //    }

//            mapData = new HashMap<>();   // Removed on 25-02-19 for only online mode process
//            mapData.put("txtTitle", "Download From Server");
//            mapData.put("txtId", "00004");
//            MenuList.add(mapData);
//            mapData = new HashMap<>();
//            mapData.put("txtTitle", "Upload To Server");
//            mapData.put("txtId", "00005");
//            MenuList.add(mapData);


        }


//        else {
//            mapData=new HashMap<>();
//            mapData.put("txtTitle", "Stock Check");
//            mapData.put("txtId", "10001");
//            MenuList.add(mapData);
//            mapData=new HashMap<>();
//            mapData.put("txtTitle", "Stock Update");
//            mapData.put("txtId", "00001");
//            MenuList.add(mapData);
//
//        }
//        else {
//            mapData=new HashMap<>();
//            mapData.put("txtTitle", "Stock Check");
//            mapData.put("txtId", "10001");
//            MenuList.add(mapData);
//            mapData=new HashMap<>();
//            mapData.put("txtTitle", "Sales Order");
//            mapData.put("txtId", "00002");
//            MenuList.add(mapData);
//            mapData=new HashMap<>();
//            mapData.put("txtTitle", "Collection Entry");
//            mapData.put("txtId", "00003");
//            MenuList.add(mapData);
//        }
        fncCreateListView();
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(in);
                finishAffinity();
            }
        });
        lstMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    mapData = MenuList.get(i);
                    Toast.makeText(getApplicationContext(), mapData.get("txtTitle"), Toast.LENGTH_SHORT).show();
                    String sId = mapData.get("txtId");
                    Intent in = null;
                    switch (sId) {
                        case "10001":
                            in = new Intent(getApplicationContext(), StockUpdateActivity.class);
                            in.putExtra("Tag", "StockCheck");
                            startActivity(in);
                            break;
                        case "00001":
                            in = new Intent(getApplicationContext(), StockUpdateActivity.class);
                            in.putExtra("Tag", "StockUpdate");
                            startActivity(in);


                            //  adminlogincheck();

                            break;
                        case "00002":
                            in = new Intent(getApplicationContext(), PurchaseOrderList.class);
                            in.putExtra("Tag", "PurchaseOrder");
                            startActivity(in);
                            break;
                        case "00003":
                            in = new Intent(getApplicationContext(), PurchaseOrderList.class);
                            in.putExtra("Tag", "PurchaseReturn");
                            startActivity(in);
                            break;
                        case "00004":
                            in = new Intent(getApplicationContext(), DBTransferActivity.class);
                            DBTransferActivity.IsDownload = true;
                            in.putExtra("Tag", "Download");
                            startActivity(in);
                            break;
                        case "00005":
                            in = new Intent(getApplicationContext(), DBTransferActivity.class);
                            DBTransferActivity.IsDownload = false;
                            in.putExtra("Tag", "Upload");
                            startActivity(in);
                            break;
                        case "00006":
                            in = new Intent(getApplicationContext(), PurchaseOrderList.class);
                            in.putExtra("Tag", "GID");
                            startActivity(in);
                            break;
                        case "00007":
                            in = new Intent(getApplicationContext(), PurchaseOrderList.class);
                            in.putExtra("Tag", "PoVerification");
                            startActivity(in);
                            break;
                        case "00008":
                            in = new Intent(getApplicationContext(), TransferOutActivity.class);
                            in.putExtra("Tag", "Transfer Out");
                            startActivity(in);
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void adminlogincheck() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setSingleLine();
        // input.setPadding(50, 0, 50, 0);
        input.setHint("Enter the Admin Password");
        //  alert.setIcon();
        alert.setTitle("Password");
        alert.setMessage("Enter the Admin Password");
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void fncCreateListView() {
        System.out.println("MenuList  "+MenuList.size());

        try {
            mAdapter = new MenuListAdapter(getApplicationContext());
            mAdapter.setData(MenuList);
            lstMenu.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
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


}
