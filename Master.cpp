#include "Master.h"

Master *Master::instance_ = nullptr;

Master::Master(QObject *parent)
    : QObject(parent),
      batteryVoltage_(0),
      usageCurrent_(0),
      throttlePosition_(0),
      igbt1Temperature_(0),
      igbt2Temperature_(0),
      batteryLife_(0),
      speed_(0),
      signalLightState_(0),
      headLightState_(0)
{
    instance_ = this;
}

Master *Master::instance() {
    return instance_;
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

double Master::throttlePosition() const
{
    return throttlePosition_;
}

void Master::setThrottlePosition(double throttlePosition)
{
    if(throttlePosition_ != throttlePosition) {
        throttlePosition_ = throttlePosition;
        emit throttlePositionChanged(throttlePosition);
    }
}

double Master::igbt1Temperature() const
{
    return igbt1Temperature_;
}

void Master::setIgbt1Temperature(double igbtTemperature)
{
    if(igbt1Temperature_ != igbtTemperature) {
        igbt1Temperature_ = igbtTemperature;
        emit igbt1TemperatureChanged(igbtTemperature);
    }
}

double Master::igbt2Temperature() const
{
    return igbt2Temperature_;
}

void Master::setIgbt2Temperature(double igbt2Temperature)
{
    if(igbt2Temperature_ != igbt2Temperature) {
        igbt2Temperature_ = igbt2Temperature;
        emit igbt2TemperatureChanged(igbt2Temperature);
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
/*#ifdef Q_OS_ANDROID
        QAndroidJniObject::callStaticMethod<void>("com/ptransportation/FullscreenActivity",
                                            "sendSignalLightState",
                                            "(I)V",
                                            signalLightState_);
#endif*/
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
/*#ifdef Q_OS_ANDROID
        QAndroidJniObject::callStaticMethod<void>("com/ptransportation/FullscreenActivity",
                                            "sendHeadLightState",
                                            "(I)V",
                                            headLightState_);
#endif*/
        emit headLightStateChanged(headLightState);
    }
}
