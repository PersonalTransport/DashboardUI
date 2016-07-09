#include "Fixed32Dataset.hpp"
#include "Master.hpp"

Fixed32Dataset::Fixed32Dataset(QString name, QString unit, uint32_t high_sid, uint32_t low_SID, Master* master)
    : Dataset(name, unit, low_SID, master)
    , has_high_(false)
    , has_low_(false)
    , high_data_(0)
    , low_data_(0)
    , high_SID_(high_sid)
{
}

void Fixed32Dataset::onSignalReceived(uint32_t SID, uint8_t* data, uint8_t)
{
    if (SID == this->SID_) {
        has_low_ = true;
        low_data_ = (uint16_t(data[1]) << 8) | uint16_t(data[0]);
    } else if (SID == this->high_SID_) {
        has_high_ = true;
        high_data_ = (uint16_t(data[1]) << 8) | uint16_t(data[0]);
    }

    if (has_high_ && has_low_) {
        has_high_ = has_low_ = false;
        int32_t value = (int32_t(high_data_) << 16) | int32_t(low_data_);
        addDataPoint(float(value) / 65536.0f);
    }
}

float Fixed32Dataset::convert(uint8_t* data, uint8_t length) const
{
    return 0;
}
