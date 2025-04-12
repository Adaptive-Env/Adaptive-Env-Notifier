package com.adaptive.environments.notify_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertRecord {
    private String deviceId;
    private AlertType type;
    private AlertSeverity severity;
    private Long timestamp;
    private String description;
}
