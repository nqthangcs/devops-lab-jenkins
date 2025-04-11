package org.springframework.samples.petclinic.customers.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetricConfigTest {

    private final MetricConfig metricConfig = new MetricConfig();

    @Test
    void shouldConfigureMeterRegistryWithCommonTags() {
        // Given
        MeterRegistry meterRegistry = mock(MeterRegistry.class);
        MeterRegistry.Config config = mock(MeterRegistry.Config.class); // Use MeterRegistry.Config
        doReturn(config).when(meterRegistry).config(); // Stub config() to return Config
        MeterRegistryCustomizer<MeterRegistry> customizer = metricConfig.metricsCommonTags();

        // When
        customizer.customize(meterRegistry);

        // Then
        verify(config).commonTags("application", "petclinic");
    }

    @Test
    void shouldCreateTimedAspectWithMeterRegistry() {
        // Given
        MeterRegistry meterRegistry = mock(MeterRegistry.class);

        // When
        TimedAspect timedAspect = metricConfig.timedAspect(meterRegistry);

        // Then
        assertThat(timedAspect).isNotNull();
        assertThat(timedAspect).isInstanceOf(TimedAspect.class);
    }
}
