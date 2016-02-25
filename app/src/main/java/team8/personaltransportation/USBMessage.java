package team8.personaltransportation;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class USBMessage {
    public int type;
    public byte[] data;

    public USBMessage() {

        this.type = 0;
        this.data = new byte[0];
    }

    public void create(byte[] rawdata) {

        this.type = ((((int) rawdata[0]) >> 24) & 0xFF000000)
                | ((((int) rawdata[1]) >> 16) & 0x00FF0000)
                | ((((int) rawdata[2]) >> 8) & 0x0000FF00)
                | (((int) rawdata[3]) & 0x000000FF);
        int len = ((((int) rawdata[4]) >> 24) & 0xFF000000)
                | ((((int) rawdata[5]) >> 16) & 0x00FF0000)
                | ((((int) rawdata[6]) >> 8) & 0x0000FF00)
                | (((int) rawdata[7]) & 0x000000FF);
        int lenleft = rawdata.length - 8;
        if (lenleft < len)
            len = lenleft;
        this.data = new byte[len];
        for (int i = 0; i < len; i++)
            this.data[i] = rawdata[i + 8];
    }

    public byte[] serialize() {

        byte[] buffer = new byte[8 + data.length];
        buffer[0] = (byte) ((this.type >> 24) & 0xFF);
        buffer[1] = (byte) ((this.type >> 16) & 0xFF);
        buffer[2] = (byte) ((this.type >> 8) & 0xFF);
        buffer[3] = (byte) ((this.type) & 0xFF);
        buffer[4] = (byte) ((this.data.length >> 24) & 0xFF);
        buffer[5] = (byte) ((this.data.length >> 16) & 0xFF);
        buffer[6] = (byte) ((this.data.length >> 8) & 0xFF);
        buffer[7] = (byte) ((this.data.length) & 0xFF);
        for (int i = 0; i < this.data.length; i++)
            buffer[i + 8] = this.data[i];

        return buffer;
    }
}
