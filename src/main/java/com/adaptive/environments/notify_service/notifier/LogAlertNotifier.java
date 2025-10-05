package com.adaptive.environments.notify_service.notifier;

import com.adaptive.environments.notify_service.model.AlertRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "notifier.log", name = "enabled", havingValue = "true")
public class LogAlertNotifier implements AlertNotifier {

    private static final Logger log = LoggerFactory.getLogger(LogAlertNotifier.class);

    @Override
    public void notify(AlertRecord alert) {
        log.warn("[ALERT LOGGED] Device: {}, Severity: {}, Desc: {}",
                alert.getDeviceId(), alert.getSeverity(), alert.getDescription());
    }
}