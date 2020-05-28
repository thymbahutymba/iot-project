#include "coap-blocking-api.h"
#include "coap-engine.h"
#include "contiki-net.h"
#include "contiki.h"
#include "lib/heapmem.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Log configuration */
#include "sys/log.h"

#define LOG_MODULE "Ambient sensor"
#define LOG_LEVEL LOG_LEVEL_DBG

#define SERVER_EP ("coap://[fd00::1]:5683")
#define SERVER_REGISTRATION ("/registration")

extern coap_resource_t res_temperature;
extern coap_resource_t res_humidity;

static coap_message_type_t result = COAP_TYPE_RST;
//static char payload[2048];

PROCESS(ambient_sensor, "Ambient sensor server");
AUTOSTART_PROCESSES(&ambient_sensor);

static void response_handler(coap_message_t *response)
{
    LOG_DBG("Response %i\n", response->type);
    result = response->type;
}

/*
static char *ser_resource(coap_resource_t *r)
{
    char methods[70] = "[ ";
    char *result;

    if (r->get_handler)
        strcat(methods, "\"get\", ");

    if (r->post_handler)
        strcat(methods, "\"post\", ");

    if (r->delete_handler)
        strcat(methods, "\"delete\", ");

    if (r->put_handler)
        strcat(methods, "\"put\", ");

    strcat(methods, "]");

    //LOG_DBG("methods(%i): %s\n", strlen(methods), methods);
    //LOG_DBG("url(%i): %s\n", strlen(r->url), r->url);

    if (!(result = (char *)malloc(strlen(methods) + strlen(r->url) + 30)))
        LOG_ERR("malloc error resource\n");
        
    //LOG_DBG("after alloc\n");

    memset(result, 0, strlen(result));
    sprintf(result, "{ \"path\": %s, \"methods\": %s },\n", r->url, methods);

    //LOG_DBG("stop ser_resource");

    return result;
}
*/
/*
{
    "resources": [
        { "path": "", "methods": ["get", "post", "put", ...] },
    ],
}
*/
/*
void ser_resources(coap_resource_t **res)
{
    char *ser_res[sizeof(res) / sizeof(coap_resource_t *)];
    //char *p;
    size_t len = 0;

    for (size_t i = 0; i < sizeof(res) / sizeof(coap_resource_t *); ++i) {
        ser_res[i] = ser_resource(res[i]);
        len += strlen(ser_res[i]);
    }

    //LOG_DBG("pre malloc %i\n", len);
    //if (!(p = (char *)malloc(20))) 
    //    LOG_ERR("malloc error resources\n");
    //LOG_DBG("post malloc\n");

    sprintf(payload, "{ \"resources\": [ \n");

    for (size_t i = 0; i < sizeof(ser_res) / sizeof(char *); ++i) {
        strcat(payload, ser_res[i]);
        free(ser_res[i]);
    }

    //LOG_DBG("post free");

    //return payload;
}
*/
PROCESS_THREAD(ambient_sensor, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();
        
    coap_activate_resource(&res_temperature, "sensors/ambient/temperature");
    coap_activate_resource(&res_humidity, "sensors/ambient/humidity");

    //if ((payload = ser_resources(resources)) == NULL) {
    //    LOG_ERR("malloc error");
    //}
    //ser_resources(resources);
    
    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    do {
        coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
        coap_set_header_uri_path(request, (const char *)&SERVER_REGISTRATION);
        //coap_set_payload(request, (uint8_t *)payload, sizeof(payload) - 1);

        COAP_BLOCKING_REQUEST(&server_ep, request, response_handler);
    } while (result == COAP_TYPE_RST);

    //if (payload)
    //    free((void *)payload);

    /* Define application-specific events here. */
    // while (1) {
    //    PROCESS_WAIT_EVENT();
    //}
    PROCESS_END();
}
