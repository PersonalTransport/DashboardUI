#include "Master.h"
#ifdef Q_OS_ANDROID
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>
#endif

Master::Master(QObject *parent)
    : QObject(parent),
      batteryVoltage_(0),
      usageCurrent_(0),
      igbtTemperature_(0),
      batteryLife_(0),
      speed_(0),
      signalLightState_(0),
      headLightState_(0)
{
}

double Master::batteryVoltage() const
{
    return batteryVoltage_;
}

void Master::setBatteryVoltage(double batteryVoltage)
{
    if(batteryVoltage_ != batteryVoltage) {
        batteryVoltage_ = batteryVoltage;
        emit batteryVoltageChanged(batteryVoltage_);
    }
}

double Master::usageCurrent() const
{
    return usageCurrent_;
}

void Master::setUsageCurrent(double usageCurrent)
{
    if(usageCurrent_ != usageCurrent) {
        usageCurrent_ = usageCurrent;
        emit usageCurrentChanged(usageCurrent);
    }
}

double Master::igbtTemperature() const
{
    return igbtTemperature_;
}

void Master::setIgbtTemperature(double igbtTemperature)
{
    if(igbtTemperature_ != igbtTemperature) {
        igbtTemperature_ = igbtTemperature;
        emit igbtTemperatureChanged(igbtTemperature);
    }
}

double Master::batteryLife() const
{
    return batteryLife_;
}

void Master::setBatteryLife(double batteryLife)
{
    if(batteryLife_ != batteryLife) {
        batteryLife_ = batteryLife;
        emit batteryLifeChanged(batteryLife);
    }
}

double Master::speed() const
{
    return speed_;
}

void Master::setSpeed(double speed)
{
    if(speed_ != speed) {
        speed_ = speed;
        emit speedChanged(speed);
    }
}

int Master::signalLightState() const
{
    return signalLightState_;
}

void Master::setSignalLightState(int signalLightState)
{
    if(signalLightState_ != signalLightState) {
        signalLightState_ = signalLightState;
#ifdef Q_OS_ANDROID
        QAndroidJniObject::callStaticMethod<void>("com/ptransportation/FullscreenActivity",
                                            "sendSignalLightState",
                                            "(I)V",
                                            signalLightState_);
#endif
        emit signalLightStateChanged(signalLightState);
    }
}

int Master::headLightState() const
{
    return headLightState_;
}

void Master::setHeadLightState(int headLightState)
{
    if(headLightState_ != headLightState) {
        headLightState_ = headLightState;
#ifdef Q_OS_ANDROID
        QAndroidJniObject::callStaticMethod<void>("com/ptransportation/FullscreenActivity",
                                            "sendHeadLightState",
                                            "(I)V",
                                            headLightState_);
#endif
        emit headLightStateChanged(headLightState);
    }
}
