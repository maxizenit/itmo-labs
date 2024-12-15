package com.vk.itmo.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class Metrics {

    private Integer maxInheritanceDepth;
    private Double averageInheritanceDepth;
    private Double abcMetrics;
    private Double averageOverrideMethodsCount;
    private Double averageClassFieldsCount;
}
