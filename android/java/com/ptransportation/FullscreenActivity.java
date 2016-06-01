package com.ptransportation;

import android.os.Bundle;
import android.widget.Toast;

import com.ptransportation.LIN.Compiler;
import com.ptransportation.LIN.runtime.MasterDevice;
import com.ptransportation.LIN.runtime.Signal;
import com.ptransportation.LIN.runtime.SignalHeader;
import com.ptransportation.LIN.runtime.SignalReceivedListener;
import com.ptransportation.USB.MasterDeviceConnectionListener;
import com.ptransportation.USB.MasterManager;

public class FullscreenActivity extends org.qtproject.qt5.android.bindings.QtActivity implements MasterDeviceConnectionListener, SignalReceivedListener {
    private MasterManager masterManager;
    private MasterDevice master;

    private static FullscreenActivity currentInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        currentInstance = this;
        masterManager = new MasterManager();
        masterManager.onCreate(this);
        masterManager.addConnectionListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        masterManager.onResume(getIntent());
    }


    @Override
    public void onPause() {
        super.onPause();

        masterManager.onPause();
    }


    @Override
    public void onDestroy() {

        masterManager.onDestroy(this);

        super.onDestroy();
    }

    @Override
    public void onMasterDeviceConnected(MasterDevice device) {
        Toast.makeText(this, "Master Connected", Toast.LENGTH_SHORT).show();
        this.master = device;
        this.master.addSignalReceivedListener(this);
    }

    @Override
    public void onSignalReceived(Signal signal) {
        final Signal s = signal;
        if(currentInstance != null) {
            currentInstance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cppOnSignalReceived(s.header.sid,s.header.length,s.data);
                }
            });
        }
    }

    private static native void cppOnSignalReceived(int sid,int length,byte [] data);

    @Override
    public void onMasterDeviceDisconnected(MasterDevice device) {
        if(this.master == device) {
            Toast.makeText(this, "Master Disconnected", Toast.LENGTH_SHORT).show();
            this.master.removeSignalReceivedListener(this);
            this.master = null;
        }
    }

    public static int signalHash(byte [] input,int i) {
        return (input.length != i) ? ((int)input[i]) + 33 * signalHash(input,i+1) : 5381;
    }

    public static void sendSignalLightState(int state) {
        if(currentInstance != null && currentInstance.master != null) {
            SignalHeader header = new SignalHeader();
            header.command = SignalHeader.COMMAND_SET;
            header.sid = signalHash("signal_light_state".getBytes(),0);
            header.length = 1;
            byte [] data = new byte[1];
            data[0] = (byte)state;
            currentInstance.master.sendSignal(new Signal(header,data));
        }
    }

    public static void sendHeadLightState(int state) {
        if(currentInstance != null && currentInstance.master != null) {
            SignalHeader header = new SignalHeader();
            header.command = SignalHeader.COMMAND_SET;
            header.sid = signalHash("head_light_state".getBytes(),0);
            header.length = 1;
            byte [] data = new byte[1];
            data[0] = (byte)state;
            currentInstance.master.sendSignal(new Signal(header,data));
        }
    }
}
