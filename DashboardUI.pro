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
}

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Default rules for deployment.
include(deployment.pri)

DISTFILES +=

HEADERS += \
    Master.h
