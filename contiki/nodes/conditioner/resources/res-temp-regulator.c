#include "../../../coap-utility.h"
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

#define LOG_MODULE "Temperature regulator"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

static void res_post_put_handler(coap_message_t *request,
                                 coap_message_t *response, uint8_t *buffer,
                                 uint16_t preferred_size, int32_t *offset);

regulator_t temp_regulator = {
    .mode = false,
    .temperature = 0.0,
};

RESOURCE(res_temp_regulator,
         "title=\"Temperature regulator\";"
         "methods=\"GET/PUT/POST\";"
         "payload=mode:on|off,temperature:float;"
         "rt=\"String\"\n",
         res_get_handler, res_post_put_handler, res_post_put_handler, NULL);

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    unsigned int accept = -1;
    coap_get_header_accept(request, &accept);

    if (accept == APPLICATION_JSON) {
        coap_set_header_content_format(response, APPLICATION_JSON);

        if (temp_regulator.mode)
            snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE,
                     "{ \"mode\" : \"%s\", \"temperature\": %f }", "on",
                     temp_regulator.temperature);
        else
            snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE,
                     "{ \"mode\" : \"%s\" }", "off");

        coap_set_payload(response, buffer, strlen((char *)buffer));
    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }

    coap_set_header_max_age(response, MAX_AGE);

    /* The coap_subscription_handler() will be called for observable resources
     * by the coap_framework. */
}

static void res_post_put_handler(coap_message_t *request,
                                 coap_message_t *response, uint8_t *buffer,
                                 uint16_t preferred_size, int32_t *offset)
{
    int success = 0;
    const char *mode;
    const char *temperature;

    LOG_DBG("%s\n", request->payload);

    size_t len_mode =
        coap_get_json_variable((const char *)request->payload,
                               request->payload_len, "\"mode\"", &mode);

    LOG_DBG("%.*s\n", len_mode, mode);

    if (len_mode) {
        if (!strncmp("\"on\"", mode, len_mode)) {
            coap_get_json_variable((const char *)request->payload,
                                   request->payload_len, "\"temperature\"",
                                   &temperature);

            temp_regulator.mode = true;
            temp_regulator.temperature = atof(temperature);
        } else
            temp_regulator.mode = false;

        success = 1;
    }

    if (!success) {
        LOG_DBG("Can not change the conditioner mode\n");
        coap_set_status_code(response, BAD_REQUEST_4_00);
    } else {
        if (temp_regulator.mode)
            LOG_DBG("Conditioner mode set to on with temperature: %f\n",
                    temp_regulator.temperature);
        else
            LOG_DBG("Conditioner mode set to off\n");
    }
}