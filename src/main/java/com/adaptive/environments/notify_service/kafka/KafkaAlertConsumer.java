package com.adaptive.environments.notify_service.kafka;

import com.adaptive.environments.notify_service.model.AlertRecord;
import com.adaptive.environments.notify_service.notifier.AlertNotifier;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.List;

@Service
public class KafkaAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaAlertConsumer.class);

    private final KafkaReceiver<String, AlertRecord> kafkaReceiver;
    private final MeterRegistry meterRegistry;
    private final List<AlertNotifier> notifiers;

    public KafkaAlertConsumer(KafkaReceiver<String, AlertRecord> kafkaReceiver,
                              MeterRegistry meterRegistry,
                              List<AlertNotifier> notifiers) {
        this.kafkaReceiver = kafkaReceiver;
        this.notifiers = notifiers;
        this.meterRegistry = meterRegistry;

        startReceiving();
    }

    private void startReceiving() {
        kafkaReceiver.receive()
                .doOnNext(this::processRecord)
                .subscribe();
    }

    private void processRecord(ReceiverRecord<String, AlertRecord> record) {
        try {
            AlertRecord alert = record.value();

            notifiers.forEach(notifier -> notifier.notify(alert));

            log.info("[Kafka] Notification was sent for device: {}, type: {}, severity: {}",
                    alert.getDeviceId(), alert.getDescription(), alert.getSeverity());

            meterRegistry.counter("iot.alerts.notify", "severity", alert.getSeverity().name()).increment();

        } catch (Exception e) {
            log.error("[Kafka] Error while notifying", e);
            meterRegistry.counter("iot.alerts.notify.error").increment();
        } finally {
            record.receiverOffset().acknowledge();
        }
    }
}
