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
#define LOG_LEVEL LOG_LEVEL_RESOURCE
#define MAX_AGE (60)
#define HUMIDITY_MAX (100)
#define HUMIDITY_MIN (20)

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);
static void res_periodic_handler(void);

static float humidity = (float)INT16_MIN;
extern humidifier_t humidifier;

PERIODIC_RESOURCE(res_humidity,
                  "title=\"Humidity\";"
                  "methods=\"GET\";"
                  "payload=humidity:float;"
                  "rt=\"float\";obs\n",
                  res_get_handler, NULL, NULL, NULL, 1000,
                  res_periodic_handler);

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    unsigned int accept = -1;
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

/*
 * Additionally, a handler function named [resource name]_handler must be
 * implemented for each PERIODIC_RESOURCE. It will be called by the coap_manager
 * process with the defined period.
 */
static void res_periodic_handler()
{
    if (humidity == (float)INT16_MIN)
        INIT_VALUE_RANGE(humidity, HUMIDITY_MIN, HUMIDITY_MAX);

    LOG_DBG("Humidifier mode %i with value %f\n", humidifier.mode,
            humidifier.humidity);

    UPDATE_VALUE(humidity);

    /* Notify the registered observers which will trigger the
     * res_get_handler to create the response. */
    coap_notify_observers(&res_humidity);
}
