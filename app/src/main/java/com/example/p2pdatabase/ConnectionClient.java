package com.example.p2pdatabase;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import compression.Compress;


public class ConnectionClient {

    ArrayList<String> ConnectedEndpointIDs = new ArrayList<String>();


    // A value that uniquely identifies the app. Used for multiplayer functionality
    public String getServiceId() {return "P2PDatabase.Telephone";}

    private String Usern = "anonymous";

    private Context C;

    public void startDiscovery(){
        Nearby.getConnectionsClient(C).startDiscovery(
                getServiceId(),
                mEndpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_POINT_TO_POINT))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're discovering!
                                //Toast.makeText(c, "discovering", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start discovering.
                                //Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    public void stopDiscovery(){
        Nearby.getConnectionsClient(C).stopDiscovery();
    }

    public void startAdvertise(){


        Nearby.getConnectionsClient(C).startAdvertising(
                Usern,
                "P2Pdatabase.Telephone", // serviceId
                mConnectionLifecycleCallback,
                new AdvertisingOptions(Strategy.P2P_CLUSTER))//multiple people are connected
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                //Toast toast = Toast.makeText(c, "Successful Advert", Toast.LENGTH_SHORT);
                                //toast.show();
                            }
                        })

                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                            Toast toast = Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT);
//                            toast.show();
                            }
                        });
    }
    public void stopAdvert(){
        Nearby.getConnectionsClient(C).stopAdvertising();
    }

    public void setContext(Context c){
        C = c;
    }

    public void connect(String e){
        Nearby.getConnectionsClient(C).requestConnection("swag", e, mConnectionLifecycleCallback)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We successfully requested a connection. Now both sides
                                // must accept before the connection is established.

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Nearby Connections failed to request the connection.
                            }
                        });
    }

    public void disconnect(){
        Nearby.getConnectionsClient(C).stopAllEndpoints();
        ConnectedEndpointIDs = new ArrayList<String>();
        startDiscovery();
        startAdvertise();
    }


    public void sendPayload(int i){

        File[] fs = (new File(Compress.inPath).listFiles());
        if(fs!=null) {
            for(File f: fs) {
                f.delete();
            }
        }

        ArrayList<File> files = Globals.sql.getFiles(Globals.android_id);
        for (File s: files){
            s.renameTo(new File(Compress.inPath + s.getName()));
        }

        Compress.zipAll();

        File fileToSend = new File(Compress.outPath, "data.zip"); // sets file to send as the zipped folder


        try {
            Payload filePayload = Payload.fromFile(fileToSend); //sets the payload to be the file
            Nearby.getConnectionsClient(C).sendPayload(ConnectedEndpointIDs.get(i), filePayload); // sends the payload
            fileToSend.delete();
        } catch (FileNotFoundException e) {
            Log.e("P2PDataBase", "File not found", e);
        }
    }

    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // We may want to make opponentList a list of key value pairs later
                    // (key = endpointId, value = discoveredEndpointInfo)
                    connect(endpointId);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                }
            };

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            //Toast toast1 = Toast.makeText(C,"got something", Toast.LENGTH_SHORT);
            // toast1.show();
            Payload.File f = payload.asFile();

            f.asJavaFile().renameTo(new File(Compress.outPath + f.asJavaFile().getName()));
            Compress.unzipAll();
            f.asJavaFile().delete();

            File[] fs = (new File(Compress.inPath).listFiles());
            if(fs!=null) { //some JVMs return null for empty dirs
                for(File fi: fs) {
                    Globals.sql.insertFile(fi);
                }
            }

            File[] fils = (new File(Compress.inPath).listFiles());
            if(fils!=null) {
                for(File fil: fils) {
                    fil.delete();
                }
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        }
    };

    final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(C).acceptConnection(endpointId, mPayloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            ConnectedEndpointIDs.add(endpointId);
                                sendPayload(ConnectedEndpointIDs.size()- 1);
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
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                }
            };
}
