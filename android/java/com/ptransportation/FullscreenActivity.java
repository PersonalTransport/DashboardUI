package com.ptransportation;

import android.os.Bundle;
import android.widget.Toast;

import com.ptransportation.LIN.runtime.Signal;
import com.ptransportation.LIN.runtime.SignalHeader;
import com.ptransportation.LIN.runtime.SignalReceivedListener;
import com.ptransportation.USB.MasterDeviceConnectionListener;
import com.ptransportation.USB.MasterManager;

import java.io.FileDescriptor;

public class FullscreenActivity extends org.qtproject.qt5.android.bindings.QtActivity implements MasterDeviceConnectionListener {
    private MasterManager masterManager;

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
    native public void onMasterDeviceConnected(FileDescriptor fileDescriptor);
    /*{
        Toast.makeText(this, "Master Connected", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    native public void onMasterDeviceDisconnected(FileDescriptor fileDescriptor);
    /*{
        Toast.makeText(this, "Master Disconnected", Toast.LENGTH_SHORT).show();
    }*/

    /*@Override
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
    }*/

    public static int signalHash(byte [] input,int i) {
        return (input.length != i) ? ((int)input[i]) + 33 * signalHash(input,i+1) : 5381;
    }
}
