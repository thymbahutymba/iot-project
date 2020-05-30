#ifndef VALUE_UPDATER_H
#define VALUE_UPDATER_H

#define OFFSET_VALUE (1)
#define PROBABILITY_UPDATE (0.2)

#include <stdio.h>
#include <stdlib.h>

#define init_value_range(value, val_min, val_max)                              \
    {                                                                          \
        value = ((float)rand() / RAND_MAX) * (val_max - val_min) + val_min;    \
    }

#define update_value(value)                                                    \
    {                                                                          \
        if (((float)rand() / RAND_MAX) < PROBABILITY_UPDATE)                   \
            value += ((float)rand() / RAND_MAX) * (2 * OFFSET_VALUE) -         \
                     OFFSET_VALUE;                                             \
    }

#endif /* VALUE_UPDATER_H */
