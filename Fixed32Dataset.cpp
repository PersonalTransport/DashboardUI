#include "Fixed32Dataset.hpp"
#include "Master.hpp"

Fixed32Dataset::Fixed32Dataset(QString name, QString unit, uint32_t SID, Master* master)
    : Dataset(name, unit, SID, master)
{
}

float Fixed32Dataset::convert(const uint8_t* const data, uint8_t length) const
{
    int32_t value = (uint32_t(data[3]) << 24) | (uint32_t(data[2]) << 16) | (uint32_t(data[1]) << 8) | uint32_t(data[0]);
    return float(value) / 65536.0f;
}
