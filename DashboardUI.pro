TEMPLATE = app

QT += qml quick
CONFIG += c++11

SOURCES += main.cpp Master.cpp
HEADERS += Master.h

RESOURCES += qml.qrc

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

android {
    QT += androidextras
    SOURCES += android/cpp/com_ptransportation_FullscreenActivity.cpp
    HEADERS += android/cpp/com_ptransportation_FullscreenActivity.h
}
