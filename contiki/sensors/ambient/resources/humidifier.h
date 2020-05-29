#ifndef HUMIDIFIER_H
#define HUMIDIFIER_H

#include "contiki.h"

typedef struct {
    bool mode;
    float humidity;
} humidifier_t;

#endif // HUMIDIFIER_H