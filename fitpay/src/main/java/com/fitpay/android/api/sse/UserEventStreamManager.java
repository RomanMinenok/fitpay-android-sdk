package com.fitpay.android.api.sse;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.services.FitPayClient;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Response;


/**
 * Created by ssteveli on 3/15/18.
 */

public class UserEventStreamManager {
    private static ConcurrentHashMap<String, UserEventStream> streams = new ConcurrentHashMap<>();

    public static UserEventStream subscribe(
            final String userId,
            final IPaymentDeviceConnector connector,
            final Device device) throws IOException {

        final String streamKey = calculateStreamKey(userId, device);
        UserEventStream stream = streams.get(streamKey);

        if (stream == null) {
            FitPayClient client = ApiManager.getInstance().getClient();
            Response<User> user = client.getUser(userId).execute();

            if (user.isSuccessful()) {
                stream = new UserEventStream(user.body(), connector, device);
                UserEventStream existing = streams.putIfAbsent(streamKey, stream);
                if (existing != null) {
                    // whoops, another thread beat us to subscribing to this event stream... no need for this new one
                    stream.close();
                    stream = existing;
                }
            }

        }

        return stream;
    }

    public static void unsubscribe(final String userId, final Device device) {
        final String streamKey = calculateStreamKey(userId, device);
        UserEventStream stream = streams.remove(streamKey);

        if (stream != null) {
            stream.close();
        }
    }

    private static String calculateStreamKey(String userId, Device device) {
        return userId + ":" + device.getDeviceIdentifier();
    }
}
