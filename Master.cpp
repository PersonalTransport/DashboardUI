#include "Master.h"
#ifdef Q_OS_ANDROID
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>
#include <com_ptransportation_FullscreenActivity.h>
#endif

Master *Master::instance_ = nullptr;

Master::Master(QObject *parent)
    : QObject(parent),
      batteryVoltage_(0),
      usageCurrent_(0),
      igbtTemperature_(0),
      batteryLife_(0),
      speed_(0),
      signalLightState_(0),
      headLightState_(0),
      throttlePosition_(0)
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

QString Master::dataIn() const
{
    return dataIn_;
}

void Master::setDataIn(const QString &dataIn)
{
    if(dataIn_ != dataIn) {
        dataIn_ = dataIn;
        emit dataInChanged(dataIn_);
    }
}


#define MOTOR_CONTROLLER_DUTY_CYCLE_SID 1398608173u
#define MOTOR_CONTROLLER_IGBT_TEMPERATURE_SID 316875851u
//#define HEAD_LIGHT_STATE_SID 999653166u
//#define SIGNAL_LIGHT_STATE_SID 2308980954u
#define AXLE_RPM_SID 3524390749u
#define BATTERY_VOLTAGE_SID 4052165617u
#define USAGE_CURRENT_SID 1512302620u
#define CHARGING_CURRENT_SID 3484793322u

#ifdef Q_OS_ANDROID
JNIEXPORT void JNICALL Java_com_ptransportation_FullscreenActivity_cppOnSignalReceived(JNIEnv *env, jclass klass, jint sid, jint length, jbyteArray data)
{
    auto master = Master::instance();
    if(master == nullptr)
        return;

    jbyte* bufferPtr = env->GetByteArrayElements(data, NULL);

#if JAVA_STUPID
    char str[512];
    sprintf(str, "%x\n%x", sid,value);
    master->setDataIn(str);
#endif JAVA_STUPID

    switch ((uint32_t)sid) {
    case MOTOR_CONTROLLER_DUTY_CYCLE_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setThrottlePosition(value/655.35);
        break;
    }
    case MOTOR_CONTROLLER_IGBT_TEMPERATURE_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setIgbtTemperature(value);
        break;
    }
    case AXLE_RPM_SID: {
        // TODO
        break;
    }
    case BATTERY_VOLTAGE_SID: {
        // TODO
        break;
    }
    case USAGE_CURRENT_SID: {
        // TODO
        break;
    }
    case CHARGING_CURRENT_SID: {
        // TODO
        break;
    }
    default:
        break;
    }
    env->ReleaseByteArrayElements(data, bufferPtr, JNI_ABORT);
}
#endif
