#ifndef MASTER_H
#define MASTER_H

#include <QAbstractListModel>
#include <QObject>
#include <QtCharts>
#include <chrono>

class Master;

class Dataset : public QObject {
    Q_OBJECT
    Q_PROPERTY(QString name READ name)
public:
    explicit Dataset(QString name,uint32_t SID,Master *master);

    QString name() const;

    Q_INVOKABLE void update(QtCharts::QAbstractSeries *series);

    virtual float convert(uint8_t *data,uint8_t length) const = 0;

    virtual void onSignalReceived(uint32_t SID,uint8_t *data,uint8_t length);

private:
    Master *master_;
    QString name_;
    uint32_t SID_;
    QVector<QPointF> data_;
};

class N16Dataset : public Dataset {
    Q_OBJECT
public:
    explicit N16Dataset(QString name,uint32_t SID,Master *master);

    virtual float convert(uint8_t *data,uint8_t length) const override;
};

class Master : public QAbstractListModel
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

    Q_PROPERTY(double time READ time)

    Q_PROPERTY(int file READ file WRITE setFile NOTIFY fileChanged)
public:
    explicit Master(QObject *parent = 0);

    ~Master();

    static Master *instance();

    int file() const;

    void setFile(int file);

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

    void signalReceived(uint32_t SID,uint8_t *data,uint8_t length);

    enum DatasetRoles {
        NameRole = Qt::UserRole + 1
    };

    double time() const;

    QHash<int, QByteArray> roleNames() const;

    virtual int rowCount(const QModelIndex &parent = QModelIndex()) const override;

    virtual QVariant data(const QModelIndex &index, int role) const override;

    Q_INVOKABLE Dataset *getDataset(int index);

signals:

    void fileChanged(int file);

    void batteryVoltageChanged(double voltage);

    void usageCurrentChanged(double current);

    void throttlePositionChanged(double throttlePosition);

    void igbt1TemperatureChanged(double temperature);

    void igbt2TemperatureChanged(double temperature);

    void batteryLifeChanged(double life);

    void speedChanged(double speed);

    void signalLightStateChanged(int state);

    void headLightStateChanged(int state);

private:
    int file_;
    double batteryVoltage_;
    double usageCurrent_;
    double throttlePosition_;
    double igbt1Temperature_;
    double igbt2Temperature_;
    double batteryLife_;
    double speed_;
    int signalLightState_;
    int headLightState_;

    static Master *instance_;

    std::chrono::high_resolution_clock::time_point start_;
    QVector<Dataset*> datasets_;
};

#endif // MASTER_H
