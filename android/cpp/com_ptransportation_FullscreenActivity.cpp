#include "com_ptransportation_FullscreenActivity.h"
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>
#include "Master.h"

#define MOTOR_CONTROLLER_DUTY_CYCLE_SID 1398608173ul
#define MOTOR_CONTROLLER_IGBT1_TEMPERATURE_SID 0x82046B5Cul
#define MOTOR_CONTROLLER_IGBT2_TEMPERATURE_SID 0xF4B0B5FDul
//#define HEAD_LIGHT_STATE_SID 999653166ul
//#define SIGNAL_LIGHT_STATE_SID 2308980954ul
#define AXLE_RPM_SID 0xD211EF5Dul
#define BATTERY_VOLTAGE_SID 4052165617ul
#define USAGE_CURRENT_SID 0x5A23E81Cul
#define CHARGING_CURRENT_SID 3484793322ul

inline static int getFileDescriptor(JNIEnv *env, jobject fileDescriptor) {
    if(fileDescriptor != nullptr) {
        jclass klass = env->FindClass("java/io/FileDescriptor");
        if(klass != nullptr) {
            jfieldID id = env->GetFieldID(fdClass, "descriptor", "I");
            if(id != nullptr) {
                return env->GetIntField(fileDescriptor, id);
            }
        }
    }
    return -1;
}

/*
 * Class:     FullscreenActivity
 * Method:    onMasterDeviceConnected
 * Signature: (Ljava/io/FileDescriptor;)V
 */
JNIEXPORT void JNICALL Java_com_ptransportation_FullscreenActivity_onMasterDeviceConnected(JNIEnv *env, jobject self, jobject fileDescriptor) {
    auto fd = getFileDescriptor(env,fileDescriptor);
    if(fd != -1)
        Master::instance()->setFile(fd);
}

/*
 * Class:     FullscreenActivity
 * Method:    onMasterDeviceDisconnected
 * Signature: (Ljava/io/FileDescriptor;)V
 */
JNIEXPORT void JNICALL Java_com_ptransportation_FullscreenActivity_onMasterDeviceDisconnected(JNIEnv *env, jobject self, jobject fd) {
    auto fd = getFileDescriptor(env,fileDescriptor);
    if(Master::instance()->file() == fd)
        Master::instance()->setFile(-1);
}


/*JNIEXPORT void JNICALL Java_com_ptransportation_FullscreenActivity_cppOnSignalReceived(JNIEnv *env, jclass klass, jint sid, jint length, jbyteArray data)
{
    auto master = Master::instance();
    if(master == nullptr)
        return;

    jbyte* bufferPtr = env->GetByteArrayElements(data, NULL);

    uint8_t real_data[8];
    for(int i=0;i<8 && i < length;++i) {
        real_data[i] = bufferPtr[i] & 0xFF;
    }

    master->signalReceived(sid,real_data,length);

    switch ((uint32_t)sid) {
    case MOTOR_CONTROLLER_DUTY_CYCLE_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setThrottlePosition(value/655.35);
        break;
    }
    case MOTOR_CONTROLLER_IGBT1_TEMPERATURE_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setIgbt1Temperature(value);
        break;
    }
    case MOTOR_CONTROLLER_IGBT2_TEMPERATURE_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setIgbt2Temperature(value);
        break;
    }
    case AXLE_RPM_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setSpeed(value*0.059499);
        break;
    }
    case BATTERY_VOLTAGE_SID: {
        // TODO
        break;
    }
    case USAGE_CURRENT_SID: {
        uint16_t value = (((uint16_t)(bufferPtr[1] & 0xFF)) << 8) | ((uint16_t)(bufferPtr[0] & 0xFF));
        master->setUsageCurrent(value);
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
}*/
