package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Strategy;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(
                    String endpointId, ConnectionInfo connectionInfo) {
                // Automatically accept the connection on both sides.
                if(host){
                    // Nearby.getConnectionsClient(MainActivity.this).stopAdvertising(); stop advertising maybe if there are connection issues

                    //Check to see if the connected individual needs to be updated
                        // if they do, begin updating process

                }
                Nearby.getConnectionsClient(MainActivity.this).acceptConnection(endpointId, mPayloadCallback);
            }

            @Override
            public void onConnectionResult(String endpointId, ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        receiver.onConnection();
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        break;
                    default:
                        // Unknown status code
                }
            }
            @Override
            public void onDisconnected(String endpointId){
                connected = false;
                receiver.onDisconnect();
            }
        };


        private void startAdvertising() {
            AdvertisingOptions advertisingOptions =
                    new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
            Nearby.getConnectionsClient(MainActivity.this)
                    .startAdvertising(
                            "anonymous", "P2Pdatabase", connectionLifecycleCallback, advertisingOptions)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We're advertising!
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // We were unable to start advertising.
                            });
        }

    }




}
