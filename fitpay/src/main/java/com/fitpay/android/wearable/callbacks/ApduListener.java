package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.wearable.interfaces.IApduMessage;

/**
 * Created by Vlad on 07.04.2016.
 */
public abstract class ApduListener extends Listener implements IListeners.ApduListener {
    public ApduListener() {
        super();
        mCommands.put(IApduMessage.class, data -> {
            ApduExecutionResult result = (ApduExecutionResult) data;

            switch (result.getState()) {
                case ResponseState.PROCESSED:
                    onApduPackageResultReceived(result);
                    break;

                default:
                    onApduPackageErrorReceived(result);
                    break;
            }
        });
    }
}
