package com.steelmate.androidbleadvertisesettings;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 1、是否过滤
 * 2、调整发送功率
 * 3、调整发送持续时间*100ms
 * 4、发送的频率
 * 5、接收的频率
 * 6、扫描可以开关
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText mEditTextSend;
    private TextView mTextViewReceive;
    private View     mButtonScan;
    private View     mButtonAdvertise;

    private List<String> mPerms = new ArrayList<>();

    private static byte[] EDDYSTONE_SERVER_DATAS = {
//            0x02, 0x01, 0x02,
//            0x1b, 0x16, (byte) 0xcd, (byte) 0xab, // uuid
            0x17, 0x16, (byte) 0xAA, (byte) 0xFE, // UID type's len is 0x17
            0x00,               // UID type
            0x08,               // tx power
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, // NID
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,                         // BID
            (byte) 0xff, (byte) 0xff          // RFU
    };
    private        View   mButtonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextSend = findViewById(R.id.editTextSend);
        mTextViewReceive = findViewById(R.id.textViewReceive);
        mButtonScan = findViewById(R.id.buttonScan);
        mButtonSettings = findViewById(R.id.buttonSettings);
        mButtonAdvertise = findViewById(R.id.buttonAdvertise);
        CheckBox checkBoxScan = findViewById(R.id.checkBoxScan);


        mButtonSettings.setOnClickListener(mOnClickListener);
        mButtonScan.setOnClickListener(mOnClickListener);
        mButtonAdvertise.setOnClickListener(mOnClickListener);
        checkBoxScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BleAdvertisingModel.getInstance().getBleAvertisingSettings().setScan(isChecked);
            }
        });

        mPerms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        mPerms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mPerms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        AppCommonPermissionsUtils.requestPermissions(this, mPerms, 1);

        BluetoothManager bluetoothManager = (BluetoothManager) AppCommonContextUtils.getApp().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
        BleAdvertisingModel.getInstance().setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                //扫描到的广播数据
                ScanRecord record = result.getScanRecord();
                if (record != null) {
                    try {
                        onReceiveData(record);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        });
        checkBoxScan.setChecked(BleAdvertisingModel.getInstance().getBleAvertisingSettings().isScan());
    }

    private void onReceiveData(ScanRecord record) {
        MyLogUtils.d(TAG, "onScanResult ScanRecord = " + record.toString());
        String rawData          = AppCommonConvertUtils.bytes2HexString(record.getBytes());
        String serviceData      = AppCommonConvertUtils.bytes2HexString(record.getServiceData(BleAdvertisingModel.ADVERTISER_SERVICE_DATA_UUID));
        String manufacturerId   = AppCommonConvertUtils.numberToHex(BleAdvertisingModel.MANUFACTURER_ID, 2);
        String manufacturerData = AppCommonConvertUtils.bytes2HexString(record.getManufacturerSpecificData(BleAdvertisingModel.MANUFACTURER_ID));
        String deviceName       = record.getDeviceName();
        mTextViewReceive.setText("接收的原始数据:" +
                                         "\n" + rawData +
                                         "\n" + "接收的serviceData数据:" +
                                         "\n" + serviceData +
                                         "\n" + "接收的manufacturerId:" +
                                         "\n" + manufacturerId +
                                         "\n" + "接收的manufacturerData数据:" +
                                         "\n" + manufacturerData +
                                         "\n" + "接收的BLE的名称:" +
                                         "\n" + deviceName
                                );
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            if (view == mButtonSettings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
            if (view == mButtonAdvertise) {
                String hex = mEditTextSend.getText().toString().trim();
                if (TextUtils.isEmpty(hex)) {
                    return;
                }
                byte[] bytes = AppCommonConvertUtils.hexString2Bytes(hex);
                if (bytes == null) {
                    return;
                }
                BleAdvertisingModel.getInstance().startAdvertisingServiceData(bytes);
            }
        }
    };


}
