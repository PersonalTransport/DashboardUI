package team8.personaltransportation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class LinBus {
    private InputStream inputStream;
    private OutputStream outputStream;
    private final List<LinSignal> signals;

    private final Semaphore waitForSendSignal = new Semaphore(0);

    public LinBus() {
        this.signals = new ArrayList<LinSignal>();
        this.inputStream = null;
        this.outputStream = null;
    }

    public LinBus(InputStream inputStream,OutputStream outputStream) {
        this.signals = new ArrayList<LinSignal>();
        initializeStreams(inputStream, outputStream);
    }

    public void initializeStreams(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public abstract void receiveSignal(LinSignal signal);

    public void sendSignal(LinSignal signal) {
        synchronized (this.signals) {
            this.signals.add(signal);
        }
    }

    public void update_send() throws IOException, InterruptedException {

        if (outputStream == null)
            return;

        synchronized (this.signals) {
            for (LinSignal signal : signals) {
                outputStream.write(signal.serialize(), 0, signal.length + LinSignal.HEADER_SIZE);
                outputStream.flush();
            }
            signals.clear();
        }
    }

    public void update_recieve() throws IOException, InterruptedException {

        if (inputStream == null)
            return;

        byte [] rawData = new byte[LinSignal.MAX_SIZE];

        // TODO split this up into two calls one for the header and one for the data.


        int size = inputStream.read(rawData, 0, LinSignal.MAX_SIZE);
        receiveSignal(new LinSignal(rawData, size));


    }
}
