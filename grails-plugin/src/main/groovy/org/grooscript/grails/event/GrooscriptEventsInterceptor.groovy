package org.grooscript.grails.event

import grails.events.Events
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import reactor.bus.Event

import static reactor.bus.selector.Selectors.*

class GrooscriptEventsInterceptor implements Events {

    private Set<String> eventsListening

    private GrooscriptGrailsHelpers grooscriptGrailsHelpers
    private ApplicationContext applicationContext

    @Autowired
    GrooscriptEventsInterceptor(
            GrooscriptGrailsHelpers grooscriptGrailsHelpers,
            ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
        this.grooscriptGrailsHelpers = grooscriptGrailsHelpers
    }

    synchronized public void addEventToIntercept(String name) {
        if (!eventsListening) {
            eventsListening = [] as Set
            if (grooscriptGrailsHelpers.isSpringWebsocketsActive(applicationContext)) {
                on(regex('.*')) { Event event ->
                    checkIfListeningEvent(event)
                }
            }
        }
        eventsListening << name
    }

    synchronized private void checkIfListeningEvent(Event event) {
        println '* EventsInterceptor ' + event.key + ' - ' + event.data
        if (eventsListening.contains(event.key)) {
            grooscriptGrailsHelpers.sendWebsocketMessake((String)event.key, event.data)
        }
    }
}
