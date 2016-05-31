package com.ptransportation.USB;

import com.ptransportation.LIN.runtime.MasterDevice;

public interface MasterDeviceConnectionListener {
    void onMasterDeviceConnected(MasterDevice device);

    void onMasterDeviceDisconnected(MasterDevice device);
}
