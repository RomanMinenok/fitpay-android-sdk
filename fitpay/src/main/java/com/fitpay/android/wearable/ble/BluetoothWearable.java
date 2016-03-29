package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.wearable.ble.callbacks.CharacteristicChangeListener;
import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.SecurityStateMessage;
import com.fitpay.android.wearable.ble.operations.GattCharacteristicReadOperation;
import com.fitpay.android.wearable.ble.operations.GattCharacteristicWriteOperation;
import com.fitpay.android.wearable.ble.operations.GattConnectOperation;
import com.fitpay.android.wearable.ble.operations.GattDeviceCharacteristicsOperation;
import com.fitpay.android.wearable.ble.operations.GattOperation;
import com.fitpay.android.wearable.ble.operations.GattOperationBundle;
import com.fitpay.android.wearable.ble.operations.GattSetIndicationOperation;
import com.fitpay.android.wearable.model.Wearable;
import com.orhanobut.logger.Logger;

import java.util.UUID;

/**
 * Created by Vlad on 29.03.2016.
 */
public final class BluetoothWearable extends Wearable {

    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private GattManager mGattManager;

    public BluetoothWearable(Context context, BluetoothDevice device) {
        super(context, device.getAddress());

        mDevice = device;

        BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager == null) {
            Logger.e("unable to initialize bluetooth manager");
            return;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.e("unable to obtain bluetooth adapter");
            return;
        }

        initialized = true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @Override
    public void connect() {
        if (mBluetoothAdapter == null || StringUtils.isEmpty(mAddress)) {
            Logger.w("BluetoothAdapter not initialized or unspecified address.");
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
        if (device == null) {
            Logger.w("Device not found.  Unable to connect.");
            return;
        }

        mGattManager = new GattManager(mContext, device);
        mGattManager.queue(new GattConnectOperation());

        startDataIndication();
    }

    @Override
    public void disconnect() {
        mGattManager.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @Override
    public void close() {
        mGattManager.close();
        mGattManager = null;
    }

    @Override
    public void getDeviceInfo() {
        GattOperationBundle bundle = new GattOperationBundle();
        bundle.addOperation(new GattDeviceCharacteristicsOperation(mDevice));
        mGattManager.queue(bundle);
    }

    @Override
    public void getSecurityState() {
        GattOperation getNFCOperation = new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        RxBus.getInstance().post(new SecurityStateMessage().withData(characteristic));
                    }
                });
        mGattManager.queue(getNFCOperation);
    }

    @Override
    public void setSecurityState(boolean enabled) {
        GattOperation setNFCOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE,
                new byte[]{(byte) (enabled ? 0x01 : 0x00)}
        );
        mGattManager.queue(setNFCOperation);
    }

    @Override
    public void sendAdpuPackage(byte[] value) {

    }

    @Override
    public void resetDevice() {
        GattOperation resetOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_DEVICE_RESET,
                new byte[]{0x01}
        );
        mGattManager.queue(resetOperation);
    }

    private void startDataIndication() {

        GattOperationBundle bundle = new GattOperationBundle();

        GattOperation nfcIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(nfcIndication);

        GattOperation adpuResultIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(adpuResultIndication);

        GattOperation continuationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(continuationControlIndication);

        GattOperation continuationPacketIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(continuationPacketIndication);

        GattOperation transactionIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(transactionIndication);

        GattOperation applicaitonControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(applicaitonControlIndication);

        mGattManager.queue(bundle);

        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE);
        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT);
        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL);
        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET);
        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION);
        addCharacteristicListener(PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL);
    }

    private void addCharacteristicListener(UUID characteristicUuid){
        mGattManager.addCharacteristicChangeListener(characteristicUuid, characteristicChangeListener);
    }

    private CharacteristicChangeListener characteristicChangeListener = new CharacteristicChangeListener() {
        @Override
        public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();

            if(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE.equals(uuid)){
                RxBus.getInstance().post(new SecurityStateMessage().withData(characteristic.getValue()));
            } else if(PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT.equals(uuid)){

            } else if(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL.equals(uuid)){

            } else if(PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET.equals(uuid)){

            } else if(PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION.equals(uuid)){

            } else if(PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL.equals(uuid)){

            }
        }
    };
}
