package com.ptransportation.USB;

import java.io.FileDescriptor;

public interface MasterDeviceConnectionListener {
    void onMasterDeviceConnected(FileDescriptor fd);

    void onMasterDeviceDisconnected(FileDescriptor fd);
}
