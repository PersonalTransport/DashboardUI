package team8.personaltransportation;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LinSignal {

    // Definitions for the command portion of the instruction format (temporary)
    public static final byte COMM_SET_VAR  = 0x66;
    public static final byte COMM_GET_VAR  = 0x77;
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

    public LinSignal(byte[] rawdata,int size) {
        this.create(rawdata,size);
    }

    // newsid must be hashed useing signalHash
    public LinSignal(int newsid, byte newlength, byte[] newdata) {
        this.command = COMM_SET_VAR;
        this.sid = newsid;
        this.length = newlength;
        this.data = newdata.clone();
    }

    // Serialize message struct into raw data
    public byte[] serialize() {
        byte[] buffer = new byte[HEADER_SIZE + data.length];
        buffer[0] = (byte) this.command;
        buffer[1] = (byte) ((this.sid >> 24) & 0xFF);
        buffer[2] = (byte) ((this.sid >> 16) & 0xFF);
        buffer[3] = (byte) ((this.sid >> 8) & 0xFF);
        buffer[4] = (byte) ((this.sid) & 0xFF);
        buffer[5] = (byte) this.data.length;
        for (int i = 0; i < this.data.length; i++)
            buffer[i + HEADER_SIZE] = this.data[i];

        return buffer;
    }

    //http://www.cse.yorku.ca/~oz/hash.html
    public int signalHash(byte [] input,int i) {
        return (input.length == i) ? ((int)input[i]) + 33 * signalHash(input,i+1) : 5381;
    }

    // Populate message struct from raw data
    private void create(byte[] rawdata,int size) {
        this.command = rawdata[0];
        this.sid = ((((int) rawdata[1]) >> 24) & 0xFF000000)
                | ((((int) rawdata[2]) >> 16) & 0x00FF0000)
                | ((((int) rawdata[3]) >> 8)  & 0x0000FF00)
                | (((int) rawdata[4])         & 0x000000FF);
        this.length = rawdata[5];
        int lenleft = size - HEADER_SIZE;
        // Error checking for data length
        if (lenleft < this.length) // lenleft should always be equal to this.length
            throw new IllegalArgumentException("Error: not enough data in LIN message");
        this.data = new byte[this.length];
        for (int i = 0; i < this.length; i++)
            this.data[i] = rawdata[HEADER_SIZE + i];
    }
}