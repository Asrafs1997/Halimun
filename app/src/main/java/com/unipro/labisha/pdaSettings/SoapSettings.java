package com.unipro.labisha.pdaSettings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sewoo.port.PortMediator;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;
import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.stock.StockUpdateActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by User1 on 2/13/2016.
 */
public class SoapSettings extends PreferenceActivity {
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_Virtual = "virtual";
    public static final String KEY_BaseURL = "URL";
    private static final String KEY_BTNSAVE = "btnSave";
    private static final String KEY_BTPRINTER = "bTPrinter";
    public String baseUrl, address, port, virtual;
    PreferenceScreen prefScreen;
    Preference btnSave, btnBTPrinter;
    SharedPreferences sharedPreferences;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;

    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothPort bluetoothPort;
    private LayoutInflater mInflater;
    ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Vector<BluetoothDevice> remoteDevices;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver discoveryResult;
    private ListView list;
    private String lastConnAddr, bTDevice;
    private connTask connectionTask;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothSocket socket;
    private boolean connected;
    private Thread hThread;
    private OutputStream os;
    private InputStream is;
    SharedPreferences.Editor editorPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_soap);
        mInflater = LayoutInflater.from(this);
        gestureDetector = new GestureDetector(SoapSettings.this, new SwipeDetector());
        prefScreen = getPreferenceScreen();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        setTitle("Settings");
        btnSave = (Preference) getPreferenceManager().findPreference(KEY_BTNSAVE);
        btnBTPrinter = (Preference) getPreferenceManager().findPreference(KEY_BTPRINTER);
        PreferenceCategory btnSelect = (PreferenceCategory) getPreferenceManager().findPreference("btnSelect");
        prefScreen.removePreference(btnSelect);
        Preferences();
        if (btnSave != null) {
            btnSave.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {

                    SharedPreferences settings = getSharedPreferences("baseUrl", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("UsersUrl", baseUrl);
                    editor.commit();

                    Intent in = new Intent(getApplicationContext(), StockUpdateActivity.class);
                    //  in.putExtra("baseUrl", baseUrl);
                    startActivity(in);
                    finish();
                    return true;
                }
            });
        }
        btnBTPrinter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                adapter.clear();
                bluetoothPort = BluetoothPort.getInstance();
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Toast.makeText(getApplicationContext(), "Device Not Support Bluetooth", Toast.LENGTH_SHORT).show();
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                if (!mBluetoothAdapter.isDiscovering()) {
                    clearBtDevData();
                    mBluetoothAdapter.startDiscovery();
                } else {
                    mBluetoothAdapter.cancelDiscovery();
                }
                discoveryResult = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String key;
                        BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (remoteDevice != null) {
                            if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                            } else {
                                key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                            }
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                };
//                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                searchStart = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                    }
                };
//                registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
                searchFinish = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                    }
                };
