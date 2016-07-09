#include "Fixed16Dataset.hpp"
#include "Master.hpp"

Fixed16Dataset::Fixed16Dataset(QString name, QString unit, uint32_t SID, Master* master)
    : Dataset(name, unit, SID, master)
{
}

float Fixed16Dataset::convert(uint8_t* data, uint8_t length) const
{
    int16_t value = (int16_t(data[1]) << 8) | int16_t(data[0]);
    return float(value) / 655.360f;
}

UFixed16Dataset::UFixed16Dataset(QString name, QString unit, uint32_t SID, Master* master)
    : Dataset(name, unit, SID, master)
{
}

float UFixed16Dataset::convert(uint8_t* data, uint8_t length) const
{
    uint16_t value = (uint16_t(data[1]) << 8) | uint16_t(data[0]);
    return float(value) / 655.360f;
}
