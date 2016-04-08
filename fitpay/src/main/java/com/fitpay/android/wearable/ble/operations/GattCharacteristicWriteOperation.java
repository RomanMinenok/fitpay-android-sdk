package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattCharacteristicWriteOperation extends GattOperation {

    private final byte[] mValue;

    public GattCharacteristicWriteOperation(UUID service, UUID characteristic, byte[] value) {
        mService = service;
        mCharacteristic = characteristic;
        mValue = value;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("writing to " + mCharacteristic);
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        characteristic.setValue(mValue);
        gatt.writeCharacteristic(characteristic);
    }
}