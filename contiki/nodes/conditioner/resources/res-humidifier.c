#include "../../../coap-utility.h"
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

#define LOG_MODULE "Humidifier"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

static void res_post_put_handler(coap_message_t *request,
                                 coap_message_t *response, uint8_t *buffer,
                                 uint16_t preferred_size, int32_t *offset);

humidifier_t humidifier = {
    .mode = false,
    .humidity = 0.0,
};

RESOURCE(res_humidifier,
         "title=\"Humidifier\";"
         "methods=\"GET/PUT/POST\";"
         "payload=mode:on|off,humidity:float;"
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

        if (humidifier.mode)
            snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE,
                     "{ \"mode\" : \"%s\", \"humidity\": %f }", "on",
                     humidifier.humidity);
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
    const char *humidity;

    LOG_DBG("%s\n", request->payload);

    size_t len_mode =
        coap_get_json_variable((const char *)request->payload,
                               request->payload_len, "\"mode\"", &mode);

    if (len_mode) {
        if (!strncmp("\"on\"", mode, len_mode)) {
            coap_get_json_variable((const char *)request->payload,
                                   request->payload_len, "\"humidity\"",
                                   &humidity);

            humidifier.mode = true;
            humidifier.humidity = atof(humidity);
        } else
            humidifier.mode = false;

        success = 1;
    }

    if (!success) {
        LOG_DBG("Can not change the humidifier mode\n");
        coap_set_status_code(response, BAD_REQUEST_4_00);
    } else {
        if (humidifier.mode)
            LOG_DBG("Humidifier mode set to on with humidity: %f\n",
                    humidifier.humidity);
        else
            LOG_DBG("Humidifier mode set to off\n");
    }
}