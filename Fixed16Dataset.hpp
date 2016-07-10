#ifndef FIXED16DATASET_HPP
#define FIXED16DATASET_HPP

#include "Dataset.hpp"

class Fixed16Dataset : public Dataset {
    Q_OBJECT
public:
    explicit Fixed16Dataset(QString name, QString unit, uint32_t SID, Master* master);

    virtual float convert(const uint8_t* const data, uint8_t length) const override;
};

class UFixed16Dataset : public Dataset {
    Q_OBJECT
public:
    explicit UFixed16Dataset(QString name, QString unit, uint32_t SID, Master* master);

    virtual float convert(const uint8_t * const data, uint8_t length) const override;
};

#endif // FIXED16DATASET_HPP
