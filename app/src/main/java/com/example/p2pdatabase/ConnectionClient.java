package com.example.p2pdatabase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import compression.Compress;


public class ConnectionClient {

    ArrayList<String> ConnectedEndpointIDs = new ArrayList<String>();


    // A value that uniquely identifies the app. Used for multiplayer functionality
    public String getServiceId() {return "P2PDatabase.Telephone";}

    private String Usern = "anoasdplkasnymous";

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
                                Toast.makeText(C, "discovering", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start discovering.
                                Toast.makeText(C, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    public void stopDiscovery(){
        Nearby.getConnectionsClient(C).stopDiscovery();
    }

    public void startAdvertise(){


        Nearby.getConnectionsClient(C).startAdvertising(
                Usern,
                getServiceId(), // serviceId
                mConnectionLifecycleCallback,
                new AdvertisingOptions(Strategy.P2P_POINT_TO_POINT))//multiple people are connected
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                Toast toast = Toast.makeText(C, "Successful Advert", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })

                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(C, e.getMessage(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
    }
    public void stopAdvert(){
        Nearby.getConnectionsClient(C).stopAdvertising();
    }

    public void setContext(Context c){ C = c;}

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
    }


    public void sendPayload(int i){

        File[] fs = (new File(Compress.inPath).listFiles());
        if(fs!=null) {
            for(File f: fs) {
                f.delete();
            }
        }

        ArrayList<File> files = Globals.sql.getFiles(Globals.android_id);
        for (File s: files) {
            InputStream is = null;
            OutputStream os = null;
            try {
                File file = new File(s.getPath());
                System.out.println(s.getPath());
                File newFile = new File(Compress.inPath + '/' + s.getName());
                is = new FileInputStream(file);
                os = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    System.out.println(buffer);
                    os.write(buffer);
                }
                is.close();
                os.close();
            } catch (Exception e) {

            }
        }

        File temp = new File(Compress.inPath);
        System.out.println("About to brint doofus llooooook herereererererer");
        for (File f: temp.listFiles()) {
            System.out.println("hey");
            System.out.println(f.getName());
        }

        Compress.zipAll();

        File fileToSend = new File(Compress.outPath); // sets file to send as the zipped folder


        try {
            Payload filePayload = Payload.fromFile(fileToSend); //sets the payload to be the file
            Nearby.getConnectionsClient(C).sendPayload(ConnectedEndpointIDs.get(i), filePayload); // sends the payload
            fileToSend.delete();
            Toast toast = Toast.makeText(C, "Sent!", Toast.LENGTH_SHORT);
            toast.show();
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
                    Toast toast = Toast.makeText(C, "Found Friend", Toast.LENGTH_SHORT);
                    toast.show();
                    connect(endpointId);

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    Toast toast = Toast.makeText(C, "Lost Friend", Toast.LENGTH_SHORT);
                    toast.show();
                }
            };



    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            Toast toast = Toast.makeText(C, "Payload here", Toast.LENGTH_SHORT);
            toast.show();

            Payload.File f = payload.asFile();
            File fnew = f.asJavaFile();
            fnew.renameTo(new File(Compress.outPath + "/"+  fnew.getName()));
            Compress.unzipAll();
            fnew.delete();

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
            startAdvertise();
            startDiscovery();
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            String title = "Downloading FIle";
            String content = "Progress: %" + (int)(payloadTransferUpdate.getBytesTransferred()/(payloadTransferUpdate.getTotalBytes()+ 1));
            int priority = 5;

            Intent newIntentService = NotificationService.updateProgressBarNotification((int)payloadTransferUpdate.getPayloadId(), title, content, priority, (int)(payloadTransferUpdate.getBytesTransferred()/(payloadTransferUpdate.getTotalBytes() +1)), C);
            C.startService(newIntentService);

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
                            stopAdvert();
                            stopDiscovery();
                            ConnectedEndpointIDs.add(endpointId);
                            Toast toast = Toast.makeText(C, "Connected to Friend", Toast.LENGTH_SHORT);
                            toast.show();
                            sendPayload(ConnectedEndpointIDs.size()- 1);


                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            Toast toast1 = Toast.makeText(C, "Rejected by Friend", Toast.LENGTH_SHORT);
                            toast1.show();
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            Toast toast2 = Toast.makeText(C, "Error Friend", Toast.LENGTH_SHORT);
                            toast2.show();
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

