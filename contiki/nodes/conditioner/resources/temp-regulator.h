#ifndef TEMP_REGULATOR_H
#define TEMP_REGULATOR_H

#include "contiki.h"

typedef struct {
    bool mode;
    float temperature;
} regulator_t;

#endif // TEMP_REGULATOR_H