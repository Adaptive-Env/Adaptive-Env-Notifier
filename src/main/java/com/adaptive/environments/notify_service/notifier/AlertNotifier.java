package com.adaptive.environments.notify_service.notifier;

import com.adaptive.environments.notify_service.model.AlertRecord;

public interface AlertNotifier {
    public void notify(AlertRecord message);
}

