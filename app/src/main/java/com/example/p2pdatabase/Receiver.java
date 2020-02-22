package com.example.p2pdatabase;

public interface Receiver {
     void receive(byte[] b);
     void onConnection();
     void onDisconnect();
}
