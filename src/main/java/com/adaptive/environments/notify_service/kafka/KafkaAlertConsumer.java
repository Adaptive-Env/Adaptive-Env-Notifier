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

            AlertRecord alertRecord = AlertRecord.builder()
                    .deviceId(alert.getDeviceId())
                    .type(alert.getType())
                    .severity(alert.getSeverity())
                    .timestamp(alert.getTimestamp())
                    .description(alert.getDescription())
                    .build();

            notifiers.forEach(notifier -> notifier.notify(alertRecord));

            log.info("[Kafka] Alert saved for device: {}, type: {}, severity: {}",
                    alert.getDeviceId(), alert.getType(), alert.getSeverity());

            meterRegistry.counter("iot.alerts.persisted", "severity", alert.getSeverity().name()).increment();

        } catch (Exception e) {
            log.error("[Kafka] Error while saving alert to DB", e);
            meterRegistry.counter("iot.alerts.persist.error").increment();
        } finally {
            record.receiverOffset().acknowledge();
        }
    }
}
