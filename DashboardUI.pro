TEMPLATE = app

QT += qml quick
CONFIG += c++11

SOURCES += main.cpp \
    Master.cpp

RESOURCES += qml.qrc

android {
    ANDROID_PACKAGE_SOURCE_DIR = $$PWD/app
    QT += androidextras
    HEADERS += com_ptransportation_FullscreenActivity.h
    OTHER_FILES += app/src/com/ptransportation/FullscreenActivity.java \
                   app/src/com/ptransportation/USB/MasterManager.java \
                   app/src/com/ptransportation/USB/MasterDeviceConnectionListener.java \
                   app/src/com/ptransportation/LIN/runtime/Signal.java \
                   app/src/com/ptransportation/LIN/runtime/MasterDevice.java \
                   app/src/com/ptransportation/LIN/runtime/SignalHeader.java \
                   app/src/com/ptransportation/LIN/runtime/SignalInputStream.java \
                   app/src/com/ptransportation/LIN/runtime/SignalOutputStream.java \
                   app/src/com/ptransportation/LIN/runtime/SignalReceivedListener.java
}

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Default rules for deployment.
include(deployment.pri)

DISTFILES +=

HEADERS += \
    Master.h
