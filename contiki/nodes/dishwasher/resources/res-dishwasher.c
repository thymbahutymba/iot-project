#include "../../../coap-utility.h"
#include "../../value-updater.h"

#include "coap-engine.h"
#include "contiki.h"
#include "sys/log.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_MODULE "ResDishwasher"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

static void res_post_put_handler(coap_message_t *request,
                                 coap_message_t *response, uint8_t *buffer,
                                 uint16_t preferred_size, int32_t *offset);

typedef struct {
    bool mode;
    char program[8];
} dishwasher_t;

dishwasher_t dishwasher = {.mode = false};

RESOURCE(res_dishwasher,
         "title=\"Dishwasher\";"
         "methods=\"GET/PUT/POST\";"
         "payload=mode:on|off,program:short|long;"
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

        if (dishwasher.mode)
            snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE,
                     "{ \"mode\" : \"%s\", \"program\": \"%s\" }", "on",
                     dishwasher.program);
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
    const char *program;

    LOG_DBG("%s\n", request->payload);

    size_t len_mode =
        coap_get_json_variable((const char *)request->payload,
                               request->payload_len, "\"mode\"", &mode);

    if (len_mode) {
        if (!strncmp("\"on\"", mode, len_mode)) {
            size_t len_program = coap_get_json_variable(
                (const char *)request->payload, request->payload_len,
                "\"program\"", &program);

            dishwasher.mode = true;
            if (!strncmp("\"long\"", program, len_program))
                strcpy(dishwasher.program, "long");
            else
                strcpy(dishwasher.program, "short");
        } else
            dishwasher.mode = false;

        success = 1;
    }

    if (!success) {
        LOG_DBG("Can not change the dishwasher mode\n");
        coap_set_status_code(response, BAD_REQUEST_4_00);
    } else {
        if (dishwasher.mode)
            LOG_DBG("Dishwasher mode set to on with program: %s\n",
                    dishwasher.program);
        else
            LOG_DBG("Dishwasher mode set to off\n");
    }
}

#define SHORT_PROGRAM (20)
#define LONG_PROGRAM (60)

PROCESS(dishwasher_program, "Dishwasher program mode process");

PROCESS_THREAD(dishwasher_program, ev, data)
{
    static struct etimer timer;
    process_start(&dishwasher_program, NULL);

    PROCESS_BEGIN();

    if (!strcmp(data, "short"))
        etimer_set(&timer, CLOCK_SECOND * SHORT_PROGRAM);
    else 
        etimer_set(&timer, CLOCK_SECOND * LONG_PROGRAM);

    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    dishwasher.mode = false;

    etimer_reset(&timer);

    PROCESS_END();
}