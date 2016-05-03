package com.ptransportation.LIN.runtime;

public class SignalHeader {
    public static final byte COMMAND_SET = 0x66;
    public static final byte COMMAND_GET = 0x77;
    public static final byte COMMAND_WARNING = 0x11;

    public byte command;
    public int sid;
    public byte length;
}
