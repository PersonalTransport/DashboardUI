package com.ptransportation.LIN.runtime;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignalOutputStream {

    private OutputStream outputStream;

    public SignalOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public boolean writeSignal(Signal signal) {
        try {
            byte [] data = new byte[1+4+1+signal.header.length];
            data[0] = signal.header.command;
            data[1] = (byte)signal.header.sid;
            data[2] = (byte)(signal.header.sid >>> 8);
            data[3] = (byte)(signal.header.sid >>> 16);
            data[4] = (byte)(signal.header.sid >>> 24);
            data[5] = signal.header.length;
            for(int i=0;i<signal.header.length;++i) {
                data[6+i] = signal.data[i];
            }
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
