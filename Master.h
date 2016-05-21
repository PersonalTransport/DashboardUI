#ifndef MASTER_H
#define MASTER_H

#include <QObject>

class Master : public QObject
{
    Q_OBJECT
    Q_PROPERTY(double batteryVoltage READ batteryVoltage NOTIFY batteryVoltageChanged)
    Q_PROPERTY(double usageCurrent READ usageCurrent NOTIFY usageCurrentChanged)
    Q_PROPERTY(double throttlePosition READ throttlePosition WRITE setThrottlePosition NOTIFY throttlePositionChanged)
    Q_PROPERTY(double igbt1Temperature READ igbt1Temperature NOTIFY igbt1TemperatureChanged)
    Q_PROPERTY(double igbt2Temperature READ igbt2Temperature NOTIFY igbt2TemperatureChanged)

    Q_PROPERTY(double batteryLife READ batteryLife NOTIFY batteryLifeChanged)
    Q_PROPERTY(double speed READ speed WRITE setSpeed NOTIFY speedChanged)


    Q_PROPERTY(int signalLightState READ signalLightState WRITE setSignalLightState NOTIFY signalLightStateChanged)
    Q_PROPERTY(int headLightState READ headLightState WRITE setHeadLightState NOTIFY headLightStateChanged)

    Q_PROPERTY(QString dataIn READ dataIn NOTIFY dataInChanged)

    Q_PROPERTY(float xAcceleration READ xAcceleration NOTIFY xAccelerationChanged)
public:
    explicit Master(QObject *parent = 0);

    static Master *instance();

    double batteryVoltage() const;
    void setBatteryVoltage(double batteryVoltage);

    double usageCurrent() const;
    void setUsageCurrent(double usageCurrent);

    double throttlePosition() const;
    void setThrottlePosition(double throttlePosition);

    double igbt1Temperature() const;
    void setIgbt1Temperature(double igbt1Temperature);

    double igbt2Temperature() const;
    void setIgbt2Temperature(double igbt2Temperature);

    double batteryLife() const;
    void setBatteryLife(double batteryLife);

    double speed() const;
    void setSpeed(double speed);

    int signalLightState() const;
    void setSignalLightState(int signalLightState);

    int headLightState() const;
    void setHeadLightState(int headLightState);

    QString dataIn() const;
    void setDataIn(const QString &dataIn);

    float xAcceleration() const;
    void setXAcceleration(float x_acceleration);

signals:

    void batteryVoltageChanged(double voltage);

    void usageCurrentChanged(double current);

    void throttlePositionChanged(double throttlePosition);

    void igbt1TemperatureChanged(double temperature);

    void igbt2TemperatureChanged(double temperature);

    void batteryLifeChanged(double life);

    void speedChanged(double speed);

    void signalLightStateChanged(int state);

    void headLightStateChanged(int state);

    void dataInChanged(QString state);

    void xAccelerationChanged(float x_acceleration);

public slots:

private:
    double batteryVoltage_;
    double usageCurrent_;
    double throttlePosition_;
    double igbt1Temperature_;
    double igbt2Temperature_;
    double batteryLife_;
    double speed_;
    int signalLightState_;
    int headLightState_;
    QString dataIn_;
    float x_acceleration_;

    static Master *instance_;
};

#endif // MASTER_H
