package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.ContinuationControlBeginMessage;
import com.fitpay.android.wearable.ble.message.ContinuationControlEndMessage;
import com.fitpay.android.wearable.ble.message.ContinuationPacketMessage;
import com.fitpay.android.wearable.ble.utils.Hex;

/**
 * Created by Vlad on 05.04.2016.
 */
public class GattApduComplexOperation extends GattApduBaseOperation {

    public GattApduComplexOperation(ApduCommand command) {
        super(command.getSequence());

        /*begin*/
        ContinuationControlBeginMessage beingMsg = new ContinuationControlBeginMessage()
                .withUuid(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL);

        GattOperation continuationStartWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                beingMsg.getMessage());

        mNestedQueue.add(continuationStartWrite);

        /*packets*/
        int currentPos = 0;
        int sortOrder = 0;
        byte[] dataToSend = null;

        byte[] byteSequenceId = Hex.sequenceToBytes(command.getSequence());
        byte[] apduCommand = command.getCommand();
        byte[] msg = new byte[3 + apduCommand.length];

        System.arraycopy(byteSequenceId, 0, msg, 1, byteSequenceId.length);
        System.arraycopy(apduCommand, 0, msg, 3, apduCommand.length);

        while (currentPos < msg.length) {
            int len = Math.min(msg.length - currentPos, ContinuationPacketMessage.getMaxDataLength());
            dataToSend = new byte[len];
            System.arraycopy(msg, currentPos, dataToSend, 0, len);

            ContinuationPacketMessage packetMessage = new ContinuationPacketMessage()
                    .withSortOrder(sortOrder)
                    .withData(dataToSend);

            GattOperation packetWrite = new GattCharacteristicWriteOperation(
                    PaymentServiceConstants.SERVICE_UUID,
                    PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                    packetMessage.getMessage());

            mNestedQueue.add(packetWrite);

            currentPos += len;
            sortOrder++;
        }

        /*end*/
        ContinuationControlEndMessage endMsg = new ContinuationControlEndMessage()
                .withPayload(msg);

        GattOperation continuationEndWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                endMsg.getMessage());

        mNestedQueue.add(continuationEndWrite);
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        //wait for ApduResultMessage
    }
}