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
extern coap_resource_t res_temp_regulator;

static coap_message_type_t result = COAP_TYPE_RST;

PROCESS(smart_conditioner, "Smart air conditioner");
AUTOSTART_PROCESSES(&smart_conditioner);

static void response_handler(coap_message_t *response)
{
    LOG_DBG("Response %i\n", response->type);
    result = response->type;
}

PROCESS_THREAD(smart_conditioner, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    coap_activate_resource(&res_temperature, "smart-conditioner/temperature");
    coap_activate_resource(&res_humidity, "smart-conditioner/humidity");
    coap_activate_resource(&res_humidifier, "smart-conditioner/humidifier");
    coap_activate_resource(&res_temp_regulator, "smart-conditioner/temp-regulator");

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    do {
        coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
        coap_set_header_uri_path(request, (const char *)&SERVER_REGISTRATION);

        COAP_BLOCKING_REQUEST(&server_ep, request, response_handler);
    } while (result == COAP_TYPE_RST);

    PROCESS_END();
}
