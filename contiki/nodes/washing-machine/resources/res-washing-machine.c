#include "../../../coap-utility.h"
#include "../../value-updater.h"

#include "coap-engine.h"
#include "contiki.h"
#include "sys/log.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_MODULE "ResWashingMachine"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60
#define SHORT_PROGRAM (30)
#define LONG_PROGRAM (60)

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

static void res_post_put_handler(coap_message_t *request,
                                 coap_message_t *response, uint8_t *buffer,
                                 uint16_t preferred_size, int32_t *offset);

typedef struct {
    bool mode;
    char program[8];
} washing_machine_t;

washing_machine_t washing_machine = {.mode = false};

RESOURCE(res_washing_machine,
         "title=\"Washing Machine\";"
         "methods=\"GET/PUT/POST\";"
         "payload=mode:on|off,program:short|long;"
         "rt=\"String\"\n",
         res_get_handler, res_post_put_handler, res_post_put_handler, NULL);

PROCESS(washing_machine_process, "Washing machine program mode process");

PROCESS_THREAD(washing_machine_process, ev, data)
{
    static struct etimer timer;
    process_start(&washing_machine_process, NULL);

    PROCESS_BEGIN();

    if (!strcmp(washing_machine.program, "short"))
        etimer_set(&timer, CLOCK_SECOND * SHORT_PROGRAM);
    else
        etimer_set(&timer, CLOCK_SECOND * LONG_PROGRAM);

    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    washing_machine.mode = false;

    PROCESS_END();
}

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    unsigned int accept = -1;
    coap_get_header_accept(request, &accept);

    if (accept == APPLICATION_JSON) {
        coap_set_header_content_format(response, APPLICATION_JSON);

        if (washing_machine.mode)
            snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE,
                     "{ \"mode\" : \"%s\", \"program\": \"%s\" }", "on",
                     washing_machine.program);
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
            if (washing_machine.mode &&
                process_is_running(&washing_machine_process)) {
                success = 2;
            } else {

                size_t len_program = coap_get_json_variable(
                    (const char *)request->payload, request->payload_len,
                    "\"program\"", &program);

                washing_machine.mode = true;
                if (!strncmp("\"long\"", program, len_program))
                    strcpy(washing_machine.program, "long");
                else
                    strcpy(washing_machine.program, "short");
                success = 1;
                process_start(&washing_machine_process, NULL);
            }
        } else {
            if (process_is_running(&washing_machine_process))
                process_exit(&washing_machine_process);

            success = 1;
            washing_machine.mode = false;
        }
    }

    if (!success) {
        LOG_DBG("Can not change the washing_machine mode\n");
        coap_set_status_code(response, BAD_REQUEST_4_00);
    } else if (success == 2) {
        LOG_DBG("Washing machine was already running\n");
    } else {
        if (washing_machine.mode)
            LOG_DBG("Washing machine mode set to on with program: %s\n",
                    washing_machine.program);
        else
            LOG_DBG("Washing machine mode set to off\n");
    }
}