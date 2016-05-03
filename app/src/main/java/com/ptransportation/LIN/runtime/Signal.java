package com.ptransportation.LIN.runtime;

public class Signal {
    public SignalHeader header;
    public byte[] data;

    public Signal(SignalHeader header, byte[] data) {
        this.header = header;
        this.data = data;
    }
}
