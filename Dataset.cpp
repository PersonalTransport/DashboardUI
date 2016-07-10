#include "Dataset.hpp"
#include "Master.hpp"

Dataset::Dataset(QString name, QString unit, uint32_t SID, Master* master)
    : QObject(master)
    , master_(master)
    , name_(name)
    , unit_(unit)
    , SID_(SID)
{
}

QString Dataset::name() const
{
    return name_;
}

QString Dataset::unit() const
{
    return unit_;
}

void Dataset::addDataPoint(float value)
{
    auto time = master_->time();
    data_.push_back(QPointF(time, value));
    auto it = std::remove_if(data_.begin(), data_.end(), [time](const QPointF& point) {
        return point.x() < time - 750; // TODO this is hard coded here.
    });
    if (it != data_.end())
        data_.erase(it, data_.end());
}

void Dataset::update(QtCharts::QAbstractSeries* series)
{
    QtCharts::QXYSeries* xySeries = static_cast<QtCharts::QXYSeries*>(series);
    if(data_.size() > 0 && data_.back().x() < master_->time() - 10)
        addDataPoint(data_.back().y());
    xySeries->replace(data_);
}

void Dataset::onSignalReceived(uint32_t SID, const uint8_t* const data, uint8_t length)
{
    if (SID == SID_) {
        addDataPoint(convert(data, length));
    }
}
