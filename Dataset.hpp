#ifndef DATASET_HPP
#define DATASET_HPP

#include <QObject>
#include <QVector>
#include <QtCharts>

class Master;

class Dataset : public QObject {
    Q_OBJECT
    Q_PROPERTY(QString name READ name)
    Q_PROPERTY(QString unit READ unit)
public:
    explicit Dataset(QString name, QString unit, uint32_t SID, Master* master);

    virtual ~Dataset() = default;

    QString name() const;

    QString unit() const;

    void addDataPoint(float value);

    Q_INVOKABLE void update(QtCharts::QAbstractSeries* series);

    virtual float convert(const uint8_t* const data, uint8_t length) const = 0;

    virtual void onSignalReceived(uint32_t SID, const uint8_t * const data, uint8_t length);

protected:
    Master* master_;
    QString name_;
    QString unit_;
    uint32_t SID_;
    QVector<QPointF> data_;
};

#endif // DATASET_HPP
