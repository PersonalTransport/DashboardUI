#ifndef MASTER_H
#define MASTER_H

#include <QObject>

class Master : public QObject
{
    Q_OBJECT
    Q_PROPERTY(double batteryVoltage READ batteryVoltage NOTIFY batteryVoltageChanged)
    Q_PROPERTY(double usageCurrent READ usageCurrent NOTIFY usageCurrentChanged)
    Q_PROPERTY(double throttlePosition READ throttlePosition WRITE setThrottlePosition NOTIFY throttlePositionChanged)
    Q_PROPERTY(double igbtTemperature READ igbtTemperature NOTIFY igbtTemperatureChanged)

    Q_PROPERTY(double batteryLife READ batteryLife NOTIFY batteryLifeChanged)
    Q_PROPERTY(double speed READ speed WRITE setSpeed NOTIFY speedChanged)


    Q_PROPERTY(int signalLightState READ signalLightState WRITE setSignalLightState NOTIFY signalLightStateChanged)
    Q_PROPERTY(int headLightState READ headLightState WRITE setHeadLightState NOTIFY headLightStateChanged)

    Q_PROPERTY(QString dataIn READ dataIn NOTIFY dataInChanged)
public:
    explicit Master(QObject *parent = 0);

    static Master *instance();

    double batteryVoltage() const;
    void setBatteryVoltage(double batteryVoltage);

    double usageCurrent() const;
    void setUsageCurrent(double usageCurrent);

    double throttlePosition() const;
    void setThrottlePosition(double throttlePosition);

    double igbtTemperature() const;
    void setIgbtTemperature(double igbtTemperature);

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

signals:

    void batteryVoltageChanged(double voltage);

    void usageCurrentChanged(double current);

    void throttlePositionChanged(double throttlePosition);

    void igbtTemperatureChanged(double temperature);

    void batteryLifeChanged(double life);

    void speedChanged(double speed);

    void signalLightStateChanged(int state);

    void headLightStateChanged(int state);

    void dataInChanged(QString state);

public slots:

private:
    double batteryVoltage_;
    double usageCurrent_;
    double throttlePosition_;
    double igbtTemperature_;
    double batteryLife_;
    double speed_;
    int signalLightState_;
    int headLightState_;
    QString dataIn_;

    static Master *instance_;
};

#endif // MASTER_H
