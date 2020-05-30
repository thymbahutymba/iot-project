#include "coap-blocking-api.h"
#include "coap-engine.h"
#include "contiki-net.h"
#include "contiki.h"
#include "lib/heapmem.h"
#include "sys/log.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_MODULE "Ambient sensor"
#define LOG_LEVEL LOG_LEVEL_DBG

#define SERVER_EP ("coap://[fd00::1]:5683")
#define SERVER_REGISTRATION ("/registration")

extern coap_resource_t res_temperature;
extern coap_resource_t res_humidity;
extern coap_resource_t res_humidifier;

static coap_message_type_t result = COAP_TYPE_RST;
// static char payload[2048];

PROCESS(ambient_sensor, "Ambient sensor server");
AUTOSTART_PROCESSES(&ambient_sensor);

static void response_handler(coap_message_t *response)
{
    LOG_DBG("Response %i\n", response->type);
    result = response->type;
}

PROCESS_THREAD(ambient_sensor, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    coap_activate_resource(&res_temperature, "sensors/ambient/temperature");
    coap_activate_resource(&res_humidity, "sensors/ambient/humidity");
    coap_activate_resource(&res_humidifier, "actuators/ambient/humidifier");

    // if ((payload = ser_resources(resources)) == NULL) {
    //    LOG_ERR("malloc error");
    //}
    // ser_resources(resources);

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    do {
        coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
        coap_set_header_uri_path(request, (const char *)&SERVER_REGISTRATION);
        // coap_set_payload(request, (uint8_t *)payload, sizeof(payload) - 1);

        COAP_BLOCKING_REQUEST(&server_ep, request, response_handler);
    } while (result == COAP_TYPE_RST);

    // if (payload)
    //    free((void *)payload);

    /* Define application-specific events here. */
    // while (1) {
    //    PROCESS_WAIT_EVENT();
    //}
    PROCESS_END();
}
