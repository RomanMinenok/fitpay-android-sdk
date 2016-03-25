package com.fitpay.android.wearable.services.payment;

import java.util.UUID;

/**
 * Created by ssteveli on 1/25/16.
 */
public class PaymentServiceConstants {

    public static final UUID SERVICE_UUID = UUID.fromString("d7cc1dc2-3603-4e71-bce6-e3b1551633e0");

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final String CHARACTERISTIC_APDU_CONTROL = "0761f49b-5f56-4008-b203-fd2406db8c20";
    public static final String CHARACTERISTIC_APDU_RESULT = "840f2622-ff4a-4a56-91ab-b1e6dd977db4";

    public static final UUID CHARACTERISTIC_APDU_CONTROL_PARCEL_UUID = UUID.fromString(CHARACTERISTIC_APDU_CONTROL);

    public static final String CHARACTERISTIC_CONTINUATION_CONTROL = "cacc2825-0a2b-4cf2-a1a4-b9db27691382";
    public static final String CHARACTERISTIC_CONTINUATION_PACKET = "52d26993-6d10-4080-8166-35d11cf23c8c";

    public static final String CHARACTERISTIC_SECURE_ELEMENT_ID = "1251697c-0826-4166-a3c0-72704954c32d";

    public static final String CHARACTERISTIC_NOTIFICATION = "37051cf0-d70e-4b3c-9e90-0f8e9278b4d3";

    public static final String CHARACTERISTIC_SECURITY_WRITE = "e4bbb38f-5aaa-4056-8cf0-57461082d598";
    public static final String CHARACTERISTIC_SECURITY_STATE = "ab1fe5e7-4e9d-4b8c-963f-5265dc7de466";

    public static final String CHARACTERISTIC_DEVICE_RESET = "50b50f72-d10a-444b-945d-d574bd67ec91";
    public static final String CHARACTERISTIC_APPLICATION_CONTROL = "6fea71ab-14ca-4921-b166-e8742e349975";


}
