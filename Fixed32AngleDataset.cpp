#include "Fixed32AngleDataset.hpp"
#include "Master.hpp"

Fixed32AngleDataset::Fixed32AngleDataset(QString name, uint32_t SID, Master* master)
    : Fixed32Dataset(name, "rad",SID, master)
{
}

float Fixed32AngleDataset::convert(const uint8_t* const data, uint8_t length) const
{
    float real = Fixed32Dataset::convert(data,length);
    double twoPi = 2.0 * 3.141592865358979;
    return real - twoPi * floor( real / twoPi );
}
