package team8.personaltransportation.LIN.runtime;

public class Signal {
    public SignalHeader header;
    public byte[] data;

    public Signal(SignalHeader header, byte[] data) {
        this.header = header;
        this.data = data;
    }
}
