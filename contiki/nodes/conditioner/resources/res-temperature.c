#include "../../value-updater.h"
#include "temp-regulator.h"

#include "coap-engine.h"
#include "contiki.h"
#include "os/lib/random.h"
#include "sys/log.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_MODULE "Temperature"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60
#define OFFSET_TEMP (2)
#define TEMP_MAX (40)
#define TEMP_MIN (20)

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);
static void res_periodic_handler(void);

static float temperature = (float)INT16_MIN;
extern regulator_t temp_regulator;

#ifdef update_value
#undef update_value
#endif

#define update_value(value)                                                    \
    {                                                                          \
        if (!temp_regulator.mode) {                                               \
            if (((float)rand() / RAND_MAX) > PROBABILITY_UPDATE)               \
                value += ((float)rand() / RAND_MAX) * (2 * OFFSET_VALUE) -     \
                         OFFSET_VALUE;                                         \
        } else {                                                               \
            if (((float)rand() / RAND_MAX) < PROBABILITY_UPDATE) {             \
                float inc = ((float)rand() / RAND_MAX) * OFFSET_VALUE;         \
                value += (temp_regulator.temperature > temperature) ? inc : -inc; \
            }                                                                  \
        }                                                                      \
    }

PERIODIC_RESOURCE(res_temperature,
                  "title=\"Temperature\";"
                  "methods=\"GET\";"
                  "payload=temperature:float;"
                  "rt=\"float\";obs\n",
                  res_get_handler, NULL, NULL, NULL, 5000,
                  res_periodic_handler);

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    unsigned int accept = APPLICATION_JSON;
    coap_get_header_accept(request, &accept);

    if (accept == APPLICATION_JSON) {
        coap_set_header_content_format(response, APPLICATION_JSON);
        snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"temperature\":%.1f}",
                 temperature);

        coap_set_payload(response, buffer, strlen((char *)buffer));
    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg =
            "Supporting content-types text/plain and application/json";
        coap_set_payload(response, msg, strlen(msg));
    }

    coap_set_header_max_age(response, MAX_AGE);

    /* The coap_subscription_handler() will be called for observable resources
     * by the coap_framework. */
}

static void res_periodic_handler()
{
    if (temperature == (float)INT16_MIN)
        init_value_range(temperature, TEMP_MIN, TEMP_MAX);

    update_value(temperature);

    /* Notify the registered observers which will trigger the
     * res_get_handler to create the response. */
    coap_notify_observers(&res_temperature);
}
