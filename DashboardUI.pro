TEMPLATE = app

QT += qml quick
CONFIG += c++11

SOURCES += main.cpp Master.cpp
HEADERS += Master.h

RESOURCES += \
    qml.qrc

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Default rules for deployment.
include(deployment.pri)

DISTFILES += \
    android/AndroidManifest.xml \
    android/gradle/wrapper/gradle-wrapper.jar \
    android/gradlew \
    android/res/values/libs.xml \
    android/build.gradle \
    android/gradle/wrapper/gradle-wrapper.properties \
    android/gradlew.bat \
    android/java/com/ptransportation/LIN/runtime/MasterDevice.java \
    android/java/com/ptransportation/LIN/runtime/Signal.java \
    android/java/com/ptransportation/LIN/runtime/SignalHeader.java \
    android/java/com/ptransportation/LIN/runtime/SignalInputStream.java \
    android/java/com/ptransportation/LIN/runtime/SignalOutputStream.java \
    android/java/com/ptransportation/LIN/runtime/SignalReceivedListener.java \
    android/java/com/ptransportation/USB/MasterDeviceConnectionListener.java \
    android/java/com/ptransportation/USB/MasterManager.java \
    android/java/com/ptransportation/FullscreenActivity.java \
    android/res/xml/accessory_filter.xml

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android

LIN_MASTER = $$PWD/../EVMaster/ev_master.ncf
LIN_SLAVES = $$PWD/../MotorControllerNode/motor_controller.ncf \
            $$PWD/../LightingNode/lighting.ncf \
            $$PWD/../EnergyManagementNode/energy_management.ncf \
            $$PWD/../SpeedometerNode/speedometer.ncf

android {
    QT += androidextras

    SOURCES += android/cpp/com_ptransportation_FullscreenActivity.cpp
    HEADERS += android/cpp/com_ptransportation_FullscreenActivity.h

    JAVAGEN.target = android/java-gen/com/ptransportation/LIN/runtime/ev_master.java
    JAVAGEN.commands = LIN -t AndroidAccessory -i com.ptransportation.LIN.runtime -o $$PWD/android/java-gen $$LIN_MASTER $$LIN_SLAVES
    JAVAGEN.depends = FORCE
    PRE_TARGETDEPS += android/java-gen/com/ptransportation/LIN/runtime/ev_master.java
    QMAKE_EXTRA_TARGETS += JAVAGEN
}
