package com.ptransportation.LIN.runtime;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SignalInputStream {
    private DataInputStream inputStream;

    public SignalInputStream(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    public Signal readSignal() throws IOException {
        SignalHeader header = new SignalHeader();
        header.command = inputStream.readByte();
        header.sid = inputStream.readInt();
        header.length = inputStream.readByte();
        byte[] data = new byte[header.length];
        inputStream.readFully(data);
        return new Signal(header, data);
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
