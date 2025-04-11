package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {MetricConfig.class, MetricConfigTest.TestConfig.class})
class MetricConfigTest {

    @Autowired
    private MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer;

    @Autowired
    private TimedAspect timedAspect;

    @Test
    void shouldConfigureMeterRegistryCustomizerWithCommonTags() {
        assertThat(meterRegistryCustomizer).isNotNull();

        // Use a real MeterRegistry to verify tags
        MeterRegistry registry = new SimpleMeterRegistry();
        meterRegistryCustomizer.customize(registry);

        // Add a dummy metric to inspect tags
        registry.counter("test.counter").increment();

        // Verify that the common tag "application=petclinic" is applied
        assertThat(registry.get("test.counter").counter().getId().getTags())
            .contains(io.micrometer.core.instrument.Tag.of("application", "petclinic"));
    }

    @Test
    void shouldConfigureTimedAspect() {
        assertThat(timedAspect).isNotNull();
        assertThat(timedAspect).isInstanceOf(TimedAspect.class);
    }

    // Configuration to provide MeterRegistry dependency for TimedAspect
    @Configuration
    static class TestConfig {
        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}
