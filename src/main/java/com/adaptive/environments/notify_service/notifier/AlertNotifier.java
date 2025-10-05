package com.adaptive.environments.notify_service.notifier;

import com.adaptive.environments.notify_service.model.AlertRecord;

public interface AlertNotifier {
    void notify(AlertRecord message);
}