//                registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
                View dialogView = mInflater.inflate(R.layout.dialog_bluetoothlist, null);
                initDialog(dialogView);
                AlertDialog dialog = showDialog(dialogView);
                list.setTag(dialog);
                return true;
            }
        });
    }

    private void initDialog(View dialogView) {
        list = (ListView) dialogView.findViewById(R.id.ListView01);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.VISIBLE);
        list.setAdapter(adapter);
        addPairedDevices();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (AlertDialog) list.getTag();
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Print Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                BluetoothDevice btDev = remoteDevices.elementAt(arg2);
                try {
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    bTDevice = btDev.getAddress();
                    btnBTPrinter.setSummary(bTDevice);
                    //sharedPreferences
                    sharedPreferences.edit().putString("BTDevice", bTDevice).commit();
                    btConn(btDev);
                    AlertDialog dialog = (AlertDialog) list.getTag();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        if ((connectionTask != null) && (connectionTask.getStatus() == AsyncTask.Status.RUNNING)) {
            connectionTask.cancel(true);
            if (!connectionTask.isCancelled())
                connectionTask.cancel(true);
            connectionTask = null;
        }
        connectionTask = new connTask();
        connectionTask.execute(btDev);
    }

    private void addPairedDevices() {
        BluetoothDevice pairedDevice;
        Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
        while (iter.hasNext()) {
            pairedDevice = iter.next();
            remoteDevices.add(pairedDevice);
            adapter.add(pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]");
        }
    }

    public AlertDialog showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(null).setView(view);
        return builder.show();
    }

    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onPause();
    }

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_ADDRESS)) {
                Preferences();
            } else if (key.equals(KEY_PORT)) {
                Preferences();
            } else if (key.equals(KEY_Virtual)) {
                Preferences();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(), StockUpdateActivity.class);
        startActivity(in);
        finish();
    }

    private void Preferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_soap, false);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        address = sharedPreferences.getString(KEY_ADDRESS, null);
        port = sharedPreferences.getString(KEY_PORT, null);
        virtual = sharedPreferences.getString(KEY_Virtual, null);
        if (port.equalsIgnoreCase("0") && virtual.equalsIgnoreCase("")) {//http://101.100.185.235/ucs_ws/iosservice.asmx
            baseUrl = "http://" + address + "/";
        } else if (port.equalsIgnoreCase("0")) {
            baseUrl = "http://" + address + "/" + virtual + "/";
        } else if (virtual.equalsIgnoreCase("")) {
            baseUrl = "http://" + address + ":" + port + "/";
        } else {
            baseUrl = "http://" + address + ":" + port + "/" + virtual + "/";
        }
        baseUrl = baseUrl + "androidservice.asmx";
        sharedPreferences.edit().putString(KEY_BaseURL, baseUrl).commit();

        if (!sharedPreferences.contains(KEY_ADDRESS)) {
            sharedPreferences.edit().putString(KEY_ADDRESS, "").commit();
        }
        findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, ""));
        findPreference(KEY_PORT).setSummary(sharedPreferences.getString(KEY_PORT, ""));
        findPreference(KEY_Virtual).setSummary(sharedPreferences.getString(KEY_Virtual, ""));
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
                    Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(in);
                    finish();
                    return true;
                }
            }

            //from Right to left
            if (e1.getX() > e2.getX()) {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    onSwipeLeft();
//                    Toast.makeText(getApplicationContext(),"From Right to Left",Toast.LENGTH_SHORT).show();

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

    class connTask extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(SoapSettings.this);

        @Override
        protected void onPreExecute() {
            dialog.setTitle("BlueTooth");
            dialog.setMessage("Connecting");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
//				bluetoothPort.connect(params[0]);
                setBTSocket(params[0]);
                connectCommon();
                lastConnAddr = params[0].getAddress();
                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result.intValue() == 0)    // Connection success.
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                // UI
//                connectButton.setText(getResources().getString(R.string.dev_disconn_btn));
                list.setEnabled(false);
//                btAddrBox.setEnabled(false);
//                searchButton.setEnabled(false);
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Device connected", Toast.LENGTH_SHORT);
                toast.show();
            } else    // Connection failed.
            {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    private void setBTSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            this.setSecureSocket(device, false);
        } else {
            this.setSecureSocket(device, true);
        }
    }

    private void connectCommon() throws IOException {
        PortMediator pm = PortMediator.getInstance();
        if (this.socket != null) {
            byte[] LF = new byte[]{(byte) 10};
            this.socket.connect();
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
            this.connected = true;
            pm.setIs(this.is);
            pm.setOs(this.os);
            os.write(0x0D);
            os.write(0x0D);
            os.write(0x0D);
            os.write(LF);
           /*  ESC_POS ec_pos=new ESC_POS();

//            os.write(ec_pos.print_and_feed_lines((byte) 3));
           os.write(ec_pos.select_position_hri((byte) 2));
            os.write(ec_pos.print_bar_code(ESC_POS.BarCode.CODE39, "IV00007"));
            os.write(ec_pos.print_linefeed());
            os.write(ec_pos.print_and_feed_lines((byte) 3));

            os.write(ec_pos.print_and_feed_lines((byte) 2));*/
          /*  os.write(ec_pos.barcode_height((byte) 80));
            os.write(ec_pos.justification_center());
            os.write(ec_pos.select_position_hri((byte) 1));
            os.write(ec_pos.print_bar_code(ESC_POS.BarCode.CODE39, "IV00007"));
            os.write(ec_pos.print_linefeed());*/
            os.flush();
            os.close();
        } else {
            throw new IOException("Bluetooth Socket is null.");
        }
    }

    private void setSecureSocket(BluetoothDevice device, boolean secure) throws IOException {
        if (secure) {
            this.socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } else {
            this.socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        }
    }
}
