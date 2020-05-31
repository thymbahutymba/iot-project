#include "../../value-updater.h"
#include "humidifier.h"

#include "coap-engine.h"
#include "contiki.h"
#include "os/lib/random.h"
#include "sys/log.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_MODULE "Humidity"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE (60)
#define HUMIDITY_MAX (100)
#define HUMIDITY_MIN (20)

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);
static void res_periodic_handler(void);

static float humidity = (float)INT16_MIN;
extern humidifier_t humidifier;

#ifdef update_value
#undef update_value
#endif

#define update_value(value)                                                    \
    {                                                                          \
        if (!humidifier.mode) {                                                \
            if (((float)rand() / RAND_MAX) > PROBABILITY_UPDATE)               \
                value += ((float)rand() / RAND_MAX) * (2 * OFFSET_VALUE) -     \
                         OFFSET_VALUE;                                         \
        } else {                                                               \
            if (((float)rand() / RAND_MAX) < PROBABILITY_UPDATE) {             \
                float inc = ((float)rand() / RAND_MAX) * OFFSET_VALUE;         \
                value += (humidifier.humidity > humidity) ? inc : -inc;        \
            }                                                                  \
        }                                                                      \
    }

PERIODIC_RESOURCE(res_humidity,
                  "title=\"Humidity\";"
                  "methods=\"GET\";"
                  "payload=humidity:float;"
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
        snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"humidity\":%.1f}",
                 humidity);

        coap_set_payload(response, buffer, strlen((char *)buffer));
    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-types application/json";
        coap_set_payload(response, msg, strlen(msg));
    }

    coap_set_header_max_age(response, MAX_AGE);

    /* The coap_subscription_handler() will be called for observable resources
     * by the coap_framework. */
}

static void res_periodic_handler()
{
    if (humidity == (float)INT16_MIN)
        init_value_range(humidity, HUMIDITY_MIN, HUMIDITY_MAX);

    update_value(humidity);

    /* Notify the registered observers which will trigger the
     * res_get_handler to create the response. */
    coap_notify_observers(&res_humidity);
}
