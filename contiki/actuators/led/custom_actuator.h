#ifndef CUSTOM_SENSOR_H
#define CUSTOM_SENSOR_H

#define OFFSET_VALUE (1)

#include "random.h"
#include <stdlib.h>

typedef struct sensor {
    float value;
    void (*init)(void *, int, int);
    void (*update)(void *);
} sensor_t;

static void init_sensor(void *self, int val_min, int val_max)
{
    ((sensor_t *)self)->value =
        ((float)rand() / RAND_MAX) * (val_max - val_min) + val_min;
}

static void update_sensor(void *self)
{
    ((sensor_t *)self)->value +=
        ((float)rand() / RAND_MAX) * (2 * OFFSET_VALUE) - OFFSET_VALUE;
}

#endif /* CUSTOM_SENSOR_H */
