package com.ptransportation.LIN.runtime;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MasterDevice {
    private SignalInputStream signalInputStream;
    private SignalOutputStream signalOutputStream;
    private ArrayList<SignalReceivedListener> listeners;
    Thread readThread;

    public MasterDevice(final InputStream inputStream, OutputStream outputStream) {
        this.signalInputStream = new SignalInputStream(inputStream);
        this.signalOutputStream = new SignalOutputStream(outputStream);
        this.listeners = new ArrayList<>();

        /*treadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(!Thread.interrupted()) {
                        Signal signal = signalInputStream.readSignal();
                        for (SignalReceivedListener listener : listeners)
                            listener.onSignalReceived(signal);
                    }
                } catch (IOException ignored) {
                }
                try {
                    signalInputStream.close();
                } catch (IOException ignored) {
                }
            }
        });
        readThread.start();*/
    }

    public void sendSignal(Signal signal) {
        signalOutputStream.writeSignal(signal);
    }

    public MasterDevice(FileDescriptor fd) {
        this(new FileInputStream(fd),new FileOutputStream(fd));
    }

    public void addSignalReceivedListener(SignalReceivedListener listener) {
        this.listeners.add(listener);
    }

    public void removeSignalReceivedListener(SignalReceivedListener listener) {
        this.listeners.remove(listener);
    }

    public void close() throws IOException {
        if(this.readThread != null)
            this.readThread.interrupt();
        if(this.signalOutputStream != null)
            this.signalOutputStream.close();
    }
}
