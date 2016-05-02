package team8.personaltransportation.USB;

import team8.personaltransportation.LIN.runtime.MasterDevice;

public interface MasterDeviceConnectionListener {
    void onMasterDeviceConnected(MasterDevice device);

    void onMasterDeviceDisconnected(MasterDevice device);
}
