#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "coap-engine.h"
#include "contiki.h"

/* Log configuration */
#include "sys/log.h"

#define LOG_MODULE "Led actuator"
#define LOG_LEVEL LOG_LEVEL_INFO

extern coap_resource_t res_leds;

PROCESS(led_actuator, "Led actuator");
AUTOSTART_PROCESSES(&led_actuator);

PROCESS_THREAD(led_actuator, ev, data)
{
    PROCESS_BEGIN();

    coap_activate_resource(&res_leds, "actuators/led");

    /* Define application-specific events here. */
    //while (1) {
    //    PROCESS_WAIT_EVENT();
    //}
    PROCESS_END();
}
