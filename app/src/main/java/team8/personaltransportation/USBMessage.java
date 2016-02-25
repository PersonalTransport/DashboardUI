package team8.personaltransportation;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class USBMessage {

    // Definitions for the comm portion of the instruction format (temporary)
    public static final byte COMM_SET_VAR  = 0x66;
    public static final byte COMM_GET_VAR  = 0x77;
    public static final byte COMM_WARN_VAR = 0x11;

    // Definitions for the SID portion of the instruction format (temporary)
    public static final int SID_LIGHTS     = 1;
    public static final int SID_TURNSIGNAL = 2;
    public static final int SID_BATTERY    = 3;
    public static final int SID_SPEED      = 4;

    // Variables in the message struct
    public byte comm;        // Defines the type of instruction (set, get, warn)
    public int sid;          // Defines what the command is about (lights, blinkers, battery, speed, etc.)
    public byte length;      // Defines the length of the data
    public byte[] data;      // The data to be sent/recieved


    // Additional variables/constants
    public static final int MESSAGE_DATA_OFFSET = 6;

    // Default constructor
    public USBMessage() {

        this.comm = COMM_SET_VAR;
        this.sid = 0;
        this.length = 0;
        this.data = new byte[0];
    }

    public USBMessage(byte[] rawdata) {

        this.create(rawdata);
    }

    public USBMessage(byte newcomm, int newsid, byte newlength, byte[] newdata) {

        this.comm = newcomm;
        this.sid = newsid;
        this.length = newlength;
        this.data = newdata.clone();
    }

    // Populate message struct from raw data
    public void create(byte[] rawdata) {

        this.comm = rawdata[0];
        this.sid = ((((int) rawdata[1]) >> 24) & 0xFF000000)
                | ((((int) rawdata[2]) >> 16) & 0x00FF0000)
                | ((((int) rawdata[3]) >> 8)  & 0x0000FF00)
                | (((int) rawdata[4])         & 0x000000FF);
        this.length = rawdata[5];
        int lenleft = rawdata.length - MESSAGE_DATA_OFFSET;
        // Error checking for data length
        if (lenleft < this.length) // lenleft should alwasy be equal to this.length
            this.length = (byte) lenleft;
        this.data = new byte[this.length];
        for (int i = 0; i < this.length; i++)
            this.data[i] = rawdata[i + MESSAGE_DATA_OFFSET];
    }

    // Serialize message struct into raw data
    public byte[] serialize() {

        byte[] buffer = new byte[MESSAGE_DATA_OFFSET + data.length];
        buffer[0] = (byte) this.comm;
        buffer[1] = (byte) ((this.sid >> 24) & 0xFF);
        buffer[2] = (byte) ((this.sid >> 16) & 0xFF);
        buffer[3] = (byte) ((this.sid >> 8) & 0xFF);
        buffer[4] = (byte) ((this.sid) & 0xFF);
        buffer[5] = (byte) this.data.length;
        for (int i = 0; i < this.data.length; i++)
            buffer[i + MESSAGE_DATA_OFFSET] = this.data[i];

        return buffer;
    }
}