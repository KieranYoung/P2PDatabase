package com.example.p2pdatabase;

public class Globals {
    public static ConnectionClient CClient = new ConnectionClient();

    enum Mode
    {
        LookingAndListening, Updating
    }

    private static Mode mode;

    public static void setMode(Mode m) {
        mode = m;
    }

    public static Mode getMode() {
        return mode;
    }
}
