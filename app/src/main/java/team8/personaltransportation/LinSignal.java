package team8.personaltransportation;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LinSignal {

    // Definitions for the command portion of the instruction format (temporary)
    public static final byte COMM_SET_VAR = 0x66;
    public static final byte COMM_GET_VAR = 0x77;
    public static final byte COMM_WARN_VAR = 0x11;

    // Variables in the message struct
    public byte command;        // Defines the type of instruction (set, get, warn)
    public int sid;          // Defines what the command is about (lights, blinkers, battery, speed, etc.)
    public byte length;      // Defines the length of the data
    public byte[] data;      // The data to be sent/recieved

    // Additional variables/constants
    public static final int HEADER_SIZE = 6;
    public static final int DATA_SIZE = 8;
    public static final int MAX_SIZE = HEADER_SIZE + DATA_SIZE;

    public LinSignal(byte[] rawdata, int size) {
        this.create(rawdata, size);
    }

    // newsid must be hashed useing signalHash
    public LinSignal(byte command, int sid, byte length, byte[] data) {
        this.command = command;
        this.sid = sid;
        this.length = length;
        this.data = data.clone();
    }

    // Serialize message struct into raw data
    public byte[] serialize() {
        byte[] buffer = new byte[HEADER_SIZE + data.length];
        buffer[0] = (byte) this.command;
        byte[] sidBytes = packIntToBytes(this.sid);
        for (int i = 0; i < 4; i++)
            buffer[i + 1] = sidBytes[i];
        buffer[5] = (byte) this.data.length;
        for (int i = 0; i < this.data.length; i++)
            buffer[i + HEADER_SIZE] = this.data[i];

        return buffer;
    }

    // Populate message struct from raw data
    private void create(byte[] rawdata, int size) {
        this.command = rawdata[0];
        this.sid = unpackBytesToInt(rawdata[1], rawdata[2], rawdata[3], rawdata[4]);
        this.length = rawdata[5];
        int lenleft = size - HEADER_SIZE;
        // Error checking for data length
        if (lenleft < this.length) // lenleft should always be equal to this.length
            throw new IllegalArgumentException("Error: not enough data in LIN message");
        this.data = new byte[this.length];
        for (int i = 0; i < this.length; i++)
            this.data[i] = rawdata[HEADER_SIZE + i];
    }

    //http://www.cse.yorku.ca/~oz/hash.html
    public static int signalHash(byte[] input, int i) {
        return (input.length != i) ? ((int) input[i]) + 33 * signalHash(input, i + 1) : 5381;
    }

    public static int unpackBytesToInt(byte byte1, byte byte2, byte byte3, byte byte4) {
        return ((((int) byte1) << 24) & 0xFF000000)
                | ((((int) byte2) << 16) & 0x00FF0000)
                | ((((int) byte3) << 8) & 0x0000FF00)
                | (((int) byte4) & 0x000000FF);

    }

    public static byte[] packIntToBytes(int input) {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ((input >> 24) & 0xFF);
        bytes[1] = (byte) ((input >> 16) & 0xFF);
        bytes[2] = (byte) ((input >> 8) & 0xFF);
        bytes[3] = (byte) ((input) & 0xFF);

        return bytes;
    }
}