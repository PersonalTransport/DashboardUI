#ifndef FIXED32DATASET_HPP
#define FIXED32DATASET_HPP

#include "Dataset.hpp"

class Fixed32Dataset : public Dataset {
    Q_OBJECT
public:
    explicit Fixed32Dataset(QString name, QString unit, uint32_t high_sid, uint32_t low_SID, Master* master);

    virtual void onSignalReceived(uint32_t SID, uint8_t* data, uint8_t length) override;

    virtual float convert(uint8_t* data, uint8_t length) const override;

private:
    bool has_high_, has_low_;
    uint16_t high_data_, low_data_;
    uint32_t high_SID_;
};

#endif // FIXED32DATASET_HPP
