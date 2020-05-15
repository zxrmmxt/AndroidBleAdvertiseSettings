package com.steelmate.androidbleadvertisesettings;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author xt on 2019/11/21 14:44
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleAdvertisingModel {
    private static final String              TAG                     = BleAdvertisingModel.class.getSimpleName();
    private static       BleAdvertisingModel sBleAdvertisingModel;
    /**
     * 00001000-0000-1000-8000-00805F9B34FB
     * 0000100A-0000-1000-8000-00805F9B34FB
     * 广为人知的uuid
     */
    private static final ParcelUuid          SERVICE_UUID_WELL_KNOWN = ParcelUuid.fromString("00001000-0000-1000-8000-00805F9B34FB");

    /**
     * 蓝牙广播中对服务 UUID 格式定义都有三种 16 bit UUID、32 bit UUID、128 bit UUID。
     * 但是熟悉安卓开发的小伙伴都知道接口都 UUID 格式，fromString 时候 16bit 的 UUID 该咋办呢？
     * 16bit 和 32bit 的 UUID 与 128bit 的值之间转换关系：
     * 128_bit_UUID = 16_bit_UUID * 2^96 + Bluetooth_Base_UUID
     * 128_bit_UUID = 32_bit_UUID * 2^96 + Bluetooth_Base_UUID
     * 其中 Bluetooth_Base_UUID 定义为 00000000-0000-1000-8000-00805F9B34FB
     * 如果你想说这是啥呀，那我这样说你应该可以明白点：
     * 若 16 bit UUID为xxxx，那么 128 bit UUID 为 0000xxxx-0000-1000-8000-00805F9B34FB
     * 若 32 bit UUID为xxxxxxxx，那么 128 bit UUID 为 xxxxxxxx-0000-1000-8000-00805F9B34FB
     */
    public static final ParcelUuid ADVERTISE_SERVICE_UUID          = getParcelUuid("FFF7");
    public static final ParcelUuid ADVERTISER_SERVICE_DATA_UUID    = getParcelUuid("FFF5");
    public static final ParcelUuid SCAN_RESPONSE_SERVICE_UUID      = getParcelUuid("FFF8");
    public static final ParcelUuid SCAN_RESPONSE_SERVICE_DATA_UUID = getParcelUuid("FFF9");

    /**
     * 厂商id，自己定义的2个字节的值
     * 如果定义成short，最高位是负数会出异常
     */
    public static final int MANUFACTURER_ID = 0xFFF6;

    private Application mApplication;
    /**
     * 1[0x03中的uuid]
     * 2[扫描过滤的uuid]
     */
    private ParcelUuid  mServiceUuid;
    private ParcelUuid  mServiceDataUuid;
    private int         mManufacturerId;

    private SampleAdvertiseCallback mAdvertiseCallback = new SampleAdvertiseCallback();
    private BluetoothLeAdvertiser   mBluetoothLeAdvertiser;
    private BluetoothLeScanner      mBluetoothLeScanner;
    private ScanCallback            mInnerScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null) {
                return;
            }
            if (mScanCallback != null) {
                mScanCallback.onScanResult(callbackType, result);
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
    };
    private ScanCallback            mScanCallback;

    private BleAvertisingSettings mBleAvertisingSettings = new BleAvertisingSettings();
    private BluetoothAdapter      mBluetoothAdapter;


    private BleAdvertisingModel() {
        BluetoothManager bluetoothManager = (BluetoothManager) AppCommonContextUtils.getApp().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public static BleAdvertisingModel getInstance() {
        if (sBleAdvertisingModel == null) {
            sBleAdvertisingModel = new BleAdvertisingModel();
        }
        return sBleAdvertisingModel;
    }

    /**
     * 此方法是初始化方法，在{@link android.app.Application#onCreate}方法中调用
     */
    public void init(Application application, ParcelUuid serviceUuid, ParcelUuid serviceDataUuid, int manufacturerId) {
        mApplication = application;
        mServiceUuid = serviceUuid;
        mServiceDataUuid = serviceDataUuid;
        mManufacturerId = manufacturerId;
    }

    public void enable(Activity activity, int requestCode) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    public boolean isEnabled() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * 设置频率:
     * ADVERTISE_MODE_LOW_LATENCY 100ms
     * ADVERTISE_MODE_LOW_POWER 1s
     * ADVERTISE_MODE_BALANCED  250ms
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        //设置广播模式，以控制广播的功率和延迟。
        settingsBuilder.setAdvertiseMode(mBleAvertisingSettings.getAdvertiseMode());
        //发射功率级别
        settingsBuilder.setTxPowerLevel(mBleAvertisingSettings.getTxPowerLevel());
        //不得超过180*1000毫秒。值为0将禁用时间限制。
        settingsBuilder.setTimeout(mBleAvertisingSettings.getTimeoutMillis());
        //设置是否可以连接
        settingsBuilder.setConnectable(false);
        return settingsBuilder.build();
    }

    /**
     * 开始蓝牙广播
     *
     * @param advertiseData 广播包
     * @param scanResponse  响应包，可以和广播包一模一样
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertising(AdvertiseData advertiseData, AdvertiseData scanResponse) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                MyToastUtils.showShortToast("蓝牙未开启");
                return;
            }
            //如果芯片组支持多广播，则返回true
            if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                MyToastUtils.showShortToast("该手机芯片不支持广播");
                return;
            }
        }
        if (mBluetoothLeAdvertiser == null) {
            if (mBluetoothAdapter != null) {
                mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            }
        }
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser.startAdvertising(buildAdvertiseSettings(), advertiseData, scanResponse, mAdvertiseCallback);
        }
    }

    /**
     * 开始蓝牙广播
     *
     * @param serviceData 最大长度为23的字节数组
     * @return <p>
     * {@link AdvertiseData.Builder#addServiceUuid(ParcelUuid)} List<ParcelUuid> mServiceUuids，可以添加多个
     * {@link AdvertiseData.Builder#addServiceData(ParcelUuid, byte[])} Map<ParcelUuid, byte[]> mServiceData，可以添加多个
     * {@link AdvertiseData.Builder#addManufacturerData(int, byte[])} SparseArray<byte[]> mManufacturerSpecificData，可以添加多个
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingServiceData(ParcelUuid serviceUuid, byte[] serviceData) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        //是否包含设备名称
        dataBuilder.setIncludeDeviceName(false);
//        mBluetoothAdapter.setName("xxxx");
        //是否包含发射功率级
        dataBuilder.setIncludeTxPowerLevel(false);
        if (serviceUuid != null) {
            dataBuilder.addServiceUuid(serviceUuid);
        }
        if (serviceData == null) {
            serviceData = new byte[0];
        }
        dataBuilder.addServiceData(mServiceDataUuid, serviceData);
        AdvertiseData advertiseData = dataBuilder.build();
        startAdvertising(advertiseData, null);
    }

    /**
     * 开始蓝牙广播
     *
     * @param serviceData 最大长度为23的字节数组
     * @return <p>
     * {@link AdvertiseData.Builder#addServiceUuid(ParcelUuid)} List<ParcelUuid> mServiceUuids，可以添加多个
     * {@link AdvertiseData.Builder#addServiceData(ParcelUuid, byte[])} Map<ParcelUuid, byte[]> mServiceData，可以添加多个
     * {@link AdvertiseData.Builder#addManufacturerData(int, byte[])} SparseArray<byte[]> mManufacturerSpecificData，可以添加多个
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingServiceData(byte[] serviceData) {
        startAdvertisingServiceData(null, serviceData);
    }

    /**
     * 开始蓝牙广播
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingManufacturerData(ParcelUuid serviceUuid, byte[] manufacturerSpecificData) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        //是否包含设备名称
        dataBuilder.setIncludeDeviceName(false);
        //是否包含发射功率级
        dataBuilder.setIncludeTxPowerLevel(false);
        if (serviceUuid != null) {
            dataBuilder.addServiceUuid(serviceUuid);
        }
        if (manufacturerSpecificData == null) {
            manufacturerSpecificData = new byte[0];
        }
        dataBuilder.addManufacturerData(mManufacturerId, manufacturerSpecificData);
        startAdvertising(dataBuilder.build(), null);
    }

    /**
     * 开始蓝牙广播
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingManufacturerData(byte[] manufacturerSpecificData) {
        startAdvertisingManufacturerData(null, manufacturerSpecificData);
    }

    /**
     * 开始蓝牙广播
     *
     * @param name 广播蓝牙名称，用16进制字符串，不然字节会丢失
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingName(ParcelUuid serviceUuid, String name) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        //是否包含设备名称
        dataBuilder.setIncludeDeviceName(false);
        mBluetoothAdapter.setName(name);
        //是否包含发射功率级
        dataBuilder.setIncludeTxPowerLevel(false);
        if (serviceUuid != null) {
            dataBuilder.addServiceUuid(serviceUuid);
        }
        startAdvertising(dataBuilder.build(), null);
    }

    /**
     * 开始蓝牙广播
     *
     * @param name 广播蓝牙名称，用16进制字符串，不然字节会丢失
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertisingName(String name) {
        startAdvertisingName(null, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            MyLogUtils.d(TAG, "Advertising start failure");
            /*if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            }*/
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            MyLogUtils.d(TAG, "Advertising start success");
            /*if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            }*/
        }
    }

    /***********************************************************************************/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(mBleAvertisingSettings.getScanMode());
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<ScanFilter> buildScanFilters(ParcelUuid serviceUuid) {
        List<ScanFilter>   scanFilters = new ArrayList<>();
        ScanFilter.Builder builder     = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        if (mBleAvertisingSettings.isScanFilterServiceUuid()) {
            if (serviceUuid != null) {
                builder.setServiceUuid(serviceUuid);
            }
        }
        scanFilters.add(builder.build());
        return scanFilters;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startScan() {
        if (!isGranted(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})) {
            return;
        }
        if (!isLocationOpen()) {
            return;
        }
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                return;
            }
        }

        if (mBluetoothLeScanner == null) {
            if (mBluetoothAdapter != null) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
        }
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mInnerScanCallback);
            if (mBleAvertisingSettings.isScan()) {
                mBluetoothLeScanner.startScan(buildScanFilters(mServiceUuid), buildScanSettings(), mInnerScanCallback);
                MyLogUtils.d(TAG, "start scan");
            }
        }
    }

    /**
     * 此方法不必调用，只是为了了解BLE广播的数据结构
     * <p>
     * 广播包中包含若干个广播数据单元，广播数据单元也称为 AD Structure。
     * 广播数据单元 = 长度值Length + AD type + AD Data。
     * 03 03 0010 第一个字节的值是0x03,转换为十进制就是3，表示后面3字节为广播数据单元的数据内容。
     * 长度值Length只占一个字节，并且位于广播数据单元的第一个字节。
     * 在广播数据单元的数据部分中，第一个字节代表数据类型（AD type），决定数据部分表示的是什么数据。（即广播数据单元第二个字节为AD type）
     *
     * @param advertisingData
     * @return
     */
    public static ParsedAd parseData(byte[] advertisingData) {
        ParsedAd parsedAd = new ParsedAd();
        parsedAd.uuids = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(advertisingData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) {
                break;
            }

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01:
                    // Flags
                    parsedAd.flags = buffer.get();
                    length--;
                    break;

                case 0x02:
                    // Partial list of 16-bit UUIDs
                case 0x03:
                    // Complete list of 16-bit UUIDs
                case 0x14:
                    // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x04:
                    //Partial list of 32 bit service UUIDs
                case 0x05:
                    //Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
                        length -= 4;
                    }
                    break;

                case 0x06:
                    //Partial list of 128-bit UUIDs
                case 0x07:
                    //Complete list of 128-bit UUIDs
                case 0x15:
                    //List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        parsedAd.uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                case 0x16:
                    if (length >= 2) {
                        //16 bit UUID Service: TYPE = 0x16, 前 2 字节是 UUID，后面是 Service 的数据
                        short uuid = buffer.getShort();
                        length -= 2;
                        byte[] serviceData = new byte[length];
                        buffer.get(serviceData, 0, length);
                        parsedAd.serviceData = serviceData;
                        length = 0;
                    }
                    break;

                case 0x08:
                    //Short local device name
                case 0x09:
                    //Complete local device name
                    byte[] nameBytes = new byte[length];
                    buffer.get(nameBytes, 0, length);
                    parsedAd.localName = new String(nameBytes).trim();
                    length = 0;
                    break;

                case (byte) 0xFF:
                    //厂商自定义数据: TYPE = 0xFF。 厂商数据中，前两个字节表示厂商ID,剩下的是厂商自定义的数据。
                    parsedAd.manufacturerId = buffer.getShort();
                    length -= 2;
                    byte[] manufacturerData = new byte[length];
                    buffer.get(manufacturerData, 0, length);
                    parsedAd.manufacturerData = manufacturerData;
                    length = 0;
                    break;

                default:
                    //skip
                    break;
            }

            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return parsedAd;
    }

    public void setScanCallback(ScanCallback scanCallback) {
        mScanCallback = scanCallback;
    }

    public class BleAvertisingSettings {
        /**
         * 扫描时是否过滤serviceUUID
         */
        private boolean isScanFilterServiceUuid = true;
        /**
         * 发送广播的功率
         */
        private int     txPowerLevel            = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;
        /**
         * 发送广播的持续时间
         */
        private int     timeoutMillis           = 50 * 100;
        /**
         * 发送广播的频率
         */
        private int     advertiseMode           = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;
        /**
         * 扫描广播的频率
         */
        private int     scanMode                = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;
        /**
         * 是否扫描
         */
        private boolean isScan                  = true;

        public boolean isScanFilterServiceUuid() {
            return isScanFilterServiceUuid;
        }

        public void setScanFilterServiceUuid(boolean scanFilterServiceUuid) {
            isScanFilterServiceUuid = scanFilterServiceUuid;
        }

        public int getTxPowerLevel() {
            return txPowerLevel;
        }

        public void setTxPowerLevel(int txPowerLevel) {
            this.txPowerLevel = txPowerLevel;
        }

        public int getTimeoutMillis() {
            return timeoutMillis;
        }

        public void setTimeoutMillis(int timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        public int getAdvertiseMode() {
            return advertiseMode;
        }

        public void setAdvertiseMode(int advertiseMode) {
            this.advertiseMode = advertiseMode;
        }

        public int getScanMode() {
            return scanMode;
        }

        public void setScanMode(int scanMode) {
            this.scanMode = scanMode;
        }

        public boolean isScan() {
            return isScan;
        }

        public void setScan(boolean scan) {
            isScan = scan;
            startScan();
        }

    }

    public BleAvertisingSettings getBleAvertisingSettings() {
        return mBleAvertisingSettings;
    }

    private static ParcelUuid getParcelUuid(String uuid16Bit) {
        return ParcelUuid.fromString("0000" + uuid16Bit + "-0000-1000-8000-00805F9B34FB");
    }

    private boolean isGranted(final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(mApplication, permission);
    }

    private boolean isLocationOpen() {
        LocationManager locationManager = (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }
}
