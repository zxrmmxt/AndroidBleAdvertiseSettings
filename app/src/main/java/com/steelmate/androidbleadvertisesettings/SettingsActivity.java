package com.steelmate.androidbleadvertisesettings;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xt on 2019/11/21 15:05
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SettingsActivity extends AppCompatActivity {

    private ArrayAdapter<TxPowerLevel> mTxPowerLevelArrayAdapter;
    private List<TxPowerLevel>         mTxPowerLevels;
    private List<AdvertiseMode>        mAdvertiseModes;
    private List<ScanMode>             mScanModes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        CheckBox         checkBoxFilterUuid      = findViewById(R.id.checkBoxFilterUuid);
        AppCompatSpinner spinnerTxPowerLevel     = findViewById(R.id.spinnerTxPowerLevel);
        View             buttonLess              = findViewById(R.id.buttonLess);
        final EditText   editTextDuringRate      = findViewById(R.id.editTextDuringRate);
        View             buttonAdd               = findViewById(R.id.buttonAdd);
        AppCompatSpinner spinnerSendFrequency    = findViewById(R.id.spinnerSendFrequency);
        AppCompatSpinner spinnerReceiveFrequency = findViewById(R.id.spinnerReceiveFrequency);

        {
            checkBoxFilterUuid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BleAdvertisingModel.getInstance().getBleAvertisingSettings().setScanFilterServiceUuid(isChecked);
                }
            });
            {
                mTxPowerLevels = new ArrayList<>();
                mTxPowerLevels.add(new TxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH, "ADVERTISE_TX_POWER_HIGH"));
                mTxPowerLevels.add(new TxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW, "ADVERTISE_TX_POWER_LOW"));
                mTxPowerLevels.add(new TxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM, "ADVERTISE_TX_POWER_MEDIUM"));
                mTxPowerLevels.add(new TxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW, "ADVERTISE_TX_POWER_ULTRA_LOW"));
                mTxPowerLevelArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.textView, mTxPowerLevels);
                spinnerTxPowerLevel.setAdapter(mTxPowerLevelArrayAdapter);
                spinnerTxPowerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BleAdvertisingModel.getInstance().getBleAvertisingSettings().setTxPowerLevel(mTxPowerLevels.get(position).getTxPowerLevel());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            {
                mAdvertiseModes = new ArrayList<>();
                mAdvertiseModes.add(new AdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY, "ADVERTISE_MODE_LOW_LATENCY"));
                mAdvertiseModes.add(new AdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED, "ADVERTISE_MODE_BALANCED"));
                mAdvertiseModes.add(new AdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER, "ADVERTISE_MODE_LOW_POWER"));
                ArrayAdapter<AdvertiseMode> advertiseModeArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.textView, mAdvertiseModes);
                spinnerSendFrequency.setAdapter(advertiseModeArrayAdapter);
                spinnerSendFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BleAdvertisingModel.getInstance().getBleAvertisingSettings().setAdvertiseMode(mAdvertiseModes.get(position).getAdvertiseMode());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            {
                mScanModes = new ArrayList<>();
                mScanModes.add(new ScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY, "SCAN_MODE_LOW_LATENCY"));
                mScanModes.add(new ScanMode(ScanSettings.SCAN_MODE_LOW_POWER, "SCAN_MODE_LOW_POWER"));
                mScanModes.add(new ScanMode(ScanSettings.SCAN_MODE_BALANCED, "SCAN_MODE_BALANCED"));
                mScanModes.add(new ScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC, "SCAN_MODE_OPPORTUNISTIC"));
                ArrayAdapter<ScanMode> scanModeArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.textView, mScanModes);
                spinnerReceiveFrequency.setAdapter(scanModeArrayAdapter);
                spinnerReceiveFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BleAdvertisingModel.getInstance().getBleAvertisingSettings().setScanMode(mScanModes.get(position).getScanMode());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            buttonLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int timeoutMillisRate = Integer.parseInt(editTextDuringRate.getText().toString().trim());
                    timeoutMillisRate = timeoutMillisRate - 1;
                    if (timeoutMillisRate < 0) {
                        timeoutMillisRate = 0;
                    }
                    editTextDuringRate.setText(timeoutMillisRate + "");
                }
            });
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int timeoutMillisRate = Integer.parseInt(editTextDuringRate.getText().toString().trim());
                    timeoutMillisRate = timeoutMillisRate + 1;
                    editTextDuringRate.setText(timeoutMillisRate + "");
                }
            });

            editTextDuringRate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String inputText = editTextDuringRate.getText().toString().trim();
                    if (TextUtils.isEmpty(inputText)) {
                        inputText = "0";
                        editTextDuringRate.setText(inputText);
                    }
                    int timeoutMillisRate = Integer.parseInt(inputText);
                    BleAdvertisingModel.getInstance().getBleAvertisingSettings().setTimeoutMillis(timeoutMillisRate * 100);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        {
            checkBoxFilterUuid.setChecked(BleAdvertisingModel.getInstance().getBleAvertisingSettings().isScanFilterServiceUuid());
            for (int i = 0; i < mTxPowerLevels.size(); i++) {
                if (mTxPowerLevels.get(i).getTxPowerLevel() == BleAdvertisingModel.getInstance().getBleAvertisingSettings().getTxPowerLevel()) {
                    spinnerTxPowerLevel.setSelection(i, true);
                }
            }
            editTextDuringRate.setText(BleAdvertisingModel.getInstance().getBleAvertisingSettings().getTimeoutMillis() / 100 + "");
            for (int i = 0; i < mAdvertiseModes.size(); i++) {
                if (mAdvertiseModes.get(i).getAdvertiseMode() == BleAdvertisingModel.getInstance().getBleAvertisingSettings().getAdvertiseMode()) {
                    spinnerSendFrequency.setSelection(i, true);
                }
            }
            for (int i = 0; i < mScanModes.size(); i++) {
                if (mScanModes.get(i).getScanMode() == BleAdvertisingModel.getInstance().getBleAvertisingSettings().getScanMode()) {
                    spinnerReceiveFrequency.setSelection(i, true);
                }
            }
        }
    }
}
