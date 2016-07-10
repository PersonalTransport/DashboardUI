#ifndef FIXED32ANGLEDATASET_H
#define FIXED32ANGLEDATASET_H

#include "Fixed32Dataset.hpp"

class Fixed32AngleDataset : public Fixed32Dataset {
    Q_OBJECT
public:
    explicit Fixed32AngleDataset(QString name, uint32_t SID, Master* master);

    virtual float convert(const uint8_t * const data, uint8_t length) const override;
};

#endif // FIXED32ANGLEDATASET_H
