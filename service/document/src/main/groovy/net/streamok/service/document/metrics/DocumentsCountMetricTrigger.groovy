package net.streamok.service.document.metrics

import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.TimerEndpoint
import net.streamok.fiber.node.TimerEvent

import static net.streamok.lib.conf.Conf.configuration

class DocumentsCountMetricTrigger extends TimerEndpoint {

    DocumentsCountMetricTrigger() {
        super(
                configuration().instance().getInt('DOCUMENT_METRIC_COUNT_INTERVAL', 15000),
                'document.metrics.count',
                )
        event = { new TimerEvent(deliveryOptions: new DeliveryOptions()) }
    }

}