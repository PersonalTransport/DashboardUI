package com.ptransportation.LIN.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class SignalInputStream {
    private InputStream inputStream;

    public SignalInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<Signal> readSignals() throws IOException {
        List<Signal> signals = new ArrayList<>();
        byte [] data = new byte[512];
        int index = 0;
        int size = inputStream.read(data);
        while(index < size) {
            SignalHeader header = new SignalHeader();
            header.command = data[index];
            header.sid = (((int)(data[index+1] & 0xFF))      ) |
                         (((int)(data[index+2] & 0xFF)) << 8 ) |
                         (((int)(data[index+3] & 0xFF)) << 16)|
                         (((int)(data[index+4] & 0xFF)) << 24);
            header.length = data[index+5];
            byte [] sigData = new byte[header.length];
            for(int i=0;i<header.length;++i)
                sigData[i] = (byte)(data[index+6+i] & 0xFF);
            index += 6 + header.length;
            signals.add(new Signal(header,sigData));
        }
        return signals;
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
