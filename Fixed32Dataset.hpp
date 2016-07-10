#ifndef FIXED32DATASET_HPP
#define FIXED32DATASET_HPP

#include "Dataset.hpp"

class Fixed32Dataset : public Dataset {
    Q_OBJECT
public:
    explicit Fixed32Dataset(QString name, QString unit, uint32_t SID, Master* master);

    virtual float convert(const uint8_t * const data, uint8_t length) const override;

private:
};

#endif // FIXED32DATASET_HPP
